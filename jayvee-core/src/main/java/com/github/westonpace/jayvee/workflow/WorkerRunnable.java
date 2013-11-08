package com.github.westonpace.jayvee.workflow;

import com.github.westonpace.jayvee.workflow.StandardWorkerRunnable.WorkerRunnableEndListener;

interface WorkerRunnable extends Runnable {

	public abstract void setEndListener(WorkerRunnableEndListener endListener);

	public abstract boolean isFinished();

	public abstract void stop();

}