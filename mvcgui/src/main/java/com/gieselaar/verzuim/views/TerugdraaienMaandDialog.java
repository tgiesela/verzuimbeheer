package com.gieselaar.verzuim.views;

import java.awt.event.ActionEvent;
import java.beans.PropertyVetoException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import com.gieselaar.verzuim.controllers.AbstractController;
import com.gieselaar.verzuim.controllers.FactuurController;
import com.gieselaar.verzuim.utils.ExceptionLogger;
import com.gieselaar.verzuim.viewsutils.CursorController;
import com.gieselaar.verzuimbeheer.exceptions.VerzuimApplicationException;
import com.gieselaar.verzuimbeheer.services.InfoBase;
import com.michaelbaranov.microba.calendar.DatePicker;

import java.awt.event.ActionListener;
import javax.swing.JButton;

public class TerugdraaienMaandDialog extends AbstractDetail{

	private static final long serialVersionUID = 1L;
	private DatePicker dtpMaand;
	private JButton btnTerugdraaien;
	private FactuurController factuurcontroller;

	public TerugdraaienMaandDialog(AbstractController controller) {
		super("Terugdraaien maand", controller);
		factuurcontroller = (FactuurController) controller;
		initialize();

		btnTerugdraaien.setEnabled(false); 

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

	private void initialize() {
		setBounds(100, 100, 391, 151);

		dtpMaand = new DatePicker();
		dtpMaand.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Calendar cal = Calendar.getInstance();
				cal.setTime(dtpMaand.getDate());
				btnTerugdraaien.setEnabled(true);
			}
		});
		dtpMaand.setBounds(30, 34, 120, 23);
		dtpMaand.setDateFormat(new SimpleDateFormat("MM-yyyy"));
		getContentPane().add(dtpMaand);
		
		btnTerugdraaien = new JButton("Terugdraaien...");
		btnTerugdraaien.addActionListener(CursorController.createListener(this, new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				btnTerugdraaienClicked();
			}
		}));
		btnTerugdraaien.setBounds(155, 34, 120, 23);
		getContentPane().add(btnTerugdraaien);
	}

	protected void btnTerugdraaienClicked() {
		int jaar, maand;
		Calendar cal = Calendar.getInstance();
		cal.setTime(dtpMaand.getDate());

		jaar = cal.get(Calendar.YEAR);
		maand = cal.get(Calendar.MONTH)+1;
		try {
			factuurcontroller.terugdraaienMaand(jaar, maand);
			btnTerugdraaien.setEnabled(false);
		} catch (VerzuimApplicationException e) {
			ExceptionLogger.ProcessException(e, this);
		}
	}

	@Override
	public InfoBase collectData() {
		return null;
	}

}
