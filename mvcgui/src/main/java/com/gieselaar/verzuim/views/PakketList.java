package com.gieselaar.verzuim.views;

import java.util.List;

import com.gieselaar.verzuim.controllers.PakketController;
import com.gieselaar.verzuim.controllers.PakketController.__pakketfields;
import com.gieselaar.verzuim.viewsutils.ColorTableModel;
import com.gieselaar.verzuimbeheer.services.PakketInfo;

public class PakketList extends AbstractList {
	private static final long serialVersionUID = -4160789227769412510L;

	private PakketController pakketcontroller;
	
	public PakketList(PakketController controller) {
		super("Overzicht pakketten", controller);
		pakketcontroller = controller;
		initialize();
		
		addColumn(__pakketfields.PAKKETNAAM.getValue(),"Pakketnaam", 150);
	}
	private void initialize(){
		/* nothing special */		
	}
	@Override
	public void refreshTable() {
		List<PakketInfo> pakketten;
		pakketten = pakketcontroller.getPakketList();

		/* Save and remove sorts and filters before populating the table */
		this.getDatatable().disableSorter();
		
		pakketcontroller.getTableModel(pakketten, (ColorTableModel) this.getTable().getModel(), colsinview);

		/* Add sorts and filters again */
		this.getDatatable().enableSorter();
	}
}
