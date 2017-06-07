package com.gieselaar.verzuim.views;

import java.util.List;
import com.gieselaar.verzuim.controllers.AbstractController;
import com.gieselaar.verzuim.controllers.DocumenttemplateController;
import com.gieselaar.verzuim.controllers.DocumenttemplateController.__documenttemplatefields;
import com.gieselaar.verzuim.viewsutils.ColorTableModel;
import com.gieselaar.verzuimbeheer.services.DocumentTemplateInfo;

public class DocumentTemplateList extends AbstractList {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private DocumenttemplateController templatecontroller;
	public DocumentTemplateList (AbstractController controller){
		super("Overzicht document templates", controller);
		templatecontroller = (DocumenttemplateController) controller;
		initialize();
		
		addColumn(__documenttemplatefields.NAAM.getValue(),"Naam",200);
		addColumn(__documenttemplatefields.PADNAAM.getValue(),"Padnaam",350);
	}
	public void initialize(){
		/* nothing special */
	}
	@Override
	public void refreshTable() {
		List<DocumentTemplateInfo> pakketten;
		pakketten = templatecontroller.getDocumentTemplateList();

		/* Save and remove sorts and filters before populating the table */
		this.getDatatable().disableSorter();
		
		templatecontroller.getTableModel(pakketten, (ColorTableModel) this.getTable().getModel(), colsinview);

		/* Add sorts and filters again */
		this.getDatatable().enableSorter();
	}

}
