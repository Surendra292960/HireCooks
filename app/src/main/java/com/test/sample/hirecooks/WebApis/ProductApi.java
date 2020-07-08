package com.test.sample.hirecooks.WebApis;

import com.test.sample.hirecooks.Models.NewProductsCategory.NewProductCategories;
import com.test.sample.hirecooks.Models.NewProductsSubCategory.NewProductSubcategories;
import com.test.sample.hirecooks.Models.OfferCategory.OffersCategories;
import com.test.sample.hirecooks.Models.OfferSubCategory.OffersSubcategories;
import com.test.sample.hirecooks.Models.SearchSubCategory.Result;
import com.test.sample.hirecooks.Models.SubCategory.Response.SubCategories;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;

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

}
