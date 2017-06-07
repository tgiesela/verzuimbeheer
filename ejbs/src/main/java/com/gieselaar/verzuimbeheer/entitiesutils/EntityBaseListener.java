package com.gieselaar.verzuimbeheer.entitiesutils;

import java.util.Date;

import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;

public class EntityBaseListener {

	 @PrePersist
	 public void prePersist(EntityBaseId e) {
	  e.setCreationts(new Date());
	 }

	 @PreUpdate
	 public void preUpdate(EntityBaseId e) {
	  e.setUpdatedts(new Date());
	 }
	}
