package com.gieselaar.verzuimbeheer.services;

import java.io.Serializable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.gieselaar.verzuimbeheer.exceptions.ValidationException;

public class AdresInfo extends InfoBase implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -6848533756030851261L;
	/**
	 * 
	 */
	/**
	 * private variables 
	 */
	private String huisnummer;
	private String huisnummertoevoeging;
	private String land;
	private String plaats;
	private String postcode;
	private String straat;

	public AdresInfo() {
		super();
	}
	public String getHuisnummer() {
		return huisnummer;
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

	public String getLand() {
		return land;
	}

	public void setLand(String land) {
		this.land = land;
	}

	public String getPlaats() {
		return plaats;
	}

	public void setPlaats(String plaats) {
		this.plaats = plaats;
	}

	public String getPostcode() {
		return postcode;
	}

	public void setPostcode(String postcode) {
		this.postcode = postcode;
	}

	public String getStraat() {
		return straat;
	}

	public void setStraat(String straat) {
		this.straat = straat;
	}

	private boolean isHuisnummerEmpty(){
		return (this.getHuisnummer() == null || this.getHuisnummer().isEmpty()) &&
			   (this.getHuisnummertoevoeging() == null || this.getHuisnummertoevoeging().isEmpty());
	}
	private boolean isAdresEmpty(){
		return (this.getPostcode() == null || this.getPostcode().isEmpty()) &&
			   (this.getStraat() == null || this.getStraat().isEmpty());
	}
	public boolean isEmtpy(){
		return 
		    isAdresEmpty() &&
		    (this.getLand() == null || this.getLand().isEmpty()) &&
			isHuisnummerEmpty();
	}
	@Override
	public boolean validate() throws ValidationException {
		if (this.isEmtpy())
		{
			return true;
		}
		if (this.getStraat() == null || this.getStraat().isEmpty())
			throw new ValidationException("Straat niet ingevuld");
		if (this.getHuisnummer() == null || this.getHuisnummer().isEmpty())
			throw new ValidationException("Huisnummer niet ingevuld");
		if (this.getPostcode() == null || this.getPostcode().isEmpty())
			throw new ValidationException("Postcode niet ingevuld");
		if (this.getPlaats() == null || this.getPlaats().isEmpty())
			throw new ValidationException("Plaats niet ingevuld");
		return false;
	}
	private static boolean isValidNumber(String str){
		return str.matches("[0-9]+");  //match a number.
	}
	public static void extractHuisnummerfromStraat(String straat, AdresInfo adr) {
		// We breken de string in stukken gescheiden door spaties
		String huisnummer = "";
		String huisnummertoevoeging = "";
		String adres = "";
		int nrofpartstostrip = 0;
		String[] parts = straat.split(" ");
		if (parts.length < 2){
			/* er valt niets te spliten */
		}
		else
		{
			/* we gaan het laatste deel onderzoeken */
			String lastpart = parts[parts.length-1];
			if (isValidNumber(lastpart)){
				huisnummer = lastpart;
				nrofpartstostrip = 1; 
			}else{
				/* het kan een huisnummer toevoeging zijn of een notatie met een '-' er in 
				 * waarin dan dus nog een huisnummer en huisnummertoevoeging zitten  
				 * */
				/*
				 * \s+:	een of meer whitespace characters
				 * \s*: nul of meer whitespace characters
				 */
				lastpart = lastpart.trim();
				String[] subparts = lastpart.split(",|-|/");
				if (subparts.length == 2){
					/* 
					 * Het bestaat uit twee delen, als het eerste een getal is, dan moet het wel kloppen.
					 * Als het eerste deel geen getal is, kunnen we er verder geen chocola van maken 
					 * */
					if (isValidNumber(subparts[0])){
						huisnummer = subparts[0];
						huisnummertoevoeging = subparts[1];
						nrofpartstostrip = 1; 
					}
				}else{
					/* 
					 * Er zit geen separator in het laatste deel, of meerdere.
					 * dan kan het ook nog iets zijn in de trant van nnX, bijvoorbeeld 23I, wat dus eigenlijk
					 * 23-I of 23 I had moeten zijn
					 */
					Pattern regex = Pattern.compile("(\\d{1,})([a-zA-Z]{1,})$");
					Matcher regexMatcher;
					regexMatcher = regex.matcher(lastpart);
					boolean matches = regexMatcher.matches();
					if (matches && regexMatcher.groupCount() == 2){
						huisnummer = regexMatcher.group(1);
						huisnummertoevoeging = regexMatcher.group(2);
						nrofpartstostrip = 1; 
					}else{
						if ((parts.length > 2) && (isValidNumber(parts[parts.length-2]))){
							huisnummertoevoeging = lastpart;
							huisnummer = parts[parts.length-2];
							nrofpartstostrip = 2; 
						}
					}
				}
			}
		}
		if (!huisnummer.isEmpty()){
			adr.setHuisnummer(huisnummer);
			adr.setHuisnummertoevoeging(huisnummertoevoeging);
			StringBuilder bldr = new StringBuilder(adres);
			for (int i=0;i<=(parts.length - 1 - nrofpartstostrip);i++){
				bldr.append(" " + parts[i]);
			}
			adr.setStraat(bldr.toString().trim());
		}else{
			adr.setHuisnummer("");
			adr.setHuisnummertoevoeging("");
			adr.setStraat(straat.trim());
		}
	}

}
