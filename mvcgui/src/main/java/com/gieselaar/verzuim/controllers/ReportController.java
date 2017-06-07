package com.gieselaar.verzuim.controllers;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFDataFormat;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import com.gieselaar.verzuim.controllers.AbstractController.__filedialogtype;
import com.gieselaar.verzuim.controllers.AbstractController.__selectfileoption;
import com.gieselaar.verzuim.interfaces.ControllerEventListener;
import com.gieselaar.verzuim.models.FactuurModel;
import com.gieselaar.verzuim.models.InstantieModel;
import com.gieselaar.verzuim.models.ReportModel;
import com.gieselaar.verzuim.models.WerkgeverModel;
import com.gieselaar.verzuim.models.WerknemerModel;
import com.gieselaar.verzuim.utils.ExceptionLogger;
import com.gieselaar.verzuim.viewsutils.ColorTableModel;
import com.gieselaar.verzuim.viewsutils.VerzuimComboBoxModel;
import com.gieselaar.verzuimbeheer.exceptions.ValidationException;
import com.gieselaar.verzuimbeheer.exceptions.VerzuimApplicationException;
import com.gieselaar.verzuimbeheer.facades.LoginSessionRemote;
import com.gieselaar.verzuimbeheer.reportservices.ActueelVerzuimInfo;
import com.gieselaar.verzuimbeheer.reportservices.WerknemerVerzuimInfo;
import com.gieselaar.verzuimbeheer.services.InfoBase;
import com.gieselaar.verzuimbeheer.services.VerzuimInfo.__vangnettype;
import com.gieselaar.verzuimbeheer.services.WerkgeverInfo;
import com.gieselaar.verzuimbeheer.services.WerknemerFastInfo;
import com.gieselaar.verzuimbeheer.services.WerkzaamhedenInfo;
import com.gieselaar.verzuimbeheer.services.OeInfo;
import com.gieselaar.verzuimbeheer.services.SettingsInfo;
import com.gieselaar.verzuimbeheer.utils.ServiceLocatorException;
import com.gieselaar.verzuimbeheer.utils.TypeEntry;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRParameter;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.util.JRLoader;
import net.sf.jasperreports.swing.JRViewer;
import reportdatasources.ActueelverzuimDatasource;
import reportdatasources.FrequentVerzuimersDatasource;
import reportdatasources.VerzuimHistorieDatasource;
import reportdatasources.VerzuimOverzichtDatasource;
import reportdatasources.WerkzaamhedenDatasource;

import com.gieselaar.verzuimbeheer.services.ActiviteitInfo;
import com.gieselaar.verzuimbeheer.services.BedrijfsgegevensInfo;
import com.gieselaar.verzuimbeheer.services.FactuurTotaalInfo;
import com.gieselaar.verzuimbeheer.services.HoldingInfo;

public class ReportController extends AbstractController {

	private static final long serialVersionUID = 1L;

	public enum __reportfieldfields {
		UNKNOWN(-1), CODE(0), NAAM(1);
		private int value;

		__reportfieldfields(int value) {
			this.value = value;
		}

		public int getValue() {
			return value;
		}

		public static __reportfieldfields parse(int type) {
			__reportfieldfields field = UNKNOWN; // Default
			for (__reportfieldfields item : __reportfieldfields.values()) {
				if (item.getValue() == type) {
					field = item;
					break;
				}
			}
			return field;
		}
	}
	private WerkgeverModel model;
	private ReportModel reportmodel;
	private WerknemerModel werknemermodel;
	private WerkgeverModel werkgevermodel;
	private FactuurModel factuurmodel;
	private List<ActueelVerzuimInfo> verzuimen;
	private List<WerknemerVerzuimInfo> verzuimenditjaar;
	private List<WerknemerVerzuimInfo> verzuimen12maanden;
	private SettingsInfo settings;
	
	public ReportController(LoginSessionRemote session) {
		super(new WerkgeverModel(session), null);
		this.model = (WerkgeverModel)getModel();
		this.reportmodel = new ReportModel(session);
		this.werknemermodel = new WerknemerModel(session);
		this.werkgevermodel = new WerkgeverModel(session);
		this.factuurmodel = new FactuurModel(session);
	}
	@Override
	public void setMaincontroller(MDIMainController maincontroller) {
		super.setMaincontroller(maincontroller);
		settings = getMaincontroller().getSettings();
	}

	public void selectOes() throws VerzuimApplicationException {
		model.selectOes();
	}

	@Override
	public void addData(InfoBase data) throws VerzuimApplicationException {
		/* noop */
	}
	@Override
	public void saveData(InfoBase data) throws VerzuimApplicationException {
		/* noop */
	}
	@Override
	public void showRow(ControllerEventListener ves, Object data) {
		/* noop */
	}
	@Override
	public void addRow(ControllerEventListener ves) {
		/* noop */
	}
	@Override
	public void deleteRow(Object data) throws VerzuimApplicationException {
		/* noop */
	}

