package com.gieselaar.verzuim.views;

import javax.swing.JButton;
import javax.swing.JOptionPane;
import java.beans.PropertyVetoException;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.List;

import javax.swing.JLabel;

import com.gieselaar.verzuim.components.AdresPanel;
import com.gieselaar.verzuim.components.ContactpersoonPanel;
import com.gieselaar.verzuim.components.JTextFieldTGI;
import com.gieselaar.verzuim.controllers.AbstractController;
import com.gieselaar.verzuim.controllers.AbstractController.__formmode;
import com.gieselaar.verzuim.controllers.WerkgeverController;
import com.gieselaar.verzuim.interfaces.DefaultControllerEventListener;
import com.gieselaar.verzuim.utils.ExceptionLogger;
import com.gieselaar.verzuim.viewsutils.VerzuimComboBoxModel;
import com.gieselaar.verzuimbeheer.facades.AutorisatieFacadeRemote.__applicatiefunctie;
import com.gieselaar.verzuimbeheer.services.ArbodienstInfo;
import com.gieselaar.verzuimbeheer.services.BedrijfsartsInfo;
import com.gieselaar.verzuimbeheer.services.InfoBase;
import com.gieselaar.verzuimbeheer.services.WerkgeverInfo;
import com.gieselaar.verzuimbeheer.utils.TypeEntry;
import com.michaelbaranov.microba.calendar.DatePicker;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.border.TitledBorder;
import javax.swing.JCheckBox;

public class WerkgeverDetail extends AbstractDetail{
	private static final long serialVersionUID = 1L;
	private JTextFieldTGI txtNaam;
	private JTextFieldTGI txtWerkweek;
	private DatePicker dtpStartdate; 
	private DatePicker dtpEnddate;
	private JButton btnPakketten;
	private JButton btnAfdelingen; 
	private JButton btnTarieven;
	private JButton btnWerknemers;
	private AdresPanel panelPostadres;
	private AdresPanel panelVestigingsadres;
	private AdresPanel panelFactuuradres;
	private JCheckBox chckbxFactureren;
	private JCheckBox chckbxDetailsSecretariaat;
	private JTextFieldTGI txtBTWNummer;
	private JTextFieldTGI txtDebNr;
	private JTextFieldTGI txtExterncontractnummer;
	private WerkgeverInfo werkgever = null;
	private ContactpersoonPanel panelContactpersoon;
	private ContactpersoonPanel panelContactpersoonfactuur;
	private JComboBox<TypeEntry> cmbArbodienst;
	private JComboBox<TypeEntry> cmbBedrijfsarts;
	private JComboBox<TypeEntry> cmbUitkeringsinstantie;
	private JComboBox<TypeEntry> cmbHolding;
	private JPanel panelFactureren;
	private JLabel lblArbodienst;
	private JLabel lblBedrijfsarts;
	private JLabel lblUwv;
	private JLabel lblDochterVan;
	private List<ArbodienstInfo> arbodiensten;
	private List<BedrijfsartsInfo> artsen;
	private VerzuimComboBoxModel bedrijfsartsmodel;
	private VerzuimComboBoxModel arbodienstmodel;
	private VerzuimComboBoxModel uitkeringinstantiemodel;
	private VerzuimComboBoxModel holdingmodel;
	private Integer origArtsid = null;
	private boolean accesstotarieven = false;
	private JTextFieldTGI txtEmailadresfactuur;
	private Integer arbodienstid = -1;
	
