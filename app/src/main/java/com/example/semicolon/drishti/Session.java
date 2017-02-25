package com.example.semicolon.drishti;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.semicolon.drishti.Adapter.SessionAdapter;
import com.example.semicolon.drishti.Model.SessionData;

import com.google.firebase.FirebaseApp;
import com.google.firebase.messaging.FirebaseMessaging;

import com.example.semicolon.drishti.bus.MessageEvent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import jp.wasabeef.recyclerview.animators.SlideInLeftAnimator;

/**
 * Created by semicolon on 2/25/2017.
 */

public class Session extends AppCompatActivity implements TextToSpeech.OnInitListener {

    public static final String TAG = "SessionActivity";
    private Camera mCamera;
    private CameraPreview mPreview;
    FrameLayout preview;
    private GestureDetectorCompat mDetector;
    ImageView camera_image_preview;
    ImageView mic;

    //boolean to check if it is safe to click an image
    private boolean safeToTakePicture = true;

    private Toolbar toolbar;
    private RecyclerView recyclerView;
    long initialCount;
    private TextToSpeech tts;
    private SessionAdapter sessionAdapter;
    private List<SessionData> sessionDataList = new ArrayList<SessionData>();
    private final int REQ_CODE_SPEECH_INPUT = 100;

    //Orientation
    int orientation;
    private String Utteranceid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_session);

        FirebaseApp.initializeApp(this);
        mDetector = new GestureDetectorCompat(this, new MyGestureListener());
        tts = new TextToSpeech(this, this);


        orientation = getResources().getConfiguration().orientation;

        if (orientation == 1) {
            //Handle Portrait views here
            FirebaseApp.initializeApp(this);
            FirebaseMessaging.getInstance().subscribeToTopic("sceneData");

            toolbar = (Toolbar) findViewById(R.id.toolbar); // Attaching the layout to the toolbar object
            setSupportActionBar(toolbar);

            mic = (ImageView) findViewById(R.id.mic);
            recyclerView = (RecyclerView) findViewById(R.id.image_rv);

            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
            recyclerView.setLayoutManager(linearLayoutManager);
            recyclerView.setItemAnimator(new SlideInLeftAnimator());

            recyclerView.addItemDecoration(new DividerItemDecoration(getApplicationContext(), DividerItemDecoration.VERTICAL));

            initialCount = SessionData.count(SessionData.class);


            mic.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Utteranceid = "speech";
                    tts.speak("Do You Want to Start A Session", TextToSpeech.QUEUE_FLUSH, null, Utteranceid);
                }
            });


            //Sugar orm

            if (initialCount >= 0) {

                sessionDataList = SessionData.findWithQuery(SessionData.class, "SELECT * FROM SESSION_DATA ORDER BY milliseconds DESC", null);

                sessionAdapter = new SessionAdapter(Session.this, sessionDataList);
                recyclerView.setAdapter(sessionAdapter);

                if (sessionDataList.isEmpty())
                    Snackbar.make(recyclerView, "No notes added.", Snackbar.LENGTH_LONG).show();

            }


            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    //Do something after 20 seconds

                    //do something
                    long newcount = SessionData.count(SessionData.class);
                    if (newcount > initialCount) {

                        sessionDataList = SessionData.findWithQuery(SessionData.class, "SELECT * FROM SESSION_DATA ORDER BY milliseconds DESC", null);

                        sessionAdapter = new SessionAdapter(Session.this, sessionDataList);
                        recyclerView.setAdapter(sessionAdapter);
                        initialCount = newcount;

                    }

                    handler.postDelayed(this, 500);
                }
            }, 500);  //the time is in miliseconds


        } else if (orientation == 2) {
            //Handle Landscape views here

            mCamera = getCameraInstance();
            mPreview = new CameraPreview(this, mCamera);
            preview = (FrameLayout) findViewById(R.id.camera_preview);
            preview.addView(mPreview);
            camera_image_preview = (ImageView) findViewById(R.id.camera_image_preview);
            //Set camera to continually auto-focus
            Camera.Parameters params = mCamera.getParameters();
            params.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
            mCamera.setParameters(params);


            preview.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    mDetector.onTouchEvent(motionEvent);
                    return true;
                }
            });


        } else {
            //Fallback to Portrait?


        }


    }

    //Get a camera instance here
    public static Camera getCameraInstance() {
        Camera c = null;
        try {
            c = Camera.open(); // attempt to get a Camera instance
        } catch (Exception e) {
            // Camera is not available (in use or does not exist)
        }
        return c; // returns null if camera is unavailable
    }

    @Override
    public void onInit(int status) {
        if (status == TextToSpeech.SUCCESS) {

            int result = tts.setLanguage(new Locale("en", "IN"));

            tts.setOnUtteranceProgressListener(new UtteranceProgressListener() {
                @Override
                public void onStart(String utteranceId) {

                }

                @Override
                public void onDone(String utteranceId) {
                    if (utteranceId.equals("speech")) ;
                    {
                        promptSpeechInput();
                    }
                }

                @Override
                public void onError(String utteranceId) {

                }
            });

            if (result == TextToSpeech.LANG_MISSING_DATA
                    || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Log.e("TTS", "This Language is not supported");
            } else {


            }

        } else {
            Log.e("TTS", "Initilization Failed!");
        }

    }

    class MyGestureListener extends GestureDetector.SimpleOnGestureListener {
        private static final String DEBUG_TAG = "Gestures";

        @Override
        public boolean onDoubleTap(MotionEvent event) {
            if (safeToTakePicture) {
                safeToTakePicture = false;
                Log.d(DEBUG_TAG, "onDoubleTap: " + event.toString());
                mCamera.takePicture(shutterCallback, rawCallback, jpegCallback);

            } else {
                Toast.makeText(getApplicationContext(), "Please try again!", Toast.LENGTH_LONG).show();
            }

            return true;

        }

    }

    Camera.ShutterCallback shutterCallback = new Camera.ShutterCallback() {
        public void onShutter() {
        }
    };

    Camera.PictureCallback rawCallback = new Camera.PictureCallback() {
        public void onPictureTaken(byte[] data, Camera camera) {
        }
    };

    Camera.PictureCallback jpegCallback = new Camera.PictureCallback() {
        public void onPictureTaken(byte[] data, Camera camera) {
            try {
                new SaveImageTask().execute(data);
                resetCam();
                Log.d(TAG, "onPictureTaken - jpeg");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    private void resetCam() {
        mCamera.startPreview();
    }


    private class SaveImageTask extends AsyncTask<byte[], Void, Void> {

        String fileName;
        File outFile;
        FileOutputStream outStream = null;
        Bitmap myBitmap;


        public SaveImageTask() {

        }


        @Override
        protected Void doInBackground(byte[]... data) {

            // Write to SD Card
            try {
                File sdCard = Environment.getExternalStorageDirectory();
                File dir = new File(sdCard.getAbsolutePath() + "/semicolons");
                dir.mkdirs();

                fileName = String.format("%d.jpg", System.currentTimeMillis());
                outFile = new File(dir, fileName);

                outStream = new FileOutputStream(outFile);
                outStream.write(data[0]);
                outStream.flush();
                outStream.close();

                Log.d(TAG, "onPictureTaken - wrote bytes: " + data.length + " to " + outFile.getAbsolutePath());
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            safeToTakePicture = true;

            myBitmap = BitmapFactory.decodeFile(outFile.getAbsolutePath());
            camera_image_preview.setVisibility(View.VISIBLE);
            camera_image_preview.setImageBitmap(myBitmap);
            // Execute some code after 5 seconds have passed
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {

                    camera_image_preview.setVisibility(View.INVISIBLE);

                }
            }, 1000);

            //TODO: Change to File if needed
            ImageUploadAsyncTask imageUploadAsyncTask = new ImageUploadAsyncTask();
            imageUploadAsyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, outFile.getAbsoluteFile());
        }


    }

    @Override
    public void onBackPressed() {
        if (orientation == 1) {
            //Can go back
            finish();
        } else if (orientation == 2) {
            //Cant go back do nothing
        }
    }


    private void promptSpeechInput() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT,
                "Say Something");
        intent.putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_POSSIBLY_COMPLETE_SILENCE_LENGTH_MILLIS, 1);
        try {
            startActivityForResult(intent, REQ_CODE_SPEECH_INPUT);
        } catch (ActivityNotFoundException a) {
            Toast.makeText(getApplicationContext(), "Not Supported",
                    Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case REQ_CODE_SPEECH_INPUT: {
                if (resultCode == RESULT_OK && null != data) {

                    ArrayList<String> result = data
                            .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    if (result.get(0).contains("yes")) {
                        startActivity(new Intent(Session.this, OngoingSession.class));
                    } else {

                    }

                }
                break;
            }

        }
    }


    @Override
    protected void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);

    }

    @Override
    protected void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(MessageEvent event) {

//
//        SessionData sessionData = SessionData.findById(SessionData.class, event.getDbID());
//        Log.d(TAG, "Query resut : " + sessionData.getResult());
//
//        sessionDataList.add(0, sessionData);
////        sessionAdapter.addItemsToList(sessionData);
//
//
//        long dbcount = SessionData.count(SessionData.class);
//        Log.d(TAG, "onMessageEvent Receive. DB count : " + dbcount);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(tts!=null) {
            tts.stop();
            tts.shutdown();
        }
    }


}
