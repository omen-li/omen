package com.omen.netty.server.handler;


import static io.netty.buffer.Unpooled.copiedBuffer;
import static io.netty.handler.codec.http.HttpHeaders.Names.CONNECTION;
import static io.netty.handler.codec.http.HttpHeaders.Names.CONTENT_LENGTH;
import static io.netty.handler.codec.http.HttpHeaders.Names.CONTENT_TYPE;
import static io.netty.handler.codec.http.HttpHeaders.Names.COOKIE;
import static io.netty.handler.codec.http.HttpHeaders.Names.SET_COOKIE;
import static io.netty.handler.codec.http.HttpResponseStatus.INTERNAL_SERVER_ERROR;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.Cookie;
import io.netty.handler.codec.http.CookieDecoder;
import io.netty.handler.codec.http.DefaultFullHttpRequest;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpObject;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.handler.codec.http.QueryStringDecoder;
import io.netty.handler.codec.http.ServerCookieEncoder;
import io.netty.handler.codec.http.multipart.Attribute;
import io.netty.handler.codec.http.multipart.DefaultHttpDataFactory;
import io.netty.handler.codec.http.multipart.HttpDataFactory;
import io.netty.handler.codec.http.multipart.HttpPostRequestDecoder;
import io.netty.handler.codec.http.multipart.HttpPostRequestDecoder.EndOfDataDecoderException;
import io.netty.handler.codec.http.multipart.InterfaceHttpData;
import io.netty.handler.codec.http.multipart.InterfaceHttpData.HttpDataType;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.CharsetUtil;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import sun.misc.BASE64Encoder;

import com.omen.netty.exception.SysErrException;
import com.omen.netty.server.http.pojo.HttpParameter;
import com.omen.netty.server.service.EventTriggerService;
import com.omen.netty.server.service.HttpServerService;

@Sharable
@Service()@Scope("prototype")
public class HttpServerHandler extends SimpleChannelInboundHandler<HttpObject> {

  private static Logger log = Logger.getLogger(HttpServerHandler.class);

  private DefaultFullHttpRequest fullHttpRequest;

  private boolean readingChunks;

  private final StringBuilder responseContent = new StringBuilder();

  private static final HttpDataFactory factory = new DefaultHttpDataFactory(DefaultHttpDataFactory.MINSIZE); //Disk

  private HttpPostRequestDecoder decoder;
  
  @Autowired(required=false)
  private HttpServerService serverService ;
  
  @Override
  public void userEventTriggered(ChannelHandlerContext ctx, Object evt)
          throws Exception {

      /*心跳处理*/
      if (evt instanceof IdleStateEvent) {
          IdleStateEvent event = (IdleStateEvent) evt;
          if (event.state() == IdleState.READER_IDLE) {
              /*读超时*/
              System.out.println("READER_IDLE 读超时");
              ctx.disconnect();
          } else if (event.state() == IdleState.WRITER_IDLE) {
              /*写超时*/  
              System.out.println("WRITER_IDLE 写超时");
          } else if (event.state() == IdleState.ALL_IDLE) {
              /*总超时*/
              System.out.println("ALL_IDLE 总超时");
          }
      }
  }

  
  /**
   * 配套心跳，对掉线处理，直接删除channel信息
   */
  @Override
  public void channelInactive(ChannelHandlerContext ctx) throws Exception {
	  // 这里加入掉线处理
      ctx.close();
      if (decoder != null) {
          decoder.cleanFiles();
      }
  }

