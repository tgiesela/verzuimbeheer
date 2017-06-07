package com.gieselaar.verzuim.models;

import java.util.Date;
import java.util.Iterator;
import java.util.List;

import com.gieselaar.verzuim.interfaces.ModelEventListener;
import com.gieselaar.verzuimbeheer.exceptions.PermissionException;
import com.gieselaar.verzuimbeheer.exceptions.ValidationException;
import com.gieselaar.verzuimbeheer.exceptions.VerzuimApplicationException;
import com.gieselaar.verzuimbeheer.facades.LoginSessionRemote;
import com.gieselaar.verzuimbeheer.services.AfdelingInfo;
import com.gieselaar.verzuimbeheer.services.HoldingInfo;
import com.gieselaar.verzuimbeheer.services.ImportResult;
import com.gieselaar.verzuimbeheer.services.OeInfo;
import com.gieselaar.verzuimbeheer.services.OeNiveauInfo;
import com.gieselaar.verzuimbeheer.services.WerkgeverInfo;
import com.gieselaar.verzuimbeheer.utils.ServiceCaller;
import com.gieselaar.verzuimbeheer.utils.ServiceLocatorException;

public class WerkgeverModel extends AbstractModel {
	private static final long serialVersionUID = 1L;
	private List<WerkgeverInfo> werkgevers;
	private List<HoldingInfo> holdings;
	private List<AfdelingInfo> afdelingen;
	private List<OeNiveauInfo> oeniveaus;
	private List<OeInfo> oes;
	private boolean filterObsolete;
	private Integer holdingid = null;
	private List<ImportResult> importresults;
	public WerkgeverModel(LoginSessionRemote session){
		super(session);
	}

	public void selectWerkgevers() throws VerzuimApplicationException {
		try {
			this.holdingid = null;
			werkgevers = ServiceCaller.werkgeverFacade(this.getSession()).allWerkgevers();
			for (ModelEventListener ml: this.changelisteners){
				ml.listComplete(werkgevers);
			}
		} catch (PermissionException | ServiceLocatorException e) {
			throw new VerzuimApplicationException(e, e.getMessage());
		}
	}
	
	public void selectWerkgevers(Integer holdingid) throws VerzuimApplicationException {
		try {
			this.holdingid = holdingid; 
			werkgevers = ServiceCaller.werkgeverFacade(this.getSession()).getWerkgeversHolding(holdingid);
			applyFilters(werkgevers);
			for (ModelEventListener ml: this.changelisteners){
				ml.listComplete(werkgevers);
			}
		} catch (PermissionException | ServiceLocatorException e) {
			throw new VerzuimApplicationException(e, e.getMessage());
		}
	}
	public void selectAfdelingen(Integer werkgeverid) throws VerzuimApplicationException {
		try {
			afdelingen = ServiceCaller.werkgeverFacade(this.getSession()).getAfdelingenWerkgever(werkgeverid);
			for (ModelEventListener ml: this.changelisteners){
				ml.listComplete(afdelingen);
			}
		} catch (PermissionException | ServiceLocatorException e) {
			throw new VerzuimApplicationException(e, e.getMessage());
		}
	}
	
	public void selectOeNiveaus() throws VerzuimApplicationException{
		try {
			oeniveaus = ServiceCaller.werkgeverFacade(getSession()).getOeNiveaus();
			for (ModelEventListener ml: this.changelisteners){
				ml.listComplete(oeniveaus);
			}
		} catch (PermissionException | ServiceLocatorException e) {
			throw new VerzuimApplicationException(e, e.getMessage());
		}
	}
	public void selectOes() throws VerzuimApplicationException{
		try {
			oes = ServiceCaller.werkgeverFacade(getSession()).getOes();
			for (ModelEventListener ml: this.changelisteners){
				ml.listComplete(oes);
			}
		} catch (PermissionException | ServiceLocatorException e) {
			throw new VerzuimApplicationException(e, e.getMessage());
		}
	}
	
	public void selectHoldings() throws VerzuimApplicationException {
		try {
			holdings = ServiceCaller.werkgeverFacade(this.getSession()).getHoldings();
			for (ModelEventListener ml: this.changelisteners){
				ml.listComplete(holdings);
			}
		} catch (PermissionException | ServiceLocatorException e) {
			throw new VerzuimApplicationException(e, e.getMessage());
		}
	}

	private void applyFilters(List<WerkgeverInfo> werkgevers) {
		if (filterObsolete){
			for (Iterator<WerkgeverInfo> iter = werkgevers.listIterator(); iter.hasNext(); ) {
			    WerkgeverInfo wgi = iter.next();
				if (wgi.isActief()){
			        iter.remove();
			    }
			}				
		}
	}
	
