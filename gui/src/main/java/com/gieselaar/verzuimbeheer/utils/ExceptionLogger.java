package com.gieselaar.verzuimbeheer.utils;

import java.awt.Component;

import javax.swing.JLabel;
import javax.swing.JOptionPane;

import org.apache.log4j.Logger;

import com.gieselaar.verzuimbeheer.exceptions.VerzuimApplicationException;
import com.gieselaar.verzuimbeheer.exceptions.VerzuimApplicationException.__applicationExceptiontype;

public class ExceptionLogger {

	static private int width = 172;
	static Logger log = Logger.getLogger(ExceptionLogger.class); 
	public ExceptionLogger(){
	}
	static void printStackTrace(Exception e){
		log.error("", e);
		//e.fillInStackTrace();
		//log.error("After fillInStackTrace()", e);
	}
	private static String getMessage(Throwable e){
		if (e == null) 
			return "";
		Throwable t = e.getCause();
		if (e.getMessage() == null || e.getMessage().isEmpty()){
			if (t == null){
				return "<p></p>";
			} else {
				return "<p></p>" + getMessage(t);
			}
		}else{
			return "<p>" + e.getMessage() + "</p><p>" + getMessage(t) + "</p>";
		}
			
	}
	private static void showDialog(Exception e, Component parent, int type, String ApplicationMessage){
		JLabel lbl;
		if (e instanceof VerzuimApplicationException){
			VerzuimApplicationException ae = (VerzuimApplicationException)e;
			if (ae.getType() == __applicationExceptiontype.OPTIMISTICLOCKEXCEPTION){
				lbl = new JLabel("<html><body width=" + width + "><p>" + "Iemand anders heeft de gegevens ondertussen gewijzigd. Vraag ze opnieuw op" + "</p></html>");				
			} else {
				lbl = new JLabel("<html><body width=" + width + "><p>" + getMessage(e) + "</p></html>");				
			}
		}
		else
			if (ApplicationMessage.isEmpty()){
				if (e instanceof NullPointerException){
					lbl = new JLabel("<html><body width=" + width + "><p>" + "NullPointerException.\nSee logfile for details." + "</p></html>");
				}else{
					lbl = new JLabel("<html><body width=" + width + "><p>" + getMessage(e) + "</p></html>");
				}
			} else
				lbl = new JLabel("<html><body width=" + width + "><p>" + getMessage(e) + "</p><p>" + ApplicationMessage + "</p></html>");
		JOptionPane.showMessageDialog(parent, lbl, "Error", type);	
	}
	public static void ProcessException(Exception e, Component parent, String ApplicationMessage){
    	printStackTrace(e);
		showDialog(e, parent, JOptionPane.ERROR_MESSAGE, ApplicationMessage);
	}
	public static void ProcessException(Exception e, Component parent){
    	printStackTrace(e);
		showDialog(e, parent, JOptionPane.ERROR_MESSAGE,"");
	}
	public static void ProcessException(Exception e, Component parent,boolean stacktrace){
    	// if (stacktrace)
    		printStackTrace(e);
		showDialog(e, parent, JOptionPane.ERROR_MESSAGE,"");
	}
}
