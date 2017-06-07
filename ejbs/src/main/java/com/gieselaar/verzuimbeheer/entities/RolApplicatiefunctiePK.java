package com.gieselaar.verzuimbeheer.entities;

import java.io.Serializable;
import javax.persistence.*;

/**
 * The primary key class for the ROL_APPLICATIEFUNCTIE database table.
 * 
 */
@Embeddable
public class RolApplicatiefunctiePK implements Serializable {
	//default serial version id, required for serializable classes.
	private static final long serialVersionUID = 1L;

	@Column(name="rol_id")
	private int rolId;

	@Column(name="applicatiefunctie_id")
	private int applicatiefunctieId;

    public RolApplicatiefunctiePK() {
    }
	public int getRolId() {
		return this.rolId;
	}
	public void setRolId(int rolId) {
		this.rolId = rolId;
	}
	public int getApplicatiefunctieId() {
		return this.applicatiefunctieId;
	}
	public void setApplicatiefunctieId(int applicatiefunctieId) {
		this.applicatiefunctieId = applicatiefunctieId;
	}

	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (!(other instanceof RolApplicatiefunctiePK)) {
			return false;
		}
		RolApplicatiefunctiePK castOther = (RolApplicatiefunctiePK)other;
		return 
			(this.rolId == castOther.rolId)
			&& (this.applicatiefunctieId == castOther.applicatiefunctieId);

    }
    
	public int hashCode() {
		final int prime = 31;
		int hash = 17;
		hash = hash * prime + this.rolId;
		hash = hash * prime + this.applicatiefunctieId;
		
		return hash;
    }
}