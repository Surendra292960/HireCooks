package com.test.sample.hirecooks.WebApis;
import com.test.sample.hirecooks.Models.MapLocationResponse.Example;
import com.test.sample.hirecooks.Models.MapLocationResponse.Maps;
import com.test.sample.hirecooks.Models.MapLocationResponse.Result;
import io.reactivex.Observable;
import retrofit2.Call;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
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


    //ManageAddress

    @FormUrlEncoded
    @POST("createAddress")
    Observable<Maps> createAddress(
            @Field("latitude") String latitude,
            @Field("longitude") String longitude,
            @Field("address") String address,
            @Field("sub_address") String subAddress,
            @Field("pincode") String pincode,
            @Field("placeId") String placeId,
            @Field("userId") int userId,
            @Field("firm_id") String firmId,
            @Field("houseNumber") String houseNumber,
            @Field("floor") String floor,
            @Field("landmark") String landmark,
            @Field("location_type") String location_type);

    @FormUrlEncoded
    @PUT("updateAddress/{mapId}")
    Observable<Maps> updateAddress(
            @Path("mapId") int mapId,
            @Field("latitude") String latitude,
            @Field("longitude") String longitude,
            @Field("address") String address,
            @Field("sub_address") String subAddress,
            @Field("pincode") String pincode,
            @Field("placeId") String placeId,
            @Field("userId") int userId,
            @Field("firm_id") String firmId,
            @Field("houseNumber") String houseNumber,
            @Field("floor") String floor,
            @Field("landmark") String landmark,
            @Field("location_type") String location_type);

    @GET("getAllAddress")
    Observable<Maps> getAllAddress();

    @DELETE("deleteAddress/{mapId}")
    Observable<Maps> deleteAddress(
            @Path("mapId") int mapId);

}
