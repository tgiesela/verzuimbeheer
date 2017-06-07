package com.gieselaar.verzuim.controllers;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.swing.JOptionPane;

import com.gieselaar.verzuim.interfaces.ControllerEventListener;
import com.gieselaar.verzuim.interfaces.DefaultControllerEventListener;
import com.gieselaar.verzuim.models.FactuurModel;
import com.gieselaar.verzuim.utils.ExceptionLogger;
import com.gieselaar.verzuim.utils.PrintFactuur;
import com.gieselaar.verzuim.views.AbstractDetail;
import com.gieselaar.verzuim.views.FactuurDetail;
import com.gieselaar.verzuim.views.FactuurList;
import com.gieselaar.verzuim.views.FactuurbetalingenList;
import com.gieselaar.verzuim.viewsutils.ColorTableModel;
import com.gieselaar.verzuimbeheer.exceptions.PermissionException;
import com.gieselaar.verzuimbeheer.exceptions.ValidationException;
import com.gieselaar.verzuimbeheer.exceptions.VerzuimApplicationException;
import com.gieselaar.verzuimbeheer.facades.LoginSessionRemote;
import com.gieselaar.verzuimbeheer.services.FactuurInfo;
import com.gieselaar.verzuimbeheer.services.FactuurInfo.__factuurstatus;
import com.gieselaar.verzuimbeheer.services.HoldingInfo;
import com.gieselaar.verzuimbeheer.services.InfoBase;
import com.gieselaar.verzuimbeheer.utils.ServiceLocatorException;
import com.gieselaar.verzuimbeheer.services.InfoBase.persistenceaction;
import com.gieselaar.verzuimbeheer.services.InfoBase.persistencestate;
import com.gieselaar.verzuimbeheer.services.FactuurTotaalInfo;

public class FactuurController extends AbstractController {

	private static final long serialVersionUID = 1L;

	public enum __factuurfields {
		UNKNOWN(-1), WERKGEVER(0), FACTUURNR(1), JAAR(2), MAAND(3), FACTUURBEDRAG(4), BETAALDBEDRAG(5), STATUS(
				6), EMAIL(7);
		private int value;

		__factuurfields(int value) {
			this.value = value;
		}

		public int getValue() {
			return value;
		}

		public static __factuurfields parse(int type) {
			__factuurfields field = UNKNOWN; // Default
			for (__factuurfields item : __factuurfields.values()) {
				if (item.getValue() == type) {
					field = item;
					break;
				}
			}
			return field;
		}
	}

	public enum __factuurcommands {
		UNKNOWN(-1), BETALINGENTONEN(1), VERBERGLEGEFACTUREN(2);
		private int value;

		__factuurcommands(int value) {
			this.value = value;
		}

		public int getValue() {
			return value;
		}

		public static __factuurcommands parse(int type) {
			__factuurcommands field = null; // Default
			for (__factuurcommands item : __factuurcommands.values()) {
				if (item.getValue() == type) {
					field = item;
					break;
				}
			}
			return field;
		}

		public static __factuurcommands parse(String type) {
			__factuurcommands field = __factuurcommands.UNKNOWN; // Default
			for (__factuurcommands item : __factuurcommands.values()) {
				if (item.toString().equals(type)) {
					field = item;
					break;
				}
			}
			return field;
		}
	}

	private FactuurModel model;

	private HoldingInfo holding;
	private boolean verberglegeFacturen;
	private FactuurTotaalInfo selectedFactuur;
	private Integer selectedWerkgeverid = -1;
	private ReportController reportcontroller;
	private FactuurController thiscontroller = this;

