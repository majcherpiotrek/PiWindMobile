package com.piotrmajcher.piwind.piwindmobile.models;

import com.piotrmajcher.piwind.piwindmobile.dto.MeteoDataTO;

import java.util.Date;

public class MeteoData {

    private Double temperature;

    private Double windSpeed;

    private Date date;

    public MeteoData(MeteoDataTO meteoDataTO) {
        temperature = meteoDataTO.getTemperature();
        windSpeed = meteoDataTO.getWindSpeed();
        date = new Date(meteoDataTO.getDateTime());
    }

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

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    @Override
    public String toString() {
        return "MeteoDataTO [temperature=" + temperature + ", windSpeed=" + windSpeed + ", date=" + date + "]";
    }
}
