package com.gieselaar.verzuimbeheer.entities;

import java.io.Serializable;
import javax.persistence.*;


/**
 * The persistent class for the WERKGEVER_HAS_PAKKET database table.
 * 
 */
@Entity
@Table(name="WERKGEVER_HAS_PAKKET")
public class WerkgeverHasPakket extends com.gieselaar.verzuimbeheer.entitiesutils.EntityBase implements Serializable {
	private static final long serialVersionUID = 1L;

	@EmbeddedId
	private WerkgeverHasPakketPK id;

    public WerkgeverHasPakket() {
    }

	public WerkgeverHasPakketPK getId() {
		return this.id;
	}

	public void setId(WerkgeverHasPakketPK id) {
		this.id = id;
	}
	
}