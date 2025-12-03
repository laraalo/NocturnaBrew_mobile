package com.example.nocturnabrew_mobile.models;

import java.util.List;

public class ProductResponse {
    private String message;
    private List<Product> values;

    public String getMessage() {
        return message;
    }

    public List<Product> getValues() {
        return values;
    }
}
