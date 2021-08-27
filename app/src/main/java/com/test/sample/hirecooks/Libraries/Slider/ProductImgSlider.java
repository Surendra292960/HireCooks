package com.test.sample.hirecooks.Libraries.Slider;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.viewpager.widget.PagerAdapter;

import com.squareup.picasso.Picasso;
import com.test.sample.hirecooks.Models.SubCategory.Subcategory;
import com.test.sample.hirecooks.R;

public class ProductImgSlider extends PagerAdapter {
    private Context mCtx;
    private LayoutInflater layoutInflater;
    Subcategory images;

    public ProductImgSlider(Context mCtx, Subcategory images) {
        this.mCtx = mCtx;
        this.images = images;
    }

    @Override
    public int getCount() {
        return images==null ? 0 : images.getImages().size();
    }

    @Override
    public boolean isViewFromObject(View view, Object o) {
        return view == o;
    }

    @Override
    public  Object instantiateItem(ViewGroup container, int position) {
        View view = LayoutInflater.from(mCtx).inflate(R.layout.product_imgslider, container, false);
        ImageView imageView =  view.findViewById(R.id.imageView);
        if(images.getImages().get( position ).getImage()!=null){
            Picasso.with( mCtx ).load( images.getImages().get( position ).getImage() ).into( imageView );
        }
        container.addView(view,0);
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