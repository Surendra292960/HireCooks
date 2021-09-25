package com.test.sample.hirecooks.WebApis;
import com.test.sample.hirecooks.Models.cooks.Cooks;
import com.test.sample.hirecooks.Models.cooks.Result;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface CookApi {

    @FormUrlEncoded
    @POST("registerCooks")
    Call<Result> createCook(
            @Field("name") String name,
            @Field("email") String email,
            @Field("password") String password,
            @Field("gender") String gender,
            @Field("phone") String phone,
            @Field("cook_type") String cook_type,
            @Field("image") String image,
            @Field("address_book") String address);


    @FormUrlEncoded
    @POST("cooklogin")
    Call<Result> cookLogin(
            @Field("email") String email,
            @Field("password")String password);

    @GET("cooks")
    Call<Cooks> getCooks();

    //updating user
    @FormUrlEncoded
    @POST("update/{id}")
    Call<Result> updateCook(
            @Path("id") int id,
            @Field("name") String name,
            @Field("email") String email,
            @Field("password") String password,
            @Field("gender") String gender,
            @Field("phone") String phone,
            @Field("image") String image,
            @Field("address_book") String address);

}
