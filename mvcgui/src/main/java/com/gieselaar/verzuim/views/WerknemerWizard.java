package com.gieselaar.verzuim.views;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.gieselaar.verzuim.components.AdresPanel;
import com.gieselaar.verzuim.components.JTextFieldTGI;
import com.gieselaar.verzuim.controllers.AbstractController;
import com.gieselaar.verzuim.controllers.AfdelingController;
import com.gieselaar.verzuim.controllers.WerknemerController;
import com.gieselaar.verzuim.utils.ExceptionLogger;
import com.gieselaar.verzuim.viewsutils.VerzuimComboBoxModel;

import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JComboBox;
import com.gieselaar.verzuimbeheer.exceptions.VerzuimApplicationException;
import com.gieselaar.verzuimbeheer.services.AdresInfo;
import com.gieselaar.verzuimbeheer.services.AfdelingHasWerknemerInfo;
import com.gieselaar.verzuimbeheer.services.AfdelingInfo;
import com.gieselaar.verzuimbeheer.services.DienstverbandInfo;
import com.gieselaar.verzuimbeheer.services.InfoBase;
import com.gieselaar.verzuimbeheer.services.WerknemerInfo;
import com.gieselaar.verzuimbeheer.services.WiapercentageInfo;
import com.gieselaar.verzuimbeheer.services.WiapercentageInfo.__wiapercentage;
import com.gieselaar.verzuimbeheer.services.InfoBase.__burgerlijkestaat;
import com.gieselaar.verzuimbeheer.services.InfoBase.__geslacht;
import com.gieselaar.verzuimbeheer.utils.TypeEntry;
import com.michaelbaranov.microba.calendar.DatePicker;

import javax.swing.JPanel;
import javax.swing.border.TitledBorder;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.beans.PropertyVetoException;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.swing.JCheckBox;

public class WerknemerWizard extends AbstractWizard {
	private static final long serialVersionUID = 1L;

	private JComboBox<TypeEntry> cmbWerkgever;
	private JTextFieldTGI txtBSN;
	private JTextFieldTGI txtAchternaam;
	private JTextFieldTGI txtVoornaam;
	private DatePicker dtpGeboortedatum;
	private JTextFieldTGI txtVoorvoegsel;
	private JTextFieldTGI txtVoorletters;
	private JComboBox<TypeEntry> cmbGeslacht;
	private JComboBox<TypeEntry> cmbBurgerlijkestaat;
	private JCheckBox cbArbeidsgehandicapt;
	private DatePicker dtpIngangsdatum;
	private JTextFieldTGI txtPersoneelsnummer;
	private JTextFieldTGI txtFunctie;
	private JTextFieldTGI txtWerkweek;
	private JTextFieldTGI txtEmailadres;
	private JTextFieldTGI txtTelefoonmobiel;
	private JTextFieldTGI txtTelefoonprive;
	private JTextFieldTGI txtTelefoonwerk;
	private JComboBox<TypeEntry> cmbAfdeling;
	private AdresPanel adresPanel;
	private WerknemerController werknemercontroller;
	private String savedBSN = "";
	private VerzuimComboBoxModel werkgevermodel; 
	private VerzuimComboBoxModel afdelingmodel;
	private Integer lastwerkgeverid = -1;

	private JsonNodeFactory factory;
	
