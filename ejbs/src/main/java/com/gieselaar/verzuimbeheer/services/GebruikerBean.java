package com.gieselaar.verzuimbeheer.services;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;
import java.util.Properties;
import java.util.Random;

import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.SessionContext;
import javax.ejb.Stateless;
import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.naming.AuthenticationException;
import javax.naming.NamingException;
import javax.persistence.Query;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import com.gieselaar.verzuimbeheer.entities.Gebruiker;
import com.gieselaar.verzuimbeheer.entities.GebruikerWerkgever;
import com.gieselaar.verzuimbeheer.exceptions.VerzuimAuthenticationException;
import com.gieselaar.verzuimbeheer.exceptions.VerzuimRuntimeException;
import com.gieselaar.verzuimbeheer.exceptions.PermissionException;
import com.gieselaar.verzuimbeheer.exceptions.ValidationException;
import com.gieselaar.verzuimbeheer.exceptions.VerzuimApplicationException;
import com.gieselaar.verzuimbeheer.facades.AutorisatieFacadeRemote.__applicatiefunctie;
import com.gieselaar.verzuimbeheer.services.GebruikerInfo.__status;
import com.gieselaar.verzuimbeheer.services.InfoBase.persistenceaction;
import com.gieselaar.verzuimbeheer.services.InfoBase.persistencestate;
import com.gieselaar.verzuimbeheer.utils.AutorisatieConversion;
/**
 * LDAP authentication
 */
import com.sun.jndi.ldap.LdapCtxFactory;

import javax.naming.Context;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;

/**
 * Session Bean implementation class GebruikerBean
 */

@Stateless
@LocalBean
// @WebService
public class GebruikerBean extends BeanBase {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@EJB AutorisatieConversion converter;
	@EJB SettingsBean settingsBean;
	@Resource transient SessionContext ctx;
	/**
	 * Default constructor.
	 */
	private static final String SALT = "devosverzuim";
	private static final Random RANDOM = new SecureRandom();

	public GebruikerInfo addGebruiker(GebruikerInfo info) throws ValidationException,
			VerzuimApplicationException {
		Gebruiker gebruiker;
		try {
			info.validate();
		} catch (ValidationException e) {
			throw e;
		}
		info.setName(info.getName().toLowerCase());
		info.setEmailadres(info.getEmailadres().toLowerCase());
		gebruiker = converter.toEntity(info, this.getCurrentuser());
		this.insertEntity(gebruiker);
		if (info.getWerkgevers() != null)
			for (GebruikerWerkgeverInfo gwi : info.getWerkgevers()) {
				gwi.setGebruikerid(gebruiker.getId());
				switch (gwi.getAction()) {
				case DELETE:
					this.deleteEntity(converter.toEntity(gwi));
					break;
				case INSERT:
					this.insertEntity(converter.toEntity(gwi));
					break;
				case UPDATE:
					this.updateEntity(converter.toEntity(gwi));
					break;
				}

			}
		try {
			this.changePassword(info.getName(), "", info.getNewPassword());
		} catch (VerzuimAuthenticationException e) {
			log.error("Could not change password: " + e.getMessage(),e);
			throw new VerzuimApplicationException(e, "Change password failed");
		}
		return getGebruikerbyId(gebruiker.getId());
	}

	public GebruikerInfo updateGebruiker(GebruikerInfo info)
			throws ValidationException, VerzuimApplicationException {
		Gebruiker gebruiker;
		try {
			info.validate();
		} catch (ValidationException e) {
			throw e;
		}
		info.setName(info.getName().toLowerCase());
		info.setEmailadres(info.getEmailadres().toLowerCase());
		gebruiker = converter.toEntity(info, this.getCurrentuser());
		this.updateEntity(gebruiker);
		if (info.getWerkgevers() != null)
			for (GebruikerWerkgeverInfo gwi : info.getWerkgevers()) {
				gwi.setGebruikerid(gebruiker.getId());
				switch (gwi.getAction()) {
				case DELETE:
					this.deleteEntity(converter.toEntity(gwi));
					break;
				case INSERT:
					this.insertEntity(converter.toEntity(gwi));
					break;
				case UPDATE:
					this.updateEntity(converter.toEntity(gwi));
					break;
				}
			}
		if ((info.getNewPassword() != null) && !info.getNewPassword().isEmpty()){
			try {
				this.changePassword(info.getName(), "", info.getNewPassword());
			} catch (VerzuimAuthenticationException e) {
				log.error("Could not change password: " + e.getMessage(),e);
				throw new VerzuimApplicationException(e, "Change password failed");
			}
		}
		return getGebruikerbyId(info.getId());
	}

