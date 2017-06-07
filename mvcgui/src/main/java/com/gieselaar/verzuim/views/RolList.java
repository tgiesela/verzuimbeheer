package com.gieselaar.verzuim.views;

import java.util.List;
import com.gieselaar.verzuim.controllers.RolController;
import com.gieselaar.verzuim.controllers.RolController.__rolfields;
import com.gieselaar.verzuim.viewsutils.ColorTableModel;
import com.gieselaar.verzuimbeheer.services.RolInfo;

public class RolList extends AbstractList {
	private static final long serialVersionUID = 2998408587286724321L;
	private RolController rolcontroller;
	public RolList(RolController controller){
		super("Overzicht rollen", controller);
		rolcontroller = controller;
		initialize();
		
		addColumn(__rolfields.ROL.getValue(),"Rol",50);
		addColumn(__rolfields.OMSCHRIJVING.getValue(),"Omschrijving",150);
		
	}
	public void initialize(){
		/* nothing special */		
	}
	@Override
	public void refreshTable() {
		List<RolInfo> rollen;
		rollen = rolcontroller.getRollenList();

		/* Save and remove sorts and filters before populating the table */
		this.getDatatable().disableSorter();
		
		rolcontroller.getTableModel(rollen, (ColorTableModel) this.getTable().getModel(), colsinview);

		/* Add sorts and filters again */
		this.getDatatable().enableSorter();
	}


}
