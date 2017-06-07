package com.gieselaar.verzuimbeheer.facades;

import java.util.Date;
import java.util.List;

import javax.ejb.Remote;

import com.gieselaar.verzuimbeheer.exceptions.VerzuimApplicationException;
import com.gieselaar.verzuimbeheer.exceptions.PermissionException;
import com.gieselaar.verzuimbeheer.exceptions.ValidationException;
import com.gieselaar.verzuimbeheer.services.BtwInfo;
import com.gieselaar.verzuimbeheer.services.FactuurInfo;
import com.gieselaar.verzuimbeheer.services.FactuurTotaalInfo;
import com.gieselaar.verzuimbeheer.services.FactuurbetalingInfo;
import com.gieselaar.verzuimbeheer.services.FactuurcategorieInfo;
import com.gieselaar.verzuimbeheer.services.FactuuritemInfo;
import com.gieselaar.verzuimbeheer.services.FactuurkopInfo;
import com.gieselaar.verzuimbeheer.services.BtwInfo.__btwtariefsoort;
import com.gieselaar.verzuimbeheer.services.WerkzaamhedenInfo;

@Remote
public interface FactuurFacadeRemote extends FacadeBaseRemote {
	public BtwInfo getBtwById(Integer id) throws PermissionException, ValidationException, VerzuimApplicationException;

	public BtwInfo getActueelBtwPercentage(__btwtariefsoort soort)
			throws PermissionException, ValidationException, VerzuimApplicationException;

	public void addBtw(BtwInfo btw) throws PermissionException, ValidationException, VerzuimApplicationException;

	public void deleteBtw(BtwInfo btw) throws PermissionException, ValidationException, VerzuimApplicationException;

	public BtwInfo updateBtw(BtwInfo btw) throws PermissionException, ValidationException, VerzuimApplicationException;

	public List<BtwInfo> allBtws() throws PermissionException, VerzuimApplicationException;

	public void setLoginSession(LoginSessionRemote session) throws PermissionException;

	public void deleteWerkzaamheid(WerkzaamhedenInfo werk)
			throws PermissionException, ValidationException, VerzuimApplicationException;

	public List<WerkzaamhedenInfo> getWerkzaamheden(Integer id, Date startperiode, Date eindperiode)
			throws PermissionException, ValidationException, VerzuimApplicationException;

	public List<WerkzaamhedenInfo> getWerkzaamhedenWerkgever(Integer gebruiker, Date startperiode, Date eindperiode,
			Integer werkgever) throws PermissionException, ValidationException, VerzuimApplicationException;

	public List<WerkzaamhedenInfo> getWerkzaamhedenHolding(Integer gebruiker, Date startperiode, Date eindperiode,
			Integer holding) throws PermissionException, ValidationException, VerzuimApplicationException;

	public WerkzaamhedenInfo addWerkzaamheid(WerkzaamhedenInfo werkzaamheid)
			throws PermissionException, ValidationException, VerzuimApplicationException;

	public WerkzaamhedenInfo updateWerkzaamheid(WerkzaamhedenInfo werkzaamheid)
			throws PermissionException, ValidationException, VerzuimApplicationException;

	public List<FactuurcategorieInfo> getFactuurcategorien()
			throws PermissionException, ValidationException, VerzuimApplicationException;

	public void addFactuurcategorie(FactuurcategorieInfo factuurcategorie)
			throws PermissionException, ValidationException, VerzuimApplicationException;

	public void deleteFactuurcategorie(FactuurcategorieInfo btw)
			throws PermissionException, ValidationException, VerzuimApplicationException;

	public FactuurcategorieInfo updateFactuurcategorie(FactuurcategorieInfo btw)
			throws PermissionException, ValidationException, VerzuimApplicationException;

	public List<FactuurkopInfo> getFactuurkoppen()
			throws PermissionException, ValidationException, VerzuimApplicationException;

	public void addFactuurkop(FactuurkopInfo factuurkop)
			throws PermissionException, ValidationException, VerzuimApplicationException;

	public void deleteFactuurkop(FactuurkopInfo btw)
			throws PermissionException, ValidationException, VerzuimApplicationException;

	public FactuurkopInfo updateFactuurkop(FactuurkopInfo btw)
			throws PermissionException, ValidationException, VerzuimApplicationException;

	public void addFactuuritem(FactuuritemInfo factuuritem)
			throws PermissionException, ValidationException, VerzuimApplicationException;

	public void deleteFactuuritem(FactuuritemInfo btw)
			throws PermissionException, ValidationException, VerzuimApplicationException;

	public FactuuritemInfo updateFactuuritem(FactuuritemInfo btw)
			throws PermissionException, ValidationException, VerzuimApplicationException;

	public List<FactuuritemInfo> getFactuuritems(Date startperiode, Date eindperiode)
			throws PermissionException, ValidationException, VerzuimApplicationException;

	public void afsluitenMaand(int jaar, int maand)
			throws PermissionException, ValidationException, VerzuimApplicationException;

	public Integer getAantalontbrekendeFacturen(int jaar, int maand)
			throws ValidationException, VerzuimApplicationException, PermissionException;

	public FactuurTotaalInfo updateFactuur(FactuurInfo factuur)
			throws PermissionException, ValidationException, VerzuimApplicationException;

	public List<FactuurTotaalInfo> getFacturenInPeriode(Date firstmonth, Date lastmonth, boolean details)
			throws ValidationException, VerzuimApplicationException, PermissionException;

	public FactuurTotaalInfo getFactuurDetails(Integer id)
			throws ValidationException, VerzuimApplicationException, PermissionException;

	public List<FactuurTotaalInfo> getFacturenHoldingByFactuurnr(Integer factuurnr)
			throws ValidationException, VerzuimApplicationException, PermissionException;

	public List<FactuurTotaalInfo> getFacturenInPeriodeByHolding(Date firstmonth, Date lastmonth, Integer holdingid,
			boolean details) throws PermissionException, VerzuimApplicationException;

	public List<FactuurTotaalInfo> getFacturenInPeriodeByWerkgever(Date firstmonth, Date lastmonth, Integer werkgeverid,
			boolean details) throws VerzuimApplicationException, PermissionException;

	public void terugdraaienMaand(int jaar, int maand)
			throws PermissionException, ValidationException, VerzuimApplicationException;

	public List<FactuurbetalingInfo> getFactuurbetalingenForFactuur(Integer id)
			throws VerzuimApplicationException, PermissionException;

	public void deleteFactuurbetaling(FactuurbetalingInfo factuurbetaling)
			throws PermissionException, ValidationException, VerzuimApplicationException;

	public FactuurbetalingInfo addFactuurbetaling(FactuurbetalingInfo factuurbetaling)
			throws PermissionException, ValidationException, VerzuimApplicationException;

	public FactuurbetalingInfo updateFactuurbetaling(FactuurbetalingInfo factuurbetaling)
			throws PermissionException, ValidationException, VerzuimApplicationException;

	public List<FactuurbetalingInfo> getFactuurbetalingen()
			throws PermissionException, ValidationException, VerzuimApplicationException;

}
