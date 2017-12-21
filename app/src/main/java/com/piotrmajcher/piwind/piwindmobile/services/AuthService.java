package com.piotrmajcher.piwind.piwindmobile.services;

import com.android.volley.Response;

import org.json.JSONException;
import org.json.JSONObject;

public interface AuthService {

    void registerUser(String username, String email, String password, String matchingPassword,
                      Response.Listener<JSONObject> responseListener,
                      Response.ErrorListener errorListener
    ) throws JSONException;

    void confirmEmail(String confirmationCode,
                      Response.Listener<String> responseListener,
                      Response.ErrorListener errorListener
    );

    void login(String username, String password,
               Response.Listener<JSONObject> responseListener,
               Response.ErrorListener errorListener
    ) throws JSONException;

    void retrievePassword(String usernameOrEmail,
                      Response.Listener<String> responseListener,
                      Response.ErrorListener errorListener
    );

    void changePassword(String token, String password, String matchingPassword,
                                 Response.Listener<JSONObject> responseListener,
                                 Response.ErrorListener errorListener
    ) throws JSONException;
}
