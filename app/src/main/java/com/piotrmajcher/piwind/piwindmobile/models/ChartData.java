package com.piotrmajcher.piwind.piwindmobile.models;

import java.io.Serializable;
import java.util.Date;

public class ChartData implements Serializable{

    private float avgWind;
    private float maxGust;
    private float minGust;
    private Date date;

    public float getAvgWind() {
        return avgWind;
    }

    public void setAvgWind(float avgWind) {
        this.avgWind = avgWind;
    }

    public float getMaxGust() {
        return maxGust;
    }

    public void setMaxGust(float maxGust) {
        this.maxGust = maxGust;
    }

    public float getMinGust() {
        return minGust;
    }

    public void setMinGust(float minGust) {
        this.minGust = minGust;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
}
