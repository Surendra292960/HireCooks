package com.test.sample.hirecooks.Utils;
import com.test.sample.hirecooks.ApiServiceCall.ShoppingDrinkApiCall.ApiClient;
import com.test.sample.hirecooks.WebApis.UserApi;
import static com.test.sample.hirecooks.Utils.APIUrl.SHOPPINGDRINK_BASE_URL;

public class Common {

    public static UserApi getAPI() {
        return ApiClient.getClient(SHOPPINGDRINK_BASE_URL).create(UserApi.class);
    }

    public static String CATEGORY_NAME = null;
    public static String NEW_PRODUCT_CATEGORY_NAME = null;
}
