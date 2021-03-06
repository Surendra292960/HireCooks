package com.test.sample.hirecooks.WebApis;


import com.test.sample.hirecooks.Models.Order.Results;

import io.reactivex.Observable;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface OrderApi {
    @FormUrlEncoded
    @POST("submitOrder")
    Call<Results> submitOrder(
            @Field("order_id") String orderId,
            @Field("product_name") String productName,
            @Field("product_sellRate") int productSellRate,
            @Field("product_displayRate") int productDisplayRate,
            @Field("product_discount") int productDiscount,
            @Field("product_quantity") int productQuantity,
            @Field("product_totalAmount") double productTotalAmount,
            @Field("order_status") String orderStatus,
            @Field("product_image") String productImage,
            @Field("firm_id") String firmId,
            @Field("userId") int userId,
            @Field("order_weight") String orderWeight,
            @Field("payment_method") String payment_method,
            @Field("order_placeId") String orderPlaceId,
            @Field("order_latitude") String orderLatitude,
            @Field("order_longitude") String orderLongitude,
            @Field("order_address") String orderAddress,
            @Field("order_sub_address") String orderSubAddress,
            @Field("order_pincode") Integer orderPincode,
            @Field("name") String name,
            @Field("email") String email,
            @Field("phone") String phone,
            @Field("firm_lat") String firmLat,
            @Field("firm_lng") String firmLng,
            @Field("firm_address") String firmAddress,
            @Field("firm_pincode") String getFirmPincode);

    @FormUrlEncoded
    @POST("getOrderByUserId")
    Call<Results> getOrderByUserId(
            @Field("userId") int userId);

    @FormUrlEncoded
    @POST("getCurrentOrders")
    Call<Results> getCurrentOrders(
            @Field("firm_id") String firmId,
            @Field("order_status") String order_status);

    @GET("getOrder")
    Call<Results> getOrder();

    @FormUrlEncoded
    @POST("verifyData")
    Observable<Results> Adeudos(
            @Field("id") int id_user);

    @FormUrlEncoded
    @PUT("updateOrderStatus/{id}")
    Call<Results> updateOrderStatus(
            @Path("id") Integer id,
            @Field("order_status") String order_status);

    @FormUrlEncoded
    @PUT("acceptOrders/{id}")
    Call<Results> acceptOrders(
            @Path("id") Integer id,
            @Field("order_confirm") String order_confirm);

}


