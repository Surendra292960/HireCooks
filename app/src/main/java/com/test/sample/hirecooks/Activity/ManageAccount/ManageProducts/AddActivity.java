package com.test.sample.hirecooks.Activity.ManageAccount.ManageProducts;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
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
import com.test.sample.hirecooks.Utils.ProgressBarUtil;
import com.test.sample.hirecooks.Utils.SharedPrefManager;
import com.test.sample.hirecooks.ViewModel.ViewModel;
import com.test.sample.hirecooks.WebApis.ProductApi;
import com.test.sample.hirecooks.databinding.ActivityAddBinding;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import androidx.lifecycle.ViewModelProvider;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddActivity extends AppCompatActivity {
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
    private Size mSize;
    private Weight mWeight;
    private Image mImage;
    private Uri filePath;
    private final int PICK_IMAGE_REQUEST = 71;
    private String sUrl;
    private User user;
    //Firebase
    private FirebaseStorage storage;
    private StorageReference storageReference;
    private ActivityAddBinding binding;
    private ViewModel viewModel;
   // private Video mCategory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        binding = ActivityAddBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        viewModel = new ViewModelProvider(this).get(ViewModel.class);
        user = SharedPrefManager.getInstance( this ).getUser();
        progressBarUtil = new ProgressBarUtil( this );
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            mColor = (Color) bundle.getSerializable( "Color" );
            mSize = (Size) bundle.getSerializable( "Size" );
            mWeight = (Weight) bundle.getSerializable( "Weight" );
            mImage = (Image) bundle.getSerializable( "Image" );
           // mCategory = (Video) bundle.getSerializable( "Video" );
        }
        if (mColor != null) {
            binding.colorName.setVisibility( View.VISIBLE );
            binding.color.setVisibility( View.VISIBLE );
            binding.colorName.setText( mColor.getColorName() );
            binding.color.setText( mColor.getColor() );
        } else if (mSize != null) {
            binding.sizeNumber.setVisibility( View.VISIBLE );
            binding.sizeText.setVisibility( View.VISIBLE );
            binding.sizeNumber.setText(""+ mSize.getSizeNumber() );
            binding.sizeText.setText( mSize.getSizeText() );
        } else if (mWeight != null) {
            binding.pond.setVisibility( View.VISIBLE );
            binding.weightKg.setVisibility( View.VISIBLE );
            binding.dozon.setVisibility( View.VISIBLE );
            binding.weightKg.setText(""+mWeight.getKg() );
            binding.dozon.setText(""+ mWeight.getDozan() );
            binding.pond.setText(""+ mWeight.getPond() );
        }else if (mImage != null) {
            binding.imageLay.setVisibility( View.VISIBLE );
            Glide.with(this).load( mImage.getImage() ).into( binding.imageView );
        }

        binding.btnChoose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseImage();
            }
        });

        binding.btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadImage();
            }
        });

        binding.submit.setOnClickListener( new View.OnClickListener() {
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
                            Glide.with(AddActivity.this).load( sUrl ).into( binding.imageView );
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
                binding.imageView.setImageBitmap(bitmap);
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
        final String color_name = binding.colorName.getText().toString().trim();
        String color_code = binding.color.getText().toString().trim();
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
        final String size_numbers = binding.sizeNumber.getText().toString().trim();
        String size_texts = binding.sizeText.getText().toString().trim();
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
        final String weight_kgs = binding.weightKg.getText().toString().trim();
        String dozons = binding.dozon.getText().toString().trim();
        String ponds = binding.pond.getText().toString().trim();
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
