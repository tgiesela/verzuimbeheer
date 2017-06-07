package com.gieselaar.verzuimbeheer.utils;

import java.util.List;

import javax.swing.JTable;
import javax.swing.RowFilter;
import javax.swing.event.TableModelEvent;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

public class SelectionRowFilter extends RowFilter<Object, Object> {

	private List<?> availableRows = null;
	private JTable tblLeft = null;
	private JTable tblRight = null;
	private boolean[] hiddenRows;
	private DefaultTableModel leftModel = null; 
	private DefaultTableModel rightModel = null;
	private static SelectionRowFilter me;
	private TableRowSorter<TableModel> sorterleft;
	private TableRowSorter<TableModel> sorterright;
	public static SelectionRowFilter CreateSelectionRowFilter(JTable left, JTable right, List<?> rows){
		me = new SelectionRowFilter();
		me.setTblLeft(left);
		me.setTblRight(right);
		me.setAvailableRows(rows);
		me.sorterleft = new TableRowSorter<TableModel>(me.tblLeft.getModel());
		me.sorterleft.setRowFilter(me);
		me.sorterright = new TableRowSorter<TableModel>(me.tblRight.getModel());
		me.sorterright.setRowFilter(me);
		me.tblLeft.setRowSorter(me.sorterleft);
		me.tblRight.setRowSorter(me.sorterright);
		return me;
	}
	public JTable getTblLeft() {
		return tblLeft;
	}
	public void setTblLeft(JTable tblLeft) {
		this.tblLeft = tblLeft;
		this.leftModel = (DefaultTableModel) tblLeft.getModel();
	}
	public JTable getTblRight() {
		return tblRight;
	}
	public void setTblRight(JTable tblRight) {
		this.tblRight = tblRight;
		this.rightModel = (DefaultTableModel) tblRight.getModel();
	}
	public List<?> getAvailableRows() {
		return availableRows;
	}
	public void setAvailableRows(List<?> availableRows) {
		this.availableRows = availableRows;
		hiddenRows = null;
		if (availableRows == null)
			;
		else
			hiddenRows = new boolean[this.availableRows.size()];
	}
	@Override
	public boolean include(Entry<? extends Object, ? extends Object> entry) {
		Integer rownr;
		if (hiddenRows == null)
			return false;

		rownr = (Integer) entry.getIdentifier();
		if (entry.getModel().equals(leftModel))
			if (hiddenRows[rownr])
				return false;
			else
				return true;
		else
			if (hiddenRows[rownr])
				return true;
			else
				return false;
	}
	private void doHideRow(JTable table, int rownr){
		int selectedrow;
		selectedrow = table.convertRowIndexToModel(rownr);
		if (table == tblLeft)
			hiddenRows[selectedrow] = true;
		else
			if (table == tblRight)
				hiddenRows[selectedrow] = false;
			else
				return;
	}
	public void hideRow(JTable table, int rownr){
		doHideRow(table, rownr);
		leftModel.fireTableChanged(new TableModelEvent(this.leftModel));
		rightModel.fireTableChanged(new TableModelEvent(this.rightModel));
	}
	public void hideSelectedRows(JTable table){
		int selectedrows[] = table.getSelectedRows();

		for (int i = 0; i < selectedrows.length; i++)
			doHideRow(table,selectedrows[i]);

		leftModel.fireTableChanged(new TableModelEvent(this.leftModel));
		rightModel.fireTableChanged(new TableModelEvent(this.rightModel));
	}
	public void hideAllRows(JTable table){
		int rowcount = table.getRowCount();

		for (int i = 0; i < rowcount; i++)
			doHideRow(table, i);
		
		leftModel.fireTableChanged(new TableModelEvent(this.leftModel));
		rightModel.fireTableChanged(new TableModelEvent(this.rightModel));
	}
}
