package com.gieselaar.verzuim.controllers;

import java.awt.event.ActionEvent;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.swing.JOptionPane;

import com.gieselaar.verzuim.interfaces.ControllerEventListener;
import com.gieselaar.verzuim.models.BetalingenModel;
import com.gieselaar.verzuim.models.FactuurModel;
import com.gieselaar.verzuim.utils.ExceptionLogger;
import com.gieselaar.verzuim.views.AbstractDetail;
import com.gieselaar.verzuim.views.FactuurbetalingDetail;
import com.gieselaar.verzuim.viewsutils.ColorTableModel;
import com.gieselaar.verzuimbeheer.exceptions.ValidationException;
import com.gieselaar.verzuimbeheer.exceptions.VerzuimApplicationException;
import com.gieselaar.verzuimbeheer.facades.LoginSessionRemote;
import com.gieselaar.verzuimbeheer.services.HoldingInfo;
import com.gieselaar.verzuimbeheer.services.ImportBetalingInfo;
import com.gieselaar.verzuimbeheer.services.ImportBetalingInfo.__checkresult;
import com.gieselaar.verzuimbeheer.services.InfoBase;
import com.gieselaar.verzuimbeheer.services.WerkgeverInfo;
import com.gieselaar.verzuimbeheer.utils.DateOnly;
import com.gieselaar.verzuimbeheer.services.InfoBase.persistenceaction;
import com.gieselaar.verzuimbeheer.services.InfoBase.persistencestate;
import com.gieselaar.verzuimbeheer.services.FactuurTotaalInfo;
import com.gieselaar.verzuimbeheer.services.FactuurbetalingInfo;

public class ImportbetalingenController extends AbstractController {

	private static final long serialVersionUID = 1L;

	public enum __importedbetalingenfields {
		UNKNOWN(-1), DATUM(0), REKENINGBETALER(1), BEDRAG(2)
	  , BEDRIJFSNAAM(4), DVVWERKGEVER(5), DVVFACTUURNUMMER(6)
	  , DVVFACTUURBEDRAG(7), DVVMELDING(8);
		private int value;

		__importedbetalingenfields(int value) {
			this.value = value;
		}

		public int getValue() {
			return value;
		}

		public static __importedbetalingenfields parse(int type) {
			__importedbetalingenfields field = UNKNOWN; // Default
			for (__importedbetalingenfields item : __importedbetalingenfields.values()) {
				if (item.getValue() == type) {
					field = item;
					break;
				}
			}
			return field;
		}
	}
	public enum __importbetalingencommands {
		UNKNOWN(-1), ALLEENPROBLEEMGEVALLENTONEN(3), UPDATEIMPORTEDBETALING(4);
		private int value;

		__importbetalingencommands(int value) {
			this.value = value;
		}

		public int getValue() {
			return value;
		}

		public static __importbetalingencommands parse(int type) {
			__importbetalingencommands field = null; // Default
			for (__importbetalingencommands item : __importbetalingencommands.values()) {
				if (item.getValue() == type) {
					field = item;
					break;
				}
			}
			return field;
		}

		public static __importbetalingencommands parse(String type) {
			__importbetalingencommands field = __importbetalingencommands.UNKNOWN; // Default
			for (__importbetalingencommands item : __importbetalingencommands.values()) {
				if (item.toString().equals(type)) {
					field = item;
					break;
				}
			}
			return field;
		}
	}
	
	private BetalingenModel model;
	private FactuurModel factuurmodel;
	
	private FactuurTotaalInfo selectedFactuur;
	private List<WerkgeverInfo> werkgevers;
	private List<HoldingInfo> holdings;
	private boolean alleenprobleemgevallen = false;
	
