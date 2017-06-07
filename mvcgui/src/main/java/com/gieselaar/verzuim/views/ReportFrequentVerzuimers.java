package com.gieselaar.verzuim.views;

import com.gieselaar.verzuim.controllers.AbstractController;
import com.gieselaar.verzuim.controllers.ReportController;
import com.gieselaar.verzuim.utils.CursorController;
import com.gieselaar.verzuim.utils.ExceptionLogger;
import com.gieselaar.verzuim.viewsutils.VerzuimComboBoxModel;
import com.gieselaar.verzuimbeheer.exceptions.VerzuimApplicationException;
import com.gieselaar.verzuimbeheer.exceptions.ValidationException;
import com.gieselaar.verzuimbeheer.services.InfoBase;
import com.gieselaar.verzuimbeheer.utils.TypeEntry;

import javax.swing.JLabel;
import javax.swing.JComboBox;
import java.awt.event.ActionListener;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.text.SimpleDateFormat;
import com.michaelbaranov.microba.calendar.DatePicker;

import javax.swing.JSpinner;
import javax.swing.JButton;

public class ReportFrequentVerzuimers extends AbstractDetail  {
	private static final long serialVersionUID = -2862612204474930868L;
	private Component thisform = this;
	private DatePicker dtpEinddatum;
	private DatePicker dtpStartdatum;
	private JComboBox<TypeEntry> cmbWerkgever = new JComboBox<>();
	private JComboBox<TypeEntry> cmbHolding = new JComboBox<>();
	private VerzuimComboBoxModel werkgevermodel;
	private VerzuimComboBoxModel holdingmodel;
	private JSpinner spinAantal = new JSpinner(); 
	private ReportController reportcontroller;
	/**
	 * Create the frame.
	 */
	public ReportFrequentVerzuimers(AbstractController controller) {
		super("Rapport Frequent Verzuim", controller);
		reportcontroller = (ReportController) controller;
		initialize();
		/* Treat OK as Cancel */
		super.getOkButton().setActionCommand(controller.cancelDetailActionCommand);
	}
	@Override
	public void setData(InfoBase info){
		werkgevermodel =  new VerzuimComboBoxModel(controller.getMaincontroller());
        controller.getMaincontroller().updateComboModelWerkgevers(werkgevermodel, true);
		cmbWerkgever.setModel(werkgevermodel);

		holdingmodel =  new VerzuimComboBoxModel(controller.getMaincontroller());
        controller.getMaincontroller().updateComboModelHoldings(holdingmodel, true);
		cmbHolding.setModel(holdingmodel);
		
		spinAantal.setValue(3);
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
				if (werkgevermodel.getId() != -1)
					holdingmodel.setId(-1);
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
				if (holdingmodel.getId() != -1)
					werkgevermodel.setId(-1);
			}
		});
		cmbHolding.setBounds(171, 19, 256, 20);
		getContentPane().add(cmbHolding);
		
		dtpStartdatum = new DatePicker();
		dtpStartdatum.setBounds(171, 69, 87, 21);
		dtpStartdatum.setDateFormat(new SimpleDateFormat("dd-MM-yyyy"));
		getContentPane().add(dtpStartdatum);
		
		dtpEinddatum = new DatePicker();
		dtpEinddatum.setBounds(171, 92, 87, 21);
		dtpEinddatum.setDateFormat(new SimpleDateFormat("dd-MM-yyyy"));
		getContentPane().add(dtpEinddatum);
		
		JLabel lblVan = new JLabel("Van");
		lblVan.setBounds(30, 72, 84, 14);
		getContentPane().add(lblVan);
		
		JLabel lblTot = new JLabel("Tot en met");
		lblTot.setBounds(30, 95, 84, 14);
		getContentPane().add(lblTot);
		
		JLabel lblMinumumAantalVerzuimen = new JLabel("Minimum aantal verzuimen");
		lblMinumumAantalVerzuimen.setBounds(30, 164, 137, 15);
		getContentPane().add(lblMinumumAantalVerzuimen);
		
		spinAantal = new JSpinner();
		spinAantal.setBounds(171, 162, 29, 20);
		getContentPane().add(spinAantal);
		
		JButton btnAfdrukken = new JButton("Afdrukken...");
		btnAfdrukken.addActionListener(CursorController.createListener(this,new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					btnAfdrukkenClicked();
				} catch (ValidationException e1) {
					ExceptionLogger.ProcessException(e1, thisform);
				}
			}
		}));
		
		btnAfdrukken.setBounds(324, 160, 103, 23);
		getContentPane().add(btnAfdrukken);
		
	}
	protected void btnAfdrukkenClicked() throws ValidationException {
		Integer werkgeverid;
		Integer holdingid;
		
		try {
			werkgeverid = werkgevermodel.getId();
			holdingid = holdingmodel.getId();
			reportcontroller.printFrequentVerzuimers(werkgeverid, holdingid, dtpStartdatum.getDate(), dtpEinddatum.getDate(), (int) spinAantal.getValue());
		} catch (VerzuimApplicationException e) {
			ExceptionLogger.ProcessException(e, this);
		}
	}
	@Override
	public InfoBase collectData() {
		return null;
	}
}
