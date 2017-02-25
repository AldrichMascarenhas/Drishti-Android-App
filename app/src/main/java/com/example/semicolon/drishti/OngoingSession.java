package com.example.semicolon.drishti;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.hardware.Camera;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.speech.RecognizerIntent;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.format.Time;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.semicolon.drishti.Adapter.OnGoingSessionAdapter;
import com.example.semicolon.drishti.Adapter.SessionAdapter;
import com.example.semicolon.drishti.Model.OnGoingSessionData;
import com.example.semicolon.drishti.Model.SessionData;
import com.example.semicolon.drishti.Model.Sessions;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import jp.wasabeef.recyclerview.animators.SlideInLeftAnimator;

import static android.R.attr.orientation;

/**
 * Created by semicolon on 2/25/2017.
 */

public class OngoingSession extends AppCompatActivity {
    Handler handler;
    Toolbar toggletoolbar;
    private RecyclerView recyclerView;
    public static final String TAG = "OnGoingSessionActivity";
    private CameraPreview mPreview;
    FrameLayout preview;
    private GestureDetectorCompat mDetector;
    ImageView camera_image_preview;
    private Camera mCamera;
    private OnGoingSessionAdapter onGoingSessionAdapter;
    private List<OnGoingSessionData> onGoingSessionDataList = new ArrayList<OnGoingSessionData>();
    long initialCount;
    private boolean safeToTakePicture = true;
    int count = 0;
    private int SESSION_ID;

    ImageView mic;
    private final int REQ_CODE_SPEECH_INPUT = 100;

    int orientation;

    TextView meeting_text, date_time;

    //USE THIS FLAG TO SET THE SESSIONS OBJECT IN DB
    boolean saveInDB = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ongoingsession);
        SESSION_ID = getIntent().getIntExtra("SESSION_ID_KEY", 0);
        Log.d("SESSION_ID_KEY", "SESSION_ID IS  : " + SESSION_ID + " IN OngoingSession");

        toggletoolbar = (Toolbar) findViewById(R.id.toggletoolbar);
        mDetector = new GestureDetectorCompat(this, new MyGestureListener());

        handler = new Handler();

        final Runnable r = new Runnable() {
            public void run() {

                handler.postDelayed(this, 1000);
                if (count % 2 == 0) {
                    toggletoolbar.setBackgroundColor(Color.BLACK);
                } else {
                    toggletoolbar.setBackgroundColor(Color.WHITE);
                }

                count++;
            }
        };

        DateFormat df = new SimpleDateFormat("EEE, d MMM yyyy, HH:mm");
        String date = df.format(Calendar.getInstance().getTime());

        if(saveInDB){


            Sessions session5 = new Sessions(date, "Meeting", SESSION_ID, "Persistent Systems Ltd, Verna");
            session5.save();
            saveInDB = false;
        }


        orientation = getResources().getConfiguration().orientation;


        if (orientation == 1) {

            mic = (ImageView) findViewById(R.id.mic_ogs);


            meeting_text = (TextView) findViewById(R.id.meeting_text);
            date_time = (TextView) findViewById(R.id.date_time);
            meeting_text.setText("Meeting " + date);
            date_time.setText(date);

            recyclerView = (RecyclerView) findViewById(R.id.ongoingsession_rv);
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
            recyclerView.setLayoutManager(linearLayoutManager);
            recyclerView.setItemAnimator(new SlideInLeftAnimator());
            recyclerView.addItemDecoration(new DividerItemDecoration(getApplicationContext(), DividerItemDecoration.VERTICAL));

            initialCount = OnGoingSessionData.count(OnGoingSessionData.class);
            if (initialCount >= 0) {

                onGoingSessionDataList = SessionData.findWithQuery(OnGoingSessionData.class, "SELECT * FROM ON_GOING_SESSION_DATA WHERE SID = ? ORDER BY milliseconds DESC",Integer.toString(SESSION_ID));

                onGoingSessionAdapter = new OnGoingSessionAdapter(OngoingSession.this, onGoingSessionDataList);
                recyclerView.setAdapter(onGoingSessionAdapter);


            }

            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    //Do something after 20 seconds

                    //do something
                    long newcount = OnGoingSessionData.count(OnGoingSessionData.class);
                    if (newcount > initialCount) {

                        onGoingSessionDataList = SessionData.findWithQuery(OnGoingSessionData.class, "SELECT * FROM ON_GOING_SESSION_DATA WHERE SID = ? ORDER BY milliseconds DESC",Integer.toString(SESSION_ID));

                        onGoingSessionAdapter = new OnGoingSessionAdapter(OngoingSession.this, onGoingSessionDataList);
                        recyclerView.setAdapter(onGoingSessionAdapter);
                        initialCount = newcount;

                    }

                    handler.postDelayed(this, 1);
                }
            }, 1);

            mic.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    promptSpeechInput();
                }
            });

        } else if (orientation == 2) {
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
            ImageUploadAsyncTaskOngoing imageUploadAsyncTaskOngoing = new ImageUploadAsyncTaskOngoing(outFile.getAbsoluteFile(), SESSION_ID);
            imageUploadAsyncTaskOngoing.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        }


    }

    @Override
    public void onBackPressed() {
        if (orientation == 1) {
            //Can go back

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
                    if (result.get(0).contains("stop")) {

                        Toast.makeText(getApplicationContext(), "Stop voice", Toast.LENGTH_SHORT).show();


                        //READ CARD HERE



//                        Intent intent = new Intent(OngoingSession.this, Dashboard.class);
//                        startActivity(intent);


                    } else {

                    }

                }
                break;
            }

        }
    }


}





