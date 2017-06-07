package com.gieselaar.verzuim.views;

import java.util.List;
import com.gieselaar.verzuim.controllers.ApplicatiefunctieController;
import com.gieselaar.verzuim.controllers.ApplicatiefunctieController.__applicatiefunctiefields;
import com.gieselaar.verzuim.viewsutils.ColorTableModel;
import com.gieselaar.verzuimbeheer.services.ApplicatieFunctieInfo;

public class ApplicatieFunctieList extends AbstractList {

	private static final long serialVersionUID = 1L;
	private ApplicatiefunctieController applicatiefunctiecontroller;
	public ApplicatieFunctieList(ApplicatiefunctieController controller){
		super("Overzicht Applicatiefuncties", controller);
		applicatiefunctiecontroller = controller;
		initialize();
		addColumn(__applicatiefunctiefields.FUNCTIEID.getValue(),"Functie Id",50);
		addColumn(__applicatiefunctiefields.OMSCHRIJVING.getValue(),"Omschrijving",150);
	}
	public void initialize(){
		/* nothing special */		
	}
	@Override
	public void refreshTable() {
		List<ApplicatieFunctieInfo> applicatiefuncties;
		applicatiefuncties = applicatiefunctiecontroller.getApplicatiefunctiesList();

		/* Save and remove sorts and filters before populating the table */
		this.getDatatable().disableSorter();
		
		applicatiefunctiecontroller.getTableModel(applicatiefuncties, (ColorTableModel) this.getTable().getModel(), colsinview);

		/* Add sorts and filters again */
		this.getDatatable().enableSorter();
	}
	
}
