package com.pace.jayvee.workflow.system;

import org.apache.log4j.Logger;

import com.pace.jayvee.util.RuntimeInterruptedException;
import com.pace.jayvee.workflow.BlockingHeapBuffer.BufferEndedException;
import com.pace.jayvee.workflow.Worker;

class WorkerRunnable implements Runnable {

	private static final Logger logger = Logger.getLogger(WorkerRunnable.class);
	
	private Worker worker;
	private boolean finished = false;
	private WorkerRunnableEndListener endListener;
	
	public interface WorkerRunnableEndListener {
		
		public void workerEnded(WorkerRunnable worker);
		
	}
	
	public WorkerRunnable(Worker worker) {
		this.worker = worker;
	}
	
	public void setEndListener(WorkerRunnableEndListener endListener) {
		this.endListener = endListener;
	}
	
	@Override
	public void run() {
		logger.debug("Starting runner for: " + worker.getClass().getSimpleName());
		while(!finished) {
			try {
				worker.iterate();
			} catch (BufferEndedException ex) {
				//Thrown when one of the source buffers ends or this worker
				//marked a sink ended.
				logger.debug("Stopping runner (buffer ended) for: " + worker.getClass().getSimpleName());
				stop();
			} catch (RuntimeInterruptedException ex) {
				//Thrown when we get asked to stop
				logger.debug("Stopping runner (interrupted ) for: " + worker.getClass().getSimpleName());
				stop();
			} 
		}
		endListener.workerEnded(this);
	}
	
	public boolean isFinished() {
		return finished;
	}
	
	public void stop() {
		this.finished = true;
	}
}
