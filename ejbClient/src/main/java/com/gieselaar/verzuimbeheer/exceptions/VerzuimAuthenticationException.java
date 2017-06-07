package com.gieselaar.verzuimbeheer.exceptions;

import javax.ejb.ApplicationException;

@ApplicationException(rollback = true)
public class VerzuimAuthenticationException extends Exception{
	/**
	 * 
	 */
	private static final long serialVersionUID = -4718213970745492480L;

	public VerzuimAuthenticationException(String msg){
		super(msg);
	}
}
