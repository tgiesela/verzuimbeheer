package com.gieselaar.verzuim.views;

import java.util.List;
import com.gieselaar.verzuim.controllers.GebruikerController;
import com.gieselaar.verzuim.controllers.GebruikerController.__gebruikerfields;
import com.gieselaar.verzuim.viewsutils.ColorTableModel;
import com.gieselaar.verzuimbeheer.services.GebruikerInfo;

public class GebruikerList extends AbstractList{
	private static final long serialVersionUID = 2998408587286724321L;
	private GebruikerController gebruikercontroller;
	public GebruikerList(GebruikerController controller){
		super("Overzicht gebruikers", controller);
		gebruikercontroller = controller;
		initialize();
		
		addColumn(__gebruikerfields.ID.getValue(),"Id",100);
		addColumn(__gebruikerfields.ACHTERNAAM.getValue(),"Achternaam",200);
	}
	public void initialize(){
		/* nothing special */		
	}
	@Override
	public void refreshTable() {
		List<GebruikerInfo> gebruikers;
		gebruikers = gebruikercontroller.getGebruikerList();

		/* Save and remove sorts and filters before populating the table */
		this.getDatatable().disableSorter();
		
		gebruikercontroller.getTableModel(gebruikers, (ColorTableModel) this.getTable().getModel(), colsinview);

		/* Add sorts and filters again */
		this.getDatatable().enableSorter();
	}
}
