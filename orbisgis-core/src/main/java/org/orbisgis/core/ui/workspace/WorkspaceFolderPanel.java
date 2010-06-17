/*
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able to
 * manipulate and create vector and raster spatial information. OrbisGIS is
 * distributed under GPL 3 license. It is produced by the "Atelier SIG" team of
 * the IRSTV Institute <http://www.irstv.cnrs.fr/> CNRS FR 2488.
 *
 * 
 *  Team leader Erwan BOCHER, scientific researcher,
 * 
 *  User support leader : Gwendall Petit, geomatic engineer.
 *
 *
 * Copyright (C) 2007 Erwan BOCHER, Fernando GONZALEZ CORTES, Thomas LEDUC
 *
 * Copyright (C) 2010 Erwan BOCHER, Pierre-Yves FADET, Alexis GUEGANNO, Maxence LAURENT
 *
 * This file is part of OrbisGIS.
 *
 * OrbisGIS is free software: you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * OrbisGIS is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * OrbisGIS. If not, see <http://www.gnu.org/licenses/>.
 *
 * For more information, please consult: <http://www.orbisgis.org/>
 *
 * or contact directly:
 * erwan.bocher _at_ ec-nantes.fr
 * gwendall.petit _at_ ec-nantes.fr
 */
package org.orbisgis.core.ui.workspace;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.orbisgis.core.Services;
import org.orbisgis.core.images.IconLoader;
import org.orbisgis.core.sif.UIFactory;
import org.orbisgis.core.sif.UIPanel;
import org.orbisgis.core.workspace.DefaultWorkspace;
import org.orbisgis.core.workspace.Workspace;

public class WorkspaceFolderPanel extends JPanel implements UIPanel {

	private static final String OUT_FILE_ID = "org.orbisgis.core.ui.workspace.WorkspaceFolderPanel";
	private JPanel panel;
	private SelectWorkspaceFilePanel outfilePanel;
	private JButton btFolder;
	private JComboBox combobox;
	private ArrayList<String> comboList;
	private JCheckBox jCheckBox;
	private boolean selected = false;

	public WorkspaceFolderPanel(ArrayList<String> workspaces) {
		setLayout(new BorderLayout());
		this.comboList = workspaces;
		panel = new JPanel(new BorderLayout());
		JLabel label = new JLabel("Workspace : ");
		combobox = new JComboBox(workspaces.toArray(new String[0]));
		combobox.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				jCheckBox.setSelected(false);
			}
		});
		btFolder = new JButton();
		btFolder.setIcon(IconLoader.getIcon("open.png"));
		btFolder.setToolTipText("Choose a folder");
		btFolder.setPreferredSize(new Dimension(30, 20));
		btFolder.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				outfilePanel = new SelectWorkspaceFilePanel(OUT_FILE_ID,
						"Select or choose a workspace folder");
				JFileChooser ret = outfilePanel.getFileChooser();
				ret.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
				ret.setMultiSelectionEnabled(false);
				ret.setSelectedFile(new File("/"));
				outfilePanel.getFileChooser();
				if (UIFactory.showDialog(outfilePanel)) {
					String savedFile = outfilePanel.getSelectedFile()
							.getAbsolutePath();

					if (!comboList.contains(savedFile)) {
						combobox.insertItemAt(savedFile, 0);
						combobox.setSelectedIndex(0);
						comboList.add(savedFile);
						jCheckBox.setSelected(false);
					}

				}

			}
		});

		jCheckBox = new JCheckBox("Use as default workspace");
		jCheckBox.setEnabled(true);
		jCheckBox.setSelected(selected);
		jCheckBox.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				setCheckBoxSelected(jCheckBox.isSelected());
			}
		});
		panel.add(label);
		panel.add(combobox);
		panel.add(btFolder);
		panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
		panel.setOpaque(false);

		add(panel, BorderLayout.NORTH);
		add(jCheckBox, BorderLayout.CENTER);
		setPreferredSize(new Dimension(400, 60));
	}

	@Override
	public Component getComponent() {
		return this;
	}

	@Override
	public URL getIconURL() {
		return UIFactory.getDefaultIcon();
	}

	@Override
	public String getInfoText() {
		return "Select a workspace to start OrbisGIS application.";
	}

	@Override
	public String getTitle() {
		return "Select a workspace";
	}

	@Override
	public String initialize() {
		return null;
	}

	@Override
	public String postProcess() {
		return checkWorkspace(getWorkspaceFile());
	}

	private void setCheckBoxSelected(boolean selected) {
		this.selected = selected;
	}

	public boolean isSelected() {
		return selected;
	}

	public String getWorkspacePath() {
		return (String) combobox.getSelectedItem();

	}

	@Override
	public String validateInput() {
		File file = getWorkspaceFile();
		if (file == null) {
			return null;
		} else if (file.exists() && !file.isDirectory()) {
			setCheckBoxSelected(false);
			return "The selection must be a directory";
		} else {
			return checkWorkspace(file);
		}
	}

	private File getWorkspaceFile() {
		return new File(getWorkspacePath());
	}

	private String checkWorkspace(File file) {

		if (file == null) {
			return null;
		} else if (file.exists() && !file.isDirectory()) {
			File versionFile = new File(file, "org.orbisgis.version.txt");
			int version;
			if (versionFile.exists()) {
				try {
					BufferedReader fr = new BufferedReader(new FileReader(
							versionFile));
					String strVersion = fr.readLine();
					fr.close();
					version = Integer.parseInt(strVersion.trim());
				} catch (IOException e1) {
					setCheckBoxSelected(false);
					return "Cannot read workspace version";
				} catch (NumberFormatException e) {
					setCheckBoxSelected(false);
					return "Cannot read workspace version";
				}
			} else {
				version = DefaultWorkspace.WORKSPACE_VERSION;
			}
			DefaultSwingWorkspace dw = (DefaultSwingWorkspace) Services
					.getService(Workspace.class);
			if (dw.getWsVersion() != version) {
				setCheckBoxSelected(false);
				return "Workspace version mistmatch. Either"
						+ " clean or select another folder.";
			}
		} else if (file.exists() && file.isDirectory()) {

			File versionFile = new File(file, "org.orbisgis.version.txt");
			if (!versionFile.exists()) {
				setCheckBoxSelected(false);
				return "This folder is not a valid OrbisGIS workspace.";
			} else {
				DefaultWorkspace workspace = (DefaultWorkspace) Services
						.getService(Workspace.class);

				String currentWorkspacefolder = workspace.getWorkspaceFolder();
				if (currentWorkspacefolder != null) {

					if (file.getAbsolutePath().equals(currentWorkspacefolder)
							&& !isSelected()) {
						return "This workspace is aleardy used.";
					}
				}
			}
		}
		return null;

	}

	public ArrayList<String> getWorkspacesList() {
		return comboList;
	}

	public static void main(String[] args) {
		ArrayList<String> list = new ArrayList<String>();
		list.add(" /home/ebocher");
		WorkspaceFolderPanel workspaceFolderPanel = new WorkspaceFolderPanel(
				list);
		if (UIFactory.showDialog(workspaceFolderPanel)) {
			System.out.println(workspaceFolderPanel.getWorkspacePath());
			System.out.println(workspaceFolderPanel.isSelected());
		}
	}

}
