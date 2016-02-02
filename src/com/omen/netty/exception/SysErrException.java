package com.omen.netty.exception;


public class SysErrException extends Exception {

	
	public SysErrException() {
		
		super();
		
	}
	
	public SysErrException(String msg) {
		
		super(msg);
		
	}
	
	public SysErrException(String msg, Throwable cause) {
		
		super(msg, cause);
		
	}
	
	public SysErrException(Throwable cause ) {
		
		super(cause);
		
	}

}