	public GebruikerInfo getGebruikerbyId(long id)
			throws VerzuimApplicationException {
		Gebruiker gebruiker;
		Query q = createQuery("select g from Gebruiker g where g.id = :id");
		q.setParameter("id", id);
		@SuppressWarnings("unchecked")
		List<Gebruiker> result = (List<Gebruiker>) getResultList(q);

		if (result.size() != 1)
			return null;
		else {
			gebruiker = result.get(0);
			GebruikerInfo gi = converter.fromEntity(gebruiker);
			gi.setWerkgevers(getWerkgeversForGebruiker(gi.getId()));
			return gi;
		}
	}

	public GebruikerInfo getGebruikerbyName(String name)
			throws VerzuimApplicationException {
		Gebruiker gebruiker;

		Query q = createQuery("select g from Gebruiker g where g.gebruikersnaam = :name");
		q.setParameter("name", name);
		@SuppressWarnings("unchecked")
		List<Gebruiker> result = (List<Gebruiker>) getResultList(q);
		if (result.size() != 1)
			return null;
		else {
			gebruiker = result.get(0);
			GebruikerInfo gi = converter.fromEntity(gebruiker);
			gi.setWerkgevers(getWerkgeversForGebruiker(gi.getId()));
			return gi;
		}
	}

	public List<GebruikerInfo> allGebruikers()
			throws VerzuimApplicationException {
		Query q = createQuery("select g from Gebruiker g");
		@SuppressWarnings("unchecked")
		List<Gebruiker> result = (List<Gebruiker>) getResultList(q);
		List<GebruikerInfo> inforesult = new ArrayList<>();
		for (Gebruiker g : result) {
			GebruikerInfo gi = converter.fromEntity(g);
			inforesult.add(gi);
		}

		return inforesult;
	}

	public List<GebruikerWerkgeverInfo> getWerkgeversForGebruiker(
			Integer gebruikerid) throws VerzuimApplicationException {
		List<GebruikerWerkgeverInfo> werkgevers = new ArrayList<>();
		Query q = createQuery("select gw from GebruikerWerkgever gw where gw.id.gebruikerid = :id");
		q.setParameter("id", gebruikerid);
		@SuppressWarnings("unchecked")
		List<GebruikerWerkgever> result = (List<GebruikerWerkgever>) getResultList(q);
		for (GebruikerWerkgever g : result) {
			GebruikerWerkgeverInfo gi = converter.fromEntity(g);
			gi.setState(persistencestate.EXISTS);
			gi.setAction(persistenceaction.UPDATE);
			werkgevers.add(gi);
		}

		return werkgevers;

	}

	private String generateHash(String input) throws NoSuchAlgorithmException {

		StringBuilder hash = new StringBuilder();

		try {
			MessageDigest sha = MessageDigest.getInstance("SHA-256");
			byte[] hashedBytes = sha.digest(input.getBytes());
			char[] digits = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
					'a', 'b', 'c', 'd', 'e', 'f' };
			for (int idx = 0; idx < hashedBytes.length; ++idx) {
				byte b = hashedBytes[idx];
				hash.append(digits[(b & 0xf0) >> 4]);
				hash.append(digits[b & 0x0f]);
			}
		} catch (NoSuchAlgorithmException e) {
			log.error("MessageDigest error: " + e.getMessage(),e);
			throw e;
		}

