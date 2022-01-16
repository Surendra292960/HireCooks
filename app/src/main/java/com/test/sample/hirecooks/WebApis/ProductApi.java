package com.test.sample.hirecooks.WebApis;

import com.test.sample.hirecooks.ApiServiceCall.Retry;
import com.test.sample.hirecooks.Models.Category.Category;
import com.test.sample.hirecooks.Models.Category.CategoryResponse;
import com.test.sample.hirecooks.Models.MapLocationResponse.Example;
import com.test.sample.hirecooks.Models.SubCategory.ColorExample;
import com.test.sample.hirecooks.Models.SubCategory.SubcategoryResponse;
import com.test.sample.hirecooks.Models.SubCategory.ImageExample;
import com.test.sample.hirecooks.Models.SubCategory.SizeExample;
import com.test.sample.hirecooks.Models.SubCategory.WeightExample;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface ProductApi {
    @Retry
    @GET("subCategoryBy_sub/{sub_id}")
    Call<ArrayList<SubcategoryResponse>> getSubCategorysBySub_id(
            @Path( "sub_id" ) int subcategoryid);
    @Retry
    @GET("subCategory")
    Call<ArrayList<SubcategoryResponse>> getSubCategorys();

    @FormUrlEncoded
    @POST("getNearBySubCategory")
    Call<List<SubcategoryResponse>> getNearBySubCategory(
            @Field("latitude") String latitude,
            @Field("longitude") String longitude);

    @FormUrlEncoded
    @POST("getNearBySubCategoryBySub_id")
    Call<List<SubcategoryResponse>> getNearBySubCategoryBySub_id(
            @Field("latitude") String latitude,
            @Field("longitude") String longitude,
            @Field("sub_id") int id);


    @FormUrlEncoded
    @POST("searchNearBySubCategory")
    Call<List<SubcategoryResponse>> searchNearBySubCategory(
            @Field("latitude") String latitude,
            @Field("longitude") String longitude,
            @Field("search_key") String search_key);

    @DELETE("subCategory/{id}")
    Call<ArrayList<SubcategoryResponse>> deleteSubCategory(@Path( "id" ) String product_uniquekey);

    @POST("subCategory/{serach_key}")
    Call<ArrayList<SubcategoryResponse>> searchAllProducts(@Path("serach_key") String search_key);

    @PUT("Category/{id}")
    Call<ArrayList<CategoryResponse>> updateCategory(
            @Path( "id" ) int id, @Body ArrayList<Category> category);

    @POST("Category")
    Call<ArrayList<CategoryResponse>> addCategory(@Body ArrayList<Category> category);

    @POST("Category")
    Call<List<SubcategoryResponse>> addSubCategory(@Body List<SubcategoryResponse> subcategory);

    @PUT("colors/{id}")
    Call<List<ColorExample>> updateColor(@Path( "id" ) int id, @Body List<ColorExample> colorList);

    @PUT("sizes/{id}")
    Call<List<SizeExample>> updateSize(@Path( "id" ) int id, @Body List<SizeExample> sizeExampleList);

    @PUT("images/{id}")
    Call<List<ImageExample>> updateImage(@Path( "id" ) int id, @Body List<ImageExample> imageExampleList);

    @PUT("weights/{id}")
    Call<List<WeightExample>> updateWeight(@Path( "id" ) int id, @Body  List<WeightExample> weightExampleList);

    @GET("weights/{x_id}")
    Call<List<WeightExample>> getProductWeight(@Path( "x_id" ) String id);

    @Retry
    @GET("colors/{x_id}")
    Call<List<ColorExample>> getProductColor(@Path( "x_id" ) String id);

    @Retry
    @GET("sizes/{x_id}")
    Call<List<SizeExample>> getProductSize(@Path( "x_id" ) String id);

    @Retry
    @GET("weights}")
    Call<List<WeightExample>> getSelectWeight();

    @Retry
    @GET("select_color")
    Call<List<ColorExample>> getSelectColor();

    @Retry
    @GET("select_sizes")
    Call<List<SizeExample>> getSelectSize();

    @POST("subCategory")
    Call<ArrayList<SubcategoryResponse>> addSubcategory(@Body List<SubcategoryResponse> exampleList);

    @PUT("subCategory/{id}")
    Call<ArrayList<SubcategoryResponse>> updateSubcategory(@Path( "id" ) int id, @Body List<SubcategoryResponse> exampleList);

    @PATCH("subCategory/{firm_id}")
    Call<ArrayList<SubcategoryResponse>> accepting_Orders(@Path("firm_id")String firmId, @Body List<SubcategoryResponse> examples);

    @Retry
    @GET("getSubCategorysByFirmId/{firm_id}")
    Call<ArrayList<SubcategoryResponse>> getSubCategorysByFirmId(@Path( "firm_id" ) String id);

    Call<List<SubcategoryResponse>> getNearBySubCategoryByPaging(int i, int firstPage, int pageSize);
}
