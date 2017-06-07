package com.gieselaar.verzuim.models;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.gieselaar.transactieparsers.Mt940Entry;
import com.gieselaar.transactieparsers.Mt940File;
import com.gieselaar.transactieparsers.Mt940Parser;
import com.gieselaar.transactieparsers.Mt940Record;
import com.gieselaar.transactieparsers.RaboCSVEntry;
import com.gieselaar.transactieparsers.RaboCSVEntry.DebitCreditIndicator;
import com.gieselaar.transactieparsers.RaboCSVFile;
import com.gieselaar.transactieparsers.RaboCVSParser;
import com.gieselaar.verzuim.controllers.MDIMainController;
import com.gieselaar.verzuim.interfaces.ModelEventListener;
import com.gieselaar.verzuimbeheer.exceptions.PermissionException;
import com.gieselaar.verzuimbeheer.exceptions.ValidationException;
import com.gieselaar.verzuimbeheer.exceptions.VerzuimApplicationException;
import com.gieselaar.verzuimbeheer.facades.LoginSessionRemote;
import com.gieselaar.verzuimbeheer.services.FactuurTotaalInfo;
import com.gieselaar.verzuimbeheer.services.FactuurbetalingInfo;
import com.gieselaar.verzuimbeheer.services.HoldingInfo;
import com.gieselaar.verzuimbeheer.services.ImportBetalingInfo;
import com.gieselaar.verzuimbeheer.services.WerkgeverInfo;
import com.gieselaar.verzuimbeheer.utils.ServiceCaller;
import com.gieselaar.verzuimbeheer.utils.ServiceLocatorException;

public class BetalingenModel extends AbstractModel {
	private static final long serialVersionUID = 1L;
	private List<FactuurbetalingInfo> betalingen;
	private List<ImportBetalingInfo> importedbetalingen;
	private List<FactuurTotaalInfo> facturen;
	private Integer factuurid;
	private FactuurModel factuurmodel;
	private List<WerkgeverInfo> werkgevers;
	private List<HoldingInfo> holdings;
	private HashMap<String, Integer> accnrsHolding = new HashMap<>();
	private HashMap<String, Integer> accnrsWerkgever = new HashMap<>();
	private HashMap<Integer, FactuurbetalingInfo> hmbetalingen = new HashMap<>();

	public BetalingenModel(LoginSessionRemote session){
		super(session);
		factuurmodel = new FactuurModel(session);
	}

	public void selectBetalingen(Integer factuurId) throws VerzuimApplicationException {
		try {
			factuurid = factuurId;
			betalingen = ServiceCaller.factuurFacade(this.getSession()).getFactuurbetalingenForFactuur(factuurId);
			for (ModelEventListener ml: this.changelisteners){
				ml.listComplete(betalingen);
			}
		} catch (PermissionException | ServiceLocatorException e) {
			throw new VerzuimApplicationException(e, e.getMessage());
		}
	}

	public List<FactuurbetalingInfo> getBetalingenList() {
		return betalingen;
	}

	@Override
	public void refreshDatabase() throws VerzuimApplicationException {
		try {
			selectBetalingen(factuurid);
		} catch (VerzuimApplicationException e) {
			throw new VerzuimApplicationException(e, e.getMessage());
		}
	}

	public void addFactuurbetaling(FactuurbetalingInfo info) throws VerzuimApplicationException {
		try {
			FactuurbetalingInfo todo = ServiceCaller.factuurFacade(getSession()).addFactuurbetaling(info);
			betalingen.add(todo);
			for (ModelEventListener ml: this.changelisteners){
				ml.rowAdded(todo);
			}
		} catch (PermissionException | ServiceLocatorException | ValidationException e) {
			throw new VerzuimApplicationException(e, e.getMessage());
		}	
	}

	public void saveFactuurbetaling(FactuurbetalingInfo info) throws VerzuimApplicationException {
		try {
			FactuurbetalingInfo updatedinfo = ServiceCaller.factuurFacade(getSession()).updateFactuurbetaling(info);
			/* Now also the list has to be updated */
			for (FactuurbetalingInfo w: betalingen){
				if (w.getId().equals(info.getId())){
					betalingen.remove(w);
					break;
				}
			}
			betalingen.add(updatedinfo);
			for (ModelEventListener ml: this.changelisteners){
				ml.rowUpdated(updatedinfo);
			}
		} catch (PermissionException | ServiceLocatorException | ValidationException e) {
			throw new VerzuimApplicationException(e, e.getMessage());
		}	
	}

