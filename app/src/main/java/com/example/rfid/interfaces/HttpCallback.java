package com.example.rfid.interfaces;

public interface HttpCallback<T> {
    void onSuccess(T response);
    void onError(String message);
}
