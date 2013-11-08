package com.github.westonpace.jayvee.workflow;

/**
 * A source that data can be 'pop'ed from.
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
