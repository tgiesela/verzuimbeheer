package com.gieselaar.verzuimbeheer.utils;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;

import com.gieselaar.verzuimbeheer.entities.Btw;
import com.gieselaar.verzuimbeheer.entities.Factuur;
import com.gieselaar.verzuimbeheer.entities.FactuurTotaal;
import com.gieselaar.verzuimbeheer.entities.Factuurbetaling;
import com.gieselaar.verzuimbeheer.entities.Factuurcategorie;
import com.gieselaar.verzuimbeheer.entities.Factuuritem;
import com.gieselaar.verzuimbeheer.entities.Factuurkop;
import com.gieselaar.verzuimbeheer.entities.Factuurregelbezoek;
import com.gieselaar.verzuimbeheer.entities.Factuurregelitem;
import com.gieselaar.verzuimbeheer.entities.Factuurregelsecretariaat;
import com.gieselaar.verzuimbeheer.entities.Tarief;
import com.gieselaar.verzuimbeheer.entities.Werkzaamheden;
import com.gieselaar.verzuimbeheer.services.BtwInfo;
import com.gieselaar.verzuimbeheer.services.FactuurInfo;
import com.gieselaar.verzuimbeheer.services.FactuurInfo.__factuurstatus;
import com.gieselaar.verzuimbeheer.services.FactuurTotaalInfo;
import com.gieselaar.verzuimbeheer.services.FactuurbetalingInfo;
import com.gieselaar.verzuimbeheer.services.FactuurcategorieInfo;
import com.gieselaar.verzuimbeheer.services.FactuuritemInfo;
import com.gieselaar.verzuimbeheer.services.FactuurkopInfo;
import com.gieselaar.verzuimbeheer.services.FactuurregelbezoekInfo;
import com.gieselaar.verzuimbeheer.services.FactuurregelitemInfo;
import com.gieselaar.verzuimbeheer.services.FactuurregelsecretariaatInfo;
import com.gieselaar.verzuimbeheer.services.TariefInfo;
import com.gieselaar.verzuimbeheer.services.BtwInfo.__btwtariefsoort;
import com.gieselaar.verzuimbeheer.services.InfoBase.__geslacht;
import com.gieselaar.verzuimbeheer.services.TariefInfo.__tariefperiode;
import com.gieselaar.verzuimbeheer.services.WerkzaamhedenInfo;
import com.gieselaar.verzuimbeheer.services.WerkzaamhedenInfo.__huisbezoekurgentie;
import com.gieselaar.verzuimbeheer.services.WerkzaamhedenInfo.__verzuimsoort;
import com.gieselaar.verzuimbeheer.services.WerkzaamhedenInfo.__werkzaamhedensoort;

