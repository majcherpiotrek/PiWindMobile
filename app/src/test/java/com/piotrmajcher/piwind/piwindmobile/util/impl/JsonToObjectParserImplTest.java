package com.piotrmajcher.piwind.piwindmobile.util.impl;

import com.piotrmajcher.piwind.piwindmobile.dto.MeteoStationTO;
import com.piotrmajcher.piwind.piwindmobile.util.JsonToObjectsParser;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Test;

import java.util.List;
import java.util.UUID;

import static org.junit.Assert.*;

public class JsonToObjectParserImplTest {


    @Test
    public void parseMeteoStationTOJsonObjectTest() throws Exception {
        JsonToObjectParserImpl<MeteoStationTO> jsonToObjectsParser = new JsonToObjectParserImpl<>();

        MeteoStationTO meteoStationTO = new MeteoStationTO();
        meteoStationTO.setId(UUID.randomUUID().toString());
        meteoStationTO.setName("station name");
        meteoStationTO.setStationBaseURL("http://domain.com");

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("id", meteoStationTO.getId());
        jsonObject.put("name", meteoStationTO.getName());
        jsonObject.put("stationBaseURL", meteoStationTO.getStationBaseURL());

        MeteoStationTO result = jsonToObjectsParser.parseJSONObject(jsonObject, MeteoStationTO.class);

        assertEquals(meteoStationTO, result);
    }

    @Test
    public void parseMeteoStationTOJsonArrayTest() throws Exception {
        JsonToObjectParserImpl<MeteoStationTO> jsonToObjectsParser = new JsonToObjectParserImpl<>();

        MeteoStationTO meteoStationTO = new MeteoStationTO();
        meteoStationTO.setId(UUID.randomUUID().toString());
        meteoStationTO.setName("station name");
        meteoStationTO.setStationBaseURL("http://domain.com");

        MeteoStationTO meteoStationTO1 = new MeteoStationTO();
        meteoStationTO1.setId(UUID.randomUUID().toString());
        meteoStationTO1.setName("station name");
        meteoStationTO1.setStationBaseURL("http://domain.com");

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("id", meteoStationTO.getId());
        jsonObject.put("name", meteoStationTO.getName());
        jsonObject.put("stationBaseURL", meteoStationTO.getStationBaseURL());

        JSONObject jsonObject1 = new JSONObject();
        jsonObject1.put("id", meteoStationTO1.getId());
        jsonObject1.put("name", meteoStationTO1.getName());
        jsonObject1.put("stationBaseURL", meteoStationTO1.getStationBaseURL());

        JSONArray jsonArray = new JSONArray();
        jsonArray.put(jsonObject);
        jsonArray.put(jsonObject1);

        List<MeteoStationTO> result = jsonToObjectsParser.parseJSONArray(jsonArray, MeteoStationTO.class);

        assertEquals(meteoStationTO, result.get(0));
        assertEquals(meteoStationTO1, result.get(1));
    }

}