/* ###  ### */
package com.mrcruwys.cpm;

/* # # #     I M P O R T S     # # # */
import java.util.ArrayList;

/* # # #     I M P O R T S     # # # */
public class DBEntry {

    // ### FIELDS ###
    private String entryName;
    private ArrayList<DBPair> fieldPairs;

    // ### CONSTRUCTOR ###
    DBEntry(String eName){
        entryName = eName;
        fieldPairs = new ArrayList<DBPair>();
    }

    /* # # #     M E T H O D S     # # # */
    public void addPair(String pName, String pValue){
        DBPair tempPair = new DBPair(pName, pValue);
        fieldPairs.add(tempPair);
    }
    public String getName (){
        return entryName;
    }
}