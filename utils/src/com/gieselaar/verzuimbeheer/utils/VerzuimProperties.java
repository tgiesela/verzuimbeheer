package com.gieselaar.verzuimbeheer.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * Provides a Excel Export page example using the Apache POI library.
 */
public class VerzuimProperties {
	private String propertiesfilename;
	private String userdir; 
	private String propertiesdir;
	private String pathseparator;
	private Properties props;
	public enum __verzuimproperty
	{
		Hostname(0) 		{public String toString(){return "hostname";}},
		Portnumber(1)		{public String toString(){return "portnumber";}},
		username(2) 		{public String toString(){return "username";}},
		lastexportdir(3) 	{public String toString(){return "exportdir";}},
		lastdocsavedir(4) 	{public String toString(){return "docsavedir";}};
		
		private Integer value;
		__verzuimproperty(Integer value){
			this.value = value;
		}
		public Integer getValue() { return value; }

        public static __verzuimproperty parse(Integer type) {
        	__verzuimproperty property = null; // Default
            for (__verzuimproperty item : __verzuimproperty.values()) {
                if (item.getValue().equals(type)) {
                    property = item;
                    break;
                }
            }
            return property;
        }
	}
	
	public VerzuimProperties(){
		loadProperties();
	}
    public void loadProperties() {
    	userdir = System.getProperty("user.home");
    	pathseparator = System.getProperty("file.separator");
    	propertiesdir = userdir + pathseparator + "verzuimbeheer";
    	props = new Properties();
    	propertiesfilename = propertiesdir + pathseparator + "verzuimproperties.ini";

    	FileInputStream fis;
		try {
			if (new File(propertiesdir).mkdir())
				;
			fis = new FileInputStream(new File(propertiesfilename ));
	    	props.load(fis);
		} catch (FileNotFoundException e1) {
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
    public void saveProperties() {
    	FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(new File(propertiesfilename));
        	props.store(fos, "Commentaar");
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fos != null) {
                try {
                    fos.flush();
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    public Object getProperty(__verzuimproperty property){
    	return props.getProperty(property.toString());
    }
    public void setProperty(__verzuimproperty property, Object value){
    	props.put(property.toString(), value);
    }
    public void deletePoperty(String name){
    	props.remove(name);
    }
    public String getPropertyFilename() {
    	return propertiesfilename;
    }
}