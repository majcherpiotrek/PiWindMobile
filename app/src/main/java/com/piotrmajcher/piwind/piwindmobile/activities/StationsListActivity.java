package com.piotrmajcher.piwind.piwindmobile.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.NetworkResponse;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.google.firebase.messaging.FirebaseMessaging;
import com.piotrmajcher.piwind.piwindmobile.ApplicationController;
import com.piotrmajcher.piwind.piwindmobile.config.CONFIG;
import com.piotrmajcher.piwind.piwindmobile.R;
import com.piotrmajcher.piwind.piwindmobile.adapters.StationsListAdapter;
import com.piotrmajcher.piwind.piwindmobile.config.REST;
import com.piotrmajcher.piwind.piwindmobile.dto.MeteoStationTO;
import com.piotrmajcher.piwind.piwindmobile.services.AuthService;
import com.piotrmajcher.piwind.piwindmobile.services.MeteoStationService;
import com.piotrmajcher.piwind.piwindmobile.services.impl.AuthServiceImpl;
import com.piotrmajcher.piwind.piwindmobile.services.impl.MeteoStationServiceImpl;
import com.piotrmajcher.piwind.piwindmobile.util.impl.JsonToObjectParserImpl;


import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


public class StationsListActivity extends AppCompatActivity {

    private static final String TAG = StationsListActivity.class.getName();
    public static final String SELECTED_STATION = "selected_station";