  /**
   * 业务逻辑，收到请求后的处理
   */
  @Override
  public void messageReceived(ChannelHandlerContext ctx, HttpObject msg) throws Exception {
	  fullHttpRequest = (DefaultFullHttpRequest) msg;
	  
	  
	  Map<String,String> parameters = new HashMap<String, String>();
	  Map<String,String> cookies = new HashMap<String, String>();
	  HttpParameter httpParameter = new HttpParameter();
	  
	  /**
	   * 判断是不是HTTPS 此处待完善
	  if (SystemInfo.getIsSsl()) {
		  log.debug("Your session is protected by " +
				  ctx.pipeline().get(SslHandler.class).engine().getSession().getCipherSuite() +
				  " cipher suite.\n");
	  }
	   */
	  
	  /**
	   * 在服务器端打印请求信息
	   */
	  log.debug("VERSION: " + fullHttpRequest.getProtocolVersion().text() );
	  log.debug("REQUEST_URI: " + fullHttpRequest.getUri());
	  for (Entry<String, String> entry : fullHttpRequest.headers()) {
		  log.debug("HEADER: " + entry.getKey() + '=' + entry.getValue() );
	  }
	  
	  /** 处理cookie 
	   * 
	   */
	  Set<Cookie> cookieSet;
	  String value = fullHttpRequest.headers().get(COOKIE);
	  if (value == null) {
		  cookieSet = Collections.emptySet();
	  } else {
		  cookieSet = CookieDecoder.decode(value);
	  }
	  for (Cookie cookie : cookieSet) {
		  log.debug("cookie : cookieName[" + cookie.getName() + "] ;cookieValue[" + cookie.getValue() +  "]" );
		  cookies.put(cookie.getName(), cookie.getValue());
	  }
	  
	  
	  
	  /**
	   * 服务器端处理请求参数
	   */
	  if (fullHttpRequest.getMethod().equals(HttpMethod.GET)) {
		  //get请求
		  log.debug("get 请求处理中...");
		  QueryStringDecoder decoderQuery = new QueryStringDecoder(fullHttpRequest.getUri());
		  Map<String, List<String>> uriAttributes = decoderQuery.parameters();
		  for (Entry<String, List<String>> attr : uriAttributes.entrySet()) {
			  for (String attrVal : attr.getValue()) {
				  parameters.put(attr.getKey(), attrVal);
			  }
		  }
		  
	  } else if (fullHttpRequest.getMethod().equals(HttpMethod.POST)) {
		  //post请求
		  log.debug("post 请求处理中...");
		  decoder = new HttpPostRequestDecoder(factory, fullHttpRequest);
		  
		  String content =  fullHttpRequest.content().toString(CharsetUtil.UTF_8);
	  
		  if(fullHttpRequest.content().hasArray()){
			  byte[] connectArray = fullHttpRequest.content().array();
		  }
		  httpParameter.setContent(content);
		  
		  readingChunks = HttpHeaders.isTransferEncodingChunked(fullHttpRequest);
		  try {
			  while (decoder.hasNext()) {
				  InterfaceHttpData data = decoder.next();
				  if (data != null) {
					  try {
						  writeHttpData(data,parameters);
					  } finally {
						  data.release();
					  }
				  }
			  }
		  } catch (EndOfDataDecoderException e1) {
			  log.debug("end of data...");
		  }
		  
	  } else {
		  String errMsg = "unkown request type!";
		  log.error(errMsg);
		  throw new SysErrException(errMsg);
	  }
	  
	  
	  /**
	   * 交付业务处理
	   */
	  httpParameter.setParameters(parameters);
	  httpParameter.setContentType(fullHttpRequest.headers().get(CONTENT_TYPE));
	  httpParameter.setCookies(cookies);
	  String retMsg  = serverService.doService(httpParameter);
	  responseContent.append(retMsg);
	  log.debug("返回结果:" + responseContent.toString());
	  log.debug("handler hashCode:" + this.hashCode());
	  writeResponse(ctx.channel());
	  return;
  }
  
  private void reset() {
	  fullHttpRequest = null;
	  // destroy the decoder to release all resources
	  decoder.destroy();
	  decoder = null;
  }
  
