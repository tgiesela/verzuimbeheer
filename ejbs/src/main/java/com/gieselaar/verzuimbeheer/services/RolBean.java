package com.gieselaar.verzuimbeheer.services;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.Query;

import com.gieselaar.verzuimbeheer.entities.Applicatiefunctie;
import com.gieselaar.verzuimbeheer.entities.Rol;
import com.gieselaar.verzuimbeheer.exceptions.VerzuimApplicationException;
import com.gieselaar.verzuimbeheer.exceptions.ValidationException;
import com.gieselaar.verzuimbeheer.utils.AutorisatieConversion;

/**
 * Session Bean implementation class RolBean
 */
@Stateless
@LocalBean
public class RolBean extends BeanBase {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
     * Default constructor. 
     */
	@EJB AutorisatieConversion converter;
	private Rol rol = null;
	public List<RolInfo> getRollen() throws VerzuimApplicationException {
		Query q = createQuery("select p from Rol p");
		@SuppressWarnings("unchecked")
		List<Rol> result = (List<Rol>)getResultList(q);
		List<RolInfo> inforesult = new ArrayList<>();
		for (Rol p: result)
		{
			RolInfo pi = converter.fromEntity(p);
			inforesult.add(pi);
		}
		return inforesult;
	}
	public RolInfo getById(long id) throws VerzuimApplicationException {
		Query q = createQuery("select p from Rol p where p.id = :id");
		q.setParameter("id", id);
		@SuppressWarnings("unchecked")
		List<Rol> result = (List<Rol>)getResultList(q);

		if (result.size() != 1)
			return null;
		else
		{
			this.rol = result.get(0);
			return converter.fromEntity(this.rol);
		}
	}
	public List<RolInfo> getByUser(long userid) throws VerzuimApplicationException{
		Query q = createQuery("select r from Rol r, GebruikerRol gr where gr.id.rolid = r.id and gr.id.gebruikerid = :user_id");
		q.setParameter("user_id", userid);
		@SuppressWarnings("unchecked")
		List<Rol> result = (List<Rol>)getResultList(q);
		List<RolInfo> inforesult = new ArrayList<>();
		for (Rol p: result)
		{
			RolInfo pi = converter.fromEntity(p);
			inforesult.add(pi);
		}
		return inforesult;
	}
	public RolInfo addRol(RolInfo info) throws ValidationException, VerzuimApplicationException {
		try {
			info.validate();
		} catch (ValidationException e) {
			throw e;
		}
		
		/* We voegen geen nieuwe activiteiten toe, alleen koppelingen
		 * met activiteiten. Persistenceaction gaat dus over de koppeling
		 */
		this.rol = converter.toEntity(info, this.getCurrentuser());
		this.insertEntity(this.rol);
		return converter.fromEntity(rol);
	}

	public boolean deleterol(RolInfo info) throws VerzuimApplicationException  {
		this.rol = converter.toEntity(info, this.getCurrentuser());
		this.deleteEntity(this.rol);
		return true;
	}
	public RolInfo updateRol(RolInfo info) throws ValidationException, VerzuimApplicationException {
		try {
			info.validate();
		} catch (ValidationException e) {
			throw e;
		}
    	this.rol = converter.toEntity(info, this.getCurrentuser());
    	this.updateEntity(this.rol);
    	return getById(rol.getId());
	}
}
