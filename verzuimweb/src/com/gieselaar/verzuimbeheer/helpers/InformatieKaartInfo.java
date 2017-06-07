package com.gieselaar.verzuimbeheer.helpers;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.gieselaar.verzuimbeheer.services.VerzuimMedischekaartInfo;

public class InformatieKaartInfo implements Serializable, Comparable<InformatieKaartInfo> {
		 
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Object date;
    private String user;
    private String text;
    private boolean editable;
    private VerzuimMedischekaartInfo kaart;
     
    public InformatieKaartInfo(Object date, String user, String text, VerzuimMedischekaartInfo object, boolean editable) {
        this.date = date;
        this.user = user;
        this.text = text;
        this.kaart = object;
        this.editable = editable;
    }
 
 
    public String getDate() {
    	if (date instanceof Date)
    		return new SimpleDateFormat("dd-MM-yyyy").format(date);
    	else
    		return date.toString();
	}


	public void setDate(Object date) {
		this.date = date;
	}


	public String getUser() {
		return user;
	}


	public void setUser(String user) {
		this.user = user;
	}


	public String getText() {
		return text;
	}


	public void setText(String text) {
		this.text = text;
	}


	public boolean isEditable() {
		return editable;
	}


	public void setEditable(boolean editable) {
		this.editable = editable;
	}


	public VerzuimMedischekaartInfo getKaart() {
		return kaart;
	}


	public void setKaart(VerzuimMedischekaartInfo kaart) {
		this.kaart = kaart;
	}


	//Eclipse Generated hashCode and equals
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((date == null) ? 0 : date.hashCode());
        result = prime * result + ((user == null) ? 0 : user.hashCode());
        result = prime * result + ((text == null) ? 0 : text.hashCode());
        return result;
    }
 
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        InformatieKaartInfo other = (InformatieKaartInfo) obj;
        if (date == null) {
            if (other.date != null)
                return false;
        } else if (!date.equals(other.date))
            return false;
        if (user == null) {
            if (other.user != null)
                return false;
        } else if (!user.equals(other.user))
            return false;
        if (text == null) {
            if (other.text != null)
                return false;
        } else if (!text.equals(other.text))
            return false;
        return true;
    }
 
    public int compareTo(InformatieKaartInfo document) {
		int i;
		i = this.getDate().compareTo(document.getDate());
		if (i != 0) return i;
		
		i = this.getUser().compareTo(document.getUser());
		if (i != 0) return i;
		
        return i;
    }
}
