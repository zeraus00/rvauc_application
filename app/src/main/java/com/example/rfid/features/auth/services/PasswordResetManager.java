package com.example.rfid.features.auth.services;

public class PasswordResetManager {

    private String email;
    private String code;
    private static PasswordResetManager instance;
    public static synchronized PasswordResetManager getInstance() {
        if (instance == null) instance = new PasswordResetManager() {};
        return instance;
    }

    public String getEmail() { return email; }
    public void setEmail(String val) { email = val; }
    public String getCode() { return code; }
    public void setCode(String val) { code = val; }
}
