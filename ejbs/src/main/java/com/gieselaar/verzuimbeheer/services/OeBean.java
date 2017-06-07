package com.gieselaar.verzuimbeheer.services;

import java.util.ArrayList;
import java.util.List;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.Query;

import com.gieselaar.verzuimbeheer.entities.Oe;
import com.gieselaar.verzuimbeheer.entities.Oeniveau;
import com.gieselaar.verzuimbeheer.exceptions.VerzuimApplicationException;
import com.gieselaar.verzuimbeheer.exceptions.ValidationException;
import com.gieselaar.verzuimbeheer.utils.WerkgeverConversion;

/**
 * Session Bean implementation class WerkgeverBean
 */
@Stateless
@LocalBean
//@WebService
public class OeBean extends BeanBase{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private WerkgeverConversion converter;

	public OeBean() {
		converter = new WerkgeverConversion();
    }
    private OeInfo completeOe(Oe w){
		return converter.fromEntity(w);
    }
	public OeInfo getOeById(Integer id) throws VerzuimApplicationException {
		if (id == null)
			return null;
		Query q = createQuery("Select o from Oe o left join fetch o.oeniveau where o.id = :id");
		q.setParameter("id", id);
		@SuppressWarnings("unchecked")
		List<Oe> result = (List<Oe>)getResultList(q);
		if (result.size() != 1)
			return null;
		Oe w = result.get(0);
		
		return completeOe(w);
	}
	public List<OeInfo> getOesByNiveauId(int oeniveauid) throws VerzuimApplicationException {
		Query q = createQuery("Select o from Oe o left join fetch o.oeniveau where o.oeniveau.id = :oeniveauid");
		q.setParameter("oeniveauid", oeniveauid);
		@SuppressWarnings("unchecked")
		List<Oe> result = (List<Oe>)getResultList(q);
		List<OeInfo> inforesult = new ArrayList<>();
		for (Oe w: result)
			inforesult.add(completeOe(w));
		
		return inforesult;
	}
	public List<OeInfo> getOesUnderId(int id) throws VerzuimApplicationException {
		Query q = createQuery("Select o from Oe o left join fetch o.oeniveau where o.parentoe_id = :id");
		q.setParameter("id", id);
		@SuppressWarnings("unchecked")
		List<Oe> result = (List<Oe>)getResultList(q);
		List<OeInfo> inforesult = new ArrayList<>();
		for (Oe w: result)
			inforesult.add(completeOe(w));
		
		return inforesult;
	}
	public List<OeInfo> getOes() throws VerzuimApplicationException {
		Query q = createQuery("Select o from Oe o left join fetch o.oeniveau");
		@SuppressWarnings("unchecked")
		List<Oe> result = (List<Oe>)getResultList(q);
		List<OeInfo> inforesult = new ArrayList<>();
		for (Oe w: result)
			inforesult.add(completeOe(w));
		
		return inforesult;
	}
	public OeInfo addOe(OeInfo info) throws ValidationException, VerzuimApplicationException{
		try {
			info.validate();
		} catch (ValidationException e) {
			throw e;
		}

		Oe oe = converter.toEntity(info, this.getCurrentuser());
		this.insertEntity(oe);
		return converter.fromEntity(oe);
	}
	public void updateOe(OeInfo info) throws ValidationException, VerzuimApplicationException{
		try {
			info.validate();
		} catch (ValidationException e) {
			throw e;
		}
    	Oe oe = converter.toEntity(info, this.getCurrentuser());
    	this.updateEntity(oe);
    	info.setId(oe.getId());
	}
	public void deleteOe(OeInfo info) throws ValidationException, VerzuimApplicationException{
		List<OeInfo> oes = this.getOesUnderId(info.getId());
		if (!oes.isEmpty())
			throw new ValidationException("Er bestaan nog onderliggende bedrijfseenheden. Verwijder deze eerst.");
		
		Oe oe = converter.toEntity(info, this.getCurrentuser());
		this.deleteEntity(oe);
	}
    private OeNiveauInfo completeOeNiveau(Oeniveau w){
		return converter.fromEntity(w);
    }
	public OeNiveauInfo getOeNiveauById(Integer id) throws VerzuimApplicationException {
		if (id == null)
			return null;
		Query q = createQuery("Select o from Oeniveau o where o.id = :id");
		q.setParameter("id", id);
		@SuppressWarnings("unchecked")
		List<Oeniveau> result = (List<Oeniveau>)getResultList(q);
		if (result.size() != 1)
			return null;
		Oeniveau w = result.get(0);
		
		return completeOeNiveau(w);
	}
	public List<OeNiveauInfo> getOeNiveaus() throws VerzuimApplicationException {
		Query q = createQuery("Select o from Oeniveau o");
		@SuppressWarnings("unchecked")
		List<Oeniveau> result = (List<Oeniveau>)getResultList(q);
		List<OeNiveauInfo> inforesult = new ArrayList<>();
		for (Oeniveau w: result)
			inforesult.add(completeOeNiveau(w));
		
		return inforesult;
	}
	public OeNiveauInfo addOeNiveau(OeNiveauInfo info) throws ValidationException, VerzuimApplicationException{
		try {
			info.validate();
		} catch (ValidationException e) {
			throw e;
		}

		Oeniveau oeniveau = converter.toEntity(info, this.getCurrentuser());
		this.insertEntity(oeniveau);
		return converter.fromEntity(oeniveau);
	}
	public void updateOeNiveau(OeNiveauInfo info) throws ValidationException, VerzuimApplicationException{
		try {
			info.validate();
		} catch (ValidationException e) {
			throw e;
		}
		Oeniveau oeniveau = converter.toEntity(info, this.getCurrentuser());
    	this.updateEntity(oeniveau);
    	info.setId(oeniveau.getId());
	}
	public void deleteOeNiveau(OeNiveauInfo info) throws ValidationException, VerzuimApplicationException{
		List<OeNiveauInfo> oeniveaus = this.getOeNiveaus();
		List<OeInfo> oes = this.getOesByNiveauId(info.getId());
		/*
		 * Test of er in de structuur nog onder liggende niveaus voorkomen 
		 */
		for (OeNiveauInfo oeniveau: oeniveaus){
			if (oeniveau.getParentoeniveauId() != null && (oeniveau.getParentoeniveauId().intValue() == info.getId().intValue()))
				throw new ValidationException("Er bestaan nog onderliggende niveau's. Verwijder deze eerst.");
		}
		
		/*
		 * Test of er nog Oes voorkomen die aan dit niveau gekoppeld zijn
		 */
		if (!oes.isEmpty()){
			throw new ValidationException("Er bestaan nog gekoppelde bedrijfseenheden. Verwijder deze eerst.");
		}
		
		Oeniveau oeniveau = converter.toEntity(info, this.getCurrentuser());
		this.deleteEntity(oeniveau);
	}
}
