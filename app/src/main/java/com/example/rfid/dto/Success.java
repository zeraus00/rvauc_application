package com.example.rfid.dto;

import com.example.rfid.interfaces.BaseResult;

public final class Success<T> implements BaseResult<T> {
    public final boolean success = true;
    public final T result;

    public Success(T result) {
        this.result = result;
    }
}
