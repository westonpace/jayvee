package com.github.westonpace.jayvee.workflow;

/**
 * A standard worker implementation which provides logic for signalling when a worker
 * has ended.
 */
public abstract class StandardWorker implements Worker {

	private boolean ended = false;
	
	@Override
	public boolean isEnded() {
		return ended;
	}
	
	/**
	 * <p>
	 * Ends this worker.  Called by subclasses when they can no longer iterate.
	 * </p><p>
	 * Most classes will not need to implement this.
	 * </p><p>
	 * @see Worker#isEnded
	 * </p>
	 */
	protected void end() {
		ended = true;
	}
	
	@Override
	public void init() {
		//The default init does nothing
	}
}
