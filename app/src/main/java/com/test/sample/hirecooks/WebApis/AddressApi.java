package com.test.sample.hirecooks.WebApis;

import com.test.sample.hirecooks.Models.MapLocationResponse.Maps;

import java.util.List;

import io.reactivex.Observable;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface AddressApi {

    @POST("address")
    Observable<Maps> addAddress(@Body List<Maps> address);


}
