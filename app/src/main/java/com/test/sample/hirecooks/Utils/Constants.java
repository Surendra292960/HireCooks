package com.test.sample.hirecooks.Utils;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.view.MotionEvent;
import android.view.View;

import com.test.sample.hirecooks.Models.MapLocationResponse.Map;
import com.test.sample.hirecooks.Models.OfferSubCategory.OffersSubcategory;
import com.test.sample.hirecooks.Models.SubCategory.Response.SubCategory;
import com.test.sample.hirecooks.Models.TokenResponse.TokenResult;
import com.test.sample.hirecooks.Models.UsersResponse.UserResponse;
import com.test.sample.hirecooks.Models.users.Result;
import com.test.sample.hirecooks.Models.users.User;

import java.util.List;

public class Constants {
    public static String USER_PROFILE = null;
    public static String locationApiKey = "AIzaSyC-BYCFrpXUa4CI-H9fRqWEc0-I_ylk31k";
    public static Result CurrentUser = null;
    public static String CurrentUserPhoneNumber = null;
    public static TokenResult CurrentToken = null;
    public static List<SubCategory> SUBCATEGORY = null;
    public static List<OffersSubcategory> OFFER_SUBCATEGORY = null;
    public static List<com.test.sample.hirecooks.Models.SearchSubCategory.Search> SEARCH = null;
    public static List<Map> NEARBY_USER_LOCATION = null;
    public static User SIGNUP_USER = null;
    public static List<UserResponse> NEARBY_VENDERS = null;

    //make bg a little bit darker
    public static final View.OnTouchListener FOCUS_TOUCH_LISTENER = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            Drawable drawable = v.getBackground();
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                case MotionEvent.ACTION_BUTTON_PRESS:

                    drawable.setColorFilter(0x20000000, PorterDuff.Mode.SRC_ATOP);
                    v.invalidate();
                    break;
                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL:
                    drawable.clearColorFilter();
                    v.invalidate();
                    break;
            }
            return false;
        }
    };
}
