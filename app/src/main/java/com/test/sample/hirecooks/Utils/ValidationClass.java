package com.test.sample.hirecooks.Utils;

import android.content.Context;

public class ValidationClass {
    Context context;

    public ValidationCallBack getValidationType(Context context, String validationType) {
        if (validationType != null) {
            return null;
        }

        if (validationType.equalsIgnoreCase( "ADDRESS" )) {
            return new ValidateAddress( context );
        }
        return null;
    }
}
