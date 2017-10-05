package com.mrcruwys.cpm;

public class DBPair {
    private String fieldName;
    private String fieldValue;

    DBPair(String fName, String fValue){
        fieldName = fName;
        fieldValue = fValue;
    }

    public String getName(){ return fieldName; }

    public String getValue(){ return fieldValue; }
}