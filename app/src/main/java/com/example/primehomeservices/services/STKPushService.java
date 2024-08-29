package com.example.primehomeservices.services;

import com.example.primehomeservices.mymodels.AccessToken;
import com.example.primehomeservices.mymodels .STKPushRequest;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface STKPushService {
    @POST("mpesa/stkpush/v1/processrequest")
    Call<STKPushRequest> sendPush(@Body STKPushRequest stkPushRequest);

    @GET("oauth/v1/generate?grant_type=client_credentials")
    Call<AccessToken> getAccessToken();
}
