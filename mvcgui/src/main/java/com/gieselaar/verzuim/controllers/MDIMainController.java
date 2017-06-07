package com.gieselaar.verzuim.controllers;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Date;
import java.util.List;

import javax.swing.JDesktopPane;

import com.gieselaar.verzuim.interfaces.ControllerEventListener;
import com.gieselaar.verzuim.models.MDIMainModel;
import com.gieselaar.verzuim.utils.ExceptionLogger;
import com.gieselaar.verzuim.utils.VerzuimAuthorisation;
import com.gieselaar.verzuim.views.ActiviteitList;
import com.gieselaar.verzuim.views.AfdrukkenMaandDialog;
import com.gieselaar.verzuim.views.AfsluitenDienstverbanden;
import com.gieselaar.verzuim.views.AfsluitenMaandDialog;
import com.gieselaar.verzuim.views.ApplicatieFunctieList;
import com.gieselaar.verzuim.views.ArbodienstList;
import com.gieselaar.verzuim.views.BtwList;
import com.gieselaar.verzuim.views.CascodeGroepList;
import com.gieselaar.verzuim.views.CascodeList;
import com.gieselaar.verzuim.views.DocumentTemplateList;
import com.gieselaar.verzuim.views.FactuurList;
import com.gieselaar.verzuim.views.FactuurcategorieList;
import com.gieselaar.verzuim.views.FactuuritemList;
import com.gieselaar.verzuim.views.FactuurkopList;
import com.gieselaar.verzuim.views.GebruikerList;
import com.gieselaar.verzuim.views.HoldingList;
import com.gieselaar.verzuim.views.ImportBetalingen;
import com.gieselaar.verzuim.views.ImportUren;
import com.gieselaar.verzuim.views.ImportWerknemers;
import com.gieselaar.verzuim.views.OeList;
import com.gieselaar.verzuim.views.OeNiveauList;
import com.gieselaar.verzuim.views.PakketList;
import com.gieselaar.verzuim.views.ReportActueelVerzuim;
import com.gieselaar.verzuim.views.ReportBTWOmzet;
import com.gieselaar.verzuim.views.ReportFrequentVerzuimers;
import com.gieselaar.verzuim.views.ReportVerzuimenHistorie;
import com.gieselaar.verzuim.views.ReportVerzuimoverzicht;
import com.gieselaar.verzuim.views.RolList;
import com.gieselaar.verzuim.views.TariefList;
import com.gieselaar.verzuim.views.TerugdraaienMaandDialog;
import com.gieselaar.verzuim.views.TodoList;
import com.gieselaar.verzuim.views.UitkeringsinstantieList;
import com.gieselaar.verzuim.views.WerkgeverList;
import com.gieselaar.verzuim.views.WerknemerList;
import com.gieselaar.verzuim.views.WerknemerWizard;
import com.gieselaar.verzuim.views.WerkzaamhedenList;
import com.gieselaar.verzuim.viewsutils.VerzuimComboBoxModel;
import com.gieselaar.verzuimbeheer.exceptions.ValidationException;
import com.gieselaar.verzuimbeheer.exceptions.VerzuimApplicationException;
import com.gieselaar.verzuimbeheer.facades.AutorisatieFacadeRemote.__applicatiefunctie;
import com.gieselaar.verzuimbeheer.services.ActiviteitInfo;
import com.gieselaar.verzuimbeheer.services.ActiviteitInfo.__meldingsoort;
import com.gieselaar.verzuimbeheer.services.ActiviteitInfo.__periodesoort;
import com.gieselaar.verzuimbeheer.services.ArbodienstInfo;
import com.gieselaar.verzuimbeheer.services.BedrijfsartsInfo;
import com.gieselaar.verzuimbeheer.services.BedrijfsgegevensInfo;
import com.gieselaar.verzuimbeheer.services.BtwInfo.__btwtariefsoort;
import com.gieselaar.verzuimbeheer.services.CascodeInfo;
import com.gieselaar.verzuimbeheer.services.DocumentTemplateInfo;
import com.gieselaar.verzuimbeheer.services.GebruikerInfo;
import com.gieselaar.verzuimbeheer.services.GebruikerInfo.__status;
import com.gieselaar.verzuimbeheer.services.HoldingInfo;
import com.gieselaar.verzuimbeheer.services.HoldingInfo.__factuurtype;
import com.gieselaar.verzuimbeheer.services.InfoBase;
import com.gieselaar.verzuimbeheer.services.InfoBase.__burgerlijkestaat;
import com.gieselaar.verzuimbeheer.services.InfoBase.__geslacht;
import com.gieselaar.verzuimbeheer.services.SettingsInfo;
import com.gieselaar.verzuimbeheer.services.TodoInfo.__soort;
import com.gieselaar.verzuimbeheer.services.UitvoeringsinstituutInfo;
import com.gieselaar.verzuimbeheer.services.VerzuimInfo.__gerelateerdheid;
import com.gieselaar.verzuimbeheer.services.VerzuimInfo.__meldingswijze;
import com.gieselaar.verzuimbeheer.services.VerzuimInfo.__vangnettype;
import com.gieselaar.verzuimbeheer.services.VerzuimInfo.__verzuimtype;
import com.gieselaar.verzuimbeheer.services.WerkgeverInfo;
import com.gieselaar.verzuimbeheer.services.WerkzaamhedenInfo.__huisbezoekurgentie;
import com.gieselaar.verzuimbeheer.services.WerkzaamhedenInfo.__verzuimsoort;
import com.gieselaar.verzuimbeheer.services.WerkzaamhedenInfo.__werkzaamhedensoort;
import com.gieselaar.verzuimbeheer.services.WiapercentageInfo.__wiapercentage;
import com.gieselaar.verzuimbeheer.utils.ServiceLocator;
import com.gieselaar.verzuimbeheer.utils.ServiceLocatorException;
import com.gieselaar.verzuimbeheer.utils.TypeEntry;
import com.ibm.icu.util.Calendar;

