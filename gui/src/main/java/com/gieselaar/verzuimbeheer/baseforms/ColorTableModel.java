package com.gieselaar.verzuimbeheer.baseforms;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import javax.swing.table.DefaultTableModel;

public class ColorTableModel extends DefaultTableModel
{
	private static final long serialVersionUID = 1L;
	private List<Color> rowColours = new ArrayList<Color>();
	private List<Class<?>> columnList = new ArrayList<>();/* bevat informatie over de kolommen */
	private class Cell{
		int column;
		int row;
		Color color;
		Cell(int row,int column, Color color){
			this.column = column;
			this.row = row;
			this.color = color;
		}
	}
	private List<Cell> cellColours = new ArrayList<Cell>();
	public ColorTableModel(){
		super();
	}
	public void setRowColour(int row, Color c) {
		if (row >= rowColours.size())
			rowColours.add(c);
		else
			rowColours.set(row, c);
        fireTableRowsUpdated(row, row);
    }
	public void setCellColour(int row, int column, Color c){
		Cell cell = null;
		for (int i = 0; i<cellColours.size();i++)
		{
			if (cellColours.get(i).row == row && cellColours.get(i).column == column)
			{
				cell = cellColours.get(i);
				cell.color = c;
				cellColours.set(i,cell);
				break;
			}
		}
		
		if (cell == null)
			cellColours.add(new Cell(row,column, c));

		fireTableCellUpdated(row, column);
	}
    public Color getRowColour(int row) {
        return rowColours.get(row);
    }
    public Color getCellColour(int row, int column) {
		Cell cell = null;
		for (int i = 0; i<cellColours.size();i++)
		{
			if (cellColours.get(i).row == row && cellColours.get(i).column == column)
			{
				cell = cellColours.get(i);
				return cell.color;
			}
		}
    	return null;
    }
    public void setDataType(int colinx, Class<?> datatype){
		columnList.add(datatype);
    }
    @Override
    public Class<?> getColumnClass(int colinx){
    	if (columnList.size() >= (colinx+1)){
   			return columnList.get(colinx);
   		}
		return String.class;
    }
}
