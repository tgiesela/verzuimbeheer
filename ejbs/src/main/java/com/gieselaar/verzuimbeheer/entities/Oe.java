package com.gieselaar.verzuimbeheer.entities;

import java.io.Serializable;

import javax.persistence.*;

import com.gieselaar.verzuimbeheer.entitiesutils.EntityBaseId;


/**
 * The persistent class for the OE database table.
 * 
 */
@Entity
@NamedQuery(name="Oe.findAll", query="SELECT o FROM Oe o")
public class Oe extends EntityBaseId implements Serializable {
	private static final long serialVersionUID = 1L;

	private Integer parentoe_id;
	private String naam;
	//@OneToOne(cascade={CascadeType.ALL},orphanRemoval=false)
	@JoinColumn(name="oeniveau_id")
	private Oeniveau oeniveau;

	@Column(name="werkgever_id")
	private Integer werkgeverId;

	public Oe() {
	}

	public String getNaam() {
		return this.naam;
	}

	public void setNaam(String naam) {
		this.naam = naam;
	}

	public int getOeniveauId() {
		return this.oeniveau.getId();
	}

	public void setOeniveauId(int oeniveauid) {
		if (this.oeniveau != null)
			this.oeniveau.setId(oeniveauid);
	}

	public Integer getParentoeId() {
		return this.parentoe_id;
	}

	public void setParentoeId(Integer parentoeId) {
		this.parentoe_id = parentoeId;
	}

	public Integer getWerkgeverId() {
		return this.werkgeverId;
	}

	public void setWerkgeverId(Integer werkgeverId) {
		this.werkgeverId = werkgeverId;
	}

	public Oeniveau getOeniveau() {
		return oeniveau;
	}
	
	public void setOeniveau(Oeniveau oeniveau) {
		this.oeniveau = oeniveau;
	}
}