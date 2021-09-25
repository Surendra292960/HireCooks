package com.test.sample.hirecooks.Activity.ManageAccount.ManageProducts;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import com.squareup.picasso.Picasso;
import com.test.sample.hirecooks.ApiServiceCall.ApiClient;
import com.test.sample.hirecooks.Models.SubCategory.Color;
import com.test.sample.hirecooks.Models.SubCategory.ColorExample;
import com.test.sample.hirecooks.Models.SubCategory.Image;
import com.test.sample.hirecooks.Models.SubCategory.ImageExample;
import com.test.sample.hirecooks.Models.SubCategory.Size;
import com.test.sample.hirecooks.Models.SubCategory.SizeExample;
import com.test.sample.hirecooks.Models.SubCategory.Weight;
import com.test.sample.hirecooks.Models.SubCategory.WeightExample;
import com.test.sample.hirecooks.Models.Users.User;
import com.test.sample.hirecooks.R;
import com.test.sample.hirecooks.Utils.ProgressBarUtil;
import com.test.sample.hirecooks.Utils.SharedPrefManager;
import com.test.sample.hirecooks.WebApis.ProductApi;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddActivity extends AppCompatActivity {
    private TextInputEditText colorName, color,weight_kg,dozon,pond,size_number,size_text;
    private TextView submit;
    private Color mColor;
    private List<ColorExample> colorExampleList;
    private List<Color> colorList;
    private List<ImageExample> imageExampleList;
    private List<Image> imageList;
    private List<SizeExample> sizeExampleList;
    private List<Size> sizeList;
    private List<WeightExample> weightExampleList;
    private List<Weight> weightList;
    private ProgressBarUtil progressBarUtil;
    private ImageView imageView;
    private Size mSize;
    private Weight mWeight;
    private Image mImage;
    private Uri filePath;
    private final int PICK_IMAGE_REQUEST = 71;
    private String sUrl;
    private User user;
    private LinearLayout image_lay;
    //Firebase
    private FirebaseStorage storage;
    private StorageReference storageReference;
    private Button btnUpload,btnChoose;
   // private Video mCategory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_add );
        Objects.requireNonNull( getSupportActionBar() ).setHomeButtonEnabled( true );
        Objects.requireNonNull( getSupportActionBar() ).setDisplayHomeAsUpEnabled( true );
        Objects.requireNonNull(getSupportActionBar()).setTitle("Add");
        progressBarUtil = new ProgressBarUtil( this );
        user = SharedPrefManager.getInstance(AddActivity.this).getUser();
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        btnChoose = findViewById(R.id.btnChoose);
        btnUpload = findViewById(R.id.btnUpload);
        colorName = findViewById( R.id.color_name );
        color = findViewById( R.id.color );
        weight_kg = findViewById( R.id.weight_kg );
        dozon = findViewById( R.id.dozon );
        pond = findViewById( R.id.pond );
        imageView = findViewById( R.id.imageView );
        size_number = findViewById( R.id.size_number );
        size_text = findViewById( R.id.size_text );
        image_lay = findViewById( R.id.image_lay );
        submit = findViewById( R.id.submit );

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            mColor = (Color) bundle.getSerializable( "Color" );
            mSize = (Size) bundle.getSerializable( "Size" );
            mWeight = (Weight) bundle.getSerializable( "Weight" );
            mImage = (Image) bundle.getSerializable( "Image" );
           // mCategory = (Video) bundle.getSerializable( "Video" );
        }
        if (mColor != null) {
            colorName.setVisibility( View.VISIBLE );
            color.setVisibility( View.VISIBLE );
            colorName.setText( mColor.getColorName() );
            color.setText( mColor.getColor() );
        } else if (mSize != null) {
            size_number.setVisibility( View.VISIBLE );
            size_text.setVisibility( View.VISIBLE );
            size_number.setText(""+ mSize.getSizeNumber() );
            size_text.setText( mSize.getSizeText() );
        } else if (mWeight != null) {
            pond.setVisibility( View.VISIBLE );
            weight_kg.setVisibility( View.VISIBLE );
            dozon.setVisibility( View.VISIBLE );
            weight_kg.setText(""+mWeight.getKg() );
            dozon.setText(""+ mWeight.getDozan() );
            pond.setText(""+ mWeight.getPond() );
        }else if (mImage != null) {
            image_lay.setVisibility( View.VISIBLE );
            Picasso.with( this ).load( mImage.getImage() ).into( imageView );
        }

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

        submit.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mColor!=null){
                    colorValidation();
                }else if(mSize!=null){
                    sizeValidation();
                }else if(mWeight!=null){
                    weightValidation();
                }else if(mImage!=null){
                    imageValidation();
                }
            }
        } );
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
                            Toast.makeText(AddActivity.this, "Uploaded successfully", Toast.LENGTH_SHORT).show();
                            final Uri downloadUrl = uri;
                            sUrl = downloadUrl.toString();
                            Picasso.with( AddActivity.this ).load( sUrl ).into( imageView );
                           // image_Link = sUrl;
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progress.dismiss();
                            Toast.makeText(AddActivity.this, "Failed "+e.getMessage(), Toast.LENGTH_SHORT).show();
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
                imageView.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void imageValidation() {
        int subcategory_id = mImage.getSubcategoryId();
        String image_Link = null;
        if(sUrl==null){
            image_Link = mImage.getImage();
        }else{
            image_Link = sUrl;
        }
        String x_id = mImage.getxId();
        if (mImage != null) {
            Image image = new Image();
            image.setId( mImage.getId() );
            image.setSubcategoryId( subcategory_id );
            image.setImage( image_Link );
            image.setxId( x_id );
            updateImage(image);
        }
    }

    private void colorValidation() {
        final String color_name = colorName.getText().toString().trim();
        String color_code = color.getText().toString().trim();
        int subcategory_id = mColor.getSubcategoryId();
        String x_id = mColor.getxId();
        if (mColor != null) {
            Color color = new Color();
            color.setColor( color_code );
            color.setColorName( color_name );
            color.setId( mColor.getId() );
            color.setSubcategoryId( subcategory_id );
            color.setxId( x_id );
            updateColor( color );

        }
    }

    private void sizeValidation() {
        final String size_numbers = size_number.getText().toString().trim();
        String size_texts = size_text.getText().toString().trim();
        int subcategory_id = mSize.getSubcategoryId();
        String x_id = mSize.getxId();
        if (mSize != null) {
            Size size = new Size();
            size.setSizeNumber( Integer.parseInt( size_numbers ) );
            size.setSizeText( size_texts );
            size.setId( mSize.getId() );
            size.setSubcategoryId( subcategory_id );
            size.setxId( x_id );
            updateSize( size );
        }
    }

    private void weightValidation() {
        final String weight_kgs = weight_kg.getText().toString().trim();
        String dozons = dozon.getText().toString().trim();
        String ponds = pond.getText().toString().trim();
        int subcategory_id = mWeight.getSubcategoryId();
        String x_id = mWeight.getxId();
        if (mWeight != null) {
            Weight weight = new Weight();
            weight.setKg(Integer.parseInt( weight_kgs ));
            weight.setDozan( Integer.parseInt( dozons ) );
            weight.setPond( Integer.parseInt( ponds ) );
            weight.setId( mWeight.getId() );
            weight.setSubcategoryId( subcategory_id );
            weight.setxId( x_id );
            updateWeight( weight );
        }
    }

    private void updateWeight(Weight weight) {
        weightExampleList = new ArrayList<>();
        weightList = new ArrayList<>();
        weightList.add( weight );
        WeightExample weightExample = new WeightExample();
        weightExample.setSubcategoryid( weight.getSubcategoryId() );
        weightExample.setWeights( weightList );
        weightExampleList.add( weightExample );
        progressBarUtil.showProgress();
        ProductApi mService = ApiClient.getClient().create( ProductApi.class );
        Call<List<WeightExample>> call = mService.updateWeight( weight.getId(), weightExampleList );
        call.enqueue( new Callback<List<WeightExample>>() {
            @Override
            public void onResponse(Call<List<WeightExample>> call, Response<List<WeightExample>> response) {
                int statusCode = response.code();
                if (statusCode == 200) {
                    for (WeightExample weightExample1 : response.body()) {
                        if (weightExample1.getError() == false) {
                            progressBarUtil.hideProgress();
                            Toast.makeText( AddActivity.this, weightExample1.getMessage(), Toast.LENGTH_SHORT ).show();
                            finish();
                        } else {
                            Toast.makeText( AddActivity.this, weightExample1.getMessage(), Toast.LENGTH_SHORT ).show();
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<List<WeightExample>> call, Throwable t) {
                progressBarUtil.hideProgress();
                System.out.println( "Suree : " + t.getMessage() );
            }
        } );
    }

    private void updateSize(Size size) {
        sizeExampleList = new ArrayList<>(  );
        sizeList = new ArrayList<>(  );
        sizeList.add( size );
        SizeExample sizeExample = new SizeExample();
        sizeExample.setSubcategoryid(size.getSubcategoryId() );
        sizeExample.setSizes(sizeList );
        sizeExampleList.add(sizeExample );
        progressBarUtil.showProgress();
        ProductApi mService = ApiClient.getClient().create(ProductApi.class);
        Call<List<SizeExample>> call = mService.updateSize(size.getId(),sizeExampleList);
        call.enqueue(new Callback<List<SizeExample>>() {
            @Override
            public void onResponse(Call<List<SizeExample>> call, Response<List<SizeExample>> response) {
                int statusCode = response.code();
                if(statusCode==200) {
                    for(SizeExample sizeExample1:response.body()){
                        if(sizeExample1.getError()==false){
                            progressBarUtil.hideProgress();
                            Toast.makeText( AddActivity.this, sizeExample1.getMessage(), Toast.LENGTH_SHORT ).show();
                            finish();
                        }else{
                            Toast.makeText( AddActivity.this, sizeExample1.getMessage(), Toast.LENGTH_SHORT ).show();
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<List<SizeExample>> call, Throwable t) {
                progressBarUtil.hideProgress();
                System.out.println("Suree : "+t.getMessage());
            }
        });
    }

    private void updateColor(Color color) {
        colorExampleList = new ArrayList<>(  );
        colorList = new ArrayList<>(  );
        colorList.add( color );
        ColorExample colorExample = new ColorExample();
        colorExample.setSubcategoryid(color.getSubcategoryId() );
        colorExample.setColors( colorList );
        colorExampleList.add( colorExample );
        progressBarUtil.showProgress();
        ProductApi mService = ApiClient.getClient().create(ProductApi.class);
        Call<List<ColorExample>> call = mService.updateColor(color.getId(),colorExampleList);
        call.enqueue(new Callback<List<ColorExample>>() {
            @Override
            public void onResponse(Call<List<ColorExample>> call, Response<List<ColorExample>> response) {
                int statusCode = response.code();
                if(statusCode==200) {
                    for(ColorExample colorExample1:response.body()){
                        if(colorExample1.getError()==false){
                            progressBarUtil.hideProgress();
                            Toast.makeText( AddActivity.this, colorExample1.getMessage(), Toast.LENGTH_SHORT ).show();
                            finish();
                        }else{
                            Toast.makeText( AddActivity.this, colorExample1.getMessage(), Toast.LENGTH_SHORT ).show();
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<List<ColorExample>> call, Throwable t) {
                progressBarUtil.hideProgress();
                System.out.println("Suree : "+t.getMessage());
            }
        });
    }

    private void updateImage(Image image) {
        imageExampleList = new ArrayList<>(  );
        imageList = new ArrayList<>(  );
        imageList.add( image );
        ImageExample imageExample = new ImageExample();
        imageExample.setSubcategoryid(image.getSubcategoryId() );
        imageExample.setImages( imageList );
        imageExampleList.add( imageExample );
        progressBarUtil.showProgress();
        ProductApi mService = ApiClient.getClient().create(ProductApi.class);
        Call<List<ImageExample>> call = mService.updateImage(image.getId(),imageExampleList);
        call.enqueue(new Callback<List<ImageExample>>() {
            @Override
            public void onResponse(Call<List<ImageExample>> call, Response<List<ImageExample>> response) {
                int statusCode = response.code();
                if(statusCode==200) {
                    for(ImageExample imageExample1:response.body()){
                        if(imageExample1.getError()==false){
                            progressBarUtil.hideProgress();
                            Toast.makeText( AddActivity.this, imageExample1.getMessage(), Toast.LENGTH_SHORT ).show();
                            finish();
                        }else{
                            Toast.makeText( AddActivity.this, imageExample1.getMessage(), Toast.LENGTH_SHORT ).show();
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<List<ImageExample>> call, Throwable t) {
                progressBarUtil.hideProgress();
                System.out.println("Suree : "+t.getMessage());
            }
        });
    }

}
