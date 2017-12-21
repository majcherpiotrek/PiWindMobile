package com.piotrmajcher.piwind.piwindmobile.config;

public interface REST {

    String BASE_URL = "https://piwind.herokuapp.com";
    //String BASE_URL = "http://10.0.2.2:8080";
    String GET_STATIONS_LIST_ENDPOINT = "/stations/all";
    String STATISTIC_ENDPOINT = "/statistics";
    String REGISTER_USER_URL = "/register-user";
    String CONFIRM_EMAIL_URL = "/confirm/";
    String LOGIN_URL = "/login";
    String AUTH_HEADER_KEY = "Authorization";
    int UNAUTHORIZED = 401;
    String REQUEST_NOTIFICATIONS_ENDPOINT = "/stations/request-notifications";
    String CANCEL_NOTIFICATIONS_ENDPOINT = "/stations/cancel-notifications";
    String RETRIEVE_PASSWORD_USERNAME_URL = "/password/retrieve/username/";
    String RETRIEVE_PASSWORD_EMAIL_URL = "/password/retrieve/username/email/";
    String CHANGE_PASSWORD_URL = "/password/retrieve/newpassword";
}
