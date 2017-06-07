package com.gieselaar.verzuim.views;
import java.awt.Toolkit;
import javax.swing.JFrame;

import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import com.gieselaar.verzuim.controllers.MDIMainController;
import com.gieselaar.verzuim.controllers.MDIMainController.__menucommands;
import com.gieselaar.verzuim.utils.EventQueueProxy;
import com.gieselaar.verzuim.utils.ExceptionLogger;
import com.gieselaar.verzuim.controllers.WerknemerController;
import com.gieselaar.verzuim.viewsutils.CursorController;
import com.gieselaar.verzuimbeheer.exceptions.VerzuimApplicationException;
import com.gieselaar.verzuimbeheer.exceptions.VerzuimAuthenticationException;
import com.gieselaar.verzuimbeheer.facades.AutorisatieFacadeRemote.__applicatiefunctie;
import com.gieselaar.verzuimbeheer.facades.LoginSessionRemote;
import com.gieselaar.verzuimbeheer.utils.ServiceLocator;
import com.gieselaar.verzuimbeheer.utils.ServiceLocatorException;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.SystemColor;
import javax.swing.JSeparator;

public class MDIMain implements WindowListener {

	private JFrame frame;
	private LoginSessionRemote session = null;
	private JDesktopPaneTGI mdiPanel;
	private MDIMainController controller;
	/** 
	 * Global menu-items
	 */
	private JMenuItem mntmTarieven;
	private JMenu mnFacturen;
	/**
	 * Create the application.
	 * @param controller 
	 */
	public MDIMain(MDIMainController controller) {
		this.controller = controller;
		initialize();
		controller.setDesktoppane(mdiPanel);
		frame.setVisible(true);
		try {
			Login();
		} catch (VerzuimAuthenticationException | VerzuimApplicationException e) {
			ExceptionLogger.ProcessException(e, null);
		}
	}
	private void Login() throws VerzuimAuthenticationException, VerzuimApplicationException{
		controller.getModel().getSession().authenticateGebruiker("tonny", "password");
		controller.logon();
		if (this.controller.isAuthorised(__applicatiefunctie.BEHEERTARIEVEN)){
			mntmTarieven.setEnabled(true);
		}else{
			mntmTarieven.setEnabled(false);
		}
		
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		EventQueue queue = Toolkit.getDefaultToolkit().getSystemEventQueue();
		queue.push(new EventQueueProxy());
		
		frame = new JFrame();
		frame.setBounds(100, 100, 1087, 752);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		JMenuBar menuBar = new JMenuBar();
		frame.getContentPane().add(menuBar, BorderLayout.NORTH);
		frame.addWindowListener(this);
		
		addMenuApplication(menuBar);
		addMenuWerkgever(menuBar);
		addMenuWerknemer(menuBar);
		addMenuTabellen(menuBar);
		addMenuInstellingen(menuBar);
		addMenuAutorisatie(menuBar);
		addMenuReports(menuBar);
		addMenuWerkzaamheden(menuBar);
		addMenuFacturen(menuBar);
					

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
		controller.setDesktoppane(mdiPanel);
		frame.getContentPane().add(mdiPanel, BorderLayout.CENTER);
	}

