package com.gieselaar.verzuimbeheer.utils;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;

import com.gieselaar.verzuimbeheer.entities.Afdeling;
import com.gieselaar.verzuimbeheer.entities.Holding;
import com.gieselaar.verzuimbeheer.entities.Oe;
import com.gieselaar.verzuimbeheer.entities.Oeniveau;
import com.gieselaar.verzuimbeheer.entities.Werkgever;
import com.gieselaar.verzuimbeheer.services.AfdelingInfo;
import com.gieselaar.verzuimbeheer.services.HoldingInfo;
import com.gieselaar.verzuimbeheer.services.HoldingInfo.__factuurtype;
import com.gieselaar.verzuimbeheer.services.OeInfo;
import com.gieselaar.verzuimbeheer.services.OeNiveauInfo;
import com.gieselaar.verzuimbeheer.services.WerkgeverInfo;

@Stateless
@LocalBean
public class WerkgeverConversion extends BaseConversion {
	private static final long serialVersionUID = 1L;
	private ContactpersoonConversion contactpersoonconverter = new ContactpersoonConversion();
	private InstantieConversion instantieconverter = new InstantieConversion();
	private AdresConversion adresconverter = new AdresConversion();
	public WerkgeverInfo fromEntity(Werkgever entity) {
		WerkgeverInfo info = null;
		if (entity == null)
			;
		else
		{
			info = create(WerkgeverInfo.class);
			this.setVersionId(info, entity);
			info.setNaam(entity.getNaam());
			info.setEinddatumcontract(entity.getEinddatumcontract());
			info.setStartdatumcontract(entity.getStartdatumcontract());
			info.setTelefoon(entity.getTelefoon());
			info.setPostAdres(adresconverter.fromEntity(entity.getPostAdres()));
			info.setVestigingsAdres(adresconverter.fromEntity(entity.getVestigingsAdres()));
			info.setFactuurAdres(adresconverter.fromEntity(entity.getFactuurAdres()));
			info.setContactpersoon(contactpersoonconverter.fromEntity(entity.getContactPersoon()));
			info.setPakketten(null);
			info.setAfdelings(null);
			info.setHolding(null);
			info.setArbodienst(null);
			info.setUwv(null);
			info.setHoldingId(entity.getHolding_ID());
			info.setArbodienstId(entity.getArbodienst_ID());
			info.setUwvId(entity.getUitvoeringsinstituut_ID());
			info.setBedrijfsartsid(entity.getBedrijfsartsid());
			info.setWerkweek(entity.getWerkweek());
			info.setBtwnummer(entity.getBTWnummer());
			info.setDetailsecretariaat(inttobool(entity.getDetailsecretariaat()));
			info.setDebiteurnummer(entity.getDebiteurnummer());
			info.setFactureren(inttobool(entity.getFactureren()));
			info.setContactpersoonfactuur(contactpersoonconverter.fromEntity(entity.getContactpersoonfactuur()));
			info.setEmailadresfactuur(entity.getEmailadresfactuur());
			info.setExterncontractnummer(entity.getExterncontractnummer());
		}
		return info;
	}
	public Werkgever toEntity(WerkgeverInfo info, Integer currentUser){
		Werkgever entity = null;
		if (info == null)
			;
		else
		{
			entity = new Werkgever();
			this.getVersionId(info, entity,currentUser);
			entity.setFactuurAdres(adresconverter.toEntity(info.getFactuurAdres(),currentUser));
			entity.setPostAdres(adresconverter.toEntity(info.getPostAdres(),currentUser));
			entity.setVestigingsAdres(adresconverter.toEntity(info.getVestigingsAdres(),currentUser));
			entity.setContactPersoon(contactpersoonconverter.toEntity(info.getContactpersoon(),currentUser));
			if (info.getArbodienst() == null){
				if (info.getArbodienstId() == null){
					entity.setArbodienst_ID(null);
				}else{
					entity.setArbodienst_ID(info.getArbodienstId());
				}
			}else
				entity.setArbodienst_ID(info.getArbodienst().getId());
			entity.setUitvoeringsinstituut_ID(info.getUwvId());
			entity.setBedrijfsartsid(info.getBedrijfsartsid());
			entity.setEinddatumcontract(info.getEinddatumcontract());
			entity.setNaam(info.getNaam());
			entity.setStartdatumcontract(info.getStartdatumcontract());
			entity.setTelefoon(info.getTelefoon());
			entity.setHolding_ID(info.getHoldingId());
			entity.setWerkweek(info.getWerkweek());
			entity.setBTWnummer(info.getBtwnummer());
			entity.setDetailsecretariaat(booltoint(info.getDetailsecretariaat()));
			entity.setDebiteurnummer(info.getDebiteurnummer());
			entity.setFactureren(booltoint(info.getFactureren()));
			entity.setContactpersoonfactuur(contactpersoonconverter.toEntity(info.getContactpersoonfactuur(),currentUser));
			entity.setEmailadresfactuur(info.getEmailadresfactuur());
			entity.setExterncontractnummer(info.getExterncontractnummer());
		}
		return entity;
	}
	public AfdelingInfo fromEntity(Afdeling entity) {
		AfdelingInfo info = null;
		if (entity == null)
			;
		else
		{
			info = create(AfdelingInfo.class);
			this.setVersionId(info, entity);
			info.setAfdelingsid(entity.getAfdelingsid());
			info.setNaam(entity.getNaam());
			info.setWerkgeverId((Integer)entity.getWerkgever().getId());
			info.setContactpersoon(contactpersoonconverter.fromEntity(entity.getContactpersoon()));
		}
		return info;
	}
	public Afdeling toEntity(AfdelingInfo info, Integer currentUser){
		Afdeling entity = null;
		if (info == null)
			;
		else
		{
			entity = new Afdeling();
			this.getVersionId(info, entity,currentUser);
			entity.setAfdelingsid(info.getAfdelingsid());
			entity.setNaam(info.getNaam());
			entity.setWerkgever(new Werkgever());
			entity.getWerkgever().setId(info.getWerkgeverId());
			if (info.getContactpersoon() == null)
				entity.setContactpersoon(null);
			else
				if (info.getContactpersoon().isEmpty())
					entity.setContactpersoon(null);
				else
					entity.setContactpersoon(contactpersoonconverter.toEntity(info.getContactpersoon(),currentUser));
		}
		return entity;
	}
	public HoldingInfo fromEntity(Holding entity) {
		HoldingInfo info = null;
		if (entity == null)
			;
		else
		{
			info = create(HoldingInfo.class);
			this.setVersionId(info, entity);
			info.setNaam(entity.getNaam());
			info.setPostAdres(adresconverter.fromEntity(entity.getPostadres()));
			info.setVestigingsAdres(adresconverter.fromEntity(entity.getVestigingsadres()));
			info.setFactuurAdres(adresconverter.fromEntity(entity.getFactuurAdres()));
			info.setContactpersoon(contactpersoonconverter.fromEntity(entity.getContactpersoon()));
			info.setFactureren(inttobool(entity.getFactureren()));
			info.setDetailsecretariaat(inttobool(entity.getDetailsecretariaat()));
			info.setBtwnummer(entity.getBtwnummer());
			info.setDebiteurnummer(entity.getDebiteurnummer());
			info.setEmailadresfactuur(entity.getEmailadresfactuur());
			info.setTelefoonnummer(entity.getTelefoon());
			info.setFactuurtype(__factuurtype.parse(entity.getFactuurtype()));
			info.setContactpersoonfactuur(contactpersoonconverter.fromEntity(entity.getContactpersoonfactuur()));
			info.setStartdatumcontract(entity.getStartdatumcontract());
			info.setEinddatumcontract(entity.getEinddatumcontract());
		}
		return info;
	}
	public Holding toEntity(HoldingInfo info, Integer currentUser){
		Holding entity = null;
		if (info == null)
			;
		else
		{
			entity = new Holding();
			this.getVersionId(info, entity,currentUser);
			entity.setNaam(info.getNaam());
			entity.setPostadres(adresconverter.toEntity(info.getPostAdres(),currentUser));
			entity.setVestigingsadres(adresconverter.toEntity(info.getVestigingsAdres(),currentUser));
			entity.setFactuurAdres(adresconverter.toEntity(info.getFactuurAdres(), currentUser));
			entity.setContactpersoon(contactpersoonconverter.toEntity(info.getContactpersoon(),currentUser));
			entity.setFactureren(booltoint(info.isFactureren()));
			entity.setDetailsecretariaat(booltoint(info.isDetailsecretariaat()));
			entity.setBtwnummer(info.getBtwnummer());
			entity.setDebiteurnummer(info.getDebiteurnummer());
			entity.setEmailadresfactuur(info.getEmailadresfactuur());
			entity.setTelefoon(info.getTelefoonnummer());
			if (info.getFactuurtype() == null){
				entity.setFactuurtype(__factuurtype.NVT.getValue());
			}else{
				entity.setFactuurtype(info.getFactuurtype().getValue());
			}
			entity.setContactpersoonfactuur(contactpersoonconverter.toEntity(info.getContactpersoonfactuur(),currentUser));
			entity.setStartdatumcontract(info.getStartdatumcontract());
			entity.setEinddatumcontract(info.getEinddatumcontract());
		}
		return entity;
	}
	public OeInfo fromEntity(Oe entity){
		OeInfo info = null;
		if (entity == null)
			;
		else
		{
			info = create(OeInfo.class);
			this.setVersionId(info, entity);
			info.setNaam(entity.getNaam());
			info.setParentoeId(entity.getParentoeId());
			info.setWerkgeverId(entity.getWerkgeverId());
			info.setOeniveau(this.fromEntity(entity.getOeniveau()));
		}
		return info;
	}
	public Oe toEntity(OeInfo info, Integer currentUser){
		Oe entity = null;
		if (info == null)
			;
		else
		{
			entity = new Oe();
			this.getVersionId(info, entity,currentUser);
			entity.setNaam(info.getNaam());
			entity.setOeniveau(this.toEntity(info.getOeniveau(),currentUser));
			entity.setOeniveauId(info.getOeniveau().getId());
			entity.setParentoeId(info.getParentoeId());
			entity.setWerkgeverId(info.getWerkgeverId());
		}
		return entity;
		
	}
	public OeNiveauInfo fromEntity(Oeniveau entity){
		OeNiveauInfo info = null;
		if (entity == null)
			;
		else
		{
			info = create(OeNiveauInfo.class);
			this.setVersionId(info, entity);
			info.setNaam(entity.getNaam());
			info.setOeniveau(entity.getOeniveau());
			info.setParentoeniveauId(entity.getParentoeniveauId());
		}
		return info;
	}
	public Oeniveau toEntity(OeNiveauInfo info, Integer currentUser){
		Oeniveau entity = null;
		if (info == null)
			;
		else
		{
			entity = new Oeniveau();
			this.getVersionId(info, entity,currentUser);
			entity.setNaam(info.getNaam());
			entity.setOeniveau(info.getOeniveau());
			entity.setParentoeniveauId(info.getParentoeniveauId());
		}
		return entity;
		
	}
}
