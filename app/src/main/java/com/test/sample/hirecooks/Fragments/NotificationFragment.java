package com.test.sample.hirecooks.Fragments;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.test.sample.hirecooks.Activity.Home.MainActivity;
import com.test.sample.hirecooks.R;
import com.test.sample.hirecooks.Utils.NetworkUtil;

public class NotificationFragment extends Fragment {
    private MainActivity mainActivity;
    private LinearLayout notification_layout, no_internet_connection_layout;

    public NotificationFragment() {
        // Required empty public constructor
    }

    public static NotificationFragment newInstance() {
        Bundle args = new Bundle();
        NotificationFragment fragment = new NotificationFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_notification, container, false);
        initViews( view );
        if(NetworkUtil.checkInternetConnection(mainActivity)) {
            notification_layout.setVisibility( View.VISIBLE );
            no_internet_connection_layout.setVisibility( View.GONE );
        }
        else {
            notification_layout.setVisibility( View.GONE );
            no_internet_connection_layout.setVisibility( View.VISIBLE );
        }
        return view;
    }

    private void initViews(View view) {
        notification_layout = view.findViewById( R.id.notification_layout );
        no_internet_connection_layout = view.findViewById( R.id.no_internet_connection_layout );
    }


    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mainActivity = (MainActivity)context;
    }

    @Override
    public void onResume() {
        super.onResume();
    }
}