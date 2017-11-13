package com.piotrmajcher.piwind.piwindmobile.websocket;

import android.util.Log;

import com.piotrmajcher.piwind.piwindmobile.updatehandlers.UpdateHandler;

import java.util.UUID;

import okhttp3.Response;
import okio.ByteString;

public class SnapshotUpdateListener extends AbstractUpdateListener<byte[]> {
    private static final String TAG = SnapshotUpdateListener.class.getName();

    public SnapshotUpdateListener(UUID stationId, UpdateHandler<byte[]> updateHandler) {
        super(stationId, updateHandler);
    }

    @Override
    public void onOpen(okhttp3.WebSocket webSocket, Response response) {
        webSocket.send(stationId.toString());
    }

    @Override
    public void onMessage(okhttp3.WebSocket webSocket, String message) {
        Log.i(TAG, "Received message: " + message);
    }

    @Override
    public void onMessage(okhttp3.WebSocket webSocket, ByteString bytes) {
        Log.i(TAG, "Received bytestring message: " + bytes.size());
        super.updateHandler.handleUpdate(bytes.toByteArray());
    }

    @Override
    public void onClosing(okhttp3.WebSocket webSocket, int code, String reason) {
        webSocket.close(NORMAL_CLOSURE_STATUS, null);
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
