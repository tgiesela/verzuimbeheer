package com.gieselaar.verzuimbeheer.entities;

import java.io.Serializable;
import javax.persistence.*;


/**
 * The persistent class for the OpenVerzuim database table.
 * 
 */
@Embeddable
public class WerknemerVerzuimPK implements Serializable {
	private static final long serialVersionUID = 1L;

	private int werkgeverid;
	private int afdelingid;
	private int werknemerid;
	private int dienstverbandid;

	public WerknemerVerzuimPK() {
	}

	public int getDienstverbandid() {
		return dienstverbandid;
	}

	public void setDienstverbandid(int dienstverbandid) {
		this.dienstverbandid = dienstverbandid;
	}

	public int getWerknemerid() {
		return werknemerid;
	}

	public void setWerknemerid(int werknemerid) {
		this.werknemerid = werknemerid;
	}

	public int getAfdelingid() {
		return afdelingid;
	}

	public void setAfdelingid(int afdelingid) {
		this.afdelingid = afdelingid;
	}

	public int getWerkgeverid() {
		return werkgeverid;
	}

	public void setWerkgeverid(int werkgeverid) {
		this.werkgeverid = werkgeverid;
	}

	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (!(other instanceof WerknemerVerzuimPK)) {
			return false;
		}
		WerknemerVerzuimPK castOther = (WerknemerVerzuimPK)other;
		return 
			(this.dienstverbandid == castOther.dienstverbandid)
			&& (this.werkgeverid == castOther.werkgeverid)
			&& (this.afdelingid == castOther.afdelingid) 
			&& (this.werknemerid == castOther.werknemerid)
			;

    }
    
	public int hashCode() {
		final int prime = 31;
		int hash = 17;
		hash = hash * prime + this.dienstverbandid;
		hash = hash * prime + this.werkgeverid;
		hash = hash * prime + this.werknemerid;
		hash = hash * prime + this.afdelingid;
		
		return hash;
    }

}