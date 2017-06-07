package com.gieselaar.verzuimbeheer.verzuimweb;

import java.io.Serializable;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import com.gieselaar.verzuimbeheer.services.AdresInfo;
 
@ManagedBean(name="adres")
@ViewScoped
public class adresClass extends BackingBeanBase implements Serializable{
	 
	 
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public String straat;
	public String huisnummer;
	public String huisnummertoevoeging;
	public String postcode;
	public String plaats;
	public String land;
	public AdresInfo adres;

	public String getStraat() {
		return adres.getStraat();
	}

	public void setStraat(String straat) {
		this.straat = straat;
	}

	public String getHuisnummer() {
		return adres.getHuisnummer();
	}

	public void setHuisnummer(String huisnummer) {
		this.huisnummer = huisnummer;
	}

	public String getHuisnummertoevoeging() {
		return huisnummertoevoeging;
	}

	public void setHuisnummertoevoeging(String huisnummertoevoeging) {
		this.huisnummertoevoeging = huisnummertoevoeging;
	}

	public String getPostcode() {
		return postcode;
	}

	public void setPostcode(String postcode) {
		this.postcode = postcode;
	}

	public String getPlaats() {
		return plaats;
	}

	public void setPlaats(String plaats) {
		this.plaats = plaats;
	}

	public String getLand() {
		return land;
	}

	public void setLand(String land) {
		this.land = land;
	}
 
	public AdresInfo getAdres() {
		if (this.adres.isEmtpy())
			return null;
		else
			return adres;
	}

	public void setAdres(AdresInfo adres) {
		System.out.println("setAdres() called");
		if (adres == null)
			this.adres = new AdresInfo();
		this.adres = adres;
	}

	public String registerAction(){
		return "result";
	}
}
