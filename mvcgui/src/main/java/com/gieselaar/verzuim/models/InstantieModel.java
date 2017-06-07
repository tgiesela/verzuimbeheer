package com.gieselaar.verzuim.models;

import java.util.List;

import com.gieselaar.verzuim.interfaces.ModelEventListener;
import com.gieselaar.verzuimbeheer.exceptions.PermissionException;
import com.gieselaar.verzuimbeheer.exceptions.ValidationException;
import com.gieselaar.verzuimbeheer.exceptions.VerzuimApplicationException;
import com.gieselaar.verzuimbeheer.facades.LoginSessionRemote;
import com.gieselaar.verzuimbeheer.services.ArbodienstInfo;
import com.gieselaar.verzuimbeheer.services.BedrijfsartsInfo;
import com.gieselaar.verzuimbeheer.services.BedrijfsgegevensInfo;
import com.gieselaar.verzuimbeheer.services.UitvoeringsinstituutInfo;
import com.gieselaar.verzuimbeheer.utils.ServiceCaller;
import com.gieselaar.verzuimbeheer.utils.ServiceLocatorException;

public class InstantieModel extends AbstractModel {
	private static final long serialVersionUID = 1L;
	private List<ArbodienstInfo> arbodiensten;
	private List<UitvoeringsinstituutInfo> uitkeringsinstanties;
	private List<BedrijfsartsInfo> bedrijfsartsen;
	private BedrijfsgegevensInfo bedrijfsgegevens;
	private Integer selectedarbodienstid = null;
	public InstantieModel(LoginSessionRemote session){
		super(session);
	}

	public void selectArbodiensten() throws VerzuimApplicationException {
		try {
			arbodiensten = ServiceCaller.instantieFacade(this.getSession()).allArbodiensten();
			for (ModelEventListener ml: this.changelisteners){
				ml.listComplete(arbodiensten);
			}
		} catch (PermissionException | ServiceLocatorException e) {
			throw new VerzuimApplicationException(e, e.getMessage());
		}
	}

	public List<ArbodienstInfo> getArbodienstenList() {
		return arbodiensten;
	}

	public void selectUitkeringsinstanties() throws VerzuimApplicationException {
		try {
			uitkeringsinstanties = ServiceCaller.instantieFacade(this.getSession()).allUitkeringsinstanties();
			for (ModelEventListener ml: this.changelisteners){
				ml.listComplete(uitkeringsinstanties);
			}
		} catch (PermissionException | ServiceLocatorException e) {
			throw new VerzuimApplicationException(e, e.getMessage());
		}
	}

	public List<UitvoeringsinstituutInfo> getUitkeringsinstantiesList() {
		return uitkeringsinstanties;
	}

	public void selectBedrijfsartsen() throws VerzuimApplicationException {
		try {
			selectedarbodienstid = null;
			bedrijfsartsen = ServiceCaller.instantieFacade(this.getSession()).allBedrijfsartsen();
			for (ModelEventListener ml: this.changelisteners){
				ml.listComplete(bedrijfsartsen);
			}
		} catch (PermissionException | ServiceLocatorException e) {
			throw new VerzuimApplicationException(e, e.getMessage());
		}
	}
	public void selectBedrijfsartsen(Integer arbodienstid) throws VerzuimApplicationException {
		try {
			selectedarbodienstid = arbodienstid;
			bedrijfsartsen = ServiceCaller.instantieFacade(this.getSession()).allBedrijfsartsenArbodienst(arbodienstid);
			for (ModelEventListener ml: this.changelisteners){
				ml.listComplete(bedrijfsartsen);
			}
		} catch (PermissionException | ServiceLocatorException e) {
			throw new VerzuimApplicationException(e, e.getMessage());
		}
	}
	public void selectBedrijfsgegevens() throws VerzuimApplicationException {
		try {
			List<BedrijfsgegevensInfo> all = ServiceCaller.instantieFacade(this.getSession()).allBedrijfsgegevens();
			if (all.isEmpty()){
				bedrijfsgegevens = null;
			}else{
				bedrijfsgegevens = all.get(0);
			}
			for (ModelEventListener ml: this.changelisteners){
				ml.listComplete(bedrijfsgegevens);
			}
		} catch (PermissionException | ServiceLocatorException e) {
			throw new VerzuimApplicationException(e, e.getMessage());
		}
	}

