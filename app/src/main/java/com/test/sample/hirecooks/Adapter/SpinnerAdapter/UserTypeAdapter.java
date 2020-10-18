package com.test.sample.hirecooks.Adapter.SpinnerAdapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.test.sample.hirecooks.R;

import java.util.List;

public class UserTypeAdapter extends BaseAdapter {
    private Context context;
    private List<String> mList;

    public UserTypeAdapter(Context context, List<String> mList) {
        this.context = context;
        this.mList = mList;
    }


    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public Object getItem(int pos) {
        return mList.get(pos);
    }

    @Override
    public long getItemId(int pos) {
        return pos;
    }

    @Override
    public View getView(int pos, View view, ViewGroup viewGroup) {
        if(view == null) {
            LayoutInflater mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = mInflater.inflate( R.layout.row,viewGroup,false);
        }
        TextView userType = view.findViewById(R.id.textView);
        userType.setText(mList.get(pos));
        return view;
    }
}
