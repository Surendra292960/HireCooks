package com.test.sample.hirecooks.WebApis;

import com.test.sample.hirecooks.Models.cooks.Request.CooksImagesResult;

import io.reactivex.Observable;
import retrofit2.Call;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface CookImages {
    @FormUrlEncoded
    @POST("getCookImagesByUserId")
    Observable<CooksImagesResult> getCookImagesByUserId(
            @Field("userId")int userId);

    @FormUrlEncoded
    @POST("addCookImages")
    Observable<CooksImagesResult> addCookImages(
            @Field("userId")int userId,
            @Field("link") String link);

    @GET("getCookImages")
    Call<CooksImagesResult> getCookImages();

    @DELETE("deleteCookImage/{id}")
    Observable<CooksImagesResult> deleteCookImages(
            @Path( "id" ) Integer id);

    @FormUrlEncoded
    @PUT("updateCookImage/{id}")
    Observable<CooksImagesResult> updateCookImage(
            @Path("id")  int id,
            @Field("userId") Integer userId,
            @Field("link") String link,
            @Field("caption") String caption);
}
