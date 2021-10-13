package com.test.sample.hirecooks.Libraries.Slider;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import androidx.viewpager.widget.PagerAdapter;

import com.bumptech.glide.Glide;
import com.test.sample.hirecooks.Activity.ProductDatails.ViewPagerActivity;
import com.test.sample.hirecooks.Models.SubCategory.Image;
import com.test.sample.hirecooks.Models.SubCategory.Subcategory;
import com.test.sample.hirecooks.R;

import java.io.Serializable;
import java.util.List;

public class ProductImgSlider extends PagerAdapter {
    private Context mCtx;
    private LayoutInflater layoutInflater;
    List<Image> images;

    public ProductImgSlider(Context mCtx, List<Image> images) {
        this.mCtx = mCtx;
        this.images = images;
    }

    @Override
    public int getCount() {
        return images==null ? 0 : images.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object o) {
        return view == o;
    }

    @Override
    public  Object instantiateItem(ViewGroup container, int position) {
        View view = LayoutInflater.from(mCtx).inflate(R.layout.product_imgslider, container, false);
        ImageView imageView =  view.findViewById(R.id.imageView);
        imageView.setVisibility(View.VISIBLE);
        LinearLayout image_slider_lay =  view.findViewById(R.id.image_slider_lay);
        if(images.get( position ).getImage()!=null){
            Glide.with(mCtx).load( images.get( position ).getImage() ).into( imageView);
        }
        container.addView(view,0);

        image_slider_lay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mCtx, ViewPagerActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("Images", (Serializable) images);
                intent.putExtras(bundle);
                mCtx.startActivity(intent);
            }
        });
        return view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    @Override
    public int getItemPosition(Object object) {
        return super.getItemPosition(object);
    }
}