	private void addMenuFacturen(JMenuBar menuBar) {
		mnFacturen = new JMenu("Facturen");
		menuBar.add(mnFacturen);
		
		JMenuItem mntmBtwPercentages = new JMenuItem("BTW percentages...");
		mntmBtwPercentages.setActionCommand(__menucommands.BTWPERCENTAGES.toString());
		mntmBtwPercentages.addActionListener(CursorController.createListener(frame, controller));
		mnFacturen.add(mntmBtwPercentages);
		
		JMenuItem mntmFactuurkoppen = new JMenuItem("Factuurkoppen...");
		mntmFactuurkoppen.setActionCommand(__menucommands.FACTUURKOPPEN.toString());
		mntmFactuurkoppen.addActionListener(CursorController.createListener(frame, controller));
		mnFacturen.add(mntmFactuurkoppen);
		
		JMenuItem mntmFactuurcategorien = new JMenuItem("Factuurcategorien...");
		mntmFactuurcategorien.setActionCommand(__menucommands.FACTUURCATEGORIEN.toString());
		mntmFactuurcategorien.addActionListener(CursorController.createListener(frame, controller));
		mnFacturen.add(mntmFactuurcategorien);
		
		JMenuItem mntmFactuuritems = new JMenuItem("Factuuritems...");
		mntmFactuuritems.setActionCommand(__menucommands.FACTUURITEMS.toString());
		mntmFactuuritems.addActionListener(CursorController.createListener(frame, controller));
		mnFacturen.add(mntmFactuuritems);
		
		JMenuItem mntmAfsluitenmaand = new JMenuItem("Afsluiten maand...");
		mntmAfsluitenmaand.setActionCommand(__menucommands.AFSLUITENMAAND.toString());
		mntmAfsluitenmaand.addActionListener(CursorController.createListener(frame, controller));
		
		JSeparator separator = new JSeparator();
		mnFacturen.add(separator);
		mnFacturen.add(mntmAfsluitenmaand);
		
		JMenuItem mntmTerugdraaienmaand = new JMenuItem("Terugdraaien maand...");
		mntmTerugdraaienmaand.setActionCommand(__menucommands.TERUGDRAAIENMAAND.toString());
		mntmTerugdraaienmaand.addActionListener(CursorController.createListener(frame, controller));
		mnFacturen.add(mntmTerugdraaienmaand);
		
		JMenuItem mntmAfdrukkenverzenden = new JMenuItem("Afdrukken/verzenden...");
		mntmAfdrukkenverzenden.setActionCommand(__menucommands.AFDRUKKENVERZENDEN.toString());
		mntmAfdrukkenverzenden.addActionListener(CursorController.createListener(frame, controller));
		mnFacturen.add(mntmAfdrukkenverzenden);
		
		JMenuItem mntmFacturen = new JMenuItem("Overzicht...");
		mntmFacturen.setActionCommand(__menucommands.FACTUUROVERZICHT.toString());
		mntmFacturen.addActionListener(CursorController.createListener(frame, controller));
		mnFacturen.add(mntmFacturen);
		
		JMenuItem mntmBtwenOmzet = new JMenuItem("BTW en Omzet...");
		mntmBtwenOmzet.setActionCommand(__menucommands.BTWENOMZET.toString());
		mntmBtwenOmzet.addActionListener(CursorController.createListener(frame, controller));
		mnFacturen.add(mntmBtwenOmzet);
		
		JMenuItem mntmImporteerBetalingen = new JMenuItem("Importeer betalingen...");
		mntmImporteerBetalingen.setActionCommand(__menucommands.IMPORTEERBETALINGEN.toString());
		mntmImporteerBetalingen.addActionListener(CursorController.createListener(frame, controller));
		mnFacturen.add(mntmImporteerBetalingen);
	}
	private void addMenuWerkzaamheden(JMenuBar menuBar) {
		JMenu mnWerkzaamheden = new JMenu("Werkzaamheden");
		menuBar.add(mnWerkzaamheden);
		
		JMenuItem mntmWerkzaamhedenOverzicht = new JMenuItem("Overzicht...");
		mntmWerkzaamhedenOverzicht.setActionCommand(__menucommands.WERKZAAMHEDENOVERZICHT.toString());
		mntmWerkzaamhedenOverzicht.addActionListener(CursorController.createListener(frame, controller));
		mnWerkzaamheden.add(mntmWerkzaamhedenOverzicht);
	}
	private void addMenuReports(JMenuBar menuBar) {
		JMenu mnNewMenu = new JMenu("Reports");
		menuBar.add(mnNewMenu);
		
		JMenuItem mntmVerzuimoverzicht = new JMenuItem("Verzuimoverzicht...");
		mntmVerzuimoverzicht.setActionCommand(__menucommands.REPORTVERZUIMOVERZICHT.toString());
		mntmVerzuimoverzicht.addActionListener(CursorController.createListener(frame, controller));
		mnNewMenu.add(mntmVerzuimoverzicht);
		
		JMenuItem mntmFrequentVerzuimers = new JMenuItem("Frequent verzuimers...");
		mntmFrequentVerzuimers.setActionCommand(__menucommands.REPORTFREQUENTVERZUIM.toString());
		mntmFrequentVerzuimers.addActionListener(CursorController.createListener(frame, controller));
		mnNewMenu.add(mntmFrequentVerzuimers);
		
		JMenuItem mntmVerzuimHistorie = new JMenuItem("Verzuim historie...");
		mntmVerzuimHistorie.setActionCommand(__menucommands.REPORTVERZUIMHISTORIE.toString());
		mntmVerzuimHistorie.addActionListener(CursorController.createListener(frame, controller));
		mnNewMenu.add(mntmVerzuimHistorie);
		
		JMenuItem mntmActueelVerzuim = new JMenuItem("Actueel verzuim...");
		mntmActueelVerzuim.setActionCommand(__menucommands.REPORTACTUEELVERZUIM.toString());
		mntmActueelVerzuim.addActionListener(CursorController.createListener(frame, controller));
		mnNewMenu.add(mntmActueelVerzuim);
	}
	private void addMenuAutorisatie(JMenuBar menuBar) {
		JMenu mnAutorisatie = new JMenu("Autorisatie");
		menuBar.add(mnAutorisatie);
		
		JMenuItem mntmGebruikers = new JMenuItem("Gebruikers...");
		mntmGebruikers.setActionCommand(__menucommands.AUTORISATIEGEBRUIKERS.toString());
		mntmGebruikers.addActionListener(CursorController.createListener(frame, controller));
		mnAutorisatie.add(mntmGebruikers);
		
		JMenuItem mntmRollen = new JMenuItem("Rollen...");
		mntmRollen.setActionCommand(__menucommands.AUTORISATIEROLLEN.toString());
		mntmRollen.addActionListener(CursorController.createListener(frame, controller));
		mnAutorisatie.add(mntmRollen);
		
		JMenuItem mntmApplicatiefuncties = new JMenuItem("Applicatiefuncties...");
		mntmApplicatiefuncties.setActionCommand(__menucommands.AUTORISATIEAPPLICATIEFUNCTIES.toString());
		mntmApplicatiefuncties.addActionListener(CursorController.createListener(frame, controller));
		mnAutorisatie.add(mntmApplicatiefuncties);
	}
	private void addMenuInstellingen(JMenuBar menuBar) {
		JMenu mnInstellingen = new JMenu("Instellingen");
		menuBar.add(mnInstellingen);
		
		JMenuItem mntmArbodiensten = new JMenuItem("Arbodiensten...");
		mntmArbodiensten.setActionCommand(__menucommands.INSTELLINGENARBODIENSTEN.toString());
		mntmArbodiensten.addActionListener(CursorController.createListener(frame,controller));
		mnInstellingen.add(mntmArbodiensten);
		
		JMenuItem mntmUitkeringsinstantie = new JMenuItem("Uitvoeringsinstanties...");
		mntmUitkeringsinstantie.setActionCommand(__menucommands.INSTELLINGENUITVOERINGSINSTANTIES.toString());
		mntmUitkeringsinstantie.addActionListener(CursorController.createListener(frame,controller));
		mnInstellingen.add(mntmUitkeringsinstantie);
		
		JMenuItem mntmBedrijfsgegevens = new JMenuItem("Bedrijfsgegevens...");
		mntmBedrijfsgegevens.setActionCommand(__menucommands.INSTELLINGENBEDRIJFSGEGEVENS.toString());
		mntmBedrijfsgegevens.addActionListener(CursorController.createListener(frame,controller));
		mnInstellingen.add(mntmBedrijfsgegevens);
	}
	private void addMenuTabellen(JMenuBar menuBar) {
		JMenu mnTabellen = new JMenu("Tabellen");
		menuBar.add(mnTabellen);
		
		JMenuItem mntmPakketten = new JMenuItem("Pakketten...");
		mntmPakketten.setActionCommand(__menucommands.TABELLENPAKKETTEN.toString());
		mntmPakketten.addActionListener(CursorController.createListener(frame,controller));
		mnTabellen.add(mntmPakketten);
		
		JMenuItem mntmActiviteiten = new JMenuItem("Activiteiten...");
		mntmActiviteiten.setActionCommand(__menucommands.TABELLENACTIVITEITEN.toString());
		mntmActiviteiten.addActionListener(CursorController.createListener(frame,controller));
		mnTabellen.add(mntmActiviteiten);
		
		JMenuItem mntmCascodegroepen = new JMenuItem("Cascodegroepen...");
		mntmCascodegroepen.setActionCommand(__menucommands.TABELLENCASCODEGROEPEN.toString());
		mntmCascodegroepen.addActionListener(CursorController.createListener(frame,controller));
		mnTabellen.add(mntmCascodegroepen);
		
		JMenuItem mntmCascodes = new JMenuItem("Cascodes...");
		mntmCascodes.setActionCommand(__menucommands.TABELLENCASCODES.toString());
		mntmCascodes.addActionListener(CursorController.createListener(frame,controller));
		mnTabellen.add(mntmCascodes);
		
		JMenuItem mntmDocumentTemplates = new JMenuItem("Document templates...");
		mntmDocumentTemplates.setActionCommand(__menucommands.TABELLENDOCUMENTTEMPLATES.toString());
		mntmDocumentTemplates.addActionListener(CursorController.createListener(frame,controller));
		mnTabellen.add(mntmDocumentTemplates);
	}
	private void addMenuWerkgever(JMenuBar menuBar) {
		JMenu mnWerkgever = new JMenu("Werkgever");
		menuBar.add(mnWerkgever);
		
		JMenuItem mntmOverzicht = new JMenuItem("Overzicht...");
		mntmOverzicht.setActionCommand(__menucommands.WERKGEVEROVERZICHT.toString());
		mntmOverzicht.addActionListener(CursorController.createListener(frame,controller));
		mnWerkgever.add(mntmOverzicht);
		
		JMenuItem mntmTodo = new JMenuItem("Todo...");
		mntmTodo.setActionCommand(__menucommands.WERKGEVERTODO.toString());
		mntmTodo.addActionListener(CursorController.createListener(frame,controller));
		mnWerkgever.add(mntmTodo);
		
		JMenuItem mntmHolding = new JMenuItem("Holding...");
		mntmHolding.setActionCommand(__menucommands.WERKGEVERHOLDING.toString());
		mntmHolding.addActionListener(CursorController.createListener(frame,controller));
		mnWerkgever.add(mntmHolding);
		
		JMenuItem mntmRapportageNiveaus = new JMenuItem("Rapportage niveaus...");
		mntmRapportageNiveaus.setActionCommand(__menucommands.WERKGEVERRAPPORTAGENIVEAUS.toString());
		mntmRapportageNiveaus.addActionListener(CursorController.createListener(frame,controller));
		mnWerkgever.add(mntmRapportageNiveaus);
		
		mntmTarieven = new JMenuItem("Tarieven...");
		mntmTarieven.setActionCommand(__menucommands.WERKGEVERTARIEVEN.toString());
		mntmTarieven.addActionListener(CursorController.createListener(frame,controller));
		mnWerkgever.add(mntmTarieven);
		mnWerkgever.add(mntmTarieven);
		mnWerkgever.add(mntmRapportageNiveaus);
		
		JMenuItem mntmRapportageStructuur = new JMenuItem("Rapportage structuur...");
		mntmRapportageStructuur.setActionCommand(__menucommands.WERKGEVERRAPPORTAGESTRUCTUUR.toString());
		mntmRapportageStructuur.addActionListener(CursorController.createListener(frame,controller));
		mnWerkgever.add(mntmRapportageStructuur);
		
		JMenuItem mntmImporteerWerknemers = new JMenuItem("Importeer werknemers...");
		mntmImporteerWerknemers.setActionCommand(__menucommands.WERKGEVERIMPORTEERWERKNEMERS.toString());
		mntmImporteerWerknemers.addActionListener(CursorController.createListener(frame,controller));
		mnWerkgever.add(mntmImporteerWerknemers);
		
		addMenuKFCSpecials(mnWerkgever);
	}
	private void addMenuKFCSpecials(JMenu mnWerkgever) {
		JMenu mnKfcSpecials = new JMenu("KFC specials");
		mnWerkgever.add(mnKfcSpecials);
		
		JMenuItem mntmImporteerUren = new JMenuItem("Importeer uren...");
		mntmImporteerUren.setActionCommand(__menucommands.WERKGEVERIMPORTEERUREN.toString());
		mntmImporteerUren.addActionListener(CursorController.createListener(frame,controller));
		mnKfcSpecials.add(mntmImporteerUren);
		
		JMenuItem mntmAfsluitenDienstverbanden = new JMenuItem("Afsluiten dienstverbanden... ");
		mntmAfsluitenDienstverbanden.setActionCommand(__menucommands.WERKGEVERAFSLUITENDIENSTVERBANDEN.toString());
		mntmAfsluitenDienstverbanden.addActionListener(CursorController.createListener(frame,controller));
		mnKfcSpecials.add(mntmAfsluitenDienstverbanden);
	}
	private void addMenuWerknemer(JMenuBar menuBar) {
		JMenu mnWerknemer = new JMenu("Werknemer");
		menuBar.add(mnWerknemer);
		
		JMenuItem mntmNieuw = new JMenuItem("Nieuw...");
		mntmNieuw.setActionCommand(__menucommands.WERKNEMERNIEUW.toString());
		mntmNieuw.addActionListener(CursorController.createListener(frame,controller));
		mnWerknemer.add(mntmNieuw);
		
		JMenuItem mntmOverzicht = new JMenuItem("Overzicht...");
		mntmOverzicht.setActionCommand(__menucommands.WERKNEMEROVERZICHT.toString());
		mntmOverzicht.addActionListener(CursorController.createListener(frame,controller));
		mnWerknemer.add(mntmOverzicht);

	}
	private void addMenuApplication(JMenuBar menuBar) {
		JMenu mnApplicatie = new JMenu("Applicatie");
		menuBar.add(mnApplicatie);
		
		JMenuItem mntmAfsluiten = new JMenuItem("Afsluiten");
		mntmAfsluiten.setActionCommand(__menucommands.APPLICATIONCLOSE.toString());
		mntmAfsluiten.addActionListener(CursorController.createListener(frame, controller));
		
		JMenuItem mntmSettings = new JMenuItem("Settings...");
		mntmSettings.addActionListener(CursorController.createListener(frame, controller));
		mntmSettings.setActionCommand(__menucommands.APPLICATIONSETTINGS.toString());
		mnApplicatie.add(mntmSettings);
		mnApplicatie.add(mntmAfsluiten);
	}
	public JDesktopPaneTGI getMdiPanel() {
		return mdiPanel;
	}

