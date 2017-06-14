package com.gieselaar.verzuim.views;

import java.awt.dnd.DropTarget;
import java.awt.event.ActionEvent;
import java.beans.PropertyVetoException;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.gieselaar.verzuim.components.JTextFieldTGI;
import com.gieselaar.verzuim.controllers.AbstractController;
import com.gieselaar.verzuim.controllers.TodoController;
import com.gieselaar.verzuim.controllers.AbstractController.__formmode;
import com.gieselaar.verzuim.controllers.TodoController.__todocommands;
import com.gieselaar.verzuim.controllers.TodoController.__todofields;
import com.gieselaar.verzuim.interfaces.ControllerEventListener;
import com.gieselaar.verzuim.interfaces.DefaultControllerEventListener;
import com.gieselaar.verzuim.interfaces.DefaultDragdropNotification;
import com.gieselaar.verzuim.interfaces.DragDropListener;
import com.gieselaar.verzuim.interfaces.ListFormNotification.__continue;
import com.gieselaar.verzuim.utils.ExceptionLogger;
import com.gieselaar.verzuim.viewsutils.ColorTableModel;
import com.gieselaar.verzuim.viewsutils.CursorController;
import com.gieselaar.verzuim.viewsutils.VerzuimComboBoxModel;
import com.gieselaar.verzuim.controllers.VerzuimController;
import com.gieselaar.verzuim.controllers.VerzuimactiviteitenController;
import com.gieselaar.verzuim.controllers.VerzuimactiviteitenController.__verzuimactiviteitfields;
import com.gieselaar.verzuim.controllers.VerzuimdocumentenController;
import com.gieselaar.verzuim.controllers.VerzuimdocumentenController.__verzuimdocumentfields;
import com.gieselaar.verzuim.controllers.VerzuimherstellenController;
import com.gieselaar.verzuim.controllers.VerzuimherstellenController.__verzuimherstelfields;
import com.gieselaar.verzuimbeheer.exceptions.VerzuimApplicationException;
import com.gieselaar.verzuimbeheer.exceptions.ValidationException;
import com.gieselaar.verzuimbeheer.services.CascodeInfo;
import com.gieselaar.verzuimbeheer.services.GebruikerInfo;
import com.gieselaar.verzuimbeheer.services.InfoBase;
import com.gieselaar.verzuimbeheer.services.TodoFastInfo;
import com.gieselaar.verzuimbeheer.services.VerzuimInfo.__meldingswijze;
import com.gieselaar.verzuimbeheer.services.VerzuimActiviteitInfo;
import com.gieselaar.verzuimbeheer.services.VerzuimDocumentInfo;
import com.gieselaar.verzuimbeheer.services.VerzuimHerstelInfo;
import com.gieselaar.verzuimbeheer.services.VerzuimInfo;
import com.gieselaar.verzuimbeheer.services.VerzuimInfo.__gerelateerdheid;
import com.gieselaar.verzuimbeheer.services.VerzuimInfo.__vangnettype;
import com.gieselaar.verzuimbeheer.services.VerzuimInfo.__verzuimtype;
import com.gieselaar.verzuimbeheer.utils.TypeEntry;
import com.michaelbaranov.microba.calendar.DatePicker;

import javax.swing.JLabel;
import javax.swing.JComboBox;
import javax.swing.JCheckBox;
import javax.swing.JOptionPane;
import javax.swing.JTabbedPane;
import javax.swing.RowSorter;
import javax.swing.SortOrder;
import java.awt.Color;
import javax.swing.JButton;

import java.awt.event.ActionListener;

public class VerzuimDetail extends AbstractDetail {

	private static final long serialVersionUID = 1L;

	private List<CascodeInfo> cascodes;
	private VerzuimInfo verzuim;
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

	private DatatablePanel tpVerzuimherstellen;
	private DatatablePanel tpVerzuimdocumenten;
	private DatatablePanel tpVerrichtingen;
	private DatatablePanel tpTodos;

	private TodoController todocontroller = null;
	private VerzuimherstellenController verzuimherstellencontroller = null;
	private VerzuimdocumentenController verzuimdocumentencontroller = null;
	private VerzuimactiviteitenController verzuimactiviteitencontroller = null;

