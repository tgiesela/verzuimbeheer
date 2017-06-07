package com.gieselaar.verzuimbeheer.verzuimweb;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;

public class validatePassword implements Validator {

	public validatePassword() {
	}

	@Override
	public void validate(FacesContext context, UIComponent component, Object value)
			throws ValidatorException {
       	String password = (String) value;
       	if(password.contains(" ")) {
       		FacesMessage message = new FacesMessage();            
       		message.setSeverity(FacesMessage.SEVERITY_ERROR);            
       		message.setSummary("password contains invalid characters." + password);            
       		message.setDetail("Invalid characters." );            
       		context.addMessage(null, message);            
       		throw new ValidatorException(message);        
       	}
     }
}
