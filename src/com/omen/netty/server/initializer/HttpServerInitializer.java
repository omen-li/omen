package com.omen.netty.server.initializer;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpContentCompressor;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;
import io.netty.handler.ssl.SslHandler;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.net.ssl.SSLEngine;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import com.omen.netty.securechat.SecureChatSslContextFactory;
import com.omen.netty.server.Main;
import com.omen.netty.server.handler.HttpServerHandler;

@Service()@Scope("prototype")@DependsOn({"httpServerHandler"})
public class HttpServerInitializer extends ChannelInitializer<SocketChannel>   {
  
	@Autowired(required=false)
    private LinkedHashMap<String, ChannelHandler> customPipelineMap;
	
	@Autowired
	private HttpServerHandler httpServerHandler;
	//默认为false
	private Boolean isSsl=false;
    
    private static Logger log = Logger.getLogger(Main.class);
   
	
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
		 * http-request解码器
		 * http服务器端对request解码
		 */
		pipeline.addLast("decoder", new HttpRequestDecoder());
		/**
		 * http-response解码器
		 * http服务器端对response编码
		 */
		pipeline.addLast("encoder", new HttpResponseEncoder());
		
		/**
		 * http报文的数据组装成为封装好的httprequest对象
		 */
		 pipeline.addLast("aggregator", new HttpObjectAggregator(1048576));
		 /**
		  * 压缩
		  * Compresses an HttpMessage and an HttpContent in gzip or deflate encoding
		  * while respecting the "Accept-Encoding" header.
		  * If there is no matching encoding, no compression is done.
		  */
		 pipeline.addLast("deflater", new HttpContentCompressor());
		 
		 for(Iterator<Map.Entry<String,ChannelHandler>> it = customPipelineMap.entrySet().iterator();it.hasNext();){  
	            Entry<String, ChannelHandler> entry = (Entry<String, ChannelHandler>)it.next();  
	            if(entry.getValue()!=null){  
	            	String handlerName = entry.getKey();
					ChannelHandler handlerValue = entry.getValue();
					pipeline.addLast(handlerName, handlerValue);
	            }  
	        }  
		 
		 pipeline.addLast("httpHandler",httpServerHandler);
	}
	
/*	public LinkedHashMap<String, ChannelHandler> getCustomPipelineMap() {
		return customPipelineMap;
	}
	
	public void setCustomPipelineMap(
			LinkedHashMap<String, ChannelHandler> customPipelineMap) {
		this.customPipelineMap = customPipelineMap;
	}*/
	
	public Boolean getIsSsl() {
		return isSsl;
	}
	
	public void setIsSsl(Boolean isSsl) {
		this.isSsl = isSsl;
	}
	
}

