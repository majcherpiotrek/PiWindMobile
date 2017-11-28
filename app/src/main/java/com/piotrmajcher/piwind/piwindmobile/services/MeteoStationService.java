package com.piotrmajcher.piwind.piwindmobile.services;

import com.android.volley.Response;
import com.piotrmajcher.piwind.piwindmobile.dto.MeteoStationTO;

import org.json.JSONArray;

import java.util.List;
import java.util.UUID;

public interface MeteoStationService {

    void getMeteoStationsList(
            Response.Listener<JSONArray> responseListener,
            Response.ErrorListener errorListener);

    void getChartData(UUID stationId, int samples, int intervalMinutes,
                      Response.Listener<JSONArray> responseListener,
                      Response.ErrorListener errorListener);

    void requestNotifications(UUID stationId, Integer minWindLimit,
                              Response.Listener<String> responseListener,
                              Response.ErrorListener errorListener);

    void cancelNotifications(UUID stationId,
                              Response.Listener<String> responseListener,
                              Response.ErrorListener errorListener);
}
