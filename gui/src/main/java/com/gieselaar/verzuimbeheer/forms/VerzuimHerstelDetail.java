package com.gieselaar.verzuimbeheer.forms;

import java.awt.event.ActionEvent;
import java.beans.PropertyVetoException;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import com.gieselaar.verzuimbeheer.baseforms.BaseDetailform;
import com.gieselaar.verzuimbeheer.components.JTextFieldTGI;
import com.gieselaar.verzuimbeheer.baseforms.JDesktopPaneTGI;
import com.gieselaar.verzuimbeheer.components.JTextAreaTGI;
import com.gieselaar.verzuimbeheer.exceptions.VerzuimApplicationException;
import com.gieselaar.verzuimbeheer.exceptions.PermissionException;
import com.gieselaar.verzuimbeheer.exceptions.ValidationException;
import com.gieselaar.verzuimbeheer.services.GebruikerInfo;
import com.gieselaar.verzuimbeheer.services.InfoBase;
import com.gieselaar.verzuimbeheer.services.VerzuimHerstelInfo;
import com.gieselaar.verzuimbeheer.services.VerzuimInfo.__meldingswijze;
import com.gieselaar.verzuimbeheer.utils.ExceptionLogger;
import com.gieselaar.verzuimbeheer.utils.ServiceCaller;
import com.gieselaar.verzuimbeheer.utils.ServiceLocatorException;
import com.gieselaar.verzuimbeheer.utils.TypeEntry;
import com.michaelbaranov.microba.calendar.DatePicker;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JOptionPane;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JComboBox;

public class VerzuimHerstelDetail extends BaseDetailform {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JTextFieldTGI txtHerstel;
	private JTextFieldTGI txtHerstelAT;
	private DatePicker dtpDatumherstel;
	private DatePicker dtpMeldingsdatum;
	private JTextAreaTGI taOpmerkingen;
	private VerzuimHerstelInfo verzuimherstel;
	private List<GebruikerInfo> gebruikers;
	private JComboBox<TypeEntry> cmbMeldingswijze;
	private JTextFieldTGI txtGebruiker;
	
