package com.mrcruwys.cpm;

import java.util.ArrayList;

public class DBEntry {
    private String entryName;
    private ArrayList<DBPair> fieldPairs;

    DBEntry(String eName){
        entryName = eName;
        fieldPairs = new ArrayList<DBPair>();
    }

    public void addPair(String pName, String pValue){
        DBPair tempPair = new DBPair(pName, pValue);
        fieldPairs.add(tempPair);
    }
    public String getName (){
        return entryName;
    }
}