@Stateless
@LocalBean
public class FactuurConversion extends BaseConversion {
	public WerkzaamhedenInfo fromEntity(Werkzaamheden entity) {
		WerkzaamhedenInfo info = null;
		if (entity == null)
			;
		else
		{
			info = create(WerkzaamhedenInfo.class);
			this.setVersionId(info, entity);
			info.setAantalkm(entity.getAantalkm());
			info.setDatum(entity.getDatum());
			info.setFiliaalid(entity.getFiliaalid());
			info.setGeslacht(__geslacht.parse(entity.getGeslacht()));
			info.setOverigekosten(entity.getOverigekosten());
			info.setPersoneelsnummer(entity.getPersoneelsnummer());
			info.setPersoon(entity.getPersoon());
			info.setSoortverzuim(__verzuimsoort.parse(entity.getSoortverzuim()));
			info.setSoortwerkzaamheden(__werkzaamhedensoort.parse(entity.getSoortwerkzaamheden()));
			info.setUren(entity.getUren());
			info.setUserid(entity.getUserid());
			info.setWerkgeverid(entity.getWerkgeverid());
			info.setHoldingid(entity.getHoldingid());
			info.setWoonplaats(entity.getWoonplaats());
			info.setOmschrijving(entity.getOmschrijving());
			info.setUrgentie(__huisbezoekurgentie.parse(entity.getUrgentie()));
		}
		return info;
	}
	public Werkzaamheden toEntity(WerkzaamhedenInfo info, Integer currentUser){
		Werkzaamheden entity = null;
		if (info == null)
			;
		else
		{
			entity = new Werkzaamheden();
			this.getVersionId(info, entity,currentUser);
			entity.setAantalkm(info.getAantalkm());
			entity.setDatum(info.getDatum());
			entity.setFiliaalid(info.getFiliaalid());
			entity.setGeslacht(info.getGeslacht().getValue());
			entity.setOverigekosten(info.getOverigekosten());
			entity.setPersoneelsnummer(info.getPersoneelsnummer());
			entity.setPersoon(info.getPersoon());
			entity.setSoortverzuim(info.getSoortverzuim().getValue());
			entity.setSoortwerkzaamheden(info.getSoortwerkzaamheden().getValue());
			entity.setUren(info.getUren());
			entity.setUserid(info.getUserid());
			entity.setWerkgeverid(info.getWerkgeverid());
			entity.setHoldingid(info.getHoldingid());
			entity.setWoonplaats(info.getWoonplaats());
			entity.setOmschrijving(info.getOmschrijving());
			entity.setUrgentie(info.getUrgentie().getValue());
		}
		return entity;
	}
	public BtwInfo fromEntity(Btw entity) {
		BtwInfo info = null;
		if (entity == null)
			;
		else
		{
			info = create(BtwInfo.class);
			this.setVersionId(info, entity);
			info.setBtwtariefsoort(__btwtariefsoort.parse(entity.getBtwtariefsoort()));
			info.setEinddatum(entity.getEinddatum());
			info.setIngangsdatum(entity.getIngangsdatum());
			info.setPercentage(entity.getPercentage());
		}
		return info;
	}
	public Btw toEntity(BtwInfo info, Integer currentUser){
		Btw entity = null;
		if (info == null)
			;
		else
		{
			entity = new Btw();
			this.getVersionId(info, entity,currentUser);
			entity.setBtwtariefsoort(info.getBtwtariefsoort().getValue());
			entity.setEinddatum(info.getEinddatum());
			entity.setIngangsdatum(info.getIngangsdatum());
			entity.setPercentage(info.getPercentage());
		}
		return entity;
	}
	public TariefInfo fromEntity(Tarief entity){
		TariefInfo info = null;
		if (entity == null)
			;
		else
		{
			info = create(TariefInfo.class);
			this.setVersionId(info, entity);
			info.setAansluitkosten(entity.getAansluitkosten());
			info.setAansluitkostenPeriode(__tariefperiode.parse(entity.getAansluitkostenPeriode()));
			info.setAbonnement(entity.getAbonnement());
			info.setAbonnementPeriode(__tariefperiode.parse(entity.getAbonnementPeriode()));
			info.setDatumAansluitkosten(entity.getDatumAansluitkosten());
			info.setEinddatum(entity.getEinddatum());
			info.setHuisbezoekTarief(entity.getHuisbezoekTarief());
			info.setHuisbezoekZaterdagTarief(entity.getHuisbezoekZaterdagTarief());
			info.setId(entity.getId());
			info.setIngangsdatum(entity.getIngangsdatum());
			info.setKmTarief(entity.getKmTarief());
			info.setMaandbedragSecretariaat(entity.getMaandbedragSecretariaat());
			info.setOmschrijvingFactuur(entity.getOmschrijvingFactuur());
			info.setSecretariaatskosten(entity.getSecretariaatskosten());
			info.setSociaalbezoekTarief(entity.getSociaalbezoekTarief());
			info.setSpoedbezoekTarief(entity.getSpoedbezoekTarief());
			info.setSpoedbezoekZelfdedagTarief(entity.getSpoedbezoekZelfdedagTarief());
			info.setStandaardHuisbezoekTarief(entity.getStandaardHuisbezoekTarief());
			info.setTelefonischeControleTarief(entity.getTelefonischeControleTarief());
			info.setUurtariefNormaal(entity.getUurtariefNormaal());
			info.setUurtariefWeekend(entity.getUurtariefWeekend());
			info.setVasttariefhuisbezoeken(inttobool(entity.getVasttariefhuisbezoeken()));
			info.setWerkgeverId(entity.getWerkgeverId());
			info.setHoldingId(entity.getHoldingId());
		}
		return info;
	}
	public Tarief toEntity(TariefInfo info, Integer currentUser){
		Tarief entity = null;
		if (info == null)
			;
		else
		{
			entity = new Tarief();
			this.getVersionId(info, entity,currentUser);
			entity.setAansluitkosten(info.getAansluitkosten());
			entity.setAansluitkostenPeriode(info.getAansluitkostenPeriode().getValue());
			entity.setAbonnement(info.getAbonnement());
			entity.setAbonnementPeriode(info.getAbonnementPeriode().getValue());
			entity.setDatumAansluitkosten(info.getDatumAansluitkosten());
			entity.setEinddatum(info.getEinddatum());
			entity.setHuisbezoekTarief(info.getHuisbezoekTarief());
			entity.setHuisbezoekZaterdagTarief(info.getHuisbezoekZaterdagTarief());
			entity.setId(info.getId());
			entity.setIngangsdatum(info.getIngangsdatum());
			entity.setKmTarief(info.getKmTarief());
			entity.setMaandbedragSecretariaat(info.getMaandbedragSecretariaat());
			entity.setOmschrijvingFactuur(info.getOmschrijvingFactuur());
			entity.setSecretariaatskosten(info.getSecretariaatskosten());
			entity.setSociaalbezoekTarief(info.getSociaalbezoekTarief());
			entity.setSpoedbezoekTarief(info.getSpoedbezoekTarief());
			entity.setSpoedbezoekZelfdedagTarief(info.getSpoedbezoekZelfdedagTarief());
			entity.setStandaardHuisbezoekTarief(info.getStandaardHuisbezoekTarief());
			entity.setTelefonischeControleTarief(info.getTelefonischeControleTarief());
			entity.setUurtariefNormaal(info.getUurtariefNormaal());
			entity.setUurtariefWeekend(info.getUurtariefWeekend());
			entity.setVasttariefhuisbezoeken(booltoint(info.getVasttariefhuisbezoeken()));
			entity.setWerkgeverId(info.getWerkgeverId());
			entity.setHoldingId(info.getHoldingId());
		}
		return entity;
		
	}
	public FactuurcategorieInfo fromEntity(Factuurcategorie entity) {
		FactuurcategorieInfo info = null;
		if (entity == null)
			;
		else
		{
			info = create(FactuurcategorieInfo.class);
			this.setVersionId(info, entity);
			info.setBtwcategorie(__btwtariefsoort.parse(entity.getBtwcategorie()));
			info.setFactuurkopid(entity.getFactuurkopid());
			info.setIsomzet(inttobool(entity.getIsomzet()));
			info.setOmschrijving(entity.getOmschrijving());
		}
		return info;
	}
	public Factuurcategorie toEntity(FactuurcategorieInfo info, Integer currentUser){
		Factuurcategorie entity = null;
		if (info == null)
			;
		else
		{
			entity = new Factuurcategorie();
			this.getVersionId(info, entity,currentUser);
			entity.setBtwcategorie(info.getBtwcategorie().getValue());
			entity.setFactuurkopid(info.getFactuurkopid());
			entity.setIsomzet(booltoint(info.isIsomzet()));
			entity.setOmschrijving(info.getOmschrijving());
		}
		return entity;
	}
	public FactuuritemInfo fromEntity(Factuuritem entity) {
		FactuuritemInfo info = null;
		if (entity == null)
			;
		else
		{
			info = create(FactuuritemInfo.class);
			this.setVersionId(info, entity);
			info.setBedrag(entity.getBedrag());
			info.setDatum(entity.getDatum());
			info.setFactuurcategorieid(entity.getFactuurcategorieid());
			info.setOmschrijving(entity.getOmschrijving());
			info.setUserid(entity.getUserid());
			info.setWerkgeverid(entity.getWerkgeverid());
			info.setHoldingid(entity.getHoldingid());
		}
		return info;
	}
	public Factuuritem toEntity(FactuuritemInfo info, Integer currentUser){
		Factuuritem entity = null;
		if (info == null)
			;
		else
		{
			entity = new Factuuritem();
			this.getVersionId(info, entity,currentUser);
			entity.setBedrag(info.getBedrag());
			entity.setDatum(info.getDatum());
			entity.setFactuurcategorieid(info.getFactuurcategorieid());
			entity.setOmschrijving(info.getOmschrijving());
			entity.setUserid(info.getUserid());
			entity.setWerkgeverid(info.getWerkgeverid());
			entity.setHoldingid(info.getHoldingid());
		}
		return entity;
	}

