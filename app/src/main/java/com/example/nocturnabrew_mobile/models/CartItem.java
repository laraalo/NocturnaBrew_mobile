package com.example.nocturnabrew_mobile.models;

public class CartItem {
    private Product product;
    private int quantity;

    public CartItem(Product product, int quantity) {
        this.product = product;
        this.quantity = quantity;
    }

    public Product getProduct() { return product; }

    public Product getProductId() {return getProductId();}

    public Product getName() {return getName();}

    public Product getPrice() { return getPrice();}

    public int getQuantity() { return quantity; }

    public void increaseQuantity() { this.quantity++; }
    public void decreaseQuantity() { if (this.quantity > 1) this.quantity--; }
}
