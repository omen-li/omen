/**
 * Project Name:nbseOrder
 * File Name:StringUtil.java
 * Package Name:cn.net.nbse.open.util
 * Date:2013-7-23下午2:24:24
 * Copyright (c) 2013, liyidong@nbse.net.cn All Rights Reserved.
 *
*/

package com.omen.netty.utils;

import java.io.UnsupportedEncodingException;

/**
 * ClassName:StringUtil <br/>
 * Function: 处理String的工具类<br/>
 * Reason:	 TODO ADD REASON. <br/>
 * Date:     2013-7-23 下午2:24:24 <br/>
 * @author   Liyidong
 * @version  
 * @since    JDK 1.6
 * @see 	 
 */
public class StringUtil {

	/**
	 * trim:String去空 <br/>
	 * <br/>
	 *
	 * @author Liyidong
	 * @param args
	 * @since JDK 1.6
	 */
	public static String trim(String input) {
		if(input==null){
			input = "";
		} else {
			input = input.trim();
		}
		return input;

	}
	public static void main(String[] args) {
		System.out.println(trim("   aaaa   aaaaa              "));
	}
	
	/**
	 * 
	 * isEmpty:判断String是否为空<br/>
	 *
	 * @author Liyidong
	 * @param input
	 * @since JDK 1.6
	 */
	public static boolean isEmpty(String input){
		if(input != null && !"".equals(input))
			return false;
		else 
			return true;
		
	}
	
	/**
	 * 判断输入的字节数组是否为空
	 * @return boolean 空则返回true,非空则flase
	 */
	public static boolean isEmpty(byte[] bytes){
		return null==bytes || 0==bytes.length;
	}
	
	
	/**
	 * 字节数组转为字符串
	 * @see 如果系统不支持所传入的<code>charset</code>字符集,则按照系统默认字符集进行转换
	 */
	public static String getString(byte[] data, String charset){
		if(isEmpty(data)){
			return "";
		}
		if(isEmpty(charset)){
			return new String(data);
		}
		try {
			return new String(data, charset);
		} catch (UnsupportedEncodingException e) {
			System.out.println("将byte数组[" + data + "]转为String时发生异常:系统不支持该字符集[" + charset + "]");
			return new String(data);
		}
	}
	
	/**
	 * 字符串转为字节数组
	 * @see 如果系统不支持所传入的<code>charset</code>字符集,则按照系统默认字符集进行转换
	 */
	public static byte[] getBytes(String data, String charset){
		data = (data==null ? "" : data);
		if(isEmpty(charset)){
			return data.getBytes();
		}
		try {
			return data.getBytes(charset);
		} catch (UnsupportedEncodingException e) {
			System.out.println("将字符串[" + data + "]转为byte[]时发生异常:系统不支持该字符集[" + charset + "]");
			return data.getBytes();
		}
	}
	
	
	/**
	 * 通过ASCII码将十进制的字节数组格式化为十六进制字符串
	 * @see 该方法会将字节数组中的所有字节均格式化为字符串
	 * @see 使用说明详见<code>formatToHexStringWithASCII(byte[], int, int)</code>方法
	 */
	public static String buildHexStringWithASCII(byte[] data){
		return buildHexStringWithASCII(data, 0, data.length);
	}
	
	/**
	 * 通过ASCII码将十进制的字节数组格式化为十六进制字符串
	 * @see 该方法常用于字符串的十六进制打印,打印时左侧为十六进制数值,右侧为对应的字符串原文
	 * @see 在构造右侧的字符串原文时,该方法内部使用的是平台的默认字符集,来解码byte[]数组
	 * @see 该方法在将字节转为十六进制时,默认使用的是<code>java.util.Locale.getDefault()</code>
	 * @see 详见String.format(String, Object...)方法和new String(byte[], int, int)构造方法
	 * @param data   十进制的字节数组
	 * @param offset 数组下标,标记从数组的第几个字节开始格式化输出
	 * @param length 格式长度,其不得大于数组长度,否则抛出java.lang.ArrayIndexOutOfBoundsException
	 * @return 格式化后的十六进制字符串
	 */
	public static String buildHexStringWithASCII(byte[] data, int offset, int length){
		int end = offset + length;
		StringBuilder sb = new StringBuilder();
		StringBuilder sb2 = new StringBuilder();
		sb.append("\r\n------------------------------------------------------------------------");
		boolean chineseCutFlag = false;
		for(int i=offset; i<end; i+=16){
			sb.append(String.format("\r\n%04X: ", i-offset)); //X或x表示将结果格式化为十六进制整数
			sb2.setLength(0);
			for(int j=i; j<i+16; j++){
				if(j < end){
					byte b = data[j];
					if(b >= 0){ //ENG ASCII
						sb.append(String.format("%02X ", b));
						if(b<32 || b>126){ //不可见字符
							sb2.append(" ");
						}else{
							sb2.append((char)b);
						}
					}else{ //CHA ASCII
						if(j == i+15){ //汉字前半个字节
							sb.append(String.format("%02X ", data[j]));
							chineseCutFlag = true;
							String s = new String(data, j, 2);
							sb2.append(s);
						}else if(j == i&&chineseCutFlag){ //后半个字节
							sb.append(String.format("%02X ", data[j]));
							chineseCutFlag = false;
							String s = new String(data, j, 1);
							sb2.append(s);
						}else{
							sb.append(String.format("%02X %02X ", data[j], data[j + 1]));
							String s = new String(data, j, 2);
							sb2.append(s);
							j++;
						}
					}
				}else{
					sb.append("   ");
				}
			}
			sb.append("| ");
			sb.append(sb2.toString());
		}
		sb.append("\r\n------------------------------------------------------------------------");
		return sb.toString();
	}

	


    /**将二进制转换成16进制 
     * @param buf 
     * @return 
     */  
    public static String parseByte2HexStr(byte buf[]) {  
            StringBuffer sb = new StringBuffer();  
            for (int i = 0; i < buf.length; i++) {  
                    String hex = Integer.toHexString(buf[i] & 0xFF);  
                    if (hex.length() == 1) {  
                            hex = '0' + hex;  
                    }  
                    sb.append(hex.toUpperCase());  
            }  
            return sb.toString();  
    }  
    


    /**将16进制转换为二进制 
     * @param hexStr 
     * @return 
     */  
    public static byte[] parseHexStr2Byte(String hexStr) {  
            if (hexStr.length() < 1)  
                    return null;  
            byte[] result = new byte[hexStr.length()/2];  
            for (int i = 0;i< hexStr.length()/2; i++) {  
                    int high = Integer.parseInt(hexStr.substring(i*2, i*2+1), 16);  
                    int low = Integer.parseInt(hexStr.substring(i*2+1, i*2+2), 16);  
                    result[i] = (byte) (high * 16 + low);  
            }  
            return result;  
    }  
	
}
