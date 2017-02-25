package com.example.semicolon.drishti;

import android.os.AsyncTask;
import android.util.Log;

import com.example.semicolon.drishti.bus.MessageEvent;

import org.greenrobot.eventbus.EventBus;

import java.io.IOException;

import okhttp3.Headers;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by semicolon on 2/26/2017.
 */

public class SummaryAsyncTask extends AsyncTask<Void, Void, String> {

    int SESSION_ID;
    OkHttpClient client = new OkHttpClient();
    Request request;
    Response response;

    public SummaryAsyncTaskResponse delegate = null;

    public SummaryAsyncTask(int SESSION_ID) {
        this.SESSION_ID = SESSION_ID;
    }

    @Override
    protected String doInBackground(Void... voids) {

        String RESPONSE_DATA = "";

        request = new Request.Builder()
                .url(CONFIG.ACTUAL_HOST+ "summary/" + SESSION_ID)
                .build();

        try {
            response = client.newCall(request).execute();
            if (!response.isSuccessful()) {
                throw new IOException("Unexpected code " + response);
            }
            RESPONSE_DATA = response.body().string();
            Log.d("Summaryasyncresponse", RESPONSE_DATA);

        } catch (IOException e) {
            e.printStackTrace();
        }



        return RESPONSE_DATA;
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);

        delegate.processFinish(s);


    }

}
