package com.github.westonpace.jayvee.workflow;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import com.github.westonpace.jayvee.util.RuntimeInterruptedException;
import com.github.westonpace.jayvee.workflow.StandardWorkerRunnable.WorkerRunnableEndListener;

/**
 * <p>
 * Controller for a collection of workers.  This class represents a set of workers
 * whose sources and sinks are connected by buffers.  This is the main driver for the
 * JayVee workflow architecture.  This should be created by a {@link SystemBuilder SystemBuilder}
 *  and once created, can be started, stopped, and joined.
 * </p><p>
 * When it is running it makes each of the workers iterate as fast as possible (unless 
 * that worker has been marked {@link OnRequestOnly OnRequestOnly}).  Once a worker has
 * ended its output buffer then any workers who read from that output buffer will soon
 * run out of input and end their output buffers.  Once all terminating buffers have
 * ended then the system will stop naturally.
 * </p><p>
 * If the system is forcefully stopped by a call to {@link #interrupt() interrupt} then 
 * it will trigger an InterruptedException on all of the workers.
 * </p>
 */
public class WorkflowSystem implements WorkerRunnableEndListener {

	private WorkerGraph workerGraph;
	private ExecutorService executorService = Executors.newCachedThreadPool();

	WorkflowSystem(WorkerGraph workerGraph) {
		this.workerGraph = workerGraph;
	}
	
	/**
	 * Starts the system.  Once started all buffers will begin iterating and
	 * taking data from their sources to push into their sinks.
	 */
	public void start() {
		for(WorkerRunnable workerRunnable : workerGraph.getWorkers()) {
			workerRunnable.setEndListener(this);
			executorService.execute(workerRunnable);
		}
	}
	
	/**
	 * Interrupts all of the workers in the system.
	 */
	public void interrupt() {
		executorService.shutdownNow();
	}
	
	/**
	 * Waits for the workers in the system to finish.  The system is finished when all
	 * workers have one or more sources which have ended.  Workers with only sinks don't
	 * count towards this check.
	 * 
	 * @throws RuntimeInterruptedException If this thread is interrupted while waiting
	 */
	public void join() {
		try {
			executorService.awaitTermination(999999, TimeUnit.DAYS);
		} catch (InterruptedException ex) {
			throw new RuntimeInterruptedException(ex);
		}
	}

	@Override
	public void workerEnded(WorkerRunnable worker) {
		workerGraph.poisonEdges(worker);
		if(workerGraph.isEnded()) {
			this.interrupt();
		}
	}
	
}
