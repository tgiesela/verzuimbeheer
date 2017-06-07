package com.gieselaar.verzuimbeheer.helpers;

import javax.faces.convert.EnumConverter;

import com.gieselaar.verzuimbeheer.services.VerzuimInfo.__verzuimtype;

public class verzuimTypeConverter extends EnumConverter{
	public verzuimTypeConverter(){
		super(__verzuimtype.class);
	}
}
