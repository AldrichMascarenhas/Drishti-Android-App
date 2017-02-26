package com.example.semicolon.drishti.bus;

/**
 * Created by semicolon on 2/25/2017.
 */

public class MessageEvent {

    String summaryData;

    public MessageEvent(String summaryData) {
        this.summaryData = summaryData;
    }

    public String getSummaryData() {
        return summaryData;
    }

    public void setSummaryData(String summaryData) {
        this.summaryData = summaryData;
    }
}
