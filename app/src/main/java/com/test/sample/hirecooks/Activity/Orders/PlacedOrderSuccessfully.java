package com.test.sample.hirecooks.Activity.Orders;

import android.Manifest;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.AnimatedVectorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.telephony.SmsManager;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.vectordrawable.graphics.drawable.AnimatedVectorDrawableCompat;

import com.test.sample.hirecooks.Activity.Home.MainActivity;
import com.test.sample.hirecooks.Models.NewOrder.OrdersTable;
import com.test.sample.hirecooks.Models.Users.User;
import com.test.sample.hirecooks.R;
import com.test.sample.hirecooks.Utils.SharedPrefManager;

import java.util.List;

public class PlacedOrderSuccessfully extends AppCompatActivity {
    private static final int MY_PERMISSIONS_REQUEST_SEND_SMS =0 ;
    private AppCompatImageView done;
    private AnimatedVectorDrawableCompat avd;
    private AnimatedVectorDrawable avd2;
    private TextView ordersTable_id, ordersTable_totalAmount;
    private List<OrdersTable> ordersTableList;
    private User user;
    private String phoneNo,message;

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
            ordersTableList = (List<OrdersTable>)bundle.getSerializable("OrdersTable");
            if(ordersTableList!=null&&ordersTableList.size()!=0){
                for(OrdersTable ordersTable:ordersTableList){
                    setData(ordersTable);
                }
            }
        }
    }

    private void setData(OrdersTable ordersTable) {
        ordersTable_id.setText("OrderId #"+ordersTable.getOrder_id());
        ordersTable_totalAmount.setText("Total Amount : " +ordersTable.getTotal_amount());
        new Handler().postDelayed( new Runnable() {

            @Override
            public void run() {
                finish();
             startActivity( new Intent( PlacedOrderSuccessfully.this,MainActivity.class ) .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK) );
             sendMessage(ordersTable);
            }
        }, 3000);
    }

    protected void sendMessage(OrdersTable ordersTable) {
        String message = "Thanks for ordering. We recieved your order and will begin processing it soon. your orderId: "+ordersTable.getOrder_id();
        SmsManager smsManager = SmsManager.getDefault();
        smsManager.sendTextMessage(R.string.app_name+" "+user.getPhone(), null, message, null, null);
        System.out.println("Suree : Sent");
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void initViews() {
        user = SharedPrefManager.getInstance(this).getUser();
        done = findViewById(R.id.done);
        ordersTable_id = findViewById(R.id.order_id);
        ordersTable_totalAmount = findViewById(R.id.order_totalAmount);

        Drawable drawable = done.getDrawable();
        if(drawable instanceof AnimatedVectorDrawableCompat){
            avd = (AnimatedVectorDrawableCompat) drawable;
            avd.start();
        }else if(drawable instanceof AnimatedVectorDrawable) {
            avd2 = (AnimatedVectorDrawable) drawable;
            avd2.start();
        }
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        this.finish();
        startActivity( new Intent( PlacedOrderSuccessfully.this, MainActivity.class ) .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK) );
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id==android.R.id.home){
            this.finish();
            startActivity( new Intent( PlacedOrderSuccessfully.this,MainActivity.class ) .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK) );
        }
        return super.onOptionsItemSelected(item);
    }
}
