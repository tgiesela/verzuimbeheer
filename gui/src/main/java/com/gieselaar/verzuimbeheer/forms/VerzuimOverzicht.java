package com.gieselaar.verzuimbeheer.forms;

import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyVetoException;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JOptionPane;

import com.gieselaar.verzuimbeheer.baseforms.BaseListForm;
import com.gieselaar.verzuimbeheer.baseforms.DefaultListFormNotification;
import com.gieselaar.verzuimbeheer.baseforms.JDesktopPaneTGI;
import com.gieselaar.verzuimbeheer.components.JTextFieldTGI;
import com.gieselaar.verzuimbeheer.exceptions.VerzuimApplicationException;
import com.gieselaar.verzuimbeheer.exceptions.PermissionException;
import com.gieselaar.verzuimbeheer.exceptions.ValidationException;
import com.gieselaar.verzuimbeheer.services.CascodeInfo;
import com.gieselaar.verzuimbeheer.services.DienstverbandInfo;
import com.gieselaar.verzuimbeheer.services.InfoBase;
import com.gieselaar.verzuimbeheer.services.InfoBase.__geslacht;
import com.gieselaar.verzuimbeheer.services.InfoBase.persistenceaction;
import com.gieselaar.verzuimbeheer.services.InfoBase.persistencestate;
import com.gieselaar.verzuimbeheer.services.VerzuimHerstelInfo;
import com.gieselaar.verzuimbeheer.services.VerzuimInfo;
import com.gieselaar.verzuimbeheer.services.VerzuimInfo.__verzuimtype;
import com.gieselaar.verzuimbeheer.services.WerknemerInfo;
import com.gieselaar.verzuimbeheer.services.InfoBase.__burgerlijkestaat;
import com.gieselaar.verzuimbeheer.utils.ExceptionLogger;
import com.gieselaar.verzuimbeheer.utils.ServiceCaller;
import javax.swing.JLabel;
import javax.swing.JComboBox;
import javax.swing.RowSorter;
import javax.swing.SortOrder;

import com.gieselaar.verzuimbeheer.utils.TypeEntry;
import com.michaelbaranov.microba.calendar.DatePicker;

import javax.swing.JButton;

public class VerzuimOverzicht extends BaseListForm {
	/**
	 * no longer used !!!!!!!!!!!!!!!!!!!!!!!!!
	 */
	private static final long serialVersionUID = 1L;
	private List<VerzuimInfo> verzuimen;
	private List<VerzuimHerstelInfo> herstellen;
	private Component thisform = this;
	private JTextFieldTGI txtBSN;
	private JTextFieldTGI txtVoornaam;
	private JTextFieldTGI txtAchternaam;
	private JTextFieldTGI txtVoorletters;
	private JTextFieldTGI txtVoorvoegsel;
	private JComboBox<TypeEntry> cmbBurgerlijkestaat;
	private JComboBox<TypeEntry> cmbGeslacht;
	private DatePicker dtpGeboortedatum;
	private WerknemerInfo werknemer;
	private JTextFieldTGI txtWerkgever;
	private List<CascodeInfo> cascodes;
	private Hashtable<Integer, Object> hashCascodes = new Hashtable<Integer, Object>();
	private JButton btnVorigDienstverband;
	private JButton btnVolgendDienstverband;
	private JLabel lblDienstverband;
	private DefaultComboBoxModel<TypeEntry> geslachtModel;
	private DefaultComboBoxModel<TypeEntry> burgerlijkestaatModel;
	private DienstverbandInfo dienstverband = null;
	private int dienstverbandinx = -1;
	private boolean openverzuimfound;
	private DatePicker dtpStartdienstverband ;
	private DatePicker dtpEinddienstverband ;
	/**
	 * Create the frame.
	 */
	
