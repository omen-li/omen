package com.omen.netty.server.codec;



import io.netty.handler.codec.marshalling.DefaultMarshallerProvider;
import io.netty.handler.codec.marshalling.DefaultUnmarshallerProvider;
import io.netty.handler.codec.marshalling.MarshallerProvider;
import io.netty.handler.codec.marshalling.MarshallingDecoder;
import io.netty.handler.codec.marshalling.UnmarshallerProvider;

import org.jboss.marshalling.MarshallerFactory;
import org.jboss.marshalling.Marshalling;
import org.jboss.marshalling.MarshallingConfiguration;

import com.omen.netty.utils.StringUtil;

/**
 * 
* 类说明
* @Description: 定义MarshallingCodeCFactory工厂类来获取JBoss Marshalling 类 
* @author  omen  www.liyidong.com
* @date 2016年1月29日 上午11:28:49
* @version V1.0
 */
public class MarshallingCodeCFactory {
	public static NettyMarshallingDecoder buildMarshallingDecoder(){
		MarshallerFactory marshallerFactory = Marshalling.getProvidedMarshallerFactory("serial");
		MarshallingConfiguration configuration = new MarshallingConfiguration();
		configuration.setVersion(5);
		UnmarshallerProvider provider = new DefaultUnmarshallerProvider(marshallerFactory, configuration);
		NettyMarshallingDecoder decoder = new NettyMarshallingDecoder(provider, 1024);
		return decoder;
	}
	
	public static NettyMarshallingEncoder buildMarshallingEncoder(){
		MarshallerFactory marshallerFactory = Marshalling.getProvidedMarshallerFactory("serial");
		MarshallingConfiguration configuration = new MarshallingConfiguration();
		configuration.setVersion(5);
		MarshallerProvider provider = new DefaultMarshallerProvider(marshallerFactory, configuration);
		NettyMarshallingEncoder encoder = new NettyMarshallingEncoder(provider);
		return encoder;
	}
	
	public static void main(String[] args) {
		byte id = (byte)0x00;
		byte[] test = {id,id};
		System.out.println(StringUtil.parseByte2HexStr(test));
	}
}

