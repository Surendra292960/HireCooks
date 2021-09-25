package com.test.sample.hirecooks.Utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.test.sample.hirecooks.Models.TokenResponse.Token;
import com.test.sample.hirecooks.Models.Users.User;

public class SharedPrefManager {
    private static SharedPrefManager mInstance;
    private static Context mCtx;
    private static SharedPreferences pref;
    private static SharedPreferences.Editor editor;
    private static int PRIVATE_MODE = 0;
    private static final String IS_FIRST_TIME_LAUNCH = "IsFirstTimeLaunch";
    private static final String SHARED_PREF_NAME = "simplifiedcodingsharedprefretrofit";
    //Users
    private static final String KEY_USER_ID = "keyuserid";
    private static final String KEY_USER_NAME = "keyusername";
    private static final String KEY_USER_EMAIL = "keyuseremail";
    private static final String KEY_USER_PHONE = "keyuserphone";
    private static final String KEY_USER_PASSWORD = "keyuserpassword";
    private static final String KEY_USER_GENDER = "keyusergender";
    private static final String KEY_USER_TYPE = "keyusertype";
    private static final String KEY_USER_IMAGE = "keyuserImage" ;
    private static final String KEY_USER_SIGNUPDATE = "keyusersignupdate";
    private static final String KEY_USER_ADDRESS = "keyuseraddress";
    private static final String KEY_USER_PINCODE = "keyuserpincode";
    private static final String KEY_FIRM_ID = "keyfirmid";
    private static final String KEY_TOKEN_ID = "keytokenid";
    private static final String KEY_TOKEN = "keytoken";
    private static final String KEY_DEVICE_ID= "keydeviceid";
    private static final String KEY_BIKE_NUMBER = "bikeNumber";
    private static final String KEY_STATUS = "key";

    public SharedPrefManager(Context context) {
        mCtx = context;
        pref = mCtx.getSharedPreferences(SHARED_PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    public static synchronized SharedPrefManager getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new SharedPrefManager(context);
        }
        return mInstance;
    }

    public void setFirstTimeLaunch(boolean isFirstTime) {
        editor.putBoolean(IS_FIRST_TIME_LAUNCH, isFirstTime);
        editor.commit();
    }

    public boolean isFirstTimeLaunch() {
        return pref.getBoolean(IS_FIRST_TIME_LAUNCH, true);
    }

    public boolean token(Token token) {
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(KEY_TOKEN_ID, token.getTokenId());
        editor.putString(KEY_TOKEN, token.getToken());
        editor.putString(KEY_DEVICE_ID, token.getDeviceId());
        editor.apply();
        return true;
    }

    public Token getTokens() {
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        return new Token(
                sharedPreferences.getInt(KEY_TOKEN_ID, 0),
                sharedPreferences.getString(KEY_TOKEN, null),
                sharedPreferences.getString(KEY_DEVICE_ID, null));
    }

    public boolean userLogin(User user) {
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(KEY_USER_ID,  user.getId() );
        editor.putString(KEY_USER_NAME, user.getName());
        editor.putString(KEY_USER_EMAIL, user.getEmail());
        editor.putString(KEY_USER_PASSWORD, user.getPassword());
        editor.putString(KEY_USER_PHONE, user.getPhone());
        editor.putString(KEY_USER_GENDER, user.getGender());
        editor.putString(KEY_USER_TYPE, user.getUserType());
        editor.putString(KEY_USER_IMAGE, user.getImage());
        editor.putString(KEY_USER_SIGNUPDATE, user.getSignupDate());
        editor.putString(KEY_USER_ADDRESS, user.getAddress());
        editor.putString(KEY_USER_PINCODE, user.getPincode());
        editor.putString(KEY_FIRM_ID, user.getFirmId());
        editor.putString(KEY_STATUS, user.getStatus());
        editor.putString(KEY_BIKE_NUMBER, user.getBikeNumber());
        editor.apply();
        return true;
    }

    public boolean isLoggedIn() {
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        if (sharedPreferences.getString(KEY_USER_EMAIL, null) != null)
            return true;
        return false;
    }

    public User getUser() {
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        return new User(
                sharedPreferences.getInt(KEY_USER_ID, 0),
                sharedPreferences.getString(KEY_USER_NAME, null),
                sharedPreferences.getString(KEY_USER_EMAIL, null),
                sharedPreferences.getString(KEY_USER_PASSWORD, null),
                sharedPreferences.getString(KEY_USER_PHONE, null),
                sharedPreferences.getString(KEY_USER_GENDER, null),
                sharedPreferences.getString(KEY_USER_TYPE, null),
                sharedPreferences.getString(KEY_USER_IMAGE, null),
                sharedPreferences.getString(KEY_USER_SIGNUPDATE, null),
                sharedPreferences.getString(KEY_USER_ADDRESS, null),
                sharedPreferences.getString(KEY_USER_PINCODE, null),
                sharedPreferences.getString(KEY_FIRM_ID, null),
                sharedPreferences.getString(KEY_STATUS, null),
                sharedPreferences.getString(KEY_BIKE_NUMBER, null));
    }

    public boolean logout() {
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
        return true;
    }


    public void savePrefValue(String key,String value){
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key,value);
        editor.commit();
    }

    public String getPrefValue(String key){
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getString(key, null);
    }
}