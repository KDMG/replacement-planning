package instancegraph.models;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.swing.SwingConstants;

import org.jgrapht.graph.DefaultEdge;
import org.processmining.models.graphbased.AttributeMap;
import org.processmining.models.graphbased.directed.AbstractDirectedGraph;
import org.processmining.models.graphbased.directed.DirectedGraph;
import org.processmining.models.graphbased.directed.DirectedGraphEdge;
import org.processmining.models.graphbased.directed.DirectedGraphElement;
import org.processmining.models.graphbased.directed.DirectedGraphNode;

public class InstanceGraphModel extends AbstractDirectedGraph<InstanceGraphNodeModel, InstanceGraphEdgeModel> {


	private Set<InstanceGraphNodeModel> _nodes;
	private Set<InstanceGraphEdgeModel> _edges;

	public InstanceGraphModel(org.jgrapht.DirectedGraph<String, DefaultEdge> instanceGraph){
		HashMap<String, InstanceGraphNodeModel> mapNodes= new HashMap<String, InstanceGraphNodeModel>();
		_nodes = new HashSet<InstanceGraphNodeModel>();
		int i = 0;
		for (String node : instanceGraph.vertexSet())
		{
			InstanceGraphNodeModel graphNode = new InstanceGraphNodeModel(this, node);
			_nodes.add(graphNode);
			mapNodes.put(node, graphNode);
			i++;
		}
		
		_edges = new HashSet<InstanceGraphEdgeModel>();
		for (org.jgrapht.graph.DefaultEdge edge : instanceGraph.edgeSet())
		{
			InstanceGraphEdgeModel graphEdge = new InstanceGraphEdgeModel(
				mapNodes.get(instanceGraph.getEdgeSource(edge)), 
				mapNodes.get(instanceGraph.getEdgeTarget(edge))
			);
			_edges.add(graphEdge);
		}
		AttributeMap attr = getAttributeMap();
		attr.put(AttributeMap.PREF_ORIENTATION, SwingConstants.WEST);
	}
	
	public InstanceGraphModel()
	{
		_nodes = new HashSet<InstanceGraphNodeModel>();
		_edges = new HashSet<InstanceGraphEdgeModel>();
		AttributeMap attr = getAttributeMap();
		attr.put(AttributeMap.PREF_ORIENTATION, SwingConstants.WEST);
		
	}
	
	public Set<InstanceGraphNodeModel> getNodes()
	{
		return _nodes;
	}

	public Set<InstanceGraphEdgeModel> getEdges()
	{
		// TODO Auto-generated method stub
		return _edges;
	}

	public void removeNode(DirectedGraphNode cell)
	{
		throw new UnsupportedOperationException();
	}

	public void removeEdge(@SuppressWarnings("rawtypes") DirectedGraphEdge edge)
	{
		throw new UnsupportedOperationException();
	}

	protected AbstractDirectedGraph<InstanceGraphNodeModel, InstanceGraphEdgeModel> getEmptyClone()
	{
		throw new UnsupportedOperationException();
	}

	protected Map<? extends DirectedGraphElement, ? extends DirectedGraphElement> cloneFrom(
			DirectedGraph<InstanceGraphNodeModel, InstanceGraphEdgeModel> graph)
	{
		throw new UnsupportedOperationException();
	}

}
