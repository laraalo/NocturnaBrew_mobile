package com.example.nocturnabrew_mobile.api;
import com.example.nocturnabrew_mobile.models.*;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Body;
public interface ApiService {
    @POST("/api/users/create")
    Call <GenericResponse<User>> createUser(@Body User user);

    @POST("/api/login")
    Call <GenericResponse> loginUser(@Body LoginRequest loginRequest);

}
