package com.gieselaar.verzuimbeheer.baseforms;

import javax.swing.table.TableModel;

import com.gieselaar.verzuimbeheer.services.InfoBase;

public interface ListFormNotification {
	public enum __continue{
		dontallow,
		allow
	}
	public void populateTableRequested();
	public void populateTableComplete();
	public __continue newButtonClicked();
	public __continue deleteButtonClicked(InfoBase info);
	public __continue detailButtonClicked(InfoBase info);
	public __continue filterButtonClicked(TableModel tableModel);
	public void newCreated(InfoBase info);
	public void newInfoAdded(InfoBase info);
	public void rowSelected(InfoBase info);
	public void rowDoubleClicked(InfoBase info);
	public void detailFormClosed();
}
