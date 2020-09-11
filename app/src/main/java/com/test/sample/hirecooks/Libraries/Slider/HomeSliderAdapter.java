package com.test.sample.hirecooks.Libraries.Slider;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;
import com.test.sample.hirecooks.R;

public class HomeSliderAdapter extends PagerAdapter {
    private Context mCtx;
    private LayoutInflater layoutInflater;
    private String[] text;
    private Integer[] images;

    public HomeSliderAdapter(Context mCtx, String[] text,Integer[] images) {
        this.mCtx = mCtx;
        this.text = text;
        this.images = images;
    }

    @Override
    public int getCount() {
        return text==null ? 0 : text.length;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, final int position) {

        layoutInflater = (LayoutInflater) mCtx.getSystemService(mCtx.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.profile_slider, null);
        ImageButton imageView = view.findViewById(R.id.imageView);
        imageView.setImageResource(images[position]);
        TextView textview = view.findViewById(R.id.textView);
        textview.setText(text[position]);

        ViewPager vp = (ViewPager) container;
        vp.addView(view, 0);
        
        textview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(mCtx, "clicked", Toast.LENGTH_SHORT).show();
            }
        });
        return view;

    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {

        ViewPager vp = (ViewPager) container;
        View view = (View) object;
        vp.removeView(view);

    }
}