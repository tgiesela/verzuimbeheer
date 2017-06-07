package com.gieselaar.verzuimbeheer.entities;

import java.io.Serializable;
import javax.persistence.*;


/**
 * The persistent class for the PAKKET_HAS_ACTIVITEIT database table.
 * 
 */
@Entity
@Table(name="PAKKET_HAS_ACTIVITEIT")
public class PakketHasActiviteit extends com.gieselaar.verzuimbeheer.entitiesutils.EntityBase implements Serializable {
	private static final long serialVersionUID = 1L;

	@EmbeddedId
	private PakketHasActiviteitPK id;

    public PakketHasActiviteit() {
    }

	public PakketHasActiviteitPK getId() {
		return this.id;
	}

	public void setId(PakketHasActiviteitPK id) {
		this.id = id;
	}
	
}