package com.piotrmajcher.piwind.piwindmobile.fragments;

import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.piotrmajcher.piwind.piwindmobile.R;
import com.piotrmajcher.piwind.piwindmobile.config.CONFIG;
import com.piotrmajcher.piwind.piwindmobile.config.WEBSOCKET;
import com.piotrmajcher.piwind.piwindmobile.dto.MeteoDataTO;
import com.piotrmajcher.piwind.piwindmobile.models.MeteoData;
import com.piotrmajcher.piwind.piwindmobile.websocket.MeteoDataUpdateListener;
import com.piotrmajcher.piwind.piwindmobile.websocket.SnapshotUpdateListener;

import java.text.DecimalFormat;
import java.util.UUID;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.WebSocket;

import static android.content.Context.MODE_PRIVATE;

public class MeteoDetailsFragment extends Fragment {
    private static final String TAG = MeteoDetailsFragment.class.getName();
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
    private SnapshotUpdateListener snapshotUpdateListener;
    private MeteoDataUpdateListener meteoDataUpdateListener;
    private WebSocket webSocketMeteo;
    private WebSocket webSocketSnapshots;
    private String meteoStationId;
    private View view;
    private String windUnit;
    private String temperatureUnit;
    private Double windFactor;
    private MeteoData meteoData;

    public static MeteoDetailsFragment newInstance(String meteoStationId) {
        MeteoDetailsFragment f = new MeteoDetailsFragment();
        Bundle args = new Bundle();
        args.putSerializable("meteoStationId", meteoStationId);
        f.setArguments(args);
        return f;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle args = getArguments();
        if (args != null  && args.getString("meteoStationId") != null) {
            meteoStationId = args.getString("meteoStationId");

        } else {
            throw new RuntimeException("Unexpected state occured. Null station object passed to station details view.");
        }


        setUpWindAndTemperatureUnits();
    }

    private void setUpWindAndTemperatureUnits() {
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(CONFIG.UNITS_PREFERENCES_KEY, MODE_PRIVATE);
        windUnit = sharedPreferences.getString(CONFIG.WIND_UNIT_KEY,null);
        temperatureUnit = sharedPreferences.getString(CONFIG.TEMPERATURE_UNIT_KEY, null);
        if (windUnit == null || temperatureUnit == null) {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            windUnit = getString(R.string.unit_knots);
            windFactor = CONFIG.MPS_TO_KTS;
            temperatureUnit = getString(R.string.unit_celsius);
            editor.putString(CONFIG.WIND_UNIT_KEY, windUnit);
            editor.putString(CONFIG.TEMPERATURE_UNIT_KEY, temperatureUnit);
            editor.apply();
        } else {
            if (this.windUnit.equals(getString(R.string.unit_knots))) {
                this.windFactor = CONFIG.MPS_TO_KTS;
            } else if (this.windUnit.equals(getString(R.string.unit_kmh))) {
                this.windFactor = CONFIG.MPS_TO_KMH;
            } else {
                this.windFactor = 1.0;
            }
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        initWebsocketConnections();
    }

    @Override
    public void onPause() {
        super.onPause();
        closeWebSocketConnections(WEBSOCKET.FRAGMENT_PAUSED);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.meteo_details, container, false);
        initViews();
        initUpdateListeners();
        SwipeRefreshLayout refresher = (SwipeRefreshLayout) view.findViewById(R.id.meteo_refresher);
        refresher.setOnRefreshListener(() -> {
            refreshConnection();
            refresher.setRefreshing(false);
        });
        return view;
    }

    private void refreshConnection() {
        closeWebSocketConnections(WEBSOCKET.REFRESH);
        initWebsocketConnections();
    }
    private void updateSnapshot(byte[] snapshot) {
        Bitmap bitmap = BitmapFactory.decodeByteArray(snapshot, 0, snapshot.length);

        getActivity().runOnUiThread(() -> {
            snapshotImageView.setImageBitmap(bitmap);
        });
    }
    private void updateMeteoData(MeteoDataTO meteoDataTO) {
        meteoData = new MeteoData(meteoDataTO);
        updateMeteoDataText();
    }

    private void updateMeteoDataText() {
        if (meteoData != null) {
            setUpWindAndTemperatureUnits();
            final String windSpeed = df.format(meteoData.getWindSpeed() * windFactor) + " " + windUnit;
            String[] split = meteoData.getWindDirectionDescription().split("-", 2);
            final String windDir = split[0].trim();
            split[1] = split[1].trim();
            split[1] = split[1].substring(0,1).toUpperCase() + split[1].substring(1);
            final String windDirDesc = split[1];
            final String windBft = meteoData.getBeaufortCategoryDescription();
            Double temp = meteoData.getTemperature();
            if (temperatureUnit.equals(getString(R.string.unit_fahrenheit))) {
                temp = temp * CONFIG.CELSIUS_TO_FAHRENHEIT_MULTIPLIET + CONFIG.CELSIUS_TO_FAHRENHEIT_ADDER;
            }
            final String temperatureData = df.format(temp) + temperatureUnit;
            final String temperatureDataDesc = meteoData.getTemperatureConditionsDescription();

            getActivity().runOnUiThread(() -> {
                windSpeedTextView.setText(windSpeed);
                windDirectionTextView.setText(windDir);
                windDirectionDescTextView.setText(windDirDesc);
                windBftTextView.setText(windBft);
                temperatureDataTextView.setText(temperatureData);
                temperatureDataDescTextView.setText(temperatureDataDesc);
            });
        }
    }

    private void initUpdateListeners() {
        snapshotUpdateListener = new SnapshotUpdateListener(
                UUID.fromString(meteoStationId.trim()),
                this::updateSnapshot
        );
        meteoDataUpdateListener = new MeteoDataUpdateListener(
                UUID.fromString(meteoStationId.trim()),
                this::updateMeteoData
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
        windSpeedTextView = (TextView) view.findViewById(R.id.wind_speed);
        windBftTextView = (TextView) view.findViewById(R.id.wind_bft);
        windDirectionTextView = (TextView) view.findViewById(R.id.wind_dir);
        windDirectionDescTextView = (TextView) view.findViewById(R.id.wind_dir_desc);
        snapshotImageView = (ImageView) view.findViewById(R.id.snapshot);
        temperatureDataTextView = (TextView) view.findViewById(R.id.therm_data);
        temperatureDataDescTextView = (TextView) view.findViewById(R.id.therm_desc);
    }


}
