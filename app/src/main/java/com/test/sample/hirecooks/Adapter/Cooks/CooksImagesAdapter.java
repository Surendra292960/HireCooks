package com.test.sample.hirecooks.Adapter.Cooks;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;
import com.test.sample.hirecooks.Activity.Cooks.UpdateCookImage;
import com.test.sample.hirecooks.Models.UsersResponse.UserResponse;
import com.test.sample.hirecooks.Models.cooks.Request.CooksImages;
import com.test.sample.hirecooks.Models.cooks.Request.CooksImagesResult;
import com.test.sample.hirecooks.R;
import com.test.sample.hirecooks.Utils.Common;
import com.test.sample.hirecooks.Utils.SharedPrefManager;
import com.test.sample.hirecooks.WebApis.CookImages;

import java.util.List;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class CooksImagesAdapter extends RecyclerView.Adapter<com.test.sample.hirecooks.Adapter.Cooks.CooksImagesAdapter.ViewHolder> {
    private UserResponse users;
    private Context mCtx;
    private List<CooksImages> cooksImages;
    AlertDialog.Builder builder;
    private CookImages mService = Common.getCookImagesAPI();
    public CooksImagesAdapter(Context mCtx, List<CooksImages> cooksImages, UserResponse users) {
        this.mCtx = mCtx;
        this.cooksImages = cooksImages;
        this.users = users;
    }

    @Override
    public com.test.sample.hirecooks.Adapter.Cooks.CooksImagesAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View itemLayoutView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.cooks_promotion_adapter, null);
        ViewHolder viewHolder = new ViewHolder(itemLayoutView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(com.test.sample.hirecooks.Adapter.Cooks.CooksImagesAdapter.ViewHolder holder, int position) {
        CooksImages cooksImage = cooksImages.get(position);
        if(cooksImage!=null){
            Picasso.with(mCtx).load(cooksImage.getLink()).into(holder.imageView);
            holder.imageView.setOnClickListener( new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(SharedPrefManager.getInstance( mCtx ).getUser().getId().equals( users.getId() )) {
                        alertDialog(cooksImage);
                    }
                }
            } );
        }
    }

    private void alertDialog(final CooksImages cooksImage){
        try {
            new AlertDialog.Builder(mCtx)
                    .setTitle("Options")
                    .setMessage("Do you want to do?")
                    .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            deleteImage(cooksImage);
                        }
                    })
                    .setNegativeButton("Update", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                          if(users!=null){
                              Bundle bundle = new Bundle(  );
                              Intent intent = new Intent( mCtx, UpdateCookImage.class );
                              bundle.putSerializable( "CookImage", cooksImage );
                              bundle.putSerializable( "User", users );
                              intent.putExtras( bundle );
                              mCtx.startActivity( intent );
                          }else{
                              Toast.makeText( mCtx, "User Not Found", Toast.LENGTH_SHORT ).show();
                          }
                        }
                    })
                    .setNeutralButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                           dialog.dismiss();
                        }
                    })
                    .show();

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void deleteImage(CooksImages cooksImages) {
        mService.deleteCookImages(cooksImages.getId())
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
                            Toast.makeText( mCtx, result.getMessage(), Toast.LENGTH_SHORT ).show();
                        }else{
                            Toast.makeText( mCtx, result.getMessage(), Toast.LENGTH_SHORT ).show();
                        }
                    }

                    @Override
                    public void onError(Throwable t) {
                        Toast.makeText( mCtx, t.getMessage(), Toast.LENGTH_SHORT ).show();
                        System.out.println("New data received: " + t.getMessage());
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    @Override
    public int getItemCount() {
        return cooksImages==null?0:cooksImages.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView imageView;

        public ViewHolder(View itemLayoutView) {
            super(itemLayoutView);
            imageView = itemLayoutView.findViewById(R.id.imageView);
        }
    }
}