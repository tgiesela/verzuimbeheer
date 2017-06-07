package com.gieselaar.verzuim.views;

import java.util.List;
import com.gieselaar.verzuim.controllers.AfdelingController;
import com.gieselaar.verzuim.controllers.AfdelingController.__afdelingfields;
import com.gieselaar.verzuim.viewsutils.ColorTableModel;
import com.gieselaar.verzuimbeheer.services.AfdelingInfo;

public class AfdelingList extends AbstractList{

	private static final long serialVersionUID = 1L;
	private AfdelingController afdelingcontroller;
	public AfdelingList(AfdelingController controller) {
		super("Afdelingen overzicht", controller);
		afdelingcontroller = controller;
		initialize();
		
		addColumn(__afdelingfields.NAAM.getValue(),"Naam",120);
		addColumn(__afdelingfields.CONTACTPERSOON.getValue(),"Contactpersoon",120);
	}
	public void initialize(){
		/* nothing special */
	}
	@Override
	public void refreshTable() {
		List<AfdelingInfo> afdelingen;
		afdelingen = afdelingcontroller.getAfdelingList();

		/* Save and remove sorts and filters before populating the table */
		this.getDatatable().disableSorter();
		
		afdelingcontroller.getTableModel(afdelingen, (ColorTableModel) this.getTable().getModel(), colsinview);

		/* Add sorts and filters again */
		this.getDatatable().enableSorter();
	}
}
