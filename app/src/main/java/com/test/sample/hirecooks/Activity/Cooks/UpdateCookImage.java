package com.test.sample.hirecooks.Activity.Cooks;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.test.sample.hirecooks.Models.Users.User;
import com.test.sample.hirecooks.Models.cooks.Request.CooksImages;
import com.test.sample.hirecooks.Models.cooks.Request.CooksImagesResult;
import com.test.sample.hirecooks.R;
import com.test.sample.hirecooks.Utils.BaseActivity;
import com.test.sample.hirecooks.Utils.Common;
import com.test.sample.hirecooks.Utils.ProgressBarUtil;
import com.test.sample.hirecooks.WebApis.CookImages;

import java.io.IOException;
import java.util.UUID;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class UpdateCookImage extends BaseActivity {
    private CooksImages cookImage;
    private TextInputEditText editTextImageUrl,editTextCaption;
    private TextView Submit;
    private Button btnChoose,btnUpload;
    private ImageView editTextUploadImage;
    private Uri filePath;
    private final int PICK_IMAGE_REQUEST = 71;
    private String sUrl;
    //Firebase
    private FirebaseStorage storage;
    private StorageReference storageReference;
    private ProgressBarUtil progressBarUtil;
    private CookImages mService = Common.getCookImagesAPI();
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_update_cook_image );

        initViews();
        Bundle bundle = getIntent().getExtras();
        if(bundle!=null){
            cookImage = (CooksImages)bundle.getSerializable("CookImage");
            user = (User)bundle.getSerializable("User");
            if(cookImage!=null&&user!=null){

            }
        }
    }

    private void initViews() {
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        progressBarUtil = new ProgressBarUtil( this );
        editTextImageUrl = findViewById( R.id.editTextImageUrl );
        editTextCaption = findViewById( R.id.editTextCaption );
        Submit = findViewById( R.id.submit );
        btnUpload = findViewById( R.id.btnUpload );
        btnChoose = findViewById( R.id.btnChoose );
        editTextUploadImage = findViewById( R.id.editTextUploadImage );

        Submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validation();
            }
        });

        btnChoose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseImage();
            }
        });

        btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadImage();
            }
        });
    }

    private void validation() {
        //first getting the values
        final String caption = editTextCaption.getText().toString().trim();
        final String imageUrl = editTextImageUrl.getText().toString().trim();

        if (TextUtils.isEmpty(imageUrl)) {
            editTextImageUrl.setError("Please enter CategoryId");
            editTextImageUrl.requestFocus();
            return;
        }
        if (TextUtils.isEmpty(caption)) {
            editTextCaption.setError("Please enter Caption");
            editTextCaption.requestFocus();
            return;
        }

        if(cookImage!=null&&user!=null){
            updateCookImage(cookImage,user.getId(),imageUrl,caption);
        }
    }

    private void chooseImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent.createChooser(intent,"Select Picture"),PICK_IMAGE_REQUEST);
    }

    private void uploadImage() {
        if(filePath != null){
            final ProgressDialog progress = new ProgressDialog(this);
            progress.setTitle("Uploading....");
            progress.show();

            StorageReference ref= storageReference.child("images/"+ UUID.randomUUID().toString());
            ref.putFile(filePath).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            progress.dismiss();
                            Toast.makeText(UpdateCookImage.this, "Uploaded successfully", Toast.LENGTH_SHORT).show();
                            final Uri downloadUrl = uri;
                            sUrl = downloadUrl.toString();
                            Picasso.with(UpdateCookImage.this ).load(sUrl).into(editTextUploadImage);
                            editTextImageUrl.setText(sUrl);
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progress.dismiss();
                            Toast.makeText(UpdateCookImage.this, "Failed "+e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                    double progres_time = (100.0*taskSnapshot.getBytesTransferred()/taskSnapshot.getTotalByteCount());
                    progress.setMessage("Uploaded "+(int)progres_time+" %");
                }
            });
        }
    }


    private void updateCookImage(CooksImages cooksImages, Integer userId, String imageUrl, String caption) {
        progressBarUtil.showProgress();
        mService.updateCookImage(cooksImages.getId(),userId,imageUrl,caption)
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
                            ShowToast( result.getMessage() );
                            UpdateCookImage.this.finish();
                        }else{
                            ShowToast( result.getMessage() );
                        }
                    }

                    @Override
                    public void onError(Throwable t) {
                        progressBarUtil.hideProgress();
                        ShowToast(t.getMessage());
                        System.out.println("New data received: " + t.getMessage());
                    }

                    @Override
                    public void onComplete() {
                        progressBarUtil.hideProgress();
                    }
                });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.getData() != null) {
            filePath = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                editTextUploadImage.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
