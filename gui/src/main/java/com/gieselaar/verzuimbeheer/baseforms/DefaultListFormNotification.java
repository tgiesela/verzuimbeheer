package com.gieselaar.verzuimbeheer.baseforms;

import javax.swing.table.TableModel;

import com.gieselaar.verzuimbeheer.services.InfoBase;

public class DefaultListFormNotification implements ListFormNotification{

	@Override
	public void populateTableRequested() {}

	@Override
	public __continue newButtonClicked() {return __continue.allow;}

	@Override
	public __continue deleteButtonClicked(InfoBase info) {return __continue.allow;}

	@Override
	public __continue detailButtonClicked(InfoBase info) {return __continue.allow;}

	@Override
	public void newCreated(InfoBase info) {}

	@Override
	public void newInfoAdded(InfoBase info) {}

	@Override
	public void rowSelected(InfoBase info) {}

	@Override
	public void rowDoubleClicked(InfoBase info) {}

	@Override
	public void detailFormClosed() {}

	@Override
	public void populateTableComplete() {}

	@Override
	public __continue filterButtonClicked(TableModel tableModel) {return __continue.allow;}
	
}

