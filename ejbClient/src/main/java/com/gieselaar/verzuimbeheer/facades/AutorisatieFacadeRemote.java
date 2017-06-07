package com.gieselaar.verzuimbeheer.facades;

import java.util.List;

import javax.ejb.Remote;

import com.gieselaar.verzuimbeheer.exceptions.VerzuimApplicationException;
import com.gieselaar.verzuimbeheer.exceptions.PermissionException;
import com.gieselaar.verzuimbeheer.exceptions.ValidationException;
import com.gieselaar.verzuimbeheer.services.ApplicatieFunctieInfo;
import com.gieselaar.verzuimbeheer.services.GebruikerInfo;
import com.gieselaar.verzuimbeheer.services.RolInfo;

@Remote
public interface AutorisatieFacadeRemote extends FacadeBaseRemote {
	List <RolInfo> getRollen() throws PermissionException, VerzuimApplicationException;
	List <RolInfo> getRollenForUser(int user) throws PermissionException, VerzuimApplicationException;
	List <ApplicatieFunctieInfo> getAppFuncties() throws PermissionException, VerzuimApplicationException;
	List <GebruikerInfo> getGebruikers() throws PermissionException, VerzuimApplicationException;

	RolInfo addRol(RolInfo rol)throws PermissionException, ValidationException, VerzuimApplicationException;
	void deleteRol(RolInfo rol)throws PermissionException, ValidationException, VerzuimApplicationException;
	RolInfo updateRol(RolInfo rol)throws PermissionException, ValidationException, VerzuimApplicationException;
	
	ApplicatieFunctieInfo addAppFunctie(ApplicatieFunctieInfo func)throws PermissionException, ValidationException, VerzuimApplicationException;
	void deleteAppFunctie(ApplicatieFunctieInfo func)throws PermissionException, ValidationException, VerzuimApplicationException;
	ApplicatieFunctieInfo updateAppFunctie(ApplicatieFunctieInfo func)throws PermissionException, ValidationException, VerzuimApplicationException;

	GebruikerInfo addGebruiker(GebruikerInfo gebruiker)throws PermissionException, ValidationException, VerzuimApplicationException;
	void deleteGebruiker(GebruikerInfo gebruiker)throws PermissionException, ValidationException, VerzuimApplicationException;
	GebruikerInfo updateGebruiker(GebruikerInfo gebruiker)throws PermissionException, ValidationException, VerzuimApplicationException;

	boolean isAuthorised(GebruikerInfo user, __applicatiefunctie func);
	void sendNewPassword(String username, String emailaddress) throws PermissionException, ValidationException, VerzuimApplicationException;
	
	void setLoginSession(LoginSessionRemote session) throws PermissionException;
	public enum __applicatiefunctie
	{
		/*
		 * Nooit de nummering aanpassen. De nummers worden in de database gebruikt.
		 * Als de nummering toch aangepast moet worden, dan moet de hele autorisatie
		 * database (Rollen, Applicatie functies) worden verwijderd.
		 */
		RAADPLEGENWERKGEVER(1) 		{@Override public String toString(){return "Raadplegen Werkgever";}},
		BEHEERWERKGEVER(2) 			{@Override public String toString(){return "Beheer Werkgever";}},
		VERWIJDERWERKGEVER(3) 		{@Override public String toString(){return "Verwijder Werkgever";}},
		RAADPLEGENWERKNEMER(4) 		{@Override public String toString(){return "Raadplegen Werknemer";}},
		BEHEERWERKNEMER(5) 			{@Override public String toString(){return "BeheerWerknemer";}},
		VERWIJDERWERKNEMER(6) 		{@Override public String toString(){return "Verwijder Werknemer";}},
		RAADPLEGENAUTORISATIES(7) 	{@Override public String toString(){return "Raadplegen Autorisaties";}},
		BEHEERAUTORISATIES(8) 		{@Override public String toString(){return "Beheer Autorisaties";}},
		VERWIJDERAUTORISATIES(9)	{@Override public String toString(){return "Verwijderd Autorisaties";}},
		RAADPLEGENPAKKET(10) 		{@Override public String toString(){return "Raadplegen Pakketten";}}, 
		BEHEERPAKKET(11) 			{@Override public String toString(){return "Beheer Pakketten";}},
		VERWIJDERPAKKET(12) 		{@Override public String toString(){return "Verwijder Pakketten";}},
		RAADPLEGENVERZUIM(13)		{@Override public String toString(){return "Raadplegen Verzuim";}},
		BEHEERVERZUIM(14)			{@Override public String toString(){return "Beheer Verzuim";}},
		VERWIJDERVERZUIM(15)		{@Override public String toString(){return "Verwijder Verzuim";}},
		RAADPLEGENTABELLEN(16)		{@Override public String toString(){return "Raadplegen Tabellen";}},
		BEHEERTABELLEN(17)			{@Override public String toString(){return "Beheer Tabellen";}},
		VERWIJDERTABELLEN(18)		{@Override public String toString(){return "Verwijder Tabellen";}},
		RAADPLEGENTEMPLATES(19)		{@Override public String toString(){return "Raadplegen Template";}},
		BEHEERTEMPLATES(20)			{@Override public String toString(){return "Beheer Template";}},
		VERWIJDERTEMPLATES(21)		{@Override public String toString(){return "Verwijder Template";}},
		RAADPLEGENINSTANTIES(22)	{@Override public String toString(){return "Raadplegen Instanties";}},
		BEHEERINSTANTIES(23)		{@Override public String toString(){return "Beheer Instanties";}},
		VERWIJDERINSTANTIES(24)		{@Override public String toString(){return "Verwijder Instanties";}},
		RAADPLEGENFACTUREN(25)		{@Override public String toString(){return "Raadplegen Facturen";}},
		BEHEERFACTUREN(26)			{@Override public String toString(){return "Beheer Facturen";}},
		VERWIJDERFACTUREN(27)		{@Override public String toString(){return "Verwijder Facturen";}},
		RAADPLEGENTARIEVEN(28)		{@Override public String toString(){return "Raadplegen Tarieven";}},
		BEHEERTARIEVEN(29)			{@Override public String toString(){return "Beheer Tarieven";}},
		VERWIJDERTARIEVEN(30)		{@Override public String toString(){return "Verwijder Tarieven";}},
		RAADPLEGENWERKZAAMHEDEN(31) {@Override public String toString(){return "Raddplegen Werkzaamheden";}},
		BEHEERWERKZAAMHEDEN(32)		{@Override public String toString(){return "Beheer Werkzaamheden";}},
		VERWIJDERWERKZAAMHEDEN(33)	{@Override public String toString(){return "Verwijder Werkzaamheden";}},
		WERKZAAMHEDENALLUSERS(34)	{@Override public String toString(){return "Werkzaamheden van alle gebruikers";}};
		
		private int value;
		__applicatiefunctie(int value){
			this.value = value;
		}
		public int getValue() { return value; }

        public static __applicatiefunctie parse(int id) {
        	__applicatiefunctie applfunc = null; // Default
            for (__applicatiefunctie item : __applicatiefunctie.values()) {
                if (item.getValue()==id) {
                    applfunc = item;
                    break;
                }
            }
            return applfunc;
        }
	}
	GebruikerInfo getGebruiker(int id) throws PermissionException, VerzuimApplicationException;
}
