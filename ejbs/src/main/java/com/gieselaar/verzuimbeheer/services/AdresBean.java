package com.gieselaar.verzuimbeheer.services;

import java.util.List;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.Query;

import com.gieselaar.verzuimbeheer.entities.Adres;
import com.gieselaar.verzuimbeheer.exceptions.VerzuimApplicationException;
import com.gieselaar.verzuimbeheer.exceptions.ValidationException;
import com.gieselaar.verzuimbeheer.services.InfoBase.persistenceaction;
import com.gieselaar.verzuimbeheer.services.InfoBase.persistencestate;
import com.gieselaar.verzuimbeheer.utils.AdresConversion;

/**
 * Session Bean implementation class AdresBean
 */

@Stateless
@LocalBean
public class AdresBean extends BeanBase {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Adres adres = null;
	/**
     * Default constructor. 
     */
	@EJB AdresConversion converter;

	private Adres getEntityById(long id) throws VerzuimApplicationException{
		Query q = createQuery("select a from Adres a where a.id = :id");
		q.setParameter("id", id);
		@SuppressWarnings("unchecked")
		List<Adres> result = (List<Adres>)getResultList(q);

		if (result.size() != 1)
			return null;
		else
			return result.get(0);
	}
	public AdresInfo getById(long id) throws com.gieselaar.verzuimbeheer.exceptions.VerzuimApplicationException {
		this.adres = getEntityById(id);
		return converter.fromEntity(this.adres);
	}

	public AdresInfo update(AdresInfo info) throws ValidationException, VerzuimApplicationException {
		if (info == null)
			/* Hiermee kan de routine altijd worden aangeroepen*/
			return null;

		info.validate();
		if (info.getState() == persistencestate.ABSENT){
			this.adres = converter.toEntity(info, this.getCurrentuser());
			if (info.getAction() == persistenceaction.INSERT)
				this.insertEntity(this.adres);
			else
				this.adres = null;
		}
		else {
			this.adres = getEntityById(info.getId());
			this.adres = converter.toEntity(info, this.getCurrentuser());
			if (info.getAction() == persistenceaction.DELETE)
			{
				this.deleteEntity(this.adres);
				this.adres = null;
			}
			else
				this.updateEntity(this.adres);
		}
    	return converter.fromEntity(this.adres);
	}
	public boolean delete(AdresInfo info) throws VerzuimApplicationException  {
		if (info != null){
			this.adres = converter.toEntity(info, this.getCurrentuser());
			this.deleteEntity(this.adres);
			return true; 
		}
		else
			return false;
	}
}