	public WerknemerWizard(AbstractController controller) {
		super("Werknemer wizard",controller);
		werknemercontroller = (WerknemerController) controller;
		this.setCloseAfterSave(false);
		initialize();
	}
	@Override
	public void setData(InfoBase info){
		werkgevermodel = new VerzuimComboBoxModel(werknemercontroller);
		cmbWerkgever.setModel(werkgevermodel);
		controller.getMaincontroller().updateComboModelWerkgevers(werkgevermodel, true);

		cmbGeslacht.setModel(controller.getMaincontroller().getEnumModel(__geslacht.class));
		cmbBurgerlijkestaat.setModel(controller.getMaincontroller().getEnumModel(__burgerlijkestaat.class));
		
		afdelingmodel = new VerzuimComboBoxModel(werknemercontroller);
		cmbAfdeling.setModel(afdelingmodel);
		this.adresPanel.setAdres(new AdresInfo());
		
		if (info != null){
			WerknemerInfo werknemer = (WerknemerInfo)info;
			if (werknemer.getDienstVerbanden() != null){
				/*
				 * Update via default update form
				 */
				JOptionPane.showMessageDialog(this, "Werknemer kan worden gewijzigd via standaard wijzigings scherm");
				savedBSN = "";
			}else{
				setFromWerknemer(werknemer);
			}
		}else{
			WerknemerInfo werknemer = new WerknemerInfo();
			werknemer.setWerkgeverid(lastwerkgeverid);
			werknemer.setGeslacht(__geslacht.ONBEKEND);
			werknemer.setBurgerlijkestaat(__burgerlijkestaat.ONBEKEND);
			setFromWerknemer(werknemer);
		}
	}
	private void setFromWerknemer(WerknemerInfo werknemer) {
		((VerzuimComboBoxModel)cmbWerkgever.getModel()).setId(werknemer.getWerkgeverid());
		txtBSN.setText(werknemer.getBurgerservicenummer());
		txtAchternaam.setText(werknemer.getAchternaam());
		txtEmailadres.setText(werknemer.getEmail());
		txtTelefoonmobiel.setText(werknemer.getMobiel());
		txtTelefoonprive.setText(werknemer.getTelefoonPrive());
		txtTelefoonwerk.setText(werknemer.getTelefoon());
		txtVoorletters.setText(werknemer.getVoorletters());
		txtVoorvoegsel.setText(werknemer.getVoorvoegsel());
		if (werknemer.getDienstVerbanden() != null){
			txtFunctie.setText(werknemer.getLaatsteDienstverband().getFunctie());
			txtPersoneelsnummer.setText(werknemer.getLaatsteDienstverband().getPersoneelsnummer());
			if (werknemer.getLaatsteDienstverband().getWerkweek() != null){
				txtWerkweek.setText(werknemer.getLaatsteDienstverband().getWerkweek().toString());
			}
		}
		try {
			dtpGeboortedatum.setDate(werknemer.getGeboortedatum());
			dtpIngangsdatum.setDate(new Date());
		} catch (PropertyVetoException e) {
			ExceptionLogger.ProcessException(e, this);
		}
		if (werknemer.getLaatsteAfdeling() != null){
			((VerzuimComboBoxModel)cmbAfdeling.getModel()).setId(werknemer.getLaatsteAfdeling().getId());
		}else{
			((VerzuimComboBoxModel)cmbAfdeling.getModel()).setId(-1);
		}
		((VerzuimComboBoxModel)cmbGeslacht.getModel()).setId(werknemer.getGeslacht().getValue());
		((VerzuimComboBoxModel)cmbBurgerlijkestaat.getModel()).setId(werknemer.getBurgerlijkestaat().getValue());
		if (adresPanel.getAdres() != null){
			adresPanel.getAdres().setStraat(werknemer.getAdres().getStraat());
			adresPanel.getAdres().setPlaats(werknemer.getAdres().getPlaats());
			adresPanel.getAdres().setHuisnummer(werknemer.getAdres().getHuisnummer());
			adresPanel.getAdres().setHuisnummertoevoeging(werknemer.getAdres().getHuisnummertoevoeging());
			adresPanel.getAdres().setPostcode(werknemer.getAdres().getPostcode());
			adresPanel.getAdres().setLand(werknemer.getAdres().getLand());
		}
	}
	private void initialize(){
		addPanelAlgemeen();
		addPanelAdresEnTelefoon();
	}
	private void addPanelAdresEnTelefoon(){
				
		JPanel panelAdresentelefoon = new JPanel();
		getPanelWizard().add(panelAdresentelefoon, "AdresEnTelefoon");
		panelAdresentelefoon.setLayout(null);
		
		adresPanel = new AdresPanel();
		adresPanel.setAdresSoort("Woonadres");
		adresPanel.setBounds(10, 11, 403, 90);
		panelAdresentelefoon.add(adresPanel);
		
		cmbAfdeling = new JComboBox<>();
		cmbAfdeling.setBounds(117, 209, 286, 20);
		panelAdresentelefoon.add(cmbAfdeling);
		
		JLabel lblAfdeling = new JLabel("Afdeling");
		lblAfdeling.setBounds(18, 212, 89, 14);
		panelAdresentelefoon.add(lblAfdeling);
		
		JLabel label_6 = new JLabel("Telefoon werk");
		label_6.setBounds(21, 115, 75, 14);
		panelAdresentelefoon.add(label_6);
		
		txtTelefoonwerk = new JTextFieldTGI();
		txtTelefoonwerk.setColumns(10);
		txtTelefoonwerk.setBounds(117, 112, 130, 20);
		panelAdresentelefoon.add(txtTelefoonwerk);
		
		JLabel label_7 = new JLabel("priv\u00E9");
		label_7.setBounds(63, 138, 32, 14);
		panelAdresentelefoon.add(label_7);
		
		txtTelefoonprive = new JTextFieldTGI();
		txtTelefoonprive.setColumns(10);
		txtTelefoonprive.setBounds(117, 135, 130, 20);
		panelAdresentelefoon.add(txtTelefoonprive);
		
		JLabel label_8 = new JLabel("mobiel");
		label_8.setBounds(63, 161, 32, 14);
		panelAdresentelefoon.add(label_8);
		
		txtTelefoonmobiel = new JTextFieldTGI();
		txtTelefoonmobiel.setColumns(10);
		txtTelefoonmobiel.setBounds(117, 158, 130, 20);
		panelAdresentelefoon.add(txtTelefoonmobiel);
		
		JLabel label_9 = new JLabel("Emailadres");
		label_9.setBounds(20, 184, 75, 14);
		panelAdresentelefoon.add(label_9);
		
		txtEmailadres = new JTextFieldTGI();
		txtEmailadres.setColumns(50);
		txtEmailadres.setBounds(117, 181, 239, 20);
		panelAdresentelefoon.add(txtEmailadres);
		
	}

