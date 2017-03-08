package com.example.semicolon.drishti;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Build;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.semicolon.drishti.Model.LoadDataIntoDatabaseAsyncTask;
import com.example.semicolon.drishti.Model.OnGoingSessionData;
import com.example.semicolon.drishti.Model.SessionData;
import com.example.semicolon.drishti.Model.Sessions;

import java.util.List;
import java.util.Locale;

import static android.R.attr.value;

public class Dashboard extends AppCompatActivity implements TextToSpeech.OnInitListener {

    public static final String TAG = "Dashboard";
    private TextToSpeech tts;
    private RelativeLayout header;

    private RecyclerView TimelineRecylerView;
    private TimeLineAdapter mTimeLineAdapter;

    long initialCount;

    List<Sessions> books;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Timeline");
        tts = new TextToSpeech(this, this);

        header = (RelativeLayout) findViewById(R.id.header);
        header.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    String utterenceid = "head";
                    tts.speak("Rotate Phone to Start Capture ", TextToSpeech.QUEUE_FLUSH, null, utterenceid);
                } else {
                    tts.speak("Rotate Phone to Start Capture ", TextToSpeech.QUEUE_FLUSH, null);
                }
            }
        });


        TimelineRecylerView = (RecyclerView) findViewById(R.id.recyclerViewtimeline);


        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        TimelineRecylerView.setLayoutManager(linearLayoutManager);
        TimelineRecylerView.setHasFixedSize(true);


        initialCount = Sessions.count(Sessions.class);


        books = Sessions.findWithQuery(Sessions.class, "SELECT * FROM SESSIONS ORDER BY ID DESC");

        mTimeLineAdapter = new TimeLineAdapter(books, this);
        TimelineRecylerView.setAdapter(mTimeLineAdapter);

        System.out.println("Hello" + books.size());


        TimelineRecylerView.addOnItemTouchListener(new SessionRecylerItemClickListener(getApplicationContext(), new SessionRecylerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                int sessionID = books.get(position).getRandomID();

                Intent i = new Intent(Dashboard.this, SessionDetail.class);
                i.putExtra("SESSION_ID_KEY", sessionID);
                startActivity(i);
            }
        }));

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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (tts != null) {
            tts.stop();
            tts.shutdown();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
}
