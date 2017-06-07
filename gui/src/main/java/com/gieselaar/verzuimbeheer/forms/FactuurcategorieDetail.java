package com.gieselaar.verzuimbeheer.forms;

import javax.swing.JOptionPane;

import com.gieselaar.verzuimbeheer.baseforms.BaseDetailform;
import com.gieselaar.verzuimbeheer.baseforms.JDesktopPaneTGI;
import com.gieselaar.verzuimbeheer.components.JTextFieldTGI;
import com.gieselaar.verzuimbeheer.exceptions.VerzuimApplicationException;
import com.gieselaar.verzuimbeheer.exceptions.PermissionException;
import com.gieselaar.verzuimbeheer.exceptions.ValidationException;
import com.gieselaar.verzuimbeheer.services.FactuurcategorieInfo;
import com.gieselaar.verzuimbeheer.services.FactuurkopInfo;
import com.gieselaar.verzuimbeheer.services.InfoBase;
import com.gieselaar.verzuimbeheer.services.BtwInfo.__btwtariefsoort;
import com.gieselaar.verzuimbeheer.utils.ExceptionLogger;
import com.gieselaar.verzuimbeheer.utils.ServiceCaller;
import com.gieselaar.verzuimbeheer.utils.TypeEntry;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JLabel;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import java.util.List;

import java.awt.event.ActionEvent;

public class FactuurcategorieDetail extends BaseDetailform {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private FactuurcategorieInfo factuurcategorie;
	private JComboBox<TypeEntry> cmbBtwSoort;
	private JComboBox<TypeEntry> cmbFactuurkoppen;
	private DefaultComboBoxModel<TypeEntry> btwModel = new DefaultComboBoxModel<TypeEntry>();
	private DefaultComboBoxModel<TypeEntry> factuurkopModel = new DefaultComboBoxModel<TypeEntry>();
	private JTextFieldTGI txtOmschrijving;
	private JCheckBox chckbxIsOmzet;

	private List<FactuurkopInfo> factuurkoppen;
	/**
	 * Create the frame.
	 */
	public FactuurcategorieDetail(JDesktopPaneTGI mdiPanel) {
		super("Beheer factuur categorien", mdiPanel);
		initialize();
	}
	private void initialize() {
		setBounds(0, 237, 439, 175);
		getContentPane().setLayout(null);
		
		cmbBtwSoort = new JComboBox<TypeEntry>();
		cmbBtwSoort.setBounds(122, 3, 239, 20);
		getContentPane().add(cmbBtwSoort);
		
		JLabel lblBtwSoort = new JLabel("BTW soort");
		lblBtwSoort.setBounds(10, 8, 65, 14);
		getContentPane().add(lblBtwSoort);
		
		JLabel lblOmschrijving = new JLabel("Omschrijving");
		lblOmschrijving.setBounds(10, 87, 95, 14);
		getContentPane().add(lblOmschrijving);
		
		txtOmschrijving = new JTextFieldTGI();
		txtOmschrijving.setBounds(123, 83, 238, 20);
		getContentPane().add(txtOmschrijving);
		txtOmschrijving.setColumns(10);
		
		JLabel lblFactuurkop = new JLabel("Factuurkop");
		lblFactuurkop.setBounds(10, 32, 65, 14);
		getContentPane().add(lblFactuurkop);
		
		cmbFactuurkoppen = new JComboBox<TypeEntry>();
		cmbFactuurkoppen.setBounds(122, 27, 239, 20);
		getContentPane().add(cmbFactuurkoppen);
		
		chckbxIsOmzet = new JCheckBox("is omzet");
		chckbxIsOmzet.setBounds(117, 53, 97, 23);
		getContentPane().add(chckbxIsOmzet);
		
	}
	protected void cmbWerkgeverClicked(ActionEvent e) {
	}
	public void setInfo(InfoBase info){
		try {
			factuurcategorie = (FactuurcategorieInfo)info;
		} catch (Exception e1){
        	ExceptionLogger.ProcessException(e1,this);
        	return;
		}
			
		try {
			factuurkoppen = ServiceCaller.factuurFacade(getLoginSession()).getFactuurkoppen();
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

		cmbBtwSoort.setModel(btwModel);
		for (__btwtariefsoort w: __btwtariefsoort.values())
		{
			TypeEntry wg = new TypeEntry(w.getValue(), w.toString());
			btwModel.addElement(wg);
		}
		cmbFactuurkoppen.setModel(factuurkopModel);
		for (FactuurkopInfo w: factuurkoppen)
		{
			TypeEntry wg = new TypeEntry(w.getId(), w.getOmschrijving());
			factuurkopModel.addElement(wg);
		}
		displayFactuurcategorie();
		
		activateListener();
	}
	private void displayFactuurcategorie() {
		TypeEntry type;
		txtOmschrijving.setText(factuurcategorie.getOmschrijving());
		for (int i=0;i<btwModel.getSize();i++)
		{
			type = (TypeEntry) btwModel.getElementAt(i);
			if (factuurcategorie.getBtwcategorie().getValue().equals(type.getValue())){
				btwModel.setSelectedItem(type);
				break;
			}
		}
		for (int i=0;i<factuurkopModel.getSize();i++)
		{
			type = (TypeEntry) factuurkopModel.getElementAt(i);
			if (factuurcategorie.getFactuurkopid() != null){
				if (factuurcategorie.getFactuurkopid().equals(type.getValue())){
					factuurkopModel.setSelectedItem(type);
					break;
				}
			}
		}
		if (factuurcategorie.isIsomzet())
			chckbxIsOmzet.setSelected(true);
		else
			chckbxIsOmzet.setSelected(false);
			
	}
	protected void okButtonClicked(ActionEvent e) {
		TypeEntry selectedBtw = (TypeEntry)cmbBtwSoort.getSelectedItem();
		TypeEntry factuurkop = (TypeEntry)cmbFactuurkoppen.getSelectedItem();
		factuurcategorie.setBtwcategorie(__btwtariefsoort.parse(selectedBtw.getValue()));
		factuurcategorie.setFactuurkopid(factuurkop.getValue());
		factuurcategorie.setIsomzet(chckbxIsOmzet.isSelected());
		factuurcategorie.setOmschrijving(txtOmschrijving.getText());
		if (this.getLoginSession() != null)
        {
        	try {
        		factuurcategorie.validate();
        		switch (this.getMode())
        		{
        			case New: 		ServiceCaller.factuurFacade(getLoginSession()).addFactuurcategorie(factuurcategorie);
        							break;
        			case Update: 	ServiceCaller.factuurFacade(getLoginSession()).updateFactuurcategorie(factuurcategorie);
        							break;
        			case Delete: 	ServiceCaller.factuurFacade(getLoginSession()).deleteFactuurcategorie(factuurcategorie);
        							break;
        		}
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
