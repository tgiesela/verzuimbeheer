package com.gieselaar.verzuimbeheer.utils;



public enum RemoteInterfaces {
	AutorisatieFacade("java:global/verzuimbeheer/AutorisatieFacade"),
	CascodeFacade("java:global/verzuimbeheer/CascodeFacade"),
	FactuurFacade("java:global/verzuimbeheer/FactuurFacade"),
	InstantieFacade("java:global/verzuimbeheer/InstantieFacade"),
	PakketFacade("java:global/verzuimbeheer/PakketFacade"),
	ReportFacade("java:global/verzuimbeheer/ReportFacade"),
	VerzuimFacade("java:global/verzuimbeheer/VerzuimFacade"),
	WerkgeverFacade("java:global/verzuimbeheer/WerkgeverFacade"),
	WerknemerFacade("java:global/verzuimbeheer/WerknemerFacade"),
	LoginSession("java:global/verzuimbeheer/LoginSession"), 
	SettingsFacade("java:global/verzuimbeheer/SettingsFacade");

	private String value;
	public String getValue(){
		return value;
	}
	RemoteInterfaces(String str) {
        value = str;
    }
}
