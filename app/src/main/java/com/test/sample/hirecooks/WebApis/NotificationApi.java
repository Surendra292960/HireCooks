package com.test.sample.hirecooks.WebApis;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface NotificationApi {
    @FormUrlEncoded
    @POST("sendNotification")
    Call<String> sendNotification(
            @Field("device_token") String device_token,
            @Field("firm_id") String firm_id);

    @FormUrlEncoded
    @POST("chatNotification")
    Call<String> chatNotification(
            @Field("device_token") String device_token,
            @Field("firm_id") String firm_id,
            @Field("message") String message);
}
