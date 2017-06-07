package com.gieselaar.verzuimbeheer.utils;

import org.junit.Test;

import com.gieselaar.verzuimbeheer.facades.LoginSessionRemote;

public class TestUtils {

	@Test
	public void test() throws ServiceLocatorException {
		ServiceLocator locator = ServiceLocator.getInstance();
		@SuppressWarnings("unused")
		LoginSessionRemote session = null; 
		session = (LoginSessionRemote)locator.getLocalHome(RemoteInterfaces.LoginSession.getValue());
		session = (LoginSessionRemote)locator.getRemoteHome(RemoteInterfaces.LoginSession.getValue(), LoginSessionRemote.class);
		
		locator = new ServiceLocator(); 
		session = (LoginSessionRemote) locator.getLocalHome(RemoteInterfaces.LoginSession.getValue());
		session = (LoginSessionRemote) locator.getRemoteHome(RemoteInterfaces.LoginSession.getValue(), LoginSessionRemote.class);
		
	}

}
