package com.acciaccatura.rational.exceptions;

public class InvalidValueException extends RuntimeException{

	private static final long serialVersionUID = -5603662549207602163L;
	
	public InvalidValueException(String message) {
		super(message);
	}
	
	public InvalidValueException() {
		super();
	}

}
