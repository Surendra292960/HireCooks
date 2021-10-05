package com.test.sample.hirecooks.Utils;

import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.view.MotionEvent;
import android.view.View;

import com.google.android.gms.maps.model.LatLng;
import com.test.sample.hirecooks.Models.MapLocationResponse.Map;
import com.test.sample.hirecooks.Models.SubCategory.Subcategory;
import com.test.sample.hirecooks.Models.TokenResponse.TokenResult;
import com.test.sample.hirecooks.Models.Users.User;

import java.util.List;

public class Constants {
    public static final String SKU_DELAROY_MONTHLY = "delaroy_monthly";
    public static final String SKU_DELAROY_THREEMONTH = "delaroy_threemonth";
    public static final String SKU_DELAROY_SIXMONTH = "delaroy_sixmonth";
    public static final String SKU_DELAROY_YEARLY = "delaroy_yearly";
    public static final String base64EncodedPublicKey = "";
    public static List<Map> NEARBY_COOKS = null;
    public static LatLng USER_CURRENT_LOCATION = null;
    public static String USER_PROFILE = null;
    public static String locationApiKey = "AIzaSyC-BYCFrpXUa4CI-H9fRqWEc0-I_ylk31k";
    String s = "AIzaSyBm_OQWOR7nRG7uPjRgtkeXwHSjWIbjmz4";
    public static User CurrentUser = null;
    public static String CurrentUserPhoneNumber = null;
    public static String add_status_type = null;
    public static TokenResult CurrentToken = null;
    public static List<Subcategory> SUBCATEGORYs = null;
    public static List<Map> NEARBY_VENDERS_LOCATION = null;
    public static User SIGNUP_USER = null;
    public static List<User> NEARBY_VENDERS = null;

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
