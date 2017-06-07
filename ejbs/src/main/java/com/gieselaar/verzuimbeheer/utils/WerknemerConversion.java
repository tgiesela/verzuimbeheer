package com.gieselaar.verzuimbeheer.utils;

import java.util.ArrayList;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;

import com.gieselaar.verzuimbeheer.entities.AfdelingHasWerknemer;
import com.gieselaar.verzuimbeheer.entities.AfdelingHasWerknemerPK;
import com.gieselaar.verzuimbeheer.entities.Dienstverband;
import com.gieselaar.verzuimbeheer.entities.Verzuim;
import com.gieselaar.verzuimbeheer.entities.Werknemer;
import com.gieselaar.verzuimbeheer.entities.WerknemerFast;
import com.gieselaar.verzuimbeheer.entities.Wiapercentage;
import com.gieselaar.verzuimbeheer.services.AfdelingHasWerknemerInfo;
import com.gieselaar.verzuimbeheer.services.DienstverbandInfo;
import com.gieselaar.verzuimbeheer.services.WerknemerInfo;
import com.gieselaar.verzuimbeheer.services.InfoBase.__geslacht;
import com.gieselaar.verzuimbeheer.services.InfoBase.persistenceaction;
import com.gieselaar.verzuimbeheer.services.InfoBase.persistencestate;
import com.gieselaar.verzuimbeheer.services.WerknemerFastInfo;
import com.gieselaar.verzuimbeheer.services.InfoBase.__burgerlijkestaat;
import com.gieselaar.verzuimbeheer.services.WiapercentageInfo.__wiapercentage;
import com.gieselaar.verzuimbeheer.services.WiapercentageInfo;

