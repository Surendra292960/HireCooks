/*
package com.test.sample.hirecooks.Activity.Users;


import android.content.Context;
import android.text.TextUtils;
import android.widget.EditText;
import android.widget.RadioButton;

import com.test.sample.hirecooks.Models.Users.Example;
import com.test.sample.hirecooks.Models.Users.User;

import java.util.ArrayList;
import java.util.List;

public class LoginValidation {
    private static List<Example> exampleList;
    private static Example  exampls;
    private static User user;
    private static List<User> userList;

    public static boolean isUserSigninValidated(Context pContext, EditText email, EditText password){
        if(TextUtils.isEmpty(email){
            email.setError( "Please Enter email" );
            password.requestFocus();
            return false;
        } if(TextUtils.isEmpty( (CharSequence) password )){
            email.setError( "Please Enter email" );
            password.requestFocus();
            return false;
        }
        else {
            return true;
        }
    }

    public static List<Example> setSigninData(EditText editTextEmail, EditText editTextPassword) {
        exampleList = new ArrayList<>();
        exampls = new Example();
        userList = new ArrayList<>();
        user = new User();
        user.setEmail( editTextEmail.getText().toString() );
        user.setPassword( editTextPassword.getText().toString() );
        userList.add( user );
        exampls.setUsers( userList );
        exampleList.add( exampls );
        return exampleList;
    }


    public static boolean isUserSignupValidated(Context pContext, EditText editTextName, EditText editTextEmail, EditText editTextPassword, EditText editTextConPassword){
        if (TextUtils.isEmpty(name)) {
            editTextName.setError("Please enter name");
            editTextName.requestFocus();
            return false;
        }if (TextUtils.isEmpty(email)) {
            editTextEmail.setError("Please enter email");
            editTextEmail.requestFocus();
            return false;
        }if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            editTextEmail.setError("Enter a valid email");
            editTextEmail.requestFocus();
            return false;
        }if (TextUtils.isEmpty(password)) {
            editTextPassword.setError("Please enter password");
            editTextPassword.requestFocus();
            return false;
        }if (TextUtils.isEmpty(cpassword)) {
            editTextConPassword.setError("Please enter confirm password");
            editTextConPassword.requestFocus();
            return false;
        }if (!user_type.isEmpty()&&user_type.equalsIgnoreCase( "Please Select" )) {
            ShowToast( "Please Select User Type" );
            return false;
        }
        if (password.equals(cpassword)) {

        }else{
            editTextConPassword.setError("Password are not matching");
            editTextConPassword.requestFocus();
            return false;
        }
        if (TextUtils.isEmpty(phone)) {
            editTextPhone.setError("Please enter phone");
            editTextPhone.requestFocus();
            return false;
        }
        else {
            return true;
        }
    }

    public static List<Example> setSignupData(EditText editTextEmail, EditText editTextPassword) {

        phone = ccp.getFullNumberWithPlus();
        phone = phone.replace(" " , "");

        List<Example> exampleList = new ArrayList<>(  );
        List<User> userList = new ArrayList<>(  );
        Example example = new Example();
        User user = new User(  );
        user.setName( name );
        user.setEmail( email );
        user.setPassword( password );
        user.setPhone( phone );
        user.setGender( gender );
        user.setUserType( user_type );
        user.setFirmId( firmId );
        user.setBikeNumber( bikeNumber );
        userList.add( user );
        example.setUsers( userList );
        exampleList.add( example );
        userSignUp(exampleList);



        exampleList = new ArrayList<>();
        exampls = new Example();
        userList = new ArrayList<>();
        user = new User();
        user.setEmail( editTextEmail.getText().toString() );
        user.setPassword( editTextPassword.getText().toString() );
        userList.add( user );
        exampls.setUsers( userList );
        exampleList.add( exampls );
        return exampleList;
    }

    private void SignUpValidation() {
        String firmId = null;
        final RadioButton radioSex = findViewById(radioGender.getCheckedRadioButtonId());
        String name = editTextName.getText().toString().trim();
        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();
        String cpassword = editTextConPassword.getText().toString().trim();
        String phone = editTextPhone.getText().toString().trim();
        firmId = "Not_Available";
        String user_type = editTextUserType.getSelectedItem().toString();
        String bikeNumber = "Not_Available";

        String gender = radioSex.getText().toString();



    }
}
*/
