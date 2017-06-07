package com.gieselaar.verzuimbeheer.entities;

import java.io.Serializable;

import javax.persistence.*;

import com.gieselaar.verzuimbeheer.entitiesutils.EntityBaseId;

import java.util.List;


/**
 * The persistent class for the PAKKET database table.
 * 
 */
@Entity
public class Pakket extends EntityBaseId implements Serializable {
	private static final long serialVersionUID = 1L;

	private String naam;

	private String omschrijving;

	//uni-directional many-to-many association to Activiteit
	@ManyToMany(fetch=FetchType.EAGER)
	@JoinTable(
		name="PAKKET_HAS_ACTIVITEIT"
		, joinColumns={
			@JoinColumn(name="pakket_ID")
			}
		, inverseJoinColumns={
			@JoinColumn(name="activiteit_ID")
			}
		)
	private List<Activiteit> activiteits;

	//bi-directional many-to-many association to Werkgever
//	@ManyToMany(mappedBy="pakkets", fetch=FetchType.EAGER)
//	private List<Werkgever> werkgevers;

    public Pakket() {
    }

	public String getNaam() {
		return this.naam;
	}

	public void setNaam(String naam) {
		this.naam = naam;
	}

	public String getOmschrijving() {
		return this.omschrijving;
	}

	public void setOmschrijving(String omschrijving) {
		this.omschrijving = omschrijving;
	}

	public List<Activiteit> getActiviteits() {
		return this.activiteits;
	}

	public void setActiviteits(List<Activiteit> activiteits) {
		this.activiteits = activiteits;
	}
	/*
	public List<WerkgeverX> getWerkgevers() {
		return this.werkgevers;
	}

	public void setWerkgevers(List<WerkgeverX> werkgevers) {
		this.werkgevers = werkgevers;
	}
	*/
}