  /**
   * 
   * @Title: writeHttpData
   * @Description: 获取POST请求参数
   * @param @param data
   * @param @param paramMap
   * @param @throws SysErrException    
   * @return void    
   * @throws
   * @author omen  www.liyidong.com 
   * @date 2014年12月24日 下午6:32:41
   */
  private void writeHttpData(InterfaceHttpData data,Map<String,String> paramMap)throws  SysErrException{
	  
	  /**
	   * HttpDataType有三种类型
	   * Attribute, FileUpload, InternalAttribute
	   */
	  if (data.getHttpDataType() == HttpDataType.Attribute) {
		  
		  Attribute attribute = (Attribute) data;
		  String value=null;
		  try {
			  value = attribute.getValue();
			  paramMap.put(attribute.getName(), value);
			  
			  /**
			   * 打印请求参数
			   */
			  log.debug("key = " + attribute.getName() + " ; value = " + value); 
		  } catch (IOException e1) {
			  e1.printStackTrace();
			  String errMsg = "\r\nBODY Attribute: " + attribute.getHttpDataType().name() + ":"
					  + attribute.getName() + " Error while reading value: " + e1.getMessage() + "\r\n";
			  log.error(errMsg);
			  
			  throw new SysErrException(errMsg,e1);
		  }
		  /*          if (value.length() > 100) {
                 responseContent.append("\r\nBODY Attribute: " + attribute.getHttpDataType().name() + ":"
                         + attribute.getName() + " data too long\r\n");
             } else {
             responseContent.append("\r\nBODY Attribute: " + attribute.getHttpDataType().name() + ":"
           		  + attribute.toString() + "\r\n");
             }*/
	  }
  }
  
  
  /**
   * http返回响应数据
   *
   * @param channel
   */
  private void writeResponse(Channel channel) {
	  // Convert the response content to a ChannelBuffer.
	  ByteBuf buf = copiedBuffer(responseContent.toString(), CharsetUtil.UTF_8);
	  responseContent.setLength(0);
	  
	  // Decide whether to close the connection or not.
	  boolean close = fullHttpRequest.headers().contains(CONNECTION, HttpHeaders.Values.CLOSE, true)
			  || (fullHttpRequest.getProtocolVersion().equals(HttpVersion.HTTP_1_0)
			  && !fullHttpRequest.headers().contains(CONNECTION, HttpHeaders.Values.KEEP_ALIVE, true));
	  
	  log.debug("CONNECTION CLOSE: " + fullHttpRequest.headers().contains(CONNECTION, HttpHeaders.Values.CLOSE, true));
	  log.debug("getProtocolVersion: " + fullHttpRequest.getProtocolVersion());
	  log.debug("含 KEEP_ALIVE: " + fullHttpRequest.headers().contains(CONNECTION, HttpHeaders.Values.KEEP_ALIVE, true));
	  
	  log.debug("whether to close: " + close);
	  // Build the response object.
	  FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK, buf);
	  response.headers().set(CONTENT_TYPE, "text/plain; charset=UTF-8");
	  
	  if (!close) {
		  // There's no need to add 'Content-Length' header
		  // if this is the last response.
		  response.headers().set(CONTENT_LENGTH, buf.readableBytes());
	  }
	  
	  Set<Cookie> cookies;
	  String value = fullHttpRequest.headers().get(COOKIE);
	  if (value == null) {
		  cookies = Collections.emptySet();
	  } else {
		  cookies = CookieDecoder.decode(value);
	  }
	  if (!cookies.isEmpty()) {
		  // Reset the cookies if necessary.
		  for (Cookie cookie : cookies) {
			  response.headers().add(SET_COOKIE, ServerCookieEncoder.encode(cookie));
		  }
	  }
	  // Write the response.
	  ChannelFuture future = channel.writeAndFlush(response);
	  // Close the connection after the write operation is done if necessary.
	  if (close) {
		  future.addListener(ChannelFutureListener.CLOSE);
	  }
  }
  
  @Override
  public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
	  if(ctx.channel().isActive()){
		  sendError(ctx, INTERNAL_SERVER_ERROR);
	  }
	  
  }
  
  /**

   * 异常后返回给客户端的信息
   *
   * @author OMEN_LI
   * @date 2014-12-11
   * @version V1.0.0
   * @param ctx
   * @param status 返回的页面状态
   * void
   */
  
  private static void sendError(ChannelHandlerContext ctx, HttpResponseStatus status) {
	  
	  FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, status);
	  
	  response.headers().set(CONTENT_TYPE, "text/plain; charset=UTF-8");
	  response.headers().set(CONTENT_LENGTH, response.content().readableBytes());
//      response.headers().set(CONNECTION, Values.KEEP_ALIVE);
	  
	  ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
	  
  }
  
  
  public static void main(String[] args) {
	  String a ="a";
	  String b = "b";
	  String c =a ;
	  System.out.println("" + c.hashCode());
	  c =b;
	  System.out.println(""+ c.hashCode());
	  
	  boolean d = false||false&&true;
	  System.out.println(d);
	  
	  BASE64Encoder encoder = new BASE64Encoder();
  }
  


}
