package com.pace.jayvee.workflow;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import com.pace.jayvee.util.RuntimeInterruptedException;

/**
 * A blocking heap buffer is a simple FIFO buffer implementation that keeps its contents
 * in memory (hence 'heap') and blocks when the buffer is full and the user is trying
 * to push or when the buffer is empty and the user is trying to pop.
 * 
 * Additionally the buffer can be "end"ed which inserts an end token in and once that
 * token is reached any call to pop() will result in a BufferEndedException being thrown.
 *
 * In addition, the buffer has a fixed capacity supplied when the buffer is constructed.
 * 
 * Typically, JayVee users should not be constructing buffers themselves.  These are 
 * usually created by the system and a user should not rely on any implementation which
 * is being used under the hood.
 *
 * @param <T> The type of value stored in the buffer
 */
public class BlockingHeapBuffer<T> implements Source<T>, Sink<T>{

	private static final int DEFAULT_SIZE = 1;
	
	private BlockingQueue<Item> queue;
	
	/**
	 * An exception thrown when trying to access a buffer that someone has already
	 * called end on (and all other items have emptied out)
	 */
	public static class BufferEndedException extends RuntimeException {

		private static final long serialVersionUID = 5867283612486596875L;
		
	}
	
	private class Item {
		T value;
		
		public Item(T value) {
			this.value = value;
		}
	}
	
	/**
	 * Constructs a new buffer of size 1.
	 */
	public BlockingHeapBuffer() {
		queue = new LinkedBlockingQueue<Item>(DEFAULT_SIZE);
	}
	
	/**
	 * Constructs a new buffer of the given size
	 * @param size The capacity of the buffer
	 */
	public BlockingHeapBuffer(int size) {
		queue = new LinkedBlockingQueue<Item>(size);
	}
	
	@Override
	public void push(T value) {
		try {
			queue.put(new Item(value));
		} catch (InterruptedException ex) {
			throw new RuntimeInterruptedException(ex);
		}
	}

	@Override
	public T pop() {
		try {
			Item result = queue.take();
			if(result.value == null) {
				throw new BufferEndedException();
			}
			return result.value;
		} catch (InterruptedException ex) {
			throw new RuntimeInterruptedException(ex);
		}
	}

	/**
	 * This needs to be cleaned up.  Typically, when one calls end() one gets a 
	 * BufferEndedException.  That is certainly not the best behavior but we are relying
	 * on it to indicate that the worker that called end is no longer processing.
	 * 
	 * This will obviously need to change before we support a multi-sink source (can't
	 * end them all!)
	 * 
	 * This method exists as a way to end a buffer without getting an exception.
	 */
	public void safeEnd() {
		//TODO: This is ugly and contentious
		queue.offer(new Item(null));
	}
	
	/**
	 * Ends the buffer by inserting a poison token at the end of the buffer's current
	 * queue and then throws a BufferEndedException to whomever it was that called end().
	 */
	public void end() {
		safeEnd();
		throw new BufferEndedException();
	}
	
}
