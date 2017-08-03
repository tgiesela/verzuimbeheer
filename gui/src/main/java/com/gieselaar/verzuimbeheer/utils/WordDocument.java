package com.gieselaar.verzuimbeheer.utils;

import java.awt.Component;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.commons.lang3.StringEscapeUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.gieselaar.verzuimbeheer.exceptions.VerzuimApplicationException;
import com.gieselaar.verzuimbeheer.exceptions.PermissionException;
import com.gieselaar.verzuimbeheer.exceptions.ValidationException;
import com.gieselaar.verzuimbeheer.facades.CascodeFacadeRemote;
import com.gieselaar.verzuimbeheer.facades.InstantieFacadeRemote;
import com.gieselaar.verzuimbeheer.facades.LoginSessionRemote;
import com.gieselaar.verzuimbeheer.facades.VerzuimFacadeRemote;
import com.gieselaar.verzuimbeheer.facades.WerknemerFacadeRemote;
import com.gieselaar.verzuimbeheer.services.AdresInfo;
import com.gieselaar.verzuimbeheer.services.AfdelingHasWerknemerInfo;
import com.gieselaar.verzuimbeheer.services.ArbodienstInfo;
import com.gieselaar.verzuimbeheer.services.BedrijfsartsInfo;
import com.gieselaar.verzuimbeheer.services.BedrijfsgegevensInfo;
import com.gieselaar.verzuimbeheer.services.CascodeGroepInfo;
import com.gieselaar.verzuimbeheer.services.CascodeInfo;
import com.gieselaar.verzuimbeheer.services.DienstverbandInfo;
import com.gieselaar.verzuimbeheer.services.DocumentTemplateInfo;
import com.gieselaar.verzuimbeheer.services.HoldingInfo;
import com.gieselaar.verzuimbeheer.services.UitvoeringsinstituutInfo;
import com.gieselaar.verzuimbeheer.services.VerzuimDocumentInfo;
import com.gieselaar.verzuimbeheer.services.VerzuimInfo;
import com.gieselaar.verzuimbeheer.services.WerkgeverInfo;
import com.gieselaar.verzuimbeheer.services.WerknemerInfo;
import com.gieselaar.verzuimbeheer.services.WiapercentageInfo;
import com.gieselaar.verzuimbeheer.utils.VerzuimProperties.__verzuimproperty;
import com.google.common.io.Files;

public class WordDocument {

	/*
	 * Vraag of het document geregistreerd moet worden en zo ja waar het
	 * opgeslagen moet worden.
	 */

	private File selectedFile = null;
	private String outputPathFilename;
	private VerzuimDocumentInfo targetdoc = null;
	private Zipper zipper = new Zipper();
	private Component thisform = null;
	private VerzuimInfo verzuim = null;
	private VerzuimFacadeRemote verzuimRemote;
	private WerknemerFacadeRemote werknemerRemote;
	private InstantieFacadeRemote instantieRemote;
	private CascodeFacadeRemote cascodeRemote;

	public WordDocument(Component form, LoginSessionRemote session){
		thisform = form;
		try {
			verzuimRemote = (VerzuimFacadeRemote) ServiceLocator
					.getInstance()
					.getRemoteHome(RemoteInterfaces.VerzuimFacade.getValue(),
							VerzuimFacadeRemote.class);
			verzuimRemote.setLoginSession(session);
			werknemerRemote = (WerknemerFacadeRemote) ServiceLocator
					.getInstance()
					.getRemoteHome(RemoteInterfaces.WerknemerFacade.getValue(),
							WerknemerFacadeRemote.class);
			werknemerRemote.setLoginSession(session);
			instantieRemote = (InstantieFacadeRemote) ServiceLocator
					.getInstance()
					.getRemoteHome(RemoteInterfaces.InstantieFacade.getValue(),
							InstantieFacadeRemote.class);
			instantieRemote.setLoginSession(session);
			cascodeRemote = (CascodeFacadeRemote) ServiceLocator
					.getInstance()
					.getRemoteHome(RemoteInterfaces.CascodeFacade.getValue(),
							CascodeFacadeRemote.class);
			cascodeRemote.setLoginSession(session);
		} catch (ServiceLocatorException e) {
			ExceptionLogger.ProcessException(e,thisform, "Unable to connect to server");
			return;
		}catch (PermissionException e) {
        	ExceptionLogger.ProcessException(e,thisform);
        	return;
		}
		
	}
	private String formatAdres(AdresInfo adres) {
		String returnadres;
		returnadres = adres.getStraat() + " " + adres.getHuisnummer();
		if (adres.getHuisnummertoevoeging().isEmpty())
			;
		else
			returnadres = returnadres + " " + adres.getHuisnummertoevoeging();
		// returnadres = returnadres + "," + adres.getPostcode() + " "
		// + adres.getPlaats();
		return returnadres;
	}

