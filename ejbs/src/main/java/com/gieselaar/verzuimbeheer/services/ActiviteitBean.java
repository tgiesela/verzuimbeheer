package com.gieselaar.verzuimbeheer.services;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.Query;

import com.gieselaar.verzuimbeheer.entities.Activiteit;
import com.gieselaar.verzuimbeheer.exceptions.VerzuimApplicationException;
import com.gieselaar.verzuimbeheer.exceptions.ValidationException;
import com.gieselaar.verzuimbeheer.utils.PakketConversion;

/**
 * Session Bean implementation class ActiviteitBean
 */
@Stateless
@LocalBean
public class ActiviteitBean extends BeanBase {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
     * Default constructor. 
     */
	@EJB PakketConversion converter;
	private Activiteit activiteit = null;

	public List<ActiviteitInfo> allActiviteiten() throws VerzuimApplicationException {
		Query q = createQuery("select p from Activiteit p");
		@SuppressWarnings("unchecked")
		List<Activiteit> result = (List<Activiteit>)getResultList(q);
		List<ActiviteitInfo> inforesult = new ArrayList<>();
		for (Activiteit p: result)
		{
			ActiviteitInfo pi = converter.fromEntity(p);
			inforesult.add(pi);
		}
		return inforesult;
	}
	public List<ActiviteitInfo> allActiviteiten(Integer werkgever) throws VerzuimApplicationException {
		Query q = createQuery("select distinct a from Activiteit a, PakketHasActiviteit pa, WerkgeverHasPakket wp "+
									"where pa.id.activiteit_ID = a.id and wp.id.pakket_ID = pa.id.pakket_ID and wp.id.werkgever_ID = :wgr");
		q.setParameter("wgr", werkgever);
		@SuppressWarnings("unchecked")
		List<Activiteit> result = (List<Activiteit>)getResultList(q);
		List<ActiviteitInfo> inforesult = new ArrayList<>();
		for (Activiteit p: result)
		{
			ActiviteitInfo pi = converter.fromEntity(p);
			inforesult.add(pi);
		}
		return inforesult;
	}
	public ActiviteitInfo getById(long id) throws VerzuimApplicationException {
		Query q = createQuery("select p from Activiteit p where p.id = :id");
		q.setParameter("id", id);
		@SuppressWarnings("unchecked")
		List<Activiteit> result = (List<Activiteit>)getResultList(q);

		if (result.size() != 1)
			return null;
		else
		{
			this.activiteit = result.get(0);
			return converter.fromEntity(this.activiteit);
		}
	}

	public ActiviteitInfo addActiviteit(ActiviteitInfo info) throws ValidationException, VerzuimApplicationException {
		try {
			info.validate();
		} catch (ValidationException e) {
			throw e;
		}
		// We kunnen de activteiten niet vooraf toevoegen. Om een of andere vage
		// reden gaat dit fout. We moeten het pakket met nulls in de foreignkeys
		// toevoegen en dan de inhoud van de foreign keys opvoeren en
		// uiteindelijk updaten
		
		this.activiteit = converter.toEntity(info, this.getCurrentuser());
		this.insertEntity(this.activiteit);
		info.setId(activiteit.getId());
		return info;
	}

	public boolean deleteActiviteit(ActiviteitInfo info) throws VerzuimApplicationException  {
		final String pactid = "act_id";
    	Query qpka 	= createQuery("Delete from PakketHasActiviteit pha where pha.id.activiteit_ID = :" + pactid);
    	Query qt 	= createQuery("Delete from Todo t where t.activiteit_ID = :"+ pactid);
    	Query qva 	= createQuery("Delete from Verzuimactiviteit va where va.activiteit_ID = :" + pactid);

    	qpka.setParameter	(pactid, info.getId());
		qt  .setParameter  	(pactid, info.getId());
		qva .setParameter	(pactid, info.getId());
    	executeUpdate(qpka);
    	executeUpdate(qt);
    	executeUpdate(qva);
		
		this.activiteit = converter.toEntity(info, this.getCurrentuser());
		this.deleteEntity(this.activiteit);
		return true;
	}
	public boolean updateActiviteit(ActiviteitInfo info) throws ValidationException, VerzuimApplicationException {
		try {
			info.validate();
		} catch (ValidationException e) {
			throw e;
		}
    	this.activiteit = converter.toEntity(info, this.getCurrentuser());
    	this.updateEntity(this.activiteit);
    	return true;
	}
	public List<ActiviteitInfo> allActiviteitenPakket(Integer pakket) throws VerzuimApplicationException {
		Query q = createQuery("select a from Activiteit a, PakketHasActiviteit pa "+
				"where pa.id.pakket_ID = :pakket and pa.id.activiteit_ID = a.id");
		q.setParameter("pakket", pakket);
		@SuppressWarnings("unchecked")
		List<Activiteit> result = (List<Activiteit>)getResultList(q);
		List<ActiviteitInfo> inforesult = new ArrayList<>();
		for (Activiteit p: result)
		{
			ActiviteitInfo pi = converter.fromEntity(p);
			inforesult.add(pi);
		}
		return inforesult;
	}
}
