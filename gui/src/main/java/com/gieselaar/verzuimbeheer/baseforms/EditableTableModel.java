package com.gieselaar.verzuimbeheer.baseforms;

import javax.swing.table.DefaultTableModel;

public class EditableTableModel extends DefaultTableModel
{
	private static final long serialVersionUID = 1L;
	EditableTableModel( ){
		super();
    }
	public boolean isCellEditable(int row,int cols){
		// for a non-editable column return false;
		return true;                                                                                    
    }
}

