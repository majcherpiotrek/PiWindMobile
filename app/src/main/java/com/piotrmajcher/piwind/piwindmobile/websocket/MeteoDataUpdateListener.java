package com.piotrmajcher.piwind.piwindmobile.websocket;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.piotrmajcher.piwind.piwindmobile.config.WEBSOCKET;
import com.piotrmajcher.piwind.piwindmobile.dto.MeteoDataTO;
import com.piotrmajcher.piwind.piwindmobile.updatehandlers.UpdateHandler;

import java.util.UUID;

import okhttp3.Response;

public class MeteoDataUpdateListener extends AbstractUpdateListener<MeteoDataTO> {
    private static final String TAG = MeteoDataUpdateListener.class.getName();

    public MeteoDataUpdateListener(UUID stationId, UpdateHandler<MeteoDataTO> updateHandler) {
        super(stationId, updateHandler);
    }

    @Override
    public void onOpen(okhttp3.WebSocket webSocket, Response response) {
        webSocket.send(stationId.toString());
    }

    @Override
    public void onMessage(okhttp3.WebSocket webSocket, String updatedMeteoData) {
        Log.i(TAG, "New update: " + updatedMeteoData);
        Gson gson = new GsonBuilder().create();
        MeteoDataTO meteoDataTO = gson.fromJson(updatedMeteoData, MeteoDataTO.class);
        super.updateHandler.handleUpdate(meteoDataTO);
    }

    @Override
    public void onClosing(okhttp3.WebSocket webSocket, int code, String reason) {
        webSocket.close(WEBSOCKET.NORMAL_CLOSURE_STATUS, null);
        Log.i(TAG, "Closing " + code + " " + reason);
    }

    @Override
    public void onClosed(okhttp3.WebSocket webSocket, int code, String reason) {
        Log.i(TAG, "Closed " + code + " " + reason);
    }

    @Override
    public void onFailure(okhttp3.WebSocket webSocket, Throwable t, Response response) {
        Log.i(TAG, "Error " + t.getMessage());
    }
}