	public ImportbetalingenController(LoginSessionRemote session) {
		super(new BetalingenModel(session), null);
		this.model = (BetalingenModel) getModel();
		factuurmodel = new FactuurModel(session);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		switch (__importbetalingencommands.parse(e.getActionCommand())) {
		case ALLEENPROBLEEMGEVALLENTONEN:
			alleenprobleemgevallen = !alleenprobleemgevallen;
			for (ControllerEventListener l : views) {
				l.refreshTable();
			}
			break;
		case UPDATEIMPORTEDBETALING:
			AbstractDetail view = getContainingForm(e.getSource());
			if (view == null){
				return;
			}
			InfoBase data = view.collectData();
			if (data == null){ 
				/* no data from view */
				return;
			}
			try {
				saveData(data);
			} catch (VerzuimApplicationException e1) {
				ExceptionLogger.ProcessException(e1, view);
				return;
			}
			break;
		default:
			super.actionPerformed(e);
			break;
		}
	}
	public void selectBetalingen() throws VerzuimApplicationException {
		if (selectedFactuur.isGesommeerd()){
			model.selectBetalingen(selectedFactuur.getHoldingFactuurId());
		}else{
			model.selectBetalingen(selectedFactuur.getId());
		}
	}
	public void selectBetalingenAll() throws VerzuimApplicationException {
		model.selectBetalingenAll();
	}
	private FactuurbetalingInfo createNewBetaling() {
		FactuurbetalingInfo info = new FactuurbetalingInfo();
		if (selectedFactuur.isGesommeerd()){
			info.setFactuurid(selectedFactuur.getHoldingFactuurId());
		}else{
			info.setFactuurid(selectedFactuur.getId());
		}
		
		return info;
	}
	@Override
	public void addData(InfoBase data) throws VerzuimApplicationException {
		FactuurbetalingInfo info = (FactuurbetalingInfo)data;
		try {
			info.validate();
			model.addFactuurbetaling(info);
		} catch (ValidationException e) {
			throw new VerzuimApplicationException(e, "Opslaan betaling niet geslaagd.");
		}
	}
	@Override
	public void saveData(InfoBase data) throws VerzuimApplicationException {
		ImportBetalingInfo info = (ImportBetalingInfo)data;
		try {
			info.validate();
			model.saveImportbetaling(info);
		} catch (ValidationException e) {
			throw new VerzuimApplicationException(e, "Opslaan betaling niet geslaagd.");
		}
	}

	public void showRow(ControllerEventListener ves, Object data) {
		if (!isShowAllowed((InfoBase)data))
			return;
		FactuurbetalingInfo info = (FactuurbetalingInfo) data;
		AbstractDetail form = super.createDetailForm(info, FactuurbetalingDetail.class, __formmode.UPDATE);
		super.showRow(ves, form, data);
	}

	public void addRow(ControllerEventListener ves) {
		if (!isNewAllowed())
			return;
		FactuurbetalingInfo info = createNewBetaling();
		AbstractDetail form = super.createDetailForm(info, FactuurbetalingDetail.class, __formmode.NEW);
		super.addRow(ves, form);
	}
	@Override
	public void deleteRow(Object data) throws VerzuimApplicationException {
		if (!isDeleteAllowed((InfoBase)data)){
			return;
		}
		ImportBetalingInfo info = (ImportBetalingInfo) data;
		info.setAction(persistenceaction.DELETE);
		info.setState(persistencestate.EXISTS);
		model.deleteImportBetaling(info);
	}

