package com.gieselaar.verzuimbeheer.services;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.EJBContext;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.Query;

import com.gieselaar.verzuimbeheer.entities.Tarief;
import com.gieselaar.verzuimbeheer.exceptions.VerzuimApplicationException;
import com.gieselaar.verzuimbeheer.services.HoldingInfo.__factuurtype;
import com.gieselaar.verzuimbeheer.exceptions.ValidationException;
import com.gieselaar.verzuimbeheer.utils.DateOnly;
import com.gieselaar.verzuimbeheer.utils.FactuurConversion;

/**
 * Session Bean implementation class WerkgeverBean
 */
@Stateless
@LocalBean
public class TariefBean extends BeanBase{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Resource transient EJBContext context;
	@EJB WerkgeverBean werkgeverEJB;
	private FactuurConversion converter;
	private HashMap<Integer, String> htHoldings = new HashMap<>();
	private HashMap<Integer, String> htWerkgevers = new HashMap<>();
	private boolean initialized = false;
	/**
     * Default constructor. 
     */
	public TariefBean() {
		converter = new FactuurConversion();
    }
	private void inithashtables() throws VerzuimApplicationException{
		List<HoldingInfo> holdings;
		List<WerkgeverInfo> werkgevers;
		holdings = werkgeverEJB.getHoldings();
		werkgevers = werkgeverEJB.getAllSimple();
		htHoldings.clear();
		for (HoldingInfo hi:holdings){
			htHoldings.put(hi.getId(), hi.getNaam());
		}
		htWerkgevers.clear();
		for (WerkgeverInfo wi:werkgevers){
			htWerkgevers.put(wi.getId(), wi.getNaam());
		}
	}
    private TariefInfo completeTarief(Tarief w) throws VerzuimApplicationException{
		TariefInfo hi = converter.fromEntity(w);
		String naam;
		if (!initialized){
			inithashtables();
			initialized = true;
		}
		if (w.getHoldingId() != null){
			if (!htHoldings.containsKey(w.getHoldingId())){
				inithashtables();
				initialized = true;
			}
			naam = htHoldings.get(w.getHoldingId());
		}else{
			if (!htWerkgevers.containsKey(w.getWerkgeverId())){
				inithashtables();
				initialized = true;
			}
			naam = htWerkgevers.get(w.getWerkgeverId());
		}
		hi.setWerkgevernaam(naam);
		return hi;
    }
	public TariefInfo getTariefById(Integer id) throws VerzuimApplicationException {
		if (id == null)
			return null;
		Query q = createQuery("select t from Tarief t where t.id = :id");
		q.setParameter("id", id);
		@SuppressWarnings("unchecked")
		List<Tarief> result = (List<Tarief>)getResultList(q);
		if (result.size() != 1)
			return null;
		Tarief t = result.get(0);
		
		return completeTarief(t);
	}
	
