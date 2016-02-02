package com.omen.netty.server.codec;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;

import org.apache.log4j.Logger;

/**
 * 
* 类说明
* @Description: 定义DooyaMsgDecoder类，注意设置LengthFieldBasedFrameDecoder的几个重要参数，直接影响到解码的结果 
* @author  omen  www.liyidong.com
* @date 2016年1月29日 上午11:27:20
* @version V1.0
* 
 */
/**
 * 
 * @param maxFrameLength
 *         信息最大长度
 * @param lengthFieldOffset
 *         长度属性的起始（偏移）位,长度属性从哪个字节开始
 * @param lengthFieldLength
 *         长度属性占用字节数
 * @param lengthAdjustment
 *        长度调节值，在总长被定义为包含包头长度时，修正信息长度，比如如果总长包含包头长，而长度字段占用从第19个字节，20个字节，则该字段填-20
 * @param initialBytesToStrip
 *        需要跳过的字节数
 */
public abstract class NettyMsgDecoder extends LengthFieldBasedFrameDecoder{
	
	
	private static Logger log = Logger.getLogger(NettyMsgDecoder.class);

	private NettyMarshallingDecoder marshallingDecoder;
	
	/**
	 * 
	 * @param maxFrameLength
	 *         信息最大长度
	 * @param lengthFieldOffset
	 *         长度属性的起始（偏移）位,长度属性从哪个字节开始
	 * @param lengthFieldLength
	 *         长度属性占用字节数
	 * @param lengthAdjustment
	 *        长度调节值，在总长被定义为包含包头长度时，修正信息长度，比如如果总长包含包头长，而长度字段占用从第19个字节，20个字节，则该字段填-20
	 * @param initialBytesToStrip
	 *        需要跳过的字节数
	 */
	public NettyMsgDecoder(int maxFrameLength, int lengthFieldOffset,
			int lengthFieldLength,int lengthAdjustment, int initialBytesToStrip) {
		super(maxFrameLength, lengthFieldOffset, lengthFieldLength, lengthAdjustment, initialBytesToStrip);
		marshallingDecoder = MarshallingCodeCFactory.buildMarshallingDecoder();
	}
	

	public Object decode(ChannelHandlerContext ctx, ByteBuf in) throws Exception{
		ByteBuf frame = (ByteBuf)super.decode(ctx, in);
		
		/**
		 * 调用父类LengthFieldBasedFramDecoder的解码方法后，返回的就是整包消息或者为空
		 * 如果为空，则说明是个半包消息，直接返回继续由I/O线程读取后续的码流
		 */
		if(frame == null){
			return null;
		}
		
		
		try {
			log.debug("start to decode byteBuf");
			Object msg = parseFromByteBuf(frame);
			
			log.debug("decode byteBuf suc!");
		
			return msg;
		} catch (Exception e) {
			log.error("decode byteBuf to dooyaMsg error", e);
			throw new Exception(e);
		}
	}

	abstract public Object parseFromByteBuf(ByteBuf frame) throws Exception;
	
	public static void main(String[] args) {
		byte a = -126;
		System.out.println(a > 0 ? a : (128 + (128 + a)));
	
		System.out.println(a & 0x01);
		
	}
}