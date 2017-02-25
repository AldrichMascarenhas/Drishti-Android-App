package com.example.semicolon.drishti;

import android.content.Intent;
import android.content.res.Configuration;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;

import static android.R.attr.value;

public class Dashboard extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        // Checks the orientation of the screen
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            Intent myIntent = new Intent(Dashboard.this, Session.class);
            myIntent.putExtra("key", value); //Optional parameters
            Dashboard.this.startActivity(myIntent);
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {

        }
    }
}
