package com.gieselaar.verzuimbeheer.services;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.Query;

import com.gieselaar.verzuimbeheer.entities.Cascode;
import com.gieselaar.verzuimbeheer.entities.Cascodegroep;
import com.gieselaar.verzuimbeheer.exceptions.VerzuimApplicationException;
import com.gieselaar.verzuimbeheer.exceptions.ValidationException;
import com.gieselaar.verzuimbeheer.utils.CascodeConversion;

/**
 * Session Bean implementation class CasecodeBean
 */
@Stateless
@LocalBean
public class CascodeBean extends BeanBase {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
     * Default constructor. 
     */
	@EJB CascodeConversion converter;

    public List<CascodeInfo> allCascodes() throws VerzuimApplicationException{
		Query q = createQuery("select c from Cascode c order by c.cascode");
		@SuppressWarnings("unchecked")
		List<Cascode> result = (List<Cascode>)getResultList(q);
		List<CascodeInfo> inforesult = new ArrayList<>();
		for (Cascode c: result)
		{
			CascodeInfo ci = converter.fromEntity(c);
			inforesult.add(ci);
		}
		return inforesult;
    }
    public List<CascodeGroepInfo> allCascodeGroepen() throws VerzuimApplicationException{
		Query q = createQuery("select c from Cascodegroep c order by c.naam");
		@SuppressWarnings("unchecked")
		List<Cascodegroep> result = (List<Cascodegroep>)getResultList(q);
		List<CascodeGroepInfo> inforesult = new ArrayList<>();
		for (Cascodegroep c: result)
		{
			CascodeGroepInfo ci = converter.fromEntity(c);
			inforesult.add(ci);
		}
		return inforesult;
    }
	public List<CascodeInfo> allCascodesForGroep(int cascodegroepid) throws VerzuimApplicationException {
		Query q = createQuery("select c from Cascode c where c.cascodegroep = :cascodegroep order by c.cascode");
		q.setParameter("cascodegroep", cascodegroepid);
		@SuppressWarnings("unchecked")
		List<Cascode> result = (List<Cascode>)getResultList(q);
		List<CascodeInfo> inforesult = new ArrayList<>();
		for (Cascode c: result)
		{
			CascodeInfo ci = converter.fromEntity(c);
			inforesult.add(ci);
		}
		return inforesult;
	}
    
	public CascodeInfo addCascode(CascodeInfo info) throws ValidationException, VerzuimApplicationException {
		try {
			info.validate();
		} catch (ValidationException e) {
			throw e;
		}
		Cascode cc = converter.toEntity(info, this.getCurrentuser());
		this.insertEntity(cc);
		return converter.fromEntity(cc);
	}

	public boolean deleteCascode(CascodeInfo info) throws VerzuimApplicationException  {
		this.deleteEntity(converter.toEntity(info, this.getCurrentuser()));
		return true;
	}
	public CascodeInfo updateCascode(CascodeInfo info) throws ValidationException, VerzuimApplicationException {
		try {
			info.validate();
		} catch (ValidationException e) {
			throw e;
		}
		Cascode entity = converter.toEntity(info, this.getCurrentuser());
    	this.updateEntity(entity);
    	return getCascode(entity.getId());
	}
	public CascodeGroepInfo updateCascodeGroep(CascodeGroepInfo info) throws ValidationException, VerzuimApplicationException {
		try {
			info.validate();
		} catch (ValidationException e) {
			throw e;
		}
		Cascodegroep entity = converter.toEntity(info, this.getCurrentuser());
    	this.updateEntity(entity);
    	return getCascodeGroep(entity.getId());
	}
	public CascodeGroepInfo addCascodeGroep(CascodeGroepInfo info) throws ValidationException, VerzuimApplicationException {
		try {
			info.validate();
		} catch (ValidationException e) {
			throw e;
		}
		Cascodegroep cg = converter.toEntity(info, this.getCurrentuser());
		this.insertEntity(cg);
		return converter.fromEntity(cg);
	}

	public boolean deleteCascodeGroep(CascodeGroepInfo info) throws VerzuimApplicationException, ValidationException  {
		List<CascodeInfo> cclist = this.allCascodesForGroep(info.getId());
		if (!cclist.isEmpty()){
			throw new ValidationException("Er zijn nog cascodes gekoppeld aan deze groep");
		}
		this.deleteEntity(converter.toEntity(info, this.getCurrentuser()));
		return true;
	}
	public CascodeGroepInfo getCascodeGroep(int groep) throws VerzuimApplicationException{
		Query q = createQuery("select c from Cascodegroep c where c.id = :id");
		q.setParameter("id", groep);
		Cascodegroep result = (Cascodegroep)getSingleResult(q);
		return converter.fromEntity(result);
	}
	public CascodeInfo getCascode(int cascode) throws VerzuimApplicationException{
		Query q = createQuery("select c from Cascode c where c.id = :id");
		q.setParameter("id", cascode);
		Cascode result = (Cascode)getSingleResult(q);
		return converter.fromEntity(result);
	}
}
