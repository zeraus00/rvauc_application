package com.example.rfid.interfaces;

public interface HttpCallback {
    void onSuccess(String json);
    void onError(Exception e);
}