	private WerkgeverController werkgevercontroller;
	public WerkgeverDetail(AbstractController controller) {
		super("Beheer Werkgever",controller);
		werkgevercontroller = (WerkgeverController) controller;
		initialize();
	}
	@Override
	public void setData(InfoBase info){
		werkgever = (WerkgeverInfo)info;
		displayWerkgever();
	}
	void displayWerkgever(){
		NumberFormat nf = NumberFormat.getNumberInstance();
		DecimalFormat df = (DecimalFormat)nf;
		df.applyPattern("##0.00");

		arbodiensten = controller.getMaincontroller().getArbodiensten();
		controller.getMaincontroller().getHoldings();
		controller.getMaincontroller().getUitkeringsinstanties();
		accesstotarieven = controller.getMaincontroller().isAuthorised(__applicatiefunctie.RAADPLEGENTARIEVEN);
		origArtsid = werkgever.getBedrijfsartsid();
		txtNaam.setText(werkgever.getNaam());
		if (werkgever.getWerkweek() != null){
			txtWerkweek.setText(df.format(werkgever.getWerkweek()));
		}

		if (werkgever.getHoldingId() != null){
			holdingmodel.setId(werkgever.getHoldingId());
		}
		if (werkgever.getArbodienstId() != null){
			arbodienstmodel.setId(werkgever.getArbodienstId());
			arbodienstid = werkgever.getArbodienstId();
			refreshComboBedrijfsarts();
		}else{
			arbodienstid = -1;
			refreshComboBedrijfsarts();
		}
		if (werkgever.getUwvId() != null){
			uitkeringinstantiemodel.setId(werkgever.getUwvId());
		}
		if (werkgever.getBedrijfsartsid() != null){
			bedrijfsartsmodel.setId(werkgever.getBedrijfsartsid());
		}

		panelPostadres.setAdres(werkgever.getPostAdres());
		panelVestigingsadres.setAdres(werkgever.getVestigingsAdres());
		panelFactuuradres.setAdres(werkgever.getFactuurAdres());

		panelContactpersoon.setContactpersoon(werkgever.getContactpersoon());
		panelContactpersoonfactuur.setContactpersoon(werkgever.getContactpersoonfactuur());
		try {
			dtpStartdate.setDate(werkgever.getStartdatumcontract());
			dtpEnddate.setDate(werkgever.getEinddatumcontract());
		} catch (PropertyVetoException e) {
			ExceptionLogger.ProcessException(e,this);
		}
		
		chckbxDetailsSecretariaat.setSelected(werkgever.getDetailsecretariaat());
		chckbxFactureren.setSelected(werkgever.getFactureren());
		txtBTWNummer.setText(werkgever.getBtwnummer());
		if (werkgever.getDebiteurnummer() == null)
			txtDebNr.setText("");
		else
			txtDebNr.setText(werkgever.getDebiteurnummer().toString());
		if (werkgever.getExterncontractnummer() == null)
			txtExterncontractnummer.setText("");
		else
			txtExterncontractnummer.setText(werkgever.getExterncontractnummer());
		
		if (accesstotarieven){
			btnTarieven.setEnabled(true);
			chckbxFactureren.setEnabled(true);
			chckbxDetailsSecretariaat.setEnabled(true);
			txtEmailadresfactuur.setEnabled(true);
		} else {
			btnTarieven.setEnabled(false);
			chckbxFactureren.setEnabled(false);
			chckbxDetailsSecretariaat.setEnabled(false);
			txtEmailadresfactuur.setEnabled(false);
		}

		if (this.getFormmode() == __formmode.NEW){
			btnTarieven.setEnabled(false);
			btnWerknemers.setEnabled(false);
		}
		
		txtEmailadresfactuur.setText(werkgever.getEmailadresfactuur());
	}
	
