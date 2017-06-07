package com.gieselaar.verzuimbeheer.forms;

import javax.swing.JOptionPane;

import com.gieselaar.verzuimbeheer.baseforms.BaseDetailform;
import com.gieselaar.verzuimbeheer.baseforms.JDesktopPaneTGI;
import com.gieselaar.verzuimbeheer.components.JTextFieldTGI;
import com.gieselaar.verzuimbeheer.exceptions.VerzuimApplicationException;
import com.gieselaar.verzuimbeheer.exceptions.PermissionException;
import com.gieselaar.verzuimbeheer.exceptions.ValidationException;
import com.gieselaar.verzuimbeheer.services.ActiviteitInfo;
import com.gieselaar.verzuimbeheer.services.ActiviteitInfo.__meldingsoort;
import com.gieselaar.verzuimbeheer.services.ActiviteitInfo.__periodesoort;
import com.gieselaar.verzuimbeheer.services.VerzuimInfo.__vangnettype;
import com.gieselaar.verzuimbeheer.services.InfoBase;
import com.gieselaar.verzuimbeheer.utils.ExceptionLogger;
import com.gieselaar.verzuimbeheer.utils.ServiceCaller;
import com.gieselaar.verzuimbeheer.utils.TypeEntry;
import javax.swing.JTextField;
import javax.swing.JLabel;
import javax.swing.JComboBox;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JCheckBox;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.util.List;

public class ActiviteitDetail extends BaseDetailform  {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2862612204474930868L;
	private ActiviteitInfo activiteit = null;
	private JTextFieldTGI txtNaam;
	private JTextFieldTGI txtDeadlineperiode;
	private JTextFieldTGI txtWaarschuwingsperiode;
	private JTextFieldTGI txtRepeteerperiode;
	private JTextFieldTGI txtHerhaalaantal;
	private JTextFieldTGI txtOmschrijving;
	private JComboBox<TypeEntry> cmbDeadlinePeriodesoort;
	private JComboBox<TypeEntry> cmbWaarschuwPeriodesoort;
	private JComboBox<TypeEntry> cmbDeadlineStartpunt;
	private JComboBox<TypeEntry> cmbHerhaalPeriodesoort;
	private JComboBox<TypeEntry> cmbPlanNaActiviteit;
	private JComboBox<TypeEntry> cmbVangnettype;
	private JCheckBox cbNormaal;
	private JCheckBox cbVangnet;
	private JCheckBox cbKetenverzuim;
	private JCheckBox cbNaActiviteit;
	private JCheckBox cbDeleteNaHerstel;
	private List<ActiviteitInfo> activiteiten;
	@SuppressWarnings("rawtypes")
	private DefaultComboBoxModel actmodel;
	
