package com.gieselaar.verzuim.models;

import java.util.Date;
import java.util.List;

import com.gieselaar.verzuim.interfaces.ModelEventListener;
import com.gieselaar.verzuimbeheer.exceptions.PermissionException;
import com.gieselaar.verzuimbeheer.exceptions.ValidationException;
import com.gieselaar.verzuimbeheer.exceptions.VerzuimApplicationException;
import com.gieselaar.verzuimbeheer.facades.LoginSessionRemote;
import com.gieselaar.verzuimbeheer.services.BtwInfo;
import com.gieselaar.verzuimbeheer.services.FactuurTotaalInfo;
import com.gieselaar.verzuimbeheer.services.FactuurbetalingInfo;
import com.gieselaar.verzuimbeheer.services.FactuurcategorieInfo;
import com.gieselaar.verzuimbeheer.services.FactuuritemInfo;
import com.gieselaar.verzuimbeheer.services.FactuurkopInfo;
import com.gieselaar.verzuimbeheer.utils.ServiceCaller;
import com.gieselaar.verzuimbeheer.utils.ServiceLocatorException;

public class FactuurModel extends AbstractModel {
	private static final long serialVersionUID = 1L;
	private List<FactuurTotaalInfo> facturen;
	private List<FactuurkopInfo> factuurkoppen;
	private List<FactuurcategorieInfo> factuurcategorien;
	private List<FactuuritemInfo> factuuritems;
	private List<BtwInfo> btws;
	private Date datefrom;
	private Date dateuntil;
	public FactuurModel(LoginSessionRemote session){
		super(session);
	}

	public void selectFacturen(Date datefrom, Date dateuntil) throws VerzuimApplicationException {
		try {
			this.datefrom = datefrom;
			this.dateuntil = dateuntil;
			facturen = ServiceCaller.factuurFacade(this.getSession()).getFacturenInPeriode(datefrom, dateuntil, true);
			for (ModelEventListener ml: this.changelisteners){
				ml.listComplete(facturen);
			}
		} catch (PermissionException | ServiceLocatorException | ValidationException e) {
			throw new VerzuimApplicationException(e, e.getMessage());
		}
	}
	public void selectFactuurkoppen() throws VerzuimApplicationException {
		try {
			factuurkoppen = ServiceCaller.factuurFacade(this.getSession()).getFactuurkoppen();
			for (ModelEventListener ml: this.changelisteners){
				ml.listComplete(factuurkoppen);
			}
		} catch (PermissionException | ServiceLocatorException | ValidationException e) {
			throw new VerzuimApplicationException(e, e.getMessage());
		}
	}
	public void selectFactuurcategorien() throws VerzuimApplicationException {
		try {
			factuurcategorien = ServiceCaller.factuurFacade(this.getSession()).getFactuurcategorien();
			for (ModelEventListener ml: this.changelisteners){
				ml.listComplete(factuurcategorien);
			}
		} catch (PermissionException | ServiceLocatorException | ValidationException e) {
			throw new VerzuimApplicationException(e, e.getMessage());
		}
	}
	public void selectFactuuritems(Date datefrom, Date dateuntil) throws VerzuimApplicationException {
		try {
			this.datefrom = datefrom;
			this.dateuntil = dateuntil;
			factuuritems = ServiceCaller.factuurFacade(this.getSession()).getFactuuritems(datefrom, dateuntil);
			for (ModelEventListener ml: this.changelisteners){
				ml.listComplete(factuuritems);
			}
		} catch (PermissionException | ServiceLocatorException | ValidationException e) {
			throw new VerzuimApplicationException(e, e.getMessage());
		}
	}
	public void selectBtws() throws VerzuimApplicationException {
		try {
			btws = ServiceCaller.factuurFacade(this.getSession()).allBtws();
			for (ModelEventListener ml: this.changelisteners){
				ml.listComplete(btws);
			}
		} catch (PermissionException | ServiceLocatorException e) {
			throw new VerzuimApplicationException(e, e.getMessage());
		}
	}
	public List<FactuurTotaalInfo> getFacturenHoldingByFactuurnr(Integer factuurnr) throws VerzuimApplicationException {
		try {
			facturen = ServiceCaller.factuurFacade(this.getSession()).getFacturenHoldingByFactuurnr(factuurnr);
			for (ModelEventListener ml: this.changelisteners){
				ml.listComplete(facturen);
			}
		} catch (PermissionException | ServiceLocatorException | ValidationException e) {
			throw new VerzuimApplicationException(e, e.getMessage());
		}
		return facturen;
	}
	
