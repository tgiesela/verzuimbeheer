package com.gieselaar.verzuim.views;

import java.awt.event.ActionEvent;
import java.beans.PropertyVetoException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.swing.JLabel;
import javax.swing.SwingWorker;

import com.gieselaar.verzuim.controllers.AbstractController;
import com.gieselaar.verzuim.controllers.FactuurController;
import com.gieselaar.verzuim.utils.ExceptionLogger;
import com.gieselaar.verzuim.viewsutils.CursorController;
import com.gieselaar.verzuimbeheer.exceptions.VerzuimApplicationException;
import com.gieselaar.verzuimbeheer.services.FactuurTotaalInfo;
import com.gieselaar.verzuimbeheer.services.HoldingInfo;
import com.gieselaar.verzuimbeheer.services.InfoBase;
import com.gieselaar.verzuimbeheer.services.WerkgeverInfo;
import com.michaelbaranov.microba.calendar.DatePicker;

import java.awt.event.ActionListener;
import javax.swing.JButton;

public class AfdrukkenMaandDialog extends AbstractDetail {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private DatePicker dtpMaand;
	private AbstractDetail thisform = this;
	private JLabel txtStatus;
	private JButton btnAfdrukken; 
	private FactuurController factuurcontroller;

	public AfdrukkenMaandDialog(AbstractController controller) {
		super("Afdrukken en verzenden", controller);
		factuurcontroller = (FactuurController) controller;
		initialize();

		btnAfdrukken.setEnabled(false);
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
		setBounds(100, 100, 395, 200);

		dtpMaand = new DatePicker();
		dtpMaand.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					Calendar cal = Calendar.getInstance();
					cal.setTime(dtpMaand.getDate());

					Integer aantal = factuurcontroller
							.getAantalontbrekendeFacturen(
									cal.get(Calendar.YEAR),
									cal.get(Calendar.MONTH) + 1);
					if (aantal > 0) {
						btnAfdrukken.setEnabled(false); 
					} else {
						btnAfdrukken.setEnabled(true);
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

		txtStatus = new JLabel();
		txtStatus.setText("Start...");
		txtStatus.setEnabled(true);
		txtStatus.setBounds(30, 63, 368, 23);
		getContentPane().add(txtStatus);

		btnAfdrukken = new JButton("Afdrukken/Verzenden");
		btnAfdrukken.addActionListener(CursorController.createListener(this, new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				btnAfdrukkenClicked();
			}
		}));
		btnAfdrukken.setBounds(159, 34, 144, 23);
		getContentPane().add(btnAfdrukken);
	}

	protected void btnAfdrukkenClicked() {
		Calendar cal = Calendar.getInstance();
		cal.setTime(dtpMaand.getDate());

		SwingWorker<Void, Integer> worker = new SwingWorker<Void, Integer>() {
	           @Override
	           protected Void doInBackground() throws Exception {
	   			try {

	        		Calendar cal = Calendar.getInstance();
	        		cal.setTime(dtpMaand.getDate());

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
	}
	private void afdrukkenfacturen() {
		try {
			List<HoldingInfo> holdings = factuurcontroller.getMaincontroller().getHoldings();
			List<FactuurTotaalInfo> facturen;
			for (HoldingInfo h : holdings) {
				if (h.isFactureren()) {
					setStatus(h.getNaam());
					/*
					 * We vragen alle facturen op van werkgevers die onder de
					 * holding vallen.
					 */
					facturen = factuurcontroller
							.getFacturenInPeriodeByHolding(
									dtpMaand.getDate(), dtpMaand.getDate(),
									h.getId(), false);
					if (facturen.size() > 0){
						factuurcontroller.printOrEmailFactuur(facturen.get(0)
															, h.getEmailadresfactuur()
															, h.getNaam());/* neem de eerste uit de lijst */
					}
				}
			}
			List<WerkgeverInfo> werkgevers = factuurcontroller.getMaincontroller().getWerkgevers();
			for (WerkgeverInfo w : werkgevers) {
				if (w.getFactureren()) {
					setStatus(w.getNaam());
					facturen = factuurcontroller
							.getFacturenInPeriodeByWerkgever(
									dtpMaand.getDate(), dtpMaand.getDate(),
									w.getId(), false);
					for (FactuurTotaalInfo fti : facturen) {
						factuurcontroller.printOrEmailFactuur(fti
														    , fti.getWerkgever().getEmailadresfactuur()
														    , w.getNaam());
					}
				}
			}
		} catch (VerzuimApplicationException e1) {
			ExceptionLogger.ProcessException(e1, this);
		}
	}
	@Override
	public InfoBase collectData() {
		return null;
	}	
}

