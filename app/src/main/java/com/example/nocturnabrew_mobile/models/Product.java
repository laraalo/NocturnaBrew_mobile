package com.example.nocturnabrew_mobile.models;

public class Product {
    private int id;
    private String name;
    private double price;
    private String description;
    private boolean available;
    private String url;

    // Getters
    public int getProductId() { return id; }
    public String getName() { return name; }
    public double getPrice() { return price; }
    public String getDescription() { return description; }
    public boolean isAvailable() { return available; }
    public String getUrl() { return url; }
}
