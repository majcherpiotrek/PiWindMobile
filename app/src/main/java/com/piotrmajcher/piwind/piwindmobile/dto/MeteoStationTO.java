package com.piotrmajcher.piwind.piwindmobile.dto;

import java.io.Serializable;
import java.util.UUID;

public class MeteoStationTO implements Serializable{

    private UUID id;

    private String name;

    private String stationBaseURL;


    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStationBaseURL() {
        return stationBaseURL;
    }

    public void setStationBaseURL(String stationBaseURL) {
        this.stationBaseURL = stationBaseURL;
    }

    @Override
    public String toString() {
        return "MeteoStationTO{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", stationBaseURL='" + stationBaseURL + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        MeteoStationTO that = (MeteoStationTO) o;

        if (!id.equals(that.id)) return false;
        if (!name.equals(that.name)) return false;
        return stationBaseURL.equals(that.stationBaseURL);

    }

    @Override
    public int hashCode() {
        int result = id.hashCode();
        result = 31 * result + name.hashCode();
        result = 31 * result + stationBaseURL.hashCode();
        return result;
    }
}
