package com.test.sample.hirecooks.WebApis;
import com.test.sample.hirecooks.Models.MapLocationResponse.Example;
import com.test.sample.hirecooks.Models.MapLocationResponse.Result;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface MapApi {
    @FormUrlEncoded
    @POST("createMap")
    Call<Result> addMapDetails(
            @Field("latitude") String latitude,
            @Field("longitude") String longitude,
            @Field("address") String address,
            @Field("sub_address") String subAddress,
            @Field("pincode") String pincode,
            @Field("placeId") String placeId,
            @Field("userId") int userId,
            @Field("firm_id") String firm_id);

/*    @POST("/updateLocation/{MapId}")
    Call<Maps> updateMapDetails(@Path("MapId") String MapId, @Body Map maps);*/

    @FormUrlEncoded
    @PUT("updateMap/{userId}")
    Call<Result> updateMapDetails(
            @Path("userId") int userId,
            @Field("latitude") String latitude,
            @Field("longitude") String longitude,
            @Field("address") String address,
            @Field("sub_address") String subAddress,
            @Field("pincode") String pincode,
            @Field("placeId") String placeId,
            @Field("firm_id") String firm_id);

    @FormUrlEncoded
    @PUT("updateUserMap/{userId}")
    Call<Result> TrackUser(
            @Path("userId") int userId,
            @Field("latitude") String latitude,
            @Field("longitude") String longitude,
            @Field("address") String address,
            @Field("sub_address") String subAddress,
            @Field("pincode") String pincode,
            @Field("firm_id") String firm_id);

    @FormUrlEncoded
    @POST("getMapByuserId")
    Call<Result> getMapDetails(@Field("userId") int userId);

    @FormUrlEncoded
    @POST("getNearByUsers")
    Call<Example> getNearByUsers(
            @Field("latitude") String latitude,
            @Field("longitude") String longitude);

}
