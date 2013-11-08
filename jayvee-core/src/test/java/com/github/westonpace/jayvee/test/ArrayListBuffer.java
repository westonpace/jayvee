package com.github.westonpace.jayvee.test;

import java.util.ArrayList;

import com.github.westonpace.jayvee.workflow.Sink;
import com.github.westonpace.jayvee.workflow.Source;

/**
 * For testing it is sometimes easiest to treat the buffer as an arraylist
 */
public class ArrayListBuffer<T> extends ArrayList<T> implements Source<T>,Sink<T> {

	private static final long serialVersionUID = 6949935670425462580L;
	private boolean ended = false;
	
	@Override
	public void push(T value) {
		this.add(value);
	}

	@Override
	public void end() {
		ended = true;
	}

	@Override
	public T pop() {
		return super.remove(0);
	}

	public boolean isEnded() {
		return ended;
	}
	
}
