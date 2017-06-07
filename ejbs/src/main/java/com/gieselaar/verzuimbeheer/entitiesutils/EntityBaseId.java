package com.gieselaar.verzuimbeheer.entitiesutils;

import java.util.Date;

import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Version;

@MappedSuperclass
@EntityListeners(value = { EntityBaseListener.class })
public class EntityBaseId extends EntityBase {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@Version
	private Long version;
	private Integer createdby;
	private Integer updatedby;
	@Temporal(TemporalType.TIMESTAMP)
	private Date creationts;
	@Temporal(TemporalType.TIMESTAMP)
	private Date updatets;
	protected EntityBaseId(){
	}
	public Integer getCreatedby() {
		return createdby;
	}

	public void setCreatedby(Integer createdby) {
		this.createdby = createdby;
	}

	public Integer getUpdatedby() {
		return updatedby;
	}

	public void setUpdatedby(Integer updatedby) {
		this.updatedby = updatedby;
	}

	public Date getCreationts() {
		return creationts;
	}

	public void setCreationts(Date creationts) {
		this.creationts = creationts;
	}

	public Date getUpdatedts() {
		return updatets;
	}

	public void setUpdatedts(Date updatedts) {
		this.updatets = updatedts;
	}

	public Long getVersion() {
		return version;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public void setVersion(Long version) {
		this.version = version;
	}

/*	@PrePersist
	public void prePersist() {
		this.setCreationts(new Date());
		this.setCreatedby(currentuser);
	}

	@PreUpdate
	public void preUpdate() {
		this.setUpdatedts(new Date());
		this.setUpdatedby(currentuser);
	}
*/
}
