package com.omen.netty.server.service;

import io.netty.channel.ChannelHandlerContext;


public interface  CustomServerService {

	/**
	 * 
	* @Title: doService
	* @Description: 处理byte请求消息
	* @param @param 
	* @param @return    
	* @return String    
	* @throws
	* @author omen  www.liyidong.com 
	* @date 2014年12月25日 下午2:59:22
	 */
	public  Object doService(ChannelHandlerContext ctx,Object msg)throws Exception ;
	
	
	/**
	 * 
	* @Title: close
	* @Description: 链路关闭时释放在doService方法中创建的，不会随channel关闭而回收但是需要回收的资源回收操作。譬如异常退出时，删除hashtable里的数据等。注：无需在此方法关闭channel
	* @param @param ctx
	* @param @throws Exception    
	* @return void    
	* @throws
	* @author omen  www.liyidong.com 
	* @date 2015年6月17日 下午4:11:49
	 */
	public void close(ChannelHandlerContext ctx, Throwable cause)throws Exception;
	
}
