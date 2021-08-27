package com.test.sample.hirecooks.Utils;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.razorpay.Checkout;
import com.razorpay.PaymentResultListener;

import org.json.JSONObject;

import static com.paytm.pgsdk.easypay.actions.EasypayBrowserFragment.TAG;

public class RazorpayPayment implements PaymentResultListener {
    private Context mCtx;

    public RazorpayPayment(Context mCtx){
        this.mCtx = mCtx;
    }
    public void startPayment(String orderamount) {
        /**
         * You need to pass current activity in order to let Razorpay create CheckoutActivity
         */
       // final Activity activity = this;

        final Checkout co = new Checkout();

        try {
            JSONObject options = new JSONObject();
            options.put("name", "HireCooks");
            options.put("description", "HireCooks Payment");
            //You can omit the image option to fetch the image from dashboard
            options.put("image", "https://ibb.co/j6CZk3C");
            options.put("currency", "INR");
            //String payment = orderamount.getText().toString();
            String payment = orderamount;
            // amount is in paise so please multiple it by 100
            //Payment failed Invalid amount (should be passed in integer paise. Minimum value is 100 paise, i.e. ₹ 1)
            double total = Double.parseDouble(payment);
            total = total * 100;
            options.put("amount", total);

            JSONObject preFill = new JSONObject();
            preFill.put("email", "hirecook@gmail.com");
            preFill.put("contact", "7055292960");

            options.put("prefill", preFill);

            co.open((Activity) mCtx, options);
        } catch (Exception e) {
            Toast.makeText(mCtx, "Error in payment: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    @Override
    public void onPaymentSuccess(String s) {
        // payment successfull pay_DGU19rDsInjcF2
        Log.e(TAG, " payment successfull "+ s.toString());
        Toast.makeText(mCtx, "Payment successfully done! " +s, Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onPaymentError(int i, String s) {
        Log.e(TAG,  "error code "+String.valueOf(i)+" -- Payment failed "+s.toString()  );
        try {
            Toast.makeText(mCtx, "Payment error please try again", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Log.e("OnPaymentError", "Exception in onPaymentError", e);
        }
    }
}