public class MDIMainController extends AbstractController implements ActionListener {

	private static final long serialVersionUID = 1L;
	private MDIMainModel mainmodel;

	public MDIMainController(MDIMainModel model, JDesktopPane desktoppane) {
		super(model, desktoppane);
		mainmodel = model;
	}

	public enum __menucommands {
		UNKNOWN(-1), 
		
		WERKNEMEROVERZICHT(1), WERKNEMERNIEUW(2), APPLICATIONCLOSE(3), APPLICATIONSETTINGS(4),
		
		WERKGEVEROVERZICHT(5), WERKGEVERTODO(6), WERKGEVERHOLDING(7), WERKGEVERRAPPORTAGENIVEAUS(8), 
		WERKGEVERTARIEVEN(9), WERKGEVERRAPPORTAGESTRUCTUUR(10), WERKGEVERIMPORTEERWERKNEMERS(11), 
		WERKGEVERIMPORTEERUREN(12), WERKGEVERAFSLUITENDIENSTVERBANDEN(13),
		
		TABELLENPAKKETTEN(20), TABELLENACTIVITEITEN(21), TABELLENDOCUMENTTEMPLATES(22), 
		TABELLENCASCODES(23), TABELLENCASCODEGROEPEN(24), 
		
		INSTELLINGENARBODIENSTEN(30), INSTELLINGENUITVOERINGSINSTANTIES(31), INSTELLINGENBEDRIJFSGEGEVENS(32),
		
		AUTORISATIEAPPLICATIEFUNCTIES(40), AUTORISATIEROLLEN(41), AUTORISATIEGEBRUIKERS(42), 
		
		REPORTVERZUIMOVERZICHT(50), REPORTFREQUENTVERZUIM(51), REPORTVERZUIMHISTORIE(52), REPORTACTUEELVERZUIM(53), 
		
		WERKZAAMHEDENOVERZICHT(60), 
		
		BTWPERCENTAGES(70), IMPORTEERBETALINGEN(71), BTWENOMZET(72), FACTUUROVERZICHT(73), 
		AFDRUKKENVERZENDEN(74), TERUGDRAAIENMAAND(75), AFSLUITENMAAND(76), FACTUURITEMS(77), 
		FACTUURCATEGORIEN(78), FACTUURKOPPEN(79);
		
		private int value;

		__menucommands(int value) {
			this.value = value;
		}

		public int getValue() {
			return value;
		}

		public static __menucommands parse(int type) {
			__menucommands field = null; // Default
			for (__menucommands item : __menucommands.values()) {
				if (item.getValue() == type) {
					field = item;
					break;
				}
			}
			return field;
		}

