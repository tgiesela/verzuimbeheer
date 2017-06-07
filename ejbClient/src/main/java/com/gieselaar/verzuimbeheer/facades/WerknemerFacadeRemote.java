package com.gieselaar.verzuimbeheer.facades;

import java.util.List;

import javax.ejb.Remote;

import com.gieselaar.verzuimbeheer.exceptions.VerzuimApplicationException;
import com.gieselaar.verzuimbeheer.exceptions.PermissionException;
import com.gieselaar.verzuimbeheer.exceptions.ValidationException;
import com.gieselaar.verzuimbeheer.services.WerkgeverInfo;
import com.gieselaar.verzuimbeheer.services.WerknemerFastInfo;
import com.gieselaar.verzuimbeheer.services.WerknemerInfo;

@Remote
public interface WerknemerFacadeRemote extends FacadeBaseRemote {

	void setLoginSession(LoginSessionRemote session) throws PermissionException;

	WerknemerInfo getWerknemer(int werknemerid) throws PermissionException, VerzuimApplicationException;
	WerkgeverInfo getWerkgever(int werkgeverid) throws PermissionException, VerzuimApplicationException;
	List<WerknemerInfo> getByBSN(Integer werkgever, String bsn) throws PermissionException, VerzuimApplicationException;
	List<WerknemerInfo> getByBSN(String bsn) throws PermissionException, VerzuimApplicationException;

	WerknemerInfo addWerknemer(WerknemerInfo werknemer) throws PermissionException, ValidationException, VerzuimApplicationException;
	void updateWerknemer(WerknemerInfo werknemer) throws PermissionException, ValidationException, VerzuimApplicationException;
	void deleteWerknemer(WerknemerInfo werknemer) throws PermissionException, ValidationException, VerzuimApplicationException;

	List<WerknemerFastInfo> getWerknemersByWerkgever(int werkgeverid) throws PermissionException, VerzuimApplicationException;
	List<WerknemerFastInfo> allWerknemers() throws PermissionException,	VerzuimApplicationException;
	List<WerknemerFastInfo> getWerknemerFast(Integer werknemerId) throws VerzuimApplicationException, PermissionException;

	List<WerknemerFastInfo> getActiveWerknemers() throws VerzuimApplicationException, PermissionException;
	List<WerknemerFastInfo> getActiveWerknemersByWerkgever(Integer werkgeverid) throws VerzuimApplicationException, PermissionException;
	List<WerknemerFastInfo> getInactiveWerknemers() throws VerzuimApplicationException, PermissionException;
	List<WerknemerFastInfo> getInactiveWerknemersByWerkgever(Integer werkgeverid) throws VerzuimApplicationException, PermissionException;


}
