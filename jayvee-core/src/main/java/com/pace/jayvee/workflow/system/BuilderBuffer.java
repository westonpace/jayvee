package com.pace.jayvee.workflow.system;

import java.lang.reflect.Field;

import com.pace.jayvee.workflow.BlockingHeapBuffer;
import com.pace.jayvee.workflow.Sink;
import com.pace.jayvee.workflow.Source;
import com.pace.jayvee.workflow.Worker;

@SuppressWarnings("rawtypes")
class BuilderBuffer implements Source,Sink {

	private BlockingHeapBuffer<?> realBuffer;
	private Field sourceField;
	private Worker source;
	
	public BuilderBuffer(Worker source, Field accessorMethod) {
		this.source = source;
		this.sourceField = accessorMethod;
	}
	
	@Override
	public void push(Object value) {
		throw new RuntimeException("You should call build on a Builder before using any objects created by it");
	}

	@Override
	public void end() {
		throw new RuntimeException("You should call build on a Builder before using any objects created by it");
	}

	@Override
	public Object pop() {
		throw new RuntimeException("You should call build on a Builder before using any objects created by it");
	}

	public void setRealBuffer(BlockingHeapBuffer<Object> realBuffer) {
		this.realBuffer = realBuffer;
	}
	
	public boolean hasRealBuffer() {
		return realBuffer != null;
	}
	
	public Worker getWorker() {
		return source;
	}
	
	public void build() {
		try {
			sourceField.set(source, realBuffer);
		} catch (IllegalArgumentException e) {
			throw new RuntimeException(e);
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}
}
