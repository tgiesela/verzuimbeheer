package com.gieselaar.verzuimbeheer.entities;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.*;

import com.gieselaar.verzuimbeheer.entitiesutils.EntityBaseId;


/**
 * The persistent class for the HOLDING database table.
 * 
 */
@Entity
public class Holding extends EntityBaseId implements Serializable {
	private static final long serialVersionUID = 1L;

	private String telefoon;
	private String naam;

	@OneToOne(cascade={CascadeType.ALL},orphanRemoval=true)
	@JoinColumn(name="contactpersoon_ID")
	private Contactpersoon contactpersoon;

	@OneToOne(cascade={CascadeType.ALL},orphanRemoval=true)
	@JoinColumn(name="postadres_ID")
	private Adres postadres;

	@OneToOne(cascade={CascadeType.ALL},orphanRemoval=true)
	@JoinColumn(name="vestigingsadres_ID")
	private Adres vestigingsadres;

	@OneToOne(cascade={CascadeType.ALL},orphanRemoval=true)
	@JoinColumn(name="factuuradres_ID")
	private Adres factuurAdres;
	
	@OneToOne(cascade={CascadeType.ALL},orphanRemoval=true)
	@JoinColumn(name="contactpersoonfactuur_ID")
	private Contactpersoon contactpersoonfactuur;

	private String btwnummer;
	private Integer debiteurnummer;
	private int factureren;
	private int detailsecretariaat;
	private String emailadresfactuur;
	private Integer factuurtype;
    @Temporal( TemporalType.DATE)
	private Date startdatumcontract;
    @Temporal( TemporalType.DATE)
	private Date einddatumcontract;

    public Holding() {
    }

	public String getNaam() {
		return this.naam;
	}

	public void setNaam(String naam) {
		this.naam = naam;
	}

	public String getTelefoon() {
		return this.telefoon;
	}

	public void setTelefoon(String telefoon) {
		this.telefoon = telefoon;
	}

	public Contactpersoon getContactpersoon() {
		return contactpersoon;
	}

	public void setContactpersoon(Contactpersoon contactpersoon) {
		this.contactpersoon = contactpersoon;
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

	public Adres getFactuurAdres() {
		return factuurAdres;
	}

	public void setFactuurAdres(Adres factuurAdres) {
		this.factuurAdres = factuurAdres;
	}

	public String getBtwnummer() {
		return btwnummer;
	}

	public void setBtwnummer(String btwnummer) {
		this.btwnummer = btwnummer;
	}

	public Integer getDebiteurnummer() {
		return debiteurnummer;
	}

	public void setDebiteurnummer(Integer debiteurnummer) {
		this.debiteurnummer = debiteurnummer;
	}

	public String getEmailadresfactuur() {
		return emailadresfactuur;
	}

	public int getFactureren() {
		return factureren;
	}

	public void setFactureren(int factureren) {
		this.factureren = factureren;
	}

	public int getDetailsecretariaat() {
		return detailsecretariaat;
	}

	public void setDetailsecretariaat(int detailsecretariaat) {
		this.detailsecretariaat = detailsecretariaat;
	}

	public void setEmailadresfactuur(String emailadresfactuur) {
		this.emailadresfactuur = emailadresfactuur;
	}

	public Integer getFactuurtype() {
		return factuurtype;
	}

	public void setFactuurtype(Integer factuurtype) {
		this.factuurtype = factuurtype;
	}

	public Contactpersoon getContactpersoonfactuur() {
		return contactpersoonfactuur;
	}

	public void setContactpersoonfactuur(Contactpersoon contactpersoon) {
		this.contactpersoonfactuur = contactpersoon;
	}

	public Date getStartdatumcontract() {
		return startdatumcontract;
	}

	public void setStartdatumcontract(Date startdatumcontract) {
		this.startdatumcontract = startdatumcontract;
	}

	public Date getEinddatumcontract() {
		return einddatumcontract;
	}

	public void setEinddatumcontract(Date einddatumcontract) {
		this.einddatumcontract = einddatumcontract;
	}

}