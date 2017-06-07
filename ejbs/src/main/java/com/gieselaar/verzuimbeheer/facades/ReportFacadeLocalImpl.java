package com.gieselaar.verzuimbeheer.facades;

import java.util.Date;
import java.util.List;

import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.EJBContext;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;

import com.gieselaar.verzuimbeheer.exceptions.VerzuimApplicationException;
import com.gieselaar.verzuimbeheer.exceptions.PermissionException;
import com.gieselaar.verzuimbeheer.exceptions.ValidationException;
import com.gieselaar.verzuimbeheer.reportservices.ActueelVerzuimInfo;
import com.gieselaar.verzuimbeheer.reportservices.VerzuimAantalInfo;
import com.gieselaar.verzuimbeheer.reportservices.WerknemerAantallenInfo;
import com.gieselaar.verzuimbeheer.reportservices.WerknemerVerzuimInfo;
import com.gieselaar.verzuimbeheer.services.GebruikerBean;
import com.gieselaar.verzuimbeheer.services.ReportBean;
import com.gieselaar.verzuimbeheer.services.WerkgeverInfo;

/**
 * Session Bean implementation class ReportFacadeLocal
 * Actually the same as ReportFacadeRemote, but without
 * security.
 */
@Stateless(mappedName="java:global/verzuimbeheer/ReportFacadeLocal", name="ReportFacadeLocal")
@LocalBean
public class ReportFacadeLocalImpl extends FacadeBase implements ReportFacadeLocal {
	@EJB private ReportBean reportEJB;
	@Resource EJBContext context;
	@EJB private GebruikerBean gebruikerEJB;
	private LoginSessionRemote loginsession;

	public ReportFacadeLocalImpl() {
	}

	private void setCurrentuser(){
		reportEJB.setCurrentuser(this.getCurrentuser());		
	}
	@Override
	public List<WerknemerVerzuimInfo> getWerknemerVerzuimen(Integer werkgeverId,
			Integer holdingId, Integer oeId, Date start, Date einde, int aantalverzuimen) throws PermissionException, VerzuimApplicationException, ValidationException{
		setCurrentuser();
		return reportEJB.getWerknemerVerzuimen(werkgeverId, holdingId, oeId, start, einde, aantalverzuimen);
	}

	public List<ActueelVerzuimInfo> getWerknemerVerzuimen(Integer werknemerId,
			Date start, Date einde) throws PermissionException, VerzuimApplicationException {
		setCurrentuser();
		return reportEJB.getWerknemerVerzuimen(werknemerId, start, einde);
		
	}

	@Override
	public List<ActueelVerzuimInfo> getVerzuimPerMaand(Integer werkgeverId,
			Integer holdingId, Date start, Date einde, boolean inclusiefzwangerschap) throws PermissionException, VerzuimApplicationException, ValidationException {
		setCurrentuser();
		return reportEJB.getVerzuimPerMaand(werkgeverId, holdingId,
				start, einde, inclusiefzwangerschap);
	}

	@Override
	public List<ActueelVerzuimInfo> getActueelVerzuim(Integer werkgeverid,
			Integer holdingid, Integer oeid, Date start, Date einde, boolean inclusiefzwangerschap)
			throws PermissionException, VerzuimApplicationException, ValidationException {
		setCurrentuser();
		return reportEJB.getActueelVerzuim(werkgeverid, holdingid, oeid, 
				start, einde, inclusiefzwangerschap);
	}
	@Override
	public List<VerzuimAantalInfo> getAantalVerzuimenInPeriode(
			Integer werkgeverid, Integer holdingid, Integer oeid, Date start,
			Date einde, boolean inclusiefzwangerschap)
			throws PermissionException, VerzuimApplicationException, ValidationException {
		setCurrentuser();
		return reportEJB.getAantalverzuimenInPeriode(werkgeverid, holdingid, oeid, 
				start, einde, inclusiefzwangerschap);
	}
	@Override
	public List<WerknemerAantallenInfo> getAantalWerknemersAfdeling(
			Integer werkgeverid, Date peildatum)
			throws VerzuimApplicationException, PermissionException {
		setCurrentuser();
		return reportEJB.getAantalWerknemersAfdeling(werkgeverid, peildatum);
	}
	@Override
	public List<WerknemerAantallenInfo> getAantalWerknemersAfdeling(Integer werkgeverid, Date startdatum,
			Date einddatum) throws VerzuimApplicationException, PermissionException {
		setCurrentuser();
		return reportEJB.getAantalWerknemersAfdeling(werkgeverid, startdatum, einddatum);
	}
	@Override
	public List<WerkgeverInfo> getWerkgevers(Integer werkgeverid,
			Integer holdingid) throws VerzuimApplicationException, ValidationException {
		setCurrentuser();
		return reportEJB.getWerkgevers(werkgeverid, holdingid);
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
