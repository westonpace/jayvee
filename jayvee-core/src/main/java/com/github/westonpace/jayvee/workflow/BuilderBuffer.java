package com.github.westonpace.jayvee.workflow;

import java.lang.reflect.Field;


@SuppressWarnings("rawtypes")
class BuilderBuffer implements Source,Sink {

	private Buffer<?> realBuffer;
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

	public void setRealBuffer(Buffer<Object> realBuffer) {
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