	public void getTableModel(List<ActiviteitInfo> activiteiten, ColorTableModel tblmodel, List<Integer> colsinview) {
		/* noop */
	}

	@Override
	protected boolean isDeleteAllowed(InfoBase data) {
		return true;
	}

	@Override
	protected boolean isShowAllowed(InfoBase data) {
		return true;
	}

	@Override
	protected boolean isNewAllowed() {
		return true;
	}

	public List<OeInfo> getOesList() {
		return model.getOeList();
	}

	public VerzuimComboBoxModel getComboModelOes() {
		List<OeInfo> niveaus = getOesList();
		/* Just a utility function to create ComboBoxModel */
		VerzuimComboBoxModel niveaumodel = new VerzuimComboBoxModel(getMaincontroller());
		niveaumodel.addElement(new TypeEntry(-1,"[]"));
		for (OeInfo w: niveaus)
		{
			TypeEntry wg = new TypeEntry(w.getId(), w.getNaam());
			niveaumodel.addElement(wg);
		}
		return niveaumodel;
	}

	public List<ActueelVerzuimInfo> getActueelVerzuim(Integer werkgeverid, Integer holdingid, Integer oeid, Date datefrom, Date dateuntil,
			boolean inclusiefzwangerschap) throws VerzuimApplicationException {
		return reportmodel.getActueelVerzuim(werkgeverid, holdingid, oeid, datefrom, dateuntil, inclusiefzwangerschap);
	}

	public List<WerknemerVerzuimInfo> getWerknemerVerzuimen(Integer werkgeverid, Integer holdingid, Integer oeid,
			Date datefrom, Date dateuntil, int aantal) throws VerzuimApplicationException {
		return reportmodel.getWerknemerVerzuimen(werkgeverid, holdingid, oeid, datefrom, dateuntil, aantal);
	}
	private void getResulsetsActueelverzuim(
			Integer werkgeverid
		  , Integer holdingid
		  , Integer oeid
		  , Date datefrom
		  , Date dateuntil)
	{
		try {
			verzuimen = getActueelVerzuim(werkgeverid, holdingid, oeid, datefrom, dateuntil, true);
			Calendar cal = Calendar.getInstance();
			cal.setTime(dateuntil);
			cal.set(Calendar.MONTH,0);
			cal.set(Calendar.DATE,1);
			verzuimenditjaar = getWerknemerVerzuimen(werkgeverid, holdingid, oeid, cal.getTime(), dateuntil,0);
			cal.setTime(dateuntil);
			cal.add(Calendar.MONTH,-12);
			verzuimen12maanden = getWerknemerVerzuimen(werkgeverid, holdingid, oeid, cal.getTime(), dateuntil,0);
		} catch (VerzuimApplicationException e) {
			ExceptionLogger.ProcessException(e, this.getActiveForm());
		}
	}
	private String getBedrijfsnaam(Integer werkgeverid, Integer holdingid, Integer oeid) throws VerzuimApplicationException{
		String selectedbedrijfsnaam = "";
		if (werkgeverid != -1){
			WerkgeverInfo werkgever = model.getWerkgeverDetails(werkgeverid);
			selectedbedrijfsnaam = werkgever.getNaam();
		}
		if (holdingid != -1){
			HoldingInfo holding = model.getHoldingDetails(holdingid);
			selectedbedrijfsnaam = holding.getNaam();
		}
		if (oeid != -1){
			OeInfo oe = model.getOeDetails(oeid);
			selectedbedrijfsnaam = oe.getNaam();
		}
		return selectedbedrijfsnaam;
	}
	private String getHoldingnaam(Integer holdingid) throws VerzuimApplicationException {
		HoldingInfo holding = model.getHoldingDetails(holdingid);
		return holding.getNaam();
	}

