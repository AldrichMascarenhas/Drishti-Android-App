package com.example.semicolon.drishti;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;

import com.example.semicolon.drishti.Model.SessionData;
import com.example.semicolon.drishti.bus.MessageEvent;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by semicolon on 2/25/2017.
 */

public class ImageUploadAsyncTask extends AsyncTask<Void, String, Long> {

    public static final String TAG = "ImageUploadAsyncTask";

    ApplicationClass applicationClass;
    Context context;

    private OkHttpClient client = new OkHttpClient.Builder()
            .connectTimeout(10, TimeUnit.SECONDS)
            .writeTimeout(10, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .build();



    Response response;
    RequestBody requestBody;
    Request request;
    private static final MediaType MEDIA_TYPE_JPG = MediaType.parse("image/jpg");

    Long databaseID;


    File file;
    int SESSION_ID;


    SharedPreferences sharedPreferences;
    String firstTime;



    SharedPreferences sharedPreferencesNW;
    String HOST_IP_SP;

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        sharedPreferences = context.getSharedPreferences("FIREBASE_ID", Context.MODE_PRIVATE);
        firstTime = sharedPreferences.getString("FIRE_ID", "");
        Log.d("ImageUploadAsyncTask", firstTime);


        sharedPreferencesNW = context.getSharedPreferences("NETWORK_SHAREDPREF", Context.MODE_PRIVATE);
        HOST_IP_SP = sharedPreferencesNW.getString("HOST_IP_SP", "104.196.153.37");
        Log.d("ImageUploadAsyncTask", HOST_IP_SP);



    }

    @Override
    protected Long doInBackground(Void ... params) {


        //GET SP HERE



        requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("file", file.getName(), RequestBody.create(MEDIA_TYPE_JPG, file))
                .addFormDataPart("image_id", file.getName())
                .addFormDataPart("session_id", Integer.toString(SESSION_ID))
                .addFormDataPart("firebase_id", firstTime)
                .build();

        request = new Request.Builder()
                .url("http://" + HOST_IP_SP + ":80/" + "upload")
                .post(requestBody)
                .build();

        try {
            response = client.newCall(request).execute();
            if (!response.isSuccessful()) {
                throw new IOException("Unexpected code " + response);
            }

            else{

                try {
                    String jsonData = response.body().string();
                    JSONObject Jobject = new JSONObject(jsonData);
                    Log.d(TAG, jsonData);

                    long unixTime = System.currentTimeMillis();
                    Log.d(TAG, "Linux time : " + unixTime);

                    SessionData sessionData = new SessionData(Jobject.getString("image_id"),Jobject.getString("result"), file.getAbsolutePath(), unixTime, SESSION_ID);
                    sessionData.save();

                    databaseID = sessionData.getId();
                    Log.d(TAG, "databse id : " + databaseID);


                }catch (JSONException e){
                    e.printStackTrace();
                }
            }


        } catch (IOException e) {
            e.printStackTrace();
        }


        return databaseID;
    }

    @Override
    protected void onPostExecute(Long dbID) {
        super.onPostExecute(dbID);



//        EventBus.getDefault().post(new MessageEvent(dbID));

    }


    public ImageUploadAsyncTask(int SESSION_ID, File file,Context context) {
        this.context = context.getApplicationContext();
        this.SESSION_ID = SESSION_ID;
        this.file = file;
    }
}