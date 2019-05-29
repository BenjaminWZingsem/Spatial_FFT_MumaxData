package main;

import java.awt.Color;

import javax.swing.JFrame;

import de.erichseifert.gral.data.DataTable;
import de.erichseifert.gral.plots.XYPlot;
import de.erichseifert.gral.plots.lines.DefaultLineRenderer2D;
import de.erichseifert.gral.plots.lines.LineRenderer;
import de.erichseifert.gral.ui.InteractivePanel;

public class LinePlotTest extends JFrame {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	DataTable data;

	public LinePlotTest(DataTable data) {
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setSize(800, 600);		
		this.data = data;
		XYPlot plot = new XYPlot(data);
		
		Color color = new Color(0.0f, 0.3f, 1.0f);
		LineRenderer lines = new DefaultLineRenderer2D();
		lines.setColor(color);
        plot.setLineRenderers(data, lines);
        plot.getPointRenderers(data).get(0).setColor(color);
		getContentPane().add(new InteractivePanel(plot));
	}
}
