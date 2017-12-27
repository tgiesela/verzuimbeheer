package com.gieselaar.verzuim.controllers;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import com.gieselaar.verzuim.controllers.AbstractController.__filedialogtype;
import com.gieselaar.verzuim.controllers.AbstractController.__selectfileoption;
import com.gieselaar.verzuim.interfaces.ControllerEventListener;
import com.gieselaar.verzuim.models.WerkgeverModel;
import com.gieselaar.verzuim.models.WerknemerModel;
import com.gieselaar.verzuim.utils.ExceptionLogger;
import com.gieselaar.verzuim.views.AbstractDetail;
import com.gieselaar.verzuim.views.ReportVerzuimenHistorie;
import com.gieselaar.verzuim.views.VerzuimDetail;
import com.gieselaar.verzuim.views.WerknemerDetail;
import com.gieselaar.verzuim.views.WerknemerWizard;
import com.gieselaar.verzuim.viewsutils.ColorTableModel;
import com.gieselaar.verzuimbeheer.exceptions.ValidationException;
import com.gieselaar.verzuimbeheer.exceptions.VerzuimApplicationException;
import com.gieselaar.verzuimbeheer.facades.LoginSessionRemote;
import com.gieselaar.verzuimbeheer.services.AfdelingHasWerknemerInfo;
import com.gieselaar.verzuimbeheer.services.InfoBase;
import com.gieselaar.verzuimbeheer.services.WerknemerFastInfo;
import com.gieselaar.verzuimbeheer.services.WerknemerInfo;
import com.gieselaar.verzuimbeheer.services.WiapercentageInfo;
import com.gieselaar.verzuimbeheer.services.InfoBase.persistenceaction;
import com.gieselaar.verzuimbeheer.services.InfoBase.persistencestate;
import com.gieselaar.verzuimbeheer.services.WerkgeverInfo;

public class WerknemerController extends AbstractController {

	private static final long serialVersionUID = 1L;

	public enum __werknemerfields {
		UNKNOWN(-1), ACHTERNAAM(0), BSN(1), GEBOORTEDATUM(2), WERKGEVER(3), DATUMINDIENST(4), DATUMUITDIENST(5), 
		VOORLETTERS(6), GESLACHT(7), PERSONEELSNUMMER(8), UREN(9), TELEFOONMOBIEL(10), TELEFOONPRIVE(11), AFDELINGNAAM(12);
		private int value;

		__werknemerfields(int value) {
			this.value = value;
		}

		public int getValue() {
			return value;
		}

		public static __werknemerfields parse(int type) {
			__werknemerfields field = UNKNOWN; // Default
			for (__werknemerfields item : __werknemerfields.values()) {
				if (item.getValue() == type) {
					field = item;
					break;
				}
			}
			return field;
		}
	}
	public enum __werknemercommands {
		UNKNOWN(-1), WERKNEMERALLEENOPENVERZUIMEN(1), WERKNEMERUITDIENSTTONEN(2), GENEREERDOCUMENT(3), WERKNEMEREXPORTEREN(4);
		private int value;

		__werknemercommands(int value) {
			this.value = value;
		}

		public int getValue() {
			return value;
		}

		public static __werknemercommands parse(int type) {
			__werknemercommands field = null; // Default
			for (__werknemercommands item : __werknemercommands.values()) {
				if (item.getValue() == type) {
					field = item;
					break;
				}
			}
			return field;
		}

		public static __werknemercommands parse(String type) {
			__werknemercommands field = __werknemercommands.UNKNOWN; // Default
			for (__werknemercommands item : __werknemercommands.values()) {
				if (item.toString().equals(type)) {
					field = item;
					break;
				}
			}
			return field;
		}
	}

	private WerknemerModel model;
	private WerkgeverModel werkgevermodel;
	/*
	 * Controllers create by this controller
	 */
	private VerzuimController verzuimcontroller;
	private AfdelingHasWerknemerController afdelingcontroller;
	private TodoController todocontroller;
	private DienstverbandController dienstverbandcontroller;
	
	private boolean alleenOpenverzuimen;
	private boolean uitDiensttonen;
	private WerknemerInfo selectedWerknemer;
	private WiapercentageController wiapercentagecontroller;
	private boolean uitDienstselected;
	private Integer werkgeverid = null;
	
