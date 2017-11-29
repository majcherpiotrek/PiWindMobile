package com.piotrmajcher.piwind.piwindmobile.websocket;

import com.piotrmajcher.piwind.piwindmobile.updatehandlers.UpdateHandler;

import java.util.UUID;

import okhttp3.WebSocketListener;

abstract class AbstractUpdateListener<T> extends WebSocketListener{
    UUID stationId;
    UpdateHandler<T> updateHandler;

    AbstractUpdateListener(UUID stationId, UpdateHandler<T> updateHandler) {
        super();
        this.stationId = stationId;
        this.updateHandler = updateHandler;
    }
}