	public FactuurbetalingInfo fromEntity(Factuurbetaling entity) {
		FactuurbetalingInfo info = null;
		if (entity == null)
			;
		else
		{
			info = create(FactuurbetalingInfo.class);
			this.setVersionId(info, entity);
			info.setBedrag(entity.getBedrag());
			info.setDatum(entity.getDatum());
			info.setFactuurid(entity.getFactuurid());
			info.setRekeningnummerbetaler(entity.getRekeningnummerbetaler());
		}
		return info;
	}
	public Factuurbetaling toEntity(FactuurbetalingInfo info, Integer currentUser){
		Factuurbetaling entity = null;
		if (info == null)
			;
		else
		{
			entity = new Factuurbetaling();
			this.getVersionId(info, entity,currentUser);
			entity.setBedrag(info.getBedrag());
			entity.setDatum(info.getDatum());
			entity.setFactuurid(info.getFactuurid());
			entity.setRekeningnummerbetaler(info.getRekeningnummerbetaler());
		}
		return entity;
	}
	
	public FactuurkopInfo fromEntity(Factuurkop entity) {
		FactuurkopInfo info = null;
		if (entity == null)
			;
		else
		{
			info = create(FactuurkopInfo.class);
			this.setVersionId(info, entity);
			info.setPrioriteit(entity.getPrioriteit());
			info.setOmschrijving(entity.getOmschrijving());
		}
		return info;
	}
	public Factuurkop toEntity(FactuurkopInfo info, Integer currentUser){
		Factuurkop entity = null;
		if (info == null)
			;
		else
		{
			entity = new Factuurkop();
			this.getVersionId(info, entity,currentUser);
			entity.setPrioriteit(info.getPrioriteit());
			entity.setOmschrijving(info.getOmschrijving());
		}
		return entity;
	}
	
