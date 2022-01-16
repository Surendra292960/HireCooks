package com.test.sample.hirecooks.WebApis;

import com.test.sample.hirecooks.ApiServiceCall.Retry;
import com.test.sample.hirecooks.Models.BannerResponse.Banners;
import com.test.sample.hirecooks.Models.Category.CategoryResponse;
import com.test.sample.hirecooks.Models.Chat.Example;
import com.test.sample.hirecooks.Models.Chat.MessageResponse;
import com.test.sample.hirecooks.Models.HotelImage;
import com.test.sample.hirecooks.Models.ImagesResponse.Images;
import com.test.sample.hirecooks.Models.Menue.MenueResponse;
import com.test.sample.hirecooks.Models.TokenResponse.TokenResult;
import com.test.sample.hirecooks.Models.TokenResponse.Tokens;
import com.test.sample.hirecooks.Models.Users.UserResponse;
import java.util.ArrayList;
import java.util.List;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
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
    Call<List<UserResponse>> createUser(
            @Body List<UserResponse> examples);

    @POST("loginProfile")
    Call<List<UserResponse>> userLogin(@Body List<UserResponse> exampleList);

    @PUT("user/{id}")
    Call<List<UserResponse>> updateUser(
            @Path( "id" ) int id,
            @Body List<UserResponse> examples);

    @DELETE("user/{id}")
    Call<List<UserResponse>> deleteUser(@Path( "id" ) int id);

    @FormUrlEncoded
    @PUT("forgotPassword")
    Call<List<UserResponse>> forgotPassword(
            @Field("email") String email,
            @Field("password") String password);

    @FormUrlEncoded
    @POST("phone")
    Call<List<UserResponse>> checkUserPhone(@Field( "phone" ) String phone);

    @Retry
    @GET("user")
    Call<List<UserResponse>> getUsers();

    @Retry
    @GET("Menue")
    Call<List<MenueResponse>> getMenue();

    @Retry
    @GET("Menue")
    Call<List<MenueResponse>> addMenue();

    @Retry
    @GET("Menue/{id}")
    Call<List<MenueResponse>> updateMenus();

    @Retry
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
            @Field("recieve") int recieve,
            @Field("sender_name") String sender_name);

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

    @Retry
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
    @Retry
    @GET("Category")
    Call<List<CategoryResponse>> getCategory();

    @POST("Category/{id}")
    Call<List<CategoryResponse>> getCategoryByCatId(@Path( "id" ) int id);

    @FormUrlEncoded
    @PUT("updateSataus/{id}")
    Call<String> updateUserStatus(
            @Path( "id" ) Integer id,
            @Field( "status" ) Integer status,
            @Field( "email" ) String email);

    @POST("messages/{id}")
    Call<List<Example>> startTypingMessage(@Path("id") Integer id, @Body ArrayList<Example> exampleArrayList);

    @POST("user/{firm_id}")
    Call<List<UserResponse>> getUserByFirmId(@Path( "firm_id" ) String firmId);

    @DELETE("user/{id}")
    Call<List<UserResponse>> deleteProfile(@Path( "id" ) int id);

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

    void getCategory(Callback<List<CategoryResponse>> listCallback);

}
