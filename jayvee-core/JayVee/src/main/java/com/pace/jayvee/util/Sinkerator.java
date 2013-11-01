package com.pace.jayvee.util;


/**
 * An iterator iterates through a list of values, reading them out one at a time.
 * A Sinkerator iterates through a list of values, writing them one at a time.
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
	 * Returns Whether or not there is a nother value.
	 * @return true if there is a next value, false otherwise
	 */
	public boolean hasNext();
	
}
