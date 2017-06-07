package com.gieselaar.verzuimbeheer.forms;

import javax.swing.JOptionPane;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import com.gieselaar.verzuimbeheer.baseforms.BaseDetailform;
import com.gieselaar.verzuimbeheer.baseforms.JDesktopPaneTGI;
import com.gieselaar.verzuimbeheer.exceptions.VerzuimApplicationException;
import com.gieselaar.verzuimbeheer.exceptions.PermissionException;
import com.gieselaar.verzuimbeheer.services.BedrijfsgegevensInfo;
import com.gieselaar.verzuimbeheer.services.FactuurTotaalInfo;
import com.gieselaar.verzuimbeheer.utils.CursorController;
import com.gieselaar.verzuimbeheer.utils.ExceptionLogger;
import com.gieselaar.verzuimbeheer.utils.ServiceCaller;
import com.gieselaar.verzuimbeheer.utils.ServiceLocatorException;

import javax.swing.JFrame;
import javax.swing.JLabel;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import com.michaelbaranov.microba.calendar.DatePicker;

import javax.swing.JButton;
import javax.swing.JFileChooser;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRParameter;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.util.JRLoader;
import net.sf.jasperreports.swing.JRViewer;

public class ReportBTWOmzet extends BaseDetailform {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2862612204474930868L;
	private DatePicker dtpEinddatum;
	private DatePicker dtpStartdatum;

	/**
	 * Getters and setters of this dialog
	 */
	public void setInfo() {
		activateListener();
	}

	/**
	 * Create the frame.
	 */
	public ReportBTWOmzet(JDesktopPaneTGI mdiPanel) {
		super("Rapport BTW en omzet", mdiPanel);
		setTitle("Rapport BTW en omzet");
		initialize();
	}

