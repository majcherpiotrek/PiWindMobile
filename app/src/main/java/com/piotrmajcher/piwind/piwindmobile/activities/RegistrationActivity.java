package com.piotrmajcher.piwind.piwindmobile.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.NetworkResponse;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.piotrmajcher.piwind.piwindmobile.ApplicationController;
import com.piotrmajcher.piwind.piwindmobile.R;
import com.piotrmajcher.piwind.piwindmobile.services.AuthService;
import com.piotrmajcher.piwind.piwindmobile.services.impl.AuthServiceImpl;

import org.json.JSONException;
import org.json.JSONObject;


public class RegistrationActivity extends AppCompatActivity {

    private static final String TAG = RegistrationActivity.class.getName();
    private EditText usernameInput;
    private EditText emailInput;
    private EditText passwordInput;
    private EditText matchingPasswordInput;
    private TextView errorLabel;
    private Button registerButton;
    private AuthService authService;
    private Context context;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        initViews();
        RequestQueue requestQueue = ApplicationController.getInstance(getApplicationContext()).getRequestQueue();
        authService = new AuthServiceImpl(requestQueue);
        context = RegistrationActivity.this;

        registerButton.setOnClickListener(v -> {
            onRegisterButtonClick();
        });
    }

    private void onRegisterButtonClick() {
        String username = usernameInput.getText().toString();
        String email = emailInput.getText().toString();
        String password = passwordInput.getText().toString();
        String matchingPassword = matchingPasswordInput.getText().toString();
        runOnUiThread(() -> errorLabel.setVisibility(View.GONE));
        if (isInputValid(username, email, password, matchingPassword)) {
            progressDialog = new ProgressDialog(context);
            progressDialog.setMessage("Registering new account ...");
            progressDialog.show();
            try {
                authService.registerUser(
                        username,
                        email,
                        password,
                        matchingPassword,
                        this::handleResponse,
                        this::handleError
                );
            } catch (JSONException e) {
                Toast.makeText(getApplicationContext(), "Something went wrong", Toast.LENGTH_LONG).show();
            }
        }
    }

    private boolean isInputValid(String username, String email, String password, String matchingPassword) {
        boolean valid = true;
        if (username == null
                || username.length() == 0
                || email == null
                || email.length() == 0
                || password == null
                || password.length() == 0
                || matchingPassword == null
                || matchingPassword.length() == 0
                ) {
            runOnUiThread(() -> {
                errorLabel.setText("Please fill in all of the fields");
                errorLabel.setVisibility(View.VISIBLE);
            });
            valid = false;
        }
        return valid;
    }

    private void handleError(VolleyError error) {
        NetworkResponse response = error.networkResponse;
        progressDialog.dismiss();
        if(response != null && response.data != null){

            String responseText = new String(response.data);

            runOnUiThread(() -> {
                try {
                    JSONObject jsonObject = new JSONObject(responseText);
                    errorLabel.setText(jsonObject.get("err").toString());
                    errorLabel.setVisibility(View.VISIBLE);
                } catch (JSONException e) {
                    errorLabel.setText("Unexpected error");
                    errorLabel.setVisibility(View.VISIBLE);
                }
            });
            Log.i(TAG, "Error " + response.statusCode + responseText);
        }
    }

    private void handleResponse(JSONObject response) {
        Log.i(TAG, response.toString());
        String toastMsg;
        try {
            toastMsg = response.get("msg").toString();
        } catch (JSONException e) {
            toastMsg = "Your account has been successully registered";
        }
        Toast.makeText(getApplicationContext(), toastMsg, Toast.LENGTH_LONG).show();
        progressDialog.dismiss();
        redirectToConfirmEmailActivity();
    }

    private void redirectToConfirmEmailActivity() {
        Intent confirmEmailActivityIntent = new Intent(this, ConfirmEmailActivity.class);
        startActivity(confirmEmailActivityIntent);
    }

    private void initViews() {
        usernameInput = (EditText) findViewById(R.id.username_input_registration);
        emailInput = (EditText) findViewById(R.id.email_input_registration);
        passwordInput = (EditText) findViewById(R.id.password_input_registration);
        matchingPasswordInput = (EditText) findViewById(R.id.matching_password_input_registration);
        registerButton = (Button) findViewById(R.id.register_button);
        errorLabel = (TextView) findViewById(R.id.registration_error_label);
    }

}
