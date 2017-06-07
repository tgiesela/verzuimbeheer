package com.gieselaar.verzuimbeheer.components;

import java.util.Map;
import java.util.Set;

import javax.faces.application.ConfigurableNavigationHandler;
import javax.faces.application.NavigationCase;
import javax.faces.application.NavigationHandler;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;

public class HistoryNavigationHandler extends ConfigurableNavigationHandler {

    private NavigationHandler wrapped;

    public HistoryNavigationHandler(NavigationHandler wrapped) {
        this.wrapped = wrapped;
    }

    @Override
    public void handleNavigation(FacesContext context, String from, String outcome) {

    	System.out.println("RequestURI:" + ((HttpServletRequest)context.getExternalContext().getRequest()).getRequestURI());
    	System.out.println("Navigating from: " + from + " outcome:" + outcome + ":" + context.getViewRoot().toString());
    	
        wrapped.handleNavigation(context, from, outcome);        
    }

    @Override
    public NavigationCase getNavigationCase(FacesContext context, String fromAction, String outcome) {
        if (wrapped instanceof ConfigurableNavigationHandler){
        	return ((ConfigurableNavigationHandler) wrapped).getNavigationCase(context, fromAction, outcome);
        } else {
            return null;
        }
    }

    @Override
    public Map<String, Set<NavigationCase>> getNavigationCases() {
        if (wrapped instanceof ConfigurableNavigationHandler){
            return ((ConfigurableNavigationHandler) wrapped).getNavigationCases();
        } else {
            return null;
        }
    }

}
