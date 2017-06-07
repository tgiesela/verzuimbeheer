package com.gieselaar.verzuimbeheer.components;

import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.RowSorter.SortKey;
import javax.swing.event.InternalFrameEvent;
import javax.swing.event.InternalFrameListener;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

import org.apache.log4j.Logger;

import com.gieselaar.verzuimbeheer.baseforms.BaseDetailform;
import com.gieselaar.verzuimbeheer.baseforms.BaseDetailform.formMode;
import com.gieselaar.verzuimbeheer.baseforms.JTableTGI;
import com.gieselaar.verzuimbeheer.baseforms.ListFormNotification.__continue;
import com.gieselaar.verzuimbeheer.baseforms.IBaseFrameListener;
import com.gieselaar.verzuimbeheer.baseforms.JDesktopPaneTGI;
import com.gieselaar.verzuimbeheer.baseforms.JFrameTGINotification;
import com.gieselaar.verzuimbeheer.baseforms.ListFormNotification;
import com.gieselaar.verzuimbeheer.facades.LoginSessionRemote;
import com.gieselaar.verzuimbeheer.services.InfoBase;
import com.gieselaar.verzuimbeheer.services.InfoBase.persistenceaction;
import com.gieselaar.verzuimbeheer.utils.ExceptionLogger;

