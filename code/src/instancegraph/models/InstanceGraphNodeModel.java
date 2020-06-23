package instancegraph.models;

import java.awt.Color;
import java.awt.Dimension;

import javax.swing.SwingConstants;

import org.processmining.models.graphbased.AttributeMap;
import org.processmining.models.graphbased.directed.AbstractDirectedGraph;
import org.processmining.models.graphbased.directed.AbstractDirectedGraphNode;
import org.processmining.models.shapes.RoundedRect;

public class InstanceGraphNodeModel extends AbstractDirectedGraphNode{


		private AbstractDirectedGraph<?, ?> _graph;

		public InstanceGraphNodeModel(AbstractDirectedGraph<?, ?> graph, String node)
		{
			_graph = graph;
			
			AttributeMap attr = getAttributeMap();
			attr.put(AttributeMap.LABEL, "<font size=6>" + node + "</font>");
			attr.put(AttributeMap.TOOLTIP, node);
			attr.put(AttributeMap.SHAPE, new RoundedRect());
			attr.put(AttributeMap.LABELVERTICALALIGNMENT, SwingConstants.CENTER);
			attr.put(AttributeMap.STROKECOLOR, Color.red);
			
			int labelWidth = 15 + node.length() * 15;
			attr.put(AttributeMap.SIZE, new Dimension(labelWidth, 30));
			attr.put(AttributeMap.BORDERWIDTH, 2);
			attr.put(AttributeMap.AUTOSIZE, false);
		}

		public AbstractDirectedGraph<?, ?> getGraph()
		{
			return _graph;
		}

		

	}


