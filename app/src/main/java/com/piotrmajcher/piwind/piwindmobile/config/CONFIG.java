package com.piotrmajcher.piwind.piwindmobile.config;

public interface CONFIG {
    String ID_KEY = "id";
    String NAME_KEY = "name";
    String URL_KEY = "stationBaseURL";
    String LOGIN_PREFERENCES_KEY = "Login";
    String NOTIFICATIONS_PREFERENCES_KEY = "Notifications";
    String UNITS_PREFERENCES_KEY = "Units";
    String USERNAME = "username";
    String PASSWORD_KEY = "password";
    String TOKEN = "token";
    String IS_USER_AUTHORIZED_KEY = "isAuthorized";
    String WIND_UNIT_KEY = "windUnit";
    String TEMPERATURE_UNIT_KEY = "temperatureUnit";
    Double MPS_TO_KMH = 3.6;
    Double MPS_TO_KTS = 1.944;
    Double CELSIUS_TO_FAHRENHEIT_ADDER = 33.8;
    Double CELSIUS_TO_FAHRENHEIT_MULTIPLIET = 9.0/5.0;
}
