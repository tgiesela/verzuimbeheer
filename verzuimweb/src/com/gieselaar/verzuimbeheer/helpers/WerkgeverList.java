package com.gieselaar.verzuimbeheer.helpers;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.EJB;
import javax.faces.model.SelectItem;

import com.gieselaar.verzuimbeheer.exceptions.VerzuimApplicationException;
import com.gieselaar.verzuimbeheer.exceptions.PermissionException;
import com.gieselaar.verzuimbeheer.facades.LoginSessionRemote;
import com.gieselaar.verzuimbeheer.facades.WerkgeverFacade;
import com.gieselaar.verzuimbeheer.services.GebruikerInfo;
import com.gieselaar.verzuimbeheer.services.GebruikerWerkgeverInfo;
import com.gieselaar.verzuimbeheer.services.HoldingInfo;
import com.gieselaar.verzuimbeheer.services.WerkgeverInfo;

public class WerkgeverList {
	@SuppressWarnings("unused")
	private GebruikerInfo gebruiker;
	@EJB
	WerkgeverFacade werkgeverFacade;
	private List<SelectItem> werkgeversforlist = new ArrayList<SelectItem>();
	private List<SelectItem> actievewerkgeversforlist = new ArrayList<SelectItem>();
	private List<SelectItem> holdingsforlist = new ArrayList<SelectItem>();
	private List<SelectItem> actieveholdingsforlist = new ArrayList<SelectItem>();
	private List<WerkgeverInfo> werkgevers;
	private List<HoldingInfo> holdings;
	private List<HoldingInfo> selectedholdings;
	private List<HoldingInfo> actieveholdings;
	
	public WerkgeverList(GebruikerInfo gebruiker, LoginSessionRemote loginsession, WerkgeverFacade facade){
		this.gebruiker = gebruiker;
		try {
			werkgeverFacade = facade;
			werkgeverFacade.setLoginSession(loginsession);
			werkgevers = werkgeverFacade.allWerkgeversList();
			holdings = werkgeverFacade.getHoldings();
			actieveholdings = new ArrayList<>();
			selectedholdings = new ArrayList<>();
			actieveholdingsforlist.add(new SelectItem(-1,"N.v.t.")); // First empty entry
			holdingsforlist.add(new SelectItem(-1,"N.v.t.")); // First empty entry
			WerkgeverInfo.sort(werkgevers, WerkgeverInfo.__sortcol.NAAM);
			HoldingInfo.sort(holdings, HoldingInfo.__sortcol.NAAM);
			if (gebruiker.isAlleklanten()) {
				/*
				 * Vul de lijst met alle klanten
				 */
				for (WerkgeverInfo wg : werkgevers) {
					addwerkgever(wg);
				}
			} else {
				/*
				 * Vul de lijst met klanten waarvoor gebruiker geautoriseerd
				 * is
				 */
				for (WerkgeverInfo wg : werkgevers) {
					for (GebruikerWerkgeverInfo gwi : gebruiker.getWerkgevers())
						if (gwi.getWerkgeverid() == wg.getId()){
							addwerkgever(wg);
						}
				}
			}
		
		} catch (PermissionException | VerzuimApplicationException e) {
			e.printStackTrace();
		}
		
	}
	private void addwerkgever(WerkgeverInfo wg) {
		werkgeversforlist.add(new SelectItem(wg.getId(), wg.getNaam()));
		if (wg.getHoldingId() != null){
			for (HoldingInfo hi:holdings){
				if (hi.getId().equals(wg.getHoldingId())){
					if (!selectedholdings.contains(hi)){
						holdingsforlist.add(new SelectItem(hi.getId(),hi.getNaam()));
						selectedholdings.add(hi);
					}
					break;
				}
			}
		}
		if (wg.isActief()){
			actievewerkgeversforlist.add(new SelectItem(wg.getId(),wg.getNaam()));
			if (wg.getHoldingId() != null){
				for (HoldingInfo hi:holdings){
					if (hi.getId().equals(wg.getHoldingId())){
						if (hi.isActief()){
							if (!actieveholdings.contains(hi)){
								actieveholdingsforlist.add(new SelectItem(hi.getId(),hi.getNaam()));
								actieveholdings.add(hi);
							}
						}
						break;
					}
				}
			}
		}
	}
	public List<SelectItem> getWerkgevers() {
		return werkgeversforlist;
	}
	public List<SelectItem> getHoldings() {
		return holdingsforlist;
	}
	public List<SelectItem> getActieveWerkgevers() {
		return actievewerkgeversforlist;
	}
	public List<SelectItem> getActieveHoldings() {
		return actieveholdingsforlist;
	}
	public void setSelectedWerkgever(String selectedWerkgever) {
		FacesUtil.setSessionMapValue("werkgeverId", Integer.parseInt(selectedWerkgever));
	}
	public String getSelectedWerkgever() {
		Integer iWerkgever = (Integer)FacesUtil.getSessionMapValue("werkgeverId");
		if (iWerkgever == null)
			return "";
		else
			return iWerkgever.toString();
	}
}
