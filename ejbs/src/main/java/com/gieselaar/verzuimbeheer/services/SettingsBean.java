package com.gieselaar.verzuimbeheer.services;

import java.util.List;

import javax.annotation.Resource;
import javax.ejb.EJBContext;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.Query;

import com.gieselaar.verzuimbeheer.entities.Settings;
import com.gieselaar.verzuimbeheer.exceptions.VerzuimApplicationException;
import com.gieselaar.verzuimbeheer.exceptions.ValidationException;
import com.gieselaar.verzuimbeheer.utils.SettingsConversion;

/**
 * Session Bean implementation class WerkgeverBean
 */
@Stateless
@LocalBean
public class SettingsBean extends BeanBase{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Resource transient EJBContext context;
	private SettingsConversion converter;
	/**
     * Default constructor. 
     */
	public SettingsBean() {
		converter = new SettingsConversion();
    }
    private SettingsInfo completeSettings(Settings w){
		return converter.fromEntity(w);
    }
	public SettingsInfo getSettings() throws VerzuimApplicationException {
		Query q = createQuery("select t from Settings t");
		@SuppressWarnings("unchecked")
		List<Settings> result = (List<Settings>)getResultList(q);
		if (result.size() != 1)
			return null;
		Settings t = result.get(0);
		
		return completeSettings(t);
	}
	
	public void updateSettings(SettingsInfo info) throws ValidationException, VerzuimApplicationException{
		try {
			info.validate();
		} catch (ValidationException e) {
			throw e;
		}
		Settings settings = converter.toEntity(info, this.getCurrentuser());
    	this.updateEntity(settings);
    	info.setId(settings.getId());
	}
}
