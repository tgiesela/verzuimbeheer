package com.gieselaar.verzuim.views;

import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.HierarchyEvent;
import java.awt.event.HierarchyListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.DefaultCellEditor;
import javax.swing.DefaultRowSorter;
import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.KeyStroke;
import javax.swing.RowFilter;
import javax.swing.RowSorter.SortKey;
import javax.swing.SwingConstants;
import javax.swing.border.LineBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

import org.apache.log4j.Logger;

import com.gieselaar.verzuim.components.JTextFieldTGI;
import com.gieselaar.verzuim.controllers.AbstractController;
import com.gieselaar.verzuim.controllers.AbstractController.__formmode;
import com.gieselaar.verzuim.interfaces.ControllerEventListener;
import com.gieselaar.verzuim.utils.ExceptionLogger;
import com.gieselaar.verzuim.viewsutils.ColorTableCellRenderer;
import com.gieselaar.verzuim.viewsutils.ColorTableModel;
import com.gieselaar.verzuim.viewsutils.CursorController;
import com.gieselaar.verzuimbeheer.exceptions.VerzuimApplicationException;
import com.gieselaar.verzuimbeheer.services.InfoBase;

public class DatatablePanel extends JPanel implements ControllerEventListener {
	/**
	 * 
	 */
	protected static Logger log = Logger.getLogger("com.gieselaar"); 
	public static final int BUTTONPANELHEIGHT = 30;
	private int toolbarwidth = 0;
	public static final int ACTIONPANELWIDTH = 650;
	private static final long serialVersionUID = 1L;
	/* Form components */
	private JScrollPane tableScrollPane;
	private JScrollPane filterScrollpane;
	private JTable table;
	private JPanel panelClose;
	private JPanel panelAction;
	private JPanel panelIcons;
	private JButton detailButton = null;
	private JButton deleteButton = null;

	/* Variables */
	private transient ControllerEventListener thissink = this;
	protected transient List<Integer> colsinview = new ArrayList<>();
	private ColorTableModel model = new ColorTableModel(false);
	private transient Object selectedData = null;

	/* Controller */
	private transient AbstractController controller;
	private transient TableRowSorter<TableModel> sorter;

	private List<? extends SortKey> keys;
	private RowFilter<? super TableModel, ? super Integer> filter;
	private ControllerEventListener thislistener = this;
	public DatatablePanel(final AbstractController controller) {
		this.controller = controller;
		if (controller != null) {
			controller.addControllerListener(this);
		}
		this.addHierarchyListener(new HierarchyListener() {
			
			@Override
			public void hierarchyChanged(HierarchyEvent e) {
				if(e.getChangeFlags() == HierarchyEvent.DISPLAYABILITY_CHANGED){       
			        if(!e.getComponent().isDisplayable()){  
			        	log.debug("Closing view: " + e.getComponent().toString());
			        	if (controller != null){
			        		controller.closeView(thislistener);
			        	}
			        }
				}
			}
		});
		initialize();
	}

	private void initialize() {
		this.setBounds(100, 100, 717, 475);
		this.setLayout(null);
		this.addComponentListener(new ComponentListener() {
			
			@Override
			public void componentShown(ComponentEvent e) {
				/* ignore */
			}
			
			@Override
			public void componentResized(ComponentEvent e) {
				resizewithPanel(e);
			}
			
			@Override
			public void componentMoved(ComponentEvent e) {
				/* ignore */
			}

			@Override
			public void componentHidden(ComponentEvent e) {
				/* ignore */
			}
		});


		addTables();
		addEnterkeyHandler();
		addPanelClose();
		addPanelAction();
		addPanelIcons();
		setModeRowSelected(false);
	}

	protected void resizewithPanel(ComponentEvent e) {
		panelIcons.setBounds(0, 0, toolbarwidth, this.getHeight() - BUTTONPANELHEIGHT);
		panelAction.setBounds(toolbarwidth, this.getHeight()-BUTTONPANELHEIGHT, ACTIONPANELWIDTH, BUTTONPANELHEIGHT);
		panelClose.setBounds(this.getWidth()-121, this.getHeight()-BUTTONPANELHEIGHT, 121, BUTTONPANELHEIGHT);
		tableScrollPane.setBounds(toolbarwidth, 0, this.getWidth()-36, this.getHeight()-BUTTONPANELHEIGHT);
	}

