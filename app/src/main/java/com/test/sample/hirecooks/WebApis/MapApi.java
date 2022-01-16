package com.test.sample.hirecooks.WebApis;
import com.test.sample.hirecooks.Models.MapLocationResponse.Example;
import com.test.sample.hirecooks.Models.MapLocationResponse.Maps;
import com.test.sample.hirecooks.Models.MapLocationResponse.MapResponse;

import java.util.List;

import io.reactivex.Observable;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface MapApi {

    @POST("address")
    Observable<List<Maps>> addAddress(@Body List<Maps> address);

    @PUT("address/{mapId}")
    Observable<List<Maps>> updateAddress(@Path("mapId") int mapId, @Body List<Maps> address);

    @GET("address/{userId}")
    Observable<List<Maps>> getAddressByUserId(@Path("userId") int id);

    @GET("address")
    Observable<List<Maps>> getAddress();

    @DELETE("address/{mapId}")
    Observable<List<Maps>> deleteAddress(
            @Path("mapId") int mapId);

    @FormUrlEncoded
    @POST("createMap")
    Call<MapResponse> addMapDetails(
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
    Call<MapResponse> updateMapDetails(
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
    Call<MapResponse> TrackUser(
            @Path("userId") int userId,
            @Field("latitude") String latitude,
            @Field("longitude") String longitude,
            @Field("address") String address,
            @Field("sub_address") String subAddress,
            @Field("pincode") String pincode,
            @Field("firm_id") String firm_id);

    @FormUrlEncoded
    @POST("getMapByuserId")
    Call<MapResponse> getMapDetails(@Field("userId") int userId);

    @FormUrlEncoded
    @POST("getNearByUsers")
    Call<Example> getNearByUsers(
            @Field("latitude") String latitude,
            @Field("longitude") String longitude);

}
