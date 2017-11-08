package com.piotrmajcher.piwind.piwindmobile.util;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public interface JsonToObjectsParser<T> {

    T parseJSONObject(JSONObject jsonObject, Class<T> targetClass);

    List<T> parseJSONArray(JSONArray jsonArray, Class<T> targetClass) throws JSONException;
}
