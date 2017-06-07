package com.gieselaar.verzuim.controllers;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.swing.JOptionPane;

import com.gieselaar.verzuim.interfaces.ControllerEventListener;
import com.gieselaar.verzuim.models.TariefModel;
import com.gieselaar.verzuim.views.AbstractDetail;
import com.gieselaar.verzuim.views.TariefDetail;
import com.gieselaar.verzuim.viewsutils.ColorTableModel;
import com.gieselaar.verzuimbeheer.exceptions.ValidationException;
import com.gieselaar.verzuimbeheer.exceptions.VerzuimApplicationException;
import com.gieselaar.verzuimbeheer.facades.LoginSessionRemote;
import com.gieselaar.verzuimbeheer.services.HoldingInfo;
import com.gieselaar.verzuimbeheer.services.InfoBase;
import com.gieselaar.verzuimbeheer.services.WerkgeverInfo;
import com.gieselaar.verzuimbeheer.services.InfoBase.persistenceaction;
import com.gieselaar.verzuimbeheer.services.InfoBase.persistencestate;
import com.gieselaar.verzuimbeheer.services.TariefInfo;
import com.gieselaar.verzuimbeheer.services.TariefInfo.__tariefperiode;

public class TariefController extends AbstractController {

	private static final long serialVersionUID = 1L;

	public enum __tarieffields {
		UNKNOWN(-1), WERKGEVER(0), INGANGSDATUM(1), EINDDATUM(2), 
		ABONNEMENT(3), PERIODEABONNEMENT(4), 
		AANSLUITKOSTEN(5), PERIODEAANSLUITKOSTEN(6);
		private int value;

		__tarieffields(int value) {
			this.value = value;
		}

		public int getValue() {
			return value;
		}

		public static __tarieffields parse(int type) {
			__tarieffields field = UNKNOWN; // Default
			for (__tarieffields item : __tarieffields.values()) {
				if (item.getValue() == type) {
					field = item;
					break;
				}
			}
			return field;
		}
	}
	public enum __tariefcommands {
		UNKNOWN(-1), TARIEFAFGESLOTENTONEN(2);
		private int value;

		__tariefcommands(int value) {
			this.value = value;
		}

		public int getValue() {
			return value;
		}

		public static __tariefcommands parse(int type) {
			__tariefcommands field = null; // Default
			for (__tariefcommands item : __tariefcommands.values()) {
				if (item.getValue() == type) {
					field = item;
					break;
				}
			}
			return field;
		}

		public static __tariefcommands parse(String type) {
			__tariefcommands field = __tariefcommands.UNKNOWN; // Default
			for (__tariefcommands item : __tariefcommands.values()) {
				if (item.toString().equals(type)) {
					field = item;
					break;
				}
			}
			return field;
		}
	}
	
	private TariefModel model;
	
	private HoldingInfo holding;
	private WerkgeverInfo werkgever;
	private boolean afgeslotenTonen;
	
	public TariefController(LoginSessionRemote session) {
		super(new TariefModel(session), null);
		this.model = (TariefModel) getModel();
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		switch (__tariefcommands.parse(e.getActionCommand())) {
		case TARIEFAFGESLOTENTONEN:
			afgeslotenTonen = !afgeslotenTonen;
			for (ControllerEventListener l : views) {
				l.refreshTable();
			}
			break;
		default:
			super.actionPerformed(e);
			break;
		}
	}
	public void selectTarievenWerkgever(Integer werkgeverid) throws VerzuimApplicationException {
		this.model.selectTarievenWerkgever(werkgeverid);
	}
	public void selectTarievenHolding(Integer holdingid) throws VerzuimApplicationException {
		this.model.selectTarievenHolding(holdingid);
	}
	public void selectTarieven() throws VerzuimApplicationException {
		this.model.selectTarieven();
	}
	public TariefInfo createTarief(Integer holdingid, Integer werkgeverid) {
		TariefInfo tarief = createNewTarief();
		tarief.setHoldingId(holdingid);
		tarief.setWerkgeverId(werkgeverid);
		return tarief;
	}

