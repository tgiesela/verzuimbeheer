package com.gieselaar.verzuimbeheer.forms;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.util.List;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import com.gieselaar.verzuimbeheer.baseforms.BaseDialog;
import com.gieselaar.verzuimbeheer.components.JTextFieldTGI;
import com.gieselaar.verzuimbeheer.exceptions.VerzuimApplicationException;
import com.gieselaar.verzuimbeheer.exceptions.PermissionException;
import com.gieselaar.verzuimbeheer.exceptions.ValidationException;
import com.gieselaar.verzuimbeheer.facades.LoginSessionRemote;
import com.gieselaar.verzuimbeheer.services.OeInfo;
import com.gieselaar.verzuimbeheer.services.InfoBase;
import com.gieselaar.verzuimbeheer.services.OeNiveauInfo;
import com.gieselaar.verzuimbeheer.services.WerkgeverInfo;
import com.gieselaar.verzuimbeheer.utils.ExceptionLogger;
import com.gieselaar.verzuimbeheer.utils.ServiceCaller;
import com.gieselaar.verzuimbeheer.utils.ServiceLocatorException;
import com.gieselaar.verzuimbeheer.utils.TypeEntry;

import javax.swing.JComboBox;


public class OeDetail extends BaseDialog {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private OeInfo oe = null;
	private List<WerkgeverInfo> werkgevers;	
	private List<OeNiveauInfo> oeniveaus;	
	private JTextFieldTGI txtNaam;
	private JComboBox<TypeEntry> cmbWerkgever = new JComboBox<TypeEntry>();
	private JComboBox<TypeEntry> cmbOeNiveau = new JComboBox<TypeEntry>();
	private LoginSessionRemote loginSession;

	public void setLoginSession(LoginSessionRemote loginSession) {
		this.loginSession = loginSession;
	}
	/**
	 * Create the frame.
	 */
	public OeDetail(JFrame jFrame, boolean modal) {
		super(jFrame, modal, "Rapportage eenheid detail");
		initialize();
	}
	public void setInfo(InfoBase info){
		try {
			werkgevers = ServiceCaller.werkgeverFacade(loginSession).allWerkgeversList();
			oeniveaus = ServiceCaller.werkgeverFacade(loginSession).getOeNiveaus();
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
		oe = (OeInfo)info;
		
		WerkgeverInfo.sort(werkgevers, WerkgeverInfo.__sortcol.NAAM);
		
		DefaultComboBoxModel<TypeEntry> soortmodelWG  = new DefaultComboBoxModel<TypeEntry>();
		DefaultComboBoxModel<TypeEntry> soortmodelOEN = new DefaultComboBoxModel<TypeEntry>();

        cmbWerkgever.setModel(soortmodelWG);
		TypeEntry dummy = new TypeEntry(-1,"[geen]");
		soortmodelWG.addElement(dummy);
        for (WerkgeverInfo wgr: werkgevers)
		{
			TypeEntry entry = new TypeEntry(wgr.getId(), wgr.getNaam());
			soortmodelWG.addElement(entry);
		}

        cmbOeNiveau.setModel(soortmodelOEN);
        for (OeNiveauInfo oeniveau: oeniveaus)
		{
			TypeEntry entry = new TypeEntry(oeniveau.getId(), oeniveau.getNaam());
			soortmodelOEN.addElement(entry);
		}

		if (oe.getWerkgeverId() != null){
			for (int i = 0; i < soortmodelWG.getSize(); i++) {
				TypeEntry werkgever = (TypeEntry) soortmodelWG.getElementAt(i);
				if (werkgever.getValue() == oe.getWerkgeverId()) {
					soortmodelWG.setSelectedItem(werkgever);
					break;
				}
			}
		}
        
		if (oe.getOeniveau() != null){
			for (int i = 0; i < soortmodelOEN.getSize(); i++) {
				TypeEntry oeniveau = (TypeEntry) soortmodelOEN.getElementAt(i);
				if (oeniveau.getValue() == oe.getOeniveau().getOeniveau()) {
					soortmodelOEN.setSelectedItem(oeniveau);
					break;
				}
			}
		}
        
		txtNaam.setText(oe.getNaam());
		//txtOmschrijving.setText(oe.getOeniveau().toString());
		
		activateListener();
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
	protected void okButtonClicked(ActionEvent e) {
		TypeEntry oeniveau = null;
		TypeEntry werkgever = null;
		
		oe.setNaam(txtNaam.getText()); 
		
		oeniveau = (TypeEntry) cmbOeNiveau.getSelectedItem();
		for (OeNiveauInfo o:oeniveaus){
			if (o.getId() == oeniveau.getValue()){
				oe.setOeniveau(o);
				break;
			}
		}
			
		//oe.setOeniveau(new OeNiveauInfo());
		//oe.getOeniveau().setId(oeniveau.getValue());

		werkgever = (TypeEntry)cmbWerkgever.getSelectedItem();
		if (werkgever.getValue() == -1)
			oe.setWerkgeverId(null);
		else{
			oe.setWerkgeverId(werkgever.getValue());
			oe.setNaam(werkgever.toString());
		}
		
        if (this.loginSession != null)
        {
        	try {
        		oe.validate();
        		ServiceCaller.werkgeverFacade(loginSession).updateOe(oe);
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
