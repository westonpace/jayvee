package com.github.westonpace.jayvee.workflow;

interface Buffer<T> extends Source<T>, Sink<T> {

	/**
	 * TODO: Get rid of this
	 */
	public void safeEnd();
	
}
