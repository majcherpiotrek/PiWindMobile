package com.piotrmajcher.piwind.piwindmobile;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.piotrmajcher.piwind.piwindmobile.dto.MeteoDataTO;
import com.piotrmajcher.piwind.piwindmobile.dto.MeteoStationTO;
import com.piotrmajcher.piwind.piwindmobile.models.MeteoData;


import java.util.UUID;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocketListener;
import okio.ByteString;

public class MeteoStationDetailsActivity extends AppCompatActivity {

    // TODO Close the websocket connection when going back from this activity

    private static final String TAG = MeteoStationDetailsActivity.class.getName();
    private TextView meteoDataTextView;
    private ImageView snapshotImageView;
    private MeteoData meteoData;
    private OkHttpClient okHttpClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meteo_station_details);
        meteoDataTextView = (TextView) findViewById(R.id.meteo_data_text_view);
        snapshotImageView = (ImageView) findViewById(R.id.snapshot);
        MeteoStationTO meteoStationTO = (MeteoStationTO) getIntent().getSerializableExtra(MainActivity.SELECTED_STATION);

        okHttpClient = new OkHttpClient();

        SnapshotUpdateListener snapshotUpdateListener = new SnapshotUpdateListener(meteoStationTO.getId());
        MeteoDataUpdateListener meteoDataUpdateListener = new MeteoDataUpdateListener(meteoStationTO.getId());

        Request requestSnapshotUpdates = new Request.Builder().url("ws://10.0.2.2:8080/snapshots").build();
        Request requestMeteoDataUpdates = new Request.Builder().url("ws://10.0.2.2:8080/meteo").build();

        okhttp3.WebSocket webSocketMeteo = okHttpClient.newWebSocket(requestMeteoDataUpdates, meteoDataUpdateListener);
        okhttp3.WebSocket webSocketSnapshots = okHttpClient.newWebSocket(requestSnapshotUpdates, snapshotUpdateListener);
        okHttpClient.dispatcher().executorService().shutdown();
    }

    private final class MeteoDataUpdateListener extends WebSocketListener {
        private static final int NORMAL_CLOSURE_STATUS = 1000;
        private UUID stationId;

        MeteoDataUpdateListener(UUID stationId) { this.stationId = stationId; }

        @Override
        public void onOpen(okhttp3.WebSocket webSocket, Response response) {
            webSocket.send(stationId.toString());
        }

        @Override
        public void onMessage(okhttp3.WebSocket webSocket, String updatedMeteoData) {
            Log.i(TAG, "New update: " + updatedMeteoData);
            Gson gson = new GsonBuilder().create();
            MeteoDataTO meteoDataTO = gson.fromJson(updatedMeteoData, MeteoDataTO.class);
            updateMeteoDataText(meteoDataTO);
        }

        @Override
        public void onClosing(okhttp3.WebSocket webSocket, int code, String reason) {
            webSocket.close(NORMAL_CLOSURE_STATUS, null);
            Log.i(TAG, "Closing " + code + " " + reason);
        }

        @Override
        public void onClosed(okhttp3.WebSocket webSocket, int code, String reason) {
            Log.i(TAG, "Closed " + code + " " + reason);
        }

        @Override
        public void onFailure(okhttp3.WebSocket webSocket, Throwable t, Response response) {
            Log.i(TAG, "Error " + t.getMessage());
        }
    }

    private final class SnapshotUpdateListener extends WebSocketListener {
        private static final int NORMAL_CLOSURE_STATUS = 1000;
        private UUID stationId;

        SnapshotUpdateListener(UUID stationId) {
            this.stationId = stationId;
        }
        @Override
        public void onOpen(okhttp3.WebSocket webSocket, Response response) {
            webSocket.send(stationId.toString());
        }

        @Override
        public void onMessage(okhttp3.WebSocket webSocket, String message) {
            Log.i(TAG, "Received message: " + message);
        }

        @Override
        public void onMessage(okhttp3.WebSocket webSocket, ByteString bytes) {
            Log.i(TAG, "Received bytestring message: " + bytes.size());
            updateSnapshot(bytes.toByteArray());
        }

        @Override
        public void onClosing(okhttp3.WebSocket webSocket, int code, String reason) {
            webSocket.close(NORMAL_CLOSURE_STATUS, null);
            Log.i(TAG, "Closing " + code + " " + reason);
        }

        @Override
        public void onClosed(okhttp3.WebSocket webSocket, int code, String reason) {
            Log.i(TAG, "Closed " + code + " " + reason);
        }

        @Override
        public void onFailure(okhttp3.WebSocket webSocket, Throwable t, Response response) {
            Log.i(TAG, "Error " + t.getMessage());
        }
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
}
