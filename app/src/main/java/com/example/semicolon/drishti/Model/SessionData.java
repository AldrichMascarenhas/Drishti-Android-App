package com.example.semicolon.drishti.Model;

import com.orm.SugarRecord;

/**
 * Created by semicolon on 2/25/2017.
 */

public class SessionData extends SugarRecord {

    String S_id;
    String info;
    String tag;
    String level;


    public SessionData() {
    }

    public SessionData(String s_id, String info, String tag, String level) {
        S_id = s_id;
        this.info = info;
        this.tag = tag;
        this.level = level;
    }

    public String getS_id() {
        return S_id;
    }

    public void setS_id(String s_id) {
        S_id = s_id;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }
}