	public List<BedrijfsartsInfo> getBedrijfsartenList() {
		return bedrijfsartsen;
	}

	@Override
	public void refreshDatabase() throws VerzuimApplicationException {
		try {
			selectArbodiensten();
			selectUitkeringsinstanties();
			if (selectedarbodienstid == null){
				selectBedrijfsartsen();
			}else{
				selectBedrijfsartsen(selectedarbodienstid);
			}
		} catch (VerzuimApplicationException e) {
			throw new VerzuimApplicationException(e, e.getMessage());
		}
	}
	
	public void addUitkerinsinstantie(UitvoeringsinstituutInfo info) throws VerzuimApplicationException {
		try {
			UitvoeringsinstituutInfo template = ServiceCaller.instantieFacade(getSession()).addUitkeringsinstantie(info);
			uitkeringsinstanties.add(template);
			for (ModelEventListener ml: this.changelisteners){
				ml.rowAdded(template);
			}
		} catch (PermissionException | ServiceLocatorException | ValidationException e) {
			throw new VerzuimApplicationException(e, e.getMessage());
		}	
	}

	public void saveUitkerinsinstantie(UitvoeringsinstituutInfo info) throws VerzuimApplicationException {
		try {
			UitvoeringsinstituutInfo updatedinfo = ServiceCaller.instantieFacade(getSession()).updateuitkeringsinstantie(info);
			/* Now also the list has to be updated */
			for (UitvoeringsinstituutInfo w: uitkeringsinstanties){
				if (w.getId().equals(info.getId())){
					uitkeringsinstanties.remove(w);
					break;
				}
			}
			uitkeringsinstanties.add(updatedinfo);
			for (ModelEventListener ml: this.changelisteners){
				ml.rowUpdated(updatedinfo);
			}
		} catch (PermissionException | ServiceLocatorException | ValidationException e) {
			throw new VerzuimApplicationException(e, e.getMessage());
		}	
	}

	public void deleteUitkerinsinstantie(UitvoeringsinstituutInfo info) throws VerzuimApplicationException {
		try {
			ServiceCaller.instantieFacade(getSession()).deleteUitkeringsinstantie(info);
			for (UitvoeringsinstituutInfo w: uitkeringsinstanties){
				if (w.getId().equals(info.getId())){
					uitkeringsinstanties.remove(w);
					break;
				}
			}
			for (ModelEventListener ml: this.changelisteners){
				ml.rowDeleted(info);
			}
		} catch (PermissionException | ServiceLocatorException | ValidationException e) {
			throw new VerzuimApplicationException(e, e.getMessage());
		}	
	}
	public void addArbodienst(ArbodienstInfo info) throws VerzuimApplicationException {
		try {
			ArbodienstInfo template = ServiceCaller.instantieFacade(getSession()).addArbodienst(info);
			arbodiensten.add(template);
			for (ModelEventListener ml: this.changelisteners){
				ml.rowAdded(template);
			}
		} catch (PermissionException | ServiceLocatorException | ValidationException e) {
			throw new VerzuimApplicationException(e, e.getMessage());
		}	
	}

	public void saveArbodienst(ArbodienstInfo info) throws VerzuimApplicationException {
		try {
			ArbodienstInfo updatedinfo = ServiceCaller.instantieFacade(getSession()).updateArbodienst(info);
			/* Now also the list has to be updated */
			for (ArbodienstInfo w: arbodiensten){
				if (w.getId().equals(info.getId())){
					arbodiensten.remove(w);
					break;
				}
			}
			arbodiensten.add(updatedinfo);
			for (ModelEventListener ml: this.changelisteners){
				ml.rowUpdated(updatedinfo);
			}
		} catch (PermissionException | ServiceLocatorException | ValidationException e) {
			throw new VerzuimApplicationException(e, e.getMessage());
		}	
	}

