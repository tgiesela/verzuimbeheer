package com.gieselaar.verzuimbeheer.helpers;

import javax.faces.context.FacesContext;

import org.apache.log4j.Logger;

public class FacesUtil {
	// Getters -----------------------------------------------------------------------------------
	protected final static Logger logger = Logger.getLogger(FacesUtil.class);
    public static Object getSessionMapValue(String key) {
    	logger.debug("View" + FacesContext.getCurrentInstance().getViewRoot().getViewId().toString());
    	logger.debug("Retrieve " + key);
    	return FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get(key);
    }

    // Setters -----------------------------------------------------------------------------------

    public static void setSessionMapValue(String key, Object value) {
    	logger.debug("View" + FacesContext.getCurrentInstance().getViewRoot().getViewId().toString());
    	logger.debug("Store " + key + ", value " + value.toString());
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(key, value);
    }

}
