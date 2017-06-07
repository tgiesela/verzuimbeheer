package com.gieselaar.verzuimbeheer.services;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.Query;

import com.gieselaar.verzuimbeheer.entities.Pakket;
import com.gieselaar.verzuimbeheer.exceptions.VerzuimApplicationException;
import com.gieselaar.verzuimbeheer.exceptions.ValidationException;
import com.gieselaar.verzuimbeheer.utils.PakketConversion;

/**
 * Session Bean implementation class PakketBean
 */
@Stateless
@LocalBean
public class PakketBean extends BeanBase {

    /**
	 * 
	 */
	private static final long serialVersionUID = 4331652778563267994L;

	/**
     * Default constructor. 
     */
	@EJB PakketConversion converter;

	private Pakket pakket = null;

	public List<PakketInfo> allPakketen() throws VerzuimApplicationException {
		Query q = createQuery("select p from Pakket p");
		@SuppressWarnings("unchecked")
		List<Pakket> result = (List<Pakket>)getResultList(q);
		List<PakketInfo> inforesult = new ArrayList<>();
		for (Pakket p: result)
		{
			PakketInfo pi = converter.fromEntity(p);
			inforesult.add(pi);
		}
		return inforesult;
	}
	public PakketInfo getById(long id) throws VerzuimApplicationException {
		Query q = createQuery("select p from Pakket p where p.id = :id");
		q.setParameter("id", id);
		@SuppressWarnings("unchecked")
		List<Pakket> result = (List<Pakket>)getResultList(q);

		if (result.size() != 1)
			return null;
		else
		{
			this.pakket = result.get(0);
			return converter.fromEntity(this.pakket);
		}
	}
	public List<PakketInfo> getByWerkgever(long werkgeverid) throws VerzuimApplicationException{
		Query q = createQuery("select p from Pakket p, WerkgeverHasPakket wp where wp.id.pakket_ID = p.id and wp.id.werkgever_ID = :wgr_id");
		q.setParameter("wgr_id", werkgeverid);
		@SuppressWarnings("unchecked")
		List<Pakket> result = (List<Pakket>)getResultList(q);
		List<PakketInfo> inforesult = new ArrayList<>();
		for (Pakket p: result)
		{
			PakketInfo pi = converter.fromEntity(p);
			inforesult.add(pi);
		}
		return inforesult;
	}
	public PakketInfo addPakket(PakketInfo info) throws ValidationException, VerzuimApplicationException {
		try {
			info.validate();
		} catch (ValidationException e) {
			throw e;
		}
		
		/* We voegen geen nieuwe activiteiten toe, alleen koppelingen
		 * met activiteiten. Persistenceaction gaat dus over de koppeling
		 */
		this.pakket = converter.toEntity(info, this.getCurrentuser());
		this.insertEntity(this.pakket);
		return converter.fromEntity(pakket);
	}

	public boolean deletepakket(PakketInfo info) throws com.gieselaar.verzuimbeheer.exceptions.VerzuimApplicationException  {
		this.pakket = converter.toEntity(info, this.getCurrentuser());
		this.deleteEntity(this.pakket);
		return true;
	}
	public boolean deletePakketten(int werkgeverid) throws VerzuimApplicationException  {
    	Query wa = createQuery("Delete from WerkgeverHasPakket wp where wp.id.werkgever_ID = :wgr_id");
    	
		wa.setParameter("wgr_id", werkgeverid);
    	executeUpdate(wa);
		
		return true;
	}
	public boolean updatePakket(PakketInfo info) throws ValidationException, VerzuimApplicationException {
		try {
			info.validate();
		} catch (ValidationException e) {
			throw e;
		}
    	this.pakket = converter.toEntity(info, this.getCurrentuser());
    	this.updateEntity(this.pakket);
    	return true;
	}
}
