package com.gieselaar.verzuimbeheer.facades;

import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateful;

import com.gieselaar.verzuimbeheer.exceptions.VerzuimApplicationException;
import com.gieselaar.verzuimbeheer.exceptions.PermissionException;
import com.gieselaar.verzuimbeheer.exceptions.ValidationException;
import com.gieselaar.verzuimbeheer.facades.AutorisatieFacadeRemote.__applicatiefunctie;
import com.gieselaar.verzuimbeheer.services.BtwInfo;
import com.gieselaar.verzuimbeheer.services.BtwInfo.__btwtariefsoort;
import com.gieselaar.verzuimbeheer.services.FactuurInfo;
import com.gieselaar.verzuimbeheer.services.FactuurTotaalInfo;
import com.gieselaar.verzuimbeheer.services.FactuurbetalingInfo;
import com.gieselaar.verzuimbeheer.services.FactuurcategorieInfo;
import com.gieselaar.verzuimbeheer.services.FactuuritemInfo;
import com.gieselaar.verzuimbeheer.services.FactuurkopInfo;
import com.gieselaar.verzuimbeheer.services.GebruikerBean;
import com.gieselaar.verzuimbeheer.services.FactuurBean;
import com.gieselaar.verzuimbeheer.services.WerkzaamhedenInfo;

