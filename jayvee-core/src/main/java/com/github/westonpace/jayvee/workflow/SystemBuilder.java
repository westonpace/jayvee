package com.github.westonpace.jayvee.workflow;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;


/**
 * <p>
 * A programmatic interface for building systems and subsystems.
 * Systems are the main driver for the JayVee library, the keep track of a series of
 * workers and run them.  For more detail see the System class.
 * </p><p>
 * To use the SystemBuilder you first instantiate workers, then wire them together
 * using the various connect methods, and finally call build when you're finished.
 * </p><p>
 * For example, your build might look something like:
 * </p>
 * <pre><code>
 * Builder builder = new Builder();
 * Foo fooWorker = builder.buildWorker(Foo.class);
 * Bar barWorker = builder.buildWorker(Bar.class);
 * builder.connect(fooWorker.fooOutput, barWorker.barInput);
 * System system = builder.build();
 * </code></pre>
 */
public class SystemBuilder {

	private List<Worker> workers = new ArrayList<Worker>();
	private List<BuilderBuffer> builderBuffers = new ArrayList<BuilderBuffer>();
	private WorkerGraph workerGraph = new WorkerGraph();
	private boolean finished = false;
	
	private <T extends Worker> T instantiateWorker(Class<T> workerClass) {
		try {
			return workerClass.newInstance();
		} catch (InstantiationException ex) {
			throw new RuntimeException(ex);
		} catch (IllegalAccessException ex) {
			//TODO: Allow private constructors
			throw new RuntimeException("Workers must have a public no-args constructor");
		}
	}
	
	/**
	 * Instantiates a worker with a system builder.  You may wonder why you should depend
	 * on the builder to instantiate the worker and you can't just instantiate it yourself.
	 * This is for several reasons.  
	 * 
	 * The first is that the system could possibly instantiate several instances of your 
	 * worker as it scales up to take advantage of multiple threads.  You should be
	 * comfortable with the fact that it will be instantiating your worker for you.
	 * 
	 * Second, what we return is not actually the same thing you would get from calling
	 * new.  Instead we create a recorder of sorts that will capture all the calls you 
	 * make to configure the worker so that we can make those same calls when we create 
	 * new instances of your worker.
	 * 
	 * Finally, we do some post-processing on your worker to hook it into the system.
	 * 
	 * With that in mind you should probably avoid doing anything clever like keeping a
	 * reference to the created worker for later use.  Obviously that won't be very
	 * useful if JayVee creates 5 more instances of your worker under the hood.
	 * 
	 * TODO: Acutally implement that clever recorder thing we say we implement
	 * 
	 * @param workerClass The class of the worker to instantiate
	 * @return An instantiated worker for you to configure
	 */
	public <T extends Worker> T buildWorker(Class<T> workerClass) {
		if(finished) {
			throw new RuntimeException("This builder has already finished.  See the javadoc for SystemBuilder.build for more details.");
		}
		T result = instantiateWorker(workerClass);
		workers.add(result);
		postProcessWorker(result);
		return result;
	}
	
	private void postProcessWorker(Worker worker) {
		addBuilderBuffers(worker);
		workerGraph.addNode(worker);
	}
	
	private void addBuilderBuffers(Worker worker) {
		List<Field> bufferFields = findBufferFields(worker);
		for(Field field : bufferFields) {
			try {
				BuilderBuffer builderBuffer = new BuilderBuffer(worker, field);
				builderBuffers.add(builderBuffer);
				field.set(worker, builderBuffer);
			} catch (IllegalArgumentException ex) {
				throw new RuntimeException("Buffer accessors should be single arg methods which take a source or a sink", ex);
			} catch (IllegalAccessException ex) {
				//TODO: allow private
				throw new RuntimeException("Buffer accessors should be public", ex);
			}
		}
	}
	
	private List<Field> findBufferFields(Worker worker) {
		List<Field> result = new ArrayList<Field>();
		for(Field field : worker.getClass().getFields()) {
			if(field.isAnnotationPresent(InputBuffer.class)) {
				result.add(field);
			} else if(field.isAnnotationPresent(OutputBuffer.class)) {
				result.add(field);
			}
		}
		return result;
	}
	
	/**
	 * This method connects the endpoints of two workers together.  The buffer size 
	 * defaults to 1.
	 * 
	 * @see SystemBuilder#connect(Sink, Source, int)
	 * @param sink The sink which is outputting data
	 * @param source The source which is receiving the output data
	 */
	public <T> void connect(Sink<? extends T> sink, Source<? extends T> source) {
		connect(sink, source, 1024);
	}
	
	/**
	 * This method connects the endpoints of two workers together.  This basically means
	 * that under the hood we are going to create some kind of buffer where the given
	 * sink is input to the buffer and the given source reads from the buffer.  Then when
	 * the worker calls pop on the source it will receive values which were push'd by the
	 * given sink.
	 * 
	 * The buffer size limits the number of objects that can be stored in memory.  For
	 * example, if you have a really fast producer outputting data into a slower consumer
	 * then objects are going to queue up.  Once that queue fills up then the fast  
	 * producer's calls to push() will start blocking until the slow consumer catches up.
	 * 
	 * @param sink The sink which is outputting data
	 * @param source The source which is receiving the output data
	 * @param bufferSize The maximum size of the buffer between the two
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public <T> void connect(Sink<? extends T> sink, Source<? extends T> source, int bufferSize) {
		if(finished) {
			throw new RuntimeException("This builder has already finished.  See the javadoc for SystemBuilder.build for more details");
		}
		if(!(source instanceof BuilderBuffer)) {
			throw new RuntimeException("First argument to connect does not appear to have been built by Builder.build()");
		} else if (!(sink instanceof BuilderBuffer)) {
			throw new RuntimeException("Second argument to connect does not appear to have been bulit by Builder.build()");
		}
		BuilderBuffer sourceBuilder = (BuilderBuffer) source;
		BuilderBuffer sinkBuilder = (BuilderBuffer) sink;
		
		if(sourceBuilder.hasRealBuffer() || sinkBuilder.hasRealBuffer()) {
			//TODO: Implement multiple source connections, multiple sink connections
			throw new RuntimeException("TO-DO");
		}
		
		Buffer buffer = null;
		
		if(sinkBuilder.getWorker().getClass().isAnnotationPresent(OnRequestOnly.class)) {
			buffer = new DirectCallBuffer(sinkBuilder.getWorker());
		} else {
			buffer = new BlockingHeapBuffer(bufferSize);
		}

		sourceBuilder.setRealBuffer(buffer);
		sinkBuilder.setRealBuffer(buffer);
		
		workerGraph.addEdge(sinkBuilder.getWorker(), buffer, sourceBuilder.getWorker());
		
	}
	
	/**
	 * Builds the actual system being configured.  This should be the last method called
	 * on the SystemBuilder and once it is called this SystemBuilder is unusable.  The
	 * returned system will contain all the workers instantiated by this builder connected
	 * as configured by calls to connect to this builder.
	 * @return A workflow system which operates on the workers configured by this builder
	 */
	public WorkflowSystem build() {
		finished = true;
		for(BuilderBuffer builderBuffer : builderBuffers) {
			builderBuffer.build();
		}
		for(Worker worker : workers) {
			worker.init();
		}
		return new WorkflowSystem(workerGraph);
	}
}
