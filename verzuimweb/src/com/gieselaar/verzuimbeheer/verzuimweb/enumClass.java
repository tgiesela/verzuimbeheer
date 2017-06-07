package com.gieselaar.verzuimbeheer.verzuimweb;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import com.gieselaar.verzuimbeheer.services.InfoBase.__burgerlijkestaat;
import com.gieselaar.verzuimbeheer.services.InfoBase.__geslacht;
import com.gieselaar.verzuimbeheer.services.VerzuimInfo.__gerelateerdheid;
import com.gieselaar.verzuimbeheer.services.VerzuimInfo.__meldingswijze;
import com.gieselaar.verzuimbeheer.services.VerzuimInfo.__vangnettype;
import com.gieselaar.verzuimbeheer.services.VerzuimInfo.__verzuimtype;
import com.gieselaar.verzuimbeheer.services.WiapercentageInfo.__wiapercentage;
 
@ManagedBean(name="enumClass")
@ViewScoped
public class enumClass implements Serializable{
	 
	 
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public List<__geslacht> getGeslachtenEnum() {
		return Arrays.asList(__geslacht.values());
	}
	public List<__burgerlijkestaat> getBurgelijkestatenEnum() {
		return Arrays.asList(__burgerlijkestaat.values());
	}
	public List<__wiapercentage> getWiapercentagesEnum(){
		return Arrays.asList(__wiapercentage.values());
	}
	public List<__gerelateerdheid> getGerelateerdheidEnum(){
		return Arrays.asList(__gerelateerdheid.values());
	}
	public List<__verzuimtype> getVerzuimtypeEnum(){
		return Arrays.asList(__verzuimtype.values());
	}
	public List<__vangnettype> getVangnettypeEnum(){
		return Arrays.asList(__vangnettype.values());
	}
	public List<__meldingswijze> getMeldingswijzeEnum(){
		return Arrays.asList(__meldingswijze.values());
	}
	
}
