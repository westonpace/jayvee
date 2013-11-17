package com.github.westonpace.jayvee.util;

import com.github.westonpace.jayvee.workflow.OnRequestOnly;
import com.github.westonpace.jayvee.workflow.OutputBuffer;
import com.github.westonpace.jayvee.workflow.Sink;
import com.github.westonpace.jayvee.workflow.StandardWorker;

/**
 * Spits out a given constant value over and over.  It can be useful whenever you want to 
 * hardcode the input to some buffer.  Since this source could spit out the constant 
 * value forever it operates in a @OnRequestOnly fashion.
 */
@OnRequestOnly
public class ConstantSource<T> extends StandardWorker {

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

}
