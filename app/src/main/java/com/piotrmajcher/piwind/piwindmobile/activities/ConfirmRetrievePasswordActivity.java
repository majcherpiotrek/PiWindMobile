package com.piotrmajcher.piwind.piwindmobile.activities;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
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

public class ConfirmRetrievePasswordActivity extends AppCompatActivity{

    private static final String TAG = ConfirmRetrievePasswordActivity.class.getName();
    private EditText tokenInput;
    private EditText passwordInput;
    private EditText matchingPasswordInput;
    private TextView errorLabel;
    private Button changePasswordButton;
    private AuthService authService;
    private Context context;
    private ProgressDialog progressDialog;
    private Toolbar toolbar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_retrieve_password);

        initViews();
        RequestQueue requestQueue = ApplicationController.getInstance(getApplicationContext()).getRequestQueue();
        authService = new AuthServiceImpl(requestQueue);
        context = ConfirmRetrievePasswordActivity.this;

        changePasswordButton.setOnClickListener(v -> {
            onChangePasswordButtonClick();
        });
    }

    private void onChangePasswordButtonClick() {
        errorLabel.setVisibility(View.GONE);
        String token = tokenInput.getText().toString();
        String newPassword = passwordInput.getText().toString();
        String matchingPassword = matchingPasswordInput.getText().toString();
        progressDialog = new ProgressDialog(context);
        progressDialog.setMessage("Changing password ...");
        progressDialog.show();
        if (token == null || token.isEmpty()
                || newPassword == null || newPassword.isEmpty()
                || matchingPassword == null || newPassword.isEmpty()) {
            errorLabel.setText("You cannot leave empty fields!");
            errorLabel.setVisibility(View.VISIBLE);
            progressDialog.dismiss();
        } else {
            try {
                authService.changePassword(
                        token,
                        newPassword,
                        matchingPassword,
                        this::handleResponse,
                        this::handleError
                );
            } catch (JSONException e) {
                e.printStackTrace();
                errorLabel.setText("Unexpected error");
                errorLabel.setVisibility(View.VISIBLE);
                progressDialog.dismiss();
            }
        }
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
            toastMsg = "Your password has been changed!";
        }
        Toast.makeText(getApplicationContext(), toastMsg, Toast.LENGTH_LONG).show();
        progressDialog.dismiss();
        redirectToLoginActivity();
    }

    private void redirectToLoginActivity() {
        Intent loginActivityIntent = new Intent(this, LoginActivity.class);
        loginActivityIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(loginActivityIntent);
    }

    private void initViews() {

        changePasswordButton = (Button) findViewById(R.id.change_password_button);
        tokenInput = (EditText) findViewById(R.id.token_input);
        passwordInput = (EditText) findViewById(R.id.new_password_input);
        matchingPasswordInput = (EditText) findViewById(R.id.new_matching_password_input);
        errorLabel = (TextView) findViewById(R.id.change_password_error_label);

        toolbar = (Toolbar) findViewById(R.id.toolbar); // Attaching the layout to the toolbar object
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }
}
