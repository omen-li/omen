package com.omen.netty.server.codec;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;

import java.util.List;

import org.apache.log4j.Logger;

/**
* 类说明
* @Description: 定义DooyaMsg的Encoder，注意消息长度的计算方法，以及最后把Message传递出去 
* @author  omen  www.liyidong.com
* @date 2016年1月29日 上午11:28:11
* @version V1.0
* 
* 编码依据dooya提供规范：
* 字段	  类型	                                                说明
发送方ID	Byte[8]	                发送方的唯一标志
接收方ID	Byte[8]	               接收方的唯一标志,全部为0x00即认为为推送消息
报文标志	UInt16BE	   最高位表示报文类型
                     0: 控制报文,  1: 传输报文
                                                              最低位表示报文内容是否经过加密
                     0: 未加密,  1: 加密
报文长度	UInt16BE	  包括20字节报文头的总长度
                                                              单个报文总长度控制在4k以内
报文内容	Byte[n]	              作为推送消息时，服务端不做解析直接透传，具体格式由APP和设备端协商完成后添加
 */
public abstract class NettyMsgEncoder extends MessageToMessageEncoder<Object>{
	
	private static Logger log = Logger.getLogger(NettyMsgEncoder.class);

	private NettyMarshallingEncoder marshallingEncoder;
	
	public NettyMsgEncoder(){
		marshallingEncoder = MarshallingCodeCFactory.buildMarshallingEncoder();
	}
	
	@Override
	protected void encode(ChannelHandlerContext ctx, Object msg,
			List<Object> out) throws Exception {
		ByteBuf sendBuf = Unpooled.buffer();
		try {
			log.debug("start to encode msg to byteBuf...msg:" + msg.toString());
			sendBuf = parse2ByteBuf(sendBuf,msg);
			out.add(sendBuf);
			log.debug("encode msg to byteBuf suc!");
			
		} catch (Exception e) {
			log.error(msg.toString());
			log.error("encode dooyaMsg to byteBuf error!", e);
			throw new Exception(e);
		}

	}
	
	abstract public ByteBuf parse2ByteBuf(ByteBuf sendBuf,Object msg) throws Exception;

}