	public WerkgeverInfo getWerkgeverDetails(int werkgeverid) throws VerzuimApplicationException  {
		try {
			return ServiceCaller.werkgeverFacade(getSession()).getWerkgever(werkgeverid);
		} catch (PermissionException | ServiceLocatorException e) {
			throw new VerzuimApplicationException(e, e.getMessage());
		}
	}
	
	public boolean isFilterObsolete() {
		return filterObsolete;
	}
	
	public void setFilterObsolete(boolean filterObsolete) {
		this.filterObsolete = filterObsolete;
	}

	public List<WerkgeverInfo> getWerkgeverList() {
		return werkgevers;
	}

	@Override
	public void refreshDatabase() throws VerzuimApplicationException {
		if (this.holdingid != null){
			this.selectWerkgevers(holdingid);
		}else{
			this.selectWerkgevers();
		}
		selectHoldings();
		selectOeNiveaus();
		selectOes();
	}

	public void addAfdeling(AfdelingInfo afdeling)throws VerzuimApplicationException {
		try {
			if (afdeling.getId() != null && afdeling.getId() > 0){
				AfdelingInfo newAfdeling = ServiceCaller.werkgeverFacade(getSession()).addAfdeling(afdeling);
				if (afdelingen != null){
					afdelingen.add(newAfdeling);
				}
				for (ModelEventListener ml: this.changelisteners){
					ml.rowAdded(newAfdeling);
				}
			}else{
				/* Werkgever not yet saved, only add to Werkgever */
				if (afdelingen != null){
					afdelingen.add(afdeling);
				}
				for (ModelEventListener ml: this.changelisteners){
					ml.rowAdded(afdeling);
				}
			}
		} catch (PermissionException | ServiceLocatorException | ValidationException e) {
			throw new VerzuimApplicationException(e, e.getMessage());
		}	
	}

	public void saveAfdeling(AfdelingInfo afdeling)throws VerzuimApplicationException {
		try {
			if (afdeling.getId() != null && afdeling.getId() > 0){
				ServiceCaller.werkgeverFacade(getSession()).updateAfdeling(afdeling);
			}
			/* Now also the list has to be updated */
			if (afdelingen != null){
				for (AfdelingInfo w: afdelingen){
					if (w.getId().equals(afdeling.getId())){
						afdelingen.remove(w);
						break;
					}
				}
				afdelingen.add(afdeling);
			}
			for (ModelEventListener ml: this.changelisteners){
				ml.rowUpdated(afdeling);
			}
		} catch (PermissionException | ServiceLocatorException | ValidationException e) {
			throw new VerzuimApplicationException(e, e.getMessage());
		}	
	}

	public void deleteAfdeling(AfdelingInfo afdeling)throws VerzuimApplicationException {
		try {
			if (afdeling.getId() != null && afdeling.getId() > 0){
				ServiceCaller.werkgeverFacade(getSession()).deleteAfdeling(afdeling);
			}
			if (afdelingen != null){
				for (AfdelingInfo w: afdelingen){
					if (w.getId().equals(afdeling.getId())){
						afdelingen.remove(w);
						break;
					}
				}
			}
			for (ModelEventListener ml: this.changelisteners){
				ml.rowDeleted(afdeling);
			}
		} catch (PermissionException | ServiceLocatorException | ValidationException e) {
			throw new VerzuimApplicationException(e, e.getMessage());
		}	
	}

	public List<AfdelingInfo> getAfdelingen() {
		return afdelingen;
	}

	public void addWerkgever(WerkgeverInfo werkgever)throws VerzuimApplicationException {
		try {
			WerkgeverInfo newWerkgever = ServiceCaller.werkgeverFacade(getSession()).addWerkgever(werkgever);
			if (werkgevers != null)
				werkgevers.add(newWerkgever);
			for (ModelEventListener ml: this.changelisteners){
				ml.rowAdded(newWerkgever);
			}
		} catch (PermissionException | ServiceLocatorException | ValidationException e) {
			throw new VerzuimApplicationException(e, e.getMessage());
		}	
	}

