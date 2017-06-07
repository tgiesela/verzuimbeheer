package com.gieselaar.verzuimbeheer.entities;

import java.io.Serializable;

import javax.persistence.*;

import com.gieselaar.verzuimbeheer.entitiesutils.EntityBase;


/**
 * The persistent class for the OEREPORT database table.
 * 
 */
@Entity
@NamedQuery(name="Oereport.findAll", query="SELECT o FROM Oereport o")
public class Oereport extends EntityBase implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	private Integer id;

	private String naam;

	@Column(name="oeniveau_id")
	private Integer oeniveauId;

	@Column(name="parentoe_id")
	private Integer parentoeId;

	@Column(name="werkgever_id")
	private Integer werkgeverId;

	public Oereport() {
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getNaam() {
		return this.naam;
	}

	public void setNaam(String naam) {
		this.naam = naam;
	}

	public Integer getOeniveauId() {
		return this.oeniveauId;
	}

	public void setOeniveauId(Integer oeniveauId) {
		this.oeniveauId = oeniveauId;
	}

	public Integer getParentoeId() {
		return this.parentoeId;
	}

	public void setParentoeId(Integer parentoeId) {
		this.parentoeId = parentoeId;
	}

	public Integer getWerkgeverId() {
		return this.werkgeverId;
	}

	public void setWerkgeverId(Integer werkgeverId) {
		this.werkgeverId = werkgeverId;
	}

}