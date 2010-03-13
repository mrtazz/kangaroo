package com.mobiletsm.routing;

public class NoRouteFoundException extends Exception {

	
	/**
	 * 
	 */
	private static final long serialVersionUID = 7472543981905266714L;


	public NoRouteFoundException() {
		super();
	}

	
	public NoRouteFoundException(String detailMessage) {
		super(detailMessage);
	}
	

}
