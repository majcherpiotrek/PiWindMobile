package com.piotrmajcher.piwind.piwindmobile;

public interface REST {
    String BASE_URL = "http://192.168.0.110:8080";
//    String BASE_URL = "http://10.38.251.30:8080";
    //String BASE_URL = "http://10.0.2.2:8080";
    String GET_STATIONS_LIST_ENDPOINT = "/stations/all";
    String STATISTIC_ENDPOINT = "/statistics";
}
