package com.gieselaar.verzuimbeheer.entities;

import java.io.Serializable;

import javax.persistence.*;

import com.gieselaar.verzuimbeheer.entitiesutils.EntityBaseId;


/**
 * The persistent class for the ACTIVITEIT database table.
 * 
 */
@Entity
public class Activiteit extends EntityBaseId implements Serializable {
	private static final long serialVersionUID = 1L;

//	@Id
//	@GeneratedValue(strategy=GenerationType.IDENTITY)
//	private int id;

	private int allewerkgevers;

	private int deadlineperiode;

	private int deadlineperiodesoort;

	private int deadlinestartpunt;

	private int deadlinewaarschuwmoment;

	private int deadlinewaarschuwmomentsoort;

	private String duur;

	private int ketenverzuim;

	private String naam;

	private int normaalverzuim;

	private String omschrijving;

	private Integer plannaactiviteit;

	private int repeteeraantal;

	private int repeteerperiode;

	private int repeteerperiodesoort;

	private int vangnet;
	
	private Integer vangnettype;

	private int verwijdernaherstel;

    public Activiteit() {
    }
//	public int getId() {
//		return this.id;
//	}

//	public void setId(int id) {
//		this.id = id;
//	}

	public int getAllewerkgevers() {
		return this.allewerkgevers;
	}

	public void setAllewerkgevers(int allewerkgevers) {
		this.allewerkgevers = allewerkgevers;
	}

	public int getDeadlineperiode() {
		return this.deadlineperiode;
	}

	public void setDeadlineperiode(int deadlineperiode) {
		this.deadlineperiode = deadlineperiode;
	}

	public int getDeadlineperiodesoort() {
		return this.deadlineperiodesoort;
	}

	public void setDeadlineperiodesoort(int deadlineperiodesoort) {
		this.deadlineperiodesoort = deadlineperiodesoort;
	}

	public int getDeadlinestartpunt() {
		return this.deadlinestartpunt;
	}

	public void setDeadlinestartpunt(int deadlinestartpunt) {
		this.deadlinestartpunt = deadlinestartpunt;
	}

	public int getDeadlinewaarschuwmoment() {
		return this.deadlinewaarschuwmoment;
	}

	public void setDeadlinewaarschuwmoment(int deadlinewaarschuwmoment) {
		this.deadlinewaarschuwmoment = deadlinewaarschuwmoment;
	}

	public int getDeadlinewaarschuwmomentsoort() {
		return this.deadlinewaarschuwmomentsoort;
	}

	public void setDeadlinewaarschuwmomentsoort(int deadlinewaarschuwmomentsoort) {
		this.deadlinewaarschuwmomentsoort = deadlinewaarschuwmomentsoort;
	}

	public String getDuur() {
		return this.duur;
	}

	public void setDuur(String duur) {
		this.duur = duur;
	}

	public int getKetenverzuim() {
		return this.ketenverzuim;
	}

	public void setKetenverzuim(int ketenverzuim) {
		this.ketenverzuim = ketenverzuim;
	}

	public String getNaam() {
		return this.naam;
	}

	public void setNaam(String naam) {
		this.naam = naam;
	}

	public int getNormaalverzuim() {
		return this.normaalverzuim;
	}

	public void setNormaalverzuim(int normaalverzuim) {
		this.normaalverzuim = normaalverzuim;
	}

	public String getOmschrijving() {
		return this.omschrijving;
	}

	public void setOmschrijving(String omschrijving) {
		this.omschrijving = omschrijving;
	}

	public Integer getPlannaactiviteit() {
		return this.plannaactiviteit;
	}

	public void setPlannaactiviteit(Integer plannaactiviteit) {
		this.plannaactiviteit = plannaactiviteit;
	}

	public int getRepeteeraantal() {
		return this.repeteeraantal;
	}

	public void setRepeteeraantal(int repeteeraantal) {
		this.repeteeraantal = repeteeraantal;
	}

	public int getRepeteerperiode() {
		return this.repeteerperiode;
	}

	public void setRepeteerperiode(int repeteerperiode) {
		this.repeteerperiode = repeteerperiode;
	}

	public int getRepeteerperiodesoort() {
		return this.repeteerperiodesoort;
	}

	public void setRepeteerperiodesoort(int repeteerperiodesoort) {
		this.repeteerperiodesoort = repeteerperiodesoort;
	}

	public int getVangnet() {
		return this.vangnet;
	}

	public void setVangnet(int vangnet) {
		this.vangnet = vangnet;
	}

	public Integer getVangnettype() {
		return this.vangnettype;
	}

	public void setVangnettype(Integer vangnettype) {
		this.vangnettype = vangnettype;
	}

	public int getVerwijdernaherstel() {
		return this.verwijdernaherstel;
	}

	public void setVerwijdernaherstel(int verwijdernaherstel) {
		this.verwijdernaherstel = verwijdernaherstel;
	}
}