package com.gieselaar.verzuimbeheer.services;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.Query;

import com.gieselaar.verzuimbeheer.entities.Arbodienst;
import com.gieselaar.verzuimbeheer.entities.Bedrijfsarts;
import com.gieselaar.verzuimbeheer.entities.Bedrijfsgegevens;
import com.gieselaar.verzuimbeheer.entities.Uitvoeringsinstituut;
import com.gieselaar.verzuimbeheer.exceptions.VerzuimApplicationException;
import com.gieselaar.verzuimbeheer.exceptions.ValidationException;
import com.gieselaar.verzuimbeheer.utils.AdresConversion;
import com.gieselaar.verzuimbeheer.utils.ContactpersoonConversion;
import com.gieselaar.verzuimbeheer.utils.InstantieConversion;

/**
 * Session Bean implementation class InstantieBean
 */
@Stateless
@LocalBean
public class InstantieBean extends BeanBase {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@EJB InstantieConversion converter;
	@EJB AdresConversion adresconverter;
	@EJB ContactpersoonConversion contactpersoonconverter;

	private Arbodienst arbodienst;
	private Uitvoeringsinstituut uitvoeringsinstituut;
	private Bedrijfsarts bedrijfsarts;
	private Bedrijfsgegevens bedrijfsgegevens;
    /**
     * Default constructor. 
     */
    private ArbodienstInfo completeArbodienst(Arbodienst w){
		return converter.fromEntity(w);
    }
    private UitvoeringsinstituutInfo completeUitvoeringsinstituut(Uitvoeringsinstituut w){
		return converter.fromEntity(w);
    }
    private BedrijfsgegevensInfo completeBedrijfsgegevens(Bedrijfsgegevens w){
		return converter.fromEntity(w);
    }    
    public List<ArbodienstInfo> getArbodiensten() throws VerzuimApplicationException {
		Query q = createQuery("select w from Arbodienst w left join fetch w.vestigingsadres left join fetch w.postadres left join fetch w.contactpersoon");
		@SuppressWarnings("unchecked")
		List<Arbodienst> result = (List<Arbodienst>)getResultList(q);
		List<ArbodienstInfo> inforesult = new ArrayList<>();
		for (Arbodienst w: result)
			inforesult.add(completeArbodienst(w));
		
		return inforesult;
	}
    public List<UitvoeringsinstituutInfo> getUitvoeringsinstituuts() throws VerzuimApplicationException {
		Query q = createQuery("select w from Uitvoeringsinstituut w left join fetch w.vestigingsadres left join fetch w.postadres");
		@SuppressWarnings("unchecked")
		List<Uitvoeringsinstituut> result = (List<Uitvoeringsinstituut>)getResultList(q);
		List<UitvoeringsinstituutInfo> inforesult = new ArrayList<>();
		for (Uitvoeringsinstituut w: result)
			inforesult.add(completeUitvoeringsinstituut(w));
		
		return inforesult;
	}
    public List<BedrijfsgegevensInfo> getBedrijfsgegevens() throws VerzuimApplicationException {
		Query q = createQuery("select w from Bedrijfsgegevens w left join fetch w.vestigingsadres left join fetch w.postadres");
		@SuppressWarnings("unchecked")
		List<Bedrijfsgegevens> result = (List<Bedrijfsgegevens>)getResultList(q);
		List<BedrijfsgegevensInfo> inforesult = new ArrayList<>();
		for (Bedrijfsgegevens ba: result)
			inforesult.add(converter.fromEntity(ba));
		return inforesult;
	}
    public List<BedrijfsartsInfo> getBedrijfsartsen() throws VerzuimApplicationException {
		Query q = createQuery("Select ba from Bedrijfsarts ba order by ba.id");
		@SuppressWarnings("unchecked")
		List<Bedrijfsarts> result = (List<Bedrijfsarts>)getResultList(q);
		List<BedrijfsartsInfo> inforesult = new ArrayList<>();
		for (Bedrijfsarts ba: result)
			inforesult.add(converter.fromEntity(ba));
		
		return inforesult;
	}
    public List<BedrijfsartsInfo> getBedrijfsartsenArbodienst(int arbodienst) throws com.gieselaar.verzuimbeheer.exceptions.VerzuimApplicationException {
		Query q = createQuery("Select ba from Bedrijfsarts ba where ba.arbodienst_ID = :id order by ba.id");
		q.setParameter("id", arbodienst);
		@SuppressWarnings("unchecked")
		List<Bedrijfsarts> result = (List<Bedrijfsarts>)getResultList(q);
		List<BedrijfsartsInfo> inforesult = new ArrayList<>();
		for (Bedrijfsarts ba: result)
			inforesult.add(converter.fromEntity(ba));
		
		return inforesult;
	}
	public ArbodienstInfo getArbodienst(Integer id) throws VerzuimApplicationException {
		if (id == null)
			return null;
		Query q = createQuery("select w from Arbodienst w left join fetch w.vestigingsadres left join fetch w.postadres left join fetch w.contactpersoon where w.id = :id");
		q.setParameter("id", id);
		@SuppressWarnings("unchecked")
		List<Arbodienst> result = (List<Arbodienst>)getResultList(q);
		if (result.size() != 1)
			return null;
		Arbodienst w = result.get(0);
		
		return completeArbodienst(w);
	}
	public UitvoeringsinstituutInfo getUitvoeringsinstituut(Integer id) throws VerzuimApplicationException {
		if (id == null)
			return null;
		Query q = createQuery("select w from Uitvoeringsinstituut w left join fetch w.vestigingsadres left join fetch w.postadres where w.id = :id");
		q.setParameter("id", id);
		@SuppressWarnings("unchecked")
		List<Uitvoeringsinstituut> result = (List<Uitvoeringsinstituut>)getResultList(q);
		if (result.size() != 1)
			return null;
		Uitvoeringsinstituut w = result.get(0);
		
		return completeUitvoeringsinstituut(w);
	}
	public BedrijfsgegevensInfo getBedrijfsgegevens(Integer id) throws VerzuimApplicationException {
		if (id == null)
			return null;
		Query q = createQuery("select w from Bedrijfsgegevens w left join fetch w.vestigingsadres left join fetch w.postadres where w.id = :id");
		q.setParameter("id", id);
		@SuppressWarnings("unchecked")
		List<Bedrijfsgegevens> result = (List<Bedrijfsgegevens>)getResultList(q);
		if (result.size() != 1)
			return null;
		Bedrijfsgegevens w = result.get(0);
		
		return completeBedrijfsgegevens(w);
	}
	public BedrijfsartsInfo getBedrijfsarts(Integer id) throws VerzuimApplicationException {
		if (id == null)
			return null;
		Query q = createQuery("Select ba from Bedrijfsarts ba where ba.id = :id");
		q.setParameter("id", id);
		@SuppressWarnings("unchecked")
		List<Bedrijfsarts> result = (List<Bedrijfsarts>)getResultList(q);
		if (result.size() != 1)
			return null;
		Bedrijfsarts ba = result.get(0);
		
		return converter.fromEntity(ba);
	}
	public ArbodienstInfo addArbodienst(ArbodienstInfo info) throws ValidationException, VerzuimApplicationException {
		try {
			info.validate();
		} catch (ValidationException e) {
			throw e;
		}

		this.arbodienst = converter.toEntity(info, this.getCurrentuser());
		this.insertEntity(this.arbodienst);
		return getArbodienst(this.arbodienst.getId());
	}
	public UitvoeringsinstituutInfo addUitvoeringsinstituut(UitvoeringsinstituutInfo info) throws ValidationException, VerzuimApplicationException {
		try {
			info.validate();
		} catch (ValidationException e) {
			throw e;
		}
		Uitvoeringsinstituut entity = converter.toEntity(info, getCurrentuser());
		this.insertEntity(entity);
		return this.getUitvoeringsinstituut(entity.getId());
	}
	public BedrijfsgegevensInfo addBedrijfsgegevens(BedrijfsgegevensInfo info) throws ValidationException, VerzuimApplicationException {
		try {
			info.validate();
		} catch (ValidationException e) {
			throw e;
		}

		Bedrijfsgegevens entity = converter.toEntity(info, this.getCurrentuser());
		this.insertEntity(entity);
		return this.getBedrijfsgegevens(entity.getId());
	}
	public BedrijfsartsInfo addBedrijfsarts(BedrijfsartsInfo info) throws ValidationException, VerzuimApplicationException {
		try {
			info.validate();
		} catch (ValidationException e) {
			throw e;
		}

		this.bedrijfsarts = converter.toEntity(info, this.getCurrentuser());
		this.insertEntity(this.bedrijfsarts);
		info.setId(this.bedrijfsarts.getId());
		return getBedrijfsarts(info.getId());
	}
	public ArbodienstInfo updateArbodienst(ArbodienstInfo info) throws ValidationException, VerzuimApplicationException {
		try {
			info.validate();
		} catch (ValidationException e) {
			throw e;
		}
    	this.arbodienst = converter.toEntity(info, this.getCurrentuser());
    	this.updateEntity(this.arbodienst);
    	return getArbodienst(arbodienst.getId());
	}
	public UitvoeringsinstituutInfo updateUitvoeringsinstituut(UitvoeringsinstituutInfo info) throws ValidationException, VerzuimApplicationException {
		try {
			info.validate();
		} catch (ValidationException e) {
			throw e;
		}
    	Uitvoeringsinstituut entity = converter.toEntity(info, this.getCurrentuser());
    	this.updateEntity(entity);
    	return this.getUitvoeringsinstituut(entity.getId());
	}
	public BedrijfsgegevensInfo updateBedrijfsgegevens(BedrijfsgegevensInfo info) throws ValidationException, VerzuimApplicationException {
		try {
			info.validate();
		} catch (ValidationException e) {
			throw e;
		}

    	this.bedrijfsgegevens = converter.toEntity(info, this.getCurrentuser());
    	this.updateEntity(this.bedrijfsgegevens);
		return this.getBedrijfsgegevens(info.getId());
	}
	public BedrijfsartsInfo updateBedrijfsarts(BedrijfsartsInfo info) throws ValidationException, VerzuimApplicationException {
		try {
			info.validate();
		} catch (ValidationException e) {
			throw e;
		}
    	this.bedrijfsarts = converter.toEntity(info, this.getCurrentuser());
    	this.updateEntity(this.bedrijfsarts);
    	return this.getBedrijfsarts(info.getId());
	}
	public boolean deleteArbodienst(ArbodienstInfo info) throws VerzuimApplicationException  {
		this.arbodienst = converter.toEntity(info, this.getCurrentuser());
		this.deleteEntity(this.arbodienst);
		return true;
	}
	public boolean deleteUitvoeringsinstituut(UitvoeringsinstituutInfo info) throws VerzuimApplicationException  {
		this.uitvoeringsinstituut = converter.toEntity(info, this.getCurrentuser());
		this.deleteEntity(this.uitvoeringsinstituut);
		return true;
	}
	public boolean deleteBedrijfsgegevens(BedrijfsgegevensInfo info) throws VerzuimApplicationException  {
		this.bedrijfsgegevens = converter.toEntity(info, this.getCurrentuser());
		this.deleteEntity(this.bedrijfsgegevens);
		return true;
	}
	public boolean deleteBedrijfsarts(BedrijfsartsInfo info) throws VerzuimApplicationException  {
		this.bedrijfsarts = converter.toEntity(info, this.getCurrentuser());
		this.deleteEntity(this.bedrijfsarts);
		return true;
	}
}
