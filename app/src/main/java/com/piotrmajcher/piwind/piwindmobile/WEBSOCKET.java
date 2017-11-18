package com.piotrmajcher.piwind.piwindmobile;

public interface WEBSOCKET {
    String BASE_URL = "ws://192.168.0.110:8080";
//    String BASE_URL = "ws://10.38.251.30:8080";
//    String BASE_URL = "ws://10.0.2.2:8080";
    String METEO_UPDATE_ENDPOINT = "/meteo";
    String SNAPSHOTS_UPDATE_ENDPOINT = "/snapshots";
    int NORMAL_CLOSURE_STATUS = 1000;
    String REFRESH = "Refresh requested";
    String FRAGMENT_PAUSED = "Fragment paused";
}
