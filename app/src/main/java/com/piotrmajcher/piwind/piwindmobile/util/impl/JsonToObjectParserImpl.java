package com.piotrmajcher.piwind.piwindmobile.util.impl;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.piotrmajcher.piwind.piwindmobile.util.JsonToObjectsParser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.LinkedList;
import java.util.List;


public class JsonToObjectParserImpl<T> implements JsonToObjectsParser {

    private Gson gson;

    public JsonToObjectParserImpl() {
        this.gson = new GsonBuilder().create();
    }

    @Override
    public T parseJSONObject(JSONObject jsonObject, Class targetClass) {
        return (T) gson.fromJson(jsonObject.toString(), targetClass);
    }

    @Override
    public List<T> parseJSONArray(JSONArray jsonArray, Class targetClass) throws JSONException {
        List<T> resultList = new LinkedList<>();
        for (int i = 0; i < jsonArray.length(); i++) {
            resultList.add(parseJSONObject(jsonArray.getJSONObject(i), targetClass));
        }
        return resultList;
    }
}
