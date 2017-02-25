package com.example.semicolon.drishti;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
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

import jp.wasabeef.recyclerview.animators.SlideInLeftAnimator;

/**
 * Created by semicolon on 2/25/2017.
 */

public class Session extends AppCompatActivity {

    public static final String TAG = "SessionActivity";
    private Camera mCamera;
    private CameraPreview mPreview;
    FrameLayout preview;
    private GestureDetectorCompat mDetector;
    ImageView camera_image_preview;

    //boolean to check if it is safe to click an image
    private boolean safeToTakePicture = true;

    private Toolbar toolbar;
    private RecyclerView recyclerView;
    long initialCount;
    private SessionAdapter sessionAdapter;
    private List<SessionData> sessionDataList = new ArrayList<SessionData>();

    //Orientation
    int orientation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_session);


        mDetector = new GestureDetectorCompat(this, new MyGestureListener());


        orientation = getResources().getConfiguration().orientation;

        if (orientation == 1) {
            //Handle Portrait views here

            toolbar = (Toolbar) findViewById(R.id.toolbar); // Attaching the layout to the toolbar object
            setSupportActionBar(toolbar);


            recyclerView = (RecyclerView) findViewById(R.id.image_rv);

            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
            recyclerView.setLayoutManager(linearLayoutManager);
            recyclerView.setItemAnimator(new SlideInLeftAnimator());

            recyclerView.addItemDecoration(new DividerItemDecoration(getApplicationContext(), DividerItemDecoration.VERTICAL));

            initialCount = SessionData.count(SessionData.class);

            //Sugar orm
            if (initialCount >= 0) {

                sessionDataList = SessionData.listAll(SessionData.class);

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

    @Subscribe(threadMode = ThreadMode.MAIN )
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

}
