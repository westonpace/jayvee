package com.github.westonpace.jayvee.workflow;

import java.util.LinkedList;
import java.util.Queue;

/**
 * A buffer which can be used for {@link OnRequestOnly OnRequestOnly} workers.  When
 * a pop request comes in the iterate method is called on the underlying worker and the
 * result is passed in to the pop request.
 */
public class DirectCallBuffer<T> implements Buffer<T> {

	private boolean finished = false;
	private Queue<T> temporaryBuffer = new LinkedList<T>();
	private Worker  worker;

	public DirectCallBuffer(Worker worker) {
		this.worker = worker;
	}
	
	@Override
	public void push(T value) {
		temporaryBuffer.add(value);
	}

	@Override
	public void end() {
		finished = true;
		throw new BufferEndedException();
	}

	@Override
	public T pop() {
		while(!finished && temporaryBuffer.isEmpty()) {
			worker.iterate();
		}
		if(finished) {
			throw new BufferEndedException();
		}
		return temporaryBuffer.poll();
	}

	@Override
	public void safeEnd() {
		this.finished = true;
	}
	
}
