package instancegraph.gui;

import javax.swing.JComponent;

public class DefaultPanelManager implements IPanelManager
{
	private JComponent _parent;

	public DefaultPanelManager(JComponent parent)
	{
		_parent = parent;
	}

	public boolean add(JComponent widget)
	{
		_parent.add(widget);
		return true;
	}
}
