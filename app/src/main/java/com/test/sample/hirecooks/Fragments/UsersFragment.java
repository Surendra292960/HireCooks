package com.test.sample.hirecooks.Fragments;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.test.sample.hirecooks.Adapter.Users.UsersAdapter;
import com.test.sample.hirecooks.R;
import com.test.sample.hirecooks.customview.OnStartDragListener;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

public class UsersFragment extends Fragment implements OnStartDragListener {

    private static FrameLayout noItemDefault;
    private static RecyclerView recyclerView;
    private static UsersAdapter adapter;
    private ItemTouchHelper mItemTouchHelper;
    public UsersFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.frag_product_category, container, false);

        view.findViewById(R.id.slide_down).setVisibility(View.VISIBLE);
       /* view.findViewById(R.id.slide_down).setOnTouchListener(
                new OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        Utils.switchFragmentWithAnimation(R.id.frag_container,
                                new HomeFragment(), ((CarLocationActivity) (getContext())),
                                Utils.HOME_FRAGMENT, Utils.AnimationType.SLIDE_DOWN);
                        return false;
                    }
                });
*/
        // Fill Recycler View
        noItemDefault = (FrameLayout) view.findViewById(R.id.default_nodata);
        recyclerView = (RecyclerView) view.findViewById(R.id.users_list_recycler_view);
        //getUsers();

      /*  view.findViewById(R.id.start_shopping).setOnClickListener(
                new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        Utils.switchContent(R.id.frag_container,
                                Utils.HOME_FRAGMENT,
                                ((CarLocationActivity) (getContext())),
                                Utils.AnimationType.SLIDE_UP);
                    }
                });*/

        // Handle Back press
        view.setFocusableInTouchMode(true);
        view.requestFocus();
        view.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
        /*        if (event.getAction() == KeyEvent.ACTION_UP
                        && keyCode == KeyEvent.KEYCODE_BACK) {

                    Utils.switchContent(R.id.frag_container,
                            Utils.HOME_FRAGMENT,
                            ((CarLocationActivity) (getContext())),
                            Utils.AnimationType.SLIDE_UP);

                }*/
                return true;
            }
        });

        return view;
    }

   /* private void getUsers() {
      UserApi service = ApiClient.getClient().create(UserApi.class);
        Call<Users> call = service.getUsers();
        call.enqueue(new Callback<Users>() {
            @Override
            public void onResponse(Call<Users> call, Response<Users> response) {
                int statusCode = response.code();
                if(statusCode==200) {
                   *//* adapter = new UsersAdapter(response.body().getUsers(), getActivity());
                    recyclerView.setAdapter(adapter);*//*
                }
            }
            @Override
            public void onFailure(Call<Users> call, Throwable t) {

            }
        });
    }*/

    public void onStartDrag(RecyclerView.ViewHolder viewHolder) {
        mItemTouchHelper.startDrag(viewHolder);
    }
}