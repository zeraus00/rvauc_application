package com.example.rfid.features.auth.services;

import android.content.Context;
import android.content.SharedPreferences;

public class SessionManager {
    private final SharedPreferences prefs;
    private static SessionManager instance;
    private String email;
    private boolean rememberMe;
    private String accessToken;
    private SessionManager(Context context) {
        prefs = context.getSharedPreferences("session", Context.MODE_PRIVATE);
    }
    public static synchronized  SessionManager getInstance() {
        if (instance == null) throw new RuntimeException("SessionManager is not initialized.");
        return instance;
    }
    public static void init(Context context) {
        if(instance == null) instance = new SessionManager(context);
    }
    public void clear() {
        email = null;
        rememberMe = false;
        accessToken = null;
        var editor = prefs.edit();
        editor.clear();
    }
    public void setAccessToken(String token) { accessToken = token; }
    public String getAccessToken() { return accessToken; }
    public void setRefreshToken(String token) {
        var editor = prefs.edit();
        editor.putString("refreshToken", token);
        editor.apply();
    }
    public String getRefreshToken() {
        return prefs.getString("refreshToken", null);
    }
    public void setEmail(String email) { this.email = email; }
    public String getEmail() { return email; }
    public void setRememberMe(boolean rememberMe) { this.rememberMe = rememberMe; }
    public boolean getRememberMe() { return rememberMe; }

}