	private void htput(HashMap<String, String> ht, String key, String value){
		if (value == null)
			ht.put(key,"");
		else
			ht.put(key,StringEscapeUtils.escapeXml11(value));
	}
	private HashMap<String, String> populateHashTable() {
		DateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
		DienstverbandInfo dvb = verzuim.getDienstverband();
		WerknemerInfo wnr = null;
		WerkgeverInfo wgr = null;
		UitvoeringsinstituutInfo uvi = null;
		ArbodienstInfo abd = null;
		BedrijfsartsInfo bda = null;
		BedrijfsgegevensInfo bg = null;
		VerzuimInfo vzm = null;
		List<BedrijfsgegevensInfo> bgl = null;
		List<CascodeInfo> cascodes = null;
		List<CascodeGroepInfo> cascodegroepen = null;
		try {
			wnr = werknemerRemote.getWerknemer(verzuim.getWerknemer().getId());
			wgr = werknemerRemote.getWerkgever(dvb.getWerkgeverId());
			uvi = instantieRemote.getUitkeringsinstantie(wgr.getUwvId());
			abd = instantieRemote.getArbodienst(wgr.getArbodienstId());
			bgl = instantieRemote.allBedrijfsgegevens();
			bda = instantieRemote.getBedrijfsarts(wgr.getBedrijfsartsid());
			cascodes = cascodeRemote.allCascodes();
			cascodegroepen = cascodeRemote.allCascodeGroepen();
			if (bgl != null)
				bg = bgl.get(0);
		} catch (PermissionException e) {
			JOptionPane.showMessageDialog(thisform, e.getMessage());
			return null;
		} catch (VerzuimApplicationException e) {
			ExceptionLogger.ProcessException(e, thisform);
			return null;
		} catch (ValidationException e) {
			JOptionPane.showMessageDialog(thisform, e.getMessage());
			return null;
		}
		AfdelingHasWerknemerInfo afd = null;
		if (wnr.getAfdelingen() != null)
			for (AfdelingHasWerknemerInfo info : wnr.getAfdelingen()) {
				if (afd == null)
					afd = info;
				else if (afd.getStartdatum().before(info.getStartdatum()))
					afd = info;
			}

		WiapercentageInfo wia = wnr.getWiaPercentages().get(0);

		for (WiapercentageInfo wi : wnr.getWiaPercentages()) {
			if (wia.getStartdatum().before(wi.getStartdatum()))
				wia = wi;
		}
		HashMap<String, String> ht = new HashMap<String, String>();

		htput(ht,"Werknemer Werkweek", verzuim.getDienstverband().getWerkweek()
				.toPlainString());
		htput(ht,"Werknemer Wao Percentage", wia.getCodeWiaPercentage()
				.toString());
		if (wnr.getVoorvoegsel() != null)
			htput(ht,"Werknemer Voorvoegsel", wnr.getVoorvoegsel());
		else
			htput(ht,"Werknemer Voorvoegsel", "");
		if (wnr.getVoornaam() != null)
			htput(ht,"Werknemer Voornaam", wnr.getVoornaam());
		else
			htput(ht,"Werknemer Voornaam", "");
		htput(ht,"Werknemer Voorletters", wnr.getVoorletters());
		htput(ht,"Werknemer Verzekering", "");
		htput(ht,"Werknemer Telefoon Werk", wnr.getTelefoon());
		htput(ht,"Werknemer Telefoon Prive", wnr.getTelefoonPrive());
		htput(ht,"Werknemer Sofi", wnr.getBurgerservicenummer());
		htput(ht,"Werknemer Postcode", wnr.getAdres().getPostcode());
		htput(ht,"Werknemer Plaats", wnr.getAdres().getPlaats());
		htput(ht,"Werknemer Personeelsnr", dvb.getPersoneelsnummer());
		htput(ht,"Werknemer Opmerkingen", wnr.getOpmerkingen());
		htput(ht,"Werknemer Mobiel", wnr.getMobiel());
		// htput(ht,"Werknemer Huisarts",);
		switch (wnr.getGeslacht()) {
		case MAN:
			htput(ht,"Werknemer heer/mevrouw", "heer");
			htput(ht,"Werknemer De heer/Mevrouw", "De heer");
			htput(ht,"Werknemer Geslacht", wnr.getGeslacht().toString());
			break;
		case VROUW:
			htput(ht,"Werknemer heer/mevrouw", "mevrouw");
			htput(ht,"Werknemer De heer/Mevrouw", "Mevrouw");
			htput(ht,"Werknemer Geslacht", wnr.getGeslacht().toString());
			break;
		case ONBEKEND:
			htput(ht,"Werknemer De heer/Mevrouw", "heer/mevrouw");
			htput(ht,"Werknemer De heer/Mevrouw", "De heer/Mevrouw");
			htput(ht,"Werknemer Geslacht", wnr.getGeslacht().toString());
			break;
		}
		htput(ht,"Werknemer Geboortedatum",
				formatter.format(wnr.getGeboortedatum()));
		htput(ht,"Werknemer Functie", dvb.getFunctie());
		htput(ht,"Werknemer Email", wnr.getEmail());
		if (dvb.getEinddatumcontract() != null)
			htput(ht,"Werknemer Datum Uit Dienst",
					formatter.format(dvb.getEinddatumcontract()));
		else
			htput(ht,"Werknemer Datum Uit Dienst", "n.v.t.");

		htput(ht,"Werknemer Datum In Dienst",
				formatter.format(dvb.getStartdatumcontract()));
		htput(ht,"Werknemer Burgerlijke Staat", wnr.getBurgerlijkestaat()
				.toString());
		if (afd != null) {
			htput(ht,"Werknemer Afdeling Opmerkingen", "");
			if (afd.getEinddatum() != null)
				htput(ht,"Werknemer Afdeling Datum Einde",
						formatter.format(afd.getEinddatum()));
			else
				htput(ht,"Werknemer Afdeling Datum Einde", "");
			htput(ht,"Werknemer Afdeling Datum Begin",
					formatter.format(afd.getStartdatum()));
		} else {
			htput(ht,"Werknemer Afdeling Opmerkingen", "");
			htput(ht,"Werknemer Afdeling Datum Einde", "");
			htput(ht,"Werknemer Afdeling Datum Begin", "");
		}
		htput(ht,"Werknemer Adres", formatAdres(wnr.getAdres()));
		htput(ht,"Werknemer Achternaam", wnr.getAchternaam());

		htput(ht,"Werkgever Werkweek", "");
		htput(ht,"Werkgever Verzekering", "");
		htput(ht,"Werkgever Telefoon", wgr.getTelefoon());
		htput(ht,"Werkgever Telefoon Bedrijfsarts", "");
		htput(ht,"Werkgever Postcode", wgr.getVestigingsAdres().getPostcode());
		htput(ht,"Werkgever Plaats", wgr.getVestigingsAdres().getPlaats());
		htput(ht,"Werkgever Adres", formatAdres(wgr.getVestigingsAdres()));
		if (wgr.getPostAdres() != null)
			htput(ht,"Werkgever Postbus", wgr.getPostAdres().getHuisnummer() + " "
					+ wgr.getPostAdres().getPostcode() + " "
					+ wgr.getPostAdres().getPlaats());
		else
			htput(ht,"Werkgever Postbus", "");
		htput(ht,"Werkgever Opmerkingen", "");
		htput(ht,"Werkgever Naam", wgr.getNaam());
		htput(ht,"Werkgever Fax", "");
		if (bda != null){
			htput(ht,"Werkgever Naam Bedrijfsarts", bda.getAchternaam());
			htput(ht,"Werkgever Email Bedrijfsarts", bda.getTelefoon());
		} else {
			htput(ht,"Werkgever Naam Bedrijfsarts", "");
			htput(ht,"Werkgever Email Bedrijfsarts", "");
		}
		htput(ht,"Werkgever Debiteurnr", "");
		if (wgr.getEinddatumcontract() != null)
			htput(ht,"Werkgever Datum Einde",
					formatter.format(wgr.getEinddatumcontract()));
		else
			htput(ht,"Werkgever Datum Einde", "");
		htput(ht,"Werkgever Datum Begin",
				formatter.format(wgr.getStartdatumcontract()));
		if (wgr.getContactpersoon() != null) {
			htput(ht,"Werkgever Contactpersoon Voorvoegsel", wgr
					.getContactpersoon().getVoorvoegsel());
			htput(ht,"Werkgever Contactpersoon Voornaam", wgr.getContactpersoon()
					.getVoornaam());
			htput(ht,"Werkgever Contactpersoon Voorletters", wgr
					.getContactpersoon().getVoorletters());
			htput(ht,"Werkgever Contactpersoon Telefoon", wgr.getContactpersoon()
					.getTelefoon());
			htput(ht,"Werkgever Contactpersoon Mobiel", wgr.getContactpersoon()
					.getMobiel());
			htput(ht,"Werkgever Contactpersoon Geslacht", wgr.getContactpersoon()
					.getGeslacht().toString());
			htput(ht,"Werkgever Contactpersoon Geboortedatum", "");
			htput(ht,"Werkgever Contactpersoon Fax", "");
			htput(ht,"Werkgever Contactpersoon Email", wgr.getContactpersoon()
					.getEmailadres());
			htput(ht,"Werkgever Contactpersoon Achternaam", wgr
					.getContactpersoon().getAchternaam());
		} else {
			htput(ht,"Werkgever Contactpersoon Voorvoegsel", "");
			htput(ht,"Werkgever Contactpersoon Voornaam", "");
			htput(ht,"Werkgever Contactpersoon Voorletters", "");
			htput(ht,"Werkgever Contactpersoon Telefoon", "");
			htput(ht,"Werkgever Contactpersoon Mobiel", "");
			htput(ht,"Werkgever Contactpersoon Geslacht", "");
			htput(ht,"Werkgever Contactpersoon Geboortedatum", "");
			htput(ht,"Werkgever Contactpersoon Fax", "");
			htput(ht,"Werkgever Contactpersoon Email", "");
			htput(ht,"Werkgever Contactpersoon Achternaam", "");
		}
		htput(ht,"Werkgever Arbopakket", "");
		htput(ht,"Werkgever Aansluitingsnr", "");

		htput(ht,"Verzuim Vangnet Type", verzuim.getVangnettype().toString());
		htput(ht,"Verzuim Opmerkingen", verzuim.getOpmerkingen());
		htput(ht,"Verzuim Meldingswijze", verzuim.getMeldingswijze().toString());
		htput(ht,"Verzuim Meldinsgdatum",
				formatter.format(verzuim.getMeldingsdatum()));
		if (verzuim.getKetenverzuim())
			htput(ht,"Verzuim Keten Flag", "Ja");
		else
			htput(ht,"Verzuim Keten Flag", "Nee");
		htput(ht,"Verzuim Gerelateerdheid", verzuim.getGerelateerdheid()
				.toString());
		if (verzuim.getEinddatumverzuim() != null)
			htput(ht,"Verzuim Datum Einde",
					formatter.format(verzuim.getEinddatumverzuim()));
		else
			htput(ht,"Verzuim Datum Einde", "n.v.t.");
		htput(ht,"Verzuim Datum Begin",
				formatter.format(verzuim.getStartdatumverzuim()));

		if (uvi != null) {
			htput(ht,"Uitvoeringsinstantie Postcode", uvi.getVestigingsadres()
					.getPostcode());
			htput(ht,"Uitvoeringsinstantie Plaats", uvi.getVestigingsadres()
					.getPlaats());
			htput(ht,"Uitvoeringsinstantie Naam", uvi.getNaam());
			htput(ht,"Uitvoeringsinstantie Adres",
					formatAdres(uvi.getVestigingsadres()));
		} else {
			htput(ht,"Uitvoeringsinstantie Postcode", "");
			htput(ht,"Uitvoeringsinstantie Plaats", "");
			htput(ht,"Uitvoeringsinstantie Naam", "");
			htput(ht,"Uitvoeringsinstantie Adres", "");
		}

		if (wgr.getHolding() != null) {
			HoldingInfo hld = wgr.getHolding();
			htput(ht,"Moederbedrijf Telefoon", hld.getTelefoonnummer());
			htput(ht,"Moederbedrijf Postcode", hld.getVestigingsAdres()
					.getPostcode());
			if (hld.getPostAdres() == null)
				htput(ht,"Moederbedrijf Postbus", "");
			else
				htput(ht,"Moederbedrijf Postbus", hld.getPostAdres().getHuisnummer());
			htput(ht,"Moederbedrijf Plaats", hld.getVestigingsAdres().getPlaats());
			htput(ht,"Moederbedrijf Naam", hld.getNaam());
			if (hld.getContactpersoon() != null){
				htput(ht,"Moederbedrijf Contactpersoon Voorvoegsel", hld
						.getContactpersoon().getVoorvoegsel());
				htput(ht,"Moederbedrijf Contactpersoon Voornaam", hld
						.getContactpersoon().getVoornaam());
				htput(ht,"Moederbedrijf Contactpersoon Voorletters", hld
						.getContactpersoon().getVoorletters());
				htput(ht,"Moederbedrijf Contactpersoon Telefoon", hld
						.getContactpersoon().getTelefoon());
				htput(ht,"Moederbedrijf Contactpersoon Mobiel", hld
						.getContactpersoon().getMobiel());
				htput(ht,"Moederbedrijf Contactpersoon Geslacht", hld
						.getContactpersoon().getGeslacht().toString());
				htput(ht,"Moederbedrijf Contactpersoon Email", hld
						.getContactpersoon().getEmailadres());
				htput(ht,"Moederbedrijf Contactpersoon Achternaam", hld
						.getContactpersoon().getAchternaam());
				htput(ht,"Moederbedrijf Adres", formatAdres(hld.getVestigingsAdres()));
			}else{
				htput(ht,"Moederbedrijf Contactpersoon Voorvoegsel", "");
				htput(ht,"Moederbedrijf Contactpersoon Voornaam", "");
				htput(ht,"Moederbedrijf Contactpersoon Voorletters","");
				htput(ht,"Moederbedrijf Contactpersoon Telefoon","");
				htput(ht,"Moederbedrijf Contactpersoon Mobiel","");
				htput(ht,"Moederbedrijf Contactpersoon Geslacht","");
				htput(ht,"Moederbedrijf Contactpersoon Email","");
				htput(ht,"Moederbedrijf Contactpersoon Achternaam", "");
				htput(ht,"Moederbedrijf Adres", formatAdres(hld.getVestigingsAdres()));
			}
		} else {
			htput(ht,"Moederbedrijf Telefoon", "");
			htput(ht,"Moederbedrijf Postcode", "");
			htput(ht,"Moederbedrijf Postbus", "");
			htput(ht,"Moederbedrijf Plaats", "");
			htput(ht,"Moederbedrijf Opmerkingen", "");
			htput(ht,"Moederbedrijf Naam", "");
			htput(ht,"Moederbedrijf Contactpersoon Voorvoegsel", "");
			htput(ht,"Moederbedrijf Contactpersoon Voornaam", "");
			htput(ht,"Moederbedrijf Contactpersoon Voorletters", "");
			htput(ht,"Moederbedrijf Contactpersoon Telefoon", "");
			htput(ht,"Moederbedrijf Contactpersoon Mobiel", "");
			htput(ht,"Moederbedrijf Contactpersoon Geslacht", "");
			htput(ht,"Moederbedrijf Contactpersoon Email", "");
			htput(ht,"Moederbedrijf Contactpersoon Achternaam", "");
			htput(ht,"Moederbedrijf Adres", "");
		}
		htput(ht,"Consultant Telefoon", "");
		htput(ht,"Consultant Opmerking", "");
		htput(ht,"Consultant Naam", "");
		htput(ht,"Consultant Email", "");

     	htput(ht,"Cascode Duur", "");
     	htput(ht,"Cascode Duur Eenheid", "");
		if (wnr.hasOpenVerzuim(wnr.getLaatsteDienstverband())){
			for (VerzuimInfo v: wnr.getLaatsteDienstverband().getVerzuimen()){
				if (v.getEinddatumverzuim() == null){
					vzm = v;
					break;
				}
			}
			int cascode = vzm.getCascode();
			for (CascodeInfo cc: cascodes){
				if (cc.getId().equals(cascode)){
			     	htput(ht,"Cascode Omschrijving", cc.getOmschrijving());
			     	htput(ht,"Cascode Cas Code", cc.getCascode());							
			     	for (CascodeGroepInfo ccg: cascodegroepen){
			     		if (ccg.getId().equals(cc.getCascodegroep())){
					     	htput(ht,"Cascode Groep Omschrijving", ccg.getOmschrijving());
					     	htput(ht,"Cascode Groep Naam", ccg.getNaam());
					     	break;
			     		}
			     	}
			     	
			     	break;
				}
			}
		}else{
	     	htput(ht,"Cascode Omschrijving", "");
	     	htput(ht,"Cascode Groep Omschrijving", "");
	     	htput(ht,"Cascode Groep Naam", "");
	     	htput(ht,"Cascode Cas Code", "");
		}

		if (bg != null){
			htput(ht,"Bedrijfsgegevens Naam", bg.getNaam());
			htput(ht,"Bedrijfsgegevens Telefoon", bg.getTelefoonnummer());
			htput(ht,"Bedrijfsgegevens Mobiel", bg.getMobiel());
			htput(ht,"Bedrijfsgegevens Fax", bg.getFax());
			if (bg.getPostAdres() != null){
				htput(ht,"Bedrijfsgegevens Postcode postadres", bg.getPostAdres().getPostcode());
				htput(ht,"Bedrijfsgegevens Plaats postadres", bg.getPostAdres().getPlaats());
				htput(ht,"Bedrijfsgegevens Adres postadres", formatAdres(bg.getPostAdres()));
			} else{
				htput(ht,"Bedrijfsgegevens Postcode postadres", "");
				htput(ht,"Bedrijfsgegevens Plaats postadres", "");
				htput(ht,"Bedrijfsgegevens Adres postadres", "");
			}
			if (bg.getVestigingsAdres() != null){
				htput(ht,"Bedrijfsgegevens Postcode vestigingsadres", bg.getVestigingsAdres().getPostcode());
				htput(ht,"Bedrijfsgegevens Plaats vestigingsadres", bg.getVestigingsAdres().getPlaats());
				htput(ht,"Bedrijfsgegevens Adres vestigingsadres", formatAdres(bg.getVestigingsAdres()));
			} else{
				htput(ht,"Bedrijfsgegevens Postcode vestigingsadres", "");
				htput(ht,"Bedrijfsgegevens Plaats vestigingsadres", "");
				htput(ht,"Bedrijfsgegevens Adres vestigingsadres", "");
			}
		}
		
		if (bg != null){
			htput(ht,"Bedrijfsgegevens Email", bg.getEmailadres());
			htput(ht,"Bedrijfsgegevens Website", bg.getWebsite());
			htput(ht,"Bedrijfsgegevens KvK", bg.getKvknr());
			htput(ht,"Bedrijfsgegevens bankrekening", bg.getBankrekening());
			htput(ht,"Bedrijfsgegevens btwnummer", bg.getBtwnummer());
		}

		if (abd != null) {
			htput(ht,"Arbodienst Postcode", abd.getVestigingsAdres()
					.getPostcode());
			htput(ht,"Arbodienst Plaats", abd.getVestigingsAdres().getPlaats());
			htput(ht,"Arbodienst Naam", abd.getNaam());
			htput(ht,"Arbodienst Adres", formatAdres(abd.getVestigingsAdres()));
		} else {
			htput(ht,"Arbodienst Postcode", "");
			htput(ht,"Arbodienst Plaats", "");
			htput(ht,"Arbodienst Naam", "");
			htput(ht,"Arbodienst Adres", "");
		}

		htput(ht,"Afdeling Opmerkingen", "");
		if (afd != null && afd.getAfdeling() != null){
			htput(ht,"Afdeling Naam", afd.getAfdeling().getNaam());
			htput(ht,"Afdeling Extern Id", afd.getAfdeling().getAfdelingsid());
			if (afd.getAfdeling().getContactpersoon() != null) {
				htput(ht,"Afdeling Contactpersoon Voorvoegsel", afd.getAfdeling()
						.getContactpersoon().getVoorvoegsel());
				htput(ht,"Afdeling Contactpersoon Voornaam", afd.getAfdeling()
						.getContactpersoon().getVoornaam());
				htput(ht,"Afdeling Contactpersoon Voorletters", afd.getAfdeling()
						.getContactpersoon().getVoorletters());
				htput(ht,"Afdeling Contactpersoon Telefoon", afd.getAfdeling()
						.getContactpersoon().getTelefoon());
				htput(ht,"Afdeling Contactpersoon Mobiel", afd.getAfdeling()
						.getContactpersoon().getMobiel());
				htput(ht,"Afdeling Contactpersoon Geslacht", afd.getAfdeling()
						.getContactpersoon().getGeslacht().toString());
				htput(ht,"Afdeling Contactpersoon Geboortedatum", "");
				htput(ht,"Afdeling Contactpersoon Fax", "");
				htput(ht,"Afdeling Contactpersoon Email", afd.getAfdeling()
						.getContactpersoon().getEmailadres());
				htput(ht,"Afdeling Contactpersoon Achternaam", afd.getAfdeling()
						.getContactpersoon().getAchternaam());
			}
		}else{
			htput(ht,"Afdeling Naam", "");
			htput(ht,"Afdeling Extern Id", "");
		}

		return ht;
	}

