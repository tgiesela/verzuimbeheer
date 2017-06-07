package com.gieselaar.verzuim.views;

import java.util.Date;
import java.util.List;

import com.gieselaar.verzuim.controllers.WerkgeverController;
import com.gieselaar.verzuim.controllers.WerkgeverController.__werkgevercommands;
import com.gieselaar.verzuim.controllers.WerkgeverController.__werkgeverfields;
import com.gieselaar.verzuim.viewsutils.ColorTableModel;
import com.gieselaar.verzuim.viewsutils.CursorController;
import com.gieselaar.verzuimbeheer.services.WerkgeverInfo;
import javax.swing.JButton;
import javax.swing.JCheckBox;

public class WerkgeverList extends AbstractList {
	private static final long serialVersionUID = 1L;
	private WerkgeverController werkgevercontroller;
	/* Variables */
	private JButton btnWerknemers;
	/**
	 * Create the frame.
	 */
	public WerkgeverList(WerkgeverController controller) {
		super("Overzicht werkgevers",controller);
		initialize();
		
		werkgevercontroller = controller;
		addColumn(__werkgeverfields.NAAM.getValue(),"Naam", 150);
		addColumn(__werkgeverfields.HOLDINGNAAM.getValue(),"Holding", 150);
		addColumn(__werkgeverfields.POSTCODE.getValue(),"PC", 50);
		addColumn(__werkgeverfields.PLAATS.getValue(),"Plaats", 90);
		addColumn(__werkgeverfields.INGANGSDATUM.getValue(),"Start", 60, Date.class);
		addColumn(__werkgeverfields.EINDDATUM.getValue(),"Einde", 60, Date.class);

	}
	private void initialize() {
		btnWerknemers = new JButton("Werknemers...");
		btnWerknemers.setSelected(false);
		btnWerknemers.setActionCommand(__werkgevercommands.WERKNEMERTONEN.toString());
		btnWerknemers.addActionListener(CursorController.createListener(this, controller));
		super.getDatatable().getPanelAction().add(btnWerknemers);

		JCheckBox cbAfgeslotentonen = new JCheckBox("Afgesloten tonen");
		cbAfgeslotentonen.setSelected(false);
		cbAfgeslotentonen.setBounds(297, 422, 104, 23);
		cbAfgeslotentonen.setActionCommand(__werkgevercommands.WERKNEMERAFGESLOTENTONEN.toString());
		cbAfgeslotentonen.addActionListener(CursorController.createListener(this,controller));
		super.getDatatable().getPanelAction().add(cbAfgeslotentonen);
	}
	@Override
	public void refreshTable() {
		List<WerkgeverInfo> werkgevers;
		werkgevers = werkgevercontroller.getWerkgeverList();

		/* Save and remove sorts and filters before populating the table */
		this.getDatatable().disableSorter();
		
		WerkgeverInfo.sort(werkgevers,WerkgeverInfo.__sortcol.NAAM);
		werkgevercontroller.getTableModel(werkgevers, (ColorTableModel) this.getTable().getModel(), colsinview);

		/* Add sorts and filters again */
		this.getDatatable().enableSorter();
		btnWerknemers.setEnabled(false);
	}
	public void setRowSelected() {
		btnWerknemers.setEnabled(true);
	}
}
