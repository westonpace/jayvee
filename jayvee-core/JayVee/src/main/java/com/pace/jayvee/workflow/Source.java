package com.pace.jayvee.workflow;

/**
 * This is the basic interface for a source of data.  Items can be popped out of a source.
 * 
 * Sources may block if there is no data yet to be processed.
 * 
 * @see Sink
 * @see Worker
 *
 * @param <T> The type of item to be pulled from the source
 */
public interface Source<T> {

	/**
	 * Reads the next item from the source.  This may block until an item is available and
	 * will throw a BufferEndedException if the source is finished and will never have any
	 * more items.
	 * @return The next item
	 * @throws BufferEndedException This is thrown when there are no more items to be read
	 */
	public T pop();
	
}