	public void saveWerkgever(WerkgeverInfo werkgever)throws VerzuimApplicationException {
		try {
			/* 
			 * For backwards compatibility we remove Afdelingen before update
			 */
			werkgever.setAfdelings(null);
			
			ServiceCaller.werkgeverFacade(getSession()).updateWerkgever(werkgever);
			/* Now also the list has to be updated */
			if (werkgevers != null){
				for (WerkgeverInfo w: werkgevers){
					if (w.getId().equals(werkgever.getId())){
						werkgevers.remove(w);
						break;
					}
				}
				werkgevers.add(werkgever);
			}
			for (ModelEventListener ml: this.changelisteners){
				ml.rowUpdated(werkgever);
			}
		} catch (PermissionException | ServiceLocatorException | ValidationException e) {
			throw new VerzuimApplicationException(e, e.getMessage());
		}	
	}

	public void deleteWerkgever(WerkgeverInfo werkgever)throws VerzuimApplicationException {
		try {
			ServiceCaller.werkgeverFacade(getSession()).deleteWerkgever(werkgever);
			if (werkgevers != null){
				for (WerkgeverInfo w: werkgevers){
					if (w.getId().equals(werkgever.getId())){
						werkgevers.remove(w);
						break;
					}
				}
			}
			for (ModelEventListener ml: this.changelisteners){
				ml.rowDeleted(werkgever);
			}
		} catch (PermissionException | ServiceLocatorException | ValidationException e) {
			throw new VerzuimApplicationException(e, e.getMessage());
		}	
	}

	public List<HoldingInfo> getHoldingsList() {
		return holdings;
	}

	public List<OeInfo> getOeList() {
		return oes;
	}

	public List<OeNiveauInfo> getOeNiveauList() {
		return oeniveaus;
	}

	public void saveHolding(HoldingInfo holding) throws VerzuimApplicationException {
		try {
			ServiceCaller.werkgeverFacade(getSession()).updateHolding(holding);
			for (ModelEventListener ml: this.changelisteners){
				ml.rowUpdated(holding);
			}
		} catch (PermissionException | ServiceLocatorException | ValidationException e) {
			throw new VerzuimApplicationException(e, e.getMessage());
		}	
	}

	public void addHolding(HoldingInfo holding) throws VerzuimApplicationException {
		try {
			HoldingInfo newHolding= ServiceCaller.werkgeverFacade(getSession()).addHolding(holding);
			if (holdings != null)
				holdings.add(newHolding);
			for (ModelEventListener ml: this.changelisteners){
				ml.rowAdded(newHolding);
			}
		} catch (PermissionException | ServiceLocatorException | ValidationException e) {
			throw new VerzuimApplicationException(e, e.getMessage());
		}	
	}
	public void deleteHolding(HoldingInfo info) throws VerzuimApplicationException {
		try {
			ServiceCaller.werkgeverFacade(getSession()).deleteHolding(info);
			if (holdings != null){
				for (HoldingInfo w: holdings){
					if (w.getId().equals(info.getId())){
						holdings.remove(w);
						break;
					}
				}
			}
			for (ModelEventListener ml: this.changelisteners){
				ml.rowDeleted(info);
			}
		} catch (PermissionException | ServiceLocatorException | ValidationException e) {
			throw new VerzuimApplicationException(e, e.getMessage());
		}	
	}

	public HoldingInfo getHoldingDetails(int holdingid) throws VerzuimApplicationException {
		try {
			return ServiceCaller.werkgeverFacade(getSession()).getHolding(holdingid);
		} catch (PermissionException | ServiceLocatorException e) {
			throw new VerzuimApplicationException(e, e.getMessage());
		}
	}
	public void saveOe(OeInfo oe) throws VerzuimApplicationException {
		try {
			ServiceCaller.werkgeverFacade(getSession()).updateOe(oe);
			for (ModelEventListener ml: this.changelisteners){
				ml.rowUpdated(oe);
			}
		} catch (PermissionException | ServiceLocatorException | ValidationException e) {
			throw new VerzuimApplicationException(e, e.getMessage());
		}	
	}
	public void saveOeniveau(OeNiveauInfo oeniveau) throws VerzuimApplicationException {
		try {
			ServiceCaller.werkgeverFacade(getSession()).updateOeniveau(oeniveau);
			for (ModelEventListener ml: this.changelisteners){
				ml.rowUpdated(oeniveau);
			}
		} catch (PermissionException | ServiceLocatorException | ValidationException e) {
			throw new VerzuimApplicationException(e, e.getMessage());
		}	
	}

