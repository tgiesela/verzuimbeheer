package com.gieselaar.verzuimbeheer.utils;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;

import com.gieselaar.verzuimbeheer.entities.Settings;
import com.gieselaar.verzuimbeheer.services.SettingsInfo;

@Stateless
@LocalBean
public class SettingsConversion extends BaseConversion {
	public SettingsInfo fromEntity(Settings entity) {
		SettingsInfo info = null;
		if (entity == null)
			;
		else
		{
			info = create(SettingsInfo.class);
			this.setVersionId(info, entity);
			info.setFactuurfolder(entity.getFactuurfolder());
			info.setFactuurmailtextbody(entity.getFactuurmailtextbody());
			info.setSmtpmailfromaddress(entity.getSmtpmailfromaddress());
			info.setSmtpmailhost(entity.getSmtpmailhost());
			info.setSmtpmailpassword(entity.getSmtpmailpassword());
			info.setSmtpmailuser(entity.getSmtpmailuser());
			info.setTodoforinformatiekaart(entity.getTodoforinformatiekaart());
			info.setBccemailaddress(entity.getBccemailaddress());
			info.setTodoforafsluitendienstverband(entity.getTodoforafsluitendienstverband());
			info.setServershare(entity.getServershare());
		}
		return info;
	}
	public Settings toEntity(SettingsInfo info, Integer currentUser){
		Settings entity = null;
		if (info != null){
			entity = new Settings();
			this.getVersionId(info, entity, currentUser);
			entity.setFactuurfolder(info.getFactuurfolder());
			entity.setFactuurmailtextbody(info.getFactuurmailtextbody());
			entity.setSmtpmailfromaddress(info.getSmtpmailfromaddress());
			entity.setSmtpmailhost(info.getSmtpmailhost());
			entity.setSmtpmailpassword(info.getSmtpmailpassword());
			entity.setSmtpmailuser(info.getSmtpmailuser());
			entity.setTodoforinformatiekaart(info.getTodoforinformatiekaart());
			entity.setBccemailaddress(info.getBccemailaddress());
			entity.setTodoforafsluitendienstverband(info.getTodoforafsluitendienstverband());
			entity.setServershare(info.getServershare());
		}
		return entity;
	}
}
