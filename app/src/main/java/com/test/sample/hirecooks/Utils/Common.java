package com.test.sample.hirecooks.Utils;

import com.test.sample.hirecooks.ApiServiceCall.ApiClient;
import com.test.sample.hirecooks.WebApis.CookImages;
import com.test.sample.hirecooks.WebApis.MapApi;

public class Common {
    public static String CATEGORY_NAME = null;
    public static String NEW_PRODUCT_CATEGORY_NAME = null;

    public static MapApi getAPI() {
        return ApiClient.getClient().create( MapApi.class);
    }

    public static CookImages getCookImagesAPI() {
        return ApiClient.getClient().create( CookImages.class);
    }
}
