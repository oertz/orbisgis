/*
 * jPanelTypeOfGeometrySelection.java
 *
 * Created on 1 de mayo de 2008, 10:20
 */

package org.orbisgis.editorViews.toc.actions.cui.gui.widgets;

import java.awt.Component;
import java.net.URL;

import org.gdms.data.types.GeometryConstraint;
import org.sif.UIPanel;

/**
 * 
 * @author david
 */
public class jPanelTypeOfGeometrySelection extends javax.swing.JPanel implements
		UIPanel {

	/** Creates new form jPanelTypeOfGeometrySelection */
	public jPanelTypeOfGeometrySelection() {
		initComponents();
		jRadioButtonMixedGeometry.setVisible(false);
	}

	public jPanelTypeOfGeometrySelection(boolean withMixed) {
		this();
		if (withMixed)
			jRadioButtonMixedGeometry.setVisible(true);
	}

	/**
	 * This method is called from within the constructor to initialize the form.
	 * WARNING: Do NOT modify this code. The content of this method is always
	 * regenerated by the Form Editor.
	 */
	// <editor-fold defaultstate="collapsed" desc="Generated
	// Code">//GEN-BEGIN:initComponents
	private void initComponents() {

		buttonGroup1 = new javax.swing.ButtonGroup();
		jRadioButtonPointGeometry = new javax.swing.JRadioButton();
		jRadioButtonLineGeometry = new javax.swing.JRadioButton();
		jRadioButtonPolygonGeometry = new javax.swing.JRadioButton();
		jRadioButtonMixedGeometry = new javax.swing.JRadioButton();

		buttonGroup1.add(jRadioButtonPointGeometry);
		jRadioButtonPointGeometry.setText("Point Geometry");

		buttonGroup1.add(jRadioButtonLineGeometry);
		jRadioButtonLineGeometry.setText("Line Geometry");

		buttonGroup1.add(jRadioButtonPolygonGeometry);
		jRadioButtonPolygonGeometry.setText("Polygon Geometry");

		buttonGroup1.add(jRadioButtonMixedGeometry);
		jRadioButtonMixedGeometry.setText("Mixed Geometry");

		javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
		this.setLayout(layout);
		layout.setHorizontalGroup(layout.createParallelGroup(
				javax.swing.GroupLayout.Alignment.LEADING).addGroup(
				layout.createSequentialGroup().addContainerGap().addGroup(
						layout.createParallelGroup(
								javax.swing.GroupLayout.Alignment.LEADING)
								.addComponent(jRadioButtonPointGeometry)
								.addComponent(jRadioButtonLineGeometry)
								.addComponent(jRadioButtonPolygonGeometry)
								.addComponent(jRadioButtonMixedGeometry))
						.addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE,
								Short.MAX_VALUE)));
		layout
				.setVerticalGroup(layout
						.createParallelGroup(
								javax.swing.GroupLayout.Alignment.LEADING)
						.addGroup(
								layout
										.createSequentialGroup()
										.addContainerGap()
										.addComponent(jRadioButtonPointGeometry)
										.addPreferredGap(
												javax.swing.LayoutStyle.ComponentPlacement.RELATED)
										.addComponent(jRadioButtonLineGeometry)
										.addPreferredGap(
												javax.swing.LayoutStyle.ComponentPlacement.RELATED)
										.addComponent(
												jRadioButtonPolygonGeometry)
										.addPreferredGap(
												javax.swing.LayoutStyle.ComponentPlacement.RELATED)
										.addComponent(jRadioButtonMixedGeometry)
										.addContainerGap(
												javax.swing.GroupLayout.DEFAULT_SIZE,
												Short.MAX_VALUE)));
	}// </editor-fold>//GEN-END:initComponents

	// Variables declaration - do not modify//GEN-BEGIN:variables
	private javax.swing.ButtonGroup buttonGroup1;
	private javax.swing.JRadioButton jRadioButtonLineGeometry;
	private javax.swing.JRadioButton jRadioButtonMixedGeometry;
	private javax.swing.JRadioButton jRadioButtonPointGeometry;
	private javax.swing.JRadioButton jRadioButtonPolygonGeometry;

	// End of variables declaration//GEN-END:variables
	public Component getComponent() {
		// TODO Auto-generated method stub
		return this;
	}

	public URL getIconURL() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getInfoText() {
		// TODO Auto-generated method stub
		return "Select type of Geometry";
	}

	public String getTitle() {
		// TODO Auto-generated method stub
		return "Type of Geometry";
	}

	public String initialize() {
		// TODO Auto-generated method stub
		return null;
	}

	public String postProcess() {
		// TODO Auto-generated method stub

		return null;
	}

	public String validateInput() {
		if (!jRadioButtonLineGeometry.isSelected()
				&& !jRadioButtonPointGeometry.isSelected()
				&& !jRadioButtonPolygonGeometry.isSelected()
				&& !jRadioButtonMixedGeometry.isSelected())
			return "You have not selected a geometry";
		return null;
	}

	public int getConstraint() {
		if (jRadioButtonLineGeometry.isSelected())
			return GeometryConstraint.LINESTRING;
		if (jRadioButtonPointGeometry.isSelected())
			return GeometryConstraint.POINT;
		if (jRadioButtonPolygonGeometry.isSelected())
			return GeometryConstraint.POLYGON;
		return GeometryConstraint.MIXED;
	}
}
