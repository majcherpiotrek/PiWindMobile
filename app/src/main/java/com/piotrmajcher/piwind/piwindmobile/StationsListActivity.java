package com.piotrmajcher.piwind.piwindmobile;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.google.firebase.messaging.FirebaseMessaging;
import com.piotrmajcher.piwind.piwindmobile.adapters.StationsListAdapter;
import com.piotrmajcher.piwind.piwindmobile.dto.MeteoStationTO;
import com.piotrmajcher.piwind.piwindmobile.services.MeteoStationService;
import com.piotrmajcher.piwind.piwindmobile.services.impl.MeteoStationServiceImpl;
import com.piotrmajcher.piwind.piwindmobile.util.impl.JsonToObjectParserImpl;


import org.json.JSONException;

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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.stations_list);

        Intent intent = getIntent();
        if (!isUserAuthorized()) {
            redirectToLoginActivity(intent);
        } else {
            RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
            meteoStationService = new MeteoStationServiceImpl(requestQueue);
            initViews();
            getStationsListFromServer();
            handleIntent(intent);
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
        }, error -> Log.e(TAG, "Failed to fetch the meteo stations list"));
    }
}
