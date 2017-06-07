package com.gieselaar.verzuimbeheer.utils;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;

import com.gieselaar.verzuimbeheer.entities.Cascode;
import com.gieselaar.verzuimbeheer.entities.Cascodegroep;
import com.gieselaar.verzuimbeheer.services.CascodeGroepInfo;
import com.gieselaar.verzuimbeheer.services.CascodeInfo;
import com.gieselaar.verzuimbeheer.services.VerzuimInfo.__vangnettype;

@Stateless
@LocalBean
public class CascodeConversion extends BaseConversion {
	public CascodeInfo fromEntity(Cascode entity) {
		CascodeInfo info = null;
		if (entity == null)
			;
		else
		{
			info = create(CascodeInfo.class);
			this.setVersionId(info, entity);

			info.setId(entity.getId());
			info.setCascode(entity.getCascode());
			info.setOmschrijving(entity.getOmschrijving());
			info.setCascodegroep(entity.getCascodegroep());
			info.setActief(inttobool(entity.getActief()));
			info.setVangnettype(__vangnettype.parse(entity.getVangnettype()));
		}
		return info;
	}
	public Cascode toEntity(CascodeInfo info, Integer currentUser){
		Cascode entity = null;
		if (info == null)
			;
		else
		{
			entity = new Cascode();
			this.getVersionId(info, entity, currentUser);
			entity.setCascode(info.getCascode());
			entity.setOmschrijving(info.getOmschrijving());
			entity.setCascodegroep(info.getCascodegroep());
			entity.setActief(booltoint(info.isActief()));
			entity.setVangnettype(info.getVangnettype().getValue());
		}
		return entity;
	}
	public CascodeGroepInfo fromEntity(Cascodegroep entity) {
		CascodeGroepInfo info = null;
		if (entity == null)
			;
		else
		{
			info = create(CascodeGroepInfo.class);
			this.setVersionId(info, entity);
			info.setNaam(entity.getNaam());
			info.setOmschrijving(entity.getOmschrijving());
		}
		return info;
	}
	public Cascodegroep toEntity(CascodeGroepInfo info, Integer currentUser){
		Cascodegroep entity = null;
		if (info == null)
			;
		else
		{
			entity = new Cascodegroep();
			this.getVersionId(info, entity, currentUser);
			entity.setNaam(info.getNaam());
			entity.setOmschrijving(info.getOmschrijving());
		}
		return entity;
	}
}
