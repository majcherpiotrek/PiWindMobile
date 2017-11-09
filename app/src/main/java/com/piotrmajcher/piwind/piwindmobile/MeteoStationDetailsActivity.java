package com.piotrmajcher.piwind.piwindmobile;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.piotrmajcher.piwind.piwindmobile.dto.MeteoStationTO;

public class MeteoStationDetailsActivity extends AppCompatActivity {

    private static final String TAG = MeteoStationDetailsActivity.class.getName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meteo_station_details);

        MeteoStationTO meteoStationTO = (MeteoStationTO) getIntent().getSerializableExtra(MainActivity.SELECTED_STATION);
        Log.i(TAG, meteoStationTO.toString());
    }
}
