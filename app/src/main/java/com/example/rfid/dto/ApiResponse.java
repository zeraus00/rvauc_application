package com.example.rfid.dto;

public class ApiResponse<T> {
    public boolean success;
    public T result;
    public String message;

    public ApiResponse() {}
    public boolean hasResult() {
        return result != null;
    }
}
