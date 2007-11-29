package org.urbsat.plugin.ui;

import javax.swing.BorderFactory;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.border.BevelBorder;

import org.orbisgis.geoview.GeoView2D;

/**
 * This class corresponds to the small bottom UrbSAT panel. The one dedicated to
 * the description of the selected menu item.
 */
public class CopyOfDescriptionScrollPane extends JScrollPane {
	private JTextArea jTextArea;

	public CopyOfDescriptionScrollPane(GeoView2D geoview) {
		jTextArea = new JTextArea();
		jTextArea.setLineWrap(true);
		jTextArea.setBorder(BorderFactory
				.createBevelBorder(BevelBorder.LOWERED));
		jTextArea.setEditable(false);

		setViewportView(jTextArea);
	}

	public JTextArea getJTextArea() {
		return jTextArea;
	}
}