		public static __menucommands parse(String type) {
			__menucommands field = __menucommands.UNKNOWN; // Default
			for (__menucommands item : __menucommands.values()) {
				if (item.toString().equals(type)) {
					field = item;
					break;
				}
			}
			return field;
		}
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		switch (__menucommands.parse(e.getActionCommand())) {
		case WERKNEMERNIEUW:
			createWerknemerDetail();
			break;
		case WERKNEMEROVERZICHT:
			createWerknemerOverzicht();
			break;
		case APPLICATIONCLOSE:
			closeApplication();
			break;
		case APPLICATIONSETTINGS:
			createApplicationSettings();
			break;
		case WERKGEVEROVERZICHT:
			createWerkgeverOverzicht();
			break;
		case WERKGEVERTODO:
			createTodoOverzicht();
			break;
		case WERKGEVERTARIEVEN:
			createTarievenOverzicht();
			break;
		case WERKGEVERHOLDING:
			createHoldingOverzicht();
			break;
		case WERKGEVERRAPPORTAGENIVEAUS:
			createOeNiveausOverzicht();
			break;
		case WERKGEVERRAPPORTAGESTRUCTUUR:
			createOeOverzicht();
			break;
		case TABELLENPAKKETTEN:
			createPakketOverzicht();
			break;
		case TABELLENACTIVITEITEN:
			createActiviteitenOverzicht();
			break;
		case TABELLENCASCODEGROEPEN:
			createCascodegroepenOverzicht();
			break;
		case TABELLENCASCODES:
			createCascodesOverzicht();
			break;
		case TABELLENDOCUMENTTEMPLATES:
			createDocumenttemplateOverzicht();
			break;
		case INSTELLINGENARBODIENSTEN:
			createArbodienstenOverzicht();
			break;
		case INSTELLINGENUITVOERINGSINSTANTIES:
			createUitvoeringsinstantiesOverzicht();
			break;
		case INSTELLINGENBEDRIJFSGEGEVENS:
			createBedrijfsgegevensDetail();
			break;
		case AUTORISATIEAPPLICATIEFUNCTIES:
			createApplicatiefunctiesOverzicht();
			break;
		case AUTORISATIEGEBRUIKERS:
			createGebruikersOverzicht();
			break;
		case AUTORISATIEROLLEN:
			createRollenOverzicht();
			break;
		case REPORTACTUEELVERZUIM:
			createReportActueelverzuim();
			break;
		case REPORTFREQUENTVERZUIM:
			createReportFrequentverzuim();
			break;
		case REPORTVERZUIMHISTORIE:
			createReportVerzuimhistorie();
			break;
		case REPORTVERZUIMOVERZICHT:
			createReportVerzuimoverzicht();
			break;
		case WERKZAAMHEDENOVERZICHT:
			createWerkzaamhedenOverzicht();
			break;
		case AFDRUKKENVERZENDEN:
			createAfdrukkenVerzenden();
			break;
		case AFSLUITENMAAND:
			createAfsluitenMaand();
			break;
		case TERUGDRAAIENMAAND:
			createTerugdraaienMaand();
			break;
		case BTWENOMZET:
			createBtwEnOmzetOverzicht();
			break;
		case BTWPERCENTAGES:
			createBtwpercentageOverzicht();
			break;
		case FACTUURCATEGORIEN:
			createFactuurcategorienOverzicht();
			break;
		case FACTUURITEMS:
			createFactuuritemOverzicht();
			break;
		case FACTUURKOPPEN:
			createFactuurkoppenOverzicht();
			break;
		case FACTUUROVERZICHT:
			createFactuuroverzicht();
			break;
		case IMPORTEERBETALINGEN:
			createImporteerBetalingen();
			break;
		case WERKGEVERAFSLUITENDIENSTVERBANDEN:
			createAfsluitenDienstverbanden();
			break;
		case WERKGEVERIMPORTEERUREN:
			createImporteerUren();
			break;
		case WERKGEVERIMPORTEERWERKNEMERS:
			createImporteerWerknemers();
			break;
		default:
			break;
		}
	}
	private void createAfsluitenDienstverbanden() {
		ImportController controller = new ImportController(this.getModel().getSession());
		controller.setDesktoppane(getDesktoppane());
		controller.setMaincontroller(this);
		AfsluitenDienstverbanden frame = new AfsluitenDienstverbanden(controller);
		frame.setData(null);
		frame.setVisible(true);
		this.getDesktoppane().add(frame);
		this.getDesktoppane().setOpaque(true);
		this.getDesktoppane().moveToFront(frame);
	}
	private void createImporteerUren() {
		ImportController controller = new ImportController(this.getModel().getSession());
		controller.setDesktoppane(getDesktoppane());
		controller.setMaincontroller(this);
		ImportUren frame = new ImportUren(controller);
		controller.setDetailform(frame);
		frame.setData(null);
		frame.setVisible(true);
		this.getDesktoppane().add(frame);
		this.getDesktoppane().setOpaque(true);
		this.getDesktoppane().moveToFront(frame);
	}
	private void createImporteerWerknemers() {
		ImportController controller = new ImportController(this.getModel().getSession());
		controller.setDesktoppane(getDesktoppane());
		controller.setMaincontroller(this);
		ImportWerknemers frame = new ImportWerknemers(controller);
		controller.setDetailform(frame);
		frame.setData(null);
		frame.setVisible(true);
		this.getDesktoppane().add(frame);
		this.getDesktoppane().setOpaque(true);
		this.getDesktoppane().moveToFront(frame);
	}
	private void createImporteerBetalingen() {
		ImportbetalingenController controller = new ImportbetalingenController(this.getModel().getSession());
		controller.setDesktoppane(getDesktoppane());
		controller.setMaincontroller(this);
		ImportBetalingen frame = new ImportBetalingen(controller);
		controller.setDetailform(frame);
		frame.setData(null);
		frame.setVisible(true);
		this.getDesktoppane().add(frame);
		this.getDesktoppane().setOpaque(true);
		this.getDesktoppane().moveToFront(frame);
	}
	private void createBtwEnOmzetOverzicht(){
		ReportController controller = new ReportController(this.getModel().getSession());
		controller.setDesktoppane(getDesktoppane());
		controller.setMaincontroller(this);
		ReportBTWOmzet frame = new ReportBTWOmzet(controller);
		controller.setDetailform(frame);
		frame.setData(null);
		frame.setVisible(true);
		this.getDesktoppane().add(frame);
		this.getDesktoppane().setOpaque(true);
		this.getDesktoppane().moveToFront(frame);
	}
	private void createTerugdraaienMaand() {
		FactuurController controller = new FactuurController(this.getModel().getSession());
		controller.setDesktoppane(getDesktoppane());
		controller.setMaincontroller(this);
		TerugdraaienMaandDialog frame = new TerugdraaienMaandDialog(controller);
		controller.setDetailform(frame);
		frame.setData(null);
		frame.setVisible(true);
		this.getDesktoppane().add(frame);
		this.getDesktoppane().setOpaque(true);
		this.getDesktoppane().moveToFront(frame);
	}
	private void createAfsluitenMaand() {
		FactuurController controller = new FactuurController(this.getModel().getSession());
		controller.setDesktoppane(getDesktoppane());
		controller.setMaincontroller(this);
		AfsluitenMaandDialog frame = new AfsluitenMaandDialog(controller);
		controller.setDetailform(frame);
		frame.setData(null);
		frame.setVisible(true);
		this.getDesktoppane().add(frame);
		this.getDesktoppane().setOpaque(true);
		this.getDesktoppane().moveToFront(frame);
	}
	private void createAfdrukkenVerzenden() {
		FactuurController controller = new FactuurController(this.getModel().getSession());
		controller.setDesktoppane(getDesktoppane());
		controller.setMaincontroller(this);
		AfdrukkenMaandDialog frame = new AfdrukkenMaandDialog(controller);
		controller.setDetailform(frame);
		frame.setData(null);
		frame.setVisible(true);
		this.getDesktoppane().add(frame);
		this.getDesktoppane().setOpaque(true);
		this.getDesktoppane().moveToFront(frame);
	}
	private void createBtwpercentageOverzicht() {
		BtwController controller = new BtwController(this.getModel().getSession());
		controller.setDesktoppane(getDesktoppane());
		controller.setMaincontroller(this);
		BtwList frame = new BtwList(controller);
		controller.setListform(frame);
		try {
			controller.selectBtw();
		} catch (VerzuimApplicationException e) {
			ExceptionLogger.ProcessException(e, this.getDesktoppane());
		}
		frame.setData(null);
		frame.setVisible(true);
		this.getDesktoppane().add(frame);
		this.getDesktoppane().setOpaque(true);
		this.getDesktoppane().moveToFront(frame);
	}
	private void createFactuurkoppenOverzicht() {
		FactuurkopController controller = new FactuurkopController(this.getModel().getSession());
		controller.setDesktoppane(getDesktoppane());
		controller.setMaincontroller(this);
		FactuurkopList frame = new FactuurkopList(controller);
		controller.setListform(frame);
		try {
			controller.selectFactuurkoppen();
		} catch (VerzuimApplicationException e) {
			ExceptionLogger.ProcessException(e, this.getDesktoppane());
		}
		frame.setData(null);
		frame.setVisible(true);
		this.getDesktoppane().add(frame);
		this.getDesktoppane().setOpaque(true);
		this.getDesktoppane().moveToFront(frame);
	}

