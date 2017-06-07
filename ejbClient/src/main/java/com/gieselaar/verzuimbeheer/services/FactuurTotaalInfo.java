package com.gieselaar.verzuimbeheer.services;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.gieselaar.verzuimbeheer.exceptions.VerzuimRuntimeException;
import com.gieselaar.verzuimbeheer.services.HoldingInfo.__factuurtype;

public class FactuurTotaalInfo extends FactuurInfo {

	/**
	 * 
	 */
	public enum __sortcol {
		NAAM,
		HOLDINGID;
	}
	private static final long serialVersionUID = 1L;

	private BigDecimal somitembedrag;
	private BigDecimal somitembtwbedraglaag;
	private BigDecimal somitembtwbedraghoog;
	private BigDecimal somuurkosten;
	private BigDecimal somvastekosten;
	private BigDecimal somkilometerkosten;
	private BigDecimal somoverigekostensecretariaat;
	private BigDecimal somcasemanagementkosten;
	private BigDecimal somoverigekostenbezoek;
	private BigDecimal somsecretariaatskosten;
	private BigDecimal somOmzetOnbelast;
	private BigDecimal somOmzetBTWLaag;
	private BigDecimal somOmzetBTWHoog;
	private BigDecimal somGeenomzetOnbelast;
	private BigDecimal somGeenomzetBTWLaag;
	private BigDecimal somGeenomzetBTWHoog;
	private BigDecimal sombetalingen;
	private WerkgeverInfo werkgever;
	private boolean vasttariefhuisbezoeken;
	private boolean gesommeerd;
	private boolean omzetbepaald;
	private Integer holdingFactuurId;

	public FactuurTotaalInfo() {
		super();
		somitembedrag = new BigDecimal(0);
		somitembtwbedraglaag = new BigDecimal(0);
		somitembtwbedraghoog = new BigDecimal(0);
		somuurkosten = new BigDecimal(0);
		somvastekosten = new BigDecimal(0);
		somkilometerkosten = new BigDecimal(0);
		somoverigekostensecretariaat = new BigDecimal(0);
		somcasemanagementkosten = new BigDecimal(0);
		somoverigekostenbezoek = new BigDecimal(0);
		somsecretariaatskosten = new BigDecimal(0);
		somOmzetOnbelast = new BigDecimal(0);
		somOmzetBTWLaag = new BigDecimal(0);
		somOmzetBTWHoog = new BigDecimal(0);
		somGeenomzetOnbelast = new BigDecimal(0);
		somGeenomzetBTWLaag = new BigDecimal(0);
		somGeenomzetBTWHoog = new BigDecimal(0);
		setSombetalingen(new BigDecimal(0));
		setGesommeerd(false);
		omzetbepaald = false;
	}

	public BigDecimal getSomitembedrag() {
		return somitembedrag;
	}

	public void setSomitembedrag(BigDecimal somitembedrag) {
		this.somitembedrag = somitembedrag;
	}

	public BigDecimal getSomitembtwbedraglaag() {
		return somitembtwbedraglaag;
	}

	public void setSomitembtwbedraglaag(BigDecimal somitembtwbedrag) {
		this.somitembtwbedraglaag = somitembtwbedrag;
	}

	public BigDecimal getSomitembtwbedraghoog() {
		return somitembtwbedraghoog;
	}

	public void setSomitembtwbedraghoog(BigDecimal somitembtwbedrag) {
		this.somitembtwbedraghoog = somitembtwbedrag;
	}

	public BigDecimal getSomuurkosten() {
		return somuurkosten;
	}

	public void setSomuurkosten(BigDecimal somuurkosten) {
		this.somuurkosten = somuurkosten;
	}

	public BigDecimal getSomvastekosten() {
		return somvastekosten;
	}

	public void setSomvastekosten(BigDecimal somvastekosten) {
		this.somvastekosten = somvastekosten;
	}

	public BigDecimal getSomkilometerkosten() {
		return somkilometerkosten;
	}

	public void setSomkilometerkosten(BigDecimal somkilometerkosten) {
		this.somkilometerkosten = somkilometerkosten;
	}

