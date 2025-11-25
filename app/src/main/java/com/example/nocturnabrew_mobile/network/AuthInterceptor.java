package com.example.nocturnabrew_mobile.network;

import com.example.nocturnabrew_mobile.BuildConfig;

import java.io.IOException;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class AuthInterceptor implements Interceptor{

    @Override
    public Response intercept(Chain chain) throws IOException{
        Request newRequest = chain.request().newBuilder()
                .addHeader("Authorization", "AppToken "+ BuildConfig.AppToken)
                .build();
        return chain.proceed(newRequest);
    }
}
