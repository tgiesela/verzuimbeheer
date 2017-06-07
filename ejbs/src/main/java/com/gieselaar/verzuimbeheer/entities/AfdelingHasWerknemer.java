package com.gieselaar.verzuimbeheer.entities;

import java.io.Serializable;
import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;


/**
 * The persistent class for the AFDELING_HAS_WERKNEMER database table.
 * 
 */
@Entity
@Table(name="AFDELING_HAS_WERKNEMER")
public class AfdelingHasWerknemer extends com.gieselaar.verzuimbeheer.entitiesutils.EntityBase implements Serializable {
	private static final long serialVersionUID = 1L;

	@EmbeddedId
	private AfdelingHasWerknemerPK id;

    @Temporal( TemporalType.DATE)
	private Date einddatum;

	private BigDecimal uren;

    public AfdelingHasWerknemer() {
    }

	public AfdelingHasWerknemerPK getId() {
		return this.id;
	}

	public void setId(AfdelingHasWerknemerPK id) {
		this.id = id;
	}
	
	public Date getEinddatum() {
		return this.einddatum;
	}

	public void setEinddatum(Date einddatum) {
		this.einddatum = einddatum;
	}

	public BigDecimal getUren() {
		return this.uren;
	}

	public void setUren(BigDecimal uren) {
		this.uren = uren;
	}

}