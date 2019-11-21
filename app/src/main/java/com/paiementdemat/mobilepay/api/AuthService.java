package com.paiementdemat.mobilepay.api;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface AuthService {
    @POST("/auth/login")
    @FormUrlEncoded
    Call<User> login(@Body String string);

}
