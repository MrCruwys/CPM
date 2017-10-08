package com.mrcruwys.cpm;

import android.os.Parcel;
import android.os.Parcelable;

// ================================================================================================
// :::::: D B P A I R - This class represents the name and value of one row inside a database entry
// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
public class DBPair implements Parcelable {

    // :::::: M E M B E R S ::::::
    private String key;
    private String value;

    // :::::: C O N S T R U C T O R - Needs to have the values passed in to create a pair
    DBPair(String passedKey, String passedValue){
        key = passedKey;
        value = passedValue;
    }

    // :::::: G E T T E R S     A N D     S E T T E R S ::::::
    public String getKey(){ return key; }

    public String getValue(){ return value; }

    // ALL CODE BELOW HERE IS NECESSARY IN ORDER TO IMPLEMENT PARCELABLE
    protected DBPair(Parcel in) {
        key = in.readString();
        value = in.readString();
    }
    @Override
    public int describeContents() {
        return 0;
    }
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(key);
        dest.writeString(value);
    }
    @SuppressWarnings("unused")
    public static final Parcelable.Creator<DBPair> CREATOR = new Parcelable.Creator<DBPair>() {
        @Override
        public DBPair createFromParcel(Parcel in) {
            return new DBPair(in);
        }
        @Override
        public DBPair[] newArray(int size) {
            return new DBPair[size];
        }
    };
}