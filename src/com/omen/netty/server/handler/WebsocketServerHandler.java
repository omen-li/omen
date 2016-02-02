package com.omen.netty.server.handler;


import static io.netty.handler.codec.http.HttpHeaders.Names.CONNECTION;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.handler.codec.http.websocketx.CloseWebSocketFrame;
import io.netty.handler.codec.http.websocketx.PingWebSocketFrame;
import io.netty.handler.codec.http.websocketx.PongWebSocketFrame;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketServerHandshaker;
import io.netty.handler.codec.http.websocketx.WebSocketServerHandshakerFactory;
import io.netty.util.CharsetUtil;

import java.util.Iterator;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import com.omen.netty.server.service.EventTriggerService;
import com.omen.netty.server.service.WebSocketServerService;
import com.omen.netty.utils.StringUtil;

@Sharable
@Scope("prototype")
@Service()
public class WebsocketServerHandler extends SimpleChannelInboundHandler<Object> {

  private static Logger log = Logger.getLogger(WebsocketServerHandler.class);

  private WebSocketServerHandshaker handshaker;
  
  
  @Autowired(required=false)
  private WebSocketServerService webSocketServerService ;
  
  @Autowired(required=false)
  private EventTriggerService eventTriggerService ;
  
  
  @Override  
  public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {  
	  if(eventTriggerService == null ){
		  ctx.fireUserEventTriggered(evt);
	  }else{
		  eventTriggerService.userEventTriggered(ctx, evt);
	  }
  }


  @Override
  protected void messageReceived(ChannelHandlerContext ctx, Object msg)
		  throws Exception {
	  //虽然是websocket，但在建立websocket连接前，先进行http握手,所以，这时也要处理http请求
      //在http握手完成后，才是websocket下的通信
	  if(msg instanceof FullHttpRequest){
		  handleHttpRequest(ctx, (FullHttpRequest)msg);
	  }
	  //websocket接入
	  else if(msg instanceof WebSocketFrame){
		  handleWebSocketFrame(ctx, (WebSocketFrame)msg);
	  }
	  
	  
  }
  
  @Override
  public void channelReadComplete(ChannelHandlerContext ctx) throws Exception{
	  ctx.flush();
  }
  
  @Override
  public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception{
	  if (ctx != null ) {  
		  log.error("error when proccessing websocket request", cause);
		  try {
			  log.info("start to do business service close process...");
				webSocketServerService.close(ctx, cause);
			} catch (Exception e) {
				log.error("error in webSocketServerService close method", e);
			}
		  log.info("start to check channel is active...");
          if (ctx.channel().isActive()) {  
              ctx.close();  
              log.info("channel is active, close it...");
          }else
        	  log.info("channel is not active, deal over...");
      }  
  }
  
