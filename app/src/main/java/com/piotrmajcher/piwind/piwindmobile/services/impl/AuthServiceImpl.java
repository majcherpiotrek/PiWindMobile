package com.piotrmajcher.piwind.piwindmobile.services.impl;

import android.support.annotation.NonNull;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.piotrmajcher.piwind.piwindmobile.config.REST;
import com.piotrmajcher.piwind.piwindmobile.services.AuthService;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

public class AuthServiceImpl implements AuthService {

    private static final String TAG = AuthServiceImpl.class.getName();
    private static final String REGISTER_USER_URL = REST.BASE_URL + REST.REGISTER_USER_URL;
    private static final String CONFIRM_EMAIL_URL = REST.BASE_URL + REST.CONFIRM_EMAIL_URL;
    private static final String LOGIN_URL = REST.BASE_URL + REST.LOGIN_URL;
    private RequestQueue requestQueue;


    public AuthServiceImpl(RequestQueue requestQueue) {
        this.requestQueue = requestQueue;
    }

    @Override
    public void registerUser(String username, String email, String password, String matchingPassword, Response.Listener<JSONObject> responseListener, Response.ErrorListener errorListener) throws JSONException {

        JSONObject json = new JSONObject();
        json.put("username", username);
        json.put("email", email);
        json.put("password", password);
        json.put("matchingPassword", matchingPassword);

        JsonObjectRequest request  = new JsonObjectRequest(
                Request.Method.POST,
                REGISTER_USER_URL,
                json,
                responseListener,
                errorListener
        ) {
            @Override
            public String getBodyContentType() {
                return "application/json";
            }
        };

        request.setRetryPolicy(getRetryPolicy());

        Log.i(TAG, "Sending request to: " + request.getUrl());
        requestQueue.add(request);
        Log.i(TAG, "Request sent.");
    }

    @Override
    public void confirmEmail(String confirmationCode, Response.Listener<String> responseListener, Response.ErrorListener errorListener) {
        String url = CONFIRM_EMAIL_URL + confirmationCode;

        StringRequest request = new StringRequest(
                Request.Method.GET,
                url,
                responseListener,
                errorListener);

        request.setRetryPolicy(getRetryPolicy());
        Log.i(TAG, "Sending request to: " + request.getUrl());
        requestQueue.add(request);
        Log.i(TAG, "Request sent.");

    }

    @Override
    public void login(String username, String password, Response.Listener<JSONObject> responseListener, Response.ErrorListener errorListener) throws JSONException {

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("username", username);
        jsonObject.put("password", password);

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.POST,
                LOGIN_URL,
                jsonObject,
                responseListener,
                errorListener
        ) {

            @Override
            protected Response<JSONObject> parseNetworkResponse(NetworkResponse response) {
                try {
                    String token = response.headers.get(REST.AUTH_HEADER_KEY);
                    JSONObject jsonResponse = new JSONObject();
                    jsonResponse.put("token", token);
                    return Response.success(jsonResponse,
                            HttpHeaderParser.parseCacheHeaders(response));
                } catch (JSONException je) {
                    return Response.error(new ParseError(je));
                }
            }
        };

        request.setRetryPolicy(new DefaultRetryPolicy(30000, 0, 0f));

        Log.i(TAG, "Sending request to: " + request.getUrl());
        requestQueue.add(request);
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
