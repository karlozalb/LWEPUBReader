package com.projectclean.lwepubreader.epub.exceptions;

public class UniqueIdentifierNotUniqueException extends EPUBException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1076848213858193820L;

	public UniqueIdentifierNotUniqueException() {
		super();
	}

	public UniqueIdentifierNotUniqueException(String message) {
		super(message);
	}

	public UniqueIdentifierNotUniqueException(Throwable cause) {
		super(cause);
	}
	
}
