package com.gieselaar.verzuim.models;

import java.util.Date;
import java.util.List;

import com.gieselaar.verzuimbeheer.exceptions.PermissionException;
import com.gieselaar.verzuimbeheer.exceptions.ValidationException;
import com.gieselaar.verzuimbeheer.exceptions.VerzuimApplicationException;
import com.gieselaar.verzuimbeheer.facades.LoginSessionRemote;
import com.gieselaar.verzuimbeheer.reportservices.ActueelVerzuimInfo;
import com.gieselaar.verzuimbeheer.reportservices.WerknemerVerzuimInfo;
import com.gieselaar.verzuimbeheer.utils.ServiceCaller;
import com.gieselaar.verzuimbeheer.utils.ServiceLocatorException;

public class ReportModel extends AbstractModel {
	private static final long serialVersionUID = 1L;

	public ReportModel(LoginSessionRemote session) {
		super(session);
	}

	public List<WerknemerVerzuimInfo> getWerknemerVerzuimen(Integer werkgeverid, Integer holdingid, Integer oeid,
			Date datefrom, Date dateuntil, int aantal) throws VerzuimApplicationException {
		try {
			return ServiceCaller.reportFacade(getSession()).getWerknemerVerzuimen(werkgeverid, holdingid, oeid,
					datefrom, dateuntil, aantal);
		} catch (PermissionException | ServiceLocatorException | ValidationException e) {
			throw new VerzuimApplicationException(e, e.getMessage());
		}
	}

	@Override
	public void refreshDatabase() throws VerzuimApplicationException {
		/* noop */
	}

	public List<ActueelVerzuimInfo> getActueelVerzuim(Integer werkgeverid, Integer holdingid, Integer oeid,
			Date datefrom, Date dateuntil, boolean inclusiefzwangerschap) throws VerzuimApplicationException {
		try {
			return ServiceCaller.reportFacade(getSession()).getActueelVerzuim(werkgeverid, holdingid, oeid, datefrom,
					dateuntil, inclusiefzwangerschap);
		} catch (PermissionException | ServiceLocatorException | ValidationException e) {
			throw new VerzuimApplicationException(e, e.getMessage());
		}
	}

}
