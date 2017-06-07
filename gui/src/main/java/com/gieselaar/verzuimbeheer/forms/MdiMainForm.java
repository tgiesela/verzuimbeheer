package com.gieselaar.verzuimbeheer.forms;
import java.awt.EventQueue;
import java.awt.Toolkit;
import java.awt.Window;

import javax.swing.JFrame;

import java.awt.BorderLayout;

import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import com.gieselaar.verzuimbeheer.baseforms.JDesktopPaneTGI;
import com.gieselaar.verzuimbeheer.facades.AutorisatieFacadeRemote.__applicatiefunctie;
import com.gieselaar.verzuimbeheer.facades.LoginSessionRemote;
import com.gieselaar.verzuimbeheer.importuren.AfsluitenDienstverbanden;
import com.gieselaar.verzuimbeheer.importuren.ImportUren;
import com.gieselaar.verzuimbeheer.services.WerknemerFastInfo;
import com.gieselaar.verzuimbeheer.utils.CursorController;
import com.gieselaar.verzuimbeheer.utils.EventQueueProxy;
import com.gieselaar.verzuimbeheer.utils.ExceptionLogger;
import com.gieselaar.verzuimbeheer.utils.RemoteInterfaces;
import com.gieselaar.verzuimbeheer.utils.ServiceLocator;
import com.gieselaar.verzuimbeheer.utils.ServiceLocatorException;
import com.gieselaar.verzuimbeheer.utils.VerzuimAuthorisation;
//import com.gieselaar.verzuimbeheer.utils.ServiceLocator.remoteinfo;



import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.SystemColor;
import javax.swing.JSeparator;

public class MdiMainForm implements WindowListener {

	private JFrame frame;
	private WerkgeverOverzicht dlgWerkgeverOverzicht;
	private WerknemerOverzicht dlgWerknemerOverzicht;
	private PakketOverzicht dlgPakketOverzicht;
	private ActiviteitOverzicht dlgActiviteitOverzicht;
	private GebruikerOverzicht dlgGebruikerOverzicht;
	private RolOverzicht dlgRollenOverzicht;
	private ApplicatieFunctieOverzicht dlgApplFuncOverzicht;
	private CascodeGroepOverzicht dlgCascodeGroepOverzicht;
	private CascodeOverzicht dlgCascodeOverzicht;
	private TodoOverzicht dlgTodoOverzicht;
	private DocumentTemplateOverzicht dlgTemplateOverzicht;
	private ArbodienstOverzicht dlgArbodienstOverzicht;
	private UitkeringsinstantieOverzicht dlgUitkeringsinstantieOverzicht;
	private ReportFrequentVerzuimers dlgReportFrequentVerzuimers;
	private ReportVerzuimoverzicht dlgReportVerzuimOverzicht;
	private ReportVerzuimenHistorie dlgReportVerzuimHistorie;
	private ReportActueelVerzuim dlgReportActueelVerzuim;
	private HoldingOverzicht dlgHoldingOverzicht;
	private WerknemerDetail dlgWerknemer;
	private BedrijfsgegevensDetail dlgBedrijfsgegevens;
	private OeNiveauOverzicht dlgRapportageNiveaus;
	private OeOverzicht dlgRapportageStructuur;
	private TariefOverzicht dlgTariefOverzicht;
	private BtwOverzicht dlgBtwOverzicht;
	private FactuurOverzicht dlgFactuurOverzicht;
	private FactuurkopOverzicht dlgFactuurkoppenOverzicht;
	private FactuurcategorieOverzicht dlgFactuurcategorienOverzicht;
	private FactuuritemOverzicht dlgFactuuritemOverzicht;
	private WerkzaamhedenOverzicht dlgWerkzaamhedenOverzicht;
	private AfsluitenMaandDialog dlgAfsluitenMaand;
	private TerugdraaienMaandDialog dlgTerugdraaienMaand;
	private AfdrukkenMaandDialog dlgAfdrukkenMaand;
	private ReportBTWOmzet dlgReportBTWOmzet;
	private ImportUren dlgImportUren;
	private AfsluitenDienstverbanden dlgAfsluitenDienstverbanden;
	private ImportBetalingen dlgImportbetalingen;
	private ImportWerknemers dlgImportwerknemers;
	private LoginDialog dlgLogin;
	private LoginSessionRemote session = null;
	private JDesktopPaneTGI mdiPanel = null;
	private SettingsDetail dlgSettings;
	