	public void setMdiPanel(JDesktopPaneTGI mdiPanel) {
		this.mdiPanel = mdiPanel;
	}

	protected void menuImporteerBetalingenClicked(ActionEvent e) {
	}

	protected void menuImporteerWerknemersClicked(ActionEvent e) {
	}
	protected void menuImporteerUren(ActionEvent e) {
	}

	protected void menuAfsluitenDienstverbanden(ActionEvent e) {
	}

	protected void menuBTWOmzetClicked(ActionEvent e) {
	}

	protected void menuAfdrukkenVerzendenClicked(ActionEvent e) {
	}

	protected void menuFacturenClicked(ActionEvent e) {
	}

	protected void menuAfsluitenmaandClicked(ActionEvent e) {
	}

	protected void menuTerugdraaienmaandClicked(ActionEvent e) {
	}

	protected void menuFactuuritemsClicked(ActionEvent e) {
	}

	protected void menuFactuurcategorienClicked(ActionEvent e) {
	}

	protected void menuFactuurkoppenClicked(ActionEvent e) {
	}

	protected void menuWerkzaamhedenOverzichtClicked(ActionEvent e) {
	}
	
	protected void menuBtwPercentagesClicked(ActionEvent e) {
	}

	protected void menuTarievenClicked(ActionEvent e) {
	}

