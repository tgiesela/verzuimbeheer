package com.gieselaar.verzuimbeheer.forms;

import java.awt.event.ActionEvent;
import java.beans.PropertyVetoException;
import java.io.File;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.SwingWorker;

import com.gieselaar.verzuimbeheer.baseforms.BaseDetailform;
import com.gieselaar.verzuimbeheer.exceptions.VerzuimApplicationException;
import com.gieselaar.verzuimbeheer.exceptions.PermissionException;
import com.gieselaar.verzuimbeheer.exceptions.ValidationException;
import com.gieselaar.verzuimbeheer.facades.LoginSessionRemote;
import com.gieselaar.verzuimbeheer.services.BedrijfsgegevensInfo;
import com.gieselaar.verzuimbeheer.services.FactuurTotaalInfo;
import com.gieselaar.verzuimbeheer.services.HoldingInfo;
import com.gieselaar.verzuimbeheer.services.InfoBase;
import com.gieselaar.verzuimbeheer.services.SettingsInfo;
import com.gieselaar.verzuimbeheer.services.WerkgeverInfo;
import com.gieselaar.verzuimbeheer.utils.ExceptionLogger;
import com.gieselaar.verzuimbeheer.utils.ServiceCaller;
import com.gieselaar.verzuimbeheer.utils.ServiceLocatorException;
import com.michaelbaranov.microba.calendar.DatePicker;

import java.awt.event.ActionListener;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRParameter;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.util.JRLoader;

public class AfsluitenMaandDialog extends BaseDetailform {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private LoginSessionRemote loginSession;
	private SettingsInfo settings = null;
	private DatePicker dtpMaand;
	private BaseDetailform thisform = this;
	private JLabel txtStatus;

	public void setLoginSession(LoginSessionRemote loginSession) {
		this.loginSession = loginSession;
	}

	public void setInfo(InfoBase info) {
		Date vandaag = new Date();
		Calendar cal = Calendar.getInstance();
		cal.setTime(vandaag);
		cal.set(Calendar.DAY_OF_MONTH, 1);
		cal.add(Calendar.MONTH, -1);
		cal.get(Calendar.YEAR);
		try {
			dtpMaand.setDate(cal.getTime());
			settings = ServiceCaller.settingsFacade(loginSession).getSettings();
		} catch (PropertyVetoException e) {
			ExceptionLogger.ProcessException(e, this, false);
			return;
		} catch (PermissionException e) {
			ExceptionLogger.ProcessException(e, this, false);
			return;
		} catch (VerzuimApplicationException e) {
			ExceptionLogger.ProcessException(e, this, false);
			return;
		} catch (ServiceLocatorException e) {
			ExceptionLogger.ProcessException(e, this, false);
			return;
		}
	}

	public AfsluitenMaandDialog(JFrame frame, boolean modal) {
		super("Afsluiten maand");
		initialize();
		this.getOkButton().setEnabled(false);

		txtStatus = new JLabel();
		txtStatus.setText("Start...");
		txtStatus.setEnabled(true);
		txtStatus.setBounds(30, 63, 368, 23);
		getContentPane().add(txtStatus);
	}
	private void setStatus(final String status){
         txtStatus.setText(status);
	}

