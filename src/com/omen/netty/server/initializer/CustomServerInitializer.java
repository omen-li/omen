package com.omen.netty.server.initializer;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.ssl.SslHandler;

import javax.net.ssl.SSLEngine;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import com.omen.netty.securechat.SecureChatSslContextFactory;
import com.omen.netty.server.Main;
import com.omen.netty.server.codec.NettyMsgDecoder;
import com.omen.netty.server.codec.NettyMsgEncoder;
import com.omen.netty.server.handler.CustomServerHandler;


@Service()@Scope("prototype")
public class CustomServerInitializer extends ChannelInitializer<SocketChannel>  {
  
    
    private static Logger log = Logger.getLogger(Main.class);
    
    @Autowired(required=false)
	private NettyMsgDecoder nettyMsgDecoder;
    
    @Autowired(required=false)
   	private NettyMsgEncoder nettyMsgEncoder;
    
    @Autowired
    private CustomServerHandler customServerHandler;
   
    //默认为false
  	private Boolean isSsl=false;
  	
  	// 读超时时间
  	private static final int READ_WAIT_SECONDS = 60;
      
  	@Override
  	protected void initChannel(SocketChannel ch) throws Exception {
  		
  		ChannelPipeline pipeline = ch.pipeline();
  		if (isSsl==null||isSsl) {
  			SSLEngine engine = SecureChatSslContextFactory.getServerContext().createSSLEngine();
  			engine.setNeedClientAuth(true); //ssl双向认证
  			engine.setUseClientMode(false);
  			engine.setWantClientAuth(true);
  			engine.setEnabledProtocols(new String[]{"SSLv3"});
  			pipeline.addLast("ssl", new SslHandler(engine));
  		}
  		
  		/**
  		第一个参数为信息最大长度，超过这个长度回报异常，第二参数为长度属性的起始（偏移）位，我们的协议中长度是0到第3个字节，
  		所以这里写0，第三个参数为“长度属性”的长度，我们是4个字节，所以写4，第四个参数为长度调节值，在总长被定义为包含包头长度时，
  		修正信息长度，第五个参数为跳过的字节数，根据需要我们跳过前4个字节，以便接收端直接接受到不含“长度属性”的内容。
  		长度字节高位在前，低位在后，如：0x000C
  		 */
  		
  		/**
  		 * LengthFieldBasedFrameDecoder 最新备注 2016-01-29
  		 * add by omen
  		 * @param maxFrameLength
  		 *         信息最大长度
  		 * @param lengthFieldOffset
  		 *         长度属性的起始（偏移）位,长度属性从哪个字节开始
  		 * @param lengthFieldLength
  		 *         长度属性占用字节数
  		 * @param lengthAdjustment
  		 *        长度调节值，在总长被定义为包含包头长度时，修正信息长度，比如如果总长包含包头长，而长度字段占用从第19个字节，20个字节，则该字段填-20
  		 * @param initialBytesToStrip
  		 *        需要跳过的字节数
  		 */
  		pipeline.addFirst("decoder", new LengthFieldBasedFrameDecoder(4*1024*1024,0,4,0,4)); 

  		pipeline.addLast("decoder",nettyMsgDecoder); 
  		pipeline.addLast("encoder",nettyMsgEncoder); 
  		
  		pipeline.addLast("customServerHandler",customServerHandler);
  	}
  	
  	
  	public Boolean getIsSsl() {
  		return isSsl;
  	}
  	
  	public void setIsSsl(Boolean isSsl) {
  		this.isSsl = isSsl;
  	}
	
}

