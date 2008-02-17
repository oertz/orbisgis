/*
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at french IRSTV institute and is able
 * to manipulate and create vectorial and raster spatial information. OrbisGIS
 * is distributed under GPL 3 license. It is produced  by the geomatic team of
 * the IRSTV Institute <http://www.irstv.cnrs.fr/>, CNRS FR 2488:
 *    Erwan BOCHER, scientific researcher,
 *    Thomas LEDUC, scientific researcher,
 *    Fernando GONZALEZ CORTES, computer engineer.
 *
 * Copyright (C) 2007 Erwan BOCHER, Fernando GONZALEZ CORTES, Thomas LEDUC
 *
 * This file is part of OrbisGIS.
 *
 * OrbisGIS is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * OrbisGIS is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with OrbisGIS. If not, see <http://www.gnu.org/licenses/>.
 *
 * For more information, please consult:
 *    <http://orbisgis.cerma.archi.fr/>
 *    <http://sourcesup.cru.fr/projects/orbisgis/>
 *    <http://listes.cru.fr/sympa/info/orbisgis-developers/>
 *    <http://listes.cru.fr/sympa/info/orbisgis-users/>
 *
 * or contact directly:
 *    erwan.bocher _at_ ec-nantes.fr
 *    fergonco _at_ gmail.com
 *    thomas.leduc _at_ cerma.archi.fr
 */
package org.orbisgis.geoview.views.sqlConsole.actions;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import javax.swing.JDialog;

import org.gdms.data.DataSource;
import org.gdms.data.DataSourceCreationException;
import org.gdms.data.DataSourceFactory;
import org.gdms.data.ExecutionException;
import org.gdms.data.metadata.Metadata;
import org.gdms.data.types.Type;
import org.gdms.driver.DriverException;
import org.gdms.sql.customQuery.showAttributes.Table;
import org.gdms.sql.parser.ParseException;
import org.gdms.sql.strategies.SQLProcessor;
import org.gdms.sql.strategies.SemanticException;
import org.orbisgis.core.OrbisgisCore;
import org.orbisgis.geoview.layerModel.CRSException;
import org.orbisgis.geoview.layerModel.LayerException;
import org.orbisgis.geoview.layerModel.LayerFactory;
import org.orbisgis.geoview.layerModel.VectorLayer;
import org.orbisgis.geoview.views.sqlConsole.ui.ConsoleAction;
import org.orbisgis.geoview.views.sqlConsole.ui.History;
import org.orbisgis.geoview.views.sqlConsole.ui.SQLConsolePanel;
import org.orbisgis.pluginManager.PluginManager;
import org.orbisgis.pluginManager.ui.OpenFilePanel;
import org.orbisgis.pluginManager.ui.SaveFilePanel;
import org.sif.UIFactory;

public class ActionsListener implements ActionListener, KeyListener {
	private final String EOL = System.getProperty("line.separator");
	private SQLConsolePanel consolePanel;
	private History history;

	public ActionsListener(SQLConsolePanel consolePanel) {
		this.consolePanel = consolePanel;
		history = consolePanel.getHistory();
	}

	public void actionPerformed(ActionEvent e) {
		switch (new Integer(e.getActionCommand())) {
		case ConsoleAction.EXECUTE:
			execute();
			break;
		case ConsoleAction.CLEAR:
			consolePanel.getJTextArea().setForeground(Color.BLACK);
			consolePanel.setText("");
			break;
		case ConsoleAction.STOP:
			break;
		case ConsoleAction.PREVIOUS:
			previous();
			break;
		case ConsoleAction.NEXT:
			next();
			break;
		case ConsoleAction.OPEN:
			open();
			break;
		case ConsoleAction.SAVE:
			save();
			break;
		}
		setButtonsStatus();
	}

	private void previous() {
		if (history.isPreviousAvailable()) {
			setQuery(history.getPrevious());
		}
	}

	private void next() {
		if (history.isNextAvailable()) {
			setQuery(history.getNext());
		}
	}

	private void setQuery(String query) {
		consolePanel.setText(query);
	}