	public BigDecimal getSomoverigekostensecretariaat() {
		return somoverigekostensecretariaat;
	}

	public void setSomoverigekostensecretariaat(
			BigDecimal somoverigekostensecretariaat) {
		this.somoverigekostensecretariaat = somoverigekostensecretariaat;
	}

	public BigDecimal getSomcasemanagementkosten() {
		return somcasemanagementkosten;
	}

	public void setSomcasemanagementkosten(BigDecimal somcasemanagementkosten) {
		this.somcasemanagementkosten = somcasemanagementkosten;
	}

	public BigDecimal getSomoverigekostenbezoek() {
		return somoverigekostenbezoek;
	}

	public void setSomoverigekostenbezoek(BigDecimal somoverigekostenbezoek) {
		this.somoverigekostenbezoek = somoverigekostenbezoek;
	}

	public BigDecimal getSomsecretariaatskosten() {
		return somsecretariaatskosten;
	}

	public void setSomsecretariaatskosten(BigDecimal somsecretariaatskosten) {
		this.somsecretariaatskosten = somsecretariaatskosten;
	}

	private void setOmzetBedragen(FactuurregelitemInfo fci){
		switch (fci.getFactuurcategorie().getBtwcategorie()) {
		case HOOG:
			somOmzetBTWHoog = somOmzetBTWHoog.add(fci.getBedrag());
			break;
		case LAAG:
			somOmzetBTWLaag = somOmzetBTWLaag.add(fci.getBedrag());
			break;
		case NVT:
			somOmzetOnbelast = somOmzetOnbelast
					.add(fci.getBedrag());
			break;
		default:
			break;

		}
	}
	private void setGeenOmzetBedragen(FactuurregelitemInfo fci){
		switch (fci.getFactuurcategorie().getBtwcategorie()) {
		case HOOG:
			somGeenomzetBTWHoog = somGeenomzetBTWHoog.add(fci
					.getBedrag());
			break;
		case LAAG:
			somGeenomzetBTWLaag = somGeenomzetBTWLaag.add(fci
					.getBedrag());
			break;
		case NVT:
			somGeenomzetOnbelast = somGeenomzetOnbelast.add(fci
					.getBedrag());
			break;
		default:
			break;
		}
	}
	private void setBedragen(){
		for (FactuurregelitemInfo fci : this.getFactuurregelitems()) {
			if (fci.getFactuurcategorie().isIsomzet()) {
				setOmzetBedragen(fci);
			} else {
				setGeenOmzetBedragen(fci);
			}
		}
	}

	private void bepaalomzetbedragen() {
		if (omzetbepaald)
			return;
		omzetbepaald = true;
		if (this.getFactuurregelitems() != null) {
			setBedragen();
		}
	}

	public BigDecimal getSomGeenomzetOnbelast() {
		bepaalomzetbedragen();
		return somGeenomzetOnbelast;
	}

	public BigDecimal getSomOmzetBTWLaag() {
		bepaalomzetbedragen();
		return somOmzetBTWLaag;
	}

	public BigDecimal getSomOmzetBTWHoog() {
		bepaalomzetbedragen();
		return somOmzetBTWHoog;
	}

	public BigDecimal getSomOmzetOnbelast() {
		bepaalomzetbedragen();
		return somOmzetOnbelast;
	}

	public BigDecimal getSomGeenomzetBTWLaag() {
		bepaalomzetbedragen();
		return somGeenomzetBTWLaag;
	}

	public BigDecimal getSomGeenomzetBTWHoog() {
		bepaalomzetbedragen();
		return somGeenomzetBTWHoog;
	}

	public BigDecimal getSombetalingen() {
		return sombetalingen;
	}

	public void setSombetalingen(BigDecimal sombetalingen) {
		this.sombetalingen = sombetalingen;
	}

	public WerkgeverInfo getWerkgever() {
		return werkgever;
	}

	public void setWerkgever(WerkgeverInfo werkgever) {
		this.werkgever = werkgever;
	}

	public boolean isVasttariefhuisbezoeken() {
		return vasttariefhuisbezoeken;
	}

