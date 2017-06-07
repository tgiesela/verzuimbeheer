package com.gieselaar.verzuimbeheer.utils;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;

import com.gieselaar.verzuimbeheer.entities.Activiteit;
import com.gieselaar.verzuimbeheer.entities.Pakket;
import com.gieselaar.verzuimbeheer.services.ActiviteitInfo;
import com.gieselaar.verzuimbeheer.services.ActiviteitInfo.__periodesoort;
import com.gieselaar.verzuimbeheer.services.ActiviteitInfo.__meldingsoort;
import com.gieselaar.verzuimbeheer.services.VerzuimInfo.__vangnettype;
import com.gieselaar.verzuimbeheer.services.PakketInfo;

@Stateless
@LocalBean
public class PakketConversion extends BaseConversion {
	public PakketInfo fromEntity(Pakket entity) {
		PakketInfo info = null;
		List <ActiviteitInfo> actinfo;
		if (entity == null)
			;
		else
		{
			info = create(PakketInfo.class);
			this.setVersionId(info, entity);
			info.setNaam(entity.getNaam());
			info.setOmschrijving(entity.getOmschrijving());
			actinfo = new ArrayList<ActiviteitInfo>();
			for (Activiteit a: entity.getActiviteits())
				actinfo.add(fromEntity(a));
			info.setActiviteiten(actinfo);
		}
		return info;
	}
	public Pakket toEntity(PakketInfo info, Integer currentUser){
		Pakket entity = null;
		List<Activiteit> act = new ArrayList<Activiteit>();
		if (info == null)
			;
		else
		{
			entity = new Pakket();
			this.getVersionId(info, entity, currentUser);
			if (info.getAktiviteiten() != null)
				for (ActiviteitInfo a: info.getAktiviteiten())
					act.add(toEntity(a, currentUser));
			entity.setActiviteits(act);
			entity.setNaam(info.getNaam());
			entity.setOmschrijving(info.getOmschrijving());
		}
		return entity;
	}
	public ActiviteitInfo fromEntity(Activiteit entity) {
		ActiviteitInfo info = null;
		if (entity == null)
			;
		else
		{
			info = create(ActiviteitInfo.class);
			this.setVersionId(info, entity);
			info.setDeadlineperiode(entity.getDeadlineperiode());
			info.setDeadlineperiodesoort(__periodesoort.parse(entity.getDeadlineperiodesoort()));
			info.setDeadlinestartpunt(__meldingsoort.parse(entity.getDeadlinestartpunt()));
			info.setDeadlinewaarschuwmoment(entity.getDeadlinewaarschuwmoment());
			info.setDeadlinewaarschuwmomentsoort(__periodesoort.parse(entity.getDeadlinewaarschuwmomentsoort()));
			info.setDuur(entity.getDuur());
			info.setKetenverzuim(inttobool(entity.getKetenverzuim()));
			info.setNaam(entity.getNaam());
			info.setNormaalverzuim(inttobool(entity.getNormaalverzuim()));
			info.setPlannaactiviteit(entity.getPlannaactiviteit());
			info.setRepeteeraantal(entity.getRepeteeraantal());
			info.setRepeteerperiode(entity.getRepeteerperiode());
			info.setRepeteerperiodesoort(__periodesoort.parse(entity.getRepeteerperiodesoort()));
			info.setVangnet(inttobool(entity.getVangnet()));
			info.setVangnettype(__vangnettype.parse(entity.getVangnettype()));
			info.setOmschrijving(entity.getOmschrijving());
			info.setVerwijdernaherstel(inttobool(entity.getVerwijdernaherstel()));
		}
		return info;
	}
	public Activiteit toEntity(ActiviteitInfo info, Integer currentUser){
		Activiteit entity = null;
		if (info == null)
			;
		else
		{
			entity = new Activiteit();
			this.getVersionId(info, entity, currentUser);
			entity.setDeadlineperiode(info.getDeadlineperiode());
			entity.setDeadlineperiodesoort(info.getDeadlineperiodesoort().getValue());
			entity.setDeadlinestartpunt(info.getDeadlinestartpunt().getValue());
			entity.setDeadlinewaarschuwmoment(info.getDeadlinewaarschuwmoment());
			entity.setDeadlinewaarschuwmomentsoort(info.getDeadlinewaarschuwmomentsoort().getValue());
			entity.setDuur(info.getDuur());
			entity.setKetenverzuim(booltoint(info.isKetenverzuim()));
			entity.setNaam(info.getNaam());
			entity.setNormaalverzuim(booltoint(info.isNormaalverzuim()));
			entity.setPlannaactiviteit(info.getPlannaactiviteit());
			entity.setRepeteeraantal(info.getRepeteeraantal());
			entity.setRepeteerperiode(info.getRepeteerperiode());
			entity.setRepeteerperiodesoort(info.getRepeteerperiodesoort().getValue());
			entity.setVangnet(booltoint(info.isVangnet()));
			if (info.getVangnettype() != null)
				entity.setVangnettype(info.getVangnettype().getValue());
			else
				entity.setVangnettype(null);
			entity.setOmschrijving(info.getOmschrijving());
			entity.setVerwijdernaherstel(booltoint(info.isVerwijdernaherstel()));
		}
		return entity;
	}

}
