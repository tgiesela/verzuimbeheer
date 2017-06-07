package com.gieselaar.verzuimbeheer.utils;

public class ServiceLocatorException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8945239781149068959L;
	public ServiceLocatorException(String msg){
		super(msg);
	}
	public ServiceLocatorException(Exception e){
		super(e);
	}

}
