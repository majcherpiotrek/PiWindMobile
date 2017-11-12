package com.piotrmajcher.piwind.piwindmobile;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ListView;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.piotrmajcher.piwind.piwindmobile.adapters.StationsListAdapter;
import com.piotrmajcher.piwind.piwindmobile.dto.MeteoStationTO;
import com.piotrmajcher.piwind.piwindmobile.rest.MeteoStationRestService;
import com.piotrmajcher.piwind.piwindmobile.rest.impl.MeteoStationRestServiceImpl;
import com.piotrmajcher.piwind.piwindmobile.util.impl.JsonToObjectParserImpl;


import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getName();
    private static Context applicationContext;
    private static RequestQueue requestQueue;
    private ListView listView;
    public static final String SELECTED_STATION = "selected_station";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        MainActivity.applicationContext = getApplicationContext();
        MainActivity.requestQueue = Volley.newRequestQueue(MainActivity.applicationContext);

        List<MeteoStationTO> stationsList = new ArrayList<>();
        final StationsListAdapter listAdapter = new StationsListAdapter(this, stationsList);

        listView = (ListView) findViewById(R.id.stations_list);
        listView.setAdapter(listAdapter);
        listView.setOnItemClickListener((parent, view, position, id) -> {
            MeteoStationTO selectedStation = (MeteoStationTO) listAdapter.getItem(position);
            goToMeteoStationDetails(selectedStation);
        });

        MeteoStationRestService meteoStationRestService = new MeteoStationRestServiceImpl();
        meteoStationRestService.getMeteoStationsList(response -> {
            JsonToObjectParserImpl<MeteoStationTO> parser = new JsonToObjectParserImpl<>();
            try {
                listAdapter.updateStationsList(parser.parseJSONArray(response, MeteoStationTO.class));
            } catch (JSONException e) {
                Log.e(TAG, "Failed to fetch the meteo stations list");
            }
        }, error -> {
                Log.e(TAG, error.getMessage());
        });
    }

    public void goToMeteoStationDetails(MeteoStationTO meteoStationTO) {
        Intent intent = new Intent(this, MeteoStationDetailsActivity.class);
        intent.putExtra(SELECTED_STATION, meteoStationTO);
        startActivity(intent);
    }

    public static Context getAppContext() {
        return MainActivity.applicationContext;
    }

    public static RequestQueue getRequestQueue() {
        return MainActivity.requestQueue;
    }
}