public class TablePanel extends JPanel 
	implements InternalFrameListener, ComponentListener, IBaseFrameListener  {

	private static final long serialVersionUID = 4503266343837591113L;
	protected static Logger log = Logger.getLogger("com.gieselaar"); 
	private ListFormNotification eventNotifier;
	private JButton newButton = null;
	private JButton detailButton = null;
	private JButton deleteButton = null;
	private JScrollPane tableScrollPane;
	private JTableTGI table = null;
	private JPanel panelAction;
	@SuppressWarnings("rawtypes")
	private Class detailFormClass = null;	/* is het type van het detailscherm dat wordt gecreeerd */
	private InfoBase newInfo = null;
	private InfoBase selectedinfo = null;				/* bevat gegevens waarop het scherm gebaseerd is */
	private InfoBase parentinfo = null;					/* bevat gegevens van een bovenliggend object waarop de gegevens op het scherm gebaseerd zijn */
	@SuppressWarnings("rawtypes")
	private Class infoClass = null;			/* is het type object waarmee het detailscherm wordt gecreeerd */
	private int selectedRow = -1;
	private LoginSessionRemote loginSession;
	private formMode mode;
	private JDesktopPaneTGI mdiPanel = null;
	
	@SuppressWarnings("rawtypes")
	public Class getDetailFormClass() {
		return detailFormClass;
	}
	@SuppressWarnings("rawtypes")
	public void setDetailFormClass(Class detailFormClass, Class infoClass) {
		this.detailFormClass = detailFormClass;
		this.infoClass = infoClass;
	}
	public JScrollPane getScrollPane() {
		return tableScrollPane;
	}
	public JDesktopPaneTGI getMdiPanel() {
		return mdiPanel;
	}
	/********************
	 * Methods for JTable  
	 ********************/
	public void addColumn(Object headerLabel, Object[] values, int width, Class<?> datatype) {
		table.addColumn(headerLabel, values, width, datatype);
	}
	public void addColumn(Object headerLabel, Object[] values, int width){
		addColumn(headerLabel, values, width, String.class);
	}
	public void addColumn(Object headerLabel, Object[] values){
		addColumn(headerLabel, values, 0, String.class);
	}
	public void addRow(Vector<Object> data, Object obj){
		addRow(data,obj,Color.WHITE);
	}
	public void addRow(Vector<Object> data, Object obj, Color color){
		table.addRow(data,obj,color);
	}
	public void setCellColour(int row, int column, Color color){
		table.setCellColour(row, column, color);
	}
	
	public void deleteAllRows(){
		table.deleteAllRows();
	}
	
	public final void populateTable_base(){
		List<? extends SortKey> keys = this.getSorter().getSortKeys();
		this.getSorter().setSortKeys(null);
		deleteAllRows();
		enableButtons(false);
		if (eventNotifier != null)
			eventNotifier.populateTableRequested();
		this.getSorter().setSortKeys(keys);
		if (eventNotifier != null)
			eventNotifier.populateTableComplete();
	}
	public void ReloadTable(){
		populateTable_base();
	}

	/*************************************
	 * Constructor and panel initialization
	 *************************************/

	public TablePanel(JDesktopPaneTGI mdiPanel) {
		super();
		this.mdiPanel = mdiPanel;
		initialize_base();
	}
	void initialize_base(){
		setLayout(null);
		tableScrollPane = new JScrollPane();
		this.setBounds(0, 0, 380, 140);
		tableScrollPane.setBounds(this.getX()+5, this.getY(), this.getWidth()-5, this.getHeight()-31);
		this.add(tableScrollPane);
		
		table = new JTableTGI(false);
		table.setEventNotifier(new JFrameTGINotification() {
			
			@Override
			public void rowSelected(Object info) {
				selectedinfo = (InfoBase)info;
				enableButtons(true);
				if (eventNotifier != null)
					eventNotifier.rowSelected(selectedinfo);
			}
			
			@Override
			public void rowDoubleClicked(Object info) {
				selectedinfo = (InfoBase)info;
				if (eventNotifier != null)
					eventNotifier.rowDoubleClicked(selectedinfo);
				detailButtonClicked(null);
			}

			@Override
			public void colSizeChanged(JTableTGI table, int colIndex,
					int newWidth) {
			}
		});

		tableScrollPane.setViewportView(table);
		newButton = new JButton("Nieuw...");
		newButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				newButtonClicked(e);
			}
		});
		
		detailButton = new JButton("Wijzig...");
		detailButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				detailButtonClicked(e);
			}
		});
		
		deleteButton = new JButton("Verwijder...");
		deleteButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				deleteButtonClicked(e);
			}
		});

		panelAction = new JPanel();
		panelAction.setBounds(this.getX()+5,this.getHeight()-31,this.getWidth()-5,30);
		panelAction.setLayout(new FlowLayout(FlowLayout.LEFT));
		panelAction.add(newButton);
		panelAction.add(detailButton);
		panelAction.add(deleteButton);
		this.add(panelAction);

		enableButtons(false);
	}
	private void enableButtons(boolean rowSelected){
		if (rowSelected){
			deleteButton.setEnabled(true);
			detailButton.setEnabled(true);
		}
		else {
			deleteButton.setEnabled(false);
			detailButton.setEnabled(false);
		}
		newButton.setEnabled(true);
	}
	protected void deleteButtonClicked(ActionEvent e) {
		mode = formMode.Delete;
		if (eventNotifier != null)
			if (eventNotifier.deleteButtonClicked(selectedinfo) == __continue.dontallow)
				return;
		selectedinfo.setAction(persistenceaction.DELETE);

		populateTable_base();
	}
	@SuppressWarnings("unchecked")
	protected void detailButtonClicked(ActionEvent e) {
		mode = formMode.Update;
		if (eventNotifier != null)
			if (eventNotifier.detailButtonClicked(selectedinfo) == __continue.dontallow)
				return;

		BaseDetailform window = null;
		if (detailFormClass == null)
			throw new RuntimeException("DetailForm not set");
		try {
			window = (BaseDetailform) detailFormClass.getConstructor(JDesktopPaneTGI.class).newInstance(this.mdiPanel);
			window.setMode(mode);
		} catch (InstantiationException e1) {
        	ExceptionLogger.ProcessException(e1,this);
		} catch (IllegalAccessException e1) {
        	ExceptionLogger.ProcessException(e1,this);
		} catch (IllegalArgumentException e1) {
        	ExceptionLogger.ProcessException(e1,this);
		} catch (SecurityException e1) {
        	ExceptionLogger.ProcessException(e1,this);
		} catch (InvocationTargetException e1) {
        	ExceptionLogger.ProcessException(e1,this);
		} catch (NoSuchMethodException e1) {
        	ExceptionLogger.ProcessException(e1,this);
		}
		
		window.setLoginSession(loginSession);
		window.setInfo(selectedinfo);

		if (window != null){
			//this.getRootPane().add(window);
			//if (this.getMdiPanel() != null){
			this.getMdiPanel().add(window);
			this.getMdiPanel().moveToFront(window);
			//}
			window.setVisible(true);
			window.moveToFront();
			window.addChangeListener(this);
		}
	}
	@SuppressWarnings("unchecked")
	protected void newButtonClicked(ActionEvent e) {
		mode = formMode.New;
		if (eventNotifier != null)
			if (eventNotifier.newButtonClicked() == __continue.dontallow)
				return;

		BaseDetailform window = null;
		if (detailFormClass == null)
			throw new RuntimeException("DetailForm not set");
		try {
			window = (BaseDetailform) detailFormClass.getConstructor(JDesktopPaneTGI.class).newInstance(this.mdiPanel);
			window.setMode(mode);
		} catch (InstantiationException e1) {
        	ExceptionLogger.ProcessException(e1,this);
		} catch (IllegalAccessException e1) {
        	ExceptionLogger.ProcessException(e1,this);
		} catch (IllegalArgumentException e1) {
        	ExceptionLogger.ProcessException(e1,this);
		} catch (SecurityException e1) {
        	ExceptionLogger.ProcessException(e1,this);
		} catch (InvocationTargetException e1) {
        	ExceptionLogger.ProcessException(e1,this);
		} catch (NoSuchMethodException e1) {
        	ExceptionLogger.ProcessException(e1,this);
		}

		window.setLoginSession(getLoginSession());

		try {
			newInfo = (InfoBase) infoClass.newInstance();
			if (eventNotifier != null)
				eventNotifier.newCreated(newInfo);
			window.setInfo(newInfo);
		} catch (InstantiationException e1) {
        	ExceptionLogger.ProcessException(e1,this);
		} catch (IllegalAccessException e1) {
        	ExceptionLogger.ProcessException(e1,this);
		}
		if (window != null){
			this.getMdiPanel().add(window);
			this.getMdiPanel().moveToFront(window);
			window.setVisible(true);
			window.moveToFront();
			window.addChangeListener(this);
			window.addInternalFrameListener(this);
		}
	}

	/******************************
	 * Implementation of Interfaces
	 ******************************/
	@Override
	public void componentResized(ComponentEvent e) {
		panelAction.setBounds(this.getX()+5,this.getHeight()-31,this.getWidth()-5,30);
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
	@Override
	public void ClosedAfterSave() {
		if (mode == formMode.New)
		{
			if (eventNotifier != null)
				eventNotifier.newInfoAdded(newInfo);
				
		}
		if (eventNotifier != null)
			eventNotifier.detailFormClosed();
		newInfo = null;
		populateTable_base();
	}
	@Override
	public void ClosedAfterCancel() {
	}
	@Override
	public void Closed() {
	}
	public int getSelectedRow() {
		return selectedRow;
	}
	public void setSelectedRow(int selectedRow) {
		this.selectedRow = selectedRow;
	}
	public TableRowSorter<TableModel> getSorter() {
		return table.getSorter();
	}
	public void setSorter(TableRowSorter<TableModel> sorter) {
		table.setSorter(sorter);
	}
	public LoginSessionRemote getLoginSession() {
		return loginSession;
	}
	public void setLoginSession(LoginSessionRemote loginSession) {
		this.loginSession = loginSession;
	}
	public JPanel getPanelAction() {
		return panelAction;
	}
	public JTableTGI getTable() {
		return table;
	}
	public InfoBase getParentInfo() {
		return parentinfo;
	}
	public void setParentInfo(InfoBase info) {
		this.parentinfo = info;
	}
	public formMode getMode() {
		return mode;
	}
	public void setMode(formMode mode) {
		this.mode = mode;
	}
	public void setMdiPanel(JDesktopPaneTGI mdiPanel) {
		this.mdiPanel = mdiPanel;
	}
	public ListFormNotification getEventNotifier() {
		return eventNotifier;
	}
	public void setEventNotifier(ListFormNotification eventNotifier) {
		this.eventNotifier = eventNotifier;
	}
	public JButton getNewButton() {
		return newButton;
	}
	public JButton getDeleteButton() {
		return deleteButton;
	}
}
