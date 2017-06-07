package com.gieselaar.verzuim.models;

import java.util.List;

import com.gieselaar.verzuim.interfaces.ModelEventListener;
import com.gieselaar.verzuimbeheer.exceptions.PermissionException;
import com.gieselaar.verzuimbeheer.exceptions.VerzuimApplicationException;
import com.gieselaar.verzuimbeheer.facades.LoginSessionRemote;
import com.gieselaar.verzuimbeheer.services.ActiviteitInfo;
import com.gieselaar.verzuimbeheer.utils.ServiceCaller;
import com.gieselaar.verzuimbeheer.utils.ServiceLocatorException;

public class ActiviteitModel extends AbstractModel {
	private static final long serialVersionUID = 1L;
	private List<ActiviteitInfo> documenttemplates;
	public ActiviteitModel(LoginSessionRemote session){
		super(session);
	}

	public void selectActiviteiten() throws VerzuimApplicationException {
		try {
			documenttemplates = ServiceCaller.verzuimFacade(this.getSession()).getActiviteiten();
			for (ModelEventListener ml: this.changelisteners){
				ml.listComplete(documenttemplates);
			}
		} catch (PermissionException | ServiceLocatorException e) {
			throw new VerzuimApplicationException(e, e.getMessage());
		}
	}

	public List<ActiviteitInfo> getActiviteitenList() {
		return documenttemplates;
	}

	@Override
	public void refreshDatabase() throws VerzuimApplicationException {
		try {
			selectActiviteiten();
		} catch (VerzuimApplicationException e) {
			throw new VerzuimApplicationException(e, e.getMessage());
		}
	}
}
