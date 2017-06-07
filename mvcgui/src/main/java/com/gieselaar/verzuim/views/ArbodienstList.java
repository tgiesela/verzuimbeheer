package com.gieselaar.verzuim.views;

import java.util.List;
import com.gieselaar.verzuim.controllers.AbstractController;
import com.gieselaar.verzuim.controllers.ArbodienstController;
import com.gieselaar.verzuim.controllers.ArbodienstController.__arbodienstfields;
import com.gieselaar.verzuim.viewsutils.ColorTableModel;
import com.gieselaar.verzuimbeheer.services.ArbodienstInfo;

public class ArbodienstList extends AbstractList {

	private static final long serialVersionUID = 1L;
	private ArbodienstController arbodienstcontroller;
	public ArbodienstList (AbstractController controller) {
		super("Overzicht arbodiensten", controller);
		arbodienstcontroller = (ArbodienstController) controller;
		initialize();
		
		addColumn(__arbodienstfields.NAAM.getValue(),"Naam", 200);
		addColumn(__arbodienstfields.PLAATS.getValue(),"Plaats",100);
	}
	void initialize(){
		/* nothing special here */
	}
	@Override
	public void refreshTable() {
		List<ArbodienstInfo> arbodiensten;
		arbodiensten = arbodienstcontroller.getArbodienstList();

		/* Save and remove sorts and filters before populating the table */
		this.getDatatable().disableSorter();
		
		arbodienstcontroller.getTableModel(arbodiensten, (ColorTableModel) this.getTable().getModel(), colsinview);

		/* Add sorts and filters again */
		this.getDatatable().enableSorter();
	}

}
