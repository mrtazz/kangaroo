package com.kangaroo.task;

public class NoLocationFoundException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1028483503593516953L;


	public NoLocationFoundException() {
		super();
	}

	
	public NoLocationFoundException(String detailMessage) {
		super(detailMessage);
	}
	
}
