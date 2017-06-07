package com.gieselaar.verzuimbeheer.utils;

/* 
 * Te gebruiken in comboboxen
 */
public class TypeEntry{
    private int value;
    private String label;
    private String[] labels;
    public TypeEntry(int id, String label){
        this.value = id;
        this.label = label;
    }
    public TypeEntry(int id, String[] labels){
        this.value = id;
        this.labels = labels;
    }
    public int getValue(){
        return value;
    }
    public String toString(){
        return label;
    }
    public String[] getLabels(){
    	return labels;
    }
}
