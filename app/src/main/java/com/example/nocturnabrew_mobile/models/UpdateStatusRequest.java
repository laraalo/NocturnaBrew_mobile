package com.example.nocturnabrew_mobile.models;

public class UpdateStatusRequest {
    private String status;

    public UpdateStatusRequest(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }
}

