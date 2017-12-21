package com.piotrmajcher.piwind.piwindmobile.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.piotrmajcher.piwind.piwindmobile.ApplicationController;
import com.piotrmajcher.piwind.piwindmobile.R;
import com.piotrmajcher.piwind.piwindmobile.config.CONFIG;
import com.piotrmajcher.piwind.piwindmobile.services.AuthService;
import com.piotrmajcher.piwind.piwindmobile.services.impl.AuthServiceImpl;

import org.json.JSONException;
import org.json.JSONObject;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = LoginActivity.class.getName();
    private EditText usernameInput;
    private EditText passwordInput;
    private Button loginButton;
    private TextView register;
    private TextView forgotPassword;
    private TextView errorLabel;
    private AuthService authService;
    private Intent intent;
    private Toolbar toolbar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        RequestQueue requestQueue = ApplicationController.getInstance(getApplicationContext()).getRequestQueue();
        authService = new AuthServiceImpl(requestQueue);
        initViews();
        intent = getIntent();
        loginButton.setOnClickListener(v -> handleLoginRequest());
        register.setOnClickListener(v -> startRegistrationActivity());
        forgotPassword.setOnClickListener(v -> startRetrievePasswordAcctivity());
    }

    private void startRetrievePasswordAcctivity() {
        Intent intentRetrievePassword = new Intent(this, RetrievePasswordActivity.class);
        startActivity(intentRetrievePassword);
    }

    private void startRegistrationActivity() {
        Intent intentRegistration = new Intent(this, RegistrationActivity.class);
        startActivity(intentRegistration);
    }

    private void handleLoginRequest() {
        String username = usernameInput.getText().toString();
        String password = passwordInput.getText().toString();
        validateCredentials(username, password);
    }

    private void saveCredentials(String email, String password) {
        SharedPreferences sp = getSharedPreferences(CONFIG.LOGIN_PREFERENCES_KEY, MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(CONFIG.USERNAME, email);
        editor.putString(CONFIG.PASSWORD_KEY, password);
        editor.putBoolean(CONFIG.IS_USER_AUTHORIZED_KEY, true);
        editor.apply();
    }


    private void startActivityBasedOnIntent(Intent intent) {
        Bundle extras = intent.getExtras();
        Intent stationsListIntent = new Intent(this, StationsListActivity.class);
        runOnUiThread(() -> {
            errorLabel.setVisibility(View.GONE);
        });
        if (extras != null && extras.containsKey(CONFIG.ID_KEY) && extras.containsKey(CONFIG.NAME_KEY) && extras.containsKey(CONFIG.URL_KEY)) {
            stationsListIntent.putExtra(CONFIG.ID_KEY, extras.getString(CONFIG.ID_KEY));
            stationsListIntent.putExtra(CONFIG.NAME_KEY, extras.getString(CONFIG.NAME_KEY));
            stationsListIntent.putExtra(CONFIG.URL_KEY, extras.getString(CONFIG.URL_KEY));
            startActivity(stationsListIntent);
        }
        startActivity(stationsListIntent);
        finishLoginActivity();

    }

    private void finishLoginActivity() {
        finish();
    }

    private void validateCredentials(String username, String password) {
        if (username.length() == 0 || password.length() == 0) {
            errorLabel.setText("Username and password cannot be empty!");
            errorLabel.setVisibility(View.VISIBLE);
        } else {
            try {
                authService.login(
                        username,
                        password,
                        response -> handleResponse(username, password, response),
                        this::handleError
                );
            } catch (JSONException e) {
                Log.e(TAG, "Could not send request");
                runOnUiThread(() -> {
                    errorLabel.setText("Unexpected error");
                    errorLabel.setVisibility(View.VISIBLE);
                });
            }
        }
    }

    private void handleError(VolleyError error) {
        String errorMsg = new String(error.networkResponse.data);
        Log.e(TAG, "error: " + errorMsg);
        runOnUiThread(() -> {
            try {
                JSONObject jsonObject = new JSONObject(errorMsg);
                errorLabel.setText(jsonObject.get("message").toString());
                errorLabel.setVisibility(View.VISIBLE);
            } catch (JSONException e) {
                errorLabel.setText("Unexpected error");
                errorLabel.setVisibility(View.VISIBLE);
            }
        });
    }

    private void handleResponse(String username, String password, JSONObject response) {
        try {
            String token = response.getString("token");
            saveCredentials(username, password);
            ApplicationController.getInstance(getApplicationContext()).setToken(token);
            startActivityBasedOnIntent(intent);
        } catch (JSONException e) {
            errorLabel.setText("Unexpected error");
            errorLabel.setVisibility(View.VISIBLE);
            Log.e(TAG, "Could not parse response");
        }
    }

    private void initViews() {
        usernameInput = (EditText) findViewById(R.id.username_input);
        passwordInput = (EditText) findViewById(R.id.password_input);
        loginButton = (Button) findViewById(R.id.login_button);
        register = (TextView) findViewById(R.id.register_text);
        forgotPassword = (TextView) findViewById(R.id.retrieve_password_text);
        errorLabel = (TextView) findViewById(R.id.login_error_label);

        toolbar = (Toolbar) findViewById(R.id.toolbar); // Attaching the layout to the toolbar object
        setSupportActionBar(toolbar);
    }
}
