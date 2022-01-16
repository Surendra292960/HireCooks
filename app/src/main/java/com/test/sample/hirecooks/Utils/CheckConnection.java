package com.test.sample.hirecooks.Utils;
import android.content.DialogInterface;
import android.util.Log;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import com.test.sample.hirecooks.R;

public class CheckConnection extends AppCompatActivity {

    public CheckConnection(){
    }
    public void showAlert(String message) {
        try {
            if (!this.isFinishing()) {
                new AlertDialog.Builder(this)
                        .setTitle(getResources().getString(R.string.app_name))
                        .setMessage(message)
                        .setCancelable(true)
                        .setPositiveButton(getResources().getString(R.string.ok), null)
                        .setOnDismissListener(dialog -> dialog.dismiss())
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();
            }
        } catch (Exception e) {
            printLog(CheckConnection.class,e.getMessage());
        }

    }

    public void printLog(Class<?> pClassName, String pStrMsg){
        Log.e(pClassName.getSimpleName(),pStrMsg);
    }
}
