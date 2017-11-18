package com.piotrmajcher.piwind.piwindmobile.rest.impl;

import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonArrayRequest;
import com.piotrmajcher.piwind.piwindmobile.MainActivity;
import com.piotrmajcher.piwind.piwindmobile.REST;
import com.piotrmajcher.piwind.piwindmobile.rest.MeteoStationRestService;

import junit.framework.Assert;

import org.json.JSONArray;

import java.util.UUID;

public class MeteoStationRestServiceImpl implements MeteoStationRestService {

    private static final String TAG = MeteoStationRestServiceImpl.class.getName();
    private static final String GET_STATIONS_LIST_URL = REST.BASE_URL + REST.GET_STATIONS_LIST_ENDPOINT;
    private static final String GET_CHART_DATA_BASE_URL = REST.BASE_URL + REST.STATISTIC_ENDPOINT;
    private RequestQueue requestQueue;


    public MeteoStationRestServiceImpl() {
        this.requestQueue = MainActivity.REQUEST_QUEUE;
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

    @Override
    public void getChartData(UUID stationId, int samples, int intervalMinutes, Response.Listener<JSONArray> responseListener, Response.ErrorListener errorListener) {
        StringBuilder sb = new StringBuilder();
        sb.append(GET_CHART_DATA_BASE_URL);
        sb.append("/");
        sb.append(stationId.toString());
        sb.append("/");
        sb.append(samples);
        sb.append("/");
        sb.append(intervalMinutes);
        String URL = sb.toString();

        JsonArrayRequest chartDataRequest = new JsonArrayRequest(
                Request.Method.GET, URL, (String) null, responseListener, errorListener);

        Log.i(TAG, "Sending request to: " + URL);
        requestQueue.add(chartDataRequest);
        Log.i(TAG, "Request sent.");
    }
}
