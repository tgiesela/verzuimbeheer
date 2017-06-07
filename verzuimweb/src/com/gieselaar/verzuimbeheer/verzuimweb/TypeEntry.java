package com.gieselaar.verzuimbeheer.verzuimweb;

public class TypeEntry{
    private int value;
    private String label;
    public TypeEntry(int id, String label){
        this.value = id;
        this.label = label;
    }
    public int getValue(){
        return value;
    }
    public String getLabel(){
        return label;
    }
    /*
    public String toString(){
        return label;
    }
    */
}
