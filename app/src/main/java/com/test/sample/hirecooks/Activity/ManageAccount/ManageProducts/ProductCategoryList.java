package com.test.sample.hirecooks.Activity.ManageAccount.ManageProducts;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.test.sample.hirecooks.Adapter.ManageAccount.ProductsCategoryAdapter;
import com.test.sample.hirecooks.Models.Offer.Offer;
import com.test.sample.hirecooks.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ProductCategoryList extends AppCompatActivity {
    private RecyclerView recyclerView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.product_categorylist );
        recyclerView = findViewById( R.id.edit_category_recycler );
        Objects.requireNonNull(getSupportActionBar()).setHomeButtonEnabled(true);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        Objects.requireNonNull(getSupportActionBar()).setTitle("Category List");
        List<Offer> offers = new ArrayList<>();
        offers.add(new Offer(1,"Offers", "https://cmkt-image-prd.freetls.fastly.net/0.1.0/ps/4021761/910/607/m2/fpnw/wm1/zpope64kaakcxgygqinp9cz1axdtaj45we4zeeuv9uv24vuadgujqxshj5pkqrwe-.jpg?1518944844&s=03a65531af02f4353d74c0816dae35a8","https://cmkt-image-prd.freetls.fastly.net/0.1.0/ps/4021761/910/607/m2/fpnw/wm1/zpope64kaakcxgygqinp9cz1axdtaj45we4zeeuv9uv24vuadgujqxshj5pkqrwe-.jpg?1518944844&s=03a65531af02f4353d74c0816dae35a8","#ff6347"));
        offers.add(new Offer(2,"Products", "https://i.pinimg.com/originals/1f/28/13/1f28133d604d126080bda739a02847cc.jpg","https://cmkt-image-prd.freetls.fastly.net/0.1.0/ps/4021761/910/607/m2/fpnw/wm1/zpope64kaakcxgygqinp9cz1axdtaj45we4zeeuv9uv24vuadgujqxshj5pkqrwe-.jpg?1518944844&s=03a65531af02f4353d74c0816dae35a8","#FF9933"));
        offers.add(new Offer(3,"New Products", "https://5.imimg.com/data5/KH/TW/MY-9134447/big-cone-ice-cream-500x500.jpg","https://cmkt-image-prd.freetls.fastly.net/0.1.0/ps/4021761/910/607/m2/fpnw/wm1/zpope64kaakcxgygqinp9cz1axdtaj45we4zeeuv9uv24vuadgujqxshj5pkqrwe-.jpg?1518944844&s=03a65531af02f4353d74c0816dae35a8","#ff6347"));
        offers.add(new Offer(4,"Carts", "https://img.freepik.com/free-photo/closeup-smartphone-fruits-vegetables_23-2148216120.jpg?size=626&ext=jpg","https://cmkt-image-prd.freetls.fastly.net/0.1.0/ps/4021761/910/607/m2/fpnw/wm1/zpope64kaakcxgygqinp9cz1axdtaj45we4zeeuv9uv24vuadgujqxshj5pkqrwe-.jpg?1518944844&s=03a65531af02f4353d74c0816dae35a8","#FF9933"));
        getCategory(offers);
    }

    private void getCategory(List<Offer> offers) {
        recyclerView.setLayoutManager(new GridLayoutManager(this, 4));
        ProductsCategoryAdapter adapter = new ProductsCategoryAdapter( ProductCategoryList.this,offers);
        recyclerView.setAdapter ( adapter );
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id==android.R.id.home){
            this.finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
