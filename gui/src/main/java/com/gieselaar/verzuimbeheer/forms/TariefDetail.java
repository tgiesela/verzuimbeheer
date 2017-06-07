package com.gieselaar.verzuimbeheer.forms;

import javax.swing.JOptionPane;

import com.gieselaar.verzuimbeheer.baseforms.BaseDetailform;
import com.gieselaar.verzuimbeheer.baseforms.JDesktopPaneTGI;
import com.gieselaar.verzuimbeheer.components.JTextFieldTGI;
import com.gieselaar.verzuimbeheer.baseforms.WerkgeverNotification;
import com.gieselaar.verzuimbeheer.exceptions.VerzuimApplicationException;
import com.gieselaar.verzuimbeheer.exceptions.PermissionException;
import com.gieselaar.verzuimbeheer.exceptions.ValidationException;
import com.gieselaar.verzuimbeheer.services.HoldingInfo;
import com.gieselaar.verzuimbeheer.services.HoldingInfo.__factuurtype;
import com.gieselaar.verzuimbeheer.services.InfoBase;
import com.gieselaar.verzuimbeheer.services.TariefInfo;
import com.gieselaar.verzuimbeheer.services.TariefInfo.__tariefperiode;
import com.gieselaar.verzuimbeheer.services.WerkgeverInfo;
import com.gieselaar.verzuimbeheer.utils.DateOnly;
import com.gieselaar.verzuimbeheer.utils.ExceptionLogger;
import com.gieselaar.verzuimbeheer.utils.ServiceCaller;
import com.gieselaar.verzuimbeheer.utils.ServiceLocatorException;
import com.michaelbaranov.microba.calendar.DatePicker;

import javax.swing.ButtonGroup;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JCheckBox;
import java.awt.Component;
import java.beans.PropertyVetoException;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

import javax.swing.JPanel;
import javax.swing.border.TitledBorder;
import javax.swing.UIManager;
import javax.swing.JRadioButton;
import javax.swing.SwingConstants;

import com.gieselaar.verzuimbeheer.components.WerkgeverSelection;
import com.gieselaar.verzuimbeheer.components.WerkgeverSelection.__WerkgeverSelectionMode;
import com.gieselaar.verzuimbeheer.components.WerkgeverSelection.__WerkgeverFilter;

public class TariefDetail extends BaseDetailform {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private TariefInfo tarief;
	private WerkgeverInfo werkgever;
	
	private WerkgeverSelection werkgeverSelection;
	private DatePicker dtpIngangsdatum ;
	private DatePicker dtpEinddatum;
	private DatePicker dtpPeildatum;
	private JCheckBox cbVastetarieven;
	private List<TariefInfo> tarieven;
	private List<WerkgeverInfo> werkgevers;
	private List<HoldingInfo> holdings;
	private JPanel panelVariabeltarief;
	private JPanel panelVasttarief;
	private JFormattedTextField txtUurtariefNormaal;
	private JFormattedTextField txtUurtariefWeekend;
	private JFormattedTextField txtKmTarief;
	private JFormattedTextField txtSecretariaatskosten;
	private JFormattedTextField txtCasemanagement;
	private JFormattedTextField txtVastHuisbezoekStandaard;
	private JFormattedTextField txtVastHuisbezoekZelfdedag;
	private JFormattedTextField txtVastZaterdag;
	private JFormattedTextField txtVastHuisbezoekSpoed;
	private JFormattedTextField txtVastSpoedAgesprokenTijd;
	private JFormattedTextField txtVastTelefonischeControle;
	private JFormattedTextField txtMaandkostenSecretariaat;
	private JFormattedTextField txtAbonnementskosten;
	private JFormattedTextField txtAansluitkosten;
	private JTextFieldTGI txtOmschrijvingFactuur; 
	private JRadioButton rbAansluitkostenPeriodeJaar;
	private JRadioButton rbAansluitkostenPeriodeMaand;
	private JRadioButton rbAbonnementskostenPeriodeJaar;
	private JRadioButton rbAbonnementskostenPeriodeMaand;

