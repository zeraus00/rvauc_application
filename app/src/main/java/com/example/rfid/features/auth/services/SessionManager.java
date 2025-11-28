package com.example.rfid.features.auth.services;

public class SessionManager {
    private static SessionManager instance;
    private String accessToken;
    private  SessionManager() {}
    public static synchronized  SessionManager getInstance() {
        if (instance == null) instance = new SessionManager(){};
        return instance;
    }

    public void setAccessToken(String token) {
        accessToken = token;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void clear() {
        accessToken = null;
    }
}