	public VerzuimHerstelDetail(JDesktopPaneTGI mdiPanel) {
		super("Verzuim herstel", mdiPanel);
		initialize();
	}
	public void setInfo(InfoBase info){
		TypeEntry Meldingswijze;
		DefaultComboBoxModel<TypeEntry> MeldingswijzeModel;
		NumberFormat nf = NumberFormat.getNumberInstance();
		DecimalFormat df = (DecimalFormat)nf;
		df.applyPattern("##0.00");
		
		verzuimherstel = (VerzuimHerstelInfo) info;
		try {
			if (this.getMode() == formMode.New){
				verzuimherstel.setDatumHerstel(new Date());
				verzuimherstel.setMeldingsdatum(new Date());
				verzuimherstel.setOpmerkingen("");
				verzuimherstel.setPercentageHerstel(new BigDecimal(100));
				verzuimherstel.setPercentageHerstelAT(new BigDecimal(0));
				verzuimherstel.setUser(this.getLoginSession().getGebruiker().getId());
			}
				
			gebruikers = ServiceCaller.verzuimFacade(getLoginSession()).getGebruikers();

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

		MeldingswijzeModel = new DefaultComboBoxModel<TypeEntry>();
        cmbMeldingswijze.setModel(MeldingswijzeModel);
        for (__meldingswijze w: __meldingswijze.values())
        {
        	TypeEntry wijze = new TypeEntry(w.getValue(), w.toString());
        	MeldingswijzeModel.addElement(wijze);
        }
        
    	try {
            for (int i=0;i<MeldingswijzeModel.getSize();i++)
    		{
    			Meldingswijze = (TypeEntry) MeldingswijzeModel.getElementAt(i);
    			if (__meldingswijze.parse(Meldingswijze.getValue()) == verzuimherstel.getMeldingswijze())
    			{
    				MeldingswijzeModel.setSelectedItem(Meldingswijze);
    				break;
    			}
    		}
			dtpDatumherstel.setDate(verzuimherstel.getDatumHerstel());
			dtpMeldingsdatum.setDate(verzuimherstel.getMeldingsdatum());
			txtHerstel.setText(df.format(verzuimherstel.getPercentageHerstel()));
			txtHerstelAT.setText(df.format(verzuimherstel.getPercentageHerstelAT()));
		} catch (PropertyVetoException e) {
			ExceptionLogger.ProcessException(e,this);		
		}
		for (GebruikerInfo gi: gebruikers)
			if (gi.getId().equals(verzuimherstel.getUser())){
				txtGebruiker.setText(gi.getAchternaam());
				break;
			}
        activateListener();
	}
	void initialize(){
		setBounds(100, 100, 450, 368);
		dtpDatumherstel = new DatePicker();
		dtpDatumherstel.setBounds(144, 36, 83, 21);
		dtpDatumherstel.setDateFormat(new SimpleDateFormat("dd-MM-yyyy"));
		getContentPane().add(dtpDatumherstel);
		
		dtpMeldingsdatum = new DatePicker();
		dtpMeldingsdatum.setBounds(144, 59, 83, 21);
		dtpMeldingsdatum.setDateFormat(new SimpleDateFormat("dd-MM-yyyy"));
		getContentPane().add(dtpMeldingsdatum);
		
		txtHerstel = new JTextFieldTGI();
		txtHerstel.setBounds(144, 82, 83, 20);
		getContentPane().add(txtHerstel);
		txtHerstel.setColumns(10);
		
		txtHerstelAT = new JTextFieldTGI();
		txtHerstelAT.setBounds(144, 105, 83, 20);
		getContentPane().add(txtHerstelAT);
		txtHerstelAT.setColumns(10);
		
		JLabel lblHersteldatum = new JLabel("Hersteldatum");
		lblHersteldatum.setBounds(10, 39, 69, 14);
		getContentPane().add(lblHersteldatum);
		
		JLabel lblMeldingsdatum = new JLabel("Meldingsdatum");
		lblMeldingsdatum.setBounds(10, 62, 95, 14);
		getContentPane().add(lblMeldingsdatum);
		
		JLabel lblPercentageHerstel = new JLabel("Percentage herstel");
		lblPercentageHerstel.setBounds(10, 85, 95, 14);
		getContentPane().add(lblPercentageHerstel);
		
		JLabel lblPercentageHerstelAt = new JLabel("Percentage herstel AT");
		lblPercentageHerstelAt.setBounds(10, 108, 121, 14);
		getContentPane().add(lblPercentageHerstelAt);
		
		JLabel lblOpmerkingen = new JLabel("Opmerkingen");
		lblOpmerkingen.setBounds(10, 164, 69, 14);
		getContentPane().add(lblOpmerkingen);
		
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(144, 164, 280, 133);
		getContentPane().add(scrollPane);
		
		taOpmerkingen = new JTextAreaTGI();
		scrollPane.setViewportView(taOpmerkingen);
		
		JLabel lblUitgevoerdDoor = new JLabel("Ingevoerd door");
		lblUitgevoerdDoor.setBounds(10, 131, 95, 14);
		getContentPane().add(lblUitgevoerdDoor);
		
		txtGebruiker = new JTextFieldTGI();
		txtGebruiker.setEditable(false);
		txtGebruiker.setEnabled(false);
		txtGebruiker.setBounds(144, 128, 134, 20);
		getContentPane().add(txtGebruiker);
		txtGebruiker.setColumns(10);
		
		JLabel lblNewLabel = new JLabel("via:");
		lblNewLabel.setBounds(237, 62, 31, 14);
		getContentPane().add(lblNewLabel);
		
		cmbMeldingswijze = new JComboBox<TypeEntry>();
		cmbMeldingswijze.setBounds(264, 59, 127, 20);
		getContentPane().add(cmbMeldingswijze);
	}
	protected void okButtonClicked(ActionEvent e) {
		TypeEntry meldingswijze;
		ParsePosition pos = new ParsePosition(0);
		DecimalFormat format = new DecimalFormat("##0,00");
		BigDecimal percentage;
		format.setParseBigDecimal(true);
		boolean cleanupTodo = false;

		verzuimherstel.setDatumHerstel(dtpDatumherstel.getDate());
		verzuimherstel.setMeldingsdatum(dtpMeldingsdatum.getDate());
		verzuimherstel.setOpmerkingen(taOpmerkingen.getText());
		meldingswijze = (TypeEntry)cmbMeldingswijze.getSelectedItem();
		verzuimherstel.setMeldingswijze(__meldingswijze.parse(meldingswijze.getValue()));

		if (verzuimherstel.getDatumHerstel().before(verzuimherstel.getVerzuim().getStartdatumverzuim())){
			JOptionPane.showMessageDialog(this, "Datum herstel ligt voor startdatum verzuim");
			return;
		}
		
		percentage = (BigDecimal)format.parse(txtHerstel.getText(),pos);
		if (pos.getIndex() < txtHerstel.getText().length())
		{
			JOptionPane.showMessageDialog(this, "Herstel percentage bevat ongeldige tekens");
			return;
		}
		verzuimherstel.setPercentageHerstel(percentage);
		
		pos.setIndex(0);
		percentage = (BigDecimal)format.parse(txtHerstelAT.getText(),pos);
		if (pos.getIndex() < txtHerstelAT.getText().length())
		{
			JOptionPane.showMessageDialog(this, "Herstel percentage AT bevat ongeldige tekens");
			return;
		}
		verzuimherstel.setPercentageHerstelAT(percentage);

		if (verzuimherstel.getPercentageHerstel().compareTo(new BigDecimal(100)) == 0) {
			/*
			 * Vraag of de todo's opgeschoond moeten worden.
			 */
			if (JOptionPane.showConfirmDialog(this, "Wilt U de Todo's voor dit verzuim opschonen?",
					"Opslaan", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
				cleanupTodo = true;
			}
		}

		if (this.getLoginSession() != null)
        {
        	try {
        		verzuimherstel.validate();
        		switch (this.getMode()){
        			case New: 
        				ServiceCaller.verzuimFacade(getLoginSession()).addVerzuimHerstel(verzuimherstel, cleanupTodo);
        				break;
        			case Update: 
        				ServiceCaller.verzuimFacade(getLoginSession()).updateVerzuimHerstel(verzuimherstel, cleanupTodo);
        				break;
        			case Delete: 
        				ServiceCaller.verzuimFacade(getLoginSession()).deleteVerzuimHerstel(verzuimherstel);
        				break;
        		}
		        super.okButtonClicked(e);
			} catch (ValidationException e1) {
	        	ExceptionLogger.ProcessException(e1,this, false);
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
