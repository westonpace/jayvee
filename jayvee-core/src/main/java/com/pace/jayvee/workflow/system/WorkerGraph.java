package com.pace.jayvee.workflow.system;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.log4j.Logger;

import com.pace.jayvee.workflow.BlockingHeapBuffer;
import com.pace.jayvee.workflow.Worker;

public class WorkerGraph {

	private static final Logger logger = Logger.getLogger(WorkerGraph.class);
	
	private static class WorkerGraphEdge {
		
		private WorkerGraphNode destination;
		private BlockingHeapBuffer<?> edge;

		public WorkerGraphEdge(WorkerGraphNode destination, BlockingHeapBuffer<?> edge) {
			this.destination = destination;
			this.edge = edge;
		}

	}
	
	private static class WorkerGraphNode {
	
		private List<WorkerGraphEdge> edges = new ArrayList<WorkerGraphEdge>();
		
		public void addEdge(WorkerGraphNode destination, BlockingHeapBuffer<?> edge) {
			this.edges.add(new WorkerGraphEdge(destination, edge));
		}
		
		public boolean isLeaf() {
			return edges.isEmpty();
		}
		
	}
	
	private Map<WorkerRunnable, WorkerGraphNode> graph = new HashMap<WorkerRunnable, WorkerGraphNode>();
	private Map<Worker, WorkerRunnable> workerMap = new HashMap<Worker, WorkerRunnable>();
	
	public void addNode(Worker worker) {
		logger.debug("Adding node for: " + worker.getClass().getSimpleName());
		WorkerRunnable runnable = new WorkerRunnable(worker);
		workerMap.put(worker, runnable);
		graph.put(runnable, new WorkerGraphNode());
	}
	
	public void addEdge(Worker source, BlockingHeapBuffer<?> edge, Worker destination) {
		logger.debug("Adding edge for " + source.getClass().getSimpleName() + " and " + destination.getClass().getSimpleName());
		WorkerRunnable sourceRunnable = workerMap.get(source);
		WorkerRunnable destinationRunnable = workerMap.get(destination);
		graph.get(sourceRunnable).addEdge(graph.get(destinationRunnable), edge);
	}
	
	public Set<WorkerRunnable> getWorkers() {
		return graph.keySet();
	}
	
	public void poisonEdges(WorkerRunnable worker) {
		WorkerGraphNode node = graph.get(worker);
		for(WorkerGraphEdge edge : node.edges) {
			edge.edge.safeEnd();
		}
	}
	
	//The graph is finished if all leaf nodes are finished
	public boolean isEnded() {
		for(Entry<WorkerRunnable, WorkerGraphNode> nodeEntry : graph.entrySet()) {
			if(nodeEntry.getValue().isLeaf() && !nodeEntry.getKey().isFinished()) {
				return false;
			}
		}
		return true;
	}
}
