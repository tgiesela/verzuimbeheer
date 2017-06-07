package com.gieselaar.verzuim.viewsutils;

import java.awt.Color;
import java.awt.Component;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;

public class ColorTableCellRenderer extends DefaultTableCellRenderer {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        int selectedRow;
        int selectedCol;
        if (table.getModel() instanceof ColorTableModel){
			ColorTableModel model = (ColorTableModel) table.getModel();
	        Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
			selectedRow = table.convertRowIndexToModel(row);
			selectedCol = table.convertColumnIndexToModel(column);
			
	        if (value instanceof Date){
	        	String strDate = new SimpleDateFormat("dd-MM-yyyy").format((Date)value);
	        	this.setText(strDate);
	            this.setToolTipText(strDate);
	        }
	        else {
	        	if (value instanceof BigDecimal){
	    			ColorTableCellRenderer temp = (ColorTableCellRenderer) table.getColumnModel().getColumn(column).getCellRenderer();
	        		temp.setHorizontalAlignment(SwingConstants.RIGHT);
	        		NumberFormat amountFormat = NumberFormat.getNumberInstance();
	        		amountFormat.setMaximumFractionDigits(2);
	        		amountFormat.setMinimumFractionDigits(2);
	        		String strAmount = amountFormat.format((BigDecimal)value);
		        	this.setText(strAmount);
		            this.setToolTipText(strAmount);
	        	}else{
	        		String tip = (String)table.getModel().getValueAt(selectedRow, column);
	        		this.setToolTipText(tip);
	        	}
	        }
	        
	        /*
	         * Set the color of the selected row 
	         * and optionally of specific cell
	         */
			if (isSelected){
	        	c.setBackground(Color.BLUE);
	        }else{
	        	c.setBackground(model.getRowColor(selectedRow));
		        Color cellcolor;
		        cellcolor = model.getCellColor(selectedRow, selectedCol);
		        if (cellcolor != null)
		        	c.setBackground(cellcolor);
	        }
	        return c;
	    } else {
	    	return super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
	    }
    }
}
