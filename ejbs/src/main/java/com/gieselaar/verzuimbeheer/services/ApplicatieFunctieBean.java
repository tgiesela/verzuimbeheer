package com.gieselaar.verzuimbeheer.services;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.Query;

import com.gieselaar.verzuimbeheer.entities.Applicatiefunctie;
import com.gieselaar.verzuimbeheer.exceptions.VerzuimApplicationException;
import com.gieselaar.verzuimbeheer.exceptions.ValidationException;
import com.gieselaar.verzuimbeheer.utils.AutorisatieConversion;

/**
 * Session Bean implementation class ApplicatieFunctieBean
 */
@Stateless
@LocalBean
public class ApplicatieFunctieBean extends BeanBase {


    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
     * Default constructor. 
     */
	@EJB AutorisatieConversion converter;
	private Applicatiefunctie applicatiefunctie = null;

	public List<ApplicatieFunctieInfo> allApplicatiefuncties() throws VerzuimApplicationException {
		Query q = createQuery("select p from Applicatiefunctie p");
		@SuppressWarnings("unchecked")
		List<Applicatiefunctie> result = (List<Applicatiefunctie>)getResultList(q);
		List<ApplicatieFunctieInfo> inforesult = new ArrayList<>();
		for (Applicatiefunctie p: result)
		{
			ApplicatieFunctieInfo pi = converter.fromEntity(p);
			inforesult.add(pi);
		}
		return inforesult;
	}
	public ApplicatieFunctieInfo getById(long id) throws VerzuimApplicationException {
		Query q = createQuery("select p from Applicatiefunctie p where p.id = :id");
		q.setParameter("id", id);
		@SuppressWarnings("unchecked")
		List<Applicatiefunctie> result = (List<Applicatiefunctie>)getResultList(q);

		if (result.size() != 1)
			return null;
		else
		{
			this.applicatiefunctie = result.get(0);
			return converter.fromEntity(this.applicatiefunctie);
		}
	}

	public ApplicatieFunctieInfo addApplicatiefunctie(ApplicatieFunctieInfo info) throws ValidationException, VerzuimApplicationException {
		try {
			info.validate();
		} catch (ValidationException e) {
			throw e;
		}
		
		this.applicatiefunctie = converter.toEntity(info, this.getCurrentuser());
		this.insertEntity(this.applicatiefunctie);
		return converter.fromEntity(applicatiefunctie);
	}

	public boolean deleteApplicatiefunctie(ApplicatieFunctieInfo info) throws VerzuimApplicationException  {
		this.applicatiefunctie = converter.toEntity(info, this.getCurrentuser());
		this.deleteEntity(this.applicatiefunctie);
		return true;
	}
	public ApplicatieFunctieInfo updateApplicatiefunctie(ApplicatieFunctieInfo info) throws ValidationException, VerzuimApplicationException {
		try {
			info.validate();
		} catch (ValidationException e) {
			throw e;
		}
    	this.applicatiefunctie = converter.toEntity(info, this.getCurrentuser());
    	this.updateEntity(this.applicatiefunctie);
    	return this.getById(applicatiefunctie.getId());
	}

}
