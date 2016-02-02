package com.omen.netty.server;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import com.omen.netty.server.factory.ServerChannelFactory;
import com.omen.netty.server.sysPojo.ServerInfo;
import com.omen.netty.server.sysPojo.ServerList;

@Service()@DependsOn({"serverChannelFactory"})@Scope("prototype")
public class BasicServer implements IServer {

    private Channel acceptorChannel;
    
	@Autowired
    ServerChannelFactory serverChannelFactory;
	
	@Override
	public void start(int serverNo)throws  Exception{
		try{
			acceptorChannel = serverChannelFactory.createAcceptorChannel(serverNo);
			acceptorChannel.closeFuture().sync();
		}finally{
			//优雅退出，释放线程组资源
			ServerList.shutDownGraceFully(serverNo);
		}
	}

	@Override
	public void stop(int serverNo)throws  Exception {
		try{
			if(acceptorChannel!=null)
				acceptorChannel.close().addListener(ChannelFutureListener.CLOSE);
		}finally{
			//优雅退出，释放线程组资源
			ServerList.shutDownGraceFully(serverNo);
		}
	}

	@Override
	public void restart(int serverNo)throws  Exception {
		stop(serverNo);
	    start(serverNo);
	}
	
}
