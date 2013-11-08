package com.github.westonpace.jayvee.workflow;

/**
 * An exception thrown when trying to access a buffer that someone has already
 * called end on (and all other items have emptied out)
 */
public class BufferEndedException extends RuntimeException {

	private static final long serialVersionUID = 5867283612486596875L;
	
}