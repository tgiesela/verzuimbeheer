package com.gieselaar.verzuimbeheer.entities;

import java.io.Serializable;
import javax.persistence.*;


/**
 * The persistent class for the ROL_APPLICATIEFUNCTIE database table.
 * 
 */
@Entity
@Table(name="ROL_APPLICATIEFUNCTIE")
public class RolApplicatiefunctie extends com.gieselaar.verzuimbeheer.entitiesutils.EntityBase implements Serializable {
	private static final long serialVersionUID = 1L;

	@EmbeddedId
	private RolApplicatiefunctiePK id;

    public RolApplicatiefunctie() {
    }

	public RolApplicatiefunctiePK getId() {
		return this.id;
	}

	public void setId(RolApplicatiefunctiePK id) {
		this.id = id;
	}
	
}