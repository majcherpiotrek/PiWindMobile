package com.piotrmajcher.piwind.piwindmobile;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.piotrmajcher.piwind.piwindmobile.dto.MeteoStationTO;

import org.java_websocket.WebSocket;
import org.w3c.dom.Text;

import ua.naiksoftware.stomp.Stomp;
import ua.naiksoftware.stomp.client.StompClient;

public class MeteoStationDetailsActivity extends AppCompatActivity {

    // TODO Close the websocket connection when going back from this activity
    
    private static final String TAG = MeteoStationDetailsActivity.class.getName();
    private TextView meteoDataTextView;

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
        private String stationId;

        MeteoDataUpdateOperation(String stationId) {
            super();
            this.stationId = stationId;
        }

        @Override
        protected String doInBackground(String... params) {

            mStompClient = Stomp.over(WebSocket.class, "ws://10.0.2.2:8080/meteo-update/websocket");
            mStompClient.connect();

            mStompClient.topic("/update/updater-url").subscribe(topicMessage -> {
                Log.i(TAG, "New update: " + topicMessage.getPayload());
                updateMeteoDataText(topicMessage.getPayload());
            });

            mStompClient.send("/hello/start-update", this.stationId).subscribe();


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

    private void updateMeteoDataText(String updatedText) {
        runOnUiThread(() -> {
            meteoDataTextView.setText(updatedText);
        });
    }
}
