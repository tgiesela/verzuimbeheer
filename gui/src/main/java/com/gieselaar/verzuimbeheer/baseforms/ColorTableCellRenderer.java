package com.gieselaar.verzuimbeheer.baseforms;

import java.awt.Color;
import java.awt.Component;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

public class ColorTableCellRenderer extends DefaultTableCellRenderer {

	private static final long serialVersionUID = 1L;
	//private final static Logger log = Logger.getLogger(ColorTableCellRenderer.class);
	@Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        int selectedRow;
        int selectedCol;
        if (table.getModel() instanceof ColorTableModel){
			ColorTableModel model = (ColorTableModel) table.getModel();
	        Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
			selectedRow = table.convertRowIndexToModel(row);
			selectedCol = table.convertColumnIndexToModel(column);
	        if (isSelected){
	    		//log.info("Selected row: " + row + ",Selected column: " + column);
	        	c.setBackground(Color.BLUE);
	        }
	        else
	        	c.setBackground(model.getRowColour(selectedRow));
	
	        if (value instanceof Date){
	        	String strDate = new SimpleDateFormat("dd-MM-yyyy").format((Date)value);
	        	this.setText(strDate);
	            this.setToolTipText(strDate);
	        }
	        else {
	        	if (value instanceof BigDecimal){
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
	        
	        Color cellcolor;
	        cellcolor = model.getCellColour(selectedRow, selectedCol);
	        if (cellcolor != null)
	        	c.setBackground(cellcolor);
	        return c;
	    } else {
	    	return super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
	    }
    }
}

