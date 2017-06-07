package com.gieselaar.verzuimbeheer.entities;

import java.io.Serializable;

import javax.persistence.*;

import com.gieselaar.verzuimbeheer.entitiesutils.EntityBaseId;


/**
 * The persistent class for the AFDELING database table.
 * 
 */
@Entity
public class Afdeling extends EntityBaseId implements Serializable {
	private static final long serialVersionUID = 1L;

	private String afdelingsid;

	private String naam;

	//uni-directional many-to-one association to Contactpersoon
	@OneToOne(cascade={CascadeType.ALL},orphanRemoval=true)
	@JoinColumn(name="contactpersoon_ID")
	private Contactpersoon contactpersoon;

	//uni-directional many-to-one association to Werkgever
    @ManyToOne
	@JoinColumn(name="werkgever_id")
	private Werkgever werkgever;

    public Afdeling() {
    }

	public String getAfdelingsid() {
		return this.afdelingsid;
	}

	public void setAfdelingsid(String afdelingsid) {
		this.afdelingsid = afdelingsid;
	}

	public String getNaam() {
		return this.naam;
	}

	public void setNaam(String naam) {
		this.naam = naam;
	}

	public Contactpersoon getContactpersoon() {
		return this.contactpersoon;
	}

	public void setContactpersoon(Contactpersoon contactpersoon) {
		this.contactpersoon = contactpersoon;
	}
	
	public Werkgever getWerkgever() {
		return this.werkgever;
	}

	public void setWerkgever(Werkgever werkgever) {
		this.werkgever = werkgever;
	}
	
}