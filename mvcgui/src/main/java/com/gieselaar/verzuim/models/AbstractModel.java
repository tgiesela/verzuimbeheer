package com.gieselaar.verzuim.models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.gieselaar.verzuim.interfaces.ModelEventListener;
import com.gieselaar.verzuimbeheer.exceptions.VerzuimApplicationException;
import com.gieselaar.verzuimbeheer.facades.LoginSessionRemote;

public abstract class AbstractModel implements Serializable{
	private static final long serialVersionUID = 1L;
	private transient LoginSessionRemote session;
	protected transient List<ModelEventListener> changelisteners = new ArrayList<>();

	public AbstractModel(LoginSessionRemote session) {
		this.session = session;
	}
	public void addModelEventListener(ModelEventListener l)
	{
		changelisteners.add(l);
	}
	/*
	 *	Getters and setters 
	 */
	public LoginSessionRemote getSession() {
		return session;
	}
	public void setSession(LoginSessionRemote session) {
		this.session = session;
	}
	public abstract void refreshDatabase() throws VerzuimApplicationException;
	public void deleteModelEventListener(ModelEventListener abstractController) {
		changelisteners.remove(abstractController);
	}
}
