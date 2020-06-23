package instancegraph.gui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.JProgressBar;

import org.deckfour.uitopia.ui.overlay.AbstractOverlayDialog;

import com.fluxicon.slickerbox.colors.SlickerColors;
import com.fluxicon.slickerbox.ui.SlickerDarkProgressBarUI;

public class WizardWaitPanel extends AbstractOverlayDialog
{
	private static final long serialVersionUID = 6132738199904482570L;

	public WizardWaitPanel()
	{
		super("Processing...");
		
		this.setOpaque(false);
		this.setLayout(new BorderLayout());
		this.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));
		JPanel header = new JPanel();
		header.setOpaque(false);
		header.setMinimumSize(new Dimension(400, 40));
		header.setMaximumSize(new Dimension(1000, 40));
		header.setPreferredSize(new Dimension(800, 40));
		header.setLayout(new BorderLayout());
		header.setBorder(BorderFactory.createEmptyBorder());

		JPanel content = new JPanel();
		content.setOpaque(false);
		content.setLayout(new BorderLayout());
		content.setBorder(BorderFactory.createEmptyBorder(20, 10, 20, 10));
		content.add(getPayload(), BorderLayout.CENTER);
		
		JPanel footer = new JPanel();
		footer.setOpaque(false);
		footer.setMinimumSize(new Dimension(400, 40));
		footer.setMaximumSize(new Dimension(1000, 40));
		footer.setPreferredSize(new Dimension(800, 40));
		footer.setLayout(new BoxLayout(footer, BoxLayout.X_AXIS));
		footer.setBorder(BorderFactory.createEmptyBorder());

		add(content, BorderLayout.CENTER);
		add(header, BorderLayout.NORTH);
		add(footer, BorderLayout.SOUTH);
	}
	
	private JPanel getPayload()
	{
		JPanel root = new JPanel();
		
		root.setLayout(new BoxLayout(root, BoxLayout.Y_AXIS));
		root.setBackground(SlickerColors.COLOR_BG_3);
		
		JPanel panelWrapper = new JPanel();
		JPanel panel = new JPanel();
		panel.setLayout(new BorderLayout(10, 10));

		panelWrapper.add(panel);
		root.add(panelWrapper, BorderLayout.CENTER);

		panelWrapper.setBackground(SlickerColors.COLOR_BG_3);
		panel.setBackground(SlickerColors.COLOR_BG_3);
		
		panelWrapper.setAlignmentX(Component.LEFT_ALIGNMENT);
		panel.setAlignmentX(Component.LEFT_ALIGNMENT);
		
		JProgressBar progress = new JProgressBar();
		progress.setUI(new SlickerDarkProgressBarUI());
		progress.setMinimumSize(new Dimension(300, 20));
		progress.setMaximumSize(new Dimension(300, 20));
		progress.setPreferredSize(new Dimension(300, 20));
		progress.setMinimum(0);
		progress.setMaximum(1000);
		progress.setIndeterminate(true);
		panel.add(progress, BorderLayout.CENTER);
		
		return root;
	}
}