	private TariefInfo createNewTarief() {
		TariefInfo info = new TariefInfo();
		info.setWerkgeverId(null);
		info.setHoldingId(null);
		info.setIngangsdatum(new Date());
		info.setEinddatum(null);
		info.setAansluitkosten(new BigDecimal(0));
		info.setAansluitkostenPeriode(__tariefperiode.JAAR);
		info.setAbonnement(new BigDecimal(0));
		info.setAbonnementPeriode(__tariefperiode.MAAND);
		info.setDatumAansluitkosten(new Date());
		info.setHuisbezoekTarief(new BigDecimal(0));
		info.setHuisbezoekZaterdagTarief(new BigDecimal(0));
		info.setKmTarief(new BigDecimal(0));
		info.setMaandbedragSecretariaat(new BigDecimal(0));
		info.setOmschrijvingFactuur("");
		info.setSecretariaatskosten(new BigDecimal(0));
		info.setSociaalbezoekTarief(new BigDecimal(0));
		info.setSpoedbezoekTarief(new BigDecimal(0));
		info.setSpoedbezoekZelfdedagTarief(new BigDecimal(0));
		info.setStandaardHuisbezoekTarief(new BigDecimal(0));
		info.setTelefonischeControleTarief(new BigDecimal(0));
		info.setUurtariefNormaal(new BigDecimal(0));
		info.setUurtariefWeekend(new BigDecimal(0));
		info.setVasttariefhuisbezoeken(false);
		if (werkgever != null){
			info.setWerkgeverId(werkgever.getId());
			initializetarief(info);
		} else {
			if (holding != null){
				info.setHoldingId(holding.getId());
				initializetarief(info);
			}
		}
		return info;
	}
	private void initializetarief(TariefInfo tarief) {
		List<TariefInfo> tarieven = getTarievenList();
		if (tarieven != null){
			for (TariefInfo t:tarieven){
				if (t.getEinddatum() == null){
					tarief.setAansluitkosten(t.getAansluitkosten());
					tarief.setAansluitkostenPeriode(t.getAansluitkostenPeriode());
					tarief.setAbonnement(t.getAbonnement());
					tarief.setAbonnementPeriode(t.getAbonnementPeriode());
					tarief.setDatumAansluitkosten(t.getDatumAansluitkosten());
					tarief.setHuisbezoekTarief(t.getHuisbezoekTarief());
					tarief.setHuisbezoekZaterdagTarief(t.getHuisbezoekZaterdagTarief());
					tarief.setKmTarief(t.getKmTarief());
					tarief.setMaandbedragSecretariaat(t.getMaandbedragSecretariaat());
					tarief.setOmschrijvingFactuur(t.getOmschrijvingFactuur());
					tarief.setSecretariaatskosten(t.getSecretariaatskosten());
					tarief.setSociaalbezoekTarief(t.getSociaalbezoekTarief());
					tarief.setSpoedbezoekTarief(t.getSpoedbezoekTarief());
					tarief.setSpoedbezoekZelfdedagTarief(t.getSpoedbezoekZelfdedagTarief());
					tarief.setStandaardHuisbezoekTarief(t.getStandaardHuisbezoekTarief());
					tarief.setTelefonischeControleTarief(t.getTelefonischeControleTarief());
					tarief.setUurtariefNormaal(t.getUurtariefNormaal());
					tarief.setUurtariefWeekend(t.getUurtariefWeekend());
					tarief.setVasttariefhuisbezoeken(t.getVasttariefhuisbezoeken());
					break;
				}
			}
		}
	}

	@Override
	public void addData(InfoBase data) throws VerzuimApplicationException {
		TariefInfo info = (TariefInfo)data;
		try {
			info.validate();
			model.addTarief(info);
		} catch (ValidationException e) {
			throw new VerzuimApplicationException(e, "Opslaan tarief niet geslaagd.");
		}
	}
	@Override
	public void saveData(InfoBase data) throws VerzuimApplicationException {
		TariefInfo info = (TariefInfo)data;
		try {
			info.validate();
			model.saveTarief(info);
		} catch (ValidationException e) {
			throw new VerzuimApplicationException(e, "Opslaan tarief niet geslaagd.");
		}
	}

