package com.test.sample.hirecooks.Utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.test.sample.hirecooks.Models.TokenResponse.Token;

public class SharedPrefToken {
    private static SharedPrefToken mInstance;
    private static Context mCtx;
    private static SharedPreferences pref;
    private static SharedPreferences.Editor editor;
    private static int PRIVATE_MODE = 0;
    private static final String SHARED_PREF_NAME = "simplifiedcodingsharedprefretrofit";
    private static final String KEY_FIRM_ID = "keyfirmid";
    private static final String KEY_TOKEN_ID = "keytokenid";
    private static final String KEY_TOKEN = "keytoken";
    private static final String KEY_DEVICE_ID= "keydeviceid";


    public SharedPrefToken(Context context) {
        mCtx = context;
        pref = mCtx.getSharedPreferences(SHARED_PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    public static synchronized SharedPrefToken getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new SharedPrefToken(context);
        }
        return mInstance;
    }

    public boolean tokens(Token token) {
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
}
