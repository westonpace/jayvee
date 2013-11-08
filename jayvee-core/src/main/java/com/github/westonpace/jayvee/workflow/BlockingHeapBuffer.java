package com.github.westonpace.jayvee.workflow;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import com.github.westonpace.jayvee.util.RuntimeInterruptedException;

/**
 * A simple FIFO buffer implementation that keeps its contents
 * in memory and blocks.  It blocks when the buffer is full and the user is trying
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
class BlockingHeapBuffer<T> implements Buffer<T> {

	private static final int DEFAULT_SIZE = 1;
	
	private BlockingQueue<T> queue;
	private boolean finished;
		
	/**
	 * Constructs a new buffer of size 1.
	 */
	public BlockingHeapBuffer() {
		queue = new LinkedBlockingQueue<T>(DEFAULT_SIZE);
	}
	
	/**
	 * Constructs a new buffer of the given size
	 * @param size The capacity of the buffer
	 */
	public BlockingHeapBuffer(int size) {
		queue = new LinkedBlockingQueue<T>(size);
	}
	
	@Override
	public synchronized void push(T value) {
		try {
			while(!finished) {
				if(queue.offer(value)) {
					notifyAll();
					return;
				} else {
					wait();
				}
			}
		} catch (InterruptedException ex) {
			throw new RuntimeInterruptedException(ex);
		}
	}

	@Override
	public synchronized T pop() {
		try {
			while(!finished) {
				T result = queue.poll();
				if(result != null) {
					notifyAll();
					return result;
				}
				wait();
			}
			throw new BufferEndedException();
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
	public synchronized void safeEnd() {
		//TODO: This is ugly and contentious
		finished = true;
		notifyAll();
	}
	
	/**
	 * Ends the buffer.  Inserts a poison token at the end of the buffer's current
	 * queue and then throws a BufferEndedException to whomever it was that called {@code end()}.
	 */
	public void end() {
		safeEnd();
		throw new BufferEndedException();
	}
	
}