	private void selectTarievenInModel() throws VerzuimApplicationException {
		if (werkgever != null){
			selectTarievenWerkgever(werkgever.getId());
		}else{
			if (holding != null){
				selectTarievenHolding(holding.getId());
			}else{
				selectTarieven();
			}
		}
	}

	public void showRow(ControllerEventListener ves, Object data) {
		if (!isShowAllowed((InfoBase)data))
			return;
		TariefInfo info = (TariefInfo) data;
		AbstractDetail form = super.createDetailForm(info, TariefDetail.class, __formmode.UPDATE);
		super.showRow(ves, form, data);
	}

	public void addRow(ControllerEventListener ves) {
		if (!isNewAllowed())
			return;
		TariefInfo info = createNewTarief();
		AbstractDetail form = super.createDetailForm(info, TariefDetail.class, __formmode.NEW);
		super.addRow(ves, form);
	}
	@Override
	public void deleteRow(Object data) throws VerzuimApplicationException {
		if (!isDeleteAllowed((InfoBase)data)){
			return;
		}
		TariefInfo info = (TariefInfo) data;
		info.setAction(persistenceaction.DELETE);
		info.setState(persistencestate.EXISTS);
		model.deleteTarief(info);
		selectTarievenInModel();
	}

	public void getTableModel(List<TariefInfo> tarieven, ColorTableModel tblmodel, List<Integer> colsinview) {
		tblmodel.setRowCount(0);
		tblmodel.setColumnCount(colsinview.size());
		for (TariefInfo info : tarieven) {
			List<Object> colsinmodel = new ArrayList<>();

			setColumnValues(colsinmodel, info, colsinview);
			if (info.getEinddatum() != null){
				if (afgeslotenTonen){
					tblmodel.addRow(colsinmodel, info, Color.GRAY);
				}
			}else{
				tblmodel.addRow(colsinmodel, info);
			}
		}
	}

	private void setColumnValues(List<Object> colsinmodel, TariefInfo ti, List<Integer> colsinview) {
		for (int i = 0; i < colsinview.size(); i++) {
			__tarieffields val = __tarieffields.parse(colsinview.get(i));
			switch (val) {
			case WERKGEVER:
				colsinmodel.add(i, ti.getWerkgevernaam());
				break;
			case INGANGSDATUM:
				colsinmodel.add(i, ti.getIngangsdatum());
				break;
			case EINDDATUM:
				colsinmodel.add(i,ti.getEinddatum());
				break;
			case ABONNEMENT:
				colsinmodel.add(i,ti.getAbonnement());
				break;
			case PERIODEABONNEMENT:
				colsinmodel.add(i,ti.getAbonnementPeriode().toString());
				break;
			case AANSLUITKOSTEN:
				colsinmodel.add(i,ti.getAansluitkosten());
				break;
			case PERIODEAANSLUITKOSTEN:
				colsinmodel.add(i,ti.getAansluitkostenPeriode().toString());
				break;
			default:
				break;
			}
		}
	}

	private boolean confirmDelete(TariefInfo wnr) {
		int rslt;
		rslt = JOptionPane
				.showConfirmDialog(null,"Weet u zeker dat u dit tarief wilt verwijderen?",
						"", JOptionPane.YES_NO_OPTION);
		if (rslt == JOptionPane.YES_OPTION) {
			return true;
		} else {
			return false;
		}
	}

	@Override
	protected boolean isDeleteAllowed(InfoBase data) {
		TariefInfo info = (TariefInfo)data;
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

	public void setWerkgever(WerkgeverInfo werkgever) {
		this.werkgever = werkgever;
	}

	public void setHolding(HoldingInfo holding) {
		this.holding = holding;
	}

	public void setTariefModel(TariefModel TariefModel) {
		this.model = TariefModel;
	}

	public List<TariefInfo> getTarievenList() {
		return model.getTarievenList(); 
	}
}
