package com.omen.netty.server.service;

import io.netty.channel.ChannelHandlerContext;

public interface EventTriggerService {
	
	/**
	 * 
	* @Title: userEventTriggered
	* @Description: 自定义user事件触发，如idle事件等,这一块有问题，TODO 最好写在各自service当中
	* @param @param ctx
	* @param @param evt
	* @param @throws Exception    
	* @return void    
	* @throws
	* @author omen  www.liyidong.com 
	* @date 2015年12月21日 上午10:42:27
	 */
	public void userEventTriggered(ChannelHandlerContext ctx, Object evt)  throws Exception;

}
