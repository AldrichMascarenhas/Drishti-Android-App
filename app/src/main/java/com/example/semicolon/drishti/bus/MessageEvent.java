package com.example.semicolon.drishti.bus;

/**
 * Created by semicolon on 2/25/2017.
 */

public class MessageEvent {

    long dbID;

    public MessageEvent(long dbID) {
        this.dbID = dbID;
    }

    public long getDbID() {
        return dbID;
    }

    public void setDbID(long dbID) {
        this.dbID = dbID;
    }
}
