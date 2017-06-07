package com.gieselaar.verzuim.models;

import java.util.ArrayList;
import java.util.List;

import com.gieselaar.verzuim.interfaces.ModelEventListener;
import com.gieselaar.verzuim.utils.ExceptionLogger;
import com.gieselaar.verzuimbeheer.exceptions.VerzuimApplicationException;
import com.gieselaar.verzuimbeheer.facades.LoginSessionRemote;
import com.gieselaar.verzuimbeheer.services.ActiviteitInfo;
import com.gieselaar.verzuimbeheer.services.ArbodienstInfo;
import com.gieselaar.verzuimbeheer.services.BedrijfsartsInfo;
import com.gieselaar.verzuimbeheer.services.CascodeInfo;
import com.gieselaar.verzuimbeheer.services.DocumentTemplateInfo;
import com.gieselaar.verzuimbeheer.services.GebruikerInfo;
import com.gieselaar.verzuimbeheer.services.HoldingInfo;
import com.gieselaar.verzuimbeheer.services.SettingsInfo;
import com.gieselaar.verzuimbeheer.services.UitvoeringsinstituutInfo;
import com.gieselaar.verzuimbeheer.services.WerkgeverInfo;

public class MDIMainModel extends AbstractModel{
	private static final long serialVersionUID = 1L;

	/*
	 * Model to contain data for combo/boxes and authentication
	 */
	
	private List<WerkgeverInfo> werkgevers = new ArrayList<>();
	private List<CascodeInfo> cascodes = new ArrayList<>();
	private List<HoldingInfo> holdings = new ArrayList<>();
	private List<ArbodienstInfo> arbodiensten = new ArrayList<>();
	private List<UitvoeringsinstituutInfo> uitkeringsinstanties = new ArrayList<>();
	private List<BedrijfsartsInfo> bedrijfsartsen = new ArrayList<>();
	private SettingsInfo settings;

	private WerkgeverModel werkgevermodel;
	private CascodeModel cascodemodel;
	private GebruikerModel gebruikermodel;
	private DocumentTemplateModel documenttemplatemodel;
	private ActiviteitModel activiteitmodel;
	private InstantieModel instantiemodel;
	private SettingsModel settingsmodel;
	private List<DocumentTemplateInfo> documenttemplates;
	private List<GebruikerInfo> gebruikers;
	private List<ActiviteitInfo> activiteiten;


	public MDIMainModel(LoginSessionRemote session) {
		super(session);
		werkgevermodel = new WerkgeverModel(session);
		werkgevermodel.addModelEventListener(new ModelEventListener() {
			
			@Override
			public void rowUpdated(Object data) {/*nothing to do*/}
			
			@Override
			public void rowAdded(Object data) {/*nothing to do*/}
			
			@Override
			public void listComplete(Object data) {
				werkgevers = werkgevermodel.getWerkgeverList();
				holdings = werkgevermodel.getHoldingsList();
			}
			@Override
			public void rowDeleted(Object data) {/*nothing to do*/}
		});
		cascodemodel = new CascodeModel(session);
		cascodemodel.addModelEventListener(new ModelEventListener() {
			
			@Override
			public void rowUpdated(Object data) {/*nothing to do*/}
			
			@Override
			public void rowAdded(Object data) {/*nothing to do*/}
			
			@Override
			public void listComplete(Object data) {
				cascodes = cascodemodel.getCascodesList();
			}
			@Override
			public void rowDeleted(Object data) {/*nothing to do*/}
		});
		gebruikermodel = new GebruikerModel(session);
		gebruikermodel.addModelEventListener(new ModelEventListener() {
			
			@Override
			public void rowUpdated(Object data) {/*nothing to do*/}
			
			@Override
			public void rowAdded(Object data) {/*nothing to do*/}
			
			@Override
			public void listComplete(Object data) {
				gebruikers = gebruikermodel.getGebruikersList();
			}
			@Override
			public void rowDeleted(Object data) {/*nothing to do*/}
		});
		activiteitmodel = new ActiviteitModel(session);
		activiteitmodel.addModelEventListener(new ModelEventListener() {
			
			@Override
			public void rowUpdated(Object data) {/*nothing to do*/}
			
			@Override
			public void rowAdded(Object data) {/*nothing to do*/}
			
			@Override
			public void listComplete(Object data) {
				activiteiten = activiteitmodel.getActiviteitenList();
			}
			@Override
			public void rowDeleted(Object data) {/*nothing to do*/}
		});
		documenttemplatemodel = new DocumentTemplateModel(session);
		documenttemplatemodel.addModelEventListener(new ModelEventListener() {
			
			@Override
			public void rowUpdated(Object data) {/*nothing to do*/}
			
			@Override
			public void rowAdded(Object data) {/*nothing to do*/}
			
			@Override
			public void listComplete(Object data) {
				documenttemplates = documenttemplatemodel.getDocumenttemplatesList();
			}
			@Override
			public void rowDeleted(Object data) {/*nothing to do*/}
		});
		instantiemodel = new InstantieModel(session);
		instantiemodel.addModelEventListener(new ModelEventListener() {
			
			@Override
			public void rowUpdated(Object data) {/*nothing to do*/}
			
			@Override
			public void rowAdded(Object data) {/*nothing to do*/}
			
			@Override
			public void listComplete(Object data) {
				arbodiensten = instantiemodel.getArbodienstenList();
				bedrijfsartsen = instantiemodel.getBedrijfsartenList();
				uitkeringsinstanties = instantiemodel.getUitkeringsinstantiesList();
			}
			@Override
			public void rowDeleted(Object data) {/*nothing to do*/}
		});
		settingsmodel = new SettingsModel(session);
		settingsmodel.addModelEventListener(new ModelEventListener() {
			
			@Override
			public void rowUpdated(Object data) {/*nothing to do*/}
			
			@Override
			public void rowAdded(Object data) {/*nothing to do*/}
			
			@Override
			public void listComplete(Object data) {
				settings = settingsmodel.getSettings();
			}
			@Override
			public void rowDeleted(Object data) {/*nothing to do*/}
		});
	}

