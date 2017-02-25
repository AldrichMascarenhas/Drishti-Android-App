package com.example.semicolon.drishti;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

/**
 * Created by semicolon on 2/25/2017.
 */

public class OngoingSession extends AppCompatActivity {
Handler handler;
    Toolbar toggletoolbar;
    int count = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ongoingsession);
        toggletoolbar =(Toolbar)findViewById(R.id.toggletoolbar);

        handler = new Handler();

        final Runnable r = new Runnable() {
            public void run() {

                handler.postDelayed(this, 1000);
                if(count % 2 == 0)
                {
                    toggletoolbar.setBackgroundColor(Color.BLACK);
                }
                else {
                    toggletoolbar.setBackgroundColor(Color.WHITE);
                }

                count ++;
            }
        };

        handler.postDelayed(r, 1000);
    }




}
