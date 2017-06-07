package com.gieselaar.verzuimbeheer.facades;

import java.util.Date;
import java.util.List;

import javax.ejb.Remote;

import com.gieselaar.verzuimbeheer.exceptions.VerzuimApplicationException;
import com.gieselaar.verzuimbeheer.exceptions.PermissionException;
import com.gieselaar.verzuimbeheer.exceptions.ValidationException;
import com.gieselaar.verzuimbeheer.reportservices.ActueelVerzuimInfo;
import com.gieselaar.verzuimbeheer.reportservices.VerzuimAantalInfo;
import com.gieselaar.verzuimbeheer.reportservices.WerknemerAantallenInfo;
import com.gieselaar.verzuimbeheer.reportservices.WerknemerVerzuimInfo;
import com.gieselaar.verzuimbeheer.services.WerkgeverInfo;

@Remote
public interface ReportFacadeRemote extends FacadeBaseRemote {

	public List<ActueelVerzuimInfo> getVerzuimPerMaand(Integer werkgeverId,
			Integer holdingId, Date start, Date einde,
			boolean inclusiefzwangerschap) throws PermissionException,
			VerzuimApplicationException, ValidationException;

	public List<WerknemerVerzuimInfo> getWerknemerVerzuimen(
			Integer werkgeverId, Integer holdingId, Integer oeId, Date start,
			Date einde, int aantalverzuimen) throws PermissionException, VerzuimApplicationException, ValidationException;

	public List<ActueelVerzuimInfo> getWerknemerVerzuimen(Integer werknemerId,
			Date start, Date einde) throws PermissionException,
			VerzuimApplicationException;

	public void setLoginSession(LoginSessionRemote session)
			throws PermissionException;

	public List<ActueelVerzuimInfo> getActueelVerzuim(Integer werkgeverid,
			Integer holdingid, Integer oeid, Date start, Date einde,
			boolean inclusiefzwangerschap) throws PermissionException,
			VerzuimApplicationException, ValidationException;

	public List<VerzuimAantalInfo> getAantalVerzuimenInPeriode(Integer werkgeverid,
			Integer holdingid, Integer oeid, Date start, Date einde,
			boolean inclusiefzwangerschap) throws PermissionException,
			VerzuimApplicationException, ValidationException;

	public List<WerknemerAantallenInfo> getAantalWerknemersAfdeling(
			Integer werkgeverid, Date peildatum)
			throws VerzuimApplicationException, PermissionException;

	public List<WerknemerAantallenInfo> getAantalWerknemersAfdeling(
			Integer werkgeverid, Date startdatum, Date einddatum)
			throws VerzuimApplicationException, PermissionException;

	public List<WerkgeverInfo> getWerkgevers(Integer werkgeverid,
			Integer holdingid) throws VerzuimApplicationException, ValidationException;

}
