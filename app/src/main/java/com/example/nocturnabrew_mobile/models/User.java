package com.example.nocturnabrew_mobile.models;

import com.google.gson.annotations.SerializedName;

public class User {

    @SerializedName("name")
    private String name;

    @SerializedName("email")
    private String email;

    @SerializedName("password")
    private String password;

    // role no es obligatorio; puede venir vacío o null
    @SerializedName("role")
    private String role;

    // Constructor vacío (necesario si quieres que Gson lo use)
    public User() {}

    // Constructor útil para crear usuarios nuevos
    public User(String name, String email, String password) {
        this.name = name;
        this.email = email;
        this.password = password;

        // role se deja vacío para que el backend lo asigne
        this.role = null;
    }

    // Constructor completo si deseas enviarlo manualmente
    public User(String name, String email, String password, String role) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.role = role;
    }

    // Getters & Setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}