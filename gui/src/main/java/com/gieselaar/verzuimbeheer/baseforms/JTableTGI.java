package com.gieselaar.verzuimbeheer.baseforms;

import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Vector;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.DefaultCellEditor;
import javax.swing.InputMap;
import javax.swing.JTable;
import javax.swing.KeyStroke;
import javax.swing.SwingConstants;
import javax.swing.border.LineBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableColumnModelEvent;
import javax.swing.event.TableColumnModelListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableColumnModel;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

import com.gieselaar.verzuimbeheer.components.JTextFieldTGI;
import com.gieselaar.verzuimbeheer.utils.CursorController;

public class JTableTGI extends JTable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private ColorTableModel tableModel;
	private EditableTableModel editModel;
	private DefaultTableColumnModel columnModel;
	private TableRowSorter<TableModel> sorter;
	private JFrameTGINotification eventNotifier;
	private int selectedRow;
	private ArrayList<Object> objectList = null; /*
													 * bevat rijen die in de tabel
													 * op het scherm staan
													 */
	private boolean editable = false;

	public JTableTGI(boolean editable) {
		super();

		this.editable = editable;
		objectList = new ArrayList<>();

		this.addMouseListener(CursorController.createListener(this, new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				tableClicked(e);
			}
		}));
		this.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent e) {
				tableClicked(null);
			}
		});
		if (editable) {
			editModel = new EditableTableModel();
			this.setModel(editModel);
			this.setTableHeader(null);
			this.setRowSelectionAllowed(false);
		} else {
			tableModel = new ColorTableModel();
			this.setModel(tableModel);
			this.setDefaultRenderer(Object.class, new ColorTableCellRenderer());
		}
		columnModel = new DefaultTableColumnModel();
		this.setColumnModel(columnModel);
		this.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		this.setAutoCreateColumnsFromModel(false);

		sorter = new TableRowSorter<TableModel>(this.getModel());
		this.setRowSorter(sorter);

		// Code to handle Enter key in Filter Pane
		Action enterKeyActionDatatable = CursorController.createListener(this, new AbstractAction() {
			private static final long serialVersionUID = 1L;

			// enable filter when the user presses enter
			public void actionPerformed(ActionEvent e) {
				tableDoubleClicked(e);
			}
		});
		/*
		 * Als de ENTER toets in de JTable wordt ingedrukt, wordt de
		 * wijzigingstoets gesimuleerd.
		 * 
		 */
		InputMap imData = this.getInputMap();
		ActionMap amData = this.getActionMap();
		imData.put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "ENTER");
		amData.put("ENTER", enterKeyActionDatatable);

		this.getColumnModel().addColumnModelListener(new TableColumnModelListener() {

			@Override
			public void columnSelectionChanged(ListSelectionEvent e) {
			}

			@Override
			public void columnRemoved(TableColumnModelEvent e) {
			}

			@Override
			public void columnMoved(TableColumnModelEvent e) {
			}

			@Override
			public void columnMarginChanged(ChangeEvent e) {
				checkColumnMarginChanged(e);
			}

			@Override
			public void columnAdded(TableColumnModelEvent e) {
			}
		});
	}

	public boolean isCellEditable(int rowIndex, int colIndex) {
		return editable;
	}

	protected void checkColumnMarginChanged(ChangeEvent e) {
		if (this.getTableHeader() != null) {
			TableColumn col = this.getTableHeader().getResizingColumn();
			if (col != null) {
				int colIndex = col.getModelIndex();
				if (eventNotifier != null)
					eventNotifier.colSizeChanged(this, colIndex, col.getWidth());
			}
		}
	}

	private void tableClicked(MouseEvent e) {

		int row;
		Object localinfo;
		row = this.getSelectedRow();
		if (row < 0)
			;// Row is filtered out
		else {
			selectedRow = this.convertRowIndexToModel(row);
			if ((objectList.size() - 1) >= selectedRow) {
				localinfo = (Object) objectList.get(selectedRow);
				if ((eventNotifier != null))
					eventNotifier.rowSelected(localinfo);
				if (e != null) {
					if (e.getClickCount() > 1) {
						if (eventNotifier != null)
							eventNotifier.rowDoubleClicked(localinfo);
					}
				}
			}
		}
	}

	private void tableDoubleClicked(ActionEvent e) {

		int row;
		Object localinfo;
		row = this.getSelectedRow();
		if (row < 0)
			;// Row is filtered out
		else {
			selectedRow = this.convertRowIndexToModel(row);
			if ((objectList.size() - 1) >= selectedRow) {
				localinfo = (Object) objectList.get(selectedRow);
				if (e != null) {
					if (eventNotifier != null)
						eventNotifier.rowDoubleClicked(localinfo);
				}
			}
		}
	}

	public void addColumn(Object headerLabel, Object[] values, int width, Class<?> datatype) {
		DefaultTableModel model = (DefaultTableModel) this.getModel();
		TableColumn col = new TableColumn(model.getColumnCount());

		// Ensure that auto-create is off
		if (this.getAutoCreateColumnsFromModel()) {
			throw new IllegalStateException();
		}
		col.setHeaderValue(headerLabel);
		if (width > 0)
			col.setPreferredWidth(width);

		this.addColumn(col);
		if (values == null)
			model.addColumn(headerLabel.toString());
		else
			model.addColumn(headerLabel.toString(), values);

		TableColumnModel colModel = this.getColumnModel();
		if (model instanceof ColorTableModel) {
			((ColorTableModel) model).setDataType(model.getColumnCount() - 1, datatype);
			colModel.getColumn(model.getColumnCount() - 1).setCellRenderer(new ColorTableCellRenderer());
		} else {
			JTextFieldTGI textField = new JTextFieldTGI();
			textField.setBorder(new LineBorder(Color.BLACK));

			DefaultCellEditor dce = new DefaultCellEditor(textField);
			col.setCellEditor(dce);
		}
		if (datatype == BigDecimal.class) {
			DecimalFormatRenderer rightRenderer = new DecimalFormatRenderer();
			rightRenderer.setHorizontalAlignment(SwingConstants.RIGHT);
			colModel.getColumn(model.getColumnCount() - 1).setCellRenderer(rightRenderer);
		}

	}

	static class DecimalFormatRenderer extends DefaultTableCellRenderer {
		private static final long serialVersionUID = 1L;
		private static final DecimalFormat formatter = new DecimalFormat("#,00");

		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
				int row, int column) {

			int selectedRow;
	        int selectedCol;

	        if (table.getModel() instanceof ColorTableModel){
				ColorTableModel model = (ColorTableModel) table.getModel();
		        Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
				selectedRow = table.convertRowIndexToModel(row);
				selectedCol = table.convertColumnIndexToModel(column);
	        	if (value != null){
	        		// First format the cell value as required
	        		NumberFormat amountFormat = NumberFormat.getNumberInstance();
	        		amountFormat.setMaximumFractionDigits(2);
	        		amountFormat.setMinimumFractionDigits(2);
	        		value = amountFormat.format((BigDecimal)value);
	        	}
		        if (isSelected){
		    		//log.info("Selected row: " + row + ",Selected column: " + column);
		        	c.setBackground(Color.BLUE);
		        }
		        else{
		        	c.setBackground(model.getRowColour(selectedRow));
		        	Color cellcolor;
		        	cellcolor = model.getCellColour(selectedRow, selectedCol);
		        	if (cellcolor != null){
		        		c.setBackground(cellcolor);
		        	}
		        }
        		return super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
	        } else {
		    	return super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
	        }
		}
	}

	public void addRow(Vector<Object> data, Object obj, Color color) {
		if (editable) {
			EditableTableModel model = (EditableTableModel) this.getModel();
			model.addRow(data);
		} else {
			ColorTableModel model = (ColorTableModel) this.getModel();
			model.addRow(data);
			model.setRowColour(model.getRowCount() - 1, color);
		}
		objectList.add(obj);
	}

	public void deleteAllRows() {
		DefaultTableModel model = (DefaultTableModel) this.getModel();
		model.setRowCount(0);
		objectList.clear();
	}

	public void setCellColour(int row, int column, Color color) {
		if (this.getModel() instanceof ColorTableModel) {
			ColorTableModel model = (ColorTableModel) this.getModel();
			model.setCellColour(row, column, color);
		}
	}

	public TableRowSorter<TableModel> getSorter() {
		return sorter;
	}

	public void setSorter(TableRowSorter<TableModel> sorter) {
		this.sorter = sorter;
	}

	public JFrameTGINotification getEventNotifier() {
		return eventNotifier;
	}

	public void setEventNotifier(JFrameTGINotification eventNotifier) {
		this.eventNotifier = eventNotifier;
	}
}
