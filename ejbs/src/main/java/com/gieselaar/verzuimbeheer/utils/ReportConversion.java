package com.gieselaar.verzuimbeheer.utils;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;

import com.gieselaar.verzuimbeheer.entities.VActueelverzuim;
import com.gieselaar.verzuimbeheer.entities.WerknemerAantallen;
import com.gieselaar.verzuimbeheer.entities.WerknemerVerzuim;
import com.gieselaar.verzuimbeheer.reportservices.ActueelVerzuimInfo;
import com.gieselaar.verzuimbeheer.reportservices.WerknemerAantallenInfo;
import com.gieselaar.verzuimbeheer.reportservices.WerknemerVerzuimInfo;
import com.gieselaar.verzuimbeheer.services.InfoBase.__geslacht;
import com.gieselaar.verzuimbeheer.services.VerzuimInfo.__gerelateerdheid;

@Stateless
@LocalBean
public class ReportConversion extends BaseConversion {
	public WerknemerAantallenInfo fromEntity(WerknemerAantallen entity){
		WerknemerAantallenInfo info = null;
		if (entity == null)
			;
		else
		{
			info = create(WerknemerAantallenInfo.class);
			info.setAantalwerknemers(entity.getAantalwerknemers());
			info.setAfdelingId(entity.getId().getAfdelingid());
			info.setGeslacht(__geslacht.parse(entity.getId().getGeslacht()));
			info.setTotaalurenwerknemers(entity.getTotaalurenwerknemers());
			info.setWerkgeverId(entity.getId().getWerkgeverid());
			info.setStartdatum(entity.getId().getStartdatum());
			info.setEinddatum(entity.getId().getEinddatum());
		}
		return info;
	}
	public ActueelVerzuimInfo fromEntity(VActueelverzuim entity){
		ActueelVerzuimInfo info = null;
		if (entity == null)
			;
		else
		{
			info = create(ActueelVerzuimInfo.class);
			
			info.setAchternaam(entity.getAchternaam());
			info.setVoornaam(entity.getVoornaam());
			info.setAfdelingnaam(entity.getAfdelingnaam());
			info.setBurgerservicenummer(entity.getBurgerservicenummer());
			info.setDatumHerstel(entity.getDatumHerstel());
			info.setDienstverbandid(entity.getDienstverbandid());
			info.setEinddatumcontract(entity.getEinddatumcontract());
			info.setEinddatumverzuim(entity.getEinddatumverzuim());
			info.setGeslacht(entity.getGeslacht());
			info.setGeboortedatum(entity.getGeboortedatum());
			info.setGerelateerdheid(__gerelateerdheid.parse(entity.getGerelateerdheid()));
			info.setHerstelmeldingsdatum(entity.getHerstelmeldingsdatum());
			info.setHerstelopmerkingen(entity.getHerstelopmerkingen());
			info.setHoldingnaam(entity.getHoldingnaam());
			info.setAfdelingid(entity.getAfdelingid());
			info.setPercentageHerstel(entity.getPercentageHerstel());
			info.setPercentageHerstelAt(entity.getPercentageHerstelAt());
			info.setStartdatumcontract(entity.getStartdatumcontract());
			info.setStartdatumverzuim(entity.getStartdatumverzuim());
			info.setVerzuimid(entity.getVerzuimid());
			info.setVerzuimmeldingsdatum(entity.getVerzuimmeldingsdatum());
			info.setVoorletters(entity.getVoorletters());
			info.setVoorvoegsel(entity.getVoorvoegsel());
			info.setWerkgeverid(entity.getWerkgeverid());
			info.setWerknemerid(entity.getWerknemerid());
			info.setWerkgevernaam(entity.getWerkgevernaam());
			info.setWerkweek(entity.getWerkweek());
			info.setHoldingid(entity.getHoldingid());
			info.setWerkgeverwerkweek(entity.getWerkgeverwerkweek());
			info.setVangnettype(entity.getVangnettype());
			info.setHerstelid(entity.getHerstelid());
			info.setCascode(entity.getCascode());
			info.setCascodeomschrijving(entity.getCascodeomschrijving());
			info.setPersoneelsnummer(entity.getPersoneelsnummer());
			info.setOpmerkingen(entity.getOpmerkingen());
			info.setUitkeringnaarwerknemer(inttobool(entity.getUitkeringnaarwerknemer()));
		}
		return info;
	}
	public WerknemerVerzuimInfo fromEntity(WerknemerVerzuim entity) {
		WerknemerVerzuimInfo info = null;
		if (entity == null)
			;
		else
		{
			info = create(WerknemerVerzuimInfo.class);
			
			info.setAantalverzuimen(entity.getAantalverzuimen());
			info.setAchternaam(entity.getAchternaam());
			info.setAfdelingnaam(entity.getAfdelingnaam());
			info.setAfdelingid(entity.getAfdelingid());
			info.setBurgerservicenummer(entity.getBurgerservicenummer());
			info.setDienstverbandid(entity.getDienstverbandid());
			info.setEinddatumcontract(entity.getEinddatumcontract());
			info.setGeslacht(entity.getGeslacht());
			info.setGeboortedatum(entity.getGeboortedatum());
			info.setHoldingnaam(entity.getHoldingnaam());
			info.setStartdatumcontract(entity.getStartdatumcontract());
			info.setVoorletters(entity.getVoorletters());
			info.setVoornaam(entity.getVoornaam());
			info.setVoorvoegsel(entity.getVoorvoegsel());
			info.setWerkgeverid(entity.getWerkgeverid());
			info.setWerknemerid(entity.getWerknemerid());
			info.setWerkgevernaam(entity.getWerkgevernaam());
			info.setWerkweek(entity.getWerkweek());
			info.setHoldingid(entity.getHoldingid());
			info.setWerkgeverwerkweek(entity.getWerkgeverwerkweek());
		}
		return info;
	}	
}
