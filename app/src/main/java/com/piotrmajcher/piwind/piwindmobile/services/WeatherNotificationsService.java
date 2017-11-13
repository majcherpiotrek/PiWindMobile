package com.piotrmajcher.piwind.piwindmobile.services;

import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class WeatherNotificationsService extends FirebaseMessagingService {

    private static final String TAG = WeatherNotificationsService.class.getName();

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Log.i(TAG, "From: " + remoteMessage.getFrom());
        Log.i(TAG, "Message: " + remoteMessage.getNotification().getBody());
    }
}
