package com.piotrmajcher.piwind.piwindmobile.services;

import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class WeatherNotificationsService extends FirebaseMessagingService {

    private static final String TAG = WeatherNotificationsService.class.getName();

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
         if (remoteMessage.getNotification() != null) {
             Log.e(TAG, "From: " + remoteMessage.getFrom());
             Log.e(TAG, "Message: " + remoteMessage.getNotification().getBody());
         }

         if (remoteMessage.getData().size() > 0) {
             Log.e(TAG, "Data: " + remoteMessage.getData());
         }
    }
}
