package com.gieselaar.verzuimbeheer.facades;

import java.util.List;

import javax.ejb.Remote;
import com.gieselaar.verzuimbeheer.exceptions.VerzuimApplicationException;
import com.gieselaar.verzuimbeheer.exceptions.PermissionException;
import com.gieselaar.verzuimbeheer.exceptions.ValidationException;
import com.gieselaar.verzuimbeheer.services.CascodeGroepInfo;
import com.gieselaar.verzuimbeheer.services.CascodeInfo;
@Remote
public interface CascodeFacadeRemote extends FacadeBaseRemote {
	List<CascodeGroepInfo> allCascodeGroepen() throws PermissionException, VerzuimApplicationException;
	List<CascodeInfo> allCascodes() throws PermissionException, VerzuimApplicationException;
	List<CascodeInfo> getCascodesForGroep(int cascodegroepid) throws PermissionException, VerzuimApplicationException;
	
	void deleteCascodeGroep(CascodeGroepInfo info) throws PermissionException, ValidationException, VerzuimApplicationException;
	void deleteCascode(CascodeInfo info) throws PermissionException, ValidationException, VerzuimApplicationException;

	CascodeGroepInfo updateCascodeGroep(CascodeGroepInfo info) throws PermissionException, ValidationException, VerzuimApplicationException;
	CascodeInfo updateCascode(CascodeInfo info) throws PermissionException, ValidationException, VerzuimApplicationException;

	void addCascodeGroep(CascodeGroepInfo info) throws PermissionException, ValidationException, VerzuimApplicationException;
	void addCascode(CascodeInfo info) throws PermissionException, ValidationException, VerzuimApplicationException;

	public void setLoginSession(LoginSessionRemote session) throws PermissionException;
}
