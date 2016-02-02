package com.omen.netty.server.http.pojo;

import java.util.Map;

public class HttpParameter {
	
	public String content;
	
	public String contentType;
	
	public  Map<String,String> parameters;

	public  Map<String,String> cookies;
	
	public String get(String key){
		if(parameters == null)
			return null;
		else
			return parameters.get(key);
	}
	
	public String getCookie(String key){
		if(cookies == null)
			return null;
		else
			return cookies.get(key);
	}
	
	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getContentType() {
		return contentType;
	}
	
	public void setContentType(String contentType) {
		this.contentType = contentType;
	}

	public Map<String, String> getParameters() {
		return parameters;
	}

	public void setParameters(Map<String, String> parameters) {
		this.parameters = parameters;
	}

	public Map<String, String> getCookies() {
		return cookies;
	}

	public void setCookies(Map<String, String> cookies) {
		this.cookies = cookies;
	}
	
	

}
