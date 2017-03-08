package com.example.semicolon.drishti;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.util.Log;

import com.example.semicolon.drishti.Adapter.SessionAdapter;
import com.example.semicolon.drishti.Model.SessionData;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

/**
 * Created by semicolon on 2/25/2017.
 */

public class FirebaseInstanceIDService extends FirebaseInstanceIdService {


    private static final String TAG = "MyFirebaseIIDService";
    String refreshedToken;


    SharedPreferences sharedPreferences;
    boolean firstTime;


    @Override
    public void onTokenRefresh() {



        //Getting registration token
        refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.d(TAG,refreshedToken.toString());
        sharedPreferences = getSharedPreferences("FIREBASE_ID", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("FIRE_ID", refreshedToken);
        editor.apply();



    }


    private void sendRegistrationToServer(String token) {
        //You can implement this method to store the token on your server
        //Not required for current project
    }

}
