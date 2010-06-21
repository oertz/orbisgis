package org.orbisgis.core.ui.components.text;

import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import org.orbisgis.core.images.IconLoader;
import org.orbisgis.core.images.OrbisGISIcon;

public class JTextFilter extends JPanel {

	private JTextField txtFilter;
	private JButton btnClear;

	public JTextFilter() {
		txtFilter = new JTextField(8);
		txtFilter.getDocument().addDocumentListener(new DocumentListener() {

			@Override
			public void removeUpdate(DocumentEvent e) {
				enableButton();
			}

			@Override
			public void insertUpdate(DocumentEvent e) {
				enableButton();
			}

			@Override
			public void changedUpdate(DocumentEvent e) {
				enableButton();
			}

			private void enableButton() {
				btnClear.setVisible(txtFilter.getText().length() > 0);
			}
		});
		this.add(txtFilter);
		btnClear = new JButton(OrbisGISIcon.REMOVE);
		btnClear.setVisible(false);
		btnClear.setMargin(new Insets(0, 0, 0, 0));
		btnClear.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				txtFilter.setText("");
			}

		});
		this.add(btnClear);
	}

	public void addDocumentListener(DocumentListener listener) {
		txtFilter.getDocument().addDocumentListener(listener);
	}

	public void removeDocumentListener(DocumentListener listener) {
		txtFilter.getDocument().removeDocumentListener(listener);
	}

	public String getText() {
		return txtFilter.getText();
	}

}
