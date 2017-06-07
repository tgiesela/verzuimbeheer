package com.gieselaar.verzuim.views;

import java.awt.Component;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.KeyEvent;
import java.beans.PropertyVetoException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JInternalFrame;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;
import javax.swing.JRootPane;
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

import com.gieselaar.verzuim.components.JTextFieldTGI;
import com.gieselaar.verzuim.controllers.AbstractController;
import com.gieselaar.verzuim.controllers.AbstractController.__formmode;
import com.gieselaar.verzuim.interfaces.ControllerEventListener;
import com.gieselaar.verzuim.utils.ExceptionLogger;
import com.gieselaar.verzuim.viewsutils.CursorController;
import com.gieselaar.verzuimbeheer.facades.LoginSessionRemote;
import com.gieselaar.verzuimbeheer.services.InfoBase;
import com.michaelbaranov.microba.calendar.DatePicker;

public abstract class AbstractDetail extends JInternalFrame implements ControllerEventListener{

	private static final long serialVersionUID = 1L;
	protected static Logger log = Logger.getLogger("com.gieselaar"); 
	/*
	 * Form components
	 */
	private JPanel panel;

	/*
	 * Variables
	 */
	private transient ControllerEventListener thissink = this;
	private transient JInternalFrame thisform = this;
	private boolean datachanged = false;
	protected transient LoginSessionRemote loginSession = null;
	
	/* Controller */
	protected transient AbstractController controller;
	private __formmode formmode;
	
