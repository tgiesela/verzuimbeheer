package com.gieselaar.verzuim.interfaces;

import javax.swing.table.TableModel;

public class DefaultListFormNotification implements ListFormNotification{

	@Override
	public void detailFormClosed() {}

	@Override
	public __continue filterButtonClicked(TableModel tableModel) {return __continue.allow;}
	
}

