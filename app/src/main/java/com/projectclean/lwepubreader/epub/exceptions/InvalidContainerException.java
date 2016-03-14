package com.projectclean.lwepubreader.epub.exceptions;

public class InvalidContainerException extends EPUBException {
	
	private static final long serialVersionUID = -1151586233826818128L;

	public InvalidContainerException() {
	}

	public InvalidContainerException(String message) {
		super(message);
	}

	public InvalidContainerException(Throwable cause) {
		super(cause);
	}

}
