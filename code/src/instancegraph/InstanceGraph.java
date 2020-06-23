package instancegraph;

import org.jgrapht.DirectedGraph;

public class InstanceGraph {
/*This class represents a single instance graph*/
	private DirectedGraph graph;
	private int numTrace;
	private String traceId;
	
	public InstanceGraph(DirectedGraph graph, int numTrace, String traceId) {
		super();
		this.graph = graph;
		this.numTrace = numTrace;
		this.traceId = traceId;
	}

	public DirectedGraph getGraph() {
		return graph;
	}

	public void setGraph(DirectedGraph graph) {
		this.graph = graph;
	}

	public int getNumTrace() {
		return numTrace;
	}

	public void setNumTrace(int numTrace) {
		this.numTrace = numTrace;
	}

	public String getTraceId() {
		return traceId;
	}

	public void setTraceId(String traceId) {
		this.traceId = traceId;
	}
	
	
	
}
