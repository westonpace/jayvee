package com.github.westonpace.jayvee.util;

/**
 * Thrown by workers and systems whenever one of their parameters is incorrect.  
 * Common reasons are that the parameter is out of range or not compatible with other 
 * parameters or some other reason like that.
 */
public class InvalidParameterException extends RuntimeException {

	private static final long serialVersionUID = 8531737518791084611L;

	/**
	 * Constructs a new InvalidParameterException with the given message
	 * @param message A short message explaining the reason for the exception
	 */
	public InvalidParameterException(String message) {
		super(message);
	}
	
	/**
	 * Constructs a new InvalidParameterException with the given message and
	 * inner exception
	 * @param message A short message explaining the reason for the exception
	 * @param cause Another exception which caused this exception
	 */
	public InvalidParameterException(String message, Throwable cause) {
		super(message, cause);
	}
	
}
