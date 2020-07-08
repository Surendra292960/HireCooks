package com.test.sample.hirecooks.WebApis;

import com.test.sample.hirecooks.Models.VendersCategory.Result;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface VendersApi {

    @GET("getVendersCategory")
    Call<Result> getVendersCategory();

    @FormUrlEncoded
    @POST("getVendersSubCategoryBySubCategoryId")
    Call<com.test.sample.hirecooks.Models.VendersSubCategory.Result> getVendersSubCategoryBySubCategoryId(
            @Field("subcategoryid") int subcategoryid);

    @GET("getVendersSubCategory")
    Call<com.test.sample.hirecooks.Models.VendersSubCategory.Result> getVendersSubCategory();
}
