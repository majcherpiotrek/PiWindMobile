package com.piotrmajcher.piwind.piwindmobile.fragments.chartutils;

import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DateTimeXAxisValueFormatter implements IAxisValueFormatter {

    private Date[] dates;
    private SimpleDateFormat df = new SimpleDateFormat("kk:mm");

    public DateTimeXAxisValueFormatter(Date[] dates) {
        this.dates = dates;
    }

    @Override
    public String getFormattedValue(float value, AxisBase axis) {
        return df.format(dates[(int) value]);
    }
}
