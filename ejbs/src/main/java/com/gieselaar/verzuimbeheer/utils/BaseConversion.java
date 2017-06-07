package com.gieselaar.verzuimbeheer.utils;

import java.io.Serializable;

import com.gieselaar.verzuimbeheer.entitiesutils.EntityBaseId;
import com.gieselaar.verzuimbeheer.services.InfoBase;
import com.gieselaar.verzuimbeheer.services.InfoBase.persistenceaction;
import com.gieselaar.verzuimbeheer.services.InfoBase.persistencestate;

public class BaseConversion implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	protected boolean inttobool(int b){
    	if (b == 0)
    		return false;
    	else
    		return true;
    }
	protected int booltoint(boolean b){
    	if (b )
    		return 1;
    	else
    		return 0;
    }
	protected void setUpdate(InfoBase info){
		info.setAction(persistenceaction.UPDATE);
	}
	void setVersionId(InfoBase info,EntityBaseId eb) {
		info.setId(eb.getId());
		info.setVersion(eb.getVersion());
		info.setCreatedby(eb.getCreatedby());
		info.setUpdatedby(eb.getUpdatedby());
		info.setCreationts(eb.getCreationts());
		info.setUpdatets(eb.getUpdatedts());
	}
	void getVersionId(InfoBase info,EntityBaseId eb,Integer user) {
		eb.setId(info.getId());
		eb.setVersion(info.getVersion());
		eb.setCreatedby(info.getCreatedby());
		eb.setUpdatedby(info.getUpdatedby());
		eb.setCreationts(info.getCreationts());
		eb.setUpdatedts(info.getUpdatets());
		if (info.getState() == persistencestate.EXISTS)
			eb.setUpdatedby(user);
		else
			eb.setCreatedby(user);
	}
	@SuppressWarnings({ "unchecked" })
	protected <T extends InfoBase> T create(Class<?> info){
    	T localclass;
    	try {
    		localclass = (T) Class.forName(info.getCanonicalName()).newInstance(); 
			localclass.setState(persistencestate.EXISTS);
			localclass.setAction(persistenceaction.UPDATE);
			return localclass;

		} catch (InstantiationException e) {
			return null;
		} catch (IllegalAccessException e) {
			return null;
		} catch (ClassNotFoundException e) {
			return null;
		}
    }

}
