package com.piotrmajcher.piwind.piwindmobile.activities;

import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ContextThemeWrapper;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.firebase.messaging.FirebaseMessaging;
import com.piotrmajcher.piwind.piwindmobile.ApplicationController;
import com.piotrmajcher.piwind.piwindmobile.R;
import com.piotrmajcher.piwind.piwindmobile.adapters.SectionsPageAdapter;
import com.piotrmajcher.piwind.piwindmobile.config.CONFIG;
import com.piotrmajcher.piwind.piwindmobile.dto.MeteoStationTO;
import com.piotrmajcher.piwind.piwindmobile.fragments.ChartsFragment;
import com.piotrmajcher.piwind.piwindmobile.fragments.MeteoDetailsFragment;
import com.piotrmajcher.piwind.piwindmobile.services.MeteoStationService;
import com.piotrmajcher.piwind.piwindmobile.services.impl.MeteoStationServiceImpl;

public class StationViewActivity extends AppCompatActivity {

    private static final String TAG = StationViewActivity.class.getName();

    private SectionsPageAdapter mSectionsPageAdapter;
    private ViewPager mViewPager;
    private MeteoStationTO meteoStationTO;
    private TabLayout tabLayout;
    private String token;
    private Toolbar toolbar;
    private RequestQueue requestQueue;
    private MeteoStationService meteoStationService;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_station_view);
        Log.d(TAG, "onCreate: Starting...");

        requestQueue = ApplicationController.getInstance(getApplicationContext()).getRequestQueue();
        meteoStationService = new MeteoStationServiceImpl(requestQueue, getToken());

        toolbar = (Toolbar) findViewById(R.id.toolbar); // Attaching the layout to the toolbar object
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        token = ApplicationController.getInstance(getApplicationContext()).getToken();
        meteoStationTO = (MeteoStationTO) getIntent().getSerializableExtra(StationsListActivity.SELECTED_STATION);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(meteoStationTO.getName());
        }

        mSectionsPageAdapter = new SectionsPageAdapter(getSupportFragmentManager());
        setupSectionsPageAdapter(mSectionsPageAdapter);

        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPageAdapter);
        mViewPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {

            @Override
            public void onPageSelected(int position) {
                if (position == 0) {
                    // Meteo details view only portrait
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                } else {
                    // Charts screen can be landscape
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_FULL_SENSOR);
                }
            }
        });


        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if(newConfig.orientation == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT){
            tabLayout.setVisibility(View.VISIBLE);
        }
        else {
            tabLayout.setVisibility(View.GONE);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_station_view, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings: {
                Log.i(TAG, "Settings selected!");
                return true;
            }
            case R.id.action_set_alert: {
                showSetWindAlertDialog();
                Log.i(TAG, "Set alert selected!");
                return true;
            }

            default: {
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);
            }
        }
    }

    private void showSetWindAlertDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(this, R.style.AlertDialogStyle));
        LayoutInflater inflater = getLayoutInflater();
        View dialogLayout = inflater.inflate(R.layout.windlimit_dialog_layout, null);
        builder.setView(dialogLayout)
                .setTitle("Setup wind alert")
                .setMessage("Choose the wind limit for alerts")
                .setPositiveButton("Ok", (dialog, which) -> {
                    SeekBar seekBar = (SeekBar) dialogLayout.findViewById(R.id.wind_limit_seekbar);
                    Log.i(TAG, "Fetched value: " + seekBar.getProgress());
                    susbscribeToFirebase();
                    requestNotifications(seekBar.getProgress());
                    // TODO sign to firebase topic and send request for notifications, update icon to yellow
                })
                .setNeutralButton("Remove alert", (dialog, which) -> {
                    // TODO usign from firebase topic and send request to cancel notifications, update icon to gray
                })
                .setNegativeButton("Cancel", (dialog, which) -> {

                });
        SeekBar seekBar = (SeekBar) dialogLayout.findViewById(R.id.wind_limit_seekbar);
        TextView selectedWindLimitTextView = (TextView) dialogLayout.findViewById(R.id.selected_wind_value);
        selectedWindLimitTextView.setText(createWindLimitText(seekBar.getProgress()));
        seekBar.setOnSeekBarChangeListener(getWindLimitSeekbarChangeListener(selectedWindLimitTextView));

        builder.create();
        builder.show();
    }

    private void requestNotifications(Integer minWindLimit) {
        meteoStationService.requestNotifications(meteoStationTO.getId(), minWindLimit,
                response -> Log.i(TAG, response),
                error -> {

                });
    }

    private void susbscribeToFirebase() {
        SharedPreferences sharedPreferences = getSharedPreferences(CONFIG.LOGIN_PREFERENCES_KEY, MODE_PRIVATE);
        String username = sharedPreferences.getString(CONFIG.USERNAME, null);
        if (username != null) {
            StringBuilder sb = new StringBuilder();
            sb.append(meteoStationTO.getId().toString());
            sb.append(username);
            FirebaseMessaging.getInstance().subscribeToTopic(sb.toString());
        }
    }

    @NonNull
    private SeekBar.OnSeekBarChangeListener getWindLimitSeekbarChangeListener(final TextView selectedWindLimitTextView) {
        return new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                selectedWindLimitTextView.setText(createWindLimitText(progress));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        };
    }

    @NonNull
    private String createWindLimitText(int progress) {
        StringBuilder sb = new StringBuilder();
        sb.append("Wind limit: ");
        sb.append(String.valueOf(progress));
        sb.append(" mps");
        return sb.toString();
    }


    private void setupSectionsPageAdapter(SectionsPageAdapter adapter) {
        adapter.addFragment(MeteoDetailsFragment.newInstance(meteoStationTO.getId().toString()), "Meteo");
        RequestQueue requestQueue = ApplicationController.getInstance(getApplicationContext()).getRequestQueue();
        adapter.addFragment(ChartsFragment.newInstance(meteoStationTO.getId().toString(), token, requestQueue), "Charts");
    }

    private String getToken() {
        return ApplicationController.getInstance(getApplicationContext()).getToken();
    }
}
