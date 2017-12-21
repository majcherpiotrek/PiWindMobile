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

public class RetrievePasswordActivity extends AppCompatActivity {

    private static final String TAG = RetrievePasswordActivity.class.getName();
    private EditText usernameOrEmailInput;
    private TextView errorLabel;
    private Button retrievePasswordButton;
    private AuthService authService;
    private Context context;
    private ProgressDialog progressDialog;
    private Toolbar toolbar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_retrieve_password);

        initViews();
        RequestQueue requestQueue = ApplicationController.getInstance(getApplicationContext()).getRequestQueue();
        authService = new AuthServiceImpl(requestQueue);
        context = RetrievePasswordActivity.this;

        retrievePasswordButton.setOnClickListener(v -> {
            onRetrievePasswordButtonClick();
        });
    }

    private void onRetrievePasswordButtonClick() {
        String usernameOrEmail = usernameOrEmailInput.getText().toString();
        runOnUiThread(() -> errorLabel.setVisibility(View.GONE));
        progressDialog = new ProgressDialog(context);
        progressDialog.setMessage("Requesting password change ...");
        progressDialog.show();

        authService.retrievePassword(
                usernameOrEmail,
                this::handleResponse,
                this::handleError);
    }

    private void handleError(VolleyError error) {
        NetworkResponse response = error.networkResponse;
        progressDialog.dismiss();
        if(response != null && response.data != null){

            String responseText = new String(response.data);

            runOnUiThread(() -> {
                    errorLabel.setText(responseText);
                    errorLabel.setVisibility(View.VISIBLE);
            });
            Log.i(TAG, "Error " + response.statusCode + responseText);
        }
    }

    private void handleResponse(String response) {
        Log.i(TAG, response.toString());
        String toastMsg;
        toastMsg = response;
        Toast.makeText(getApplicationContext(), toastMsg, Toast.LENGTH_LONG).show();
        progressDialog.dismiss();
        redirectToConfirmRetrievePasswordActivity();
    }

    private void redirectToConfirmRetrievePasswordActivity() {
        Intent confirmRetrievePasswordActivityIntent = new Intent(this, ConfirmRetrievePasswordActivity.class);
        startActivity(confirmRetrievePasswordActivityIntent);
    }

    private void initViews() {
        usernameOrEmailInput = (EditText) findViewById(R.id.email_or_username_input);
        errorLabel = (TextView) findViewById(R.id.retrieve_password_error_label);
        retrievePasswordButton = (Button) findViewById(R.id.retrieve_password_button);

        toolbar = (Toolbar) findViewById(R.id.toolbar); // Attaching the layout to the toolbar object
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }
}