	/** 
	 * Global menu-items
	 */
	private JMenuItem mntmTarieven;
	private boolean accesstotarieven;
	private JMenu mnFacturen;
	private boolean accesstofacturen;
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				MdiMainForm window = new MdiMainForm();
				try {
					/*
					 * To prevent SSL errors when using STARTTLS, the truststore must be
					 * set. If not set before jndi context.lookup, the SSL libraries will
					 * fail with strange errors about trustAnchors not beeing set. 
					 */
					String javahome = System.getProperty("java.home");
					System.setProperty("javax.net.ssl.trustStore",javahome + "\\lib\\security\\cacerts");
					
					window.Login();
					window.frame.setVisible(true);
				} catch (Exception e) {
					ExceptionLogger.ProcessException(e, null, e.getMessage());
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public MdiMainForm() {
		initialize();
        try {
			session   = (LoginSessionRemote) ServiceLocator.getInstance().getRemoteHome (
					RemoteInterfaces.LoginSession.getValue(),LoginSessionRemote.class);
        } catch (ServiceLocatorException e1) {
        	ExceptionLogger.ProcessException(e1,frame,"Can't create LoginSession");
		}  
	}
	public void Login(){
		dlgLogin = new LoginDialog(this.frame, true);
		dlgLogin.setLoginSession(session);
		dlgLogin.setVisible(true);
		if (dlgLogin.getResult() == true)
		{
			dlgLogin.dispose();
			this.frame.setTitle("De Vos Verzuimbeheer - Verzuim Administratie ");
			accesstotarieven = VerzuimAuthorisation.isAuthorised(session.getGebruiker(), __applicatiefunctie.RAADPLEGENTARIEVEN);
			if (accesstotarieven){
				mntmTarieven.setEnabled(true);
			}else{
				mntmTarieven.setEnabled(false);
			}
			accesstofacturen = VerzuimAuthorisation.isAuthorised(session.getGebruiker(), __applicatiefunctie.RAADPLEGENFACTUREN);
			if (accesstofacturen){
				mnFacturen.setEnabled(true);
			}else{
				mnFacturen.setEnabled(false);
			}
		}
		else
		{
			JOptionPane.showMessageDialog(this.frame, "Login failure");
			dlgLogin.dispose();
			this.frame.setVisible(false);
			this.frame.dispose();
			System.exit(0);
		}
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		String laf = UIManager.getSystemLookAndFeelClassName();
		try {
			UIManager.setLookAndFeel(laf);
		} catch (ClassNotFoundException e1) {
			e1.printStackTrace();
		} catch (InstantiationException e1) {
			e1.printStackTrace();
		} catch (IllegalAccessException e1) {
			e1.printStackTrace();
		} catch (UnsupportedLookAndFeelException e1) {
			e1.printStackTrace();
		}
		EventQueue queue = Toolkit.getDefaultToolkit().getSystemEventQueue();
		queue.push(new EventQueueProxy());
		frame = new JFrame();
		frame.setBounds(100, 100, 1087, 752);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		JMenuBar menuBar = new JMenuBar();
		frame.getContentPane().add(menuBar, BorderLayout.NORTH);
		frame.addWindowListener(this);
		
		JMenu mnApplicatie = new JMenu("Applicatie");
		menuBar.add(mnApplicatie);
		
		JMenuItem mntmAfsluiten = new JMenuItem("Afsluiten");
		mntmAfsluiten.addActionListener(CursorController.createListener(frame, new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				menuAfsluitenClicked(e);
			}
		}));
		
