package com.gieselaar.verzuim.views;

import java.util.List;
import com.gieselaar.verzuim.controllers.AbstractController;
import com.gieselaar.verzuim.controllers.FactuurcategorieController;
import com.gieselaar.verzuim.controllers.FactuurcategorieController.__factuurcategoriefields;
import com.gieselaar.verzuim.viewsutils.ColorTableModel;
import com.gieselaar.verzuimbeheer.services.FactuurcategorieInfo;

public class FactuurcategorieList extends AbstractList {
	private static final long serialVersionUID = 9202374053287935929L;
	private FactuurcategorieController factuurcategoriecontroller;
	
	/**
	 * Create the frame.
	 */
	public FactuurcategorieList(AbstractController controller) {
		super("factuurcategorie overzicht", controller);
		factuurcategoriecontroller = (FactuurcategorieController) controller;
		initialize();

		addColumn(__factuurcategoriefields.OMSCHRIJVING.getValue(),"Omschrijving",120);
		addColumn(__factuurcategoriefields.BTWTARIEFSOORT.getValue(),"Btwtariefsoort",60);
		addColumn(__factuurcategoriefields.FACTUURKOP.getValue(),"Factuurkop",120);
		addColumn(__factuurcategoriefields.OMZET.getValue(),"Omzet",60);
	}

	public void initialize() {
		setBounds(50, 50, 807, 459);
		
	}

	@Override
	public void refreshTable() {
		List<FactuurcategorieInfo> factuurcategorien;
		factuurcategorien = factuurcategoriecontroller.getFactuurcategorienList();

		/* Save and remove sorts and filters before populating the table */
		this.getDatatable().disableSorter();
		
		factuurcategoriecontroller.getTableModel(factuurcategorien, (ColorTableModel) this.getTable().getModel(), colsinview);

		/* Add sorts and filters again */
		this.getDatatable().enableSorter();
	}
}
