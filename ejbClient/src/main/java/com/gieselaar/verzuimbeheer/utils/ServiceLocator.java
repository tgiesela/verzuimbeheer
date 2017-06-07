package com.gieselaar.verzuimbeheer.utils;

import javax.naming.*;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

import javax.ejb.*;
import javax.rmi.PortableRemoteObject;

import org.apache.log4j.Logger;

import com.gieselaar.verzuimbeheer.utils.VerzuimProperties.__verzuimproperty;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
/*
 * ServiceLocator implementation of Service Locator pattern
 * http://java.sun.com/blueprints/corej2eepatterns/Patterns/ServiceLocator.html
 */
import java.lang.reflect.Method;

public class ServiceLocator {

	private static ServiceLocator me;
	private Map<Object, Object> cache;
	private InitialContext context = null;
	private Properties properties = new Properties();
	private VerzuimProperties verzuimProps = new VerzuimProperties();
	private static final Logger logger = Logger.getLogger(ServiceLocator.class);

	private class Remoteinfo {
		long creationtime;
		Method removemethod;
		Object reference;
	}

	public ServiceLocator() throws ServiceLocatorException {
		verzuimProps.loadProperties();
		logger.info("Loaded properties from: "
				+ verzuimProps.getPropertyFilename());
		String hostname = (String) verzuimProps
				.getProperty(__verzuimproperty.Hostname);
		String portnr = (String) verzuimProps
				.getProperty(__verzuimproperty.Portnumber);
		if (hostname == null)
			verzuimProps.setProperty(__verzuimproperty.Hostname, "payara");
		if (portnr == null)
			verzuimProps.setProperty(__verzuimproperty.Portnumber, "3700");
		if (hostname == null || portnr == null) {
			hostname = (String) verzuimProps
					.getProperty(__verzuimproperty.Hostname);
			portnr = (String) verzuimProps
					.getProperty(__verzuimproperty.Portnumber);
			verzuimProps.saveProperties();
		}
		try {
			
			properties.setProperty("java.naming.factory.initial",
					"com.sun.enterprise.naming.SerialInitContextFactory");
			properties.setProperty("java.naming.factory.url.pkgs",
					"com.sun.enterprise.naming");
			properties
					.setProperty("java.naming.factory.state",
							"com.sun.corba.ee.impl.presentation.rmi.JNDIStateFactoryImpl");
			properties.put("org.omg.CORBA.ORBInitialHost", hostname);
			properties.put("org.omg.CORBA.ORBInitialPort", portnr);

			logger.info("Attempting connect to: " + hostname + ":" + portnr);
			context = new InitialContext(properties);
			logger.info("Connected to: " + hostname + ":" + portnr);

			cache = Collections.synchronizedMap(new HashMap<Object, Object>());

		} catch (NamingException ne) {
			logger.info(ne.getMessage());
			throw new ServiceLocatorException(ne);
		}
	}

	public void release() throws ServiceLocatorException {
		if (me == null) {
			return;
		}
		if (context == null) {
			return;
		}
		try {

			for (Entry<Object, Object> entry : cache.entrySet()) {
				String facadename = (String) entry.getKey();
				Remoteinfo info = (Remoteinfo) entry.getValue();
				logger.info("Removing " + facadename);
				if (info.removemethod != null) {
					info.removemethod.invoke(info.reference);
				}
			}
			context.close();
		} catch (NamingException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			logger.debug(e);
			throw new ServiceLocatorException("Error during close of context");
		}
	}

	// Returns the instance of ServiceLocator class
	public static ServiceLocator getInstance() throws ServiceLocatorException {
		if (me == null) {
			me = new ServiceLocator();
		}
		return me;
	}

