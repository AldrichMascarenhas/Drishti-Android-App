package com.example.semicolon.drishti;

import android.os.AsyncTask;
import android.util.Log;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by semicolon on 2/25/2017.
 */

public class ImageUploadAsyncTask extends AsyncTask<File, String, String> {

    public static final String TAG = "ImageUploadAsyncTask";

    private final OkHttpClient client = new OkHttpClient();

    Response response;
    RequestBody requestBody;
    Request request;
    private static final MediaType MEDIA_TYPE_PNG = MediaType.parse("image/png");


    @Override
    protected String doInBackground(File... files) {

        File file = files[0];

        requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("photo", file.getName(),
                        RequestBody.create(MEDIA_TYPE_PNG, file))
                .build();

        request = new Request.Builder()
                .url("http://10.244.25.41:3000/api/v2/first-level")
                .post(requestBody)
                .build();

        try {
            response = client.newCall(request).execute();
            if (!response.isSuccessful()) {
                throw new IOException("Unexpected code " + response);
            }

            Log.d(TAG, response.toString());
            Log.d(TAG, response.body().string());


        } catch (IOException e) {
            e.printStackTrace();
        }


        return null;
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    public ImageUploadAsyncTask() {
        super();
    }
}