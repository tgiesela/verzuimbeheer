package com.gieselaar.verzuimbeheer.services;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import com.gieselaar.verzuimbeheer.exceptions.ValidationException;
import com.gieselaar.verzuimbeheer.exceptions.VerzuimRuntimeException;

public class WerkgeverInfo extends InfoBase{
	/**
	 * 
	 */
	public enum __sortcol {
		NAAM;
	}
	private static final long serialVersionUID = 4894651595201554675L;
	/**
	 * 
	 */
	/**
	 * private variables 
	 */
	private Date einddatumcontract;
	private String naam;
	private Date startdatumcontract;
	private String telefoon;
	private List<AfdelingInfo> afdelings;
	private AdresInfo vestigingsAdres;
	private AdresInfo postAdres;
	private AdresInfo factuurAdres;
	private ContactpersoonInfo contactpersoon;
	private HoldingInfo holding;
	private ArbodienstInfo arbodienst;
//	private BedrijfsartsInfo bedrijfsarts;
	private Integer bedrijfsartsid;
	private UitvoeringsinstituutInfo uwv;
	private Integer holdingId;
	private Integer arbodienstId;
	private Integer uwvId;
	private List<PakketInfo> pakketten;
	private BigDecimal werkweek;
	private boolean actief;
	private String btwnummer;
	private Integer debiteurnummer;
	private boolean factureren;
	private boolean detailsecretariaat;
	private String emailadresfactuur;
	private ContactpersoonInfo contactpersoonfactuur;
	private String externcontractnummer;
	
	public WerkgeverInfo() {
		super();
	}
	@Override
	public boolean validate() throws ValidationException{
		if (this.getNaam() == null || this.getNaam().isEmpty())
			throw new ValidationException("Naam werkgever mag niet leeg zijn"); 
    	if (this.getStartdatumcontract() == null)
			throw new ValidationException("Startdatum mag niet leeg zijn"); 
    	if (this.getEinddatumcontract() == null){
    		/* ok */
    	} else {
    		if (this.getStartdatumcontract().compareTo(this.getEinddatumcontract()) > 0){
    			throw new ValidationException("Startdatum moet voor einddatum liggen");
    		}
    	}
    	if (this.getVestigingsAdres() == null || this.getVestigingsAdres().isEmtpy())
    		throw new ValidationException("Vestigingsadres is verplicht");
    	if (this.getWerkweek() == null)
    		throw new ValidationException("Werkweek is verplicht");
    	return true;
	}
	public List<PakketInfo> getPakketten() {
		return pakketten;
	}
	public void setPakketten(List<PakketInfo> pakketten) {
		this.pakketten = pakketten;
	}

	public Date getEinddatumcontract() {
		return einddatumcontract;
	}

	public void setEinddatumcontract(Date einddatumcontract) {
		this.einddatumcontract = einddatumcontract;
	}

	public String getNaam() {
		return naam;
	}

	public void setNaam(String naam) {
		this.naam = naam;
	}

	public Date getStartdatumcontract() {
		return startdatumcontract;
	}

	public void setStartdatumcontract(Date startdatumcontract) {
		this.startdatumcontract = startdatumcontract;
	}

	public String getTelefoon() {
		return telefoon;
	}

	public void setTelefoon(String telefoon) {
		this.telefoon = telefoon;
	}
	public boolean isActief() {
		actief = false;
		if ((this.getEinddatumcontract() == null) ||
			(this.getEinddatumcontract().after(new Date())))
			actief = true;

		return actief;
	}

	public boolean isActief(Date peildatum) {
		actief = false;
		if ((this.getEinddatumcontract() == null) ||
			(this.getEinddatumcontract().after(peildatum)))
			actief = true;

		return actief;
	}
	public List<AfdelingInfo> getAfdelings() {
		return afdelings;
	}

	public void setAfdelings(List<AfdelingInfo> afdelings) {
		this.afdelings = afdelings;
	}

	public AdresInfo getVestigingsAdres() {
		return vestigingsAdres;
	}

	public void setVestigingsAdres(AdresInfo vestigingsAdres) {
		if (vestigingsAdres == null)
			if (this.vestigingsAdres == null){
				/* 
				 * this is correct, it didn't exist before 
				 */
			} else {
				/* 
				 * attempt to delete an existing address
				 */
				this.vestigingsAdres.setAction(persistenceaction.DELETE);
			}
		this.vestigingsAdres = vestigingsAdres;
	}