	public void addOe(OeInfo oe) throws VerzuimApplicationException {
		try {
			OeInfo newOe= ServiceCaller.werkgeverFacade(getSession()).addOe(oe);
			if (oes != null)
				oes.add(newOe);
			for (ModelEventListener ml: this.changelisteners){
				ml.rowAdded(newOe);
			}
		} catch (PermissionException  | ServiceLocatorException | ValidationException e) {
			throw new VerzuimApplicationException(e, e.getMessage());
		}	
	}
	public void addOeNiveau(OeNiveauInfo oeniveau) throws VerzuimApplicationException {
		try {
			OeNiveauInfo newOeniveau= ServiceCaller.werkgeverFacade(getSession()).addOeniveau(oeniveau);
			if (oeniveaus != null)
				oeniveaus.add(newOeniveau);
			for (ModelEventListener ml: this.changelisteners){
				ml.rowAdded(newOeniveau);
			}
		} catch (PermissionException | ServiceLocatorException | ValidationException e) {
			throw new VerzuimApplicationException(e, e.getMessage());
		}	
	}
	public void deleteOe(OeInfo oe)throws VerzuimApplicationException {
		try {
			ServiceCaller.werkgeverFacade(getSession()).deleteOe(oe);
			if (oes != null){
				for (OeInfo w: oes){
					if (w.getId().equals(oe.getId())){
						oes.remove(w);
						break;
					}
				}
			}
			for (ModelEventListener ml: this.changelisteners){
				ml.rowDeleted(oe);
			}
		} catch (PermissionException | ServiceLocatorException | ValidationException e) {
			throw new VerzuimApplicationException(e, e.getMessage());
		}	
	}
	public void deleteOeNiveau(OeNiveauInfo oeniveau)throws VerzuimApplicationException {
		try {
			ServiceCaller.werkgeverFacade(getSession()).deleteOeniveau(oeniveau);
			if (oeniveaus != null){
				for (OeNiveauInfo w: oeniveaus){
					if (w.getId().equals(oeniveau.getId())){
						oeniveaus.remove(w);
						break;
					}
				}
			}
			for (ModelEventListener ml: this.changelisteners){
				ml.rowDeleted(oeniveau);
			}
		} catch (PermissionException | ServiceLocatorException | ValidationException e) {
			throw new VerzuimApplicationException(e, e.getMessage());
		}	
	}

	public OeInfo getOeDetails(Integer oeid) {
		for (OeInfo oe: oes){
			if (oe.getId().equals(oeid)){
				return oe;
			}
		}
		return null;
	}

	public boolean isVasttariefHuisbezoeken(Date peildatum, Integer holdingid, Integer werkgeverid) throws VerzuimApplicationException {
		try {
			return ServiceCaller.werkgeverFacade(getSession()).isVasttariefHuisbezoeken(peildatum, holdingid, werkgeverid);
		} catch (ValidationException | ServiceLocatorException | PermissionException e) {
			throw new VerzuimApplicationException(e, e.getMessage());
		}
	}

	public void afsluitenDienstverbanden(Integer holdingid2, String separator, byte[] barray, boolean veldnamenrij) throws VerzuimApplicationException {
		try {
			importresults = ServiceCaller.werkgeverFacade(getSession()).afsluitenDienstverbanden(holdingid2, separator, barray, veldnamenrij);
			for (ModelEventListener ml: this.changelisteners){
				ml.listComplete(importresults);
			}
		} catch (ValidationException | PermissionException | ServiceLocatorException e) {
			throw new VerzuimApplicationException(e, e.getMessage());
		}
	}
	public List<ImportResult> getImportResults() {
		return importresults;
	}

	public void importUren(byte[] barray, String separator, boolean veldnamenrij) throws VerzuimApplicationException {
		try {
			importresults = ServiceCaller.werkgeverFacade(getSession()).importUren(separator, barray, veldnamenrij);
			for (ModelEventListener ml: this.changelisteners){
				ml.listComplete(importresults);
			}
		} catch (ValidationException | PermissionException | ServiceLocatorException e) {
			throw new VerzuimApplicationException(e, e.getMessage());
		}
	}

	public void importWerknemers(int werkgeverid, byte[] barray, String separator, boolean veldnamenrij) throws VerzuimApplicationException {
		try {
			importresults = ServiceCaller.werkgeverFacade(getSession()).importWerknemers(werkgeverid, separator, barray, veldnamenrij);
			for (ModelEventListener ml: this.changelisteners){
				ml.listComplete(importresults);
			}
		} catch (ValidationException | PermissionException | ServiceLocatorException e) {
			throw new VerzuimApplicationException(e, e.getMessage());
		}
	}

	public void setImportresults(List<ImportResult> results) {
		importresults = results;
		for (ModelEventListener ml: this.changelisteners){
			ml.listComplete(importresults);
		}
	}
}
