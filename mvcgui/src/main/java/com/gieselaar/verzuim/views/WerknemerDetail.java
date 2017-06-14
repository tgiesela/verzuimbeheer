package com.gieselaar.verzuim.views;

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
import java.util.List;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.RowSorter;
import javax.swing.SortOrder;

import com.gieselaar.verzuim.components.AdresPanel;
import com.gieselaar.verzuim.components.JTextAreaTGI;
import com.gieselaar.verzuim.components.JTextFieldTGI;
import com.gieselaar.verzuim.controllers.AbstractController;
import com.gieselaar.verzuim.controllers.AbstractController.__formmode;
import com.gieselaar.verzuim.controllers.DienstverbandController;
import com.gieselaar.verzuim.controllers.DienstverbandController.__dienstverbandfields;
import com.gieselaar.verzuim.controllers.TodoController;
import com.gieselaar.verzuim.controllers.TodoController.__todocommands;
import com.gieselaar.verzuim.controllers.TodoController.__todofields;
import com.gieselaar.verzuim.interfaces.ControllerEventListener;
import com.gieselaar.verzuim.interfaces.DefaultControllerEventListener;
import com.gieselaar.verzuim.utils.ExceptionLogger;
import com.gieselaar.verzuim.viewsutils.ColorTableModel;
import com.gieselaar.verzuim.viewsutils.CursorController;
import com.gieselaar.verzuim.viewsutils.VerzuimComboBoxModel;
import com.gieselaar.verzuim.controllers.VerzuimController;
import com.gieselaar.verzuim.controllers.VerzuimController.__verzuimfields;
import com.gieselaar.verzuim.controllers.AfdelingHasWerknemerController.__werknemerafdelingfields;
import com.gieselaar.verzuim.controllers.WiapercentageController;
import com.gieselaar.verzuim.controllers.WiapercentageController.__wiapercentagefields;
import com.gieselaar.verzuim.controllers.AfdelingHasWerknemerController;
import com.gieselaar.verzuim.controllers.WerknemerController;
import com.gieselaar.verzuimbeheer.exceptions.VerzuimApplicationException;
import com.gieselaar.verzuimbeheer.exceptions.ValidationException;
import com.gieselaar.verzuimbeheer.services.AdresInfo;
import com.gieselaar.verzuimbeheer.services.AfdelingHasWerknemerInfo;
import com.gieselaar.verzuimbeheer.services.DienstverbandInfo;
import com.gieselaar.verzuimbeheer.services.InfoBase;
import com.gieselaar.verzuimbeheer.services.VerzuimInfo;
import com.gieselaar.verzuimbeheer.services.InfoBase.persistenceaction;
import com.gieselaar.verzuimbeheer.services.TodoFastInfo;
import com.gieselaar.verzuimbeheer.services.WerkgeverInfo;
import com.gieselaar.verzuimbeheer.services.WerknemerInfo;
import com.gieselaar.verzuimbeheer.services.InfoBase.__geslacht;
import com.gieselaar.verzuimbeheer.services.InfoBase.__burgerlijkestaat;
import com.gieselaar.verzuimbeheer.services.WiapercentageInfo;
import com.gieselaar.verzuimbeheer.utils.TypeEntry;
import com.michaelbaranov.microba.calendar.DatePicker;

import javax.swing.JComboBox;
import javax.swing.JCheckBox;
import javax.swing.JTabbedPane;
import java.awt.Color;
import java.awt.EventQueue;
import java.awt.KeyboardFocusManager;
import javax.swing.JButton;

import java.awt.event.ActionListener;

import javax.swing.JPanel;
import javax.swing.border.TitledBorder;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import javax.swing.event.ChangeListener;
import javax.swing.event.ChangeEvent;

public class WerknemerDetail extends AbstractDetail {


	private static final long serialVersionUID = -5378765279785908721L;
	
	private WerknemerInfo werknemer = null;
	private List<VerzuimInfo> verzuimen;
	private List<AfdelingHasWerknemerInfo> teverwijderenafdelingen;
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
	private DatatablePanel tpAfdelingen;
	private DatatablePanel tpWiaPercentages;
	private DatatablePanel tpVerzuimen;
	private DatatablePanel tpDienstverbanden;
	private DatatablePanel tpTodos;
	private JButton btnVorigDienstverband;
	private JButton btnVolgendDienstverband;
	private DatePicker dtpEindatum;
	private JTextFieldTGI txtWerkweekUren;
	private JTextFieldTGI txtFunctie;
	private JTextFieldTGI txtPersoneelsnummer;
	private DatePicker dtpIngangsdatum;
	private JButton btnMedischekaart;
	private JTextAreaTGI taOpmerkingen;
	private DienstverbandInfo huidigdienstverband = null;
	private VerzuimInfo selectedVerzuim = null;
	private String savedBSN = "";

