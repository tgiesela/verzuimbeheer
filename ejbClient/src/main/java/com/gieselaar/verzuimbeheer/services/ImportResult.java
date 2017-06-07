package com.gieselaar.verzuimbeheer.services;

import com.gieselaar.verzuimbeheer.exceptions.ValidationException;

public class ImportResult extends InfoBase {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	String werknemer;
	String sourceLine;
	boolean importok;
	boolean warning;
	String errorMessage;
	int result;
	
	public int getResult() {
		return result;
	}

	public void setResult(int result) {
		this.result = result;
	}

	public String getWerknemer() {
		return werknemer;
	}

	public void setWerknemer(String werknemer) {
		this.werknemer = werknemer;
	}

	public String getSourceLine() {
		return sourceLine;
	}

	public void setSourceLine(String sourceLine) {
		this.sourceLine = sourceLine;
	}

	public boolean isImportok() {
		return importok;
	}

	public void setImportok(boolean importok) {
		this.importok = importok;
	}

	public boolean isWarning() {
		return warning;
	}

	public void setWarning(boolean warning) {
		this.warning = warning;
	}

	public String getErrorMessage() {
		return errorMessage;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}

	@Override
	public boolean validate() throws ValidationException {
		return false;
	}
	

}
