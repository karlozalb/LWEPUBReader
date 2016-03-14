package com.projectclean.lwepubreader.epub.exceptions;

public class OptionalElementWithZeroLengthValueException extends EPUBException{

	private static final long serialVersionUID = 58431682281332125L;

	public OptionalElementWithZeroLengthValueException() {
		super();
	}

	public OptionalElementWithZeroLengthValueException(String message) {
		super(message);
	}

	public OptionalElementWithZeroLengthValueException(Throwable cause) {
		super(cause);
	}

}
