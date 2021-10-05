package com.test.sample.hirecooks.Activity.ManageAccount.ManageProducts;

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
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.bumptech.glide.Glide;
import com.test.sample.hirecooks.ApiServiceCall.ApiClient;
import com.test.sample.hirecooks.Models.Category.Category;
import com.test.sample.hirecooks.Models.Category.Example;
import com.test.sample.hirecooks.Models.Users.User;
import com.test.sample.hirecooks.R;
import com.test.sample.hirecooks.Utils.SharedPrefManager;
import com.test.sample.hirecooks.WebApis.ProductApi;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;
import java.util.UUID;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class StartEditCategory extends AppCompatActivity {
    private TextInputEditText editTextCategoryName,editTextCategoryId,editTextCategoryImageUrl;
    private TextView Submit;
    private ProductApi mService;
    private String categoryName;
    private Button btnChoose,btnUpload;
    private ImageView editTextUploadCategoryImage;
    private Uri filePath;
    private final int PICK_IMAGE_REQUEST = 71;
    private String sUrl;
    private User user;
    //Firebase
    private FirebaseStorage storage;
    private StorageReference storageReference;
    private Category mCategory;
    private int subcategoryId;
    private ArrayList<Category> categories;
    private Category category;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView( R.layout.activity_start_edit_category);
        Objects.requireNonNull(getSupportActionBar()).setHomeButtonEnabled(true);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        Bundle bundle = getIntent().getExtras();
        if(bundle!=null){
            categoryName= bundle.getString("CategoryName");
            mCategory = (Category) bundle.getSerializable("Category");
            subcategoryId = bundle.getInt( "CreateCategory" );
        }
        if(mCategory!=null){
            getSupportActionBar().setTitle("Update "+mCategory.getName());
        }else{
            getSupportActionBar().setTitle("Create Video");
        }
        initViews();
    }

    private void initViews() {
        user = SharedPrefManager.getInstance(StartEditCategory.this).getUser();
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        editTextCategoryName = findViewById(R.id.editTextCategoryName);
        editTextCategoryImageUrl = findViewById(R.id.editTextCategoryImage);
        editTextCategoryId = findViewById(R.id.editTextCategoryId);
        btnChoose = findViewById(R.id.btnChoose);
        btnUpload = findViewById(R.id.btnUpload);
        editTextUploadCategoryImage = findViewById(R.id.editTextUploadCategoryImage);
        Submit = findViewById(R.id.submit);

        if(mCategory!=null){
            editTextCategoryName.setText( mCategory.getName() );
            editTextCategoryImageUrl.setText( mCategory.getLink() );
            Glide.with(this).load( mCategory.getLink() ).into( editTextUploadCategoryImage );
        }

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
                            Toast.makeText(StartEditCategory.this, "Uploaded successfully", Toast.LENGTH_SHORT).show();
                            final Uri downloadUrl = uri;
                            sUrl = downloadUrl.toString();
                            Glide.with(StartEditCategory.this).load( sUrl ).into( editTextUploadCategoryImage );
                            editTextCategoryImageUrl.setText(sUrl);
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progress.dismiss();
                            Toast.makeText(StartEditCategory.this, "Failed "+e.getMessage(), Toast.LENGTH_SHORT).show();
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.getData() != null) {
            filePath = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                editTextUploadCategoryImage.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void validation() {
        //first getting the values
        final String categoryname = editTextCategoryName.getText().toString().trim();
        final String imageUrl = editTextCategoryImageUrl.getText().toString().trim();

        if (TextUtils.isEmpty(imageUrl)) {
            editTextCategoryImageUrl.setError("Please enter imageUrl");
            editTextCategoryImageUrl.requestFocus();
            return;
        }
        if (TextUtils.isEmpty(categoryname)) {
            editTextCategoryName.setError("Please enter CategoryName");
            editTextCategoryName.requestFocus();
            return;
        }

        if(mCategory!=null){
            category = new Category();
            category.setCategoryid( mCategory.getCategoryid());
            category.setName( categoryname );
            category.setLink( imageUrl );
            categories = new ArrayList<>(  );
            categories.add( category );
            updatCategory(mCategory.getId(),categories);
        }else if(subcategoryId!=0&&mCategory==null){
            category = new Category();
            category.setCategoryid(subcategoryId);
            category.setName( categoryname );
            category.setLink( imageUrl );
            categories = new ArrayList<>(  );
            categories.add( category );
            addCategory(categories);
        }
    }

    private void updatCategory(int id, ArrayList<Category> category) {
        mService = ApiClient.getClient().create( ProductApi.class);
        Call<ArrayList<Example>> call = mService.updateCategory(id, category);
        call.enqueue(new Callback<ArrayList<Example>>() {
            @Override
            public void onResponse(Call<ArrayList<Example>> call, Response<ArrayList<Example>> response) {
                int statusCode = response.code();
                if(statusCode==200){
                    for(Example example:response.body()){
                        if(example.getError()==false) {
                            Toast.makeText( StartEditCategory.this, example.getMessage(), Toast.LENGTH_SHORT ).show();
                            StartEditCategory.this.finish();
                        }else{
                            Toast.makeText( StartEditCategory.this, example.getMessage(), Toast.LENGTH_SHORT ).show();
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<ArrayList<Example>> call, Throwable t) {
                Toast.makeText(getApplicationContext(),t.getMessage(),Toast.LENGTH_LONG).show();
            }
        });
    }

    private void addCategory( ArrayList<Category> category) {
        mService = ApiClient.getClient().create( ProductApi.class);
        Call<ArrayList<Example>> call = mService.addCategory(category);
        call.enqueue(new Callback<ArrayList<Example>>() {
            @Override
            public void onResponse(Call<ArrayList<Example>> call, Response<ArrayList<Example>> response) {
                int statusCode = response.code();
                if(statusCode==200){
                    for(Example example:response.body()){
                        if(example.getError()==false) {
                            Toast.makeText( StartEditCategory.this, example.getMessage(), Toast.LENGTH_SHORT ).show();
                            StartEditCategory.this.finish();
                        }else{
                            Toast.makeText( StartEditCategory.this, example.getMessage(), Toast.LENGTH_SHORT ).show();
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<ArrayList<Example>> call, Throwable t) {
                Toast.makeText(getApplicationContext(),t.getMessage(),Toast.LENGTH_LONG).show();
            }
        });
    }

/*    private void createBanner(int bannerId, String bannerName, String imageUrl) {
        BannerApi mService = ApiClient.getClient().create(BannerApi.class);
        Call<Banners> call = mService.createBanner(bannerName,bannerId,imageUrl,user.getFirmId());
        call.enqueue(new Callback<Banners>() {
            @SuppressLint("ShowToast")
            @Override
            public void onResponse(Call<Banners> call, Response<Banners> response) {
                int statusCode = response.code();
                if(statusCode==200) {
                    finish();
                    Toast.makeText(CreateCategoryActivity.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(CreateCategoryActivity.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
            @SuppressLint("ShowToast")
            @Override
            public void onFailure(Call<Banners> call, Throwable t) {
                Toast.makeText(getApplicationContext(),"Failed: "+t.getMessage(),Toast.LENGTH_LONG);
            }
        });
    }

    private void updateBanner(String bannerName, String imageUrl, Integer id) {
        BannerApi mService = ApiClient.getClient().create(BannerApi.class);
        Call<Banners> call = mService.updateBanner(bannerName,imageUrl,id);
        call.enqueue(new Callback<Banners>() {
            @SuppressLint("ShowToast")
            @Override
            public void onResponse(Call<Banners> call, Response<Banners> response) {
                int statusCode = response.code();
                if(statusCode==200) {
                    finish();
                    Toast.makeText(CreateCategoryActivity.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(CreateCategoryActivity.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
            @SuppressLint("ShowToast")
            @Override
            public void onFailure(Call<Banners> call, Throwable t) {
                Toast.makeText(getApplicationContext(),"Suree: "+t.getMessage(),Toast.LENGTH_LONG);
            }
        });*/

    @Override
    public void onBackPressed() {
        this.finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            this.finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
