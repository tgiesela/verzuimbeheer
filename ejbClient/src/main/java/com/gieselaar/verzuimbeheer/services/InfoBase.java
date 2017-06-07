package com.gieselaar.verzuimbeheer.services;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.Serializable;
import java.util.Date;
import com.gieselaar.verzuimbeheer.exceptions.ValidationException;

public abstract class InfoBase implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -4604948330686586675L;

	public enum persistencestate {
		EXISTS, ABSENT
	}

	public enum persistenceaction {
		INSERT, UPDATE, DELETE
	}

	public enum __burgerlijkestaat {
		ONBEKEND(0) {
			@Override
			public String toString() {
				return "Onbekend";
			}
		},
		ONGEHUWD(1) {
			@Override
			public String toString() {
				return "Ongehuwd";
			}
		},
		GEHUWD(2) {
			@Override
			public String toString() {
				return "Gehuwd";
			}
		},
		GESCHEIDEN(3) {
			@Override
			public String toString() {
				return "Gescheiden";
			}
		},
		SAMENWONEND(4) {
			@Override
			public String toString() {
				return "Samenwonend";
			}
		},
		WEDUWE(5) {
			@Override
			public String toString() {
				return "Weduwe/Weduwnaar";
			}
		};

		private Integer value;

		__burgerlijkestaat(Integer value) {
			this.value = value;
		}

		public Integer getValue() {
			return value;
		}

		public static __burgerlijkestaat parse(Integer staat) {
			__burgerlijkestaat burgerlijkestaat = null; // Default
			for (__burgerlijkestaat item : __burgerlijkestaat.values()) {
				if (item.getValue().intValue() == staat.intValue()) {
					burgerlijkestaat = item;
					break;
				}
			}
			return burgerlijkestaat;
		}
	}

	private persistencestate state;
	private persistenceaction action;
	private Long version = null;
	private Integer id = null;
	private Integer createdby = null;
	private Integer updatedby = null;
	private Date creationts = null;
	private Date updatets = null;

	public InfoBase() {
		state = persistencestate.ABSENT;
		action = persistenceaction.INSERT;
		id = -1;
		version = null;
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

	public Date getUpdatets() {
		return updatets;
	}

	public void setUpdatets(Date updatets) {
		this.updatets = updatets;
	}

	public enum __geslacht {
		MAN(1) {
			@Override
			public String toString() {
				return "Man";
			}
		}, 
		VROUW(2){
			@Override
			public String toString() {
				return "Vrouw";
			}
		}, 
		ONBEKEND(3){
			@Override
			public String toString() {
				return "Onbekend";
			}
		};

		private int value;

		__geslacht(int value) {
			this.value = value;
		}

		public int getValue() {
			return value;
		}

		public static __geslacht parse(int id) {
			__geslacht geslacht = ONBEKEND; // Default
			for (__geslacht item : __geslacht.values()) {
				if (item.getValue() == id) {
					geslacht = item;
					break;
				}
			}
			return geslacht;
		}
	}

	public abstract boolean validate() throws ValidationException;

	public persistencestate getState() {
		return state;
	}

	public void setState(persistencestate state) {
		this.state = state;
	}

	public persistenceaction getAction() {
		return action;
	}

	public void setAction(persistenceaction action) {
		this.action = action;
	}

	public Long getVersion() {
		return version;
	}

	public void setVersion(Long version) {
		this.version = version;
	}

	public Integer getId() {
		return this.id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	class FieldListener implements PropertyChangeListener {

		@Override
		public void propertyChange(PropertyChangeEvent evt) {
			evt.getSource();
		}

	}
}
