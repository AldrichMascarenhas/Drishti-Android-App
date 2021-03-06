package com.example.semicolon.drishti;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;

import com.example.semicolon.drishti.Model.Sessions;
import com.example.semicolon.drishti.bus.MessageEvent;

import org.greenrobot.eventbus.EventBus;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

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


    SharedPreferences sharedPreferencesNW;
    String HOST_IP_SP;
    Context context;

    public SummaryAsyncTask(int SESSION_ID, Context context) {
        this.SESSION_ID = SESSION_ID;
        this.context = context.getApplicationContext();

    }

    @Override
    protected void onPreExecute() {
        sharedPreferencesNW = context.getSharedPreferences("NETWORK_SHAREDPREF", Context.MODE_PRIVATE);
        HOST_IP_SP = sharedPreferencesNW.getString("HOST_IP_SP", "104.196.153.37");
        Log.d("SummaryAsyncTask", HOST_IP_SP);
    }

    @Override
    protected String doInBackground(Void... voids) {

        String RESPONSE_DATA = "";

        request = new Request.Builder()
                .url("http://" + HOST_IP_SP + ":80/" + "summary/" + SESSION_ID)
                .build();

        try {
            response = client.newCall(request).execute();
            if (!response.isSuccessful()) {
                throw new IOException("Unexpected code " + response);
            }
            RESPONSE_DATA = response.body().string();
            Log.d("Summaryasyncresponse", RESPONSE_DATA);


            DateFormat df = new SimpleDateFormat("EEE, d MMM yyyy, HH:mm");
            String date = df.format(Calendar.getInstance().getTime());

            Sessions session5 = new Sessions(date, "Meeting", SESSION_ID, "Persistent Systems Ltd, Verna", RESPONSE_DATA);
            session5.save();

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
