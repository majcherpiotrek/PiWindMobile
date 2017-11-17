package com.piotrmajcher.piwind.piwindmobile;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import com.piotrmajcher.piwind.piwindmobile.adapters.SectionsPageAdapter;
import com.piotrmajcher.piwind.piwindmobile.dto.MeteoStationTO;
import com.piotrmajcher.piwind.piwindmobile.tabfragments.ChartsFragment;
import com.piotrmajcher.piwind.piwindmobile.tabfragments.MeteoDetailsFragment;

public class StationViewActivity extends AppCompatActivity {

    private static final String TAG = StationViewActivity.class.getName();

    private SectionsPageAdapter mSectionsPageAdapter;
    private ViewPager mViewPager;
    private MeteoStationTO meteoStationTO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_station_view);
        Log.d(TAG, "onCreate: Starting...");

        meteoStationTO = (MeteoStationTO) getIntent().getSerializableExtra(MainActivity.SELECTED_STATION);
        if (super.getSupportActionBar() != null) {
            super.getSupportActionBar().setTitle(meteoStationTO.getName());
        }

        mSectionsPageAdapter = new SectionsPageAdapter(getSupportFragmentManager());
        setupSectionsPageAdapter(mSectionsPageAdapter);

        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPageAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);
    }

    private void setupSectionsPageAdapter(SectionsPageAdapter adapter) {
        adapter.addFragment(MeteoDetailsFragment.newInstance(meteoStationTO.getId().toString()), "Meteo");
        adapter.addFragment(new ChartsFragment(), "Charts");
    }
}
