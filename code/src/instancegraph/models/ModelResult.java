package instancegraph.models;

import java.util.ArrayList;
import java.util.HashMap;

import org.jgraph.graph.DefaultEdge;
import org.jgrapht.DirectedGraph;

public class ModelResult {
	
	private HashMap<Integer, DirectedGraph<String, org.jgrapht.graph.DefaultEdge>> res;

	public ModelResult(HashMap<Integer, DirectedGraph<String, org.jgrapht.graph.DefaultEdge>> res) {
		super();
		this.res = res;
	}

	public HashMap<Integer, DirectedGraph<String, org.jgrapht.graph.DefaultEdge>> getRes() {
		return this.res;
	}

	public void setRes(HashMap<Integer, DirectedGraph<String, org.jgrapht.graph.DefaultEdge>> hashMap) {
		this.res = hashMap;
	}

}