	public FactuurController(LoginSessionRemote session) {
		super(new FactuurModel(session), null);
		this.model = (FactuurModel) getModel();
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		switch (__factuurcommands.parse(e.getActionCommand())) {
		case VERBERGLEGEFACTUREN:
			verberglegeFacturen = !verberglegeFacturen;
			for (ControllerEventListener l : views) {
				l.refreshTable();
			}
			break;
		case BETALINGENTONEN:
			BetalingenController controller = new BetalingenController(this.getModel().getSession());
			controller.setDesktoppane(getDesktoppane());
			controller.setMaincontroller(this.getMaincontroller());
			FactuurbetalingenList frame = new FactuurbetalingenList(controller);
			controller.setFactuur(this.selectedFactuur);
			controller.addControllerListener(new DefaultControllerEventListener() {
				
				@Override
				public void formClosed(ControllerEventListener ves) {
					/* First read the selectedrow again.
					 */
					try {
						if (ves instanceof FactuurbetalingenList){
							saveData(selectedFactuur);
							for (ControllerEventListener l: views){
								l.refreshTable();
							}
						}
					} catch (VerzuimApplicationException e) {
						ExceptionLogger.ProcessException(e, thiscontroller.getActiveForm());
					}
				}
			});
			try {
				controller.selectBetalingen();
			} catch (VerzuimApplicationException e1) {
				ExceptionLogger.ProcessException(e1, null);
			}
			frame.setVisible(true);
			this.getDesktoppane().add(frame);
			this.getDesktoppane().setOpaque(true);
			this.getDesktoppane().moveToFront(frame);
		default:
			super.actionPerformed(e);
			break;
		}
	}

	public void selectFacturen(Date datefrom, Date dateuntil) throws VerzuimApplicationException {
		this.model.selectFacturen(datefrom, dateuntil);
	}

	public FactuurInfo createTarief(Integer holdingid, Integer werkgeverid) {
		FactuurInfo tarief = createNewTarief();
		return tarief;
	}

	private FactuurInfo createNewTarief() {
		FactuurInfo info = new FactuurInfo();
		return info;
	}

	@Override
	public void addData(InfoBase data) throws VerzuimApplicationException {
		FactuurTotaalInfo info = (FactuurTotaalInfo) data;
		try {
			info.validate();
		} catch (ValidationException e) {
			throw new VerzuimApplicationException(e, "Opslaan factuur niet geslaagd.");
		}
	}

	@Override
	public void saveData(InfoBase data) throws VerzuimApplicationException {
		FactuurTotaalInfo info = (FactuurTotaalInfo) data;
		try {
			info.validate();
			model.saveFactuur(info);
		} catch (ValidationException e) {
			throw new VerzuimApplicationException(e, "Opslaan tarief niet geslaagd.");
		}
	}

	public void showRow(ControllerEventListener ves, Object data) {
		if (!isShowAllowed((InfoBase) data))
			return;
		FactuurInfo info = (FactuurInfo) data;
		AbstractDetail form = super.createDetailForm(info, FactuurDetail.class, __formmode.UPDATE);
		super.showRow(ves, form, data);
	}

	public void addRow(ControllerEventListener ves) {
		if (!isNewAllowed())
			return;
		FactuurInfo info = createNewTarief();
		AbstractDetail form = super.createDetailForm(info, FactuurDetail.class, __formmode.NEW);
		super.addRow(ves, form);
	}

	@Override
	public void deleteRow(Object data) throws VerzuimApplicationException {
		if (!isDeleteAllowed((InfoBase) data)) {
			return;
		}
		FactuurTotaalInfo info = (FactuurTotaalInfo) data;
		info.setAction(persistenceaction.DELETE);
		info.setState(persistencestate.EXISTS);
		model.saveFactuur(info);
	}

