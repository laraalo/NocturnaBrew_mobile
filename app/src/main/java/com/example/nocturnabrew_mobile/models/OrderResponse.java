package com.example.nocturnabrew_mobile.models;

import java.util.List;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class OrderResponse {

    @SerializedName("message")
    private String message;

    @SerializedName("order")
    private Order order;

    public String getMessage() {
        return message;
    }

    public Order getOrder() {
        return order;
    }

    public static class Order {

        @SerializedName("orderId")
        private String orderId;

        @SerializedName("total")
        private double total;

        @SerializedName("items")
        private List<Item> items;
        private String status = "pending";


        public String getOrderId() {
            return orderId;
        }

        public double getTotal() {
            return total;
        }

        public List<Item> getItems() {
            return items;
        }
        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public static class Item {

            @SerializedName("productId")
            private String productId;

            @SerializedName("name")
            private String name;

            @SerializedName("qty")
            private int qty;

            @SerializedName("price")
            private double price;

            @SerializedName("subtotal")
            private double subtotal;

            public String getProductId() {
                return productId;
            }

            public String getName() {
                return name;
            }

            public int getQty() {
                return qty;
            }

            public double getPrice() {
                return price;
            }

            public double getSubtotal() {
                return subtotal;
            }
        }
    }
}

