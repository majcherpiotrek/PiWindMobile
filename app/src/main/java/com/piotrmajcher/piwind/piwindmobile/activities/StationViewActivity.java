package com.piotrmajcher.piwind.piwindmobile.activities;

import android.content.Intent;
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

    private static final int MAX_WIND_LIMIT_MPS = 40;
    private static final String TAG = StationViewActivity.class.getName();

    private SectionsPageAdapter mSectionsPageAdapter;
    private ViewPager mViewPager;
    private MeteoStationTO meteoStationTO;
    private TabLayout tabLayout;
    private String token;
    private Toolbar toolbar;
    private RequestQueue requestQueue;
    private MeteoStationService meteoStationService;
    private String windUnit;
    private Double windFactor = 1.0;
    private String temperatureUnit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_station_view);
        Log.d(TAG, "onCreate: Starting...");

        requestQueue = ApplicationController.getInstance(getApplicationContext()).getRequestQueue();
        meteoStationService = new MeteoStationServiceImpl(requestQueue, getToken());

        setupToolbar();

        token = ApplicationController.getInstance(getApplicationContext()).getToken();
        meteoStationTO = (MeteoStationTO) getIntent().getSerializableExtra(StationsListActivity.SELECTED_STATION);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(meteoStationTO.getName());
        }

        setUpWindAndTemperatureUnits();
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


    private void setupToolbar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar); // Attaching the layout to the toolbar object
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
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

            case R.id.action_logout: {
                redirectToLoginActivity();
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

        Spinner windUnitsSpinner = (Spinner) dialogLayout.findViewById(R.id.wind_speed_units_spinner);
        Spinner temperatureUnitsSpinner = (Spinner) dialogLayout.findViewById(R.id.temperature_units_spinner);
        ArrayAdapter<CharSequence> adapterWindUnits = ArrayAdapter.createFromResource(this,
                R.array.wind_units_array, R.layout.spinner_row_layout);
        adapterWindUnits.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        windUnitsSpinner.setAdapter(adapterWindUnits);

        ArrayAdapter<CharSequence> adapterTemperatureUnits = ArrayAdapter.createFromResource(this,
                R.array.temperature_units_array, R.layout.spinner_row_layout);
        adapterTemperatureUnits.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        temperatureUnitsSpinner.setAdapter(adapterTemperatureUnits);
        temperatureUnitsSpinner.setSelection(0);

        builder.setView(dialogLayout)
                .setTitle("Change units")
                .setPositiveButton("Ok", (dialog, which) -> {;
                    String windUnit = (String) windUnitsSpinner.getSelectedItem();
                    String temperatureUnit = (String) temperatureUnitsSpinner.getSelectedItem();
                    saveChosenUnits(windUnit, temperatureUnit);
                    Log.i(TAG, "wind unit: " + windUnit + ", temperature unit: " + temperatureUnit);
                })
                .setNegativeButton("Cancel", (dialog, which) -> {

                });

        builder.create();
        builder.show();
    }

    private void saveChosenUnits(String windUnit, String temperatureUnit) {
        SharedPreferences sharedPreferences = getSharedPreferences(CONFIG.UNITS_PREFERENCES_KEY, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        this.windUnit = windUnit;
        if (this.windUnit.equals(getString(R.string.unit_knots))) {
            this.windFactor = CONFIG.MPS_TO_KTS;
        } else if (this.windUnit.equals(getString(R.string.unit_kmh))) {
            this.windFactor = CONFIG.MPS_TO_KMH;
        } else {
            this.windFactor = 1.0;
        }
        editor.putString(CONFIG.WIND_UNIT_KEY, windUnit);
        editor.putString(CONFIG.TEMPERATURE_UNIT_KEY, temperatureUnit);
        editor.apply();
    }

    private void setUpWindAndTemperatureUnits() {
        SharedPreferences sharedPreferences = getSharedPreferences(CONFIG.UNITS_PREFERENCES_KEY, MODE_PRIVATE);
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

    private void showSetWindAlertDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(this, R.style.AlertDialogStyle));
        LayoutInflater inflater = getLayoutInflater();

        View dialogLayout = inflater.inflate(R.layout.windlimit_dialog_layout, null);
        SeekBar seekBar = (SeekBar) dialogLayout.findViewById(R.id.wind_limit_seekbar);
        TextView selectedWindLimitTextView = (TextView) dialogLayout.findViewById(R.id.selected_wind_value);

        seekBar.setOnSeekBarChangeListener(getWindLimitSeekbarChangeListener(selectedWindLimitTextView));
        seekBar.setMax((int) (MAX_WIND_LIMIT_MPS * windFactor));
        selectedWindLimitTextView.setText(createWindLimitText(seekBar.getProgress()));


        builder.setView(dialogLayout)
                .setTitle("Setup wind alert")
                .setMessage("Choose the wind limit for alerts")
                .setPositiveButton("Ok", (dialog, which) -> {
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
        sb.append(" " + windUnit);
        return sb.toString();
    }


    private void setupSectionsPageAdapter(SectionsPageAdapter adapter) {
        adapter.addFragment(MeteoDetailsFragment.newInstance(meteoStationTO.getId().toString(), temperatureUnit, windUnit, windFactor), "Meteo");
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

    private void redirectToLoginActivity() {
        Intent loginActivityIntent = new Intent(this, LoginActivity.class);
        loginActivityIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(loginActivityIntent);
        this.finish();
    }
}
