package com.omen.netty.exception;


public class BusiErrException extends Exception {

	
	public BusiErrException() {
		
		super();
		
	}
	
	public BusiErrException(String msg) {
		
		super(msg);
		
	}
	
	public BusiErrException(String msg, Throwable cause) {
		
		super(msg, cause);
		
	}
	
	public BusiErrException(Throwable cause ) {
		
		super(cause);
		
	}

}
