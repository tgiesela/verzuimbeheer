package com.gieselaar.verzuim.views;

import java.util.List;
import com.gieselaar.verzuim.controllers.ActiviteitController;
import com.gieselaar.verzuim.controllers.ActiviteitController.__activiteitfields;
import com.gieselaar.verzuim.viewsutils.ColorTableModel;
import com.gieselaar.verzuimbeheer.services.ActiviteitInfo;

public class ActiviteitList extends AbstractList{
	private static final long serialVersionUID = 2998408587286724321L;

	private ActiviteitController activiteitcontroller;
	public ActiviteitList(ActiviteitController controller){
		super("Overzicht activiteiten", controller);
		activiteitcontroller = controller;
		initialize();
		
		addColumn(__activiteitfields.NAAM.getValue(),"Naam",150);
		addColumn(__activiteitfields.CODE.getValue(),"Omschrijving",150);
	}
	public void initialize(){
		/* nothing special */		
	}
	@Override
	public void refreshTable() {
		List<ActiviteitInfo> pakketten;
		pakketten = activiteitcontroller.getActiviteitList();

		/* Save and remove sorts and filters before populating the table */
		this.getDatatable().disableSorter();
		
		activiteitcontroller.getTableModel(pakketten, (ColorTableModel) this.getTable().getModel(), colsinview);

		/* Add sorts and filters again */
		this.getDatatable().enableSorter();
	}

}