	public VerzuimOverzicht(JDesktopPaneTGI mdiPanel) {
		super("Werknemer verzuimen", mdiPanel);
		getExportButton().setLocation(0, 204);
		initialize();
	}
	public void initialize() {
		setBounds(100, 100, 699, 454);
		setDetailFormClass(VerzuimDetail.class, VerzuimInfo.class);
		this.setFiltering(false);
		
		getPanelAction().setLocation(10, 229);
		getScrollPane().setBounds(39, 205, 614, 178);
		
		JLabel label = new JLabel("Werkgever");
		label.setBounds(38, 39, 65, 14);
		getContentPane().add(label);
		
		JLabel label_1 = new JLabel("Achternaam");
		label_1.setBounds(38, 63, 75, 14);
		getContentPane().add(label_1);
		
		JLabel label_2 = new JLabel("Voornaam");
		label_2.setBounds(39, 86, 74, 14);
		getContentPane().add(label_2);
		
		JLabel label_3 = new JLabel("BSN");
		label_3.setBounds(39, 109, 46, 14);
		getContentPane().add(label_3);
		
		txtBSN = new JTextFieldTGI();
		txtBSN.setEditable(false);
		txtBSN.setEnabled(false);
		txtBSN.setColumns(10);
		txtBSN.setBounds(135, 106, 86, 20);
		getContentPane().add(txtBSN);
		
		txtVoornaam = new JTextFieldTGI();
		txtVoornaam.setEditable(false);
		txtVoornaam.setEnabled(false);
		txtVoornaam.setColumns(10);
		txtVoornaam.setBounds(135, 83, 152, 20);
		getContentPane().add(txtVoornaam);
		
		txtAchternaam = new JTextFieldTGI();
		txtAchternaam.setEditable(false);
		txtAchternaam.setEnabled(false);
		txtAchternaam.setColumns(10);
		txtAchternaam.setBounds(135, 60, 152, 20);
		getContentPane().add(txtAchternaam);
		
		JLabel label_4 = new JLabel("Voorvoegsel");
		label_4.setBounds(300, 63, 65, 14);
		getContentPane().add(label_4);
		
		JLabel label_5 = new JLabel("Voorletters");
		label_5.setBounds(300, 86, 65, 14);
		getContentPane().add(label_5);
		
		JLabel label_6 = new JLabel("Burg. staat");
		label_6.setBounds(300, 109, 65, 14);
		getContentPane().add(label_6);
		
		cmbBurgerlijkestaat = new JComboBox<TypeEntry>();
		cmbBurgerlijkestaat.setEnabled(false);
		cmbBurgerlijkestaat.setMaximumRowCount(3);
		cmbBurgerlijkestaat.setBounds(372, 106, 130, 20);
		getContentPane().add(cmbBurgerlijkestaat);
		
		txtVoorletters = new JTextFieldTGI();
		txtVoorletters.setEditable(false);
		txtVoorletters.setEnabled(false);
		txtVoorletters.setColumns(10);
		txtVoorletters.setBounds(372, 83, 86, 20);
		getContentPane().add(txtVoorletters);
		
		txtVoorvoegsel = new JTextFieldTGI();
		txtVoorvoegsel.setEditable(false);
		txtVoorvoegsel.setEnabled(false);
		txtVoorvoegsel.setColumns(10);
		txtVoorvoegsel.setBounds(372, 60, 86, 20);
		getContentPane().add(txtVoorvoegsel);
		
		JLabel label_7 = new JLabel("Geboortedatum");
		label_7.setBounds(39, 132, 75, 14);
		getContentPane().add(label_7);
		
		dtpGeboortedatum = new DatePicker();
		dtpGeboortedatum.setFieldEditable(false);
		dtpGeboortedatum.setEnabled(false);
		dtpGeboortedatum.setBounds(135, 129, 112, 21);
		dtpGeboortedatum.setDateFormat(new SimpleDateFormat("dd-MM-yyyy"));
		getContentPane().add(dtpGeboortedatum);
		
		JLabel label_8 = new JLabel("Geslacht");
		label_8.setBounds(300, 132, 64, 14);
		getContentPane().add(label_8);
		
		cmbGeslacht = new JComboBox<TypeEntry>();
		cmbGeslacht.setEnabled(false);
		cmbGeslacht.setMaximumRowCount(3);
		cmbGeslacht.setBounds(372, 129, 62, 20);
		getContentPane().add(cmbGeslacht);
		
		txtWerkgever = new JTextFieldTGI();
		txtWerkgever.setEditable(false);
		txtWerkgever.setEnabled(false);
		txtWerkgever.setBounds(135, 36, 152, 20);
		getContentPane().add(txtWerkgever);
		txtWerkgever.setColumns(10);
		
		btnVorigDienstverband = new JButton("<");
		btnVorigDienstverband.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				btnVorigDienstverbandClicked(e);
			}
		});
		btnVorigDienstverband.setBounds(39, 179, 46, 23);
		getContentPane().add(btnVorigDienstverband);
		
		btnVolgendDienstverband = new JButton(">");
		btnVolgendDienstverband.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				btnVolgendDienstverbandClicked(e);
			}
		});
		btnVolgendDienstverband.setBounds(607, 179, 46, 23);
		getContentPane().add(btnVolgendDienstverband);
		
		lblDienstverband = new JLabel("Dienstverband van:");
		lblDienstverband.setBounds(106, 183, 112, 14);
		getContentPane().add(lblDienstverband);
		
		dtpStartdienstverband = new DatePicker();
		dtpStartdienstverband.setEnabled(false);
		dtpStartdienstverband.setBounds(209, 179, 86, 21);
		dtpStartdienstverband.setDateFormat(new SimpleDateFormat("dd-MM-yyyy"));
		getContentPane().add(dtpStartdienstverband);
		
		JLabel lblTot = new JLabel("tot:");
		lblTot.setBounds(305, 183, 31, 14);
		getContentPane().add(lblTot);
		
		dtpEinddienstverband = new DatePicker();
		dtpEinddienstverband.setEnabled(false);
		dtpEinddienstverband.setBounds(331, 179, 86, 21);
		dtpEinddienstverband.setDateFormat(new SimpleDateFormat("dd-MM-yyyy"));
		getContentPane().add(dtpEinddienstverband);

		this.setEventNotifier(new DefaultListFormNotification() {
			
			@Override
			public void populateTableRequested() {
				populateTable();
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
					if (rslt == JOptionPane.YES_OPTION)
						ServiceCaller.verzuimFacade(getLoginSession()).deleteVerzuim((VerzuimInfo)info);
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
		});

		addColumn("CAScode", null,120 );
		addColumn("Type",null,60);
		addColumn("Aanvang",null,60, Date.class);
		addColumn("Vangnet",null,60);
		addColumn("Herstel",null,60, Date.class);
		addColumn("%Herstel",null,60);
		addColumn("Ketenvzm",null,70);
	}
	protected void btnVolgendDienstverbandClicked(ActionEvent e) {
		if (dienstverbandinx >= werknemer.getDienstVerbanden().size()-1)
			;
		else
		{
			dienstverbandinx++;
			dienstverband = werknemer.getDienstVerbanden().get(dienstverbandinx);
			displayDienstverband();
			this.ReloadTable();
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
			this.ReloadTable();
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
	}

	public void setInfo(InfoBase info){
		boolean found;

		werknemer = (WerknemerInfo) info;
		/*
		 * We gaan nu alle details ophalen van deze werknemer en de cascodes
		 */
		try {
			werknemer = ServiceCaller.werknemerFacade(getLoginSession()).getWerknemer(werknemer.getId());
			cascodes = ServiceCaller.cascodeFacade(getLoginSession()).allCascodes();
			for (CascodeInfo ci: cascodes)
			{
				hashCascodes.put(ci.getId(), ci);
			}
		} catch (PermissionException e1) {
			ExceptionLogger.ProcessException(e1,this);
			return;
		} catch (VerzuimApplicationException e1) {
        	ExceptionLogger.ProcessException(e1,this);
        	return;
		} catch (Exception e1){
        	ExceptionLogger.ProcessException(e1,this);
        	return;
		}

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
		
		if (!found)
		{
			dienstverbandinx = werknemer.getDienstVerbanden().size() - 1;
			dienstverband 	 = werknemer.getDienstVerbanden().get(dienstverbandinx);
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
		
        enableNextPrevButtons();
        displayDienstverband();

	}
	private void displayDienstverband() {
		TypeEntry geslacht;
		TypeEntry burgerlijkestaat;

        verzuimen = dienstverband.getVerzuimen();

        try {
			dtpStartdienstverband.setDate(dienstverband.getStartdatumcontract());
			dtpEinddienstverband.setDate(dienstverband.getEinddatumcontract());
		} catch (PropertyVetoException e1) {
			ExceptionLogger.ProcessException(e1,this);
		}
        
        txtAchternaam.setText(werknemer.getAchternaam());
		txtBSN.setText(werknemer.getBurgerservicenummer());
		txtVoorletters.setText(werknemer.getVoorletters());
		txtVoornaam.setText(werknemer.getVoornaam());
		txtVoorvoegsel.setText(werknemer.getVoorvoegsel());
		try {
			dtpGeboortedatum.setDate(werknemer.getGeboortedatum());
		} catch (PropertyVetoException e) {
			ExceptionLogger.ProcessException(e,this);
		}

	
		txtWerkgever.setText(werknemer.getWerkgever().getNaam());
		
        if (werknemer.getState() != persistencestate.EXISTS) {
        	cmbGeslacht.setSelectedIndex(0);
        	cmbBurgerlijkestaat.setSelectedIndex(0);
        }
        else
        {
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
		
	}
	private void displayVerzuimen(){
		CascodeInfo cascode;
		Date datumHerstel;
		BigDecimal percentage = new BigDecimal(0);
		List<RowSorter.SortKey> sortKeys;
		
		openverzuimfound = false;
		deleteAllRows();
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
							addRow(rowData, v, Color.ORANGE);
						else
							addRow(rowData, v, Color.GREEN);
					else
						if (v.getVerzuimtype() == __verzuimtype.PREVENTIEF)
							addRow(rowData, v, Color.PINK);
						else
							addRow(rowData, v);
				}
			}
			sortKeys = new ArrayList<RowSorter.SortKey>();
	        sortKeys.add(new RowSorter.SortKey(2, SortOrder.DESCENDING));
	        this.getSorter().setSortKeys(sortKeys);
	        this.getSorter().sort();
		}
        //this.getTable().setRowSelectionInterval(0, 0);
	}
	public void populateTable(){
        try {
			herstellen = ServiceCaller.verzuimFacade(getLoginSession()).getVerzuimHerstellen(dienstverband.getId());
			verzuimen = ServiceCaller.verzuimFacade(getLoginSession()).getVerzuimenWerknemer(dienstverband.getId());
		} catch (PermissionException e2) {
			ExceptionLogger.ProcessException(e2, this,"Unable to obtain details");
			return;
		} catch (VerzuimApplicationException e2) {
        	ExceptionLogger.ProcessException(e2,this);
        	return;
		} catch (Exception e1){
        	ExceptionLogger.ProcessException(e1,this);
        	return;
		}
		displayVerzuimen();
	}
}
