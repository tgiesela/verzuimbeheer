package com.gieselaar.verzuim.utils;

import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;

public class SMTPAuthenticator extends Authenticator {
	private String smtpusername, smtppassword;
	public SMTPAuthenticator(String username, String password){
		smtpusername = username;
		smtppassword = password;
	}
	public PasswordAuthentication getPasswordAuthentication() {
		//String username = "info@devosverzuimbeheer.nl";
		//String password = "infodvj";
		return new PasswordAuthentication(smtpusername, smtppassword);
	}
}

