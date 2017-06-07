package reportdatasources;

import com.gieselaar.verzuimbeheer.services.WerkzaamhedenInfo;

public class WerkzaamhedenInfoReport {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private WerkzaamhedenInfo werkzaamheid;
	private String werkgevernaam;
	private String gebruikernaam;
	public String getWerkgevernaam() {
		return werkgevernaam;
	}
	public void setWerkgevernaam(String werkgevernaam) {
		this.werkgevernaam = werkgevernaam;
	}
	public String getGebruikernaam() {
		return gebruikernaam;
	}
	public void setGebruikernaam(String gebruikernaam) {
		this.gebruikernaam = gebruikernaam;
	}
	public WerkzaamhedenInfo getWerkzaamheid() {
		return werkzaamheid;
	}
	public void setWerkzaamheid(WerkzaamhedenInfo werkzaamheid) {
		this.werkzaamheid = werkzaamheid;
	}
}
