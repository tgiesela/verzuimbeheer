package com.gieselaar.verzuimbeheer.baseforms;

import java.awt.Color;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.KeyEvent;
import java.beans.PropertyVetoException;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Vector;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JInternalFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.KeyStroke;
import javax.swing.RowFilter;
import javax.swing.RowSorter.SortKey;
import javax.swing.event.InternalFrameEvent;
import javax.swing.event.InternalFrameListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import com.gieselaar.verzuimbeheer.baseforms.BaseDetailform.formMode;
import com.gieselaar.verzuimbeheer.baseforms.ListFormNotification.__continue;
import com.gieselaar.verzuimbeheer.facades.LoginSessionRemote;
import com.gieselaar.verzuimbeheer.services.InfoBase;
import com.gieselaar.verzuimbeheer.services.InfoBase.persistenceaction;
import com.gieselaar.verzuimbeheer.utils.CursorController;
import com.gieselaar.verzuimbeheer.utils.ExceptionLogger;
import com.gieselaar.verzuimbeheer.utils.WildcardRegEx;

import javax.swing.ImageIcon;

import org.apache.log4j.Logger;

import java.awt.Font;

public class BaseListForm extends JInternalFrame 
	implements InternalFrameListener, ComponentListener, IBaseFrameListener {

	private static final long serialVersionUID = 1L;
	protected static Logger log = Logger.getLogger("com.gieselaar"); 
	private ListFormNotification eventNotifier;
	private JButton closeButton = null;
	private JButton newButton = null;
	private JButton detailButton = null;
	private JButton deleteButton = null;
	private JButton exportButton;
	private JButton filterOffButton = null;
	private JButton filterButton = null;
	private JButton btnRefresh = null;
	private Container _contentPane;
	private JPanel panelClose;
	private JPanel panelAction;
	private String screenTitle;
	private JScrollPane tableScrollPane;
	private JScrollPane filterScrollpane = null; 
	private JTableTGI table = null;
	private JTableTGI filterTable = null;
	private int selectedRow = -1;
	private LoginSessionRemote loginSession = null;
	private Class<BaseDetailform> detailFormClass = null;	/* is het type van het detailscherm dat wordt gecreeerd */
	@SuppressWarnings("rawtypes")
	private Class infoClass = null;						/* is het type object waarmee het detailscherm wordt gecreeerd */
	private InfoBase newInfo = null;
	private InfoBase parentinfo = null;					/* bevat gegevens van een bovenliggend object waarop de gegevens op het scherm gebaseerd zijn */
	private InfoBase selectedinfo = null;				/* bevat gegevens waarop het scherm gebaseerd is */
	private JDesktopPaneTGI mdiPanel = null;
	private boolean filtering = true;
	private boolean filterEnabled = false;

	private boolean isinitialized = false;
	private int rightMargin = -1;
	private int bottomMargin = -1;
	private formMode mode;

	
	@SuppressWarnings("rawtypes")
	public Class getDetailFormClass() {
		return detailFormClass;
	}
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void setDetailFormClass(Class detailFormClass, Class infoClass) {
		this.detailFormClass = detailFormClass;
		this.infoClass = infoClass;
	}
	public JScrollPane getScrollPane() {
		return tableScrollPane;
	}
	/***********************
	 * Methods for JTableTGI  
	 ***********************/
	public void addColumn(Object headerLabel, Object[] values, int width, Class<?> datatype) {
		table.addColumn(headerLabel, values, width, datatype);
		filterTable.addColumn(headerLabel, values, width, datatype);
	}
	public void addColumn(Object headerLabel, Object[] values, int width){
		addColumn(headerLabel, values, width, String.class);
	}
	public void addColumn(Object headerLabel, Object[] values){
		addColumn(headerLabel, values, 0);
	}
	public void addRow(Vector<Object> data, Object obj, Color color){
		table.addRow(data,obj,color);
	}
	public void addRow(Vector<Object> data, Object obj){
		addRow(data,obj,Color.WHITE);
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
	 * Constructor and form initialization
	 *************************************/
	public BaseListForm(String title, JDesktopPaneTGI mdiPanel) {
		super();
		this.title = title;
		this.mdiPanel = mdiPanel;
		initialize_base();
	}
	private void initialize_base(){
		this.setBounds(100, 100, 683, 454);
		screenTitle = title;
		setTitle(screenTitle);
		setResizable(true);
		setTitle(screenTitle);
		setClosable(true);
		setMaximizable(true);
		setIconifiable(true);
		this.addInternalFrameListener(this);
		this.addComponentListener(this);
		
		// START: Code to handle escape key
		KeyStroke escapeKeyStroke = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0, false);
		Action escapeAction = new AbstractAction() {
			private static final long serialVersionUID = 1L;

			// close the frame when the user presses escape
		    public void actionPerformed(ActionEvent e) {
		        closeButtonClicked(e);
		    }
		}; 
		this.getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(escapeKeyStroke, "ESCAPE");
		this.getRootPane().getActionMap().put("ESCAPE", escapeAction);
		// EINDE: Code to handle escape key
		
		closeButton = new JButton("Close");
		closeButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				closeButtonClicked(e);
			}
		});
		
		panelClose = new JPanel();
		panelClose.setBounds(542,394,121,30);
		panelClose.setLayout(new FlowLayout(FlowLayout.RIGHT));
		panelClose.add(closeButton);
		_contentPane = this.getContentPane();
		_contentPane.add(panelClose);

		newButton = new JButton("Nieuw...");
		newButton.addActionListener(CursorController.createListener(this, new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				newButtonClicked(e);
			}
		}));
		
		detailButton = new JButton("Wijzig...");
		detailButton.addActionListener(CursorController.createListener(this, new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				detailButtonClicked(e);
			}
		}));
		detailButton.setDefaultCapable(true);
		this.getRootPane().setDefaultButton(detailButton);

		deleteButton = new JButton("Verwijder...");
		deleteButton.addActionListener(CursorController.createListener(this, new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				deleteButtonClicked(e);
			}
		}));

		panelAction = new JPanel();
		panelAction.setBounds(0,394,279,33);
		panelAction.setLayout(new FlowLayout(FlowLayout.LEFT));
		panelAction.add(newButton);
		panelAction.add(detailButton);
		panelAction.add(deleteButton);

		_contentPane.add(panelAction);
		
		_contentPane.setLayout(null);
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		
		tableScrollPane = new JScrollPane();
		tableScrollPane.setBounds(36, 40, this.getWidth() - (36+5)*2, this.getHeight()-140);
		_contentPane.add(tableScrollPane);
		
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
				detailButtonClicked(null);
				if (eventNotifier != null)
					eventNotifier.rowDoubleClicked(selectedinfo);
			}

			@Override
			public void colSizeChanged(JTableTGI table, int colIndex,
					int newWidth) {
				TableColumn col = filterTable.getColumnModel().getColumn(colIndex);
				if (col != null){
					col.setMaxWidth(newWidth);
					col.setMinWidth(newWidth);
				}
			}
		});
		tableScrollPane.setViewportView(table);

		filterScrollpane = new JScrollPane();
		filterScrollpane.setBounds(36, 10, this.getWidth() - (36+5)*2, 35);
		_contentPane.add(filterScrollpane);

		filterTable = new JTableTGI(true);
		filterTable.setEventNotifier(new JFrameTGINotification() {
			
			@Override
			public void rowSelected(Object info) {
			}
			
			@Override
			public void rowDoubleClicked(Object info) {
				filterButtonClicked(null);
			}

			@Override
			public void colSizeChanged(JTableTGI table, int colIndex,
					int newWidth) {
				TableColumn col = filterTable.getColumnModel().getColumn(colIndex);
				if (col != null){
					col.setMaxWidth(newWidth);
					col.setMinWidth(newWidth);
				}
			}
		});

		filterScrollpane.setViewportView(filterTable);

		Vector<Object> rowData = new Vector<Object>();
		rowData.add("");
		filterTable.addRow(rowData, null, Color.WHITE);
			
		exportButton = new JButton("");
		exportButton.setIcon(new ImageIcon(BaseListForm.class.getResource("/excel-8_28x28.png")));
		exportButton.setToolTipText("exporteren naar excel");
		exportButton.setBounds(0, 80, 32, 32);
		getContentPane().add(exportButton);
		
		exportButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				exportButtonClicked(e);
			}
		});
		filterButton = new JButton("");
		filterButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				filterButtonClicked(e);
			}
		});
		filterButton.setToolTipText("filter gegevens");
		filterButton.setSelectedIcon(new ImageIcon(BaseListForm.class.getResource("/disable_filter_28x28.png")));
		filterButton.setIcon(new ImageIcon(BaseListForm.class.getResource("/filter_28x28.png")));
		filterButton.setBounds(0, 10, 32, 32);
		getContentPane().add(filterButton);
		
		filterOffButton = new JButton("");
		filterOffButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				FilterOffButtonClicked(e);
			}
		});
		filterOffButton.setEnabled(false);
		filterOffButton.setIcon(new ImageIcon(BaseListForm.class.getResource("/disable_filter_28x28.png")));
		filterOffButton.setBounds(0, 45, 32, 32);
		getContentPane().add(filterOffButton);
		
		btnRefresh = new JButton("");
		btnRefresh.setFont(new Font("Tahoma", Font.PLAIN, 10));
		btnRefresh.setToolTipText("Verversen lijst");
		btnRefresh.setBounds(0, 116, 32, 32);
		ImageIcon icon = new ImageIcon(BaseListForm.class.getResource("/refresh-icon-614x460.png"));
		Image img = icon.getImage();
		Image newimg = img.getScaledInstance(28, 28, java.awt.Image.SCALE_DEFAULT);
		btnRefresh.setIcon(new ImageIcon(newimg));
		getContentPane().add(btnRefresh);
		btnRefresh.addActionListener(CursorController.createListener(this,new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				btnRefreshClicked(e);
			}
		}));
		

		enableButtons(false);
	}
	
	protected void btnRefreshClicked(ActionEvent e) {
		this.ReloadTable();
		
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
	protected void filterButtonClicked(ActionEvent e) {
		String filterText;
		filterEnabled = true;
		if (filterTable.isEditing())
		    filterTable.getCellEditor().stopCellEditing();
		filterOffButton.setEnabled(true);
		if (filterEnabled)
		{
			if (eventNotifier != null)
				eventNotifier.filterButtonClicked(filterTable.getModel());
			List<RowFilter<Object,Object>> rfs = new ArrayList<RowFilter<Object,Object>>(filterTable.getModel().getColumnCount());
			for (int i = 0; i < filterTable.getModel().getColumnCount(); i++)
			{
				filterText = (String)filterTable.getModel().getValueAt(0, i);
				if (filterText == null)
					;
				else
				{
					if (filterText.length() == 0)
						; /*sorter.setRowFilter(null);*/
					else{
						ColorTableModel model = (ColorTableModel) table.getModel();
						Class<?> clazz = model.getColumnClass(i);
						if (clazz == Date.class){
							DateFormat format = new SimpleDateFormat("dd-MM-yyyy");
							
							try {
								rfs.add(RowFilter.dateFilter(RowFilter.ComparisonType.EQUAL, format.parse(filterText)));
							} catch (ParseException e1) {
								JOptionPane.showMessageDialog(this, "Ongeldig datum formaat. Moet zijn: dd-mm-jjjj");
							}
						}else{
							rfs.add(RowFilter.regexFilter(WildcardRegEx.wildcardToRegex(filterText + '*'), i));
						}
					}
				}
			}
			table.getSorter().setRowFilter(RowFilter.andFilter(rfs));
		}
		else
			table.getSorter().setRowFilter(null);

	}
	protected void FilterOffButtonClicked(ActionEvent e) {
		filterEnabled = false;
		if (filterTable.isEditing())
		    filterTable.getCellEditor().stopCellEditing();
		table.getSorter().setRowFilter(null);	
		filterOffButton.setEnabled(false);
	}
	protected void deleteButtonClicked(ActionEvent e) {
		mode = formMode.Delete;
		if (eventNotifier != null)
			if (eventNotifier.deleteButtonClicked(selectedinfo) == __continue.dontallow)
				return;
		selectedinfo.setAction(persistenceaction.DELETE);

		populateTable_base();
	}
	protected void detailButtonClicked(ActionEvent e) {
		mode = formMode.Update;
		if (eventNotifier != null)
			if (eventNotifier.detailButtonClicked(selectedinfo) == __continue.dontallow)
				return;

		BaseDetailform window = null;
		if (detailFormClass == null)
			throw new RuntimeException("DetailForm not set");
		try {
			window = detailFormClass.getConstructor(JDesktopPaneTGI.class).newInstance(this.mdiPanel);
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
		window.setInfo(selectedinfo);

		if (window != null){
			this.getDesktopPane().add(window);
			this.getDesktopPane().moveToFront(window);
			window.setVisible(true);
			window.addChangeListener(this);
		}
		window.setLocation(0, 0);
	}
	protected void newButtonClicked(ActionEvent e) {
		mode = formMode.New;
		if (eventNotifier != null)
			if (eventNotifier.newButtonClicked() == __continue.dontallow)
				return;
		
		BaseDetailform window = null;
		if (detailFormClass == null)
			throw new RuntimeException("DetailForm not set");
		try {
			window = detailFormClass.getConstructor(JDesktopPaneTGI.class).newInstance(this.mdiPanel);
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
			window.setMdiPanel(this.getMdiPanel());
			window.setInfo(newInfo);
		} catch (InstantiationException e1) {
        	ExceptionLogger.ProcessException(e1,this);
		} catch (IllegalAccessException e1) {
        	ExceptionLogger.ProcessException(e1,this);
		}
		if (window != null){
			this.getDesktopPane().add(window);
			this.getDesktopPane().moveToFront(window);
			window.setVisible(true);
			//window.addInternalFrameListener(childFormAdapter);
			window.addChangeListener(this);
			window.setLocation(0, 0);
		}
	}
	protected void exportButtonClicked(ActionEvent e) {
		// Vraag om naam excel bestand
		String documentname = "export.xls";
		String headerLabel;

		File selectedFile = null;

		selectedFile = SelectFilename();
		if (selectedFile == null)
			return;
		else {
			if (selectedFile.exists()){
				if (JOptionPane.showConfirmDialog(this, "Bestand "
						+ selectedFile.getAbsolutePath()
						+ " bestaat al. Wilt u het verwijderen?", "Verwijderen",
						JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION){
					if (!selectedFile.delete()){
						JOptionPane.showMessageDialog(this, "Kan bestand niet verwijderen.");
						return;
					}
				}
				else
					return;
			}
			documentname = selectedFile.getAbsolutePath();
		}
		
		//
        // Create an instance of HSSFWorkbook.
        //
        HSSFWorkbook workbook = new HSSFWorkbook();

        //
        // Create two sheets in the excel document and name it First Sheet and
        // Second Sheet.
        //
        HSSFSheet Sheet = workbook.createSheet("Blad 1");
        
		DefaultTableModel model = (DefaultTableModel)table.getModel();
        HSSFRow rowHeader = Sheet.createRow(0);
		
		for (int i=0;i<model.getColumnCount();i++){
			headerLabel = model.getColumnName(i);
	        HSSFCell cellA = rowHeader.createCell(i);
	        cellA.setCellValue(new HSSFRichTextString(headerLabel));
		}
		for (int row=0;row<table.getRowCount();row++)
		{
			HSSFRow excelRow = Sheet.createRow(row+1);
			int actualrow = table.convertRowIndexToModel(row);
			for (int column=0;column<model.getColumnCount();column++)
			{
				HSSFCell cell = excelRow.createCell(column);
				Object value = ((Vector<?>)model.getDataVector().elementAt(actualrow)).elementAt(column);
				if (value == null)
					cell.setCellValue("");
				else
				if (value instanceof Date){
		        	String strDate = new SimpleDateFormat("dd-MM-yyyy").format((Date)value);
					cell.setCellValue(strDate);
		        }
				else
					cell.setCellValue(value.toString());
			}
		}
		
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(new File(documentname));
            workbook.write(fos);
        } catch (IOException ex) {
        	ExceptionLogger.ProcessException(ex,this);
        } finally {
            if (fos != null) {
                try {
                    fos.flush();
                    fos.close();
                } catch (IOException ex) {
                	ExceptionLogger.ProcessException(ex,this);
                }
            }
        }
	}
	protected File SelectFilename() {
		File selectedfile;
		JFileChooser fd = new JFileChooser();

		fd.setDialogType(JFileChooser.SAVE_DIALOG);
		fd.setFileSelectionMode(JFileChooser.FILES_ONLY);
		int retval = fd.showSaveDialog(this);
		if (retval == JFileChooser.APPROVE_OPTION) {
			selectedfile = fd.getSelectedFile();
			String filename = selectedfile.getAbsolutePath();
			if (filename.endsWith(".xls"))
				return selectedfile;
			else {
				return new File(filename + ".xls");
			}
		} else
			return null;

	}

	protected void closeButtonClicked(ActionEvent e) {
		this.setVisible(false);
		try {
			this.setClosed(true);
		} catch (PropertyVetoException e1) {
        	ExceptionLogger.ProcessException(e1,this);
		}
	}
	/******************************
	 * Implementation of Interfaces
	 ******************************/
	@Override
	public void componentResized(ComponentEvent e) {
		panelAction.setBounds(0,this.getHeight()-60,panelAction.getWidth(),panelAction.getHeight());
		panelClose.setBounds(this.getWidth()-140,this.getHeight()-60,panelClose.getWidth(),panelClose.getHeight());
		//filterScrollpane.setBounds(36,0,this.getWidth() - (36+5)*2,filterScrollpane.getHeight());
		if (isinitialized == false){
			/*
			 * Nu slaan we de coordinaten van de JTable op, zodat we de tabel kunnen 
			 * vergroten en verkleinen.
			 */
			rightMargin = this._contentPane.getWidth() - this.tableScrollPane.getX() - this.tableScrollPane.getWidth();
			bottomMargin = this._contentPane.getHeight() - this.tableScrollPane.getY() - this.tableScrollPane.getHeight();
			isinitialized = true;
		}
		else{
			this.tableScrollPane.setBounds(this.tableScrollPane.getX()
							   			 , this.tableScrollPane.getY()
							   			 , (this._contentPane.getWidth() - this.tableScrollPane.getX() - rightMargin)
							   			 , (this._contentPane.getHeight() - this.tableScrollPane.getY() - bottomMargin));
		}
		filterScrollpane.setBounds(this.tableScrollPane.getX(),0,this.tableScrollPane.getWidth(),filterScrollpane.getHeight());
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
		newInfo = null;
		populateTable_base();
	}
	@Override
	public void ClosedAfterCancel() {
	}
	@Override
	public void Closed() {
	}
	public boolean isFiltering() {
		return filtering;
	}
	public void setFiltering(boolean filtering) {
		this.filtering = filtering;
		if (!filtering)
		{
			filterScrollpane.setVisible(false);
			filterButton.setVisible(false);
			filterButton.setEnabled(false);
		}
		else
		{
			filterScrollpane.setVisible(true);
			filterButton.setVisible(true);
			filterButton.setEnabled(true);
		}
	}
	/*
	 * Getter and setters
	 */
	public JButton getExportButton() {
		return exportButton;
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
	public JTable getTable() {
		System.out.println("getTable() should not be called!!");
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
	public JDesktopPaneTGI getMdiPanel() {
		return mdiPanel;
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
	
}
