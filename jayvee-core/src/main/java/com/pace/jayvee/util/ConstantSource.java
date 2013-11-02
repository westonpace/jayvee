package com.pace.jayvee.util;

import com.pace.jayvee.workflow.OutputBuffer;
import com.pace.jayvee.workflow.Sink;
import com.pace.jayvee.workflow.Worker;

/**
 * A constant source is given some value and simply spits out that value over
 * and over.  It can be useful whenever you want to hardcode the input to some
 * buffer.  Since this source could spit out the constant value forever it operates
 * in a @OnRequestOnly fashion.
 */
public class ConstantSource<T> implements Worker {

	/**
	 * The buffer which will be outputting the constant value given to this source.
	 */
	@OutputBuffer
	public Sink<T> outputSink;
	
	private T value;
	
	/**
	 * Sets the value which this source should be emitting.
	 * @param value The value to emit
	 */
	public void setValue(T value) {
		this.value = value;
	}
	
	@Override
	public void iterate() {
		outputSink.push(value);
	}

	@Override
	public void init() {
		
	}

}
