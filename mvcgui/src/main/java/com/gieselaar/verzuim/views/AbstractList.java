package com.gieselaar.verzuim.views;

import java.awt.Color;
import java.awt.Container;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.KeyEvent;
import java.beans.PropertyVetoException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.ImageIcon;
import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.KeyStroke;
import javax.swing.RowFilter;
import javax.swing.event.ChangeEvent;
import javax.swing.event.InternalFrameEvent;
import javax.swing.event.InternalFrameListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.TableColumnModelEvent;
import javax.swing.event.TableColumnModelListener;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

import org.apache.log4j.Logger;

import com.gieselaar.verzuim.controllers.AbstractController;
import com.gieselaar.verzuim.controllers.AbstractController.__formmode;
import com.gieselaar.verzuim.interfaces.ControllerEventListener;
import com.gieselaar.verzuim.utils.ExceptionLogger;
import com.gieselaar.verzuim.viewsutils.ColorTableCellRenderer;
import com.gieselaar.verzuim.viewsutils.ColorTableModel;
import com.gieselaar.verzuim.viewsutils.CursorController;
import com.gieselaar.verzuimbeheer.exceptions.VerzuimApplicationException;
import com.gieselaar.verzuimbeheer.services.InfoBase;
import com.gieselaar.verzuimbeheer.utils.WildcardRegEx;

import java.awt.Image;