	private void addPanelAlgemeen() {
		JPanel panelAlgemeen = new JPanel();
		getPanelWizard().add(panelAlgemeen, "Algemeen");
		panelAlgemeen.setLayout(null);
		
		JLabel label = new JLabel("Werkgever");
		label.setBounds(10, 14, 65, 14);
		panelAlgemeen.add(label);
		
		cmbWerkgever = new JComboBox<TypeEntry>();
		cmbWerkgever.setBounds(107, 11, 239, 20);
		cmbWerkgever.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				AfdelingController controller = new AfdelingController(werknemercontroller.getMaincontroller().getModel().getSession());
				Integer werkgeverid = werkgevermodel.getId();
				controller.setWerkgever(werkgeverid);
				controller.selectAfdelingen();
				List<AfdelingInfo> afdelingen = controller.getAfdelingList();
				if (afdelingmodel != null){
					afdelingmodel.removeAllElements();
					afdelingmodel.addElement(new TypeEntry(-1, "[]"));
					for (AfdelingInfo afd: afdelingen){
						afdelingmodel.addElement(new TypeEntry(afd.getId(), afd.getNaam()));
					}
				}
			}
		});
		panelAlgemeen.add(cmbWerkgever);
		
		JLabel lblBSN = new JLabel("BSN");
		lblBSN.setBounds(10, 37, 46, 14);
		panelAlgemeen.add(lblBSN);
		
		txtBSN = new JTextFieldTGI();
		txtBSN.setColumns(10);
		txtBSN.setBounds(107, 34, 86, 20);
		txtBSN.addFocusListener(new FocusListener() {
			
			@Override
			public void focusLost(FocusEvent e) {
				bsnFieldExit();
			}
			
			@Override
			public void focusGained(FocusEvent e) {/* noop */}
		});
		
		panelAlgemeen.add(txtBSN);
		
		JLabel lblAchternaam = new JLabel("Achternaam");
		lblAchternaam.setBounds(10, 60, 75, 14);
		panelAlgemeen.add(lblAchternaam);
		
		txtAchternaam = new JTextFieldTGI();
		txtAchternaam.setColumns(10);
		txtAchternaam.setBounds(107, 57, 152, 20);
		panelAlgemeen.add(txtAchternaam);
		
		JLabel lblVoornaam = new JLabel("Voornaam");
		lblVoornaam.setBounds(10, 83, 74, 14);
		panelAlgemeen.add(lblVoornaam);
		
		txtVoornaam = new JTextFieldTGI();
		txtVoornaam.setColumns(10);
		txtVoornaam.setBounds(107, 80, 152, 20);
		panelAlgemeen.add(txtVoornaam);
		
		JLabel lblGeboortedatum = new JLabel("Geboortedatum");
		lblGeboortedatum.setBounds(11, 107, 75, 14);
		panelAlgemeen.add(lblGeboortedatum);
		
		dtpGeboortedatum = new DatePicker();
		dtpGeboortedatum.setBounds(107, 103, 112, 21);
		dtpGeboortedatum.setDateFormat(new SimpleDateFormat("dd-MM-yyyy"));
		panelAlgemeen.add(dtpGeboortedatum);
		
		JLabel lblVoorvoegsel = new JLabel("Voorvoegsel");
		lblVoorvoegsel.setBounds(272, 60, 65, 14);
		panelAlgemeen.add(lblVoorvoegsel);
		
		txtVoorvoegsel = new JTextFieldTGI();
		txtVoorvoegsel.setColumns(10);
		txtVoorvoegsel.setBounds(344, 57, 86, 20);
		panelAlgemeen.add(txtVoorvoegsel);
		
		JLabel lblVoorletters = new JLabel("Voorletters");
		lblVoorletters.setBounds(272, 83, 65, 14);
		panelAlgemeen.add(lblVoorletters);
		
		txtVoorletters = new JTextFieldTGI();
		txtVoorletters.setColumns(10);
		txtVoorletters.setBounds(344, 80, 86, 20);
		panelAlgemeen.add(txtVoorletters);
		
		JLabel label_20 = new JLabel("Geslacht");
		label_20.setBounds(10, 129, 64, 14);
		panelAlgemeen.add(label_20);
		
		cmbGeslacht = new JComboBox<TypeEntry>();
		cmbGeslacht.setMaximumRowCount(3);
		cmbGeslacht.setBounds(106, 126, 62, 20);
		panelAlgemeen.add(cmbGeslacht);
		
		cmbBurgerlijkestaat = new JComboBox<TypeEntry>();
		cmbBurgerlijkestaat.setMaximumRowCount(3);
		cmbBurgerlijkestaat.setBounds(107, 149, 130, 20);
		panelAlgemeen.add(cmbBurgerlijkestaat);
		
		cbArbeidsgehandicapt = new JCheckBox("Arbeidsgehandicapt");
		cbArbeidsgehandicapt.setBounds(344, 131, 130, 23);
		panelAlgemeen.add(cbArbeidsgehandicapt);
		
		JLabel label_25 = new JLabel("Burg. staat");
		label_25.setBounds(10, 152, 65, 14);
		panelAlgemeen.add(label_25);
		
		JPanel panel = new JPanel();
		panel.setLayout(null);
		panel.setBorder(new TitledBorder(null, "Dienstverband", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		panel.setBounds(0, 180, 311, 119);
		panelAlgemeen.add(panel);
		
		JLabel label_11 = new JLabel("Ingangsdatum");
		label_11.setBounds(26, 24, 89, 14);
		panel.add(label_11);
		
		dtpIngangsdatum = new DatePicker();
		dtpIngangsdatum.setBounds(141, 21, 89, 21);
		panel.add(dtpIngangsdatum);
		
		JLabel label_12 = new JLabel("Personeelsnummer");
		label_12.setBounds(26, 47, 104, 14);
		panel.add(label_12);
		
		txtPersoneelsnummer = new JTextFieldTGI();
		txtPersoneelsnummer.setColumns(45);
		txtPersoneelsnummer.setBounds(141, 44, 125, 20);
		panel.add(txtPersoneelsnummer);
		
		JLabel label_13 = new JLabel("Functie");
		label_13.setBounds(26, 70, 46, 14);
		panel.add(label_13);
		
		txtFunctie = new JTextFieldTGI();
		txtFunctie.setColumns(45);
		txtFunctie.setBounds(141, 67, 125, 20);
		panel.add(txtFunctie);
		
		JLabel label_14 = new JLabel("Werkweek");
		label_14.setBounds(26, 93, 65, 14);
		panel.add(label_14);
		
		txtWerkweek = new JTextFieldTGI();
		txtWerkweek.setBounds(141, 90, 104, 20);
		panel.add(txtWerkweek);
	}
	protected void bsnFieldExit() {
		try{
			if (savedBSN.equals(txtBSN.getText().trim())){
				/* nothing changed */
			} else {
				Integer werkgeverid = ((VerzuimComboBoxModel)cmbWerkgever.getModel()).getId();
				savedBSN = txtBSN.getText();
				werknemercontroller.validateBurgerservicenummer(this, txtBSN.getText().trim(),werkgeverid);
			}
		}catch(VerzuimApplicationException e){
        	ExceptionLogger.ProcessException(e,this);
		}
		
	}
	private String formatTelefoonr(String telnr) {
		if (telnr.isEmpty())
			return telnr;
		if (telnr.startsWith("+") || telnr.startsWith("0")){
			telnr = telnr.replace("-", "");
			telnr = telnr.replace(" ", "");
			telnr = telnr.replace(".", "");
			return telnr;
		}else{
			return "0" + telnr;
		}
	}

	@Override 
	public InfoBase collectData(){
		ParsePosition pos = new ParsePosition(0);
		DecimalFormat format = new DecimalFormat("##0,00");
		BigDecimal uren;
		format.setParseBigDecimal(true);

		WerknemerInfo werknemer = new WerknemerInfo();
		werknemer.setAdres(adresPanel.getAdres());

		werknemer.setWerkgeverid(((VerzuimComboBoxModel)cmbWerkgever.getModel()).getId());
		
		werknemer.setAchternaam(txtAchternaam.getText().trim());
		werknemer.setBurgerservicenummer(txtBSN.getText().trim());
		werknemer.setEmail(txtEmailadres.getText().trim());
		werknemer.setMobiel(formatTelefoonr(txtTelefoonmobiel.getText().trim()));
		werknemer.setTelefoonPrive(formatTelefoonr(txtTelefoonprive.getText().trim()));
		werknemer.setTelefoon(formatTelefoonr(txtTelefoonwerk.getText().trim()));
		werknemer.setVoorletters(txtVoorletters.getText().trim());
		werknemer.setVoornaam(txtVoornaam.getText().trim());
		werknemer.setVoorvoegsel(txtVoorvoegsel.getText().trim());
		werknemer.setGeboortedatum(dtpGeboortedatum.getDate());
        int geslacht = ((VerzuimComboBoxModel)cmbGeslacht.getModel()).getId();
        werknemer.setGeslacht(__geslacht.parse(geslacht));
        int burgerlijkestaat = ((VerzuimComboBoxModel)cmbBurgerlijkestaat.getModel()).getId();
        werknemer.setBurgerlijkestaat(__burgerlijkestaat.parse(burgerlijkestaat));
        werknemer.setArbeidsgehandicapt(cbArbeidsgehandicapt.isSelected());
        
        DienstverbandInfo dvb = new DienstverbandInfo();
        dvb.setStartdatumcontract(dtpIngangsdatum.getDate());
        dvb.setEinddatumcontract(null);
        dvb.setPersoneelsnummer(txtPersoneelsnummer.getText());
        dvb.setWerkgeverId(werknemer.getWerkgeverid());
		uren = (BigDecimal)format.parse(txtWerkweek.getText(),pos);
		if (pos.getIndex() < txtWerkweek.getText().length()){
			JOptionPane.showMessageDialog(this, "Werkweek bevat ongeldige tekens");
			return null;
		}
        dvb.setWerkweek(uren);
        werknemer.setDienstVerbanden(new ArrayList<DienstverbandInfo>());
        werknemer.getDienstVerbanden().add(dvb);
        
        AfdelingHasWerknemerInfo afd = new AfdelingHasWerknemerInfo();
        afd.setAfdelingId(((VerzuimComboBoxModel)cmbAfdeling.getModel()).getId());
        if (afd.getAfdelingId() == -1){
			JOptionPane.showMessageDialog(this, "Afdeling niet ingevuld");
			return null;
        }
        afd.setStartdatum(dtpIngangsdatum.getDate());
        werknemer.setAfdelingen(new ArrayList<AfdelingHasWerknemerInfo>());
        werknemer.getAfdelingen().add(afd);
        
        WiapercentageInfo wia = new WiapercentageInfo();
        wia.setStartdatum(dtpIngangsdatum.getDate());
        wia.setCodeWiaPercentage(__wiapercentage.NVT);
        werknemer.setWiaPercentages(new ArrayList<WiapercentageInfo>());
        werknemer.getWiaPercentages().add(wia);
        
        lastwerkgeverid = werknemer.getWerkgeverid();
		return werknemer;
	}
	private void addField(String name, String value, ArrayNode fields){
		ObjectNode fieldnode = factory.objectNode();
		fieldnode.put(name, value);
		fields.add(fieldnode);
		
	}
	@Override
	protected void saveIntermediateresults() {
		
		factory = new JsonNodeFactory(true);
		ObjectNode rootnode = factory.objectNode();
		rootnode.put("WerknemerWizard", "");
		
		ObjectNode nodeAlgemeen = factory.objectNode();
		ArrayNode fields = factory.arrayNode();
		nodeAlgemeen.put("Fields",fields);
		
		addField("Werkgever", ((VerzuimComboBoxModel)cmbWerkgever.getModel()).getId().toString(), fields);
		addField("BSN", txtBSN.getText(), fields);
		addField("Achternaam", txtAchternaam.getText(), fields);
		addField("Emailadres", txtEmailadres.getText(), fields);
		addField("Functie", txtFunctie.getText(), fields);
		addField("Personeelsnummer", txtPersoneelsnummer.getText(), fields);
		addField("Telefoonmobiel", txtTelefoonmobiel.getText(), fields);
		addField("Telefoonprive", txtTelefoonprive.getText(), fields);
		addField("Telefoonwerk", txtTelefoonwerk.getText(), fields);
		addField("Voorletters", txtVoorletters.getText(), fields);
		addField("Voorvoegsel", txtVoorvoegsel.getText(), fields);
		addField("Werkweek", txtWerkweek.getText(), fields);
		addField("Geboortedatum", getFormattedDate(dtpGeboortedatum.getDate()), fields);
		addField("Ingangsdatum", getFormattedDate(dtpIngangsdatum.getDate()), fields);
		addField("Afdeling", ((VerzuimComboBoxModel)cmbAfdeling.getModel()).getId().toString(), fields);
		addField("Geslacht", ((VerzuimComboBoxModel)cmbGeslacht.getModel()).getId().toString(), fields);
		addField("Burgerlijkestaat", ((VerzuimComboBoxModel)cmbBurgerlijkestaat.getModel()).getId().toString(), fields);
		if (adresPanel.getAdres() != null){
			addField("Straat", adresPanel.getAdres().getStraat(), fields);
			addField("Plaats", adresPanel.getAdres().getPlaats(), fields);
			addField("Huisnummer", adresPanel.getAdres().getHuisnummer(), fields);
			addField("Huisnummer toevoeging", adresPanel.getAdres().getHuisnummertoevoeging(), fields);
			addField("Postcode", adresPanel.getAdres().getPostcode(), fields);
			addField("Land", adresPanel.getAdres().getLand(), fields);
		}
		rootnode.put("Algemeen", nodeAlgemeen);
		
		super.savetoFile(rootnode);
	}
	private String getFormattedDate(Date date) {
		DateFormat format =  new SimpleDateFormat("yyyy-MM-dd:HH:mm:ss");
		return format.format(date);
	}
	private Date getDateFromFormattedDate(String stringdate){
		DateFormat format =  new SimpleDateFormat("yyyy-MM-dd:HH:mm:ss");
		Date date;
		try {
			date = format.parse(stringdate);
		} catch (ParseException e) {
			date = new Date();
		}
		return date;
	}
	public JsonNode getField(String name, ArrayNode array){
		JsonNode node = array.findValue(name);
		return node;
	}
	@Override
	protected void loadIntermerdiateresults() {
		JsonNode rootnode = super.loadfromFile();
		JsonNode node;
		ArrayNode array;
		node = rootnode.path("WerknemerWizard");
		node = rootnode.path("Algemeen");
		array = (ArrayNode) node.path("Fields");
		
		((VerzuimComboBoxModel)cmbWerkgever.getModel()).setId(getField("Werkgever",array).asInt());
		txtBSN.setText(getField("BSN",array).textValue());
		txtAchternaam.setText(getField("Achternaam",array).textValue());
		txtEmailadres.setText(getField("Emailadres",array).textValue());
		txtFunctie.setText(getField("Functie",array).textValue());
		txtPersoneelsnummer.setText(getField("Personeelsnummer",array).textValue());
		txtTelefoonmobiel.setText(getField("Telefoonmobiel",array).textValue());
		txtTelefoonprive.setText(getField("Telefoonprive",array).textValue());
		txtTelefoonwerk.setText(getField("Telefoonwerk",array).textValue());
		txtVoorletters.setText(getField("Voorletters",array).textValue());
		txtVoorvoegsel.setText(getField("Voorvoegsel",array).textValue());
		txtWerkweek.setText(getField("Werkweek",array).textValue());
		
		try {
			dtpGeboortedatum.setDate(getDateFromFormattedDate(getField("Geboortedatum",array).textValue()));
			dtpIngangsdatum.setDate(getDateFromFormattedDate(getField("Ingangsdatum",array).textValue()));
		} catch (PropertyVetoException e) {
			ExceptionLogger.ProcessException(e, this);
		}
		((VerzuimComboBoxModel)cmbAfdeling.getModel()).setId(getField("Afdeling",array).asInt());
		((VerzuimComboBoxModel)cmbGeslacht.getModel()).setId(getField("Geslacht",array).asInt());
		((VerzuimComboBoxModel)cmbBurgerlijkestaat.getModel()).setId(getField("Burgerlijkestaat",array).asInt());
		if (adresPanel.getAdres() != null){
			adresPanel.getAdres().setStraat(getField("Straat",array).textValue() );
			adresPanel.getAdres().setPlaats(getField("Plaats",array).textValue());
			adresPanel.getAdres().setHuisnummer(getField("Huisnummer",array).textValue());
			adresPanel.getAdres().setHuisnummertoevoeging(getField("Huisnummer toevoeging", array).textValue());
			adresPanel.getAdres().setPostcode(getField("Postcode", array).textValue());
			adresPanel.getAdres().setLand(getField("Land",array).textValue());
		}
	}
}