@Stateless
@LocalBean
public class WerknemerConversion extends BaseConversion {
	private AdresConversion adresconverter = new AdresConversion();
	public AfdelingHasWerknemerInfo fromEntity(AfdelingHasWerknemer entity) {
		AfdelingHasWerknemerInfo info = null;
		if (entity == null)
			;
		else
		{
			info = create(AfdelingHasWerknemerInfo.class);

			info.setAfdelingId(entity.getId().getAfdeling_ID());
			info.setWerknemerId(entity.getId().getWerknemer_ID());
			info.setStartdatum(entity.getId().getStartdatum());
			info.setEinddatum(entity.getEinddatum());
		}
		return info;
	}
	public AfdelingHasWerknemer toEntity(AfdelingHasWerknemerInfo info){
		AfdelingHasWerknemer entity = null;
		if (info == null)
			;
		else
		{
			entity = new AfdelingHasWerknemer();
			entity.setId(new AfdelingHasWerknemerPK());
			entity.getId().setAfdeling_ID(info.getAfdelingId());
			entity.getId().setWerknemer_ID(info.getWerknemerId());
			entity.getId().setStartdatum(info.getStartdatum());
			entity.setEinddatum(info.getEinddatum());
		}
		return entity;
	}
	public WerknemerInfo fromEntity(Werknemer entity){
		WerknemerInfo info = null;
		if (entity == null)
			;
		else
		{
			info = create(WerknemerInfo.class);
			this.setVersionId(info, entity);
			info.setAchternaam(entity.getAchternaam());
			info.setAdres(adresconverter.fromEntity(entity.getAdres()));
			info.setWerkgeverid(entity.getWerkgever_ID());
			info.setArbeidsgehandicapt(inttobool(entity.getArbeidsgehandicapt()));
			info.setBurgerlijkestaat(__burgerlijkestaat.parse(entity.getBurgerlijkestaat()));
			info.setBurgerservicenummer(entity.getBurgerservicenummer());
			info.setEmail(entity.getEmail());
			info.setGeboortedatum(entity.getGeboortedatum());
			info.setGeslacht(__geslacht.parse(entity.getGeslacht()));
			info.setMobiel(entity.getMobiel());
			info.setTelefoon(entity.getTelefoon());
			info.setTelefoonPrive(entity.getTelefoonprive());
			info.setVoornaam(entity.getVoornaam());
			info.setVoorletters(entity.getVoorletters());
			info.setVoorvoegsel(entity.getVoorvoegsel());
			info.setOpmerkingen(entity.getOpmerkingen());
		}
		return info;
	}
	public WerknemerFastInfo fromEntity(WerknemerFast entity){
		WerknemerFastInfo info = null;
		if (entity == null)
			;
		else
		{
			info = new WerknemerFastInfo();
			info.setState(persistencestate.EXISTS);
			info.setAction(persistenceaction.UPDATE);
			info.setAchternaam(entity.getAchternaam());
			info.setArbeidsgehandicapt(entity.getArbeidsgehandicapt() == 0 ? false : true);
			info.setBurgerlijkestaat(com.gieselaar.verzuimbeheer.services.WerknemerFastInfo.__burgerlijkestaat
					.parse(entity.getBurgerlijkestaat()));
			info.setBurgerservicenummer(entity.getBurgerservicenummer());
			info.setDetailsPresent(false);
			info.setEmail(entity.getEmail());
			info.setGeboortedatum(entity.getGeboortedatum());
			info.setGeslacht(__geslacht.parse(entity.getGeslacht()));
			info.setId(entity.getId());
			info.setMobiel(entity.getMobiel());
			info.setOpmerkingen(entity.getOpmerkingen());
			info.setTelefoon(entity.getTelefoon());
			info.setTelefoonPrive(entity.getTelefoonprive());
			info.setTelefoonWerk(null);
			info.setVoorletters(entity.getVoorletters());
			info.setVoornaam(entity.getVoornaam());
			info.setVoorvoegsel(entity.getVoorvoegsel());
			info.setOpenvzm(entity.getOpenvzm());
			info.setVzmcnt(entity.getVzmcnt());
	
			info.setWerkgeverid(entity.getWerkgever_ID());
			info.setWerkgevernaam(entity.getWerkgevernaam());
			info.setEinddatumcontract(entity.getEinddatumcontract());
			info.setStartdatumcontract(entity.getStartdatumcontract());
			info.setWerkweek(entity.getWerkweek());
			info.setPersoneelsnummer(entity.getPersoneelsnummer());
			info.setVersion(entity.getVersion());
			info.setAfdelingnaam(entity.getAfdelingnaam());
		}
		return info;
	}
	public Werknemer toEntity(WerknemerInfo info, Integer currentUser){
		Werknemer entity = null;
		if (info == null)
			;
		else
		{
			entity = new Werknemer();
			this.getVersionId(info, entity,currentUser);
			entity.setAchternaam(info.getAchternaam());
			entity.setAdres(adresconverter.toEntity(info.getAdres(),currentUser));
			entity.setArbeidsgehandicapt(booltoint(info.isArbeidsgehandicapt()));
			entity.setBurgerlijkestaat(info.getBurgerlijkestaat().getValue());
			entity.setBurgerservicenummer(info.getBurgerservicenummer());
			entity.setEmail(info.getEmail());
			entity.setGeboortedatum(info.getGeboortedatum());
			entity.setGeslacht(info.getGeslacht().getValue());
			entity.setMobiel(info.getMobiel());
			entity.setOpmerkingen(info.getOpmerkingen());
			entity.setTelefoon(info.getTelefoon());
			entity.setTelefoonprive(info.getTelefoonPrive());
			entity.setVoornaam(info.getVoornaam());
			entity.setVoorletters(info.getVoorletters());
			entity.setVoorvoegsel(info.getVoorvoegsel());
			entity.setWerkgever_ID(info.getWerkgeverid());
			entity.setOpmerkingen(info.getOpmerkingen());
		}
		return entity;
	}
	public DienstverbandInfo fromEntity(Dienstverband entity){
		DienstverbandInfo info = null;
		if (entity == null)
			;
		else
		{
			info = create(DienstverbandInfo.class);
			this.setVersionId(info, entity);
			info.setEinddatumcontract(entity.getEinddatumcontract());
			info.setFunctie(entity.getFunctie());
			info.setPersoneelsnummer(entity.getPersoneelsnummer());
			info.setStartdatumcontract(entity.getStartdatumcontract());
			info.setWerkweek(entity.getWerkweek());
			info.setWerkgeverId(entity.getWerkgever_ID());
			info.setWerknemerId(entity.getWerknemer_ID());
		}
		return info;
	}
	public Dienstverband toEntity(DienstverbandInfo info, Integer currentUser){
		Dienstverband entity = null;
		new ArrayList<Verzuim>();
		if (info == null)
			;
		else
		{
			entity = new Dienstverband();
			this.getVersionId(info, entity,currentUser);
			entity.setEinddatumcontract(info.getEinddatumcontract());
			entity.setFunctie(info.getFunctie());
			entity.setPersoneelsnummer(info.getPersoneelsnummer());
			entity.setWerkweek(info.getWerkweek());
			entity.setStartdatumcontract(info.getStartdatumcontract());
			entity.setWerkgever_ID(info.getWerkgeverId());
			entity.setWerknemer_ID(info.getWerknemerId());
		}
		return entity;
	}
	public WiapercentageInfo fromEntity(Wiapercentage entity){
		WiapercentageInfo info = null;
		if (entity == null)
			;
		else
		{
			info = create(WiapercentageInfo.class);
			this.setVersionId(info, entity);
			info.setWerknemerId(entity.getWerknemer_ID());
			info.setStartdatum(entity.getStartdatum());
			info.setEinddatum(entity.getEinddatum());
			info.setCodeWiaPercentage(__wiapercentage.parse(entity.getCodeWiaPercentage()));
		}
		return info;
	}
	public Wiapercentage toEntity(WiapercentageInfo info, Integer currentUser){
		Wiapercentage entity = null;
		if (info == null)
			;
		else
		{
			entity = new Wiapercentage();
			this.getVersionId(info, entity,currentUser);
			entity.setWerknemer_ID(info.getWerknemerId());
			entity.setStartdatum(info.getStartdatum());
			entity.setEinddatum(info.getEinddatum());
			entity.setCodeWiaPercentage(info.getCodeWiaPercentage().getValue());
		}
		return entity;
	}

}