	private void initialize() {
		setBounds(100, 100, 440, 183);

		dtpMaand = new DatePicker();
		dtpMaand.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					Calendar cal = Calendar.getInstance();
					cal.setTime(dtpMaand.getDate());

					Integer aantal = ServiceCaller.factuurFacade(loginSession)
							.getAantalontbrekendeFacturen(
									cal.get(Calendar.YEAR),
									cal.get(Calendar.MONTH) + 1);
					if (aantal > 0) {
						((BaseDetailform) thisform).getOkButton().setEnabled(
								true);
					} else {
						((BaseDetailform) thisform).getOkButton().setEnabled(
								false);
					}
				} catch (ServiceLocatorException | PermissionException
						| ValidationException | VerzuimApplicationException ee) {
					ExceptionLogger.ProcessException(ee, thisform, false);
					return;
				}
			}
		});
		dtpMaand.setBounds(30, 34, 120, 23);
		dtpMaand.setDateFormat(new SimpleDateFormat("MM-yyyy"));
		getContentPane().add(dtpMaand);
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

	protected String maakPDF(List<FactuurTotaalInfo> facturen, String naam) {
		Map<String, Object> parameters = new HashMap<String, Object>();
		URL url = JRLoader.getResource("Factuur.jasper");
		if (url == null){
			System.out.println("Kan resource Factuur.jasper niet vinden!");
			throw new RuntimeException("Kan resource Factuur.jasper niet vinden!");
		}
		String path = new File(url.getFile()).getParent();

		parameters.put("id", 42);
		parameters.put("SUBREPORT_DIR", path);
		parameters.put(JRParameter.REPORT_LOCALE, new Locale("nl", "NL"));
		JasperReport report;
		try {
			List<BedrijfsgegevensInfo> bedrijfsgegevens = new ArrayList<>();
			bedrijfsgegevens.add(ServiceCaller.instantieFacade(loginSession)
					.allBedrijfsgegevens().get(0));
			parameters.put("Footer_datasource", bedrijfsgegevens);
			parameters.put("Huisbezoeken_datasource", null);
			parameters.put("Secretariaat_datasource", null);
			parameters.put("Items_datasource", null);

			// InputStream template =
			// JRLoader.getFileInputStream("G:/verzuim_1.1/verzuimbeheer/JasperReports/Factuur.jrxml");
			// report = JasperCompileManager.compileReport(template);
			//report = (JasperReport) JRLoader.loadObjectFromFile(url.getFile());
			report = (JasperReport) JRLoader.loadObject(url);
			JasperPrint print = JasperFillManager.fillReport(report,
					parameters, new JRBeanCollectionDataSource(facturen));

			String pdfFilename = getPdfFilenaam(facturen.get(0), naam);
			File pdfdir = new File(pdfFilename);
			pdfdir.getParentFile().mkdirs();

			JasperExportManager.exportReportToPdfFile(print, pdfFilename);
			return pdfFilename;
		} catch (JRException e) {
			ExceptionLogger.ProcessException(e, this, false);
		} catch (PermissionException e) {
			ExceptionLogger.ProcessException(e, this, false);
		} catch (VerzuimApplicationException e) {
			ExceptionLogger.ProcessException(e, this, false);
		} catch (ServiceLocatorException e) {
			ExceptionLogger.ProcessException(e, this, false);
		}
		return null;
	}