	private void createFactuuritemOverzicht() {
		FactuuritemController controller = new FactuuritemController(this.getModel().getSession());
		controller.setDesktoppane(getDesktoppane());
		controller.setMaincontroller(this);
		FactuuritemList frame = new FactuuritemList(controller);
		controller.setListform(frame);
		try {
			Calendar cal = Calendar.getInstance();
			Date enddate = cal.getTime();
			cal.set(Calendar.DATE, 1);
			controller.selectFactuuritems(cal.getTime(), enddate);
		} catch (VerzuimApplicationException | ValidationException e) {
			ExceptionLogger.ProcessException(e, this.getDesktoppane());
		}
		frame.setVisible(true);
		this.getDesktoppane().add(frame);
		this.getDesktoppane().setOpaque(true);
		this.getDesktoppane().moveToFront(frame);
	}

	private void createFactuurcategorienOverzicht() {
		FactuurcategorieController controller = new FactuurcategorieController(this.getModel().getSession());
		controller.setDesktoppane(getDesktoppane());
		controller.setMaincontroller(this);
		FactuurcategorieList frame = new FactuurcategorieList(controller);
		controller.setListform(frame);
		try {
			controller.selectFactuurcategorien();
		} catch (VerzuimApplicationException e) {
			ExceptionLogger.ProcessException(e, this.getDesktoppane());
		}
		frame.setData(null);
		frame.setVisible(true);
		this.getDesktoppane().add(frame);
		this.getDesktoppane().setOpaque(true);
		this.getDesktoppane().moveToFront(frame);
	}

	private void createFactuuroverzicht() {
		FactuurController controller = new FactuurController(this.getModel().getSession());
		controller.setDesktoppane(getDesktoppane());
		controller.setMaincontroller(this);
		FactuurList frame = new FactuurList(controller);
		controller.setListform(frame);
		try {
			Calendar cal = Calendar.getInstance();
			cal.set(Calendar.DATE, 1);
			cal.set(Calendar.HOUR, 0);
			cal.set(Calendar.MINUTE, 0);
			cal.set(Calendar.SECOND, 0);
			cal.add(Calendar.MONTH, -1);
			
			controller.selectFacturen(cal.getTime(), cal.getTime());
		} catch (VerzuimApplicationException e) {
			ExceptionLogger.ProcessException(e, this.getDesktoppane());
		}
		frame.setVisible(true);
		this.getDesktoppane().add(frame);
		this.getDesktoppane().setOpaque(true);
		this.getDesktoppane().moveToFront(frame);
	}

	private void createWerkzaamhedenOverzicht() {
		WerkzaamhedenController controller = new WerkzaamhedenController(this.getModel().getSession());
		controller.setDesktoppane(getDesktoppane());
		controller.setMaincontroller(this);
		WerkzaamhedenList frame = new WerkzaamhedenList(controller);
		controller.setListform(frame);
		try {
			Calendar cal = Calendar.getInstance();
			Date enddate = cal.getTime();
			cal.set(Calendar.DATE, 1);
			controller.selectWerkzaamheden(getGebruiker().getId(),cal.getTime(), enddate);
		} catch (VerzuimApplicationException | ValidationException e) {
			ExceptionLogger.ProcessException(e, this.getDesktoppane());
		}
		frame.setVisible(true);
		this.getDesktoppane().add(frame);
		this.getDesktoppane().setOpaque(true);
		this.getDesktoppane().moveToFront(frame);
	}

	private void createReportVerzuimoverzicht() {
		ReportController controller = new ReportController(this.getModel().getSession());
		controller.setDesktoppane(getDesktoppane());
		controller.setMaincontroller(this);
		ReportVerzuimoverzicht frame = new ReportVerzuimoverzicht(controller);
		controller.setDetailform(frame);
		frame.setData(null);
		frame.setVisible(true);
		this.getDesktoppane().add(frame);
		this.getDesktoppane().setOpaque(true);
		this.getDesktoppane().moveToFront(frame);
	}

	private void createReportVerzuimhistorie() {
		ReportController controller = new ReportController(this.getModel().getSession());
		controller.setDesktoppane(getDesktoppane());
		controller.setMaincontroller(this);
		ReportVerzuimenHistorie frame = new ReportVerzuimenHistorie(controller);
		controller.setDetailform(frame);
		frame.setData(null, null);
		frame.setVisible(true);
		this.getDesktoppane().add(frame);
		this.getDesktoppane().setOpaque(true);
		this.getDesktoppane().moveToFront(frame);
	}

	private void createReportFrequentverzuim() {
		ReportController controller = new ReportController(this.getModel().getSession());
		controller.setDesktoppane(getDesktoppane());
		controller.setMaincontroller(this);
		ReportFrequentVerzuimers frame = new ReportFrequentVerzuimers(controller);
		controller.setDetailform(frame);
		frame.setData(null);
		frame.setVisible(true);
		this.getDesktoppane().add(frame);
		this.getDesktoppane().setOpaque(true);
		this.getDesktoppane().moveToFront(frame);
	}

	private void createReportActueelverzuim() {
		ReportController controller = new ReportController(this.getModel().getSession());
		controller.setDesktoppane(getDesktoppane());
		controller.setMaincontroller(this);
		ReportActueelVerzuim frame = new ReportActueelVerzuim(controller);
		controller.setDetailform(frame);
		try {
			controller.selectOes();
			frame.setData(null);
		} catch (VerzuimApplicationException e) {
			ExceptionLogger.ProcessException(e, this.getDesktoppane());
		}
		frame.setVisible(true);
		this.getDesktoppane().add(frame);
		this.getDesktoppane().setOpaque(true);
		this.getDesktoppane().moveToFront(frame);
	}

