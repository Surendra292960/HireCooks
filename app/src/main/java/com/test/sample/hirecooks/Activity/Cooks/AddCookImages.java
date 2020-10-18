package com.test.sample.hirecooks.Activity.Cooks;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.MenuItem;
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
import com.test.sample.hirecooks.Models.UsersResponse.UserResponse;
import com.test.sample.hirecooks.Models.cooks.Request.CooksImagesResult;
import com.test.sample.hirecooks.R;
import com.test.sample.hirecooks.Utils.BaseActivity;
import com.test.sample.hirecooks.Utils.Common;
import com.test.sample.hirecooks.Utils.ProgressBarUtil;
import com.test.sample.hirecooks.WebApis.CookImages;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class AddCookImages extends BaseActivity {
    private UserResponse users;
    private ProgressBarUtil progressBarUtil;
    private CookImages mService = Common.getCookImagesAPI();
    private final int PICK_IMAGE_REQUEST = 71;
    private String sUrl;
    private Uri filePath;
    private FirebaseStorage storage;
    private StorageReference storageReference;
    private Button btnChoose,btnUpload;
    private ImageView editTextUploadImage1,editTextUploadImage2,editTextUploadImage3,editTextUploadImage4;
    private TextInputEditText editTextImagetUrl1,editTextImagetUrl2,editTextImagetUrl3,editTextImagetUrl4,editTextCaption;
    private TextView submit;
    private Uri ImageUri;
    private List<Uri> ImageList = new ArrayList<>();
    private List<Uri> newImageList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_add_cook_images );
        Objects.requireNonNull(getSupportActionBar()).setHomeButtonEnabled(true);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        Objects.requireNonNull(getSupportActionBar()).setTitle("Add Cooks Images");

        iniViews();

        Bundle bundle = getIntent().getExtras();
        if(bundle!=null){
            users = (UserResponse)bundle.getSerializable("Cooks");
            if(users!=null){
              //  ApiServiceCall(users);
            }
        }
    }

    private void iniViews() {
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        progressBarUtil = new ProgressBarUtil( this );
        btnChoose = findViewById( R.id.btnChoose );
        btnUpload = findViewById( R.id.btnUpload );
        editTextImagetUrl1 = findViewById( R.id.editTextImagetUrl1 );
        editTextImagetUrl2 = findViewById( R.id.editTextImagetUrl2 );
        editTextImagetUrl3 = findViewById( R.id.editTextImagetUrl3 );
        editTextImagetUrl4 = findViewById( R.id.editTextImagetUrl4 );
        editTextCaption = findViewById( R.id.editTextCaption );
        editTextUploadImage1 = findViewById( R.id.editTextUploadImage1 );
        editTextUploadImage2 = findViewById( R.id.editTextUploadImage2 );
        editTextUploadImage3 = findViewById( R.id.editTextUploadImage3 );
        editTextUploadImage4 = findViewById( R.id.editTextUploadImage4 );
        submit = findViewById( R.id.submit );

        btnChoose.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseImage();
            }
        } );

        btnUpload.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadImage();
            }
        } );

        submit.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validate();
            }
        } );
    }

    private void validate() {
        //first getting the values
        final String caption = editTextCaption.getText().toString().trim();
        final String imageUrl_1 = editTextImagetUrl1.getText().toString().trim();
        final String imageUrl_2 = editTextImagetUrl2.getText().toString().trim();
        final String imageUrl_3 = editTextImagetUrl3.getText().toString().trim();
        final String imageUrl_4 = editTextImagetUrl4.getText().toString().trim();

        if (TextUtils.isEmpty( imageUrl_1 )) {
            editTextImagetUrl1.setError( "Please enter imageUrl_1" );
            editTextImagetUrl1.requestFocus();
            return;
        }if (TextUtils.isEmpty( imageUrl_2 )) {
            editTextImagetUrl2.setError( "Please enter imageUrl_1" );
            editTextImagetUrl2.requestFocus();
            return;
        }if (TextUtils.isEmpty( imageUrl_3 )) {
            editTextImagetUrl3.setError( "Please enter imageUrl_3" );
            editTextImagetUrl3.requestFocus();
            return;
        }if (TextUtils.isEmpty( imageUrl_4 )) {
            editTextImagetUrl4.setError( "Please enter imageUrl_4" );
            editTextImagetUrl4.requestFocus();
            return;
        }

        if(users!=null){
            for(Uri url:newImageList){
                 ApiServiceCall(users, url);
            }

        }else{
            ShowToast( "User Not Found" );
        }
    }

    private void chooseImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent,"Select Picture"), PICK_IMAGE_REQUEST);
    }

    private void uploadImage() {
        if(ImageList != null&&ImageList.size()!=0){
            final ProgressDialog progress = new ProgressDialog(this);
            progress.setTitle("Uploading....");
            progress.show();

            Uri[] uri=new Uri[ImageList.size()];
            for (int i =0 ; i < ImageList.size(); i++) {
                uri[i] = Uri.parse(ImageList.get(i).toString());
                StorageReference ref= storageReference.child("images/"+ UUID.randomUUID().toString());
                ref.putFile(uri[i]).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                progress.dismiss();
                                newImageList.add(uri);
                                if(newImageList.size()>=4){
                                    setImages(newImageList);
                                }
                                Toast.makeText(AddCookImages.this, "Uploaded successfully", Toast.LENGTH_SHORT).show();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                progress.dismiss();
                                Toast.makeText(AddCookImages.this, "Failed "+e.getMessage(), Toast.LENGTH_SHORT).show();
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
    }

    private void setImages(List<Uri> newImageList) {
        if(newImageList!=null&&newImageList.size()!=0){
            for(int i=0; i<newImageList.size(); i++){
                if(i==0){
                    sUrl = newImageList.get(i).toString();
                    editTextImagetUrl1.setVisibility( View.VISIBLE );
                    Picasso.with( AddCookImages.this ).load(sUrl).into(editTextUploadImage1);
                    editTextImagetUrl1.setText(sUrl);
                }else if(i==1){
                    editTextImagetUrl2.setVisibility( View.VISIBLE );
                    sUrl = newImageList.get(i).toString();
                    Picasso.with( AddCookImages.this ).load(sUrl).into(editTextUploadImage2);
                    editTextImagetUrl2.setText(sUrl);
                }else if(i==2){
                    editTextImagetUrl3.setVisibility( View.VISIBLE );
                    sUrl = newImageList.get(i).toString();
                    Picasso.with( AddCookImages.this ).load(sUrl).into(editTextUploadImage3);
                    editTextImagetUrl3.setText(sUrl);
                }else if(i==3){
                    editTextImagetUrl4.setVisibility( View.VISIBLE );
                    sUrl = newImageList.get(i).toString();
                    Picasso.with( AddCookImages.this ).load(sUrl).into(editTextUploadImage4);
                    editTextImagetUrl4.setText(sUrl);
                }
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST&&resultCode == Activity.RESULT_OK) {
            if (data.getClipData() != null) {
                int countClipData = data.getClipData().getItemCount();
                int currentImageSlect = 0;
                ImageList = new ArrayList<>();
                while (currentImageSlect < countClipData) {
                    ImageUri = data.getClipData().getItemAt(currentImageSlect).getUri();
                    ImageList.add(ImageUri);
                    currentImageSlect = currentImageSlect + 1;
                }

                if(ImageList!=null&&ImageList.size()==4) {
                    for (int i = 0; i < ImageList.size(); i++) {
                        if (i == 0) {
                            filePath = ImageList.get( i );
                            try {
                                Bitmap bitmap = MediaStore.Images.Media.getBitmap( getContentResolver(), filePath );
                                editTextUploadImage1.setImageBitmap( bitmap );
                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                        } else if (i == 1) {
                            filePath = ImageList.get( i );
                            try {
                                Bitmap bitmap = MediaStore.Images.Media.getBitmap( getContentResolver(), filePath );
                                editTextUploadImage2.setImageBitmap( bitmap );
                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                        } else if (i == 2) {
                            filePath = ImageList.get( i );
                            try {
                                Bitmap bitmap = MediaStore.Images.Media.getBitmap( getContentResolver(), filePath );
                                editTextUploadImage3.setImageBitmap( bitmap );
                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                        } else if (i == 3) {
                            filePath = ImageList.get( i );
                            try {
                                Bitmap bitmap = MediaStore.Images.Media.getBitmap( getContentResolver(), filePath );
                                editTextUploadImage4.setImageBitmap( bitmap );
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }else{
                    ShowToast( "Please Select atleast 4 Images" );
                }
            } else {
                ShowToast( "Please Select atleast 4 Images" );
            }
        }
    }

    private void ApiServiceCall(UserResponse users, Uri imageUrl) {
        String url = imageUrl.toString();
        progressBarUtil.showProgress();
        mService.addCookImages(users.getId(),url)
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
                            AddCookImages.this.finish();
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
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id==android.R.id.home){
            this.finish();
        }
        return super.onOptionsItemSelected( item );
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.finish();
    }
}
