package com.gieselaar.verzuimbeheer.services;

import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import com.gieselaar.verzuimbeheer.exceptions.ValidationException;
import com.gieselaar.verzuimbeheer.exceptions.VerzuimRuntimeException;

public class HoldingInfo extends InfoBase{
	/**
	 * 
	 */
	public enum __sortcol {
		NAAM;
	}
	private static final long serialVersionUID = -1625073206456588221L;
	/**
	 * 
	 */
	/**
	 * private variables 
	 */
	private String naam;
	private String telefoonnummer;
	private AdresInfo vestigingsAdres;
	private AdresInfo postAdres;
	private AdresInfo factuurAdres;
	private ContactpersoonInfo contactpersoon;
	private String btwnummer;
	private Integer debiteurnummer;
	private boolean factureren;
	private boolean detailsecretariaat;
	private String emailadresfactuur;
	private __factuurtype factuurtype;
	private ContactpersoonInfo contactpersoonfactuur;
	private Date startdatumcontract;
	private Date einddatumcontract;
	boolean actief;

	public HoldingInfo() {
		super();
	}

	public AdresInfo getVestigingsAdres() {
		return vestigingsAdres;
	}

	public void setVestigingsAdres(AdresInfo vestigingsAdres) {
		this.vestigingsAdres = vestigingsAdres;
	}

	public AdresInfo getPostAdres() {
		return postAdres;
	}

	public void setPostAdres(AdresInfo postAdres) {
		this.postAdres = postAdres;
	}

	public ContactpersoonInfo getContactpersoon() {
		return contactpersoon;
	}

	public void setContactpersoon(ContactpersoonInfo contactpersoon) {
		this.contactpersoon = contactpersoon;
	}

	public String getNaam() {
		return naam;
	}

	public void setNaam(String naam) {
		this.naam = naam;
	}

	public String getTelefoonnummer() {
		return telefoonnummer;
	}

	public void setTelefoonnummer(String telefoonnummer) {
		this.telefoonnummer = telefoonnummer;
	}

	public AdresInfo getFactuurAdres() {
		return factuurAdres;
	}

	public void setFactuurAdres(AdresInfo factuurAdres) {
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

	public boolean isFactureren() {
		return factureren;
	}

	public void setFactureren(boolean factureren) {
		this.factureren = factureren;
	}

	public boolean isDetailsecretariaat() {
		return detailsecretariaat;
	}

	public void setDetailsecretariaat(boolean detailsecretariaat) {
		this.detailsecretariaat = detailsecretariaat;
	}

	public String getEmailadresfactuur() {
		return emailadresfactuur;
	}

	public void setEmailadresfactuur(String emailadresfactuur) {
		this.emailadresfactuur = emailadresfactuur;
	}

	public __factuurtype getFactuurtype() {
		return factuurtype;
	}

	public void setFactuurtype(__factuurtype factuurtype) {
		this.factuurtype = factuurtype;
	}

	public ContactpersoonInfo getContactpersoonfactuur() {
		return contactpersoonfactuur;
	}

	public void setContactpersoonfactuur(ContactpersoonInfo contactpersoonfactuur) {
		this.contactpersoonfactuur = contactpersoonfactuur;
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

	public enum __factuurtype {
		NVT(0) {
			@Override
			public String toString() {
				return "N.v.t";
			}
		},
		GEAGGREGEERD(1) {
			@Override
			public String toString() {
				return "Geaggregeerd";
			}
		},
		GESPECIFICEERD(2) {
			@Override
			public String toString() {
				return "Gespecificeerd per werkgever";
			}
		},
		SEPARAAT(3) {
			@Override
			public String toString() {
				return "Factuur per werkgever naar holding";
			}
		};

		private Integer value;

		__factuurtype(Integer value) {
			this.value = value;
		}

		public Integer getValue() {
			return value;
		}

		public static __factuurtype parse(Integer type) {
			__factuurtype factuurtype = null; // Default
			for (__factuurtype item : __factuurtype.values()) {
				if (item.getValue().equals(type)) {
					factuurtype = item;
					break;
				}
			}
			return factuurtype;
		}
	}
	@Override
	public boolean validate() throws ValidationException {
		if ((this.getNaam() == null) || (this.getNaam().isEmpty()))
			throw new ValidationException("Naam holding mag niet leeg zijn"); 
    	if ((this.isFactureren()) && ((this.getFactuurtype() == null) || (this.getFactuurtype() == __factuurtype.NVT))){
    		throw new ValidationException("Factuurtype is verplicht als er gefactuurd kan worden"); 
    	}
    	if ((this.getVestigingsAdres() == null) || (this.getVestigingsAdres().isEmtpy()))
    		throw new ValidationException("Vestigingsadres is verplicht");
    	return true;
	}
	
	public static class Holdingcompare implements Comparator<HoldingInfo> {

		__sortcol sortcol;

		public Holdingcompare(__sortcol column) {
			sortcol = column;
		}

		@Override
		public int compare(HoldingInfo o1, HoldingInfo o2) {
			if (sortcol == __sortcol.NAAM) {
				return o1.getNaam().compareToIgnoreCase(o2.getNaam());
			}else{
				throw new VerzuimRuntimeException(
						"Unknown sortcol in HoldingInfo comparator");
			}

		}
	}
	public static void sort(List<HoldingInfo> holdings, __sortcol col){
		Collections.sort(holdings, new Holdingcompare(col));
	}
	public boolean isActief() {
		actief = false;
		if ((this.getEinddatumcontract() == null) ||
			(this.getEinddatumcontract().after(new Date())))
			actief = true;

		return actief;
	}
	public void setActief(boolean actief) {
		this.actief = actief;
	}

}