	private void addPanelIcons() {
		panelIcons = new JPanel();
		panelIcons.setBounds(0, 0, toolbarwidth, this.getHeight() - BUTTONPANELHEIGHT);
		panelIcons.setLayout(new FlowLayout(FlowLayout.CENTER));
		this.add(panelIcons);
	}
	private void addEnterkeyHandler(){
		// Code to handle Enter key 
		Action enterKeyActionDatatable = CursorController.createListener(this, new AbstractAction() {
			private static final long serialVersionUID = 1L;

			// enable filter when the user presses enter
			@Override
			public void actionPerformed(ActionEvent e) {
				if (detailButton.isEnabled()){
					controller.showRow(thissink, selectedData);
				}
			}
		});
		InputMap imData = this.getInputMap();
		ActionMap amData = this.getActionMap();
		imData.put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "ENTER");
		amData.put("ENTER", enterKeyActionDatatable);
	}

	private void addPanelAction() {
		JButton newButton = new JButton("Nieuw...");
		newButton.addActionListener(CursorController.createListener(this, new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				controller.addRow(thissink);
			}
		}));

		detailButton = new JButton("Wijzig...");
		detailButton.addActionListener(CursorController.createListener(this, new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				controller.showRow(thissink,selectedData);
			}
		}));

		detailButton.setDefaultCapable(true);
		if (this.getRootPane() != null) {
			this.getRootPane().setDefaultButton(detailButton);
		}

		deleteButton = new JButton("Verwijder...");
		deleteButton.addActionListener(CursorController.createListener(this, new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					controller.deleteRow(selectedData);
				} catch (VerzuimApplicationException e1) {
					ExceptionLogger.ProcessException(e1, null);
				}
			}
		}));
		panelAction = new JPanel();
		panelAction.setBounds(36, 445, ACTIONPANELWIDTH, 30);
		panelAction.setLayout(new FlowLayout(FlowLayout.LEFT));
		panelAction.add(newButton);
		panelAction.add(detailButton);
		panelAction.add(deleteButton);

		this.add(panelAction);
	}

	private void addPanelClose() {
		panelClose = new JPanel();
		panelClose.setBounds(this.getWidth()-121, this.getHeight()-BUTTONPANELHEIGHT, 121, BUTTONPANELHEIGHT);
		panelClose.setLayout(new FlowLayout(FlowLayout.RIGHT));
		this.add(panelClose);
	}

	private void addTables() {
		table = new JTable();
		table.setModel(model);
		table.setDefaultRenderer(TableColumn.class, new ColorTableCellRenderer());
		table.setAutoCreateColumnsFromModel(false);
		table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		table.addMouseListener(CursorController.createListener(this, new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				controller.tableClicked(thissink, table, e);
			}
		}));
		table.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
			
			@Override
			public void valueChanged(ListSelectionEvent e) {
				controller.tableSelectionChanged(thissink, table, e);
			}
		});
		sorter = new TableRowSorter<>(table.getModel());
		table.setRowSorter(this.sorter);
		tableScrollPane = new JScrollPane();
		tableScrollPane.setBounds(toolbarwidth, 0, this.getWidth()-36, this.getHeight()-BUTTONPANELHEIGHT);
		this.add(tableScrollPane);
		tableScrollPane.setViewportView(table);

	}

	protected void addColumn(int colid, Object header, int width, Class<?> datatype) {
		colsinview.add(colid);
		TableColumn col = new TableColumn(model.getColumnCount());
		col.setHeaderValue(header);
		col.setWidth(width);
		if (width > 0)
			col.setPreferredWidth(width);
		this.table.addColumn(col);
		model.addColumn(col); 		/* is call to addColumn of DefaultTableModel */
		model.addColumn(datatype);	/* is call to addColumn of ColorTableModel   */

		// Ensure that auto-create is off
		if (this.table.getAutoCreateColumnsFromModel()) {
			throw new IllegalStateException();
		}

		TableColumnModel colModel = table.getColumnModel();
		ColorTableCellRenderer colrenderer = new ColorTableCellRenderer();
		colrenderer.setHorizontalAlignment(SwingConstants.LEFT);
		colModel.getColumn(colModel.getColumnCount() - 1).setCellRenderer(colrenderer);

		JTextFieldTGI textField = new JTextFieldTGI();
		textField.setBorder(new LineBorder(Color.BLACK));
		DefaultCellEditor dce = new DefaultCellEditor(textField);
		col.setCellEditor(dce);
	}

	protected void addColumn(int colid, Object headerLabel, int width) {
		addColumn(colid, headerLabel, width, String.class);
	}

	protected void addColumn(int colid, Object headerLabel) {
		addColumn(colid, headerLabel, 0);
	}

	protected void addRow(List<Object> cols, Object data, Color color) {
		model.addRow(cols, data, color);
	}

	/*
	 * Implementation of interfaces
	 */

	/*
	 * Getters and setters
	 */
	public JTable getTable() {
		return table;
	}

	public void setTable(JTable table) {
		this.table = table;
	}

	protected JScrollPane getTableScrollPane() {
		return tableScrollPane;
	}

	protected JScrollPane getFilterScrollpane() {
		return filterScrollpane;
	}

	public JPanel getPanelClose() {
		return panelClose;
	}

	public JPanel getPanelAction() {
		return panelAction;
	}
	protected JPanel getPanelIcons() {
		return panelIcons;
	}

	public TableRowSorter<TableModel> getSorter() {
		return sorter;
	}

	public void setSorter(TableRowSorter<TableModel> sorter) {
		this.sorter = sorter;
	}

	@Override
	public void refreshTable() {
		setModeRowSelected(false);
		selectedData = null;
	}

	@Override
	public void setDetailFormmode(__formmode mode) {
		/* Only applicable to detailforms*/
	}

	@Override
	public void setData(InfoBase info) {
		/* Only applicable to detailforms*/
	}

	@Override
	public void close() {
		/* The datatable does not close, its parent container may close */
	}
	@Override
	public void formClosed(ControllerEventListener ves) {
	}
	@Override
	public void rowSelected(int selectedRow, Object data) {
		selectedData = data;
		setModeRowSelected(true);
	}
	protected void setModeRowSelected(boolean selected) {
		this.detailButton.setEnabled(selected);
		this.deleteButton.setEnabled(selected);
	}

	@SuppressWarnings("unchecked")
	public void disableSorter() {
		TableRowSorter<? super TableModel> sorter = (TableRowSorter<? super TableModel>) this.getTable().getRowSorter(); 
		keys = sorter.getSortKeys();
		filter = ((DefaultRowSorter<TableModel, Integer>) sorter).getRowFilter();
		
		sorter.setSortKeys(null);
		sorter.setRowFilter(null);
	}
	public void enableSorter() {
		@SuppressWarnings({ "unchecked"})
		TableRowSorter<? super TableModel> sorter = (TableRowSorter<? super TableModel>) this.getTable().getRowSorter(); 
		sorter.setSortKeys(keys);
		sorter.setRowFilter(filter);

	}
	public void setToolbarWidth(int width){
		toolbarwidth = width;
	}
	public int getToolbarWidth(){
		return toolbarwidth;
	}
}
