package com.piotrmajcher.piwind.piwindmobile.tabfragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.piotrmajcher.piwind.piwindmobile.R;
import com.piotrmajcher.piwind.piwindmobile.models.ChartData;
import com.piotrmajcher.piwind.piwindmobile.tabfragments.chartutils.DateTimeXAxisValueFormatter;

import java.util.Arrays;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

public class ChartsFragment extends Fragment {
    private static final String TAG = ChartsFragment.class.getName();
    private UUID stationId;
    private LineChart chart;

    public static ChartsFragment newInstance(String meteoStationId) {
        ChartsFragment f = new ChartsFragment();
        Bundle args = new Bundle();
        args.putSerializable("meteoStationId", meteoStationId);
        f.setArguments(args);
        return f;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle args = getArguments();
        if (args != null  && args.getString("meteoStationId") != null) {
            stationId = UUID.fromString(args.getString("meteoStationId").trim());

        } else {
            throw new RuntimeException("Unexpected state occured. Null station object passed to station details view.");
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.charts, container, false);

        chart = (LineChart) view.findViewById(R.id.chart);
        SwipeRefreshLayout refresher = (SwipeRefreshLayout) view.findViewById(R.id.charts_refresher);
        refresher.setOnRefreshListener(() -> {
            refreshChart(chart);
            refresher.setRefreshing(false);
        });

        refreshChart(chart);
        return view;
    }

    @NonNull
    private LineData createWindChartLineData(List<ChartData> chartData) {
        List<Entry> avgWindEntries = new LinkedList<>();
        List<Entry> maxGustsEntries = new LinkedList<>();
        List<Entry> minGustsEntries = new LinkedList<>();

        for (int i = 0; i < chartData.size(); i++) {
            ChartData d = chartData.get(i);
            avgWindEntries.add(new Entry(i, d.getAvgWind()));
            maxGustsEntries.add(new Entry(i, d.getMaxGust()));
            minGustsEntries.add(new Entry(i, d.getMinGust()));
        }

        LineDataSet avgWindDataSet = new LineDataSet(avgWindEntries, "Average wind");
        setupLineDataSet(avgWindDataSet, 2f, ColorTemplate.COLORFUL_COLORS[2]);

        LineDataSet maxGustsDataSet = new LineDataSet(maxGustsEntries, "Strongest gusts");
        setupLineDataSet(maxGustsDataSet, 2f, ColorTemplate.COLORFUL_COLORS[0]);

        LineDataSet minGustsDataSet = new LineDataSet(minGustsEntries, "Weakest gusts");
        setupLineDataSet(minGustsDataSet, 2f, ColorTemplate.JOYFUL_COLORS[4]);

        return new LineData(Arrays.asList(avgWindDataSet, maxGustsDataSet, minGustsDataSet));
    }

    private void setupChart(LineChart chart, LineData lineData) {
        chart.setData(lineData);
        chart.setVisibleXRangeMinimum(6f);
        chart.setScaleYEnabled(false);
        chart.setDoubleTapToZoomEnabled(false);
        chart.setHighlightPerDragEnabled(false);
        chart.setHighlightPerTapEnabled(false);
        chart.getDescription().setEnabled(false);
        chart.invalidate(); // refresh
    }

    private void setupLineDataSet(LineDataSet dataSet, float lineWidth, int color) {
        dataSet.setColor(color);
        dataSet.setHighlightEnabled(false);
        dataSet.setDrawCircles(false);
        dataSet.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        dataSet.setLineWidth(lineWidth);
    }
    private void setupXAxis(LineChart chart, List<ChartData> windChartData) {
        XAxis xAxis = chart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.addLimitLine(new LimitLine(30, "Next day"));
        xAxis.setDrawGridLines(false);
        xAxis.setValueFormatter(new DateTimeXAxisValueFormatter(createDatesXAxisLabels(windChartData)));
    }

    private Date[] createDatesXAxisLabels(List<ChartData> data) {
        Date[] result = new Date[data.size()];
        for (int i = 0; i < result.length; i++) {
            result[i] = data.get(i).getDate();
        }
        return result;
    }

    private void refreshChart(LineChart chart) {
        List<ChartData> windChartData = generateRandomData(20, 5);
        LineData lineData = createWindChartLineData(windChartData);
        setupXAxis(chart, windChartData);
        setupChart(chart, lineData);
    }
    private List<ChartData> generateRandomData(int dataSize, long measurementsInterval) {
        Date startDate = new Date();
        Random random = new Random();
        List<ChartData> result = new LinkedList<>();
        float start = 20f;
        for (int i = 0; i < dataSize; i++) {
            ChartData data = new ChartData();
            Float f = random.nextFloat()*4;
            Float gustMax = random.nextFloat()*10;
            Float gustMin = random.nextFloat()*10;

            int n = random.nextInt();
            if (n > 6) {
                start += f;
            } else {
                if (start - f >= 0) {
                    start-=f;
                }
            }
            data.setAvgWind(start);
            data.setMaxGust(start + gustMax);
            while (start - gustMin < 0) {
                gustMin--;
            }
            data.setMinGust(start - gustMin);
            Date date = new Date(startDate.getTime() + measurementsInterval);
            startDate = date;
            data.setDate(date);
            result.add(data);
        }
        return result;
    }
}
