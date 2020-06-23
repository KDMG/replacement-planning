package instancegraph.gui;


import javax.swing.JComponent;
import javax.swing.JPanel;

import org.deckfour.uitopia.api.event.TaskListener.InteractionResult;
import org.deckfour.xes.model.XLog;
import org.processmining.contexts.uitopia.UIPluginContext;

public class WizardUtil {

	public static class WizardEntry {

		private final String _title;
		private final Class<? extends JComponent> _panel;

		public WizardEntry(String title, Class<? extends JComponent> panel) {
			_title = title;
			_panel = panel;
		}

		public String getTitle() {
			return _title;
		}

		public Class<? extends JComponent> getPanel() {
			return _panel;
		}
	}
	
	private static final JPanel waitPanel = new WizardWaitPanel();

	public static InteractionResult runWizard(UIPluginContext context, WizardEntry[] pages,
			XLog log) {
		try
		{
			final int numPages = pages.length;
			int currentPage = 0;
	
			InteractionResult result = null;
			boolean done = false;
	
			while (!done) {
				WizardEntry entry = pages[currentPage];
				
				context.getGlobalContext()
					.getController()
					.getMainView()
					.showOverlay(waitPanel);
				
				JComponent panel = entry.getPanel().newInstance();
				if (panel instanceof IWizardPanel) {
					((IWizardPanel)panel).init(log);
				}
	
				result = context.showWizard(
					entry.getTitle(), 
					currentPage == 0, 
					currentPage == numPages - 1,
					panel
				);
	
				switch (result) {
					case CANCEL :
					case FINISHED :
						done = true;
						break;
	
					case NEXT :
					case CONTINUE :
						done = false;
						currentPage++;
						break;
	
					case PREV :
						done = false;
						currentPage--;
						break;
				}
			}
	
			return result;
		}
		catch (InstantiationException e)
		{
			throw new RuntimeException(e);
		}
		catch (IllegalAccessException e)
		{
			throw new RuntimeException(e);
		}
	}
}