	@Override
	public void refreshDatabase() throws VerzuimApplicationException {
		/*
		 * All data will be refreshed, this is also called the first time after
		 * the application is started and the user was authenticated.
		 */
		try {
			werkgevermodel.selectHoldings();
			werkgevermodel.selectWerkgevers();
			documenttemplatemodel.selectDocumentTemplates();
			activiteitmodel.selectActiviteiten();
			gebruikermodel.selectGebruikers();
			cascodemodel.selectCascodes();
			instantiemodel.selectArbodiensten();
			instantiemodel.selectUitkeringsinstanties();
			instantiemodel.selectBedrijfsartsen();
			settingsmodel.selectSettings();
		} catch (VerzuimApplicationException e) {
			ExceptionLogger.ProcessException(e, null);
		}
	}

	public void modelUpdated(Class<?> updatedclass) throws VerzuimApplicationException {
		if (updatedclass == WerkgeverInfo.class){
			werkgevermodel.refreshDatabase();
		}
		if (updatedclass == HoldingInfo.class){
			werkgevermodel.refreshDatabase();
		}
		if (updatedclass == CascodeInfo.class){
			cascodemodel.refreshDatabase();
		}
		if (updatedclass == DocumentTemplateInfo.class){
			documenttemplatemodel.refreshDatabase();
		}
		if (updatedclass == GebruikerInfo.class){
			gebruikermodel.refreshDatabase();
		}
		if (updatedclass == ActiviteitInfo.class){
			activiteitmodel.refreshDatabase();
		}
	}

	public List<WerkgeverInfo> getWerkgevers() {
		return werkgevers;
	}

	public List<CascodeInfo> getCascodes() {
		return cascodes;
	}

	public List<ActiviteitInfo> getActiviteiten() {
		return activiteiten;
	}

	public List<GebruikerInfo> getGebruikers() {
		return gebruikers;
	}

	public List<DocumentTemplateInfo> getDocumentTemplates() {
		return documenttemplates;
	}

	public List<HoldingInfo> getHoldings() {
		return holdings;
	}

	public List<ArbodienstInfo> getArbodiensten() {
		return arbodiensten;
	}

	public List<BedrijfsartsInfo> getBedrijfsartsen(Integer arbodienstid) {
		List<BedrijfsartsInfo> artsen = new ArrayList<>();
		for (BedrijfsartsInfo arts: bedrijfsartsen){
			if (arbodienstid.equals(-1)){
				artsen.add(arts);
			}else{
				if (arts.getArbodienstId().equals(arbodienstid))
					artsen.add(arts);
			}
		}
		return artsen;
	}
	public SettingsInfo getSettings() {
		return settings;
	}

	public List<UitvoeringsinstituutInfo> getUitkeringsinstanties() {
		return uitkeringsinstanties;
	}
}
