package com.gieselaar.verzuimbeheer.forms;

import java.awt.event.ActionEvent;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.beans.PropertyVetoException;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JDesktopPane;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.RowSorter;
import javax.swing.SortOrder;

import com.gieselaar.verzuimbeheer.baseforms.BaseDetailform;
import com.gieselaar.verzuimbeheer.baseforms.DefaultListFormNotification;
import com.gieselaar.verzuimbeheer.baseforms.JDesktopPaneTGI;
import com.gieselaar.verzuimbeheer.components.AdresPanel;
import com.gieselaar.verzuimbeheer.components.JTextAreaTGI;
import com.gieselaar.verzuimbeheer.components.TablePanel;
import com.gieselaar.verzuimbeheer.components.TodoPanel;
import com.gieselaar.verzuimbeheer.exceptions.VerzuimApplicationException;
import com.gieselaar.verzuimbeheer.exceptions.PermissionException;
import com.gieselaar.verzuimbeheer.exceptions.ValidationException;
import com.gieselaar.verzuimbeheer.services.AdresInfo;
import com.gieselaar.verzuimbeheer.services.AfdelingHasWerknemerInfo;
import com.gieselaar.verzuimbeheer.services.CascodeInfo;
import com.gieselaar.verzuimbeheer.services.DienstverbandInfo;
import com.gieselaar.verzuimbeheer.services.DocumentTemplateInfo;
import com.gieselaar.verzuimbeheer.services.GebruikerInfo;
import com.gieselaar.verzuimbeheer.services.InfoBase;
import com.gieselaar.verzuimbeheer.services.VerzuimHerstelInfo;
import com.gieselaar.verzuimbeheer.services.VerzuimInfo;
import com.gieselaar.verzuimbeheer.services.InfoBase.persistenceaction;
import com.gieselaar.verzuimbeheer.services.InfoBase.persistencestate;
import com.gieselaar.verzuimbeheer.services.VerzuimInfo.__verzuimtype;
import com.gieselaar.verzuimbeheer.services.WerkgeverInfo;
import com.gieselaar.verzuimbeheer.services.WerknemerFastInfo;
import com.gieselaar.verzuimbeheer.services.WerknemerInfo;
import com.gieselaar.verzuimbeheer.services.InfoBase.__geslacht;
import com.gieselaar.verzuimbeheer.services.InfoBase.__burgerlijkestaat;
import com.gieselaar.verzuimbeheer.services.WiapercentageInfo.__wiapercentage;
import com.gieselaar.verzuimbeheer.services.WiapercentageInfo;
import com.gieselaar.verzuimbeheer.utils.DateOnly;
import com.gieselaar.verzuimbeheer.utils.ExceptionLogger;
import com.gieselaar.verzuimbeheer.utils.ServiceCaller;
import com.gieselaar.verzuimbeheer.utils.ServiceLocatorException;
import com.gieselaar.verzuimbeheer.utils.TypeEntry;
import com.gieselaar.verzuimbeheer.utils.WordDocument;
import com.michaelbaranov.microba.calendar.DatePicker;

import javax.swing.JComboBox;
import javax.swing.JCheckBox;
import javax.swing.JTabbedPane;

import java.awt.Color;
import java.awt.Component;
import java.awt.EventQueue;
import java.awt.KeyboardFocusManager;
import java.awt.Rectangle;

import javax.swing.JButton;

import java.awt.event.ActionListener;

import com.gieselaar.verzuimbeheer.components.JTextFieldTGI;
import javax.swing.JPanel;
import javax.swing.border.TitledBorder;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;

public class WerknemerDetail extends BaseDetailform {


	private static final long serialVersionUID = -5378765279785908721L;
	
	private WerknemerFastInfo werknemerFast = null;
	private WerknemerInfo werknemer = null;
	private List<WerkgeverInfo> werkgevers;
	private List<VerzuimInfo> verzuimen;
	private List<VerzuimHerstelInfo> herstellen;
	private List<GebruikerInfo> gebruikers;
	private List<AfdelingHasWerknemerInfo> teverwijderenafdelingen;
	private List<DocumentTemplateInfo> templates;
	private AdresPanel adresPanel = null;
	private JTextFieldTGI txtAchternaam;
	private JTextFieldTGI txtVoornaam;
	private JTextFieldTGI txtVoorvoegsel;
	private JTextFieldTGI txtBSN;
	private JTextFieldTGI txtVoorletters;
	private JTextFieldTGI txtTelefoonthuis;
	private JTextFieldTGI txtTelefoonprive;
	private JTextFieldTGI txtTelefoonMobiel;
	private JTextFieldTGI txtEmail;
	private JComboBox<TypeEntry> cmbGeslacht; 
	private JComboBox<TypeEntry> cmbBurgerlijkestaat;
	private JComboBox<TypeEntry> cmbWerkgever;	
	private DatePicker dtpGeboortedatum;
	private JCheckBox cbArbeidsgehandicapt;
	private TablePanel tpAfdelingen;
	private TablePanel tpWiaPercentages;
	private TablePanel tpVerzuimen;
	private TablePanel tpDienstverbanden;
	private TodoPanel tpTodos;
	private JButton btnVorigDienstverband;
	private JButton btnVolgendDienstverband;
	private DatePicker dtpEindatum;
	private JTextFieldTGI txtWerkweekUren;
	private JTextFieldTGI txtFunctie;
	private JTextFieldTGI txtPersoneelsnummer;
	private DatePicker dtpIngangsdatum;
	private JButton btnNewDienstverband;
	private JButton btnMedischekaart;
	private JTextAreaTGI taOpmerkingen;
	private int dienstverbandinx = -1;
	private WiapercentageInfo huidigwiapercentage = null;
	private AfdelingHasWerknemerInfo huidigeafdeling = null;
	private DienstverbandInfo dienstverband = null;
	private boolean openverzuimfound;
	private List<CascodeInfo> cascodes;
	private Hashtable<Integer, Object> hashCascodes = new Hashtable<Integer, Object>();
	private Component thisform = this;
	private ReportVerzuimenHistorie dlgReportVerzuimHistorie = null;
	private boolean initialized = false;
	private VerzuimInfo selectedVerzuim = null;
	private String savedBSN = "";

