package com.test.sample.hirecooks.Activity.Orders;
import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.test.sample.hirecooks.Models.NewOrder.Order;
import com.test.sample.hirecooks.R;
import java.util.Objects;

public class RecievedOrderDetails extends AppCompatActivity {
    private TextView order_id, product_name, product_sellRate, product_displayRate,
            product_discount, product_quantity, product_totalAmount, firm_id, firm_address;
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
    private void setData(com.test.sample.hirecooks.Models.NewOrder.Order orders) {
       if(orders!=null){
           order_id.setText(            " Order ID      :  # "+orders.getOrderId());
           product_name.setText(        "Product Name   :  "+orders.getName());
           product_sellRate.setText(    "Sellrate       :  "+orders.getSellRate());
           product_displayRate.setText( "DisplayRate    :  "+orders.getDisplayRate());
           product_discount.setText(    "Discount       :  "+orders.getDiscount());
           product_quantity.setText(    "Quantity       :  "+orders.getQuantity());
           product_totalAmount.setText( "Total Amount   :  "+orders.getTotalAmount());
           firm_id.setText(             "FirmId         :  "+orders.getFirmId());
           firm_address.setText(        "Address        :  "+orders.getFirmAddress());
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
        firm_id = findViewById(R.id.firm_id);
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
