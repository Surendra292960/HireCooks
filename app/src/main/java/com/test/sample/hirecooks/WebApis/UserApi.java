package com.test.sample.hirecooks.WebApis;

import com.test.sample.hirecooks.Models.BannerResponse.Banners;
import com.test.sample.hirecooks.Models.Category.Example;
import com.test.sample.hirecooks.Models.HotelImage;
import com.test.sample.hirecooks.Models.Images.Image;
import com.test.sample.hirecooks.Models.ImagesResponse.Images;
import com.test.sample.hirecooks.Models.MenuResponse.Menus;
import com.test.sample.hirecooks.Models.TokenResponse.TokenResult;
import com.test.sample.hirecooks.Models.TokenResponse.Tokens;
import com.test.sample.hirecooks.Models.UsersResponse.UsersResponse;
import com.test.sample.hirecooks.Models.users.MessageResponse;
import com.test.sample.hirecooks.Models.users.Messages;
import com.test.sample.hirecooks.Models.users.Result;
import com.test.sample.hirecooks.Utils.APIUrl;

import java.util.ArrayList;
import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;

public interface UserApi {
    @FormUrlEncoded
    @POST("registerProfile")
    Call<Result> createUser(
            @Field("name") String name,
            @Field("email") String email,
            @Field("phone") String phone,
            @Field("password") String password,
            @Field("gender") String gender,
            @Field("firmId") String firmId,
            @Field("userType") String userType,
            @Field("bikeNumber") String bikeNumber);

    //updating user
    @FormUrlEncoded
    @PUT("updateProfile/{id}")
    Call<Result> updateUser(
            @Path("id") int id,
            @Field("name") String name,
            @Field("email") String email,
            @Field("phone") String phone,
            @Field("gender") String gender,
            @Field("firmId") String firmId,
            @Field("userType") String userType,
            @Field("bikeNumber") String bikeNumber);

    @FormUrlEncoded
    @POST("loginProfile")
    Call<Result> userLogin(
            @Field("email") String email,
            @Field("password") String password);

    @FormUrlEncoded
    @PUT("forgotPassword")
    Call<Result> forgotPassword(
            @Field("email") String email,
            @Field("password") String password);

    @FormUrlEncoded
    @POST("phone")
    Call<Result> getUserPhone(@Field("phone") String phone);

    @GET("users")
    Call<UsersResponse> getUsers();

    @GET("menu")
    Call<Menus> getMenu();

    @GET("getBanner")
    Call<Banners> getBanners();

    //sending message
    @FormUrlEncoded
    @Headers(APIUrl.HEADER_CONTENT_TYPE)
    @POST("sendmessage")
    Call<MessageResponse> sendMessage(
            @Field("from") int from,
            @Field("to") int to,
            @Field("title") String title,
            @Field("message") String message);

    //getting messages
    @GET("messages/{id}")
    Call<Messages> getMessages(@Path("id") int id);

    @Multipart
    @POST("upload")
    Call<String> uploadFile(
            @Part MultipartBody.Part phone,
            @Part MultipartBody.Part file);

    @FormUrlEncoded
    @POST("registerToken")
    Call<TokenResult> registerToken(
            @Field("phone") String phone,
            @Field("token") String token,
            @Field("deviceId") String deviceId,
            @Field("isServerToken") Integer isServerToken,
            @Field("userId") Integer userId);

    @FormUrlEncoded
    @PUT("updateToken/{userId}")
    Call<TokenResult> updateToken(
            @Field("phone") String phone,
            @Field("token") String token,
            @Field("deviceId") String deviceId,
            @Field("isServerToken") Integer isServerToken,
            @Path("userId") int userId,
            @Field("firm_id") String firm_id);

    @FormUrlEncoded
    @POST("getTokenByUserId")
    Call<TokenResult> getTokenFromServer(
            @Field("userId") int userId);

    @GET("getTokens")
    Call<Tokens> getTokens();

    //gettingUserImages
    @FormUrlEncoded
    @POST("getUserImages")
    Call<Images> getUserImages(@Field("userId") Integer userId);

    @FormUrlEncoded
    @Headers(APIUrl.HEADER_CONTENT_TYPE)
    @POST("multipleImages")
    Call<Image> uploadMultiple(@Body String body);

    @Multipart
    @POST("multipleImages")
    Call<String> uploadFile(@Part MultipartBody.Part file, @Part("files[]") RequestBody name);

    @POST("multipleImages")
    Call<HotelImage> uploadImages(@Body HotelImage body);

    @PUT("HotelImages/{id}")
    Call<HotelImage> updateImages(@Path("id") int id, @Body HotelImage body);

    @GET("Hotels/GetHotelImagesByHotelId/{HotelId}")
    Call<ArrayList<HotelImage>> getHotelImages(@Header("Authorization") String authKey, @Path("HotelId") int HotelId);


    @FormUrlEncoded
    @POST("getTokenByUserId")
    Call<TokenResult> getTokenByUserId(
            @Field("userId") int userId);

    @GET("Category")
    Call<List<Example>> getCategory();

    @POST("Category/{id}")
    Call<List<Example>> getCategoryByCatId(@Path( "id" ) int id);
}
