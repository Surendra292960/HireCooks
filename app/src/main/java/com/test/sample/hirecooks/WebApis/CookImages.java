package com.test.sample.hirecooks.WebApis;

import com.test.sample.hirecooks.Models.cooks.Request.CooksImagesResult;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface CookImages {
    @FormUrlEncoded
    @POST("getCookImagesByUserId")
    Call<CooksImagesResult> getCookImagesByUserId(
            @Field("userId")int userId);

    @GET("getCookImages")
    Call<CooksImagesResult> getCookImages();
}
