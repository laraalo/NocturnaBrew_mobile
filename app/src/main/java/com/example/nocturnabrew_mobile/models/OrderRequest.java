package com.example.nocturnabrew_mobile.models;

import java.util.List;

public class OrderRequest {
    private List<Item> items;
    private double total;
    private String notes;

    public OrderRequest(List<Item> items, double total, String notes) {
        this.items = items;
        this.total = total;
        this.notes = notes;
    }

    public static class Item {
        private int productId;
        private int qty;

        public Item(int productId, int qty) {
            this.productId = productId;
            this.qty = qty;
        }
    }
}