	private String checkBetaling(ImportBetalingInfo ibi) {
		FactuurTotaalInfo factuur = ibi.getFactuur();
		ibi.setCheckresult(__checkresult.OK);
		if (factuur != null) {
			if (factuur.isGesommeerd()) {
				if (ibi.getHoldingid() != null) {
					if (!ibi.getHoldingid().equals(ibi.getFactuur().getHoldingid())) {
						ibi.setCheckresult(__checkresult.HOLDINGMISMATCH);
					}
				}
			} else {
				if (ibi.getWerkgeverid() != null) {
					if (!ibi.getWerkgeverid().equals(ibi.getFactuur().getWerkgeverid())) {
						ibi.setCheckresult(__checkresult.WERKGEVERMISMATCH);
					}
				} else {
					if (!ibi.getHoldingid().equals(ibi.getFactuur().getHoldingid())) {
						ibi.setCheckresult(__checkresult.HOLDINGMISMATCH);
					}
				}
			}
		} else {
			ibi.setCheckresult(__checkresult.NOMATCHINGFACTUUR);
		}
		if (ibi.getCheckresult().compareTo(__checkresult.OK) == 0) {
			if (ibi.getFactuurbedrag().setScale(2, RoundingMode.HALF_UP)
					.compareTo(ibi.getBedrag().setScale(2, RoundingMode.HALF_UP)) != 0) {
				ibi.setCheckresult(__checkresult.AMOUNTMISMATCH);
			}
			if (factuur != null) {
				if (ibi.getFactuur().getSombetalingen()
						.equals(ibi.getFactuurbedrag().setScale(2, RoundingMode.HALF_UP))) {
					ibi.setCheckresult(__checkresult.ALREADYPAID);
					List<FactuurbetalingInfo> betalingen;
					betalingen = getFactuurbetalingenForFactuur(factuur.getId());
					for (FactuurbetalingInfo fbi : betalingen) {
						if (DateOnly.equals(fbi.getDatum(), ibi.getDatumbetaling())) {
							;
						} else {
							ibi.setCheckresult(__checkresult.DOUBLEPAYMENT);
						}
					}
				}
			}
		}
		return ibi.getCheckresult().toString();
	}
	public void getTableModel(List<ImportBetalingInfo> importedbetalingen, ColorTableModel tblmodel,
			List<Integer> colsinview) {
		tblmodel.setRowCount(0);
		tblmodel.setColumnCount(colsinview.size());
		for (ImportBetalingInfo ibi : importedbetalingen) {
			if (alleenprobleemgevallen && (ibi.getCheckresult() == __checkresult.OK
					|| ibi.getCheckresult() == __checkresult.ALREADYPAID)) {
				;
			} else {
				List<Object> colsinmodel = new ArrayList<>();
				setColumnValues(colsinmodel, ibi, colsinview);
				tblmodel.addRow(colsinmodel, ibi);
			}
		}
	}

	private void setColumnValues(List<Object> colsinmodel, ImportBetalingInfo ibi,
			List<Integer> colsinview) {
		FactuurTotaalInfo factuur = ibi.getFactuur();
		if (factuur != null){
			ibi.setFactuurnummer(factuur.getFactuurnr());
			ibi.setFactuurbedrag(factuur.getTotaalInclBtw());
		}
		for (int i = 0; i < colsinview.size(); i++) {
			__importedbetalingenfields val = __importedbetalingenfields.parse(colsinview.get(i));
			switch (val) {
			case DATUM:
				colsinmodel.add(i, ibi.getDatumbetaling());
				break;
			case BEDRAG:
				colsinmodel.add(i, ibi.getBedrag());
				break;
			case REKENINGBETALER:
				colsinmodel.add(i,ibi.getRekeningnummerBetaler());
				break;
			case BEDRIJFSNAAM:
				colsinmodel.add(i,ibi.getBedrijfsnaam());
				break;
			case DVVWERKGEVER:
				if (factuur != null)
					if (factuur.getHoldingid() != null) {
						colsinmodel.add(i, getHolding(factuur.getHoldingid()).getNaam());
					} else {
						colsinmodel.add(i, getWerkgever(factuur.getWerkgeverid()).getNaam());
				}else{
					if (ibi.getWerkgeverid() != null){
						colsinmodel.add(i, getWerkgever(ibi.getWerkgeverid()).getNaam());
					}else{
						if (ibi.getHoldingid() != null){
							colsinmodel.add(i, getHolding(ibi.getHoldingid()).getNaam());
						}else{
							colsinmodel.add(i, "");
						}
					}
				}
				break;
			case DVVFACTUURBEDRAG:
				if (factuur != null){
					colsinmodel.add(i, ibi.getFactuurbedrag());
				}else{
					colsinmodel.add(i, null);
				}
				break;
			case DVVFACTUURNUMMER:
				if (factuur != null){
					colsinmodel.add(i, ibi.getFactuurnummer().toString());
				}else{
					colsinmodel.add(i, "");
				}
				break;
			case DVVMELDING:
				colsinmodel.add(i, checkBetaling(ibi));
				break;
			default:
				break;
			}
		}
	}

	private HoldingInfo getHolding(Integer holdingid) {
		for (HoldingInfo holding: holdings){
			if (holding.getId().equals(holdingid))
				return holding;
		}
		return null;
	}

