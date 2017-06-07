package com.gieselaar.verzuimbeheer.utils;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;

import com.gieselaar.verzuimbeheer.entities.Contactpersoon;
import com.gieselaar.verzuimbeheer.services.ContactpersoonInfo;
import com.gieselaar.verzuimbeheer.utils.BaseConversion;
import com.gieselaar.verzuimbeheer.services.InfoBase.__geslacht;

@Stateless
@LocalBean
public class ContactpersoonConversion extends BaseConversion {
	public ContactpersoonInfo fromEntity(Contactpersoon entity) {
		ContactpersoonInfo info = null;
		if (entity == null) {
			/* nothing to do */
		} else {
			info = create(ContactpersoonInfo.class);
			this.setVersionId(info, entity);

			info.setAchternaam(entity.getAchternaam());
			info.setEmailadres(entity.getEmailadres());
			info.setGeslacht(__geslacht.parse(entity.getGeslacht()));
			info.setMobiel(entity.getMobiel());
			info.setTelefoon(entity.getTelefoon());
			info.setVoorletters(entity.getVoorletters());
			info.setVoornaam(entity.getVoornaam());
			info.setVoorvoegsel(entity.getVoorvoegsel());
		}
		return info;
	}
	public Contactpersoon toEntity(ContactpersoonInfo info, Integer currentUser){
		Contactpersoon entity = null;
		if (info == null){
			/* nothing to do */
		}else{
			entity = new Contactpersoon();
			this.getVersionId(info, entity, currentUser);
			entity.setAchternaam(info.getAchternaam());
			entity.setEmailadres(info.getEmailadres());
			if (info.getGeslacht() != null)
				entity.setGeslacht(info.getGeslacht().getValue());
			entity.setMobiel(info.getMobiel());
			entity.setTelefoon(info.getTelefoon());
			entity.setVoorletters(info.getVoorletters());
			entity.setVoornaam(info.getVoornaam());
			entity.setVoorvoegsel(info.getVoorvoegsel());
		}
		return entity;
	}

}
