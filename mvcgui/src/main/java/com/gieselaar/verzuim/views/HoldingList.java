package com.gieselaar.verzuim.views;

import java.util.Date;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import com.gieselaar.verzuim.controllers.HoldingController;
import com.gieselaar.verzuim.controllers.HoldingController.__holdingcommands;
import com.gieselaar.verzuim.controllers.HoldingController.__holdingfields;
import com.gieselaar.verzuim.viewsutils.ColorTableModel;
import com.gieselaar.verzuim.viewsutils.CursorController;
import com.gieselaar.verzuimbeheer.services.HoldingInfo;


public class HoldingList extends AbstractList {
	private static final long serialVersionUID = 1L;
	private HoldingController holdingcontroller;
	private JButton btnWerkgevers;

	/**
	 * Create the frame.
	 */
	public HoldingList(HoldingController controller) {
		super("Overzicht holdings",controller);
		initialize();

		holdingcontroller = controller;
		addColumn(__holdingfields.NAAM.getValue(),"Naam",150);
		addColumn(__holdingfields.PLAATS.getValue(),"Plaats", 200);
		addColumn(__holdingfields.INGANGSDATUM.getValue(),"Start",60, Date.class);
		addColumn(__holdingfields.EINDDATUM.getValue(),"Einde",60, Date.class);

	}
	void initialize(){
		
		btnWerkgevers = new JButton("Werkgevers...");
		btnWerkgevers.setSelected(false);
		btnWerkgevers.setActionCommand(__holdingcommands.WERKGEVERSTONEN.toString());
		btnWerkgevers.addActionListener(CursorController.createListener(this, controller));
		super.getDatatable().getPanelAction().add(btnWerkgevers);

		JCheckBox cbAfgeslotentonen = new JCheckBox("Afgesloten tonen");
		cbAfgeslotentonen.setSelected(false);
		cbAfgeslotentonen.setBounds(297, 422, 104, 23);
		cbAfgeslotentonen.setActionCommand(__holdingcommands.HOLDINGAFGESLOTENTONEN.toString());
		cbAfgeslotentonen.addActionListener(CursorController.createListener(this,controller));
		super.getDatatable().getPanelAction().add(cbAfgeslotentonen);
	}
	@Override
	public void refreshTable() {
		List<HoldingInfo> holdings;
		holdings = holdingcontroller.getHoldingList();

		/* Save and remove sorts and filters before populating the table */
		this.getDatatable().disableSorter();
		
		HoldingInfo.sort(holdings,HoldingInfo.__sortcol.NAAM);
		holdingcontroller.getTableModel(holdings, (ColorTableModel) this.getTable().getModel(), colsinview);

		/* Add sorts and filters again */
		this.getDatatable().enableSorter();
		btnWerkgevers.setEnabled(false);
	}
	public void setRowSelected() {
		btnWerkgevers.setEnabled(true);
	}
}
