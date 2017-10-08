package com.mrcruwys.cpm;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

// ================================================================================================
// :::::: D B E N T R Y - This class represents a single password entry in the database
// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
public class DBEntry implements Parcelable {

    // :::::: M E M B E R S ::::::
    private String entryName;
    private ArrayList<DBPair> fieldPairs;

    // :::::: C O N S T R U C T O R - Just needs a name for the entry, pairs are added later
    DBEntry(String eName){
        entryName = eName;
        fieldPairs = new ArrayList<>();
    }

    public int size() { return fieldPairs.size(); }

    // :::::: G E T T E R S     A N D     S E T T E R S ::::::
    public String getName (){
        return entryName;
    }

    public ArrayList<DBPair> getPairs () { return fieldPairs; }

    public String getPairKey(int pairVal) { return fieldPairs.get(pairVal).getKey(); }

    public String getPairValue(int pairVal) { return fieldPairs.get(pairVal).getValue(); }

    public void addPair(String pKey, String pValue){
        DBPair tempPair = new DBPair(pKey, pValue);
        fieldPairs.add(tempPair);
    }

    // ALL CODE BELOW HERE IS NECESSARY IN ORDER TO IMPLEMENT PARCELABLE
    protected DBEntry(Parcel in) {
        entryName = in.readString();
        if (in.readByte() == 0x01) {
            fieldPairs = new ArrayList<DBPair>();
            in.readList(fieldPairs, DBPair.class.getClassLoader());
        } else {
            fieldPairs = null;
        }
    }
    @Override
    public int describeContents() {
        return 0;
    }
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(entryName);
        if (fieldPairs == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeList(fieldPairs);
        }
    }
    @SuppressWarnings("unused")
    public static final Parcelable.Creator<DBEntry> CREATOR = new Parcelable.Creator<DBEntry>() {
        @Override
        public DBEntry createFromParcel(Parcel in) {
            return new DBEntry(in);
        }

        @Override
        public DBEntry[] newArray(int size) {
            return new DBEntry[size];
        }
    };
}