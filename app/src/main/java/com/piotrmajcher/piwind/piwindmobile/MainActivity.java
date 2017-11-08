package com.piotrmajcher.piwind.piwindmobile;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.piotrmajcher.piwind.piwindmobile.dto.MeteoStationTO;
import com.piotrmajcher.piwind.piwindmobile.rest.MeteoStationRestService;
import com.piotrmajcher.piwind.piwindmobile.rest.impl.MeteoStationRestServiceImpl;
import com.piotrmajcher.piwind.piwindmobile.util.impl.JsonToObjectParserImpl;

import org.java_websocket.WebSocket;
import org.json.JSONArray;
import org.json.JSONException;

import java.util.List;

import ua.naiksoftware.stomp.Stomp;
import ua.naiksoftware.stomp.client.StompClient;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getName();
    private static Context applicationContext;
    private static RequestQueue requestQueue;
    private MeteoStationRestService meteoStationRestService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MainActivity.applicationContext = getApplicationContext();
        MainActivity.requestQueue = Volley.newRequestQueue(MainActivity.applicationContext);

        meteoStationRestService = new MeteoStationRestServiceImpl();
        meteoStationRestService.getMeteoStationsList(new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                JsonToObjectParserImpl<MeteoStationTO> parser = new JsonToObjectParserImpl<>();
                try {
                    List<MeteoStationTO> meteoStationTOList = parser.parseJSONArray(response, MeteoStationTO.class);
                    Log.i(TAG, "Meteo stations list: " + meteoStationTOList.toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        setContentView(R.layout.activity_main);
    }

    private class LongOperation extends AsyncTask<String, Void, String> {
        private StompClient mStompClient;
        String TAG="LongOperation";
        private String stationId;

        LongOperation(String stationId) {
            super();
            this.stationId = stationId;
        }

        @Override
        protected String doInBackground(String... params) {

            mStompClient = Stomp.over(WebSocket.class, "ws://10.0.2.2:8080/meteo-update/websocket");
            mStompClient.connect();

            mStompClient.topic("/update/updater-url").subscribe(topicMessage -> {
                Log.i(TAG, "New update: " + topicMessage.getPayload());
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

    public static Context getAppContext() {
        return MainActivity.applicationContext;
    }

    public static RequestQueue getRequestQueue() {
        return MainActivity.requestQueue;
    }
}
