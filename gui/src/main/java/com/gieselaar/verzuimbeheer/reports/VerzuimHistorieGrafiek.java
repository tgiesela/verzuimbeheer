package com.gieselaar.verzuimbeheer.reports;

import java.math.BigDecimal;
import java.util.Date;

public class VerzuimHistorieGrafiek {
	private Date startdate;
	private BigDecimal percentage;
	public Date getStartdate() {
		return startdate;
	}
	public void setStartdate(Date startdate) {
		this.startdate = startdate;
	}
	public BigDecimal getPercentage() {
		return percentage;
	}
	public void setPercentage(BigDecimal percentage) {
		this.percentage = percentage;
	}
	
}