	private void createOeOverzicht() {
		OeController controller = new OeController(this.getModel().getSession());
		controller.setDesktoppane(getDesktoppane());
		controller.setMaincontroller(this);
		OeList frame = new OeList(controller);
		controller.setDetailform(frame);
		try {
			controller.selectOes();
			controller.selectOeNiveaus();
			frame.setData(null);
		} catch (VerzuimApplicationException e) {
			ExceptionLogger.ProcessException(e, this.getDesktoppane());
		}
		frame.setVisible(true);
		this.getDesktoppane().add(frame);
		this.getDesktoppane().setOpaque(true);
		this.getDesktoppane().moveToFront(frame);
	}

	private void createOeNiveausOverzicht() {
		OeniveauController controller = new OeniveauController(this.getModel().getSession());
		controller.setDesktoppane(getDesktoppane());
		controller.setMaincontroller(this);
		OeNiveauList frame = new OeNiveauList(controller);
		controller.setDetailform(frame);
		try {
			controller.selectOeNiveaus();
			frame.setData(null);
		} catch (VerzuimApplicationException e) {
			ExceptionLogger.ProcessException(e, this.getDesktoppane());
		}
		frame.setVisible(true);
		this.getDesktoppane().add(frame);
		this.getDesktoppane().setOpaque(true);
		this.getDesktoppane().moveToFront(frame);
	}

	private void createRollenOverzicht() {
		RolController controller = new RolController(this.getModel().getSession());
		controller.setDesktoppane(getDesktoppane());
		controller.setMaincontroller(this);
		RolList frame = new RolList(controller);
		controller.setListform(frame);
		try {
			controller.selectRollen();
		} catch (VerzuimApplicationException e) {
			ExceptionLogger.ProcessException(e, this.getDesktoppane());
		}
		frame.setVisible(true);
		this.getDesktoppane().add(frame);
		this.getDesktoppane().setOpaque(true);
		this.getDesktoppane().moveToFront(frame);
	}

	private void createGebruikersOverzicht() {
		GebruikerController controller = new GebruikerController(this.getModel().getSession());
		controller.setDesktoppane(getDesktoppane());
		controller.setMaincontroller(this);
		GebruikerList frame = new GebruikerList(controller);
		controller.setListform(frame);
		try {
			controller.selectGebruikers();
		} catch (VerzuimApplicationException e) {
			ExceptionLogger.ProcessException(e, this.getDesktoppane());
		}
		frame.setVisible(true);
		this.getDesktoppane().add(frame);
		this.getDesktoppane().setOpaque(true);
		this.getDesktoppane().moveToFront(frame);
	}

	private void createApplicatiefunctiesOverzicht() {
		ApplicatiefunctieController controller = new ApplicatiefunctieController(this.getModel().getSession());
		controller.setDesktoppane(getDesktoppane());
		controller.setMaincontroller(this);
		ApplicatieFunctieList frame = new ApplicatieFunctieList(controller);
		controller.setListform(frame);
		try {
			controller.selectApplicatiefuncties();
		} catch (VerzuimApplicationException e) {
			ExceptionLogger.ProcessException(e, this.getDesktoppane());
		}
		frame.setVisible(true);
		this.getDesktoppane().add(frame);
		this.getDesktoppane().setOpaque(true);
		this.getDesktoppane().moveToFront(frame);
	}

	private void createBedrijfsgegevensDetail() {
		BedrijfsgegevensController controller = new BedrijfsgegevensController(this.getModel().getSession());
		controller.setDesktoppane(getDesktoppane());
		controller.setMaincontroller(this);
		try {
			controller.selectBedrijfsgegevens();
		} catch (VerzuimApplicationException e) {
			ExceptionLogger.ProcessException(e, this.getDesktoppane());
		}
		BedrijfsgegevensInfo gegevens = controller.getBedrijfsgegevens();
		if (gegevens == null){
			controller.addRow(null);
		}else{
			controller.showRow(null,gegevens);
		}
	}

	private void createUitvoeringsinstantiesOverzicht() {
		UitkeringsinstituutController controller = new UitkeringsinstituutController(this.getModel().getSession());
		controller.setDesktoppane(getDesktoppane());
		controller.setMaincontroller(this);
		UitkeringsinstantieList frame = new UitkeringsinstantieList(controller);
		controller.setListform(frame);
		try {
			controller.selectUitkeringsinstituten();
		} catch (VerzuimApplicationException e) {
			ExceptionLogger.ProcessException(e, this.getDesktoppane());
		}
		frame.setVisible(true);
		this.getDesktoppane().add(frame);
		this.getDesktoppane().setOpaque(true);
		this.getDesktoppane().moveToFront(frame);
	}

	private void createArbodienstenOverzicht() {
		ArbodienstController controller = new ArbodienstController(this.getModel().getSession());
		controller.setDesktoppane(getDesktoppane());
		controller.setMaincontroller(this);
		ArbodienstList frame = new ArbodienstList(controller);
		controller.setListform(frame);
		try {
			controller.selectArbodiensten();
		} catch (VerzuimApplicationException e) {
			ExceptionLogger.ProcessException(e, this.getDesktoppane());
		}
		frame.setVisible(true);
		this.getDesktoppane().add(frame);
		this.getDesktoppane().setOpaque(true);
		this.getDesktoppane().moveToFront(frame);
	}

	private void createDocumenttemplateOverzicht() {
		DocumenttemplateController controller = new DocumenttemplateController(this.getModel().getSession());
		controller.setDesktoppane(getDesktoppane());
		controller.setMaincontroller(this);
		DocumentTemplateList frame = new DocumentTemplateList(controller);
		controller.setListform(frame);
		try {
			controller.selectDocumenttemplates();
		} catch (VerzuimApplicationException e) {
			ExceptionLogger.ProcessException(e, this.getDesktoppane());
		}
		frame.setVisible(true);
		this.getDesktoppane().add(frame);
		this.getDesktoppane().setOpaque(true);
		this.getDesktoppane().moveToFront(frame);
	}

