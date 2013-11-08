package com.github.westonpace.jayvee.workflow;

/**
 * A data sink that items are pushed into.  Items can be pushed into a sink and when a 
 * source has run out of things to put into the sink the sink can be end()'ed.
 * 
 * Sinks are typically implemented with some kind of buffer and calling sink may block
 * until the buffer is free to accept the data.
 * 
 * Calling end on a sink will not end it immediately.  Rather, it will place a special
 * token at the end of the sink which will explode when processed.  This way you don't 
 * have to worry about ensuring the other side has ready everything in the buffer before
 * you end it and you can safely call end as soon as you have run out of data.
 * 
 * @see Source
 * @see Worker
 *
 * @param <T> The type of item placed in the sink
 */
public interface Sink<T> {

	/**
	 * Inserts the next item into the sink.  May block until the sink is ready to 
	 * accept more data (e.g. a buffer is full)
	 * @param value The item to insert into the sink
	 */
	public void push(T value);
	
	/**
	 * Inserts an 'end' item into a sink.  Once ended a sink is no longer usable.
	 * Once the other side reads the end item they will know that there is no more data
	 * being sent.
	 */
	public void end();
	
}
