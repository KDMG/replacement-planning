package socialnet;

import java.util.ArrayList;

import org.jgrapht.graph.DefaultWeightedEdge;

public class HandoverEdge extends
DefaultWeightedEdge
{
private ArrayList<Handover> list;


/**
 * Constructs a relationship edge
 *
 * @param label the label of the new edge.
 * 
 */
public HandoverEdge(ArrayList<Handover> list)
{
    this.list = list;
}

/**
 * Gets the label associated with this edge.
 *
 * @return edge label
 */
public ArrayList<Handover> getList()
{
    return list;
}

public void filter(Float param) {
	
	ArrayList<Handover> triple = new ArrayList<Handover>();
	for (Handover item: list) {
		if (item.getHandoverOfWork()>= param) triple.add(item);
	}
	this.list = triple;
}

@Override
public String toString()
{
	String lista = "";
	for (Handover triple: list) {
		lista = lista + " (" + triple.getFirstActivity() + ", " + triple.getSecondActivity() + ", " + String.valueOf(triple.getHandoverOfWork()) + ") ";
	}
    return  lista;
}
}
