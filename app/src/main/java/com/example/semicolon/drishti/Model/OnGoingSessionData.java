package com.example.semicolon.drishti.Model;

import com.orm.SugarRecord;

/**
 * Created by semicolon on 2/25/2017.
 */

public class OnGoingSessionData extends SugarRecord {

    int s_id;
    String image_id; //File name
    String result;

    String image_location; //location on device

    long milliseconds;

    public OnGoingSessionData() {
    }

    public OnGoingSessionData(int s_id, String image_id, String result, String image_location, long milliseconds) {
        this.s_id = s_id;
        this.image_id = image_id;
        this.result = result;
        this.image_location = image_location;
        this.milliseconds = milliseconds;
    }


    public int getS_id() {
        return s_id;
    }

    public void setS_id(int s_id) {
        this.s_id = s_id;
    }

    public String getImage_id() {
        return image_id;
    }

    public void setImage_id(String image_id) {
        this.image_id = image_id;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getImage_location() {
        return image_location;
    }

    public void setImage_location(String image_location) {
        this.image_location = image_location;
    }

    public long getMilliseconds() {
        return milliseconds;
    }

    public void setMilliseconds(long milliseconds) {
        this.milliseconds = milliseconds;
    }
}
