package com.gieselaar.verzuimbeheer.baseforms;

public interface WerkgeverNotification {
	/**
	 * 
	 * @return true if the selection should not change
	 */
	public boolean werkgeverSelected(Integer werkgeverid);
	/**
	 * 
	 * @return true if the selection should not change
	 */
	public boolean holdingSelected(Integer holdingid);
}