	private void createCascodesOverzicht() {
		CascodeController controller = new CascodeController(this.getModel().getSession());
		controller.setDesktoppane(getDesktoppane());
		controller.setMaincontroller(this);
		CascodeList frame = new CascodeList(controller);
		controller.setListform(frame);
		try {
			controller.selectCascodes(-1);
		} catch (VerzuimApplicationException e) {
			ExceptionLogger.ProcessException(e, this.getDesktoppane());
		}
		frame.setVisible(true);
		this.getDesktoppane().add(frame);
		this.getDesktoppane().setOpaque(true);
		this.getDesktoppane().moveToFront(frame);
	}

	private void createCascodegroepenOverzicht() {
		CascodegroepController controller = new CascodegroepController(this.getModel().getSession());
		controller.setDesktoppane(getDesktoppane());
		controller.setMaincontroller(this);
		CascodeGroepList frame = new CascodeGroepList(controller);
		controller.setListform(frame);
		try {
			controller.selectCascodegroepen();
		} catch (VerzuimApplicationException e) {
			ExceptionLogger.ProcessException(e, this.getDesktoppane());
		}
		frame.setVisible(true);
		this.getDesktoppane().add(frame);
		this.getDesktoppane().setOpaque(true);
		this.getDesktoppane().moveToFront(frame);
	}

	private void createActiviteitenOverzicht() {
		ActiviteitController controller = new ActiviteitController(this.getModel().getSession());
		controller.setDesktoppane(getDesktoppane());
		controller.setMaincontroller(this);
		ActiviteitList frame = new ActiviteitList(controller);
		controller.setListform(frame);
		try {
			controller.selectActiviteiten();
		} catch (VerzuimApplicationException e) {
			ExceptionLogger.ProcessException(e, this.getDesktoppane());
		}
		frame.setVisible(true);
		this.getDesktoppane().add(frame);
		this.getDesktoppane().setOpaque(true);
		this.getDesktoppane().moveToFront(frame);
	}

	private void createPakketOverzicht() {
		PakketController controller = new PakketController(this.getModel().getSession());
		controller.setDesktoppane(getDesktoppane());
		controller.setMaincontroller(this);
		PakketList frame = new PakketList(controller);
		controller.setListform(frame);
		try {
			controller.selectPakketten();
		} catch (VerzuimApplicationException e) {
			ExceptionLogger.ProcessException(e, this.getDesktoppane());
		}
		frame.setVisible(true);
		this.getDesktoppane().add(frame);
		this.getDesktoppane().setOpaque(true);
		this.getDesktoppane().moveToFront(frame);
	}

	private void createHoldingOverzicht() {
		HoldingController controller = new HoldingController(this.getModel().getSession());
		controller.setDesktoppane(getDesktoppane());
		controller.setMaincontroller(this);
		HoldingList frame = new HoldingList(controller);
		controller.setListform(frame);
		controller.selectHoldings();
		frame.setVisible(true);
		this.getDesktoppane().add(frame);
		this.getDesktoppane().setOpaque(true);
		this.getDesktoppane().moveToFront(frame);
	}

	private void createApplicationSettings() {
		SettingsController controller = new SettingsController(this.getModel().getSession());
		controller.setDesktoppane(getDesktoppane());
		controller.setMaincontroller(this);
		SettingsInfo settings = getSettings();
		if (settings == null){
			controller.addRow(null);
		}else{
			controller.showRow(null,settings);
		}
	}

	private void closeApplication() {
		try {
			ServiceLocator.getInstance().release();
		} catch (ServiceLocatorException e1) {
			ExceptionLogger.ProcessException(e1, null, e1.getMessage());
		}
	}

	private void createTodoOverzicht() {
		TodoController controller = new TodoController(this.getModel().getSession());
		controller.setDesktoppane(getDesktoppane());
		controller.setMaincontroller(this);
		TodoList frame = new TodoList(controller);
		controller.setListform(frame);
		controller.selectTodos();
		frame.setVisible(true);
		this.getDesktoppane().add(frame);
		this.getDesktoppane().setOpaque(true);
		this.getDesktoppane().moveToFront(frame);
	}
	private void createTarievenOverzicht(){
		TariefController controller = new TariefController(this.getModel().getSession());
		controller.setDesktoppane(getDesktoppane());
		controller.setMaincontroller(this);
		TariefList frame = new TariefList(controller);
		controller.setListform(frame);
		try {
			controller.setWerkgever(null);
			controller.setHolding(null);
			controller.selectTarieven();
		} catch (VerzuimApplicationException e) {
			ExceptionLogger.ProcessException(e, this.getDesktoppane());
		}
		frame.setVisible(true);
		this.getDesktoppane().add(frame);
		this.getDesktoppane().setOpaque(true);
		this.getDesktoppane().moveToFront(frame);
		
	}
	private void createWerknemerDetail() {
		WerknemerController controller = new WerknemerController(this.getModel().getSession());
		controller.setDesktoppane(getDesktoppane());
		controller.setMaincontroller(this);
		controller.addRow(null);
	}

	private void createWerknemerOverzicht() {
		WerknemerController controller = new WerknemerController(this.getModel().getSession());
		controller.setDesktoppane(getDesktoppane());
		controller.setMaincontroller(this);
		WerknemerList frame = new WerknemerList(controller);
		controller.setListform(frame);
		controller.selectWerknemers();
		frame.setVisible(true);
		this.getDesktoppane().add(frame);
		this.getDesktoppane().setOpaque(true);
		this.getDesktoppane().moveToFront(frame);
	}
	private void createWerkgeverOverzicht(){
		WerkgeverController controller = new WerkgeverController(this.getModel().getSession());
		controller.setDesktoppane(getDesktoppane());
		controller.setMaincontroller(this);
		WerkgeverList frame = new WerkgeverList(controller);
		controller.setListform(frame);
		controller.getWerkgevers();
		frame.setVisible(true);
		this.getDesktoppane().add(frame);
		this.getDesktoppane().setOpaque(true);
		this.getDesktoppane().moveToFront(frame);
	}
	public void logoff() {
		// ServiceLocator.getInstance().release();
		this.getModel().getSession().logoffSession();
	}

