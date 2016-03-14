package com.projectclean.lwepubreader.epub.exceptions;

public class EPUBException extends Exception {
	
	private static final long serialVersionUID = -5127284242063641917L;
	
	private String message = null;
	 
    public EPUBException() {
        super();
    }
 
    public EPUBException(String message) {
        super(message);
        this.message = message;
    }
 
    public EPUBException(Throwable cause) {
        super(cause);
    }
 
    @Override
    public String toString() {
        return message;
    }
 
    @Override
    public String getMessage() {
        return message;
    }
	
}
