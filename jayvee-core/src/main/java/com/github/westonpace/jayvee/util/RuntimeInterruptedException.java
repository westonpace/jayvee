package com.github.westonpace.jayvee.util;

/**
 * An InterruptedException rethrown as a runtime exception.
 * It can be cumbersome to propagate InterrutpedException's up and down the
 * chain.  This should never have been a checked exception.  The odds of someone
 * doing something significant with an InterruptedException is rare and tends to
 * be only at the highest levels of logic.
 * 
 * This exception simply wraps up an InterruptedException as a runtime exception
 * allowing those higher levels to deal with it if they choose without burdening
 * the entire stack trace with having to throw.
 */
public class RuntimeInterruptedException extends RuntimeException {
	
	private static final long serialVersionUID = 1549942710774072856L;

	/**
	 * Constructs a runtime version of an interrupted exception
	 * @param ex The source interrupted exception
	 */
	public RuntimeInterruptedException(InterruptedException ex) {
		super(ex);
	}
	
}