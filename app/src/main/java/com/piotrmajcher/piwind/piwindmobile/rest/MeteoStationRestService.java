package com.piotrmajcher.piwind.piwindmobile.rest;

import com.android.volley.Response;
import com.piotrmajcher.piwind.piwindmobile.dto.MeteoStationTO;

import org.json.JSONArray;

import java.util.List;
import java.util.UUID;

public interface MeteoStationRestService {

    void getMeteoStationsList(
            Response.Listener<JSONArray> responseListener,
            Response.ErrorListener errorListener);

    void getChartData(UUID stationId, int samples, int intervalMinutes,
                      Response.Listener<JSONArray> responseListener,
                      Response.ErrorListener errorListener);
}
