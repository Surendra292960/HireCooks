package com.test.sample.hirecooks.Adapter.Users;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityOptions;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.RecyclerView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.google.android.material.textfield.TextInputEditText;
import com.kinda.alert.KAlertDialog;
import com.squareup.picasso.Picasso;
import com.test.sample.hirecooks.Activity.Users.UserDetailsActivity;
import com.test.sample.hirecooks.ApiServiceCall.ApiClient;
import com.test.sample.hirecooks.Models.UsersResponse.UserResponse;
import com.test.sample.hirecooks.Models.users.MessageResponse;
import com.test.sample.hirecooks.R;
import com.test.sample.hirecooks.Tasks.Activity.TaskActivity;
import com.test.sample.hirecooks.Utils.APIUrl;
import com.test.sample.hirecooks.Utils.EncryptPassword;
import com.test.sample.hirecooks.Utils.OnButtonClickListener;
import com.test.sample.hirecooks.Utils.SharedPrefManager;
import com.test.sample.hirecooks.WebApis.UserApi;

import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UsersAdapter extends RecyclerView.Adapter<UsersAdapter.ViewHolder> {
    private static final String TAG = "";
    private List<UserResponse> users;
    private Context mCtx;
    private OnButtonClickListener listener;

    public UsersAdapter( Context mCtx,List<UserResponse> users,OnButtonClickListener  listener) {
        this.users = users;
        this.mCtx = mCtx;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_users, parent, false);
        return new ViewHolder(v);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        final UserResponse user = users.get(position);
        if (!TextUtils.isEmpty(user.getImage())) {
            Picasso.with(mCtx).load(APIUrl.PROFILE_URL+user.getImage()).placeholder(R.drawable.no_image).into(holder.textViewImage);
        }

        holder.textViewId.setText("ID:  "+user.getId());
        holder.textViewName.setText("Name:  "+user.getName());
        holder.textViewEmail.setText("Email:  "+user.getEmail());
        holder.textViewPassword.setText("Password:  "+user.getPassword());
        holder.textViewPhone.setText("Phone:  "+user.getPhone());
        holder.textViewGender.setText("Gender:  "+user.getGender());
        holder.textViewUserType.setText("Type:  "+user.getUserType());
        holder.textViewSignUpDate.setText("SignUpDateTime:  "+user.getSignupDate());

        holder.user_details_layout.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("NewApi")
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mCtx, UserDetailsActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("User", user);
                intent.putExtras(bundle);
                mCtx.startActivity(intent, ActivityOptions.makeSceneTransitionAnimation((Activity) mCtx).toBundle());

            }
        });

        /*        holder.textViewImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               *//* Intent intent = new Intent(mCtx, UserDetailsActivity.class);
                ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation((Activity) mCtx, holder.textViewImage, ViewCompat.getTransitionName(holder.textViewImage));
                Bundle bundle = new Bundle();
                bundle.putSerializable("User", user);
                intent.putExtras(bundle);
                mCtx.startActivity(intent, options.toBundle());*//*
                Intent intent = new Intent(mCtx, UserDetailsActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("User", user);
                intent.putExtras(bundle);
                ((Activity) mCtx).overridePendingTransition(R.anim.abc_slide_in_bottom,R.anim.abc_slide_out_bottom);
                mCtx.startActivity(intent);

            }
        });*/

        holder.layout_message.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                LayoutInflater li = LayoutInflater.from(mCtx);
                @SuppressLint("InflateParams") final View promptsView = li.inflate(R.layout.dialog_send_message, null);
                final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(mCtx);
                alertDialogBuilder.setView(promptsView);
                final AlertDialog dialog = alertDialogBuilder.show();

                final TextInputEditText editTextTitle = promptsView.findViewById(R.id.editTextTitle);
                final TextInputEditText editTextMessage = promptsView.findViewById(R.id.editTextMessage);
                final AppCompatButton cancelBtn = promptsView.findViewById(R.id.cancelBtn);
                final AppCompatButton sendBtn = promptsView.findViewById(R.id.sendBtn);

                sendBtn.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        dialog.dismiss();
                        // sendBtn has been clicked
                        String title = Objects.requireNonNull(editTextTitle.getText()).toString().trim();
                        String message = Objects.requireNonNull(editTextMessage.getText()).toString().trim();
                        //sending the message
                        sendMessage(user.getId(), title, message);
                    }
                });

                cancelBtn.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        dialog.dismiss();
                        String normalTextEnc = user.getPassword();
                        String normalTextDec;
                        String seedValue = user.getPassword();
                        try {

                            normalTextDec = EncryptPassword.decrypt(seedValue, normalTextEnc.trim());
                         System.out.println("Suree: "+normalTextDec.trim());

                        } catch (Exception e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                            System.out.println("Suree: "+e.toString());
                        }
                    }
                });

                alertDialogBuilder.setView(promptsView);
                dialog.show();
            }
        });

        holder.layout_task.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //alertDialog();
                Intent intent = new Intent(mCtx, TaskActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("User", user);
                intent.putExtras(bundle);
                mCtx.startActivity(intent);
            }
        });
    }

    private void alertDialog() {
        KAlertDialog pDialog = new KAlertDialog(mCtx, KAlertDialog.SUCCESS_TYPE);
        pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        pDialog.setTitleText("Loading");
        pDialog.setCancelable(false);
        pDialog.show();
        new KAlertDialog(mCtx, KAlertDialog.WARNING_TYPE)
                .setTitleText("Are you sure?")
                .setContentText("Won't be able to recover this file!")
                .setConfirmText("Yes,delete it!")
                .setConfirmClickListener(new KAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(KAlertDialog sDialog) {
                        sDialog
                                .setTitleText("Deleted!")
                                .setContentText("Your imaginary file has been deleted!")
                                .setConfirmText("OK")
                                .setConfirmClickListener(null)
                                .changeAlertType(KAlertDialog.SUCCESS_TYPE);
                    }
                })
                .show();
    }

    private void sendMessage(int id, final String title, final String message) {
        final ProgressDialog progressDialog = new ProgressDialog(mCtx);
        progressDialog.setMessage("Sending Message...");
        progressDialog.show();
        UserApi service = ApiClient.getClient().create(UserApi.class);
        Call<MessageResponse> call = service.sendMessage(SharedPrefManager.getInstance(mCtx).getUser().getId(), id, title, message);
        call.enqueue(new Callback<MessageResponse>() {
            @Override
            public void onResponse(@NonNull Call<MessageResponse> call, @NonNull Response<MessageResponse> response) {
                if(response.body()!=null){
                    progressDialog.dismiss();
                    Toast.makeText(mCtx, response.body().getMessage(), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<MessageResponse> call,  @NonNull Throwable t) {
                progressDialog.dismiss();
                Toast.makeText(mCtx, t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return users == null ? 0 : users.size();
    }

     class ViewHolder extends RecyclerView.ViewHolder {

        private TextView textViewName,textViewEmail,textViewPassword,textViewGender,textViewUserType,textViewSignUpDate,textViewPhone,textViewId;
        private LinearLayout layout_message,layout_task, user_details_layout;
        private SimpleDraweeView textViewImage;
         ViewHolder(View itemView) {
            super(itemView);

             textViewImage = itemView.findViewById(R.id.image_cartlist);
             textViewName = itemView.findViewById(R.id.textViewName);
             textViewId = itemView.findViewById(R.id.textViewId);
             textViewEmail = itemView.findViewById(R.id.textViewEmail);
             textViewPassword = itemView.findViewById(R.id.textViewPassword);
             textViewGender = itemView.findViewById(R.id.textViewGender);
             textViewUserType = itemView.findViewById(R.id.textViewUserType);
             textViewSignUpDate = itemView.findViewById(R.id.textViewSignUpDate);
             textViewPhone = itemView.findViewById(R.id.textViewPhone);
             layout_message = itemView.findViewById(R.id.layout_message);
             layout_task = itemView.findViewById(R.id.layout_task);
             user_details_layout = itemView.findViewById(R.id.user_details_layout);
        }
    }
}