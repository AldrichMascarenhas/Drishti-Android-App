package com.example.semicolon.drishti;

import android.content.Intent;
import android.content.res.Configuration;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;

import java.util.Locale;

import static android.R.attr.value;

public class Dashboard extends AppCompatActivity implements TextToSpeech.OnInitListener {

    public static final String TAG = "Dashboard";
    private TextToSpeech tts;
    private RelativeLayout header;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        tts = new TextToSpeech(this,this);

        header = (RelativeLayout)findViewById(R.id.header);
        header.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String utterenceid ="head";
                tts.speak("Rotate Phone to Start Capture ",TextToSpeech.QUEUE_FLUSH,null,utterenceid);
            }
        });


        int orientation = getResources().getConfiguration().orientation;

        if (orientation == 1) {
            //Handle Portrait views here
            Log.d(TAG, "ORIENTATION_PORTRAIT");

        } else if (orientation == 2) {
            //Handle Landscape views here
            Log.d(TAG, "ORIENTATION_LANDSCAPE");

            Intent myIntent = new Intent(Dashboard.this, Session.class);
            myIntent.putExtra("key", value); //Optional parameters
            Dashboard.this.startActivity(myIntent);
        }

    }

    @Override
    public void onInit(int status) {
        if (status == TextToSpeech.SUCCESS) {

            int result = tts.setLanguage(new Locale("en", "IN"));

            if (result == TextToSpeech.LANG_MISSING_DATA
                    || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Log.e("TTS", "This Language is not supported");
            } else {


            }

        } else {
            Log.e("TTS", "Initilization Failed!");
        }
    }
}
