package com.gieselaar.verzuimbeheer.forms;

import java.awt.dnd.DropTarget;
import java.awt.event.ActionEvent;
import java.beans.PropertyVetoException;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Vector;

import com.gieselaar.verzuimbeheer.baseforms.BaseDetailform;
import com.gieselaar.verzuimbeheer.baseforms.DefaultDragdropNotification;
import com.gieselaar.verzuimbeheer.baseforms.DefaultListFormNotification;
import com.gieselaar.verzuimbeheer.baseforms.DragDropListener;
import com.gieselaar.verzuimbeheer.baseforms.JDesktopPaneTGI;
import com.gieselaar.verzuimbeheer.components.JTextFieldTGI;
import com.gieselaar.verzuimbeheer.baseforms.ListFormNotification.__continue;
import com.gieselaar.verzuimbeheer.components.TablePanel;
import com.gieselaar.verzuimbeheer.components.TodoPanel;
import com.gieselaar.verzuimbeheer.exceptions.VerzuimApplicationException;
import com.gieselaar.verzuimbeheer.exceptions.PermissionException;
import com.gieselaar.verzuimbeheer.exceptions.ValidationException;
import com.gieselaar.verzuimbeheer.services.ActiviteitInfo;
import com.gieselaar.verzuimbeheer.services.CascodeInfo;
import com.gieselaar.verzuimbeheer.services.DocumentTemplateInfo;
import com.gieselaar.verzuimbeheer.services.GebruikerInfo;
import com.gieselaar.verzuimbeheer.services.InfoBase;
import com.gieselaar.verzuimbeheer.services.VerzuimInfo.__meldingswijze;
import com.gieselaar.verzuimbeheer.services.WerknemerInfo;
import com.gieselaar.verzuimbeheer.services.VerzuimActiviteitInfo;
import com.gieselaar.verzuimbeheer.services.VerzuimDocumentInfo;
import com.gieselaar.verzuimbeheer.services.VerzuimHerstelInfo;
import com.gieselaar.verzuimbeheer.services.VerzuimInfo;
import com.gieselaar.verzuimbeheer.services.VerzuimInfo.__gerelateerdheid;
import com.gieselaar.verzuimbeheer.services.VerzuimInfo.__vangnettype;
import com.gieselaar.verzuimbeheer.services.VerzuimInfo.__verzuimtype;
import com.gieselaar.verzuimbeheer.services.WiapercentageInfo;
import com.gieselaar.verzuimbeheer.services.WiapercentageInfo.__wiapercentage;
import com.gieselaar.verzuimbeheer.utils.ExceptionLogger;
import com.gieselaar.verzuimbeheer.utils.ServiceCaller;
import com.gieselaar.verzuimbeheer.utils.ServiceLocatorException;
import com.gieselaar.verzuimbeheer.utils.TypeEntry;
import com.gieselaar.verzuimbeheer.utils.WordDocument;
import com.michaelbaranov.microba.calendar.DatePicker;

import javax.ejb.EJBException;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JLabel;
import javax.swing.JComboBox;
import javax.swing.JCheckBox;
import javax.swing.JOptionPane;
import javax.swing.JTabbedPane;
import javax.swing.RowSorter;
import javax.swing.SortOrder;

import java.awt.Color;
import java.awt.Component;

import javax.swing.JButton;

import java.awt.event.ActionListener;

public class VerzuimDetail extends BaseDetailform {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private List<CascodeInfo> cascodes;
	private List<ActiviteitInfo> activiteiten;
	private List<GebruikerInfo> gebruikers;
	private List<WiapercentageInfo> wiapercentages;
	private List<VerzuimInfo> verzuimen;
	private List<DocumentTemplateInfo> templates;
	private VerzuimInfo verzuim;
	private WerknemerInfo werknemer;
	private JTextFieldTGI txtUsername;
	private DatePicker dtpStartdatumverzuim;
	private DatePicker dtpEinddatumverzuim;
	private DatePicker dtpMeldingsdatum;
	private JComboBox<TypeEntry> cmbCascode;
	private JComboBox<TypeEntry> cmbVangnet;
	private JComboBox<TypeEntry> cmbVerzuimtype;
	private JComboBox<TypeEntry> cmbGerelateerdheid;
	private JComboBox<TypeEntry> cmbTemplates;
	private JComboBox<TypeEntry> cmbMeldingswijze;
	private JCheckBox chckbxKetenverzuim;
	private JCheckBox chckbxUitkeringNaarWerknemer;

	private TablePanel tpVerzuimherstellen;
	private TablePanel tpVerzuimdocumenten;
	private TablePanel tpVerrichtingen;
	private TodoPanel tpTodos;

	private Component thisform = this;
	private boolean initialized = false;
	private JButton btnGenereer;

