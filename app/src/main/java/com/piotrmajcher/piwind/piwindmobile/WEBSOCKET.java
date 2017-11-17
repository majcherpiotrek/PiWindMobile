package com.piotrmajcher.piwind.piwindmobile;

public interface WEBSOCKET {

//    String BASE_URL = "ws://10.38.250.159:8080";
    String BASE_URL = "ws://10.0.2.2:8080";
    String METEO_UPDATE_ENDPOINT = "/meteo";
    String SNAPSHOTS_UPDATE_ENDPOINT = "/snapshots";
    int NORMAL_CLOSURE_STATUS = 1000;
    String ACTIVITY_STOPPED = "Activity stopped";
}
