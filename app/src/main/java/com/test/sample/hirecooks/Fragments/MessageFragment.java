package com.test.sample.hirecooks.Fragments;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.test.sample.hirecooks.Activity.Home.MainActivity;
import com.test.sample.hirecooks.R;
import com.test.sample.hirecooks.Utils.NetworkUtil;

public class MessageFragment extends Fragment {
    private LinearLayout chat_layout, no_internet_connection_layout;
    private MainActivity mainActivity;

    public static MessageFragment newInstance() {
        Bundle args = new Bundle();
        MessageFragment fragment = new MessageFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_messages, container, false);
        initViews( view );
        if(NetworkUtil.checkInternetConnection(mainActivity)) {
            chat_layout.setVisibility( View.VISIBLE );
            no_internet_connection_layout.setVisibility( View.GONE );
        }
        else {
            chat_layout.setVisibility( View.GONE );
            no_internet_connection_layout.setVisibility( View.VISIBLE );
        }
        return view;
    }


    private void initViews(View view) {
        chat_layout = view.findViewById( R.id.chat_layout );
        no_internet_connection_layout = view.findViewById( R.id.no_internet_connection_layout );
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mainActivity = (MainActivity)context;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }
}