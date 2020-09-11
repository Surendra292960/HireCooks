package com.test.sample.hirecooks.Activity.Orders;

import android.graphics.drawable.AnimatedVectorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.vectordrawable.graphics.drawable.AnimatedVectorDrawableCompat;

import com.test.sample.hirecooks.Models.Order.Order;
import com.test.sample.hirecooks.R;

public class PlacedOrderSuccessfully extends AppCompatActivity {
    private AppCompatImageView done;
    private AnimatedVectorDrawableCompat avd;
    private AnimatedVectorDrawable avd2;
    private Order order;
    private TextView order_id, order_totalAmount;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_placed_order_successfully);
        this.getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        |View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        |View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        |View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        |View.SYSTEM_UI_FLAG_FULLSCREEN
                        |View.SYSTEM_UI_FLAG_IMMERSIVE);

        initViews();
        Bundle bundle = getIntent().getExtras();
        if(bundle!=null){
            order = (Order)bundle.getSerializable("Order");
            if(order!=null){
                setData();
            }
        }
    }

    private void setData() {
        order_id.setText("OrderId #"+order.getOrderId());
        order_totalAmount.setText("Total Amount : "+order.getProductTotalAmount());
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void initViews() {
        done = findViewById(R.id.done);
        order_id = findViewById(R.id.order_id);
        order_totalAmount = findViewById(R.id.order_totalAmount);

        Drawable drawable = done.getDrawable();
        if(drawable instanceof AnimatedVectorDrawableCompat){
            avd = (AnimatedVectorDrawableCompat) drawable;
            avd.start();
        }else if(drawable instanceof AnimatedVectorDrawable) {
            avd2 = (AnimatedVectorDrawable) drawable;
            avd2.start();
        }
    }
}
