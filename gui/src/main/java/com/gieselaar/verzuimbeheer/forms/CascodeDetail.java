package com.gieselaar.verzuimbeheer.forms;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.util.List;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import com.gieselaar.verzuimbeheer.baseforms.BaseDetailform;
import com.gieselaar.verzuimbeheer.baseforms.JDesktopPaneTGI;
import com.gieselaar.verzuimbeheer.components.JTextFieldTGI;
import com.gieselaar.verzuimbeheer.exceptions.VerzuimApplicationException;
import com.gieselaar.verzuimbeheer.exceptions.PermissionException;
import com.gieselaar.verzuimbeheer.exceptions.ValidationException;
import com.gieselaar.verzuimbeheer.services.CascodeGroepInfo;
import com.gieselaar.verzuimbeheer.services.CascodeInfo;
import com.gieselaar.verzuimbeheer.services.InfoBase;
import com.gieselaar.verzuimbeheer.services.VerzuimInfo.__vangnettype;
import com.gieselaar.verzuimbeheer.utils.ExceptionLogger;
import com.gieselaar.verzuimbeheer.utils.ServiceCaller;
import com.gieselaar.verzuimbeheer.utils.ServiceLocatorException;
import com.gieselaar.verzuimbeheer.utils.TypeEntry;

import javax.swing.JLabel;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;

public class CascodeDetail extends BaseDetailform {

	private static final long serialVersionUID = 1L;

	private CascodeInfo cascode = null;
	private JTextFieldTGI txtNaam;
	private JTextFieldTGI txtOmschrijving;
	private JComboBox<TypeEntry> cmbCascodeGroep;
	private JComboBox<TypeEntry> cmbVangnettype;
	private JCheckBox chckbxActief;
	private List<CascodeGroepInfo> cascodegroepen;
	private DefaultComboBoxModel<TypeEntry> cascodegroepModel;
	private DefaultComboBoxModel<TypeEntry> VangnetModel;
	
	public CascodeDetail(JDesktopPaneTGI mdiPanel) {
		super("Beheer cascode", mdiPanel);
		initialize();
	}
	public void setInfo(InfoBase info){
		TypeEntry cascodegroeptype;
		TypeEntry vangnettype;
		
		try {
			cascodegroepen = ServiceCaller.cascodeFacade(getLoginSession()).allCascodeGroepen();
		} catch (ServiceLocatorException e) {
        	ExceptionLogger.ProcessException(e,this,"Unable to connect to server");
        	return;
		} catch (PermissionException e) {
			ExceptionLogger.ProcessException(e,this);
			return;
		} catch (VerzuimApplicationException e) {
        	ExceptionLogger.ProcessException(e,this);
        	return;
		} catch (Exception e1){
        	ExceptionLogger.ProcessException(e1,this);
        	return;
		}
		cascode = (CascodeInfo)info;
		if (this.getMode() == formMode.New){
			cascode.setActief(true);
			cascode.setCascode("");
			cascode.setVangnettype(__vangnettype.NVT);
		}
		txtNaam.setText(cascode.getCascode());
		txtOmschrijving.setText(cascode.getOmschrijving());

		for (CascodeGroepInfo cg: cascodegroepen)
		{
			TypeEntry cc = new TypeEntry(cg.getId(), cg.getNaam());
			cascodegroepModel.addElement(cc);
		}
		cascodegroepModel.addElement(new TypeEntry(-1, "[Alles]"));
		for (int i=0;i<cascodegroepModel.getSize();i++)
		{
			cascodegroeptype = (TypeEntry) cascodegroepModel.getElementAt(i);
			if (cascodegroeptype.getValue() == cascode.getCascodegroep())
			{
				cascodegroepModel.setSelectedItem(cascodegroeptype);
				break;
			}
		}
		VangnetModel = new DefaultComboBoxModel<TypeEntry>();
        cmbVangnettype.setModel(VangnetModel);
        for (__vangnettype g: __vangnettype.values())
        {
        	TypeEntry soort = new TypeEntry(g.getValue(), g.toString());
        	VangnetModel.addElement(soort);
        }
		for (int i=0;i<VangnetModel.getSize();i++)
		{
			vangnettype = (TypeEntry) VangnetModel.getElementAt(i);
			if (vangnettype.getValue() == cascode.getVangnettype().getValue())
			{
				VangnetModel.setSelectedItem(vangnettype);
				break;
			}
		}
		chckbxActief.setSelected(cascode.isActief());
		
		activateListener();
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
		
		JLabel lblOmschrijving_1 = new JLabel("Omschrijving");
		lblOmschrijving_1.setBounds(32, 60, 83, 14);
		getContentPane().add(lblOmschrijving_1);
		
		cmbCascodeGroep = new JComboBox<TypeEntry>();
		cmbCascodeGroep.setBounds(108, 11, 274, 20);
		getContentPane().add(cmbCascodeGroep);
		cascodegroepModel = new DefaultComboBoxModel<TypeEntry>();
		cmbCascodeGroep.setModel(cascodegroepModel);
		
		JLabel lblGroep = new JLabel("Groep");
		lblGroep.setBounds(32, 14, 46, 14);
		getContentPane().add(lblGroep);
		
		cmbVangnettype = new JComboBox<TypeEntry>();
		cmbVangnettype.setBounds(108, 103, 274, 20);
		getContentPane().add(cmbVangnettype);
		
		chckbxActief = new JCheckBox("Actief");
		chckbxActief.setBounds(108, 126, 97, 23);
		getContentPane().add(chckbxActief);
		
		JLabel lblVangnet = new JLabel("Vangnet");
		lblVangnet.setBounds(32, 106, 46, 14);
		getContentPane().add(lblVangnet);
	}
	protected void okButtonClicked(ActionEvent e) {
		TypeEntry cascodegroeptype;
		TypeEntry vangnettype;
		cascode.setCascode(txtNaam.getText()); 
		cascode.setOmschrijving(txtOmschrijving.getText());
		cascodegroeptype = (TypeEntry) cascodegroepModel.getSelectedItem();
		cascode.setCascodegroep(cascodegroeptype.getValue());
		cascode.setActief(chckbxActief.isSelected());
		vangnettype = (TypeEntry) VangnetModel.getSelectedItem();
		cascode.setVangnettype(__vangnettype.parse(vangnettype.getValue()));
		
        if (this.getLoginSession() != null)
        {
        	try {
        		cascode.validate();
        		ServiceCaller.cascodeFacade(getLoginSession()).updateCascode(cascode);
				super.okButtonClicked(e);
			} catch (ValidationException e1) {
	        	ExceptionLogger.ProcessException(e1,this,false);
			} catch (PermissionException e1) {
	        	ExceptionLogger.ProcessException(e1,this);
			} catch (VerzuimApplicationException e1) {
	        	ExceptionLogger.ProcessException(e1,this);
			} catch (Exception e1){
	        	ExceptionLogger.ProcessException(e1,this);
			}
        }
        else
        	JOptionPane.showMessageDialog(this,"Logic error: loginSession is null");
	}
}
