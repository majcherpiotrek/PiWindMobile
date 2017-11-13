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
import com.piotrmajcher.piwind.piwindmobile.updatehandlers.UpdateHandler;
import com.piotrmajcher.piwind.piwindmobile.websocket.MeteoDataUpdateListener;
import com.piotrmajcher.piwind.piwindmobile.websocket.SnapshotUpdateListener;


import java.util.UUID;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocketListener;
import okio.ByteString;

public class MeteoStationDetailsActivity extends AppCompatActivity {

    // TODO Close the websocket connection when going back from this activity

    private static final String TAG = MeteoStationDetailsActivity.class.getName();
    private static final String UPDATE_METEO_URL = WEBSOCKET.BASE_URL + WEBSOCKET.METEO_UPDATE_ENDPOINT;
    private static final String UPDATE_SNAPSHOTS_URL = WEBSOCKET.BASE_URL + WEBSOCKET.SNAPSHOTS_UPDATE_ENDPOINT;

    private TextView meteoDataTextView;
    private ImageView snapshotImageView;
    private MeteoData meteoData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meteo_station_details);
        meteoDataTextView = (TextView) findViewById(R.id.meteo_data_text_view);
        snapshotImageView = (ImageView) findViewById(R.id.snapshot);
        MeteoStationTO meteoStationTO = (MeteoStationTO) getIntent().getSerializableExtra(MainActivity.SELECTED_STATION);

        OkHttpClient okHttpClient = new OkHttpClient();

        SnapshotUpdateListener snapshotUpdateListener = new SnapshotUpdateListener(
                meteoStationTO.getId(),
                this::updateSnapshot
        );
        MeteoDataUpdateListener meteoDataUpdateListener = new MeteoDataUpdateListener(
                meteoStationTO.getId(),
                this::updateMeteoDataText
        );

        Request requestSnapshotUpdates = new Request.Builder().url(UPDATE_SNAPSHOTS_URL).build();
        Request requestMeteoDataUpdates = new Request.Builder().url(UPDATE_METEO_URL).build();

        okhttp3.WebSocket webSocketMeteo = okHttpClient.newWebSocket(requestMeteoDataUpdates, meteoDataUpdateListener);
        okhttp3.WebSocket webSocketSnapshots = okHttpClient.newWebSocket(requestSnapshotUpdates, snapshotUpdateListener);
        okHttpClient.dispatcher().executorService().shutdown();
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
