package com.example.semicolon.drishti;

import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
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
import android.widget.TextView;
import android.widget.Toast;

import com.example.semicolon.drishti.Adapter.SessionAdapter;
import com.example.semicolon.drishti.Model.SessionData;

import com.google.firebase.FirebaseApp;
import com.google.firebase.messaging.FirebaseMessaging;

import com.example.semicolon.drishti.bus.MessageEvent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONException;
import org.json.JSONObject;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Random;

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
    int imagecount;
    int count;

    //boolean to check if it is safe to click an image
    private boolean safeToTakePicture = true;

    private Toolbar toolbar;
    private RecyclerView recyclerView;
    long initialCount;
    private TextToSpeech tts;
    private SessionAdapter sessionAdapter;
    private List<SessionData> sessionDataList = new ArrayList<SessionData>();
    private List<SessionData> sessionDataListleveltwo = new ArrayList<SessionData>();
    private final int REQ_CODE_SPEECH_INPUT = 100;
    private SharedPreferences sharedpreferences;

    //Orientation
    int orientation;
    private String Utteranceid;
    private boolean changeofdata;
    public static final String MyPREFERENCES = "Drishti";
    public int clickcount;

    ApplicationClass applicationClass;

    private CardView facescard;
    private ImageView facesImageView;
    private TextView facetext;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        changeofdata = false;
        setContentView(R.layout.activity_session);
        registerReceiver(myReceiver, new IntentFilter(FireBaseMessagingService.INTENT_FILTER));

        mDetector = new GestureDetectorCompat(this, new MyGestureListener());
        sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);

        tts = new TextToSpeech(this, this);
        applicationClass = (ApplicationClass) getApplicationContext();
        count = 0;


        Log.d("SESSION_ID_KEY", "SESSION_ID IS  : " + applicationClass.getSESSION_ID() + " IN Session");


        orientation = getResources().getConfiguration().orientation;

        if (orientation == 1) {
            //Handle Portrait views here
            clickcount = 0;

            facescard = (CardView) findViewById(R.id.faces_card);
            facetext = (TextView) findViewById(R.id.face_textview);

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
                    promptSpeechInput();
                }
            });


            //Sugar orm

            if (initialCount >= 0) {

                sessionDataList = SessionData.findWithQuery(SessionData.class, "SELECT * FROM SESSION_DATA WHERE SESSIONID = ? ORDER BY milliseconds DESC", Integer.toString(applicationClass.getSESSION_ID()));
                sessionAdapter = new SessionAdapter(Session.this, sessionDataList);
                recyclerView.setAdapter(sessionAdapter);

            }


            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    //Do something after 20 seconds

                    //do something
                    long newcount = SessionData.count(SessionData.class);
                    if (newcount > initialCount) {

                        sessionDataList = SessionData.findWithQuery(SessionData.class, "SELECT * FROM SESSION_DATA WHERE SESSIONID = ? ORDER BY milliseconds DESC", Integer.toString(applicationClass.getSESSION_ID()));
                        Log.d("abc", "abc");
                        sessionAdapter = new SessionAdapter(Session.this, sessionDataList);
                        recyclerView.setAdapter(sessionAdapter);
                        initialCount = newcount;

                    }
                    if (changeofdata) {
                        update();
                    }


                   /* if (changeofdata == true && count == 1) {
                        count++;

                        Log.d("xyz","xyz");
                        tts.speak("We have More Details Updating Info ", TextToSpeech.QUEUE_ADD, null, "hello");
                        SharedPreferences.Editor editor = sharedpreferences.edit();
                        editor.putInt("imagecount", 0);
                        editor.commit();

                    }*/


                    handler.postDelayed(this, 1);
                }
            }, 1);  //the time is in miliseconds


            recyclerView.addOnItemTouchListener(
                    new SessionRecylerItemClickListener(getApplicationContext(), new SessionRecylerItemClickListener.OnItemClickListener() {
                        @Override
                        public void onItemClick(View view, int position) {
                            TextView name = (TextView) view.findViewById(R.id.info_textview);
                            clickcount++;
                            Utteranceid = this.hashCode() + "";
                            if (position == 0) {

                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                    tts.speak("To your Right  " + name.getText().toString(), TextToSpeech.QUEUE_ADD, null, Utteranceid);

                                } else {
                                    tts.speak("To your Right  " + name.getText().toString(), TextToSpeech.QUEUE_ADD, null);
                                }

                            } else if (position == 1) {

                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                    tts.speak("In front of you " + name.getText().toString(), TextToSpeech.QUEUE_ADD, null, Utteranceid);

                                } else {
                                    tts.speak("In front of you  " + name.getText().toString(), TextToSpeech.QUEUE_ADD, null);
                                }


                            } else if (position == 2) {

                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                    tts.speak("To your Left " + name.getText().toString(), TextToSpeech.QUEUE_ADD, null, Utteranceid);

                                } else {
                                    tts.speak("To your Left  " + name.getText().toString(), TextToSpeech.QUEUE_ADD, null);
                                }


                            } else {

                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                    tts.speak(name.getText().toString(), TextToSpeech.QUEUE_ADD, null, Utteranceid);

                                } else {
                                    tts.speak(name.getText().toString(), TextToSpeech.QUEUE_ADD, null);
                                }


                            }


                        }
                    })
            );


        } else if (orientation == 2) {
            //Handle Landscape views here
            SharedPreferences.Editor editor = sharedpreferences.edit();
            editor.putInt("imagecount", 0);
            editor.commit();
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

    private void update() {
        long newcount = SessionData.count(SessionData.class);
        sessionDataList = SessionData.findWithQuery(SessionData.class, "SELECT * FROM SESSION_DATA WHERE SESSIONID = ? ORDER BY milliseconds DESC", Integer.toString(applicationClass.getSESSION_ID()));
        changeofdata = false;
        sessionAdapter = new SessionAdapter(Session.this, sessionDataList);
        recyclerView.setAdapter(sessionAdapter);
        initialCount = newcount;
    }

    private BroadcastReceiver myReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(final Context context, Intent intent) {
            String utteranceId = this.hashCode() + "";
            String text = "There is new Data For You";
            Log.d("Hello", "Hello");
            final String a = intent.getStringExtra("message");
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Log.d("Hello", "Hello");

                    String jsonData = a;
                    String result = " ";
                    String image_id = " ";
                    String tag = " ";
                    count++;

                    if (count == 1) {


                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            tts.speak("We have New Data for You", TextToSpeech.QUEUE_ADD, null, "HELLO");

                        } else {
                            tts.speak("We have New Data for You", TextToSpeech.QUEUE_ADD, null);
                        }


                    }


                    try {
                        JSONObject Jobject = new JSONObject(jsonData);
                        image_id = Jobject.getString("image_id");
                        result = Jobject.getString("result");
                        tag = Jobject.getString("tag");

                        if (tag.equals("faces")) {
                            // Toast.makeText(Session.this, Jobject.getString("result"), Toast.LENGTH_LONG).show();



                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                tts.speak(Jobject.getString("result"), TextToSpeech.QUEUE_ADD, null, "abc");

                            } else {
                                tts.speak(Jobject.getString("result"), TextToSpeech.QUEUE_ADD, null);
                            }





                            facescard.setVisibility(View.VISIBLE);
                            facetext.setText(Jobject.getString("result"));

                            facescard.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {

                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                        tts.speak(facetext.getText(), TextToSpeech.QUEUE_ADD, null, "abc");

                                    } else {
                                        tts.speak(facetext.getText().toString(), TextToSpeech.QUEUE_ADD, null);
                                    }




                                }
                            });
                        } else {
                            sessionDataList = SessionData.findWithQuery(SessionData.class, "SELECT * FROM SESSION_DATA WHERE imageid=?", image_id);
                            if (sessionDataList.size() != 0) {
                                Log.d("hello", String.valueOf(imagecount));
                                SessionData book = SessionData.findById(SessionData.class, sessionDataList.get(0).getId());
                                book.setResult(result); // modify the values
                                book.save();
                                Log.d("give", String.valueOf(sharedpreferences.getInt("imagecount", 0)));

                                changeofdata = true;


                            }
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }


                }
            });

        }
    };

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
                    Log.d("utteranceid", utteranceId);
                    if (utteranceId.equals("hello")) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                update();
                            }
                        });

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
                int c = sharedpreferences.getInt("imagecount", 0);
                c++;
                SharedPreferences.Editor editor = sharedpreferences.edit();
                editor.putInt("imagecount", c);
                editor.commit();
                imagecount++;

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
            ImageUploadAsyncTask imageUploadAsyncTask = new ImageUploadAsyncTask(applicationClass.getSESSION_ID(), outFile.getAbsoluteFile(), getApplicationContext());
            imageUploadAsyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
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
                    if (!result.isEmpty()) {

                        Intent intent = new Intent(Session.this, OngoingSession.class);
                        intent.putExtra("SESSION_ID_KEY", applicationClass.getSESSION_ID());
                        startActivity(intent);

                    } else {

                    }

                }
                break;
            }

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


    //TTS TO READ CARD ITEM


}