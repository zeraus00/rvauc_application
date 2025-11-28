package com.example.rfid.dto;

import com.example.rfid.interfaces.BaseResult;

public class Fail<TException extends Exception> implements BaseResult<Void> {
    public final boolean success = false;
    public final TException exception;

    public Fail(TException exception) {
        this.exception = exception;
    }
}
