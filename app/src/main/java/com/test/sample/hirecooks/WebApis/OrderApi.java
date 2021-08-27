package com.test.sample.hirecooks.WebApis;


/*import com.test.sample.hirecooks.Models.NewOrder.OrdersTable;
import com.test.sample.hirecooks.Models.NewOrder.Root;
import com.test.sample.hirecooks.Models.Order.Results;*/

import com.test.sample.hirecooks.Models.NewOrder.OrdersTable;
import com.test.sample.hirecooks.Models.NewOrder.Root;
import com.test.sample.hirecooks.Models.Order.Results;

import java.util.List;

import io.reactivex.Observable;
import retrofit2.Call;
import retrofit2.http.Body;
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

    @GET("getOrder")
    Call<Results> getOrder();

    @FormUrlEncoded
    @POST("verifyData")
    Observable<Results> Adeudos(
            @Field("id") int id_user);

/*    @FormUrlEncoded
    @PUT("updateOrderStatus/{id}")
    Call<Results> updateOrderStatus(
            @Path("id") Integer id,
            @Field("order_status") String order_status);*/

  /*  @FormUrlEncoded
    @PUT("acceptOrders/{id}")
    Call<Results> acceptOrders(
            @Path("id") Integer id,
            @Field("order_confirm") String order_confirm);*/

    @POST("orders")
    Call<List<Root>> addOrder(@Body List<OrdersTable> root);

    @POST("orders/{order_status}")
    Call<List<Root>> getCurrentOrders(@Path("order_status") String order_status);

    @POST("ordersByUserId/{id}")
    Call<List<Root>> getOrdersByUserId(@Path("id") int id);

    @PUT("orders/{order_id}")
    Call<List<Root>> updateOrderStatus(
            @Path("order_id") Integer id,@Body List<OrdersTable> order_status);

    @PUT("confirm_orders/{order_id}")
    Call<List<Root>> acceptOrders(
            @Path("order_id") Integer id,@Body List<OrdersTable> confirm_status);
}


