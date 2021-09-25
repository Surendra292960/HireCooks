package com.test.sample.hirecooks.WebApis;

import com.test.sample.hirecooks.Models.BannerResponse.Banners;
import com.test.sample.hirecooks.Models.Chat.Example;
import com.test.sample.hirecooks.Models.Chat.MessageResponse;
import com.test.sample.hirecooks.Models.HotelImage;
import com.test.sample.hirecooks.Models.ImagesResponse.Images;
import com.test.sample.hirecooks.Models.MenuResponse.Menus;
import com.test.sample.hirecooks.Models.TokenResponse.TokenResult;
import com.test.sample.hirecooks.Models.TokenResponse.Tokens;

import java.util.ArrayList;
import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;

public interface UserApi {
    @POST("user")
    Call<List<com.test.sample.hirecooks.Models.Users.Example>> createUser(
            @Body List<com.test.sample.hirecooks.Models.Users.Example> examples);

    @POST("loginProfile")
    Call<List<com.test.sample.hirecooks.Models.Users.Example>> userLogin(@Body List<com.test.sample.hirecooks.Models.Users.Example> exampleList);

    @PUT("user/{id}")
    Call<List<com.test.sample.hirecooks.Models.Users.Example>> updateUser(
            @Path( "id" ) int id,
            @Body List<com.test.sample.hirecooks.Models.Users.Example> examples);

    @DELETE("user/{id}")
    Call<List<com.test.sample.hirecooks.Models.Users.Example>> deleteUser(@Path( "id" ) int id);

    @FormUrlEncoded
    @PUT("forgotPassword")
    Call<List<com.test.sample.hirecooks.Models.Users.Example>> forgotPassword(
            @Field("email") String email,
            @Field("password") String password);

    @FormUrlEncoded
    @POST("phone")
    Call<List<com.test.sample.hirecooks.Models.Users.Example>> checkUserPhone(@Field( "phone" ) String phone);

    @GET("user")
    Call<List<com.test.sample.hirecooks.Models.Users.Example>> getUsers();

    @GET("menu")
    Call<Menus> getMenu();

    @GET("getBanner")
    Call<Banners> getBanners();

    //sending message
    @FormUrlEncoded
    @POST("sendmessage")
    Call<MessageResponse> sendMessage(
            @Field("from") int from,
            @Field("to") int to,
            @Field("title") String title,
            @Field("message") String message,
            @Field("sent") int sent,
            @Field("recieve") int recieve);

    //getting messages
    @GET("messages/{id}")
    Call<List<Example>> getMessages(@Path("id") int id);

    @Multipart
    @POST("upload")
    Call<String> uploadFile(
            @Part MultipartBody.Part userEmail,
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

    @FormUrlEncoded
    @POST("getTokenByFirmId")
    Call<TokenResult> getTokenByFirmId(
            @Field("userId") int userId,@Field("firm_id") String firm_id);

    //gettingUserImages
    @FormUrlEncoded
    @POST("getUserImages")
    Call<Images> getUserImages(@Field("userId") Integer userId);

    @Multipart
    @POST("multipleImages")
    Call<String> uploadFile(@Part MultipartBody.Part file, @Part("files[]") RequestBody name);

    @POST("multipleImages")
    Call<HotelImage> uploadImages(@Body HotelImage body);


    @FormUrlEncoded
    @POST("getTokenByUserId")
    Call<TokenResult> getTokenByUserId(
            @Field("userId") int userId);

    @GET("Category")
    Call<List<com.test.sample.hirecooks.Models.Category.Example>> getCategory();

    @POST("Category/{id}")
    Call<List<com.test.sample.hirecooks.Models.Category.Example>> getCategoryByCatId(@Path( "id" ) int id);

    @FormUrlEncoded
    @PUT("updateSataus/{id}")
    Call<String> updateUserStatus(
            @Path( "id" ) Integer id,
            @Field( "status" ) Integer status,
            @Field( "email" ) String email);

    @POST("messages/{id}")
    Call<List<Example>> startTypingMessage(@Path("id") Integer id, @Body ArrayList<Example> exampleArrayList);

    @POST("user/{firm_id}")
    Call<List<com.test.sample.hirecooks.Models.Users.Example>> getUserByFirmId(@Path( "firm_id" ) String firmId);

    @DELETE("user/{id}")
    Call<List<String>> deleteProfile(@Path( "id" ) int id);

    @POST("firmusers")
    Call<List<com.test.sample.hirecooks.Models.FirmUsers.Example>> firmUserSignIn(
            @Body List<com.test.sample.hirecooks.Models.FirmUsers.Example> user);

    @PUT("firmusers/{id}")
    Call<List<com.test.sample.hirecooks.Models.FirmUsers.Example>> firmUserSignOut(
            @Path("id") Integer userId,
            @Body List<com.test.sample.hirecooks.Models.FirmUsers.Example> user);

    @FormUrlEncoded
    @POST("firmusersByDate")
    Call<List<com.test.sample.hirecooks.Models.FirmUsers.Example>> getFirmUserByDate(
            @Field("userId") Integer userId,
            @Field("from_date") String from_date,
            @Field("to_date") String to_date);

    @FormUrlEncoded
    @POST("allfirmusersByDate")
    Call<List<com.test.sample.hirecooks.Models.FirmUsers.Example>> getAllFirmUserByDate(
            @Field("firmId") String userId,
            @Field("from_date") String from_date,
            @Field("to_date") String to_date);

}
