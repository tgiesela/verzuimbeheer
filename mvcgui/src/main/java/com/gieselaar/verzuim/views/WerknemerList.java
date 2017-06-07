package com.gieselaar.verzuim.views;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import com.gieselaar.verzuim.controllers.WerknemerController;
import com.gieselaar.verzuim.controllers.WerknemerController.__werknemercommands;
import com.gieselaar.verzuim.controllers.WerknemerController.__werknemerfields;
import com.gieselaar.verzuim.viewsutils.ColorTableModel;
import com.gieselaar.verzuim.viewsutils.CursorController;
import com.gieselaar.verzuimbeheer.services.WerknemerFastInfo;

import javax.swing.JCheckBox;

public class WerknemerList extends AbstractList {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private WerknemerController werknemercontroller;
	/* Variables */
	/**
	 * Create the frame.
	 */
	public WerknemerList(WerknemerController controller) {
		super("Overzicht werknemers",controller);
		initialize();
		
		werknemercontroller = controller;
		addColumn(__werknemerfields.BSN.getValue(),"BSN", 60);
		addColumn(__werknemerfields.WERKGEVER.getValue(),"Werkgever", 100);
		addColumn(__werknemerfields.DATUMINDIENST.getValue(),"In dienst", 70, Date.class);
		addColumn(__werknemerfields.DATUMUITDIENST.getValue(),"Uit dienst", 70, Date.class);
		addColumn(__werknemerfields.ACHTERNAAM.getValue(),"Achternaam", 120);
		addColumn(__werknemerfields.VOORLETTERS.getValue(),"Voorl.", 40);
		addColumn(__werknemerfields.GEBOORTEDATUM.getValue(),"Geb. datum", 70, Date.class);
		addColumn(__werknemerfields.GESLACHT.getValue(),"Gesl.", 40);
		addColumn(__werknemerfields.PERSONEELSNUMMER.getValue(),"Pnr", 40);
		addColumn(__werknemerfields.UREN.getValue(),"Uren", 40, BigDecimal.class);
		addColumn(__werknemerfields.TELEFOONMOBIEL.getValue(),"Mobiel", 70);
		addColumn(__werknemerfields.TELEFOONPRIVE.getValue(),"Telefoonprive", 70);
		addColumn(__werknemerfields.AFDELINGNAAM.getValue(),"Afdelingnaam", 145);
	}
	private void initialize() {	
		JCheckBox cbUitdienstTonen = new JCheckBox("Uit dienst tonen");
		cbUitdienstTonen.setSelected(false);
		cbUitdienstTonen.setBounds(297, 422, 104, 23);
		cbUitdienstTonen.setActionCommand(__werknemercommands.WERKNEMERUITDIENSTTONEN.toString());
		cbUitdienstTonen.addActionListener(CursorController.createListener(this,controller));
		super.getDatatable().getPanelAction().add(cbUitdienstTonen);
		
		JCheckBox cbAlleenOpenverzuimen = new JCheckBox("Alleen open verzuimen");
		cbAlleenOpenverzuimen.setBounds(403, 422, 133, 23);
		cbAlleenOpenverzuimen.setActionCommand(__werknemercommands.WERKNEMERALLEENOPENVERZUIMEN.toString());
		cbAlleenOpenverzuimen.addActionListener(CursorController.createListener(this,controller));
		super.getDatatable().getPanelAction().add(cbAlleenOpenverzuimen);
	}
	@Override
	public void refreshTable() {
		List<WerknemerFastInfo> werknemers;
		werknemers = werknemercontroller.getWerknemerList();

		/* Save and remove sorts and filters before populating the table */
		this.getDatatable().disableSorter();
		
		WerknemerFastInfo.sort(werknemers,WerknemerFastInfo.__sortcol.WERKGEVERNAAM);
		werknemercontroller.getTableModel(werknemers, (ColorTableModel) this.getTable().getModel(), colsinview);

		/* Add sorts and filters again */
		this.getDatatable().enableSorter();
	}
}
