package com.gieselaar.verzuimbeheer.helpers;

import com.gieselaar.verzuimbeheer.services.VerzuimInfo;

public class VerzuimInfoWeb {

	private String cascodeOmschrijving;
	private VerzuimInfo verzuim;
	public VerzuimInfoWeb(VerzuimInfo vzm){
		super();
		verzuim = vzm;
	}
	public String getCascodeOmschrijving() {
		return cascodeOmschrijving;
	}
	public void setCascodeOmschrijving(String omschrijving) {
		this.cascodeOmschrijving = omschrijving;
	}
	public VerzuimInfo getVerzuim() {
		return verzuim;
	}
	public void setVerzuim(VerzuimInfo verzuim) {
		this.verzuim = verzuim;
	}
}
