package com.gieselaar.verzuim.views;

import java.util.List;
import com.gieselaar.verzuim.controllers.AbstractController;
import com.gieselaar.verzuim.controllers.UitkeringsinstituutController;
import com.gieselaar.verzuim.controllers.UitkeringsinstituutController.__uitkeringsinstituutfields;
import com.gieselaar.verzuim.viewsutils.ColorTableModel;
import com.gieselaar.verzuimbeheer.services.UitvoeringsinstituutInfo;

public class UitkeringsinstantieList extends AbstractList {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private UitkeringsinstituutController uitkeringsinstituutcontroller;
	/**
	 * Create the frame.
	 */
	public UitkeringsinstantieList(AbstractController controller) {
		super("Overzicht uitvoeringsinstanties", controller);
		uitkeringsinstituutcontroller = (UitkeringsinstituutController) controller;
		initialize();

		addColumn(__uitkeringsinstituutfields.NAAM.getValue(),"Naam",150);
		addColumn(__uitkeringsinstituutfields.PLAATS.getValue(),"Plaats", 200);
	}
	void initialize(){
		/* nothing special here */
	}
	@Override
	public void refreshTable() {
		List<UitvoeringsinstituutInfo> diensten;
		diensten = uitkeringsinstituutcontroller.getUitkeringsinstituutList();

		/* Save and remove sorts and filters before populating the table */
		this.getDatatable().disableSorter();
		
		uitkeringsinstituutcontroller.getTableModel(diensten, (ColorTableModel) this.getTable().getModel(), colsinview);

		/* Add sorts and filters again */
		this.getDatatable().enableSorter();
	}

}
