package com.gieselaar.verzuim.views;

import java.awt.event.ActionEvent;
import java.beans.PropertyVetoException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import javax.swing.JLabel;
import javax.swing.SwingWorker;

import com.gieselaar.verzuim.controllers.AbstractController;
import com.gieselaar.verzuim.controllers.FactuurController;
import com.gieselaar.verzuim.controllers.ReportController;
import com.gieselaar.verzuim.utils.ExceptionLogger;
import com.gieselaar.verzuim.viewsutils.CursorController;
import com.gieselaar.verzuimbeheer.exceptions.VerzuimApplicationException;
import com.gieselaar.verzuimbeheer.exceptions.PermissionException;
import com.gieselaar.verzuimbeheer.exceptions.ValidationException;
import com.gieselaar.verzuimbeheer.services.FactuurTotaalInfo;
import com.gieselaar.verzuimbeheer.services.HoldingInfo;
import com.gieselaar.verzuimbeheer.services.InfoBase;
import com.gieselaar.verzuimbeheer.services.WerkgeverInfo;
import com.gieselaar.verzuimbeheer.utils.ServiceLocatorException;
import com.michaelbaranov.microba.calendar.DatePicker;

import java.awt.event.ActionListener;
import javax.swing.JButton;

public class AfsluitenMaandDialog extends AbstractDetail {

	private static final long serialVersionUID = 1L;
	private DatePicker dtpMaand;
	private AbstractDetail thisform = this;
	private JLabel txtStatus;
	private JButton btnAfsluiten;
	private FactuurController factuurcontroller;
	private ReportController reportcontroller;
	
	public AfsluitenMaandDialog(AbstractController controller) {
		super("Afsluiten maand", controller);
		factuurcontroller = (FactuurController) controller;
		reportcontroller = factuurcontroller.getReportController();
		initialize();
		
		/* Disable OK */
		btnAfsluiten.setEnabled(false); 

		/* Treat OK as Cancel */
		super.getOkButton().setActionCommand(controller.cancelDetailActionCommand);

	}
	@Override
	public void setData(InfoBase info) {
		Date vandaag = new Date();
		Calendar cal = Calendar.getInstance();
		cal.setTime(vandaag);
		cal.set(Calendar.DAY_OF_MONTH, 1);
		cal.add(Calendar.MONTH, -1);
		cal.get(Calendar.YEAR);
		try {
			dtpMaand.setDate(cal.getTime());
		} catch (PropertyVetoException e) {
			ExceptionLogger.ProcessException(e, this, false);
			return;
		}
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
					int aantal = factuurcontroller
							.getAantalontbrekendeFacturen(
									cal.get(Calendar.YEAR),
									cal.get(Calendar.MONTH) + 1);
					if (aantal > 0) {
						btnAfsluiten.setEnabled(true); 
					} else {
						btnAfsluiten.setEnabled(false);
					}
				} catch (VerzuimApplicationException ee) {
					ExceptionLogger.ProcessException(ee, thisform, false);
					return;
				}
			}
		});
		dtpMaand.setBounds(30, 34, 120, 23);
		dtpMaand.setDateFormat(new SimpleDateFormat("MM-yyyy"));
		getContentPane().add(dtpMaand);

		btnAfsluiten = new JButton("Afsluiten maand...");
		btnAfsluiten.addActionListener(CursorController.createListener(this, new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				btnAfsluitenClicked();
			}
		}));
		btnAfsluiten.setBounds(163, 34, 138, 23);
		getContentPane().add(btnAfsluiten);

		txtStatus = new JLabel();
		txtStatus.setText("Start...");
		txtStatus.setEnabled(true);
		txtStatus.setBounds(30, 63, 368, 23);
		getContentPane().add(txtStatus);

	}


	protected void aanmakenPdfs() throws PermissionException,
			VerzuimApplicationException, ServiceLocatorException,
			ValidationException {
		List<FactuurTotaalInfo> somfacturen;
		FactuurTotaalInfo somfactuur;
		List<HoldingInfo> holdings = controller.getMaincontroller().getHoldings();
		List<FactuurTotaalInfo> facturen;
		for (HoldingInfo h : holdings) {
			if (h.isFactureren()) {
				setStatus(h.getNaam());
				facturen = factuurcontroller.getFacturenInPeriodeByHolding(dtpMaand.getDate(),
						dtpMaand.getDate(), h.getId(), true);
				if (facturen.size() > 0) {
					/*
					 * Ophalen details is niet meer nodig.
					 */
					switch (h.getFactuurtype()) {
					case SEPARAAT:
						/*
						 * Voor elke werkgever onder de holding wordt een losse
						 * factuur(PDF) aangemaakt
						 */
						for (FactuurTotaalInfo fti : facturen) {
							somfacturen = new ArrayList<>();
							somfacturen.add(fti);
							String pdf = reportcontroller.printFactuurToPDF(somfacturen, fti.getWerkgever()
									.getNaam());
							fti.setPdflocation(pdf);
							factuurcontroller.saveData(fti);
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
						String pdfagg = reportcontroller.printFactuurToPDF(somfacturen, h.getNaam());
						for (FactuurTotaalInfo fti : facturen) {
							fti.setPdflocation(pdfagg);
							factuurcontroller.saveData(fti);
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
						String pdf = reportcontroller.printFactuurToPDF(somfacturen, h.getNaam());
						for (FactuurTotaalInfo fti : facturen) {
							fti.setPdflocation(pdf);
							factuurcontroller.saveData(fti);
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
		List<WerkgeverInfo> werkgevers = factuurcontroller.getMaincontroller().getWerkgevers();
		for (WerkgeverInfo w : werkgevers) {
			if (w.getFactureren()) {
				setStatus(w.getNaam());
				facturen = factuurcontroller
						.getFacturenInPeriodeByWerkgever(dtpMaand.getDate(),
								dtpMaand.getDate(), w.getId(),true);
				for (FactuurTotaalInfo fti : facturen) {
					/*
					fti = ServiceCaller.factuurFacade(loginSession)
							.getFactuurDetails(fti.getId());
							*/
					facturen = new ArrayList<FactuurTotaalInfo>();
					facturen.add(fti);
					String pdf = reportcontroller.printFactuurToPDF(facturen, w.getNaam());
					fti.setPdflocation(pdf);
					factuurcontroller.saveData(fti);
				}
			}
		}

	}

	protected void btnAfsluitenClicked() {

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
	        		factuurcontroller.afsluitenMaand(jaar, maand);
					setStatus("Aanmaken pdf bestanden...");
					aanmakenPdfs();
					setStatus("Klaar.");
					btnAfsluiten.setEnabled(false);
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
			
	}
	@Override
	public InfoBase collectData() {
		return null;
	}
}
