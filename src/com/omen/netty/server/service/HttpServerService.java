package com.omen.netty.server.service;

import com.omen.netty.server.http.pojo.HttpParameter;

public interface  HttpServerService {

	/**
	 * 
	* @Title: doService
	* @Description: Http业务请求应答消息处理类，返回String
	* @param @param paramMap 第一个key存放Set<Cookie> cookies
	* @param @return    
	* @return String    
	* @throws
	* @author omen  www.liyidong.com 
	* @date 2014年12月25日 下午2:59:22
	 */
	public  String doService(HttpParameter httpParameter)throws Exception ;
	
}
