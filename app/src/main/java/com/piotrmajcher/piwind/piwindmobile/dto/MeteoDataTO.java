package com.piotrmajcher.piwind.piwindmobile.dto;

import java.util.Date;

public class MeteoDataTO {

    private double temperature;

    private double windSpeed;

    private long dateTime;

    public double getTemperature() {
        return temperature;
    }

    public void setTemperature(double temperature) {
        this.temperature = temperature;
    }

    public double getWindSpeed() {
        return windSpeed;
    }

    public void setWindSpeed(double windSpeed) {
        this.windSpeed = windSpeed;
    }

    public long getDateTime() {
        return dateTime;
    }

    public void setDateTime(long dateTime) {
        this.dateTime = dateTime;
    }

    @Override
    public String toString() {
        return "MeteoDataTO [temperature=" + temperature + ", windSpeed=" + windSpeed + ", dateTime=" + dateTime + "]";
    }
}
