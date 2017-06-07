package com.gieselaar.verzuimbeheer.utils;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;

import com.gieselaar.verzuimbeheer.entities.Documenttemplate;
import com.gieselaar.verzuimbeheer.entities.Todo;
import com.gieselaar.verzuimbeheer.entities.TodoFast;
import com.gieselaar.verzuimbeheer.entities.Verzuim;
import com.gieselaar.verzuimbeheer.entities.Verzuimactiviteit;
import com.gieselaar.verzuimbeheer.entities.Verzuimdocument;
import com.gieselaar.verzuimbeheer.entities.Verzuimherstel;
import com.gieselaar.verzuimbeheer.entities.Verzuimmedischekaart;
import com.gieselaar.verzuimbeheer.services.DocumentTemplateInfo;
import com.gieselaar.verzuimbeheer.services.TodoFastInfo;
import com.gieselaar.verzuimbeheer.services.TodoInfo;
import com.gieselaar.verzuimbeheer.services.TodoInfo.__soort;
import com.gieselaar.verzuimbeheer.services.VerzuimActiviteitInfo;
import com.gieselaar.verzuimbeheer.services.VerzuimDocumentInfo;
import com.gieselaar.verzuimbeheer.services.VerzuimHerstelInfo;
import com.gieselaar.verzuimbeheer.services.VerzuimInfo;
import com.gieselaar.verzuimbeheer.services.VerzuimInfo.__gerelateerdheid;
import com.gieselaar.verzuimbeheer.services.VerzuimInfo.__meldingswijze;
import com.gieselaar.verzuimbeheer.services.VerzuimInfo.__vangnettype;
import com.gieselaar.verzuimbeheer.services.VerzuimInfo.__verzuimtype;
import com.gieselaar.verzuimbeheer.services.VerzuimMedischekaartInfo;
import com.gieselaar.verzuimbeheer.services.InfoBase.persistenceaction;
import com.gieselaar.verzuimbeheer.services.InfoBase.persistencestate;

