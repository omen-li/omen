package com.omen.netty.server.sysPojo;

import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;

import com.omen.netty.utils.StringUtil;

public class ServerInfo {
	
	private static Logger log = Logger.getLogger(ServerInfo.class);
	
	private  String protocolType;

	private   Integer port;
	
	private  Boolean isSsl;
	
	private  String jksPath;
	
	private   String jksPwd;
	
	private  Integer channelType;

	private   ApplicationContext ctx;
	
	private  Integer bossGroupSize;
	
	private  Integer workerGroupSize;
	
	private EventLoopGroup bossGroup;
	
	private EventLoopGroup workerGroup;
	
	public void shutDownGraceFully(){
		bossGroup.shutdownGracefully();
		workerGroup.shutdownGracefully();
	}
	public  void printSysInfo(){
		long threadId = Thread.currentThread().getId();
		
		log.info("**************SYSTEM [" + threadId + "] INFO******************");
		log.info("[" + threadId + "] protocolType  : " + protocolType);
		log.info("[" + threadId + "] port          : " + port);
		log.info("[" + threadId + "] channelType   : " + channelType + " (0=NIO 1=OIO)");
		log.info("[" + threadId + "] isSsl         : " + isSsl);
		if(!StringUtil.isEmpty(jksPath))
			log.info("[" + threadId + "] jksPath       : " + jksPath);
		if(!StringUtil.isEmpty(jksPwd))
			log.info("[" + threadId + "] jksPwd        : " + jksPwd);
		if(bossGroupSize!=null)
			log.info("[" + threadId + "] bossGroupSize : " + bossGroupSize);
		if(workerGroupSize!=null)
			log.info("[" + threadId + "] workerGroupSize: " + workerGroupSize);
		log.info("**************SYSTEM [" + threadId + "] INFO******************");
	}
	
	


	public  String getProtocolType() {
		return protocolType;
	}
	
	public  void setProtocolType(String protocolType) {
		this.protocolType = protocolType.toUpperCase();
	}
	
	public  Integer getPort() {
		return port;
	}

	public  void setPort(Integer port) {
		this.port = port;
	}

	public  Boolean getIsSsl() {
		return isSsl;
	}

	public  void setIsSsl(Boolean isSsl) {
		this.isSsl = isSsl;
	}

	public  String getJksPath() {
		return jksPath;
	}

	public  void setJksPath(String jksPath) {
		this.jksPath = jksPath;
	}

	public  String getJksPwd() {
		return jksPwd;
	}

	public  void setJksPwd(String jksPwd) {
		this.jksPwd = jksPwd;
	}

	public  Integer getChannelType() {
		return channelType;
	}

	public  void setChannelType(Integer channelType) {
		this.channelType = channelType;
	}
	
	public  ApplicationContext getCtx() {
		return ctx;
	}
	
	public  void setCtx(ApplicationContext ctx) {
		this.ctx = ctx;
	}

	public  Integer getBossGroupSize() {
		return bossGroupSize;
	}

	public  void setBossGroupSize(Integer bossGroupSize) {
		this.bossGroupSize = bossGroupSize;
	}
	

	public  Integer getWorkerGroupSize() {
		return workerGroupSize;
	}

	public  void setWorkerGroupSize(Integer workerGroupSize) {
		this.workerGroupSize = workerGroupSize;
	}
	
	public EventLoopGroup getBossGroup() {
		return bossGroup;
	}
	
	public void setBossGroup(EventLoopGroup bossGroup) {
		this.bossGroup = bossGroup;
	}
	
	public EventLoopGroup getWorkerGroup() {
		return workerGroup;
	}
	
	public void setWorkerGroup(EventLoopGroup workerGroup) {
		this.workerGroup = workerGroup;
	}
	
	public static void main(String[] args) {
		
	}


	

}