	public void deleteArbodienst(ArbodienstInfo info) throws VerzuimApplicationException {
		try {
			ServiceCaller.instantieFacade(getSession()).deleteArbodienst(info);
			for (ArbodienstInfo w: arbodiensten){
				if (w.getId().equals(info.getId())){
					arbodiensten.remove(w);
					break;
				}
			}
			for (ModelEventListener ml: this.changelisteners){
				ml.rowDeleted(info);
			}
		} catch (PermissionException | ServiceLocatorException | ValidationException e) {
			throw new VerzuimApplicationException(e, e.getMessage());
		}	
	}

	public void deleteBedrijfsarts(BedrijfsartsInfo info) throws VerzuimApplicationException {
		try {
			ServiceCaller.instantieFacade(getSession()).deleteBedrijfsarts(info);
			for (BedrijfsartsInfo w: bedrijfsartsen){
				if (w.getId().equals(info.getId())){
					bedrijfsartsen.remove(w);
					break;
				}
			}
			for (ModelEventListener ml: this.changelisteners){
				ml.rowDeleted(info);
			}
		} catch (PermissionException | ServiceLocatorException | ValidationException e) {
			throw new VerzuimApplicationException(e, e.getMessage());
		}	
	}

	public void addBedrijfsarts(BedrijfsartsInfo info) throws VerzuimApplicationException {
		try {
			BedrijfsartsInfo arts = ServiceCaller.instantieFacade(getSession()).addBedrijfsarts(info);
			bedrijfsartsen.add(arts);
			for (ModelEventListener ml: this.changelisteners){
				ml.rowAdded(arts);
			}
		} catch (PermissionException | ServiceLocatorException | ValidationException e) {
			throw new VerzuimApplicationException(e, e.getMessage());
		}	
	}

	public void saveBedrijfsarts(BedrijfsartsInfo info) throws VerzuimApplicationException {
		try {
			BedrijfsartsInfo updatedinfo = ServiceCaller.instantieFacade(getSession()).updateBedrijfsarts(info);
			/* Now also the list has to be updated */
			for (BedrijfsartsInfo w: bedrijfsartsen){
				if (w.getId().equals(info.getId())){
					bedrijfsartsen.remove(w);
					break;
				}
			}
			bedrijfsartsen.add(updatedinfo);
			for (ModelEventListener ml: this.changelisteners){
				ml.rowUpdated(updatedinfo);
			}
		} catch (PermissionException | ServiceLocatorException | ValidationException e) {
			throw new VerzuimApplicationException(e, e.getMessage());
		}	
	}


	public void addBedrijfsgegevens(BedrijfsgegevensInfo info) throws VerzuimApplicationException {
		try {
			BedrijfsgegevensInfo arts = ServiceCaller.instantieFacade(getSession()).addBedrijfsgegevens(info);
			for (ModelEventListener ml: this.changelisteners){
				ml.rowAdded(arts);
			}
		} catch (PermissionException | ServiceLocatorException | ValidationException e) {
			throw new VerzuimApplicationException(e, e.getMessage());
		}	
	}

	public void saveBedrijfsgegevens(BedrijfsgegevensInfo info) throws VerzuimApplicationException {
		try {
			ServiceCaller.instantieFacade(getSession()).updateBedrijfsgegevens(info);
			for (ModelEventListener ml: this.changelisteners){
				ml.rowUpdated(info);
			}
		} catch (PermissionException | ServiceLocatorException | ValidationException e) {
			throw new VerzuimApplicationException(e, e.getMessage());
		}	
	}

	public void deleteBedrijfsgegevens(BedrijfsgegevensInfo info) {
		/* no delete */
	}

	public BedrijfsgegevensInfo getBedrijfsgegevens() {
		return bedrijfsgegevens;
	}

}
