package com.test.sample.hirecooks.Activity.Orders;
import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.test.sample.hirecooks.Models.Order.Order;
import com.test.sample.hirecooks.R;

import java.util.Objects;

public class RecievedOrderDetails extends AppCompatActivity {
    private TextView order_id, product_name, product_sellRate, product_displayRate, product_discount,
            product_quantity, product_totalAmount, order_status, firm_id, userId,
            order_date_time, order_weight, payment_method, order_address, name, email, phone, firm_address, order_confirm;
    private Order orders;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recieved_order_details);
        Objects.requireNonNull(getSupportActionBar()).setHomeButtonEnabled(true);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("RecievedOrderDetails");
        Bundle bundle = getIntent().getExtras();
        if(bundle!=null){
            orders = (Order)bundle.getSerializable("Orders");
        }

        initViews();
        setData(orders);
    }

    @SuppressLint("SetTextI18n")
    private void setData(Order orders) {
       if(orders!=null){
           order_id.setText("   =>  # "+orders.getOrderId());
           product_name.setText("   =>  "+orders.getProductName());
           product_sellRate.setText("   =>  "+orders.getProductSellRate());
           product_displayRate.setText("    =>  "+orders.getProductDisplayRate());
           product_discount.setText("   =>  "+orders.getProductDiscount()+" %");
           product_quantity.setText("   =>  "+orders.getProductQuantity());
           product_totalAmount.setText("    =>  "+orders.getProductTotalAmount());
           order_status.setText("   =>  "+orders.getOrderStatus());
           firm_id.setText("    =>  "+orders.getFirmId());
           userId.setText("     =>  "+orders.getUserId());
           order_date_time.setText("    =>  "+orders.getOrderDateTime());
           order_weight.setText("   =>  "+orders.getOrderWeight());
           payment_method.setText("     =>  "+orders.getPaymentMethod());
           order_address.setText("     =>   "+orders.getOrderAddress());
           name.setText("     =>    "+orders.getName());
           email.setText("    =>    "+orders.getEmail());
           phone.setText("    =>    "+orders.getPhone());
           order_confirm.setText("    =>    "+orders.getOrderConfirm());
           firm_address.setText("   =>  "+orders.getFirmAddress());
       }
    }

    private void initViews() {
        order_id = findViewById(R.id.order_id);
        product_name = findViewById(R.id.product_name);
        product_sellRate = findViewById(R.id.product_sellRate);
        product_displayRate = findViewById(R.id.product_displayRate);
        product_discount = findViewById(R.id.product_discount);
        product_quantity = findViewById(R.id.product_quantity);
        product_totalAmount = findViewById(R.id.product_totalAmount);
        order_status = findViewById(R.id.order_status);
        firm_id = findViewById(R.id.firm_id);
        userId = findViewById(R.id.userId);
        order_date_time = findViewById(R.id.order_date_time);
        order_weight = findViewById(R.id.order_weight);
        payment_method = findViewById(R.id.payment_method);
        order_confirm = findViewById(R.id.order_confirm);
        order_address = findViewById(R.id.order_address);
        name = findViewById(R.id.username);
        email = findViewById(R.id.email);
        phone = findViewById(R.id.phone);
        firm_address = findViewById(R.id.firm_address);
    }

    @Override
    public void onBackPressed() {
        this.finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            this.finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
