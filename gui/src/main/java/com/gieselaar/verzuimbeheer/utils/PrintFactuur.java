package com.gieselaar.verzuimbeheer.utils;

import java.awt.Desktop;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Calendar;
import java.util.List;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import org.apache.log4j.Logger;

import com.gieselaar.verzuimbeheer.exceptions.VerzuimApplicationException;
import com.gieselaar.verzuimbeheer.exceptions.PermissionException;
import com.gieselaar.verzuimbeheer.exceptions.ValidationException;
import com.gieselaar.verzuimbeheer.facades.LoginSessionRemote;
import com.gieselaar.verzuimbeheer.services.FactuurTotaalInfo;
import com.gieselaar.verzuimbeheer.services.HoldingInfo;
import com.gieselaar.verzuimbeheer.services.HoldingInfo.__factuurtype;
import com.gieselaar.verzuimbeheer.services.SettingsInfo;

public class PrintFactuur {
	protected static Logger log = Logger.getLogger("com.gieselaar");
	private FactuurTotaalInfo fti;
	private LoginSessionRemote loginSession;
	private List<FactuurTotaalInfo> werkgeverfacturen;
	private boolean dontprint = false;
	private boolean separaatversturen = false;
	private SettingsInfo settings;

	public PrintFactuur(FactuurTotaalInfo factuur, LoginSessionRemote session)
			throws PermissionException, VerzuimApplicationException,
			ServiceLocatorException {
		if (factuur == null)
			throw new RuntimeException("FactuurTotaalInfo should not be null!");
		fti = factuur;
		if (session == null)
			throw new RuntimeException("LoginSessionRemote should not be null!");
		loginSession = session;
		settings = ServiceCaller.settingsFacade(loginSession).getSettings();
		werkgeverfacturen = getWerkgeverFacturen();
		dontprint = true;
		if (werkgeverfacturen == null || werkgeverfacturen.size() == 0) { /* Het gaat om 1 enkele factuur*/
			if (fti.getTotaalInclBtw().compareTo(BigDecimal.ZERO) == 0) {
				;
			}else{
				dontprint = false;
			}
		} else {
			for (FactuurTotaalInfo f : werkgeverfacturen) {
				if (f.getTotaalInclBtw().compareTo(BigDecimal.ZERO) == 0) {
					;
				} else {
					dontprint = false;
				}
			}
		}
	}
	public PrintFactuur(){
		
	}

	private List<FactuurTotaalInfo> getWerkgeverFacturen()
			throws PermissionException, VerzuimApplicationException,
			ServiceLocatorException {
		HoldingInfo h;
		Calendar cal = Calendar.getInstance();
		cal.set(fti.getJaar(), fti.getMaand() - 1, 1);
		if (fti.getHoldingid() == null)
			return null;
		h = ServiceCaller.werkgeverFacade(loginSession).getHolding(
				fti.getHoldingid());
		if (h.isFactureren()) {
			/*
			 * Als het om een holding gaat, dan zijn er drie mogelijkheden: 1:
			 * Separaat: alle facturen van werkgevers onder de holding worden
			 * allemaal meegstuurd/afgedrukt 2: Geaggregeerd: alle facturen
			 * wijzen naar dezelfde pdf. Die van de opgegeven factuur kan worden
			 * verzonden/afgedrukt 3: Gespecificeerd: Als Geaggregeerd.
			 * 
			 * Als het om 'Separaat' gaat, dan worden de afzonderlijke facturen
			 * in werkgeverfacturen gezet In alle andere gevallen is
			 * werkgeverfacturen leeg.
			 */
			List<FactuurTotaalInfo> facturen = ServiceCaller.factuurFacade(loginSession)
					.getFacturenInPeriodeByHolding(cal.getTime(),
							cal.getTime(), h.getId(), false);
			if (h.getFactuurtype() == __factuurtype.SEPARAAT) {
				separaatversturen = true;
				return facturen;
			} else {
				return facturen;
			}
		} else {
			return null;
		}

	}
	public boolean isPrinten(){
		return !dontprint;
	}
	