		JMenuItem mntmSettings = new JMenuItem("Settings...");
		mntmSettings.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				menuSettingsClicked(e);
			}
		});
		mnApplicatie.add(mntmSettings);
		mnApplicatie.add(mntmAfsluiten);
		
		JMenu mnWerkgever = new JMenu("Werkgever");
		menuBar.add(mnWerkgever);
		
		JMenuItem mntmOverzicht_1 = new JMenuItem("Overzicht...");
		mntmOverzicht_1.addActionListener(CursorController.createListener(frame, new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				menuWerkgeverOverzichtclicked(e);
			}
		}));
		mnWerkgever.add(mntmOverzicht_1);
		
		JMenuItem mntmTodo = new JMenuItem("Todo...");
		mntmTodo.addActionListener(CursorController.createListener(frame, new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				menuTodoOverzichtClicked(e);
			}
		}));
		mnWerkgever.add(mntmTodo);
		
		JMenuItem mntmHolding = new JMenuItem("Holding...");
		mntmHolding.addActionListener(CursorController.createListener(frame, new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				menuHoldingClicked(e);
			}
		}));
		mnWerkgever.add(mntmHolding);
		
		JMenuItem mntmRapportageNiveaus = new JMenuItem("Rapportage niveaus...");
		mntmRapportageNiveaus.addActionListener(CursorController.createListener(frame, new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				menuRapportageNiveauClicked(e);
			}
		}));
		
		mntmTarieven = new JMenuItem("Tarieven...");
		mntmTarieven.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				menuTarievenClicked(e);
			}
		});
		mntmTarieven.setEnabled(false);
		mnWerkgever.add(mntmTarieven);
		mnWerkgever.add(mntmRapportageNiveaus);
		
		JMenuItem mntmRapportageStructuur = new JMenuItem("Rapportage structuur...");
		mntmRapportageStructuur.addActionListener(CursorController.createListener(frame, new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				menuRapportageStructuur(e);
			}
		}));
		mnWerkgever.add(mntmRapportageStructuur);
		
		JMenuItem mntmImporteerWerknemers = new JMenuItem("Importeer werknemers...");
		mntmImporteerWerknemers.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				menuImporteerWerknemersClicked(e);
			}
		});
		mnWerkgever.add(mntmImporteerWerknemers);
		
		JMenu mnKfcSpecials = new JMenu("KFC specials");
		mnWerkgever.add(mnKfcSpecials);
		
		JMenuItem mntmImporteerUren = new JMenuItem("Importeer uren...");
		mnKfcSpecials.add(mntmImporteerUren);
		mntmImporteerUren.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				menuImporteerUren(e);
			}
		});
		
		JMenuItem mntmAfsluitenDienstverbanden = new JMenuItem("Afsluiten dienstverbanden... ");
		mnKfcSpecials.add(mntmAfsluitenDienstverbanden);
		mntmAfsluitenDienstverbanden.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				menuAfsluitenDienstverbanden(e);
			}
		});
		
		JMenu mnWerknemer = new JMenu("Werknemer");
		menuBar.add(mnWerknemer);
		
		JMenuItem mntmNieuw = new JMenuItem("Nieuw...");
		mntmNieuw.addActionListener(CursorController.createListener(frame, new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				menuWerknemerNieuw(e);
			}
		}));
		mnWerknemer.add(mntmNieuw);
		
		JMenuItem mntmOverzicht = new JMenuItem("Overzicht...");
		mntmOverzicht.addActionListener(CursorController.createListener(frame, new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				menuWerknemerOverzichtClicked(e);
			}
		}));
		mnWerknemer.add(mntmOverzicht);
		
		JMenu mnTabellen = new JMenu("Tabellen");
		menuBar.add(mnTabellen);
		
		JMenuItem mntmPaketten = new JMenuItem("Paketten...");
		mntmPaketten.addActionListener(CursorController.createListener(frame, new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				menuPakkettenPakkettenclicked(e);
			}
		}));
		mnTabellen.add(mntmPaketten);
		
		JMenuItem mntmActiviteiten = new JMenuItem("Activiteiten...");
		mntmActiviteiten.addActionListener(CursorController.createListener(frame, new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				menuActiviteitenClicked(e);
			}
		}));
		mnTabellen.add(mntmActiviteiten);
		
		JMenuItem mntmCascodegroepen = new JMenuItem("Cascodegroepen...");
		mntmCascodegroepen.addActionListener(CursorController.createListener(frame, new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				menuCascodeGroepenClicked(e);
			}
		}));
		mnTabellen.add(mntmCascodegroepen);
		
		JMenuItem mntmCascodes = new JMenuItem("Cascodes...");
		mntmCascodes.addActionListener(CursorController.createListener(frame, new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				menuCascodesClicked(e);
			}
		}));
		mnTabellen.add(mntmCascodes);
		
		JMenuItem mntmDocumentTemplates = new JMenuItem("Document templates...");
		mntmDocumentTemplates.addActionListener(CursorController.createListener(frame, new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				menuDocumentTemplatesClicked(e);
			}
		}));
		mnTabellen.add(mntmDocumentTemplates);
		
		JMenu mnInstellingen = new JMenu("Instellingen");
		menuBar.add(mnInstellingen);
		
		JMenuItem mntmArbodiensten_1 = new JMenuItem("Arbodiensten...");
		mntmArbodiensten_1.addActionListener(CursorController.createListener(frame, new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				menuArbodienstenClicked(e);
			}
		}));
		mnInstellingen.add(mntmArbodiensten_1);
		
		JMenuItem mntmUitkeringsinstantie = new JMenuItem("Uitvoeringsinstanties...");
		mntmUitkeringsinstantie.addActionListener(CursorController.createListener(frame, new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				menuUitkeringsinstantieClicked(e);
			}
		}));
		mnInstellingen.add(mntmUitkeringsinstantie);
		
		JMenuItem mntmBedrijfsgegevens = new JMenuItem("Bedrijfsgegevens...");
		mntmBedrijfsgegevens.addActionListener(CursorController.createListener(frame, new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				menuBedrijfsgegevensClicked(e);
			}
		}));
		mnInstellingen.add(mntmBedrijfsgegevens);
		
		JMenu mnAutorisatie = new JMenu("Autorisatie");
		menuBar.add(mnAutorisatie);
		
		JMenuItem mntmGebruikers = new JMenuItem("Gebruikers...");
		mntmGebruikers.addActionListener(CursorController.createListener(frame, new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				menuGebruikersClicked(e);
			}
		}));
		mnAutorisatie.add(mntmGebruikers);
		
		JMenuItem mntmRollen = new JMenuItem("Rollen...");
		mntmRollen.addActionListener(CursorController.createListener(frame,new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				menuRollenClicked(e);
			}
		}));
		mnAutorisatie.add(mntmRollen);
		
		JMenuItem mntmApplicatiefuncties = new JMenuItem("Applicatiefuncties...");
		mntmApplicatiefuncties.addActionListener(CursorController.createListener(frame,new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				menuApplicatiefunctiesClicked(e);
			}
		}));
		mnAutorisatie.add(mntmApplicatiefuncties);
		
		JMenu mnNewMenu = new JMenu("Reports");
		menuBar.add(mnNewMenu);
		
		JMenuItem mntmVerzuimoverzicht = new JMenuItem("Verzuimoverzicht...");
		mntmVerzuimoverzicht.addActionListener(CursorController.createListener(frame,new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				menuVerzuimoverzichtClicked(e);
			}
		}));
		mnNewMenu.add(mntmVerzuimoverzicht);
		
		JMenuItem mntmFrequentVerzuimers = new JMenuItem("Frequent verzuimers...");
		mntmFrequentVerzuimers.addActionListener(CursorController.createListener(frame,new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				menuFrequentVerzuimersClicked(e);
			}
		}));
		mnNewMenu.add(mntmFrequentVerzuimers);
		
		JMenuItem mntmVerzuimHistorie = new JMenuItem("Verzuim historie...");
		mntmVerzuimHistorie.addActionListener(CursorController.createListener(frame,new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				menuVerzuimHistorieClicked(e);
			}
		}));
		mnNewMenu.add(mntmVerzuimHistorie);
		
		JMenuItem mntmActueelVerzuim = new JMenuItem("Actueel verzuim...");
		mntmActueelVerzuim.addActionListener(CursorController.createListener(frame, new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				menuActueelverzuimClicked();
			}
		}));
		mnNewMenu.add(mntmActueelVerzuim);
		
		JMenu mnWerkzaamheden = new JMenu("Werkzaamheden");
		menuBar.add(mnWerkzaamheden);
		JMenuItem mntmWerkzaamhedenOverzicht = new JMenuItem("Overzicht...");
		mntmWerkzaamhedenOverzicht.addActionListener(CursorController.createListener(frame, new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				menuWerkzaamhedenOverzichtClicked(e);
			}
		}));
		mnWerkzaamheden.add(mntmWerkzaamhedenOverzicht);
		
		mnFacturen = new JMenu("Facturen");
		menuBar.add(mnFacturen);
		
		JMenuItem mntmBtwPercentages = new JMenuItem("BTW percentages...");
		mntmBtwPercentages.addActionListener(CursorController.createListener(frame, new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				menuBtwPercentagesClicked(e);
			}
		}));
		mnFacturen.add(mntmBtwPercentages);
		
		JMenuItem mntmFactuurkoppen = new JMenuItem("Factuurkoppen...");
		mntmFactuurkoppen.addActionListener(CursorController.createListener(frame, new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				menuFactuurkoppenClicked(e);
			}
		}));
		mnFacturen.add(mntmFactuurkoppen);
		
		JMenuItem mntmFactuurcategorien = new JMenuItem("Factuurcategorien...");
		mntmFactuurcategorien.addActionListener(CursorController.createListener(frame, new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				menuFactuurcategorienClicked(e);
			}
		}));
		mnFacturen.add(mntmFactuurcategorien);
		
		JMenuItem mntmFactuuritems = new JMenuItem("Factuuritems...");
		mntmFactuuritems.addActionListener(CursorController.createListener(frame, new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				menuFactuuritemsClicked(e);
			}
		}));
		mnFacturen.add(mntmFactuuritems);
		
		JMenuItem mntmAfsluitenmaand = new JMenuItem("Afsluiten maand...");
		mntmAfsluitenmaand.addActionListener(CursorController.createListener(frame, new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				menuAfsluitenmaandClicked(e);
			}
		}));
		
		JSeparator separator = new JSeparator();
		mnFacturen.add(separator);
		mnFacturen.add(mntmAfsluitenmaand);
		
		JMenuItem mntmTerugdraaienmaand = new JMenuItem("Terugdraaien maand...");
		mntmTerugdraaienmaand.addActionListener(CursorController.createListener(frame, new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				menuTerugdraaienmaandClicked(e);
			}
		}));
		mnFacturen.add(mntmTerugdraaienmaand);
		
		JMenuItem mntmAfdrukkenverzenden = new JMenuItem("Afdrukken/verzenden...");
		mntmAfdrukkenverzenden.addActionListener(CursorController.createListener(frame, new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				menuAfdrukkenVerzendenClicked(e);
			}
		}));
		mnFacturen.add(mntmAfdrukkenverzenden);
		
		JMenuItem mntmFacturen = new JMenuItem("Overzicht...");
		mntmFacturen.addActionListener(CursorController.createListener(frame, new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				menuFacturenClicked(e);
			}
		}));
		mnFacturen.add(mntmFacturen);
		
		JMenuItem menuItem = new JMenuItem("BTW en Omzet...");
		menuItem.addActionListener(CursorController.createListener(frame, new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				menuBTWOmzetClicked(e);
			}
		}));
		mnFacturen.add(menuItem);
		
		JMenuItem mntmImporteerBetalingen = new JMenuItem("Importeer betalingen...");
		mntmImporteerBetalingen.addActionListener(CursorController.createListener(frame, new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				menuImporteerBetalingenClicked(e);
			}
		}));
		mnFacturen.add(mntmImporteerBetalingen);

		JMenu mnWindows = new JMenu("Windows");
		menuBar.add(mnWindows);
		
		JMenuItem mntmCascade = new JMenuItem("Cascade");
		mntmCascade.addActionListener(CursorController.createListener(frame, new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				memuWindowCascade(e);
			}
		}));
		mntmCascade.setActionCommand("Cascade");
		mnWindows.add(mntmCascade);
		
		mdiPanel = new JDesktopPaneTGI();
		mdiPanel.setBackground(SystemColor.control);
		frame.getContentPane().add(mdiPanel, BorderLayout.CENTER);
	}

	protected void menuImporteerBetalingenClicked(ActionEvent e) {
		dlgImportbetalingen = new ImportBetalingen(mdiPanel);
		dlgImportbetalingen.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		dlgImportbetalingen.setLoginSession(session);
		dlgImportbetalingen.setInfo(null);
		dlgImportbetalingen.setVisible(true);
		mdiPanel.add(dlgImportbetalingen);
		mdiPanel.setOpaque(true);
		mdiPanel.moveToFront(dlgImportbetalingen);
	}

	protected void menuImporteerWerknemersClicked(ActionEvent e) {
		dlgImportwerknemers = new ImportWerknemers(mdiPanel);
		dlgImportwerknemers.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		dlgImportwerknemers.setLoginSession(session);
		dlgImportwerknemers.setInfo(null);
		dlgImportwerknemers.setVisible(true);
		mdiPanel.add(dlgImportwerknemers);
		mdiPanel.setOpaque(true);
		mdiPanel.moveToFront(dlgImportwerknemers);
	}
	protected void menuImporteerUren(ActionEvent e) {
		dlgImportUren = new ImportUren(mdiPanel);
		dlgImportUren.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		dlgImportUren.setLoginSession(session);
		dlgImportUren.setInfo(null);
		dlgImportUren.setVisible(true);
		mdiPanel.add(dlgImportUren);
		mdiPanel.setOpaque(true);
		mdiPanel.moveToFront(dlgImportUren);
	}

	protected void menuAfsluitenDienstverbanden(ActionEvent e) {
		dlgAfsluitenDienstverbanden = new AfsluitenDienstverbanden(mdiPanel);
		dlgAfsluitenDienstverbanden.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		dlgAfsluitenDienstverbanden.setLoginSession(session);
		dlgAfsluitenDienstverbanden.setInfo(null);
		dlgAfsluitenDienstverbanden.setVisible(true);
		mdiPanel.add(dlgAfsluitenDienstverbanden);
		mdiPanel.setOpaque(true);
		mdiPanel.moveToFront(dlgAfsluitenDienstverbanden);
	}

	protected void menuBTWOmzetClicked(ActionEvent e) {
		dlgReportBTWOmzet = new ReportBTWOmzet(mdiPanel);
		dlgReportBTWOmzet.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		dlgReportBTWOmzet.setLoginSession(session);
		dlgReportBTWOmzet.setInfo(null);
		dlgReportBTWOmzet.setVisible(true);
		mdiPanel.add(dlgReportBTWOmzet);
		mdiPanel.setOpaque(true);
		mdiPanel.moveToFront(dlgReportBTWOmzet);
	}

	protected void menuAfdrukkenVerzendenClicked(ActionEvent e) {
		dlgAfdrukkenMaand = new AfdrukkenMaandDialog(( JFrame )SwingUtilities.getAncestorOfClass(Window.class, mdiPanel),true);
		dlgAfdrukkenMaand.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		dlgAfdrukkenMaand.setLoginSession(session);
		dlgAfdrukkenMaand.setInfo(null);
		dlgAfdrukkenMaand.setVisible(true);
		mdiPanel.add(dlgAfdrukkenMaand);
		mdiPanel.setOpaque(true);
		mdiPanel.moveToFront(dlgAfdrukkenMaand);
	}

	protected void menuFacturenClicked(ActionEvent e) {
		dlgFactuurOverzicht = new FactuurOverzicht(mdiPanel);
		dlgFactuurOverzicht.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		dlgFactuurOverzicht.setLoginSession(session);
		dlgFactuurOverzicht.setInfo(null);
		dlgFactuurOverzicht.ReloadTable();
		dlgFactuurOverzicht.setVisible(true);
		mdiPanel.add(dlgFactuurOverzicht);
		mdiPanel.setOpaque(true);
		mdiPanel.moveToFront(dlgFactuurOverzicht);
	}

	protected void menuAfsluitenmaandClicked(ActionEvent e) {
		dlgAfsluitenMaand = new AfsluitenMaandDialog(( JFrame )SwingUtilities.getAncestorOfClass(Window.class, mdiPanel),true);
		dlgAfsluitenMaand.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		dlgAfsluitenMaand.setLoginSession(session);
		dlgAfsluitenMaand.setInfo(null);
		dlgAfsluitenMaand.setVisible(true);
		mdiPanel.add(dlgAfsluitenMaand);
		mdiPanel.setOpaque(true);
		mdiPanel.moveToFront(dlgAfsluitenMaand);
	}

	protected void menuTerugdraaienmaandClicked(ActionEvent e) {
		dlgTerugdraaienMaand = new TerugdraaienMaandDialog(( JFrame )SwingUtilities.getAncestorOfClass(Window.class, mdiPanel),true);
		dlgTerugdraaienMaand.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		dlgTerugdraaienMaand.setLoginSession(session);
		dlgTerugdraaienMaand.setInfo(null);
		dlgTerugdraaienMaand.setVisible(true);
		mdiPanel.add(dlgTerugdraaienMaand);
		mdiPanel.setOpaque(true);
		mdiPanel.moveToFront(dlgTerugdraaienMaand);
	}

	protected void menuFactuuritemsClicked(ActionEvent e) {
		dlgFactuuritemOverzicht = new FactuuritemOverzicht(mdiPanel);
		dlgFactuuritemOverzicht.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		dlgFactuuritemOverzicht.setLoginSession(session);
		dlgFactuuritemOverzicht.setInfo(null);
		dlgFactuuritemOverzicht.ReloadTable();
		dlgFactuuritemOverzicht.setVisible(true);
		mdiPanel.add(dlgFactuuritemOverzicht);
		mdiPanel.setOpaque(true);
		mdiPanel.moveToFront(dlgFactuuritemOverzicht);
	}

	protected void menuFactuurcategorienClicked(ActionEvent e) {
		dlgFactuurcategorienOverzicht = new FactuurcategorieOverzicht(mdiPanel);
		dlgFactuurcategorienOverzicht.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		dlgFactuurcategorienOverzicht.setLoginSession(session);
		dlgFactuurcategorienOverzicht.ReloadTable();
		dlgFactuurcategorienOverzicht.setVisible(true);
		mdiPanel.add(dlgFactuurcategorienOverzicht);
		mdiPanel.setOpaque(true);
		mdiPanel.moveToFront(dlgFactuurcategorienOverzicht);
	}

	protected void menuFactuurkoppenClicked(ActionEvent e) {
		dlgFactuurkoppenOverzicht = new FactuurkopOverzicht(mdiPanel);
		dlgFactuurkoppenOverzicht.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		dlgFactuurkoppenOverzicht.setLoginSession(session);
		dlgFactuurkoppenOverzicht.ReloadTable();
		dlgFactuurkoppenOverzicht.setVisible(true);
		mdiPanel.add(dlgFactuurkoppenOverzicht);
		mdiPanel.setOpaque(true);
		mdiPanel.moveToFront(dlgFactuurkoppenOverzicht);
	}

	protected void menuWerkzaamhedenOverzichtClicked(ActionEvent e) {
		dlgWerkzaamhedenOverzicht = new WerkzaamhedenOverzicht(mdiPanel);
		dlgWerkzaamhedenOverzicht.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		dlgWerkzaamhedenOverzicht.setLoginSession(session);
		dlgWerkzaamhedenOverzicht.setInfo(null);
		dlgWerkzaamhedenOverzicht.ReloadTable();
		dlgWerkzaamhedenOverzicht.setVisible(true);
		mdiPanel.add(dlgWerkzaamhedenOverzicht);
		mdiPanel.setOpaque(true);
		mdiPanel.moveToFront(dlgWerkzaamhedenOverzicht);
	}
	protected void menuBtwPercentagesClicked(ActionEvent e) {
		dlgBtwOverzicht = new BtwOverzicht(mdiPanel);
		dlgBtwOverzicht.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		dlgBtwOverzicht.setLoginSession(session);
		dlgBtwOverzicht.ReloadTable();
		dlgBtwOverzicht.setVisible(true);
		mdiPanel.add(dlgBtwOverzicht);
		mdiPanel.setOpaque(true);
		mdiPanel.moveToFront(dlgBtwOverzicht);
	}

	protected void menuTarievenClicked(ActionEvent e) {
		dlgTariefOverzicht = new TariefOverzicht(mdiPanel);
		dlgTariefOverzicht.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		dlgTariefOverzicht.setLoginSession(session);
		dlgTariefOverzicht.ReloadTable();
		dlgTariefOverzicht.setVisible(true);
		mdiPanel.add(dlgTariefOverzicht);
		mdiPanel.setOpaque(true);
		mdiPanel.moveToFront(dlgTariefOverzicht);
	}

	protected void menuSettingsClicked(ActionEvent e) {
		dlgSettings = new SettingsDetail(mdiPanel);
		dlgSettings.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		dlgSettings.setLoginSession(session);
		dlgSettings.setVisible(true);
		dlgSettings.setInfo(null);
		mdiPanel.add(dlgSettings);
		mdiPanel.setOpaque(true);
		mdiPanel.moveToFront(dlgSettings);
		dlgSettings.setLocation(0, 0);
	}

	protected void menuRapportageStructuur(ActionEvent e) {
		dlgRapportageStructuur = new OeOverzicht(mdiPanel);
		dlgRapportageStructuur.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		dlgRapportageStructuur.setLoginSession(session);
		dlgRapportageStructuur.setVisible(true);
		dlgRapportageStructuur.setInfo(null);
		mdiPanel.add(dlgRapportageStructuur);
		mdiPanel.setOpaque(true);
		mdiPanel.moveToFront(dlgRapportageStructuur);
	}

	protected void menuRapportageNiveauClicked(ActionEvent e) {
		dlgRapportageNiveaus = new OeNiveauOverzicht(mdiPanel);
		dlgRapportageNiveaus.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		dlgRapportageNiveaus.setLoginSession(session);
		dlgRapportageNiveaus.setVisible(true);
		dlgRapportageNiveaus.setInfo(null);
		mdiPanel.add(dlgRapportageNiveaus);
		mdiPanel.setOpaque(true);
		mdiPanel.moveToFront(dlgRapportageNiveaus);
	}

	protected void menuBedrijfsgegevensClicked(ActionEvent e) {
		dlgBedrijfsgegevens = new BedrijfsgegevensDetail(mdiPanel);
		dlgBedrijfsgegevens.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		dlgBedrijfsgegevens.setLoginSession(session);
		dlgBedrijfsgegevens.setVisible(true);
		dlgBedrijfsgegevens.setInfo(null);
		mdiPanel.add(dlgBedrijfsgegevens);
		mdiPanel.setOpaque(true);
		mdiPanel.moveToFront(dlgBedrijfsgegevens);
	}

	protected void menuWerknemerNieuw(ActionEvent e) {
		dlgWerknemer = new WerknemerDetail(mdiPanel);
		dlgWerknemer.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		dlgWerknemer.setLoginSession(session);
		dlgWerknemer.setVisible(true);
		dlgWerknemer.setInfo(new WerknemerFastInfo());
		mdiPanel.add(dlgWerknemer);
		mdiPanel.setOpaque(true);
		mdiPanel.moveToFront(dlgWerknemer);
		dlgWerknemer.setLocation(0, 0);
	}

	protected void menuActueelverzuimClicked() {
		dlgReportActueelVerzuim = new ReportActueelVerzuim(mdiPanel);
		dlgReportActueelVerzuim.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		dlgReportActueelVerzuim.setLoginSession(session);
		dlgReportActueelVerzuim.setVisible(true);
		dlgReportActueelVerzuim.setInfo();
		mdiPanel.add(dlgReportActueelVerzuim);
		mdiPanel.setOpaque(true);
		mdiPanel.moveToFront(dlgReportActueelVerzuim);
	}

	protected void menuVerzuimHistorieClicked(ActionEvent e) {
		dlgReportVerzuimHistorie = new ReportVerzuimenHistorie(mdiPanel);
		dlgReportVerzuimHistorie.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		dlgReportVerzuimHistorie.setLoginSession(session);
		dlgReportVerzuimHistorie.setVisible(true);
		dlgReportVerzuimHistorie.setInfo();
		mdiPanel.add(dlgReportVerzuimHistorie);
		mdiPanel.setOpaque(true);
		mdiPanel.moveToFront(dlgReportVerzuimHistorie);
	}

	protected void menuFrequentVerzuimersClicked(ActionEvent e) {
		dlgReportFrequentVerzuimers = new ReportFrequentVerzuimers(mdiPanel);
		dlgReportFrequentVerzuimers.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		dlgReportFrequentVerzuimers.setLoginSession(session);
		dlgReportFrequentVerzuimers.setVisible(true);
		dlgReportFrequentVerzuimers.setInfo();
		mdiPanel.add(dlgReportFrequentVerzuimers);
		mdiPanel.setOpaque(true);
		mdiPanel.moveToFront(dlgReportFrequentVerzuimers);
	
	}

	protected void menuVerzuimoverzichtClicked(ActionEvent e) {
		dlgReportVerzuimOverzicht = new ReportVerzuimoverzicht(mdiPanel);
		dlgReportVerzuimOverzicht.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		dlgReportVerzuimOverzicht.setLoginSession(session);
		dlgReportVerzuimOverzicht.setVisible(true);
		dlgReportVerzuimOverzicht.setInfo();
		mdiPanel.add(dlgReportVerzuimOverzicht);
		mdiPanel.setOpaque(true);
		mdiPanel.moveToFront(dlgReportVerzuimOverzicht);
	}

	protected void menuUitkeringsinstantieClicked(ActionEvent e) {
		dlgUitkeringsinstantieOverzicht = new UitkeringsinstantieOverzicht(mdiPanel);
		dlgUitkeringsinstantieOverzicht.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		dlgUitkeringsinstantieOverzicht.setLoginSession(session);
		dlgUitkeringsinstantieOverzicht.ReloadTable();
		dlgUitkeringsinstantieOverzicht.setVisible(true);
		mdiPanel.add(dlgUitkeringsinstantieOverzicht);
		mdiPanel.setOpaque(true);
		mdiPanel.moveToFront(dlgUitkeringsinstantieOverzicht);
	}

	protected void menuArbodienstenClicked(ActionEvent e) {
		dlgArbodienstOverzicht = new ArbodienstOverzicht(mdiPanel);
		dlgArbodienstOverzicht.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		dlgArbodienstOverzicht.setLoginSession(session);
		dlgArbodienstOverzicht.ReloadTable();
		dlgArbodienstOverzicht.setVisible(true);
		mdiPanel.add(dlgArbodienstOverzicht);
		mdiPanel.setOpaque(true);
		mdiPanel.moveToFront(dlgArbodienstOverzicht);
	}

	protected void menuHoldingClicked(ActionEvent e) {
		dlgHoldingOverzicht = new HoldingOverzicht(mdiPanel);
		dlgHoldingOverzicht.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		dlgHoldingOverzicht.setLoginSession(session);
		dlgHoldingOverzicht.ReloadTable();
		dlgHoldingOverzicht.setVisible(true);
		mdiPanel.add(dlgHoldingOverzicht);
		mdiPanel.setOpaque(true);
		mdiPanel.moveToFront(dlgHoldingOverzicht);
	}

	protected void menuDocumentTemplatesClicked(ActionEvent e) {
		dlgTemplateOverzicht = new DocumentTemplateOverzicht(mdiPanel);
		dlgTemplateOverzicht.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		dlgTemplateOverzicht.setLoginSession(session);
		dlgTemplateOverzicht.ReloadTable();
		dlgTemplateOverzicht.setVisible(true);
		mdiPanel.add(dlgTemplateOverzicht);
		mdiPanel.setOpaque(true);
		mdiPanel.moveToFront(dlgTemplateOverzicht);
	}

	protected void menuTodoOverzichtClicked(ActionEvent e) {
		dlgTodoOverzicht = new TodoOverzicht(mdiPanel);
		dlgTodoOverzicht.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		dlgTodoOverzicht.setLoginSession(session);
		dlgTodoOverzicht.ReloadTable();
		dlgTodoOverzicht.setVisible(true);
		mdiPanel.add(dlgTodoOverzicht);
		mdiPanel.setOpaque(true);
		mdiPanel.moveToFront(dlgTodoOverzicht);
	}

	protected void menuCascodesClicked(ActionEvent e) {
		dlgCascodeOverzicht = new CascodeOverzicht(mdiPanel);
		dlgCascodeOverzicht.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		dlgCascodeOverzicht.setLoginSession(session);
		dlgCascodeOverzicht.ReloadTable();
		dlgCascodeOverzicht.setVisible(true);
		mdiPanel.add(dlgCascodeOverzicht);
		mdiPanel.setOpaque(true);
		mdiPanel.moveToFront(dlgCascodeOverzicht);
	}

	protected void menuCascodeGroepenClicked(ActionEvent e) {
		dlgCascodeGroepOverzicht = new CascodeGroepOverzicht(mdiPanel);
		dlgCascodeGroepOverzicht.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		dlgCascodeGroepOverzicht.setLoginSession(session);
		dlgCascodeGroepOverzicht.ReloadTable();
		dlgCascodeGroepOverzicht.setVisible(true);
		mdiPanel.add(dlgCascodeGroepOverzicht);
		mdiPanel.setOpaque(true);
		mdiPanel.moveToFront(dlgCascodeGroepOverzicht);
	}

	protected void menuApplicatiefunctiesClicked(ActionEvent e) {
		dlgApplFuncOverzicht = new ApplicatieFunctieOverzicht(mdiPanel);
		dlgApplFuncOverzicht.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		dlgApplFuncOverzicht.setLoginSession(session);
		dlgApplFuncOverzicht.ReloadTable();
		dlgApplFuncOverzicht.setVisible(true);
		mdiPanel.add(dlgApplFuncOverzicht);
		mdiPanel.setOpaque(true);
		mdiPanel.moveToFront(dlgApplFuncOverzicht);
	}

	protected void menuRollenClicked(ActionEvent e) {
		dlgRollenOverzicht = new RolOverzicht(mdiPanel);
		dlgRollenOverzicht.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		dlgRollenOverzicht.setLoginSession(session);
		dlgRollenOverzicht.ReloadTable();
		dlgRollenOverzicht.setVisible(true);
		mdiPanel.add(dlgRollenOverzicht);
		mdiPanel.setOpaque(true);
		mdiPanel.moveToFront(dlgRollenOverzicht);
	}

	protected void menuGebruikersClicked(ActionEvent e) {
		dlgGebruikerOverzicht = new GebruikerOverzicht(mdiPanel);
		dlgGebruikerOverzicht.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		dlgGebruikerOverzicht.setLoginSession(session);
		dlgGebruikerOverzicht.ReloadTable();
		dlgGebruikerOverzicht.setVisible(true);
		mdiPanel.add(dlgGebruikerOverzicht);
		mdiPanel.setOpaque(true);
		mdiPanel.moveToFront(dlgGebruikerOverzicht);
	}

	protected void menuWerknemerOverzichtClicked(ActionEvent e) {
		dlgWerknemerOverzicht = new WerknemerOverzicht(mdiPanel);
		dlgWerknemerOverzicht.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		dlgWerknemerOverzicht.setLoginSession(session);
		dlgWerknemerOverzicht.ReloadTable();
		dlgWerknemerOverzicht.setVisible(true);
		mdiPanel.add(dlgWerknemerOverzicht);
		mdiPanel.setOpaque(true);
		mdiPanel.moveToFront(dlgWerknemerOverzicht);
	}
	protected void menuActiviteitenClicked(ActionEvent e) {
		dlgActiviteitOverzicht = new ActiviteitOverzicht(mdiPanel);
		dlgActiviteitOverzicht.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		dlgActiviteitOverzicht.setLoginSession(session);
		dlgActiviteitOverzicht.ReloadTable();
		dlgActiviteitOverzicht.setVisible(true);
		mdiPanel.add(dlgActiviteitOverzicht);
		mdiPanel.setOpaque(true);
		mdiPanel.moveToFront(dlgActiviteitOverzicht);
	}

	protected void menuPakkettenPakkettenclicked(ActionEvent e) {
		dlgPakketOverzicht = new PakketOverzicht(mdiPanel);
		dlgPakketOverzicht.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		dlgPakketOverzicht.setLoginSession(session);
		dlgPakketOverzicht.ReloadTable();
		dlgPakketOverzicht.setVisible(true);
		mdiPanel.add(dlgPakketOverzicht);
		mdiPanel.setOpaque(true);
		mdiPanel.moveToFront(dlgPakketOverzicht);
	}

	protected void memuWindowCascade(ActionEvent e) {
		mdiPanel.cascade(mdiPanel);
	}

	protected void menuAfsluitenClicked(ActionEvent e) {
		try {
			ServiceLocator.getInstance().release();
		} catch (ServiceLocatorException e1) {
			e1.printStackTrace();
		}
		this.frame.dispose();
	}
	protected void menuWerkgeverOverzichtclicked(ActionEvent e) {
		dlgWerkgeverOverzicht = new WerkgeverOverzicht(mdiPanel);
		dlgWerkgeverOverzicht.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		dlgWerkgeverOverzicht.setLoginSession(session);
		dlgWerkgeverOverzicht.ReloadTable();
		dlgWerkgeverOverzicht.setVisible(true);
		mdiPanel.add(dlgWerkgeverOverzicht);
		mdiPanel.setOpaque(true);
		mdiPanel.moveToFront(dlgWerkgeverOverzicht);
	}

	@Override
	public void windowOpened(WindowEvent e) {
	}

	@Override
	public void windowClosing(WindowEvent e) {
		try {
			ServiceLocator.getInstance().release();
			session.logoffSession();
		} catch (ServiceLocatorException e1) {
			e1.printStackTrace();
		}
	}

	@Override
	public void windowClosed(WindowEvent e) {
	}

	@Override
	public void windowIconified(WindowEvent e) {
	}

	@Override
	public void windowDeiconified(WindowEvent e) {
	}

	@Override
	public void windowActivated(WindowEvent e) {
	}

	@Override
	public void windowDeactivated(WindowEvent e) {
	}
}
