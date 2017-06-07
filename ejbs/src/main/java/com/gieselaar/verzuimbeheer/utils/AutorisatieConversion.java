package com.gieselaar.verzuimbeheer.utils;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import com.gieselaar.verzuimbeheer.entities.Applicatiefunctie;
import com.gieselaar.verzuimbeheer.entities.Gebruiker;
import com.gieselaar.verzuimbeheer.entities.GebruikerWerkgever;
import com.gieselaar.verzuimbeheer.entities.GebruikerWerkgeverPK;
import com.gieselaar.verzuimbeheer.entities.Rol;
import com.gieselaar.verzuimbeheer.services.ApplicatieFunctieInfo;
import com.gieselaar.verzuimbeheer.services.GebruikerInfo;
import com.gieselaar.verzuimbeheer.services.GebruikerWerkgeverInfo;
import com.gieselaar.verzuimbeheer.services.GebruikerInfo.__status;
import com.gieselaar.verzuimbeheer.services.RolInfo;

/**
 * Session Bean implementation class Conversion
 */
@Stateless
@LocalBean
public class AutorisatieConversion extends BaseConversion{

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
     * Default constructor. 
     */
	WerkgeverConversion werkgeverconverter = new WerkgeverConversion();
    /**
     * @param entity
     * @return
     */
    public GebruikerInfo fromEntity(Gebruiker entity)
	{
		GebruikerInfo info = null;
		List<RolInfo> rolinfo;
		if (entity == null){
			/* niets doen */
		}else
		{
			info = create(GebruikerInfo.class);
			this.setVersionId(info, entity);
			info.setName(entity.getGebruikersnaam());
			info.setStatus(__status.parse(entity.getStatus()));
			rolinfo = new ArrayList<>();
			for (Rol r: entity.getRols())
				rolinfo.add(fromEntity(r));
			info.setRollen(rolinfo);
			info.setAchternaam(entity.getAchternaam());
			info.setEmailadres(entity.getEmailadres());
			info.setInlogfouten(entity.getInlogfouten());
			info.setLaatstepoging(entity.getLaatstepoging());
			info.setPasswordhash(entity.getPasswordhash());
			info.setTussenvoegsel(entity.getTussenvoegsel());
			info.setVoornaam(entity.getVoornaam());
			info.setAlleklanten(inttobool(entity.getAlleklanten()));
			info.setDomainname(entity.getDomainname());
			info.setAduser(inttobool(entity.getAduser()));
		}
		return info;
	}
	public Gebruiker toEntity(GebruikerInfo info, Integer currentUser)
	{
		Gebruiker entity = null;
		if (info == null){
			/* niets doen */
		}else{
			entity = new Gebruiker();
			this.getVersionId(info, entity, currentUser);
			List<Rol> rollen = new ArrayList<>();
			entity.setGebruikersnaam(info.getName());
			entity.setStatus(info.getStatus().getValue());
			if (info.getRollen() != null)
				for (RolInfo r: info.getRollen())
					rollen.add(toEntity(r,currentUser));
			entity.setRols(rollen);
			entity.setAchternaam(info.getAchternaam());
			entity.setEmailadres(info.getEmailadres());
			entity.setInlogfouten(entity.getInlogfouten());
			entity.setLaatstepoging(info.getLaatstepoging());
			entity.setPasswordhash(info.getPasswordhash());
			entity.setTussenvoegsel(info.getTussenvoegsel());
			entity.setVoornaam(info.getVoornaam());
			entity.setAlleklanten(booltoint(info.isAlleklanten()));
			entity.setDomainname(info.getDomainname());
			entity.setAduser(booltoint(info.isAduser()));
		}
		return entity;
	}
	public GebruikerWerkgeverInfo fromEntity(GebruikerWerkgever entity){
		GebruikerWerkgeverInfo info = null;
		if (entity == null){
			/* niets doen */
		}else{
			info = create(GebruikerWerkgeverInfo.class);
			info.setGebruikerid(entity.getId().getGebruikerid());
			info.setWerkgeverid(entity.getId().getWerkgeverid());
		}
		return info;
	}
	public GebruikerWerkgever toEntity(GebruikerWerkgeverInfo info){
		GebruikerWerkgever entity = null;
		if (info == null){
			/* niets doen */
		}else{
			entity = new GebruikerWerkgever();
			GebruikerWerkgeverPK entityPK = new GebruikerWerkgeverPK();
			entityPK.setGebruikerid(info.getGebruikerid());
			entityPK.setWerkgeverid(info.getWerkgeverid());
			entity.setId(entityPK);
		}
		return entity;
	}
	public ApplicatieFunctieInfo fromEntity(Applicatiefunctie entity){
		ApplicatieFunctieInfo info = null;
		if (entity == null){
			/* niets doen */
		}else{
			info = create(ApplicatieFunctieInfo.class);
			this.setVersionId(info, entity);
			info.setFunctieId(entity.getFunctieId());
			info.setFunctieomschrijving(entity.getFunctieomschrijving());
		}
		return info;
	}
	public Applicatiefunctie toEntity(ApplicatieFunctieInfo info, Integer currentUser){
		Applicatiefunctie entity = null;
		if (info == null){
			/* niets doen */
		}else{
			entity = new Applicatiefunctie();
			this.getVersionId(info, entity, currentUser);
			entity.setFunctieId(info.getFunctieId());
			entity.setFunctieomschrijving(info.getFunctieomschrijving());
		}
		return entity;
	}
	public RolInfo fromEntity(Rol entity) {
		RolInfo info = null;
		List <ApplicatieFunctieInfo> applfuncinfo;
		if (entity == null){
			/* niets doen */
		}else{
			info = create(RolInfo.class);
			this.setVersionId(info, entity);
			info.setRolid(entity.getRolid());
			info.setOmschrijving(entity.getOmschrijving());
			applfuncinfo = new ArrayList<>();
			for (Applicatiefunctie a: entity.getApplicatiefuncties())
				applfuncinfo.add(fromEntity(a));
			info.setApplicatiefuncties(applfuncinfo);
		}
		return info;
	}
	public Rol toEntity(RolInfo info, Integer currentUser){
		Rol entity = null;
		List<Applicatiefunctie> act = new ArrayList<>();
		if (info == null){
			/* niets doen */
		}else{
			entity = new Rol();
			this.getVersionId(info, entity, currentUser);
			if (info.getApplicatiefuncties() != null)
				for (ApplicatieFunctieInfo a: info.getApplicatiefuncties())
					act.add(toEntity(a,currentUser));
			entity.setApplicatiefuncties(act);
			entity.setOmschrijving(info.getOmschrijving());
			entity.setRolid(info.getRolid());
		}
		return entity;
	}
}
