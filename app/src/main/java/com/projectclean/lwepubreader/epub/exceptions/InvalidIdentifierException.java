package com.projectclean.lwepubreader.epub.exceptions;

public class InvalidIdentifierException extends EPUBException{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1992772920896048964L;
	
	public InvalidIdentifierException() {
		super();
	}

	public InvalidIdentifierException(String message) {
		super(message);
	}

	public InvalidIdentifierException(Throwable cause) {
		super(cause);
	}

}
