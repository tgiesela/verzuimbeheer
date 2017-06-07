package com.gieselaar.verzuim.views;

import java.awt.FlowLayout;
import javax.swing.JPanel;

import com.gieselaar.verzuim.components.JTextFieldTGI;
import com.gieselaar.verzuim.controllers.AbstractController;
import com.gieselaar.verzuim.viewsutils.VerzuimComboBoxModel;
import com.gieselaar.verzuim.controllers.CascodeController;
import com.gieselaar.verzuim.utils.ExceptionLogger;
import com.gieselaar.verzuimbeheer.exceptions.VerzuimApplicationException;
import com.gieselaar.verzuimbeheer.services.CascodeInfo;
import com.gieselaar.verzuimbeheer.services.InfoBase;
import com.gieselaar.verzuimbeheer.services.VerzuimInfo.__vangnettype;
import com.gieselaar.verzuimbeheer.utils.TypeEntry;

import javax.swing.JLabel;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;

public class CascodeDetail extends AbstractDetail {
	private static final long serialVersionUID = 1L;
	private CascodeInfo cascode = null;
	private JTextFieldTGI txtNaam;
	private JTextFieldTGI txtOmschrijving;
	private JComboBox<TypeEntry> cmbCascodeGroep;
	private JComboBox<TypeEntry> cmbVangnettype;
	private JCheckBox chckbxActief;
	private VerzuimComboBoxModel cascodegroepModel;
	private VerzuimComboBoxModel vangnetModel;
	private CascodeController cascodecontroller; 
	
	public CascodeDetail(AbstractController controller) {
		super("Beheer cascode", controller);
		cascodecontroller = (CascodeController) controller;
		initialize();
	}
	@Override
	public void setData(InfoBase info){
		cascode = (CascodeInfo)info;
		displayCascode();
	}
	private void displayCascode(){
		try {
			cascodecontroller.selectCascodegroepen();
		} catch (VerzuimApplicationException e) {
			ExceptionLogger.ProcessException(e, this);
		}
		txtNaam.setText(cascode.getCascode());
		txtOmschrijving.setText(cascode.getOmschrijving());
		cascodecontroller.updateComboModelCascodegroepen(cascodegroepModel);
		cascodegroepModel.setId(cascode.getCascodegroep());

		vangnetModel = controller.getMaincontroller().getEnumModel(__vangnettype.class);
		vangnetModel.setId(cascode.getVangnettype().getValue());
        cmbVangnettype.setModel(vangnetModel);
		chckbxActief.setSelected(cascode.isActief());
	}
	
	private void initialize(){
		setBounds(100, 100, 546, 222);
		getContentPane().setLayout(null);
		
		JPanel panel = new JPanel();
		panel.setBounds(10, 379, 608, 33);
		getContentPane().add(panel);
		panel.setLayout(new FlowLayout(FlowLayout.RIGHT));
		
		txtNaam = new JTextFieldTGI();
		txtNaam.setBounds(108, 34, 124, 20);
		getContentPane().add(txtNaam);
		txtNaam.setColumns(10);
		
		txtOmschrijving = new JTextFieldTGI();
		txtOmschrijving.setBounds(108, 57, 403, 20);
		getContentPane().add(txtOmschrijving);
		txtOmschrijving.setColumns(10);
		
		JLabel lblCascode = new JLabel("Cascode");
		lblCascode.setBounds(32, 37, 46, 14);
		getContentPane().add(lblCascode);
		
		JLabel lblOmschrijving = new JLabel("Omschrijving");
		lblOmschrijving.setBounds(32, 60, 83, 14);
		getContentPane().add(lblOmschrijving);
		
		cmbCascodeGroep = new JComboBox<>();
		cmbCascodeGroep.setBounds(108, 11, 274, 20);
		getContentPane().add(cmbCascodeGroep);
		cascodegroepModel = new VerzuimComboBoxModel(controller.getMaincontroller());
		cmbCascodeGroep.setModel(cascodegroepModel);
		
		JLabel lblGroep = new JLabel("Groep");
		lblGroep.setBounds(32, 14, 46, 14);
		getContentPane().add(lblGroep);
		
		cmbVangnettype = new JComboBox<>();
		cmbVangnettype.setBounds(108, 103, 274, 20);
		getContentPane().add(cmbVangnettype);
		
		chckbxActief = new JCheckBox("Actief");
		chckbxActief.setBounds(108, 126, 97, 23);
		getContentPane().add(chckbxActief);
		
		JLabel lblVangnet = new JLabel("Vangnet");
		lblVangnet.setBounds(32, 106, 46, 14);
		getContentPane().add(lblVangnet);
	}
	@Override
	public InfoBase collectData(){
		cascode.setCascode(txtNaam.getText()); 
		cascode.setOmschrijving(txtOmschrijving.getText());
		cascode.setCascodegroep(cascodegroepModel.getId());
		cascode.setActief(chckbxActief.isSelected());
		cascode.setVangnettype(__vangnettype.parse(vangnetModel.getId()));
		return cascode;
	}
}