	// Converts the serialized string into EJBHandle
	// then to EJBObject.
	public EJBObject getService(String id) throws ServiceLocatorException {

		if (id == null) {
			throw new ServiceLocatorException(
					"getService called with null parameter");
		}
		try {
			byte[] bytes = id.getBytes();
			InputStream io = new ByteArrayInputStream(bytes);
			ObjectInputStream os = new ObjectInputStream(io);
			javax.ejb.Handle handle = (javax.ejb.Handle) os.readObject();
			return handle.getEJBObject();
		} catch (Exception ex) {
			throw new ServiceLocatorException(ex);
		}
	}

	// Returns the String that represents the given
	// EJBObject's handle in serialized format.
	protected String getId(EJBObject session) throws ServiceLocatorException {
		try {
			javax.ejb.Handle handle = session.getHandle();
			ByteArrayOutputStream fo = new ByteArrayOutputStream();
			ObjectOutputStream so = new ObjectOutputStream(fo);
			so.writeObject(handle);
			so.flush();
			so.close();
			return new String(fo.toByteArray());
		} catch (IOException ex) {
			throw new ServiceLocatorException(ex);
		}
	}

	private Method removeMethod(Class<?> clazz){
		try {
			return clazz.getMethod("remove");
		} catch (NoSuchMethodException e) {
			return null;
		}
	}
	private void invokeRemove(Remoteinfo info, Object home){
		if (info.removemethod != null) {
			try{
				info.removemethod.invoke(info.reference);
			} catch (Exception e) {
				logger.info("Exception during removal of bean");
				logger.debug(e);
			}
			cache.remove(home);
		}
	}
	/**
	 * 
	 * Returns the EJBHome object for requested service name. Throws
	 * ServiceLocatorException If Any Error occurs in lookup.
	 * 
	 * If object isn't used for more than an hour, it will no longer be used
	 * because the Application Server (Glassfish) will clean up unused session
	 * beans.
	 * 
	 * The assumption is: no one keeps a reference to the bean to use it without
	 * checking if the bean reference is still valid.
	 * 
	 * @param jndiHomeName
	 * @param clazz
	 * @return
	 * @throws ServiceLocatorException
	 */
	public Object getRemoteHome(String jndiHomeName, Class<?> clazz)
			throws ServiceLocatorException {
		Object home = null;
		try {
			if (cache.containsKey(jndiHomeName)) {
				Remoteinfo info = (Remoteinfo) cache.get(jndiHomeName);
				long currentts = System.currentTimeMillis();
				if ((currentts - info.creationtime) < 1000 * 60 * 60) {
					/*
					 * CreationTime within hour, we can still use this reference
					 */
					home = info.reference;
					logger.debug("Reusing obtained reference to: "
							+ jndiHomeName);
				} else {
					home = info.reference;
					invokeRemove(info, home);
					home = null;
				}
			}
			if (home == null) {
				Object objref = context.lookup(jndiHomeName);
				home = PortableRemoteObject.narrow(objref, clazz);
				Remoteinfo info = new Remoteinfo();
				info.creationtime = System.currentTimeMillis();
				info.removemethod = removeMethod(clazz);
				info.reference = home;
				cache.put(jndiHomeName, info);
				logger.debug("Obtained reference to: " + jndiHomeName);
			}
			return home;
		} catch (NamingException ex) {
			throw new ServiceLocatorException(ex);
		} catch (Exception e) {
			throw new ServiceLocatorException(e);
		}
	}

	/**
	 * Enterprise bean lookups for local clients
	 * 
	 * @param jndiHomeName
	 * @return
	 * @throws ServiceLocatorException
	 */
	public Object getLocalHome(String jndiHomeName)
			throws ServiceLocatorException {
		Object home = null;
		try {
			if (cache.containsKey(jndiHomeName)) {
				home = cache.get(jndiHomeName);
			} else {
				home = context.lookup(jndiHomeName);
				cache.put(jndiHomeName, home);
			}
		} catch (Exception ne) {
			throw new ServiceLocatorException(ne);
		}
		return home;
	}
}