	private class Registration{
		private ControllerEventListener listener;
		private AbstractController controller;
		public Registration(AbstractController controller, ControllerEventListener listener) {
			this.setListener(listener);
			this.controller = controller;
		}
		public void setListener(ControllerEventListener listener) {
			this.listener = listener;
		}
	}
	private transient List<Registration> listeners = new ArrayList<>();
	private JButton okButton;
	private boolean closeaftersave = true;
	public AbstractDetail(String title, AbstractController controller){
		this.controller = controller;
		if (controller != null){
			registerControllerListener(controller, this);
		}
		initializeBase(title);
	}
	void initializeBase(String screenTitle){
		addInternalFrameListener();
		addComponentListener();
		this.setBounds(50, 50, 672, 446);
		this.setTitle(screenTitle);
		this.setResizable(true);
		this.setTitle(screenTitle);
		this.setClosable(true);
		this.setIconifiable(true);
		this.setMaximizable(true);
		
		addEscapekeyHandler();
		addComponentListener();
		Container contentPane = this.getContentPane();
		okButton = new JButton("OK");
		if (controller != null){
			okButton.setActionCommand(controller.okDetailActionCommand);
			okButton.addActionListener(CursorController.createListener(this,controller));
		}
		okButton.setDefaultCapable(true);
		JButton cancelButton = new JButton("Cancel");
		if (controller != null){
			cancelButton.setActionCommand(controller.cancelDetailActionCommand);
			cancelButton.addActionListener(CursorController.createListener(this,controller));
		}

		this.getRootPane().setDefaultButton(okButton);

		panel = new JPanel();
		setPanelBounds();
		panel.setLayout(new FlowLayout(FlowLayout.RIGHT));
		panel.add(okButton);
		panel.add(cancelButton);
		contentPane.add(panel);
		contentPane.setLayout(null);
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
	}
	/*
	protected void okButtonClicked(){
		this.setVisible(false);
		try {
			this.setClosed(true);
			close();
		} catch (PropertyVetoException e1) {
        	ExceptionLogger.ProcessException(e1,this);
		}
	}
	*/
/*
	protected void cancelButtonClicked(){
		int choice = 0;
		try {
			if (datachanged)
			{
				choice = JOptionPane.showConfirmDialog(this, "Gegevens niet opgeslagen. Als u doorgaat gaan de wijzigingen verloren. Weet u het zeker?","Waarschuwing", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
				if (choice != JOptionPane.YES_OPTION)
					return;
			}
			this.setClosed(true);
			close();
		} catch (PropertyVetoException e1) {
	    	ExceptionLogger.ProcessException(e1,this);
		}
		
	}
	*/
	private void addInternalFrameListener() {
		this.addInternalFrameListener(new InternalFrameListener() {
			
			@Override
			public void internalFrameOpened(InternalFrameEvent e) {
				/* noop */
			}
			
			@Override
			public void internalFrameIconified(InternalFrameEvent e) {
				/* noop */
			}
			
			@Override
			public void internalFrameDeiconified(InternalFrameEvent e) {
				/* noop */
			}
			
			@Override
			public void internalFrameDeactivated(InternalFrameEvent e) {
				/* noop */
			}
			
			@Override
			public void internalFrameClosing(InternalFrameEvent e) {
				controller.deleteControllerListener(thissink);
				thisform.setVisible(false);
			}
			
			@Override
			public void internalFrameClosed(InternalFrameEvent e) {
				/* noop */
			}
			
			@Override
			public void internalFrameActivated(InternalFrameEvent e) {
				/* noop */
			}
		});
	}
	public void registerControllerListener(AbstractController controller, ControllerEventListener listener){
		controller.addControllerListener(listener);
		listeners.add(new Registration(controller,listener));
	}
	private void setPanelBounds() {
		panel.setBounds(0,this.getHeight()-60,this.getWidth()-20,30);
	}
	private void addComponentListener() {
		this.addComponentListener(new ComponentListener() {
			
			@Override
			public void componentShown(ComponentEvent e) {
				/* noop */
			}
			
			@Override
			public void componentResized(ComponentEvent e) {
				formResized();
			}
			
			@Override
			public void componentMoved(ComponentEvent e) {
				/* noop */
			}
			
			@Override
			public void componentHidden(ComponentEvent e) {
				/* noop */
			}
		});
	}
	protected void formResized() {
		setPanelBounds();
	}
	private void addEscapekeyHandler() {
		KeyStroke escapeKeyStroke = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0, false);
		Action escapeAction = new AbstractAction() {
			private static final long serialVersionUID = 1L;

			// inform the controller when the user presses escape
			@Override
		    public void actionPerformed(ActionEvent e) {
		    	controller.closeView(thissink);
		    }
		}; 
		this.getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(escapeKeyStroke, "ESCAPE");
		this.getRootPane().getActionMap().put("ESCAPE", escapeAction);
	}

	protected void closeView() {
		for (Registration r:listeners){
			r.controller.deleteControllerListener(r.listener);
		}
		this.setVisible(false);
		try {
			this.setClosed(true);
		} catch (PropertyVetoException e1) {
        	ExceptionLogger.ProcessException(e1,this);
		}
	}

	public __formmode getFormmode() {
		return formmode;
	}
	public void setFormmode(__formmode formmode) {
		this.formmode = formmode;
	}

	@SuppressWarnings({ "rawtypes"})
	public void activateListener(Container container){
		ChangeListenerImpl l = new ChangeListenerImpl();
		DocumentListenerImpl d = new DocumentListenerImpl();
		ActionListenerImpl a = new ActionListenerImpl();
		for (Component c : container.getComponents()) {
			if (c instanceof JTextField || c instanceof JTextFieldTGI)
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
			if (c instanceof JPanel || c instanceof JRootPane || c instanceof JLayeredPane)
				activateListener((Container)c);
		}
		datachanged = false;
	}
	public boolean isDatachanged() {
		return datachanged;
	}
	public void setDatachanged(boolean datachanged) {
		this.datachanged = datachanged;
	}
	protected JPanel getPanel() {
		return panel;
	}
	@Override
	public void refreshTable() {
	}
	@Override
	public void setDetailFormmode(__formmode mode) {
		this.setFormmode(mode);
	}
	@Override
	public void setData(InfoBase info) {
	}
	@Override
	public void close() {
		closeView();
	}
	@Override
	public void rowSelected(int selectedRow, Object data) {
	}
	@Override
	public void formClosed(ControllerEventListener cel){
	}
	public abstract InfoBase collectData();
	/*
	 * Listeners to detect data changes
	 */
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
	
	public JButton getOkButton() {
		return okButton;
	}
	/**
	 * Used to decide if the form has to be closed when the OK-button
	 * is clicked. When true, the form closes, when false, the form remains
	 * visible and has to be closed in another way.
	 * 
	 * @param closeaftersave
	 */
	public void setCloseAfterSave(boolean closeaftersave){
		this.closeaftersave = closeaftersave;
	}
	public boolean getCloseAfterSave() {
		return closeaftersave;
	}
}