public abstract class AbstractList extends JInternalFrame 
								   implements ControllerEventListener{
	/**
	 * 
	 */
	protected static Logger log = Logger.getLogger("com.gieselaar"); 
	private static final long serialVersionUID = 1L;
	/* Form components */
	private Container contentPane;
	private DatatablePanel datatable;
	private JScrollPane filterScrollpane; 
	private JTable table;
	private JTable filterTable;
	private JButton filterOffButton;
	
	/* Constants */
	private static final int STATUSBARHEIGHT = 0;
	private static final int WINDOWMENUHEIGHT = 30;
	private static final int FILTERTABLEHEIGHT = 20;
	private int customfilterheight = 0;
	
	/* Variables */
	private transient ControllerEventListener thissink = this;
	private transient JInternalFrame thisform = this;
	protected transient List<Integer> colsinview = new ArrayList<>();
	private ColorTableModel model = new ColorTableModel(false);
	private ColorTableModel filtermodel = new ColorTableModel(true);
	/* Controller */
	protected transient AbstractController controller;
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
	private List<Registration> listeners = new ArrayList<>();

	public AbstractList(String title, AbstractController controller){
		this.controller = controller;
		if (controller != null){
			registerControllerListener(controller, this);
		}
		initializeBase(title);
	}
	private void initializeBase(String title){
		addInternalFrameListener();
		addComponentListener();
		setTitle(title);
		setResizable(true);
		setClosable(true);
		setMaximizable(true);
		setIconifiable(true);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(50, 50, 944, 552);

		contentPane = new JPanel();
		contentPane.setLayout(null);
		setContentPane(contentPane);

		addEscapekeyHandler();
		addTables();
		addEnterkeyHandler();
		addPanelClose();
		addButtonsIconPanel();
	}
	private void addButtonsIconPanel() {
		int iconpanelwidth = datatable.getPanelIcons().getWidth();
		
		JButton btnRefresh = new JButton("");
		btnRefresh.setToolTipText("Verversen lijst");
		btnRefresh.setBounds(0, 0, iconpanelwidth, iconpanelwidth);
		ImageIcon icon = new ImageIcon(AbstractList.class.getResource("/refresh-icon-614x460.png"));
		Image img = icon.getImage();
		Image newimg = img.getScaledInstance(28, 28, java.awt.Image.SCALE_DEFAULT);
		btnRefresh.setIcon(new ImageIcon(newimg));
		datatable.getPanelIcons().add(btnRefresh);
		btnRefresh.addActionListener(CursorController.createListener(this,new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					controller.refreshDatabase();
				} catch (VerzuimApplicationException e1) {
					ExceptionLogger.ProcessException(e1, thisform);
				}
			}
		}));
		
		JButton filterButton = new JButton("");
		filterButton.setToolTipText("Filter gegevens");
		filterButton.setBounds(0, 1*(iconpanelwidth+1), iconpanelwidth, iconpanelwidth);
		filterButton.setSelectedIcon(new ImageIcon(AbstractList.class.getResource("/disable_filter_28x28.png")));
		filterButton.setIcon(new ImageIcon(AbstractList.class.getResource("/filter_28x28.png")));
		filterButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				filterTable();
			}
		});
		datatable.getPanelIcons().add(filterButton);
		
		filterOffButton = new JButton("");
		filterButton.setToolTipText("Filter uit");
		filterOffButton.setEnabled(false);
		filterOffButton.setIcon(new ImageIcon(AbstractList.class.getResource("/disable_filter_28x28.png")));
		filterOffButton.setBounds(0, 2*(iconpanelwidth+1), iconpanelwidth, iconpanelwidth);
		filterOffButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				disableFilter();
			}
		});
		datatable.getPanelIcons().add(filterOffButton);

		JButton exportButton = new JButton("");
		exportButton.setToolTipText("exporteren naar excel");
		exportButton.setIcon(new ImageIcon(AbstractList.class.getResource("/excel-8_28x28.png")));
		exportButton.setBounds(0, 3*(iconpanelwidth+1), iconpanelwidth, iconpanelwidth);
		datatable.getPanelIcons().add(exportButton);
		
		exportButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				controller.exportToExcel(datatable.getTable());
			}
		});
		
	}
	private void addComponentListener() {
		this.addComponentListener(new ComponentListener() {
			
			@Override
			public void componentShown(ComponentEvent e) {/* noop */}
			
			@Override
			public void componentResized(ComponentEvent e) {
				formResized();
			}
			
			@Override
			public void componentMoved(ComponentEvent e) {/* noop */}
			
			@Override
			public void componentHidden(ComponentEvent e) {/* noop */}
		});
	}
	private void addInternalFrameListener() {
		this.addInternalFrameListener(new InternalFrameListener() {
			@Override
			public void internalFrameOpened(InternalFrameEvent e) {/* noop */}
			@Override
			public void internalFrameIconified(InternalFrameEvent e) {/* noop */}
			@Override
			public void internalFrameDeiconified(InternalFrameEvent e) {/* noop */}
			@Override
			public void internalFrameDeactivated(InternalFrameEvent e) {/* noop */}
			@Override
			public void internalFrameClosing(InternalFrameEvent e) {
				thisform.setVisible(false);
			}
			@Override
			public void internalFrameClosed(InternalFrameEvent e) {/* noop */}
			@Override
			public void internalFrameActivated(InternalFrameEvent e) {/* noop */}
		});
	}
	private void setBoundsDatatable() {
		datatable.setBounds(0
						  , FILTERTABLEHEIGHT + 5 + customfilterheight
						  , this.getWidth()-17
						  , this.getHeight()-FILTERTABLEHEIGHT-STATUSBARHEIGHT-WINDOWMENUHEIGHT-customfilterheight - 5);
	}
	private void addPanelClose() {
		JButton closeButton = new JButton("Close");
		if (controller != null){
			closeButton.setActionCommand(controller.closeListActionCommand);
			closeButton.addActionListener(controller);
		}
		JPanel panelClose = datatable.getPanelClose();
		panelClose.add(closeButton);
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
	private void addEnterkeyHandler(){
		// Code to handle Enter key in Filter Pane
		Action enterKeyActionDatatable = CursorController.createListener(this, new AbstractAction() {
			private static final long serialVersionUID = 1L;

			// enable filter when the user presses enter
			@Override
			public void actionPerformed(ActionEvent e) {
				filterTable();
			}
		});
		/*
		 * Als de ENTER toets in de JTable wordt ingedrukt, wordt de
		 * wijzigingstoets gesimuleerd.
		 * 
		 */
		InputMap imData = filterTable.getInputMap();
		ActionMap amData = filterTable.getActionMap();
		imData.put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "ENTER");
		amData.put("ENTER", enterKeyActionDatatable);
	}
	private void addTables() {
		datatable = new DatatablePanel(controller);
		datatable.setToolbarWidth(36);
		setBoundsDatatable();
		contentPane.add(datatable);
		this.table = datatable.getTable();
		this.table.addComponentListener(new ComponentListener() {
			
			@Override
			public void componentShown(ComponentEvent e) {/* noop */}
			
			@Override
			public void componentResized(ComponentEvent e) {
				dataTableSizeChanged();
			}
			
			@Override
			public void componentMoved(ComponentEvent e) {/* noop */}
			
			@Override
			public void componentHidden(ComponentEvent e) {/* noop */}
		});
		this.table.getColumnModel().addColumnModelListener(new TableColumnModelListener() {

			@Override
			public void columnMarginChanged(ChangeEvent e) {
				if (table.getTableHeader() != null) {
					TableColumn col = table.getTableHeader().getResizingColumn();
					if (col != null) {
						int colIndex = col.getModelIndex();
						dataTablecolSizeChanged(colIndex, col.getWidth());
					}
				}
			}
			@Override
			public void columnAdded(TableColumnModelEvent arg0) {/* not used */}
			@Override
			public void columnMoved(TableColumnModelEvent arg0) {/* not used */}
			@Override
			public void columnRemoved(TableColumnModelEvent arg0) {/* not used */}
			@Override
			public void columnSelectionChanged(ListSelectionEvent arg0) {/* not used */}

		});
		filterTable = new JTable();
		filterTable.setRowSelectionAllowed(false);
		filterTable.setDefaultRenderer(TableColumn.class, new ColorTableCellRenderer());
		filterTable.setAutoCreateColumnsFromModel(false);
		filterTable.setTableHeader(null);
		filtermodel = new ColorTableModel(true);
		filterTable.setModel(filtermodel);
		filterScrollpane = new JScrollPane();

		filterScrollpane.setBounds(datatable.getToolbarWidth()
								 , customfilterheight
								 , this.getWidth()-datatable.getToolbarWidth()
								 , FILTERTABLEHEIGHT);
		filterScrollpane.setViewportView(filterTable);
		contentPane.add(filterScrollpane);

	}
	protected void dataTableSizeChanged() {
		filterScrollpane.setBounds(datatable.getToolbarWidth()
				 , customfilterheight
				 , this.getWidth()-datatable.getToolbarWidth()
				 , FILTERTABLEHEIGHT);
		Rectangle dimensionsTable;
		Rectangle dimensionsFiltertable;
		dimensionsTable = table.getBounds();
		dimensionsFiltertable = filterTable.getBounds();
		dimensionsFiltertable.setRect(dimensionsFiltertable.getX(), dimensionsFiltertable.getX(), dimensionsTable.getWidth(), dimensionsFiltertable.getHeight());
		filterTable.setBounds(dimensionsFiltertable);
		filterScrollpane.setSize(filterTable.getWidth(), filterScrollpane.getHeight());
		
		for (int i=0;i< filterTable.getColumnCount();i++){
			int width = datatable.getTable().getColumnModel().getColumn(i).getWidth();
			TableColumn filtercol = filterTable.getColumnModel().getColumn(i);
			filtercol.setWidth(width);
			filtercol.setMinWidth(width);
			filtercol.setMaxWidth(width);
		}
	}
	protected void dataTablecolSizeChanged(int colIndex, int width) {
		/* One of the columns of the datatable changed in size.
		 * We will adjust the same column in the filertable 
		 */
		TableColumn filtercol = filterTable.getColumnModel().getColumn(colIndex);
		filtercol.setWidth(width);
		filtercol.setMinWidth(width);
		filtercol.setMaxWidth(width);
		
	}
	protected void formResized() {
		setBoundsDatatable();
		datatable.resizewithPanel(null);
		dataTableSizeChanged();
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
	public void registerControllerListener(AbstractController controller, ControllerEventListener listener){
		controller.addControllerListener(listener);
		listeners.add(new Registration(controller,listener));
	}
	protected void addColumn(int colid, Object header, int width, Class<?> datatype){
		colsinview.add(colid);
		datatable.addColumn(colid, header, width, datatype);

		TableColumnModel colModel = datatable.getTable().getColumnModel();
		colModel.getColumn(colModel.getColumnCount() - 1).setCellRenderer(new ColorTableCellRenderer());

		TableColumn filtercol = new TableColumn(filtermodel.getColumnCount());
		filtercol.setHeaderValue(null);
		filtercol.setWidth(width);
		if (width > 0)
			filtercol.setPreferredWidth(width);
		filterTable.addColumn(filtercol);
		filtermodel.addColumn(filtercol);
		filtermodel.setRowCount(0);
		List<Object> dummycolumns = new ArrayList<>();
		for (int i=0;i<filtermodel.getColumnCount();i++){
			dummycolumns.add("");
		}
		filtermodel.addRow(dummycolumns, null, Color.WHITE);
	}
	protected void addColumn(int colid, Object headerLabel, int width){
		addColumn(colid, headerLabel, width, String.class);
	}
	protected void addColumn(int colid, Object headerLabel){
		addColumn(colid, headerLabel,  0);
	}
	protected void addRow(List<Object> cols, Object data, Color color){
		model.addRow(cols, data, color);
	}
	protected void filterTable() {
		String filterText;
		if (filterTable.isEditing())
		    filterTable.getCellEditor().stopCellEditing();
		filterOffButton.setEnabled(true);

		List<RowFilter<Object,Object>> rfs = new ArrayList<>(filterTable.getModel().getColumnCount());
		for (int i = 0; i < filterTable.getModel().getColumnCount(); i++)
		{
			filterText = (String)filterTable.getModel().getValueAt(0, i);
			if (filterText == null || filterText.isEmpty()){
				/* nothing to filter on */
			}else{
				ColorTableModel fltmodel = (ColorTableModel) table.getModel();
				Class<?> clazz = fltmodel.getColumnClass(i);
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
		datatable.getSorter().setRowFilter(RowFilter.andFilter(rfs));
	}
	protected void disableFilter() {
		if (filterTable.isEditing())
		    filterTable.getCellEditor().stopCellEditing();
		datatable.getSorter().setRowFilter(null);	
		filterOffButton.setEnabled(false);
	}


	/*
	 * Implementation of interfaces
	 */
	@Override
	public void setDetailFormmode(__formmode mode) {
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
	
	/*
	 * Getters and setters
	 */
	public JTable getTable() {
		return table;
	}
	public void setTable(JTable table) {
		this.table = table;
	}
	protected DatatablePanel getDatatable() {
		return datatable;
	}
	@Override
	public JPanel getContentPane() {
		return (JPanel) contentPane;
	}
	public TableRowSorter<TableModel> getSorter() {
		return datatable.getSorter();
	}
	public void setSorter(TableRowSorter<TableModel> sorter) {
		datatable.setSorter(sorter);
	}
	public void setFreespaceontop(Integer freespace){
		customfilterheight = freespace;
		setBoundsDatatable();
		dataTableSizeChanged();
	}
	public JScrollPane getFilterScrollpane() {
		return filterScrollpane;
	}
}
