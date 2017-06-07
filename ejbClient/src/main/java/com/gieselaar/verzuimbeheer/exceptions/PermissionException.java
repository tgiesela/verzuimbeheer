package com.gieselaar.verzuimbeheer.exceptions;

import javax.ejb.ApplicationException;

@ApplicationException(rollback = true)
public class PermissionException extends Exception{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public PermissionException(String msg){
		super(msg);
	}
}
