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


import java.util.UUID;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.WebSocket;

public class MeteoStationDetailsActivity extends AppCompatActivity {

    private static final String TAG = MeteoStationDetailsActivity.class.getName();
    private static final String UPDATE_METEO_URL = WEBSOCKET.BASE_URL + WEBSOCKET.METEO_UPDATE_ENDPOINT;
    private static final String UPDATE_SNAPSHOTS_URL = WEBSOCKET.BASE_URL + WEBSOCKET.SNAPSHOTS_UPDATE_ENDPOINT;

    private TextView meteoDataTextView;
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
        runOnUiThread(() -> meteoDataTextView.setText(meteoData.toString()));
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
        meteoDataTextView = (TextView) findViewById(R.id.meteo_data_text_view);
        snapshotImageView = (ImageView) findViewById(R.id.snapshot);
    }
}
