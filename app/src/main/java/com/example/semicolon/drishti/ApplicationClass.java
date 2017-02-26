package com.example.semicolon.drishti;

import android.app.Application;

import com.google.firebase.FirebaseApp;
import com.orm.SugarApp;
import com.orm.SugarContext;

import java.util.Random;

/**
 * Created by semicolon on 2/25/2017.
 */

public class ApplicationClass extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        FirebaseApp.initializeApp(this);

        SugarContext.init(this);
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        SugarContext.terminate();
    }

    //SESSION ID
    Random rand = new Random();
    int SESSION_ID =  rand.nextInt(297322) + 1;

    public int getSESSION_ID() {
        return SESSION_ID;
    }
}
