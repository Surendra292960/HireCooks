package com.test.sample.hirecooks.WebApis;

import com.test.sample.hirecooks.Models.Category.Category;
import com.test.sample.hirecooks.Models.NewProductsCategory.NewProductCategories;
import com.test.sample.hirecooks.Models.NewProductsSubCategory.NewProductSubcategories;
import com.test.sample.hirecooks.Models.OfferCategory.OffersCategories;
import com.test.sample.hirecooks.Models.OfferSubCategory.OffersSubcategories;
import com.test.sample.hirecooks.Models.SearchSubCategory.Result;
import com.test.sample.hirecooks.Models.SubCategory.ColorExample;
import com.test.sample.hirecooks.Models.SubCategory.Example;
import com.test.sample.hirecooks.Models.SubCategory.ImageExample;
import com.test.sample.hirecooks.Models.SubCategory.Response.SubCategories;
import com.test.sample.hirecooks.Models.SubCategory.SizeExample;
import com.test.sample.hirecooks.Models.SubCategory.WeightExample;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface ProductApi {

    ///Offers
    @GET("getOfferSubCategory")
    Call<OffersSubcategories> getOfferSubCategory();

    @GET("getOfferCategory")
    Call<OffersCategories> getOffersCategory();

    @FormUrlEncoded
    @POST("getOfferSubCategoryBySubCategoryId")
    Call<OffersSubcategories> getOfferSubCategory(
            @Field("subcategoryid") int subcategoryid);

    //Popular Products
    @FormUrlEncoded
    @POST("getSubCategoryBySubCategoryId")
    Call<SubCategories> getSubCategory(
            @Field("subcategoryid") int subcategoryid);

    @GET("getSubCategory")
    Call<SubCategories> getSubCategory();



    //New Products
    @GET("getNewProductCategory")
    Call<NewProductCategories> getNewProductCategory();

    @FormUrlEncoded
    @POST("getNewProductSubCategoryBySubCategoryId")
    Call<NewProductSubcategories> getNewProductSubCategory(@Field("subcategoryid") int subcategoryid);

    @GET("getNewProductSubCategory")
    Call<NewProductSubcategories> getNewProductSubCategory();

    @GET("searchProducts")
    Call<Result> searchProducts();

    @GET("subCategoryBy_sub/{sub_id}")
    Call<ArrayList<Example>> getSubCategorysBySub_id(
            @Path( "sub_id" ) int subcategoryid);

    @GET("subCategory")
    Call<ArrayList<Example>> getSubCategorys();

    @POST("subCategory/{serach_key}")
    Call<ArrayList<Example>> searchAllProducts(@Path("serach_key") String search_key);

    @PUT("Category/{id}")
    Call<ArrayList<com.test.sample.hirecooks.Models.Category.Example>> updateCategory(
            @Path( "id" ) int id, @Body ArrayList<Category> category);

    @POST("Category")
    Call<ArrayList<com.test.sample.hirecooks.Models.Category.Example>> addCategory(@Body ArrayList<Category> category);

    @POST("Category")
    Call<List<Example>> addSubCategory(@Body List<Example> subcategory);

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

    @GET("colors/{x_id}")
    Call<List<ColorExample>> getProductColor(@Path( "x_id" ) String id);

    @GET("sizes/{x_id}")
    Call<List<SizeExample>> getProductSize(@Path( "x_id" ) String id);

    @GET("weights}")
    Call<List<WeightExample>> getSelectWeight();

    @GET("select_color")
    Call<List<ColorExample>> getSelectColor();

    @GET("select_sizes")
    Call<List<SizeExample>> getSelectSize();

    @POST("subCategory")
    Call<ArrayList<Example>> addSubcategory(@Body List<Example> exampleList);

    @PUT("subCategory/{id}")
    Call<ArrayList<Example>> updateSubcategory(@Path( "id" ) int id, @Body List<Example> exampleList);
}
