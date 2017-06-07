package com.gieselaar.verzuim.views;

import com.gieselaar.verzuim.controllers.AbstractController;
import com.gieselaar.verzuim.controllers.ReportController;
import com.gieselaar.verzuim.utils.CursorController;
import com.gieselaar.verzuim.utils.ExceptionLogger;
import com.gieselaar.verzuim.viewsutils.VerzuimComboBoxModel;
import com.gieselaar.verzuimbeheer.exceptions.ValidationException;
import com.gieselaar.verzuimbeheer.exceptions.VerzuimApplicationException;
import com.gieselaar.verzuimbeheer.services.InfoBase;
import com.gieselaar.verzuimbeheer.utils.TypeEntry;

import javax.swing.JLabel;
import javax.swing.JComboBox;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.text.SimpleDateFormat;
import com.michaelbaranov.microba.calendar.DatePicker;

import javax.swing.JCheckBox;
import javax.swing.JButton;

public class ReportActueelVerzuim extends AbstractDetail  {
	private static final long serialVersionUID = -2862612204474930868L;
	private DatePicker dtpEinddatum;
	private DatePicker dtpStartdatum;
	private JComboBox<TypeEntry> cmbWerkgever = new JComboBox<>();
	private JComboBox<TypeEntry> cmbHolding = new JComboBox<>();
	private JComboBox<TypeEntry> cmbOe = new JComboBox<>(); 
	VerzuimComboBoxModel werkgevermodel;
	VerzuimComboBoxModel holdingmodel;
	VerzuimComboBoxModel oemodel;
	private JCheckBox chckbxRedenVerzuimTonen;
	private Integer selectedwerkgeverid = -1;
	private Integer selectedholdingid = -1;
	private Integer selectedoeid = -1;
	private ReportController reportcontroller;
	/**
	 * Create the frame.
	 */
	public ReportActueelVerzuim(AbstractController controller) {
		super("rapport Actueel Verzuim", controller);
		reportcontroller = (ReportController) controller;
		initialize();
		/* Treat OK as Cancel */
		super.getOkButton().setActionCommand(controller.cancelDetailActionCommand);
	}
	@Override
	public void setData(InfoBase info){
		try {
			reportcontroller.selectOes();
		} catch (VerzuimApplicationException e) {
			ExceptionLogger.ProcessException(e, this);
		}
		reportcontroller.getOesList();

		werkgevermodel =  new VerzuimComboBoxModel(controller.getMaincontroller());
        controller.getMaincontroller().updateComboModelWerkgevers(werkgevermodel, true);
		cmbWerkgever.setModel(werkgevermodel);

		holdingmodel =  new VerzuimComboBoxModel(controller.getMaincontroller());
        controller.getMaincontroller().updateComboModelHoldings(holdingmodel, true);
		cmbHolding.setModel(holdingmodel);

		oemodel = reportcontroller.getComboModelOes();
		cmbOe.setModel(oemodel);
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
				if (werkgevermodel.getId() != -1){
					holdingmodel.setId(-1);
					oemodel.setId(-1);
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
				if (holdingmodel.getId() != -1){
					werkgevermodel.setId(-1);
					oemodel.setId(-1);
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
				if (oemodel.getId() != -1){
					werkgevermodel.setId(-1);
					holdingmodel.setId(-1);
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
				btnExporterenClicked();
			}
		});
		btnExporteren.setBounds(281, 173, 110, 23);
		getContentPane().add(btnExporteren);
		
		JButton btnAfdrukken = new JButton("Afdrukken...");
		btnAfdrukken.addActionListener(CursorController.createListener(this,new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				afdrukkenClicked();
			}
		}));
		
		btnAfdrukken.setBounds(281, 196, 110, 23);
		getContentPane().add(btnAfdrukken);
		
	}
	
	protected void btnExporterenClicked() {
		try {
			selectedwerkgeverid = werkgevermodel.getId();
			selectedholdingid = holdingmodel.getId();
			selectedoeid = oemodel.getId();
			reportcontroller.exporteerActueelverzuim(selectedwerkgeverid, selectedholdingid, selectedoeid, dtpStartdatum.getDate(), dtpEinddatum.getDate());
		} catch (ValidationException e) {
			ExceptionLogger.ProcessException(e, this);
		}
	}
	protected void afdrukkenClicked() {
		try {
			selectedwerkgeverid = werkgevermodel.getId();
			selectedholdingid = holdingmodel.getId();
			selectedoeid = oemodel.getId();
			reportcontroller.printActueelverzuimOverzicht(selectedwerkgeverid, selectedholdingid, selectedoeid, dtpStartdatum.getDate(), dtpEinddatum.getDate(), chckbxRedenVerzuimTonen.isSelected());
		} catch (VerzuimApplicationException e) {
			ExceptionLogger.ProcessException(e, this);
		}
	}
	@Override
	public InfoBase collectData() {
		return null;
	}
}
