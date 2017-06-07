package com.gieselaar.verzuimbeheer.baseforms;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import com.michaelbaranov.microba.calendar.DatePicker;

import java.awt.Component;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

public class BaseDialog extends JDialog implements WindowListener, ComponentListener{

	/**
	 * 
	 */
	private static final long serialVersionUID = -8392066905201004228L;
	private JButton okButton = null;
	private JButton cancelButton = null;
	private Container contentPane;
	private Rectangle bounds;
	private JPanel panel;
	private String screenTitle;
	private boolean datachanged; 
	public BaseDialog(JFrame frame, boolean modal, String title){
		super(frame,modal);
		screenTitle = title;
		this.addComponentListener(this);
		this.setResizable(true);
		this.setTitle(screenTitle);
		contentPane = this.getContentPane();
		okButton = new JButton("OK");
		okButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				okButtonClicked(e);
			}
		});
		cancelButton = new JButton("Cancel");
		cancelButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				cancelButtonClicked(e);
			}
		});
		
		panel = new JPanel();
		panel.setLayout(new FlowLayout(FlowLayout.RIGHT));
		panel.add(okButton);
		panel.add(cancelButton);
		contentPane.add(panel);
		contentPane.setLayout(null);
		this.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
	}
	public JButton getOkButton() {
		return okButton;
	}
	public void setOkButton(JButton okButton) {
		this.okButton = okButton;
	}
	public JButton getCancelButton() {
		return cancelButton;
	}
	public void setCancelButton(JButton cancelButton) {
		this.cancelButton = cancelButton;
	}
	public void registerButtons(JButton okBtn, JButton cancelBtn){
		this.okButton = okBtn;
		okButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				okButtonClicked(e);
			}
		});
		okButton.setActionCommand("OK");
		okButton.setText("OK");

		this.cancelButton = cancelBtn;
		cancelButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				cancelButtonClicked(e);
			}
		});
		cancelButton.setActionCommand("Cancel");
		cancelButton.setText("Cancel");
	}
	private boolean result = false;
	protected void okButtonClicked(ActionEvent e) {
		result = true;
		this.setVisible(false);
	}

	protected void cancelButtonClicked(ActionEvent e) {
		int choice;
		if (datachanged)
		{
			choice = JOptionPane.showConfirmDialog(this, "Gegevens niet opgeslagen. Als u doorgaat gaan de wijzigingen verloren. Weet u het zeker?","Waarschuwing", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
			if (choice != JOptionPane.YES_OPTION)
				return;
		}
		result = false;
		this.setVisible(false);
	}

	@Override
	public void windowOpened(WindowEvent e) {
	}

	@Override
	public void windowClosing(WindowEvent e) {
	}

	@Override
	public void windowClosed(WindowEvent e) {
	}

	@Override
	public void windowIconified(WindowEvent e) {
	}

	@Override
	public void windowDeiconified(WindowEvent e) {
	}

	@Override
	public void windowActivated(WindowEvent e) {
	}

	@Override
	public void windowDeactivated(WindowEvent e) {
	}

	@Override
	public void componentResized(ComponentEvent e) {
		bounds = getContentPane().getBounds();
		panel.setBounds(bounds.x + 10, bounds.y + bounds.height - 40, bounds.width - 20, 33);
	}

	@Override
	public void componentMoved(ComponentEvent e) {
	}

	@Override
	public void componentShown(ComponentEvent e) {
	}

	@Override
	public void componentHidden(ComponentEvent e) {
	}
	public boolean isResult() {
		return result;
	}
	public void setResult(boolean result) {
		this.result = result;
	}
	@SuppressWarnings("rawtypes")
	public void activateListener(Container container){
		ChangeListenerImpl l = new ChangeListenerImpl();
		DocumentListenerImpl d = new DocumentListenerImpl();
		ActionListenerImpl a = new ActionListenerImpl();
		for (Component c : container.getComponents()) {
			if (c instanceof JTextField)
				((JTextField)c).getDocument().addDocumentListener(d);
			else
			if (c instanceof JTextArea) 
				((JTextArea)c).getDocument().addDocumentListener(d);
			else
			if (c instanceof JCheckBox) 
		    	((JCheckBox)c).addChangeListener(l);
			else
			if (c instanceof JComboBox)
				((JComboBox)c).addActionListener(a);
			else
			if (c instanceof DatePicker)
				((DatePicker)c).addActionListener(a);
			else
			if (c instanceof JPanel)
				activateListener((JPanel)c);
		}
		datachanged = false;
	}
	public void activateListener(){
		activateListener(this.getContentPane());
	}
	public class ActionListenerImpl implements ActionListener{
		@Override
		public void actionPerformed(ActionEvent e) {
			datachanged=true;
		}
	}
	public class ChangeListenerImpl implements ChangeListener{
		@Override
		public void stateChanged(ChangeEvent e) {
			datachanged=true;
		}
	}
	public class DocumentListenerImpl implements DocumentListener{
		@Override
		public void insertUpdate(DocumentEvent e) {
			datachanged=true;
		}
		@Override
		public void removeUpdate(DocumentEvent e) {
			datachanged=true;
		}
		@Override
		public void changedUpdate(DocumentEvent e) {
			datachanged=true;
		}
	}
}