	public void email(String emailaddress, String naam) throws VerzuimApplicationException {
		String bccaddress;
		boolean usetls = false;
		boolean usessl = false;
		if (dontprint)
			return;
		Properties props = System.getProperties();
		Authenticator auth = new SMTPAuthenticator(settings.getSmtpmailuser(), settings.getSmtpmailpassword());
		
		props.setProperty("mail.smtp.auth","true");
		props.setProperty("mail.smtp.host",
				settings.getSmtpmailhost());
		if (usetls){
			props.setProperty("mail.smtp.starttls.enable", "true");
			props.setProperty("mail.smtp.port","587");
		}else{
			if (usessl){
				props.setProperty("mail.smtp.host",
						settings.getSmtpmailhost());
				props.setProperty("mail.smtp.socketFactory.port", "465");
				props.setProperty("mail.smtp.socketFactory.class","javax.net.ssl.SSLSocketFactory");			
				props.setProperty("mail.smtp.port","465");
			}
		}
		   
		Session session = Session.getInstance(props, auth);

		MimeMessage message = new MimeMessage(session);
		try {
			message.setFrom(new InternetAddress(settings.getSmtpmailfromaddress()));
			message.addRecipient(Message.RecipientType.TO, new InternetAddress(emailaddress));
			bccaddress = settings.getBccemailaddress();
			if (bccaddress == null  || bccaddress.isEmpty())
				;
			else
				message.addRecipient(Message.RecipientType.BCC, new InternetAddress(bccaddress));
			
			message.setSubject("Factuur van De Vos Verzuimbeheer voor " + naam + " over periode: "
					+ fti.getMaand() + "-" + fti.getJaar());

			MimeBodyPart messagebodyPart = new MimeBodyPart();
			messagebodyPart.setHeader("Content-type", "text/xml");
			byte[] encoded = Files.readAllBytes(Paths.get(settings.getFactuurmailtextbody()));
			messagebodyPart.setContent(new String(encoded,
					StandardCharsets.US_ASCII), "text/plain");

			Multipart multipart = new MimeMultipart();
			multipart.addBodyPart(messagebodyPart);

			if (separaatversturen) {
				for (FactuurTotaalInfo factuur : werkgeverfacturen) {
					MimeBodyPart attachmentpart = new MimeBodyPart();
					DataSource source = new FileDataSource(factuur.getPdflocation());
					attachmentpart.setDataHandler(new DataHandler(source));
					attachmentpart.setFileName(factuur.getWerkgever().getNaam()
							+ ".pdf");
					multipart.addBodyPart(attachmentpart);
				}
			} else {
				MimeBodyPart attachmentpart = new MimeBodyPart();
				DataSource source = new FileDataSource(fti.getPdflocation());
				attachmentpart.setDataHandler(new DataHandler(source));
				attachmentpart.setFileName("Factuur.pdf");
				multipart.addBodyPart(attachmentpart);
			}

			message.setContent(multipart);

			Transport.send(message);
		} catch (MessagingException ex) {
			throw new VerzuimApplicationException(ex, "Cannot send email.");
		} catch (FileNotFoundException e) {
			throw new VerzuimApplicationException(e, "Cannot send email: FileNotFound. ");
		} catch (IOException e) {
			throw new VerzuimApplicationException(e, "Cannot send email: IOException. ");
		}

	}
	public void afdrukken() throws ValidationException,
			VerzuimApplicationException, PermissionException,
			ServiceLocatorException, IOException {
		if (dontprint)
			return;
		Desktop desktop = Desktop.getDesktop();
		if (werkgeverfacturen != null) {
			for (FactuurTotaalInfo factuur : werkgeverfacturen) {
				desktop.print(new File(factuur.getPdflocation()));
			}
		} else {
			desktop.print(new File(fti.getPdflocation()));
		}
	}

	public void afdrukkenDezeFactuur() throws IOException, ValidationException,
			VerzuimApplicationException, PermissionException,
			ServiceLocatorException {
		werkgeverfacturen = null;
		if (fti.getTotaalInclBtw().equals(BigDecimal.ZERO)) {
			/* Factuur with amount zeroes not send */
		} else {
			afdrukken();
		}
	}

	public void emailDezeFactuur(String emailaddress, String naam) throws VerzuimApplicationException {
		werkgeverfacturen = null;
		if (fti.getTotaalInclBtw().equals(BigDecimal.ZERO)) {
			/* Factuur with amount zeroes not send */
		} else {
			email(emailaddress, naam);
		}
	}
}
