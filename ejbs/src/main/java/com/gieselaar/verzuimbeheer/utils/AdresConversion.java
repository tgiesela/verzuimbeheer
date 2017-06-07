package com.gieselaar.verzuimbeheer.utils;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;

import com.gieselaar.verzuimbeheer.entities.Adres;
import com.gieselaar.verzuimbeheer.services.AdresInfo;

@Stateless
@LocalBean
public class AdresConversion extends BaseConversion {
	public AdresInfo fromEntity(Adres entity) {
		AdresInfo info = null;
		if (entity == null)
			;
		else
		{
			info = create(AdresInfo.class);
			this.setVersionId(info, entity);

			info.setHuisnummer(entity.getHuisnummer());
			info.setHuisnummertoevoeging(entity.getHuisnummertoevoeging());
			info.setLand(entity.getLand());
			info.setPlaats(entity.getPlaats());
			info.setPostcode(entity.getPostcode());
			info.setStraat(entity.getStraat());
		}
		return info;
	}
	public Adres toEntity(AdresInfo info, Integer currentUser){
		Adres entity = null;
		if (info == null)
			;
		else
		{
			entity = new Adres();
			this.getVersionId(info, entity, currentUser);
			entity.setHuisnummer(info.getHuisnummer());
			entity.setHuisnummertoevoeging(info.getHuisnummertoevoeging());
			entity.setLand(info.getLand());
			entity.setPlaats(info.getPlaats());
			entity.setPostcode(info.getPostcode());
			entity.setStraat(info.getStraat());
		}
		return entity;
	}

}