	private WerkgeverInfo getWerkgever(Integer werkgeverid) {
		for (WerkgeverInfo werkgever: werkgevers){
			if (werkgever.getId().equals(werkgeverid))
				return werkgever;
		}
		return null;
	}

	private boolean confirmDelete(ImportBetalingInfo wnr) {
		int rslt;
		rslt = JOptionPane
				.showConfirmDialog(null,"Weet u zeker dat u deze betaling wilt verwijderen?",
						"", JOptionPane.YES_NO_OPTION);
		if (rslt == JOptionPane.YES_OPTION) {
			return true;
		} else {
			return false;
		}
	}

	@Override
	protected boolean isDeleteAllowed(InfoBase data) {
		ImportBetalingInfo info = (ImportBetalingInfo)data;
		return confirmDelete(info);
	}

	@Override
	protected boolean isShowAllowed(InfoBase data) {
		return true;
	}

	@Override
	protected boolean isNewAllowed() {
		return true;
	}

	public List<FactuurbetalingInfo> getBetalingenList() {
		return model.getBetalingenList(); 
	}

	public void setFactuur(FactuurTotaalInfo selectedFactuur) {
		this.selectedFactuur = selectedFactuur;
		
	}

	public List<ImportBetalingInfo> getImportedBetalingen() {
		return model.getImportedbetalingenList();
	}


	public List<FactuurTotaalInfo> getFacturenInPeriode(Date firstmonth, Date lastmonth) {
		try {
			return factuurmodel.getFacturenInPeriode(firstmonth, lastmonth, false);
		} catch (VerzuimApplicationException e) {
			ExceptionLogger.ProcessException(e, this.getActiveForm());
			return null;
		}
		
	}

	public List<FactuurbetalingInfo> getFactuurbetalingenForFactuur(Integer factuurid) {
		try {
			return factuurmodel.getFactuurbetalingenForFactuur(factuurid);
		} catch (VerzuimApplicationException e) {
			ExceptionLogger.ProcessException(e, this.getActiveForm());
		}
		return null;
	}

	public void addFactuurbetalingen(List<ImportBetalingInfo> importedbetalingen) {
		for (ImportBetalingInfo ibi : importedbetalingen) {
			if (ibi.getCheckresult() != __checkresult.OK && ibi.getCheckresult() != __checkresult.ALREADYPAID) {
				if (ibi.isAccepted()) {
					;
				} else {
					JOptionPane.showMessageDialog(null,
							"Er zijn betalingen met problemen.\n\r" + "Los die eerst op of verwijder ze.");
					return;
				}
			}
		}
		for (ImportBetalingInfo ibi : importedbetalingen) {
			FactuurbetalingInfo fbi = new FactuurbetalingInfo();
			fbi.setBedrag(ibi.getBedrag());
			fbi.setDatum(ibi.getDatumbetaling());
			if (ibi.getFactuur().isGesommeerd()) {
				fbi.setFactuurid(ibi.getFactuur().getHoldingFactuurId());
			} else {
				fbi.setFactuurid(ibi.getFactuur().getId());
			}
			fbi.setRekeningnummerbetaler(ibi.getRekeningnummerBetaler());
			if ((ibi.getCheckresult() == __checkresult.OK)
					|| (ibi.getCheckresult() == __checkresult.AMOUNTMISMATCH && ibi.isAccepted())) {
				try {
					model.addFactuurbetaling(fbi);
				} catch (VerzuimApplicationException e1) {
					ExceptionLogger.ProcessException(e1, null);
					return;
				}
			}
		}
	}

	public void importeerbetalingen(String docnaam, String separator, boolean raboformat, int periodlengthinmothns) throws VerzuimApplicationException {
		werkgevers = getMaincontroller().getWerkgevers();
		holdings = getMaincontroller().getHoldings();
		model.importeerbetalingen(docnaam, separator, raboformat, periodlengthinmothns, this.getMaincontroller());
	}

	public FactuurTotaalInfo findFactuurByFactuurnr(Integer factuurnummer, Integer werkgeverid, Integer holdingid,
			BigDecimal bedrag) {
		return model.findFactuurByFactuurnr(factuurnummer, werkgeverid, holdingid, bedrag);
	}
	
}