	public void deleteFactuurbetaling(FactuurbetalingInfo info) throws VerzuimApplicationException {
		try {
			ServiceCaller.factuurFacade(getSession()).deleteFactuurbetaling(info);
			for (FactuurbetalingInfo w: betalingen){
				if (w.getId().equals(info.getId())){
					betalingen.remove(w);
					break;
				}
			}
			for (ModelEventListener ml: this.changelisteners){
				ml.rowDeleted(info);
			}
		} catch (PermissionException | ServiceLocatorException | ValidationException e) {
			throw new VerzuimApplicationException(e, e.getMessage());
		}	
	}

	public void selectBetalingenAll() throws VerzuimApplicationException {
		try {
			betalingen = ServiceCaller.factuurFacade(getSession()).getFactuurbetalingen();
		} catch (PermissionException | ServiceLocatorException | ValidationException e) {
			throw new VerzuimApplicationException(e, e.getMessage());
		}	
	}
	/* 
	 * Stuff for import betalingen
	 */
	public void importeerbetalingen(String docnaam
								  , String separator
								  , boolean raboformat
								  , int periodlengthinmothns
								  , MDIMainController maincontroller) throws VerzuimApplicationException {
		List<FactuurbetalingInfo> factuurbetalingen;
		Integer id = 1;
		Date lastmonth = new Date();
		Calendar cal = Calendar.getInstance();
		cal.setTime(lastmonth);
		lastmonth = cal.getTime();
		cal.add(Calendar.MONTH, -periodlengthinmothns); 

		Date firstmonth = cal.getTime();
		
		werkgevers = maincontroller.getWerkgevers();
		holdings = maincontroller.getHoldings();
		
		LineNumberReader reader;
		try {
			/*
			 * Facturen en betalingen opvragen
			 * Hashmaps aanmaken om zoeken te versnellen
			 */
			facturen = factuurmodel.getFacturenInPeriode(firstmonth, new Date(), false);
			selectBetalingenAll();
			factuurbetalingen = getBetalingenList();
			createHashMaps(factuurbetalingen, facturen);
			
			/*
			 * Actual import
			 */
			
			importedbetalingen = new ArrayList<>();
			reader = new LineNumberReader(new InputStreamReader(new FileInputStream(docnaam)));
			if (!raboformat) {
				Mt940Parser mt940 = new Mt940Parser();
				Mt940File mt940file = mt940.parse(reader);
				for (Mt940Record record : mt940file.getRecords()) {
					for (Mt940Entry entry : record.getEntries()) {
						if (entry.getDebitCreditIndicator() == Mt940Entry.DebitCreditIndicator.CREDIT) {
							ImportBetalingInfo ibi = new ImportBetalingInfo();
							ibi.setId(id);
							id = id + 1;
							ibi.setDatumbetaling(entry.getValutaDatum());
							ibi.setBedrag(entry.getAmount());
							ibi.setRekeningnummerBetaler(entry.getIban());
							ibi.setBedrijfsnaam(entry.getName());
							ibi.setOmschrijving1(entry.getOmschrijvingen());

							FactuurTotaalInfo factuur = selecteerFactuur(ibi);
							if (factuur != null) {
								ibi.setFactuurnummer(factuur.getFactuurnr());
								if (ibi.getWerkgeverid() == null) {
									ibi.setWerkgeverid(factuur.getWerkgeverid());
								}
								if (ibi.getHoldingid() == null) {
									ibi.setHoldingid(factuur.getHoldingid());
								}
								ibi.setFactuurbedrag(factuur.getTotaalInclBtw());
								ibi.setFactuur(factuur);
							} else {
								ibi.setFactuur(null);
							}

							importedbetalingen.add(ibi);
						}
					}
				}
			} else {
				RaboCVSParser rabo = new RaboCVSParser(separator);
				RaboCSVFile rabofile;
					rabofile = rabo.parse(reader);
				for (RaboCSVEntry entry : rabofile.getRecords()) {
					if (entry.getDebitCreditIndicator().equals(DebitCreditIndicator.CREDIT)) {
						ImportBetalingInfo ibi = new ImportBetalingInfo();
						ibi.setId(id);
						id = id + 1;
						ibi.setDatumbetaling(entry.getValutaDatum());
						ibi.setBedrag(entry.getAmount());
						ibi.setRekeningnummerBetaler(entry.getIban());
						ibi.setBedrijfsnaam(entry.getName());
						ibi.setOmschrijving1(entry.getOmschrijvingen());

						FactuurTotaalInfo factuur = selecteerFactuur(ibi);
						if (factuur != null) {
							ibi.setFactuurnummer(factuur.getFactuurnr());
							if (ibi.getWerkgeverid() == null) {
								ibi.setWerkgeverid(factuur.getWerkgeverid());
							}
							if (ibi.getHoldingid() == null) {
								ibi.setHoldingid(factuur.getHoldingid());
							}
							ibi.setFactuurbedrag(factuur.getTotaalInclBtw());
							ibi.setFactuur(factuur);
						} else {
							ibi.setFactuur(null);
						}

						importedbetalingen.add(ibi);
					}
				}
			}
			for (ModelEventListener ml: this.changelisteners){
				ml.listComplete(importedbetalingen);
			}
		} catch (IOException | ValidationException | ParseException e) {
			throw new VerzuimApplicationException(e, "Kan bestand niet importeren");
		}
		
	}
	private FactuurTotaalInfo selecteerFactuur(ImportBetalingInfo ibi) {
		Calendar cal = Calendar.getInstance();
		FactuurTotaalInfo factuur = null;
		List<FactuurTotaalInfo> localfacturen = new ArrayList<>();
		HoldingInfo holding = null;
		WerkgeverInfo werkgever = null;

		werkgever = findWerkgeverByRekeningNr(ibi.getRekeningnummerBetaler());
		if (werkgever == null) {
			werkgever = findKlantByNaam(ibi.getBedrijfsnaam());
		} else {
			ibi.setWerkgeverid(werkgever.getId());
		}
		if (werkgever == null) {
			holding = findHoldingByRekeningNr(ibi.getRekeningnummerBetaler());
			if (holding == null) {
				holding = findHoldingByNaam(ibi.getBedrijfsnaam());
			} else {
				ibi.setHoldingid(holding.getId());
			}
		}
		String tokens[] = (ibi.getOmschrijving1() + ibi.getOmschrijving2() + ibi.getOmschrijving3())
				.split("\\s|,|:|;|/|>|<|-");
		for (int i = 0; i < tokens.length; i++) {
			String factuurnr = tokens[i];
			if (factuurnr.length() < 7) {
				/*
				 * Soms wordt een factuurnummer over twee regels verdeeld Dan
				 * staat er uiteindelijk bijvoorbeeld 20 16123. Dit proberen we
				 * te herstellen.
				 */
				if (i > 0) {
					int totallen = tokens[i].length() + tokens[i - 1].length();
					if (StringUtils.isNumeric(tokens[i]) && StringUtils.isNumeric(tokens[i - 1])
							&& (totallen == 7 || totallen == 8)) {
						factuurnr = tokens[i - 1] + tokens[i];
					}
				}
			}
			if (factuurnr.length() == 7 || factuurnr.length() == 8) {
				if (StringUtils.isNumeric(factuurnr)) {
					String jaar = factuurnr.substring(0, 4);
					cal.setTime(ibi.getDatumbetaling());
					if (cal.get(Calendar.YEAR) == Integer.parseInt(jaar)
							|| (cal.get(Calendar.YEAR) + 1) == Integer.parseInt(jaar)) {
						factuur = findFactuurByFactuurnr(Integer.parseInt(factuurnr), ibi.getWerkgeverid(),
								ibi.getHoldingid(),ibi.getBedrag());
						if (factuur == null) {
							/*
							 * Let op: NOODOPLOSSING! Soms is de maand
							 * teruggedraaid geweest en verschillen de
							 * factuurnummers 1 cijfertje doordat er een bedrijf
							 * is tussengevoegd.
							 */
							factuur = findFactuurByFactuurnr(Integer.parseInt(factuurnr) - 1, ibi.getWerkgeverid(),
									ibi.getHoldingid(),ibi.getBedrag());
						}
						if (factuur == null && factuurnr.length() == 7) {
							/*
							 * Probeer het nog eens, maar nu met een nul er
							 * tussen ivm fout eind 2015 in toekennen
							 * factuurnummers
							 */
							String gewijzigdfactuurnr = factuurnr.substring(0, 4) + "0" + factuurnr.substring(4, 7);
							factuur = findFactuurByFactuurnr(Integer.parseInt(gewijzigdfactuurnr), ibi.getWerkgeverid(),
									ibi.getHoldingid(),ibi.getBedrag());
							if (factuur != null) {
								factuurnr = gewijzigdfactuurnr;
							}
						}
						if (factuur != null){
							localfacturen.add(factuur);
						}
					}
				}
			}
		}
		if (localfacturen.size() > 1) {
			for (FactuurTotaalInfo fti : localfacturen) {
				BigDecimal totaal = fti.getTotaalInclBtw();
				totaal = totaal.setScale(2, RoundingMode.HALF_UP);
				if (totaal.compareTo(ibi.getBedrag()) == 0) {
					return fti;
				}
			}
			/* Geen passend bedrag gevonden, dan geven we de eerste terug */
			return localfacturen.get(0);
		} else {
			if (localfacturen.size() == 1)
				return localfacturen.get(0);
			else
				return null;
		}
	}
	private WerkgeverInfo findWerkgeverByRekeningNr(String rekeningnummerBetaler) {
		Integer werkgeverid;
		werkgeverid = accnrsWerkgever.get(rekeningnummerBetaler);
		if (werkgeverid != null) {
			for (WerkgeverInfo wgr : werkgevers) {
				if (wgr.getId().equals(werkgeverid)) {
					return wgr;
				}
			}
		}
		return null;
	}

