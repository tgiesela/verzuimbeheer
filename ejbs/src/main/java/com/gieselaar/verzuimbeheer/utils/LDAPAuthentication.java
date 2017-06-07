package com.gieselaar.verzuimbeheer.utils;

import java.util.Hashtable;

import javax.naming.AuthenticationException;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

public class LDAPAuthentication {
	Hashtable<String, String> env = new Hashtable<String, String>();
	Context ctx = null;
	
	public LDAPAuthentication(){
		
	}
	public void authenticate(){
		/* Voor LDAP */
		env.put(Context.INITIAL_CONTEXT_FACTORY, 
			"com.sun.jndi.ldap.LdapCtxFactory");
		/* Voor FileSystem Service provider
		env.put(Context.INITIAL_CONTEXT_FACTORY, 
			"com.sun.jndi.fscontext.RefFSContextFactory");*/

		// Onderstaande werkt
        
		env.put(Context.PROVIDER_URL, "ldap://pdc.verzuim.local:389/");
		env.put(Context.SECURITY_PRINCIPAL, "uid=tonny,ou=people,dc=verzuim,dc=local");
		env.put(Context.SECURITY_CREDENTIALS, "password");
		env.put(Context.SECURITY_AUTHENTICATION,"DIGEST");
       
		try {
			ctx = new InitialContext(env);
			//ctx.addToEnvironment(Context.SECURITY_AUTHENTICATION, "none");
		} catch (AuthenticationException e) {
			System.out.println("Invalid username/password");	
		} catch (NamingException e) {
			e.printStackTrace();
		}
	}
}
