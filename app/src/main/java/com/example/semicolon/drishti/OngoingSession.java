package com.example.semicolon.drishti;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.hardware.Camera;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

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

    int orientation;

    TextView meeting_text, date_time;

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


        orientation = getResources().getConfiguration().orientation;


        if (orientation == 1) {

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

                onGoingSessionDataList = OnGoingSessionData.findWithQuery(OnGoingSessionData.class, "SELECT * FROM ON_GOING_SESSION_DATA ORDER BY milliseconds DESC", null);

                onGoingSessionAdapter = new OnGoingSessionAdapter(OngoingSession.this, onGoingSessionDataList);
                recyclerView.setAdapter(onGoingSessionAdapter);

                if (onGoingSessionDataList.isEmpty())
                    Snackbar.make(recyclerView, "No notes added.", Snackbar.LENGTH_LONG).show();

            }

            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    //Do something after 20 seconds

                    //do something
                    long newcount = OnGoingSessionData.count(OnGoingSessionData.class);
                    if (newcount > initialCount) {

                        onGoingSessionDataList = OnGoingSessionData.findWithQuery(OnGoingSessionData.class, "SELECT * FROM ON_GOING_SESSION_DATA ORDER BY milliseconds DESC", null);

                        onGoingSessionAdapter = new OnGoingSessionAdapter(OngoingSession.this, onGoingSessionDataList);
                        recyclerView.setAdapter(onGoingSessionAdapter);
                        initialCount = newcount;

                    }

                    handler.postDelayed(this, 1);
                }
            }, 1);

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
            finish();
        } else if (orientation == 2) {
            //Cant go back do nothing
        }
    }


}