	/**
	 * Create the frame.
	 */
	public VerzuimDetail(JDesktopPaneTGI mdiPanel) {
		super("Verzuim", mdiPanel);
		initialize();
		DragDropListener listener = new DragDropListener();
		listener.setDragDropNotification(new DefaultDragdropNotification() {
			@Override
			public void fileDropped(File droppedfile) {
				if (((BaseDetailform) thisform).getMode() == formMode.New)
					// Drop not allowed
					;
				else {
					VerzuimDocumentInfo newDoc = new VerzuimDocumentInfo();
					newDoc.setAanmaakdatum(new Date());
					newDoc.setAanmaakuser(getLoginSession().getGebruiker()
							.getId());
					newDoc.setDocumentnaam(droppedfile.getName());
					newDoc.setPadnaam(droppedfile.getAbsolutePath());
					newDoc.setVerzuimId(verzuim.getId());
					VerzuimDocumentDetail docform = new VerzuimDocumentDetail(
							((BaseDetailform) thisform).getMdiPanel());
					docform.setLoginSession(getLoginSession());
					docform.setInfo(newDoc);

					((BaseDetailform) thisform).getDesktopPane().add(docform);
					((BaseDetailform) thisform).getDesktopPane().moveToFront(
							docform);
					docform.setVisible(true);

				}

			}

		});
		new DropTarget(thisform, listener);
	}