	protected void menuSettingsClicked(ActionEvent e) {
	}

	protected void menuRapportageStructuur(ActionEvent e) {
	}

	protected void menuRapportageNiveauClicked(ActionEvent e) {
	}

	protected void menuBedrijfsgegevensClicked(ActionEvent e) {
	}

	protected void menuWerknemerNieuw(ActionEvent e) {
	}

	protected void menuActueelverzuimClicked() {
	}

	protected void menuVerzuimHistorieClicked(ActionEvent e) {
	}

	protected void menuFrequentVerzuimersClicked(ActionEvent e) {
	}

	protected void menuVerzuimoverzichtClicked(ActionEvent e) {
	}

	protected void menuUitkeringsinstantieClicked(ActionEvent e) {
	}

	protected void menuArbodienstenClicked(ActionEvent e) {
	}

	protected void menuHoldingClicked(ActionEvent e) {
	}

	protected void menuDocumentTemplatesClicked(ActionEvent e) {
	}

	protected void menuTodoOverzichtClicked(ActionEvent e) {
	}

	protected void menuCascodesClicked(ActionEvent e) {
	}

	protected void menuCascodeGroepenClicked(ActionEvent e) {
	}

	protected void menuApplicatiefunctiesClicked(ActionEvent e) {
	}

	protected void menuRollenClicked(ActionEvent e) {
	}

	protected void menuGebruikersClicked(ActionEvent e) {
	}

	protected void menuWerknemerOverzichtClicked(ActionEvent e) {
		WerknemerController controller = new WerknemerController(session);
		WerknemerList frame = new WerknemerList(controller);
		frame.setVisible(true);
		mdiPanel.add(frame);
		mdiPanel.setOpaque(true);
		mdiPanel.moveToFront(frame);
		
	}
	protected void menuActiviteitenClicked(ActionEvent e) {
	}

	protected void menuPakkettenPakkettenclicked(ActionEvent e) {
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
	}

	@Override
	public void windowOpened(WindowEvent e) {
	}

	@Override
	public void windowClosing(WindowEvent e) {
		controller.logoff();
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