	@SuppressWarnings({ })
	private void initialize() {
		setBounds(100, 100, 858, 561);
		getContentPane().setLayout(null);

		JLabel lblNaam = new JLabel("Naam");
		lblNaam.setBounds(24, 37, 46, 14);
		getContentPane().add(lblNaam);

		txtNaam = new JTextFieldTGI();
		txtNaam.setBounds(127, 34, 270, 20);
		getContentPane().add(txtNaam);
		txtNaam.setColumns(10);

		dtpStartdate = new DatePicker();
		dtpStartdate.setBounds(127, 55, 103, 21);
		dtpStartdate.setDateFormat(new SimpleDateFormat("dd-MM-yyyy"));
		getContentPane().add(dtpStartdate);
		
		JLabel lblIngangsdatum = new JLabel("Ingangsdatum");
		lblIngangsdatum.setBounds(24, 58, 69, 14);
		getContentPane().add(lblIngangsdatum);
		
		JLabel lblBeeindigingsdatum = new JLabel("Beeindigingsdatum");
		lblBeeindigingsdatum.setBounds(24, 82, 96, 14);
		getContentPane().add(lblBeeindigingsdatum);
		
		dtpEnddate = new DatePicker();
		dtpEnddate.setBounds(127, 79, 103, 21);
		dtpEnddate.setDateFormat(new SimpleDateFormat("dd-MM-yyyy"));
		getContentPane().add(dtpEnddate);
		try {
			dtpEnddate.setDate(null);
		} catch (PropertyVetoException e) {
			ExceptionLogger.ProcessException(e,this);
		}
		
		btnPakketten = new JButton("Pakketten");
		btnPakketten.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				btnPakkettenClicked(e);
			}
		});
		btnPakketten.setBounds(600, 11, 103, 23);
		getContentPane().add(btnPakketten);
		
		btnAfdelingen = new JButton("Afdelingen");
		btnAfdelingen.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				btnAfdelingenClicked(e);
			}
		});
		btnAfdelingen.setBounds(600, 45, 103, 23);
		getContentPane().add(btnAfdelingen);
		
		btnWerknemers = new JButton("Werknemers");
		btnWerknemers.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				btnWerknemersClicked(e);
			}
		});
		btnWerknemers.setBounds(600, 79, 103, 23);
		getContentPane().add(btnWerknemers);
		
		panelVestigingsadres = new AdresPanel();
		panelVestigingsadres.setBounds(10, 111, 403, 90);
		panelVestigingsadres.setAdresSoort("Vestigingsadres");
		getContentPane().add(panelVestigingsadres);
		
		panelPostadres = new AdresPanel();
		panelPostadres.setBounds(10, 201, 403, 90);
		panelPostadres.setAdresSoort("Postadres");
		getContentPane().add(panelPostadres);
		
		panelFactuuradres = new AdresPanel();
		panelFactuuradres.setAdresSoort("Factuuradres");
		panelFactuuradres.setBounds(425, 201, 403, 90);
		getContentPane().add(panelFactuuradres);

		panelContactpersoon = new ContactpersoonPanel();
		panelContactpersoon.setBounds(10, 294, 403, 112);
		panelContactpersoon.setTitle("Contactpersoon verzuim");
		getContentPane().add(panelContactpersoon);
		
		panelContactpersoonfactuur = new ContactpersoonPanel();
		panelContactpersoonfactuur.setBounds(425, 294, 403, 112);
		panelContactpersoonfactuur.setTitle("Contactpersoon factuur");
		getContentPane().add(panelContactpersoonfactuur);
		
		addComboHolding();
		addComboArbodienst();
		addComboBedrijfsarts();
		addComboUitkeringsinstantie();

		JLabel lblWerkweek = new JLabel("Werkweek");
		lblWerkweek.setBounds(240, 58, 69, 15);
		getContentPane().add(lblWerkweek);
		
		txtWerkweek = new JTextFieldTGI();
		txtWerkweek.setColumns(5);
		txtWerkweek.setBounds(321, 55, 39, 21);
		getContentPane().add(txtWerkweek);
		
		panelFactureren = new JPanel();
		panelFactureren.setBounds(425, 417, 294, 90);
		panelFactureren.setBorder(new TitledBorder(null, "Facturatie", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		
		getContentPane().add(panelFactureren);
		panelFactureren.setLayout(null);
		
		chckbxFactureren = new JCheckBox("Factureren");
		chckbxFactureren.setBounds(6, 16, 137, 20);
		panelFactureren.add(chckbxFactureren);
		
		chckbxDetailsSecretariaat = new JCheckBox("Gedetailleerde secr. kosten");
		chckbxDetailsSecretariaat.setBounds(6, 37, 198, 20);
		panelFactureren.add(chckbxDetailsSecretariaat);
		
		txtEmailadresfactuur = new JTextFieldTGI();
		txtEmailadresfactuur.setColumns(10);
		txtEmailadresfactuur.setBounds(115, 59, 167, 20);
		panelFactureren.add(txtEmailadresfactuur);
		
		JLabel lblEmail = new JLabel("Email");
		lblEmail.setBounds(10, 62, 93, 14);
		panelFactureren.add(lblEmail);
		
		JLabel lblBtwNummer = new JLabel("BTW nummer");
		lblBtwNummer.setBounds(240, 82, 83, 14);
		getContentPane().add(lblBtwNummer);
		
		txtBTWNummer = new JTextFieldTGI();
		txtBTWNummer.setColumns(10);
		txtBTWNummer.setBounds(321, 79, 112, 20);
		getContentPane().add(txtBTWNummer);
		
		btnTarieven = new JButton("Tarieven");
		btnTarieven.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				btnTarievenClicked(e);
			}
		});
		btnTarieven.setBounds(600, 113, 103, 23);
		getContentPane().add(btnTarieven);
		
		JLabel lblDebNummer = new JLabel("Deb. nummer");
		lblDebNummer.setBounds(408, 14, 69, 14);
		getContentPane().add(lblDebNummer);
		
		txtDebNr = new JTextFieldTGI();
		txtDebNr.setEnabled(false);
		txtDebNr.setColumns(10);
		txtDebNr.setBounds(476, 11, 112, 20);
		getContentPane().add(txtDebNr);
		
		txtExterncontractnummer = new JTextFieldTGI();
		txtExterncontractnummer.setColumns(10);
		txtExterncontractnummer.setBounds(476, 34, 112, 20);
		getContentPane().add(txtExterncontractnummer);
		
		JLabel lblExtNummer = new JLabel("Ext. nummer");
		lblExtNummer.setBounds(407, 37, 69, 14);
		getContentPane().add(lblExtNummer);
		
	}
	private void addComboBedrijfsarts() {
		lblBedrijfsarts = new JLabel("Bedrijfsarts");
		lblBedrijfsarts.setBounds(20, 443, 60, 14);
		getContentPane().add(lblBedrijfsarts);

		cmbBedrijfsarts = new JComboBox<>();
		cmbBedrijfsarts.setBounds(108, 440, 232, 20);
		getContentPane().add(cmbBedrijfsarts);
		cmbBedrijfsarts.setModel(new VerzuimComboBoxModel(controller.getMaincontroller()));
		bedrijfsartsmodel = (VerzuimComboBoxModel) cmbBedrijfsarts.getModel();
		DefaultControllerEventListener listener = new DefaultControllerEventListener(){
			@Override
			public void refreshTable() {
				refreshComboBedrijfsarts();
			}
		};
		registerControllerListener(controller.getMaincontroller(), listener); 

		refreshComboBedrijfsarts();
		
	}
	private void addComboUitkeringsinstantie() {
		lblUwv = new JLabel("Uwv");
		lblUwv.setBounds(20, 467, 46, 14);
		getContentPane().add(lblUwv);
		
		cmbUitkeringsinstantie = new JComboBox<>();
		cmbUitkeringsinstantie.setBounds(109, 464, 232, 20);
		getContentPane().add(cmbUitkeringsinstantie);
		cmbUitkeringsinstantie.setModel(new VerzuimComboBoxModel(controller.getMaincontroller()));
		uitkeringinstantiemodel = (VerzuimComboBoxModel) cmbUitkeringsinstantie.getModel();
		DefaultControllerEventListener listener = new DefaultControllerEventListener(){
			@Override
			public void refreshTable() {
				refreshComboUitkeringsinstantie();
			}
		};
		registerControllerListener(controller.getMaincontroller(), listener); 

		refreshComboUitkeringsinstantie();
	}
	private void addComboArbodienst() {
		lblArbodienst = new JLabel("Arbodienst");
		lblArbodienst.setBounds(20, 420, 73, 14);
		getContentPane().add(lblArbodienst);

		cmbArbodienst = new JComboBox<>();
		cmbArbodienst.setBounds(109, 417, 232, 20);
		getContentPane().add(cmbArbodienst);
				
		cmbArbodienst.setModel(new VerzuimComboBoxModel(controller.getMaincontroller()));
		arbodienstmodel = (VerzuimComboBoxModel) cmbArbodienst.getModel();
		DefaultControllerEventListener listener = new DefaultControllerEventListener(){
			@Override
			public void refreshTable() {
				refreshComboArbodienst();
			}
		};
		registerControllerListener(controller.getMaincontroller(), listener); 

		refreshComboArbodienst();
	}
	private void addComboHolding() {
		lblDochterVan = new JLabel("Onderdeel van");
		lblDochterVan.setBounds(24, 14, 83, 14);
		getContentPane().add(lblDochterVan);
		
		cmbHolding = new JComboBox<>();
		cmbHolding.setBounds(127, 11, 200, 20);
		getContentPane().add(cmbHolding);

		cmbHolding.setModel(new VerzuimComboBoxModel(controller.getMaincontroller()));
		holdingmodel = (VerzuimComboBoxModel) cmbHolding.getModel();
		DefaultControllerEventListener listener = new DefaultControllerEventListener(){
			@Override
			public void refreshTable() {
				refreshComboHolding();
			}
		};
		registerControllerListener(controller.getMaincontroller(), listener); 

		refreshComboHolding();
	}
	private void refreshComboHolding(){
		werkgevercontroller.getMaincontroller().updateComboModelHoldings((VerzuimComboBoxModel) cmbHolding.getModel(),true);
	}
	private void refreshComboArbodienst(){
		werkgevercontroller.getMaincontroller().updateComboModelArbodiensten((VerzuimComboBoxModel) cmbArbodienst.getModel());
	}
	private void refreshComboBedrijfsarts() {
		werkgevercontroller.getMaincontroller().updateComboModelBedrijfsartsen((VerzuimComboBoxModel) cmbBedrijfsarts.getModel(),arbodienstid);
	}
	private void refreshComboUitkeringsinstantie() {
		werkgevercontroller.getMaincontroller().updateComboModelUitkeringsinstanties((VerzuimComboBoxModel) cmbUitkeringsinstantie.getModel());
	}
	
	protected void cmbArboDienstClicked(ActionEvent e) {
		arbodienstmodel = (VerzuimComboBoxModel) cmbArbodienst.getModel();
		arbodienstid = arbodienstmodel.getId();
		if (arbodiensten != null){
			if (werkgever.getArbodienstId() != null && arbodienstid.equals(werkgever.getArbodienstId())){
				/* Gekozen waarde gelijk aan oorspronkelijke waarde */
				/* Oorspronkelijke arts wordt weer hersteld */					
				werkgever.setBedrijfsartsid(origArtsid);			
			}else{
				werkgever.setBedrijfsartsid(null);
			}
			if (arbodienstid == -1){							/* Geen arbodienst gekozen */
				bedrijfsartsmodel.removeAllElements();			/* Dan ook geen bedrijfartsen zichtbaar*/
			}else{
				refreshComboBedrijfsarts();
			}
		}
	}
	protected void btnWerknemersClicked(ActionEvent e) {
		werkgevercontroller.openwerknemers();
	}
	protected void btnAfdelingenClicked(ActionEvent e) {
		werkgevercontroller.openafdelingen();
	}
	protected void btnPakkettenClicked(ActionEvent e) {
		werkgevercontroller.openpakketten();
	}
	protected void btnTarievenClicked(ActionEvent e) {
		werkgevercontroller.opentarieven();
	}
	@Override
	public InfoBase collectData() {
		ParsePosition pos = new ParsePosition(0);
		DecimalFormat format = new DecimalFormat("##0,00");
		BigDecimal werkweek;
		format.setParseBigDecimal(true);

		arbodienstid = arbodienstmodel.getId();
		Integer holdingid = holdingmodel.getId();
		Integer artsid = bedrijfsartsmodel.getId();
		Integer uwvid = uitkeringinstantiemodel.getId();

		werkgever.setNaam(txtNaam.getText());
        werkgever.setStartdatumcontract(dtpStartdate.getDate());
        werkgever.setEinddatumcontract(dtpEnddate.getDate());

        werkweek = (BigDecimal)format.parse(txtWerkweek.getText(),pos);
		if (pos.getIndex() < txtWerkweek.getText().length())
		{
			JOptionPane.showMessageDialog(this, "Werkweek bevat ongeldige tekens");
			return null;
		}
		werkgever.setWerkweek(werkweek);
        
       	werkgever.setPostAdres(panelPostadres.getAdres());
       	werkgever.setVestigingsAdres(panelVestigingsadres.getAdres());
       	werkgever.setFactuurAdres(panelFactuuradres.getAdres());
       	werkgever.setContactpersoon(panelContactpersoon.getContactpersoon());
       	werkgever.setContactpersoonfactuur(panelContactpersoonfactuur.getContactpersoon());
       	if (arbodienstid == -1)
       		werkgever.setArbodienstId(null);
       	else
       		werkgever.setArbodienstId(arbodienstid);
       	if (holdingid == -1){
       		werkgever.setHolding(null);
       		werkgever.setHoldingId(null);
       	}else{
       		werkgever.setHolding(null);
       		werkgever.setHoldingId(holdingid);
       	}
       	if (artsid != null) 
       		if (artsid == -1)
       			werkgever.setBedrijfsartsid(null);
       		else
       			werkgever.setBedrijfsartsid(artsid);
       	else
       		werkgever.setBedrijfsartsid(null);
       	if (uwvid == -1)
       		werkgever.setUwv(null);
       	else
       		werkgever.setUwvId(uwvid);
       	
       	werkgever.setFactureren(chckbxFactureren.isSelected());
       	werkgever.setDetailsecretariaat(chckbxDetailsSecretariaat.isSelected());
       	werkgever.setBtwnummer(txtBTWNummer.getText().trim());
       	werkgever.setEmailadresfactuur(txtEmailadresfactuur.getText());
       	werkgever.setExterncontractnummer(txtExterncontractnummer.getText().trim());
       	return werkgever;
	}
}
