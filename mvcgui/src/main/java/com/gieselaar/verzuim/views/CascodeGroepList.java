package com.gieselaar.verzuim.views;

import java.util.List;
import com.gieselaar.verzuim.controllers.AbstractController;
import com.gieselaar.verzuim.controllers.CascodegroepController;
import com.gieselaar.verzuim.controllers.CascodegroepController.__cascodegroepcommands;
import com.gieselaar.verzuim.controllers.CascodegroepController.__cascodegroepfields;
import com.gieselaar.verzuim.viewsutils.ColorTableModel;
import com.gieselaar.verzuim.viewsutils.CursorController;
import com.gieselaar.verzuimbeheer.services.CascodeGroepInfo;
import javax.swing.JButton;

public class CascodeGroepList extends AbstractList {

	private static final long serialVersionUID = 1L;
	private JButton btnCascodes;
	private CascodegroepController cascodecontroller;
	
	public CascodeGroepList(AbstractController controller) {
		super("Overzicht cascodegroepen", controller);
		cascodecontroller = (CascodegroepController) controller;
		initialize();

		addColumn(__cascodegroepfields.NAAM.getValue(),"Naam",150);
		addColumn(__cascodegroepfields.OMSCHRIJVING.getValue(),"Omschrijving",150);
		
	}
	public void initialize(){
		btnCascodes = new JButton("Cascodes...");
		btnCascodes.setSelected(false);
		btnCascodes.setActionCommand(__cascodegroepcommands.CASCODESTONEN.toString());
		btnCascodes.addActionListener(CursorController.createListener(this, controller));
		super.getDatatable().getPanelAction().add(btnCascodes);

		enableButtons(false);
	}
	public void setRowSelected() {
		btnCascodes.setEnabled(true);
	}
	private void enableButtons(boolean rowSelected){
		if (rowSelected){
			btnCascodes.setEnabled(true);
		}
		else {
			btnCascodes.setEnabled(false);
		}
	}
	
	@Override
	public void refreshTable() {
		List<CascodeGroepInfo> cacodegroepen;
		cacodegroepen = cascodecontroller.getCascodegroepenList();

		/* Save and remove sorts and filters before populating the table */
		this.getDatatable().disableSorter();
		
		cascodecontroller.getTableModel(cacodegroepen, (ColorTableModel) this.getTable().getModel(), colsinview);

		/* Add sorts and filters again */
		this.getDatatable().enableSorter();
		enableButtons(false);
	}
}
