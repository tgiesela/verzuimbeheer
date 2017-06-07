package com.gieselaar.verzuim.views;

import java.util.Date;
import java.util.List;
import com.gieselaar.verzuim.controllers.AbstractController;
import com.gieselaar.verzuim.controllers.BtwController;
import com.gieselaar.verzuim.controllers.BtwController.__btwcommands;
import com.gieselaar.verzuim.controllers.BtwController.__btwfields;
import com.gieselaar.verzuim.viewsutils.ColorTableModel;
import com.gieselaar.verzuim.viewsutils.CursorController;
import com.gieselaar.verzuimbeheer.services.BtwInfo;
import javax.swing.JCheckBox;

public class BtwList extends AbstractList {

	private static final long serialVersionUID = 9202374053287935929L;
	private BtwController btwcontroller;
	
	/**
	 * Create the frame.
	 */
	public BtwList(AbstractController controller) {
		super("BTW overzicht", controller);
		btwcontroller = (BtwController) controller;
		initialize();
		
		addColumn(__btwfields.INGANGSDATUM.getValue(),"Ingangsdatum",80, Date.class);
		addColumn(__btwfields.EINDDATUM.getValue(),"Einddatum",60, Date.class);
		addColumn(__btwfields.PERCENTAGE.getValue(),"Percentage",60);
		addColumn(__btwfields.SOORT.getValue(),"Soort",60);
	}


	public void initialize() {
		JCheckBox cbOudePercentagesTonen = new JCheckBox("Oude percentages tonen");
		cbOudePercentagesTonen.setSelected(false);
		cbOudePercentagesTonen.setActionCommand(__btwcommands.BTWAFGESLOTENTONEN.toString());
		cbOudePercentagesTonen.addActionListener(CursorController.createListener(this,controller));
		super.getDatatable().getPanelAction().add(cbOudePercentagesTonen);
	}


	@Override
	public void refreshTable() {
		List<BtwInfo> tarieven;
		tarieven = btwcontroller.getBtwList();

		/* Save and remove sorts and filters before populating the table */
		this.getDatatable().disableSorter();

		btwcontroller.getTableModel(tarieven, (ColorTableModel) this.getTable().getModel(), colsinview);

		/* Add sorts and filters again */
		this.getDatatable().enableSorter();
	}
}
