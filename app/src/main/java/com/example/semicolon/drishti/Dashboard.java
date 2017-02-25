package com.example.semicolon.drishti;

import android.content.Intent;
import android.content.res.Configuration;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import static android.R.attr.value;

public class Dashboard extends AppCompatActivity {

    public static final String TAG = "Dashboard";

    private RecyclerView TimelineRecylerView;
    private TimeLineAdapter mTimeLineAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        TimelineRecylerView = (RecyclerView) findViewById(R.id.recyclerViewtimeline);

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

}
