package com.test.sample.hirecooks.Activity.Users.FirmUsers;
import android.content.Context;
import android.view.View;
import android.widget.EditText;
import static com.test.sample.hirecooks.Utils.AppUtil.isEmpty;
import static com.test.sample.hirecooks.Utils.AppUtil.showToastMsg;

public class SignUpValidation {

    public static boolean isUserDataValidated(Context context,
    EditText editTextName, EditText editTextEmail, EditText editTextPassword,
    EditText editTextConPassword, EditText editTextPhone, EditText editTextUserBike,
    String userType,EditText editTextFirmId) {

        if (isEmpty(editTextName)) {
            editTextName.setError("Please enter name");
            editTextName.requestFocus();
            return false;
        } else if (isEmpty(editTextEmail)) {
            editTextEmail.setError("Please enter email");
            editTextEmail.requestFocus();
            return false;
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(editTextEmail.getText().toString()).matches()) {
            editTextEmail.setError("Enter a valid email");
            editTextEmail.requestFocus();
            return false;
        } else if (isEmpty(editTextPassword)) {
            editTextPassword.setError("Please enter password");
            editTextPassword.requestFocus();
            return false;
        } else if (isEmpty(editTextConPassword)) {
            editTextConPassword.setError("Please enter confirm password");
            editTextConPassword.requestFocus();
            return false;
        } else if (!userType.isEmpty() && userType.equalsIgnoreCase("Select")) {
            showToastMsg(context, "Please Select User Type");
            return false;
        } else if (userType.equalsIgnoreCase("Rider")) {
            editTextUserBike.setVisibility(View.VISIBLE);
            if (isEmpty(editTextUserBike)) {
                editTextUserBike.setError("Please enter bike number");
                editTextUserBike.requestFocus();
                return false;
            }
        } else {
            editTextUserBike.setText("Not_Available");
            return false;
        }
        if (editTextPassword.equals(editTextConPassword)) {
            return false;
        } else if(!editTextPassword.equals(editTextConPassword)) {
            editTextConPassword.setError("Password are not matching");
            editTextConPassword.requestFocus();
            return false;
        } else if (isEmpty(editTextPhone)) {
            editTextPhone.setError("Please enter phone");
            editTextPhone.requestFocus();
            return false;
        }else if (isEmpty(editTextFirmId)) {
            editTextFirmId.setError("Please enter firmId");
            editTextFirmId.requestFocus();
            return false;
        }
        return true;
    }
}