	public WerknemerController(LoginSessionRemote session) {
		super(new WerknemerModel(session), null);
		this.model = (WerknemerModel) getModel();
		this.werkgevermodel = new WerkgeverModel(session);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		__werknemercommands cmd = __werknemercommands.parse(e.getActionCommand());
		if (cmd != null){
			switch (__werknemercommands.parse(e.getActionCommand())) {
			case WERKNEMERALLEENOPENVERZUIMEN:
				alleenOpenverzuimen = !alleenOpenverzuimen;
				for (ControllerEventListener l : views) {
					l.refreshTable();
				}
				break;
			case WERKNEMERUITDIENSTTONEN:
				uitDiensttonen = !uitDiensttonen;
				if (uitDiensttonen && !uitDienstselected){
					try {
						if (this.werkgeverid == null){
							model.getWerknemersuitdienst();
						}else{
							model.getWerknemersuitdienst(werkgeverid);
						}
					} catch (VerzuimApplicationException e1) {
						ExceptionLogger.ProcessException(e1, null);
					}
					uitDienstselected = true;
				}
				for (ControllerEventListener l : views) {
					l.refreshTable();
				}
				break;
			case WERKNEMEREXPORTEREN:
				this.exportToExcel();
				break;
			case UNKNOWN:
				super.actionPerformed(e);
			default:
				break;
			}
		}else{
			super.actionPerformed(e);
		}
	}
	private void addCSVColumn(String val, StringBuilder sb){
		if (val == null){
			sb.append("");
		}else{
			sb.append(val);
		}
		sb.append(";");
	}
	private void exportToExcel(){
		DateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
		// Vraag om naam excel bestand
		String documentname = "export.csv";
		String headerLabel;

		File selectedFile = null;

		selectedFile = selectFilename(__selectfileoption.FILEONLY,__filedialogtype.SAVE,"",".csv");
		if (selectedFile == null)
			return;
		else {
			if (selectedFile.exists()){
				if (JOptionPane.showConfirmDialog(null, "Bestand "
						+ selectedFile.getAbsolutePath()
						+ " bestaat al. Wilt u het verwijderen?", "Verwijderen",
						JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION){
					if (!selectedFile.delete()){
						JOptionPane.showMessageDialog(null, "Kan bestand niet verwijderen.");
						return;
					}
				}
				else
					return;
			}
			documentname = selectedFile.getAbsolutePath();
		}
		
		List<WerknemerFastInfo> werknemers = model.getAllWerknemersFast();
		
		PrintWriter pw;
		try{
			pw = new PrintWriter(new File(documentname));
		} catch (FileNotFoundException e){
			return;
		}
		StringBuilder sb = new StringBuilder();
        addCSVColumn("Organisatiecode",sb);
        addCSVColumn("Volgnr.dvb",sb);	
        addCSVColumn("Persoonsnr.",sb);
        addCSVColumn("Startdat.Contract",sb);
        addCSVColumn("Einddat.Contract",sb);
        addCSVColumn("Ingangsdat.",sb);
        addCSVColumn("Bruto jaarsalaris",sb);
        addCSVColumn("fte",sb);
        addCSVColumn("NVT",sb);
        addCSVColumn("Achternaam",sb);
        addCSVColumn("Voornaam",sb);
        addCSVColumn("Tussenvoegsel",sb);
        addCSVColumn("Voorletters",sb);
        addCSVColumn("Geslacht",sb);
        addCSVColumn("Meisjesnaam",sb);
        addCSVColumn("Geb.dat.",sb);
        addCSVColumn("Straat woonadres",sb);
        addCSVColumn("Huisnr.woonadres",sb);
        addCSVColumn("Postcode woonadres",sb);
        addCSVColumn("Plaats woonadres",sb);
        addCSVColumn("Landnaam",sb);
        addCSVColumn("Tel.nr.woonadres",sb);
        addCSVColumn("Tel.nr.mobiel",sb);
        addCSVColumn("Zakelijk Emailadres",sb);
        addCSVColumn("Prive Emailadres",sb);
        addCSVColumn("Oms functie",sb);
        addCSVColumn("Startdatum SFB / AGH",sb);
        addCSVColumn("Einddatum SFB / Agh",sb);
        addCSVColumn("WIA / WGA percentage",sb);
        addCSVColumn("Status SFB / AGH",sb);
        addCSVColumn("Oms SFB / AGH",sb);
        sb.append('\n');
		pw.write(sb.toString());
		WerkgeverInfo wgi = null;
		int currentWerkgever = -1;
		for (WerknemerFastInfo wi: werknemers){
			WerknemerInfo wfi;
			try {
				wfi= model.getWerknemerDetails(wi.getId());
				if (wfi.getActiefDienstverband().getWerkgeverId() != currentWerkgever){
					wgi = werkgevermodel.getWerkgeverDetails(wfi.getActiefDienstverband().getWerkgeverId());
					currentWerkgever = wgi.getId();
				}
			} catch (Exception e){
				ExceptionLogger.ProcessException(e, this.getActiveForm());
				pw.close();
				return;
			}
			sb = new StringBuilder();
	        addCSVColumn(wfi.getActiefDienstverband().getWerkgeverId().toString(), sb);
	        addCSVColumn(wfi.getActiefDienstverband().getId().toString(), sb);
	        if (wfi.getActiefDienstverband().getPersoneelsnummer() == null || wfi.getActiefDienstverband().getPersoneelsnummer().isEmpty()){
		        addCSVColumn(wfi.getActiefDienstverband().getId().toString(), sb);
	        }else{
	        	addCSVColumn(wfi.getActiefDienstverband().getPersoneelsnummer(), sb);
	        }
	        addCSVColumn(formatter.format(wfi.getActiefDienstverband().getStartdatumcontract()), sb);
	        addCSVColumn("", sb);
	        addCSVColumn(formatter.format(wfi.getActiefDienstverband().getStartdatumcontract()), sb);
	        addCSVColumn("", sb);
	        BigDecimal fte = wfi.getActiefDienstverband().getWerkweek().divide(wgi.getWerkweek()).setScale(2, BigDecimal.ROUND_HALF_UP);
	        addCSVColumn(fte.toPlainString(), sb); /* FTE berekenen: werkg uren <> dvb uren */
	        addCSVColumn("", sb);
	        addCSVColumn(wfi.getAchternaam(), sb);
	        addCSVColumn(wfi.getVoornaam(), sb);
	        addCSVColumn(wfi.getVoorvoegsel(), sb);
	        addCSVColumn(wfi.getVoorletters(), sb);
	        switch (wfi.getGeslacht()){
	        case MAN: addCSVColumn("M", sb); break;
	        case VROUW: addCSVColumn("V", sb); break;
	        case ONBEKEND: addCSVColumn("M", sb);
	        }
	        addCSVColumn("", sb);
	        addCSVColumn(formatter.format(wfi.getGeboortedatum()),sb);
	        addCSVColumn(wfi.getAdres().getStraat(),sb);
	        addCSVColumn(wfi.getAdres().getHuisnummer() + " " + wfi.getAdres().getHuisnummertoevoeging(),sb);
	        addCSVColumn(wfi.getAdres().getPostcode(),sb);
	        addCSVColumn(wfi.getAdres().getPlaats(),sb);
	        addCSVColumn(wfi.getAdres().getLand(),sb);
	        addCSVColumn(wfi.getTelefoonPrive(),sb);
	        addCSVColumn(wfi.getMobiel(),sb);
	        addCSVColumn(wfi.getEmail(),sb);
	        addCSVColumn("",sb);
	        if (wfi.getActiefDienstverband().getFunctie() == null || wfi.getActiefDienstverband().getFunctie().isEmpty()){
		        addCSVColumn("Onbekend",sb);
	        }else{
		        addCSVColumn(wfi.getActiefDienstverband().getFunctie(),sb);
	        }
	        addCSVColumn("",sb);
	        addCSVColumn("",sb);
	        addCSVColumn("",sb);
	        addCSVColumn("",sb);
	        addCSVColumn("",sb);
	        sb.append('\n');
	        
	        pw.write(sb.toString());
	    }
        pw.close();
	
	}

	public void selectWerknemers() {
		try {
			this.werkgeverid = null;
			model.getWerknemers();
			if (uitDiensttonen){
				model.getWerknemersuitdienst();
			}
		} catch (VerzuimApplicationException e) {
			ExceptionLogger.ProcessException(e, this.getActiveForm());
		}
	}

	public void selectWerknemers(Integer werkgeverid) {
		try {
			this.werkgeverid = werkgeverid; 
			model.getWerknemers(werkgeverid);
			if (uitDiensttonen){
				model.getWerknemersuitdienst(werkgeverid);
			}
		} catch (VerzuimApplicationException e) {
			ExceptionLogger.ProcessException(e, this.getActiveForm());
		}
	}

	public WerknemerInfo getWerknemer(Integer werknemerid) {
		try {
			selectedWerknemer = model.getWerknemerDetails(werknemerid); 
			return selectedWerknemer;
		} catch (VerzuimApplicationException | ValidationException e) {
			ExceptionLogger.ProcessException(e, this.getActiveForm());
		}
		return null;
	}

	public VerzuimController getVerzuimController() throws VerzuimApplicationException {
//		if (verzuimcontroller == null) {
			verzuimcontroller = new VerzuimController(this.model.getSession()){
				private static final long serialVersionUID = 1L;

				@Override
				public void rowSelected(int selectedRow, Object data) {
					for (ControllerEventListener l: views){
						l.rowSelected(selectedRow, data);
					}
				}
				@Override
				public void closeView(ControllerEventListener listener) {
					handleVerzuimformClosed();
					
				}
			};
			verzuimcontroller.setDesktoppane(getDesktoppane());
			verzuimcontroller.setMaincontroller(this.getMaincontroller());
//		}
		verzuimcontroller.setWerknemer(selectedWerknemer);
		return verzuimcontroller;
	}
	protected void handleVerzuimformClosed() {
		try {
			selectedWerknemer = model.getWerknemerDetails(selectedWerknemer.getId());
//			this.getDetailform().setData(selectedWerknemer);
		} catch (VerzuimApplicationException | ValidationException e) {
			ExceptionLogger.ProcessException(e, this.getActiveForm());
		}
		for (ControllerEventListener l: views){
			if (l instanceof VerzuimDetail){
				((VerzuimDetail)l).setData(selectedWerknemer);
			}
		}
	}
	public TodoController getTodoController() throws VerzuimApplicationException {
		todocontroller = new TodoController(this.model.getSession()){
			private static final long serialVersionUID = 1L;
			@Override
			public void rowSelected(int selectedRow, Object data) {
				for (ControllerEventListener l: views){
					l.rowSelected(selectedRow, data);
				}
			}
		};
		todocontroller.setDesktoppane(getDesktoppane());
		todocontroller.setMaincontroller(this.getMaincontroller());
		todocontroller.setVerzuim(null);
		return todocontroller;
	}
	public AfdelingHasWerknemerController getAfdelingController(WerknemerInfo werknemer) throws VerzuimApplicationException {
		afdelingcontroller = new AfdelingHasWerknemerController(this.model.getSession(), this.getModel()){
			private static final long serialVersionUID = 1L;
			@Override
			public void rowSelected(int selectedRow, Object data) {
				for (ControllerEventListener l: views){
					l.rowSelected(selectedRow, data);
				}
			}
		};
		afdelingcontroller.setDesktoppane(getDesktoppane());
		afdelingcontroller.setMaincontroller(this.getMaincontroller());
		if (werknemer != null){
			afdelingcontroller.setWerknemer(werknemer);
		}else{
			afdelingcontroller.setWerknemer(selectedWerknemer);
		}
		return afdelingcontroller;
	}
	public WiapercentageController getWiapercentageController(WerknemerInfo werknemer) throws VerzuimApplicationException {
		wiapercentagecontroller = new WiapercentageController(this.model.getSession()){
			private static final long serialVersionUID = 1L;

			@Override
			public void rowSelected(int selectedRow, Object data) {
				for (ControllerEventListener l: views){
					l.rowSelected(selectedRow, data);
				}
			}
		};
		wiapercentagecontroller.setDesktoppane(getDesktoppane());
		wiapercentagecontroller.setMaincontroller(this.getMaincontroller());
		if (werknemer != null){
			wiapercentagecontroller.setWerknemer(werknemer);
		}else{
			wiapercentagecontroller.setWerknemer(selectedWerknemer);
		}
		return wiapercentagecontroller;
	}
	public DienstverbandController getDienstverbandController(WerknemerInfo werknemer) throws VerzuimApplicationException {
		dienstverbandcontroller = new DienstverbandController(this.model.getSession()){
			private static final long serialVersionUID = 1L;

			@Override
			public void rowSelected(int selectedRow, Object data) {
				for (ControllerEventListener l: views){
					l.rowSelected(selectedRow, data);
				}
			}
		};
		dienstverbandcontroller.setDesktoppane(getDesktoppane());
		dienstverbandcontroller.setMaincontroller(this.getMaincontroller());
		if (werknemer != null){
			dienstverbandcontroller.setWerknemer(werknemer);
		}else{
			dienstverbandcontroller.setWerknemer(selectedWerknemer);
		}
		return dienstverbandcontroller;
	}
	
	public void showRow(ControllerEventListener ves, Object data) {
		if (!isShowAllowed((InfoBase)data))
			return;
		WerknemerFastInfo wfi = (WerknemerFastInfo) data;
		selectedWerknemer = this.getWerknemer(wfi.getId());
		AbstractDetail form = super.createDetailForm(selectedWerknemer, WerknemerDetail.class, __formmode.UPDATE);
		super.showRow(ves, form, data);
	}

	public void addRow(ControllerEventListener ves) {
		if (!isNewAllowed())
			return;
		selectedWerknemer = null;
		AbstractDetail form = super.createDetailForm(selectedWerknemer, WerknemerWizard.class, __formmode.NEW);
		super.addRow(ves, form);
	}
	@Override
	public void deleteRow(Object data) throws VerzuimApplicationException {
		if (!isDeleteAllowed((InfoBase)data)){
			return;
		}
		WerknemerFastInfo wnrf = (WerknemerFastInfo) data;
		WerknemerInfo wnr = new WerknemerInfo();
		wnr.setAction(persistenceaction.DELETE);
		wnr.setState(persistencestate.EXISTS);
		wnr.setId(wnrf.getId());
		model.deleteWerknemer(wnr);

	}

	public void getTableModel(List<WerknemerFastInfo> werknemers, ColorTableModel tblmodel, List<Integer> colsinview) {
		/*
		 * Assumption: Data in werknemers is already filtered
		 */
		boolean openverzuimfound;
		tblmodel.setRowCount(0);
		tblmodel.setColumnCount(colsinview.size());
		for (WerknemerFastInfo wfi : werknemers) {
			List<Object> colsinmodel = new ArrayList<>();
			if (wfi.getVzmcnt() > 0)
				openverzuimfound = wfi.getOpenvzm() > 0;
			else
				openverzuimfound = false;
			setColumnValues(colsinmodel, wfi, colsinview);
			if (!wfi.isActief()) {
				if (uitDiensttonen)
					tblmodel.addRow(colsinmodel, (Object) wfi, Color.GRAY);
			} else {
				if (openverzuimfound)
					tblmodel.addRow(colsinmodel, wfi, Color.ORANGE);
				else if (alleenOpenverzuimen) {
					/* do not show */
				} else
					tblmodel.addRow(colsinmodel, wfi);
			}
		}
	}

	private void setColumnValues(List<Object> colsinmodel, WerknemerFastInfo wfi, List<Integer> colsinview) {
		for (int i = 0; i < colsinview.size(); i++) {
			__werknemerfields val = __werknemerfields.parse(colsinview.get(i));
			switch (val) {
			case BSN:
				colsinmodel.add(i, wfi.getBurgerservicenummer());
				break;
			case WERKGEVER:
				colsinmodel.add(i, wfi.getWerkgevernaam());
				break;
			case GEBOORTEDATUM:
				colsinmodel.add(i, wfi.getGeboortedatum());
				break;
			case ACHTERNAAM:
				if (wfi.getVoorvoegsel() == null || wfi.getVoorvoegsel().isEmpty())
					colsinmodel.add(i, wfi.getAchternaam());
				else
					colsinmodel.add(i, wfi.getAchternaam() + ", " + wfi.getVoorvoegsel());
				break;
			case VOORLETTERS:
				colsinmodel.add(i, wfi.getVoorletters());
				break;
			case GESLACHT:
				colsinmodel.add(i, wfi.getGeslacht().toString());
				break;
			case DATUMINDIENST:
				colsinmodel.add(i, wfi.getStartdatumcontract());
				break;
			case DATUMUITDIENST:
				colsinmodel.add(i, wfi.getEinddatumcontract());
				break;
			case PERSONEELSNUMMER:
				colsinmodel.add(i, wfi.getPersoneelsnummer());
				break;
			case UREN:
				colsinmodel.add(i, wfi.getWerkweek());
				break;
			case TELEFOONMOBIEL:
				colsinmodel.add(i, wfi.getMobiel());
				break;
			case TELEFOONPRIVE:
				colsinmodel.add(i, wfi.getTelefoonPrive());
				break;
			case AFDELINGNAAM:
				colsinmodel.add(i, wfi.getAfdelingnaam());
				break;
			default:
				break;
			}
		}
	}

	public void applyFilters(List<WerknemerFastInfo> werknemers) {
		if (alleenOpenverzuimen) {
			for (Iterator<WerknemerFastInfo> iter = werknemers.listIterator(); iter.hasNext();) {
				WerknemerFastInfo wgi = iter.next();
				if (!(wgi.getVzmcnt() > 0 && wgi.getOpenvzm() > 0)) {
					iter.remove();
				}
			}
		}
		if (uitDiensttonen) {
			for (Iterator<WerknemerFastInfo> iter = werknemers.listIterator(); iter.hasNext();) {
				WerknemerFastInfo wgi = iter.next();
				if (!(wgi.getVzmcnt() > 0 && wgi.getOpenvzm() > 0)) {
					iter.remove();
				}
			}
		}
	}
	/*
	 * returns resultlist with werknemers.
	 * depending on the select* function it will return all werknemers
	 * or werknemers belonging to Werkgever.
	 */
	public List<WerknemerFastInfo> getWerknemerList() {
		
		if (this.uitDiensttonen){
			return model.getAllWerknemersFast();
		}else{
			return model.getActiveWerknemersFast();
		}
	}

	private boolean confirmDelete(WerknemerInfo wnr, WerknemerFastInfo wnrf) {
		int rslt;
		rslt = JOptionPane
				.showConfirmDialog(null,
						"Weet u zeker dat u de werknemer wilt verwijderen?\n"
								+ "Hiermee wordt alle informatie definitief verwijderd.",
						"", JOptionPane.YES_NO_OPTION);
		if (rslt == JOptionPane.YES_OPTION) {
			if (wnr.getDienstVerbanden().size() > 1) {
				rslt = JOptionPane.showConfirmDialog(null,
						"De werknemer heeft meerdere dienstverbanden.\n"
								+ "Weer u zeker dat u alle dienstverbanden wilt verwijderen?",
						"", JOptionPane.YES_NO_OPTION);
				if (rslt != JOptionPane.YES_OPTION) {
					return false;
				}
			}
			if (wnrf.getVzmcnt() > 0) {
				rslt = JOptionPane.showConfirmDialog(null,
						"Er zijn meerdere verzuimen.\n" + "Weer u zeker dat u alle verzuimen wilt verwijderen?", "",
						JOptionPane.YES_NO_OPTION);
				if (rslt != JOptionPane.YES_OPTION) {
					return false;
				}
			}
			if (wnrf.getOpenvzm() > 0) {
				JOptionPane.showMessageDialog(null, "Er is nog een open verzuim.\n" + "verwijderen niet mogelijk", "",
						JOptionPane.OK_OPTION);
				return false;
			} else {
				return true;
			}
		} else {
			return false;
		}
	}

	@Override
	protected boolean isDeleteAllowed(InfoBase data) {
		WerknemerFastInfo wnrf;
		WerknemerInfo wnr;
		wnrf = (WerknemerFastInfo) data;
		try {
			wnr = model.getWerknemerDetails(wnrf.getId());
			return confirmDelete(wnr, wnrf);
		} catch (VerzuimApplicationException | ValidationException e) {
			ExceptionLogger.ProcessException(e, this.getActiveForm());
		}
		return false;
	}

	@Override
	protected boolean isShowAllowed(InfoBase data) {
		return true;
	}

	@Override
	protected boolean isNewAllowed() {
		return true;
	}

	public WerkgeverInfo getWerkgeverDetails(int werkgeverid) {
		try {
			return werkgevermodel.getWerkgeverDetails(werkgeverid);
		} catch (VerzuimApplicationException e) {
			ExceptionLogger.ProcessException(e, this.getActiveForm());
		}
		return null;
	}

	public void validateBurgerservicenummer(ControllerEventListener form, String text, Integer werkgeverid) throws VerzuimApplicationException {
		WerknemerInfo werknemer = new WerknemerInfo();
		try {
			WerknemerInfo.validateBSN(text);

			List<WerknemerInfo> existingbsns;
			existingbsns = model.getByBSN(werkgeverid,text);
			if (existingbsns.isEmpty()){
				existingbsns = model.getByBSN(text);
				if (existingbsns.isEmpty()){
					/* no existing bsns */
				}else{
					int reply = JOptionPane.showConfirmDialog(null, 
							"Er bestaat reeds een medewerker bij een ander bedrijf met hetzelfde BSN.\r" +
							"Wilt u deze gebruiken?", "BSN", JOptionPane.YES_NO_OPTION);
					if (reply == JOptionPane.YES_OPTION){
						werknemer = getMostRecentBSN(existingbsns);
						
						werknemer.setAction(persistenceaction.INSERT);
						werknemer.setId(-1);
						if (werknemer.getAdres() != null){
							werknemer.getAdres().setAction(persistenceaction.INSERT);
							werknemer.getAdres().setState(persistencestate.ABSENT);
							werknemer.getAdres().setId(-1);
						}
						for (WiapercentageInfo wpi: werknemer.getWiaPercentages()){
							wpi.setAction(persistenceaction.INSERT);
							wpi.setId(-1);
							wpi.setWerknemerId(-1);
						}
						werknemer.setWerkgeverid(werkgeverid);
						List<AfdelingHasWerknemerInfo> afdelingen = new ArrayList<>();
						werknemer.setAfdelingen(afdelingen);
						werknemer.setDienstVerbanden(null);
						form.setDetailFormmode(__formmode.NEW);
						form.setData(werknemer);
					}
				}
			}else{
				int reply = JOptionPane.showConfirmDialog(null, 
									"Er bestaat reeds een medewerker bij dit bedrijf met hetzelfde BSN.\r" +
									"Wilt u deze gebruiken?", "BSN", JOptionPane.YES_NO_OPTION);
				if (reply == JOptionPane.YES_OPTION){
					werknemer = getMostRecentBSN(existingbsns); 
					form.setDetailFormmode(__formmode.UPDATE);
					form.setData(werknemer);
				}
			}
		} catch (ValidationException  e) {
			throw new VerzuimApplicationException(e, "Controle BSN");
		}
	}	
	private WerknemerInfo getMostRecentBSN(List<WerknemerInfo> existingbsns) {
		WerknemerInfo recentst = existingbsns.get(0);
		for (WerknemerInfo wi : existingbsns){
			if (recentst.getLaatsteDienstverband().getStartdatumcontract().before(wi.getLaatsteDienstverband().getStartdatumcontract()))
				recentst = wi;
		}
		return recentst;
	}
	public void showVerzuimhistorie(Integer werkgeverid, Integer werknemerid) {
		ReportController controller = new ReportController(this.getModel().getSession());
		controller.setDesktoppane(getDesktoppane());
		controller.setMaincontroller(this.getMaincontroller());
		ReportVerzuimenHistorie form = new ReportVerzuimenHistorie(controller);
		ControllerEventListener listener = form;
		listener.setDetailFormmode(__formmode.NEW);
		form.setData(werkgeverid, werknemerid);
		this.getDesktoppane().add(form);
		this.getDesktoppane().moveToFront(form);
		form.setVisible(true);
	}

	@Override
	public void saveData(InfoBase data) throws VerzuimApplicationException {
		WerknemerInfo werknemer = (WerknemerInfo)data;
		try {
			werknemer.validate();
			model.saveWerknemer(werknemer);
		} catch (ValidationException e) {
			throw new VerzuimApplicationException(e, "Opslaan werknemer niet geslaagd.");
		}
	}

	@Override
	public void addData(InfoBase data) throws VerzuimApplicationException {
		WerknemerInfo werknemer = (WerknemerInfo)data;
		try {
			werknemer.validate();
			model.addWerknemer(werknemer);
		} catch (ValidationException e) {
			throw new VerzuimApplicationException(e, "Opslaan werknemer niet geslaagd.");
		}
	}
	@Override
	public void dataSaved(AbstractDetail view) {
		view.setData(null);
	}

	public void openwerkgever(Integer werkgeverid) {
		WerkgeverController controller = new WerkgeverController(this.getModel().getSession());
		controller.setDesktoppane(getDesktoppane());
		controller.setMaincontroller(this.getMaincontroller());
		WerkgeverInfo werkgever = controller.getWerkgeverDetails(werkgeverid);
		controller.showRow(null, werkgever);
	}
}
