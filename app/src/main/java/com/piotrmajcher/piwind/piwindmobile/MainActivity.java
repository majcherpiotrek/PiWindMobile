package com.piotrmajcher.piwind.piwindmobile;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.google.firebase.messaging.FirebaseMessaging;
import com.piotrmajcher.piwind.piwindmobile.adapters.StationsListAdapter;
import com.piotrmajcher.piwind.piwindmobile.dto.MeteoStationTO;
import com.piotrmajcher.piwind.piwindmobile.rest.MeteoStationRestService;
import com.piotrmajcher.piwind.piwindmobile.rest.impl.MeteoStationRestServiceImpl;
import com.piotrmajcher.piwind.piwindmobile.util.impl.JsonToObjectParserImpl;


import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getName();
    public static final String SELECTED_STATION = "selected_station";
    private static final String ID_KEY = "id";
    private static final String NAME_KEY = "name";
    private static final String URL_KEY = "stationBaseURL";
    public static RequestQueue REQUEST_QUEUE;
    private ListView listView;
    private TextView textView;
    private StationsListAdapter listAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        MainActivity.REQUEST_QUEUE = Volley.newRequestQueue(getApplicationContext());
        initViews();
        getStationsListFromServer();
        handleIntent(getIntent());
    }

    public void goToMeteoStationDetails(MeteoStationTO meteoStationTO) {
        Intent intent = new Intent(this, MeteoStationDetailsActivity.class);
        intent.putExtra(SELECTED_STATION, meteoStationTO);
        startActivity(intent);
    }

    private void handleIntent(Intent intent) {
        if (intent != null && intent.getExtras() != null) {
            Bundle extras = intent.getExtras();
            if (extras.containsKey(ID_KEY) && extras.containsKey(NAME_KEY) && extras.containsKey(URL_KEY)) {
                MeteoStationTO meteoStationTO = new MeteoStationTO();
                meteoStationTO.setId(UUID.fromString(extras.getString(ID_KEY)));
                meteoStationTO.setName(intent.getExtras().getString(NAME_KEY));
                meteoStationTO.setStationBaseURL(intent.getExtras().getString(URL_KEY));
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
    }

    public void getStationsListFromServer() {
        MeteoStationRestService meteoStationRestService = new MeteoStationRestServiceImpl();
        meteoStationRestService.getMeteoStationsList(response -> {
            JsonToObjectParserImpl<MeteoStationTO> parser = new JsonToObjectParserImpl<>();
            try {
                List<MeteoStationTO> meteoStationTOs = parser.parseJSONArray(response, MeteoStationTO.class);
                for (MeteoStationTO meteoStationTO : meteoStationTOs) {
                    FirebaseMessaging.getInstance().subscribeToTopic(meteoStationTO.getId().toString());
                }
                listAdapter.updateStationsList(meteoStationTOs);
            } catch (JSONException e) {
                Log.e(TAG, "Failed to fetch the meteo stations list");
            }
        }, error -> Log.e(TAG, error.getMessage()));
    }
}
