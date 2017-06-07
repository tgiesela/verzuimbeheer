package com.gieselaar.verzuimbeheer.baseforms;

import java.awt.Component;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.beans.PropertyVetoException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JInternalFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.InternalFrameEvent;
import javax.swing.event.InternalFrameListener;

import org.apache.log4j.Logger;

import com.gieselaar.verzuimbeheer.baseforms.IBaseFrameListener.formCloseAction;
import com.gieselaar.verzuimbeheer.facades.LoginSessionRemote;
import com.gieselaar.verzuimbeheer.services.InfoBase;
import com.gieselaar.verzuimbeheer.utils.ExceptionLogger;
import com.michaelbaranov.microba.calendar.DatePicker;

import java.awt.event.KeyEvent;

public class BaseDetailform extends JInternalFrame implements InternalFrameListener, ComponentListener {

	/**
	 * 
	 */
	protected static final long serialVersionUID = -2165083723081467262L;
	protected static Logger log = Logger.getLogger("com.gieselaar"); 
	private JButton okButton = null;
	private JButton cancelButton = null;
	private Container _contentPane;
	private JPanel panel;
	private String screenTitle;
	private LoginSessionRemote loginSession = null;
	private InfoBase verzuim = null;
	private List<IBaseFrameListener> listener = new ArrayList<IBaseFrameListener>();
	private JDesktopPaneTGI mdiPanel;
	private boolean datachanged = false;
	public enum formMode {
		New,
		Update,
		Delete
	}	
	public boolean isDatachanged() {
		return datachanged;
	}
	public void setDatachanged(boolean datachanged) {
		this.datachanged = datachanged;
	}
	private void notifyListeners(formCloseAction action) {
		for (@SuppressWarnings("rawtypes")
		Iterator iterator = listener.iterator(); iterator.hasNext();) {
			IBaseFrameListener name = (IBaseFrameListener) iterator
					.next();
			switch (action){
			case	cancel:	name.ClosedAfterCancel();
							break;
			case	save:	name.ClosedAfterSave();
							break;
			case	close:	name.Closed();
							break;
			}
			
		}
	}
/*	public enum formMode {
		New,
		Update,
		Delete
	}
*/
	private formMode mode = formMode.New;

	public void addChangeListener(IBaseFrameListener newListener) {
		listener.add(newListener);
	}
	public InfoBase getInfo() {
		return verzuim;
	}
	public void setInfo(InfoBase info){
		this.verzuim = info;
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
	/**
	 * Create the frame.
	 */
	public BaseDetailform(String title) {
		super();
		screenTitle = title;
		initialize_base();
	}
	/**
	 * @wbp.parser.constructor
	 */
	public BaseDetailform(String title, JDesktopPaneTGI mdiPanel) {
		super();
		this.mdiPanel = mdiPanel;
		screenTitle = title;
		initialize_base();
	}
	void initialize_base(){
		this.setBounds(100, 100, 672, 302);
		this.setTitle(screenTitle);
		this.setResizable(true);
		this.setTitle(screenTitle);
		this.setClosable(true);
		this.setIconifiable(true);
		this.setMaximizable(true);
		
		// START: Code to handle escape key
		KeyStroke escapeKeyStroke = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0, false);
		Action escapeAction = new AbstractAction() {
			private static final long serialVersionUID = 1L;

			// close the frame when the user presses escape
		    public void actionPerformed(ActionEvent e) {
		        cancelButtonClicked(e);
		    }
		}; 
		this.getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(escapeKeyStroke, "ESCAPE");
		this.getRootPane().getActionMap().put("ESCAPE", escapeAction);
		// EINDE: Code to handle escape key

		_contentPane = this.getContentPane();
		this.addInternalFrameListener(this);
		this.addComponentListener(this);
		okButton = new JButton("OK");
		okButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				okButtonClicked(e);
			}
		});
		okButton.setDefaultCapable(true);
		cancelButton = new JButton("Cancel");
		cancelButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				cancelButtonClicked(e);
			}
		});
		
		this.getRootPane().setDefaultButton(okButton);

		panel = new JPanel();
		panel.setBounds(0,this.getHeight()-60,this.getWidth()-20,30);
		panel.setLayout(new FlowLayout(FlowLayout.RIGHT));
		panel.add(okButton);
		panel.add(cancelButton);
		_contentPane.add(panel);
		_contentPane.setLayout(null);
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
	}
	protected void okButtonClicked(ActionEvent e) {
		this.setVisible(false);
		try {
			this.setClosed(true);
			this.notifyListeners(formCloseAction.save);
		} catch (PropertyVetoException e1) {
        	ExceptionLogger.ProcessException(e1,this);
		}
	}

	protected void cancelButtonClicked(ActionEvent e) {
		int choice = 0;
		try {
			if (datachanged)
			{
				choice = JOptionPane.showConfirmDialog(this, "Gegevens niet opgeslagen. Als u doorgaat gaan de wijzigingen verloren. Weet u het zeker?","Waarschuwing", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
				if (choice != JOptionPane.YES_OPTION)
					return;
			}
			this.setClosed(true);
			this.notifyListeners(formCloseAction.cancel);
		} catch (PropertyVetoException e1) {
        	ExceptionLogger.ProcessException(e1,this);
		}
	}
	
	@Override
	public void componentResized(ComponentEvent e) {
		panel.setBounds(0,this.getHeight()-60,this.getWidth()-20,30);

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
	@Override
	public void internalFrameOpened(InternalFrameEvent e) {
	}
	@Override
	public void internalFrameClosing(InternalFrameEvent e) {
		this.setVisible(false);
	}
	@Override
	public void internalFrameClosed(InternalFrameEvent e) {
	}
	@Override
	public void internalFrameIconified(InternalFrameEvent e) {
	}
	@Override
	public void internalFrameDeiconified(InternalFrameEvent e) {
	}
	@Override
	public void internalFrameActivated(InternalFrameEvent e) {
	}
	@Override
	public void internalFrameDeactivated(InternalFrameEvent e) {
	}
	public LoginSessionRemote getLoginSession() {
		return loginSession;
	}
	public void setLoginSession(LoginSessionRemote loginSession) {
		this.loginSession = loginSession;
	}
	public JDesktopPaneTGI getMdiPanel() {
		return mdiPanel;
	}
	public void setMdiPanel(JDesktopPaneTGI mdiPanel) {
		this.mdiPanel = mdiPanel;
	}
	public formMode getMode() {
		return mode;
	}
	public void setMode(formMode mode) {
		this.mode = mode;
	}
	public JPanel getPanel() {
		return panel;
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
