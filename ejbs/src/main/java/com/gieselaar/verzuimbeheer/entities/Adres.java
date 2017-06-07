package com.gieselaar.verzuimbeheer.entities;

import java.io.Serializable;

import javax.persistence.*;

import com.gieselaar.verzuimbeheer.entitiesutils.EntityBaseId;


/**
 * The persistent class for the ADRES database table.
 * 
 */
@Entity
public class Adres extends EntityBaseId implements Serializable {
	private static final long serialVersionUID = 1L;

	private String huisnummer;

	private String huisnummertoevoeging;

	private String land;

	private String plaats;

	private String postcode;

	private String straat;

    public Adres() {
    }

	public String getHuisnummer() {
		return this.huisnummer;
	}

	public void setHuisnummer(String huisnummer) {
		this.huisnummer = huisnummer;
	}

	public String getHuisnummertoevoeging() {
		return this.huisnummertoevoeging;
	}

	public void setHuisnummertoevoeging(String huisnummertoevoeging) {
		this.huisnummertoevoeging = huisnummertoevoeging;
	}

	public String getLand() {
		return this.land;
	}

	public void setLand(String land) {
		this.land = land;
	}

	public String getPlaats() {
		return this.plaats;
	}

	public void setPlaats(String plaats) {
		this.plaats = plaats;
	}

	public String getPostcode() {
		return this.postcode;
	}

	public void setPostcode(String postcode) {
		this.postcode = postcode;
	}

	public String getStraat() {
		return this.straat;
	}

	public void setStraat(String straat) {
		this.straat = straat;
	}

}