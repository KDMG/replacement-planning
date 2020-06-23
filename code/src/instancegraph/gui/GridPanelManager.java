package instancegraph.gui;

import java.awt.GridBagConstraints;

import javax.swing.JComponent;
import javax.swing.JPanel;

public class GridPanelManager implements IPanelManager {

	private JPanel _panel;
	private GridBagConstraints _c;
	private int _maxRows;
	private int _maxColumns;
	
	private AddModifier _addModifier = null;
	
	public interface AddModifier
	{
		void modify(JComponent widget, GridBagConstraints c);
	}

	public GridPanelManager(JPanel panel, GridBagConstraints c, int maxRows, int maxColumns)
	{
		_panel = panel;
		_c = c;
		_maxRows = maxRows;
		_maxColumns = maxColumns;
		
		_c.gridx = 0;
		_c.gridy = 0;
	}

	public boolean add(JComponent widget)
	{
		// setup basis
		_c.gridwidth = 1;
		_c.gridheight = 1;

		// add modifier
		if (_addModifier != null)
		{
			_addModifier.modify(widget, _c);
		}
		
		// check bounds
		if (_maxRows > 0 && _c.gridy > _maxRows)
		{
			return false;
		}
		
		// add widget
		_panel.add(widget, _c);
		
		// advance grid for next item
		for (int i = 0; i < _c.gridwidth; i++)
		{
			_c.gridx = (_c.gridx + 1) % _maxColumns;
			_c.gridy = _c.gridy + (_c.gridx == 0 ? 1 : 0);
		}
		for (int i = 0; i < _c.gridheight - 1; i++)
		{
			_c.gridy++;
		}

		// done
		return true;
	}

	public void setAddModifier(AddModifier addModifier)
	{
		_addModifier = addModifier;
	}

}
