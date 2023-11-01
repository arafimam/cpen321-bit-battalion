package com.example.triptrooperapp;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class FirebaseMessageService extends FirebaseMessagingService {

    private final String TAG = "FIREBASE";

    @Override
    public void onNewToken(@NonNull String token) {
        Log.d(TAG, "Refreshed token: " + token);

        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // FCM registration token to your app server.

        //TODO: Make call to backend server
        //sendRegistrationToServer(token);
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        Log.d("TAG", "From: " + remoteMessage.getFrom());

        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            Log.d(TAG, "Message data payload: " + remoteMessage.getData());

        }

        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            Log.d(TAG,
                    "Message Notification Body: " + remoteMessage.getNotification().getBody());
            Log.d(TAG,
                    "Message Notification Title: " + remoteMessage.getNotification().getTitle());

//            NotificationCompat.Builder builder = new NotificationCompat
//            .Builder(this,"CHANNEL_ID")
//                    .setContentTitle(remoteMessage.getNotification()
//                    .getBody())
//                    .setContentText(remoteMessage.getNotification()
//                    .getTitle())
//                    .setPriority(NotificationCompat.PRIORITY_HIGH);
//
//            NotificationManagerCompat notificationManager =
//            NotificationManagerCompat.from(this);
//            notificationManager.notify(1, builder.build());


        }


        // Also if you intend on generating your own notifications as a
        // result of a received FCM
        // message, here is where that should be initiated. See
        // sendNotification method below.
    }


}
