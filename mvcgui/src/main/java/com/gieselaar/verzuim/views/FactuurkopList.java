package com.gieselaar.verzuim.views;

import java.util.List;
import com.gieselaar.verzuim.controllers.AbstractController;
import com.gieselaar.verzuim.controllers.FactuurkopController;
import com.gieselaar.verzuim.controllers.FactuurkopController.__factuurkopfields;
import com.gieselaar.verzuim.viewsutils.ColorTableModel;
import com.gieselaar.verzuimbeheer.services.FactuurkopInfo;

public class FactuurkopList extends AbstractList {

	private static final long serialVersionUID = 9202374053287935929L;
	private FactuurkopController factuurkopcontroller;
	/**
	 * Create the frame.
	 */
	public FactuurkopList(AbstractController controller) {
		super("Factuurkoppen overzicht", controller);
		factuurkopcontroller = (FactuurkopController) controller;
		initialize();
		
		addColumn(__factuurkopfields.OMSCHRIJVING.getValue(),"Omschrijving",120);
		addColumn(__factuurkopfields.PRIORITEIT.getValue(),"Prioriteit",60);
	}

	public void initialize() {
		setBounds(50, 50, 807, 459);
	}
	@Override
	public void refreshTable() {
		List<FactuurkopInfo> factuurkoppen;
		factuurkoppen = factuurkopcontroller.getFactuurkoppenList();

		/* Save and remove sorts and filters before populating the table */
		this.getDatatable().disableSorter();
		
		factuurkopcontroller.getTableModel(factuurkoppen, (ColorTableModel) this.getTable().getModel(), colsinview);

		/* Add sorts and filters again */
		this.getDatatable().enableSorter();
	}
}