	private AbstractDetail thisform = this;
	private boolean initialized = false;

	private VerzuimController verzuimcontroller;

	private static final String PANELNAMEVERRICHTINGEN = "Verrichtingen";
	private static final String PANELNAMETODOS = "TODO";
	private static final String PANELNAMEVERZUIMHERSTELLEN = "Herstellen";
	private static final String PANELNAMEVERZUIMDOCUMENTEN = "Documenten";

	/**
	 * Create the frame.
	 */
	public VerzuimDetail(AbstractController controller) {
		super("Verzuim", controller);
		verzuimcontroller = (VerzuimController) super.controller;
		initialize();

	}

	@Override
	public void setData(InfoBase info) {
		verzuim = (VerzuimInfo) info;
		refreshComboCascodes();
		displayVerzuim();
	}

	public void displayVerzuim() {
		List<GebruikerInfo> gebruikers;
		VerzuimComboBoxModel vangnetModel;
		VerzuimComboBoxModel verzuimtypeModel;
		VerzuimComboBoxModel gerelateerdheidModel;
		VerzuimComboBoxModel meldingswijzeModel;

		cascodes = this.controller.getMaincontroller().getCascodes();
		gebruikers = this.controller.getMaincontroller().getGebruikers();

		cmbVangnet.setModel(controller.getMaincontroller().getEnumModel(__vangnettype.class));
		vangnetModel = (VerzuimComboBoxModel) cmbVangnet.getModel();
		vangnetModel.setId(verzuim.getVangnettype().getValue());

		cmbVerzuimtype.setModel(controller.getMaincontroller().getEnumModel(__verzuimtype.class));
		verzuimtypeModel = (VerzuimComboBoxModel) cmbVerzuimtype.getModel();
		verzuimtypeModel.setId(verzuim.getVerzuimtype().getValue());

		cmbGerelateerdheid.setModel(controller.getMaincontroller().getEnumModel(__gerelateerdheid.class));
		gerelateerdheidModel = (VerzuimComboBoxModel) cmbGerelateerdheid.getModel();
		gerelateerdheidModel.setId(verzuim.getGerelateerdheid().getValue());

		cmbMeldingswijze.setModel(controller.getMaincontroller().getEnumModel(__meldingswijze.class));
		meldingswijzeModel = (VerzuimComboBoxModel) cmbMeldingswijze.getModel();
		if (verzuim.getMeldingswijze() != null) {
			meldingswijzeModel.setId(verzuim.getMeldingswijze().getValue());
		}

		((VerzuimComboBoxModel)cmbCascode.getModel()).setId(verzuim.getCascode());
		
		try {
			dtpStartdatumverzuim.setDate(verzuim.getStartdatumverzuim());
			dtpEinddatumverzuim.setDate(verzuim.getEinddatumverzuim());
			dtpMeldingsdatum.setDate(verzuim.getMeldingsdatum());
		} catch (PropertyVetoException e) {
			ExceptionLogger.ProcessException(e, this);
		}
		chckbxKetenverzuim.setSelected(verzuim.getKetenverzuim());
		for (GebruikerInfo gi : gebruikers)
			if (gi.getId() == verzuim.getGebruiker()) {
				txtUsername.setText(gi.getAchternaam());
				break;
			}

		chckbxUitkeringNaarWerknemer.setSelected(verzuim.isUitkeringnaarwerknemer());
		try {
			verzuimherstellencontroller.selectVerzuimherstellen(verzuim);
			verzuimdocumentencontroller.selectVerzuimdocumenten(verzuim);
			verzuimactiviteitencontroller.selectVerzuimactiviteiten(verzuim);
			todocontroller.selectTodosVerzuim(verzuim.getId());
		} catch (VerzuimApplicationException e) {
			ExceptionLogger.ProcessException(e, this);
		}
		initialized = true;
	}

	protected void refreshCascodes() {
		controller.getMaincontroller().updateComboModelCascodes((VerzuimComboBoxModel)cmbCascode.getModel(), true);
	}

