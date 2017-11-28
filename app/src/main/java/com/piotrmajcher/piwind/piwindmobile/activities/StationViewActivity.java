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
import android.widget.ArrayAdapter;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.android.volley.RequestQueue;
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
        changeAlertIconState(isUserAlreadySubscribedToStationTopic(meteoStationTO));
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_change_units: {
                Log.i(TAG, "Change units selected!");
                showChangeUnitsAlertDialog();
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

    private void showChangeUnitsAlertDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(this, R.style.AlertDialogStyle));
        LayoutInflater inflater = getLayoutInflater();
        View dialogLayout = inflater.inflate(R.layout.change_units_dialog_layout, null);
        builder.setView(dialogLayout)
                .setTitle("Change units")
                .setPositiveButton("Ok", (dialog, which) -> {

                })
                .setNegativeButton("Cancel", (dialog, which) -> {

                });

        Spinner windUnitsSpinner = (Spinner) dialogLayout.findViewById(R.id.wind_speed_units_spinner);
        Spinner temperatureUnitsSpinner = (Spinner) dialogLayout.findViewById(R.id.temperature_units_spinner);

        ArrayAdapter<CharSequence> adapterWindUnits = ArrayAdapter.createFromResource(this,
                R.array.wind_units_array, android.R.layout.simple_spinner_item);
        adapterWindUnits.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        windUnitsSpinner.setAdapter(adapterWindUnits);

        ArrayAdapter<CharSequence> adapterTemperatureUnits = ArrayAdapter.createFromResource(this,
                R.array.temperature_units_array, android.R.layout.simple_spinner_item);
        adapterTemperatureUnits.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        temperatureUnitsSpinner.setAdapter(adapterTemperatureUnits);

        builder.create();
        builder.show();
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
                    if (!isUserAlreadySubscribedToStationTopic(meteoStationTO)) {
                        susbscribeToFirebase(meteoStationTO);
                        saveStationSubscriptionInPreferences(meteoStationTO);
                    }

                    // Can be either new notification or update the wind limit of exisitng one
                    requestNotifications(meteoStationTO, seekBar.getProgress());
                })
                .setNeutralButton("Remove alert", (dialog, which) -> {
                    if (isUserAlreadySubscribedToStationTopic(meteoStationTO)) {
                        unsubscribeFromFirebase(meteoStationTO);
                        cancelNotifications(meteoStationTO);
                        removestationSubscriptionFromPreferences(meteoStationTO);
                    }
                })
                .setNegativeButton("Cancel", (dialog, which) -> {
                    // Does nothing
                });
        SeekBar seekBar = (SeekBar) dialogLayout.findViewById(R.id.wind_limit_seekbar);
        TextView selectedWindLimitTextView = (TextView) dialogLayout.findViewById(R.id.selected_wind_value);
        selectedWindLimitTextView.setText(createWindLimitText(seekBar.getProgress()));
        seekBar.setOnSeekBarChangeListener(getWindLimitSeekbarChangeListener(selectedWindLimitTextView));

        builder.create();
        builder.show();
    }

    private void cancelNotifications(MeteoStationTO meteoStationTO) {
        meteoStationService.cancelNotifications(meteoStationTO.getId(),
                response -> {
                    Log.i(TAG, "Cancel notifications response: " + response);
                    changeAlertIconState(false);
                }, error -> {

                });
    }

    private void changeAlertIconState(boolean enabled) {
        if (enabled) {
            toolbar.getMenu().getItem(0).setIcon(R.drawable.ic_alert_enabled);
        } else {
            toolbar.getMenu().getItem(0).setIcon(R.drawable.ic_alert_disabled);
        }
    }

    private void requestNotifications(MeteoStationTO meteoStationTO, Integer minWindLimit) {
        meteoStationService.requestNotifications(meteoStationTO.getId(), minWindLimit,
                response -> {
                    Log.i(TAG, "Request notifications response: " + response);
                    changeAlertIconState(true);
                },
                error -> {

                });
    }

    private void susbscribeToFirebase(MeteoStationTO meteoStationTO) {
        String username = getLoggedInUsername();
        if (username != null) {
            StringBuilder sb = new StringBuilder();
            sb.append(meteoStationTO.getId().toString());
            sb.append(username);
            FirebaseMessaging.getInstance().subscribeToTopic(sb.toString());
        }
    }

    private void unsubscribeFromFirebase(MeteoStationTO meteoStationTO) {
        String username = getLoggedInUsername();
        if (username != null) {
            StringBuilder sb = new StringBuilder();
            sb.append(meteoStationTO.getId().toString());
            sb.append(username);
            FirebaseMessaging.getInstance().unsubscribeFromTopic(sb.toString());
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

    private String getLoggedInUsername() {
        SharedPreferences sharedPreferences = getSharedPreferences(CONFIG.LOGIN_PREFERENCES_KEY, MODE_PRIVATE);
        return sharedPreferences.getString(CONFIG.USERNAME, null);
    }

    private boolean isUserAlreadySubscribedToStationTopic(MeteoStationTO meteoStationTO) {
        SharedPreferences sharedPreferences = getSharedPreferences(CONFIG.NOTIFICATIONS_PREFERENCES_KEY, MODE_PRIVATE);
        return sharedPreferences.getBoolean(meteoStationTO.getId().toString(), false);
    }

    private void saveStationSubscriptionInPreferences(MeteoStationTO meteoStationTO) {
        SharedPreferences sharedPreferences = getSharedPreferences(CONFIG.NOTIFICATIONS_PREFERENCES_KEY, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(meteoStationTO.getId().toString(), true);
        editor.apply();
    }

    private void removestationSubscriptionFromPreferences(MeteoStationTO meteoStationTO) {
        SharedPreferences sharedPreferences = getSharedPreferences(CONFIG.NOTIFICATIONS_PREFERENCES_KEY, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove(meteoStationTO.getId().toString());
        editor.apply();
    }
}
