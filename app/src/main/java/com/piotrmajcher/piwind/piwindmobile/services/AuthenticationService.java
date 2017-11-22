package com.piotrmajcher.piwind.piwindmobile.services;

import com.android.volley.Response;

import org.json.JSONArray;

public interface AuthenticationService {

    void requestUserAuthentication(String email, String password,
                                   Response.Listener<JSONArray> responseListener,
                                   Response.ErrorListener errorListener);
}
