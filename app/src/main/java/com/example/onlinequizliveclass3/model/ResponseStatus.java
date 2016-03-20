package com.example.onlinequizliveclass3.model;

/**
 * Created by Promlert on 1/13/2016.
 */
public class ResponseStatus {

    public final boolean success;
    public final String message;

    public ResponseStatus(boolean success, String message) {
        this.success = success;
        this.message = message;
    }
}
