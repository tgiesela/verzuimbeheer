package com.gieselaar.verzuimbeheer.helpers;

import javax.faces.bean.RequestScoped;
import javax.faces.convert.EnumConverter;
import javax.faces.convert.FacesConverter;

import com.gieselaar.verzuimbeheer.services.InfoBase.__geslacht;

@RequestScoped
@FacesConverter(value = "geslachtConverter")
public class geslachtConverter extends EnumConverter{
	public geslachtConverter(){
		super(__geslacht.class);
	}
}
