package com.gieselaar.verzuimbeheer.forms;

import java.awt.Component;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Vector;

import javax.swing.JOptionPane;

import com.gieselaar.verzuimbeheer.baseforms.BaseListForm;
import com.gieselaar.verzuimbeheer.baseforms.DefaultListFormNotification;
import com.gieselaar.verzuimbeheer.baseforms.JDesktopPaneTGI;
import com.gieselaar.verzuimbeheer.exceptions.VerzuimApplicationException;
import com.gieselaar.verzuimbeheer.exceptions.PermissionException;
import com.gieselaar.verzuimbeheer.exceptions.ValidationException;
import com.gieselaar.verzuimbeheer.services.FactuurTotaalInfo;
import com.gieselaar.verzuimbeheer.services.FactuurbetalingInfo;
import com.gieselaar.verzuimbeheer.services.InfoBase;
import com.gieselaar.verzuimbeheer.services.InfoBase.persistenceaction;
import com.gieselaar.verzuimbeheer.utils.ExceptionLogger;
import com.gieselaar.verzuimbeheer.utils.ServiceCaller;

import javax.swing.RowSorter;
import javax.swing.SortOrder;

public class FactuurbetalingenOverzicht extends BaseListForm {
	private static final long serialVersionUID = 1L;
	private List<FactuurbetalingInfo> betalingen;
	private Component thisform = this;
	private FactuurTotaalInfo factuur;
	
	private boolean initialized = false;
	
	/**
	 * Create the frame.
	 */

	public FactuurbetalingenOverzicht(JDesktopPaneTGI mdiPanel) {
		super("Factuurbetalingen", mdiPanel);
		getExportButton().setLocation(0, 146);
		initialize();
	}

	public void initialize() {
		setBounds(100, 100, 754, 454);
		setDetailFormClass(FactuurbetalingDetail.class, FactuurbetalingInfo.class);
		this.setFiltering(true);

		getPanelAction().setLocation(10, 229);
		getScrollPane().setBounds(39, 86, 687, 297);

		this.setEventNotifier(new DefaultListFormNotification() {

			@Override
			public void populateTableRequested() {
				populateTable();
			}

			@Override
			public void newCreated(InfoBase info) {
				FactuurbetalingInfo betaling = (FactuurbetalingInfo) info;
				if (factuur.isGesommeerd()){
					betaling.setFactuurid(factuur.getHoldingFactuurId());
				}else{
					betaling.setFactuurid(factuur.getId());
				}
				betaling.setDatum(new Date());
				betaling.setBedrag(BigDecimal.ZERO);
			}

			@Override
			public __continue newButtonClicked() {
				return __continue.allow;
			}

			@Override
			public __continue deleteButtonClicked(InfoBase info) {
				try {
					int rslt = JOptionPane.showConfirmDialog(thisform,
							"Weet u zeker dat u de regel wilt verwijderen?",
							"", JOptionPane.YES_NO_OPTION);
					if (rslt == JOptionPane.YES_OPTION)
						ServiceCaller.factuurFacade(getLoginSession())
								.deleteFactuurbetaling((FactuurbetalingInfo) info);
					else
						return __continue.dontallow;
				} catch (PermissionException e) {
					ExceptionLogger.ProcessException(e, thisform);
				} catch (ValidationException e) {
					ExceptionLogger.ProcessException(e, thisform, false);
				} catch (VerzuimApplicationException e) {
					ExceptionLogger.ProcessException(e, thisform);
				} catch (Exception e1) {
					ExceptionLogger.ProcessException(e1, thisform);
				}
				return __continue.allow;
			}
		});

		addColumn("Datum", null, 60, Date.class);
		addColumn("Rekening  betaler", null, 100);
		addColumn("Bedrag", null, 60, BigDecimal.class);
	}

	public void setInfo(InfoBase info) {
		factuur = (FactuurTotaalInfo)info;
		initialized = true;
	}

	private void displayFactuurbetalingen() {
		if (!initialized)
			return;
		List<RowSorter.SortKey> sortKeys;
		
		deleteAllRows();
		if (betalingen != null) {
			for (FactuurbetalingInfo v : betalingen) {
				if (v.getAction() == persistenceaction.DELETE){
					;
				} else {
					Vector<Object> rowData = new Vector<Object>();
					rowData.add(v.getDatum());
					rowData.add(v.getRekeningnummerbetaler());
					rowData.add(v.getBedrag());
					addRow(rowData, v);
				}
			}
			sortKeys = new ArrayList<RowSorter.SortKey>();
			sortKeys.add(new RowSorter.SortKey(2, SortOrder.DESCENDING));
			this.getSorter().setSortKeys(sortKeys);
			this.getSorter().sort();
		}
		// this.getTable().setRowSelectionInterval(0, 0);
	}

	public void populateTable() {
		if (!initialized)
			return;
		try {
			if (factuur.isGesommeerd()){
				betalingen = ServiceCaller
						.factuurFacade(getLoginSession())
						.getFactuurbetalingenForFactuur(factuur.getHoldingFactuurId());
			}else{
				betalingen = ServiceCaller
						.factuurFacade(getLoginSession())
						.getFactuurbetalingenForFactuur(factuur.getId());
			}
		} catch (PermissionException e2) {
			ExceptionLogger.ProcessException(e2, this,
					"Kan betalingen niet opvragen");
			return;
		} catch (VerzuimApplicationException e2) {
			ExceptionLogger.ProcessException(e2, this);
			return;
		} catch (Exception e1) {
			ExceptionLogger.ProcessException(e1, this);
			return;
		}
		displayFactuurbetalingen();
	}
}
