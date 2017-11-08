package com.piotrmajcher.piwind.piwindmobile.rest.impl;

import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonArrayRequest;
import com.piotrmajcher.piwind.piwindmobile.MainActivity;
import com.piotrmajcher.piwind.piwindmobile.rest.MeteoStationRestService;

import junit.framework.Assert;

import org.json.JSONArray;

public class MeteoStationRestServiceImpl implements MeteoStationRestService {

    private static final String TAG = MeteoStationRestServiceImpl.class.getName();
    private static final String GET_STATIONS_LIST_URL = "http://10.0.2.2:8080/stations/all";
    private RequestQueue requestQueue;


    public MeteoStationRestServiceImpl() {
        this.requestQueue = MainActivity.getRequestQueue();
    }

    @Override
    public void getMeteoStationsList(
            Response.Listener<JSONArray> responseListener,
            Response.ErrorListener errorListener) {
        Assert.assertNotNull(responseListener);
        Assert.assertNotNull(errorListener);


        JsonArrayRequest stationsRequest = new JsonArrayRequest(
                Request.Method.GET, GET_STATIONS_LIST_URL, (String) null, responseListener, errorListener);
        Log.i(TAG, "Sending request to: " + GET_STATIONS_LIST_URL);
        requestQueue.add(stationsRequest);
        Log.i(TAG, "Request sent.");
    }
}