	public void getTableModel(List<FactuurTotaalInfo> facturen, ColorTableModel tblmodel, List<Integer> colsinview) {
		boolean showrow;
		tblmodel.setRowCount(0);
		tblmodel.setColumnCount(colsinview.size());
		
		for (FactuurTotaalInfo info : facturen) {
			List<Object> colsinmodel = new ArrayList<>();

			showrow = true;
			if (info.getAction() == persistenceaction.DELETE){
				showrow = false;
			}
			if (verberglegeFacturen && (info.getTotaalInclBtw().compareTo(BigDecimal.ZERO) == 0)) {
				showrow = false;
			}
			if (selectedWerkgeverid != -1 && !info.getWerkgeverid().equals(selectedWerkgeverid)){
				showrow = false;
			}
			if (showrow){
				setColumnValues(colsinmodel, info, colsinview);
				if (info.getTotaalInclBtw().compareTo(BigDecimal.ZERO) == 0) {
					tblmodel.addRow(colsinmodel, info);
				} else {
					int result = info.getSombetalingen().setScale(2, RoundingMode.HALF_UP)
							.compareTo(info.getTotaalInclBtw().setScale(2, RoundingMode.HALF_UP));
					if (result == 0) {
						tblmodel.addRow(colsinmodel, info, Color.GREEN);
					} else {
						if (result > 0) {
							tblmodel.addRow(colsinmodel, info, Color.RED);
						} else {
							tblmodel.addRow(colsinmodel, info);
						}
					}
				}
			}
		}
	}

	private void setColumnValues(List<Object> colsinmodel, FactuurTotaalInfo fti, List<Integer> colsinview) {
		for (int i = 0; i < colsinview.size(); i++) {
			__factuurfields val = __factuurfields.parse(colsinview.get(i));
			switch (val) {
			case WERKGEVER:
				colsinmodel.add(i, fti.getWerkgever().getNaam());
				break;
			case BETAALDBEDRAG:
				colsinmodel.add(i, fti.getSombetalingen());
				break;
			case EMAIL:
				if (fti.isGesommeerd()) {
					if (fti.getWerkgever().getHolding() != null) {
						if (fti.getWerkgever().getHolding().getEmailadresfactuur() == null
								|| fti.getWerkgever().getHolding().getEmailadresfactuur().isEmpty()) {
							colsinmodel.add(i, "");
						} else {
							colsinmodel.add(i, fti.getWerkgever().getHolding().getEmailadresfactuur());
						}
					} else {
						colsinmodel.add("");
					}
				} else {
					if (fti.getWerkgever().getEmailadresfactuur() == null
							|| fti.getWerkgever().getEmailadresfactuur().isEmpty()) {
						colsinmodel.add(i, "");
					} else {
						colsinmodel.add(i, fti.getWerkgever().getEmailadresfactuur());
					}
				}
				break;
			case FACTUURBEDRAG:
				colsinmodel.add(i, fti.getTotaalInclBtw());
				break;
			case FACTUURNR:
				colsinmodel.add(i, fti.getFactuurnr().toString());
				break;
			case JAAR:
				colsinmodel.add(i, fti.getJaar().toString());
				break;
			case MAAND:
				colsinmodel.add(i, fti.getMaand().toString());
				break;
			case STATUS:
				colsinmodel.add(i, fti.getFactuurstatus().toString());
				break;
			default:
				break;
			}
		}
	}

	private boolean confirmDelete(FactuurInfo info) {
		int rslt = JOptionPane.showConfirmDialog(null, "Weet u zeker dat u de regel wilt verwijderen?", "",
				JOptionPane.YES_NO_OPTION);
		if (rslt == JOptionPane.YES_OPTION) {
			return true;
		} else {
			return false;
		}
	}