	private HoldingInfo findHoldingByRekeningNr(String rekeningnummerBetaler) {
		Integer holdingid;
		holdingid = accnrsHolding.get(rekeningnummerBetaler);
		if (holdingid != null) {
			for (HoldingInfo h : holdings) {
				if (h.getId().equals(holdingid)) {
					return h;
				}
			}
		}
		return null;
	}
	private WerkgeverInfo findKlantByNaam(String bedrijfsnaam) {
		for (WerkgeverInfo wgi : werkgevers) {
			if (wgi.getNaam().equalsIgnoreCase(bedrijfsnaam))
				return wgi;
		}
		return null;
	}
	private HoldingInfo findHoldingByNaam(String bedrijfsnaam) {
		for (HoldingInfo hi : holdings) {
			if (hi.getNaam().equalsIgnoreCase(bedrijfsnaam))
				return hi;
		}
		return null;
	}
	/**
	 * importeerbetalingen must be called before!
	 * 
	 * @param factuurnr
	 * @param werkgeverid
	 * @param holdingid
	 * @param bedrag
	 * @return
	 */
	public FactuurTotaalInfo findFactuurByFactuurnr
		( int factuurnr
		, Integer werkgeverid
		, Integer holdingid
		, BigDecimal bedrag) {
		for (FactuurTotaalInfo fti : facturen) {
			if (fti.getFactuurnr() == factuurnr) {
				if (fti.getTotaalInclBtw().compareTo(BigDecimal.ZERO) != 0) {
					/*
					 * Facturen met bedrag nul slaan we over, want daar kunnen
					 * geen betalingen voor zijn
					 */
					if (werkgeverid != null) {
						if (fti.getWerkgeverid().equals(werkgeverid)) {
							/*
							 * match gevonden
							 */
							return fti;
						}
					} else {
						if (holdingid != null && fti.getHoldingid() != null) {
							if (fti.getHoldingid().equals(holdingid)) {
								return fti;
							}
						} else {
							if (fti.getTotaalInclBtw().compareTo(bedrag)==0){
								return fti;
							}
						}
					}
				} else {
					return null;
				}
			}
		}
		return null;
	}
	private void createHashMaps(List<FactuurbetalingInfo> factuurbetalingen, List<FactuurTotaalInfo> facturen) {
		for (FactuurbetalingInfo fbi : factuurbetalingen) {
			if (fbi.getRekeningnummerbetaler().isEmpty()) {
				/* skip empty ones*/
			} else {
				hmbetalingen.put(fbi.getFactuurid(), fbi);
			}
		}
		for (FactuurTotaalInfo fti : facturen) {
			if (fti.getSombetalingen().compareTo(BigDecimal.ZERO) == 0) {
				/* Hier zitten geen betalingen bij */
			} else {
				FactuurbetalingInfo fbi = hmbetalingen.get(fti.getId());
				if (fbi != null) {
					if (fti.getWerkgeverid() != null) {
						if (accnrsWerkgever.containsKey(fbi.getRekeningnummerbetaler())) {
							;
						} else {
							accnrsWerkgever.put(fbi.getRekeningnummerbetaler(), fti.getWerkgeverid());
						}
					}
					if (fti.getHoldingid() != null) {
						if (accnrsHolding.containsKey(fbi.getRekeningnummerbetaler())) {
							;
						} else {
							accnrsHolding.put(fbi.getRekeningnummerbetaler(), fti.getHoldingid());
						}
					}
				}
			}
		}
		
	}

	public List<ImportBetalingInfo> getImportedbetalingenList() {
		return importedbetalingen;
	}

	public void saveImportbetaling(ImportBetalingInfo info) {
		/* Now the list has to be updated */
		for (ModelEventListener ml: this.changelisteners){
			ml.rowUpdated(info);
		}	
	}

	public void deleteImportBetaling(ImportBetalingInfo info) {
		for (ImportBetalingInfo w: importedbetalingen){
			if (w.getId().equals(info.getId())){
				importedbetalingen.remove(w);
				break;
			}
		}
		for (ModelEventListener ml: this.changelisteners){
			ml.rowDeleted(info);
		}	
		
	}
}
