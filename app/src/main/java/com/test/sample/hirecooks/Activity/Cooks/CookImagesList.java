package com.test.sample.hirecooks.Activity.Cooks;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.MenuItem;
import android.view.View;

import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.test.sample.hirecooks.Adapter.Cooks.CooksImagesAdapter;
import com.test.sample.hirecooks.Models.Users.User;
import com.test.sample.hirecooks.Models.cooks.Request.CooksImages;
import com.test.sample.hirecooks.Models.cooks.Request.CooksImagesResult;
import com.test.sample.hirecooks.R;
import com.test.sample.hirecooks.Utils.BaseActivity;
import com.test.sample.hirecooks.Utils.Common;
import com.test.sample.hirecooks.Utils.ProgressBarUtil;
import com.test.sample.hirecooks.WebApis.CookImages;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class CookImagesList extends BaseActivity {
    private RecyclerView cooks_images_recycler_view;
    private FloatingActionButton add_cook_images;
    private User users;
    private CookImages mService = Common.getCookImagesAPI();
    private ProgressBarUtil progressBarUtil;
    private List<CooksImages> imageList = new ArrayList<>(  );
    private SwipeRefreshLayout mSwipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_cook_images_list );
        Objects.requireNonNull(getSupportActionBar()).setHomeButtonEnabled(true);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        Objects.requireNonNull(getSupportActionBar()).setTitle("Cook Images");

        initViews();
        Bundle bundle = getIntent().getExtras();
        if(bundle!=null){
            users = (User)bundle.getSerializable("Cooks");
            if(users!=null){
                ApiServiceCall(users);
            }
        }
    }

    private void initViews() {
        progressBarUtil = new ProgressBarUtil( this );
        cooks_images_recycler_view = findViewById( R.id.cooks_images_recycler_view );
        mSwipeRefreshLayout = findViewById( R.id.swipeToRefresh );
        add_cook_images = findViewById( R.id.add_cook_images );

        add_cook_images.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(imageList.size()==4){
                    ShowToast( "You Can`t add more than 4 Images" );
                }else{
                    Bundle bundle = new Bundle(  );
                    Intent intent = new Intent( CookImagesList.this, AddCookImages.class );
                    bundle.putSerializable( "Cooks", users );
                    intent.putExtras( bundle );
                    intent .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity( intent );
                }
            }
        } );

        mSwipeRefreshLayout = findViewById(R.id.swipeToRefresh);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new Handler().postDelayed( new Runnable() {
                    @Override
                    public void run() {
                        mSwipeRefreshLayout.setRefreshing(false);
                        if(users!=null){
                            ApiServiceCall(users);
                        }
                    }
                }, 3000);
            }
        });
    }

    private void ApiServiceCall(User users) {
        progressBarUtil.showProgress();
        mService.getCookImagesByUserId(users.getId())
                .subscribeOn( Schedulers.io())
                .observeOn( AndroidSchedulers.mainThread())
                .unsubscribeOn(Schedulers.io())
                .subscribe(new Observer<CooksImagesResult>() {

                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(CooksImagesResult result) {
                        if(!result.getError()){
                           imageList = result.getImages();
                            setAdapter(result.getImages());
                        }else{
                            ShowToast( result.getMessage() );
                        }
                    }

                    @Override
                    public void onError(Throwable t) {
                        progressBarUtil.hideProgress();
                        ShowToast(t.getMessage());
                        System.out.println("New data received: " + t.getMessage());
                    }

                    @Override
                    public void onComplete() {
                        progressBarUtil.hideProgress();
                    }
                });
    }

    private void setAdapter(List<CooksImages> result) {
        CooksImagesAdapter imagesAdapter = new CooksImagesAdapter( this,result ,users);
        cooks_images_recycler_view.setAdapter( imagesAdapter );
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id==android.R.id.home){
            this.finish();
        }
        return super.onOptionsItemSelected( item );
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (users != null) {
            ApiServiceCall( users );
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(users!=null){
            ApiServiceCall(users);
        }
    }
}
