package com.omen.netty.server;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.omen.netty.server.sysPojo.ServerInfo;
import com.omen.netty.server.sysPojo.ServerList;

public class Main {

	private static Logger log = Logger.getLogger(Main.class);
	 @Autowired
	 private ServerInfo systemInfo;
	
//	private static ApplicationContext ctx;
	static {
		// 先加载spring
		log.info("准备载入spring...");
		ServerList.setCtx(new ClassPathXmlApplicationContext("omen.xml"));
		log.info("载入spring 完毕...");
	}

	public static void main(String[] args) throws Exception {
		Integer serverNum = ServerList.getServerNum();
		
		for(int serverNo=0;serverNo<serverNum;serverNo++){
			new Thread(new Main().new ServerThread(serverNo)).start();
			log.info("单次server开始启动...");
		}
	}
	
	/**
	 * 
	* 类说明
	* @Description: 内部类，并发启动server用
	* @author  omen  www.liyidong.com
	* @date 2015年11月5日 下午3:33:21
	* @version V1.0
	 */
	class ServerThread implements Runnable{
		int serverNo=0;
		
		public ServerThread(int serverNo){
			this.serverNo = serverNo;
		}
		
		@Override
		public void run() {
			try {
				IServer iServer = (IServer) ServerList.getCtx().getBean("basicServer");
				iServer.start(serverNo);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				log.error("error when startup server thread to create new server["+ serverNo+"]", e);
			}
		}
	}
}