	/**
	 * Getters and setters of this dialog
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void setInfo(InfoBase info){
		activiteit = (ActiviteitInfo)info;
		txtDeadlineperiode.setText(Integer.toString(activiteit.getDeadlineperiode()));
		if (activiteit.getRepeteeraantal() == null)
			txtHerhaalaantal.setText("");
		else
			txtHerhaalaantal.setText(Integer.toString(activiteit.getRepeteeraantal()));
		txtNaam.setText(activiteit.getNaam());
		txtOmschrijving.setText(activiteit.getOmschrijving());
		if (activiteit.getRepeteerperiode() == null)
			txtRepeteerperiode.setText("");
		else
			txtRepeteerperiode.setText(Integer.toString(activiteit.getRepeteerperiode()));
		if (activiteit.getDeadlinewaarschuwmoment() == null)
			txtWaarschuwingsperiode.setText("");
		else
			txtWaarschuwingsperiode.setText(Integer.toString(activiteit.getDeadlinewaarschuwmoment()));
		cbKetenverzuim.setSelected(activiteit.isKetenverzuim());
		cbNormaal.setSelected(activiteit.isNormaalverzuim());
		cbVangnet.setSelected(activiteit.isVangnet());
		cbDeleteNaHerstel.setSelected(activiteit.isVerwijdernaherstel());
		cbNormaalClicked(null);
		
		DefaultComboBoxModel soortmodelDL = new DefaultComboBoxModel();
		DefaultComboBoxModel soortmodelHP = new DefaultComboBoxModel();
		DefaultComboBoxModel soortmodelWP = new DefaultComboBoxModel();
		DefaultComboBoxModel soortmodelDS = new DefaultComboBoxModel();
		DefaultComboBoxModel VangnetModel = new DefaultComboBoxModel();

        cmbDeadlinePeriodesoort.setModel(soortmodelDL);
        cmbHerhaalPeriodesoort.setModel(soortmodelHP);
        cmbWaarschuwPeriodesoort.setModel(soortmodelWP);
        cmbDeadlineStartpunt.setModel(soortmodelDS);
		for (__periodesoort srt: __periodesoort.values())
		{
			TypeEntry soort = new TypeEntry(srt.getValue(), srt.toString());
			soortmodelDL.addElement(soort);
			if (soort.getValue() == activiteit.getDeadlineperiodesoort().getValue())
				soortmodelDL.setSelectedItem(soort);
			soortmodelHP.addElement(new TypeEntry(srt.getValue(), srt.toString()));
			if (soort.getValue() == activiteit.getRepeteerperiodesoort().getValue())
				soortmodelHP.setSelectedItem(soort);
			soortmodelWP.addElement(new TypeEntry(srt.getValue(), srt.toString()));
			if (soort.getValue() == activiteit.getDeadlinewaarschuwmomentsoort().getValue())
				soortmodelWP.setSelectedItem(soort);
		}
        
		for (__meldingsoort srt: __meldingsoort.values())
		{
			TypeEntry soort = new TypeEntry(srt.getValue(), srt.toString());
			soortmodelDS.addElement(soort);
			if (soort.getValue() == activiteit.getDeadlinestartpunt().getValue())
				soortmodelDS.setSelectedItem(soort);
		}

		VangnetModel = new DefaultComboBoxModel<TypeEntry>();
        cmbVangnettype.setModel(VangnetModel);
        VangnetModel.addElement(new TypeEntry(-1,"[Alle]"));
        for (__vangnettype g: __vangnettype.values())
        {
        	TypeEntry soort = new TypeEntry(g.getValue(), g.toString());
        	VangnetModel.addElement(soort);
        }
        if (activiteit.isVangnet()){
        	if (activiteit.getVangnettype() == null)
        		VangnetModel.setSelectedItem(VangnetModel.getElementAt(0));
        	else{
				for (int i=0;i<VangnetModel.getSize();i++)
				{
					TypeEntry vangnettype = (TypeEntry) VangnetModel.getElementAt(i);
					if (vangnettype.getValue() == activiteit.getVangnettype().getValue())
					{
						VangnetModel.setSelectedItem(vangnettype);
						break;
					}
				}
	        }
        } else {
        	cmbVangnettype.setEnabled(false);
        	VangnetModel.setSelectedItem(VangnetModel.getElementAt(0));
        }
		
		if (activiteit.getPlannaactiviteit() == null)
			cbNaActiviteit.setSelected(false);
		else
			cbNaActiviteit.setSelected(true);
		cbNaActivitietClicked(null);
		actmodel = new DefaultComboBoxModel();
		try {
			activiteiten = ServiceCaller.pakketFacade(getLoginSession()).allActivteiten();
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
		for (ActiviteitInfo a: activiteiten)
		{
			TypeEntry act = new TypeEntry(a.getId(), a.toString());
			actmodel.addElement(act);
			if (activiteit.getPlannaactiviteit() != null)
				if (act.getValue() == activiteit.getPlannaactiviteit())
					actmodel.setSelectedItem(act);
		}
        cmbPlanNaActiviteit.setModel(actmodel);
        activateListener();
	}
	/**
	 * Create the frame.
	 */
	public ActiviteitDetail(JDesktopPaneTGI mdiPanel) {
		super("Beheer Activiteit", mdiPanel);
		initialize();
	}
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void initialize(){
		setBounds(100, 100, 509, 450);
		getContentPane().setLayout(null);
		
		txtNaam = new JTextFieldTGI();
		txtNaam.setBounds(124, 44, 124, 20);
		getContentPane().add(txtNaam);
		txtNaam.setColumns(10);
		
		JLabel lblNaam = new JLabel("Korte naam/code");
		lblNaam.setLabelFor(txtNaam);
		lblNaam.setBounds(30, 47, 84, 14);
		getContentPane().add(lblNaam);
		
		txtDeadlineperiode = new JTextFieldTGI();
		txtDeadlineperiode.setBounds(124, 256, 36, 20);
		getContentPane().add(txtDeadlineperiode);
		txtDeadlineperiode.setColumns(10);
		
		JLabel lblDeadline = new JLabel("Uitvoeren");
		lblDeadline.setLabelFor(txtDeadlineperiode);
		lblDeadline.setBounds(30, 259, 84, 14);
		getContentPane().add(lblDeadline);
		
		cmbDeadlinePeriodesoort = new JComboBox();
		cmbDeadlinePeriodesoort.setBounds(170, 256, 78, 20);
		getContentPane().add(cmbDeadlinePeriodesoort);
		
		JLabel lblWaarschuwen = new JLabel("Waarschuwen");
		lblWaarschuwen.setBounds(30, 284, 96, 14);
		getContentPane().add(lblWaarschuwen);
		
		txtWaarschuwingsperiode = new JTextFieldTGI();
		lblWaarschuwen.setLabelFor(txtWaarschuwingsperiode);
		txtWaarschuwingsperiode.setColumns(10);
		txtWaarschuwingsperiode.setBounds(124, 278, 36, 20);
		getContentPane().add(txtWaarschuwingsperiode);
		
		cmbWaarschuwPeriodesoort = new JComboBox();
		cmbWaarschuwPeriodesoort.setBounds(170, 278, 78, 20);
		getContentPane().add(cmbWaarschuwPeriodesoort);
		
		JLabel lblNa = new JLabel("voor verstrijken deadline");
		lblNa.setBounds(257, 284, 124, 14);
		getContentPane().add(lblNa);
		
		cmbDeadlineStartpunt = new JComboBox();
		cmbDeadlineStartpunt.setModel(new DefaultComboBoxModel(new String[] {"Aanvang verzuim", "Volledig herstel", "Gedeeltelijk herstel"}));
		cmbDeadlineStartpunt.setBounds(293, 256, 145, 20);
		getContentPane().add(cmbDeadlineStartpunt);
		
		JLabel lblRepeteer = new JLabel("Herhaalmoment");
		lblRepeteer.setBounds(30, 309, 84, 14);
		getContentPane().add(lblRepeteer);
		
		txtRepeteerperiode = new JTextFieldTGI();
		lblRepeteer.setLabelFor(txtRepeteerperiode);
		txtRepeteerperiode.setColumns(10);
		txtRepeteerperiode.setBounds(124, 303, 36, 20);
		getContentPane().add(txtRepeteerperiode);
		
		cmbHerhaalPeriodesoort = new JComboBox();
		cmbHerhaalPeriodesoort.setBounds(170, 303, 78, 20);
		getContentPane().add(cmbHerhaalPeriodesoort);
		
		JLabel lblAantal = new JLabel("aantal");
		lblAantal.setBounds(257, 309, 36, 14);
		getContentPane().add(lblAantal);
		
		txtHerhaalaantal = new JTextFieldTGI();
		lblAantal.setLabelFor(txtHerhaalaantal);
		txtHerhaalaantal.setColumns(10);
		txtHerhaalaantal.setBounds(293, 306, 36, 20);
		getContentPane().add(txtHerhaalaantal);
		
		JLabel lblOmschrijving = new JLabel("Omschrijving");
		lblOmschrijving.setBounds(30, 71, 84, 14);
		getContentPane().add(lblOmschrijving);
		
		txtOmschrijving = new JTextFieldTGI();
		lblOmschrijving.setLabelFor(txtOmschrijving);
		txtOmschrijving.setColumns(10);
		txtOmschrijving.setBounds(124, 68, 333, 20);
		getContentPane().add(txtOmschrijving);
		
		JLabel lblPlannenBij = new JLabel("Plannen bij");
		lblPlannenBij.setBounds(30, 96, 62, 14);
		getContentPane().add(lblPlannenBij);
		
		cbNormaal = new JCheckBox("Normaal verzuim");
		cbNormaal.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				cbNormaalClicked(e);
			}
		});
		cbNormaal.setBounds(124, 95, 124, 23);
		getContentPane().add(cbNormaal);
		
		cbVangnet = new JCheckBox("Vangnet");
		cbVangnet.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				cbVangnetClicked(e);
			}
		});
		cbVangnet.setBounds(124, 121, 72, 23);
		getContentPane().add(cbVangnet);
		
		cbKetenverzuim = new JCheckBox("Ketenverzuim");
		cbKetenverzuim.setBounds(124, 206, 97, 23);
		getContentPane().add(cbKetenverzuim);
		
		JLabel lblAlleenPlannenBij = new JLabel("na");
		lblAlleenPlannenBij.setBounds(257, 259, 26, 14);
		getContentPane().add(lblAlleenPlannenBij);
		
		cmbPlanNaActiviteit = new JComboBox();
		cmbPlanNaActiviteit.setBounds(124, 177, 154, 20);
		getContentPane().add(cmbPlanNaActiviteit);
		
		cbNaActiviteit = new JCheckBox("plannen na");
		cbNaActiviteit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				cbNaActivitietClicked(e);
			}
		});
		cbNaActiviteit.setBounds(30, 174, 84, 23);
		getContentPane().add(cbNaActiviteit);
		
		cbDeleteNaHerstel = new JCheckBox("Verwijderen na herstel");
		cbDeleteNaHerstel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				cbDeleteNaHerstelClicked(e);
			}
		});
		cbDeleteNaHerstel.setBounds(124, 226, 145, 23);
		getContentPane().add(cbDeleteNaHerstel);
		
		cmbVangnettype = new JComboBox<TypeEntry>();
		cmbVangnettype.setBounds(202, 122, 274, 20);
		getContentPane().add(cmbVangnettype);
		
	}
	protected void cbVangnetClicked(ActionEvent e) {
		EnableDisableAllFields();
	}
	protected void cbNormaalClicked(ActionEvent e) {
		EnableDisableAllFields();
	}
	private void ToggleEnable(boolean enableOrDisable){
		cbNaActiviteit.setEnabled(enableOrDisable);
		cbNaActivitietClicked(null);
		cbKetenverzuim.setEnabled(enableOrDisable);
		cbDeleteNaHerstel.setEnabled(enableOrDisable);
		txtDeadlineperiode.setEnabled(enableOrDisable);
		txtWaarschuwingsperiode.setEnabled(enableOrDisable);
		txtHerhaalaantal.setEnabled(enableOrDisable);
		txtRepeteerperiode.setEnabled(enableOrDisable);
		cmbDeadlinePeriodesoort.setEnabled(enableOrDisable);
		cmbDeadlineStartpunt.setEnabled(enableOrDisable);
		cmbHerhaalPeriodesoort.setEnabled(enableOrDisable);
		cmbWaarschuwPeriodesoort.setEnabled(enableOrDisable);
		cmbVangnettype.setEnabled(cbVangnet.isSelected());
	}
	private void EnableDisableAllFields() {
		if (cbVangnet.isSelected() ||
			cbNormaal.isSelected())
			ToggleEnable(true);
		else
			ToggleEnable(false);
		
	}
	protected void cbDeleteNaHerstelClicked(ActionEvent e) {
	}
	protected void cbNaActivitietClicked(ActionEvent e) {
		if (cbNaActiviteit.isSelected())
			cmbPlanNaActiviteit.setEnabled(true);
		else
			cmbPlanNaActiviteit.setEnabled(false);
	}
	protected void okButtonClicked(ActionEvent e) {
		TypeEntry soort;
		TypeEntry planActiviteit;
		
		activiteit.setDuur("");//Wordt momenteel niet gebruikt
		activiteit.setDeadlineperiode(Integer.parseInt(txtDeadlineperiode.getText()));
		activiteit.setRepeteeraantal(ParseInt(txtHerhaalaantal));
		activiteit.setNaam(txtNaam.getText());
		activiteit.setOmschrijving(txtOmschrijving.getText());
		activiteit.setRepeteerperiode(ParseInt(txtRepeteerperiode));
		activiteit.setDeadlinewaarschuwmoment(ParseInt(txtWaarschuwingsperiode));
		activiteit.setKetenverzuim(cbKetenverzuim.isSelected());
		activiteit.setNormaalverzuim(cbNormaal.isSelected());
		activiteit.setVangnet(cbVangnet.isSelected());
		activiteit.setVerwijdernaherstel(cbDeleteNaHerstel.isSelected());

		soort = (TypeEntry) cmbDeadlinePeriodesoort.getSelectedItem();
		activiteit.setDeadlineperiodesoort(__periodesoort.parse(soort.getValue()));
		
		soort = (TypeEntry) cmbWaarschuwPeriodesoort.getSelectedItem();
		activiteit.setDeadlinewaarschuwmomentsoort(__periodesoort.parse(soort.getValue()));
		
		soort = (TypeEntry) cmbHerhaalPeriodesoort.getSelectedItem();
		activiteit.setRepeteerperiodesoort(__periodesoort.parse(soort.getValue()));
		
		soort = (TypeEntry) cmbDeadlineStartpunt.getSelectedItem();
		activiteit.setDeadlinestartpunt(__meldingsoort.parse(soort.getValue()));

		soort = (TypeEntry) cmbVangnettype.getSelectedItem();
		if (soort.getValue() == -1)
			activiteit.setVangnettype(__vangnettype.parse(null));
		else
			activiteit.setVangnettype(__vangnettype.parse(soort.getValue()));
		
		if (cbNaActiviteit.isSelected()){
			planActiviteit = (TypeEntry)cmbPlanNaActiviteit.getSelectedItem();
			if (planActiviteit != null)
				activiteit.setPlannaactiviteit(planActiviteit.getValue());
			else
				activiteit.setPlannaactiviteit(null);
		}
		else
			activiteit.setPlannaactiviteit(null);
        if (this.getLoginSession() != null)
        {
        	try {
        		activiteit.validate();
        		ServiceCaller.pakketFacade(getLoginSession()).updateActiviteit(activiteit);
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
	private Integer ParseInt(JTextField fld){
		if (fld.getText().isEmpty())
			return null;
		else
			try {
				return Integer.parseInt(fld.getText());
			}
			catch (Exception e){
				throw new RuntimeException("Ongeldig getal");
			}
	}
}