	public List<FactuurTotaalInfo> getFacturenList() {
		return facturen;
	}
	public List<FactuurkopInfo> getFactuurkoppenList() {
		return factuurkoppen;
	}
	public List<FactuurcategorieInfo> getFactuurcategorienList() {
		return factuurcategorien;
	}
	public List<FactuuritemInfo> getFactuuritemList() {
		return factuuritems;
	}
	public List<BtwInfo> getBtwList() {
		return btws;
	}

	@Override
	public void refreshDatabase() throws VerzuimApplicationException {
		try {
			selectFacturen(datefrom, dateuntil);
			selectFactuurkoppen();
			selectFactuurcategorien();
			selectFactuuritems(datefrom, dateuntil);
			selectBtws();
		} catch (VerzuimApplicationException e) {
			throw new VerzuimApplicationException(e, e.getMessage());
		}
	}

	public void addFactuur(FactuurTotaalInfo info) throws VerzuimApplicationException {
	}
	public void addFactuuritem(FactuuritemInfo info) throws VerzuimApplicationException{
		try {
			FactuuritemInfo item = ServiceCaller.factuurFacade(getSession()).updateFactuuritem(info);
			factuuritems.add(item);
			for (ModelEventListener ml: this.changelisteners){
				ml.rowAdded(item);
			}
		} catch (PermissionException | ServiceLocatorException | ValidationException e) {
			throw new VerzuimApplicationException(e, e.getMessage());
		}	
	}
	public void addFactuurkop(FactuurkopInfo info) throws VerzuimApplicationException{
		try {
			FactuurkopInfo kop = ServiceCaller.factuurFacade(getSession()).updateFactuurkop(info);
			factuurkoppen.add(kop);
			for (ModelEventListener ml: this.changelisteners){
				ml.rowAdded(kop);
			}
		} catch (PermissionException | ServiceLocatorException | ValidationException e) {
			throw new VerzuimApplicationException(e, e.getMessage());
		}	
	}
	public void addFactuurcategorie(FactuurcategorieInfo info) throws VerzuimApplicationException{
		try {
			FactuurcategorieInfo categorie = ServiceCaller.factuurFacade(getSession()).updateFactuurcategorie(info);
			factuurcategorien.add(categorie);
			for (ModelEventListener ml: this.changelisteners){
				ml.rowAdded(categorie);
			}
		} catch (PermissionException | ServiceLocatorException | ValidationException e) {
			throw new VerzuimApplicationException(e, e.getMessage());
		}	
	}
	
	public void addBtw(BtwInfo info) throws VerzuimApplicationException{
		try {
			BtwInfo btw = ServiceCaller.factuurFacade(getSession()).updateBtw(info);
			btws.add(btw);
			for (ModelEventListener ml: this.changelisteners){
				ml.rowAdded(btw);
			}
		} catch (PermissionException | ServiceLocatorException | ValidationException e) {
			throw new VerzuimApplicationException(e, e.getMessage());
		}	
	}
	
