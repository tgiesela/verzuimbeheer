package com.gieselaar.verzuim.views;

import java.util.List;
import com.gieselaar.verzuim.controllers.AbstractController;
import com.gieselaar.verzuim.controllers.BedrijfsartsController;
import com.gieselaar.verzuim.controllers.BedrijfsartsController.__bedrijfsartsfields;
import com.gieselaar.verzuim.viewsutils.ColorTableModel;
import com.gieselaar.verzuimbeheer.services.BedrijfsartsInfo;

public class BedrijfsartsenList extends AbstractList {
	private static final long serialVersionUID = 1L;
	private BedrijfsartsController bedrijfsartscontroller;
	/**
	 * Create the frame.
	 */
	public BedrijfsartsenList(AbstractController controller) {
		super("Overzicht bedrijfsartsen", controller);
		bedrijfsartscontroller = (BedrijfsartsController) controller;
		initialize();

		addColumn(__bedrijfsartsfields.ACHTERNAAM.getValue(),"Achternaam",100);
		addColumn(__bedrijfsartsfields.VOORNAAM.getValue(),"Voornaam", 80);
		addColumn(__bedrijfsartsfields.TELEFOON.getValue(),"Telefoon", 80);
		addColumn(__bedrijfsartsfields.EMAIL.getValue(),"Email", 80);
	}
	void initialize(){
		/* nothing special here */
	}
	@Override
	public void refreshTable() {
		List<BedrijfsartsInfo> artsen;
		artsen = bedrijfsartscontroller.getBedrijfsartsenList();

		/* Save and remove sorts and filters before populating the table */
		this.getDatatable().disableSorter();
		
		bedrijfsartscontroller.getTableModel(artsen, (ColorTableModel) this.getTable().getModel(), colsinview);

		/* Add sorts and filters again */
		this.getDatatable().enableSorter();
	}

}
