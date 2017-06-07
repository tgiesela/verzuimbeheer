package com.gieselaar.verzuimbeheer.services;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.Query;

import com.gieselaar.verzuimbeheer.entities.Afdeling;
import com.gieselaar.verzuimbeheer.exceptions.VerzuimApplicationException;
import com.gieselaar.verzuimbeheer.exceptions.ValidationException;
import com.gieselaar.verzuimbeheer.services.InfoBase.persistenceaction;
import com.gieselaar.verzuimbeheer.services.InfoBase.persistencestate;
import com.gieselaar.verzuimbeheer.utils.ContactpersoonConversion;
import com.gieselaar.verzuimbeheer.utils.WerkgeverConversion;

/**
 * Session Bean implementation class AfdelingBean
 */
@Stateless
@LocalBean
public class AfdelingBean extends BeanBase {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
     * Default constructor. 
     */
	@EJB WerkgeverConversion converter;
	@EJB WerkgeverBean werkgeverbean;
	@EJB ContactpersoonConversion contactpersoonconverter;
	@EJB ContactpersoonBean contactpersoon;
	private Afdeling afdeling = null;
	private Afdeling getEntityById(long id) throws VerzuimApplicationException{
		Query q = createQuery("select a from Afdeling a where a.id = :id");
		q.setParameter("id", id);
		@SuppressWarnings("unchecked")
		List<Afdeling> result = (List<Afdeling>)getResultList(q);

		if (result.size() != 1)
			return null;
		else
			return result.get(0);
	}
    private AfdelingInfo completeAfdeling(Afdeling a){
		return converter.fromEntity(a);
    }
	public List<AfdelingInfo> allAfdelingen() throws VerzuimApplicationException {
		Query q = createQuery("select p from Afdeling p");
		@SuppressWarnings("unchecked")
		List<Afdeling> result = (List<Afdeling>)getResultList(q);
		List<AfdelingInfo> inforesult = new ArrayList<>();
		for (Afdeling p: result)
		{
			AfdelingInfo pi = completeAfdeling(p);
			inforesult.add(pi);
		}
		return inforesult;
	}
	public AfdelingInfo getById(long id) throws VerzuimApplicationException {
		Query q = createQuery("select p from Afdeling p where p.id = :id");
		q.setParameter("id", id);
		@SuppressWarnings("unchecked")
		List<Afdeling> result = (List<Afdeling>)getResultList(q);

		if (result.size() != 1)
			return null;
		else
		{
			this.afdeling = result.get(0);
			return completeAfdeling(this.afdeling);
		}
	}
	public List<AfdelingInfo> getByWerkgeverId(long id) throws VerzuimApplicationException {
		Query q = createQuery("select p from Afdeling p where p.werkgever.id = :id");
		q.setParameter("id", id);
		@SuppressWarnings("unchecked")
		List<Afdeling> result = (List<Afdeling>)getResultList(q);
		List<AfdelingInfo> inforesult = new ArrayList<>();

		for (Afdeling p: result)
		{
			AfdelingInfo pi = completeAfdeling(p);
			inforesult.add(pi);
		}
		return inforesult;
	}
	public AfdelingInfo addAfdeling(AfdelingInfo info) throws ValidationException, VerzuimApplicationException {
		try {
			info.validate();
			WerkgeverInfo wg = werkgeverbean.getById(info.getWerkgeverId());
			if (wg == null){
				throw new ValidationException("Werkgever niet gevonden (" + info.getWerkgeverId().toString() +")");
			}
		} catch (ValidationException e) {
			throw e;
		}
		
		this.afdeling = converter.toEntity(info, this.getCurrentuser());
		ContactpersoonInfo cp = info.getContactpersoon();
		if (cp != null && !cp.isEmpty()){
			cp.validate();
			contactpersoon.setCurrentuser(getCurrentuser());
			this.afdeling.setContactpersoon(contactpersoonconverter.toEntity(contactpersoon.update(cp), this.getCurrentuser()));
		}else{
			info.setContactpersoon(null);
		}
		this.insertEntity(this.afdeling);
		
		return converter.fromEntity(afdeling);
	}

	public AfdelingInfo updateAfdeling(AfdelingInfo info) throws ValidationException, VerzuimApplicationException {
		if (info == null)
			return null;
		
		info.validate();
		WerkgeverInfo wg = werkgeverbean.getById(info.getWerkgeverId());
		if (wg == null){
			throw new ValidationException("Werkgever niet gevonden (" + info.getWerkgeverId().toString() +")");
		}
		
		if (info.getState() == persistencestate.ABSENT){
			this.afdeling = converter.toEntity(info, this.getCurrentuser());
			if (info.getAction() == persistenceaction.INSERT)
			{
				this.insertEntity(this.afdeling);
			}
			else
				this.afdeling = null;
		}
		else {
			this.afdeling = getEntityById(info.getId());
			if (info.getAction() == persistenceaction.DELETE)
			{
				deleteAfdeling(info);
				afdeling = null;
			}
			else
			{
				this.afdeling = converter.toEntity(info, this.getCurrentuser());
				this.updateEntity(this.afdeling);
			}
		}
		return converter.fromEntity(this.afdeling);		
	}
	public boolean deleteAfdeling(AfdelingInfo info) throws ValidationException, VerzuimApplicationException  {
		Query q = createQuery("select count(aw) from AfdelingHasWerknemer aw where aw.id.afdeling_ID = :id");
		q.setParameter("id", info.getId());
		Long werknemercount = (Long) q.getSingleResult();
		if (werknemercount > 0)
			throw new ValidationException("Afdeling heeft nog werknemers");
		this.afdeling = converter.toEntity(info, this.getCurrentuser());
		contactpersoon.setCurrentuser(getCurrentuser());
		this.contactpersoon.delete(info.getContactpersoon());
		this.deleteEntity(this.afdeling);
		return true;
	}
	public void deleteAfdelingen(int werkgeverid) throws VerzuimApplicationException, ValidationException {
		List<AfdelingInfo> afdn = this.getByWerkgeverId(werkgeverid);
		for (AfdelingInfo afd:afdn){
			this.deleteAfdeling(afd);
		}
		
	}
}
