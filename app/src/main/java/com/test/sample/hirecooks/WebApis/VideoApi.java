package com.test.sample.hirecooks.WebApis;

import com.test.sample.hirecooks.Models.Video.Example;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface VideoApi {

    @POST("video")
    Call<List<Example>> addVideo(@Body List<Example> examples);

    @PUT("video/{id}")
    Call<List<Example>> updateVideo(@Path( "id" ) int id, @Body List<Example> examples);

    @GET("video/{userId}")
    Call<List<Example>> getVideos(@Path( "userId" ) Integer userId);

    @DELETE("video/{id}")
    Call<List<Example>> deleteVideo(@Path( "id" ) int id);
}
