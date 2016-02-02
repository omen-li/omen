/**
 * Project Name:
 * File Name:Xmldata.java
 * Package Name:cn.net.nbse.open.message
 * Date:2013-7-26上午9:05:36
 * Copyright (c) 2013, liyidong@nbse.net.cn All Rights Reserved.
 *
*/

package com.omen.netty.message;


/**
 * ClassName:Xmldata <br/>
 * Function: 数据字典 <br/>
 * Reason:	 TODO ADD REASON. <br/>
 * Date:     2013-7-26 上午9:05:36 <br/>
 * @author   Liyidong
 * @version  
 * @since    JDK 1.6
 * @see 	 
 */
public class XmlData {
	
	public static final String CONTENT="content";
	
	public static final String RET_CODE="retCode";
	
	public static final String RET_MSG="retMsg";
	
	public static final String STATUS="status";
	
	public static final int YES=1;
	
	public static final int NO=0;
	
	/**
	 * 成功状态
	 */
	public static final String SUCCESS="0000";
	
	/**
	 * 失败状态
	 */
	public static final String FAIL="1111";
	
	public static final String REPEAT="1112";
	
	public static final String REPEAT_MSG="数据重复!";
	
	public static final String TIME_OUT="1113";
	
	public static final String TIME_OUT_MSG="连接超时!";
	
	public static final String DECODE_ERROR="1114";
	
	public static final String DECODE_ERROR_MSG="数据验签未通过!";
	
	/**
	 * 失败通用提示
	 */
	public static final String FAIL_MSG="系统忙,请稍后再试";
	
	

	
	
}