	public FactuurregelbezoekInfo fromEntity(Factuurregelbezoek entity) {
		FactuurregelbezoekInfo info = null;
		if (entity == null)
			;
		else
		{
			info = create(FactuurregelbezoekInfo.class);
			this.setVersionId(info, entity);
			info.setCasemanagementkosten(entity.getCasemanagementkosten());
			info.setFactuurid(entity.getFactuurid());
			info.setKilometerkosten(entity.getKilometerkosten());
			info.setKilometertarief(entity.getKilometertarief());
			info.setOverigekosten(entity.getOverigekosten());
			info.setUurkosten(entity.getUurkosten());
			info.setUurtarief(entity.getUurtarief());
			info.setVastekosten(entity.getVastekosten());
			info.setWerkzaamhedenid(entity.getWerkzaamhedenid());
		}
		return info;
	}
	public Factuurregelbezoek toEntity(FactuurregelbezoekInfo info, Integer currentUser){
		Factuurregelbezoek entity = null;
		if (info == null)
			;
		else
		{
			entity = new Factuurregelbezoek();
			this.getVersionId(info, entity,currentUser);
			entity.setCasemanagementkosten(info.getCasemanagementkosten());
			entity.setFactuurid(info.getFactuurid());
			entity.setKilometerkosten(info.getKilometerkosten());
			entity.setKilometertarief(info.getKilometertarief());
			entity.setOverigekosten(info.getOverigekosten());
			entity.setUurkosten(info.getUurkosten());
			entity.setUurtarief(info.getUurtarief());
			entity.setVastekosten(info.getVastekosten());
			entity.setWerkzaamhedenid(info.getWerkzaamhedenid());
		}
		return entity;
	}
	public FactuurregelitemInfo fromEntity(Factuurregelitem entity) {
		FactuurregelitemInfo info = null;
		if (entity == null)
			;
		else
		{
			info = create(FactuurregelitemInfo.class);
			this.setVersionId(info, entity);
			info.setBedrag(entity.getBedrag());
			info.setBtwbedrag(entity.getBtwbedrag());
			info.setFactuuritemid(entity.getFactuuritemid());
			info.setFactuurid(entity.getFactuurid());
			info.setBtwcategorie(__btwtariefsoort.parse(entity.getBtwcategorie()));
			info.setBtwpercentage(entity.getBtwpercentage());
		}
		return info;
	}
	public Factuurregelitem toEntity(FactuurregelitemInfo info, Integer currentUser){
		Factuurregelitem entity = null;
		if (info == null)
			;
		else
		{
			entity = new Factuurregelitem();
			this.getVersionId(info, entity,currentUser);
			entity.setBedrag(info.getBedrag());
			entity.setBtwbedrag(info.getBtwbedrag());
			entity.setFactuuritemid(info.getFactuuritemid());
			entity.setFactuurid(info.getFactuurid());
			entity.setBtwcategorie(info.getBtwcategorie().getValue());
			entity.setBtwpercentage(info.getBtwpercentage());
		}
		return entity;
	}
	public FactuurregelsecretariaatInfo fromEntity(Factuurregelsecretariaat entity) {
		FactuurregelsecretariaatInfo info = null;
		if (entity == null)
			;
		else
		{
			info = create(FactuurregelsecretariaatInfo.class);
			this.setVersionId(info, entity);
			info.setFactuurid(entity.getFactuurid());
			info.setOverigekosten(entity.getOverigekosten());
			info.setSecretariaatskosten(entity.getSecretariaatskosten());
			info.setUurtarief(entity.getUurtarief());
			info.setWeeknummer(entity.getWeeknummer());
			info.setWerkzaamhedenid(entity.getWerkzaamhedenid());
		}
		return info;
	}
	public Factuurregelsecretariaat toEntity(FactuurregelsecretariaatInfo info, Integer currentUser){
		Factuurregelsecretariaat entity = null;
		if (info == null)
			;
		else
		{
			entity = new Factuurregelsecretariaat();
			this.getVersionId(info, entity,currentUser);
			entity.setFactuurid(info.getFactuurid());
			entity.setOverigekosten(info.getOverigekosten());
			entity.setSecretariaatskosten(info.getSecretariaatskosten());
			entity.setUurtarief(info.getUurtarief());
			entity.setWeeknummer(info.getWeeknummer());
			entity.setWerkzaamhedenid(info.getWerkzaamhedenid());
		}
		return entity;
	}
	private void setFactuurInfo(FactuurInfo info, Factuur entity){
		this.setVersionId(info, entity);
		info.setAanmaakdatum(entity.getAanmaakdatum());
		info.setAansluitkosten(entity.getAansluitkosten());
		info.setAansluitkostenperiode(__tariefperiode.parse(entity.getAansluitkostenperiode()));
		info.setAantalmedewerkers(entity.getAantalmedewerkers());
		info.setAbonnementskosten(entity.getAbonnementskosten());
		info.setAbonnementskostenperiode(__tariefperiode.parse(entity.getAbonnementskostenperiode()));
		info.setBtwpercentagehoog(entity.getBtwpercentagehoog());
		info.setBtwpercentagelaag(entity.getBtwpercentagelaag());
		info.setFactuurnr(entity.getFactuurnr());
		info.setFactuurstatus(__factuurstatus.parse(entity.getFactuurstatus()));
		info.setJaar(entity.getJaar());
		info.setMaand(entity.getMaand());
		info.setMaandbedragsecretariaat(entity.getMaandbedragsecretariaat());
		info.setOmschrijvingfactuur(entity.getOmschrijvingfactuur());
		info.setPdflocation(entity.getPdflocation());
		info.setPeildatumaansluitkosten(entity.getPeildatumaansluitkosten());
		info.setWerkgeverid(entity.getWerkgeverid());
		info.setTariefid(entity.getTariefid());
		info.setHoldingid(entity.getHoldingid());
		
	}
	public FactuurInfo fromEntity(Factuur entity) {
		FactuurInfo info = null;
		if (entity == null)
			;
		else
		{
			info = create(FactuurInfo.class);
			setFactuurInfo(info, entity);
		}
		return info;
	}
	public Factuur toEntity(FactuurInfo info, Integer currentUser){
		Factuur entity = null;
		if (info == null)
			;
		else
		{
			entity = new Factuur();
			this.getVersionId(info, entity,currentUser);
			entity.setAanmaakdatum(info.getAanmaakdatum());
			entity.setAansluitkosten(info.getAansluitkosten());
			entity.setAansluitkostenperiode(info.getAansluitkostenperiode().getValue());
			entity.setAantalmedewerkers(info.getAantalmedewerkers());
			entity.setAbonnementskosten(info.getAbonnementskosten());
			entity.setAbonnementskostenperiode(info.getAbonnementskostenperiode().getValue());
			entity.setBtwpercentagehoog(info.getBtwpercentagehoog());
			entity.setBtwpercentagelaag(info.getBtwpercentagelaag());
			entity.setFactuurnr(info.getFactuurnr());
			entity.setFactuurstatus(info.getFactuurstatus().getValue());
			entity.setJaar(info.getJaar());
			entity.setMaand(info.getMaand());
			entity.setMaandbedragsecretariaat(info.getMaandbedragsecretariaat());
			entity.setOmschrijvingfactuur(info.getOmschrijvingfactuur());
			entity.setPdflocation(info.getPdflocation());
			entity.setPeildatumaansluitkosten(info.getPeildatumaansluitkosten());
			entity.setWerkgeverid(info.getWerkgeverid());
			entity.setTariefid(info.getTariefid());
			entity.setHoldingid(info.getHoldingid());
		}
		return entity;
	}
	public FactuurTotaalInfo fromEntity(FactuurTotaal entity) {
		FactuurTotaalInfo info = null;
		if (entity == null)
			;
		else
		{
			info = create(FactuurTotaalInfo.class);
			this.setVersionId(info, entity);
			info.setAanmaakdatum(entity.getAanmaakdatum());
			info.setAansluitkosten(entity.getAansluitkosten());
			info.setAansluitkostenperiode(__tariefperiode.parse(entity.getAansluitkostenperiode()));
			info.setAantalmedewerkers(entity.getAantalmedewerkers());
			info.setAbonnementskosten(entity.getAbonnementskosten());
			info.setAbonnementskostenperiode(__tariefperiode.parse(entity.getAbonnementskostenperiode()));
			info.setBtwpercentagehoog(entity.getBtwpercentagehoog());
			info.setBtwpercentagelaag(entity.getBtwpercentagelaag());
			info.setFactuurnr(entity.getFactuurnr());
			info.setFactuurstatus(__factuurstatus.parse(entity.getFactuurstatus()));
			info.setJaar(entity.getJaar());
			info.setMaand(entity.getMaand());
			info.setMaandbedragsecretariaat(entity.getMaandbedragsecretariaat());
			info.setOmschrijvingfactuur(entity.getOmschrijvingfactuur());
			info.setPdflocation(entity.getPdflocation());
			info.setPeildatumaansluitkosten(entity.getPeildatumaansluitkosten());
			info.setWerkgeverid(entity.getWerkgeverid());
			info.setTariefid(entity.getTariefid());
			info.setSomitembedrag(entity.getSomitembedrag());
			info.setSomitembtwbedraghoog(entity.getSomitembtwbedraghoog());
			info.setSomitembtwbedraglaag(entity.getSomitembtwbedraglaag());
			info.setSomcasemanagementkosten(entity.getSomcasemanagementkosten());
			info.setSomkilometerkosten(entity.getSomkilometerkosten());
			info.setSomoverigekostenbezoek(entity.getSomoverigekostenbezoek());
			info.setSomoverigekostensecretariaat(entity.getSomoverigekostensecretariaat());
			info.setSomuurkosten(entity.getSomuurkosten());
			info.setSomvastekosten(entity.getSomvastekosten());
			info.setVasttariefhuisbezoeken(inttobool(entity.getVasttariefhuisbezoeken()));
			info.setHoldingid(entity.getHoldingid());
		}
		return info;
	}
}
