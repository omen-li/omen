package com.omen.netty.server.factory;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import com.omen.netty.exception.SysErrException;
import com.omen.netty.server.initializer.CustomServerInitializer;
import com.omen.netty.server.initializer.HttpServerInitializer;
import com.omen.netty.server.initializer.TcpServerInitializer;
import com.omen.netty.server.initializer.WebsocketServerInitializer;
import com.omen.netty.server.sysPojo.ProtocolType;
import com.omen.netty.server.sysPojo.ServerInfo;
import com.omen.netty.server.sysPojo.ServerList;

@Service()@DependsOn({"httpServerHandler"})@Scope("prototype")
public class ServerChannelFactory {

	@Autowired
	private HttpServerInitializer httpServerInitializer;
	
	@Autowired
	private TcpServerInitializer tcpServerInitializer;
	
	@Autowired
	private CustomServerInitializer customServerInitializer;
	
	@Autowired
	private WebsocketServerInitializer websocketServerInitializer;
	
	private static Logger log = Logger.getLogger(ServerChannelFactory.class);
	
	public  Channel createAcceptorChannel(int serverNo) throws SysErrException{
		
		ServerInfo serverInfo = ServerList.getServerInfo(serverNo);
		
		final ServerBootstrap serverBootstrap = ServerBootstrapFactory.createServerBootstrap(serverNo);
		serverBootstrap.childHandler(getChildHandler(serverNo));
		log.info("创建Server...");
		 try {
			 ChannelFuture channelFuture = serverBootstrap.bind(serverInfo.getPort()).sync();
			 channelFuture.awaitUninterruptibly();
	            if (channelFuture.isSuccess()) {
	            	serverInfo.printSysInfo();
	                return channelFuture.channel();
	            } else {
	            	String errMsg="Failed to open socket! Cannot bind to port: "+serverInfo.getPort()+"!";
	            	log.error(errMsg);
	            	throw new SysErrException(errMsg);
	            }
				
		} catch (Exception e){
			throw new SysErrException(e);
		}
	}
	
	/**
	 * 
	* @Title: getChildHandler
	* @Description: 初始化获取对应ServerInitializer实例，可配置ssl,TODO 以后合并TCP和custom
	* @param @param serverNo
	* @param @return
	* @param @throws SysErrException    
	* @return ChannelInitializer<SocketChannel>    
	* @throws
	* @author omen  www.liyidong.com 
	* @date 2016年1月13日 上午11:22:06
	 */
	private  ChannelInitializer<SocketChannel> getChildHandler(int serverNo) throws SysErrException{
		ServerInfo serverInfo = ServerList.getServerInfo(serverNo);
		switch (serverInfo.getProtocolType()) {
		case ProtocolType.HTTP:
//			httpServerInitializer.setIsSsl(serverInfo.getIsSsl());选择是否支持SSL，默认false
			return (ChannelInitializer<SocketChannel>)httpServerInitializer;
			
		case ProtocolType.HTTPS:
			return (ChannelInitializer<SocketChannel>)httpServerInitializer;
			
		case ProtocolType.TCP:
			return (ChannelInitializer<SocketChannel>)tcpServerInitializer;	
			
		case ProtocolType.CUSTOM:
			return (ChannelInitializer<SocketChannel>)customServerInitializer;	
			
		case ProtocolType.WEBSOCKET:
			return (ChannelInitializer<SocketChannel>)websocketServerInitializer;	

		default:
			String errMsg="undefined protocol:"+serverInfo.getProtocolType()+"!";
			throw new SysErrException(errMsg);
		}
		
		
		/*if(ProtocolType.HTTP.equals(SystemInfo.getProtocolType())
				||ProtocolType.HTTPS.equals(SystemInfo.getProtocolType())){
			return (ChannelInitializer<SocketChannel>)httpServerInitializer;
		}
		
		else if(ProtocolType.TCP.equals(SystemInfo.getProtocolType()))
			return (ChannelInitializer<SocketChannel>)tcpServerInitializer;
		
		else if(ProtocolType.CUSTOM.equals(SystemInfo.getProtocolType()))
			return (ChannelInitializer<SocketChannel>)customServerInitializer;
		
		else{
			String errMsg="undefined protocol:"+SystemInfo.getProtocolType()+"!";
			throw new SysErrException(errMsg);
		}*/
		
	}
}
