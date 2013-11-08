package com.github.westonpace.jayvee.workflow;

import com.github.westonpace.jayvee.workflow.StandardWorkerRunnable.WorkerRunnableEndListener;

public class WorklessWorkerRunnable implements WorkerRunnable {

	@Override
	public void run() {
		
	}

	@Override
	public void setEndListener(WorkerRunnableEndListener endListener) {
		
	}

	@Override
	public boolean isFinished() {
		return true;
	}

	@Override
	public void stop() {
		
	}

}
