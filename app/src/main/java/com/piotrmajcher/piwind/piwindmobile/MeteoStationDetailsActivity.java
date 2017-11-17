package com.piotrmajcher.piwind.piwindmobile;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.piotrmajcher.piwind.piwindmobile.dto.MeteoDataTO;
import com.piotrmajcher.piwind.piwindmobile.dto.MeteoStationTO;
import com.piotrmajcher.piwind.piwindmobile.models.MeteoData;
import com.piotrmajcher.piwind.piwindmobile.websocket.MeteoDataUpdateListener;
import com.piotrmajcher.piwind.piwindmobile.websocket.SnapshotUpdateListener;


import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.WebSocket;
import java.text.DecimalFormat;

public class MeteoStationDetailsActivity extends AppCompatActivity {

    private static final String TAG = MeteoStationDetailsActivity.class.getName();
    private static final String UPDATE_METEO_URL = WEBSOCKET.BASE_URL + WEBSOCKET.METEO_UPDATE_ENDPOINT;
    private static final String UPDATE_SNAPSHOTS_URL = WEBSOCKET.BASE_URL + WEBSOCKET.SNAPSHOTS_UPDATE_ENDPOINT;
    private static DecimalFormat df = new DecimalFormat("0.0");
    private TextView windSpeedTextView;
    private TextView windBftTextView;
    private TextView windDirectionTextView;
    private TextView windDirectionDescTextView;
    private TextView temperatureDataTextView;
    private TextView temperatureDataDescTextView;
    private ImageView snapshotImageView;
    private MeteoData meteoData;
    private SnapshotUpdateListener snapshotUpdateListener;
    private MeteoDataUpdateListener meteoDataUpdateListener;
    private WebSocket webSocketMeteo;
    private WebSocket webSocketSnapshots;
    private MeteoStationTO meteoStationTO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meteo_station_details);

        initViews();
        meteoStationTO = (MeteoStationTO) getIntent().getSerializableExtra(MainActivity.SELECTED_STATION);
        if (super.getSupportActionBar() != null) {
            super.getSupportActionBar().setTitle(meteoStationTO.getName());
        }
        initUpdateListeners();
    }

    @Override
    protected void onStop() {
        super.onStop();
        closeWebSocketConnections(WEBSOCKET.ACTIVITY_STOPPED);
    }

    @Override
    protected void onStart() {
        super.onStart();
        initWebsocketConnections();
    }

    private void updateSnapshot(byte[] snapshot) {
        runOnUiThread(() -> {
            Bitmap bitmap = BitmapFactory.decodeByteArray(snapshot, 0, snapshot.length);
            snapshotImageView.setImageBitmap(bitmap);
        });
    }
    private void updateMeteoDataText(MeteoDataTO meteoDataTO) {
        meteoData = new MeteoData(meteoDataTO);
        final String windSpeed = df.format(meteoData.getWindSpeed()) + " mps";
        String[] split = meteoData.getWindDirectionDescription().split("-", 2);
        final String windDir = split[0].trim();
        split[1] = split[1].trim();
        split[1] = split[1].substring(0,1).toUpperCase() + split[1].substring(1);
        final String windDirDesc = split[1];
        final String windBft = meteoData.getBeaufortCategoryDescription();
        final String temperatureData = df.format(meteoData.getTemperature()) + "\u2103";
        final String temperatureDataDesc = meteoData.getTemperatureConditionsDescription();
        runOnUiThread(() -> {
            windSpeedTextView.setText(windSpeed);
            windDirectionTextView.setText(windDir);
            windDirectionDescTextView.setText(windDirDesc);
            windBftTextView.setText(windBft);
            temperatureDataTextView.setText(temperatureData);
            temperatureDataDescTextView.setText(temperatureDataDesc);
        });
    }

    private void initUpdateListeners() {
        snapshotUpdateListener = new SnapshotUpdateListener(
               meteoStationTO.getId(),
                this::updateSnapshot
        );
        meteoDataUpdateListener = new MeteoDataUpdateListener(
                meteoStationTO.getId(),
                this::updateMeteoDataText
        );
    }

    private void initWebsocketConnections() {
        OkHttpClient okHttpClient = new OkHttpClient();
        Request requestSnapshotUpdates = new Request.Builder().url(UPDATE_SNAPSHOTS_URL).build();
        Request requestMeteoDataUpdates = new Request.Builder().url(UPDATE_METEO_URL).build();

        webSocketMeteo = okHttpClient.newWebSocket(requestMeteoDataUpdates, meteoDataUpdateListener);
        webSocketSnapshots = okHttpClient.newWebSocket(requestSnapshotUpdates, snapshotUpdateListener);
        okHttpClient.dispatcher().executorService().shutdown();
        Log.i(TAG, "Websocket connections initialized!");
    }

    private void closeWebSocketConnections(String reason) {
        webSocketMeteo.close(WEBSOCKET.NORMAL_CLOSURE_STATUS, reason);
        webSocketSnapshots.close(WEBSOCKET.NORMAL_CLOSURE_STATUS, reason);
        Log.i(TAG, "Websocket connections closed. Reason: " + reason);
    }

    private void initViews() {
        windSpeedTextView = (TextView) findViewById(R.id.wind_speed);
        windBftTextView = (TextView) findViewById(R.id.wind_bft);
        windDirectionTextView = (TextView) findViewById(R.id.wind_dir);
        windDirectionDescTextView = (TextView) findViewById(R.id.wind_dir_desc);
        snapshotImageView = (ImageView) findViewById(R.id.snapshot);
        temperatureDataTextView = (TextView) findViewById(R.id.therm_data);
        temperatureDataDescTextView = (TextView) findViewById(R.id.therm_desc);
    }
}