	public void setInfo(InfoBase info) {
		TypeEntry Cascode;
		TypeEntry Vangnet;
		TypeEntry Verzuimtype;
		TypeEntry Gerelateerdheid;
		TypeEntry Meldingswijze;
		DefaultComboBoxModel<TypeEntry> CascodeModel;
		DefaultComboBoxModel<TypeEntry> VangnetModel;
		DefaultComboBoxModel<TypeEntry> VerzuimtypeModel;
		DefaultComboBoxModel<TypeEntry> GerelateerdheidModel;
		DefaultComboBoxModel<TypeEntry> TemplateModel;
		DefaultComboBoxModel<TypeEntry> MeldingswijzeModel;

		tpVerzuimherstellen.setLoginSession(this.getLoginSession());
		tpVerzuimdocumenten.setLoginSession(this.getLoginSession());
		tpVerrichtingen.setLoginSession(this.getLoginSession());
		tpTodos.setLoginSession(this.getLoginSession());

		verzuim = (VerzuimInfo) info;
		try {
			if (this.getMode() == formMode.New) {
				verzuim.setStartdatumverzuim(new Date());
				verzuim.setEinddatumverzuim(null);
				verzuim.setMeldingsdatum(new Date());
				verzuim.setKetenverzuim(false);
				verzuim.setGerelateerdheid(__gerelateerdheid.NVT);
				verzuim.setVangnettype(__vangnettype.NVT);
				verzuim.setVerzuimtype(__verzuimtype.VERZUIM);
				verzuim.setGebruiker(this.getLoginSession().getGebruiker()
						.getId());
				werknemer = verzuim.getWerknemer();
				wiapercentages = werknemer.getWiaPercentages();
				if (wiapercentages != null) {
					for (WiapercentageInfo wp : wiapercentages) {
						if (wp.getEinddatum() == null
								&& wp.getCodeWiaPercentage() != __wiapercentage.NVT) {
							verzuim.setVangnettype(__vangnettype.WIA);
						}
					}
				}
			} else
				verzuim = ServiceCaller.verzuimFacade(getLoginSession()).getVerzuimDetails(verzuim.getId());

			cascodes = ServiceCaller.verzuimFacade(getLoginSession()).getCascodes();
			activiteiten = ServiceCaller.verzuimFacade(getLoginSession()).getActiviteiten();
			gebruikers = ServiceCaller.verzuimFacade(getLoginSession()).getGebruikers();
			verzuimen = ServiceCaller.verzuimFacade(getLoginSession()).getVerzuimenWerknemer(verzuim
					.getDienstverbandId());
			templates = ServiceCaller.verzuimFacade(getLoginSession()).getDocumentTemplates();

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

		VangnetModel = new DefaultComboBoxModel<TypeEntry>();
		cmbVangnet.setModel(VangnetModel);
		for (__vangnettype g : __vangnettype.values()) {
			TypeEntry soort = new TypeEntry(g.getValue(), g.toString());
			VangnetModel.addElement(soort);
		}

		VerzuimtypeModel = new DefaultComboBoxModel<TypeEntry>();
		cmbVerzuimtype.setModel(VerzuimtypeModel);
		for (__verzuimtype g : __verzuimtype.values()) {
			TypeEntry soort = new TypeEntry(g.getValue(), g.toString());
			VerzuimtypeModel.addElement(soort);
		}

		GerelateerdheidModel = new DefaultComboBoxModel<TypeEntry>();
		cmbGerelateerdheid.setModel(GerelateerdheidModel);
		for (__gerelateerdheid g : __gerelateerdheid.values()) {
			TypeEntry soort = new TypeEntry(g.getValue(), g.toString());
			GerelateerdheidModel.addElement(soort);
		}

		MeldingswijzeModel = new DefaultComboBoxModel<TypeEntry>();
		cmbMeldingswijze.setModel(MeldingswijzeModel);
		for (__meldingswijze w : __meldingswijze.values()) {
			TypeEntry wijze = new TypeEntry(w.getValue(), w.toString());
			MeldingswijzeModel.addElement(wijze);
		}

		CascodeModel = new DefaultComboBoxModel<TypeEntry>();
		cmbCascode.setModel(CascodeModel);
		CascodeModel.addElement(new TypeEntry(-1,""));
		CascodeInfo.sort(cascodes, CascodeInfo.__sortcol.NAAM);
		for (CascodeInfo c : cascodes) {
			TypeEntry soort = new TypeEntry(c.getId(), c.getOmschrijving());
			/*
			 * Alleen actieve cascodes kunnen worden geselecteerd. Voor
			 * backwards compatibility wordt een oude cascode wel toegevoegd als
			 * deze al aan het verzuim was toegewezen.
			 */
			if (c.isActief())
				CascodeModel.addElement(soort);
			else if (c.getId() == verzuim.getCascode()) {
				CascodeModel.addElement(soort);
			}
		}

		TemplateModel = new DefaultComboBoxModel<TypeEntry>();
		DocumentTemplateInfo.sort(templates, DocumentTemplateInfo.__sortcol.NAAM);
		cmbTemplates.setModel(TemplateModel);
		for (DocumentTemplateInfo dt : templates) {
			TypeEntry tmpl = new TypeEntry(dt.getId(), dt.getNaam());
			TemplateModel.addElement(tmpl);
		}

		for (int i = 0; i < VangnetModel.getSize(); i++) {
			Vangnet = (TypeEntry) VangnetModel.getElementAt(i);
			if (__vangnettype.parse(Vangnet.getValue()) == verzuim
					.getVangnettype()) {
				VangnetModel.setSelectedItem(Vangnet);
				break;
			}
		}
		for (int i = 0; i < MeldingswijzeModel.getSize(); i++) {
			Meldingswijze = (TypeEntry) MeldingswijzeModel.getElementAt(i);
			if (__meldingswijze.parse(Meldingswijze.getValue()) == verzuim
					.getMeldingswijze()) {
				MeldingswijzeModel.setSelectedItem(Meldingswijze);
				break;
			}
		}
		for (int i = 0; i < VerzuimtypeModel.getSize(); i++) {
			Verzuimtype = (TypeEntry) VerzuimtypeModel.getElementAt(i);
			if (__verzuimtype.parse(Verzuimtype.getValue()) == verzuim
					.getVerzuimtype()) {
				VerzuimtypeModel.setSelectedItem(Verzuimtype);
				break;
			}
		}
		for (int i = 0; i < GerelateerdheidModel.getSize(); i++) {
			Gerelateerdheid = (TypeEntry) GerelateerdheidModel.getElementAt(i);
			if (__gerelateerdheid.parse(Gerelateerdheid.getValue()) == verzuim
					.getGerelateerdheid()) {
				GerelateerdheidModel.setSelectedItem(Gerelateerdheid);
				break;
			}
		}
		for (int i = 0; i < CascodeModel.getSize(); i++) {
			Cascode = (TypeEntry) CascodeModel.getElementAt(i);
			if (Cascode.getValue() == verzuim.getCascode()) {
				CascodeModel.setSelectedItem(Cascode);
				break;
			}
		}
		try {
			dtpStartdatumverzuim.setDate(verzuim.getStartdatumverzuim());
			dtpEinddatumverzuim.setDate(verzuim.getEinddatumverzuim());
			dtpMeldingsdatum.setDate(verzuim.getMeldingsdatum());
		} catch (PropertyVetoException e) {
			ExceptionLogger.ProcessException(e,this);
		}
		chckbxKetenverzuim.setSelected(verzuim.getKetenverzuim());
		for (GebruikerInfo gi : gebruikers)
			if (gi.getId() == verzuim.getGebruiker()) {
				txtUsername.setText(gi.getAchternaam());
				break;
			}
		
		chckbxUitkeringNaarWerknemer.setSelected(verzuim.isUitkeringnaarwerknemer());
		displayVerzuimDocumenten();
		displayVerzuimHerstellen();
		displayVerzuimVerrichtingen();
		tpTodos.setVerzuim(verzuim);
		initialized = true;
		activateListener();
	}

	void initialize() {
		setBounds(100, 100, 669, 524);

		dtpStartdatumverzuim = new DatePicker();
		dtpStartdatumverzuim.setBounds(159, 41, 86, 21);
		dtpStartdatumverzuim.setDateFormat(new SimpleDateFormat("dd-MM-yyyy"));
		getContentPane().add(dtpStartdatumverzuim);

		dtpEinddatumverzuim = new DatePicker();
		dtpEinddatumverzuim.setFieldEditable(false);
		dtpEinddatumverzuim.setEnabled(false);
		dtpEinddatumverzuim.setBounds(159, 65, 86, 21);
		dtpEinddatumverzuim.setDateFormat(new SimpleDateFormat("dd-MM-yyyy"));
		getContentPane().add(dtpEinddatumverzuim);

		dtpMeldingsdatum = new DatePicker();
		dtpMeldingsdatum.setBounds(159, 89, 86, 21);
		dtpMeldingsdatum.setDateFormat(new SimpleDateFormat("dd-MM-yyyy"));
		getContentPane().add(dtpMeldingsdatum);

		JLabel lblAanvangVerzuim = new JLabel("Aanvang verzuim:");
		lblAanvangVerzuim.setLabelFor(dtpStartdatumverzuim);
		lblAanvangVerzuim.setBounds(26, 44, 103, 14);
		getContentPane().add(lblAanvangVerzuim);

		JLabel lblEinddatumVerzuim = new JLabel("Einddatum verzuim:");
		lblEinddatumVerzuim.setLabelFor(dtpEinddatumverzuim);
		lblEinddatumVerzuim.setBounds(26, 68, 103, 14);
		getContentPane().add(lblEinddatumVerzuim);

		JLabel lblMeldingsdatum = new JLabel("Meldingsdatum:");
		lblMeldingsdatum.setLabelFor(dtpMeldingsdatum);
		lblMeldingsdatum.setBounds(26, 92, 105, 14);
		getContentPane().add(lblMeldingsdatum);

		cmbCascode = new JComboBox<TypeEntry>();
		cmbCascode.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				cmbCascodeClicked(e);
			}
		});
		cmbCascode.setBounds(159, 113, 254, 20);
		getContentPane().add(cmbCascode);

		JLabel lblCascode = new JLabel("Cascode:");
		lblCascode.setLabelFor(cmbCascode);
		lblCascode.setBounds(26, 116, 76, 14);
		getContentPane().add(lblCascode);

		chckbxKetenverzuim = new JCheckBox("Ketenverzuim");
		chckbxKetenverzuim.setEnabled(false);
		chckbxKetenverzuim.setBounds(255, 41, 97, 23);
		getContentPane().add(chckbxKetenverzuim);

		cmbVangnet = new JComboBox<TypeEntry>();
		cmbVangnet.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				TypeEntry vt = (TypeEntry)cmbVangnet.getSelectedItem();
				__vangnettype type = __vangnettype.parse(vt.getValue());
				if (type == __vangnettype.NVT){
					chckbxUitkeringNaarWerknemer.setEnabled(false);
					chckbxUitkeringNaarWerknemer.setSelected(false);
				}else{
					chckbxUitkeringNaarWerknemer.setEnabled(true);
				}
			}
		});
		cmbVangnet.setBounds(159, 161, 254, 20);
		getContentPane().add(cmbVangnet);

		JLabel lblVangnet = new JLabel("Vangnet:");
		lblVangnet.setLabelFor(cmbVangnet);
		lblVangnet.setBounds(26, 164, 46, 14);
		getContentPane().add(lblVangnet);

		JLabel lblGerelateerdheid = new JLabel("Gerelateerdheid:");
		lblGerelateerdheid.setBounds(26, 188, 103, 14);
		getContentPane().add(lblGerelateerdheid);

		cmbGerelateerdheid = new JComboBox<TypeEntry>();
		cmbGerelateerdheid.setBounds(159, 185, 254, 20);
		getContentPane().add(cmbGerelateerdheid);

		cmbVerzuimtype = new JComboBox<TypeEntry>();
		cmbVerzuimtype.setBounds(159, 137, 254, 20);
		getContentPane().add(cmbVerzuimtype);

		JLabel lblVerzuimtype = new JLabel("Verzuimtype");
		lblVerzuimtype.setBounds(26, 140, 76, 14);
		getContentPane().add(lblVerzuimtype);

		JLabel lblIngevoerdDoor = new JLabel("door:");
		lblIngevoerdDoor.setBounds(255, 93, 35, 14);
		getContentPane().add(lblIngevoerdDoor);

		txtUsername = new JTextFieldTGI();
		lblIngevoerdDoor.setLabelFor(txtUsername);
		txtUsername.setBounds(286, 89, 86, 20);
		getContentPane().add(txtUsername);
		txtUsername.setColumns(10);

		JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		tabbedPane.setBackground(Color.LIGHT_GRAY);
		tabbedPane.setBounds(26, 231, 615, 204);
		getContentPane().add(tabbedPane);

		tpVerzuimherstellen = new TablePanel(this.getMdiPanel());
		tpVerzuimherstellen.getPanelAction().setSize(582, 30);
		tpVerzuimherstellen.getPanelAction().setLocation(5, 146);
		
		tpVerzuimherstellen.getScrollPane().setBounds(5, 0, 595, 146);
		tpVerzuimherstellen.setDetailFormClass(VerzuimHerstelDetail.class,
				VerzuimHerstelInfo.class);
		tpVerzuimherstellen.addColumn("Datum", null, 80, Date.class);
		tpVerzuimherstellen.addColumn("% herstel", null, 70);
		tpVerzuimherstellen.addColumn("% AT", null, 50);
		tpVerzuimherstellen.addColumn("Opmerkingen", null, 300);
		tpVerzuimherstellen.setEventNotifier(new DefaultListFormNotification() {

			@Override
			public void populateTableRequested() {
				try {
					verzuim = ServiceCaller.verzuimFacade(getLoginSession()).getVerzuimDetails(verzuim.getId());
					dtpEinddatumverzuim.setDate(verzuim.getEinddatumverzuim());
				} catch (PermissionException e) {
					ExceptionLogger.ProcessException(e,thisform);
					return;
				} catch (PropertyVetoException e) {
					ExceptionLogger.ProcessException(e,thisform);
					return;
				} catch (VerzuimApplicationException e) {
		        	ExceptionLogger.ProcessException(e,thisform);
		        	return;
				} catch (Exception e1){
		        	ExceptionLogger.ProcessException(e1,thisform);
		        	return;
				}
				getVerzuimDetails();
				displayVerzuimHerstellen();
				tpTodos.setVerzuim(verzuim);
			}

			@Override
			public void newCreated(InfoBase info) {
				VerzuimHerstelInfo herstelinfo = (VerzuimHerstelInfo) info;
				herstelinfo.setVerzuim(verzuim);
			}

			@Override
			public __continue newButtonClicked() {
				if (getMode() == formMode.New) {
					return confirmSave();
				} else if (verzuim.getEinddatumverzuim() != null) {
					JOptionPane.showMessageDialog(thisform,
							"Verzuim is reeds afgesloten");
					return __continue.dontallow;
				}
				return __continue.allow;
			}

			@Override
			public __continue deleteButtonClicked(InfoBase info) {
				try {
					int rslt = JOptionPane.showConfirmDialog(thisform, "Weet u zeker dat u het herstel wilt verwijderen?","",JOptionPane.YES_NO_OPTION);
					if (rslt == JOptionPane.YES_OPTION)
						ServiceCaller.verzuimFacade(getLoginSession()).deleteVerzuimHerstel((VerzuimHerstelInfo) info);
					else
						return __continue.dontallow;
				} catch (PermissionException e) {
					ExceptionLogger.ProcessException(e,thisform);
				} catch (ValidationException e) {
					ExceptionLogger.ProcessException(e,thisform,false);
				} catch (VerzuimApplicationException e) {
		        	ExceptionLogger.ProcessException(e,thisform);
				} catch (EJBException e) {
		        	ExceptionLogger.ProcessException(e,thisform);
				} catch (Exception e1){
		        	ExceptionLogger.ProcessException(e1,thisform);
				}
				return __continue.allow;
			}
		});

		tabbedPane.addTab("Herstellen", null, tpVerzuimherstellen, null);

		tpVerzuimdocumenten = new TablePanel(this.getMdiPanel());
		tpVerzuimdocumenten.getPanelAction().setLocation(5, 146);
		tpVerzuimdocumenten.getScrollPane().setBounds(5, 11, 595, 136);
		tpVerzuimdocumenten.setDetailFormClass(VerzuimDocumentDetail.class,
				VerzuimDocumentInfo.class);
		tpVerzuimdocumenten.addColumn("Document", null, 100);
		tpVerzuimdocumenten.addColumn("Locatie", null, 200);
		tpVerzuimdocumenten.addColumn("datum", null, 80, Date.class);
		tpVerzuimdocumenten.setEventNotifier(new DefaultListFormNotification() {

			@Override
			public void populateTableRequested() {
				try {
					verzuim = ServiceCaller.verzuimFacade(getLoginSession()).getVerzuimDetails(verzuim.getId());
				} catch (PermissionException e) {
					ExceptionLogger.ProcessException(e,thisform);
					return;
				} catch (VerzuimApplicationException e) {
		        	ExceptionLogger.ProcessException(e,thisform);
		        	return;
				} catch (Exception e1){
		        	ExceptionLogger.ProcessException(e1,thisform);
		        	return;
				}
				displayVerzuimDocumenten();
			}

			@Override
			public void newCreated(InfoBase info) {
				VerzuimDocumentInfo docinfo = (VerzuimDocumentInfo) info;
				docinfo.setVerzuimId(verzuim.getId());
				docinfo.setAanmaakdatum(new Date());
				docinfo.setDocumentnaam("");
				docinfo.setPadnaam("");
			}

			@Override
			public __continue newButtonClicked() {
				if (getMode() == formMode.New) {
					return confirmSave();
				} else
					return __continue.allow;
			}

			@Override
			public __continue deleteButtonClicked(InfoBase info) {
				try {
					int rslt = JOptionPane.showConfirmDialog(thisform, "Weet u zeker dat u het document wilt verwijderen?","",JOptionPane.YES_NO_OPTION);
					if (rslt == JOptionPane.YES_OPTION)
						ServiceCaller.verzuimFacade(getLoginSession()).deleteVerzuimDocument((VerzuimDocumentInfo) info);
					else
						return __continue.dontallow;
				} catch (PermissionException e) {
					ExceptionLogger.ProcessException(e,thisform);
				} catch (VerzuimApplicationException e) {
		        	ExceptionLogger.ProcessException(e,thisform);
				} catch (Exception e1){
		        	ExceptionLogger.ProcessException(e1,thisform);
				}
				return __continue.allow;
			}

			@Override
			public void rowDoubleClicked(InfoBase info) {
				VerzuimDocumentInfo doc = (VerzuimDocumentInfo) info;
				URI uri;
				try {

					uri = new URI("File://"
							+ URLEncoder.encode(doc.getPadnaam(), "UTF-8"));
					// uri = new URI("File://" +
					// URLEncoder.encode("C:\\Temp\\Toestemming begraving DNO.docx","UTF-8"));
					open(uri);
				} catch (URISyntaxException e) {
					ExceptionLogger.ProcessException(e,thisform);
				} catch (UnsupportedEncodingException e) {
					ExceptionLogger.ProcessException(e,thisform);
				}
			}
		});

		tabbedPane.addTab("Documenten", null, tpVerzuimdocumenten, null);

		tpVerrichtingen = new TablePanel(this.getMdiPanel());
		tpVerrichtingen.getPanelAction().setLocation(5, 146);
		tpVerrichtingen.getScrollPane().setBounds(5, 0, 595, 148);
		tpVerrichtingen.setDetailFormClass(VerzuimActiviteitDetail.class,
				VerzuimActiviteitInfo.class);
		tpVerrichtingen.addColumn("Activiteit", null, 100);
		tpVerrichtingen.addColumn("uitgevoerd", null, 80, Date.class);
		tpVerrichtingen.addColumn("deadline", null, 80, Date.class);
		tpVerrichtingen.addColumn("door", null, 80);
		tpVerrichtingen.setEventNotifier(new DefaultListFormNotification() {

			@Override
			public void populateTableRequested() {
				try {
					verzuim = ServiceCaller.verzuimFacade(getLoginSession()).getVerzuimDetails(verzuim.getId());
				} catch (PermissionException e) {
					ExceptionLogger.ProcessException(e,thisform);
					return;
				} catch (VerzuimApplicationException e) {
		        	ExceptionLogger.ProcessException(e,thisform);
		        	return;
				} catch (Exception e1){
		        	ExceptionLogger.ProcessException(e1,thisform);
		        	return;
				}
				displayVerzuimVerrichtingen();
			}

			@Override
			public void newCreated(InfoBase info) {
				VerzuimActiviteitInfo actinfo = (VerzuimActiviteitInfo) info;
				actinfo.setVerzuimId(verzuim.getId());
			}

			@Override
			public __continue newButtonClicked() {
				if (getMode() == formMode.New) {
					return confirmSave();
				} else
					return __continue.allow;
			}

			@Override
			public __continue deleteButtonClicked(InfoBase info) {
				try {
					int rslt = JOptionPane.showConfirmDialog(thisform, "Weet u zeker dat u de verrichting wilt verwijderen?","",JOptionPane.YES_NO_OPTION);
					if (rslt == JOptionPane.YES_OPTION)
						ServiceCaller.verzuimFacade(getLoginSession()).deleteVerzuimActiviteit((VerzuimActiviteitInfo) info);
					else
						return __continue.dontallow;
				} catch (PermissionException e) {
					ExceptionLogger.ProcessException(e,thisform);
				} catch (VerzuimApplicationException e) {
		        	ExceptionLogger.ProcessException(e,thisform);
				} catch (Exception e1){
		        	ExceptionLogger.ProcessException(e1,thisform);
				}
				return __continue.allow;
			}

		});

		tabbedPane.addTab("Werkzaamheden", null, tpVerrichtingen, null);

		tpTodos = new TodoPanel(this.getMdiPanel());
		tpTodos.getPanelAction().setSize(540, 30);
		tpTodos.getPanelAction().setLocation(5, 124);
		tpTodos.getScrollPane().setBounds(5, 0, 595, 124);
		tabbedPane.addTab("TODO", null, tpTodos);
		tpTodos.setMyListener(new DefaultListFormNotification() {

			@Override
			public __continue newButtonClicked() {
				if (getMode() == formMode.New) {
					return confirmSave();
				} else
					return __continue.allow;
			}
		});

		tabbedPane.addTab("TODO", null, tpTodos, null);

		JButton btnMedischeKaart = new JButton("Medische kaart...");
		btnMedischeKaart.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				btnMedischeKaartClicked();
			}
		});
		btnMedischeKaart.setBounds(454, 184, 117, 23);
		getContentPane().add(btnMedischeKaart);

		cmbTemplates = new JComboBox<TypeEntry>();
		cmbTemplates.setBounds(268, 441, 254, 20);
		getContentPane().add(cmbTemplates);

		btnGenereer = new JButton("Genereer...");
		btnGenereer.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				btnGenereerClicked(e);
			}
		});
		btnGenereer.setBounds(525, 440, 117, 23);
		getContentPane().add(btnGenereer);

		cmbMeldingswijze = new JComboBox<TypeEntry>();
		cmbMeldingswijze.setBounds(413, 89, 114, 20);
		getContentPane().add(cmbMeldingswijze);

		JLabel lblVia = new JLabel("via:");
		lblVia.setBounds(382, 93, 46, 14);
		getContentPane().add(lblVia);
		
		chckbxUitkeringNaarWerknemer = new JCheckBox("Uitkering naar werknemer");
		chckbxUitkeringNaarWerknemer.setEnabled(false);
		chckbxUitkeringNaarWerknemer.setBounds(26, 209, 149, 23);
		getContentPane().add(chckbxUitkeringNaarWerknemer);
	}

	protected void getVerzuimDetails() {
		try {
			verzuim = ServiceCaller.verzuimFacade(getLoginSession()).getVerzuimDetails(verzuim.getId());
		} catch (PermissionException e) {
			ExceptionLogger.ProcessException(e,this);
		} catch (VerzuimApplicationException e) {
	    	ExceptionLogger.ProcessException(e,this);
		} catch (Exception e1){
        	ExceptionLogger.ProcessException(e1,this);
		}
	}

	private __continue confirmSave() {
		if (JOptionPane.showConfirmDialog(thisform,
				"Het verzuim moet eerst worden opgeslagen. Doorgaan?",
				"Opslaan", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
			if (saveVerzuim()){
				setMode(formMode.Update);
				return __continue.allow;
			}else{
				return __continue.dontallow;
			}
		} else
			return __continue.dontallow;
	}

	protected void btnGenereerClicked(ActionEvent e) {
		TypeEntry document = (TypeEntry) cmbTemplates.getSelectedItem();
		for (DocumentTemplateInfo ti : templates) {
			if (document.getValue() == ti.getId()) {
				WordDocument doc = new WordDocument(thisform, getLoginSession());
				try {
					doc.GenerateDocument(verzuim, ti);
				} catch (IOException e1) {
					ExceptionLogger.ProcessException(e1,this);
				}
				tpVerzuimdocumenten.populateTable_base();
				break;
			}
		}
	}

	protected void cmbCascodeClicked(ActionEvent e) {
		DefaultComboBoxModel<TypeEntry> VangnetModel;
		TypeEntry vangnet;
		TypeEntry cascode;
		if (initialized) {
			cascode = (TypeEntry) cmbCascode.getSelectedItem();
			vangnet = (TypeEntry) cmbVangnet.getSelectedItem();
			for (CascodeInfo cci : cascodes) {
				if (cci.getId() == cascode.getValue()) {
					/* Geselecteerde cascode gevonden */
					if (cci.getVangnettype().getValue() == vangnet.getValue())
						/* Vangnettype staat al goed */;
					else {
						if (vangnet.getValue() == __vangnettype.WIA.getValue()){
							; // Vangnettype laten staan
						} else {
							VangnetModel = (DefaultComboBoxModel<TypeEntry>) cmbVangnet
									.getModel();
							for (int i = 0; i < VangnetModel.getSize(); i++) {
								vangnet = (TypeEntry) VangnetModel.getElementAt(i);
								if (vangnet.getValue() == cci.getVangnettype()
										.getValue()) {
									JOptionPane.showMessageDialog(this,
											"Let op: Vangnettype gewijzigd");
									VangnetModel.setSelectedItem(vangnet);
								}
							}
						}
					}
					break;
				}

			}
		}
	}

	protected void btnMedischeKaartClicked() {
		if (getMode() == formMode.New)
			if (confirmSave() == __continue.dontallow)
				return;

		VerzuimMedischeKaartOverzicht dlgVerzuimMedischeKaart = new VerzuimMedischeKaartOverzicht(
				this.getMdiPanel());
		dlgVerzuimMedischeKaart.setLoginSession(this.getLoginSession());
		dlgVerzuimMedischeKaart.setVisible(true);
		dlgVerzuimMedischeKaart.setInfo(verzuim, gebruikers);
		this.getMdiPanel().add(dlgVerzuimMedischeKaart);
		this.getMdiPanel().setOpaque(true);
		this.getMdiPanel().moveToFront(dlgVerzuimMedischeKaart);
	}

	protected void displayVerzuimVerrichtingen() {
		ActiviteitInfo act = null;
		if (verzuim.getVerzuimactiviteiten() != null) {
			for (VerzuimActiviteitInfo va : verzuim.getVerzuimactiviteiten()) {
				Vector<Object> rowData = new Vector<Object>();
				for (ActiviteitInfo ai : activiteiten)
					if (ai.getId().equals(va.getActiviteitId())) {
						act = ai;
						break;
					}
				if (act == null){
					rowData.add("Onbekend");
				}else{
					rowData.add(act.getNaam());
				}
				rowData.add(va.getDatumactiviteit());
				if (va.getDatumdeadline() == null)
					rowData.add("");
				else
					rowData.add(va.getDatumdeadline());
				for (GebruikerInfo gi : gebruikers)
					if (gi.getId() == va.getUser()) {
						rowData.add(gi.getAchternaam());
						break;
					}

				tpVerrichtingen.addRow(rowData, va);
			}
		}
		tpVerrichtingen.setMdiPanel(this.getMdiPanel());
	}


	protected void displayVerzuimDocumenten() {
		new SimpleDateFormat("yyyy-MM-dd");
		List<RowSorter.SortKey> sortKeys;

		if (verzuim.getVerzuimdocumenten() != null) {
			for (VerzuimDocumentInfo vd : verzuim.getVerzuimdocumenten()) {
				Vector<Object> rowData = new Vector<Object>();
				// rowData.add(vd.getDocumentnaam());
				rowData.add(vd.getDocumentnaam());
				rowData.add(vd.getPadnaam());
				rowData.add(vd.getAanmaakdatum());
				tpVerzuimdocumenten.addRow(rowData, vd);
			}
		}
		tpVerzuimdocumenten.setMdiPanel(this.getMdiPanel());

		sortKeys = new ArrayList<RowSorter.SortKey>();
		sortKeys.add(new RowSorter.SortKey(2, SortOrder.ASCENDING));
		tpVerzuimherstellen.getSorter().setSortKeys(sortKeys);
		tpVerzuimherstellen.getSorter().sort();
	}

	protected void displayVerzuimHerstellen() {
		List<RowSorter.SortKey> sortKeys;

		if (verzuim.getVerzuimherstellen() != null) {
			for (VerzuimHerstelInfo vh : verzuim.getVerzuimherstellen()) {
				Vector<Object> rowData = new Vector<Object>();
				vh.setVerzuim(verzuim);
				rowData.add(vh.getDatumHerstel());
				rowData.add(vh.getPercentageHerstel().toString());
				rowData.add(vh.getPercentageHerstelAT().toString());
				rowData.add(vh.getOpmerkingen());
				tpVerzuimherstellen.addRow(rowData, vh);
			}
		}
		tpVerzuimherstellen.setMdiPanel(this.getMdiPanel());

		sortKeys = new ArrayList<RowSorter.SortKey>();
		sortKeys.add(new RowSorter.SortKey(0, SortOrder.ASCENDING));
		tpVerzuimherstellen.getSorter().setSortKeys(sortKeys);
		tpVerzuimherstellen.getSorter().sort();
	}

	private boolean saveVerzuim(){
		TypeEntry cascode, verzuimtype, vangnettype, gerelateerdheid, meldingswijze;

		cascode = (TypeEntry) cmbCascode.getSelectedItem();
		verzuim.setCascode(cascode.getValue());
		verzuimtype = (TypeEntry) cmbVerzuimtype.getSelectedItem();
		verzuim.setVerzuimtype(__verzuimtype.parse(verzuimtype.getValue()));
		vangnettype = (TypeEntry) cmbVangnet.getSelectedItem();
		verzuim.setVangnettype(__vangnettype.parse(vangnettype.getValue()));
		gerelateerdheid = (TypeEntry) cmbGerelateerdheid.getSelectedItem();
		verzuim.setGerelateerdheid(__gerelateerdheid.parse(gerelateerdheid
				.getValue()));
		meldingswijze = (TypeEntry) cmbMeldingswijze.getSelectedItem();
		verzuim.setMeldingswijze(__meldingswijze.parse(meldingswijze.getValue()));

		verzuim.setStartdatumverzuim(dtpStartdatumverzuim.getDate());
		verzuim.setEinddatumverzuim(dtpEinddatumverzuim.getDate());
		verzuim.setMeldingsdatum(dtpMeldingsdatum.getDate());
		verzuim.setKetenverzuim(chckbxKetenverzuim.isSelected());
		verzuim.setUitkeringnaarwerknemer(chckbxUitkeringNaarWerknemer.isSelected());

		if (chckbxKetenverzuim.isSelected() == false)
			try{
				if (verzuim.checkIsKetenverzuim(verzuim, verzuimen))
					JOptionPane
							.showMessageDialog(
									this,
									"Let op: dit is een ketenverzuim. De Todo's worden berekend\r\n"
											+ "vanaf de verzuimdatum minus het aantal ketenverzuimdagen.");
			} catch (ValidationException e2) {
				ExceptionLogger.ProcessException(e2,this,false);
				return false;
			}

		if (this.getMode() == formMode.New)
			if (verzuim.isFrequentverzuim(verzuim, verzuimen))
				JOptionPane
						.showMessageDialog(this, "Let op: Frequent verzuim.");

		try {
			verzuim.checkOverlap(verzuim, verzuimen);
		} catch (ValidationException e2) {
			ExceptionLogger.ProcessException(e2,this,false);
			return false;
		}

		if (this.getLoginSession() != null) {
			try {
				verzuim.validate();
				switch (this.getMode()) {
				case New:
					VerzuimInfo newverzuim = ServiceCaller.verzuimFacade(getLoginSession()).addVerzuim(verzuim);
					verzuim = ServiceCaller.verzuimFacade(getLoginSession()).getVerzuimDetails(newverzuim.getId());
					this.setMode(formMode.Update);
					return true;
				case Update:
					ServiceCaller.verzuimFacade(getLoginSession()).updateVerzuim(verzuim);
					return true;
				case Delete:
					ServiceCaller.verzuimFacade(getLoginSession()).updateVerzuim(verzuim);
					return true;
				}
			} catch (ValidationException e1) {
				ExceptionLogger.ProcessException(e1,this,false);
			} catch (PermissionException e1) {
				ExceptionLogger.ProcessException(e1,this);
			} catch (VerzuimApplicationException e1) {
	        	ExceptionLogger.ProcessException(e1,this);
			} catch (Exception e1){
	        	ExceptionLogger.ProcessException(e1,this);
			} 
		} else{
			JOptionPane.showMessageDialog(this,
					"Logic error: loginSession is null");
		}
		return false;
	}
	protected void okButtonClicked(ActionEvent e) {
		if (saveVerzuim())
			super.okButtonClicked(e);
	}

	private void open(URI uri) {
		String os = System.getProperty("os.name").toLowerCase();
		try {
			if (os.indexOf("win") >= 0) {
				Runtime.getRuntime().exec(
						"rundll32 url.dll,FileProtocolHandler "
								+ "\""
								+ uri.getScheme()
								+ ":"
								+ URLDecoder.decode(
										uri.getSchemeSpecificPart(), "UTF-8")
								+ "\"");
			} else
				Runtime.getRuntime().exec(
						new String[] { "/usr/bin/open",
								URLDecoder.decode(uri.getPath(), "UTF-8") });
		} catch (IOException e) {
			ExceptionLogger.ProcessException(e,this);
		}
	}
}
