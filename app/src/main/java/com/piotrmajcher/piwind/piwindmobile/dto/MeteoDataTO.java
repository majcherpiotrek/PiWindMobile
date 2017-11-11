package com.piotrmajcher.piwind.piwindmobile.dto;

import java.util.Date;

public class MeteoDataTO {

    private double temperature;

    private double windSpeed;

    private long dateTime;

    private String windDirectionDescription;

    private String beaufortCategoryDescription;

    private String temperatureConditionsDescription;

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

    public String getWindDirectionDescription() {
        return windDirectionDescription;
    }

    public void setWindDirectionDescription(String windDirectionDescription) {
        this.windDirectionDescription = windDirectionDescription;
    }

    public String getBeaufortCategoryDescription() {
        return beaufortCategoryDescription;
    }

    public void setBeaufortCategoryDescription(String beaufortCategoryDescription) {
        this.beaufortCategoryDescription = beaufortCategoryDescription;
    }

    public String getTemperatureConditionsDescription() {
        return temperatureConditionsDescription;
    }

    public void setTemperatureConditionsDescription(String temperatureConditionsDescription) {
        this.temperatureConditionsDescription = temperatureConditionsDescription;
    }

    @Override
    public String toString() {
        return "MeteoDataTOAndroid [temperature=" + temperature + ", windSpeed=" + windSpeed + ", dateTime=" + dateTime
                + ", windDirectionDescription=" + windDirectionDescription + ", beaufortCategoryDescription="
                + beaufortCategoryDescription + ", temperatureConditionsDescription=" + temperatureConditionsDescription
                + "]";
    }
}
