package com.gieselaar.verzuimbeheer.entities;

import java.io.Serializable;

import javax.persistence.*;

import com.gieselaar.verzuimbeheer.entitiesutils.EntityBaseId;

import java.util.List;


/**
 * The persistent class for the ROL database table.
 * 
 */
@Entity
public class Rol extends EntityBaseId implements Serializable {
	private static final long serialVersionUID = 1L;

	private String omschrijving;

	private String rolid;

	//uni-directional many-to-many association to Applicatiefunctie
	@ManyToMany(fetch=FetchType.EAGER)
	@JoinTable(
		name="ROL_APPLICATIEFUNCTIE"
		, joinColumns={
			@JoinColumn(name="rol_id")
			}
		, inverseJoinColumns={
			@JoinColumn(name="applicatiefunctie_id")
			}
		)
	private List<Applicatiefunctie> applicatiefuncties;

    public Rol() {
    }

	public String getOmschrijving() {
		return this.omschrijving;
	}

	public void setOmschrijving(String omschrijving) {
		this.omschrijving = omschrijving;
	}

	public String getRolid() {
		return this.rolid;
	}

	public void setRolid(String rolid) {
		this.rolid = rolid;
	}

	public List<Applicatiefunctie> getApplicatiefuncties() {
		return this.applicatiefuncties;
	}

	public void setApplicatiefuncties(List<Applicatiefunctie> applicatiefuncties) {
		this.applicatiefuncties = applicatiefuncties;
	}
	
}