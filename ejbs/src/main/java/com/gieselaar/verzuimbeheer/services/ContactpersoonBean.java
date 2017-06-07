package com.gieselaar.verzuimbeheer.services;

import java.util.List;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.Query;

import com.gieselaar.verzuimbeheer.entities.Contactpersoon;
import com.gieselaar.verzuimbeheer.exceptions.VerzuimApplicationException;
import com.gieselaar.verzuimbeheer.exceptions.ValidationException;
import com.gieselaar.verzuimbeheer.services.InfoBase.persistenceaction;
import com.gieselaar.verzuimbeheer.services.InfoBase.persistencestate;
import com.gieselaar.verzuimbeheer.utils.ContactpersoonConversion;

/**
 * Session Bean implementation class AdresBean
 */

@Stateless
@LocalBean
public class ContactpersoonBean extends BeanBase {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
     * Default constructor. 
     */
	@EJB ContactpersoonConversion converter;
	private Contactpersoon contactpersoon = null;

	private Contactpersoon getEntityById(long id) throws VerzuimApplicationException{
		Query q = createQuery("select a from Contactpersoon a where a.id = :id");
		q.setParameter("id", id);
		@SuppressWarnings("unchecked")
		List<Contactpersoon> result = (List<Contactpersoon>)getResultList(q);

		if (result.size() != 1)
			return null;
		else
			return result.get(0);
	}
	public ContactpersoonInfo getById(long id) throws VerzuimApplicationException {
		this.contactpersoon = getEntityById(id);
		return converter.fromEntity(this.contactpersoon);
	}

	public ContactpersoonInfo update(ContactpersoonInfo info) throws ValidationException, VerzuimApplicationException {
		if (info == null)
			/* Hiermee kan de routine altijd worden aangeroepen*/
			return null;

		info.validate();
		if (info.getState() == persistencestate.ABSENT){
			this.contactpersoon = converter.toEntity(info, this.getCurrentuser());
			if (info.getAction() == persistenceaction.INSERT)
				this.insertEntity(this.contactpersoon);
			else
				this.contactpersoon = null;
		}
		else {
			this.contactpersoon = getEntityById(info.getId());
			this.contactpersoon = converter.toEntity(info, this.getCurrentuser());
			if (info.getAction() == persistenceaction.DELETE)
			{
				this.deleteEntity(this.contactpersoon);
				this.contactpersoon = null;
			}
			else
				this.updateEntity(this.contactpersoon);
		}
    	return converter.fromEntity(this.contactpersoon);
	}
	public boolean delete(ContactpersoonInfo info) throws com.gieselaar.verzuimbeheer.exceptions.VerzuimApplicationException  {
		if (info != null){
			this.contactpersoon = converter.toEntity(info, this.getCurrentuser());
			this.deleteEntity(this.contactpersoon);
			return true; 
		}
		else
			return false;
	}
	
}
