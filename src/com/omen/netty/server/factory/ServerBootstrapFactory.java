package com.omen.netty.server.factory;


import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.oio.OioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.oio.OioServerSocketChannel;

import com.omen.netty.server.sysPojo.ChannelType;
import com.omen.netty.server.sysPojo.ServerInfo;
import com.omen.netty.server.sysPojo.ServerList;


/**
 * @author liyidong       
 * @version 1.0     
 * @created 2014-12-16 上午11:14:33 
 * @function:工厂模式  server服务器 ServerBootstrap创建,同时将线程组配置入serverList
 */
public class ServerBootstrapFactory {
	private ServerBootstrapFactory() {
    }
    public static ServerBootstrap createServerBootstrap(int serverNo) throws UnsupportedOperationException {
    	ServerInfo serverInfo = ServerList.getServerInfo(serverNo);
    	ServerBootstrap serverBootstrap = new ServerBootstrap();
        switch (serverInfo.getChannelType()) {
            case ChannelType.NIO:
            	EventLoopGroup bossGroup = new NioEventLoopGroup();
            	EventLoopGroup workerGroup = new NioEventLoopGroup();
            	serverBootstrap.group(bossGroup, workerGroup);
            	serverInfo.setBossGroup(bossGroup);
            	serverInfo.setWorkerGroup(workerGroup);
            	ServerList.update(serverNo, serverInfo);
            	
            	serverBootstrap.channel(NioServerSocketChannel.class);
            	//连接的最大队列长度。如果队列满时收到连接指示，则拒绝该连接，建议生产代码适当放大数值
            	serverBootstrap.option(ChannelOption.SO_BACKLOG, 500);
            	serverBootstrap.childOption(ChannelOption.SO_KEEPALIVE, true);
//              serverBootstrap.childOption(ChannelOption.TCP_NODELAY,true);TCP默认关闭着的，如果存在有很多小包连续write,write再read的情况，开启nodelay比较合适
//            	serverBootstrap.option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 3000);客户端使用，客户端发起链接时超时时间
            	
            	return serverBootstrap;
            //TODO
            case ChannelType.OIO:
            	serverBootstrap.group(new OioEventLoopGroup());
            	serverBootstrap.channel(OioServerSocketChannel.class);
                 
                return serverBootstrap;
            default:
                throw new UnsupportedOperationException("Failed to create ServerBootstrap,  " + serverInfo.getChannelType() + " not supported!");
        }
    }
}