	public void printActueelverzuimOverzicht(
			Integer werkgeverid
		  , Integer holdingid
		  ,	Integer oeid
		  , Date datefrom
		  , Date dateuntil
		  , boolean redenverzuimtonen) throws VerzuimApplicationException {
		try {
			if (werkgeverid == -1 && holdingid == -1 && oeid == -1){
				throw new ValidationException("Geen bedrijf gekozen!");
			}
			if (dateuntil.before(datefrom)){
				throw new ValidationException("Einddatum ligt voor de startdatum!");
			}
			if (dateuntil.equals(datefrom)){
				throw new ValidationException("Einddatum is gelijk aan startdatum!");
			}
			if (dateuntil.after(new Date())){
				throw new ValidationException("Einddatum ligt in de toekomst!");
			}
			
	    	ActueelverzuimDatasource ds = new ActueelverzuimDatasource(datefrom, dateuntil, model.getSession());
			JasperPrint print;
			String bedrijfsnaam = getBedrijfsnaam(werkgeverid, holdingid, oeid);
			print = ds.getReport(werkgeverid, holdingid, oeid, bedrijfsnaam, redenverzuimtonen);
			showPrintFrame(print);
		} catch (ValidationException e1) {
			ExceptionLogger.ProcessException(e1, null);
			return;
		}
	}
	public void printVerzuimhistorie(Integer werknemerid, Date datefrom, Date dateuntil) throws ValidationException {
		if (dateuntil.before(datefrom)){
			throw new ValidationException("Einddatum ligt voor de startdatum!");
		}
		if (dateuntil.equals(datefrom)){
			throw new ValidationException("Einddatum is gelijk aan startdatum!");
		}
		if (dateuntil.after(new Date())){
        	throw new ValidationException("Einddatum ligt in de toekomst!");
		}
		VerzuimHistorieDatasource ds = new VerzuimHistorieDatasource(datefrom, dateuntil, model.getSession());
		JasperPrint print;
		try {
			print = ds.getReport(werknemerid);
			showPrintFrame(print);
		} catch (ValidationException e1) {
			ExceptionLogger.ProcessException(e1, null);
        	return;
		}

	}
	public void printVerzuimoverzicht(Integer werkgeverid, Integer holdingid, Integer oeid, Date datefrom, Date dateuntil,
			boolean inclusiefzwangerschap) throws ValidationException, ServiceLocatorException, VerzuimApplicationException {
		if (werkgeverid == -1 && holdingid == -1){
			throw new ValidationException("Geen bedrijf gekozen!");
		}
		if (dateuntil.before(datefrom)){
			throw new ValidationException("Einddatum ligt voor de startdatum!");
		}
		if (dateuntil.equals(datefrom)){
			throw new ValidationException("Einddatum is gelijk aan startdatum!");
		}
		if (dateuntil.after(new Date())){
			throw new ValidationException("Einddatum ligt in de toekomst!");
		}
		VerzuimOverzichtDatasource ds = new VerzuimOverzichtDatasource(datefrom, dateuntil, model.getSession());
		JasperPrint print;
		if (holdingid.equals(-1)){
			print = ds.getReport(werkgeverid, holdingid, oeid, "", inclusiefzwangerschap);
			showPrintFrame(print);
		}else{
			String holdingnaam = getHoldingnaam(holdingid);
			print = ds.getReport(werkgeverid, holdingid, oeid, holdingnaam, inclusiefzwangerschap);
			showPrintFrame(print);
		}
	}
	
	public void printFrequentVerzuimers(
			Integer werkgeverid
		  , Integer holdingid
		  , Date datefrom
		  , Date dateuntil
		  , int aantal) throws VerzuimApplicationException, ValidationException {

		if (dateuntil.before(datefrom)){
        	throw new ValidationException("Einddatum ligt voor de startdatum!");
		}
		if (dateuntil.equals(datefrom)){
			throw new ValidationException("Einddatum is gelijk aan startdatum!");
		}
		if (dateuntil.after(new Date())){
			throw new ValidationException("Einddatum ligt in de toekomst!");
		}
		
		FrequentVerzuimersDatasource ds = new FrequentVerzuimersDatasource(datefrom, dateuntil, model.getSession());
		JasperPrint print;
		try {
			print = ds.getReport(werkgeverid, holdingid, -1, aantal);
			showPrintFrame(print);
		} catch (ValidationException e1) {
			ExceptionLogger.ProcessException(e1, null);
        	return;
		}
	}

	public void printWerkzaamhedenOverzicht(
			Integer werkgeverid
		  , Integer holdingid
		  , Integer gebruikerid
		  , List<WerkzaamhedenInfo> werkzaamheden
		  ) throws VerzuimApplicationException, ValidationException {

		URL url = JRLoader.getResource("WerkzaamhedenOverzicht.jasper");
		if (url == null){
			throw new ValidationException("Kan resource WerkzaamhedenOverzicht.jasper niet vinden!");
		}
		String path = new File(url.getFile()).getParent();

		Map<String,Object> parameters = new HashMap<String, Object>();
		parameters.put("SUBREPORT_DIR", path);
		parameters.put(JRParameter.REPORT_LOCALE, new Locale("nl","NL"));
		JasperReport report;

		parameters.put("Footer_datasource",getBedrijfsgegevens());
		parameters.put("holdingid", holdingid);
		parameters.put("werkgeverid", werkgeverid);

		try {
			report = (JasperReport) JRLoader.loadObject(url);

			WerkzaamhedenDatasource wzds = new WerkzaamhedenDatasource(getMaincontroller().getWerkgevers()
																	 , getMaincontroller().getGebruikers()
																	 , getMaincontroller().getHoldings());
			JasperPrint print = JasperFillManager.fillReport(report, parameters, wzds.getDataSource(werkzaamheden, gebruikerid, werkgeverid, holdingid));
			showPrintFrame(print);
		} catch (JRException e1) {
			ExceptionLogger.ProcessException(e1, null);
        	return;
		}
	}
	public void printBTWenOmzetOverzicht(Date datefrom, Date dateuntil) {
		URL url = JRLoader.getResource("BTWOverzicht.jasper");
		if (url == null) {
			throw new RuntimeException("Kan resource BTWOverzicht.jasper niet vinden!");
		}
		String path = new File(url.getFile()).getParent();

		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("SUBREPORT_DIR", path);
		parameters.put(JRParameter.REPORT_LOCALE, new Locale("nl", "NL"));
		JasperReport report;

		parameters.put("Footer_datasource", getBedrijfsgegevens());
		try {
			report = (JasperReport) JRLoader.loadObject(url);
			List<FactuurTotaalInfo> somfacturen;
			somfacturen = getsomfacturen(datefrom, dateuntil);
			JasperPrint print = JasperFillManager.fillReport(report, parameters,
					new JRBeanCollectionDataSource(somfacturen));
			showPrintFrame(print);
		} catch (JRException e) {
			ExceptionLogger.ProcessException(e, this.getActiveForm());
        	return;
		}
	}
	