	public void setVasttariefhuisbezoeken(boolean vasttariefhuisbezoeken) {
		this.vasttariefhuisbezoeken = vasttariefhuisbezoeken;
	}

	public BigDecimal getTotaalExBtw() {
		return getAansluitkosten()
				.add(somuurkosten.add(somsecretariaatskosten.add(somkilometerkosten.add(somoverigekostenbezoek.add(somoverigekostensecretariaat
						.add(somcasemanagementkosten.add(getAbonnementskosten()
								.add(somitembedrag
										.add(getMaandbedragsecretariaat().add(
												somvastekosten))))))))));
	}

	public BigDecimal getTotaalInclBtw() {
		return getTotaalExBtw().add(getTotaalBtwLaag()).add(getTotaalBtwHoog().setScale(2, RoundingMode.HALF_UP));
	}

	public BigDecimal getTotaalBtwLaag() {
		return somitembtwbedraglaag;
	}

	public BigDecimal getTotaalBtwHoog() {
		BigDecimal bedrag = getTotaalExBtw().subtract(somitembedrag);
		return somitembtwbedraghoog.add(bedrag.multiply(getBtwpercentagehoog()
				.divide(new BigDecimal(100))));
	}

	public Integer getHoldingFactuurId() {
		return holdingFactuurId;
	}

	public void setHoldingFactuurId(Integer holdingFactuurId) {
		this.holdingFactuurId = holdingFactuurId;
	}

	public boolean isGesommeerd() {
		return gesommeerd;
	}

	public void setGesommeerd(boolean gesommeerd) {
		this.gesommeerd = gesommeerd;
	}

	public boolean getPrintSubreports() {
		/*
		 * Voor normale facturen worden de subreports altijd afgedrukt. Voor
		 * gesommeerde facturen, worden de subreports alleen afgedrukt als het
		 * om een geaggregeerd factuurtype gaat.
		 */
		if (this.isGesommeerd()) {
			if (this.getWerkgever() == null) {
				return false;
			}
			return this.getWerkgever().getHolding().getFactuurtype() == __factuurtype.GEAGGREGEERD; 
		} else {
			return true;
		}

	}

