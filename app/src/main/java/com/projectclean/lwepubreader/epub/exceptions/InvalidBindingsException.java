package com.projectclean.lwepubreader.epub.exceptions;

public class InvalidBindingsException extends EPUBException {

	private static final long serialVersionUID = 6969927290491050300L;
	
	public InvalidBindingsException() {
		super();
	}

	public InvalidBindingsException(String message) {
		super(message);
	}

	public InvalidBindingsException(Throwable cause) {
		super(cause);
	}
	
}
