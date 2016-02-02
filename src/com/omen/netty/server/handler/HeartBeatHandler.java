package com.omen.netty.server.handler;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelHandler.Sharable;

import org.apache.log4j.Logger;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import com.omen.netty.utils.StringUtil;

/**
 * 
* 类说明
* @Description: 检测心跳，服务端不返回数据
* @author  omen  www.liyidong.com
* @date 2015年12月23日 下午3:54:50
* @version V1.0
 */
@Sharable
@Service()@Scope("prototype")
public class HeartBeatHandler extends ChannelHandlerAdapter {

	private static Logger log = Logger.getLogger(HeartBeatHandler.class);

	//业务层服务器接收心跳值
	private static final int HEARTBEAT_REC=0;
	
	//业务层服务器返回心跳值
	private static final int HEARTBEAT_RES=0;
	
	@Override
	public void channelRead(ChannelHandlerContext ctx,Object msg)throws Exception{
		ByteBuf buf = (ByteBuf)msg;
		byte[] req = new byte[buf.readableBytes()];
		buf.readBytes(req);
		if(req.length==0){
			log.debug("channelid[" + ctx.channel().id().asLongText() +"] receive an empty msg,drop it");
		}
		
		else if(req.length==1 &&req[0]==HEARTBEAT_REC ){
			log.debug("channelid[" + ctx.channel().id().asLongText() +"] receive one heartBeat");
//			byte[] heartBeat =buildHeartBeat();
//			ByteBuf resp = Unpooled.copiedBuffer(heartBeat);
//			ctx.writeAndFlush(resp);
			
		}else{
			//如果不是心跳则传递给下一次
			log.debug("channelid[" + ctx.channel().id().asLongText() +"] receive one msg(not hearBeat),trans to next handler...");
			log.debug("heartBeat read Bytes:" + StringUtil.parseByte2HexStr(req));
			ctx.fireChannelRead(req);
		}
	}
	
	private byte[] buildHeartBeat(){
		byte[] heartBeat = new byte[1];
		heartBeat[0] = HEARTBEAT_RES;
		return heartBeat;
	}
	
	public static void main(String[] args) {
		byte[] req = new byte[1];
		req[0] = (byte)0;
		System.out.println(req[0]==0);
	}
}
