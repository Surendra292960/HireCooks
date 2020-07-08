package com.test.sample.hirecooks.Utils;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;

import com.test.sample.hirecooks.R;

public class ProgressBarUtil {
    private ProgressDialog progressBar;
    private Context context;
    public ProgressBarUtil(Context context){
        this.context = context;

        if(progressBar==null) {
            progressBar = new ProgressDialog(context);
        }
    }
    public void showProgress(){
        if(progressBar!=null) {
            progressBar.setCancelable(true);
            progressBar.setProgressStyle(R.style.AccountKit_ActionBar);
            progressBar.setMessage("Please wait . . .");
            if(!((Activity) context).isFinishing()) {
                progressBar.show();
            }
        }
    }
    public void hideProgress(){
        if(progressBar!=null){
            progressBar.dismiss();
        }
    }
}