  /**
   * 
  * @Title: handleHttpRequest
  * @Description: 处理HTTP请求
  * @param @param ctx
  * @param @param req
  * @param @throws Exception    
  * @return void    
  * @throws
  * @author omen  www.liyidong.com 
  * @date 2015年5月29日 下午4:34:15
   */
  private void handleHttpRequest(ChannelHandlerContext ctx, FullHttpRequest req) throws Exception{
	  //如果HTTP解码失败，返回HTTP异常
	  if(!req.getDecoderResult().isSuccess()
		  || (!"websocket".equals(req.headers().get("Upgrade")))) {
		  log.debug("HTTP解码失败，返回HTTP异常");
		  sendHttpResponse(ctx, req, 
				  new DefaultFullHttpResponse(HttpVersion.HTTP_1_1,HttpResponseStatus.BAD_REQUEST));
		  return;
		  }
	  
	  //构造握手响应返回
	  WebSocketServerHandshakerFactory wsShakerFactory = new WebSocketServerHandshakerFactory(
              "ws://"+req.headers().get(HttpHeaders.Names.HOST),
              null,false );
      WebSocketServerHandshaker wsShakerHandler = wsShakerFactory.newHandshaker(req);
      if(null==wsShakerHandler){
          //无法处理的websocket版本
    	  log.debug("无法处理的websocket版本");
          wsShakerFactory.sendUnsupportedWebSocketVersionResponse(ctx.channel());
      }else{
          //向客户端发送websocket握手,完成握手
          //客户端收到的状态是101 switching protocol
    	  log.debug("客户端发送websocket握手,完成握手");
    	  Iterator it =req.headers().iterator();
    	  if(it.hasNext()){
    		  it.next();
    	  }
    	  
          wsShakerHandler.handshake(ctx.channel(), req);
          
          for(String pro:wsShakerHandler.subprotocols() ){
        	  log.debug("protocols:" + pro);
          }
          
          log.debug("WS version="+ wsShakerHandler.version());
          
      }
	  
  }
  
  
  /**
   * 
  * @Title: handleWebSocketFrame
  * @Description: 处理websocket消息
  * @param @param ctx
  * @param @param frame
  * @param @throws Exception    
  * @return void    
  * @throws
  * @author omen  www.liyidong.com 
  * @date 2015年5月29日 下午4:34:52
   */
  private void handleWebSocketFrame(ChannelHandlerContext ctx, WebSocketFrame frame) throws Exception{
	  Channel channel = ctx.channel();
	  //判断是否是关闭链路的指令
	  if(frame instanceof CloseWebSocketFrame){
		  handshaker.close(channel, (CloseWebSocketFrame)frame.retain());
		  return;
	  }
	  
	  //判断是否是ping消息
	  if(frame instanceof PingWebSocketFrame){
		  channel.write(new PongWebSocketFrame(frame.content().retain()));
//		  log.debug("ping pong returns!");
		  return;
	  }
	  
	  //TODO 目前仅支持文本消息，不支持二进制消息
	  if (frame instanceof TextWebSocketFrame) {  
		  // 业务请求返回消息处理
		  String request = ((TextWebSocketFrame) frame).text();
		  log.debug("server 收到 文本消息[" + request + "]");
		  
		  if(!StringUtil.isEmpty(request)){
/*			  //返回ACK，告知设备消息已抵达
			  String ackMsg = getAckRes(request);
			  if(!StringUtil.isEmpty(ackMsg))
				  channel.write(new TextWebSocketFrame(ackMsg));*/
			  
			  String res = webSocketServerService.doService(ctx, request);
		        log.debug("server 返回 文本消息[" + res + "]");
		        channel.write(new TextWebSocketFrame(res));
			  
			
				
		  }
		  
		/*  Thread t = new Thread(new Runnable() {
				@Override
				public void run() {
					Channel channel = ChannelHashTable.getCtx("test");
					try {
						log.debug("new thread start to sleep 30");
						Thread.sleep(70000);
						 log.debug("new thread start to send response");
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					if(channel.isWritable()){
						log.debug("channel is writable!");
						channel.writeAndFlush(new TextWebSocketFrame("second" ));
					}else{
						log.debug("channel is not writable!");
					}
				}
			});
		 
		 
		 t.start();*/
		 
		  
		 
	    }   
	  else{
		  throw new UnsupportedOperationException(String.format("%s frame types not supported", frame.getClass()  
	                .getName())); 
	  }
  
  }
  
  /**
   * 
  * @Title: sendHttpResponse
  * @Description: 返回http握手的结果
  * @param @param ctx
  * @param @param req
  * @param @param res    
  * @return void    
  * @throws
  * @author omen  www.liyidong.com 
  * @date 2015年6月15日 下午5:55:29
   */
  private void sendHttpResponse(ChannelHandlerContext ctx, FullHttpRequest req ,FullHttpResponse res){
	  //返回应答给客户端
	  if(res.getStatus().code()!=200) {
		  ByteBuf buf = Unpooled.copiedBuffer(res.getStatus().toString(),CharsetUtil.UTF_8);
		  res.content().writeBytes(buf);
		  buf.release();
		  res.headers().setContentLength(res, res.content().readableBytes());  
	  }
	  
	  //如果是非keep-alive，关闭连接
	  ChannelFuture f = ctx.channel().writeAndFlush(res);
	  if(!isKeepAlive(req)|| res.getStatus().code()!=200){
		  try {
			  log.info("this request is not keepalive, start to close it..");
			webSocketServerService.close(ctx, new Throwable("for it is un keepAlive connection"));
		} catch (Exception e) {
			log.error("error in webSocketServerService close method", e);
		}
		  f.addListener(ChannelFutureListener.CLOSE);
	  }
  }
  
  /**
   * 
  * @Title: isKeepAlive
  * @Description: 判断HTTP请求是否为keep-alive
  * @param @param req
  * @param @return    
  * @return boolean    
  * @throws
  * @author omen  www.liyidong.com 
  * @date 2015年5月29日 下午4:35:16
   */
  private static boolean isKeepAlive(FullHttpRequest req){
	  boolean keepAlive = !req.headers().contains(CONNECTION, HttpHeaders.Values.CLOSE, true)
			  && (!req.getProtocolVersion().equals(HttpVersion.HTTP_1_0)
					  || req.headers().contains(CONNECTION, HttpHeaders.Values.KEEP_ALIVE, true));
	  return keepAlive;  
  }
  
  /**
   * 
  * @Title: getAckRes
  * @Description: 返回终端设备请求抵达响应
  * @param @param request
  * @param @return    
  * @return String    
  * @throws
  * @author omen  www.liyidong.com 
  * @date 2015年6月15日 下午5:55:53
   */
//  private static String getAckRes(String request){
//	  if(request.indexOf("|")>=0){
//		  String requestId = request.split("|", -1)[0];
//		  String retMsg = "ACK|" +requestId;
//		  return retMsg;
//	  }else{
//		  return null;
//	  }
//  }
  

}