	private WerkgeverInfo werkgever;
	private JComboBox<TypeEntry> cmbTemplates;
	private JButton btnGenereer;
	/**
	 * Create the frame.
	 */
	public WerknemerDetail(JDesktopPaneTGI mdiPanel) {
		super("Werknemer", mdiPanel);
		initialize();
	}
	protected void displayAfdelingen() {
		Date einddatum;
		tpAfdelingen.deleteAllRows();
		huidigeafdeling = null;
		if (werknemer.getAfdelingen() != null){
			for(AfdelingHasWerknemerInfo a :werknemer.getAfdelingen())
			{
				Vector<Object> rowData = new Vector<Object>();
				if (a.getEinddatum() == null)
					huidigeafdeling = a;
				rowData.add(a.getAfdeling().getNaam().toString());
				rowData.add(a.getStartdatum());
				einddatum = a.getEinddatum();
				if (einddatum == null)
					rowData.add("");
				else
					rowData.add(einddatum);
				
				if (a.getAction() == persistenceaction.DELETE)
					;
				else
					if (einddatum == null)
						tpAfdelingen.addRow(rowData,a);
					else
						tpAfdelingen.addRow(rowData, a, Color.gray);
			}
		}
		tpAfdelingen.setMdiPanel(this.getMdiPanel());
	}
	protected void displayWiapercentages() {
		Date einddatum;
		tpWiaPercentages.deleteAllRows();
		huidigwiapercentage = null;
		if (werknemer.getWiaPercentages() != null){
			for(WiapercentageInfo a :werknemer.getWiaPercentages())
			{
				Vector<Object> rowData = new Vector<Object>();
				if (a.getEinddatum() == null)
					huidigwiapercentage = a;
						
				rowData.add(a.getCodeWiaPercentage().toString());
				rowData.add(a.getStartdatum());
				if (a.getEinddatum() == null)
					rowData.add("");
				else
					rowData.add(a.getEinddatum());
				einddatum = a.getEinddatum();
				if (einddatum == null)
					rowData.add("");
				else
					rowData.add(einddatum);
				
				if (a.getAction() == persistenceaction.DELETE)
					;
				else
					if (einddatum == null)
						tpWiaPercentages.addRow(rowData,a);
					else
						tpWiaPercentages.addRow(rowData, a, Color.gray);
			}
		}
		tpWiaPercentages.setMdiPanel(this.getMdiPanel());
	}
	protected void displayDienstverbanden() {
		tpDienstverbanden.deleteAllRows();
		if (werknemer.getDienstVerbanden() != null){
			for(DienstverbandInfo a :werknemer.getDienstVerbanden())
			{
				Vector<Object> rowData = new Vector<Object>();
						
				rowData.add(a.getPersoneelsnummer());
				rowData.add(a.getStartdatumcontract());
				if (a.getEinddatumcontract() == null)
					rowData.add("");
				else
					rowData.add(a.getEinddatumcontract());
				rowData.add(a.getWerkweek().toString());
				rowData.add(a.getFunctie());
				
				if (a.getAction() == persistenceaction.DELETE)
					;
				else
					if (a.getEinddatumcontract() == null)
						tpDienstverbanden.addRow(rowData,a);
					else
						tpDienstverbanden.addRow(rowData, a, Color.gray);
			}
		}
		tpDienstverbanden.setMdiPanel(this.getMdiPanel());
	}
	private void displayVerzuimen(){
		CascodeInfo cascode;
		Date datumHerstel;
		BigDecimal percentage = new BigDecimal(0);
		try{
			herstellen = ServiceCaller.verzuimFacade(getLoginSession()).getVerzuimHerstellen(dienstverband.getId());
			verzuimen = ServiceCaller.verzuimFacade(getLoginSession()).getVerzuimenWerknemer(dienstverband.getId());
			if (verzuimen == null)
				verzuimen = new ArrayList<VerzuimInfo>();
		} catch (PermissionException e) {
			ExceptionLogger.ProcessException(e,this);
			return;
		} catch (VerzuimApplicationException e2) {
        	ExceptionLogger.ProcessException(e2,this);
        	return;
		} catch (Exception e1){
        	ExceptionLogger.ProcessException(e1,this);
        	return;
		}
		
		openverzuimfound = false;
		if (verzuimen != null){
			for(VerzuimInfo v :verzuimen)
			{
				if (v.getAction() == persistenceaction.DELETE)
					;
				else
				{
					Vector<Object> rowData = new Vector<Object>();
					cascode = (CascodeInfo) hashCascodes.get(v.getCascode());
					v.setWerknemer(werknemer);
					rowData.add(cascode.getCascode() + "," + cascode.getOmschrijving());
					rowData.add(v.getVerzuimtype().toString());
					rowData.add(v.getStartdatumverzuim());
					rowData.add(v.getVangnettype().toString());
					if (v.getEinddatumverzuim() == null)
					{
						openverzuimfound = true;
						rowData.add(null);
					}
					else
						rowData.add(v.getEinddatumverzuim());
					datumHerstel = v.getStartdatumverzuim();
					percentage = BigDecimal.ZERO;
					for (VerzuimHerstelInfo h:herstellen)
					{
						if (v.getId().equals(h.getVerzuimId()))
						{
							if (h.getDatumHerstel().after(datumHerstel) ||
								h.getDatumHerstel().equals(datumHerstel))
							{
								datumHerstel = h.getDatumHerstel();
								percentage = h.getPercentageHerstel();
							}
						}
					}
					
					rowData.add(percentage.toString());
					if (v.getKetenverzuim())
						rowData.add("Ja");
					else
						rowData.add("N.v.t.");
					if (v.getEinddatumverzuim() == null)
						if (v.getVerzuimtype() == __verzuimtype.VERZUIM)
							tpVerzuimen.addRow(rowData, v, Color.ORANGE);
						else
							tpVerzuimen.addRow(rowData, v, Color.GREEN);
					else
						if (v.getVerzuimtype() == __verzuimtype.PREVENTIEF)
							tpVerzuimen.addRow(rowData, v, Color.PINK);
						else
							tpVerzuimen.addRow(rowData, v);
				}
			}
		}
	}

