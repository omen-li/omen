package com.omen.netty.server.sysPojo;

import java.util.List;

import org.springframework.context.ApplicationContext;

public class ServerList {

	
	private static List<ServerInfo> servers;
	
	private  static ApplicationContext ctx;
	
	public static void update(int serverNo,ServerInfo serverInfo){
		servers.set(serverNo, serverInfo);
	}
	
	public static ServerInfo getServerInfo(int serverNo){
		return servers.get(serverNo);
	}
	
	public static void shutDownGraceFully(int serverNo){
		servers.get(serverNo).shutDownGraceFully();
	}

	public static Integer getServerNum() {
		return servers.size();
	}

//	public static void setServerNum(Integer serverNum) {
//		ServerList.serverNum = serverNum;
//	}

	public static List<ServerInfo> getServers() {
		return servers;
	}
	
	public static void setServers(List<ServerInfo> servers) {
		ServerList.servers = servers;
	}
	
	public static ApplicationContext getCtx() {
		return ctx;
	}
	
	public static void setCtx(ApplicationContext ctx) {
		ServerList.ctx = ctx;
	}
	
	


}
