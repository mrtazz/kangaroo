package com.kangaroo;

public class MissingParameterException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -548635734913347050L;


	public MissingParameterException() {
		super();
	}

	
	public MissingParameterException(String detailMessage) {
		super(detailMessage);
	}
	
}
