package com.gieselaar.verzuimbeheer.entities;

import java.io.Serializable;

import javax.persistence.*;

import com.gieselaar.verzuimbeheer.entitiesutils.EntityBaseId;


/**
 * The persistent class for the UITVOERINGSINSTITUUT database table.
 * 
 */
@Entity
public class Uitvoeringsinstituut extends EntityBaseId implements Serializable {
	private static final long serialVersionUID = 1L;

	private String naam;
	private String telefoonnummer;

	@OneToOne(cascade={CascadeType.ALL},orphanRemoval=true)
	@JoinColumn(name="postadres_ID")
	private Adres postadres;

	@OneToOne(cascade={CascadeType.ALL},orphanRemoval=true)
	@JoinColumn(name="vestigingsadres_ID")
	private Adres vestigingsadres;

    public Uitvoeringsinstituut() {
    }

	public String getNaam() {
		return this.naam;
	}

	public void setNaam(String naam) {
		this.naam = naam;
	}

	public String getTelefoonnummer() {
		return this.telefoonnummer;
	}

	public void setTelefoonnummer(String telefoonnummer) {
		this.telefoonnummer = telefoonnummer;
	}

	public Adres getPostadres() {
		return postadres;
	}

	public void setPostadres(Adres postadres) {
		this.postadres = postadres;
	}

	public Adres getVestigingsadres() {
		return vestigingsadres;
	}

	public void setVestigingsadres(Adres vestigingsadres) {
		this.vestigingsadres = vestigingsadres;
	}

}