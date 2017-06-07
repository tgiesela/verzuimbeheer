package com.gieselaar.verzuimbeheer.forms;

import java.awt.Window;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JDesktopPane;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import java.beans.PropertyVetoException;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.List;

import javax.swing.JLabel;

import com.gieselaar.verzuimbeheer.baseforms.BaseDetailform;
import com.gieselaar.verzuimbeheer.baseforms.JDesktopPaneTGI;
import com.gieselaar.verzuimbeheer.exceptions.VerzuimApplicationException;
import com.gieselaar.verzuimbeheer.exceptions.PermissionException;
import com.gieselaar.verzuimbeheer.exceptions.ValidationException;
import com.gieselaar.verzuimbeheer.facades.AutorisatieFacadeRemote.__applicatiefunctie;
import com.gieselaar.verzuimbeheer.services.ArbodienstInfo;
import com.gieselaar.verzuimbeheer.services.BedrijfsartsInfo;
import com.gieselaar.verzuimbeheer.services.HoldingInfo;
import com.gieselaar.verzuimbeheer.services.InfoBase;
import com.gieselaar.verzuimbeheer.services.UitvoeringsinstituutInfo;
import com.gieselaar.verzuimbeheer.services.WerkgeverInfo;
import com.gieselaar.verzuimbeheer.utils.ExceptionLogger;
import com.gieselaar.verzuimbeheer.utils.ServiceCaller;
import com.gieselaar.verzuimbeheer.utils.TypeEntry;
import com.michaelbaranov.microba.calendar.DatePicker;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

import com.gieselaar.verzuimbeheer.components.AdresPanel;
import com.gieselaar.verzuimbeheer.components.ContactpersoonPanel;

import javax.swing.JComboBox;

import com.gieselaar.verzuimbeheer.components.JTextFieldTGI;

import javax.swing.JPanel;
import javax.swing.border.TitledBorder;
import javax.swing.JCheckBox;

public class WerkgeverDetail extends BaseDetailform{
	private static final long serialVersionUID = 4517368680789127146L;
	private JTextFieldTGI txtNaam;
	private JTextFieldTGI txtWerkweek;
	private DatePicker dtpStartdate; 
	private DatePicker dtpEnddate;
	private JButton btnPakketten;
	private JButton btnAfdelingen; 
	private JButton btnTarieven;
	private AdresPanel panelPostadres;
	private AdresPanel panelVestigingsadres;
	private AdresPanel panelFactuuradres;
	private JCheckBox chckbxFactureren;
	private JCheckBox chckbxDetailsSecretariaat;
	private JTextFieldTGI txtBTWNummer;
	private JTextFieldTGI txtDebNr;
	private JTextFieldTGI txtExterncontractnummer;
	private WerkgeverInfo werkgever = null;
	private WerkgeverPakketten dlgWerkgeverPakketten;
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
	private List<HoldingInfo> holdings;
	private List<ArbodienstInfo> arbodiensten;
	private List<UitvoeringsinstituutInfo> uwvs;
	private List<BedrijfsartsInfo> artsen;
	private boolean initialized = false;
	private DefaultComboBoxModel<TypeEntry> bedrijfsartsModel = new DefaultComboBoxModel<TypeEntry>();
	private Integer origArts = null;
	private boolean accesstotarieven = false;
	private JTextFieldTGI txtEmailadresfactuur;
	
