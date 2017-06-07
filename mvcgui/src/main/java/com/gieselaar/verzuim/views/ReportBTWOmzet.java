package com.gieselaar.verzuim.views;

import javax.swing.JOptionPane;
import com.gieselaar.verzuim.controllers.AbstractController;
import com.gieselaar.verzuim.controllers.ReportController;
import com.gieselaar.verzuim.utils.CursorController;
import com.gieselaar.verzuimbeheer.services.InfoBase;

import javax.swing.JLabel;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.text.SimpleDateFormat;
import java.util.Date;
import com.michaelbaranov.microba.calendar.DatePicker;

import javax.swing.JButton;

public class ReportBTWOmzet extends AbstractDetail {

	private static final long serialVersionUID = -2862612204474930868L;
	private ReportController reportcontroller;
	private JButton btnExporteren;
	private DatePicker dtpEinddatum;
	private DatePicker dtpStartdatum;

	/**
	 * Create the frame.
	 */
	public ReportBTWOmzet(AbstractController controller) {
		super("Rapport BTW en omzet", controller);
		reportcontroller = (ReportController) controller;
		initialize();
		/* Treat OK as Cancel */
		super.getOkButton().setActionCommand(controller.cancelDetailActionCommand);
	}
	/**
	 * Getters and setters of this dialog
	 */
	@Override
	public void setData(InfoBase info){
	}

	private void initialize() {
		setBounds(50, 50, 526, 312);
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

		btnExporteren = new JButton("Exporteren...");
		btnExporteren.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				btnExporterenClicked(arg0);
			}
		});
		btnExporteren.setBounds(281, 169, 110, 23);
		getContentPane().add(btnExporteren);

	}

	protected void btnExporterenClicked(ActionEvent e) {
		if (!FormChecksOk())
			return;
		reportcontroller.exportBTWenOmzet(dtpStartdatum.getDate(), dtpEinddatum.getDate());
	}

	protected void afdrukkenClicked(ActionEvent e) {
		if (!FormChecksOk())
			return;
		reportcontroller.printBTWenOmzetOverzicht(dtpStartdatum.getDate(), dtpEinddatum.getDate());
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
	@Override
	public InfoBase collectData() {
		return null;
	}
}
