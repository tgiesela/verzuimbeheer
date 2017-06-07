package com.gieselaar.verzuimbeheer.entities;

import java.io.Serializable;
import javax.persistence.*;


/**
 * The persistent class for the OpenVerzuim database table.
 * 
 */
@Embeddable
public class VActueelVerzuimPK implements Serializable {
	private static final long serialVersionUID = 1L;

	private int dienstverbandid;
	private int verzuimid;
	private int herstelid;

	public VActueelVerzuimPK() {
	}

	public int getDienstverbandid() {
		return dienstverbandid;
	}

	public void setDienstverbandid(int dienstverbandid) {
		this.dienstverbandid = dienstverbandid;
	}

	public int getVerzuimid() {
		return verzuimid;
	}

	public void setVerzuimid(int verzuimid) {
		this.verzuimid = verzuimid;
	}

	public int getHerstelid() {
		return herstelid;
	}

	public void setHerstelid(int herstelid) {
		this.herstelid = herstelid;
	}

	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (!(other instanceof VActueelVerzuimPK)) {
			return false;
		}
		VActueelVerzuimPK castOther = (VActueelVerzuimPK)other;
		return 
			(this.dienstverbandid == castOther.dienstverbandid)
			&& (this.verzuimid == castOther.verzuimid);

    }
    
	public int hashCode() {
		final int prime = 31;
		int hash = 17;
		hash = hash * prime + this.dienstverbandid;
		hash = hash * prime + this.verzuimid;
		
		return hash;
    }

}