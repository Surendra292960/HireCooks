package com.test.sample.hirecooks.ViewModel;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.test.sample.hirecooks.Activity.Users.FirmUsers.FirmUserReportActivity;
import com.test.sample.hirecooks.Activity.Users.FirmUsers.FirmUserTracking;
import com.test.sample.hirecooks.Activity.Users.FirmUsers.UpdateFirmUser;
import com.test.sample.hirecooks.Models.Users.User;
import com.test.sample.hirecooks.R;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatTextView;

public class AlertDialog extends AppCompatActivity {
    private Context mCtx;
    private ViewModel viewModel;

    public AlertDialog( Context mCtx){
        this.mCtx = mCtx;
        //viewModel = new ViewModelProvider((ViewModelStoreOwner) mCtx).get(ViewModel.class);
    }

    public void userAlert(Context mCtx, User firm_user, String userAction, String btn1, String btn2) {
        final android.app.AlertDialog.Builder dialogBuilder = new android.app.AlertDialog.Builder( mCtx);
        LayoutInflater inflater = (LayoutInflater) mCtx.getSystemService( Context.LAYOUT_INFLATER_SERVICE );
        View view = inflater.inflate(R.layout.edit_subcategory_alert,null);
        TextView text = view.findViewById(R.id.text);
        AppCompatTextView deletBtn = view.findViewById(R.id.no_btn);
        AppCompatTextView editBtn = view.findViewById(R.id.edit_btn);
        deletBtn.setText( btn1 );
        editBtn.setText( btn2 );
        text.setText( userAction );
        dialogBuilder.setView(view);
        final android.app.AlertDialog dialog = dialogBuilder.create();
        dialog.show();
        deletBtn.setOnClickListener( v -> {
            try {
                dialog.dismiss();
                //deleteProfile(firm_user.getId());
                dialogAction(btn1,firm_user);
            }
            catch (Exception ex) {
                ex.printStackTrace();
            }
        } );
        editBtn.setOnClickListener( v -> {
            try {
                dialog.dismiss();
                dialogAction(btn2,firm_user);
            }
            catch (Exception ex) {
                ex.printStackTrace();
            }
        } );
    }

    @SuppressLint("NewApi")
    private void dialogAction(String btn2, User firm_user) {
        if(btn2.equalsIgnoreCase("Trace")){
            Intent intent = new Intent( mCtx,FirmUserTracking.class );
            Bundle bundle = new Bundle(  );
            bundle.putSerializable( "FirmUser",firm_user );
            intent.putExtras( bundle );
            mCtx.startActivity( intent);
        }else if(btn2.equalsIgnoreCase("Report")){
            Bundle bundle = new Bundle();
            Intent intent = new Intent(mCtx, FirmUserReportActivity.class);
            bundle.putSerializable( "FirmUser",firm_user );
            intent.putExtras(bundle);
            mCtx.startActivity(intent);
        }else if(btn2.equalsIgnoreCase("Update")){
            Bundle bundle = new Bundle();
            Intent intent = new Intent(mCtx, UpdateFirmUser.class);
            bundle.putSerializable("User",firm_user);
            intent.putExtras(bundle);
            mCtx.startActivity(intent);
        }else if(btn2.equalsIgnoreCase("Delete")){
            viewModel.deleteUser(firm_user.getId()).observe(this, userResponses -> userResponses.forEach(userResponse -> {
                if (!userResponse.getError()) {
                    Toast.makeText(mCtx, userResponse.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }));
        }
    }
}
