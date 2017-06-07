package com.gieselaar.verzuimbeheer.exceptions;

import javax.ejb.ApplicationException;

@ApplicationException(rollback = true)
public class VerzuimApplicationException extends Exception {
	/**
	 * 
	 */
	public enum __applicationExceptiontype{
		STANDARD,
		OPTIMISTICLOCKEXCEPTION;
	}
	private static final long serialVersionUID = 1L;
	private final __applicationExceptiontype type;
	public VerzuimApplicationException(Exception e, String msg){
		super(msg, e);
		this.type = __applicationExceptiontype.STANDARD;
	}
	public VerzuimApplicationException(Exception e, String msg, __applicationExceptiontype exctype){
		super(msg, e);
		this.type = exctype;
	}
	public __applicationExceptiontype getType() {
		return type;
	}
}
