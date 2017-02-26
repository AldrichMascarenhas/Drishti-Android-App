package com.example.semicolon.drishti;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.semicolon.drishti.Adapter.OnGoingSessionAdapter;
import com.example.semicolon.drishti.Model.OnGoingSessionData;
import com.example.semicolon.drishti.Model.SessionData;
import com.example.semicolon.drishti.Model.Sessions;

import java.util.ArrayList;
import java.util.List;

import jp.wasabeef.recyclerview.animators.SlideInLeftAnimator;

/**
 * Created by semicolon on 2/25/2017.
 */

public class SessionDetail extends AppCompatActivity {

    int SESSION_ID;
    private RecyclerView recyclerView;
    private OnGoingSessionAdapter onGoingSessionAdapter;
    private List<OnGoingSessionData> onGoingSessionDataList = new ArrayList<OnGoingSessionData>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_session_detail);

        Toolbar toolbar = (Toolbar)findViewById(R.id.session_detail_toolbar) ;
        TextView textView = (TextView)findViewById(R.id.summarytextview_SESSION);

        SESSION_ID = getIntent().getIntExtra("SESSION_ID_KEY", 0);


        List<Sessions> session = Sessions.findWithQuery(Sessions.class, "Select * from SESSIONS where random_ID = ?", Integer.toString(SESSION_ID));
        if(!session.isEmpty()){
            Toast.makeText(getApplicationContext(), session.get(0).getSummary(),Toast.LENGTH_LONG);
            textView.setText(session.get(0).getSummary());
        }
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Timeline Detail");




        recyclerView = (RecyclerView) findViewById(R.id.session_detail_list);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setItemAnimator(new SlideInLeftAnimator());
        recyclerView.addItemDecoration(new DividerItemDecoration(getApplicationContext(), DividerItemDecoration.VERTICAL));

        onGoingSessionDataList = SessionData.findWithQuery(OnGoingSessionData.class, "SELECT * FROM ON_GOING_SESSION_DATA WHERE SID = ? ORDER BY milliseconds DESC", Integer.toString(SESSION_ID));

        onGoingSessionAdapter = new OnGoingSessionAdapter(SessionDetail.this, onGoingSessionDataList);


        recyclerView.setAdapter(onGoingSessionAdapter);

    }
}
