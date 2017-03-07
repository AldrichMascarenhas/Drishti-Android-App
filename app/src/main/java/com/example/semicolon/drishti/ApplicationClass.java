package com.example.semicolon.drishti;

import android.app.Application;
import android.util.Log;

import com.google.firebase.FirebaseApp;
import com.orm.SugarApp;
import com.orm.SugarContext;

import java.util.Random;

/**
 * Created by semicolon on 2/25/2017.
 */

public class ApplicationClass extends Application {


    int SESSION_ID;
    @Override
    public void onCreate() {
        super.onCreate();

        FirebaseApp.initializeApp(this);

        SugarContext.init(this);

        Random rand = new Random();
        this.SESSION_ID =  rand.nextInt(297322) + 1;
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        SugarContext.terminate();
    }


    public void setSESSION_ID() {
        Log.d("ApplicationClass", "SESSION_ID : " + SESSION_ID);
        Random rand = new Random();
         this.SESSION_ID =  rand.nextInt(297322) + 1;
    }



    public int getSESSION_ID() {
        return SESSION_ID;
    }
}
