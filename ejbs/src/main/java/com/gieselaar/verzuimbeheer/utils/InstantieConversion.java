package com.gieselaar.verzuimbeheer.utils;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;

import com.gieselaar.verzuimbeheer.entities.Arbodienst;
import com.gieselaar.verzuimbeheer.entities.Bedrijfsarts;
import com.gieselaar.verzuimbeheer.entities.Bedrijfsgegevens;
import com.gieselaar.verzuimbeheer.entities.Uitvoeringsinstituut;
import com.gieselaar.verzuimbeheer.services.ArbodienstInfo;
import com.gieselaar.verzuimbeheer.services.BedrijfsartsInfo;
import com.gieselaar.verzuimbeheer.services.BedrijfsgegevensInfo;
import com.gieselaar.verzuimbeheer.services.UitvoeringsinstituutInfo;
import com.gieselaar.verzuimbeheer.services.InfoBase.__geslacht;

@Stateless
@LocalBean
public class InstantieConversion extends BaseConversion {
	private AdresConversion adresconverter = new AdresConversion();
	private ContactpersoonConversion contactpersoonconverter = new ContactpersoonConversion();
	public ArbodienstInfo fromEntity(Arbodienst entity) {
		ArbodienstInfo info = null;
		if (entity == null)
			;
		else
		{
			info = create(ArbodienstInfo.class);
			
			this.setVersionId(info, entity);
			info.setNaam(entity.getNaam());
			info.setPostAdres(adresconverter.fromEntity(entity.getPostadres()));
			info.setVestigingsAdres(adresconverter.fromEntity(entity.getVestigingsadres()));
			info.setContactpersoon(contactpersoonconverter.fromEntity(entity.getContactpersoon()));
			info.setTelefoonnummer(entity.getTelefoonnummer());
		}
		return info;
	}
	public Arbodienst toEntity(ArbodienstInfo info, Integer currentUser){
		Arbodienst entity = null;
		if (info == null)
			;
		else
		{
			entity = new Arbodienst();
			this.getVersionId(info, entity,currentUser);
			entity.setNaam(info.getNaam());
			entity.setPostadres(adresconverter.toEntity(info.getPostAdres(),currentUser));
			entity.setVestigingsadres(adresconverter.toEntity(info.getVestigingsAdres(),currentUser));
			entity.setContactpersoon(contactpersoonconverter.toEntity(info.getContactpersoon(),currentUser));
			entity.setTelefoonnummer(info.getTelefoonnummer());
		}
		return entity;
	}
	public UitvoeringsinstituutInfo fromEntity(Uitvoeringsinstituut entity) {
		UitvoeringsinstituutInfo info = null;
		if (entity == null)
			;
		else
		{
			info = create(UitvoeringsinstituutInfo.class);
			this.setVersionId(info, entity);
			info.setNaam(entity.getNaam());
			info.setPostadres(adresconverter.fromEntity(entity.getPostadres()));
			info.setVestigingsadres(adresconverter.fromEntity(entity.getVestigingsadres()));
			info.setTelefoonnummer(entity.getTelefoonnummer());
		}
		return info;
	}
	public Uitvoeringsinstituut toEntity(UitvoeringsinstituutInfo info, Integer currentUser){
		Uitvoeringsinstituut entity = null;
		if (info == null)
			;
		else
		{
			entity = new Uitvoeringsinstituut();
			this.getVersionId(info, entity,currentUser);
			entity.setPostadres(adresconverter.toEntity(info.getPostadres(),currentUser));
			entity.setVestigingsadres(adresconverter.toEntity(info.getVestigingsadres(),currentUser));
			entity.setNaam(info.getNaam());
			entity.setTelefoonnummer(info.getTelefoonnummer());
		}
		return entity;
	}
	public BedrijfsartsInfo fromEntity(Bedrijfsarts entity) {
		BedrijfsartsInfo info = null;
		if (entity == null)
			;
		else
		{
			info = create(BedrijfsartsInfo.class);
			this.setVersionId(info, entity);
			info.setAchternaam(entity.getAchternaam());
			info.setArbodienstId(entity.getArbodienst_ID());
			info.setEmail(entity.getEmail());
			info.setGeslacht(__geslacht.parse(entity.getGeslacht()));
			info.setTelefoon(entity.getTelefoon());
			info.setVoorletters(entity.getVoorletters());
			info.setVoornaam(entity.getVoornaam());
		}
		return info;
	}
	public Bedrijfsarts toEntity(BedrijfsartsInfo info, Integer currentUser){
		Bedrijfsarts entity = null;
		if (info == null)
			;
		else
		{
			entity = new Bedrijfsarts();
			this.getVersionId(info, entity,currentUser);
			entity.setAchternaam(info.getAchternaam());
			entity.setArbodienst_ID(info.getArbodienstId());
			entity.setEmail(info.getEmail());
			entity.setGeslacht((info.getGeslacht().getValue()));
			entity.setTelefoon(info.getTelefoon());
			entity.setVoorletters(info.getVoorletters());
			entity.setVoornaam(info.getVoornaam());
		}
		return entity;
	}
	public BedrijfsgegevensInfo fromEntity(Bedrijfsgegevens entity) {
		BedrijfsgegevensInfo info = null;
		if (entity == null)
			;
		else
		{
			info = create(BedrijfsgegevensInfo.class);

			this.setVersionId(info, entity);
			info.setNaam(entity.getNaam());
			info.setTelefoonnummer(entity.getTelefoon());
			info.setMobiel(entity.getMobiel());
			info.setFax(entity.getFax());

			info.setPostAdres(adresconverter.fromEntity(entity.getPostadres()));
			info.setVestigingsAdres(adresconverter.fromEntity(entity.getVestigingsadres()));
			
			info.setEmailadres(entity.getEmailadres());
			info.setWebsite(entity.getWebsite());
			info.setKvknr(entity.getKvknr());
			info.setBankrekening(entity.getBankrekening());
			info.setBtwnummer(entity.getBtwnummer());
			
		}
		return info;
	}
	public Bedrijfsgegevens toEntity(BedrijfsgegevensInfo info, Integer currentUser){
		Bedrijfsgegevens entity = null;
		if (info == null)
			;
		else
		{
			entity = new Bedrijfsgegevens();
			this.getVersionId(info, entity,currentUser);
			entity.setNaam(info.getNaam());
			entity.setTelefoon(info.getTelefoonnummer());
			entity.setMobiel(info.getMobiel());
			entity.setFax(info.getFax());
			
			entity.setPostadres(adresconverter.toEntity(info.getPostAdres(),currentUser));
			entity.setVestigingsadres(adresconverter.toEntity(info.getVestigingsAdres(),currentUser));
			
			entity.setEmailadres(info.getEmailadres());
			entity.setWebsite(info.getWebsite());
			entity.setKvknr(info.getKvknr());
			entity.setBankrekening(info.getBankrekening());
			entity.setBtwnummer(info.getBtwnummer());
		}
		return entity;
	}

}
