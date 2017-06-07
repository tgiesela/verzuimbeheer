package com.gieselaar.verzuimbeheer.helpers;

import javax.faces.convert.EnumConverter;

import com.gieselaar.verzuimbeheer.services.VerzuimInfo.__vangnettype;

public class vangnetTypeConverter extends EnumConverter{
	public vangnetTypeConverter(){
		super(__vangnettype.class);
	}
}
