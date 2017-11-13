package com.piotrmajcher.piwind.piwindmobile.websocket;

import com.piotrmajcher.piwind.piwindmobile.updatehandlers.UpdateHandler;

import java.util.UUID;

import okhttp3.WebSocketListener;

public abstract class AbstractUpdateListener<T> extends WebSocketListener{
    static final int NORMAL_CLOSURE_STATUS = 1000;
    UUID stationId;
    UpdateHandler<T> updateHandler;

    public AbstractUpdateListener(UUID stationId, UpdateHandler<T> updateHandler) {
        this.stationId = stationId;
        this.updateHandler = updateHandler;
    }
}