		return hash.toString();
	}

	/**
	 * Detect the LDAP server for a specific domain or the default LDAP server
	 * if domainname is empty or absent
	 * 
	 * @return server:port or null
	 * @throws Exception 
	 */
	private String getLdapHost(String domainname) {
		try {
			Hashtable<String, String> env = new Hashtable<>();
			env.put(Context.INITIAL_CONTEXT_FACTORY,
					"com.sun.jndi.dns.DnsContextFactory");
			if (domainname == null || domainname.isEmpty())
				env.put("java.naming.provider.url", "dns:");
			else
				env.put("java.naming.provider.url",
						"dns://" + domainname.toLowerCase());

			DirContext dns = new InitialDirContext(env);

			Attributes attrs;
			if (domainname == null || domainname.isEmpty()){
				InetAddress address = InetAddress.getLocalHost();
				log.debug("InetAddress.getLocalHost=" + address.toString());
				String domain = address.getCanonicalHostName();
				log.debug("address.getCanonicalHostName()="
						+ address.getCanonicalHostName());
				log.debug("address.getHostAddress()=" + address.getHostAddress());

				if (domain.equals(address.getHostAddress())) {
					// domain is a ip address
					domain = getDnsPtr(dns);
				}
				int idx = domain.indexOf('.');
				if (idx < 0) {
					// computer is not in a domain? We will look in the DNS self.
					domain = getDnsPtr(dns);
					idx = domain.indexOf('.');
					if (idx < 0) {
						// computer is not in a domain
						return null;
					}
				}
				domain = domain.substring(idx + 1);
				attrs = dns.getAttributes("_ldap._tcp." + domain,
						new String[] { "SRV" });
			}else{
				attrs = dns.getAttributes("_ldap._tcp." + domainname.toLowerCase(),
					new String[] { "SRV" });
			}

			Attribute attr = attrs.getAll().nextElement();
			String srv = attr.get().toString();

			String[] parts = srv.split(" ");
			return parts[3] + ":" + parts[2];
		} catch (Exception ex) {
			log.error("Problem in getLdapHost", ex);
			throw new VerzuimRuntimeException(ex.getMessage());
		}
	}

	/**
	 * Look for a reverse PTR record on any available ip address
	 * 
	 * @param dns
	 *            DNS context
	 * @return the PTR value
	 * @throws SocketException 
	 * @throws Exception
	 *             if the PTR entry was not found
	 */
	private String getDnsPtr(DirContext dns) throws SocketException {
		Exception exception = null;
		Enumeration<NetworkInterface> interfaces = NetworkInterface
				.getNetworkInterfaces();
		while (interfaces.hasMoreElements()) {
			NetworkInterface nif = interfaces.nextElement();
			if (nif.isLoopback()) {
				continue;
			}
			Enumeration<InetAddress> adresses = nif.getInetAddresses();
			while (adresses.hasMoreElements()) {
				InetAddress address = adresses.nextElement();
				if (address.isLoopbackAddress()
						|| address instanceof Inet6Address) {
					continue;
				}
				String domain = address.getCanonicalHostName();
				if (!domain.equals(address.getHostAddress())
						&& (domain.indexOf('.') >= 0)) {
					return domain;
				}

				String ip = address.getHostAddress();
				String[] digits = ip.split("\\.");
				StringBuilder builder = new StringBuilder();
				builder.append(digits[3]).append('.');
				builder.append(digits[2]).append('.');
				builder.append(digits[1]).append('.');
				builder.append(digits[0]).append(".in-addr.arpa.");
				try {
					Attributes attrs = dns.getAttributes(builder.toString(),
							new String[] { "PTR" });
					return attrs.get("PTR").get().toString();
				} catch (Exception ex) {
					exception = ex;
				}
			}
		}
		if (exception != null) {
			throw new VerzuimRuntimeException(exception.getMessage());
		}
		throw new IllegalStateException("No network");
	}

	@SuppressWarnings("restriction")
	private boolean authenticateAgainstAD(String username, String password,
			String domain) throws NamingException  {

		String domainName;
		String serverName;
		String hostname;
		try{
			hostname = getLdapHost(domain);
		} catch (Exception e){
			/*
			 * Try again, but now without domain name
			 */
			log.info("getLdapHost failed: " + e.getMessage(), e);
			hostname = getLdapHost(null);
		}

		if (domain != null && !domain.isEmpty()) {
			domainName = domain;
			serverName = hostname;
		} else {
			domainName = "verzuim.local";
			serverName = "pdc";
		}

		log.info("Authenticating " + username + "@" + domainName + " through "
				+ serverName + "." + domainName);

		// bind by using the specified username/password
		Hashtable<String, String> props = new Hashtable<>();
		String principalName = username + "@" + domainName;
		props.put(Context.SECURITY_AUTHENTICATION, "simple");
		props.put(Context.SECURITY_PRINCIPAL, principalName);
		props.put(Context.SECURITY_CREDENTIALS, password);

		try {
			LdapCtxFactory.getLdapCtxInstance("ldap://" + serverName
					+ '/', props);
			return true;
		} catch (AuthenticationException a) {
			log.info("Authentication failed: " + a.getMessage(),a);
		} catch (NamingException e) {
			log.info("Failed to bind to LDAP / get account information: " + e,e);
		}
		return false;
	}

	public boolean authenticateGebruiker(String name, String password)
			throws VerzuimAuthenticationException, VerzuimApplicationException {
		GebruikerInfo info;
		String saltedPassword;
		String hashedPassword;
		boolean authresult;

		info = this.getGebruikerbyName(name);
		if (info == null) {
			log.warn("unknown user " + name + " attempted sign in");
			return false;
		}
		if (info.getStatus() == __status.BLOCKED) {
			Calendar lastattempt = Calendar.getInstance();
			if (info.getLaatstepoging() != null) {
				lastattempt.setTime(info.getLaatstepoging());
				Calendar current = Calendar.getInstance();
				if ((current.getTimeInMillis() - lastattempt.getTimeInMillis()) > 1000 * 60 * 15) {
					info.setStatus(__status.ACTIVE);
				}
			}
		}
		if (info.getStatus() != __status.ACTIVE) {
			log.warn("WARN: User " + name + " account not active: "
					+ Integer.toString(info.getStatus().getValue()));
			if (info.getStatus() == __status.BLOCKED)
				throw new VerzuimAuthenticationException("Account blocked");
			else if (info.getStatus() == __status.INACTIVE)
				throw new VerzuimAuthenticationException("Account inactive");
			else
				throw new VerzuimAuthenticationException("Account has invalid state");
		}
		if (info.isAduser()) {
			try {
				authresult = authenticateAgainstAD(name, password,
						info.getDomainname());
			} catch (Exception e) {
				log.warn("WARN: authenticate against AD failed: " + e);
				return false;
			}
		} else {
			saltedPassword = SALT + name + password;
			try {
				hashedPassword = generateHash(saltedPassword);
			} catch (NoSuchAlgorithmException e) {
				log.error("Could not hash password: " + e.getMessage(),e);
				throw new VerzuimAuthenticationException("Could not hash password");
			}
			authresult = hashedPassword.equals(info.getPasswordhash());
		}
		if (authresult) {
			log.info("user " + name + " signed in");
			info.setInlogfouten(0);
			info.setLaatstepoging(new Date());
			updateEntity(converter.toEntity(info, this.getCurrentuser()));
		} else {
			info.setInlogfouten(info.getInlogfouten() + 1);
			info.setLaatstepoging(new Date());
			log.error("user " + name + " authentication failure");
			if (info.getInlogfouten() >= 3) {
				info.setStatus(__status.BLOCKED);
				log.error("user " + name + " account blocked");
			}
			updateEntity(converter.toEntity(info, this.getCurrentuser()));
		}
		return authresult;
	}

	public boolean changePassword(String username, String newusername,
			String newPassword) throws VerzuimApplicationException, VerzuimAuthenticationException {
		GebruikerInfo info;
		String saltedPassword;
		String hashedPassword;
		info = this.getGebruikerbyName(username);
		if (info == null)
			return false;

		if (info.isAduser()) // AD Users cannot change password here
			return false;

		if (newusername.isEmpty())
			saltedPassword = SALT + username + newPassword;
		else {
			log.info("user " + info.getName() + " username changed to "
					+ newusername);
			info.setName(newusername);
			saltedPassword = SALT + newusername + newPassword;
		}
		try {
			hashedPassword = generateHash(saltedPassword);
		} catch (NoSuchAlgorithmException e) {
			log.error("Could not hash password: " + e.getMessage(),e);
			throw new VerzuimAuthenticationException("Could not hash password");
		}
		info.setPasswordhash(hashedPassword);
		log.debug("salted password   = " + saltedPassword);
		log.debug("hashed password   = " + hashedPassword);
		log.debug("database password = " + info.getPasswordhash());
		updateEntity(converter.toEntity(info, this.getCurrentuser()));
		log.info("INFO: user " + info.getName() + " password changed");
		return false;
	}

	public void deleteGebruiker(GebruikerInfo info)
			throws VerzuimApplicationException {
		Query qg = createQuery("Delete from Gebruiker g where g.id = :user_id");
		Query qw = createQuery("Delete from GebruikerWerkgever gw where gw.id.gebruikerid = :user_id");

		qg.setParameter("user_id", info.getId());
		qw.setParameter("user_id", info.getId());
		executeUpdate(qw);
		executeUpdate(qg);

	}

	public boolean isAuthorised(GebruikerInfo user, __applicatiefunctie func) {
		List<ApplicatieFunctieInfo> appfuncties;
		for (RolInfo r : user.getRollen()) {
			appfuncties = r.getApplicatiefuncties();
			for (ApplicatieFunctieInfo af : appfuncties) {
				if (Integer.parseInt(af.getFunctieId()) == func.getValue())
					return true;
			}
		}
		return false;
	}

	/**
	 * Open a specific text file containing mail server parameters, and populate
	 * a corresponding Properties object.
	 */
	private class SMTPAuthenticator extends Authenticator {
		private String localusername;
		private String localpassword;
		public SMTPAuthenticator(String username, String password){
			localusername = username;
			localpassword = password;
		}
		@Override
		public PasswordAuthentication getPasswordAuthentication() {
			return new PasswordAuthentication(localusername, localpassword);
		}
	}

	private void sendEmail(String toEmailAddr,
			String subject, String body) {

		Properties props = System.getProperties();
		Authenticator auth;
		try {
			SettingsInfo settings = settingsBean.getSettings();
			auth = new SMTPAuthenticator(settings.getSmtpmailuser(), settings.getSmtpmailpassword());
			
			props.setProperty("mail.smtp.host", settings.getSmtpmailhost());
			props.setProperty("mail.from", settings.getSmtpmailfromaddress());
			props.setProperty("mail.transport.protocol", "smtp");
			props.setProperty("mail.smtp.auth", "true");
		} catch (VerzuimApplicationException e) {
			log.error("Cannot send email. " + e);
			return;
		}

		Session session = Session.getInstance(props, auth);

		MimeMessage message = new MimeMessage(session);
		try {
			Transport transport = session.getTransport();
			message.addRecipient(Message.RecipientType.TO, new InternetAddress(
					toEmailAddr));
			message.setSubject(subject);
			message.setText(body);
			transport.connect();
			transport.sendMessage(message,
					message.getRecipients(Message.RecipientType.TO));
			transport.close();
		} catch (MessagingException ex) {
			log.error("Cannot send email. " + ex);
		}
	}

	public static String generateRandomPassword() {
		String letters = "abcdefghjklmnopqrstuvwxyzABCDEFGHJKLMNOPQRSTUVWXYZ0123456789+_#$%^&@";

		String pw = "";
		for (int i = 0; i < 12; i++) {
			int index = RANDOM.nextInt(letters.length()-1);
			pw += letters.substring(index, index + 1);
		}
		return pw;
	}

	public boolean sendNewpassword(GebruikerInfo info)
			throws PermissionException, VerzuimApplicationException {
		log.info("New password generated for " + info.getName());
		if (info.isAduser())
			throw new PermissionException(
					"AD Users kunnen geen nieuw wachtwoord ontvangen\n\r Neem contact op met de systeembeheerder");
		info.setNewPassword(generateRandomPassword());
		try {
			updateGebruiker(info);
		} catch (ValidationException e) {
			log.error("updateGebruiker failed during password change: " + e.getMessage(),e);
			return false;
		}
		String text = "Uw nieuwe wachtwoord voor devosverzuimbeheer.nl is\n\r\n\r"
				+ info.getNewPassword()
				+ "\n\r\n\r"
				+ "U kunt na het inloggen zelf uw wachtwoord weer wijzigen.\n"
				+ "Indien uw account geblokkeerd is na een aantal foutieve inlogpogingen, \n"
				+ "dan moet u maximaal 15 minuten wachten, alvorens u een nieuwe poging \n"
				+ "kunt doen.";
		sendEmail(info.getEmailadres(),
				"Wachtwoord vergeten", text);
		info.setNewPassword("");
		return true;
	}

}
