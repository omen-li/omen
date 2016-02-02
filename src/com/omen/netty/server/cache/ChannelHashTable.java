package com.omen.netty.server.cache;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelId;

import java.util.Hashtable;


public class ChannelHashTable {
	
	private static Hashtable<String,Channel> busiTable = new Hashtable<String, Channel>();
	
	private static Hashtable<ChannelId,String> channelTable= new Hashtable<ChannelId, String>();
	
	
	public static Channel getChannel(String busiId){
		return busiTable.get(busiId);
	}
	
	public static String getBusiId(ChannelId channelId){
		return channelTable.get(channelId);
	}
	
	/**
	 * 
	* @Title: put
	* @Description: 这里先检查有没有旧的channel和busiId绑定，如果已有绑定且channelId不一致则关闭旧的channel
	* @param @param busiId
	* @param @param channel    
	* @return void    
	* @throws
	* @author omen  www.liyidong.com 
	* @date 2015年12月4日 上午10:32:58
	 */
	public static void put(String busiId,Channel channel){
		Channel oldChannel = busiTable.get(busiId);
		if(exists(busiId) &&(!oldChannel.id().asLongText().equals(channel.id().asLongText())))
				remove(busiId);
		
		busiTable.put(busiId, channel);
		channelTable.put(channel.id(), busiId);
	}
	
	public static boolean exists(String busiId){
		if(busiTable.containsKey(busiId))
			return true;
		else
			return false;
	}
	
	/**
	 * 
	* @Title: remove
	* @Description: remove之后并关闭channel
	* @param @param channelId    
	* @return void    
	* @throws
	* @author omen  www.liyidong.com 
	* @date 2015年12月4日 上午10:46:57
	 */
	public static void remove(ChannelId channelId){
		if(channelTable.containsKey(channelId)){
			String busiId = channelTable.get(channelId);
			Channel oldChannel = busiTable.get(busiId);
			oldChannel.close();
			busiTable.remove(busiId);
			channelTable.remove(channelId);
		}
	}
	
	/**
	 * 
	* @Title: remove
	* @Description: 删除之后并关闭channel
	* @param @param busiId    
	* @return void    
	* @throws
	* @author omen  www.liyidong.com 
	* @date 2015年12月4日 上午10:48:44
	 */
	public static void remove(String busiId){
		if(busiTable.containsKey(busiId)){
			Channel oldChannel = busiTable.get(busiId);
			oldChannel.close();
			busiTable.remove(busiId);
			channelTable.remove(oldChannel.id());
		}
	}
	
	
	public static boolean existsWritableChannel(String busiId){
		if(busiTable.containsKey(busiId)&&busiTable.get(busiId).isWritable())
			return true;
		else
			return false;
	}
	
	
	public static void main(String[] args) {
		
		String a ="1";
		Integer b = 2;
		
		Hashtable<String, Integer> table = new Hashtable<String, Integer>();
		table.put(a, b);
		b =3;
		
		System.out.println(table.containsValue(2));
		
	}
	

}
