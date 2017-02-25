package com.example.semicolon.drishti.Model;

import com.orm.SugarRecord;

/**
 * Created by Sarvesh on 23-02-2017.
 */

public class Sessions extends SugarRecord {
    String date;
    String name;
    int randomID;

    String location;


    public Sessions()
    {

    }

    public Sessions(String date, String name, int randomID, String location) {
        this.date = date;
        this.name = name;
        this.randomID = randomID;
        this.location = location;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getRandomID() {
        return randomID;
    }

    public void setRandomID(int randomID) {
        this.randomID = randomID;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }
}
