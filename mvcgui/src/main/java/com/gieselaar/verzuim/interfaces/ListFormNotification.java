package com.gieselaar.verzuim.interfaces;

import javax.swing.table.TableModel;

public interface ListFormNotification {
	public enum __continue{
		dontallow,
		allow
	}
	public __continue filterButtonClicked(TableModel tableModel);
	public void detailFormClosed();
}
