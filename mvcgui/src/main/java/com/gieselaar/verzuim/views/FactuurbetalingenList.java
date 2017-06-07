package com.gieselaar.verzuim.views;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import com.gieselaar.verzuim.controllers.BetalingenController;
import com.gieselaar.verzuim.controllers.BetalingenController.__betalingenfields;
import com.gieselaar.verzuim.utils.ExceptionLogger;
import com.gieselaar.verzuim.viewsutils.ColorTableModel;
import com.gieselaar.verzuimbeheer.exceptions.VerzuimApplicationException;
import com.gieselaar.verzuimbeheer.services.FactuurbetalingInfo;
import com.gieselaar.verzuimbeheer.services.InfoBase;

import javax.swing.RowSorter;
import javax.swing.SortOrder;

public class FactuurbetalingenList extends AbstractList {
	private static final long serialVersionUID = 1L;
	private List<FactuurbetalingInfo> betalingen;
	private boolean initialized = false;
	private BetalingenController betalingencontroller;
	private List<RowSorter.SortKey> sortKeys;
	
	/**
	 * Create the frame.
	 */

	public FactuurbetalingenList(BetalingenController controller) {
		super("Factuurbetalingen", controller);
		betalingencontroller = controller;
		initialize();

		addColumn(__betalingenfields.DATUM.getValue(),"Datum", 60, Date.class);
		addColumn(__betalingenfields.REKENINGBETALER.getValue(),"Rekening  betaler", 100);
		addColumn(__betalingenfields.BEDRAG.getValue(),"Bedrag", 60, BigDecimal.class);

		sortKeys = new ArrayList<RowSorter.SortKey>();
		sortKeys.add(new RowSorter.SortKey(2, SortOrder.DESCENDING));
		this.getSorter().setSortKeys(sortKeys);
		this.getSorter().sort();
	}

	public void initialize() {
		setBounds(50, 50, 754, 454);
	}

	public void setInfo(InfoBase info) {
		initialized = true;
	}

	public void populateTable() {
		if (!initialized)
			return;
		try {
			betalingencontroller.selectBetalingen();
		} catch (VerzuimApplicationException e) {
			ExceptionLogger.ProcessException(e, this);
		}
	}
	@Override
	public void refreshTable() {
		betalingen = betalingencontroller.getBetalingenList();

		/* Save and remove sorts and filters before populating the table */
		this.getDatatable().disableSorter();
		
		betalingencontroller.getTableModel(betalingen, (ColorTableModel) this.getTable().getModel(), colsinview);

		/* Add sorts and filters again */
		this.getDatatable().enableSorter();
	}
}
