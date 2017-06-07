package com.gieselaar.verzuimbeheer.services;

import java.util.Date;

import com.gieselaar.verzuimbeheer.exceptions.ValidationException;

public class WiapercentageInfo extends InfoBase {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5543957718981522161L;
	private __wiapercentage codeWiaPercentage;
	private Date startdatum;
	private Date einddatum;
	private WerknemerInfo werknemer;
	private Integer werknemerId;

	public enum __wiapercentage {
		NVT(0) {
			@Override
			public String toString() {
				return "n.v.t";
			}
		},
		WIA_0_15(1) {
			@Override
			public String toString() {
				return "0-15%";
			}
		},
		WIA_15_25(2) {
			@Override
			public String toString() {
				return "15-25%";
			}
		},
		WIA_25_35(3) {
			@Override
			public String toString() {
				return "25-35%";
			}
		},
		WIA_35_45(4) {
			@Override
			public String toString() {
				return "35-45%";
			}
		},
		WIA_45_55(5) {
			@Override
			public String toString() {
				return "45-55%";
			}
		},
		WIA_55_65(6) {
			@Override
			public String toString() {
				return "55-65%";
			}
		},
		WIA_65_80(7) {
			@Override
			public String toString() {
				return "65-80%";
			}
		},
		WIA_80_100(8) {
			@Override
			public String toString() {
				return "80-100%";
			}
		};

		private Integer value;

		__wiapercentage(Integer value) {
			this.value = value;
		}

		public Integer getValue() {
			return value;
		}

		public static __wiapercentage parse(Integer id) {
			__wiapercentage percentagecode = null; // Default
			for (__wiapercentage item : __wiapercentage.values()) {
				if (item.getValue().intValue() == id.intValue()) {
					percentagecode = item;
					break;
				}
			}
			return percentagecode;
		}
	}

	public __wiapercentage getCodeWiaPercentage() {
		return codeWiaPercentage;
	}

	public void setCodeWiaPercentage(__wiapercentage codeWiaPercentage) {
		this.codeWiaPercentage = codeWiaPercentage;
	}

	public Date getStartdatum() {
		return startdatum;
	}

	public void setStartdatum(Date startdatum) {
		this.startdatum = startdatum;
	}

	public Date getEinddatum() {
		return einddatum;
	}

	public void setEinddatum(Date einddatum) {
		this.einddatum = einddatum;
	}

	public WerknemerInfo getWerknemer() {
		return werknemer;
	}

	public void setWerknemer(WerknemerInfo werknemer) {
		this.werknemer = werknemer;
	}

	public Integer getWerknemerId() {
		return werknemerId;
	}

	public void setWerknemerId(Integer werknemerId) {
		this.werknemerId = werknemerId;
	}

	@Override
	public boolean validate() throws ValidationException {
		if (this.getStartdatum() == null)
			throw new ValidationException("Ingangsdatum niet ingevuld");
		if ((this.getEinddatum() != null) && (this.getEinddatum().before(this.getStartdatum())))
			throw new ValidationException("Einddatum ligt voor ingangsdatum");
		if (this.getWerknemerId() == null)
			throw new ValidationException("Werknemer niet ingevuld");
		return false;
	}

}
