package com.piotrmajcher.piwind.piwindmobile.activities;

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

public class ConfirmEmailActivity extends AppCompatActivity {

    private static final String TAG = ConfirmEmailActivity.class.getName();
    private Button confirmEmailButton;
    private EditText confirmationCodeInput;
    private TextView errorLabel;
    private AuthService authService;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_email);
        RequestQueue requestQueue = ApplicationController.getInstance(getApplicationContext()).getRequestQueue();
        authService = new AuthServiceImpl(requestQueue);
        initViews();

        confirmEmailButton.setOnClickListener((v) -> onConfirmEmailButtonClick());
    }

    private void onConfirmEmailButtonClick() {
        String confirmationCode = confirmationCodeInput.getText().toString();
        runOnUiThread(() -> errorLabel.setVisibility(View.GONE));
        if (confirmationCode.length() == 0) {
            runOnUiThread(() -> {
                errorLabel.setText("Please enter the confirmation code");
                errorLabel.setVisibility(View.VISIBLE);
            });
        } else {
            authService.confirmEmail(
                    confirmationCode,
                    this::handleResponse,
                    this::handleError);
        }
    }

    private void handleResponse(String response) {
        Toast.makeText(getApplicationContext(), response, Toast.LENGTH_LONG);
        redirectToLoginActivity();
    }

    private void redirectToLoginActivity() {
        Intent loginActivityIntent = new Intent(this, LoginActivity.class);
        loginActivityIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(loginActivityIntent);
    }

    private void handleError(VolleyError error) {
        NetworkResponse response = error.networkResponse;
        String err = new String(response.data);
        Log.e(TAG, "error: " + err);
        runOnUiThread(() -> {
            if (err.length() == 0) {
                errorLabel.setText("Something went wrong");
            } else {
                errorLabel.setText(err);
            }
            errorLabel.setVisibility(View.VISIBLE);
        });
    }

    private void initViews() {
        confirmEmailButton = (Button) findViewById(R.id.confirm_email_button);
        confirmationCodeInput = (EditText) findViewById(R.id.confirmation_code_input);
        errorLabel = (TextView) findViewById(R.id.confirm_email_error_label);
    }
}