	void initialize() {
		setBounds(50, 50, 669, 524);

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

		addComboCascodes();

		JLabel lblCascode = new JLabel("Cascode:");
		lblCascode.setLabelFor(cmbCascode);
		lblCascode.setBounds(26, 116, 76, 14);
		getContentPane().add(lblCascode);

		chckbxKetenverzuim = new JCheckBox("Ketenverzuim");
		chckbxKetenverzuim.setEnabled(false);
		chckbxKetenverzuim.setBounds(255, 41, 97, 23);
		getContentPane().add(chckbxKetenverzuim);

		cmbVangnet = new JComboBox<>();
		cmbVangnet.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				TypeEntry vt = (TypeEntry) cmbVangnet.getSelectedItem();
				__vangnettype type = __vangnettype.parse(vt.getValue());
				if (type == __vangnettype.NVT) {
					chckbxUitkeringNaarWerknemer.setEnabled(false);
					chckbxUitkeringNaarWerknemer.setSelected(false);
				} else {
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

		cmbGerelateerdheid = new JComboBox<>();
		cmbGerelateerdheid.setBounds(159, 185, 254, 20);
		getContentPane().add(cmbGerelateerdheid);

		cmbVerzuimtype = new JComboBox<>();
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
		addTabVerzuimherstellen(tabbedPane);
		addTabVerzuimdocumenten(tabbedPane);
		addTabVerrichtingen(tabbedPane);
		addTabTodos(tabbedPane);

		addDragAndDropListener();

		JButton btnMedischeKaart = new JButton("Medische kaart...");
		btnMedischeKaart.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				btnMedischeKaartClicked();
			}
		});
		btnMedischeKaart.setBounds(454, 184, 117, 23);
		getContentPane().add(btnMedischeKaart);

		addComboTemplates();

		JButton btnGenereer = new JButton("Genereer...");
		btnGenereer.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				btnGenereerClicked(e);
			}
		});
		btnGenereer.setBounds(525, 440, 117, 23);
		getContentPane().add(btnGenereer);

		cmbMeldingswijze = new JComboBox<>();
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

	private void addDragAndDropListener() {
		DragDropListener listener = new DragDropListener();
		listener.setDragDropNotification(new DefaultDragdropNotification() {
			@Override
			public void fileDropped(File droppedfile) {
				if ((thisform).getFormmode() == __formmode.NEW) {
					/* drop not allowed in mode NEW */
				} else {
					verzuimcontroller.fileDropped(droppedfile);
				}
			}
		});
		new DropTarget(thisform, listener);
	}

	private void addTabTodos(JTabbedPane tabbedPane) {
		try {
			todocontroller = verzuimcontroller.getTodoController();
		} catch (VerzuimApplicationException e) {
			ExceptionLogger.ProcessException(e, this);
		}
		tpTodos = new DatatablePanel(todocontroller);
		tpTodos.setName(PANELNAMETODOS);
		tpTodos.addColumn(__todofields.INDICATOR.getValue(), "", 20);
		tpTodos.addColumn(__todofields.ACTIVITEIT.getValue(), "Activiteit", 100);
		tpTodos.addColumn(__todofields.DEADLINE.getValue(), "Deadline", 80, Date.class);
		tpTodos.addColumn(__todofields.WAARSCHUWEN.getValue(), "Waarschuwen", 80, Date.class);
		tpTodos.addColumn(__todofields.HERHALEN.getValue(), "Herhalen", 60);

		JCheckBox chckbxToekomstigeTonen = new JCheckBox("Toekomstige tonen");
		chckbxToekomstigeTonen.setSelected(false);
		chckbxToekomstigeTonen.setBounds(297, 422, 104, 23);
		chckbxToekomstigeTonen.setActionCommand(__todocommands.TODOTOEKOMSTIGETONEN.toString());
		chckbxToekomstigeTonen.addActionListener(CursorController.createListener(this, todocontroller));
		tpTodos.getPanelAction().add(chckbxToekomstigeTonen);

		JCheckBox chckbxAfgeslotenTonen = new JCheckBox("Afgesloten tonen");
		chckbxAfgeslotenTonen.setBounds(403, 422, 133, 23);
		chckbxAfgeslotenTonen.setActionCommand(__todocommands.TODOAFGESLOTENTONEN.toString());
		chckbxAfgeslotenTonen.addActionListener(CursorController.createListener(this, todocontroller));
		tpTodos.getPanelAction().add(chckbxAfgeslotenTonen);

		tabbedPane.addTab("TODO", null, tpTodos);

		ControllerEventListener listener = new DefaultControllerEventListener() {
			@Override
			public void refreshTable() {
				/* Save and remove sort before populating the table */
				tpTodos.disableSorter();

				List<TodoFastInfo> todos = todocontroller.getTodoList();
				TodoFastInfo.sort(todos, TodoFastInfo.__sortcol.DATUMDEADLINE);
				todocontroller.getTableModel(todos, (ColorTableModel) tpTodos.getTable().getModel(),
						tpTodos.colsinview);

				/* Add sort again */
				tpTodos.enableSorter();
			}
		};
		this.registerControllerListener(todocontroller, listener);
	}

	private void addTabVerrichtingen(JTabbedPane tabbedPane) {
		try {
			verzuimactiviteitencontroller = verzuimcontroller.getVerzuimactiviteitenController();
		} catch (VerzuimApplicationException e) {
			ExceptionLogger.ProcessException(e, this);
		}
		tpVerrichtingen = new DatatablePanel(verzuimactiviteitencontroller);
		tpVerrichtingen.setName(PANELNAMEVERRICHTINGEN);
		tpVerrichtingen.addColumn(__verzuimactiviteitfields.ACTIVITEIT.getValue(),"Activiteit", 100);
		tpVerrichtingen.addColumn(__verzuimactiviteitfields.DATUM.getValue(),"uitgevoerd", 80, Date.class);
		tpVerrichtingen.addColumn(__verzuimactiviteitfields.DEADLINE.getValue(),"deadline", 80, Date.class);
		tpVerrichtingen.addColumn(__verzuimactiviteitfields.USER.getValue(),"door", 80);
		tabbedPane.addTab("Verrichtingen", null, tpVerrichtingen);

		ControllerEventListener listener = new DefaultControllerEventListener() {
			@Override
			public void refreshTable() {
				/* Save and remove sort before populating the table */
				tpVerrichtingen.disableSorter();

				List<VerzuimActiviteitInfo> activiteiten = verzuimactiviteitencontroller.getVerzuimactiviteitenList();
				verzuimactiviteitencontroller.getTableModel(activiteiten,
						(ColorTableModel) tpVerrichtingen.getTable().getModel(), tpVerrichtingen.colsinview);

				/* Add sort again */
				tpVerrichtingen.enableSorter();
			}
		};
		this.registerControllerListener(verzuimactiviteitencontroller, listener);

		List<RowSorter.SortKey> sortKeys = new ArrayList<>();
		sortKeys.add(new RowSorter.SortKey(0, SortOrder.ASCENDING));
		tpVerrichtingen.getSorter().setSortKeys(sortKeys);
		tpVerrichtingen.getSorter().sort();
		
	}

	private void addTabVerzuimdocumenten(JTabbedPane tabbedPane) {
		try {
			verzuimdocumentencontroller = verzuimcontroller.getVerzuimdocumentenController();
		} catch (VerzuimApplicationException e) {
			ExceptionLogger.ProcessException(e, this);
		}
		tpVerzuimdocumenten = new DatatablePanel(verzuimdocumentencontroller);
		tpVerzuimdocumenten.setName(PANELNAMEVERZUIMDOCUMENTEN);
		tpVerzuimdocumenten.addColumn(__verzuimdocumentfields.DOCUMENT.getValue(),"Document", 100);
		tpVerzuimdocumenten.addColumn(__verzuimdocumentfields.LOCATIE.getValue(),"Locatie", 200);
		tpVerzuimdocumenten.addColumn(__verzuimdocumentfields.DATUM.getValue(),"datum", 80, Date.class);
		tabbedPane.addTab("Documenten", null, tpVerzuimdocumenten);

		ControllerEventListener listener = new DefaultControllerEventListener() {
			@Override
			public void refreshTable() {
				/* Save and remove sort before populating the table */
				tpVerzuimdocumenten.disableSorter();

				List<VerzuimDocumentInfo> documenten = verzuimdocumentencontroller.getVerzuimdocumentList();
				verzuimdocumentencontroller.getTableModel(documenten,
						(ColorTableModel) tpVerzuimdocumenten.getTable().getModel(), tpVerzuimdocumenten.colsinview);

				/* Add sort again */
				tpVerzuimdocumenten.enableSorter();
			}
		};
		this.registerControllerListener(verzuimdocumentencontroller, listener);

		List<RowSorter.SortKey> sortKeys = new ArrayList<>();
		sortKeys.add(new RowSorter.SortKey(2, SortOrder.ASCENDING));
		tpVerzuimdocumenten.getSorter().setSortKeys(sortKeys);
		tpVerzuimdocumenten.getSorter().sort();
	}

	private void addTabVerzuimherstellen(JTabbedPane tabbedPane) {
		try {
			verzuimherstellencontroller = verzuimcontroller.getVerzuimherstellenController();
		} catch (VerzuimApplicationException e) {
			ExceptionLogger.ProcessException(e, this);
		}
		tpVerzuimherstellen = new DatatablePanel(verzuimherstellencontroller);
		tpVerzuimherstellen.setName(PANELNAMEVERZUIMHERSTELLEN);
		tpVerzuimherstellen.addColumn(__verzuimherstelfields.DATUMHERSTEL.getValue(), "Datum", 80, Date.class);
		tpVerzuimherstellen.addColumn(__verzuimherstelfields.PERCENTAGEHERSTEL.getValue(), "% Herstel", 70);
		tpVerzuimherstellen.addColumn(__verzuimherstelfields.PERCENTAGEHERSTELAT.getValue(), "% Herstel AT", 50);
		tpVerzuimherstellen.addColumn(__verzuimherstelfields.OPMERKINGEN.getValue(), "Opmerkingen", 300);

		tabbedPane.addTab("Herstellen", null, tpVerzuimherstellen);

		ControllerEventListener listener = new DefaultControllerEventListener() {
			@Override
			public void refreshTable() {
				/* Save and remove sort before populating the table */
				tpVerzuimherstellen.disableSorter();

				List<VerzuimHerstelInfo> herstellen = verzuimherstellencontroller.getVerzuimherstelList();
				VerzuimHerstelInfo.sort(herstellen, VerzuimHerstelInfo.__sortcol.HERSTELDATUM);
				verzuimherstellencontroller.getTableModel(herstellen,
						(ColorTableModel) tpVerzuimherstellen.getTable().getModel(), tpVerzuimherstellen.colsinview);

				/* Add sort again */
				tpVerzuimherstellen.enableSorter();
			}
		};
		this.registerControllerListener(verzuimherstellencontroller, listener);
		
		List<RowSorter.SortKey> sortKeys = new ArrayList<>();
		sortKeys.add(new RowSorter.SortKey(0, SortOrder.ASCENDING));
		tpVerzuimherstellen.getSorter().setSortKeys(sortKeys);
		tpVerzuimherstellen.getSorter().sort();
	}

	private __continue confirmSave() {
		if (JOptionPane.showConfirmDialog(thisform, "Het verzuim moet eerst worden opgeslagen. Doorgaan?", "Opslaan",
				JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
			if (saveVerzuim()) {
				this.setFormmode(__formmode.UPDATE);
				return __continue.allow;
			} else {
				return __continue.dontallow;
			}
		} else
			return __continue.dontallow;
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
		cmbTemplates.setBounds(268, 441, 254, 20);

		getContentPane().add(cmbTemplates);
		refreshComboTemplates();
	}

	private void refreshComboTemplates() {
		verzuimcontroller.getMaincontroller()
				.updateComboModelDocumentTemplates((VerzuimComboBoxModel) cmbTemplates.getModel());
	}
	private void addComboCascodes() {
		cmbCascode = new JComboBox<>();
		cmbCascode.setModel(new VerzuimComboBoxModel(controller.getMaincontroller()));
		DefaultControllerEventListener listener = new DefaultControllerEventListener(){
			@Override
			public void refreshTable() {
				refreshCascodes();
			}
		};
		registerControllerListener(controller.getMaincontroller(), listener);
		cmbCascode.setBounds(159, 113, 254, 20);
		cmbCascode.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				cmbCascodeClicked(e);
			}
		});

		getContentPane().add(cmbCascode);
	}

	private void refreshComboCascodes() {
		initialized = false;
		verzuimcontroller.getMaincontroller()
				.updateComboModelCascodes((VerzuimComboBoxModel) cmbCascode.getModel(), true, verzuim.getCascode());
		initialized = true;
	}

	protected void btnGenereerClicked(ActionEvent e) {
		Integer documentid = ((VerzuimComboBoxModel) cmbTemplates.getModel()).getId();
		controller.genereerDocument(verzuim, documentid);
	}

	protected void cmbCascodeClicked(ActionEvent e) {
		VerzuimComboBoxModel vangnetModel;
		int vangnet;
		Integer cascode = null;
		if (initialized) {
			cascode = ((VerzuimComboBoxModel) cmbCascode.getModel()).getId();
			vangnetModel = (VerzuimComboBoxModel) cmbVangnet.getModel();
			vangnet = vangnetModel.getId();
			for (CascodeInfo cci : cascodes) {
				if (cci.getId().equals(cascode)) {
					/* Geselecteerde cascode gevonden */
					if (cci.getVangnettype().getValue() == vangnet){
						/* Vangnettype staat al goed */
					} else {
						if (vangnet == __vangnettype.WIA.getValue()) {
							/* Vangnettype laten staan */
						} else {
							for (int i = 0; i < vangnetModel.getSize(); i++) {
								vangnet = vangnetModel.getElementAt(i).getValue();
								if (vangnet == cci.getVangnettype().getValue()) {
									JOptionPane.showMessageDialog(this, "Let op: Vangnettype gewijzigd");
									vangnetModel.setId(vangnet);
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
		if (this.getFormmode() == __formmode.NEW && confirmSave() == __continue.dontallow)
			return;

		verzuimcontroller.openMedischekaart(this,__formmode.UPDATE);
	}

	private boolean saveVerzuim() {
		verzuim = (VerzuimInfo) collectData();

		try {
			verzuim.validate();
			switch (this.getFormmode()) {
			case NEW:
				verzuimcontroller.addData(verzuim);
				return true;
			case UPDATE:
				verzuimcontroller.saveData(verzuim);
				return true;
			default:
				break;
			}
		} catch (ValidationException | VerzuimApplicationException e) {
			ExceptionLogger.ProcessException(e, thisform);
		}
		return false;
	}


	@Override
	public InfoBase collectData() {
		verzuim.setCascode(((VerzuimComboBoxModel) cmbCascode.getModel()).getId());
		verzuim.setVerzuimtype(__verzuimtype.parse(((VerzuimComboBoxModel) cmbVerzuimtype.getModel()).getId()));
		verzuim.setVangnettype(__vangnettype.parse(((VerzuimComboBoxModel) cmbVangnet.getModel()).getId()));
		verzuim.setGerelateerdheid(
				__gerelateerdheid.parse(((VerzuimComboBoxModel) cmbGerelateerdheid.getModel()).getId()));
		verzuim.setMeldingswijze(__meldingswijze.parse(((VerzuimComboBoxModel) cmbMeldingswijze.getModel()).getId()));

		verzuim.setStartdatumverzuim(dtpStartdatumverzuim.getDate());
		verzuim.setEinddatumverzuim(dtpEinddatumverzuim.getDate());
		verzuim.setMeldingsdatum(dtpMeldingsdatum.getDate());
		verzuim.setKetenverzuim(chckbxKetenverzuim.isSelected());
		verzuim.setUitkeringnaarwerknemer(chckbxUitkeringNaarWerknemer.isSelected());
		return verzuim;
	}
//	@Override
//	public void refreshTable() {
//		try {
//			verzuimcontroller.refreshDatabase();
//		} catch (VerzuimApplicationException e) {
//			ExceptionLogger.ProcessException(e, this);
//		}
//	}
}
