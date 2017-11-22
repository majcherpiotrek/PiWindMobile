package com.piotrmajcher.piwind.piwindmobile.services.impl;

import android.support.annotation.NonNull;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.piotrmajcher.piwind.piwindmobile.config.REST;
import com.piotrmajcher.piwind.piwindmobile.services.MeteoStationService;

import org.json.JSONArray;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class MeteoStationServiceImpl implements MeteoStationService {

    private static final String TAG = MeteoStationServiceImpl.class.getName();
    private static final String GET_STATIONS_LIST_URL = REST.BASE_URL + REST.GET_STATIONS_LIST_ENDPOINT;
    private static final String GET_CHART_DATA_BASE_URL = REST.BASE_URL + REST.STATISTIC_ENDPOINT;
    private RequestQueue requestQueue;
    private String token;


    public MeteoStationServiceImpl(RequestQueue requestQueue, String token) {
        this.requestQueue = requestQueue;
        this.token = token;
    }

    @Override
    public void getMeteoStationsList(
            Response.Listener<JSONArray> responseListener,
            Response.ErrorListener errorListener) {

        JsonArrayRequest stationsRequest = new JsonArrayRequest(
                Request.Method.GET, GET_STATIONS_LIST_URL, (String) null, responseListener, errorListener) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json");
                headers.put("Authorization", token);
                return headers;
            }
        };
        stationsRequest.setRetryPolicy(getRetryPolicy());

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
                Request.Method.GET, URL, (String) null, responseListener, errorListener) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json");
                headers.put("Authorization", token);
                return headers;
            }
        };
        chartDataRequest.setRetryPolicy(getRetryPolicy());

        Log.i(TAG, "Sending request to: " + chartDataRequest.getUrl());
        requestQueue.add(chartDataRequest);
        Log.i(TAG, "Request sent.");
    }

    @NonNull
    private RetryPolicy getRetryPolicy() {
        return new RetryPolicy() {
            @Override
            public int getCurrentTimeout() {
                return 50000;
            }

            @Override
            public int getCurrentRetryCount() {
                return 50000;
            }

            @Override
            public void retry(VolleyError error) throws VolleyError {

            }
        };
    }
}
