package com.gieselaar.verzuimbeheer.verzuimweb;

import java.io.Serializable;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

import com.gieselaar.verzuimbeheer.facades.LoginSessionRemote;

@ManagedBean
@ViewScoped
public class mainClass extends BackingBeanBase implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	/* 
	 * Backing bean voor het main form
	 */
	private LoginSessionRemote loginsession;
	private FacesContext context;
	public mainClass(){
		context = FacesContext.getCurrentInstance();
		loginClass sessioncontext = (loginClass) context.getApplication()
				.evaluateExpressionGet(context, "#{loginClass}",
						loginClass.class);
		loginsession = sessioncontext.loginSession;
	}
}