	protected String getPdfFilenaam(FactuurTotaalInfo factuur, String naam) {
		String filename = "";
		Integer Jaar, Maand;
		if (factuur != null) {
			Jaar = factuur.getJaar();
			Maand = factuur.getMaand();
			String factuurfolder = settings.getFactuurfolder();
			if (factuurfolder == null || factuurfolder.isEmpty())
				throw new RuntimeException("Geen folder om facturen op te slaan");
			filename = factuurfolder + "\\" + naam.trim() + "\\"
					+ Jaar.toString() + "\\";
			filename = filename + Maand.toString() + "-"
					+ factuur.getFactuurnr().toString().trim() + ".pdf";
		}
		return filename;
	}
	
	public String printFactuurToPDF(List<FactuurTotaalInfo> facturen, String naam) throws ValidationException {
		Map<String, Object> parameters = new HashMap<String, Object>();
		URL url = JRLoader.getResource("Factuur.jasper");
		if (url == null){
			throw new ValidationException("Kan resource Factuur.jasper niet vinden!");
		}
		String path = new File(url.getFile()).getParent();

		parameters.put("id", 42);
		parameters.put("SUBREPORT_DIR", path);
		parameters.put(JRParameter.REPORT_LOCALE, new Locale("nl", "NL"));
		JasperReport report;
		try {
			parameters.put("Footer_datasource", getBedrijfsgegevens());
			parameters.put("Huisbezoeken_datasource", null);
			parameters.put("Secretariaat_datasource", null);
			parameters.put("Items_datasource", null);

			report = (JasperReport) JRLoader.loadObject(url);
			JasperPrint print = JasperFillManager.fillReport(report,
					parameters, new JRBeanCollectionDataSource(facturen));

			String pdfFilename = getPdfFilenaam(facturen.get(0), naam);
			File pdfdir = new File(pdfFilename);
			pdfdir.getParentFile().mkdirs();

			JasperExportManager.exportReportToPdfFile(print, pdfFilename);
			return pdfFilename;
		} catch (JRException e) {
			ExceptionLogger.ProcessException(e, null, false);
		}
		return null;
	}

	private List<BedrijfsgegevensInfo> getBedrijfsgegevens() {
		List<BedrijfsgegevensInfo> bedrijfsgegevens = new ArrayList<>();
		InstantieModel instantiemodel = new InstantieModel(this.model.getSession());
		try {
			instantiemodel.selectBedrijfsgegevens();
		} catch (VerzuimApplicationException e) {
			ExceptionLogger.ProcessException(e, this.getActiveForm());
		}
		bedrijfsgegevens.add(instantiemodel.getBedrijfsgegevens());
		return bedrijfsgegevens;
	}