	@Override
	protected boolean isDeleteAllowed(InfoBase data) {
		FactuurInfo info = (FactuurInfo) data;
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

	public void setFactuurModel(FactuurModel FactuurModel) {
		this.model = FactuurModel;
	}

	public List<FactuurTotaalInfo> getFacturenList() {
		return model.getFacturenList();
	}
	@Override 
	public void rowSelected(int selectedRow, Object data) {
		selectedFactuur = (FactuurTotaalInfo)data;
		for (ControllerEventListener l : views) {
			if (l instanceof FactuurList){
				((FactuurList)l).setRowSelected();
			}
		}
	}

	public void setSelectedWerkgever(Integer id) {
		selectedWerkgeverid = id;
		for (ControllerEventListener l : views) {
			l.refreshTable();
		}
	}

	public void printWerkgeverFactuur(FactuurTotaalInfo factuur) {
		PrintFactuur pf;
		try {
			pf = new PrintFactuur(factuur, this.model.getSession());
			pf.afdrukkenDezeFactuur();
		} catch (PermissionException | VerzuimApplicationException | ServiceLocatorException | ValidationException | IOException e) {
			ExceptionLogger.ProcessException(e, this.getActiveForm());
		}
	}
	public void printHoldingFactuur(FactuurTotaalInfo factuur){
		PrintFactuur pf;
		try {
			pf = new PrintFactuur(factuur, this.model.getSession());
			pf.afdrukken();
		} catch (PermissionException | VerzuimApplicationException | ServiceLocatorException | ValidationException | IOException e) {
			ExceptionLogger.ProcessException(e, this.getActiveForm());
		}
	}
	public void printOrEmailFactuur(FactuurTotaalInfo fti, String emailaddress, String naam) throws VerzuimApplicationException {
		PrintFactuur pf;
		try {
			pf = new PrintFactuur(fti,this.model.getSession());
			if (pf.isPrinten()){
			    if (emailaddress == null || 
			    		emailaddress.isEmpty()) {
			    	pf.afdrukken();
			    } else {
			    	pf.email(emailaddress, naam);
			    }
		    	fti.setFactuurstatus(__factuurstatus.VERZONDEN);
			    saveData(fti);
			}
		} catch (PermissionException | ServiceLocatorException | ValidationException | IOException e) {
			throw new VerzuimApplicationException(e, "Kan factuur niet afdrukken of verzenden: " + fti.getPdflocation());
		}
	}	

	public void emailWerkgeverFactuur(FactuurTotaalInfo factuur) {
		PrintFactuur pf;
		try {
			pf = new PrintFactuur(factuur, this.model.getSession());
	    	String emailaddress = factuur.getWerkgever().getEmailadresfactuur();
			pf.emailDezeFactuur(emailaddress, factuur.getWerkgever().getNaam());
		} catch (PermissionException | VerzuimApplicationException | ServiceLocatorException e) {
			ExceptionLogger.ProcessException(e, this.getActiveForm());
		}
	}

	public void emailHoldingFactuur(FactuurTotaalInfo factuur) {
		PrintFactuur pf;
		try {
			pf = new PrintFactuur(factuur, this.model.getSession());
	    	String emailaddress = holding.getEmailadresfactuur();
			pf.email(emailaddress, factuur.getWerkgever().getHolding().getNaam());
		} catch (PermissionException | VerzuimApplicationException | ServiceLocatorException e) {
			ExceptionLogger.ProcessException(e, this.getActiveForm());
		}
	}
	public ReportController getReportController() {
		if (reportcontroller == null) {
			reportcontroller = new ReportController(this.model.getSession()){
				private static final long serialVersionUID = 1L;

				@Override
				public void rowSelected(int selectedRow, Object data) {
					for (ControllerEventListener l: views){
						l.rowSelected(selectedRow, data);
					}
				}
			};
			reportcontroller.setDesktoppane(getDesktoppane());
			reportcontroller.setMaincontroller(this.getMaincontroller());
		}
		return reportcontroller;
	}

	public int getAantalontbrekendeFacturen(int jaar, int maand) throws VerzuimApplicationException {
		return model.getAantalontbrekendeFacturen(jaar, maand);
	}

	public void afsluitenMaand(int jaar, int maand) throws VerzuimApplicationException {
		model.afsluitenMaand(jaar, maand);
	}
	public void terugdraaienMaand(int jaar, int maand) throws VerzuimApplicationException {
		model.terugdraaienMaand(jaar, maand);
	}
	public List<FactuurTotaalInfo> getFacturenInPeriodeByHolding(Date datefrom, Date dateuntil, Integer holdingid, boolean details) throws VerzuimApplicationException {
		return model.getFacturenInPeriodeByHolding(datefrom, dateuntil, holdingid, true);
	}
	public List<FactuurTotaalInfo> getFacturenInPeriodeByWerkgever(Date datefrom, Date dateuntil, Integer werkgeverid, boolean details) throws VerzuimApplicationException {
		return model.getFacturenInPeriodeByWerkgever(datefrom, dateuntil, werkgeverid, true);
	}
}
