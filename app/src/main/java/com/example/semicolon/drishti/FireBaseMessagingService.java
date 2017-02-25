package com.example.semicolon.drishti;

import android.content.Intent;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

/**
 * Created by semicolon on 2/25/2017.
 */

public class FireBaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "MyFirebaseMsgService";
    public static final String INTENT_FILTER = "INTENT_FILTER";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        //Displaying data in log
        //It is optional
        Log.d(TAG, "From: " + remoteMessage.getFrom());
        Log.d(TAG, "Notification Message Body: " + remoteMessage.getNotification().getBody());
        Intent i = new Intent("com.example.semicolon.drishti");
        i.putExtra("message", remoteMessage.getNotification().getBody());
        Intent intent = new Intent(INTENT_FILTER);
        intent.putExtra("message", remoteMessage.getNotification().getBody());
        sendBroadcast(intent);



        //sendNotification(remoteMessage.getNotification().getBody());
    }






}
