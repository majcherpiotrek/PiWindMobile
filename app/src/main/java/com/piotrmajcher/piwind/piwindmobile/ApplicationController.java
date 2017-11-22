package com.piotrmajcher.piwind.piwindmobile;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

public class ApplicationController {

    private static ApplicationController instance;

    private Context context;
    private RequestQueue requestQueue;
    private String token;


    private ApplicationController(Context context) {
        this.context = context;
        this.requestQueue = getRequestQueue();
    }

    public static synchronized ApplicationController getInstance(Context context) {
        if (instance == null) {
            instance = new ApplicationController(context);
        }

        return instance;
    }

    public RequestQueue getRequestQueue() {
        if (requestQueue == null) {
            requestQueue = Volley.newRequestQueue(context.getApplicationContext());
        }
        return requestQueue;
    }

    public String getToken() {
        return this.token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public <T> void addToRequestQueue(Request<T> req) {
        getRequestQueue().add(req);
    }
}