	public void logon() {
		try {
			mainmodel.refreshDatabase();
		} catch (VerzuimApplicationException e) {
			ExceptionLogger.ProcessException(e, this.getDesktoppane());
		}
	}

	@Override
	public void listComplete(Object data) {/* noop */}

	@Override
	public void rowUpdated(Object data) {/* noop */}

	@Override
	public void rowAdded(Object data) {/* noop */}

	public void modelUpdated(Class<?> updatedclass) {
		try {
			mainmodel.modelUpdated(updatedclass);
		} catch (VerzuimApplicationException e) {
			ExceptionLogger.ProcessException(e, this.getDesktoppane());
		}
	}

	public List<CascodeInfo> getCascodes() {
		return mainmodel.getCascodes();
	}

	public List<ActiviteitInfo> getActiviteiten() {
		return mainmodel.getActiviteiten();
	}

	public List<GebruikerInfo> getGebruikers() {
		return mainmodel.getGebruikers();
	}

	public List<DocumentTemplateInfo> getDocumentTemplates() {
		return mainmodel.getDocumentTemplates();
	}

	@Override
	protected boolean isDeleteAllowed(InfoBase data) {
		return false;
	}

	@Override
	protected boolean isShowAllowed(InfoBase data) {
		return false;
	}

	@Override
	protected boolean isNewAllowed() {
		return false;
	}

	@Override
	public void showRow(ControllerEventListener ves, Object data) {/* noop */}

	@Override
	public void addRow(ControllerEventListener ves) {/* noop */}

	public void updateComboModelWerkgevers(VerzuimComboBoxModel werkgevermodel, boolean activeonly) {
		/* Just a utility function to update ComboBoxModel */
		werkgevermodel.removeAllElements();
		List<WerkgeverInfo> werkgevers = mainmodel.getWerkgevers();
		werkgevermodel.addElement(new TypeEntry(-1, "[]"));
		WerkgeverInfo.sort(werkgevers, WerkgeverInfo.__sortcol.NAAM);
		for (WerkgeverInfo w : werkgevers) {
			TypeEntry wg = new TypeEntry(w.getId(), w.getNaam());
			if (activeonly) {
				if (w.isActief()) {
					werkgevermodel.addElement(wg);
				}
			} else {
				werkgevermodel.addElement(wg);
			}
		}
	}
	public void updateComboModelCascodes(VerzuimComboBoxModel model, boolean b) {
		updateComboModelCascodes(model, b, null);
	}
	public void updateComboModelCascodes(VerzuimComboBoxModel cascodemodel, boolean activeonly, Integer cascodetoadd) {
		/* Just a utility function to update ComboBoxModel */
		cascodemodel.removeAllElements();
		List<CascodeInfo> werkgevers = mainmodel.getCascodes();
		cascodemodel.addElement(new TypeEntry(-1, "[]"));
		CascodeInfo.sort(werkgevers, CascodeInfo.__sortcol.NAAM);
		for (CascodeInfo cascode : werkgevers) {
			TypeEntry te = new TypeEntry(cascode.getId(), cascode.getOmschrijving());
			if (activeonly) {
				if (cascode.isActief() || cascode.getId().equals(cascodetoadd)) {
					cascodemodel.addElement(te);
				}
			} else {
				cascodemodel.addElement(te);
			}
		}
	}

	public void updateComboModelDocumentTemplates(VerzuimComboBoxModel templatesmodel) {
		/* Just a utility function to update ComboBoxModel */
		templatesmodel.removeAllElements();
		List<DocumentTemplateInfo> templates = mainmodel.getDocumentTemplates();
		templatesmodel.addElement(new TypeEntry(-1, "[]"));
		DocumentTemplateInfo.sort(templates, DocumentTemplateInfo.__sortcol.NAAM);
		for (DocumentTemplateInfo w : templates) {
			TypeEntry wg = new TypeEntry(w.getId(), w.getNaam());
			templatesmodel.addElement(wg);
		}
	}
	public void updateComboModelActiviteiten(VerzuimComboBoxModel activiteitenmodel) {
		/* Just a utility function to update ComboBoxModel */
		activiteitenmodel.removeAllElements();
		List<ActiviteitInfo> activiteiten = mainmodel.getActiviteiten();
		activiteitenmodel.addElement(new TypeEntry(-1, "[]"));
		ActiviteitInfo.sort(activiteiten, ActiviteitInfo.__sortcol.NAAM);
		for (ActiviteitInfo w : activiteiten) {
			TypeEntry wg = new TypeEntry(w.getId(), w.getNaam());
			activiteitenmodel.addElement(wg);
		}
	}
	public void updateComboModelUitkeringsinstanties(VerzuimComboBoxModel model) {
		model.removeAllElements();
		List<UitvoeringsinstituutInfo> list = mainmodel.getUitkeringsinstanties();
		model.addElement(new TypeEntry(-1, "[]"));
		for (UitvoeringsinstituutInfo w : list) {
			TypeEntry wg = new TypeEntry(w.getId(), w.getNaam());
			model.addElement(wg);
		}
	}

	public void updateComboModelBedrijfsartsen(VerzuimComboBoxModel model, Integer arbodienstid) {
		model.removeAllElements();
		List<BedrijfsartsInfo> list = mainmodel.getBedrijfsartsen(arbodienstid);
		model.addElement(new TypeEntry(-1, "[]"));
		for (BedrijfsartsInfo w : list) {
			TypeEntry wg = new TypeEntry(w.getId(), w.getAchternaam());
			model.addElement(wg);
		}
	}

