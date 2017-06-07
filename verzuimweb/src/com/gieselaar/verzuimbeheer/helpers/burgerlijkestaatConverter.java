package com.gieselaar.verzuimbeheer.helpers;

import javax.faces.convert.EnumConverter;

import com.gieselaar.verzuimbeheer.services.InfoBase.__burgerlijkestaat;

public class burgerlijkestaatConverter extends EnumConverter{
	public burgerlijkestaatConverter(){
		super(__burgerlijkestaat.class);
	}
}
