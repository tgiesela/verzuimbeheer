package com.gieselaar.verzuim.views;

import java.awt.FlowLayout;
import java.util.List;

import javax.swing.JLabel;
import javax.swing.JPanel;

import com.gieselaar.verzuim.components.JTextFieldTGI;
import com.gieselaar.verzuim.controllers.AbstractController;
import com.gieselaar.verzuim.controllers.OeController;
import com.gieselaar.verzuim.viewsutils.VerzuimComboBoxModel;
import com.gieselaar.verzuimbeheer.facades.LoginSessionRemote;
import com.gieselaar.verzuimbeheer.services.OeInfo;
import com.gieselaar.verzuimbeheer.services.InfoBase;
import com.gieselaar.verzuimbeheer.services.OeNiveauInfo;
import com.gieselaar.verzuimbeheer.utils.TypeEntry;

import javax.swing.JComboBox;


public class OeDetail extends AbstractDetail {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private OeInfo oe = null;
	private List<OeNiveauInfo> oeniveaus;	
	private JTextFieldTGI txtNaam;
	private JComboBox<TypeEntry> cmbWerkgever = new JComboBox<TypeEntry>();
	private JComboBox<TypeEntry> cmbOeNiveau = new JComboBox<TypeEntry>();
	private OeController oecontroller;

	public void setLoginSession(LoginSessionRemote loginSession) {
		this.loginSession = loginSession;
	}
	/**
	 * Create the frame.
	 */
	public OeDetail(AbstractController controller) {
		super( "Rapportage eenheid detail", controller);
		oecontroller = (OeController) controller;
		initialize();
	}
	@Override
	public void setData(InfoBase info){
		oe = (OeInfo)info;
		displayOe();
	}
	private void displayOe(){
		oeniveaus = oecontroller.getOeNiveauList();

		VerzuimComboBoxModel soortmodelWG = new VerzuimComboBoxModel(oecontroller);
		controller.getMaincontroller().updateComboModelWerkgevers(soortmodelWG, true);
		if (oe.getWerkgeverId() == null){
			soortmodelWG.setId(-1);
		}else{
			soortmodelWG.setId(oe.getWerkgeverId());
		}
		cmbWerkgever.setModel(soortmodelWG);
		VerzuimComboBoxModel soortmodelOEN = oecontroller.getComboModelOeniveaus();
		soortmodelOEN.setId(oe.getOeniveau().getOeniveau());
		cmbOeNiveau.setModel(soortmodelOEN);
		txtNaam.setText(oe.getNaam());
	}
	
	private void initialize(){
		setBounds(100, 100, 606, 231);
		getContentPane().setLayout(null);
		
		JPanel panel = new JPanel();
		panel.setBounds(10, 379, 608, 33);
		getContentPane().add(panel);
		panel.setLayout(new FlowLayout(FlowLayout.RIGHT));
		
		txtNaam = new JTextFieldTGI();
		txtNaam.setBounds(173, 36, 256, 20);
		getContentPane().add(txtNaam);
		txtNaam.setColumns(10);
		
		JLabel lblNaam = new JLabel("Eenheid omschrijving");
		lblNaam.setLabelFor(txtNaam);
		lblNaam.setBounds(30, 39, 118, 14);
		getContentPane().add(lblNaam);
		
		cmbWerkgever.setBounds(173, 60, 256, 20);
		getContentPane().add(cmbWerkgever);
		
		JLabel lblWerkgever = new JLabel("Werkgever");
		lblWerkgever.setBounds(30, 62, 75, 14);
		getContentPane().add(lblWerkgever);
		
		JLabel lblHierarchischNiveau = new JLabel("Hi\u00EBrarchisch niveau");
		lblHierarchischNiveau.setBounds(30, 14, 118, 14);
		getContentPane().add(lblHierarchischNiveau);
		
		cmbOeNiveau.setBounds(173, 11, 256, 20);
		getContentPane().add(cmbOeNiveau);
	}
	@Override
	public InfoBase collectData(){
		TypeEntry werkgever;
		
		oe.setNaam(txtNaam.getText()); 
		
		Integer oeniveauid = ((VerzuimComboBoxModel)cmbOeNiveau.getModel()).getId();
		for (OeNiveauInfo o:oeniveaus){
			if (o.getId().equals(oeniveauid)){
				oe.setOeniveau(o);
				break;
			}
		}
			
		werkgever = (TypeEntry)cmbWerkgever.getSelectedItem();
		if (werkgever.getValue() == -1)
			oe.setWerkgeverId(null);
		else{
			oe.setWerkgeverId(werkgever.getValue());
			oe.setNaam(werkgever.toString());
		}
		return oe;
	}
}
