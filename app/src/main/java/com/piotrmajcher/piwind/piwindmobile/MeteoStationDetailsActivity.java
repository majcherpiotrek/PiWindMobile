package com.piotrmajcher.piwind.piwindmobile;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParser;
import com.piotrmajcher.piwind.piwindmobile.dto.MeteoDataTO;
import com.piotrmajcher.piwind.piwindmobile.dto.MeteoStationTO;
import com.piotrmajcher.piwind.piwindmobile.models.MeteoData;
import com.piotrmajcher.piwind.piwindmobile.util.impl.JsonToObjectParserImpl;

import org.java_websocket.WebSocket;
import org.w3c.dom.Text;

import java.util.UUID;

import ua.naiksoftware.stomp.Stomp;
import ua.naiksoftware.stomp.client.StompClient;
import ua.naiksoftware.stomp.client.StompMessage;

public class MeteoStationDetailsActivity extends AppCompatActivity {

    // TODO Close the websocket connection when going back from this activity

    private static final String TAG = MeteoStationDetailsActivity.class.getName();
    private TextView meteoDataTextView;
    private MeteoData meteoData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meteo_station_details);
        meteoDataTextView = (TextView) findViewById(R.id.meteo_data_text_view);
        MeteoStationTO meteoStationTO = (MeteoStationTO) getIntent().getSerializableExtra(MainActivity.SELECTED_STATION);

        MeteoDataUpdateOperation meteoDataUpdateOperation = new MeteoDataUpdateOperation(meteoStationTO.getId());
        meteoDataUpdateOperation.execute();
    }

    private class MeteoDataUpdateOperation extends AsyncTask<String, Void, String> {
        private StompClient mStompClient;
        String TAG = MeteoDataUpdateOperation.class.getName();
        private UUID stationId;

        MeteoDataUpdateOperation(UUID stationId) {
            super();
            this.stationId = stationId;
        }

        @Override
        protected String doInBackground(String... params) {

            mStompClient = Stomp.over(WebSocket.class, "ws://10.0.2.2:8080/meteo-update/websocket");
            mStompClient.connect();

            mStompClient.topic("/update/updater-url").subscribe(topicMessage -> {
                Log.i(TAG, "New update: " + topicMessage.getPayload());
                JsonParser parser = new JsonParser();
                Gson gson = new GsonBuilder().create();
                MeteoDataTO meteoDataTO = gson.fromJson(topicMessage.getPayload(), MeteoDataTO.class);
                meteoData = new MeteoData(meteoDataTO);
                updateMeteoDataText();
            });

            mStompClient.send("/hello/start-update", this.stationId.toString()).subscribe();


            mStompClient.lifecycle().subscribe(lifecycleEvent -> {
                switch (lifecycleEvent.getType()) {

                    case OPENED:
                        Log.d(TAG, "Stomp connection opened");
                        break;

                    case ERROR:
                        Log.e(TAG, "Error", lifecycleEvent.getException());
                        break;

                    case CLOSED:
                        Log.d(TAG, "Stomp connection closed");
                        break;
                }
            });
            return "Executed";
        }

        @Override
        protected void onPostExecute(String result) {

        }
    }

    private void updateMeteoDataText() {
        runOnUiThread(() -> meteoDataTextView.setText(meteoData.toString()));
    }
}
