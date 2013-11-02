package com.pace.jayvee.workflow.system;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import com.pace.jayvee.workflow.system.WorkerRunnable.WorkerRunnableEndListener;

public class WorkflowSystem implements WorkerRunnableEndListener {

	private WorkerGraph workerGraph;
	private ExecutorService executorService = Executors.newCachedThreadPool();
	
	public WorkflowSystem(WorkerGraph workerGraph) {
		this.workerGraph = workerGraph;
	}
	
	public void start() {
		for(WorkerRunnable workerRunnable : workerGraph.getWorkers()) {
			workerRunnable.setEndListener(this);
			executorService.execute(workerRunnable);
		}
	}
	
	public void interrupt() {
		executorService.shutdownNow();
	}
	
	public void join() throws InterruptedException {
		executorService.awaitTermination(999999, TimeUnit.DAYS);
	}

	@Override
	public void workerEnded(WorkerRunnable worker) {
		workerGraph.poisonEdges(worker);
		if(workerGraph.isEnded()) {
			this.interrupt();
		}
	}
	
}
