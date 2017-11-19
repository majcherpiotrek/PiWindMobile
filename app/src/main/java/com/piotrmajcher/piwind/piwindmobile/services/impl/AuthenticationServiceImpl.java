package com.piotrmajcher.piwind.piwindmobile.services.impl;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.piotrmajcher.piwind.piwindmobile.services.AuthenticationService;

import org.json.JSONArray;

public class AuthenticationServiceImpl implements AuthenticationService {

    private RequestQueue requestQueue;

    public AuthenticationServiceImpl(RequestQueue requestQueue) {
        this.requestQueue = requestQueue;
    }

    @Override
    public void requestUserAuthentication(String email, String password, Response.Listener<JSONArray> responseListener, Response.ErrorListener errorListener) {

    }
}
