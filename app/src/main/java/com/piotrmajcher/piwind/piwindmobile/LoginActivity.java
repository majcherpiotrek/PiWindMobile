package com.piotrmajcher.piwind.piwindmobile;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.EditText;

import com.android.volley.toolbox.Volley;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = LoginActivity.class.getName();
    private EditText emailInput;
    private EditText passwordInput;
    private Button loginButton;
    private Button registerButton;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        initViews();

        loginButton.setOnClickListener(v -> handleLoginRequest(getIntent()));
    }

    private void handleLoginRequest(Intent intent) {
        String email = emailInput.getText().toString();
        String password = passwordInput.getText().toString();
        
        if (validateCredentials(email, password)) {
            saveCredentials(email, password);
            startActivityBasedOnIntent(intent);
        }
    }

    private void saveCredentials(String email, String password) {
        SharedPreferences sp = getSharedPreferences(CONFIG.LOGIN_PREFERENCES_KEY, MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(CONFIG.EMAIL_KEY, email);
        editor.putString(CONFIG.PASSWORD_KEY, password);
        editor.putBoolean(CONFIG.IS_USER_AUTHORIZED_KEY, true);
        editor.apply();
    }


    private void startActivityBasedOnIntent(Intent intent) {
        Bundle extras = intent.getExtras();
        Intent stationsListIntent = new Intent(this, StationsListActivity.class);
     
        if (extras != null && extras.containsKey(CONFIG.ID_KEY) && extras.containsKey(CONFIG.NAME_KEY) && extras.containsKey(CONFIG.URL_KEY)) {
            stationsListIntent.putExtra(CONFIG.ID_KEY, extras.getString(CONFIG.ID_KEY));
            stationsListIntent.putExtra(CONFIG.NAME_KEY, extras.getString(CONFIG.NAME_KEY));
            stationsListIntent.putExtra(CONFIG.URL_KEY, extras.getString(CONFIG.URL_KEY));
            startActivity(stationsListIntent);
        }

        startActivity(stationsListIntent);
        this.finish();
    }

    private boolean validateCredentials(String email, String password) {
        // TODO validate over the server
        if (email.equals("piotr@majcher.com") && password.equals("piotrek")) {
            return true;
        }
        return false;
    }

    private void initViews() {
        emailInput = (EditText) findViewById(R.id.email_input);
        passwordInput = (EditText) findViewById(R.id.password_input);
        loginButton = (Button) findViewById(R.id.login_button);
    }
}
