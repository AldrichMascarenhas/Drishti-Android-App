package com.example.semicolon.drishti;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.Manifest;

import com.example.semicolon.drishti.Model.LoadDataIntoDatabaseAsyncTask;

import agency.tango.materialintroscreen.MaterialIntroActivity;
import agency.tango.materialintroscreen.MessageButtonBehaviour;
import agency.tango.materialintroscreen.SlideFragmentBuilder;

public class IntroActivity  extends MaterialIntroActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        LoadDataIntoDatabaseAsyncTask loadDataIntoDatabaseAsyncTask = new LoadDataIntoDatabaseAsyncTask();
        loadDataIntoDatabaseAsyncTask.execute();

        hideBackButton();

        addSlide(new SlideFragmentBuilder()
                        .backgroundColor(R.color.colorPrimary)
                        .buttonsColor(R.color.colorAccent)
                        .neededPermissions(new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE})
                        .title("Hello there")
                        .description("Before we get started we need a Few Permissions")
                        .build());


        addSlide(new SlideFragmentBuilder()
                        .backgroundColor(R.color.colorPrimary)
                        .buttonsColor(R.color.colorAccent)
                        .image(R.drawable.ic_launcher)
                        .title("Almost there")
                        .description("Building required Databases")
                        .build(),
                new MessageButtonBehaviour(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {


                        final Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                //Do something after 100ms
                                Intent i = new Intent(IntroActivity.this, Dashboard.class);
                                startActivity(i);
                                finish();
                            }
                        }, 3000);


                    }
                }, "Continue"));
    }


    @Override
    public void onFinish() {
        super.onFinish();
    }

}