	public void save() {
		final SaveFilePanel outfilePanel = new SaveFilePanel(
				"org.orbisgis.geoview.sqlConsoleOutFile", "Select a sql file");
		outfilePanel.addFilter("sql", "SQL script (*.sql)");
		outfilePanel.addFilter("txt", "Text file (*.txt)");

		if (UIFactory.showDialog(outfilePanel)) {
			try {
				final BufferedWriter out = new BufferedWriter(new FileWriter(
						outfilePanel.getSelectedFile()));
				out.write(consolePanel.getText());
				out.close();
			} catch (IOException e) {
				PluginManager.warning("IOException with "
						+ outfilePanel.getSelectedFile(), e);
			}
		}
	}

	private void open() {
		final OpenFilePanel inFilePanel = new OpenFilePanel(
				"org.orbisgis.geoview.sqlConsoleInFile", "Select a sql file");
		inFilePanel.addFilter("sql", "SQL script (*.sql)");

		if (UIFactory.showDialog(inFilePanel)) {
			try {
				for (File selectedFile : inFilePanel.getSelectedFiles()) {
					final BufferedReader in = new BufferedReader(
							new FileReader(selectedFile));
					String line;
					while ((line = in.readLine()) != null) {
						consolePanel.getJTextArea().append(line + EOL);
					}
					in.close();
				}
			} catch (FileNotFoundException e) {
				PluginManager.warning("SQL script file not found : "
						+ inFilePanel.getSelectedFile(), e);
			} catch (IOException e) {
				PluginManager.warning("IOException with "
						+ inFilePanel.getSelectedFile(), e);
			}
		}
	}

	public void execute() {
		final DataSourceFactory dsf = OrbisgisCore.getDSF();
		consolePanel.getJTextArea().setForeground(Color.BLACK);
		final String queryPanelContent = consolePanel.getText();

		history.push(queryPanelContent);
		SQLProcessor sqlProcessor = new SQLProcessor(dsf);
		org.gdms.sql.strategies.Instruction[] instructions;
		try {
			instructions = sqlProcessor.prepareScript(queryPanelContent);

			for (int i = 0; i < instructions.length; i++) {

				org.gdms.sql.strategies.Instruction instruction = instructions[i];

				Metadata metadata = instruction.getResultMetadata();
				if ((metadata != null) && (metadata.getFieldCount() > 0)) {
					boolean spatial = false;
					for (int k = 0; k < metadata.getFieldCount(); k++) {
						if (metadata.getFieldType(k).getTypeCode() == Type.GEOMETRY) {
							spatial = true;
						}
					}

					DataSource ds = instruction.getDataSource();
					if (spatial) {
						final VectorLayer layer = LayerFactory
								.createVectorialLayer(ds);
						consolePanel.getGeoview().getViewContext().getLayerModel()
								.addLayer(layer);
					} else {
						final JDialog dlg = new JDialog();

						dlg.setModal(true);
						dlg.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
						dlg.getContentPane().add(new Table(ds));
						dlg.pack();
						ds.open();
						dlg.setVisible(true);
						ds.cancel();
					}
				} else {
					instruction.execute();
				}

			}

		} catch (SemanticException e) {
			PluginManager.error("Semantic error in the instruction:", e);
		} catch (DriverException e) {
			PluginManager.error("Data access error:", e);
		} catch (ParseException e) {
			PluginManager.error("Impossible to parse :", e);
		} catch (ExecutionException e) {
			PluginManager.error("Impossible to execute the query :", e);
		} catch (DataSourceCreationException e) {
			PluginManager.error("Impossible to create the new datasource :", e);
		} catch (LayerException e) {
			PluginManager.error("Impossible to create the layer:", e);
		} catch (CRSException e) {
			PluginManager.error("CRS error :", e);
		}

	}

	public void setButtonsStatus() {
		consolePanel.setButtonsStatus();
	}

	public void keyPressed(KeyEvent e) {
		setButtonsStatus();
	}

	public void keyReleased(KeyEvent e) {
		setButtonsStatus();
	}

	public void keyTyped(KeyEvent e) {
		setButtonsStatus();
	}
}