	public AdresInfo getPostAdres() {
		return postAdres;
	}

	public void setPostAdres(AdresInfo postAdres) {
		if (postAdres == null)
			if (this.postAdres == null){
				/* 
				 * this is correct, it didn't exist before 
				 */
			} else {
				/* 
				 * attempt to delete an existing address
				 */
				this.postAdres.setAction(persistenceaction.DELETE);
			}
		this.postAdres = postAdres;
	}

	public AdresInfo getFactuurAdres() {
		return factuurAdres;
	}

	public void setFactuurAdres(AdresInfo factuurAdres) {
		if (factuurAdres == null){
			if (this.factuurAdres == null){
				/* 
				 * this is correct, it didn't exist before 
				 */
			} else {
				/* 
				 * attempt to delete an existing address
				 */
				this.factuurAdres.setAction(persistenceaction.DELETE);
			}
		}
		this.factuurAdres = factuurAdres;
	}

	public ContactpersoonInfo getContactpersoon() {
		return contactpersoon;
	}

	public void setContactpersoon(ContactpersoonInfo contactpersoon) {
		this.contactpersoon = contactpersoon;
	}

	public HoldingInfo getHolding() {
		return holding;
	}

	public void setHolding(HoldingInfo holding) {
		this.holding = holding;
	}

	public ArbodienstInfo getArbodienst() {
		return arbodienst;
	}
	public void setArbodienst(ArbodienstInfo arbodienst) {
		this.arbodienst = arbodienst;
	}
//	public BedrijfsartsInfo getBedrijfsarts() {
//		return bedrijfsarts;
//	}
//	public void setBedrijfsarts(BedrijfsartsInfo bedrijfsarts) {
//		this.bedrijfsarts = bedrijfsarts;
//	}
	public UitvoeringsinstituutInfo getUwv() {
		return uwv;
	}
	public void setUwv(UitvoeringsinstituutInfo uwv) {
		this.uwv = uwv;
	}
	public Integer getHoldingId() {
		return holdingId;
	}
	public void setHoldingId(Integer holdingId) {
		this.holdingId = holdingId;
	}
	public Integer getArbodienstId() {
		return arbodienstId;
	}
	public void setArbodienstId(Integer arbodienstid) {
		this.arbodienstId = arbodienstid;
	}
	public Integer getUwvId() {
		return uwvId;
	}
	public void setUwvId(Integer uwvId) {
		this.uwvId = uwvId;
	}
	public BigDecimal getWerkweek() {
		return werkweek;
	}
	public void setWerkweek(BigDecimal werkweek) {
		this.werkweek = werkweek;
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
	public boolean getFactureren() {
		return factureren;
	}
	public void setFactureren(boolean factureren) {
		this.factureren = factureren;
	}
	public ContactpersoonInfo getContactpersoonfactuur() {
		return contactpersoonfactuur;
	}
	public void setContactpersoonfactuur(ContactpersoonInfo contactpersoonfactuur) {
		this.contactpersoonfactuur = contactpersoonfactuur;
	}
	public boolean getDetailsecretariaat() {
		return detailsecretariaat;
	}
	public void setDetailsecretariaat(boolean detailsecretariaat) {
		this.detailsecretariaat = detailsecretariaat;
	}
	public void setActief(boolean actief) {
		this.actief = actief;
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
	public static class Werkgevercompare implements Comparator<WerkgeverInfo> {

		__sortcol sortcol;

		public Werkgevercompare(__sortcol column) {
			sortcol = column;
		}
		@Override
		public int compare(WerkgeverInfo o1, WerkgeverInfo o2) {
			if (sortcol == __sortcol.NAAM) {
				return o1.getNaam().compareToIgnoreCase(o2.getNaam());
			}else{
				throw new VerzuimRuntimeException(
						"Unknown sortcol in WerkgeverInfo comparator");
			}

		}
	}
	public static void sort(List<WerkgeverInfo> werkgevers, __sortcol col){
		Collections.sort(werkgevers, new Werkgevercompare(col));
	}
	public void setBedrijfsartsid(Integer bedrijfsartsid) {
		this.bedrijfsartsid = bedrijfsartsid;
	}
	public Integer getBedrijfsartsid(){
		return bedrijfsartsid;
	}
}