	public void saveFactuur(FactuurTotaalInfo info) throws VerzuimApplicationException {
		try {
			FactuurTotaalInfo updatedinfo = ServiceCaller.factuurFacade(getSession()).updateFactuur(info);
			/* Now also the list has to be updated */
			if (facturen != null){
				for (FactuurTotaalInfo w: facturen){
					if (w.getId().equals(info.getId())){
						facturen.remove(w);
						break;
					}
				}
				facturen.add(updatedinfo);
			}
			for (ModelEventListener ml: this.changelisteners){
				ml.rowUpdated(updatedinfo);
			}
		} catch (PermissionException | ServiceLocatorException | ValidationException e) {
			throw new VerzuimApplicationException(e, e.getMessage());
		}	
	}
	public void saveFactuuritem(FactuuritemInfo info) throws VerzuimApplicationException {
		try {
			FactuuritemInfo updatedinfo = ServiceCaller.factuurFacade(getSession()).updateFactuuritem(info);
			/* Now also the list has to be updated */
			for (FactuuritemInfo w: factuuritems){
				if (w.getId().equals(info.getId())){
					factuuritems.remove(w);
					break;
				}
			}
			factuuritems.add(updatedinfo);
			for (ModelEventListener ml: this.changelisteners){
				ml.rowUpdated(updatedinfo);
			}
		} catch (PermissionException | ServiceLocatorException | ValidationException e) {
			throw new VerzuimApplicationException(e, e.getMessage());
		}	
	}
	public void saveFactuurkop(FactuurkopInfo info) throws VerzuimApplicationException {
		try {
			FactuurkopInfo updatedinfo = ServiceCaller.factuurFacade(getSession()).updateFactuurkop(info);
			/* Now also the list has to be updated */
			for (FactuurkopInfo w: factuurkoppen){
				if (w.getId().equals(info.getId())){
					factuurkoppen.remove(w);
					break;
				}
			}
			factuurkoppen.add(updatedinfo);
			for (ModelEventListener ml: this.changelisteners){
				ml.rowUpdated(updatedinfo);
			}
		} catch (PermissionException | ServiceLocatorException | ValidationException e) {
			throw new VerzuimApplicationException(e, e.getMessage());
		}	
	}
	public void saveFactuurcategorie(FactuurcategorieInfo info) throws VerzuimApplicationException {
		try {
			FactuurcategorieInfo updatedinfo = ServiceCaller.factuurFacade(getSession()).updateFactuurcategorie(info);
			/* Now also the list has to be updated */
			for (FactuurcategorieInfo w: factuurcategorien){
				if (w.getId().equals(info.getId())){
					factuurcategorien.remove(w);
					break;
				}
			}
			factuurcategorien.add(updatedinfo);
			for (ModelEventListener ml: this.changelisteners){
				ml.rowUpdated(updatedinfo);
			}
		} catch (PermissionException | ServiceLocatorException | ValidationException e) {
			throw new VerzuimApplicationException(e, e.getMessage());
		}	
	}
	public void saveBtw(BtwInfo info) throws VerzuimApplicationException {
		try {
			BtwInfo updatedinfo = ServiceCaller.factuurFacade(getSession()).updateBtw(info);
			/* Now also the list has to be updated */
			for (BtwInfo w: btws){
				if (w.getId().equals(info.getId())){
					btws.remove(w);
					break;
				}
			}
			btws.add(updatedinfo);
			for (ModelEventListener ml: this.changelisteners){
				ml.rowUpdated(updatedinfo);
			}
		} catch (PermissionException | ServiceLocatorException | ValidationException e) {
			throw new VerzuimApplicationException(e, e.getMessage());
		}	
	}