	@SuppressWarnings("unchecked")
	public List<TariefInfo> getTarieven() throws VerzuimApplicationException {
		Query q = createQuery("select t from Tarief t"); 
		List<Tarief> result = (List<Tarief>)getResultList(q);
		List<TariefInfo> inforesult = new ArrayList<>();
		for (Tarief w: result)
			inforesult.add(completeTarief(w));
		
		return inforesult;
	}
	public List<TariefInfo> getTarievenByWerkgever(Integer werkgeverid) throws VerzuimApplicationException {
		Query q = createQuery("Select t from Tarief t where t.werkgeverId = :werkgeverid");
		q.setParameter("werkgeverid", werkgeverid);
		@SuppressWarnings("unchecked")
		List<Tarief> result = (List<Tarief>)getResultList(q);
		List<TariefInfo> inforesult = new ArrayList<>();
		for (Tarief w: result)
			inforesult.add(completeTarief((Tarief)w));

		return inforesult;
	}
	public List<TariefInfo> getTarievenByHolding(Integer holdingid) throws VerzuimApplicationException {
		Query q = createQuery("Select t from Tarief t where t.holdingId = :holdingid");
		q.setParameter("holdingid", holdingid);
		@SuppressWarnings("unchecked")
		List<Tarief> result = (List<Tarief>)getResultList(q);
		List<TariefInfo> inforesult = new ArrayList<>();
		for (Tarief w: result)
			inforesult.add(completeTarief(w));

		return inforesult;
	}
	public TariefInfo getActueelTariefByWerkgever(Integer werkgeverid) throws ValidationException, VerzuimApplicationException {
		Query q = createQuery("Select t from Tarief t where t.werkgeverId = :werkgeverid and t.einddatum is null");
		q.setParameter("werkgeverid", werkgeverid);
		@SuppressWarnings("unchecked")
		List<Tarief> result = (List<Tarief>)getResultList(q);
		if (result.isEmpty())
			return null;

		if (result.size() > 1)
			throw new ValidationException("Meer dan 1 actueel tarief gevonden bij werkgever: " + werkgeverid);

		Tarief w = result.get(0);
		return completeTarief(w);
	}
	public TariefInfo getActueelTariefByHolding(Integer holdingid) throws ValidationException, VerzuimApplicationException {
		Query q = createQuery("Select t from Tarief t where t.holdingId = :holdingid and t.einddatum is null");
		q.setParameter("holdingid", holdingid);
		@SuppressWarnings("unchecked")
		List<Tarief> result = (List<Tarief>)getResultList(q);
		if (result.isEmpty())
			return null;

		if (result.size() > 1)
			throw new ValidationException("Meer dan 1 actueel tarief gevonden bij werkgever: " + holdingid);

		Tarief w = result.get(0);
		return completeTarief(w);
	}
	public TariefInfo getTariefByWerkgever(Integer werkgeverid, Date date) throws ValidationException, VerzuimApplicationException {
		Query q = createQuery("Select t from Tarief t where t.werkgeverId = :werkgeverid " + 
							  "   and t.ingangsdatum <= :date and (t.einddatum is null or t.einddatum >= :date)");
		q.setParameter("werkgeverid", werkgeverid);
		q.setParameter("date", date);
		@SuppressWarnings("unchecked")
		List<Tarief> result = (List<Tarief>)getResultList(q);
		if (result.isEmpty())
			return null;

		if (result.size() > 1)
			throw new ValidationException("Meer dan 1 actueel tarief gevonden bij werkgever: " + werkgeverid + " voor datum: " + date.toString());

		Tarief w = result.get(0);
		return completeTarief(w);
	}
	public TariefInfo getTariefByHolding(Integer holdingid, Date date) throws ValidationException, VerzuimApplicationException {
		Query q = createQuery("Select t from Tarief t where t.holdingId = :holdingid " + 
							  "   and t.ingangsdatum <= :date and (t.einddatum is null or t.einddatum >= :date)");
		q.setParameter("holdingid", holdingid);
		q.setParameter("date", date);
		@SuppressWarnings("unchecked")
		List<Tarief> result = (List<Tarief>)getResultList(q);
		if (result.isEmpty())
			return null;

		if (result.size() > 1)
			throw new ValidationException("Meer dan 1 actueel tarief gevonden bij holding: " + holdingid + " voor datum: " + date.toString());

		Tarief w = result.get(0);
		return completeTarief(w);
	}
	private void checkHoldingTarieven(TariefInfo tarief) throws ValidationException, VerzuimApplicationException{
		if (tarief.getHoldingId() != null){
			HoldingInfo hi = werkgeverEJB.getHoldingById(tarief.getHoldingId());
			if (hi.getId().equals(tarief.getHoldingId())){
				if (hi.isFactureren()){
					if (hi.getFactuurtype() == __factuurtype.SEPARAAT){
						if (tarief.getEinddatum() == null || DateOnly.after(tarief.getEinddatum(), new Date())){
							throw new ValidationException("Tarief voor holding waarnaar facturen separaat worden verstuurd. Dit is niet toegestaan.");
						}
					}
				}else{
					throw new ValidationException("Tarief voor holding waar factureren niet aanstaat. Dit is niet toegestaan.");
				}
			}
		}
	}
	public void addTarief(TariefInfo info) throws ValidationException, VerzuimApplicationException{
		try {
			info.validate();
		} catch (ValidationException e) {
			throw e;
		}
		/*
		 * Check overlapping dates
		 */
		Query q;
		TariefInfo current;
		if (info.getHoldingId() == null){
			q = createQuery("Select count(t.id) from Tarief t where t.werkgeverId = :werkgever and (t.ingangsdatum > :start OR " 
						  + "t.einddatum > :start OR "
						  + "(:start > t.ingangsdatum and :start < t.einddatum))");
			q.setParameter("werkgever", info.getWerkgeverId());
		}else{
			q = createQuery("Select count(t.id) from Tarief t where t.holdingId = :holding and (t.ingangsdatum > :start OR " 
					  + "t.einddatum > :start OR "
					  + "(:start > t.ingangsdatum and :start < t.einddatum))");
			q.setParameter("holding", info.getHoldingId());
		}
		q.setParameter("start", info.getIngangsdatum());
		long count = (long) q.getSingleResult();
		if (count > 0)
			throw new ValidationException("Ingangsdatum tarief overlapt met reeds bestaand tarief");

		if (info.getWerkgeverId()!=null){
			current = getActueelTariefByWerkgever(info.getWerkgeverId());
		}else{
			current = getActueelTariefByHolding(info.getHoldingId());
		}
				
		if (current != null){
			if (info.getIngangsdatum().before(current.getIngangsdatum())){
				throw new ValidationException("Er zijn al recentere tarieven.");
			}
			Calendar cal = Calendar.getInstance();
			cal.setTime(info.getIngangsdatum());
			cal.add(Calendar.DATE, -1);
			current.setEinddatum(cal.getTime());
			this.updateTarief(current);
		}
		
		checkHoldingTarieven(info);

		if (info.getWerkgeverId()==null){
			List<WerkgeverInfo> werkgevers = werkgeverEJB.getWerkgeversHolding(info.getHoldingId());
			for (WerkgeverInfo wg:werkgevers){
				current = getActueelTariefByWerkgever(wg.getId());
				if (current != null){
					if (info.getIngangsdatum().before(current.getIngangsdatum())){
						/*
						 * Dit is waarschijnlijk niet de bedoeling. Hoeft ook niet fout te zijn,
						 * maar om onbedoelde tariefwijzigingen te voorkomen toch maar de controle.
						 */
						throw new ValidationException("Er zijn al recentere tarieven bij werkgever: " + wg.getNaam());
					}
					Calendar cal = Calendar.getInstance();
					cal.setTime(info.getIngangsdatum());
					cal.add(Calendar.DATE, -1);
					current.setEinddatum(cal.getTime());
					this.updateTarief(current);
				}
			}
		}
		
		Tarief tarief = converter.toEntity(info, this.getCurrentuser());
		this.insertEntity(tarief);
	}
	public void updateTarief(TariefInfo info) throws ValidationException, VerzuimApplicationException{
		try {
			info.validate();
		} catch (ValidationException e) {
			throw e;
		}
		TariefInfo current = getTariefById(info.getId());
		if (current == null){
			throw new ValidationException("Kan niet bestaand tarief niet wijzigen");
		}
		checkHoldingTarieven(info);

    	Tarief tarief = converter.toEntity(info, this.getCurrentuser());
    	this.updateEntity(tarief);
	}
	public void deleteTarief(TariefInfo info) throws ValidationException, VerzuimApplicationException{
		Tarief tarief= converter.toEntity(info, this.getCurrentuser());
		this.deleteEntity(tarief);
	}
	public boolean isVasttariefHuisbezoeken(Date peildatum, Integer holdingid,
			Integer werkgeverid) throws VerzuimApplicationException, ValidationException {
		List<TariefInfo> tarieven;
		TariefInfo currenttarief = null;

		if (holdingid != null){
			tarieven = getTarievenByHolding(holdingid);
			currenttarief = getCurrentTarief(tarieven, peildatum);
		}
		if (currenttarief == null){
			tarieven = getTarievenByWerkgever(werkgeverid);
			currenttarief = getCurrentTarief(tarieven, peildatum);
		}		
		if (currenttarief == null) {
			throw new ValidationException(
					"Kan tarieven niet vinden voor datum: "
							+ peildatum.toString());
		}
		return currenttarief.getVasttariefhuisbezoeken();
	}
	private TariefInfo getCurrentTarief(List<TariefInfo> tarieven, Date peildatum) {
		for (TariefInfo t : tarieven) {
			if ((t.getIngangsdatum().before(peildatum)) &&
				(t.getEinddatum() == null
						|| t.getEinddatum().after(peildatum))){
					return t;
			}
		}
		return null;
	}
	public void deleteTarievenWerkgever(Integer id) throws VerzuimApplicationException, ValidationException {
		List<TariefInfo> tarieven = this.getTarievenByWerkgever(id);
		for (TariefInfo tarief:tarieven){
			this.deleteTarief(tarief);
		}
	}
	public void deleteTarievenHolding(Integer id) throws VerzuimApplicationException, ValidationException {
		List<TariefInfo> tarieven = this.getTarievenByHolding(id);
		for (TariefInfo tarief:tarieven){
			this.deleteTarief(tarief);
		}
	}

}
