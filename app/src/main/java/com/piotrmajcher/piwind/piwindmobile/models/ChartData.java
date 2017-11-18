package com.piotrmajcher.piwind.piwindmobile.models;

import com.piotrmajcher.piwind.piwindmobile.dto.ChartDataTO;

import java.io.Serializable;
import java.util.Date;

public class ChartData implements Serializable{

    private Float avgWind;
    private Float maxGust;
    private Float minGust;
    private Date date;

    public ChartData() {
        super();
    }

    public ChartData(ChartDataTO to) {
        this.avgWind = to.getAvgWind();
        this.maxGust = to.getMaxGust();
        this.minGust = to.getMinGust();
        this.date = new Date(to.getDate());
    }
    public Float getAvgWind() {
        return avgWind;
    }

    public void setAvgWind(Float avgWind) {
        this.avgWind = avgWind;
    }

    public Float getMaxGust() {
        return maxGust;
    }

    public void setMaxGust(Float maxGust) {
        this.maxGust = maxGust;
    }

    public Float getMinGust() {
        return minGust;
    }

    public void setMinGust(Float minGust) {
        this.minGust = minGust;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
}
