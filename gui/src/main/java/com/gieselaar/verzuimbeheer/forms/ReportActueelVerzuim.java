package com.gieselaar.verzuimbeheer.forms;

import javax.swing.JOptionPane;

import com.gieselaar.verzuimbeheer.baseforms.BaseDetailform;
import com.gieselaar.verzuimbeheer.baseforms.JDesktopPaneTGI;
import com.gieselaar.verzuimbeheer.exceptions.VerzuimApplicationException;
import com.gieselaar.verzuimbeheer.exceptions.PermissionException;
import com.gieselaar.verzuimbeheer.exceptions.ValidationException;
import com.gieselaar.verzuimbeheer.reportservices.ActueelVerzuimInfo;
import com.gieselaar.verzuimbeheer.reportservices.WerknemerVerzuimInfo;
import com.gieselaar.verzuimbeheer.services.HoldingInfo;
import com.gieselaar.verzuimbeheer.services.OeInfo;
import com.gieselaar.verzuimbeheer.services.VerzuimInfo.__vangnettype;
import com.gieselaar.verzuimbeheer.services.WerkgeverInfo;
import com.gieselaar.verzuimbeheer.utils.CursorController;
import com.gieselaar.verzuimbeheer.utils.ExceptionLogger;
import com.gieselaar.verzuimbeheer.utils.ServiceCaller;
import com.gieselaar.verzuimbeheer.utils.ServiceLocatorException;
import com.gieselaar.verzuimbeheer.utils.TypeEntry;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JComboBox;
import javax.swing.DefaultComboBoxModel;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;

import com.michaelbaranov.microba.calendar.DatePicker;

import javax.swing.JCheckBox;
import javax.swing.JButton;

import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.swing.JRViewer;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFDataFormat;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import reportdatasources.ActueelverzuimDatasource;

public class ReportActueelVerzuim extends BaseDetailform  {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2862612204474930868L;
	private List<WerkgeverInfo> werkgevers;
	private List<HoldingInfo> holdings;
	private List<OeInfo> oes;
	private JComboBox<TypeEntry> cmbWerkgever = new JComboBox<TypeEntry>();
	private DatePicker dtpEinddatum;
	private DatePicker dtpStartdatum;
	private JComboBox<TypeEntry> cmbHolding = new JComboBox<TypeEntry>();
	private JComboBox<TypeEntry> cmbOe = new JComboBox<TypeEntry>(); 
	private JCheckBox chckbxRedenVerzuimTonen;
	private List<ActueelVerzuimInfo> verzuimen;
	private List<WerknemerVerzuimInfo> verzuimenditjaar;
	private List<WerknemerVerzuimInfo> verzuimen12maanden;
	private String selectedbedrijfsnaam = "";
	private Integer selectedwerkgeverid = -1;
	private Integer selectedholdingid = -1;
	private Integer selectedoeid = -1;
	
