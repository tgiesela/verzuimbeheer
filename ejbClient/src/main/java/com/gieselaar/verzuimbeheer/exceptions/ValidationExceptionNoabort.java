package com.gieselaar.verzuimbeheer.exceptions;

import javax.ejb.ApplicationException;

@ApplicationException(rollback = false)
public class ValidationExceptionNoabort extends Exception{
	/**
	 * 
	 */
	private static final long serialVersionUID = -4718213970745492480L;

	public ValidationExceptionNoabort(String msg){
		super(msg);
	}
}