	public void deleteFactuuritem(FactuuritemInfo info) throws VerzuimApplicationException {
		try {
			/*
			 * Factuur is not deleted, only updated
			 */
			ServiceCaller.factuurFacade(getSession()).deleteFactuuritem(info);
			for (FactuuritemInfo w: factuuritems){
				if (w.getId().equals(info.getId())){
					factuuritems.remove(w);
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
	public void deleteFactuurkop(FactuurkopInfo info) throws VerzuimApplicationException {
		try {
			/*
			 * Factuur is not deleted, only updated
			 */
			ServiceCaller.factuurFacade(getSession()).deleteFactuurkop(info);
			for (FactuurkopInfo w: factuurkoppen){
				if (w.getId().equals(info.getId())){
					factuurkoppen.remove(w);
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
	public void deleteFactuurcategorie(FactuurcategorieInfo info) throws VerzuimApplicationException {
		try {
			/*
			 * Factuur is not deleted, only updated
			 */
			ServiceCaller.factuurFacade(getSession()).deleteFactuurcategorie(info);
			for (FactuurcategorieInfo w: factuurcategorien){
				if (w.getId().equals(info.getId())){
					factuurcategorien.remove(w);
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
	public void deleteBtw(BtwInfo info) throws VerzuimApplicationException {
		try {
			/*
			 * Factuur is not deleted, only updated
			 */
			ServiceCaller.factuurFacade(getSession()).deleteBtw(info);
			for (BtwInfo w: btws){
				if (w.getId().equals(info.getId())){
					btws.remove(w);
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

	public int getAantalontbrekendeFacturen(int jaar, int maand) throws VerzuimApplicationException  {
		try {
			return ServiceCaller.factuurFacade(getSession()).getAantalontbrekendeFacturen(jaar, maand);
		} catch (PermissionException | ServiceLocatorException | ValidationException e) {
			throw new VerzuimApplicationException(e, "Kan aantal ontbrekende facturen niet bepalen");
		}	
	}

	public void afsluitenMaand(int jaar, int maand) throws VerzuimApplicationException {
		try {
			ServiceCaller.factuurFacade(getSession()).afsluitenMaand(jaar,maand);
		} catch (PermissionException | ValidationException | ServiceLocatorException e) {
			throw new VerzuimApplicationException(e, "Kan maand niet afsluiten");
		}
	}
	public void terugdraaienMaand(int jaar, int maand) throws VerzuimApplicationException {
		try {
			ServiceCaller.factuurFacade(getSession()).terugdraaienMaand(jaar, maand);
		} catch (PermissionException | ServiceLocatorException | ValidationException e) {
			throw new VerzuimApplicationException(e, "kan maand niet terugdraaien");
		}
	}

	public List<FactuurTotaalInfo> getFacturenInPeriodeByHolding(Date datefrom, Date dateuntil, Integer holdingid,
			boolean details) throws VerzuimApplicationException {
		try {
			return ServiceCaller.factuurFacade(getSession()).getFacturenInPeriodeByHolding(datefrom, dateuntil, holdingid, details);
		} catch (PermissionException | ServiceLocatorException e) {
			throw new VerzuimApplicationException(e, "kan holding facturen niet opvragen voor holding: " + holdingid);
		}
	}

	public List<FactuurTotaalInfo> getFacturenInPeriodeByWerkgever(Date datefrom, Date dateuntil,
			Integer werkgeverid, boolean details) throws VerzuimApplicationException {
		try {
			return ServiceCaller.factuurFacade(getSession()).getFacturenInPeriodeByWerkgever(datefrom, dateuntil, werkgeverid, details);
		} catch (PermissionException | ServiceLocatorException e) {
			throw new VerzuimApplicationException(e, "kan werkgever facturen niet opvragen voor werkgever: " + werkgeverid);
		}
	}
	public List<FactuurTotaalInfo> getFacturenInPeriode(Date startdate, Date enddate, boolean details) throws VerzuimApplicationException {
		try {
			return ServiceCaller.factuurFacade(getSession()).getFacturenInPeriode(startdate, enddate, details);
		} catch (PermissionException | ServiceLocatorException | ValidationException e) {
			throw new VerzuimApplicationException(e, "kan facturen niet opvragen");
		}
	}

	public List<FactuurbetalingInfo> getFactuurbetalingenForFactuur(Integer factuurid) throws VerzuimApplicationException {
		try {
			return ServiceCaller.factuurFacade(getSession()).getFactuurbetalingenForFactuur(factuurid);
		} catch (PermissionException | ServiceLocatorException e) {
			throw new VerzuimApplicationException(e, "kan facturen niet opvragen");
		}
	}

}
