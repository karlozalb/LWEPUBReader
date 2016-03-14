package com.projectclean.lwepubreader.epub.exceptions;

public class InvalidMetadataException extends EPUBException{
	
	private static final long serialVersionUID = 7411786345037448767L;
	
	public InvalidMetadataException() {
		super();
	}

	public InvalidMetadataException(String message) {
		super(message);
	}

	public InvalidMetadataException(Throwable cause) {
		super(cause);
	}
}
