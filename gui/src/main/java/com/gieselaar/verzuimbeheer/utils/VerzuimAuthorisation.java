package com.gieselaar.verzuimbeheer.utils;

import java.util.List;

import com.gieselaar.verzuimbeheer.facades.AutorisatieFacadeRemote.__applicatiefunctie;
import com.gieselaar.verzuimbeheer.services.ApplicatieFunctieInfo;
import com.gieselaar.verzuimbeheer.services.GebruikerInfo;
import com.gieselaar.verzuimbeheer.services.RolInfo;

public class VerzuimAuthorisation {
	public static boolean isAuthorised(GebruikerInfo user, __applicatiefunctie func) {
		List<ApplicatieFunctieInfo> appfuncties; 
		for (RolInfo r: user.getRollen())
		{
			appfuncties = r.getApplicatiefuncties();
			for (ApplicatieFunctieInfo af: appfuncties)
			{
				if (Integer.parseInt(af.getFunctieId()) == func.getValue())
					return true;
			}
		}
		return false;
	}

}