	private List<String> ReadXMLFile (File fXmlFile){
		List<String> fileparts = new ArrayList<String>();
		try {
		 
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(fXmlFile);
		 
			//optional, but recommended
			//read this - http://stackoverflow.com/questions/13786607/normalization-in-dom-parsing-with-java-how-does-it-work
			doc.getDocumentElement().normalize();
		 
			// System.out.println("Root element :" + doc.getDocumentElement().getNodeName());
		 
			NodeList nList = doc.getElementsByTagName("Override");
		 
			//System.out.println("----------------------------");
		 
			for (int temp = 0; temp < nList.getLength(); temp++) {
		 
				Node nNode = nList.item(temp);
		 
				if (nNode.getNodeType() == Node.ELEMENT_NODE) {
		 
					Element eElement = (Element) nNode;
		 
					// System.out.println("Content type: " + eElement.getAttribute("ContentType"));
					String contentType = eElement.getAttribute("ContentType");
					if (contentType.equals("application/vnd.openxmlformats-officedocument.wordprocessingml.header+xml") ||
						contentType.equals("application/vnd.openxmlformats-officedocument.wordprocessingml.footer+xml") ||
						contentType.equals("application/vnd.openxmlformats-officedocument.wordprocessingml.document.main+xml")){

						fileparts.add(eElement.getAttribute("PartName"));
						
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return fileparts;
	}
	private void open(URI uri) {
		String os = System.getProperty("os.name").toLowerCase();
		try {
			if (os.indexOf("win") >= 0) {
				Runtime.getRuntime().exec(
						"rundll32 url.dll,FileProtocolHandler "
								+ "\""
								+ uri.getScheme()
								+ ":"
								+ URLDecoder.decode(
										uri.getSchemeSpecificPart(), "UTF-8")
								+ "\"");
			} else
				Runtime.getRuntime().exec(
						new String[] { "/usr/bin/open",
								URLDecoder.decode(uri.getPath(), "UTF-8") });
		} catch (IOException e) {
			JOptionPane.showMessageDialog(thisform, e.getMessage());
		}
	}
	private void generateWordDocRegex(HashMap<String, String> ht,
			String templatePathFilename, String outputPathFilename) {
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					new FileInputStream(templatePathFilename), "UTF-8"));

			new File(outputPathFilename);
			BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(
					new FileOutputStream(outputPathFilename), "UTF-8"));

			String thisLine;
			String pattern = "(#{1})([a-zA-Z0-9\\s/]{1,})(#{1})";
			Pattern regex = Pattern.compile(pattern, Pattern.CASE_INSENSITIVE);
			Matcher regexMatcher;
			while ((thisLine = reader.readLine()) != null) {

				//System.out.println("#!#" + thisLine);
				regexMatcher = regex.matcher(thisLine);
				while (regexMatcher.find()) {
					String value = ht.get(regexMatcher.group(2));
					if (value == null)
						continue;
					else
						thisLine = thisLine.replaceAll(regexMatcher.group(0),
								value);

				}

				// System.out.println("@!@" + thisLine);
				writer.write(thisLine);
				writer.newLine();
			}
			writer.close();
			reader.close();
		} catch (Exception e) {
			ExceptionLogger.ProcessException(e, thisform);
		}
	}

	
	public void GenerateDocument(VerzuimInfo vzm, DocumentTemplateInfo template) throws IOException{
		String localOutputfilename;
		verzuim = vzm;
		selectedFile = SelectFilename();
		if (selectedFile == null)
			return;
		else {
			if (selectedFile.exists()) {
				if (JOptionPane.showConfirmDialog(thisform, "Bestand "
						+ selectedFile.getAbsolutePath()
						+ " bestaat al. Wilt u het verwijderen?",
						"Verwijderen", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
					if (!selectedFile.delete()) {
						JOptionPane.showMessageDialog(thisform,
								"Kan bestand niet verwijderen.");
						return;
					}
				} else
					return;
			}
			targetdoc = new VerzuimDocumentInfo();
			targetdoc.setPadnaam(selectedFile.getAbsolutePath());
			targetdoc.setDocumentnaam(selectedFile.getName());
			targetdoc.setVerzuimId(verzuim.getId());
			targetdoc.setOmschrijving("");
			targetdoc.setAanmaakdatum(new Date());
			outputPathFilename = selectedFile.getAbsolutePath();
		}

		String templatePathFilename = template.getPadnaam();
		String tempDir = System.getProperty("user.home")
				+ File.separatorChar + "__tempdoc";
		zipper.unZipIt(templatePathFilename, tempDir);
		for (String filepart: ReadXMLFile(new File(tempDir + File.separator + "[Content_Types].xml")))
		{	
			String origWorddoc = tempDir + filepart;
			String newWorddoc = tempDir + filepart + ".new";
			generateWordDocRegex(populateHashTable(), origWorddoc,
					newWorddoc);
			new File(origWorddoc).delete();
			File nw = new File(newWorddoc);
			nw.renameTo(new File(origWorddoc));
		}

		zipper.addPath(new File(tempDir));
		localOutputfilename = tempDir + File.separator + "__convertedWorddoc";
		new File(localOutputfilename).delete();
		zipper.zipIt(localOutputfilename);
		Files.move(new File(localOutputfilename), new File(outputPathFilename));
		//Files.copy(new File(localOutputfilename), new File(outputPathFilename));
		
		//zipper.zipIt(outputPathFilename);
		removeDirectory(new File(tempDir));

		URI uri;
		try {

			uri = new URI("File://"
					+ URLEncoder.encode(outputPathFilename, "UTF-8"));
			open(uri);
		} catch (URISyntaxException e1) {
			ExceptionLogger.ProcessException(e1, thisform);
		} catch (UnsupportedEncodingException e1) {
			ExceptionLogger.ProcessException(e1, thisform);
		}
		if (JOptionPane.showConfirmDialog(thisform,
				"Wilt u het document aan het verzuim koppelen?",
				"Document koppelen", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
			try {
				verzuimRemote.addVerzuimDocument(targetdoc);
				//tpVerzuimdocumenten.populateTable_base();
			} catch (ValidationException e2) {
				ExceptionLogger.ProcessException(e2, thisform);
			} catch (PermissionException e2) {
				ExceptionLogger.ProcessException(e2, thisform);
			} catch (VerzuimApplicationException e) {
				ExceptionLogger.ProcessException(e, thisform);
			}
		}
	}
	private boolean removeDirectory(File directory) {

		if (directory == null)
			return false;
		if (!directory.exists())
			return true;
		if (!directory.isDirectory())
			return false;

		String[] list = directory.list();

		// Some JVMs return null for File.list() when the
		// directory is empty.
		if (list != null) {
			for (int i = 0; i < list.length; i++) {
				File entry = new File(directory, list[i]);

				// System.out.println("\tremoving entry " + entry);

				if (entry.isDirectory()) {
					if (!removeDirectory(entry))
						return false;
				} else {
					if (!entry.delete())
						return false;
				}
			}
		}

		return directory.delete();
	}

	protected File SelectFilename() {
		File selectedfile;
		JFileChooser fd = new JFileChooser();
		VerzuimProperties verzuimProps = new VerzuimProperties();
		String lastsavedir = (String) verzuimProps
				.getProperty(__verzuimproperty.lastdocsavedir);

		fd.setDialogType(JFileChooser.SAVE_DIALOG);
		fd.setFileSelectionMode(JFileChooser.FILES_ONLY);
		if (lastsavedir != null)
			fd.setCurrentDirectory(new File(lastsavedir));
		int retval = fd.showSaveDialog(thisform);
		if (retval == JFileChooser.APPROVE_OPTION) {
			selectedfile = fd.getSelectedFile();
			verzuimProps.setProperty(__verzuimproperty.lastdocsavedir,
					selectedfile.getParent());
			verzuimProps.saveProperties();
			String filename = selectedfile.getAbsolutePath();
			if (filename.endsWith(".docx"))
				return selectedfile;
			else {
				return new File(filename + ".docx");
			}
		} else
			return null;

	}
	
}
