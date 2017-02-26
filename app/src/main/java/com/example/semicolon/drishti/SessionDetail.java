package com.example.semicolon.drishti;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

/**
 * Created by semicolon on 2/25/2017.
 */

public class SessionDetail extends AppCompatActivity {

    int SESSION_ID;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_session_detail);

        SESSION_ID = getIntent().getIntExtra("SESSION_ID_KEY", 0);
        Toast.makeText(getApplicationContext(), "Passed session is : " + SESSION_ID,Toast.LENGTH_LONG).show();

    }
}
