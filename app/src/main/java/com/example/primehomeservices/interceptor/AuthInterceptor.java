package com.example.primehomeservices.interceptor;

import java.io.IOException;

import okhttp3.Interceptor;
import androidx.annotation.NonNull;
import okhttp3.Request;
import okhttp3.Response;

public class AuthInterceptor implements Interceptor {
    private String mAuthToken;

    public AuthInterceptor(String authToken) {
        mAuthToken = authToken;
    }

    @Override
    public Response intercept(@NonNull Chain chain) throws IOException {
        Request request  = chain.request().newBuilder()
                .addHeader("Authorization", "Bearer " + mAuthToken)
                .build();
        System.out.println(mAuthToken);
        return chain.proceed(request);
    }
}
