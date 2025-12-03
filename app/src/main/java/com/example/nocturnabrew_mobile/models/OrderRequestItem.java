package com.example.nocturnabrew_mobile.models;

public class OrderRequestItem {
    private int productId;
    private int qty;

    public OrderRequestItem(int productId, int qty) {
        this.productId = productId;
        this.qty = qty;
    }
}

