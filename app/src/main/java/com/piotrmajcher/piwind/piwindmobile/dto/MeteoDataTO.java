package com.piotrmajcher.piwind.piwindmobile.dto;

import java.util.Date;

public class MeteoDataTO {

    private double temperature;

    private double windSpeed;

    private Date dateTime;

    private String windDirectionDescription;

    private String beaufortCategoryDescription;

    private String temperatureConditionsDescription;

    private String waterConditionsDescription;

    private String equipmentSuggestion;

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

    public Date getDateTime() {
        return dateTime;
    }

    public void setDateTime(Date dateTime) {
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

    public String getWaterConditionsDescription() {
        return waterConditionsDescription;
    }

    public void setWaterConditionsDescription(String waterConditionsDescription) {
        this.waterConditionsDescription = waterConditionsDescription;
    }

    public String getEquipmentSuggestion() {
        return equipmentSuggestion;
    }

    public void setEquipmentSuggestion(String equipmentSuggestion) {
        this.equipmentSuggestion = equipmentSuggestion;
    }

    @Override
    public String toString() {
        return "MeteoDataTO{" +
                "temperature=" + temperature +
                ", windSpeed=" + windSpeed +
                ", dateTime=" + dateTime +
                ", windDirectionDescription='" + windDirectionDescription + '\'' +
                ", beaufortCategoryDescription='" + beaufortCategoryDescription + '\'' +
                ", temperatureConditionsDescription='" + temperatureConditionsDescription + '\'' +
                ", waterConditionsDescription='" + waterConditionsDescription + '\'' +
                ", equipmentSuggestion='" + equipmentSuggestion + '\'' +
                '}';
    }
}