	public void setInfo(InfoBase info){
		NumberFormat nf = NumberFormat.getNumberInstance();
		DecimalFormat df = (DecimalFormat)nf;
		df.applyPattern("##0.00");
		DefaultComboBoxModel<TypeEntry> holdingModel;
		DefaultComboBoxModel<TypeEntry> arbodienstModel;
		DefaultComboBoxModel<TypeEntry> uwvModel;

		werkgever = (WerkgeverInfo) info;
		try {
			if (this.getMode() != formMode.New)
				werkgever = ServiceCaller.werkgeverFacade(getLoginSession()).getWerkgever(werkgever.getId());
			arbodiensten = ServiceCaller.instantieFacade(getLoginSession()).allArbodiensten();
			holdings = ServiceCaller.werkgeverFacade(getLoginSession()).getHoldings();
			uwvs = ServiceCaller.instantieFacade(getLoginSession()).allUitkeringsinstanties();
			accesstotarieven = ServiceCaller.autorisatieFacade(getLoginSession()).isAuthorised(getLoginSession().getGebruiker(), __applicatiefunctie.RAADPLEGENTARIEVEN);
		} catch (PermissionException e1) {
			ExceptionLogger.ProcessException(e1,this);
			return;
		} catch (VerzuimApplicationException e2) {
        	ExceptionLogger.ProcessException(e2,this);
        	return;
		} catch (Exception e1){
        	ExceptionLogger.ProcessException(e1,this);
        	return;
		}
		origArts = werkgever.getBedrijfsartsid();
		txtNaam.setText(werkgever.getNaam());
		txtWerkweek.setText(df.format(werkgever.getWerkweek()));

		holdingModel = new DefaultComboBoxModel<TypeEntry>();
		cmbHolding.setModel(holdingModel);
		TypeEntry holdingType = new TypeEntry(-1,"N.v.t");
		holdingModel.addElement(holdingType);
		HoldingInfo.sort(holdings,HoldingInfo.__sortcol.NAAM);
		if (holdings != null){
			for (HoldingInfo w: holdings)
			{
				TypeEntry h = new TypeEntry(w.getId(), w.getNaam());
				holdingModel.addElement(h);
			}
		}
		
		arbodienstModel = new DefaultComboBoxModel<TypeEntry>();
		TypeEntry arboType = new TypeEntry(-1,"N.v.t");
		arbodienstModel.addElement(arboType);
		cmbArbodienst.setModel(arbodienstModel);
		if (arbodiensten != null){
			for (ArbodienstInfo w: arbodiensten)
			{
				TypeEntry h = new TypeEntry(w.getId(), w.getNaam());
				arbodienstModel.addElement(h);
			}
		}
		
		uwvModel = new DefaultComboBoxModel<TypeEntry>();
		cmbUitkeringsinstantie.setModel(uwvModel);
		TypeEntry uwvType = new TypeEntry(-1,"N.v.t");
		uwvModel.addElement(uwvType);
		if (uwvs != null){
			for (UitvoeringsinstituutInfo w: uwvs)
			{
				TypeEntry h = new TypeEntry(w.getId(), w.getNaam());
				uwvModel.addElement(h);
			}
		}
		initialized = true;
		if (werkgever.getHolding() != null)
			for (int i=0;i<holdingModel.getSize();i++)
			{
				holdingType = (TypeEntry) holdingModel.getElementAt(i);
				if (holdingType.getValue() == werkgever.getHolding().getId())
				{
					holdingModel.setSelectedItem(holdingType);
					break;
				}
			}
		else
			holdingModel.setSelectedItem(holdingModel.getElementAt(0));
		
		if (werkgever.getArbodienst() != null)
			for (int i=0;i<arbodienstModel.getSize();i++)
			{
				arboType = (TypeEntry) arbodienstModel.getElementAt(i);
				if (arboType.getValue() == werkgever.getArbodienst().getId())
				{
					arbodienstModel.setSelectedItem(arboType);
					break;
				}
			}
		else
			arbodienstModel.setSelectedItem(arbodienstModel.getElementAt(0));

		if (werkgever.getUwv() != null)
			for (int i=0;i<uwvModel.getSize();i++)
			{
				uwvType = (TypeEntry) uwvModel.getElementAt(i);
				if (uwvType.getValue() == werkgever.getUwv().getId())
				{
					uwvModel.setSelectedItem(uwvType);
					break;
				}
			}
		else
			uwvModel.setSelectedItem(uwvModel.getElementAt(0));

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
		} else {
			btnTarieven.setEnabled(false);
			chckbxFactureren.setEnabled(false);
			chckbxDetailsSecretariaat.setEnabled(false);
		}
		txtEmailadresfactuur.setText(werkgever.getEmailadresfactuur());
		activateListener();
	}
	
	/**
	 * Create the dialog.
	 */
	public WerkgeverDetail(JDesktopPaneTGI mdiPanel) {
		super("Beheer Werkgever",mdiPanel);
		initialize();
	}
	@SuppressWarnings({ })
	private void initialize() {
		setBounds(100, 100, 858, 561);
		getContentPane().setLayout(null);
		{
			JLabel lblNaam = new JLabel("Naam");
			lblNaam.setBounds(24, 37, 46, 14);
			getContentPane().add(lblNaam);
		}
		{
			txtNaam = new JTextFieldTGI();
			txtNaam.setBounds(127, 34, 270, 20);
			getContentPane().add(txtNaam);
			txtNaam.setColumns(10);
		}
		
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
			public void actionPerformed(ActionEvent e) {
				btnPakkettenClicked(e);
			}
		});
		btnPakketten.setBounds(600, 11, 103, 23);
		getContentPane().add(btnPakketten);
		
		btnAfdelingen = new JButton("Afdelingen");
		btnAfdelingen.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				btnAfdelingenClicked(e);
			}
		});
		btnAfdelingen.setBounds(600, 45, 103, 23);
		getContentPane().add(btnAfdelingen);
		
		JButton btnWerknemers = new JButton("Werknemers");
		btnWerknemers.addActionListener(new ActionListener() {
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
		
		cmbArbodienst = new JComboBox<TypeEntry>();
		cmbArbodienst.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				cmbArboDienstClicked(e);
			}
		});
		cmbArbodienst.setBounds(109, 417, 232, 20);
		getContentPane().add(cmbArbodienst);
		
		lblArbodienst = new JLabel("Arbodienst");
		lblArbodienst.setBounds(20, 420, 73, 14);
		getContentPane().add(lblArbodienst);
		
		cmbBedrijfsarts = new JComboBox<TypeEntry>();
		cmbBedrijfsarts.setBounds(108, 440, 232, 20);
		getContentPane().add(cmbBedrijfsarts);
		
		lblBedrijfsarts = new JLabel("Bedrijfsarts");
		lblBedrijfsarts.setBounds(20, 443, 60, 14);
		getContentPane().add(lblBedrijfsarts);
		
		lblUwv = new JLabel("Uwv");
		lblUwv.setBounds(20, 467, 46, 14);
		getContentPane().add(lblUwv);
		
		cmbUitkeringsinstantie = new JComboBox<TypeEntry>();
		cmbUitkeringsinstantie.setBounds(109, 464, 232, 20);
		getContentPane().add(cmbUitkeringsinstantie);
		
		lblDochterVan = new JLabel("Onderdeel van");
		lblDochterVan.setBounds(24, 14, 83, 14);
		getContentPane().add(lblDochterVan);
		
		cmbHolding = new JComboBox<TypeEntry>();
		cmbHolding.setBounds(127, 11, 200, 20);
		getContentPane().add(cmbHolding);
		
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
	protected void btnTarievenClicked(ActionEvent e) {
		JDesktopPane mdiPanel = this.getDesktopPane();
		TariefOverzicht dlgTariefOverzicht = new TariefOverzicht(this.getMdiPanel());
		dlgTariefOverzicht.setLoginSession(this.getLoginSession());
		dlgTariefOverzicht.setParentInfo(werkgever);
		dlgTariefOverzicht.setVisible(true);
		dlgTariefOverzicht.ReloadTable();
		mdiPanel.add(dlgTariefOverzicht);
		mdiPanel.setOpaque(true);
		mdiPanel.moveToFront(dlgTariefOverzicht);
	}
	protected void cmbArboDienstClicked(ActionEvent e) {
		TypeEntry selected;
		if (initialized){
			selected = (TypeEntry)cmbArbodienst.getSelectedItem();
			if (arbodiensten != null){
				for (ArbodienstInfo ai: arbodiensten){
					if (werkgever.getArbodienst() != null && selected.getValue() == werkgever.getArbodienst().getId()) /* Gekozen waarde gelijk aan oorspronkelijke waarde */
						werkgever.setBedrijfsartsid(origArts);   			/* Oorspronkelijke arts wordt weer hersteld */
					else
						werkgever.setBedrijfsartsid(null);
					if (selected.getValue() == -1)							/* Geen arbodienst gekozen */
						bedrijfsartsModel.removeAllElements();				/* Dan ook geen bedrijfartsen */
					else
						if (selected.getValue() == ai.getId()){
							try {
								artsen = ServiceCaller.instantieFacade(getLoginSession()).allBedrijfsartsenArbodienst(ai.getId());
							} catch (PermissionException e1) {
								ExceptionLogger.ProcessException(e1,this);
								return;
							} catch (VerzuimApplicationException e2) {
					        	ExceptionLogger.ProcessException(e2,this);
					        	return;
							} catch (Exception e1){
					        	ExceptionLogger.ProcessException(e1,this);
					        	return;
							}
							populateBedrijfsartsen();
							break;
						}
				}
			}
		}
	}

	private void populateBedrijfsartsen() {
		bedrijfsartsModel.removeAllElements();
		bedrijfsartsModel = new DefaultComboBoxModel<TypeEntry>();
		TypeEntry artsType;

		TypeEntry arboType = new TypeEntry(-1,"N.v.t");
		bedrijfsartsModel.addElement(arboType);
		cmbBedrijfsarts.setModel(bedrijfsartsModel);
		
		for (BedrijfsartsInfo w: artsen)
		{
			TypeEntry h = new TypeEntry(w.getId(), w.getAchternaam());
			bedrijfsartsModel.addElement(h);
		}
		if (werkgever.getBedrijfsartsid() != null)
			for (int i=0;i<bedrijfsartsModel.getSize();i++)
			{
				artsType = (TypeEntry) bedrijfsartsModel.getElementAt(i);
				if (artsType.getValue() == werkgever.getBedrijfsartsid())
				{
					bedrijfsartsModel.setSelectedItem(artsType);
					break;
				}
			}
		else
			bedrijfsartsModel.setSelectedItem(bedrijfsartsModel.getElementAt(0));
	}

	protected void okButtonClicked(ActionEvent e) {
		ParsePosition pos = new ParsePosition(0);
		DecimalFormat format = new DecimalFormat("##0,00");
		BigDecimal werkweek;
		format.setParseBigDecimal(true);

		TypeEntry arbodienst = (TypeEntry)cmbArbodienst.getSelectedItem();
		TypeEntry holding = (TypeEntry)cmbHolding.getSelectedItem();
		TypeEntry arts = (TypeEntry)cmbBedrijfsarts.getSelectedItem();
		TypeEntry uwv = (TypeEntry)cmbUitkeringsinstantie.getSelectedItem();
		werkgever.setNaam(txtNaam.getText());
        werkgever.setStartdatumcontract(dtpStartdate.getDate());
        werkgever.setEinddatumcontract(dtpEnddate.getDate());

        werkweek = (BigDecimal)format.parse(txtWerkweek.getText(),pos);
		if (pos.getIndex() < txtWerkweek.getText().length())
		{
			JOptionPane.showMessageDialog(this, "Werkweek bevat ongeldige tekens");
			return;
		}
		werkgever.setWerkweek(werkweek);
        
       	werkgever.setPostAdres(panelPostadres.getAdres());
       	werkgever.setVestigingsAdres(panelVestigingsadres.getAdres());
       	werkgever.setFactuurAdres(panelFactuuradres.getAdres());
       	werkgever.setContactpersoon(panelContactpersoon.getContactpersoon());
       	werkgever.setContactpersoonfactuur(panelContactpersoonfactuur.getContactpersoon());
       	if (arbodienst.getValue() == -1)
       		werkgever.setArbodienst(null);
       	else
       		werkgever.setArbodienst(lookupArbodienst(arbodienst.getValue()));
       	if (holding.getValue() == -1){
       		werkgever.setHolding(null);
       		werkgever.setHoldingId(null);
       	}else{
       		werkgever.setHolding(lookupHolding(holding.getValue()));
       		werkgever.setHoldingId(werkgever.getHolding().getId());
       	}
       	if (arts != null) 
       		if (arts.getValue() == -1)
       			werkgever.setBedrijfsartsid(null);
       		else
       			werkgever.setBedrijfsartsid(lookupArts(arts.getValue()).getId());
       	else
       		werkgever.setBedrijfsartsid(null);
       	if (uwv.getValue() == -1)
       		werkgever.setUwv(null);
       	else
       		werkgever.setUwv(lookupUwv(uwv.getValue()));
       	
       	werkgever.setFactureren(chckbxFactureren.isSelected());
       	werkgever.setDetailsecretariaat(chckbxDetailsSecretariaat.isSelected());
       	werkgever.setBtwnummer(txtBTWNummer.getText().trim());
       	werkgever.setEmailadresfactuur(txtEmailadresfactuur.getText());
       	werkgever.setExterncontractnummer(txtExterncontractnummer.getText().trim());
        if (this.getLoginSession() != null)
        {
        	try {
        		werkgever.validate();
	    		switch (this.getMode()){
    			case New: 
    				ServiceCaller.werkgeverFacade(getLoginSession()).addWerkgever(werkgever);
    				break;
    			case Update: 
    				ServiceCaller.werkgeverFacade(getLoginSession()).updateWerkgever(werkgever);
    				break;
    			case Delete: 
    				ServiceCaller.werkgeverFacade(getLoginSession()).deleteWerkgever(werkgever);
    				break;
    		}
    		super.okButtonClicked(e);
		        super.okButtonClicked(e);
			} catch (ValidationException e1) {
	        	ExceptionLogger.ProcessException(e1,this,false);
			} catch (PermissionException e1) {
				ExceptionLogger.ProcessException(e1,this);
			} catch (VerzuimApplicationException e2) {
	        	ExceptionLogger.ProcessException(e2,this);
			} catch (Exception e1){
	        	ExceptionLogger.ProcessException(e1,this);
			}
        }
        else
        	JOptionPane.showMessageDialog(this,"Logic error: loginSession is null");
	}

	private UitvoeringsinstituutInfo lookupUwv(int value) {
		for (UitvoeringsinstituutInfo ui: uwvs)
			if (ui.getId() == value)
				return ui;
		return null;
	}

	private BedrijfsartsInfo lookupArts(int value) {
		for (BedrijfsartsInfo bi: artsen)
			if (bi.getId() == value)
				return bi;
		return null;
	}

	private HoldingInfo lookupHolding(int value) {
		for (HoldingInfo hi: holdings)
			if (hi.getId() == value)
				return hi;
		return null;
	}

	private ArbodienstInfo lookupArbodienst(int value) {
		for (ArbodienstInfo ai: arbodiensten)
			if (ai.getId() == value)
				return ai;
		return null;
	}

	protected void btnWerknemersClicked(ActionEvent e) {
		JDesktopPane mdiPanel = this.getDesktopPane();
		WerknemerOverzicht dlgWerknemers = new WerknemerOverzicht(this.getMdiPanel());
		dlgWerknemers.setLoginSession(this.getLoginSession());
		dlgWerknemers.setParentInfo(werkgever);
		dlgWerknemers.populateTable();
		dlgWerknemers.setVisible(true);
		mdiPanel.add(dlgWerknemers);
		mdiPanel.setOpaque(true);
		mdiPanel.moveToFront(dlgWerknemers);
	}
	protected void btnAfdelingenClicked(ActionEvent e) {
		JDesktopPane mdiPanel = this.getDesktopPane();
		Afdelingenoverzicht dlgAfdelingen = new Afdelingenoverzicht(this.getMdiPanel());
		dlgAfdelingen.setLoginSession(this.getLoginSession());
		dlgAfdelingen.setParentInfo(werkgever);
		dlgAfdelingen.populateTable();
		dlgAfdelingen.setVisible(true);
		mdiPanel.add(dlgAfdelingen);
		mdiPanel.setOpaque(true);
		mdiPanel.moveToFront(dlgAfdelingen);

	}
	protected void btnPakkettenClicked(ActionEvent e) {
		dlgWerkgeverPakketten = new WerkgeverPakketten((JFrame)SwingUtilities.getAncestorOfClass(Window.class, btnPakketten),true);
		dlgWerkgeverPakketten.setLoginSession(this.getLoginSession());
		dlgWerkgeverPakketten.setInfo(werkgever);
		dlgWerkgeverPakketten.setVisible(true);
	}
}
