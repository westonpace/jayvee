package com.github.westonpace.jayvee.util;


/**
 * Iterates through a list of values, allowing each one to be written.
 */
public interface Sinkerator<T> {

	/**
	 * Writes in the next value and advances
	 * @param value The value to set
	 */
	public void putNext(T value);
	
	/**
	 * Skips the next value, leaving it as is
	 */
	public void skipNext();
	
	/**
	 * Returns whether or not there is another value.
	 * @return true if there is a next value, false otherwise
	 */
	public boolean hasNext();
	
}
