package com.gieselaar.verzuim.views;

import com.gieselaar.verzuim.components.JTextFieldTGI;
import com.gieselaar.verzuim.controllers.AbstractController;
import com.gieselaar.verzuim.controllers.ActiviteitController;
import com.gieselaar.verzuim.utils.ExceptionLogger;
import com.gieselaar.verzuim.viewsutils.VerzuimComboBoxModel;
import com.gieselaar.verzuimbeheer.exceptions.VerzuimApplicationException;
import com.gieselaar.verzuimbeheer.services.ActiviteitInfo;
import com.gieselaar.verzuimbeheer.services.ActiviteitInfo.__meldingsoort;
import com.gieselaar.verzuimbeheer.services.ActiviteitInfo.__periodesoort;
import com.gieselaar.verzuimbeheer.services.VerzuimInfo.__vangnettype;
import com.gieselaar.verzuimbeheer.services.InfoBase;
import com.gieselaar.verzuimbeheer.utils.TypeEntry;
import javax.swing.JTextField;
import javax.swing.JLabel;
import javax.swing.JComboBox;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JCheckBox;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class ActiviteitDetail extends AbstractDetail  {

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
	private VerzuimComboBoxModel soortmodelDL;
	private VerzuimComboBoxModel soortmodelHP;
	private VerzuimComboBoxModel soortmodelWP;
	private VerzuimComboBoxModel soortmodelDS;
	private VerzuimComboBoxModel vangnetModel;
	private JCheckBox cbNormaal;
	private JCheckBox cbVangnet;
	private JCheckBox cbKetenverzuim;
	private JCheckBox cbNaActiviteit;
	private JCheckBox cbDeleteNaHerstel;
	private VerzuimComboBoxModel actmodel;
	private ActiviteitController activiteitcontroller; 
	
	public ActiviteitDetail(AbstractController controller) {
		super("Beheer Activiteit", controller);
		activiteitcontroller = (ActiviteitController) controller;
		initialize();
	}
	@Override
	public void setData(InfoBase info){
		activiteit = (ActiviteitInfo)info;
		displayActiviteit();
	}
	private void displayActiviteit(){
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
		

        cmbDeadlinePeriodesoort.setModel(controller.getMaincontroller().getEnumModel(__periodesoort.class));
        cmbHerhaalPeriodesoort.setModel(controller.getMaincontroller().getEnumModel(__periodesoort.class));
        cmbWaarschuwPeriodesoort.setModel(controller.getMaincontroller().getEnumModel(__periodesoort.class));
        cmbDeadlineStartpunt.setModel(controller.getMaincontroller().getEnumModel(__meldingsoort.class));
        cmbVangnettype.setModel(controller.getMaincontroller().getEnumModel(__vangnettype.class));
		soortmodelDL = (VerzuimComboBoxModel) cmbDeadlinePeriodesoort.getModel();
		soortmodelHP = (VerzuimComboBoxModel) cmbHerhaalPeriodesoort.getModel();
		soortmodelWP = (VerzuimComboBoxModel) cmbWaarschuwPeriodesoort.getModel();
		soortmodelDS = (VerzuimComboBoxModel) cmbDeadlineStartpunt.getModel();
		vangnetModel = (VerzuimComboBoxModel) cmbVangnettype.getModel();
        soortmodelDL.setId(activiteit.getDeadlineperiodesoort().getValue());
		soortmodelHP.setId(activiteit.getRepeteerperiodesoort().getValue());
		soortmodelWP.setId(activiteit.getDeadlinewaarschuwmomentsoort().getValue());
		soortmodelDS.setId(activiteit.getDeadlinestartpunt().getValue());

        vangnetModel.addElement(new TypeEntry(-1,"[Alle]"));

        if (activiteit.isVangnet()){
        	if (activiteit.getVangnettype() == null)
        		vangnetModel.setId(-1);
        	else{
        		vangnetModel.setId(activiteit.getVangnettype().getValue());
	        }
        } else {
        	cmbVangnettype.setEnabled(false);
        	vangnetModel.setId(-1);
        }
		
		if (activiteit.getPlannaactiviteit() == null)
			cbNaActiviteit.setSelected(false);
		else
			cbNaActiviteit.setSelected(true);
		cbNaActiviteitClicked(null);
		actmodel = new VerzuimComboBoxModel(controller.getMaincontroller());
		controller.getMaincontroller().updateComboModelActiviteiten(actmodel);
		if (activiteit.getPlannaactiviteit() != null)
			actmodel.setId(activiteit.getPlannaactiviteit());
        cmbPlanNaActiviteit.setModel(actmodel);
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
		cmbPlanNaActiviteit.setModel(new VerzuimComboBoxModel(controller.getMaincontroller()));
		getContentPane().add(cmbPlanNaActiviteit);
		
		cbNaActiviteit = new JCheckBox("plannen na");
		cbNaActiviteit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				cbNaActiviteitClicked(e);
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
		
		cmbVangnettype = new JComboBox<>();
		cmbVangnettype.setBounds(202, 122, 274, 20);
		getContentPane().add(cmbVangnettype);
		
	}
	protected void cbVangnetClicked(ActionEvent e) {
		enableDisableAllFields();
	}
	protected void cbNormaalClicked(ActionEvent e) {
		enableDisableAllFields();
	}
	private void toggleEnable(boolean enableOrDisable){
		cbNaActiviteit.setEnabled(enableOrDisable);
		cbNaActiviteitClicked(null);
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
	private void enableDisableAllFields() {
		if (cbVangnet.isSelected() ||
			cbNormaal.isSelected())
			toggleEnable(true);
		else
			toggleEnable(false);
		
	}
	protected void cbDeleteNaHerstelClicked(ActionEvent e) {
	}
	protected void cbNaActiviteitClicked(ActionEvent e) {
		if (cbNaActiviteit.isSelected()){
			cmbPlanNaActiviteit.setEnabled(true);
			try {
				activiteitcontroller.selectActiviteiten();
			} catch (VerzuimApplicationException e1) {
				ExceptionLogger.ProcessException(e1, this);
			}
		}else{
			cmbPlanNaActiviteit.setEnabled(false);
		}
	}
	@Override
	public InfoBase collectData(){
		Integer soort;
		activiteit.setDuur("");//Wordt momenteel niet gebruikt
		activiteit.setDeadlineperiode(Integer.parseInt(txtDeadlineperiode.getText()));
		activiteit.setRepeteeraantal(parseInt(txtHerhaalaantal));
		activiteit.setNaam(txtNaam.getText());
		activiteit.setOmschrijving(txtOmschrijving.getText());
		activiteit.setRepeteerperiode(parseInt(txtRepeteerperiode));
		activiteit.setDeadlinewaarschuwmoment(parseInt(txtWaarschuwingsperiode));
		activiteit.setKetenverzuim(cbKetenverzuim.isSelected());
		activiteit.setNormaalverzuim(cbNormaal.isSelected());
		activiteit.setVangnet(cbVangnet.isSelected());
		activiteit.setVerwijdernaherstel(cbDeleteNaHerstel.isSelected());

		activiteit.setDeadlineperiodesoort(__periodesoort.parse(soortmodelDL.getId()));
		activiteit.setDeadlinewaarschuwmomentsoort(__periodesoort.parse(soortmodelWP.getId()));
		activiteit.setRepeteerperiodesoort(__periodesoort.parse(soortmodelHP.getId()));
		activiteit.setDeadlinestartpunt(__meldingsoort.parse(soortmodelDS.getId()));

		soort = vangnetModel.getId();
		if (soort == -1)
			activiteit.setVangnettype(__vangnettype.parse(null));
		else
			activiteit.setVangnettype(__vangnettype.parse(soort));
		
		if (cbNaActiviteit.isSelected()){
			Integer planActiviteit = ((VerzuimComboBoxModel)cmbPlanNaActiviteit.getModel()).getId();
			if (planActiviteit != null)
				activiteit.setPlannaactiviteit(planActiviteit);
			else
				activiteit.setPlannaactiviteit(null);
		}
		else
			activiteit.setPlannaactiviteit(null);
		return activiteit;
	}
	private Integer parseInt(JTextField fld){
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
