package com.example.nocturnabrew_mobile.api;
import com.example.nocturnabrew_mobile.models.*;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Body;
import retrofit2.http.Path;

public interface ApiService {
    @POST("/api/users/create")
    Call <GenericResponse<User>> createUser(@Body User user);

    @POST("/api/login")
    Call<LoginResponse> loginUser(@Body LoginRequest request);;

    @GET("api/products/getAll")
    Call <ProductResponse> getProducts( @Header("auth-user") String userToken);

    @GET("api/products/category/{category}")
    Call<ProductResponse> getProductsByCategory(
            @Path("category") String category,
            @Header("auth-user") String userToken
    );
    @POST("api/ticket/create")
    Call<OrderResponse> createOrder(
            @Header("auth-user") String token,
            @Body OrderRequest order
    );

}
