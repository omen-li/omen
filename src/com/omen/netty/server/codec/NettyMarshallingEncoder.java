package com.omen.netty.server.codec;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.marshalling.MarshallerProvider;
import io.netty.handler.codec.marshalling.MarshallingEncoder;

/**
 * 
* 类说明
* @Description: 扩展MarshallingEncoder 和 MarshallingDecoder，将protected方法编程public可以调用 
* @author  omen  www.liyidong.com
* @date 2016年1月29日 上午11:10:55
* @version V1.0
 */
public class NettyMarshallingEncoder extends MarshallingEncoder {

	public NettyMarshallingEncoder(MarshallerProvider provider) {
		super(provider);
	}

	public void encode(ChannelHandlerContext ctx, Object msg, ByteBuf out) throws Exception{
		super.encode(ctx, msg, out);
	}
}
