package com.example.semicolon.drishti.Model;

import com.orm.SugarRecord;

/**
 * Created by Sarvesh on 23-02-2017.
 */

public class Sessions extends SugarRecord {
    String date;
    String name;
    int randomID;

    public Sessions()
    {

    }
    public Sessions(String date, String name, int randomID) {
        this.date = date;
        this.name = name;
        this.randomID = randomID;
    }

    public int getRandomID() {
        return randomID;
    }

    public void setRandomID(int randomID) {
        this.randomID = randomID;
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
}
