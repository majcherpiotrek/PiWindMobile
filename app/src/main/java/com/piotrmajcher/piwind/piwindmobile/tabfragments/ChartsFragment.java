package com.piotrmajcher.piwind.piwindmobile.tabfragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.piotrmajcher.piwind.piwindmobile.R;

public class ChartsFragment extends Fragment {
    private static final String TAG = ChartsFragment.class.getName();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.charts, container, false);

        return view;
    }
}