	private void initialize() {
		setBounds(100, 100, 526, 312);
		getContentPane().setLayout(null);

		dtpStartdatum = new DatePicker();
		dtpStartdatum.setBounds(172, 97, 87, 21);
		dtpStartdatum.setDateFormat(new SimpleDateFormat("MM-yyyy"));
		getContentPane().add(dtpStartdatum);

		dtpEinddatum = new DatePicker();
		dtpEinddatum.setBounds(172, 121, 87, 21);
		dtpEinddatum.setDateFormat(new SimpleDateFormat("MM-yyyy"));
		getContentPane().add(dtpEinddatum);

		JLabel lblVan = new JLabel("Van");
		lblVan.setBounds(31, 100, 84, 14);
		getContentPane().add(lblVan);

		JLabel lblTot = new JLabel("Tot en met");
		lblTot.setBounds(31, 124, 84, 14);
		getContentPane().add(lblTot);

		JButton btnAfdrukken = new JButton("Tonen...");
		btnAfdrukken.addActionListener(CursorController.createListener(this, new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				afdrukkenClicked(e);
			}
		}));

		btnAfdrukken.setBounds(281, 196, 110, 23);
		getContentPane().add(btnAfdrukken);

		JButton btnExporteren = new JButton("Exporteren...");
		btnExporteren.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				btnExporterenClicked(arg0);
			}
		});
		btnExporteren.setBounds(281, 169, 110, 23);
		getContentPane().add(btnExporteren);

	}

	protected String formatBigDecimal(BigDecimal bd, int precision) {
		bd = bd.setScale(precision, BigDecimal.ROUND_HALF_UP);
		DecimalFormat df = new DecimalFormat();
		df.setMaximumFractionDigits(precision);
		df.setMinimumFractionDigits(precision);
		df.setGroupingUsed(false);
		return df.format(bd);
	}

	protected void btnExporterenClicked(ActionEvent e) {
		BigDecimal somOmzetBTWHoog;
		if (!FormChecksOk())
			return;

		List<FactuurTotaalInfo> somfacturen;
		somfacturen = getsomfacturen();

		// Vraag om naam excel bestand
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM");
		String documentname = "Facturen " + df.format(dtpStartdatum.getDate()) + "-" + df.format(dtpEinddatum.getDate())
				+ ".xls";

		File selectedFile = null;

		selectedFile = SelectFilename(documentname);
		if (selectedFile == null)
			return;
		else {
			if (selectedFile.exists()) {
				if (JOptionPane.showConfirmDialog(this,
						"Bestand " + selectedFile.getAbsolutePath() + " bestaat al. Wilt u het verwijderen?",
						"Verwijderen", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
					if (!selectedFile.delete()) {
						JOptionPane.showMessageDialog(this, "Kan bestand niet verwijderen.");
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
			ExceptionLogger.ProcessException(ex, this);
		} finally {
			if (fos != null) {
				try {
					fos.flush();
					fos.close();
				} catch (IOException ex) {
					ExceptionLogger.ProcessException(ex, this);
				}
			}
		}
		JOptionPane.showMessageDialog(this, "Exporteren gereed");
	}

	private void setValue(HSSFCell datacell, Object value) {
		if (value == null)
			datacell.setCellValue("");
		else if (value instanceof Date) {
			String strDate = new SimpleDateFormat("dd-MM-yyyy").format((Date) value);
			datacell.setCellValue(strDate);
		} else
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

	protected void afdrukkenClicked(ActionEvent e) {
		if (!FormChecksOk())
			return;

		if (this.getLoginSession() != null) {
			try {
				URL url = JRLoader.getResource("BTWOverzicht.jasper");
				if (url == null) {
					System.out.println("Kan resource BTWOverzicht.jasper niet vinden!");
					throw new RuntimeException("Kan resource BTWOverzicht.jasper niet vinden!");
				}
				String path = new File(url.getFile()).getParent();

				Map<String, Object> parameters = new HashMap<String, Object>();
				parameters.put("SUBREPORT_DIR", path);
				parameters.put(JRParameter.REPORT_LOCALE, new Locale("nl", "NL"));
				JasperReport report;

				List<BedrijfsgegevensInfo> bedrijfsgegevens = new ArrayList<>();
				bedrijfsgegevens.add(ServiceCaller.instantieFacade(getLoginSession()).allBedrijfsgegevens().get(0));
				parameters.put("Footer_datasource", bedrijfsgegevens);
				report = (JasperReport) JRLoader.loadObject(url);

				List<FactuurTotaalInfo> somfacturen;
				somfacturen = getsomfacturen();
				JasperPrint print = JasperFillManager.fillReport(report, parameters,
						new JRBeanCollectionDataSource(somfacturen));

				JFrame frame = new JFrame("Report");
				frame.getContentPane().add(new JRViewer(print));
				frame.pack();
				frame.setVisible(true);

			} catch (JRException e1) {
				ExceptionLogger.ProcessException(e1, this, false);
			} catch (PermissionException e1) {
				ExceptionLogger.ProcessException(e1, this, false);
			} catch (VerzuimApplicationException e1) {
				ExceptionLogger.ProcessException(e1, this, false);
			} catch (ServiceLocatorException e1) {
				ExceptionLogger.ProcessException(e1, this, false);
			}
		} else
			JOptionPane.showMessageDialog(this, "Logic error: loginSession is null");
	}

	private List<FactuurTotaalInfo> getsomfacturen(){
		List<FactuurTotaalInfo> allfacturen;
		try {
			allfacturen = ServiceCaller.factuurFacade(getLoginSession())
					.getFacturenInPeriode(dtpStartdatum.getDate(), dtpEinddatum.getDate(),true);
		} catch (PermissionException e2) {
			ExceptionLogger.ProcessException(e2, this, "Kan Facturen niet opvragen");
			return null;
		} catch (VerzuimApplicationException e2) {
			ExceptionLogger.ProcessException(e2, this);
			return null;
		} catch (Exception e1) {
			ExceptionLogger.ProcessException(e1, this);
			return null;
		}
		return allfacturen;
	}

	protected void cancelButtonClicked(ActionEvent e) {
		this.setDatachanged(false);
		super.cancelButtonClicked(e);
	}

	protected void okButtonClicked(ActionEvent e) {
		super.okButtonClicked(e);
	}

	private boolean FormChecksOk() {

		if (dtpEinddatum.getDate().before(dtpStartdatum.getDate())) {
			JOptionPane.showMessageDialog(this, "Einddatum ligt voor de startdatum!");
			return false;
		}
		if (dtpEinddatum.getDate().after(new Date())) {
			JOptionPane.showMessageDialog(this, "Einddatum ligt in de toekomst!");
			return false;
		}
		return true;
	}
}
