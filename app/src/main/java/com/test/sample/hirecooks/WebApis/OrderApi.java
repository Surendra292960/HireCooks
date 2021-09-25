package com.test.sample.hirecooks.WebApis;
import com.test.sample.hirecooks.Models.NewOrder.OrdersTable;
import com.test.sample.hirecooks.Models.NewOrder.Root;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface OrderApi {
    @POST("orders")
    Call<List<Root>> addOrder(@Body List<OrdersTable> root);

    @POST("orders/{status}")
    Call<List<Root>> getCurrentOrders(@Path("status") String status);

    @GET("/RetrofitExample/public/orders/{id}")
    Call<List<Root>> getOrdersByOrderId(@Path("id") String id);

    @POST("ordersByUserId/{id}")
    Call<List<Root>> getOrdersByUserId(@Path("id") int id);

    @PUT("orders/{order_id}")
    Call<List<Root>> updateOrderStatus(
            @Path("order_id") Integer id,@Body List<OrdersTable> order_status);

    @PUT("confirm_orders/{order_id}")
    Call<List<Root>> acceptOrders(
            @Path("order_id") Integer id,@Body List<OrdersTable> confirm_status);

    @PATCH("/RetrofitExample/public/orders/{order_id}")
    Call<List<Root>> updateOrderAddresss(
            @Path("order_id") Integer order_id,@Body List<Root> roots);
}