    private ListView listView;
    private TextView textView;
    private StationsListAdapter listAdapter;
    private MeteoStationService meteoStationService;
    private String token;
    private Intent intent;
    private AuthService authService;
    private RequestQueue requestQueue;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.stations_list);

        requestQueue = ApplicationController.getInstance(getApplicationContext()).getRequestQueue();
        authService = new AuthServiceImpl(requestQueue);
        intent = getIntent();
        token = getToken();
        if (!isUserAuthorized()) {
            redirectToLoginActivity(intent);
        } else if (token == null){
            SharedPreferences sharedPreferences = getSharedPreferences(CONFIG.LOGIN_PREFERENCES_KEY, MODE_PRIVATE);
            String username = sharedPreferences.getString(CONFIG.USERNAME, null);
            String password = sharedPreferences.getString(CONFIG.PASSWORD_KEY, null);
            try {
                if (username == null || password == null) {
                    throw new Exception("No user credentials saved");
                }

                authService.login(
                        username,
                        password,
                        this::handleResponse,
                        error -> {
                            flushSavedCredentials();
                            redirectToLoginActivity(intent);
                        });

            } catch (Exception e) {
                redirectToLoginActivity(intent);
            }
        } else {
            initActivity();
        }
    }

    private void flushSavedCredentials() {
        SharedPreferences sharedPreferences = getSharedPreferences(CONFIG.LOGIN_PREFERENCES_KEY, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove(CONFIG.PASSWORD_KEY);
        editor.remove(CONFIG.USERNAME);
        editor.remove(CONFIG.IS_USER_AUTHORIZED_KEY);
    }

    private void initActivity() {
        meteoStationService = new MeteoStationServiceImpl(requestQueue, token);
        initViews();
        getStationsListFromServer();
        handleIntent(intent);
    }

    private void handleResponse(JSONObject response) {
        try {
            token = response.getString(CONFIG.TOKEN);
            ApplicationController.getInstance(getApplicationContext()).setToken(token);
            initActivity();
        } catch (JSONException e) {
            redirectToLoginActivity(intent);
        }
    }

    private void redirectToLoginActivity(Intent intent) {
        Intent loginActivityIntent = new Intent(this, LoginActivity.class);
        if (intent != null) {
            addExtrasToIntent(intent.getExtras(), loginActivityIntent);
            startActivity(loginActivityIntent);
            this.finish();
        }
    }

    private void addExtrasToIntent(Bundle extras, Intent intent) {
        if (extras != null &&
                extras.containsKey(CONFIG.ID_KEY) &&
                extras.containsKey(CONFIG.NAME_KEY) &&
                extras.containsKey(CONFIG.URL_KEY)) {
            intent.putExtra(CONFIG.ID_KEY, extras.getString(CONFIG.ID_KEY));
            intent.putExtra(CONFIG.NAME_KEY, extras.getString(CONFIG.NAME_KEY));
            intent.putExtra(CONFIG.URL_KEY, extras.getString(CONFIG.URL_KEY));
        }
    }

    private boolean isUserAuthorized() {
        SharedPreferences sharedPreferences = getSharedPreferences(CONFIG.LOGIN_PREFERENCES_KEY, MODE_PRIVATE);
        return sharedPreferences.getBoolean(CONFIG.IS_USER_AUTHORIZED_KEY, false);
    }

    private String getToken() {
        return ApplicationController.getInstance(getApplicationContext()).getToken();
    }

    public void goToMeteoStationDetails(MeteoStationTO meteoStationTO) {
        Intent intent = new Intent(this, StationViewActivity.class);
        intent.putExtra(SELECTED_STATION, meteoStationTO);
        startActivity(intent);
    }

    private void handleIntent(Intent intent) {
        if (intent != null && intent.getExtras() != null) {
            Bundle extras = intent.getExtras();
            if (extras.containsKey(CONFIG.ID_KEY) && extras.containsKey(CONFIG.NAME_KEY) && extras.containsKey(CONFIG.URL_KEY)) {
                MeteoStationTO meteoStationTO = new MeteoStationTO();
                meteoStationTO.setId(UUID.fromString(extras.getString(CONFIG.ID_KEY)));
                meteoStationTO.setName(intent.getExtras().getString(CONFIG.NAME_KEY));
                meteoStationTO.setStationBaseURL(intent.getExtras().getString(CONFIG.URL_KEY));
                goToMeteoStationDetails(meteoStationTO);
            }
        }
    }

    public void initViews() {
        textView = (TextView) findViewById(R.id.loading_stations);
        listView = (ListView) findViewById(R.id.stations_list);

        List<MeteoStationTO> stationsList = new ArrayList<>();
        listAdapter = new StationsListAdapter(this, stationsList);

        listView.setAdapter(listAdapter);
        listView.setOnItemClickListener((parent, view, position, id) -> {
            MeteoStationTO selectedStation = (MeteoStationTO) listAdapter.getItem(position);
            goToMeteoStationDetails(selectedStation);
        });

        SwipeRefreshLayout refresher = (SwipeRefreshLayout) findViewById(R.id.swiperefresh);
        refresher.setOnRefreshListener(() -> {
            getStationsListFromServer();
            refresher.setRefreshing(false);
        });
    }

    public void getStationsListFromServer() {
        meteoStationService.getMeteoStationsList(response -> {
            JsonToObjectParserImpl<MeteoStationTO> parser = new JsonToObjectParserImpl<>();
            try {
                List<MeteoStationTO> meteoStationTOs = parser.parseJSONArray(response, MeteoStationTO.class);
                for (MeteoStationTO meteoStationTO : meteoStationTOs) {
                    FirebaseMessaging.getInstance().subscribeToTopic(meteoStationTO.getId().toString());
                }
                listAdapter.updateStationsList(meteoStationTOs);
            } catch (JSONException e) {
                Log.e(TAG, "Failed to parse the meteo stations list");
            }
        }, error -> {
            NetworkResponse response = error.networkResponse;
            String errMsg = "Failed to fetch the meteo stations list";
            if (response != null && response.data != null) {
                errMsg += " : ";
                errMsg += new String(response.data);
            }
            Log.e(TAG, errMsg);

            if (response != null && response.statusCode == REST.UNAUTHORIZED) {
                SharedPreferences sharedPreferences = getSharedPreferences(CONFIG.LOGIN_PREFERENCES_KEY, MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString(CONFIG.USERNAME, null);
                editor.putString(CONFIG.PASSWORD_KEY, null);
                editor.putBoolean(CONFIG.IS_USER_AUTHORIZED_KEY, false);
                redirectToLoginActivity(intent);
            }
        });
    }
}