	private WerkgeverInfo werkgever;
	private JComboBox<TypeEntry> cmbTemplates;
	private JButton btnGenereer;
	private WerknemerController werknemercontroller = null;
	private VerzuimController verzuimcontroller = null;
	private TodoController todocontroller = null;
	private AfdelingHasWerknemerController werknemerafdelingcontroller = null;
	private WiapercentageController wiapercentagecontroller = null;
	private DienstverbandController dienstverbandcontroller = null;
	private int selectedrowdienstverband = -1;

	private final String PANELNAMEAFDELINGEN = "Afdelingen"; 
	private final String PANELNAMETODOS = "TODO"; 
	private final String PANELNAMEVERZUIMEN = "Verzuimen"; 
	private final String PANELNAMEDIENSTVERBANDEN = "Dienstverbanden"; 
	private final String PANELNAMEWIAPERCENTAGES = "Wiapercentages"; 
	private boolean verzuimselected;
	/**
	 * Create the frame.
	 */
	public WerknemerDetail(AbstractController controller) {
		super("Werknemer", controller);
		werknemercontroller = (WerknemerController) super.controller;
		initialize();
	}
	@Override
	public void setData(InfoBase info) {
		werknemer = (WerknemerInfo) info;
		if (this.getFormmode() == __formmode.NEW){
			savedBSN = werknemer.getBurgerservicenummer();
			if (savedBSN == null){
				savedBSN = "";
			}
			txtBSN.setEnabled(false);
			if (werknemer.getDienstVerbanden() != null){
				huidigdienstverband = werknemer.getDienstVerbanden().get(0);
			}else{
				werknemer.setDienstVerbanden(new ArrayList<DienstverbandInfo>());
				btnNewDienstverbandClicked(null);
			}
			if (werknemer.getAfdelingen() == null){
				werknemer.setAfdelingen(new ArrayList<AfdelingHasWerknemerInfo>());
			}
		}
		displayWerknemerInfo();
	}
	private void displayWerknemerInfo() {
		AdresInfo woonadres;
		VerzuimComboBoxModel geslachtModel;
		VerzuimComboBoxModel burgerlijkestaatModel;
		VerzuimComboBoxModel templateModel;
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

		geslachtModel = controller.getMaincontroller().getEnumModel(__geslacht.class);
		cmbGeslacht.setModel(geslachtModel);

        burgerlijkestaatModel = controller.getMaincontroller().getEnumModel(__burgerlijkestaat.class);
        cmbBurgerlijkestaat.setModel(burgerlijkestaatModel);

        if (this.getFormmode() == __formmode.NEW) {
        	cmbGeslacht.setSelectedIndex(0);
        	cmbBurgerlijkestaat.setSelectedIndex(0);
        	cbArbeidsgehandicapt.setSelected(false);
        }
        else
        {
        	cbArbeidsgehandicapt.setSelected(werknemer.isArbeidsgehandicapt());
			((VerzuimComboBoxModel)cmbGeslacht.getModel()).setId(werknemer.getGeslacht().getValue());
			((VerzuimComboBoxModel)cmbBurgerlijkestaat.getModel()).setId(werknemer.getBurgerlijkestaat().getValue());
        }
       	setWerkgeverCombobox(werknemer.getWerkgeverid());
       	templateModel = new VerzuimComboBoxModel(werknemercontroller.getMaincontroller());
		controller.getMaincontroller().updateComboModelDocumentTemplates(templateModel);
		
    	dienstverbandcontroller.selectDienstverbanden();
	}
	private void setWerkgeverCombobox(Integer werkgeverid) {
		if (werknemer.getWerkgeverid() != null){
			((VerzuimComboBoxModel)cmbWerkgever.getModel()).setId(werkgeverid);
			txtBSN.setEnabled(true);
		}
	}
	void initialize() {
		setBounds(50, 50, 782, 628);

		addButtons();
		addWerknemerFields();
		addComboWerkgever();
		addComboTemplates();
		
		final JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		tabbedPane.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent evt) {
				DatatablePanel panel = (DatatablePanel) tabbedPane.getSelectedComponent();
				if (panel.getName() != null && !panel.getName().isEmpty()){
					if (panel.getName().equals(PANELNAMEAFDELINGEN)){
						displayAfdelingen();
						return;
					}
					if (panel.getName().equals(PANELNAMETODOS)){
						displayTodos();
						return;
					}
					if (panel.getName().equals(PANELNAMEWIAPERCENTAGES)){
						displayWiapercentages();
						return;
					}
					if (panel.getName().equals(PANELNAMEDIENSTVERBANDEN)){
						displayDienstverbanden();
						return;
					}
				}
			}
		});
		tabbedPane.setBackground(Color.LIGHT_GRAY);
		tabbedPane.setBounds(21, 349, 615, 190);
		getContentPane().add(tabbedPane);
		
		addTabVerzuimen(tabbedPane);
		addTabAfdelingen(tabbedPane);
		addTabTodos(tabbedPane);
		addTabWiaPercentages(tabbedPane);
		addTabDienstverbanden(tabbedPane);

		addPanelDienstverband();
		
		btnMedischekaart.setEnabled(false);
		btnGenereer.setEnabled(false);
		cmbTemplates.setEnabled(false);
		
	}
	private void addPanelDienstverband() {
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
	}
	protected void displayTodos() {
		if (verzuimselected){
			todocontroller.setVerzuim(selectedVerzuim);
			todocontroller.selectTodosVerzuim(selectedVerzuim.getId());
		}else{
			((ColorTableModel)tpTodos.getTable().getModel()).setRowCount(0);
		}
	}
	protected void displayAfdelingen() {
		werknemerafdelingcontroller.setWerknemer(werknemer);
		werknemerafdelingcontroller.getAfdelingen();
	}
	protected void displayDienstverbanden() {
		dienstverbandcontroller.setWerknemer(werknemer);
		dienstverbandcontroller.selectDienstverbanden();
	}
	protected void displayWiapercentages() {
		wiapercentagecontroller.setWerknemer(werknemer);
		wiapercentagecontroller.getWiapercentages();
	}
	private void addButtons(){
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
		
		btnGenereer = new JButton("Genereer...");
		btnGenereer.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				btnGenereerClicked();
			}
		});
		btnGenereer.setBounds(643, 541, 119, 23);
		getContentPane().add(btnGenereer);

		btnMedischekaart = new JButton("Medische kaart...");
		btnMedischekaart.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				btnMedischeKaartClicked();
			}
		});
		btnMedischekaart.setBounds(643, 491, 119, 23);
		getContentPane().add(btnMedischekaart);
		
		JButton btnWerkgever = new JButton("Details...");
		btnWerkgever.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				btnWerkgeverClicked(e);
			}
		});
		btnWerkgever.setBounds(377, 10, 86, 23);
		getContentPane().add(btnWerkgever);
		
		JButton btnVerzuimhistorie = new JButton("Verzuimhistorie...");
		btnVerzuimhistorie.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				btnVerzuimhistorieClicked(e);
			}
		});
		btnVerzuimhistorie.setBounds(643, 516, 119, 23);
		getContentPane().add(btnVerzuimhistorie);
		
	}
	private void addWerknemerFields() {
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
			public void focusGained(FocusEvent e) {/* noop */}
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
	
		cbArbeidsgehandicapt = new JCheckBox("Arbeidsgehandicapt");
		cbArbeidsgehandicapt.setBounds(367, 131, 130, 23);
		getContentPane().add(cbArbeidsgehandicapt);
	
	}
	private void addTabAfdelingen(JTabbedPane tabbedPane) {
		try{
			werknemerafdelingcontroller = werknemercontroller.getAfdelingController(werknemer);
		} catch (VerzuimApplicationException e){
			ExceptionLogger.ProcessException(e, this);
		}
		tpAfdelingen = new DatatablePanel(werknemerafdelingcontroller);
		tpAfdelingen.setName(PANELNAMEAFDELINGEN);
		tpAfdelingen.addColumn(__werknemerafdelingfields.NAAM.getValue(), "Naam", 100);
		tpAfdelingen.addColumn(__werknemerafdelingfields.INGANGSDATUM.getValue(),"Ingangsdatum", 70, Date.class);
		tpAfdelingen.addColumn(__werknemerafdelingfields.EINDDATUM.getValue(),"Einddatum",70, Date.class);
		DefaultControllerEventListener listener = new DefaultControllerEventListener(){
			@Override
			public void refreshTable() {
				/* Save and remove sort before populating the table */
				tpAfdelingen.disableSorter();

				List<AfdelingHasWerknemerInfo> afdelingen = werknemerafdelingcontroller.getAfdelingenList();
				werknemerafdelingcontroller.getTableModel(afdelingen, (ColorTableModel) tpAfdelingen.getTable().getModel(), tpAfdelingen.colsinview);

				/* Add sort again */
				tpAfdelingen.enableSorter();
			}
			@Override
			public void rowSelected(int rownr, Object info) {
			}
		};
		registerControllerListener(werknemerafdelingcontroller, listener);
		tabbedPane.addTab("Afdelingen", null, tpAfdelingen, null);
		
	}
	private void addTabWiaPercentages(JTabbedPane tabbedPane) {
		try{
			wiapercentagecontroller = werknemercontroller.getWiapercentageController(werknemer);
		} catch (VerzuimApplicationException e){
			ExceptionLogger.ProcessException(e, this);
		}
		tpWiaPercentages = new DatatablePanel(wiapercentagecontroller);
		tpWiaPercentages.setName(PANELNAMEWIAPERCENTAGES);
		tpWiaPercentages.addColumn(__wiapercentagefields.PERCENTAGE.getValue(),"Percentage", 100);
		tpWiaPercentages.addColumn(__wiapercentagefields.INGANGSDATUM.getValue(),"Ingangsdatum", 100, Date.class);
		tpWiaPercentages.addColumn(__wiapercentagefields.EINDDATUM.getValue(),"Einddatum",80,Date.class);
		DefaultControllerEventListener listener = new DefaultControllerEventListener(){
			@Override
			public void refreshTable() {
				/* Save and remove sort before populating the table */
				tpWiaPercentages.disableSorter();
				
				List<WiapercentageInfo> wiapercentages = wiapercentagecontroller.getWiapercentagesList();
				wiapercentagecontroller.getTableModel(wiapercentages, (ColorTableModel) tpWiaPercentages.getTable().getModel(), tpWiaPercentages.colsinview);

				/* Add sort again */
				tpWiaPercentages.enableSorter();
			}
			@Override
			public void rowSelected(int rownr, Object info) {
			}
		};
		registerControllerListener(wiapercentagecontroller, listener);
		tabbedPane.addTab("Wia percentages", null, tpWiaPercentages, null);
	}
	private void addTabDienstverbanden(JTabbedPane tabbedPane) {
		try{
			dienstverbandcontroller = werknemercontroller.getDienstverbandController(werknemer);
			dienstverbandcontroller.setVerzuimmodel(verzuimcontroller.getVerzuimmodel());
		} catch (VerzuimApplicationException e){
			ExceptionLogger.ProcessException(e, this);
		}
		tpDienstverbanden = new DatatablePanel(dienstverbandcontroller);
		tpDienstverbanden.addColumn(__dienstverbandfields.PERSONEELSNUMMER.getValue(),"Pers. Nr", 80);
		tpDienstverbanden.addColumn(__dienstverbandfields.INGANGSDATUM.getValue(),"Ingangsdatum", 80, Date.class);
		tpDienstverbanden.addColumn(__dienstverbandfields.EINDDATUM.getValue(),"Einddatum",80, Date.class);
		tpDienstverbanden.addColumn(__dienstverbandfields.WERKWEEK.getValue(),"Werkweek",80);
		tpDienstverbanden.addColumn(__dienstverbandfields.FUNCTIE.getValue(),"Functie",120);
		tpDienstverbanden.setName(PANELNAMEDIENSTVERBANDEN);
		DefaultControllerEventListener listener = new DefaultControllerEventListener(){
			@Override
			public void refreshTable() {
				/* Save and remove sort before populating the table */
				tpDienstverbanden.disableSorter();

				List<DienstverbandInfo> dienstverbanden = dienstverbandcontroller.getDienstverbandenList();
				dienstverbandcontroller.getTableModel(dienstverbanden, (ColorTableModel) tpDienstverbanden.getTable().getModel(), tpDienstverbanden.colsinview);

				/* Add sort again */
				tpDienstverbanden.enableSorter();
				selectLatestDienstverband();
			}
			@Override
			public void rowSelected(int rownr, Object info) {
				selectedrowdienstverband = rownr;
				huidigdienstverband = (DienstverbandInfo)info;
				verzuimcontroller.setDienstverband(huidigdienstverband);
				selectLatestDienstverband();
			}
		};
		registerControllerListener(dienstverbandcontroller, listener);
		tabbedPane.addTab("Dienstverbanden", null, tpDienstverbanden, null);
	}
	private void selectLatestDienstverband(){
		/* If no dienstverband selected, select the most recent one */
		/* If a dienstverband was already selected try to select the row */
		int rownr = selectedrowdienstverband;
		if (rownr < 0){
			/* no row selected */
			if (werknemer.getLaatsteDienstverband() != null){
				int dvbid = werknemer.getLaatsteDienstverband().getId();
				rownr = lookupDienstverbandInTable(dvbid);
			}else{
				rownr = 0;
			}
		}else{
			/* huidigdienstverband is set */
			rownr = lookupDienstverbandInTable(huidigdienstverband.getId());
			if (rownr < 0){
				rownr = 0;
			}
		}
    		
    	if (tpDienstverbanden.getTable().getModel().getRowCount() > 0){
        	tpDienstverbanden.getTable().changeSelection(rownr, 0, false, false);
			setDienstverbandSelected(true);
    	}
		displayDienstverband();
	}
	
	private void setDienstverbandSelected(boolean b) {
		verzuimcontroller.selectVerzuimen(huidigdienstverband.getId());
	}
	private void addTabVerzuimen(JTabbedPane tabbedPane) {
		try{
			verzuimcontroller = werknemercontroller.getVerzuimController();
		} catch (VerzuimApplicationException e){
			ExceptionLogger.ProcessException(e, this);
		}
		tpVerzuimen = new DatatablePanel(verzuimcontroller);
		tpVerzuimen.setName(PANELNAMEVERZUIMEN);
		tpVerzuimen.addColumn(__verzuimfields.CASCODE.getValue(), "CAScode",120 );
		tpVerzuimen.addColumn(__verzuimfields.TYPE.getValue(),"Type",60);
		tpVerzuimen.addColumn(__verzuimfields.AANVANG.getValue(),"Aanvang",60,Date.class);
		tpVerzuimen.addColumn(__verzuimfields.VANGNET.getValue(),"Vangnet",60);
		tpVerzuimen.addColumn(__verzuimfields.HERSTEL.getValue(),"Herstel",60,Date.class);
		tpVerzuimen.addColumn(__verzuimfields.HERSTELPERCENTAGE.getValue(),"%Herstel",60);
		tpVerzuimen.addColumn(__verzuimfields.KETENVZM.getValue(),"Ketenvzm",70);
		DefaultControllerEventListener listener = new DefaultControllerEventListener(){
			@Override
			public void refreshTable() {
				/* Save and remove sort before populating the table */
				tpVerzuimen.disableSorter();

				verzuimen = verzuimcontroller.getVerzuimen();
				verzuimcontroller.getTableModel(verzuimen, (ColorTableModel) tpVerzuimen.getTable().getModel(), tpVerzuimen.colsinview);

				/* Add sort again */
				tpVerzuimen.enableSorter();
				
				/* Select the first row */
		    	if (tpVerzuimen.getTable().getModel().getRowCount() > 0){
			    	tpVerzuimen.getTable().changeSelection(0, 0, false, false);
		    	}
			}
			@Override
			public void rowSelected(int rownr, Object info) {
				selectedVerzuim = (VerzuimInfo)info;
				setVerzuimSelected(true);
			}
			@Override
			public void formClosed(ControllerEventListener cev){
				displayWerknemerInfo();
			}
		};
		registerControllerListener(verzuimcontroller, listener);
        List<RowSorter.SortKey> sortKeys;
		sortKeys = new ArrayList<>();
        sortKeys.add(new RowSorter.SortKey(2, SortOrder.DESCENDING));
        tpVerzuimen.getSorter().setSortKeys(sortKeys);
        tpVerzuimen.getSorter().sort();
		
		tabbedPane.addTab("Verzuimen", null, tpVerzuimen, null);
	}
	private void addTabTodos(JTabbedPane tabbedPane){
		try{
			todocontroller = werknemercontroller.getTodoController();
		} catch (VerzuimApplicationException e){
			ExceptionLogger.ProcessException(e, this);
		}
		tpTodos = new DatatablePanel(todocontroller);
		tpTodos.setName(PANELNAMETODOS);
		tpTodos.addColumn(__todofields.INDICATOR.getValue(),"", 20);
		tpTodos.addColumn(__todofields.DEADLINE.getValue(),"Deadline", 80, Date.class);
		tpTodos.addColumn(__todofields.WAARSCHUWEN.getValue(),"Waarschuwen", 80, Date.class);
		tpTodos.addColumn(__todofields.ACTIVITEIT.getValue(),"Activiteit", 100);
		tpTodos.addColumn(__todofields.HERHALEN.getValue(),"Herhalen", 60);

		JCheckBox chckbxToekomstigeTonen = new JCheckBox("Toekomstige tonen");
		chckbxToekomstigeTonen.setSelected(false);
		chckbxToekomstigeTonen.setBounds(297, 422, 104, 23);
		chckbxToekomstigeTonen.setActionCommand(__todocommands.TODOTOEKOMSTIGETONEN.toString());
		chckbxToekomstigeTonen.addActionListener(CursorController.createListener(this,todocontroller));
		tpTodos.getPanelAction().add(chckbxToekomstigeTonen);
		
		JCheckBox chckbxAfgeslotenTonen = new JCheckBox("Afgesloten tonen");
		chckbxAfgeslotenTonen.setBounds(403, 422, 133, 23);
		chckbxAfgeslotenTonen.setActionCommand(__todocommands.TODOAFGESLOTENTONEN.toString());
		chckbxAfgeslotenTonen.addActionListener(CursorController.createListener(this,todocontroller));
		tpTodos.getPanelAction().add(chckbxAfgeslotenTonen);
		
		tabbedPane.addTab("TODO", null, tpTodos);
		
		DefaultControllerEventListener listener = new DefaultControllerEventListener(){
			@Override
			public void refreshTable() {
				/* Save and remove sort before populating the table */
				tpTodos.disableSorter();

				List<TodoFastInfo> todos = todocontroller.getTodoList();
				TodoFastInfo.sort(todos, TodoFastInfo.__sortcol.DATUMDEADLINE);
				todocontroller.getTableModel(todos, (ColorTableModel) tpTodos.getTable().getModel(), tpTodos.colsinview);

				/* Add sort again */
				tpTodos.enableSorter();
			}
		};
		registerControllerListener(todocontroller, listener);
        List<RowSorter.SortKey> sortKeys;
		sortKeys = new ArrayList<>();
        sortKeys.add(new RowSorter.SortKey(1, SortOrder.ASCENDING));
        tpTodos.getSorter().setSortKeys(sortKeys);
        tpTodos.getSorter().sort();
	}
	protected void setVerzuimSelected(boolean selected) {
		verzuimselected = selected;
		btnMedischekaart.setEnabled(selected);
		btnGenereer.setEnabled(selected);
		cmbTemplates.setEnabled(selected);
		verzuimcontroller.setSelectedverzuim(selectedVerzuim);
	}
	private void addComboTemplates() {
		cmbTemplates = new JComboBox<>();
		cmbTemplates.setModel(new VerzuimComboBoxModel(controller.getMaincontroller()));
		DefaultControllerEventListener listener = new DefaultControllerEventListener(){
			@Override
			public void refreshTable() {
				refreshComboTemplates();
			}
		};
		registerControllerListener(controller.getMaincontroller(), listener);
		cmbTemplates.setBounds(415, 541, 221, 20);
	
		getContentPane().add(cmbTemplates);
		refreshComboTemplates();
	}
	private void addComboWerkgever() {
		JLabel lblWerkgever = new JLabel("Werkgever");
		lblWerkgever.setLabelFor(cmbWerkgever);
		lblWerkgever.setBounds(33, 14, 65, 14);
		getContentPane().add(lblWerkgever);

		cmbWerkgever = new JComboBox<>();
		cmbWerkgever.setModel(new VerzuimComboBoxModel(controller.getMaincontroller()));
		DefaultControllerEventListener listener = new DefaultControllerEventListener(){
			@Override
			public void refreshTable() {
				refreshComboWerkgever();
			}
		};
		registerControllerListener(controller.getMaincontroller(), listener); 
		cmbWerkgever.addPopupMenuListener(new PopupMenuListener() {
			
			@Override
			public void popupMenuWillBecomeVisible(PopupMenuEvent e) {/* not used */}
			
			@Override
			public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
				EventQueue.invokeLater(new Runnable() {
					@Override
					public void run() {
						werkgeverSelected();
					}
				});
			}
			
			@Override
			public void popupMenuCanceled(PopupMenuEvent e) {/* not used*/}
		});
		cmbWerkgever.setBounds(130, 11, 239, 20);
		getContentPane().add(cmbWerkgever);

		refreshComboWerkgever();
	}
	private void refreshComboWerkgever(){
		werknemercontroller.getMaincontroller().updateComboModelWerkgevers((VerzuimComboBoxModel) cmbWerkgever.getModel(),true);
	}
	private void refreshComboTemplates(){
		werknemercontroller.getMaincontroller().updateComboModelDocumentTemplates((VerzuimComboBoxModel) cmbTemplates.getModel());
	}
	protected DienstverbandInfo getVorigDienstverband(DienstverbandInfo dvb) {
		return null;
	}
	protected void bsnFieldExit() {
		try{
			if (this.getFormmode() == __formmode.NEW ){
				if (savedBSN.equals(txtBSN.getText().trim())){
					/* nothing changed */
				} else {
					werknemercontroller.validateBurgerservicenummer(this, txtBSN.getText().trim(),werkgever.getId());
				}
			}else{
				if (savedBSN.equals(txtBSN.getText().trim())){
					/* nothing changed */
				} else {
					WerknemerInfo.validateBSN(txtBSN.getText());
				}
			}
		}catch(VerzuimApplicationException | ValidationException e){
        	ExceptionLogger.ProcessException(e,this);
		}
		
	}
	protected void btnGenereerClicked() {
		Integer documentid = ((VerzuimComboBoxModel)cmbTemplates.getModel()).getId();
		selectedVerzuim.setWerknemer(this.werknemer);
		controller.genereerDocument(selectedVerzuim, documentid);
	}
	protected void btnWerkgeverClicked(ActionEvent e) {
		Integer werkgeverid = ((VerzuimComboBoxModel)cmbWerkgever.getModel()).getId();
		werknemercontroller.openwerkgever(werkgeverid);
	}
	protected void btnVerzuimhistorieClicked(ActionEvent e) {
		werknemercontroller.showVerzuimhistorie(werknemer.getWerkgeverid(), werknemer.getId());;
	}
	protected void btnMedischeKaartClicked() {
		verzuimcontroller.openMedischekaart(this,__formmode.UPDATE);
	}
	
	private void werkgeverSelected(){
		Integer selected;
		selected = ((VerzuimComboBoxModel)cmbWerkgever.getModel()).getId();
		try {
			if (selected != null && selected != -1){
				/* row selected */
				if (werknemer.getWerkgeverid() != null && 
						!werknemer.getWerkgeverid().equals(-1) && 
						!selected.equals(werknemer.getWerkgeverid())){
					/* current Werkgever is set and is different from the new selected one */
					int result = JOptionPane.showConfirmDialog(this, "Werkgever wordt gewijzigd. \n Het dienstverband en de verzuimen verhuizen mee." + 
																 "\n De afdelingen worden verwijderd. Weet u het zeker?", "Werkgever wijzigen", JOptionPane.YES_NO_OPTION);
					if (result == JOptionPane.NO_OPTION){
						// Zet de oorspronkelijke werkgever weer terug.
						setWerkgeverCombobox(werknemer.getWerkgeverid());
						return;
					}
				}
				txtBSN.setEnabled(true);
				werkgever = werknemercontroller.getWerkgeverDetails(selected);
				huidigdienstverband.setWerkgeverId(werkgever.getId());
				huidigdienstverband.setWerkgever(werkgever);
				huidigdienstverband.setWerknemer(werknemer);
				werknemer.setWerkgever(werkgever);
				werknemer.setWerkgeverid(werkgever.getId());
				for (DienstverbandInfo dvb:werknemer.getDienstVerbanden()){
					dvb.setWerkgever(werkgever);
					dvb.setWerkgeverId(werkgever.getId());
				}
				teverwijderenafdelingen = new ArrayList<>();
				for (AfdelingHasWerknemerInfo ahwi: werknemer.getAfdelingen()){
					ahwi.setAction(persistenceaction.DELETE);
					teverwijderenafdelingen.add(ahwi);
				}
				werknemer.setAfdelingen(new ArrayList<AfdelingHasWerknemerInfo>());
				//tpAfdelingen.ReloadTable();
			} else {
				werknemer.setWerkgever(null);
				werknemer.setWerkgeverid(-1);
				huidigdienstverband.setWerkgeverId(-1);
				huidigdienstverband.setWerkgever(null);
			}
		} catch (Exception e1){
        	ExceptionLogger.ProcessException(e1,this);
		}
	}
	protected void btnNewDienstverbandClicked(ActionEvent e) {
		huidigdienstverband = new DienstverbandInfo();
		huidigdienstverband.setStartdatumcontract(null);
		huidigdienstverband.setWerkweek(BigDecimal.ZERO);
		if (werknemer.getWerkgeverid() != null){
			huidigdienstverband.setWerkgeverId(werknemer.getWerkgeverid());
			huidigdienstverband.setWerkgever(werknemer.getWerkgever());
			huidigdienstverband.setWerknemer(werknemer);
		}
		werknemer.getDienstVerbanden().add(huidigdienstverband);
		displayDienstverband();
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
	/*
	protected void okButtonClicked() {
    	try {
	        switch (this.getFormmode()){
	        case NEW:
				werknemercontroller.addData(werknemer);
	        	break;
	        case UPDATE: 
	        	werknemercontroller.updateData(werknemer);
	        	break;
	        }
	        werknemercontroller.closeView(this);
		} catch (VerzuimApplicationException e1) {
			ExceptionLogger.ProcessException(e1, thisform);
		}
        
	}
	*/
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
		selectedrowdienstverband++;
		selectLatestDienstverband();
//		displayDienstverband();
//		enableNextPrevButtons();
	}
	private int lookupDienstverbandInTable(Integer id) {
		int rownr = -1;
		ColorTableModel model = (ColorTableModel) tpDienstverbanden.getTable().getModel();
		for (int i=0;i<model.getRowCount();i++){
			DienstverbandInfo dvb = (DienstverbandInfo) model.getRowData(i);
			if (dvb.getId().equals(id)){
				rownr = i;
				break;
			}
		}
		return rownr;
	}
	protected void btnVorigDienstverbandClicked(ActionEvent e) {
		selectedrowdienstverband--;
		selectLatestDienstverband();
	}
	private void enableNextPrevButtons() {
		if (dienstverbandcontroller.isLastRow())
			btnVolgendDienstverband.setEnabled(false);
		else
			btnVolgendDienstverband.setEnabled(true);
			
		if (dienstverbandcontroller.isFirstRow())
			btnVorigDienstverband.setEnabled(false);
		else
			btnVorigDienstverband.setEnabled(true);
		
		if (huidigdienstverband != null){
			dtpIngangsdatum.setEnabled((huidigdienstverband.getEinddatumcontract() == null));
			txtFunctie.setEnabled((huidigdienstverband.getEinddatumcontract() == null));
			txtWerkweekUren.setEnabled((huidigdienstverband.getEinddatumcontract() == null));
			txtPersoneelsnummer.setEnabled((huidigdienstverband.getEinddatumcontract() == null));
		}
	}
	private void displayDienstverband() {
		NumberFormat nf = NumberFormat.getNumberInstance();
		DecimalFormat df = (DecimalFormat)nf;
		df.applyPattern("##0.00");

		enableNextPrevButtons();

        try {
        	if (huidigdienstverband != null){
				dtpIngangsdatum.setDate(huidigdienstverband.getStartdatumcontract());
				dtpEindatum.setDate(huidigdienstverband.getEinddatumcontract());
				txtPersoneelsnummer.setText(huidigdienstverband.getPersoneelsnummer());
				if (huidigdienstverband.getWerkweek() == null)
					txtWerkweekUren.setText("");
				else
					txtWerkweekUren.setText(df.format(huidigdienstverband.getWerkweek()));
				txtFunctie.setText(huidigdienstverband.getFunctie());
        	}
		} catch (PropertyVetoException e1) {
			ExceptionLogger.ProcessException(e1,this);
		}       
	}
	@Override
	public void setFormmode(__formmode formmode) {
		super.setFormmode(formmode);
		werknemer = new WerknemerInfo();
	}
	@Override
	public InfoBase collectData() {
		TypeEntry geslacht;
		TypeEntry burgerlijkestaat;
		ParsePosition pos = new ParsePosition(0);
		DecimalFormat format = new DecimalFormat("##0,00");
		BigDecimal uren;
		format.setParseBigDecimal(true);

		huidigdienstverband.setStartdatumcontract(dtpIngangsdatum.getDate());
		huidigdienstverband.setEinddatumcontract(dtpEindatum.getDate());
		huidigdienstverband.setFunctie(txtFunctie.getText().trim());
		huidigdienstverband.setWerknemerId(werknemer.getId());
		uren = (BigDecimal)format.parse(txtWerkweekUren.getText(),pos);
		if (pos.getIndex() < txtWerkweekUren.getText().length())
		{
			JOptionPane.showMessageDialog(this, "Werkweek bevat ongeldige tekens");
			return null;
		}
			
		huidigdienstverband.setWerkweek(uren);
		huidigdienstverband.setPersoneelsnummer(txtPersoneelsnummer.getText());
		try {
			huidigdienstverband.validate();
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
        	return null;
		}
		if (this.getFormmode() == __formmode.NEW){
			werknemer.getWiaPercentages().get(0).setStartdatum(huidigdienstverband.getStartdatumcontract());
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
        return werknemer;
	}
}