	/**
	 * Getters and setters of this dialog
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void setInfo(){
		try {
			werkgevers = ServiceCaller.werkgeverFacade(getLoginSession()).allWerkgeversList();
			holdings = ServiceCaller.werkgeverFacade(getLoginSession()).getHoldings();
			oes = ServiceCaller.werkgeverFacade(getLoginSession()).getOes();
		} catch (ServiceLocatorException | PermissionException e) {
        	ExceptionLogger.ProcessException(e,this,"Unable to connect to server");
        	return;
		} catch (VerzuimApplicationException e) {
        	ExceptionLogger.ProcessException(e,this);
        	return;
		} catch (Exception e1){
        	ExceptionLogger.ProcessException(e1,this);
        	return;
		}
		
		WerkgeverInfo.sort(werkgevers, WerkgeverInfo.__sortcol.NAAM);
		
		DefaultComboBoxModel soortmodelWG = new DefaultComboBoxModel();
		DefaultComboBoxModel soortmodelHD = new DefaultComboBoxModel();
		DefaultComboBoxModel soortmodelOE = new DefaultComboBoxModel();

        cmbWerkgever.setModel(soortmodelWG);
        cmbHolding.setModel(soortmodelHD);
        cmbOe.setModel(soortmodelOE);
		TypeEntry dummy = new TypeEntry(-1,"[geen]");
		soortmodelWG.addElement(dummy);
		soortmodelHD.addElement(dummy);
		soortmodelOE.addElement(dummy);
        for (WerkgeverInfo wgr: werkgevers)
		{
			TypeEntry entry = new TypeEntry(wgr.getId(), wgr.getNaam());
			soortmodelWG.addElement(entry);
		}
        HoldingInfo.sort(holdings, HoldingInfo.__sortcol.NAAM);
		for (HoldingInfo a: holdings)
		{
			TypeEntry act = new TypeEntry(a.getId(), a.getNaam());
			soortmodelHD.addElement(act);
		}
		for (OeInfo o: oes)
		{
			TypeEntry oe = new TypeEntry(o.getId(), o.getNaam());
			soortmodelOE.addElement(oe);
		}
        activateListener();
	}
	/**
	 * Create the frame.
	 */
	public ReportActueelVerzuim(JDesktopPaneTGI mdiPanel) {
		super("rapport Actueel Verzuim", mdiPanel);
		setTitle("Rapport Actueelverzuim");
		initialize();
	}
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void initialize(){
		setBounds(100, 100, 526, 312);
		getContentPane().setLayout(null);
		
		JLabel lblNaam = new JLabel("Werkgever");
		lblNaam.setBounds(30, 47, 84, 14);
		getContentPane().add(lblNaam);
		
		cmbWerkgever = new JComboBox();
		cmbWerkgever.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (cmbWerkgever.getSelectedIndex() != 0){
					cmbHolding.setSelectedIndex(0);
					cmbOe.setSelectedIndex(0);
				}
			}
		});
		cmbWerkgever.setBounds(171, 44, 256, 20);
		getContentPane().add(cmbWerkgever);
		
		JLabel lblHolding = new JLabel("Holding");
		lblHolding.setBounds(30, 22, 84, 14);
		getContentPane().add(lblHolding);
		
		cmbHolding = new JComboBox();
		cmbHolding.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (cmbHolding.getSelectedIndex() != 0){
					cmbWerkgever.setSelectedIndex(0);
					cmbOe.setSelectedIndex(0);
				}
			}
		});
		cmbHolding.setBounds(171, 19, 256, 20);
		getContentPane().add(cmbHolding);
		
		dtpStartdatum = new DatePicker();
		dtpStartdatum.setBounds(172, 97, 87, 21);
		dtpStartdatum.setDateFormat(new SimpleDateFormat("dd-MM-yyyy"));
		getContentPane().add(dtpStartdatum);
		
		dtpEinddatum = new DatePicker();
		dtpEinddatum.setBounds(172, 121, 87, 21);
		dtpEinddatum.setDateFormat(new SimpleDateFormat("dd-MM-yyyy"));
		getContentPane().add(dtpEinddatum);
		
		JLabel lblVan = new JLabel("Van");
		lblVan.setBounds(31, 100, 84, 14);
		getContentPane().add(lblVan);
		
		JLabel lblTot = new JLabel("Tot en met");
		lblTot.setBounds(31, 124, 84, 14);
		getContentPane().add(lblTot);
		
		cmbOe.setBounds(171, 69, 256, 20);
		cmbOe.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (cmbOe.getSelectedIndex() != 0){
					cmbWerkgever.setSelectedIndex(0);
					cmbHolding.setSelectedIndex(0);
				}
			}
		});
		getContentPane().add(cmbOe);
		
		JLabel lblArea = new JLabel("Area");
		lblArea.setBounds(30, 72, 84, 14);
		getContentPane().add(lblArea);
		
		chckbxRedenVerzuimTonen = new JCheckBox("Reden verzuim tonen");
		chckbxRedenVerzuimTonen.setSelected(true);
		chckbxRedenVerzuimTonen.setBounds(30, 157, 148, 23);
		getContentPane().add(chckbxRedenVerzuimTonen);
		
		JButton btnExporteren = new JButton("Exporteren...");
		btnExporteren.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				btnExporterenClicked(e);
			}
		});
		btnExporteren.setBounds(281, 173, 110, 23);
		getContentPane().add(btnExporteren);
		
		JButton btnAfdrukken = new JButton("Afdrukken...");
		btnAfdrukken.addActionListener(CursorController.createListener(this,new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				afdrukkenClicked(e);
			}
		}));
		
		btnAfdrukken.setBounds(281, 196, 110, 23);
		getContentPane().add(btnAfdrukken);
		
	}
	
	protected void afdrukkenClicked(ActionEvent e) {
		if (!FormChecksOk())
			return;
		
		if (this.getLoginSession() != null)
        {
			getResulsets(selectedwerkgeverid, selectedholdingid, selectedoeid, dtpStartdatum.getDate(), dtpEinddatum.getDate());
        	ActueelverzuimDatasource ds = new ActueelverzuimDatasource(dtpStartdatum.getDate(), dtpEinddatum.getDate(), this.getLoginSession());
			JasperPrint print;
			try {
				print = ds.getReport(selectedwerkgeverid, selectedholdingid, selectedoeid, selectedbedrijfsnaam, chckbxRedenVerzuimTonen.isSelected());
				JFrame frame = new JFrame("Report");
				JRViewer vwr = new JRViewer(print);
				vwr.setFitPageZoomRatio();
				frame.getContentPane().add(vwr);
				frame.setSize(1200, 800);
				frame.setVisible(true);
			} catch (ValidationException e1) {
				ExceptionLogger.ProcessException(e1, this);
				return;
			}
        }
        else
        	JOptionPane.showMessageDialog(this,"Logic error: loginSession is null");
	}
	private void getResulsets(Integer werkgeverid, Integer holdingid, Integer oeid, Date startdatum, Date eindatum)
	{
    	try {
			verzuimen = ServiceCaller.reportFacade(getLoginSession()).getActueelVerzuim(werkgeverid, holdingid, oeid, dtpStartdatum.getDate(), dtpEinddatum.getDate(),true);
			Calendar cal = Calendar.getInstance();
			cal.setTime(dtpEinddatum.getDate());
			cal.set(Calendar.MONTH,0);
			cal.set(Calendar.DATE,1);
			verzuimenditjaar = ServiceCaller.reportFacade(getLoginSession()).getWerknemerVerzuimen(werkgeverid, holdingid, oeid, cal.getTime(), dtpEinddatum.getDate(),0);
			cal.setTime(dtpEinddatum.getDate());
			cal.add(Calendar.MONTH,-12);
			verzuimen12maanden = ServiceCaller.reportFacade(getLoginSession()).getWerknemerVerzuimen(werkgeverid, holdingid, oeid, cal.getTime(), dtpEinddatum.getDate(),0);
		} catch (PermissionException e1) {
			ExceptionLogger.ProcessException(e1,this);
		} catch (VerzuimApplicationException e1) {
        	ExceptionLogger.ProcessException(e1,this);
		} catch (Exception e1){
        	ExceptionLogger.ProcessException(e1,this);
		}
		
	}
	protected void btnExporterenClickedOld(ActionEvent e) {
	Date einddatum;
	int aantaldagenziek;
	
		if (!FormChecksOk())
			return;

		getResulsets(selectedwerkgeverid, selectedholdingid, selectedoeid, dtpStartdatum.getDate(), dtpEinddatum.getDate());
		
		// Vraag om naam excel bestand
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM");
		String documentname = "Actueelverzuim " + df.format(dtpStartdatum.getDate()) + "-" + df.format(dtpEinddatum.getDate())
		+ ".xls";

		File selectedFile = null;

		selectedFile = SelectFilename(documentname);
		if (selectedFile == null)
			return;
		else {
			if (selectedFile.exists()){
				if (JOptionPane.showConfirmDialog(this, "Bestand "
						+ selectedFile.getAbsolutePath()
						+ " bestaat al. Wilt u het verwijderen?", "Verwijderen",
						JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION){
					if (!selectedFile.delete()){
						JOptionPane.showMessageDialog(this, "Kan bestand niet verwijderen.");
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
        HSSFSheet Sheet = workbook.createSheet("Blad 1");
        
        HSSFRow rowHeader = Sheet.createRow(0);
        List<HSSFCell> cells = new ArrayList<>();
		int i=0;
		HSSFCell cell;

		cell = rowHeader.createCell(i);
        cell.setCellValue(new HSSFRichTextString("NAAM"));
        cells.add(cell);
		i++;
        cell = rowHeader.createCell(i);
        cell.setCellValue(new HSSFRichTextString("ACHTERNAAM"));
        cells.add(cell);
		i++;
        HSSFCell cellC = rowHeader.createCell(i);
        cellC.setCellValue(new HSSFRichTextString("VOORVOEGSEL"));
        cells.add(cell);
		i++;
        cell = rowHeader.createCell(i);
        cell.setCellValue(new HSSFRichTextString("VOORNAAM"));
        cells.add(cell);
		i++;
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
        cell.setCellValue(new HSSFRichTextString("DATUM_HERSTEL"));
        cells.add(cell);
		i++;
        cell = rowHeader.createCell(i);
        cell.setCellValue(new HSSFRichTextString("PERCENTAGE_HERSTEL"));
        cells.add(cell);
		i++;
        cell = rowHeader.createCell(i);
        cell.setCellValue(new HSSFRichTextString("PERCENTAGE_HERSTEL_AT"));
        cells.add(cell);
		i++;
        cell = rowHeader.createCell(i);
        cell.setCellValue(new HSSFRichTextString("AantalDagenZiek"));
        cells.add(cell);
		i++;
        cell = rowHeader.createCell(i);
        cell.setCellValue(new HSSFRichTextString("OMSCHRIJVING"));
        cells.add(cell);
		i++;
        cell = rowHeader.createCell(i);
        cell.setCellValue(new HSSFRichTextString("VANGNET_TYPE"));
        cells.add(cell);
		i++;
        cell = rowHeader.createCell(i);
        cell.setCellValue(new HSSFRichTextString("AantalDitJaar"));
        cells.add(cell);
		i++;
        cell = rowHeader.createCell(i);
        cell.setCellValue(new HSSFRichTextString("AantalAfgelopenJaar"));
        cells.add(cell);
		i++;
        cell = rowHeader.createCell(i);
        cell.setCellValue(new HSSFRichTextString("Uitkering"));
        cells.add(cell);

        Hashtable<Integer,Integer> vzmditjaar = new Hashtable<>();
        for (WerknemerVerzuimInfo wvi: verzuimen12maanden){
        	if (vzmditjaar.containsKey(wvi.getWerknemerid()))
        		;
        	else
        		vzmditjaar.put(wvi.getWerknemerid(), wvi.getAantalverzuimen());
        }
        Hashtable<Integer,Integer> vzmafgjaar = new Hashtable<>();
        for (WerknemerVerzuimInfo wvi: verzuimen12maanden){
        	if (vzmafgjaar.containsKey(wvi.getWerknemerid()))
        		;
        	else
        		vzmafgjaar.put(wvi.getWerknemerid(), wvi.getAantalverzuimen());
        }
        
        int excelrow=0;
        for (int row=0;row<verzuimen.size();row++)
		{
        	ActueelVerzuimInfo verzuim = verzuimen.get(row);
        	if (verzuim.getStartdatumverzuim() != null){
        		excelrow++;
	        	HSSFRow excelRow = Sheet.createRow(excelrow);
				HSSFCell datacell;
				Object value;
				i=0;
				datacell = excelRow.createCell(i);
				value = (Object)verzuim.getWerkgevernaam();
				setValue(datacell,value);
	
				i++;
				datacell = excelRow.createCell(i);
				value = (Object)verzuim.getAchternaam();
				setValue(datacell,value);
				
				i++;
				datacell = excelRow.createCell(i);
				value = (Object)verzuim.getVoorvoegsel();
				setValue(datacell,value);
	
				i++;
				datacell = excelRow.createCell(i);
				value = (Object)verzuim.getVoornaam();
				setValue(datacell,value);
				
				i++;
				datacell = excelRow.createCell(i);
				value = (Object)verzuim.getPersoneelsnummer();
				setValue(datacell,value);
	
				i++;
				datacell = excelRow.createCell(i);
				value = (Object)verzuim.getStartdatumverzuim();
				setValue(datacell,value);
	
				i++;
				datacell = excelRow.createCell(i);
				value = (Object)verzuim.getEinddatumverzuim();
				setValue(datacell,value);
				
				i++;
				datacell = excelRow.createCell(i);
				value = (Object)verzuim.getDatumHerstel();
				setValue(datacell,value);
	
				i++;
				datacell = excelRow.createCell(i);
				value = (Object)verzuim.getPercentageHerstel();
				setValue(datacell,value);
				
				i++;
				datacell = excelRow.createCell(i);
				value = (Object)verzuim.getPercentageHerstelAt();
				setValue(datacell,value);
				
				i++;
				datacell = excelRow.createCell(i);
				einddatum = verzuim.getEinddatumverzuim();
				if (einddatum == null)
					einddatum = verzuim.getEindperiode();
				aantaldagenziek = (int) ((einddatum.getTime() - verzuim.getStartdatumverzuim().getTime())/1000L/60L/60L/24L);
				if (verzuim.getEinddatumverzuim() == null)
					aantaldagenziek = aantaldagenziek + 1;
				setValue(datacell,aantaldagenziek);
	
				i++;
				datacell = excelRow.createCell(i);
				value = (Object)verzuim.getCascodeomschrijving();
				setValue(datacell,value);
				
				i++;
				datacell = excelRow.createCell(i);
				if (chckbxRedenVerzuimTonen.isSelected()){
					value = (Object)verzuim.getVangnettypeomschrijving();
				}else{
					value = "";
				}
				setValue(datacell,value);
	
				i++;
				datacell = excelRow.createCell(i);
				value = (Object)(vzmditjaar.get(verzuim.getWerknemerid()));
				setValue(datacell,value);
	
				i++;
				datacell = excelRow.createCell(i);
				value = (Object)(vzmditjaar.get(verzuim.getWerknemerid()));
				setValue(datacell,value);

				i++;
				datacell = excelRow.createCell(i);
				if (verzuim.isUitkeringnaarwerknemer()){
					value = (Object)("Wordt rechtstreeks via het UWV uitgekeerd");
				}else{
					value = (Object)("");
				}
				setValue(datacell,value);
//	        	if (verzuimChangedThisPeriod(verzuim)){
//	        		HSSFCellStyle style = excelRow.getRowStyle();
//	        		if (style == null){
//	        			style = workbook.createCellStyle();
//	        		}
//	        		style.setFillForegroundColor(HSSFColor.YELLOW.index);
//	        		style.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
//	        		excelRow.setRowStyle(style);
//	        	}
        	}
		}
		

		FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(new File(documentname));
            workbook.write(fos);
        } catch (IOException ex) {
        	ExceptionLogger.ProcessException(ex,this);
        } finally {
            if (fos != null) {
                try {
                    fos.flush();
                    fos.close();
                } catch (IOException ex) {
                	ExceptionLogger.ProcessException(ex,this);
                }
            }
        }
        JOptionPane.showMessageDialog(this, "Exporteren gereed");
	}

	protected void btnExporterenClicked(ActionEvent e) {
	int row;
	ActueelVerzuimInfo verzuim;
	ActueelVerzuimInfo currentverzuim;
	String verzuimtype;
	
		if (!FormChecksOk())
			return;

		getResulsets(selectedwerkgeverid, selectedholdingid, selectedoeid, dtpStartdatum.getDate(), dtpEinddatum.getDate());
		
		// Vraag om naam excel bestand
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		String documentname = "Actueelverzuim " + df.format(dtpStartdatum.getDate()) + "-" + df.format(dtpEinddatum.getDate())
		+ ".xls";

		File selectedFile = null;

		selectedFile = SelectFilename(documentname);
		if (selectedFile == null)
			return;
		else {
			if (selectedFile.exists()){
				if (JOptionPane.showConfirmDialog(this, "Bestand "
						+ selectedFile.getAbsolutePath()
						+ " bestaat al. Wilt u het verwijderen?", "Verwijderen",
						JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION){
					if (!selectedFile.delete()){
						JOptionPane.showMessageDialog(this, "Kan bestand niet verwijderen.");
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
        HSSFSheet Sheet = workbook.createSheet("Blad 1");
        
        HSSFRow rowHeader = Sheet.createRow(0);
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
        for (WerknemerVerzuimInfo wvi: verzuimen12maanden){
        	if (vzmditjaar.containsKey(wvi.getWerknemerid()))
        		;
        	else
        		vzmditjaar.put(wvi.getWerknemerid(), wvi.getAantalverzuimen());
        }
        Hashtable<Integer,Integer> vzmafgjaar = new Hashtable<>();
        for (WerknemerVerzuimInfo wvi: verzuimen12maanden){
        	if (vzmafgjaar.containsKey(wvi.getWerknemerid()))
        		;
        	else
        		vzmafgjaar.put(wvi.getWerknemerid(), wvi.getAantalverzuimen());
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
	        	HSSFRow excelRow = Sheet.createRow(excelrow);
				HSSFCell datacell;
				Object value;
				i=0;
				datacell = excelRow.createCell(i);
				value = (Object)verzuim.getPersoneelsnummer().trim();
				setValue(datacell,value);
	
				i++;
				datacell = excelRow.createCell(i);
				value = (Object)verzuim.getStartdatumverzuim();
				setValue(datacell,value);
	
				i++;
				datacell = excelRow.createCell(i);
				if (verzuim.getEinddatumverzuim() == null){
					value = "";
				}else{
					value = (Object)verzuim.getEinddatumverzuim();
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
						verzuimtype = "O";
						break;
					case OVERIG:
						verzuimtype = "O";
						break;
					case WIA:
						verzuimtype = "O";
						break;
					default:
						switch (verzuim.getGerelateerdheid()){
							case ARBEID:
								verzuimtype = "B";
								break;
							case NVT:
								verzuimtype = "Z";
								break;
							case SPORT:
								verzuimtype = "O";
								break;
							case VERKEER:
								verzuimtype = "O";
								break;
							default:
								verzuimtype = "Z";
								break;
						}
						break;
				}
				value = (Object)verzuimtype;
				setValue(datacell,value);
	
				i++;
				datacell = excelRow.createCell(i);
				if (verzuim.getPercentageHerstel().compareTo(BigDecimal.ZERO) == 0){
					value = (Object)"N";
				}else{
					value = (Object)"J";
				}
				setValue(datacell,value);
				
				i++;
				datacell = excelRow.createCell(i);
				datacell.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
				HSSFCellStyle style = datacell.getCellStyle();
				style.setDataFormat(HSSFDataFormat.getBuiltinFormat("#,##0.00"));
				value = (Object)verzuim.getPercentageHerstel().setScale(2);
				setValue(datacell,value);
				
				i++;
				datacell = excelRow.createCell(i);
				if (verzuim.isUitkeringnaarwerknemer()){
					value = (Object)("Wordt rechtstreeks via het UWV uitgekeerd");
				}else{
					value = (Object)("");
				}
				setValue(datacell,value);

				i++;
				datacell = excelRow.createCell(i);
				value = (Object)verzuim.getAchternaam();
				setValue(datacell,value);
        	}
		}
		

		FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(new File(documentname));
            workbook.write(fos);
        } catch (IOException ex) {
        	ExceptionLogger.ProcessException(ex,this);
        } finally {
            if (fos != null) {
                try {
                    fos.flush();
                    fos.close();
                } catch (IOException ex) {
                	ExceptionLogger.ProcessException(ex,this);
                }
            }
        }
        JOptionPane.showMessageDialog(this, "Exporteren gereed");
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
	private File SelectFilename(String proposedfilename) {
		File selectedfile;
		JFileChooser fd = new JFileChooser();

		fd.setDialogType(JFileChooser.SAVE_DIALOG);
		fd.setFileSelectionMode(JFileChooser.FILES_ONLY);
		fd.setSelectedFile(new File(proposedfilename));
		int retval = fd.showSaveDialog(this);
		if (retval == JFileChooser.APPROVE_OPTION) {
			selectedfile = fd.getSelectedFile();
			String filename = selectedfile.getAbsolutePath();
			if (filename.endsWith(".xls"))
				return selectedfile;
			else {
				return new File(filename + ".xls");
			}
		} else
			return null;

	}

	protected void cancelButtonClicked(ActionEvent e) {
		this.setDatachanged(false);
		super.cancelButtonClicked(e);
	}
	protected void okButtonClicked(ActionEvent e) {
		super.okButtonClicked(e);
	}
	private boolean FormChecksOk() {
		TypeEntry entry;
		entry = (TypeEntry) cmbWerkgever.getSelectedItem();
		selectedwerkgeverid = entry.getValue();
		if (selectedwerkgeverid != -1)
			selectedbedrijfsnaam = entry.toString();
		entry = (TypeEntry) cmbHolding.getSelectedItem();
		selectedholdingid = entry.getValue();
		if (selectedholdingid != -1)
			selectedbedrijfsnaam = entry.toString();
		entry = (TypeEntry) cmbOe.getSelectedItem();
		selectedoeid = entry.getValue();
		if (selectedoeid != -1)
			selectedbedrijfsnaam = entry.toString();
		
		
		if (selectedwerkgeverid == -1 && selectedholdingid == -1 && selectedoeid == -1){
        	JOptionPane.showMessageDialog(this,"Geen bedrijf gekozen!");
        	return false;
		}
		
		if (dtpEinddatum.getDate().before(dtpStartdatum.getDate())){
        	JOptionPane.showMessageDialog(this,"Einddatum ligt voor de startdatum!");
        	return false;
		}
		if (dtpEinddatum.getDate().equals(dtpStartdatum.getDate())){
        	JOptionPane.showMessageDialog(this,"Einddatum is gelijk aan startdatum!");
        	return false;
		}
		if (dtpEinddatum.getDate().after(new Date())){
        	JOptionPane.showMessageDialog(this,"Einddatum ligt in de toekomst!");
        	return false;
		}
		return true;
	}
}
class actueelverzuimcompare implements Comparator<ActueelVerzuimInfo>{
	@Override
	public int compare(ActueelVerzuimInfo o1, ActueelVerzuimInfo o2) {
		if (o1.getWerkgeverid() < (o2.getWerkgeverid()))
			return -1;
		else
			if (o1.getWerkgeverid() > (o2.getWerkgeverid()))
				return 1;
			else{
				// zelfde werkgeverid
				if(o1.getAfdelingid().intValue() < o2.getAfdelingid().intValue())
					return -1;
				else
					if (o1.getAfdelingid().intValue() > o2.getAfdelingid().intValue())
						return 1;
					else{
						// zelfde afdeling;
						if (o1.getAchternaam().compareToIgnoreCase(o2.getAchternaam()) < -1)
							return -1;
						else
							if (o1.getAchternaam().compareToIgnoreCase(o2.getAchternaam()) < -1)
								return -1;
							else
								if (o1.getWerknemerid() < o2.getWerknemerid())
									return -1;
								else
									if (o1.getWerknemerid() > o2.getWerknemerid())
										return 1;
									else{
										// zelfde werknemer
										if (o1.getVerzuimid() < o2.getVerzuimid())
											return -1;
										else
											if (o1.getVerzuimid() > o2.getVerzuimid())
												return 1;
											else
												return 0;
									}
					}
			}
	}
}
