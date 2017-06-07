package com.gieselaar.verzuimbeheer.utils;

import java.util.Calendar;
import java.util.Date;

/**
 * Class to compare two dates without the time part
 */
public class DateOnly {
	private static Date datewithouttime(Date date){
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.set(Calendar.HOUR_OF_DAY,0);
		cal.set(Calendar.MINUTE,0);
		cal.set(Calendar.SECOND,0);
		cal.set(Calendar.MILLISECOND,0);
		return cal.getTime();
	}
	/**
	 * Check if date1 < date2
	 * 
	 * 
	 * @param date1
	 * @param date2
	 * @return true/false
	 */
	public static boolean before(Date date1, Date date2){
		return datewithouttime(date1).before(datewithouttime(date2));
	}
	/**
	 * Check if date1 > date2
	 * 
	 * 
	 * @param date1
	 * @param date2
	 * @return true/false
	 */
	public static boolean after(Date date1, Date date2){
		return datewithouttime(date1).after(datewithouttime(date2));
	}
	/**
	 * Check if date1 = date2
	 * 
	 * 
	 * @param date1
	 * @param date2
	 * @return true/false
	 */
	public static boolean equals(Date date1, Date date2){
		return datewithouttime(date1).equals(datewithouttime(date2));
	}
	public static int diffDays(Date date1, Date date2){
		long diff = date1.getTime() - date2.getTime();
		return (int)(diff/1000/60/60/24);
	}
	public static Date addDays(Date startdatum, int nrofdays) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(startdatum);
		cal.add(Calendar.DATE, nrofdays);
		return cal.getTime();
	}
}
