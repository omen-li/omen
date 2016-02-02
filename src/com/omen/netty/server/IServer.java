package com.omen.netty.server;


public interface IServer {
	
	public void start(int serverNo ) throws Exception;
	
	public void stop(int serverNo) throws Exception;

	public void restart(int serverNo) throws Exception;
}
