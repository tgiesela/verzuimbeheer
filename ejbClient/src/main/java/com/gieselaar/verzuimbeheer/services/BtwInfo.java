package com.gieselaar.verzuimbeheer.services;

import java.math.BigDecimal;
import java.util.Date;

import com.gieselaar.verzuimbeheer.exceptions.ValidationException;

public class BtwInfo extends InfoBase {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private __btwtariefsoort btwtariefsoort;
	private Date einddatum;
	private Date ingangsdatum;
	private BigDecimal percentage;
	
	@Override
	public boolean validate() throws ValidationException {
		return false;
	}

	public Date getEinddatum() {
		return einddatum;
	}

	public void setEinddatum(Date einddatum) {
		this.einddatum = einddatum;
	}

	public Date getIngangsdatum() {
		return ingangsdatum;
	}

	public void setIngangsdatum(Date ingangsdatum) {
		this.ingangsdatum = ingangsdatum;
	}


	public __btwtariefsoort getBtwtariefsoort() {
		return btwtariefsoort;
	}

	public BigDecimal getPercentage() {
		return percentage;
	}

	public void setPercentage(BigDecimal percentage) {
		this.percentage = percentage;
	}

	public void setBtwtariefsoort(__btwtariefsoort btwtariefsoort) {
		this.btwtariefsoort = btwtariefsoort;
	}


	public enum __btwtariefsoort {
		LAAG(1) {
			@Override
			public String toString() {
				return "Laag";
			}
		},
		HOOG(2) {
			@Override
			public String toString() {
				return "Hoog";
			}
		},
		NVT(3) {
			@Override
			public String toString() {
				return "Nvt";
			}
		};

		private Integer value;

		__btwtariefsoort(Integer value) {
			this.value = value;
		}

		public Integer getValue() {
			return value;
		}

		public static __btwtariefsoort parse(Integer type) {
			__btwtariefsoort soort = null; // Default
			for (__btwtariefsoort item : __btwtariefsoort.values()) {
				if (item.getValue().intValue() == type.intValue()) {
					soort = item;
					break;
				}
			}
			return soort;
		}
	}
}
