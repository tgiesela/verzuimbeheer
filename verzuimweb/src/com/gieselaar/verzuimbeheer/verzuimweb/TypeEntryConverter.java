package com.gieselaar.verzuimbeheer.verzuimweb;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;

public class TypeEntryConverter implements Converter{
    public String getAsString(FacesContext context, UIComponent component, Object value) {
        // This method is called when item value is to be converted to HTTP request parameter.
        // Normal practice is to return an unique identifier here, such as student ID.
    	System.out.println("TypeEntryConvert.getAsString()" + value.toString());
    	if (value instanceof TypeEntry){
        	TypeEntry entry = (TypeEntry) value;
            int id = entry.getValue();
            return String.valueOf(id);
    	}
    	return null;
    }

    public Object getAsObject(FacesContext context, UIComponent component, String value) {
        // This method is called when HTTP request parameter is to be converted to item value.
        // You need to convert the student ID back to Student.
    	try{
        	System.out.println("TypeEntryConvert.getAsObject()" + value);
            TypeEntry student = new TypeEntry(2,"Item 2");
            return student;
    	}catch(Throwable e) {
    		FacesMessage msg = new FacesMessage(e.getMessage());
    		msg.setSeverity(FacesMessage.SEVERITY_ERROR);
    		throw new ConverterException(msg);
    	}
    }
}

