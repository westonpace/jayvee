package com.github.westonpace.jayvee.workflow;

import org.apache.log4j.Logger;

import com.github.westonpace.jayvee.util.RuntimeInterruptedException;

class StandardWorkerRunnable implements WorkerRunnable {

	private static final Logger logger = Logger.getLogger(StandardWorkerRunnable.class);
	
	private Worker worker;
	private boolean finished = false;
	private WorkerRunnableEndListener endListener;
	
	public interface WorkerRunnableEndListener {
		
		public void workerEnded(WorkerRunnable worker);
		
	}
	
	public StandardWorkerRunnable(Worker worker) {
		this.worker = worker;
	}
		
	/* (non-Javadoc)
	 * @see com.pace.jayvee.workflow.WorkerRunnabl#setEndListener(com.pace.jayvee.workflow.WorkerRunnable.WorkerRunnableEndListener)
	 */
	@Override
	public void setEndListener(WorkerRunnableEndListener endListener) {
		this.endListener = endListener;
	}
	
	/* (non-Javadoc)
	 * @see com.pace.jayvee.workflow.WorkerRunnabl#run()
	 */
	@Override
	public void run() {
		logger.debug("Starting runner for: " + worker.getClass().getSimpleName());
		while(!finished) {
			try {
				worker.iterate();
				if(worker.isEnded()) {
					logger.debug("Stopping runner (worker singalled it was finished) for: " + worker.getClass().getSimpleName());
					stop();
				}
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
	
	/* (non-Javadoc)
	 * @see com.pace.jayvee.workflow.WorkerRunnabl#isFinished()
	 */
	@Override
	public boolean isFinished() {
		return finished;
	}
	
	/* (non-Javadoc)
	 * @see com.pace.jayvee.workflow.WorkerRunnabl#stop()
	 */
	@Override
	public void stop() {
		this.finished = true;
	}
}
