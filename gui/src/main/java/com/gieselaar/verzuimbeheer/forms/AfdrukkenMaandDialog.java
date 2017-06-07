package com.gieselaar.verzuimbeheer.forms;

import java.awt.event.ActionEvent;
import java.beans.PropertyVetoException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.SwingWorker;

import com.gieselaar.verzuimbeheer.baseforms.BaseDetailform;
import com.gieselaar.verzuimbeheer.exceptions.VerzuimApplicationException;
import com.gieselaar.verzuimbeheer.exceptions.PermissionException;
import com.gieselaar.verzuimbeheer.exceptions.ValidationException;
import com.gieselaar.verzuimbeheer.facades.LoginSessionRemote;
import com.gieselaar.verzuimbeheer.services.FactuurInfo.__factuurstatus;
import com.gieselaar.verzuimbeheer.services.FactuurTotaalInfo;
import com.gieselaar.verzuimbeheer.services.HoldingInfo;
import com.gieselaar.verzuimbeheer.services.InfoBase;
import com.gieselaar.verzuimbeheer.services.WerkgeverInfo;
import com.gieselaar.verzuimbeheer.utils.ExceptionLogger;
import com.gieselaar.verzuimbeheer.utils.PrintFactuur;
import com.gieselaar.verzuimbeheer.utils.ServiceCaller;
import com.gieselaar.verzuimbeheer.utils.ServiceLocatorException;
import com.michaelbaranov.microba.calendar.DatePicker;

import java.awt.event.ActionListener;

public class AfdrukkenMaandDialog extends BaseDetailform {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private LoginSessionRemote loginSession;
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
		} catch (PropertyVetoException e) {
			ExceptionLogger.ProcessException(e, this, false);
			return;
		}
	}

	public AfdrukkenMaandDialog(JFrame frame, boolean modal) {
		super("Afdrukken en verzenden");
		setTitle("Afdrukken en verzenden");
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
		setBounds(100, 100, 229, 230);

		dtpMaand = new DatePicker();
		dtpMaand.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					Calendar cal = Calendar.getInstance();
					cal.setTime(dtpMaand.getDate());

					Integer aantal = ServiceCaller.factuurFacade(loginSession)
							.getAantalontbrekendeFacturen(
									cal.get(Calendar.YEAR),
									cal.get(Calendar.MONTH)+1);
					if (aantal > 0) {
						((BaseDetailform) thisform).getOkButton().setEnabled(
								false);
					} else {
						((BaseDetailform) thisform).getOkButton().setEnabled(
								true);
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

	private void afdrukkenFactuur(FactuurTotaalInfo fti, String emailaddress, String naam)
			throws ValidationException, VerzuimApplicationException,
			PermissionException, ServiceLocatorException, IOException {
		/**
		 * Opvragen factuur details is niet meer nodig. De details zitten er al in 
		 */
		/*fti = ServiceCaller.factuurFacade(loginSession).getFactuurDetails(
				fti.getId());*/
		PrintFactuur pf = new PrintFactuur(fti,loginSession);

		if (pf.isPrinten()){
		    if (emailaddress == null
		    		|| emailaddress.isEmpty()) {
		    	pf.afdrukken();
		    	System.out.println("Afdrukken " + naam);
		    	fti.setFactuurstatus(__factuurstatus.VERZONDEN);
		    } else {
		    	pf.email(emailaddress, naam);
		    	System.out.println("Verzenden werkgever " + naam + ":" + emailaddress);
		    	fti.setFactuurstatus(__factuurstatus.VERZONDEN);
		    }
		    ServiceCaller.factuurFacade(loginSession).updateFactuur(fti);
		}
	}

	protected void okButtonClicked(ActionEvent e) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(dtpMaand.getDate());

		if (this.loginSession != null) {
			SwingWorker<Void, Integer> worker = new SwingWorker<Void, Integer>() {
	            @Override
	            protected Void doInBackground() throws Exception {
	    			try {

//		            	int jaar, maand;
		        		Calendar cal = Calendar.getInstance();
		        		cal.setTime(dtpMaand.getDate());

//		        		jaar = cal.get(Calendar.YEAR);
//		        		maand = cal.get(Calendar.MONTH) + 1;

		        		setStatus("Afdrukken/verzenden facturen...");
						afdrukkenfacturen();
						setStatus("Klaar.");
						thisform.getOkButton().setEnabled(false);
		                return null;
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
	private void afdrukkenfacturen() {
		try {
			List<HoldingInfo> holdings = ServiceCaller.werkgeverFacade(
					loginSession).getHoldings();
			List<FactuurTotaalInfo> facturen;
			for (HoldingInfo h : holdings) {
				if (h.isFactureren()) {
					setStatus(h.getNaam());
					/*
					 * We vragen alle facturen op van werkgevers die onder de
					 * holding vallen.
					 */
					facturen = ServiceCaller.factuurFacade(loginSession)
							.getFacturenInPeriodeByHolding(
									dtpMaand.getDate(), dtpMaand.getDate(),
									h.getId(), false);
					if (facturen.size() > 0){
						afdrukkenFactuur(facturen.get(0),h.getEmailadresfactuur(),h.getNaam()); /* neem de eerste uit de lijst */
					}
				}
			}
			List<WerkgeverInfo> werkgevers = ServiceCaller.werkgeverFacade(
					loginSession).allWerkgevers();
			for (WerkgeverInfo w : werkgevers) {
				if (w.getFactureren()) {
					setStatus(w.getNaam());
					facturen = ServiceCaller.factuurFacade(loginSession)
							.getFacturenInPeriodeByWerkgever(
									dtpMaand.getDate(), dtpMaand.getDate(),
									w.getId(), false);
					for (FactuurTotaalInfo fti : facturen) {
						afdrukkenFactuur(fti, fti.getWerkgever().getEmailadresfactuur(), w.getNaam());
					}
				}
			}
	//	} catch (validationException e1) {
	//		ExceptionLogger.ProcessException(e1, this, false);
		} catch (PermissionException e1) {
			ExceptionLogger.ProcessException(e1, this);
		} catch (VerzuimApplicationException e1) {
			ExceptionLogger.ProcessException(e1, this);
		} catch (Exception e1) {
			ExceptionLogger.ProcessException(e1, this, true);
		}
	}	
}