	private boolean initialized;
	private Component thisform = this;
	/**
	 * Create the frame.
	 */
	public TariefDetail(JDesktopPaneTGI mdiPanel) {
		super("Beheer tarief", mdiPanel);
		initialize();
	}
	private void initialize() {
		setBounds(0, 237, 527, 582);
		getContentPane().setLayout(null);
		
		NumberFormat amountFormat = NumberFormat.getNumberInstance();
		amountFormat.setMaximumFractionDigits(2);
		amountFormat.setMinimumFractionDigits(2);

		dtpIngangsdatum = new DatePicker();
		dtpIngangsdatum.setBounds(134, 74, 97, 21);
		dtpIngangsdatum.setDateFormat(new SimpleDateFormat("dd-MM-yyyy"));
		getContentPane().add(dtpIngangsdatum);
		
		dtpEinddatum = new DatePicker();
		dtpEinddatum.setBounds(134, 97, 97, 21);
		dtpEinddatum.setDateFormat(new SimpleDateFormat("dd-MM-yyyy"));
		getContentPane().add(dtpEinddatum);
		
		JLabel lblIngangsdatum = new JLabel("Ingangsdatum");
		lblIngangsdatum.setBounds(20, 77, 86, 14);
		getContentPane().add(lblIngangsdatum);
		
		JLabel lblEinddatum = new JLabel("Einddatum");
		lblEinddatum.setBounds(20, 100, 108, 14);
		getContentPane().add(lblEinddatum);
		
		panelVariabeltarief = new JPanel();
		panelVariabeltarief.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "Variabel tarief (uur/km)", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		panelVariabeltarief.setBounds(10, 152, 240, 73);
		getContentPane().add(panelVariabeltarief);
		panelVariabeltarief.setLayout(null);
		
		JLabel lblNewLabel = new JLabel("Standaard");
		lblNewLabel.setBounds(21, 24, 95, 14);
		panelVariabeltarief.add(lblNewLabel);
		
		txtUurtariefNormaal = new JFormattedTextField(amountFormat);
		txtUurtariefNormaal.setColumns(10);
		txtUurtariefNormaal.setBounds(123, 21, 76, 20);
		txtUurtariefNormaal.setValue(new BigDecimal(100));
		txtUurtariefNormaal.setHorizontalAlignment(SwingConstants.RIGHT);
		panelVariabeltarief.add(txtUurtariefNormaal);
		
		JLabel lblUurtariefWeekend = new JLabel("Weekend");
		lblUurtariefWeekend.setBounds(21, 46, 108, 14);
		panelVariabeltarief.add(lblUurtariefWeekend);
		
		txtUurtariefWeekend = new JFormattedTextField(amountFormat);
		txtUurtariefWeekend.setColumns(10);
		txtUurtariefWeekend.setBounds(123, 43, 76, 20);
		txtUurtariefWeekend.setHorizontalAlignment(SwingConstants.RIGHT);
		panelVariabeltarief.add(txtUurtariefWeekend);
		
		panelVasttarief = new JPanel();
		panelVasttarief.setLayout(null);
		panelVasttarief.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "Vast tarief", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		panelVasttarief.setBounds(249, 152, 240, 189);
		getContentPane().add(panelVasttarief);
		
		JLabel label = new JLabel("Standaard");
		label.setBounds(21, 25, 95, 14);
		panelVasttarief.add(label);
		
		txtVastHuisbezoekStandaard = new JFormattedTextField(amountFormat);
		txtVastHuisbezoekStandaard.setColumns(10);
		txtVastHuisbezoekStandaard.setBounds(134, 21, 76, 20);
		txtVastHuisbezoekStandaard.setHorizontalAlignment(SwingConstants.RIGHT);
		panelVasttarief.add(txtVastHuisbezoekStandaard);
		
		JLabel lblZelfdeDag = new JLabel("Zelfde dag");
		lblZelfdeDag.setBounds(21, 47, 108, 14);
		panelVasttarief.add(lblZelfdeDag);
		
		txtVastHuisbezoekZelfdedag = new JFormattedTextField(amountFormat);
		txtVastHuisbezoekZelfdedag.setColumns(10);
		txtVastHuisbezoekZelfdedag.setBounds(134, 43, 76, 20);
		txtVastHuisbezoekZelfdedag.setHorizontalAlignment(SwingConstants.RIGHT);
		panelVasttarief.add(txtVastHuisbezoekZelfdedag);
		
		JLabel lblZaterdag = new JLabel("Zaterdag");
		lblZaterdag.setBounds(21, 70, 108, 14);
		panelVasttarief.add(lblZaterdag);
		
		txtVastZaterdag = new JFormattedTextField(amountFormat);
		txtVastZaterdag.setColumns(10);
		txtVastZaterdag.setBounds(134, 66, 76, 20);
		txtVastZaterdag.setHorizontalAlignment(SwingConstants.RIGHT);
		panelVasttarief.add(txtVastZaterdag);
		
		JLabel lblSpoed = new JLabel("Spoed");
		lblSpoed.setBounds(21, 93, 108, 14);
		panelVasttarief.add(lblSpoed);
		
		txtVastHuisbezoekSpoed = new JFormattedTextField(amountFormat);
		txtVastHuisbezoekSpoed.setColumns(10);
		txtVastHuisbezoekSpoed.setBounds(134, 89, 76, 20);
		txtVastHuisbezoekSpoed.setHorizontalAlignment(SwingConstants.RIGHT);
		panelVasttarief.add(txtVastHuisbezoekSpoed);
		
		JLabel lblSpoedAfgesprokenTijd = new JLabel("Afgesproken tijd");
		lblSpoedAfgesprokenTijd.setBounds(21, 115, 108, 14);
		panelVasttarief.add(lblSpoedAfgesprokenTijd);
		
		txtVastSpoedAgesprokenTijd = new JFormattedTextField(amountFormat);
		txtVastSpoedAgesprokenTijd.setColumns(10);
		txtVastSpoedAgesprokenTijd.setBounds(134, 112, 76, 20);
		txtVastSpoedAgesprokenTijd.setHorizontalAlignment(SwingConstants.RIGHT);
		panelVasttarief.add(txtVastSpoedAgesprokenTijd);
		
		JLabel lblTelefonischeControle = new JLabel("Telefonische controle");
		lblTelefonischeControle.setBounds(21, 138, 108, 14);
		panelVasttarief.add(lblTelefonischeControle);
		
		txtVastTelefonischeControle = new JFormattedTextField(amountFormat);
		txtVastTelefonischeControle.setColumns(10);
		txtVastTelefonischeControle.setBounds(134, 135, 76, 20);
		txtVastTelefonischeControle.setHorizontalAlignment(SwingConstants.RIGHT);
		panelVasttarief.add(txtVastTelefonischeControle);
		
		JLabel lblSecrKostenPm = new JLabel("Secr. kosten/maand");
		lblSecrKostenPm.setBounds(21, 161, 108, 14);
		panelVasttarief.add(lblSecrKostenPm);
		
		txtMaandkostenSecretariaat = new JFormattedTextField(amountFormat);
		txtMaandkostenSecretariaat.setColumns(10);
		txtMaandkostenSecretariaat.setBounds(134, 158, 76, 20);
		txtMaandkostenSecretariaat.setHorizontalAlignment(SwingConstants.RIGHT);
		panelVasttarief.add(txtMaandkostenSecretariaat);
		
		JPanel panelPeriodeAbonnementJaar = new JPanel();
		panelPeriodeAbonnementJaar.setLayout(null);
		panelPeriodeAbonnementJaar.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "Abonnementskosten", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		panelPeriodeAbonnementJaar.setBounds(10, 352, 479, 57);
		getContentPane().add(panelPeriodeAbonnementJaar);
		
		JLabel lblBedrag = new JLabel("Bedrag");
		lblBedrag.setBounds(21, 24, 95, 14);
		panelPeriodeAbonnementJaar.add(lblBedrag);
		
		txtAbonnementskosten = new JFormattedTextField(amountFormat);
		txtAbonnementskosten.setColumns(10);
		txtAbonnementskosten.setBounds(134, 21, 76, 20);
		txtAbonnementskosten.setHorizontalAlignment(SwingConstants.RIGHT);
		panelPeriodeAbonnementJaar.add(txtAbonnementskosten);
		
		JLabel lblPer = new JLabel("per");
		lblPer.setBounds(220, 24, 28, 14);
		panelPeriodeAbonnementJaar.add(lblPer);
		
		rbAbonnementskostenPeriodeJaar = new JRadioButton("Jaar");
		rbAbonnementskostenPeriodeJaar.setBounds(254, 20, 56, 23);
		panelPeriodeAbonnementJaar.add(rbAbonnementskostenPeriodeJaar);
		
		rbAbonnementskostenPeriodeMaand = new JRadioButton("Maand");
		rbAbonnementskostenPeriodeMaand.setBounds(316, 20, 76, 23);
		panelPeriodeAbonnementJaar.add(rbAbonnementskostenPeriodeMaand);
		
		ButtonGroup abonnementPeriode = new ButtonGroup();
		abonnementPeriode.add(rbAbonnementskostenPeriodeJaar);
		abonnementPeriode.add(rbAbonnementskostenPeriodeMaand);
		
		JPanel panel = new JPanel();
		panel.setLayout(null);
		panel.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "Aansluitkosten", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		panel.setBounds(10, 409, 479, 57);
		getContentPane().add(panel);
		
		JLabel label_1 = new JLabel("Bedrag");
		label_1.setBounds(21, 24, 95, 14);
		panel.add(label_1);
		
		txtAansluitkosten = new JFormattedTextField(amountFormat);
		txtAansluitkosten.setColumns(10);
		txtAansluitkosten.setBounds(134, 21, 76, 20);
		txtAansluitkosten.setHorizontalAlignment(SwingConstants.RIGHT);
		panel.add(txtAansluitkosten);
		
		JLabel label_2 = new JLabel("per");
		label_2.setBounds(220, 24, 28, 14);
		panel.add(label_2);
		
		rbAansluitkostenPeriodeJaar = new JRadioButton("Jaar");
		rbAansluitkostenPeriodeJaar.setBounds(254, 20, 56, 23);
		panel.add(rbAansluitkostenPeriodeJaar);

		rbAansluitkostenPeriodeMaand = new JRadioButton("Maand");
		rbAansluitkostenPeriodeMaand.setBounds(316, 20, 76, 23);
		panel.add(rbAansluitkostenPeriodeMaand);
		
		ButtonGroup aansluitkostenPeriode = new ButtonGroup();
		aansluitkostenPeriode.add(rbAansluitkostenPeriodeJaar);
		aansluitkostenPeriode.add(rbAansluitkostenPeriodeMaand);

		JLabel lblPeildatum = new JLabel("Peildatum");
		lblPeildatum.setBounds(20, 469, 108, 14);
		getContentPane().add(lblPeildatum);
		
		dtpPeildatum = new DatePicker();
		dtpPeildatum.setBounds(134, 466, 97, 21);
		dtpPeildatum.setDateFormat(new SimpleDateFormat("dd-MM-yyyy"));
		getContentPane().add(dtpPeildatum);
		
		cbVastetarieven = new JCheckBox("Vaste tarieven");
		cbVastetarieven.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				cbVastetarievenchecked(e);
			}
		});
		cbVastetarieven.setBounds(10, 124, 108, 21);
		getContentPane().add(cbVastetarieven);
		
		JLabel label_3 = new JLabel("Omschrijving factuur");
		label_3.setBounds(20, 494, 108, 14);
		getContentPane().add(label_3);
		
		txtOmschrijvingFactuur = new JTextFieldTGI();
		txtOmschrijvingFactuur.setColumns(10);
		txtOmschrijvingFactuur.setBounds(134, 493, 355, 20);
		getContentPane().add(txtOmschrijvingFactuur);
		
		
		werkgeverSelection = new WerkgeverSelection(__WerkgeverSelectionMode.SelectOne, __WerkgeverFilter.Actief);
		werkgeverSelection.setBounds(10, 11, 378, 63);
		getContentPane().add(werkgeverSelection);
		werkgeverSelection.setEventNotifier(new WerkgeverNotification() {
			
			@Override
			public boolean werkgeverSelected(Integer werkgeverid) {
				boolean dontchange = WerkgeverSelected(werkgeverid);
				if (!dontchange){
					tarief.setWerkgeverId(werkgeverid);
					tarief.setHoldingId(null);
				}
				return dontchange;
			}
			
			@Override
			public boolean holdingSelected(Integer holdingid) {
				boolean dontchange = HoldingSelected(holdingid);
				if (!dontchange){
					tarief.setHoldingId(holdingid);
					tarief.setWerkgeverId(null);
				}
				return dontchange;
			}
		});
		
		JLabel lblKmtarief = new JLabel("Km tarief");
		lblKmtarief.setBounds(20, 234, 108, 14);
		getContentPane().add(lblKmtarief);
		
		txtKmTarief = new JFormattedTextField(amountFormat);
		txtKmTarief.setBounds(133, 231, 76, 20);
		getContentPane().add(txtKmTarief);
		txtKmTarief.setColumns(10);
		txtKmTarief.setHorizontalAlignment(SwingConstants.RIGHT);
		
		JLabel lblSecretariaatskosten = new JLabel("Secretariaat");
		lblSecretariaatskosten.setBounds(20, 257, 108, 14);
		getContentPane().add(lblSecretariaatskosten);
		
		txtSecretariaatskosten = new JFormattedTextField(amountFormat);
		txtSecretariaatskosten.setBounds(133, 254, 76, 20);
		getContentPane().add(txtSecretariaatskosten);
		txtSecretariaatskosten.setColumns(10);
		txtSecretariaatskosten.setHorizontalAlignment(SwingConstants.RIGHT);
		
		JLabel lblCasemanagement = new JLabel("Casemanagement");
		lblCasemanagement.setBounds(20, 280, 108, 14);
		getContentPane().add(lblCasemanagement);
		
		txtCasemanagement = new JFormattedTextField(amountFormat);
		txtCasemanagement.setBounds(133, 277, 76, 20);
		getContentPane().add(txtCasemanagement);
		txtCasemanagement.setColumns(10);
		txtCasemanagement.setHorizontalAlignment(SwingConstants.RIGHT);
		
	}
	protected boolean HoldingSelected(Integer holdingid){
		if (tarief.getHoldingId() != null && tarief.getHoldingId() != -1){
			if (initialized){
				int result = JOptionPane.showConfirmDialog(this, "Holding wordt gewijzigd. \n Tarieven worden geinitialiseerd." + 
															 "\n Weet u het zeker?", "Holding wijzigen", JOptionPane.YES_NO_OPTION);
				if (result == JOptionPane.NO_OPTION){
					return true;
				}
			}
			try {
				tarief.setHoldingId(holdingid);
				tarief.setWerkgeverId(null);
				tarieven  = ServiceCaller.werkgeverFacade(getLoginSession()).getTarievenByHolding(holdingid);
				initializetarief();
			} catch (PermissionException e1) {
				JOptionPane.showMessageDialog(thisform, e1.getMessage());
			} catch (VerzuimApplicationException e2) {
	        	ExceptionLogger.ProcessException(e2,this);
			} catch (Exception e1){
	        	ExceptionLogger.ProcessException(e1,this);
			}
		}
		return false;
	}
	private boolean WerkgeverSelected(Integer werkgeverid){
		if (tarief.getWerkgeverId() != null && tarief.getWerkgeverId() != -1){
			if (initialized){
				int result = JOptionPane.showConfirmDialog(this, "Werkgever wordt gewijzigd. \n Tarieven worden geinitialiseerd." + 
							"\n Weet u het zeker?", "Werkgever wijzigen", JOptionPane.YES_NO_OPTION);
				if (result == JOptionPane.NO_OPTION){
					return true;
				}
			}
			try{
				werkgever = ServiceCaller.werkgeverFacade(getLoginSession()).getWerkgever(werkgeverid);
				tarief.setWerkgeverId(werkgeverid);
				tarief.setHoldingId(null);
				tarieven  = ServiceCaller.werkgeverFacade(getLoginSession()).getTarievenByWerkgever(werkgever.getId());
				initializetarief();
			} catch (PermissionException e1) {
				JOptionPane.showMessageDialog(thisform, e1.getMessage());
			} catch (VerzuimApplicationException e2) {
	        	ExceptionLogger.ProcessException(e2,this);
			} catch (Exception e1){
	        	ExceptionLogger.ProcessException(e1,this);
			}
		}
		return false;
	}
	private void initializetarief() {
		if (tarieven != null){
			for (TariefInfo t:tarieven){
				if (t.getEinddatum() == null){
					tarief.setAansluitkosten(t.getAansluitkosten());
					tarief.setAansluitkostenPeriode(t.getAansluitkostenPeriode());
					tarief.setAbonnement(t.getAbonnement());
					tarief.setAbonnementPeriode(t.getAbonnementPeriode());
					tarief.setDatumAansluitkosten(t.getDatumAansluitkosten());
					tarief.setHuisbezoekTarief(t.getHuisbezoekTarief());
					tarief.setHuisbezoekZaterdagTarief(t.getHuisbezoekZaterdagTarief());
					tarief.setKmTarief(t.getKmTarief());
					tarief.setMaandbedragSecretariaat(t.getMaandbedragSecretariaat());
					tarief.setOmschrijvingFactuur(t.getOmschrijvingFactuur());
					tarief.setSecretariaatskosten(t.getSecretariaatskosten());
					tarief.setSociaalbezoekTarief(t.getSociaalbezoekTarief());
					tarief.setSpoedbezoekTarief(t.getSpoedbezoekTarief());
					tarief.setSpoedbezoekZelfdedagTarief(t.getSpoedbezoekZelfdedagTarief());
					tarief.setStandaardHuisbezoekTarief(t.getStandaardHuisbezoekTarief());
					tarief.setTelefonischeControleTarief(t.getTelefonischeControleTarief());
					tarief.setUurtariefNormaal(t.getUurtariefNormaal());
					tarief.setUurtariefWeekend(t.getUurtariefWeekend());
					tarief.setVasttariefhuisbezoeken(t.getVasttariefhuisbezoeken());
					displayTarief();
					break;
				}
			}
		}
	}
	
	protected void cbVastetarievenchecked(ActionEvent e) {
		if (cbVastetarieven.isSelected()){
			panelVariabeltarief.setEnabled(false);
			for (Component c: panelVariabeltarief.getComponents()){
				c.setEnabled(false);
			}
			panelVasttarief.setEnabled(true);
			for (Component c: panelVasttarief.getComponents()){
				c.setEnabled(true);
			}
		} else {
			panelVariabeltarief.setEnabled(true);
			for (Component c: panelVariabeltarief.getComponents()){
				c.setEnabled(true);
			}
			panelVasttarief.setEnabled(false);
			for (Component c: panelVasttarief.getComponents()){
				c.setEnabled(false);
			}
		}
	}
	public void setInfo(InfoBase info){
		try {
			tarief = (TariefInfo)info;
			tarieven = ServiceCaller.werkgeverFacade(getLoginSession()).getTarievenByWerkgever(((TariefInfo)info).getWerkgeverId());
			werkgevers = ServiceCaller.werkgeverFacade(getLoginSession()).allWerkgeversList();
			holdings = ServiceCaller.werkgeverFacade(getLoginSession()).getHoldings();
			werkgeverSelection.setWerkgevers(werkgevers);
			werkgeverSelection.setHoldings(holdings);
			if (tarief.getHoldingId() != null)
				werkgeverSelection.setHoldingId(tarief.getHoldingId());
			else
				werkgeverSelection.setWerkgeverId(tarief.getWerkgeverId());
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
			
		try {
			if (this.getMode() != formMode.New){
				tarief = ServiceCaller.werkgeverFacade(getLoginSession()).getTariefById(tarief.getId());
				if (tarief == null){
					JOptionPane.showMessageDialog(this, "Tarief kan niet worden opgevraagd. \nDeze is mogelijk verwijderd");
					return;
				}
			}
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
		displayTarief();

		if (tarief.getWerkgeverId() != null){
			werkgeverSelection.setWerkgeverId(tarief.getWerkgeverId());
			WerkgeverSelected(tarief.getWerkgeverId());
		}
		if (tarief.getHoldingId() != null){
			werkgeverSelection.setHoldingId(tarief.getHoldingId());
			HoldingSelected(tarief.getHoldingId());
		}
		initialized = true;
		activateListener();
	}
	private void displayTarief() {
		try {
			dtpIngangsdatum.setDate(tarief.getIngangsdatum());
			dtpEinddatum.setDate(tarief.getEinddatum());
			dtpPeildatum.setDate(tarief.getDatumAansluitkosten());
			
			txtAansluitkosten.setValue(tarief.getAansluitkosten());
			if (tarief.getAansluitkostenPeriode() == __tariefperiode.JAAR){
				rbAansluitkostenPeriodeJaar.setSelected(true);
//				rbAansluitkostenPeriodeMaand.setSelected(false);
			} else {
				rbAansluitkostenPeriodeMaand.setSelected(true);
//				rbAansluitkostenPeriodeMaand.setSelected(true);
			}
			txtAbonnementskosten.setValue(tarief.getAbonnement());
			if (tarief.getAbonnementPeriode() == __tariefperiode.JAAR){
				rbAbonnementskostenPeriodeJaar.setSelected(true);
//				rbAbonnementskostenPeriodeMaand.setSelected(false);
			} else {
				rbAbonnementskostenPeriodeMaand.setSelected(true);
//				rbAbonnementskostenPeriodeMaand.setSelected(true);
			}
			cbVastetarieven.setSelected(tarief.getVasttariefhuisbezoeken());
			cbVastetarievenchecked(null);
			/*
			 * vaste tarieven
			 */
			txtVastHuisbezoekStandaard.setValue(tarief.getStandaardHuisbezoekTarief());
			txtVastZaterdag.setValue(tarief.getHuisbezoekZaterdagTarief());
			txtMaandkostenSecretariaat.setValue(tarief.getMaandbedragSecretariaat());
			txtVastSpoedAgesprokenTijd.setValue(tarief.getSpoedbezoekZelfdedagTarief());
			txtVastHuisbezoekZelfdedag.setValue(tarief.getHuisbezoekTarief());
			txtVastHuisbezoekSpoed.setValue(tarief.getSpoedbezoekTarief());
			txtVastTelefonischeControle.setValue(tarief.getTelefonischeControleTarief());

			/*
			 * Variabele kosten
			 */
			txtKmTarief.setValue(tarief.getKmTarief());
			txtCasemanagement.setValue(tarief.getSociaalbezoekTarief());
			txtSecretariaatskosten.setValue(tarief.getSecretariaatskosten());
			txtUurtariefNormaal.setValue(tarief.getUurtariefNormaal());
			txtUurtariefWeekend.setValue(tarief.getUurtariefWeekend());
			
			txtOmschrijvingFactuur.setText(tarief.getOmschrijvingFactuur());
			
		} catch (PropertyVetoException e) {
			e.printStackTrace();
			ExceptionLogger.ProcessException(e,this);
		}
	}
	private BigDecimal formatBigDecimal(DecimalFormat format, String value) throws ValidationException{
		BigDecimal bedrag;
		ParsePosition pos = new ParsePosition(0);		
		bedrag = (BigDecimal)format.parse(value,pos);
		if (pos.getIndex() < value.length()) {
			throw new ValidationException("Ongeldige tekens in bedrag");
		}
		return bedrag;
	}
	protected void okButtonClicked(ActionEvent e) {
		DecimalFormat format = new DecimalFormat("##0,00");
		format.setParseBigDecimal(true);

		if (tarief.getWerkgeverId() == null && tarief.getHoldingId() == null){
			JOptionPane.showMessageDialog(this, "Geen werkgever of holding geselecteerd.");
			return;

		}
		
		tarief.setEinddatum(dtpEinddatum.getDate());
		tarief.setIngangsdatum(dtpIngangsdatum.getDate());
		try {
			/*
			 * Abonnementen
			 */
			
			tarief.setDatumAansluitkosten(dtpPeildatum.getDate());
			tarief.setAansluitkosten(formatBigDecimal(format, txtAansluitkosten.getText()));
			tarief.setAbonnement(formatBigDecimal(format, txtAbonnementskosten.getText()));

			if (rbAansluitkostenPeriodeJaar.isSelected())
				tarief.setAansluitkostenPeriode(__tariefperiode.JAAR);
			else
				tarief.setAansluitkostenPeriode(__tariefperiode.MAAND);
			if (rbAbonnementskostenPeriodeJaar.isSelected())
				tarief.setAbonnementPeriode(__tariefperiode.JAAR);
			else
				tarief.setAbonnementPeriode(__tariefperiode.MAAND);
			tarief.setVasttariefhuisbezoeken(cbVastetarieven.isSelected());

			/*
			 * vaste tarieven
			 */

			tarief.setStandaardHuisbezoekTarief(formatBigDecimal(format, txtVastHuisbezoekStandaard.getText()));
			tarief.setHuisbezoekZaterdagTarief(formatBigDecimal(format, txtVastZaterdag.getText()));
			tarief.setMaandbedragSecretariaat(formatBigDecimal(format, txtMaandkostenSecretariaat.getText()));
			tarief.setSpoedbezoekZelfdedagTarief(formatBigDecimal(format, txtVastSpoedAgesprokenTijd.getText()));
			tarief.setHuisbezoekTarief(formatBigDecimal(format, txtVastHuisbezoekZelfdedag.getText()));
			tarief.setSpoedbezoekTarief(formatBigDecimal(format, txtVastHuisbezoekSpoed.getText()));
			tarief.setTelefonischeControleTarief(formatBigDecimal(format, txtVastTelefonischeControle.getText()));

			/*
			 * Variabele kosten
			 */
			tarief.setKmTarief(formatBigDecimal(format, txtKmTarief.getText()));
			tarief.setSociaalbezoekTarief(formatBigDecimal(format, txtCasemanagement.getText()));
			tarief.setSecretariaatskosten(formatBigDecimal(format, txtSecretariaatskosten.getText()));
			tarief.setUurtariefNormaal(formatBigDecimal(format, txtUurtariefNormaal.getText()));
			tarief.setUurtariefWeekend(formatBigDecimal(format, txtUurtariefWeekend.getText()));
			
			tarief.setOmschrijvingFactuur(txtOmschrijvingFactuur.getText());

			if (tarief.getHoldingId() != null){
				for (HoldingInfo hi: holdings){
					if (hi.getId().equals(tarief.getHoldingId())){
						if (hi.isFactureren()){
							if (hi.getFactuurtype() == __factuurtype.SEPARAAT){
								if (tarief.getEinddatum() == null || DateOnly.after(tarief.getEinddatum(), new Date())){
									throw new ValidationException("Tarief voor holding waarnaar facturen separaat worden verstuurd. Dit is niet toegestaan.");
								}
							}
						}else{
							throw new ValidationException("Tarief voor holding waar factureren niet aanstaat. Dit is niet toegestaan.");
						}
					}
				}
			}
			
			tarief.validate();
			
		} catch (ValidationException e1) {
        	ExceptionLogger.ProcessException(e1,this);
        	return;
		}
		if (this.getLoginSession() != null)
        {
        	try {
        		tarief.validate();
        		switch (this.getMode())
        		{
        			case New: 		ServiceCaller.werkgeverFacade(getLoginSession()).addTarief(tarief);
        							break;
        			case Update: 	ServiceCaller.werkgeverFacade(getLoginSession()).updateTarief(tarief);
        							break;
        			case Delete: 	ServiceCaller.werkgeverFacade(getLoginSession()).deleteTarief(tarief);
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
