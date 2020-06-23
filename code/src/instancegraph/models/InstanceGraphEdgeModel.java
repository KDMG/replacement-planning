package instancegraph.models;
import java.awt.Color;

import org.processmining.models.graphbased.AttributeMap;
import org.processmining.models.graphbased.directed.AbstractDirectedGraphEdge;

public class InstanceGraphEdgeModel extends AbstractDirectedGraphEdge<InstanceGraphNodeModel, InstanceGraphNodeModel> {

	private static final float[] DashPattern = new float[] {10.0f};
	private static final float[] NoDashPattern = new float[0];
	
	public InstanceGraphEdgeModel(InstanceGraphNodeModel source, InstanceGraphNodeModel target)
	{
		super(source, target);
		
		AttributeMap attr = getAttributeMap();
		attr.put(AttributeMap.EDGEEND, AttributeMap.ArrowType.ARROWTYPE_CLASSIC);
		attr.put(AttributeMap.EDGECOLOR, Color.red);
		attr.put(AttributeMap.DASHPATTERN, DashPattern);
	}


}