/*
	private FactuurTotaalInfo sommeerFacturen(List<FactuurTotaalInfo> facturen) {
		FactuurTotaalInfo som;
		FactuurTotaalInfo eerstefactuur = facturen.get(0);
		som = new FactuurTotaalInfo();
		som.setAanmaakdatum(eerstefactuur.getAanmaakdatum());
		som.setAansluitkostenperiode(eerstefactuur.getAansluitkostenperiode());
		som.setAbonnementskostenperiode(eerstefactuur
				.getAbonnementskostenperiode());
		som.setBtwpercentagehoog(eerstefactuur.getBtwpercentagehoog());
		som.setBtwpercentagelaag(eerstefactuur.getBtwpercentagelaag());
		som.setFactuurnr(eerstefactuur.getFactuurnr());
		som.setFactuurstatus(eerstefactuur.getFactuurstatus());
		som.setHoldingid(eerstefactuur.getHoldingid());
		som.setJaar(eerstefactuur.getJaar());
		som.setMaand(eerstefactuur.getMaand());
		som.setOmschrijvingfactuur(eerstefactuur.getOmschrijvingfactuur());
		som.setPdflocation(eerstefactuur.getPdflocation());
		som.setPeildatumaansluitkosten(eerstefactuur
				.getPeildatumaansluitkosten());
		som.setTariefid(eerstefactuur.getTariefid());
		som.setVasttariefhuisbezoeken(eerstefactuur.isVasttariefhuisbezoeken());
		som.setWerkgeverid(eerstefactuur.getWerkgeverid());
		som.setWerkgever(eerstefactuur.getWerkgever());
		for (int i = 0; i < facturen.size(); i++) {
			FactuurTotaalInfo fti = facturen.get(i);
			som.setAansluitkosten(som.getAansluitkosten().add(
					fti.getAansluitkosten()));
			som.setAantalmedewerkers(som.getAantalmedewerkers()
					+ fti.getAantalmedewerkers());
			som.setAbonnementskosten(som.getAbonnementskosten().add(
					fti.getAbonnementskosten()));
			som.setMaandbedragsecretariaat(som.getMaandbedragsecretariaat()
					.add(fti.getMaandbedragsecretariaat()));
			som.setSomcasemanagementkosten(som.getSomcasemanagementkosten()
					.add(fti.getSomcasemanagementkosten()));
			som.setSomitembedrag(som.getSomitembedrag().add(
					fti.getSomitembedrag()));
			som.setSomitembtwbedraghoog(som.getSomitembtwbedraghoog().add(
					fti.getSomitembtwbedraghoog()));
			som.setSomitembtwbedraglaag(som.getSomitembtwbedraglaag().add(
					fti.getSomitembtwbedraglaag()));
			som.setSomkilometerkosten(som.getSomkilometerkosten().add(
					fti.getSomkilometerkosten()));
			som.setSomoverigekostenbezoek(som.getSomoverigekostenbezoek().add(
					fti.getSomoverigekostenbezoek()));
			som.setSomoverigekostensecretariaat(som
					.getSomoverigekostensecretariaat().add(
							fti.getSomoverigekostensecretariaat()));
			som.setSomsecretariaatskosten(som.getSomsecretariaatskosten().add(
					fti.getSomsecretariaatskosten()));
			som.setSomuurkosten(som.getSomuurkosten()
					.add(fti.getSomuurkosten()));
			som.setSomvastekosten(som.getSomvastekosten().add(
					fti.getSomvastekosten()));
			if (fti.getFactuurregelbezoeken() != null) {
				if (som.getFactuurregelbezoeken() == null)
					som.setFactuurregelbezoeken(new ArrayList<FactuurregelbezoekInfo>());
				som.getFactuurregelbezoeken().addAll(
						fti.getFactuurregelbezoeken());
			}
			if (fti.getFactuurregelitems() != null) {
				if (som.getFactuurregelitems() == null)
					som.setFactuurregelitems(new ArrayList<FactuurregelitemInfo>());
				som.getFactuurregelitems().addAll(fti.getFactuurregelitems());
			}
			if (fti.getFactuurregelsecretariaat() != null) {
				if (som.getFactuurregelsecretariaat() == null)
					som.setFactuurregelsecretariaat(new ArrayList<FactuurregelsecretariaatInfo>());
				som.getFactuurregelsecretariaat().addAll(
						fti.getFactuurregelsecretariaat());
			}
		}
		som.setGesommeerd(true);
		som.setId(-1);
		return som;
	}
*/
	protected void aanmakenPdfs() throws PermissionException,
			VerzuimApplicationException, ServiceLocatorException,
			ValidationException {
		List<FactuurTotaalInfo> somfacturen;
		FactuurTotaalInfo somfactuur;
		List<HoldingInfo> holdings = ServiceCaller
				.werkgeverFacade(loginSession).getHoldings();
		List<FactuurTotaalInfo> facturen;
		for (HoldingInfo h : holdings) {
			if (h.isFactureren()) {
				setStatus(h.getNaam());
				facturen = ServiceCaller.factuurFacade(loginSession)
						.getFacturenInPeriodeByHolding(dtpMaand.getDate(),
								dtpMaand.getDate(), h.getId(), true);
				if (facturen.size() > 0) {
					/*
					 * Ophalen details is niet meer nodig.
					 */
					//for (FactuurTotaalInfo fti : facturen) {
					//	fti = ServiceCaller.factuurFacade(loginSession)
					//			.getFactuurDetails(fti.getId());
					//}
					switch (h.getFactuurtype()) {
					case SEPARAAT:
						/*
						 * Voor elke werkgever onder de holding wordt een losse
						 * factuur(PDF) aangemaakt
						 */
						for (FactuurTotaalInfo fti : facturen) {
							somfacturen = new ArrayList<>();
							somfacturen.add(fti);
							String pdf = maakPDF(somfacturen, fti.getWerkgever()
									.getNaam());
							fti.setPdflocation(pdf);
							ServiceCaller.factuurFacade(loginSession)
									.updateFactuur(fti);
						}
						break;
					case GEAGGREGEERD:
						/*
						 * Alle facturen worden gesommeerd tot 1 factuur. Er
						 * zijn geen tellingen per werkgever zichtbaar
						 */
						somfactuur = FactuurTotaalInfo.sommeer(facturen);
						somfacturen = new ArrayList<>();
						somfacturen.add(somfactuur);
						String pdfagg = maakPDF(somfacturen, h.getNaam());
						for (FactuurTotaalInfo fti : facturen) {
							fti.setPdflocation(pdfagg);
							ServiceCaller.factuurFacade(loginSession)
									.updateFactuur(fti);
						}
						break;
					case GESPECIFICEERD:
						/*
						 * Er wordt een factuur met een totaalblad voor de
						 * holding aangemaakt. De werkgevers worden apart
						 * vermeld
						 */
						somfactuur = FactuurTotaalInfo.sommeer(facturen);
						somfacturen = new ArrayList<>();
						somfacturen.add(somfactuur);
						for (FactuurTotaalInfo fti:facturen){
							if (fti.getWerkgeverid() != -1){
								somfacturen.add(fti);
							}
						}
						// somfacturen.addAll(facturen);
						String pdf = maakPDF(somfacturen, h.getNaam());
						for (FactuurTotaalInfo fti : facturen) {
							fti.setPdflocation(pdf);
							ServiceCaller.factuurFacade(loginSession)
									.updateFactuur(fti);
						}
						break;
					case NVT:
						break;
					default:
						break;
					}
				}
			}
		}
		List<WerkgeverInfo> werkgevers = ServiceCaller.werkgeverFacade(
				loginSession).allWerkgevers();
		for (WerkgeverInfo w : werkgevers) {
			if (w.getFactureren()) {
				setStatus(w.getNaam());
				facturen = ServiceCaller.factuurFacade(loginSession)
						.getFacturenInPeriodeByWerkgever(dtpMaand.getDate(),
								dtpMaand.getDate(), w.getId(),true);
				for (FactuurTotaalInfo fti : facturen) {
					/*
					fti = ServiceCaller.factuurFacade(loginSession)
							.getFactuurDetails(fti.getId());
							*/
					facturen = new ArrayList<FactuurTotaalInfo>();
					facturen.add(fti);
					String pdf = maakPDF(facturen, w.getNaam());
					fti.setPdflocation(pdf);
					ServiceCaller.factuurFacade(loginSession)
							.updateFactuur(fti);
				}
			}
		}

	}

	protected void okButtonClicked(ActionEvent e) {

		if (this.loginSession != null) {
			
			SwingWorker<Void, Integer> worker = new SwingWorker<Void, Integer>() {
	            @Override
	            protected Void doInBackground() throws Exception {
	    			try {

		            	int jaar, maand;
		        		Calendar cal = Calendar.getInstance();
		        		cal.setTime(dtpMaand.getDate());

		        		jaar = cal.get(Calendar.YEAR);
		        		maand = cal.get(Calendar.MONTH) + 1;

		        		setStatus("Aanmaken facturen...");
						ServiceCaller.factuurFacade(loginSession).afsluitenMaand(jaar,
								maand);
						setStatus("Aanmaken pdf bestanden...");
						aanmakenPdfs();
						setStatus("Klaar.");
						thisform.getOkButton().setEnabled(false);
		                return null;
	    			} catch (ValidationException e1) {
	    				ExceptionLogger.ProcessException(e1, thisform, false);
	    			} catch (PermissionException e1) {
	    				ExceptionLogger.ProcessException(e1, thisform);
	    			} catch (VerzuimApplicationException e1) {
	    				ExceptionLogger.ProcessException(e1, thisform);
	    			} catch (Exception e1) {
	    				ExceptionLogger.ProcessException(e1, thisform);
	    			}
					return null;

	            }

	            @Override
	            protected void process(List<Integer> chunks) {
	                setStatus(chunks.get(chunks.size() - 1).toString());
	            }

	            @Override
	            protected void done() {
	                setStatus("Klaar");
	            }
	        };
	        try {
	        	worker.execute();
	        } catch (Exception we) {
	        	worker.cancel(true);
	        }
			
		} else
			JOptionPane.showMessageDialog(this,
					"Logic error: loginSession is null");
	}
}