@Stateless
@LocalBean
public class VerzuimConversion extends BaseConversion {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public VerzuimInfo fromEntity(Verzuim entity){
		VerzuimInfo info = null;
		if (entity == null)
			;
		else
		{
			info = create(VerzuimInfo.class);
			this.setVersionId(info, entity);
			info.setCascode(entity.getCascode());
			info.setDienstverbandId(entity.getDienstverband_ID());
			info.setEinddatumverzuim(entity.getEinddatumverzuim());
			info.setGerelateerdheid(__gerelateerdheid.parse(entity.getGerelateerdheid()));
			info.setGebruiker(entity.getGebruiker());
			info.setKetenverzuim(inttobool(entity.getKetenverzuim()));
			info.setMeldingsdatum(entity.getMeldingsdatum());
			info.setMeldingswijze(__meldingswijze.parse(entity.getMeldingswijze()));
			info.setOpmerkingen(entity.getOpmerkingen());
			info.setVangnettype(__vangnettype.parse(entity.getVangnettype()));
			info.setVerzuimtype(__verzuimtype.parse(entity.getVerzuimtype()));
			info.setStartdatumverzuim(entity.getStartdatumverzuim());
			info.setUitkeringnaarwerknemer(inttobool(entity.getUitkeringnaarwerknemer()));
		}
		return info;
	}
	public Verzuim toEntity(VerzuimInfo info, Integer currentUser){
		Verzuim entity = null;
		if (info == null)
			;
		else
		{
			entity = new Verzuim();
			this.getVersionId(info, entity,currentUser);
			entity.setCascode(info.getCascode());
			entity.setDienstverband_ID(info.getDienstverbandId());
			entity.setEinddatumverzuim(info.getEinddatumverzuim());
			entity.setGerelateerdheid(info.getGerelateerdheid().getValue());
			entity.setGebruiker(info.getGebruiker());
			entity.setKetenverzuim(booltoint(info.getKetenverzuim()));
			entity.setMeldingsdatum(info.getMeldingsdatum());
			entity.setMeldingswijze(info.getMeldingswijze().getValue());
			entity.setOpmerkingen(info.getOpmerkingen());
			entity.setStartdatumverzuim(info.getStartdatumverzuim());
			entity.setVangnettype(info.getVangnettype().getValue());
			entity.setVerzuimtype(info.getVerzuimtype().getValue());
			entity.setUitkeringnaarwerknemer(booltoint(info.isUitkeringnaarwerknemer()));
		}
		return entity;
	}
	public VerzuimHerstelInfo fromEntity(Verzuimherstel entity){
		VerzuimHerstelInfo info = null;
		if (entity == null)
			;
		else
		{
			info = create(VerzuimHerstelInfo.class);
			this.setVersionId(info, entity);
			info.setDatumHerstel(entity.getDatumHerstel());
			info.setMeldingsdatum(entity.getMeldingsdatum());
			info.setMeldingswijze(__meldingswijze.parse(entity.getMeldingswijze()));
			info.setOpmerkingen(entity.getOpmerkingen());
			info.setPercentageHerstel(entity.getPercentageHerstel());
			info.setPercentageHerstelAT(entity.getPercentageHerstelAt());
			info.setVerzuimId(entity.getVerzuimId());
			info.setUser(entity.getUser());
		}
		return info;
	}
	public Verzuimherstel toEntity(VerzuimHerstelInfo info, Integer currentUser){
		Verzuimherstel entity = null;
		if (info == null)
			;
		else
		{
			entity = new Verzuimherstel();
			this.getVersionId(info, entity, currentUser);
			entity.setDatumHerstel(info.getDatumHerstel());
			entity.setMeldingsdatum(info.getMeldingsdatum());
			entity.setMeldingswijze(info.getMeldingswijze().getValue());
			entity.setOpmerkingen(info.getOpmerkingen());
			entity.setPercentageHerstel(info.getPercentageHerstel());
			entity.setPercentageHerstelAt(info.getPercentageHerstelAT());
			entity.setVerzuimId(info.getVerzuimId());
			entity.setUser(info.getUser());
		}
		return entity;
	}
	public VerzuimMedischekaartInfo fromEntity(Verzuimmedischekaart entity){
		VerzuimMedischekaartInfo info = null;
		if (entity == null)
			;
		else
		{
			info = create(VerzuimMedischekaartInfo.class);
			this.setVersionId(info, entity);
			info.setMedischekaart(entity.getMedischekaart());
			info.setOpenbaar(inttobool(entity.getOpenbaar()));
			info.setUser(entity.getUser());
			info.setVerzuimId(entity.getVerzuim_ID());
			info.setWijzigingsdatum(entity.getWijzigingsdatum());
		}
		return info;
	}
	public Verzuimmedischekaart toEntity(VerzuimMedischekaartInfo info, Integer currentUser){
		Verzuimmedischekaart entity = null;
		if (info == null)
			;
		else
		{
			entity = new Verzuimmedischekaart();
			this.getVersionId(info, entity, currentUser);
			entity.setMedischekaart(info.getMedischekaart());
			entity.setOpenbaar(booltoint(info.getOpenbaar()));
			entity.setUser(info.getUser());
			entity.setVerzuim_ID(info.getVerzuimId());
			entity.setWijzigingsdatum(info.getWijzigingsdatum());
		}
		return entity;
	}
	public VerzuimDocumentInfo fromEntity(Verzuimdocument entity){
		VerzuimDocumentInfo info = null;
		if (entity == null)
			;
		else
		{
			info = create(VerzuimDocumentInfo.class);
			this.setVersionId(info, entity);
			info.setAanmaakdatum(entity.getAanmaakdatum());
			info.setAanmaakuser(entity.getAanmaakuser());
			info.setDocumentnaam(entity.getDocumentnaam());
			info.setOmschrijving(entity.getOmschrijving());
			info.setPadnaam(entity.getPadnaam());
			info.setVerzuimId(entity.getVerzuimId());
		}
		return info;
	}
	public Verzuimdocument toEntity(VerzuimDocumentInfo info, Integer currentUser){
		Verzuimdocument entity = null;
		if (info == null)
			;
		else
		{
			entity = new Verzuimdocument();
			this.getVersionId(info, entity,currentUser);
			entity.setAanmaakdatum(info.getAanmaakdatum());
			entity.setAanmaakuser(info.getAanmaakuser());
			entity.setDocumentnaam(info.getDocumentnaam());
			entity.setOmschrijving(info.getOmschrijving());
			entity.setPadnaam(info.getPadnaam());
			entity.setVerzuimId(info.getVerzuimId());
		}
		return entity;
	}
	public VerzuimActiviteitInfo fromEntity(Verzuimactiviteit entity){
		VerzuimActiviteitInfo info = null;
		if (entity == null)
			;
		else
		{
			info = create(VerzuimActiviteitInfo.class);
			this.setVersionId(info, entity);
			info.setActiviteitId(entity.getActiviteit_ID());
			info.setDatumactiviteit(entity.getDatumactiviteit());
			info.setDatumdeadline(entity.getDatumdeadline());
			info.setOpmerkingen(entity.getOpmerkingen());
			info.setTijdbesteed(entity.getTijdbesteed());
			info.setUser(entity.getUser());
			info.setVerzuimId(entity.getVerzuim_ID());
		}
		return info;
	}
	public Verzuimactiviteit toEntity(VerzuimActiviteitInfo info, Integer currentUser){
		Verzuimactiviteit entity = null;
		if (info == null)
			;
		else
		{
			entity = new Verzuimactiviteit();
			this.getVersionId(info, entity,currentUser);
			entity.setActiviteit_ID(info.getActiviteitId());
			entity.setDatumactiviteit(info.getDatumactiviteit());
			entity.setDatumdeadline(info.getDatumdeadline());
			entity.setOpmerkingen(info.getOpmerkingen());
			entity.setTijdbesteed(info.getTijdbesteed());
			entity.setUser(info.getUser());
			entity.setVerzuim_ID(info.getVerzuimId());
		}
		return entity;
	}
	public TodoInfo fromEntity(Todo entity){
		TodoInfo info = null;
		if (entity == null)
			;
		else
		{
			info = create(TodoInfo.class);
			this.setVersionId(info, entity);
			info.setActiviteitId(entity.getActiviteit_ID());
			info.setAanmaakdatum(entity.getAanmaakdatum());
			info.setVerzuimId(entity.getVerzuim_ID());
			info.setVerzuimactiviteitId(entity.getVerzuimactiviteit_ID());
			info.setUser(entity.getUser());
			info.setDeadlinedatum(entity.getDeadlinedatum());
			info.setWaarschuwingsdatum(entity.getWaarschuwingsdatum());
			info.setHerhalen(inttobool(entity.getHerhalen()));
			info.setOpmerking(entity.getOpmerking());
			info.setSoort(__soort.parse(entity.getSoort()));
		}
		return info;
	}
	public TodoFastInfo fromEntity(TodoFast entity){
		TodoFastInfo info = null;
		if (entity == null){
			/*skip*/
		}else{
			info = create(TodoFastInfo.class);
			info.setState(persistencestate.EXISTS);
			info.setAction(persistenceaction.UPDATE);
			info.setId(entity.getId());
			info.setVersion(entity.getVersion());
			info.setActiviteitId(entity.getActiviteit_ID());
			info.setAanmaakdatum(entity.getAanmaakdatum());
			info.setVerzuimId(entity.getVerzuim_ID());
			info.setVerzuimactiviteitId(entity.getVerzuimactiviteit_ID());
			info.setUser(entity.getUser());
			info.setDeadlinedatum(entity.getDeadlinedatum());
			info.setWaarschuwingsdatum(entity.getWaarschuwingsdatum());
			info.setHerhalen(entity.getHerhalen() == 0 ? false : true);
			info.setOpmerking(entity.getOpmerking());
			info.setSoort(__soort.parse(entity.getSoort()));
			info.setStartdatumverzuim(entity.getStartdatumverzuim());
			info.setAchternaam(entity.getAchternaam());
			info.setBurgerservicenummer(entity.getBurgerservicenummer());
			info.setWerkgevernaam(entity.getWerkgevernaam());
			info.setWerknemerid(entity.getWerknemerid());
		}
		return info;
	}
	public Todo toEntity(TodoInfo info, Integer currentUser){
		Todo entity = null;
		if (info == null)
			;
		else
		{
			entity = new Todo();
			this.getVersionId(info, entity, currentUser);
			entity.setActiviteit_ID(info.getActiviteitId());
			entity.setAanmaakdatum(info.getAanmaakdatum());
			entity.setVerzuim_ID(info.getVerzuimId());
			entity.setVerzuimactiviteit_ID(info.getVerzuimactiviteitId());
			entity.setUser(info.getUser());
			entity.setDeadlinedatum(info.getDeadlinedatum());
			entity.setWaarschuwingsdatum(info.getWaarschuwingsdatum());
			entity.setHerhalen(booltoint(info.getHerhalen()));
			entity.setOpmerking(info.getOpmerking());
			entity.setSoort(info.getSoort().getValue());
		}
		return entity;
	}
	public DocumentTemplateInfo fromEntity(Documenttemplate entity){
		DocumentTemplateInfo info = null;
		if (entity == null)
			;
		else{
			info = new DocumentTemplateInfo();
			this.setVersionId(info, entity);
			info.setNaam(entity.getNaam());
			info.setOmschrijving(entity.getOmschrijving());
			info.setPadnaam(entity.getPadnaam());
		}
			
		return info;
	}
	public Documenttemplate toEntity(DocumentTemplateInfo info, Integer currentUser){
		Documenttemplate entity = null;
		if (info == null)
			;
		else{
			entity = new Documenttemplate();
			this.getVersionId(info, entity,currentUser);
			entity.setNaam(info.getNaam());
			entity.setOmschrijving(info.getOmschrijving());
			entity.setPadnaam(info.getPadnaam());
		}
		return entity;
	}
	
}
