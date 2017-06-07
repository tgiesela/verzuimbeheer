package com.gieselaar.verzuimbeheer.facades;

import java.util.List;

import javax.ejb.Remote;

import com.gieselaar.verzuimbeheer.exceptions.VerzuimApplicationException;
import com.gieselaar.verzuimbeheer.exceptions.PermissionException;
import com.gieselaar.verzuimbeheer.exceptions.ValidationException;
import com.gieselaar.verzuimbeheer.services.ArbodienstInfo;
import com.gieselaar.verzuimbeheer.services.BedrijfsartsInfo;
import com.gieselaar.verzuimbeheer.services.BedrijfsgegevensInfo;
import com.gieselaar.verzuimbeheer.services.UitvoeringsinstituutInfo;

@Remote
public interface InstantieFacadeRemote extends FacadeBaseRemote  {
	List<ArbodienstInfo> allArbodiensten() throws PermissionException, VerzuimApplicationException;
	List<UitvoeringsinstituutInfo> allUitkeringsinstanties() throws PermissionException, VerzuimApplicationException;
	List<BedrijfsartsInfo> allBedrijfsartsenArbodienst(int arbodienst)	throws PermissionException, VerzuimApplicationException;
	List<BedrijfsartsInfo> allBedrijfsartsen() throws PermissionException, VerzuimApplicationException;
	List<BedrijfsgegevensInfo> allBedrijfsgegevens() throws PermissionException, VerzuimApplicationException;

	ArbodienstInfo getArbodienst(Integer id) throws PermissionException, VerzuimApplicationException;
	UitvoeringsinstituutInfo getUitkeringsinstantie(Integer id) throws PermissionException, VerzuimApplicationException;
	BedrijfsartsInfo getBedrijfsarts(Integer id) throws PermissionException, VerzuimApplicationException;
	BedrijfsgegevensInfo getBedrijfsgegevens(Integer id) throws PermissionException, VerzuimApplicationException;
	
	void deleteArbodienst(ArbodienstInfo info) throws PermissionException, ValidationException, VerzuimApplicationException;
	void deleteUitkeringsinstantie(UitvoeringsinstituutInfo info) throws PermissionException, ValidationException, VerzuimApplicationException;
	void deleteBedrijfsarts(BedrijfsartsInfo info) throws PermissionException,ValidationException, VerzuimApplicationException;
	void deleteBedrijfsgegevens(BedrijfsgegevensInfo info) throws PermissionException,ValidationException, VerzuimApplicationException;

	ArbodienstInfo updateArbodienst(ArbodienstInfo info) throws PermissionException, ValidationException, VerzuimApplicationException;
	UitvoeringsinstituutInfo updateuitkeringsinstantie(UitvoeringsinstituutInfo info) throws PermissionException, ValidationException, VerzuimApplicationException;
	BedrijfsartsInfo updateBedrijfsarts(BedrijfsartsInfo info) throws PermissionException,ValidationException, VerzuimApplicationException;
	void updateBedrijfsgegevens(BedrijfsgegevensInfo info) throws PermissionException,ValidationException, VerzuimApplicationException;

	ArbodienstInfo addArbodienst(ArbodienstInfo info) throws PermissionException, ValidationException, VerzuimApplicationException;
	UitvoeringsinstituutInfo addUitkeringsinstantie(UitvoeringsinstituutInfo info) throws PermissionException, ValidationException, VerzuimApplicationException;
	BedrijfsartsInfo addBedrijfsarts(BedrijfsartsInfo info) throws PermissionException,	ValidationException, VerzuimApplicationException;
	BedrijfsgegevensInfo addBedrijfsgegevens(BedrijfsgegevensInfo info) throws PermissionException,	ValidationException, VerzuimApplicationException;

	public void setLoginSession(LoginSessionRemote session) throws PermissionException;

}