	public static FactuurTotaalInfo sommeer(List<FactuurTotaalInfo> facturen) {
		int i;
		boolean found;
		FactuurTotaalInfo som;
		FactuurTotaalInfo eerstefactuur;
		i = 0;
		found = false;
		som = new FactuurTotaalInfo();
		som.setHoldingFactuurId(-1);
		while (i < facturen.size() && !found) {
			if (facturen.get(i).getWerkgeverid() == -1) {
				i++;
				// TODO Onder bijzondere omstandigheden kan een Holding zonder werkgevers voorkomen bij afsluiten maand.
				//		Dat geeft hier problemen bij de aanname dat er altijd 1 volgende factuur is!!
				som.setHoldingFactuurId(facturen.get(i).getId());
			} else {
				found = true;
			}
		}
		if (!found)
			return null;
		eerstefactuur = facturen.get(i);
		som.setAanmaakdatum(eerstefactuur.getAanmaakdatum());
		som.setAansluitkostenperiode(eerstefactuur.getAansluitkostenperiode());
		som.setAbonnementskostenperiode(eerstefactuur
				.getAbonnementskostenperiode());
		som.setBtwpercentagehoog(eerstefactuur.getBtwpercentagehoog());
		som.setBtwpercentagelaag(eerstefactuur.getBtwpercentagelaag());
		som.setFactuurnr(eerstefactuur.getFactuurnr());
		som.setFactuurstatus(facturen.get(0).getFactuurstatus()); /*
																 * Alleen van de
																 * holding
																 * factuur wordt
																 * de status
																 * aangepast
																 */
		som.setHoldingid(eerstefactuur.getHoldingid());
		som.setJaar(eerstefactuur.getJaar());
		som.setMaand(eerstefactuur.getMaand());
		som.setOmschrijvingfactuur(eerstefactuur.getOmschrijvingfactuur());
		som.setPdflocation(eerstefactuur.getPdflocation());
		som.setPeildatumaansluitkosten(eerstefactuur
				.getPeildatumaansluitkosten());
		som.setTariefid(eerstefactuur.getTariefid());
		som.setVasttariefhuisbezoeken(eerstefactuur.isVasttariefhuisbezoeken());
		som.setWerkgeverid(eerstefactuur.getWerkgeverid());
		som.setWerkgever(eerstefactuur.getWerkgever());
		for (i = 0; i < facturen.size(); i++) {
			FactuurTotaalInfo fti = facturen.get(i);
			som.setAansluitkosten(som.getAansluitkosten().add(
					fti.getAansluitkosten()));
			som.setAantalmedewerkers(som.getAantalmedewerkers()
					+ fti.getAantalmedewerkers());
			som.setAbonnementskosten(som.getAbonnementskosten().add(
					fti.getAbonnementskosten()));
			som.setMaandbedragsecretariaat(som.getMaandbedragsecretariaat()
					.add(fti.getMaandbedragsecretariaat()));
			som.setSomcasemanagementkosten(som.getSomcasemanagementkosten()
					.add(fti.getSomcasemanagementkosten()));
			som.setSomitembedrag(som.getSomitembedrag().add(
					fti.getSomitembedrag()));
			som.setSomitembtwbedraghoog(som.getSomitembtwbedraghoog().add(
					fti.getSomitembtwbedraghoog()));
			som.setSomitembtwbedraglaag(som.getSomitembtwbedraglaag().add(
					fti.getSomitembtwbedraglaag()));
			som.setSomkilometerkosten(som.getSomkilometerkosten().add(
					fti.getSomkilometerkosten()));
			som.setSomoverigekostenbezoek(som.getSomoverigekostenbezoek().add(
					fti.getSomoverigekostenbezoek()));
			som.setSomoverigekostensecretariaat(som
					.getSomoverigekostensecretariaat().add(
							fti.getSomoverigekostensecretariaat()));
			som.setSomsecretariaatskosten(som.getSomsecretariaatskosten().add(
					fti.getSomsecretariaatskosten()));
			som.setSomuurkosten(som.getSomuurkosten()
					.add(fti.getSomuurkosten()));
			som.setSomvastekosten(som.getSomvastekosten().add(
					fti.getSomvastekosten()));
			if (fti.getFactuurregelbezoeken() != null) {
				if (som.getFactuurregelbezoeken() == null)
					som.setFactuurregelbezoeken(new ArrayList<FactuurregelbezoekInfo>());
				som.getFactuurregelbezoeken().addAll(
						fti.getFactuurregelbezoeken());
			}
			if (fti.getFactuurregelitems() != null) {
				if (som.getFactuurregelitems() == null)
					som.setFactuurregelitems(new ArrayList<FactuurregelitemInfo>());
				som.getFactuurregelitems().addAll(fti.getFactuurregelitems());
			}
			if (fti.getFactuurregelsecretariaat() != null) {
				if (som.getFactuurregelsecretariaat() == null)
					som.setFactuurregelsecretariaat(new ArrayList<FactuurregelsecretariaatInfo>());
				som.getFactuurregelsecretariaat().addAll(
						fti.getFactuurregelsecretariaat());
			}
			som.setSombetalingen(som.getSombetalingen()
					.add(fti.getSombetalingen()));
		}
		som.setGesommeerd(true);
		som.setId(-1);
		return som;
	}

	public static class Factuurcompare implements Comparator<FactuurTotaalInfo> {

		__sortcol sortcol;

		public Factuurcompare(__sortcol column) {
			sortcol = column;
		}

		@Override
		public int compare(FactuurTotaalInfo o1, FactuurTotaalInfo o2) {
			switch (sortcol) {
			case NAAM:
				return o1.getWerkgever().getNaam()
						.compareToIgnoreCase(o2.getWerkgever().getNaam());
			case HOLDINGID:
				return o1.getHoldingid().compareTo(o2.getHoldingid());
			default:
				throw new VerzuimRuntimeException(
						"Unknown sortcol in FactuurTotaalInfo comparator");
			}

		}
	}
	public static void sort(List<FactuurTotaalInfo> facturen, __sortcol col){
		Collections.sort(facturen, new Factuurcompare(col));
	}

}
