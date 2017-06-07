package com.gieselaar.verzuimbeheer.facades;

import java.util.List;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateful;

import com.gieselaar.verzuimbeheer.exceptions.VerzuimApplicationException;
import com.gieselaar.verzuimbeheer.exceptions.PermissionException;
import com.gieselaar.verzuimbeheer.exceptions.ValidationException;
import com.gieselaar.verzuimbeheer.facades.AutorisatieFacadeRemote.__applicatiefunctie;
import com.gieselaar.verzuimbeheer.services.ArbodienstInfo;
import com.gieselaar.verzuimbeheer.services.BedrijfsartsInfo;
import com.gieselaar.verzuimbeheer.services.BedrijfsgegevensInfo;
import com.gieselaar.verzuimbeheer.services.GebruikerBean;
import com.gieselaar.verzuimbeheer.services.InstantieBean;
import com.gieselaar.verzuimbeheer.services.UitvoeringsinstituutInfo;

@Stateful(mappedName="java:global/verzuimbeheer/InstantieFacade", name="InstantieFacade")
@LocalBean
public class InstantieFacade extends FacadeBase implements
		InstantieFacadeRemote {

	@EJB InstantieBean instantieEJB;
	@EJB private GebruikerBean gebruikerEJB;
	private LoginSessionRemote loginsession;

	private void setCurrentuser(){
		instantieEJB.setCurrentuser(this.getCurrentuser());
	}
	@Override
	public List<ArbodienstInfo> allArbodiensten() throws PermissionException, VerzuimApplicationException {
		setCurrentuser();
		checkPermission(__applicatiefunctie.RAADPLEGENINSTANTIES);
		return instantieEJB.getArbodiensten();
	}

	@Override
	public List<UitvoeringsinstituutInfo> allUitkeringsinstanties()
			throws PermissionException, VerzuimApplicationException {
		setCurrentuser();
		checkPermission(__applicatiefunctie.RAADPLEGENINSTANTIES);
		return instantieEJB.getUitvoeringsinstituuts();
	}

	@Override
	public List<BedrijfsartsInfo> allBedrijfsartsen() throws PermissionException, VerzuimApplicationException {
		setCurrentuser();
		checkPermission(__applicatiefunctie.RAADPLEGENINSTANTIES);
		return instantieEJB.getBedrijfsartsen();
	}

	@Override
	public List<BedrijfsgegevensInfo> allBedrijfsgegevens()
			throws PermissionException, VerzuimApplicationException {
		setCurrentuser();
		checkPermission(__applicatiefunctie.RAADPLEGENINSTANTIES);
		return instantieEJB.getBedrijfsgegevens();
	}

	@Override
	public List<BedrijfsartsInfo> allBedrijfsartsenArbodienst(int arbodienst) throws PermissionException, VerzuimApplicationException {
		setCurrentuser();
		checkPermission(__applicatiefunctie.RAADPLEGENINSTANTIES);
		return instantieEJB.getBedrijfsartsenArbodienst(arbodienst);
	}

	@Override
	public ArbodienstInfo getArbodienst(Integer id) throws PermissionException, VerzuimApplicationException {
		setCurrentuser();
		checkPermission(__applicatiefunctie.RAADPLEGENINSTANTIES);
		return instantieEJB.getArbodienst(id);
	}

	@Override
	public UitvoeringsinstituutInfo getUitkeringsinstantie(Integer id)
			throws PermissionException, VerzuimApplicationException {
		setCurrentuser();
		checkPermission(__applicatiefunctie.RAADPLEGENINSTANTIES);
		return instantieEJB.getUitvoeringsinstituut(id);
	}
	@Override
	public BedrijfsartsInfo getBedrijfsarts(Integer id) throws PermissionException, VerzuimApplicationException {
		setCurrentuser();
		checkPermission(__applicatiefunctie.RAADPLEGENINSTANTIES);
		return instantieEJB.getBedrijfsarts(id);
	}
	@Override
	public BedrijfsgegevensInfo getBedrijfsgegevens(Integer id)
			throws PermissionException, VerzuimApplicationException {
		setCurrentuser();
		checkPermission(__applicatiefunctie.RAADPLEGENINSTANTIES);
		return instantieEJB.getBedrijfsgegevens(id);
	}

	@Override
	public void deleteArbodienst(ArbodienstInfo info)
			throws PermissionException, ValidationException, VerzuimApplicationException {
		setCurrentuser();
		checkPermission(__applicatiefunctie.VERWIJDERINSTANTIES);
		instantieEJB.deleteArbodienst(info);
	}

	@Override
	public void deleteUitkeringsinstantie(UitvoeringsinstituutInfo info)
			throws PermissionException, ValidationException, VerzuimApplicationException {
		setCurrentuser();
		checkPermission(__applicatiefunctie.VERWIJDERINSTANTIES);
		instantieEJB.deleteUitvoeringsinstituut(info);
	}

	@Override
	public void deleteBedrijfsarts(BedrijfsartsInfo info)
			throws PermissionException, ValidationException, VerzuimApplicationException {
		setCurrentuser();
		checkPermission(__applicatiefunctie.VERWIJDERINSTANTIES);
		instantieEJB.deleteBedrijfsarts(info);
	}

	@Override
	public void deleteBedrijfsgegevens(BedrijfsgegevensInfo info)
			throws PermissionException, ValidationException, VerzuimApplicationException {
		setCurrentuser();
		checkPermission(__applicatiefunctie.VERWIJDERINSTANTIES);
		instantieEJB.deleteBedrijfsgegevens(info);
	}

	@Override
	public ArbodienstInfo updateArbodienst(ArbodienstInfo info)
			throws PermissionException, ValidationException, VerzuimApplicationException {
		setCurrentuser();
		checkPermission(__applicatiefunctie.BEHEERINSTANTIES);
		return instantieEJB.updateArbodienst(info);
	}

	@Override
	public UitvoeringsinstituutInfo updateuitkeringsinstantie(UitvoeringsinstituutInfo info)
			throws PermissionException, ValidationException, VerzuimApplicationException {
		setCurrentuser();
		checkPermission(__applicatiefunctie.BEHEERINSTANTIES);
		return instantieEJB.updateUitvoeringsinstituut(info);
	}

	@Override
	public BedrijfsartsInfo updateBedrijfsarts(BedrijfsartsInfo info)
			throws PermissionException, ValidationException, VerzuimApplicationException {
		setCurrentuser();
		checkPermission(__applicatiefunctie.BEHEERINSTANTIES);
		return instantieEJB.updateBedrijfsarts(info);
	}
	@Override
	public void updateBedrijfsgegevens(BedrijfsgegevensInfo info)
			throws PermissionException, ValidationException, VerzuimApplicationException {
		setCurrentuser();
		checkPermission(__applicatiefunctie.BEHEERINSTANTIES);
		instantieEJB.updateBedrijfsgegevens(info);
	}

	@Override
	public ArbodienstInfo addArbodienst(ArbodienstInfo info) throws PermissionException,
			ValidationException, VerzuimApplicationException {
		setCurrentuser();
		checkPermission(__applicatiefunctie.BEHEERINSTANTIES);
		return instantieEJB.addArbodienst(info);
	}

	@Override
	public UitvoeringsinstituutInfo addUitkeringsinstantie(UitvoeringsinstituutInfo info)
			throws PermissionException, ValidationException, VerzuimApplicationException {
		setCurrentuser();
		checkPermission(__applicatiefunctie.BEHEERINSTANTIES);
		return instantieEJB.addUitvoeringsinstituut(info);
	}
	@Override
	public BedrijfsartsInfo addBedrijfsarts(BedrijfsartsInfo info)
			throws PermissionException, ValidationException, VerzuimApplicationException {
		setCurrentuser();
		checkPermission(__applicatiefunctie.BEHEERINSTANTIES);
		return instantieEJB.addBedrijfsarts(info);
	}
	@Override
	public BedrijfsgegevensInfo addBedrijfsgegevens(BedrijfsgegevensInfo info)
			throws PermissionException, ValidationException, VerzuimApplicationException {
		setCurrentuser();
		checkPermission(__applicatiefunctie.BEHEERINSTANTIES);
		return instantieEJB.addBedrijfsgegevens(info);
	}
	/**
	 * Implementation abstract functions from FacadeBase
	 */
	@Override
	public void setLoginSession(LoginSessionRemote session) throws PermissionException {
		loginsession = session;
		super.setSession(loginsession);
	}

	@Override
	public void initSuperclass() {
		super.setGebruikerEJB(gebruikerEJB);
		super.setSession(loginsession);
	}

}
