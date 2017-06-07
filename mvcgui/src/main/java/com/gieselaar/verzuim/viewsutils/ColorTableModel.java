package com.gieselaar.verzuim.viewsutils;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import javax.swing.table.DefaultTableModel;

public class ColorTableModel extends DefaultTableModel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private boolean editable = false;
	private class TableCell{
		private Color color;
		private TableCell(Color color){
			this.color = color;
		}
	}
	private class TableRow{
		private Color color;
		private Object data;
		private List<TableCell> cellColours = new ArrayList<>();
		TableRow(int colcount, Object data, Color color){
			this.data = data;
			this.color = color;
			for (int i=0;i<colcount;i++){
				cellColours.add(new TableCell(color));
			}
		}
	}
	private class TableCol{
		private Class<?> datatype;
		TableCol(Class<?> datatype){
			this.datatype = datatype;
		}
	}
	public ColorTableModel(boolean editable){
		super();
		this.editable = editable;
	}
	@Override 
	public boolean isCellEditable(int row, int column){
		return editable;
	}
	private transient List<TableRow> rowdata = new ArrayList<>();
	private transient List<TableCol> coldata = new ArrayList<>();
	public void addColumn(Class<?> datatype){
		coldata.add(new TableCol(datatype));
	}
	public void addRow(List<Object> cols, Object data, Color color){
		super.addRow(new Vector<Object>(cols));
		rowdata.add(new TableRow(this.getColumnCount(),data,color));
	}
	public void addRow(List<Object> cols, Object data){
		addRow(cols, data, null);
	}
	public Color getRowColor(int selectedRow) {
		return rowdata.get(selectedRow).color;
	}
	public Object getRowData(int selectedRow) {
		return rowdata.get(selectedRow).data;
	}
	public void setCellColour(int row, int column, Color c){
		TableCell cell = rowdata.get(row).cellColours.get(column);
		cell.color = c;
	}
	public Color getCellColor(int selectedRow, int selectedCol) {
		return rowdata.get(selectedRow).cellColours.get(selectedCol).color;
	}
    @Override
    public Class<?> getColumnClass(int colinx){
    	if (coldata.size() >= (colinx+1)){
   			return coldata.get(colinx).datatype;
   		}
		return String.class;
    }
	@Override
	public void setRowCount(int rowCount){
		super.setRowCount(rowCount);
		if (rowCount == 0){
			rowdata.clear();
		}else{
			while (rowdata.size()>rowCount){
				rowdata.remove(rowCount);
			}
		}
	}
}