@Stateful(mappedName="java:global/verzuimbeheer/FactuurFacade", name="FactuurFacade")
@LocalBean
public class FactuurFacade extends FacadeBase implements
		FactuurFacadeRemote {

	@EJB FactuurBean factuurEJB;
	@EJB private GebruikerBean gebruikerEJB;
	private LoginSessionRemote loginsession;

	private void setCurrentuser(){
		factuurEJB.setCurrentuser(this.getCurrentuser());
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
	/**
	 * Implementation abstract functions from FactuurFacadeRemote
	 */
	@Override
	public BtwInfo getBtwById(Integer id) throws PermissionException,
			ValidationException, VerzuimApplicationException {
		setCurrentuser();
		checkPermission(__applicatiefunctie.RAADPLEGENFACTUREN);
		return factuurEJB.getBtwById(id);
	}
	@Override
	public void addBtw(BtwInfo info) throws PermissionException,
			ValidationException, VerzuimApplicationException {
		setCurrentuser();
		checkPermission(__applicatiefunctie.BEHEERFACTUREN);
		factuurEJB.addBtw(info);
	}
	@Override
	public void deleteBtw(BtwInfo info) throws PermissionException,
			ValidationException, VerzuimApplicationException {
		setCurrentuser();
		checkPermission(__applicatiefunctie.VERWIJDERFACTUREN);
		factuurEJB.deleteBtw(info);
	}
	@Override
	public BtwInfo updateBtw(BtwInfo info) throws PermissionException,
			ValidationException, VerzuimApplicationException {
		setCurrentuser();
		checkPermission(__applicatiefunctie.BEHEERFACTUREN);
		return factuurEJB.updateBtw(info);
	}
	@Override
	public List<BtwInfo> allBtws() throws PermissionException,
			VerzuimApplicationException {
		setCurrentuser();
		checkPermission(__applicatiefunctie.RAADPLEGENFACTUREN);
		return factuurEJB.getBtws();
	}

	@Override
	public BtwInfo getActueelBtwPercentage(__btwtariefsoort soort)
			throws PermissionException, ValidationException,
			VerzuimApplicationException {
		setCurrentuser();
		checkPermission(__applicatiefunctie.RAADPLEGENFACTUREN);
		return factuurEJB.getActueelBtwPercentage(soort);
	}

	@Override
	public void deleteWerkzaamheid(WerkzaamhedenInfo werk)
			throws PermissionException, ValidationException,
			VerzuimApplicationException {
		setCurrentuser();
		checkPermission(__applicatiefunctie.VERWIJDERWERKZAAMHEDEN);
		factuurEJB.deleteWerkzaamheid(werk);
	}

	/**
	 * Obtain list of Werkzaamheden for a user between start and end date.
	 * 
	 * @user
	 * @startdate
	 * @enddate
	 */
	@Override
	public List<WerkzaamhedenInfo> getWerkzaamheden(Integer user,
			Date startperiode, Date eindperiode) throws PermissionException,
			ValidationException, VerzuimApplicationException {
		setCurrentuser();
		checkPermission(__applicatiefunctie.RAADPLEGENWERKZAAMHEDEN);
		return factuurEJB.getWerkzaamheden(user, startperiode, eindperiode);
	}

	@Override
	public List<WerkzaamhedenInfo> getWerkzaamhedenWerkgever(Integer gebruiker,
			Date startperiode, Date eindperiode, Integer werkgever)
			throws PermissionException, ValidationException,
			VerzuimApplicationException {
		setCurrentuser();
		checkPermission(__applicatiefunctie.RAADPLEGENWERKZAAMHEDEN);
		return factuurEJB.getWerkzaamhedenWerkgever(gebruiker, startperiode, eindperiode, werkgever);
	}

	@Override
	public List<WerkzaamhedenInfo> getWerkzaamhedenHolding(Integer gebruiker,
			Date startperiode, Date eindperiode, Integer holding)
			throws PermissionException, ValidationException,
			VerzuimApplicationException {
		setCurrentuser();
		checkPermission(__applicatiefunctie.RAADPLEGENWERKZAAMHEDEN);
		return factuurEJB.getWerkzaamhedenHolding(gebruiker, startperiode, eindperiode, holding);
	}
	@Override
	public WerkzaamhedenInfo addWerkzaamheid(WerkzaamhedenInfo werkzaamheid) throws PermissionException, 
	ValidationException, VerzuimApplicationException {
		setCurrentuser();
		checkPermission(__applicatiefunctie.BEHEERWERKZAAMHEDEN);
		return factuurEJB.addWerkzaamheid(werkzaamheid);
	}

	@Override
	public WerkzaamhedenInfo updateWerkzaamheid(WerkzaamhedenInfo werkzaamheid) throws PermissionException, 
	ValidationException, VerzuimApplicationException {
		setCurrentuser();
		checkPermission(__applicatiefunctie.BEHEERWERKZAAMHEDEN);
		return factuurEJB.updateWerkzaamheid(werkzaamheid);
	}

	@Override
	public List<FactuurcategorieInfo> getFactuurcategorien() throws PermissionException,
			ValidationException, VerzuimApplicationException {
		setCurrentuser();
		checkPermission(__applicatiefunctie.RAADPLEGENFACTUREN);
		return factuurEJB.getFactuurcategorien();
	}

	@Override
	public void addFactuurcategorie(FactuurcategorieInfo factuurcategorie)
			throws PermissionException, ValidationException,
			VerzuimApplicationException {
		setCurrentuser();
		checkPermission(__applicatiefunctie.BEHEERFACTUREN);
		factuurEJB.addFactuurcategorie(factuurcategorie);
	}

	@Override
	public void deleteFactuurcategorie(FactuurcategorieInfo factuurcategorie)
			throws PermissionException, ValidationException,
			VerzuimApplicationException {
		setCurrentuser();
		checkPermission(__applicatiefunctie.VERWIJDERFACTUREN);
		factuurEJB.deleteFactuurcategorie(factuurcategorie);
	}

	@Override
	public FactuurcategorieInfo updateFactuurcategorie(FactuurcategorieInfo factuurcategorie)
			throws PermissionException, ValidationException,
			VerzuimApplicationException {
		setCurrentuser();
		checkPermission(__applicatiefunctie.BEHEERWERKZAAMHEDEN);
		return factuurEJB.updateFactuurcategorie(factuurcategorie);
	}

	@Override
	public List<FactuurkopInfo> getFactuurkoppen() throws PermissionException, ValidationException,
			VerzuimApplicationException {
		setCurrentuser();
		checkPermission(__applicatiefunctie.RAADPLEGENFACTUREN);
		return factuurEJB.getFactuurkoppen();
	}

	@Override
	public void addFactuurkop(FactuurkopInfo factuurkop)
			throws PermissionException, ValidationException,
			VerzuimApplicationException {
		setCurrentuser();
		checkPermission(__applicatiefunctie.BEHEERFACTUREN);
		factuurEJB.addFactuurkop(factuurkop);
	}

	@Override
	public void deleteFactuurkop(FactuurkopInfo factuurkop)
			throws PermissionException, ValidationException,
			VerzuimApplicationException {
		setCurrentuser();
		checkPermission(__applicatiefunctie.VERWIJDERFACTUREN);
		factuurEJB.deleteFactuurkop(factuurkop);
	}

	@Override
	public FactuurkopInfo updateFactuurkop(FactuurkopInfo factuurkop)
			throws PermissionException, ValidationException,
			VerzuimApplicationException {
		setCurrentuser();
		checkPermission(__applicatiefunctie.BEHEERFACTUREN);
		return factuurEJB.updateFactuurkop(factuurkop);
	}

	@Override
	public List<FactuuritemInfo> getFactuuritems(Date startperiode, Date eindperiode) throws PermissionException,
			ValidationException, VerzuimApplicationException {
		setCurrentuser();
		checkPermission(__applicatiefunctie.RAADPLEGENFACTUREN);
		return factuurEJB.getFactuuritems(startperiode, eindperiode);
	}

	@Override
	public void addFactuuritem(FactuuritemInfo factuuritem)
			throws PermissionException, ValidationException,
			VerzuimApplicationException {
		setCurrentuser();
		checkPermission(__applicatiefunctie.BEHEERFACTUREN);
		factuurEJB.addFactuuritem(factuuritem);
	}

	@Override
	public void deleteFactuuritem(FactuuritemInfo factuuritem)
			throws PermissionException, ValidationException,
			VerzuimApplicationException {
		setCurrentuser();
		checkPermission(__applicatiefunctie.VERWIJDERFACTUREN);
		factuurEJB.deleteFactuuritem(factuuritem);
	}

	@Override
	public FactuuritemInfo updateFactuuritem(FactuuritemInfo factuuritem)
			throws PermissionException, ValidationException,
			VerzuimApplicationException {
		setCurrentuser();
		checkPermission(__applicatiefunctie.BEHEERFACTUREN);
		return factuurEJB.updateFactuuritem(factuuritem);
	}

	@Override
	public void afsluitenMaand(int jaar, int maand) throws PermissionException,
			ValidationException, VerzuimApplicationException {
		setCurrentuser();
		checkPermission(__applicatiefunctie.BEHEERFACTUREN);
		factuurEJB.afsluitenMaand(jaar, maand);
	}
	@Override
	public void terugdraaienMaand(int jaar, int maand)
			throws PermissionException, ValidationException,
			VerzuimApplicationException {
		setCurrentuser();
		checkPermission(__applicatiefunctie.BEHEERFACTUREN);
		factuurEJB.terugdraaienMaand(jaar, maand);
	}
	@Override
	public Integer getAantalontbrekendeFacturen(int jaar, int maand) throws ValidationException, VerzuimApplicationException, PermissionException {
		setCurrentuser();
		checkPermission(__applicatiefunctie.BEHEERFACTUREN);
		return factuurEJB.getAantalontbrekendeFacturen(jaar, maand);
	}

	@Override
	public FactuurTotaalInfo updateFactuur(FactuurInfo factuur) throws PermissionException,
			ValidationException, VerzuimApplicationException {
		setCurrentuser();
		checkPermission(__applicatiefunctie.BEHEERFACTUREN);
		return factuurEJB.updateFactuur(factuur);
	}

	@Override
	public List<FactuurTotaalInfo> getFacturenInPeriode(Date firstmonth,
			Date lastmonth, boolean details) throws ValidationException,
			VerzuimApplicationException, PermissionException {
		setCurrentuser();
		checkPermission(__applicatiefunctie.RAADPLEGENFACTUREN);
		return factuurEJB.getFacturenInPeriode(firstmonth, lastmonth, details);
	}

	@Override
	public FactuurTotaalInfo getFactuurDetails(Integer id)
			throws ValidationException, VerzuimApplicationException,
			PermissionException {
		setCurrentuser();
		checkPermission(__applicatiefunctie.RAADPLEGENFACTUREN);
		return factuurEJB.getFactuurDetails(id);
	}

	@Override
	public List<FactuurTotaalInfo> getFacturenHoldingByFactuurnr(
			Integer factuurnr) throws ValidationException,
			VerzuimApplicationException, PermissionException {
		setCurrentuser();
		checkPermission(__applicatiefunctie.RAADPLEGENFACTUREN);
		return factuurEJB.getFacturenHoldingByFactuurnr(factuurnr);
	}

	@Override
	public List<FactuurTotaalInfo> getFacturenInPeriodeByHolding(
			Date firstmonth, Date lastmonth, Integer holdingid, boolean details) throws PermissionException, VerzuimApplicationException {
		setCurrentuser();
		checkPermission(__applicatiefunctie.RAADPLEGENFACTUREN);
		return factuurEJB.getFacturenInPeriodeByHolding(firstmonth, lastmonth, holdingid, details);
	}

	@Override
	public List<FactuurTotaalInfo> getFacturenInPeriodeByWerkgever(
			Date firstmonth, Date lastmonth, Integer werkgeverid, boolean details) throws VerzuimApplicationException, PermissionException {
		setCurrentuser();
		checkPermission(__applicatiefunctie.RAADPLEGENFACTUREN);
		return factuurEJB.getFacturenInPeriodeByWerkgever(firstmonth, lastmonth, werkgeverid, details);
	}

	@Override
	public List<FactuurbetalingInfo> getFactuurbetalingenForFactuur(Integer factuurid)
			throws VerzuimApplicationException, PermissionException {
		setCurrentuser();
		checkPermission(__applicatiefunctie.RAADPLEGENFACTUREN);
		return factuurEJB.getFactuurbetalingenForFactuur(factuurid);
	}

	@Override
	public void deleteFactuurbetaling(FactuurbetalingInfo factuurbetaling)
			throws PermissionException, ValidationException, VerzuimApplicationException {
		setCurrentuser();
		checkPermission(__applicatiefunctie.VERWIJDERFACTUREN);
		factuurEJB.deleteFactuurbetaling(factuurbetaling);
	}

	@Override
	public FactuurbetalingInfo addFactuurbetaling(FactuurbetalingInfo factuurbetaling)
			throws PermissionException, ValidationException, VerzuimApplicationException {
		setCurrentuser();
		checkPermission(__applicatiefunctie.BEHEERFACTUREN);
		return factuurEJB.addFactuurbetaling(factuurbetaling);
	}

	@Override
	public FactuurbetalingInfo updateFactuurbetaling(FactuurbetalingInfo factuurbetaling)
			throws PermissionException, ValidationException, VerzuimApplicationException {
		setCurrentuser();
		checkPermission(__applicatiefunctie.BEHEERFACTUREN);
		return factuurEJB.updateFactuurbetaling(factuurbetaling);
	}

	public List<FactuurbetalingInfo> getFactuurbetalingen() throws VerzuimApplicationException, PermissionException {
		setCurrentuser();
		checkPermission(__applicatiefunctie.RAADPLEGENFACTUREN);
		return factuurEJB.getFactuurbetalingen();
	}
}