	public void setInfo(InfoBase info){
		
		werknemerFast = (WerknemerFastInfo)info;
		
		tpAfdelingen.setLoginSession(getLoginSession());
		tpVerzuimen.setLoginSession(getLoginSession());
		tpWiaPercentages.setLoginSession(getLoginSession());
		tpTodos.setLoginSession(getLoginSession());
		teverwijderenafdelingen = null;
		
		try {
			tpAfdelingen.setLoginSession(this.getLoginSession());
			werkgevers = ServiceCaller.werkgeverFacade(getLoginSession()).allWerkgeversList();
			templates = ServiceCaller.verzuimFacade(getLoginSession()).getDocumentTemplates();
		} catch (ServiceLocatorException e) {
        	ExceptionLogger.ProcessException(e,this,"Unable to connect to server");
        	return;
		} catch (PermissionException e) {
			ExceptionLogger.ProcessException(e,this);
			return;
		} catch (VerzuimApplicationException e2) {
        	ExceptionLogger.ProcessException(e2,this);
        	return;
		} catch (RuntimeException e) {
			ExceptionLogger.ProcessException(e,this);
			throw e;
		} catch (Exception e1){
        	ExceptionLogger.ProcessException(e1,this);
        	return;
		}

		try {
			cmbWerkgever.setEnabled(true);
			if (this.getMode() == formMode.New){
				txtBSN.setEnabled(false);
				werknemer = new WerknemerInfo();
				werknemer.setAdres(new AdresInfo());
				if (werknemerFast.getWerkgeverid() == null){
					// cmbWerkgever.setEnabled(true);
				}else{
					try {
						werknemer.setWerkgeverid(werknemerFast.getWerkgeverid());
						werkgever = ServiceCaller.werkgeverFacade(getLoginSession()).getWerkgever(werknemerFast.getWerkgeverid());
					} catch (PermissionException e1) {
						ExceptionLogger.ProcessException(e1,thisform);
						return;
					} catch (VerzuimApplicationException e2) {
						ExceptionLogger.ProcessException(e2,this);
						return;
					} catch (Exception e1){
			        	ExceptionLogger.ProcessException(e1,this);
			        	return;
					}
				}
				btnNewDienstverbandClicked(null);
				
				List<WiapercentageInfo> wiapercentages = new ArrayList<WiapercentageInfo>();
				List<AfdelingHasWerknemerInfo> afdelingen = new ArrayList<AfdelingHasWerknemerInfo>();
				werknemer.setWiaPercentages(wiapercentages);
				werknemer.setAfdelingen(afdelingen);
				WiapercentageInfo wiapercentage = new WiapercentageInfo();
				wiapercentage.setCodeWiaPercentage(__wiapercentage.NVT);
				wiapercentage.setStartdatum(new Date());
				wiapercentage.setWerknemer(werknemer);
				wiapercentages.add(wiapercentage);
			}else{ 
				werknemer = ServiceCaller.werknemerFacade(getLoginSession()).getWerknemer(werknemerFast.getId());
				if (werknemer.getWerkgever() == null){
					werkgever = ServiceCaller.werkgeverFacade(getLoginSession()).getWerkgever(werknemer.getWerkgeverid());
				}else{
					werkgever = ServiceCaller.werkgeverFacade(getLoginSession()).getWerkgever(werknemer.getWerkgever().getId());
				}
			}
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
		displayWerknemerInfo();
        initialized = true;
	}
	private void displayWerknemerInfo() {
		AdresInfo woonadres;
		TypeEntry geslacht;
		TypeEntry burgerlijkestaat;
		TypeEntry werkgevertype;
		DefaultComboBoxModel<TypeEntry> geslachtModel;
		DefaultComboBoxModel<TypeEntry> burgerlijkestaatModel;
		DefaultComboBoxModel<TypeEntry> werkgeverModel = (DefaultComboBoxModel<TypeEntry>) cmbWerkgever.getModel();
		DefaultComboBoxModel<TypeEntry> templateModel;
		boolean found;
		woonadres = werknemer.getAdres();
		adresPanel.setAdres(woonadres);

		txtAchternaam.setText(werknemer.getAchternaam());
		txtBSN.setText(werknemer.getBurgerservicenummer());
		txtEmail.setText(werknemer.getEmail());
		txtTelefoonMobiel.setText(werknemer.getMobiel());
		txtTelefoonprive.setText(werknemer.getTelefoonPrive());
		txtTelefoonthuis.setText(werknemer.getTelefoon());
		txtVoorletters.setText(werknemer.getVoorletters());
		txtVoornaam.setText(werknemer.getVoornaam());
		txtVoorvoegsel.setText(werknemer.getVoorvoegsel());
		taOpmerkingen.setText(werknemer.getOpmerkingen());
		try {
			dtpGeboortedatum.setDate(werknemer.getGeboortedatum());
		} catch (PropertyVetoException e) {
			ExceptionLogger.ProcessException(e,this);		}

		found = false;
		for (dienstverbandinx=0;dienstverbandinx < werknemer.getDienstVerbanden().size();dienstverbandinx++) 
		{
			dienstverband =  werknemer.getDienstVerbanden().get(dienstverbandinx);
			if (dienstverband.getEinddatumcontract() == null)
			{
				found = true;
				break;
			}
		}
		
		btnNewDienstverband.setEnabled(found == false);
		tpDienstverbanden.getNewButton().setEnabled(found == false);

		if (!found)
		{
			dienstverbandinx = werknemer.getDienstVerbanden().size() - 1;
			dienstverband 	 = werknemer.getDienstVerbanden().get(dienstverbandinx);
		}
	
		if (!initialized){
			werkgeverModel = new DefaultComboBoxModel<TypeEntry>();
			cmbWerkgever.setModel(werkgeverModel);
			werkgeverModel.addElement(new TypeEntry(-1,"[]"));
			WerkgeverInfo.sort(werkgevers, WerkgeverInfo.__sortcol.NAAM);
			for (WerkgeverInfo w: werkgevers)
			{
				if (w.isActief()){
					TypeEntry wg = new TypeEntry(w.getId(), w.getNaam());
					werkgeverModel.addElement(wg);
				}
			}
		}		
		geslachtModel = new DefaultComboBoxModel<TypeEntry>();
        cmbGeslacht.setModel(geslachtModel);
        for (__geslacht g: __geslacht.values())
        {
        	TypeEntry soort = new TypeEntry(g.getValue(), g.toString());
        	geslachtModel.addElement(soort);
        }

        burgerlijkestaatModel = new DefaultComboBoxModel<TypeEntry>();
        cmbBurgerlijkestaat.setModel(burgerlijkestaatModel);
        for (__burgerlijkestaat g: __burgerlijkestaat.values())
        {
        	TypeEntry soort = new TypeEntry(g.getValue(), g.toString());
        	burgerlijkestaatModel.addElement(soort);
        }

        if (werknemer.getState() != persistencestate.EXISTS) {
        	cmbGeslacht.setSelectedIndex(0);
        	cmbBurgerlijkestaat.setSelectedIndex(0);
        	cbArbeidsgehandicapt.setSelected(false);
        }
        else
        {
        	cbArbeidsgehandicapt.setSelected(werknemer.isArbeidsgehandicapt());
			for (int i=0;i<geslachtModel.getSize();i++)
			{
				geslacht = (TypeEntry) geslachtModel.getElementAt(i);
				if (__geslacht.parse(geslacht.getValue()) == werknemer.getGeslacht())
				{
					geslachtModel.setSelectedItem(geslacht);
					break;
				}
			}
			for (int i=0;i<burgerlijkestaatModel.getSize();i++)
			{
				burgerlijkestaat = (TypeEntry) burgerlijkestaatModel.getElementAt(i);
				if (__burgerlijkestaat.parse(burgerlijkestaat.getValue()) == werknemer.getBurgerlijkestaat())
				{
					burgerlijkestaatModel.setSelectedItem(burgerlijkestaat);
					break;
				}
			}
        }
		for (int i=0;i<werkgeverModel.getSize();i++)
		{
			werkgevertype = (TypeEntry) werkgeverModel.getElementAt(i);
			if (werknemer.getWerkgeverid() != null){
				if (werkgevertype.getValue() == werknemer.getWerkgeverid())
				{
					werkgeverModel.setSelectedItem(werkgevertype);
					txtBSN.setEnabled(true);
					break;
				}
			}
		}
		templateModel = new DefaultComboBoxModel<TypeEntry>();
		
		DocumentTemplateInfo.sort(templates, DocumentTemplateInfo.__sortcol.NAAM);
		
		cmbTemplates.setModel(templateModel);
		for (DocumentTemplateInfo dt : templates) {
			TypeEntry tmpl = new TypeEntry(dt.getId(), dt.getNaam());
			templateModel.addElement(tmpl);
		}
		
		try{
			herstellen = ServiceCaller.verzuimFacade(getLoginSession()).getVerzuimHerstellen(dienstverband.getId());
			verzuimen = ServiceCaller.verzuimFacade(getLoginSession()).getVerzuimenWerknemer(dienstverband.getId());
			if (verzuimen == null)
				verzuimen = new ArrayList<VerzuimInfo>();
			cascodes = ServiceCaller.cascodeFacade(getLoginSession()).allCascodes();
			gebruikers = ServiceCaller.verzuimFacade(getLoginSession()).getGebruikers();
			for (CascodeInfo ci: cascodes)
			{
				hashCascodes.put(ci.getId(), ci);
			}
		} catch (PermissionException e) {
			ExceptionLogger.ProcessException(e,this);
			return;
		} catch (VerzuimApplicationException e2) {
        	ExceptionLogger.ProcessException(e2,this);
        	return;
		} catch (Exception e1){
        	ExceptionLogger.ProcessException(e1,this);
        	return;
		}
        enableNextPrevButtons();
        displayDienstverband();
        displayDienstverbanden();
        displayAfdelingen();
        displayWiapercentages();
		
        List<RowSorter.SortKey> sortKeys;
		sortKeys = new ArrayList<RowSorter.SortKey>();
        sortKeys.add(new RowSorter.SortKey(2, SortOrder.DESCENDING));
        tpVerzuimen.getSorter().setSortKeys(sortKeys);
        tpVerzuimen.getSorter().sort();

        tpVerzuimen.ReloadTable();
        
        activateListener();
		
	}
	void initialize() {
		setBounds(100, 100, 782, 628);

		adresPanel = new AdresPanel();
		adresPanel.setBounds(21, 237, 403, 90);
		adresPanel.setAdresSoort("Woonadres");
		getContentPane().add(adresPanel);
		
		JLabel lblAchternaam = new JLabel("Achternaam");
		lblAchternaam.setBounds(33, 60, 75, 14);
		getContentPane().add(lblAchternaam);
		
		txtAchternaam = new JTextFieldTGI();
		lblAchternaam.setLabelFor(txtAchternaam);
		txtAchternaam.setBounds(130, 57, 152, 20);
		getContentPane().add(txtAchternaam);
		txtAchternaam.setColumns(10);
		
		JLabel lblVoornaam = new JLabel("Voornaam");
		lblVoornaam.setBounds(33, 83, 74, 14);
		getContentPane().add(lblVoornaam);
		
		txtVoornaam = new JTextFieldTGI();
		lblVoornaam.setLabelFor(txtVoornaam);
		txtVoornaam.setBounds(130, 80, 152, 20);
		getContentPane().add(txtVoornaam);
		txtVoornaam.setColumns(10);
		
		JLabel lblVoorvoegsel = new JLabel("Voorvoegsel");
		lblVoorvoegsel.setBounds(295, 60, 65, 14);
		getContentPane().add(lblVoorvoegsel);
		
		txtVoorvoegsel = new JTextFieldTGI();
		lblVoorvoegsel.setLabelFor(txtVoorvoegsel);
		txtVoorvoegsel.setBounds(367, 57, 86, 20);
		getContentPane().add(txtVoorvoegsel);
		txtVoorvoegsel.setColumns(10);
		
		JLabel lblBsn = new JLabel("BSN");
		lblBsn.setBounds(33, 37, 46, 14);
		getContentPane().add(lblBsn);
		
		txtBSN = new JTextFieldTGI();
		lblBsn.setLabelFor(txtBSN);
		txtBSN.setBounds(130, 34, 86, 20);
		getContentPane().add(txtBSN);
		txtBSN.setColumns(10);
		txtBSN.addFocusListener(new FocusListener() {
			
			@Override
			public void focusLost(FocusEvent e) {
				bsnFieldExit();
			}
			
			@Override
			public void focusGained(FocusEvent e) {
			}
		});
		
		JLabel lblVoorletters = new JLabel("Voorletters");
		lblVoorletters.setBounds(295, 83, 65, 14);
		getContentPane().add(lblVoorletters);
		
		txtVoorletters = new JTextFieldTGI();
		lblVoorletters.setLabelFor(txtVoorletters);
		txtVoorletters.setBounds(367, 80, 86, 20);
		getContentPane().add(txtVoorletters);
		txtVoorletters.setColumns(10);
		
		dtpGeboortedatum = new DatePicker();
		dtpGeboortedatum.setBounds(130, 104, 112, 21);
		dtpGeboortedatum.setDateFormat(new SimpleDateFormat("dd-MM-yyyy"));
		getContentPane().add(dtpGeboortedatum);
		
		JLabel lblGeboortedatum = new JLabel("Geboortedatum");
		lblGeboortedatum.setLabelFor(dtpGeboortedatum);
		lblGeboortedatum.setBounds(34, 107, 75, 14);
		getContentPane().add(lblGeboortedatum);
		
		JLabel label = new JLabel("Geslacht");
		label.setBounds(295, 107, 64, 14);
		getContentPane().add(label);

		cmbGeslacht = new JComboBox<TypeEntry>();
		label.setLabelFor(cmbGeslacht);
		cmbGeslacht.setMaximumRowCount(3);
		cmbGeslacht.setBounds(367, 104, 62, 20);
		getContentPane().add(cmbGeslacht);
		
		cmbBurgerlijkestaat = new JComboBox<TypeEntry>();
		cmbBurgerlijkestaat.setMaximumRowCount(3);
		cmbBurgerlijkestaat.setBounds(522, 105, 130, 20);
		getContentPane().add(cmbBurgerlijkestaat);
		
		JLabel lblBurgStaat = new JLabel("Burg. staat");
		lblBurgStaat.setLabelFor(cmbBurgerlijkestaat);
		lblBurgStaat.setBounds(450, 108, 65, 14);
		getContentPane().add(lblBurgStaat);
		
		JLabel lblTelefoonThuis = new JLabel("Telefoon werk");
		lblTelefoonThuis.setBounds(34, 130, 75, 14);
		getContentPane().add(lblTelefoonThuis);
		
		JLabel lblPriv = new JLabel("priv\u00E9");
		lblPriv.setBounds(76, 153, 32, 14);
		getContentPane().add(lblPriv);
		
		JLabel lblMobiel = new JLabel("mobiel");
		lblMobiel.setBounds(76, 176, 32, 14);
		getContentPane().add(lblMobiel);
		
		txtTelefoonthuis = new JTextFieldTGI();
		lblTelefoonThuis.setLabelFor(txtTelefoonthuis);
		txtTelefoonthuis.setBounds(130, 127, 130, 20);
		getContentPane().add(txtTelefoonthuis);
		txtTelefoonthuis.setColumns(10);
		
		txtTelefoonprive = new JTextFieldTGI();
		lblPriv.setLabelFor(txtTelefoonprive);
		txtTelefoonprive.setColumns(10);
		txtTelefoonprive.setBounds(130, 150, 130, 20);
		getContentPane().add(txtTelefoonprive);
		
		txtTelefoonMobiel = new JTextFieldTGI();
		lblMobiel.setLabelFor(txtTelefoonMobiel);
		txtTelefoonMobiel.setColumns(10);
		txtTelefoonMobiel.setBounds(130, 173, 130, 20);
		getContentPane().add(txtTelefoonMobiel);
		
		JLabel lblEmailadres = new JLabel("Emailadres");
		lblEmailadres.setBounds(33, 199, 75, 14);
		getContentPane().add(lblEmailadres);
		
		txtEmail = new JTextFieldTGI();
		lblEmailadres.setLabelFor(txtEmail);
		txtEmail.setColumns(50);
		txtEmail.setBounds(130, 196, 239, 20);
		getContentPane().add(txtEmail);
		
		cmbWerkgever = new JComboBox<TypeEntry>();
		cmbWerkgever.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				cmbWerkgeverClicked(e);
			}
		});
		cmbWerkgever.addPopupMenuListener(new PopupMenuListener() {
			
			@Override
			public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
			}
			
			@Override
			public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
				EventQueue.invokeLater(new Runnable() {
					@Override
					public void run() {
						WerkgeverSelected();
					}
				});
			}
			
			@Override
			public void popupMenuCanceled(PopupMenuEvent e) {
			}
		});
		cmbWerkgever.setBounds(130, 11, 239, 20);
		getContentPane().add(cmbWerkgever);
		
		JLabel lblWerkgever = new JLabel("Werkgever");
		lblWerkgever.setLabelFor(cmbWerkgever);
		lblWerkgever.setBounds(33, 14, 65, 14);
		getContentPane().add(lblWerkgever);
		
		cbArbeidsgehandicapt = new JCheckBox("Arbeidsgehandicapt");
		cbArbeidsgehandicapt.setBounds(367, 131, 130, 23);
		getContentPane().add(cbArbeidsgehandicapt);
		
		JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		tabbedPane.setBackground(Color.LIGHT_GRAY);
		tabbedPane.setBounds(21, 349, 615, 190);
		getContentPane().add(tabbedPane);
		
		tpVerzuimen = new TablePanel(this.getMdiPanel());
		tpVerzuimen.getPanelAction().setSize(583, 30);
		tpVerzuimen.getPanelAction().setLocation(5, 124);
		tpVerzuimen.getScrollPane().setBounds(5, 0, 612, 124);
		tpVerzuimen.setDetailFormClass(VerzuimDetail.class, VerzuimInfo.class);
		tpVerzuimen.addColumn("CAScode", null,120 );
		tpVerzuimen.addColumn("Type",null,60);
		tpVerzuimen.addColumn("Aanvang",null,60,Date.class);
		tpVerzuimen.addColumn("Vangnet",null,60);
		tpVerzuimen.addColumn("Herstel",null,60,Date.class);
		tpVerzuimen.addColumn("%Herstel",null,60);
		tpVerzuimen.addColumn("Ketenvzm",null,70);
		tpVerzuimen.setEventNotifier(new DefaultListFormNotification() {
			@Override
			public void rowSelected(InfoBase info){
				selectedVerzuim = (VerzuimInfo)info;
				btnMedischekaart.setEnabled(true);
				btnGenereer.setEnabled(true);
				cmbTemplates.setEnabled(true);
				tpTodos.setVerzuim(selectedVerzuim);
			}
			
			@Override
			public void populateTableRequested() {
				displayVerzuimen();
			}
			@Override
			public void populateTableComplete() {
				btnMedischekaart.setEnabled(false);
				btnGenereer.setEnabled(false);
				cmbTemplates.setEnabled(false);
		        if (openverzuimfound){
		        	tpVerzuimen.getTable().changeSelection(0, 0, false, false);
		        }
		        int SelectedRow = tpVerzuimen.getTable().getSelectedRow();
		        Rectangle rect = tpVerzuimen.getTable().getCellRect(SelectedRow, 0, true);  
		        tpVerzuimen.getScrollPane().getViewport().scrollRectToVisible(rect); 		        
			}
			@Override
			public void newCreated(InfoBase info) {
				VerzuimInfo verzuim = (VerzuimInfo)info;
				verzuim.setDienstverbandId(dienstverband.getId());
				verzuim.setDienstverband(dienstverband);
				verzuim.setWerknemer(werknemer);
			}
			@Override
			public __continue newButtonClicked() {
				if (openverzuimfound)
				{
					JOptionPane.showMessageDialog(thisform, "Nieuw verzuim aanmaken niet toegestaan.\r\nEr is nog een open verzuim");
					return __continue.dontallow;
				}
				else
					return __continue.allow;
			}
			@Override
			public __continue deleteButtonClicked(InfoBase info) {
				try {
					int rslt = JOptionPane.showConfirmDialog(thisform, "Weet u zeker dat u het verzuim wilt verwijderen?","",JOptionPane.YES_NO_OPTION);
					if (rslt == JOptionPane.YES_OPTION){
						VerzuimInfo vzm = ServiceCaller.verzuimFacade(getLoginSession()).getVerzuimDetails(((VerzuimInfo)info).getId());
						if (vzm.getVerzuimmedischekaarten() != null &&
								vzm.getVerzuimmedischekaarten().size() > 0){
							rslt = JOptionPane.showConfirmDialog(thisform, "De medische kaart is niet leeg.\n Weet u zeker dat u het verzuim wilt verwijderen?","",JOptionPane.YES_NO_OPTION);
							if (rslt != JOptionPane.YES_OPTION){
								return __continue.dontallow;
							}
						}
						
						ServiceCaller.verzuimFacade(getLoginSession()).deleteVerzuim((VerzuimInfo)info);
						selectedVerzuim = null;
						btnMedischekaart.setEnabled(false);
						btnGenereer.setEnabled(false);
						cmbTemplates.setEnabled(false);
					}
					else
						return __continue.dontallow;
				} catch (PermissionException e) {
					ExceptionLogger.ProcessException(e,thisform);
				} catch (ValidationException e) {
					ExceptionLogger.ProcessException(e,thisform,false);
				} catch (VerzuimApplicationException e) {
					ExceptionLogger.ProcessException(e,thisform);
				} catch (Exception e1){
		        	ExceptionLogger.ProcessException(e1,thisform);
				}
				return __continue.allow;
			}
			@Override
			public void newInfoAdded(InfoBase info){
				verzuimen.add((VerzuimInfo)info);
			}
			@Override
			public __continue detailButtonClicked(InfoBase info){
				selectedVerzuim = (VerzuimInfo)info;
				return __continue.allow;
			}
			@Override
			public void detailFormClosed(){
				if (selectedVerzuim != null){
					if (verzuimen.contains(selectedVerzuim))
						verzuimen.remove(selectedVerzuim);
					try {
						herstellen = ServiceCaller.verzuimFacade(getLoginSession()).getVerzuimHerstellen(dienstverband.getId());
						selectedVerzuim = ServiceCaller.verzuimFacade(getLoginSession()).getVerzuimDetails(selectedVerzuim.getId());
						verzuimen.add(selectedVerzuim);
					} catch (PermissionException e) {
						ExceptionLogger.ProcessException(e,thisform);
						return;
					} catch (VerzuimApplicationException e2) {
			        	ExceptionLogger.ProcessException(e2,thisform);
			        	return;
					} catch (Exception e1){
			        	ExceptionLogger.ProcessException(e1,thisform);
			        	return;
					}
				}
			}

		});
		
		tabbedPane.addTab("Verzuimen", null, tpVerzuimen, null);
		tpAfdelingen = new TablePanel(this.getMdiPanel());
		tpAfdelingen.getPanelAction().setLocation(5, 124);
		tpAfdelingen.getScrollPane().setBounds(5, 0, 375, 125);
		tpAfdelingen.setDetailFormClass(AfdelingHasWerknemerDetail.class, AfdelingHasWerknemerInfo.class);
		tpAfdelingen.addColumn("Naam", null, 100);
		tpAfdelingen.addColumn("Ingangsdatum", null, 100, Date.class);
		tpAfdelingen.addColumn("Einddatum",null,80, Date.class);
		tpAfdelingen.setEventNotifier(new DefaultListFormNotification() {
			
			@Override
			public void populateTableRequested() {
				displayAfdelingen();
			}
			@Override
			public void newCreated(InfoBase info) {
				AfdelingHasWerknemerInfo afd = (AfdelingHasWerknemerInfo)info;
				if (((BaseDetailform)thisform).getMode() == formMode.New)
					afd.setStartdatum(dtpIngangsdatum.getDate());
				else{
					dienstverband.setStartdatumcontract(dtpIngangsdatum.getDate());
					afd.setStartdatum(werknemer.getLaatsteDienstverband().getStartdatumcontract());
				}
				afd.setWerkgeverId(werknemer.getWerkgeverid());
				afd.setWerknemerId(werknemer.getId());
			}
			@Override
			public __continue newButtonClicked() {
				if (werkgever.getAfdelings() != null && werkgever.getAfdelings().size() > 0){
					if (dtpIngangsdatum.getDate() == null){
						JOptionPane.showMessageDialog(thisform, "Vul eerst de startdatum van het dienstverband in!");
						return __continue.dontallow;
					}else{
						return __continue.allow;
					}
				}else{
					JOptionPane.showMessageDialog(thisform, "Er zijn (nog) geen afdelingen bij de werkgever");
					return __continue.dontallow;
				}
			}
			@Override
			public void newInfoAdded(InfoBase info){
				werknemer.getAfdelingen().add((AfdelingHasWerknemerInfo)info);
				if (huidigeafdeling != null)
					huidigeafdeling.setEinddatum(DateOnly.addDays(((AfdelingHasWerknemerInfo)info).getStartdatum(),-1));
				
			}
			@Override
			public __continue deleteButtonClicked(InfoBase info) {
				AfdelingHasWerknemerInfo afd = (AfdelingHasWerknemerInfo)info;
				int rslt = JOptionPane.showConfirmDialog(thisform, "Weet u zeker dat u afdeling wilt verwijderen?","",JOptionPane.YES_NO_OPTION);
				if (rslt == JOptionPane.YES_OPTION){
					afd.setAction(persistenceaction.DELETE);
				}
				else
					return __continue.dontallow;
				return __continue.allow;
			}
			
		});
	
		tabbedPane.addTab("Afdelingen", null, tpAfdelingen, null);

		tpTodos = new TodoPanel(this.getMdiPanel());
		tpTodos.getPanelAction().setSize(540, 30);
		tpTodos.getPanelAction().setLocation(5, 124);
		tpTodos.getScrollPane().setBounds(5, 0, 595, 124);
		tpTodos.setMyListener(new DefaultListFormNotification(){
			@Override
			public __continue newButtonClicked() {
				if (selectedVerzuim != null)
					return __continue.allow;
				else{
					JOptionPane.showMessageDialog(thisform, "Er is (nog) geen verzuim geselecteerd"); 
					return __continue.dontallow;
				}
			}
		});
		tabbedPane.addTab("TODO", null, tpTodos);
		
		tpWiaPercentages = new TablePanel(this.getMdiPanel());
		tpWiaPercentages.getPanelAction().setLocation(5, 124);
		tpWiaPercentages.getScrollPane().setBounds(5, 0, 375, 124);
		tpWiaPercentages.setDetailFormClass(WiapercentageDetail.class, WiapercentageInfo.class);
		tpWiaPercentages.addColumn("Percentage", null, 100);
		tpWiaPercentages.addColumn("Ingangsdatum", null, 100, Date.class);
		tpWiaPercentages.addColumn("Einddatum",null,80,Date.class);
		tpWiaPercentages.setEventNotifier(new DefaultListFormNotification() {
			
			@Override
			public void populateTableRequested() {
				displayWiapercentages();
			}
			@Override
			public void newCreated(InfoBase info) {
				WiapercentageInfo wia = (WiapercentageInfo)info;
				wia.setStartdatum(new Date());
				wia.setWerknemerId(werknemer.getId());
				wia.setWerknemer(werknemer);
			}
			@Override
			public __continue deleteButtonClicked(InfoBase info) {return __continue.allow;}

			@Override
			public void rowSelected(InfoBase info) {}
			
			@Override
			public void newInfoAdded(InfoBase info){
				werknemer.getWiaPercentages().add((WiapercentageInfo)info);
				if (huidigwiapercentage != null)
					huidigwiapercentage.setEinddatum(((WiapercentageInfo)info).getStartdatum());
			}
			@Override
			public __continue newButtonClicked() {
				return __continue.allow;
			}
		});

		tabbedPane.addTab("WiaPercentages", null, tpWiaPercentages, null);
		
		tpDienstverbanden = new TablePanel(this.getMdiPanel());
		tpDienstverbanden.getPanelAction().setLocation(5, 124);
		tpDienstverbanden.getScrollPane().setBounds(5, 0, 450, 125);
		tpDienstverbanden.setDetailFormClass(DienstverbandDetail.class, DienstverbandInfo.class);
		tpDienstverbanden.addColumn("Pers. Nr", null, 80);
		tpDienstverbanden.addColumn("Ingangsdatum", null, 80, Date.class);
		tpDienstverbanden.addColumn("Einddatum",null,80, Date.class);
		tpDienstverbanden.addColumn("Werkweek",null,80);
		tpDienstverbanden.addColumn("Functie",null,120);
		tpDienstverbanden.setEventNotifier(new DefaultListFormNotification() {
			
			@Override
			public void populateTableRequested() {
				displayDienstverbanden();
			}
			@Override
			public void populateTableComplete() {
				int rowcount = tpDienstverbanden.getTable().getRowCount();
	        	tpDienstverbanden.getTable().changeSelection(rowcount-1, 0, false, false);
		        int SelectedRow = tpDienstverbanden.getTable().getSelectedRow();
		        Rectangle rect = tpDienstverbanden.getTable().getCellRect(SelectedRow, 0, true);  
		        tpDienstverbanden.getScrollPane().getViewport().scrollRectToVisible(rect); 		        
			}
			@Override
			public void newCreated(InfoBase info) {
				
				DienstverbandInfo dvb = (DienstverbandInfo)info;
				dvb.setStartdatumcontract(null);
				if (werknemer.getWerkgeverid() != null){
					dvb.setWerkgeverId(werknemer.getWerkgeverid());
					dvb.setWerkgever(werknemer.getWerkgever());
					dvb.setWerknemer(werknemer);
				}
				if (werknemer.getDienstVerbanden() == null)
					werknemer.setDienstVerbanden(new ArrayList<DienstverbandInfo>());
				werknemer.getDienstVerbanden().add(dvb);
				dienstverbandinx = werknemer.getDienstVerbanden().size()-1;
				dienstverband = dvb;
				displayDienstverband();
				btnNewDienstverband.setEnabled(false);
				enableNextPrevButtons();
			}
			@Override
			public __continue newButtonClicked() {
				if (werknemer.getActiefDienstverband() != null){
					JOptionPane.showMessageDialog(thisform, "Er is nog een actief dienstverband bij de werknemer, sluit dit eerst af");
					return __continue.dontallow;
				}else{
					return __continue.allow;
				}
			}
			@Override
			public void newInfoAdded(InfoBase info){
				btnNewDienstverband.setEnabled(false);
				displayDienstverband();
			}
			@Override
			public void detailFormClosed() {
				if (werknemer.getActiefDienstverband() == null){
					/* Geen actief dienstverband meer. Sluit dan ook de afdeling */
					for (AfdelingHasWerknemerInfo afd: werknemer.getAfdelingen()){
						/*
						 * Als dienstverband wordt afgesloten, dan moet ook de nog actieve
						 * afdeling worden afgesloten
						 */
						if (afd.getEinddatum() == null){
							afd.setEinddatum(werknemer.getLaatsteDienstverband().getEinddatumcontract());
						}
					}
			        enableNextPrevButtons();
					displayDienstverbanden();
					displayAfdelingen();
				}
			}

			@Override
			public void rowSelected(InfoBase info){
				if (werknemer.getActiefDienstverband() != null){
					tpDienstverbanden.getNewButton().setEnabled(false);
				}else{
					tpDienstverbanden.getNewButton().setEnabled(true);
				}
				if (werknemer.getLaatsteDienstverband() != null){
					if (((DienstverbandInfo)info).getId() == werknemer.getLaatsteDienstverband().getId()){
						tpDienstverbanden.getDeleteButton().setEnabled(true);
					}else{
						tpDienstverbanden.getDeleteButton().setEnabled(false);
					}
				}else{
					tpDienstverbanden.getDeleteButton().setEnabled(false);
				}
				dienstverbandinx = -1;
				for (int i=0;i<werknemer.getDienstVerbanden().size();i++){
					if (werknemer.getDienstVerbanden().get(i).getId().equals(((DienstverbandInfo)info).getId())){
						dienstverbandinx = i;
					}
				}
				if (dienstverbandinx < 0){
					throw new RuntimeException("Kan dienstverband niet vinden!");
				}
				
				dienstverband = werknemer.getDienstVerbanden().get(dienstverbandinx);
		        enableNextPrevButtons();
				displayDienstverband();
				tpVerzuimen.ReloadTable();
				
			}
			@Override
			public __continue deleteButtonClicked(InfoBase info) {
				DienstverbandInfo dvb = null;

				for (DienstverbandInfo d: werknemer.getDienstVerbanden()){
					if (d.getId().equals(((DienstverbandInfo)info).getId())){
						dvb = d;
					}
				}
				if (dvb == null){
					return __continue.dontallow;
				}
				
				// Add logic to relocate verzuimen 
				int result = JOptionPane.showConfirmDialog(thisform, "Dienstverband wordt verwijderd. \nWeet u het zeker?", "Dienstverband verwijderen", JOptionPane.YES_NO_OPTION);
				if (result == JOptionPane.NO_OPTION){
					return __continue.dontallow;
				}
				if (werknemer.getDienstVerbanden().size() == 1){
					JOptionPane.showMessageDialog(thisform, "Dit is het enige dienstverband. Het kan niet worden verwijderd. ", "Dienstverband verwijderen", JOptionPane.OK_OPTION);
					return __continue.dontallow;
				}
				
				
				DienstverbandInfo vorigdvb = werknemer.getDienstVerbanden().get(werknemer.getDienstVerbanden().size()-2);
				if (vorigdvb == null){
					JOptionPane.showMessageDialog(thisform, "Vorig dienstverband niet gevonden. Het kan niet worden verwijderd. ", "Dienstverband verwijderen", JOptionPane.OK_OPTION);
					return __continue.dontallow;
				}
				if (!vorigdvb.getWerknemerId().equals(dvb.getWerknemerId())){
					JOptionPane.showMessageDialog(thisform, "Vorig dienstverband niet gevonden. Werknemers ongelijk. ", "Dienstverband verwijderen", JOptionPane.OK_OPTION);
					return __continue.dontallow;
				}
				vorigdvb.setEinddatumcontract(null);
				if (dvb.getVerzuimen() != null && dvb.getVerzuimen().size() > 0){
					/* Er zijn verzuimen aan het dienstverband gekoppeld */
					result = JOptionPane.showConfirmDialog(thisform, "Moeten de verzuimen aan het vorige dienstverband worden gekoppeld?", "Dienstverband verwijderen", JOptionPane.YES_NO_CANCEL_OPTION);
					if (result == JOptionPane.CANCEL_OPTION){
						return __continue.dontallow;
					}
					if (result == JOptionPane.NO_OPTION){
						result = JOptionPane.showConfirmDialog(thisform, "De verzuimen gekoppeld aan dit dienstverand worden verwijderd.\n Weet u het zeker?", "Verzuimen verwijderen", JOptionPane.YES_NO_OPTION);
						if (result == JOptionPane.NO_OPTION){
							return __continue.dontallow;
						}
					}else{
						/* De verzuimen worden omgehangen */
	    				try {
							for (VerzuimInfo vzm: dvb.getVerzuimen()){
								vzm.setDienstverbandId(vorigdvb.getId());
								vzm.setWerknemer(werknemer);
								vzm.setDienstverband(vorigdvb);
								ServiceCaller.verzuimFacade(getLoginSession()).updateVerzuim(vzm);
							}
	    				} catch (ValidationException e1) {
	    		        	ExceptionLogger.ProcessException(e1,thisform,false);
	    				} catch (PermissionException e1) {
	    					ExceptionLogger.ProcessException(e1,thisform);
	    				} catch (VerzuimApplicationException e2) {
	    		        	ExceptionLogger.ProcessException(e2,thisform);
	    				} catch (Exception e1){
	    		        	ExceptionLogger.ProcessException(e1,thisform);
	    				}
					}
				}
   				try {
   					dvb.setAction(persistenceaction.DELETE);
   					/* Afdelingen met ingangsdatum gelijk aan of na startdatum dvb worden verwijderd */
   					AfdelingHasWerknemerInfo prevafd = null;
   					for (AfdelingHasWerknemerInfo afhwi:werknemer.getAfdelingen()){
   						if (DateOnly.before(afhwi.getStartdatum(),dvb.getStartdatumcontract())){
   							;
   						}else{
   							afhwi.setAction(persistenceaction.DELETE);
   							if (prevafd != null){
   								prevafd.setEinddatum(null);
   							}
   						}
   						prevafd = afhwi;
   					}
   					
   					ServiceCaller.werknemerFacade(getLoginSession()).updateWerknemer(werknemer);
   					werknemer = ServiceCaller.werknemerFacade(getLoginSession()).getWerknemer(werknemer.getId());
   					displayWerknemerInfo();
   				} catch (ValidationException e1) {
   		        	ExceptionLogger.ProcessException(e1,thisform,false);
   				} catch (PermissionException e1) {
   					ExceptionLogger.ProcessException(e1,thisform);
   				} catch (VerzuimApplicationException e2) {
   		        	ExceptionLogger.ProcessException(e2,thisform);
   				} catch (Exception e1){
   		        	ExceptionLogger.ProcessException(e1,thisform);
   				}

				return __continue.allow;
			}
		});
	
		tabbedPane.addTab("Dienstverbanden", null, tpDienstverbanden, null);
		tpDienstverbanden.getNewButton().setEnabled(false);
		tpDienstverbanden.getDeleteButton().setEnabled(false);
		
		btnVorigDienstverband = new JButton("<");
		btnVorigDienstverband.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				btnVorigDienstverbandClicked(e);
			}
		});
		btnVorigDienstverband.setBounds(450, 153, 46, 23);
		getContentPane().add(btnVorigDienstverband);
		
		btnVolgendDienstverband = new JButton(">");
		btnVolgendDienstverband.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				btnVolgendDienstverbandClicked(e);
			}
		});
		btnVolgendDienstverband.setBounds(500, 153, 46, 23);
		getContentPane().add(btnVolgendDienstverband);
		
		JPanel panelDienstverband = new JPanel();
		panelDienstverband.setBorder(new TitledBorder(null, "Dienstverband", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		panelDienstverband.setBounds(441, 201, 311, 146);
		getContentPane().add(panelDienstverband);
		panelDienstverband.setLayout(null);
		
		JLabel lblIngangsdatum = new JLabel("Ingangsdatum");
		lblIngangsdatum.setBounds(26, 24, 89, 14);
		panelDienstverband.add(lblIngangsdatum);
		
		dtpIngangsdatum = new DatePicker();
		dtpIngangsdatum.setBounds(141, 21, 89, 21);
		dtpIngangsdatum.setDateFormat(new SimpleDateFormat("dd-MM-yyyy"));
		panelDienstverband.add(dtpIngangsdatum);
		
		JLabel lblPersoneelsnummer = new JLabel("Personeelsnummer");
		lblPersoneelsnummer.setBounds(26, 47, 104, 14);
		panelDienstverband.add(lblPersoneelsnummer);
		
		txtPersoneelsnummer = new JTextFieldTGI();
		txtPersoneelsnummer.setColumns(45);
		txtPersoneelsnummer.setBounds(141, 44, 125, 20);
		panelDienstverband.add(txtPersoneelsnummer);
		
		JLabel lblFunctie = new JLabel("Functie");
		lblFunctie.setBounds(26, 70, 46, 14);
		panelDienstverband.add(lblFunctie);
		
		txtFunctie = new JTextFieldTGI();
		txtFunctie.setColumns(45);
		txtFunctie.setBounds(141, 67, 125, 20);
		panelDienstverband.add(txtFunctie);
		
		JLabel lblWerkweek = new JLabel("Werkweek");
		lblWerkweek.setBounds(26, 93, 65, 14);
		panelDienstverband.add(lblWerkweek);
		
		txtWerkweekUren = new JTextFieldTGI();
		txtWerkweekUren.setBounds(141, 90, 104, 20);
		panelDienstverband.add(txtWerkweekUren);
		
		JLabel lblEinddatum = new JLabel("Einddatum");
		lblEinddatum.setBounds(26, 117, 65, 14);
		panelDienstverband.add(lblEinddatum);
		
		dtpEindatum = new DatePicker();
		dtpEindatum.setBounds(141, 114, 89, 21);
		dtpEindatum.setDateFormat(new SimpleDateFormat("dd-MM-yyyy"));
		panelDienstverband.add(dtpEindatum);
		
		btnNewDienstverband = new JButton("Nieuw");
		btnNewDienstverband.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				btnNewDienstverbandClicked(e);
			}
		});
		btnNewDienstverband.setBounds(556, 153, 80, 23);
		getContentPane().add(btnNewDienstverband);
		
		JButton btnVerzuimhistorie = new JButton("Verzuimhistorie...");
		btnVerzuimhistorie.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				btnVerzuimhistorieClicked(e);
			}
		});
		btnVerzuimhistorie.setBounds(643, 516, 119, 23);
		getContentPane().add(btnVerzuimhistorie);
		
		JButton btnWerkgever = new JButton("Details...");
		btnWerkgever.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				btnWerkgeverClicked(e);
			}
		});
		btnWerkgever.setBounds(377, 10, 86, 23);
		getContentPane().add(btnWerkgever);
		
		btnMedischekaart = new JButton("Medische kaart...");
		btnMedischekaart.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				btnMedischeKaartClicked();
			}
		});
		btnMedischekaart.setBounds(643, 491, 119, 23);
		getContentPane().add(btnMedischekaart);
		
		cmbTemplates = new JComboBox<TypeEntry>();
		cmbTemplates.setBounds(415, 541, 221, 20);
		getContentPane().add(cmbTemplates);
		
		btnGenereer = new JButton("Genereer...");
		btnGenereer.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				btnGenereerClicked();
			}
		});
		btnGenereer.setBounds(643, 541, 119, 23);
		getContentPane().add(btnGenereer);

		btnMedischekaart.setEnabled(false);
		btnGenereer.setEnabled(false);
		cmbTemplates.setEnabled(false);
		
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(474, 34, 278, 67);
		getContentPane().add(scrollPane);
		
		taOpmerkingen = new JTextAreaTGI();
		taOpmerkingen.setFont(btnGenereer.getFont());
		taOpmerkingen.setFocusTraversalKeys(KeyboardFocusManager.BACKWARD_TRAVERSAL_KEYS, null);
		taOpmerkingen.setFocusTraversalKeys(KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS, null);
		scrollPane.setViewportView(taOpmerkingen);
		
		JLabel lblOpmerkingen = new JLabel("Opmerkingen");
		lblOpmerkingen.setBounds(473, 14, 65, 14);
		getContentPane().add(lblOpmerkingen);

	}
	protected void bsnFieldExit() {
		if (this.getMode() == formMode.New ){
			if (savedBSN.equals(txtBSN.getText().trim()))
				;/* nothing changed */
			else {
				try {
					WerknemerInfo.validateBSN(txtBSN.getText());

					List<WerknemerInfo> existingbsns;
					existingbsns = ServiceCaller.werknemerFacade(getLoginSession()).getByBSN(werkgever.getId(), txtBSN.getText().trim());
					if (existingbsns.size() == 0){
						existingbsns = ServiceCaller.werknemerFacade(getLoginSession()).getByBSN(txtBSN.getText().trim());
						if (existingbsns.size() == 0)
							;
						else{
							int reply = JOptionPane.showConfirmDialog(thisform, 
									"Er bestaat reeds een medewerker bij een ander bedrijf met hetzelfde BSN.\r" +
									"Wilt u deze gebruiken?", "BSN", JOptionPane.YES_NO_OPTION);
							if (reply == JOptionPane.YES_OPTION){
								werknemer = getMostRecentBSN(existingbsns);
								
								this.setMode(formMode.New);
								werknemer.setAction(persistenceaction.INSERT);
								werknemer.setId(-1);
								werknemer.getAdres().setAction(persistenceaction.INSERT);
								werknemer.getAdres().setState(persistencestate.ABSENT);
								werknemer.getAdres().setId(-1);
								for (WiapercentageInfo wpi: werknemer.getWiaPercentages()){
									wpi.setAction(persistenceaction.INSERT);
									wpi.setId(-1);
									wpi.setWerknemerId(-1);
								}
								werknemer.setWerkgever(werkgever);
								werknemer.setWerkgeverid(werkgever.getId());
								List<AfdelingHasWerknemerInfo> afdelingen = new ArrayList<AfdelingHasWerknemerInfo>();
								werknemer.setAfdelingen(afdelingen);
								werknemer.setDienstVerbanden(null);
								btnNewDienstverbandClicked(null);
								displayWerknemerInfo();
							}
						}
					}
					else
						if (existingbsns.size() > 0)
						{
							int reply = JOptionPane.showConfirmDialog(thisform, 
												"Er bestaat reeds een medewerker bij dit bedrijf met hetzelfde BSN.\r" +
												"Wilt u deze gebruiken?", "BSN", JOptionPane.YES_NO_OPTION);
							if (reply == JOptionPane.YES_OPTION){
								werknemer = getMostRecentBSN(existingbsns); 
								this.setMode(formMode.Update);
								displayWerknemerInfo();
							}
						}							
					savedBSN = txtBSN.getText().trim();
					
				} catch (PermissionException e) {
					ExceptionLogger.ProcessException(e,thisform);
					return;
				} catch (VerzuimApplicationException e2) {
		        	ExceptionLogger.ProcessException(e2,this);
		        	return;
				} catch (Exception e1){
		        	ExceptionLogger.ProcessException(e1,this);
		        	return;
				}
				
			}
		}else{
			if (savedBSN.equals(txtBSN.getText().trim()))
				;/* nothing changed */
			else {
				try {
					WerknemerInfo.validateBSN(txtBSN.getText());
				} catch (Exception e1){
		        	ExceptionLogger.ProcessException(e1,this);
		        	return;
				}
			}
		}
	}
	private WerknemerInfo getMostRecentBSN(List<WerknemerInfo> existingbsns) {
		WerknemerInfo recentst = existingbsns.get(0);
		for (WerknemerInfo wi : existingbsns){
			if (recentst.getLaatsteDienstverband().getStartdatumcontract().before(wi.getLaatsteDienstverband().getStartdatumcontract()))
				recentst = wi;
		}
		return recentst;
	}
	protected void btnGenereerClicked() {
		TypeEntry document = (TypeEntry) cmbTemplates.getSelectedItem();
		for (DocumentTemplateInfo ti : templates) {
			if (document.getValue() == ti.getId()) {
				WordDocument doc = new WordDocument(this, getLoginSession());
				try {
					doc.GenerateDocument(ServiceCaller.verzuimFacade(getLoginSession()).getVerzuimDetails(selectedVerzuim.getId()), ti);
				} catch (PermissionException e) {
					ExceptionLogger.ProcessException(e,this);
				} catch (VerzuimApplicationException e2) {
		        	ExceptionLogger.ProcessException(e2,this);
				} catch (Exception e1){
		        	ExceptionLogger.ProcessException(e1,this);
				}
				break;
			}
		}
	}
	protected void btnWerkgeverClicked(ActionEvent e) {
		JDesktopPaneTGI mdiPanel = this.getMdiPanel();
		WerkgeverDetail dlgWerkgeverdetail;
		
		if (werkgever != null && werkgever.getId() != -1){
			dlgWerkgeverdetail = new WerkgeverDetail(mdiPanel);
			dlgWerkgeverdetail.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			dlgWerkgeverdetail.setLoginSession(getLoginSession());
			dlgWerkgeverdetail.setMode(formMode.Update);
			dlgWerkgeverdetail.setVisible(true);
			WerkgeverInfo werkgever = new WerkgeverInfo();
			werkgever.setId(werknemer.getWerkgeverid());
			dlgWerkgeverdetail.setInfo(werkgever);
			mdiPanel.add(dlgWerkgeverdetail);
			mdiPanel.setOpaque(true);
			mdiPanel.moveToFront(dlgWerkgeverdetail);
		}
	}
	protected void btnVerzuimhistorieClicked(ActionEvent e) {
		JDesktopPaneTGI mdiPanel = this.getMdiPanel();
		dlgReportVerzuimHistorie = new ReportVerzuimenHistorie(mdiPanel);
		dlgReportVerzuimHistorie.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		dlgReportVerzuimHistorie.setLoginSession(getLoginSession());
		dlgReportVerzuimHistorie.setVisible(true);
		dlgReportVerzuimHistorie.setInfo(werknemer.getWerkgeverid(),werknemer.getId());
		mdiPanel.add(dlgReportVerzuimHistorie);
		mdiPanel.setOpaque(true);
		mdiPanel.moveToFront(dlgReportVerzuimHistorie);
	}
	protected void btnMedischeKaartClicked() {
		JDesktopPane mdiPanel = this.getDesktopPane();
		VerzuimMedischeKaartOverzicht dlgVerzuimMedischeKaart = new VerzuimMedischeKaartOverzicht(
				this.getMdiPanel());
		dlgVerzuimMedischeKaart.setLoginSession(this.getLoginSession());
		dlgVerzuimMedischeKaart.setVisible(true);
		try {
			dlgVerzuimMedischeKaart.setInfo(ServiceCaller.verzuimFacade(getLoginSession()).getVerzuimDetails(selectedVerzuim.getId()), gebruikers);
		} catch (PermissionException e) {
			ExceptionLogger.ProcessException(e,this);
			return;
		} catch (VerzuimApplicationException e2) {
        	ExceptionLogger.ProcessException(e2,this);
        	return;
		} catch (Exception e1){
        	ExceptionLogger.ProcessException(e1,this);
        	return;
		}
		mdiPanel.add(dlgVerzuimMedischeKaart);
		mdiPanel.setOpaque(true);
		mdiPanel.moveToFront(dlgVerzuimMedischeKaart);
	}
	
	private void WerkgeverSelected(){
		TypeEntry selected;
		if (initialized){
			selected = (TypeEntry)cmbWerkgever.getSelectedItem();
			try {
				if (selected.getValue() != -1){
					if (werknemer.getWerkgeverid() != null && werknemer.getWerkgeverid() != -1){
						int result = JOptionPane.showConfirmDialog(this, "Werkgever wordt gewijzigd. \n Het dienstverband en de verzuimen verhuizen mee." + 
																	 "\n De afdelingen worden verwijderd. Weet u het zeker?", "Werkgever wijzigen", JOptionPane.YES_NO_OPTION);
						if (result == JOptionPane.NO_OPTION){
							// Zet de oorspronkelijke werkgever weer terug.
							DefaultComboBoxModel<TypeEntry> model = (DefaultComboBoxModel<TypeEntry>)cmbWerkgever.getModel();
							for (int i=0;i<model.getSize();i++)
							{
								TypeEntry werkgevertype = (TypeEntry) model.getElementAt(i);
								if (werknemer.getWerkgeverid() != null){
									if (werkgevertype.getValue() == werknemer.getWerkgeverid())
									{
										model.setSelectedItem(werkgevertype);
										break;
									}
								}
							}
							return;
						}
					}
					txtBSN.setEnabled(true);
					werkgever = ServiceCaller.werkgeverFacade(getLoginSession()).getWerkgever(selected.getValue());
					dienstverband.setWerkgeverId(werkgever.getId());
					dienstverband.setWerkgever(werkgever);
					dienstverband.setWerknemer(werknemer);
					werknemer.setWerkgever(werkgever);
					werknemer.setWerkgeverid(werkgever.getId());
					for (DienstverbandInfo dvb:werknemer.getDienstVerbanden()){
						dvb.setWerkgever(werkgever);
						dvb.setWerkgeverId(werkgever.getId());
					}
					teverwijderenafdelingen = new ArrayList<AfdelingHasWerknemerInfo>();
					for (AfdelingHasWerknemerInfo ahwi: werknemer.getAfdelingen()){
						ahwi.setAction(persistenceaction.DELETE);
						teverwijderenafdelingen.add(ahwi);
					}
					werknemer.setAfdelingen(new ArrayList<AfdelingHasWerknemerInfo>());
					tpAfdelingen.ReloadTable();
				} else {
					werknemer.setWerkgever(null);
					werknemer.setWerkgeverid(-1);
					dienstverband.setWerkgeverId(-1);
					dienstverband.setWerkgever(null);
				}
			} catch (PermissionException e1) {
				JOptionPane.showMessageDialog(thisform, e1.getMessage());
			} catch (VerzuimApplicationException e2) {
	        	ExceptionLogger.ProcessException(e2,this);
			} catch (Exception e1){
	        	ExceptionLogger.ProcessException(e1,this);
			}
		}
	}
	protected void cmbWerkgeverClicked(ActionEvent e) {
	}
	protected void btnNewDienstverbandClicked(ActionEvent e) {
		dienstverband = new DienstverbandInfo();
		dienstverband.setStartdatumcontract(null);
		dienstverband.setWerkweek(BigDecimal.ZERO);
		if (werknemer.getWerkgeverid() != null){
			dienstverband.setWerkgeverId(werknemer.getWerkgeverid());
			dienstverband.setWerkgever(werknemer.getWerkgever());
			dienstverband.setWerknemer(werknemer);
		}
		if (werknemer.getDienstVerbanden() == null)
			werknemer.setDienstVerbanden(new ArrayList<DienstverbandInfo>());
		werknemer.getDienstVerbanden().add(dienstverband);
		dienstverbandinx = werknemer.getDienstVerbanden().size()-1;
		displayDienstverband();
		btnNewDienstverband.setEnabled(false);
		enableNextPrevButtons();
	}
	protected void wiaPercentageAdded(WiapercentageInfo info) {
		werknemer.getWiaPercentages().add(info);
		for (Object o: werknemer.getWiaPercentages())
		{
			WiapercentageInfo dv = (WiapercentageInfo)o;
			if (dv.getEinddatum() == null)
				dv.setEinddatum(info.getStartdatum());
		}
	}
	protected void okButtonClicked(ActionEvent e) {
		TypeEntry geslacht, burgerlijkestaat;
		ParsePosition pos = new ParsePosition(0);
		DecimalFormat format = new DecimalFormat("##0,00");
		BigDecimal uren;
		format.setParseBigDecimal(true);

		dienstverband.setStartdatumcontract(dtpIngangsdatum.getDate());
		dienstverband.setEinddatumcontract(dtpEindatum.getDate());
		dienstverband.setFunctie(txtFunctie.getText().trim());
		dienstverband.setWerknemerId(werknemer.getId());
		uren = (BigDecimal)format.parse(txtWerkweekUren.getText(),pos);
		if (pos.getIndex() < txtWerkweekUren.getText().length())
		{
			JOptionPane.showMessageDialog(this, "Werkweek bevat ongeldige tekens");
			return;
		}
			
		dienstverband.setWerkweek(uren);
		dienstverband.setPersoneelsnummer(txtPersoneelsnummer.getText());
		try {
			dienstverband.validate();
			AfdelingHasWerknemerInfo laatsteAfd = werknemer.getLaatsteAfdeling(); 
			for (AfdelingHasWerknemerInfo afhwi:werknemer.getAfdelingen()){
				if (afhwi.getAction() != persistenceaction.DELETE){
					afhwi.validate();
				}
				if (afhwi.getStartdatum().before(laatsteAfd.getStartdatum())){
					if (afhwi.getEinddatum() == null){
						afhwi.setEinddatum(laatsteAfd.getStartdatum());
					}
				}
			}
		} catch (ValidationException e1) {
        	ExceptionLogger.ProcessException(e1,this,false);
        	return;
		}
		if (this.getMode() == formMode.New){
			werknemer.getWiaPercentages().get(0).setStartdatum(dienstverband.getStartdatumcontract());
		}
		if (teverwijderenafdelingen != null){
			for (AfdelingHasWerknemerInfo ahwi: teverwijderenafdelingen){
				if (werknemer.getAfdelingen().contains(ahwi)){
					/*Dit gebeurt als er iets misgaat tijdens het opslaan, dan hebben we dit al een keer gedaan*/;
				}else{
					werknemer.getAfdelingen().add(ahwi);
				}
			}
		}
		
		werknemer.setAdres(adresPanel.getAdres());

		werknemer.setAchternaam(txtAchternaam.getText().trim());
		werknemer.setBurgerservicenummer(txtBSN.getText().trim());
		werknemer.setEmail(txtEmail.getText().trim());
		werknemer.setMobiel(formatTelefoonr(txtTelefoonMobiel.getText().trim()));
		werknemer.setTelefoonPrive(formatTelefoonr(txtTelefoonprive.getText().trim()));
		werknemer.setTelefoon(formatTelefoonr(txtTelefoonthuis.getText().trim()));
		werknemer.setVoorletters(txtVoorletters.getText().trim());
		werknemer.setVoornaam(txtVoornaam.getText().trim());
		werknemer.setVoorvoegsel(txtVoorvoegsel.getText().trim());
		werknemer.setGeboortedatum(dtpGeboortedatum.getDate());
        geslacht = (TypeEntry) cmbGeslacht.getSelectedItem();
        werknemer.setGeslacht(__geslacht.parse(geslacht.getValue()));
        burgerlijkestaat = (TypeEntry) cmbBurgerlijkestaat.getSelectedItem();
        werknemer.setBurgerlijkestaat(__burgerlijkestaat.parse(burgerlijkestaat.getValue()));
        werknemer.setArbeidsgehandicapt(cbArbeidsgehandicapt.isSelected());
        werknemer.setOpmerkingen(taOpmerkingen.getText());
        if (this.getLoginSession() != null)
        {
        	try {
        		werknemer.validate();
	    		switch (this.getMode()){
    			case New: 
    				ServiceCaller.werknemerFacade(getLoginSession()).addWerknemer(werknemer);
    				break;
    			case Update: 
    				ServiceCaller.werknemerFacade(getLoginSession()).updateWerknemer(werknemer);
    				break;
    			case Delete: 
    				ServiceCaller.werknemerFacade(getLoginSession()).deleteWerknemer(werknemer);
    				break;
	    		}
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
	protected void btnVolgendDienstverbandClicked(ActionEvent e) {
		if (dienstverbandinx >= werknemer.getDienstVerbanden().size()-1)
			;
		else
		{
			dienstverbandinx++;
			dienstverband = werknemer.getDienstVerbanden().get(dienstverbandinx);
			displayDienstverband();
			tpVerzuimen.ReloadTable();
		}
		enableNextPrevButtons();
	}
	protected void btnVorigDienstverbandClicked(ActionEvent e) {
		if (dienstverbandinx <= 0)
			;
		else
		{
			dienstverbandinx--;
			dienstverband = werknemer.getDienstVerbanden().get(dienstverbandinx);
			displayDienstverband();
			tpVerzuimen.ReloadTable();
		}
		enableNextPrevButtons();
	}
	private void enableNextPrevButtons() {
		if (dienstverbandinx >= werknemer.getDienstVerbanden().size()-1)
			btnVolgendDienstverband.setEnabled(false);
		else
			btnVolgendDienstverband.setEnabled(true);
			
		if (dienstverbandinx <= 0)
			btnVorigDienstverband.setEnabled(false);
		else
			btnVorigDienstverband.setEnabled(true);
		
		dtpIngangsdatum.setEnabled((dienstverband.getEinddatumcontract() == null));
		txtFunctie.setEnabled((dienstverband.getEinddatumcontract() == null));
		txtWerkweekUren.setEnabled((dienstverband.getEinddatumcontract() == null));
		txtPersoneelsnummer.setEnabled((dienstverband.getEinddatumcontract() == null));
	}
	private void displayDienstverband() {
		NumberFormat nf = NumberFormat.getNumberInstance();
		DecimalFormat df = (DecimalFormat)nf;
		df.applyPattern("##0.00");

        try {
			dtpIngangsdatum.setDate(dienstverband.getStartdatumcontract());
			dtpEindatum.setDate(dienstverband.getEinddatumcontract());
			txtPersoneelsnummer.setText(dienstverband.getPersoneelsnummer());
			if (dienstverband.getWerkweek() == null)
				txtWerkweekUren.setText("");
			else
				txtWerkweekUren.setText(df.format(dienstverband.getWerkweek()));
			txtFunctie.setText(dienstverband.getFunctie());
		} catch (PropertyVetoException e1) {
			ExceptionLogger.ProcessException(e1,this);
		}       
	}
}