	public void printFactuur(FactuurTotaalInfo factuur) {
		try{
			URL url = JRLoader.getResource("Factuur.jasper"); 
			if (url == null){
				throw new ValidationException("Kan resource Factuur.jasper niet vinden!");
			}
			String path = new File(url.getFile()).getParent();
	
			Map<String,Object> parameters = new HashMap<String, Object>();
			parameters.put("SUBREPORT_DIR", path);
			parameters.put(JRParameter.REPORT_LOCALE, new Locale("nl","NL"));
			JasperReport report;
	
			parameters.put("Footer_datasource",getBedrijfsgegevens());
			parameters.put("Huisbezoeken_datasource", null);
			parameters.put("Secretariaat_datasource", null);
			parameters.put("Items_datasource", null);
			report = (JasperReport)JRLoader.loadObjectFromFile(url.getFile());
			List<FactuurTotaalInfo> facturen = new ArrayList<>();
	
			WerkgeverInfo werkgever = werkgevermodel.getWerkgeverDetails(factuur.getWerkgeverid());
			if (werkgever.getHoldingId() != null){
				HoldingInfo holding = werkgevermodel.getHoldingDetails(werkgever.getHoldingId());
				if (holding.isFactureren()){
					List<FactuurTotaalInfo> facturenholding = factuurmodel.getFacturenHoldingByFactuurnr(factuur.getFactuurnr());
					facturen.addAll(facturenholding);
				}else{
					facturen.add(factuur);
				}
			}else{
				facturen.add(factuur);
			}
			JasperPrint print = JasperFillManager.fillReport(report, parameters, new JRBeanCollectionDataSource(facturen));
			JFrame frame = new JFrame("Report");
			frame.getContentPane().add(new JRViewer(print));
			frame.pack();
			frame.setVisible(true);
		} catch (JRException e) {
			ExceptionLogger.ProcessException(e, null, false);
		} catch (VerzuimApplicationException e) {
			ExceptionLogger.ProcessException(e, null, false);
		} catch (ValidationException e) {
			ExceptionLogger.ProcessException(e, null, false);
		}
	}
	private void showPrintFrame(JasperPrint print) {
		JFrame frame = new JFrame("Report");
		JRViewer vwr = new JRViewer(print);
		vwr.setFitPageZoomRatio();
		frame.getContentPane().add(vwr);
		frame.setSize(1200, 800);
		frame.setVisible(true);
	}
	public void exporteerActueelverzuim(
		  Integer werkgeverid
		, Integer holdingid
		, Integer oeid
		, Date datefrom
		, Date dateuntil) throws ValidationException {
	int row;
	ActueelVerzuimInfo verzuim;
	ActueelVerzuimInfo currentverzuim;
	String verzuimtype;
	
		if (werkgeverid == -1 && holdingid == -1 && oeid == -1){
			throw new ValidationException("Geen bedrijf gekozen!");
		}
		if (dateuntil.before(datefrom)){
			throw new ValidationException("Einddatum ligt voor de startdatum!");
		}
		if (dateuntil.equals(datefrom)){
			throw new ValidationException("Einddatum is gelijk aan startdatum!");
		}
		if (dateuntil.after(new Date())){
			throw new ValidationException("Einddatum ligt in de toekomst!");
		}
		
		getResulsetsActueelverzuim(werkgeverid, holdingid, oeid, datefrom, dateuntil);
		
		// Vraag om naam excel bestand
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		String documentname = "Actueelverzuim " + df.format(datefrom) + "-" + df.format(dateuntil)	+ ".xls";

		File selectedFile = null;

		selectedFile = selectFilename(__selectfileoption.FILEONLY,__filedialogtype.SAVE, documentname, ".xls");
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
		
		//
        // Create an instance of HSSFWorkbook.
        //
        HSSFWorkbook workbook = new HSSFWorkbook();

        //
        // Create a sheet in the excel document and name it Blad 1
        //
        HSSFSheet sheet = workbook.createSheet("Blad 1");
        
        HSSFRow rowHeader = sheet.createRow(0);
        List<HSSFCell> cells = new ArrayList<>();
		int i=0;
		HSSFCell cell;

        cell = rowHeader.createCell(i);
        cell.setCellValue(new HSSFRichTextString("PERSONEELSNUMMER"));
        cells.add(cell);
		i++;

		cell = rowHeader.createCell(i);
        cell.setCellValue(new HSSFRichTextString("DATUM_BEGIN"));
        cells.add(cell);
		i++;
		
        cell = rowHeader.createCell(i);
        cell.setCellValue(new HSSFRichTextString("DATUM_EINDE"));
        cells.add(cell);
		i++;

        cell = rowHeader.createCell(i);
        cell.setCellValue(new HSSFRichTextString("SOORT_VERZUIM"));
        cells.add(cell);
		i++;
		
        cell = rowHeader.createCell(i);
        cell.setCellValue(new HSSFRichTextString("GEDEELTELIJK_VERZUIM"));
        cells.add(cell);
		i++;
		
        cell = rowHeader.createCell(i);
        cell.setCellValue(new HSSFRichTextString("PERCENTAGE_HERSTEL"));
        cells.add(cell);
		i++;

        cell = rowHeader.createCell(i);
        cell.setCellValue(new HSSFRichTextString("Uitkering"));
        cells.add(cell);
		i++;

		cell = rowHeader.createCell(i);
        cell.setCellValue(new HSSFRichTextString("ACHTERNAAM"));
        cells.add(cell);

        Hashtable<Integer,Integer> vzmditjaar = new Hashtable<>();
        for (WerknemerVerzuimInfo wvi: verzuimenditjaar){
        	if (vzmditjaar.containsKey(wvi.getWerknemerid())){
        	}else{
        		vzmditjaar.put(wvi.getWerknemerid(), wvi.getAantalverzuimen());
        	}
        }
        Hashtable<Integer,Integer> vzmafgjaar = new Hashtable<>();
        for (WerknemerVerzuimInfo wvi: verzuimen12maanden){
        	if (vzmafgjaar.containsKey(wvi.getWerknemerid())){
        	}else{
        		vzmafgjaar.put(wvi.getWerknemerid(), wvi.getAantalverzuimen());
        	}
        }
        
        int excelrow=0;
        row = 0;
        while (row < verzuimen.size())
		{
        	verzuim = verzuimen.get(row);
        	currentverzuim = verzuim;
        	/*
        	 * Doorspoelen van alle herstellen tot de laatste is gevonden
        	 */
        	do {
        		row++;
        		if (row < verzuimen.size())
        			verzuim = verzuimen.get(row);
        	}while (verzuim.getVerzuimid().equals(currentverzuim.getVerzuimid()) && row < verzuimen.size());
        	verzuim = currentverzuim;
        	
        	if (verzuim.getStartdatumverzuim() != null){
        		excelrow++;
	        	HSSFRow excelRow = sheet.createRow(excelrow);
				HSSFCell datacell;
				Object value;
				i=0;
				datacell = excelRow.createCell(i);
				value = verzuim.getPersoneelsnummer().trim();
				setValue(datacell,value);
	
				i++;
				datacell = excelRow.createCell(i);
				value = verzuim.getStartdatumverzuim();
				setValue(datacell,value);
	
				i++;
				datacell = excelRow.createCell(i);
				if (verzuim.getEinddatumverzuim() == null){
					value = "";
				}else{
					value = verzuim.getEinddatumverzuim();
				}
				setValue(datacell,value);
				
				i++;
				datacell = excelRow.createCell(i);
				switch (__vangnettype.parse(verzuim.getVangnettype())){
					case ORGAANDONATIE:
						verzuimtype = "ZOD";
						break;
					case ZIEKDOORZWANGERSCHAP:
						verzuimtype = "ZZW";
						break;
					case ZWANGERSCHAP:
						verzuimtype = "ZW";
						break;
					case PLEEGZORG:
						verzuimtype = "P";
						break;
					case ADOPTIE:
					case OVERIG:
					case WIA:
						verzuimtype = "O";
						break;
					default:
						switch (verzuim.getGerelateerdheid()){
							case ARBEID:
								verzuimtype = "B";
								break;
							case SPORT:
							case VERKEER:
								verzuimtype = "O";
								break;
							default:
								verzuimtype = "Z";
								break;
						}
						break;
				}
				value = verzuimtype;
				setValue(datacell,value);
	
				i++;
				datacell = excelRow.createCell(i);
				if (verzuim.getPercentageHerstel().compareTo(BigDecimal.ZERO) == 0){
					value = "N";
				}else{
					value = "J";
				}
				setValue(datacell,value);
				
				i++;
				datacell = excelRow.createCell(i);
				datacell.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
				HSSFCellStyle style = datacell.getCellStyle();
				style.setDataFormat(HSSFDataFormat.getBuiltinFormat("#,##0.00"));
				value = verzuim.getPercentageHerstel().setScale(2);
				setValue(datacell,value);
				
				i++;
				datacell = excelRow.createCell(i);
				if (verzuim.isUitkeringnaarwerknemer()){
					value = "Wordt rechtstreeks via het UWV uitgekeerd";
				}else{
					value = "";
				}
				setValue(datacell,value);

				i++;
				datacell = excelRow.createCell(i);
				value = verzuim.getAchternaam();
				setValue(datacell,value);
        	}
		}
		

		FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(new File(documentname));
            workbook.write(fos);
        } catch (IOException ex) {
        	ExceptionLogger.ProcessException(ex,null);
        } finally {
            if (fos != null) {
                try {
                    fos.flush();
                    fos.close();
                } catch (IOException ex) {
                	ExceptionLogger.ProcessException(ex,null);
                }
            }
        }
        JOptionPane.showMessageDialog(null, "Exporteren gereed");
	}
	private void setValue(HSSFCell datacell, Object value) {
		if (value == null)
			datacell.setCellValue("");
		else
		if (value instanceof Date){
        	String strDate = new SimpleDateFormat("dd-MM-yyyy").format((Date)value);
			datacell.setCellValue(strDate);
        }
		else
			datacell.setCellValue(value.toString());
	
    }

	public List<WerknemerFastInfo> getWerknemerList() {
		return werknemermodel.getActiveWerknemersFast();
	}

	public void selectWerknemers(int werkgeverid) {
		try {
			werknemermodel.getWerknemers(werkgeverid);
		} catch (VerzuimApplicationException e) {
			ExceptionLogger.ProcessException(e, this.getActiveForm());
		}
	}
	private List<FactuurTotaalInfo> getsomfacturen(Date startdate, Date enddate){
		List<FactuurTotaalInfo> allfacturen;
		try {
			factuurmodel.selectFacturen(startdate, startdate);
			allfacturen = factuurmodel.getFacturenList(); 
		} catch (VerzuimApplicationException e2) {
			ExceptionLogger.ProcessException(e2, null);
			return null;
		}
		return allfacturen;
	}	
	public void exportBTWenOmzet(Date startdate, Date enddate) {
		BigDecimal somOmzetBTWHoog;

		List<FactuurTotaalInfo> somfacturen;
		somfacturen = getsomfacturen(startdate, enddate);

		// Vraag om naam excel bestand
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM");
		String documentname = "Facturen " + df.format(startdate) + "-" + df.format(enddate)	+ ".xls";

		File selectedFile = null;

		selectedFile = selectFilename(__selectfileoption.FILEONLY,__filedialogtype.SAVE,documentname,".xls");
		if (selectedFile == null)
			return;
		else {
			if (selectedFile.exists()) {
				if (JOptionPane.showConfirmDialog(null,
						"Bestand " + selectedFile.getAbsolutePath() + " bestaat al. Wilt u het verwijderen?",
						"Verwijderen", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
					if (!selectedFile.delete()) {
						JOptionPane.showMessageDialog(null, "Kan bestand niet verwijderen.");
						return;
					}
				} else
					return;
			}
			documentname = selectedFile.getAbsolutePath();
		}

		//
		// Create an instance of HSSFWorkbook.
		//
		HSSFWorkbook workbook = new HSSFWorkbook();

		//
		// Create a sheet in the excel document and name it Blad 1
		//
		HSSFSheet Sheet = workbook.createSheet("Facturen");

		HSSFRow rowHeader = Sheet.createRow(0);
		List<HSSFCell> cells = new ArrayList<>();
		int i = 0;
		HSSFCell cell;

		cell = rowHeader.createCell(i);
		cell.setCellValue(new HSSFRichTextString("DEBITEURNUMMER"));
		cells.add(cell);
		i++;
		cell = rowHeader.createCell(i);
		cell.setCellValue(new HSSFRichTextString("BEDRIJFSNAAM"));
		cells.add(cell);
		i++;
		HSSFCell cellC = rowHeader.createCell(i);
		cellC.setCellValue(new HSSFRichTextString("FACTUURNUMMER"));
		cells.add(cell);
		i++;
		cell = rowHeader.createCell(i);
		cell.setCellValue(new HSSFRichTextString("JAAR"));
		cells.add(cell);
		i++;
		cell = rowHeader.createCell(i);
		cell.setCellValue(new HSSFRichTextString("MAAND"));
		cells.add(cell);
		i++;
		cell = rowHeader.createCell(i);
		cell.setCellValue(new HSSFRichTextString("FACTUURBEDRAG_EX_BTW"));
		cells.add(cell);
		i++;
		cell = rowHeader.createCell(i);
		cell.setCellValue(new HSSFRichTextString("OMZETONBELAST"));
		cells.add(cell);
		i++;
		cell = rowHeader.createCell(i);
		cell.setCellValue(new HSSFRichTextString("OMZET_6%_BEDRAG"));
		cells.add(cell);
		i++;
		cell = rowHeader.createCell(i);
		cell.setCellValue(new HSSFRichTextString("OMZET_6%_BTWBEDRAG"));
		cells.add(cell);
		i++;
		cell = rowHeader.createCell(i);
		cell.setCellValue(new HSSFRichTextString("OMZET_21%_BEDRAG"));
		cells.add(cell);
		i++;
		cell = rowHeader.createCell(i);
		cell.setCellValue(new HSSFRichTextString("OMZET_21%_BTWBEDRAG"));
		cells.add(cell);
		i++;
		cell = rowHeader.createCell(i);
		cell.setCellValue(new HSSFRichTextString("DOORLOPENDEPOSTENONBELAST"));
		cells.add(cell);
		i++;
		cell = rowHeader.createCell(i);
		cell.setCellValue(new HSSFRichTextString("DOORLOPENDEPOSTEN_6%_BEDRAG"));
		cells.add(cell);
		i++;
		cell = rowHeader.createCell(i);
		cell.setCellValue(new HSSFRichTextString("DOORLOPENDEPOSTEN_6%_BTWBEDRAG"));
		cells.add(cell);
		i++;
		cell = rowHeader.createCell(i);
		cell.setCellValue(new HSSFRichTextString("DOORLOPENDEPOSTEN_21%_BEDRAG"));
		cells.add(cell);
		i++;
		cell = rowHeader.createCell(i);
		cell.setCellValue(new HSSFRichTextString("DOORLOPENDEPOSTEN_21%_BTWBEDRAG"));
		cells.add(cell);
		i++;

		int excelrow = 0;
		for (int row = 0; row < somfacturen.size(); row++) {
			FactuurTotaalInfo factuur = somfacturen.get(row);
			excelrow++;
			HSSFRow excelRow = Sheet.createRow(excelrow);
			HSSFCell datacell;
			Object value;

			i = 0;
			datacell = excelRow.createCell(i);
			if (factuur.isGesommeerd())
				value = (Object) factuur.getWerkgever().getHolding().getDebiteurnummer();
			else
				if (factuur.getWerkgever() != null){
					value = (Object) factuur.getWerkgever().getDebiteurnummer();
				}else{
					value = "";
				}
			setValue(datacell, value);

			i++;
			datacell = excelRow.createCell(i);
			if (factuur.isGesommeerd())
				value = (Object) factuur.getWerkgever().getHolding().getNaam();
			else
				if (factuur.getWerkgever() != null){
					value = (Object) factuur.getWerkgever().getNaam();
				}else{
					value = (Object) factuur.getWerkgever().getNaam();
				}
			setValue(datacell, value);

			i++;
			datacell = excelRow.createCell(i);
			value = (Object) factuur.getFactuurnr();
			setValue(datacell, value);

			i++;
			datacell = excelRow.createCell(i);
			value = (Object) factuur.getJaar();
			setValue(datacell, value);

			i++;
			datacell = excelRow.createCell(i);
			value = (Object) factuur.getMaand();
			setValue(datacell, value);

			i++;
			datacell = excelRow.createCell(i);
			value = (Object) formatBigDecimal(factuur.getTotaalExBtw(), 2);
			setValue(datacell, value);

			i++;
			datacell = excelRow.createCell(i);
			value = (Object) formatBigDecimal(factuur.getSomOmzetOnbelast(), 2);
			setValue(datacell, value);

			i++;
			datacell = excelRow.createCell(i);
			value = (Object) formatBigDecimal(factuur.getSomOmzetBTWLaag(), 2);
			setValue(datacell, value);

			i++;
			datacell = excelRow.createCell(i);
			value = (Object) formatBigDecimal(
					factuur.getSomOmzetBTWLaag().multiply(factuur.getBtwpercentagelaag().divide(new BigDecimal(100))),
					2);
			setValue(datacell, value);

			i++;
			datacell = excelRow.createCell(i);
			somOmzetBTWHoog = new BigDecimal(0);
			somOmzetBTWHoog = somOmzetBTWHoog.add(
					factuur.getSomOmzetBTWHoog().add(factuur.getTotaalExBtw().subtract(factuur.getSomitembedrag())));
			value = (Object) formatBigDecimal(somOmzetBTWHoog, 2);
			setValue(datacell, value);

			i++;
			datacell = excelRow.createCell(i);
			value = (Object) formatBigDecimal(
					somOmzetBTWHoog.multiply(factuur.getBtwpercentagehoog().divide(new BigDecimal(100))), 2);
			setValue(datacell, value);

			i++;
			datacell = excelRow.createCell(i);
			value = (Object) formatBigDecimal(factuur.getSomGeenomzetOnbelast(), 2);
			setValue(datacell, value);

			i++;
			datacell = excelRow.createCell(i);
			value = (Object) formatBigDecimal(factuur.getSomGeenomzetBTWLaag(), 2);
			setValue(datacell, value);

			i++;
			datacell = excelRow.createCell(i);
			value = (Object) formatBigDecimal(factuur.getSomGeenomzetBTWLaag()
					.multiply(factuur.getBtwpercentagelaag().divide(new BigDecimal(100))), 2);
			setValue(datacell, value);

			i++;
			datacell = excelRow.createCell(i);
			value = (Object) formatBigDecimal(factuur.getSomGeenomzetBTWHoog(), 2);
			setValue(datacell, value);

			i++;
			datacell = excelRow.createCell(i);
			value = (Object) formatBigDecimal(factuur.getSomGeenomzetBTWHoog()
					.multiply(factuur.getBtwpercentagehoog().divide(new BigDecimal(100))), 2);
			setValue(datacell, value);
		}

		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream(new File(documentname));
			workbook.write(fos);
		} catch (IOException ex) {
			ExceptionLogger.ProcessException(ex, null);
		} finally {
			if (fos != null) {
				try {
					fos.flush();
					fos.close();
				} catch (IOException ex) {
					ExceptionLogger.ProcessException(ex, null);
				}
			}
		}
		JOptionPane.showMessageDialog(null, "Exporteren gereed");
	}
	protected String formatBigDecimal(BigDecimal bd, int precision) {
		bd = bd.setScale(precision, BigDecimal.ROUND_HALF_UP);
		DecimalFormat df = new DecimalFormat();
		df.setMaximumFractionDigits(precision);
		df.setMinimumFractionDigits(precision);
		df.setGroupingUsed(false);
		return df.format(bd);
	}


}
