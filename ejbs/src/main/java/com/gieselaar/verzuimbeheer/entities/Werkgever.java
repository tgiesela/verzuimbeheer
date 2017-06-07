package com.gieselaar.verzuimbeheer.entities;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.persistence.*;

import com.gieselaar.verzuimbeheer.entitiesutils.EntityBaseId;

import java.util.Date;


/**
 * The persistent class for the WERKGEVER database table.
 * 
 */
@Entity
public class Werkgever extends EntityBaseId implements Serializable {
	private static final long serialVersionUID = 1L;

    @Temporal( TemporalType.DATE)
	private Date startdatumcontract;
	private String telefoon;
	private String naam;
    @Temporal( TemporalType.DATE)
	private Date einddatumcontract;

	private Integer holding_ID;					// Deze entiteiten bevatten weer subtabellen. 
	private Integer arbodienst_ID;				// Dat lukt niet met een 'left join fetch' 
	private Integer uitvoeringsinstituut_ID; 	// Dus die halen we dan maar gewoon op;
	private BigDecimal werkweek;
	
//	@OneToOne
//	@JoinColumn(name="bedrijfsarts_ID")
//	private Bedrijfsarts bedrijfsArts;
	@Column(name="bedrijfsarts_ID")
	private Integer bedrijfsartsid;

	@OneToOne(cascade={CascadeType.ALL},orphanRemoval=true)
	@JoinColumn(name="contactpersoon_ID")
	private Contactpersoon contactPersoon;

	@OneToOne(cascade={CascadeType.ALL},orphanRemoval=true)
	@JoinColumn(name="postadres_ID")
	private Adres postAdres;

	@OneToOne(cascade={CascadeType.ALL},orphanRemoval=true)
	@JoinColumn(name="vestigingsadres_ID")
	private Adres vestigingsAdres;

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
	private String externcontractnummer;
	
	public Date getEinddatumcontract() {
		return this.einddatumcontract;
	}

	public void setEinddatumcontract(Date einddatumcontract) {
		this.einddatumcontract = einddatumcontract;
	}

	public String getNaam() {
		return this.naam;
	}

	public void setNaam(String naam) {
		this.naam = naam;
	}

	public Date getStartdatumcontract() {
		return this.startdatumcontract;
	}

	public void setStartdatumcontract(Date startdatumcontract) {
		this.startdatumcontract = startdatumcontract;
	}

	public String getTelefoon() {
		return this.telefoon;
	}

	public void setTelefoon(String telefoon) {
		this.telefoon = telefoon;
	}

	public Adres getFactuurAdres() {
		return this.factuurAdres;
	}
	
	public void setFactuurAdres(Adres factuurAdres) {
		this.factuurAdres = factuurAdres;
	}

//	public Bedrijfsarts getBedrijfsArts() {
//		return bedrijfsArts;
//	}

//	public void setBedrijfsArts(Bedrijfsarts bedrijfsArts) {
//		this.bedrijfsArts = bedrijfsArts;
//	}
	public Integer getBedrijfsartsid(){
		return bedrijfsartsid;
	}
	
	public void setBedrijfsartsid(Integer bedrijfsartsid){
		this.bedrijfsartsid = bedrijfsartsid;
	}
	
	public Contactpersoon getContactPersoon() {
		return contactPersoon;
	}

	public void setContactPersoon(Contactpersoon contactPersoon) {
		this.contactPersoon = contactPersoon;
	}

	public Adres getPostAdres() {
		return postAdres;
	}

	public void setPostAdres(Adres postAdres) {
		this.postAdres = postAdres;
	}

	public Adres getVestigingsAdres() {
		return vestigingsAdres;
	}

	public void setVestigingsAdres(Adres vestigingsAdres) {
		this.vestigingsAdres = vestigingsAdres;
	}

	public Integer getHolding_ID() {
		return holding_ID;
	}

	public void setHolding_ID(Integer holding_ID) {
		this.holding_ID = holding_ID;
	}

	public Integer getArbodienst_ID() {
		return arbodienst_ID;
	}

	public void setArbodienst_ID(Integer arbodienst_ID) {
		this.arbodienst_ID = arbodienst_ID;
	}

	public Integer getUitvoeringsinstituut_ID() {
		return uitvoeringsinstituut_ID;
	}

	public void setUitvoeringsinstituut_ID(Integer uitvoeringsinstituut_ID) {
		this.uitvoeringsinstituut_ID = uitvoeringsinstituut_ID;
	}

	public BigDecimal getWerkweek() {
		return werkweek;
	}

	public void setWerkweek(BigDecimal werkweek) {
		this.werkweek = werkweek;
	}

	public String getBTWnummer() {
		return btwnummer;
	}

	public void setBTWnummer(String bTWnummer) {
		btwnummer = bTWnummer;
	}

	public Integer getDebiteurnummer() {
		return debiteurnummer;
	}

	public void setDebiteurnummer(Integer debiteurnummer) {
		this.debiteurnummer = debiteurnummer;
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

	public String getEmailadresfactuur() {
		return emailadresfactuur;
	}

	public void setEmailadresfactuur(String emailadresfactuur) {
		this.emailadresfactuur = emailadresfactuur;
	}

	public String getExterncontractnummer() {
		return externcontractnummer;
	}

	public void setExterncontractnummer(String externcontractnummer) {
		this.externcontractnummer = externcontractnummer;
	}

	public Contactpersoon getContactpersoonfactuur() {
		return contactpersoonfactuur;
	}

	public void setContactpersoonfactuur(Contactpersoon contactpersoon) {
		this.contactpersoonfactuur = contactpersoon;
	}

}