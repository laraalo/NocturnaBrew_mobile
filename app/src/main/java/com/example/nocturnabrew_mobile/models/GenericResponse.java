package com.example.nocturnabrew_mobile.models;

public class GenericResponse<T> {
    private String message;
    private T values;

    public String getMessage() { return message; }
    public T getValues() { return values; }
}
