package com.piotrmajcher.piwind.piwindmobile.activities;

import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.piotrmajcher.piwind.piwindmobile.ApplicationController;
import com.piotrmajcher.piwind.piwindmobile.R;
import com.piotrmajcher.piwind.piwindmobile.adapters.SectionsPageAdapter;
import com.piotrmajcher.piwind.piwindmobile.config.CONFIG;
import com.piotrmajcher.piwind.piwindmobile.dto.MeteoStationTO;
import com.piotrmajcher.piwind.piwindmobile.tabfragments.ChartsFragment;
import com.piotrmajcher.piwind.piwindmobile.tabfragments.MeteoDetailsFragment;

public class StationViewActivity extends AppCompatActivity {

    private static final String TAG = StationViewActivity.class.getName();

    private SectionsPageAdapter mSectionsPageAdapter;
    private ViewPager mViewPager;
    private MeteoStationTO meteoStationTO;
    private TabLayout tabLayout;
    private String token;
    private Toolbar toolbar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_station_view);
        Log.d(TAG, "onCreate: Starting...");

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


    private void setupSectionsPageAdapter(SectionsPageAdapter adapter) {
        adapter.addFragment(MeteoDetailsFragment.newInstance(meteoStationTO.getId().toString()), "Meteo");
        RequestQueue requestQueue = ApplicationController.getInstance(getApplicationContext()).getRequestQueue();
        adapter.addFragment(ChartsFragment.newInstance(meteoStationTO.getId().toString(), token, requestQueue), "Charts");
    }
}