	public void updateComboModelArbodiensten(VerzuimComboBoxModel model) {
		model.removeAllElements();
		List<ArbodienstInfo> list = mainmodel.getArbodiensten();
		model.addElement(new TypeEntry(-1, "[]"));
		for (ArbodienstInfo w : list) {
			TypeEntry wg = new TypeEntry(w.getId(), w.getNaam());
			model.addElement(wg);
		}
	}

	public void updateComboModelHoldings(VerzuimComboBoxModel model, boolean b) {
		model.removeAllElements();
		List<HoldingInfo> list = mainmodel.getHoldings();
		HoldingInfo.sort(list, HoldingInfo.__sortcol.NAAM);
		model.addElement(new TypeEntry(-1, "[]"));
		for (HoldingInfo w : list) {
			TypeEntry wg = new TypeEntry(w.getId(), w.getNaam());
			model.addElement(wg);
		}
	}

	public VerzuimComboBoxModel getEnumModel(Class<?> enumclass) {
		VerzuimComboBoxModel model = new VerzuimComboBoxModel(this);
		if (enumclass == __geslacht.class) {
			for (__geslacht g : __geslacht.values()) {
				TypeEntry soort = new TypeEntry(g.getValue(), g.toString());
				model.addElement(soort);
			}
			return model;
		}
		if (enumclass == __burgerlijkestaat.class) {
			for (__burgerlijkestaat g : __burgerlijkestaat.values()) {
				TypeEntry soort = new TypeEntry(g.getValue(), g.toString());
				model.addElement(soort);
			}
			return model;
		}
		if (enumclass == __soort.class) {
			for (__soort g : __soort.values()) {
				TypeEntry soort = new TypeEntry(g.getValue(), g.toString());
				model.addElement(soort);
			}
			return model;
		}

		if (enumclass == __vangnettype.class) {
			for (__vangnettype g : __vangnettype.values()) {
				TypeEntry soort = new TypeEntry(g.getValue(), g.toString());
				model.addElement(soort);
			}
			return model;
		}
		if (enumclass == __verzuimtype.class) {
			for (__verzuimtype g : __verzuimtype.values()) {
				TypeEntry soort = new TypeEntry(g.getValue(), g.toString());
				model.addElement(soort);
			}
			return model;
		}
		if (enumclass == __gerelateerdheid.class) {
			for (__gerelateerdheid g : __gerelateerdheid.values()) {
				TypeEntry soort = new TypeEntry(g.getValue(), g.toString());
				model.addElement(soort);
			}
			return model;
		}
		if (enumclass == __meldingswijze.class) {
			for (__meldingswijze g : __meldingswijze.values()) {
				TypeEntry soort = new TypeEntry(g.getValue(), g.toString());
				model.addElement(soort);
			}
			return model;
		}
		if (enumclass == __wiapercentage.class) {
			for (__wiapercentage g : __wiapercentage.values()) {
				TypeEntry soort = new TypeEntry(g.getValue(), g.toString());
				model.addElement(soort);
			}
			return model;
		}
		if (enumclass == __factuurtype.class){
			for (__factuurtype g: __factuurtype.values()){
				TypeEntry soort = new TypeEntry(g.getValue(), g.toString());
				model.addElement(soort);
			}
			return model;
		}
		if (enumclass == __periodesoort.class){
			for (__periodesoort g: __periodesoort.values()){
				TypeEntry soort = new TypeEntry(g.getValue(), g.toString());
				model.addElement(soort);
			}
			return model;
		}
		if (enumclass == __meldingsoort.class){
			for (__meldingsoort g: __meldingsoort.values()){
				TypeEntry soort = new TypeEntry(g.getValue(), g.toString());
				model.addElement(soort);
			}
			return model;
		}
		if (enumclass == __status.class){
			for (__status g: __status.values()){
				TypeEntry soort = new TypeEntry(g.getValue(), g.toString());
				model.addElement(soort);
			}
			return model;
		}
		if (enumclass == __werkzaamhedensoort.class){
			for (__werkzaamhedensoort g: __werkzaamhedensoort.values()){
				TypeEntry soort = new TypeEntry(g.getValue(), g.toString());
				model.addElement(soort);
			}
			return model;
		}
		if (enumclass == __verzuimsoort.class){
			for (__verzuimsoort g: __verzuimsoort.values()){
				TypeEntry soort = new TypeEntry(g.getValue(), g.toString());
				model.addElement(soort);
			}
			return model;
		}
		if (enumclass == __huisbezoekurgentie.class){
			for (__huisbezoekurgentie g: __huisbezoekurgentie.values()){
				TypeEntry soort = new TypeEntry(g.getValue(), g.toString());
				model.addElement(soort);
			}
			return model;
		}		
		if (enumclass == __btwtariefsoort.class){
			for (__btwtariefsoort g: __btwtariefsoort.values()){
				TypeEntry soort = new TypeEntry(g.getValue(), g.toString());
				model.addElement(soort);
			}
			return model;
		}
		return null;
	}

	public List<ArbodienstInfo> getArbodiensten() {
		return mainmodel.getArbodiensten();
	}

	public List<HoldingInfo> getHoldings() {
		return mainmodel.getHoldings();
	}

	public List<WerkgeverInfo> getWerkgevers() {
		return mainmodel.getWerkgevers();
	}

	public SettingsInfo getSettings() {
		return mainmodel.getSettings();
	}

	public List<UitvoeringsinstituutInfo> getUitkeringsinstanties() {
		return mainmodel.getUitkeringsinstanties();
	}

	public boolean isAuthorised(__applicatiefunctie functie) {
		return VerzuimAuthorisation.isAuthorised(mainmodel.getSession().getGebruiker(), functie);		
	}

	@Override
	public void saveData(InfoBase data) throws VerzuimApplicationException {
		/* noop */
	}

	@Override
	public void addData(InfoBase data) throws VerzuimApplicationException {
		/* noop */
	}

}
