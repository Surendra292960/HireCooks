package com.test.sample.hirecooks.Activity.ManageAccount.ManageProducts;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;
import com.test.sample.hirecooks.ApiServiceCall.ApiClient;
import com.test.sample.hirecooks.Models.Category.Category;
import com.test.sample.hirecooks.Models.MapLocationResponse.Map;
import com.test.sample.hirecooks.Models.MapLocationResponse.Result;
import com.test.sample.hirecooks.Models.SubCategory.Color;
import com.test.sample.hirecooks.Models.SubCategory.ColorExample;
import com.test.sample.hirecooks.Models.SubCategory.Example;
import com.test.sample.hirecooks.Models.SubCategory.Image;
import com.test.sample.hirecooks.Models.SubCategory.Size;
import com.test.sample.hirecooks.Models.SubCategory.SizeExample;
import com.test.sample.hirecooks.Models.SubCategory.Subcategory;
import com.test.sample.hirecooks.Models.SubCategory.Weight;
import com.test.sample.hirecooks.Models.SubCategory.WeightExample;
import com.test.sample.hirecooks.Models.users.User;
import com.test.sample.hirecooks.R;
import com.test.sample.hirecooks.Utils.ProgressBarUtil;
import com.test.sample.hirecooks.Utils.SharedPrefManager;
import com.test.sample.hirecooks.WebApis.MapApi;
import com.test.sample.hirecooks.WebApis.ProductApi;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

public class StartEditSubCategoryActivity extends AppCompatActivity {
    private static final int CODE_MULTIPLE_IMAGE_GALLARY = 2;
    private TextInputEditText editTextSubCategoryName, editTextSellRate, editTextDisplayRate,editTextAddress,
            editTextFirmId, editTextImagetUrl2, editTextImagetUrl3, editTextImagetUrl4,
            editTextWeight, available_stock, editTextShiledUrl, product_unique_key, age, brand, gender, search_key;
    private TextView Submit, add_weight, add_size, add_color, add_images,size_text,color_text,weight_text;
    private EditText editTextDiscription, editTextDetailDiscription;
    private ProductApi mService;
    private String categoryName;
    private User user;
    private Button btnChoose, btnUpload;
    private ImageView editTextUploadImage, editTextUploadImage2, editTextUploadImage3, editTextUploadImage4, editTextUploadShieldImage;
    private Uri filePath;
    private final int PICK_IMAGE_REQUEST = 1;
    private String sUrl;
    private RecyclerView weight_recycler, sizes_number_recycler,sizes_recycler, colors_recycler, images_recycler;
    //Firebase
    private FirebaseStorage storage;
    private StorageReference storageReference;
    private Uri ImageUri;
    private ProgressBarUtil progressBarUtil;
    private Subcategory mSubCategory;
    private ProgressBar size_progress, color_progress, weight_progress;
    Color color;
    Weight weight;
    private Image image;
    private Category mCategory;
    private List<Uri> ImageList = new ArrayList<>();
    private List<Uri> newImageList = new ArrayList<>();
    private Map maps;
    private List<Size> mSizeNumberList = new ArrayList<>(  );
    private List<Size> mSizesTextList = new ArrayList<>(  );
    private ArrayList<Image> mImagesList= new ArrayList<>(  );
    private List<Color> mColoList= new ArrayList<>(  );
    private ArrayList<Weight> mWeightList= new ArrayList<>(  );
    /**/
    private List<Size> sizeList = new ArrayList<>(  );
    private List<Image> imageList = new ArrayList<>(  );
    private List<Color> colorList = new ArrayList<>(  );
    private List<Weight> weightList = new ArrayList<>(  );
    /**/
    private Size sizeText;
    private Size sizeNumber;
    private LinearLayout image_upload_btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_start_edit_sub_category );
        Objects.requireNonNull( getSupportActionBar() ).setHomeButtonEnabled( true );
        Objects.requireNonNull( getSupportActionBar() ).setDisplayHomeAsUpEnabled( true );
        Objects.requireNonNull(getSupportActionBar()).setTitle("");
        this.getWindow().setSoftInputMode( WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            categoryName = bundle.getString( "CategoryName" );
            mSubCategory = (Subcategory) bundle.getSerializable( "SubCategory" );
            mCategory = (Category) bundle.getSerializable( "mCategory" );
        }

        if (mSubCategory != null) {
            getSupportActionBar().setTitle( mSubCategory.getName() );
        }

        initViews();
    }

    @SuppressLint("WrongConstant")
    private void initViews() {
        user = SharedPrefManager.getInstance( this ).getUser();
        progressBarUtil = new ProgressBarUtil( this );
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        weight_progress = findViewById( R.id.weight_progress );
        color_progress = findViewById( R.id.color_progress );
        size_progress = findViewById( R.id.size_progress );
        size_text = findViewById( R.id.size_text );
        weight_text = findViewById( R.id.weight_text );
        color_text = findViewById( R.id.color_text );
        editTextSubCategoryName = findViewById( R.id.editTextSubCategoryName );
        add_weight = findViewById( R.id.add_weight );
        add_size = findViewById( R.id.add_size );
        add_color = findViewById( R.id.add_color );
        add_images = findViewById( R.id.add_image );
        weight_recycler = findViewById( R.id.weight_recycler );
        sizes_recycler = findViewById( R.id.sizes_recycler );
        colors_recycler = findViewById( R.id.colors_recycler );
        images_recycler = findViewById( R.id.images_recycler );
        sizes_number_recycler = findViewById( R.id.sizes_number_recycler );
        editTextSellRate = findViewById( R.id.editTextSellRate );
        editTextDisplayRate = findViewById( R.id.editTextDisplayRate );
        editTextFirmId = findViewById( R.id.editTextFirmId );
        age = findViewById( R.id.age );
        gender = findViewById( R.id.gender );
        brand = findViewById( R.id.product_brand );
        search_key = findViewById( R.id.search_key );
        product_unique_key = findViewById( R.id.product_unique_key );
        editTextDiscription = findViewById( R.id.editTextDiscription );
        editTextDetailDiscription = findViewById( R.id.editTextDetailDiscription );
        editTextWeight = findViewById( R.id.editTextWeight );
        available_stock = findViewById( R.id.available_stock );
        btnChoose = findViewById( R.id.btnChoose );
        btnUpload = findViewById( R.id.btnUpload );
        image_upload_btn = findViewById( R.id.image_upload_btn );
        editTextImagetUrl2 = findViewById( R.id.editTextImagetUrl2 );
        editTextImagetUrl3 = findViewById( R.id.editTextImagetUrl3 );
        editTextImagetUrl4 = findViewById( R.id.editTextImagetUrl4 );
        editTextShiledUrl = findViewById( R.id.editTextShiledUrl );
        editTextUploadImage = findViewById( R.id.editTextUploadImage );
        editTextUploadImage2 = findViewById( R.id.editTextUploadImage2 );
        editTextUploadImage3 = findViewById( R.id.editTextUploadImage3 );
        editTextUploadImage4 = findViewById( R.id.editTextUploadImage4 );
        editTextUploadShieldImage = findViewById( R.id.editTextUploadShieldImage );
        editTextAddress = findViewById( R.id.address );
        Submit = findViewById( R.id.submit );

        getMapDetails(user.getId());

        if (mSubCategory!= null) {
            add_color.setVisibility( VISIBLE );
            add_size.setVisibility( VISIBLE );
            add_weight.setVisibility( VISIBLE );
            add_images.setVisibility( VISIBLE );
            add_weight.setText( "Update" );
            add_size.setText( "Update" );
            add_color.setText( "Update" );
            add_images.setText( "Update" );
            image_upload_btn.setVisibility( GONE );
            editTextSubCategoryName.setText( mSubCategory.getName() );
            age.setText( "" + mSubCategory.getAge() );
            brand.setText( mSubCategory.getBrand() );
            gender.setText( mSubCategory.getGender() );
            search_key.setText( mSubCategory.getSearchKey() );
            product_unique_key.setText( mSubCategory.getProductUniquekey() );
            editTextSellRate.setText( "" + mSubCategory.getSellRate() );
            editTextDisplayRate.setText( "" + mSubCategory.getDisplayRate() );
            editTextDiscription.setText( mSubCategory.getDiscription() );
            editTextDetailDiscription.setText( mSubCategory.getDetailDiscription() );
            editTextFirmId.setText( user.getFirmId() );
            editTextShiledUrl.setText( mSubCategory.getShieldLink() );
            editTextImagetUrl2.setText( mSubCategory.getLink2() );
            editTextImagetUrl3.setText( mSubCategory.getLink3() );
            editTextImagetUrl4.setText( mSubCategory.getLink4() );
            editTextShiledUrl.setText( mSubCategory.getShieldLink() );
            available_stock.setText( "" + mSubCategory.getAvailableStock() );
            editTextWeight.setVisibility( View.GONE );
            available_stock.setVisibility( View.VISIBLE );
            editTextShiledUrl.setVisibility( View.VISIBLE );

            if (mSubCategory.getWeights().size() != 0 && mSubCategory.getWeights() != null) {
                getProductWeight( mSubCategory.getProductUniquekey() );
            }
            if (mSubCategory.getSizes().size() != 0 && mSubCategory.getSizes() != null) {
                getProductSize( mSubCategory.getProductUniquekey() );

            }
            if (mSubCategory.getColors().size() != 0 && mSubCategory.getColors() != null) {
                getProductColor( mSubCategory.getProductUniquekey() );

            }
            if (mSubCategory.getImages().size() != 0 && mSubCategory.getImages() != null) {
                images_recycler.setHasFixedSize( true );
                ImagesAdapter adapter = new ImagesAdapter( StartEditSubCategoryActivity.this, mSubCategory.getImages() );
                images_recycler.setAdapter( adapter );
                images_recycler.setVisibility( View.VISIBLE );
                LinearLayoutManager linearLayoutManager = new LinearLayoutManager( StartEditSubCategoryActivity.this );
                if (StartEditSubCategoryActivity.this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
                    linearLayoutManager.setOrientation( LinearLayout.HORIZONTAL );
                } else {
                    linearLayoutManager.setOrientation( LinearLayout.HORIZONTAL );
                }
                RecyclerView.LayoutParams params = new RecyclerView.LayoutParams( RecyclerView.LayoutParams.MATCH_PARENT, RecyclerView.LayoutParams.WRAP_CONTENT );
                params.setMargins( 20, 20, 20, 20 );
                linearLayoutManager.canScrollHorizontally();

                images_recycler.setLayoutManager( linearLayoutManager );
                images_recycler.setItemAnimator( new DefaultItemAnimator() );
            }

            add_color.setOnClickListener( new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent( StartEditSubCategoryActivity.this, AddActivity.class );
                    Bundle bundle = new Bundle();
                    if (color != null) {
                        bundle.putSerializable( "Color", color );
                        intent.putExtras( bundle );
                        startActivity( intent );
                        color = null;
                    } else {
                        showalertbox("Please Select Color");
                        return;
                    }
                }
            } );

            add_size.setOnClickListener( new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent( StartEditSubCategoryActivity.this, AddActivity.class );
                    Bundle bundle = new Bundle();
                    if (sizeText != null) {
                        bundle.putSerializable( "Size", sizeText );
                        intent.putExtras( bundle );
                        startActivity( intent );
                        sizeText = null;
                    }else if (sizeNumber != null) {
                        bundle.putSerializable( "Size", sizeNumber );
                        intent.putExtras( bundle );
                        startActivity( intent );
                        sizeNumber = null;
                    } else {
                        showalertbox("Please Select Size");
                        return;
                    }
                }
            } );
            add_weight.setOnClickListener( new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent( StartEditSubCategoryActivity.this, AddActivity.class );
                    Bundle bundle = new Bundle();
                    if (weight != null&&add_weight.getText().toString().equalsIgnoreCase( "Update" )) {
                        bundle.putSerializable( "Weight", weight );
                        intent.putExtras( bundle );
                        startActivity( intent );
                        weight = null;
                    } else {
                        showalertbox("Please Select Weight");
                        return;
                    }
                }
            } );
            add_images.setOnClickListener( new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent( StartEditSubCategoryActivity.this, AddActivity.class );
                    Bundle bundle = new Bundle();
                    if (image != null) {
                        bundle.putSerializable( "Image", image );
                        intent.putExtras( bundle );
                        startActivity( intent );
                        image = null;
                    } else {
                        showalertbox("Please Select Image");
                        return;
                    }
                }
            } );
        } else{
            if (mCategory != null) {
                add_color.setVisibility( GONE );
                add_size.setVisibility( GONE );
                add_weight.setVisibility( VISIBLE );
                add_images.setVisibility( GONE );
                color_text.setText( "Select Color :" );
                size_text.setText( "Select Size :" );
                weight_text.setText( "Select Weight :" );
                image_upload_btn.setVisibility( VISIBLE );
                editTextFirmId.setText( user.getFirmId() );
                getSelectSize( );
                getSelectColor( );
                add_weight.setOnClickListener( new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showalertbox();
                    }
                } );
            }
        }

        Submit.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validation();
            }
        } );

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
    }

    private void validation() {
        final String search_key_ = search_key.getText().toString().trim();
        final int pincode = maps.getPincode();
        final String lat_ = maps.getLatitude();
        final String lang_ = maps.getLongitude();
        final String address = editTextAddress.getText().toString().trim();
        final String subcategoryname = editTextSubCategoryName.getText().toString().trim();
        final String sellRate = editTextSellRate.getText().toString().trim();
        final String displayRate = editTextDisplayRate.getText().toString().trim();
        final String firmId = user.getFirmId();
        final String discription = editTextDiscription.getText().toString().trim();
        final String detail_discription = editTextDetailDiscription.getText().toString().trim();
        String available_stock_ = available_stock.getText().toString().trim();
        String shiledUrl = editTextShiledUrl.getText().toString().trim();
        final String age_ = age.getText().toString().trim();
        final String gender_ = gender.getText().toString().trim();
        final String brand_ = brand.getText().toString().trim();
        final String product_unique_key_ = product_unique_key.getText().toString().trim();

        if (TextUtils.isEmpty( search_key_ )) {
            search_key.setError( "Please enter search_key" );
            search_key.requestFocus();
            return;
        }
        if (TextUtils.isEmpty( subcategoryname )) {
            editTextSubCategoryName.setError( "Please enter SubCategoryName" );
            editTextSubCategoryName.requestFocus();
            return;
        }
        if (TextUtils.isEmpty( sellRate )) {
            editTextSellRate.setError( "Please enter sellRate" );
            editTextSellRate.requestFocus();
            return;
        }
        if (TextUtils.isEmpty( displayRate )) {
            editTextDisplayRate.setError( "Please enter displayRate" );
            editTextDisplayRate.requestFocus();
            return;
        }
        if (TextUtils.isEmpty( product_unique_key_ )) {
            product_unique_key.setError( "Please enter Product Unique Key" );
            product_unique_key.requestFocus();
            return;
        } if (TextUtils.isEmpty( age_ )) {
            age.setError( "Please enter Age" );
            age.requestFocus();
            return;
        } if (TextUtils.isEmpty( brand_ )) {
            brand.setError( "Please enter Brand" );
            brand.requestFocus();
            return;
        } if (TextUtils.isEmpty( gender_ )) {
            gender.setError( "Please enter Gender" );
            gender.requestFocus();
            return;
        } if (TextUtils.isEmpty( address )) {
            editTextAddress.setError( "Please enter Address" );
            editTextAddress.requestFocus();
            return;
        } if (TextUtils.isEmpty( firmId )) {
            editTextFirmId.setError( "Please enter firmId" );
            editTextFirmId.requestFocus();
            return;
        }
        if (TextUtils.isEmpty( discription )) {
            editTextDiscription.setError( "Please enter discription" );
            editTextDiscription.requestFocus();
            return;
        }
        if (TextUtils.isEmpty( detail_discription )) {
            editTextDetailDiscription.setError( "Please enter Detail Discription" );
            editTextDetailDiscription.requestFocus();
            return;
        }
        if (TextUtils.isEmpty( shiledUrl )) {
            shiledUrl = "Null";
        }
        if (TextUtils.isEmpty( available_stock_ ) && available_stock_ == null) {
            available_stock_ = "0";
        }else{
            available_stock_ = available_stock.getText().toString().trim();
        }

        if(mWeightList!=null&&mWeightList.size()!=0){
            weightList = (ArrayList<Weight>)((ArrayList<Weight>)mWeightList).clone();
        }else {
           // showalertbox("Please Select All Weight");
           // return;
        }
        if(mSizeNumberList!=null&&mSizeNumberList.size()!=0||mSizesTextList!=null&&mSizesTextList.size()!=0){
            sizeList = (ArrayList<Size>)((ArrayList<Size>)mSizeNumberList).clone();
            sizeList = (ArrayList<Size>)((ArrayList<Size>)mSizesTextList).clone();
        }else {
           // showalertbox("Please Select All Size");
           // return;
        }
        if(mColoList!=null&&mColoList.size()!=0){
            colorList = (ArrayList<Color>)((ArrayList<Color>)mColoList).clone();
        }
        else {
           // showalertbox("Please Select All Color");
           // return;
        }
        if(mImagesList!=null&&mImagesList.size()!=0){
            imageList = (ArrayList<Image>)((ArrayList<Image>)mImagesList).clone();
        }else {
            showalertbox("Please Add Images");
            return;
        }

        if(mCategory!=null&&mSubCategory==null){
            final int subcategoryId = mCategory.getCategoryid();
            Subcategory subcategory = new Subcategory(
                    mCategory.getId(),
                    String.valueOf(subcategoryId), "",
                    search_key_,
                    subcategoryname,
                    product_unique_key_, "",  "",  "",
                    "", discription, detail_discription, Integer.parseInt( sellRate ),
                    Integer.parseInt( displayRate ),firmId, Double.parseDouble( lat_ ) ,
                    Double.parseDouble( lang_ ) ,address,pincode,colorList,imageList,sizeList,weightList, 0.0,
                    available_stock_,
                    1,
                    brand_,gender_,
                    Integer.parseInt( age_ ));
            List<Example> exampleList = new ArrayList<>(  );
            List<Subcategory> subcategoryList = new ArrayList<>(  );
            subcategoryList.add( subcategory );
            Example example = new Example();
            example.setSubcategory( subcategoryList );
            exampleList.add( example );
            Gson gson = new Gson();
            String json = gson.toJson( exampleList );
            System.out.println("Suree Json: "+ json );
            addSubcategory(exampleList);
        }else if(mSubCategory!=null&&mCategory==null){
            final int subcategoryId = Integer.parseInt( mSubCategory.getSubcategoryid() );
            Subcategory subcategory = new Subcategory(
                    mSubCategory.getId(),String.valueOf(subcategoryId),"",search_key_, subcategoryname, product_unique_key_, "",  "",  "",
                    "", discription, detail_discription, Integer.parseInt( sellRate ), Integer.parseInt( displayRate ),firmId, Double.parseDouble( lat_ ) ,
                    Double.parseDouble( lang_ ) ,address,pincode,colorList,imageList,sizeList,weightList, 0.0,available_stock_, 1,brand_,gender_,
                    Integer.parseInt( age_ ));
            List<Example> exampleList = new ArrayList<>(  );
            List<Subcategory> subcategoryList = new ArrayList<>(  );
            subcategoryList.add( subcategory );
            Example example = new Example();
            example.setSubcategory( subcategoryList );
            exampleList.add( example );
            Gson gson = new Gson();
            String json = gson.toJson( exampleList );
            System.out.println("Suree Json: "+ json );
            updateSubcategory(mSubCategory.getId(),exampleList);
        }
    }

    private void showalertbox(String string) {
        final android.app.AlertDialog.Builder dialogBuilder = new android.app.AlertDialog.Builder( this);
        LayoutInflater inflater = this.getLayoutInflater();
        View view = inflater.inflate(R.layout.show_alert_message,null);
        TextView ask = view.findViewById( R.id.ask );
        TextView textView = view.findViewById( R.id.text );
        ask.setText( string );
        textView.setText( "Alert !" );
        AppCompatTextView cancelBtn = view.findViewById(R.id.exit_app_btn);
        dialogBuilder.setView(view);
        final android.app.AlertDialog dialog = dialogBuilder.create();
        dialog.show();
        cancelBtn.setOnClickListener( v -> {
            try {
                dialog.dismiss();
            }
            catch (Exception ex) {
                ex.printStackTrace();
            }
        } );
    }

    @SuppressLint("WrongConstant")
    private void showalertbox() {
        final android.app.AlertDialog.Builder dialogBuilder = new android.app.AlertDialog.Builder( StartEditSubCategoryActivity.this);
        LayoutInflater inflater = this.getLayoutInflater();
        View view = inflater.inflate(R.layout.add_weight,null);
        TextInputEditText weight_kg = view.findViewById( R.id.weight_kg );
        TextInputEditText weight_lt = view.findViewById( R.id.weight_lt );
        TextInputEditText weight_gm = view.findViewById( R.id.weight_gm );
        TextInputEditText weight_pound = view.findViewById( R.id.weight_pound );
        TextInputEditText weight_dozan = view.findViewById( R.id.weight_dozan );
        AppCompatButton okBtn = view.findViewById(R.id.ok_btn);
        dialogBuilder.setView(view);
        final android.app.AlertDialog dialog = dialogBuilder.create();
        dialog.show();
        okBtn.setOnClickListener( v -> {
            try {
                final String kg = weight_kg.getText().toString().trim();
                final String lt = weight_lt.getText().toString().trim();
                final String gm = weight_gm.getText().toString().trim();
                final String pound = weight_pound.getText().toString().trim();
                final String dozan = weight_dozan.getText().toString().trim();
                if(TextUtils.isEmpty( kg )){
                    weight_kg.setError("Please enter Wieght");
                    weight_kg.requestFocus();
                    return;
                }if(TextUtils.isEmpty( lt )){
                    weight_lt.setError("Please enter Litre");
                    weight_lt.requestFocus();
                    return;
                }if(TextUtils.isEmpty( gm )){
                    weight_gm.setError("Please enter gram");
                    weight_gm.requestFocus();
                    return;
                }if(TextUtils.isEmpty( dozan )){
                    weight_dozan.setError("Please enter dozan");
                    weight_dozan.requestFocus();
                    return;
                }if(TextUtils.isEmpty( pound )){
                    weight_pound.setError("Please enter pound");
                    weight_pound.requestFocus();
                    return;
                }
                List<Weight> weightArrayList = new ArrayList<>(  );
                Weight weight1 = new Weight();
                weight1.setKg(  Integer.parseInt( kg ) );
                weight1.setPond(  Integer.parseInt( pound ) );
                weight1.setDozan(Integer.parseInt( dozan )  );
                weightArrayList.add( weight1 );
                weight_progress.setVisibility( View.GONE );
                weight_recycler.setHasFixedSize( true );
                weight_recycler.setLayoutManager( new GridLayoutManager( StartEditSubCategoryActivity.this, 4 ) );
                WeightAdapter adapter = new WeightAdapter( StartEditSubCategoryActivity.this, weightArrayList );
                weight_recycler.setAdapter( adapter );
                weight_recycler.setVisibility( View.VISIBLE );
                LinearLayoutManager linearLayoutManager = new LinearLayoutManager( StartEditSubCategoryActivity.this );
                if (StartEditSubCategoryActivity.this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
                    linearLayoutManager.setOrientation( LinearLayout.HORIZONTAL );
                } else {
                    linearLayoutManager.setOrientation( LinearLayout.HORIZONTAL );
                }
                dialog.dismiss();
            }
            catch (Exception ex) {
                ex.printStackTrace();
            }
        } );
    }

    private void addSubcategory(List<Example> exampleList) {
        ProductApi mService = ApiClient.getClient().create(ProductApi.class);
        Call<ArrayList<Example>> call = mService.addSubcategory(exampleList);
        call.enqueue(new Callback<ArrayList<Example>>() {
            @SuppressLint("WrongConstant")
            @Override
            public void onResponse(Call<ArrayList<Example>> call, Response<ArrayList<Example>> response) {
                int statusCode = response.code();
                if (statusCode == 200) {
                    for(Example example:response.body()){
                        if(example.getError()==false){
                             Toast.makeText( StartEditSubCategoryActivity.this, example.getMessage(), Toast.LENGTH_SHORT ).show();
                             finish();
                        }else{
                            Toast.makeText( StartEditSubCategoryActivity.this, example.getMessage(), Toast.LENGTH_SHORT ).show();
                        }
                    }
                } else {
                  //  Toast.makeText( StartEditSubCategoryActivity.this, R.string.failed_due_to + statusCode, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<ArrayList<Example>> call, Throwable t) {
                System.out.println("Suree : " + t.getMessage());
            }
        });
    }

    private void updateSubcategory(int id, List<Example> exampleList) {
        ProductApi mService = ApiClient.getClient().create(ProductApi.class);
        Call<ArrayList<Example>> call = mService.updateSubcategory(id,exampleList);
        call.enqueue(new Callback<ArrayList<Example>>() {
            @SuppressLint("WrongConstant")
            @Override
            public void onResponse(Call<ArrayList<Example>> call, Response<ArrayList<Example>> response) {
                int statusCode = response.code();
                if (statusCode == 200) {
                    for(Example example:response.body()){
                        if(example.getError()==false){
                             Toast.makeText( StartEditSubCategoryActivity.this, example.getMessage(), Toast.LENGTH_SHORT ).show();
                             finish();
                        }else{
                            Toast.makeText( StartEditSubCategoryActivity.this, example.getMessage(), Toast.LENGTH_SHORT ).show();
                        }

                    }
                } else {
                  //  Toast.makeText( StartEditSubCategoryActivity.this, R.string.failed_due_to + statusCode, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<ArrayList<Example>> call, Throwable t) {
                System.out.println("Suree : " + t.getMessage());
            }
        });
    }

    private void getMapDetails(final Integer id) {
        MapApi mapApi = ApiClient.getClient().create(MapApi.class);
        Call<Result> postMapDetailsResponse = mapApi.getMapDetails(id);
        postMapDetailsResponse.enqueue(new Callback<Result>() {
            @Override
            public void onResponse(@NonNull Call<Result> call, @NonNull Response<Result> response) {
                if(response.code() == 200 && response.body() != null) {
                    try{
                        if(response.body().getError()==false){
                            maps = response.body().getMaps();
                            editTextAddress.setText( maps.getAddress() );
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<Result> call, @NonNull Throwable t) {
                System.out.println("suree: "+t.getMessage());
            }
        });
    }

    private void getSelectSize() {
        size_progress.setVisibility( View.VISIBLE );
        ProductApi mService = ApiClient.getClient().create( ProductApi.class );
        Call<List<SizeExample>> call = mService.getSelectSize( );
        call.enqueue( new Callback<List<SizeExample>>() {
            @SuppressLint("WrongConstant")
            @Override
            public void onResponse(Call<List<SizeExample>> call, Response<List<SizeExample>> response) {
                int statusCode = response.code();
                if (statusCode == 200) {
                    for (SizeExample sizeExample1 : response.body()) {
                        if (sizeExample1.getError() == false) {
                            size_progress.setVisibility( View.GONE );
                            sizes_recycler.setHasFixedSize( true );
                            sizes_recycler.setLayoutManager( new GridLayoutManager( StartEditSubCategoryActivity.this, 5 ) );
                            SizeTextAdapter adapter = new SizeTextAdapter( StartEditSubCategoryActivity.this, sizeExample1.getSizes() );
                            sizes_recycler.setAdapter( adapter );
                            sizes_recycler.setVisibility( View.VISIBLE );
                            sizes_number_recycler.setHasFixedSize( true );
                            sizes_number_recycler.setLayoutManager( new GridLayoutManager( StartEditSubCategoryActivity.this, 4 ) );
                            SizeNumberAdapter mAdapter = new SizeNumberAdapter( StartEditSubCategoryActivity.this, sizeExample1.getSizes() );
                            sizes_number_recycler.setAdapter( mAdapter );
                            sizes_number_recycler.setVisibility( View.VISIBLE );
                            LinearLayoutManager linearLayoutManager = new LinearLayoutManager( StartEditSubCategoryActivity.this );
                            if (StartEditSubCategoryActivity.this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
                                linearLayoutManager.setOrientation( LinearLayout.HORIZONTAL );
                            } else {
                                linearLayoutManager.setOrientation( LinearLayout.HORIZONTAL );
                            }

                        } else {
                            Toast.makeText( StartEditSubCategoryActivity.this, sizeExample1.getMessage(), Toast.LENGTH_SHORT ).show();
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<List<SizeExample>> call, Throwable t) {
                System.out.println( "Suree : " + t.getMessage() );
            }
        } );
    }

    private void getSelectColor() {
        color_progress.setVisibility( View.VISIBLE );
        ProductApi mService = ApiClient.getClient().create( ProductApi.class );
        Call<List<ColorExample>> call = mService.getSelectColor( );
        call.enqueue( new Callback<List<ColorExample>>() {
            @SuppressLint("WrongConstant")
            @Override
            public void onResponse(Call<List<ColorExample>> call, Response<List<ColorExample>> response) {
                int statusCode = response.code();
                if (statusCode == 200) {
                    for (ColorExample colorExample1 : response.body()) {
                        if (colorExample1.getError() == false) {
                            color_progress.setVisibility( View.GONE );
                            colors_recycler.setHasFixedSize( true );
                            colors_recycler.setLayoutManager( new GridLayoutManager( StartEditSubCategoryActivity.this, 4 ) );
                            ColorsAdapter adapter = new ColorsAdapter( StartEditSubCategoryActivity.this, colorExample1.getColors() );
                            colors_recycler.setAdapter( adapter );
                            colors_recycler.setVisibility( View.VISIBLE );
                            LinearLayoutManager linearLayoutManager = new LinearLayoutManager( StartEditSubCategoryActivity.this );
                            if (StartEditSubCategoryActivity.this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
                                linearLayoutManager.setOrientation( LinearLayout.HORIZONTAL );
                            } else {
                                linearLayoutManager.setOrientation( LinearLayout.HORIZONTAL );
                            }
                        } else {
                            Toast.makeText( StartEditSubCategoryActivity.this, colorExample1.getMessage(), Toast.LENGTH_SHORT ).show();
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<List<ColorExample>> call, Throwable t) {
                System.out.println( "Suree : " + t.getMessage() );
            }
        } );
    }

    private void getProductWeight(String productUniquekey) {
        weight_progress.setVisibility( View.VISIBLE );
        ProductApi mService = ApiClient.getClient().create( ProductApi.class );
        Call<List<WeightExample>> call = mService.getProductWeight( productUniquekey );
        call.enqueue( new Callback<List<WeightExample>>() {
            @SuppressLint("WrongConstant")
            @Override
            public void onResponse(Call<List<WeightExample>> call, Response<List<WeightExample>> response) {
                int statusCode = response.code();
                if (statusCode == 200) {
                    for (WeightExample weightExample1 : response.body()) {
                        if (weightExample1.getError() == false) {
                            weight_progress.setVisibility( View.GONE );
                            weight_recycler.setHasFixedSize( true );
                            weight_recycler.setLayoutManager( new GridLayoutManager( StartEditSubCategoryActivity.this, 4 ) );
                            WeightAdapter adapter = new WeightAdapter( StartEditSubCategoryActivity.this, weightExample1.getWeights() );
                            weight_recycler.setAdapter( adapter );
                            weight_recycler.setVisibility( View.VISIBLE );
                            LinearLayoutManager linearLayoutManager = new LinearLayoutManager( StartEditSubCategoryActivity.this );
                            if (StartEditSubCategoryActivity.this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
                                linearLayoutManager.setOrientation( LinearLayout.HORIZONTAL );
                            } else {
                                linearLayoutManager.setOrientation( LinearLayout.HORIZONTAL );
                            }
                        } else {
                            Toast.makeText( StartEditSubCategoryActivity.this, weightExample1.getMessage(), Toast.LENGTH_SHORT ).show();
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<List<WeightExample>> call, Throwable t) {
                System.out.println( "Suree : " + t.getMessage() );
            }
        } );
    }

    private void getProductSize(String productUniquekey) {
        size_progress.setVisibility( View.VISIBLE );
        ProductApi mService = ApiClient.getClient().create( ProductApi.class );
        Call<List<SizeExample>> call = mService.getProductSize( productUniquekey );
        call.enqueue( new Callback<List<SizeExample>>() {
            @SuppressLint("WrongConstant")
            @Override
            public void onResponse(Call<List<SizeExample>> call, Response<List<SizeExample>> response) {
                List<Size> NumberSizeList = new ArrayList<>(  );
                List<Size> textSizeList = new ArrayList<>(  );
                int statusCode = response.code();
                if (statusCode == 200) {
                    for (SizeExample sizeExample1 : response.body()) {
                        if (sizeExample1.getError() == false) {
                            size_progress.setVisibility( View.GONE );

                            for(Size size:sizeExample1.getSizes()){
                                if(size.getSizeNumber()!=0){
                                    NumberSizeList.add( size );
                                }else{
                                    textSizeList.add( size );
                                }
                            }
                           if(textSizeList.size()!=0&&textSizeList!=null){
                               sizes_recycler.setVisibility( VISIBLE );
                               sizes_recycler.setHasFixedSize( true );
                               sizes_recycler.setLayoutManager( new GridLayoutManager( StartEditSubCategoryActivity.this, 4 ) );
                               SizeTextAdapter adapter = new SizeTextAdapter( StartEditSubCategoryActivity.this, textSizeList );
                               sizes_recycler.setAdapter( adapter );
                           }else {
                               sizes_number_recycler.setVisibility( VISIBLE );
                               sizes_number_recycler.setHasFixedSize( true );
                               sizes_number_recycler.setLayoutManager( new GridLayoutManager( StartEditSubCategoryActivity.this, 4 ) );
                               SizeNumberAdapter mAdapter = new SizeNumberAdapter( StartEditSubCategoryActivity.this, NumberSizeList );
                               sizes_number_recycler.setAdapter( mAdapter );
                           }

                            LinearLayoutManager linearLayoutManager = new LinearLayoutManager( StartEditSubCategoryActivity.this );
                            if (StartEditSubCategoryActivity.this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
                                linearLayoutManager.setOrientation( LinearLayout.HORIZONTAL );
                            } else {
                                linearLayoutManager.setOrientation( LinearLayout.HORIZONTAL );
                            }
                        } else {
                            Toast.makeText( StartEditSubCategoryActivity.this, sizeExample1.getMessage(), Toast.LENGTH_SHORT ).show();
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<List<SizeExample>> call, Throwable t) {
                System.out.println( "Suree : " + t.getMessage() );
            }
        } );
    }

    private void getProductColor(String productUniquekey) {
        color_progress.setVisibility( View.VISIBLE );
        ProductApi mService = ApiClient.getClient().create( ProductApi.class );
        Call<List<ColorExample>> call = mService.getProductColor( productUniquekey );
        call.enqueue( new Callback<List<ColorExample>>() {
            @SuppressLint("WrongConstant")
            @Override
            public void onResponse(Call<List<ColorExample>> call, Response<List<ColorExample>> response) {
                int statusCode = response.code();
                if (statusCode == 200) {
                    for (ColorExample colorExample1 : response.body()) {
                        if (colorExample1.getError() == false) {
                            color_progress.setVisibility( View.GONE );
                            colors_recycler.setHasFixedSize( true );
                            colors_recycler.setLayoutManager( new GridLayoutManager( StartEditSubCategoryActivity.this, 4 ) );
                            ColorsAdapter adapter = new ColorsAdapter( StartEditSubCategoryActivity.this, colorExample1.getColors() );
                            colors_recycler.setAdapter( adapter );
                            colors_recycler.setVisibility( View.VISIBLE );
                            LinearLayoutManager linearLayoutManager = new LinearLayoutManager( StartEditSubCategoryActivity.this );
                            if (StartEditSubCategoryActivity.this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
                                linearLayoutManager.setOrientation( LinearLayout.HORIZONTAL );
                            } else {
                                linearLayoutManager.setOrientation( LinearLayout.HORIZONTAL );
                            }
                        } else {
                            Toast.makeText( StartEditSubCategoryActivity.this, colorExample1.getMessage(), Toast.LENGTH_SHORT ).show();
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<List<ColorExample>> call, Throwable t) {
                System.out.println( "Suree : " + t.getMessage() );
            }
        } );
    }

    private void chooseImage() {
        Intent intent = new Intent();
        intent.setType( "image/*" );
        intent.putExtra( Intent.EXTRA_ALLOW_MULTIPLE, true );
        intent.setAction( Intent.ACTION_GET_CONTENT );
        startActivityForResult( Intent.createChooser( intent, "Select Picture" ), PICK_IMAGE_REQUEST );
    }

    private void uploadImage() {
        if (ImageList != null && ImageList.size() != 0) {
            final ProgressDialog progress = new ProgressDialog( this );
            progress.setTitle( "Uploading...." );
            progress.setCancelable( false );
            progress.show();

            Uri[] uri = new Uri[ImageList.size()];
            for (int i = 0; i < ImageList.size(); i++) {
                uri[i] = Uri.parse( ImageList.get( i ).toString() );
                StorageReference ref = storageReference.child( "images/" + UUID.randomUUID().toString() );
                ref.putFile( uri[i] ).addOnSuccessListener( new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        ref.getDownloadUrl().addOnSuccessListener( new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                progress.dismiss();
                                newImageList.add( uri );
                                if (newImageList.size() >= 4) {
                                    setImages( newImageList );
                                }
                                Toast.makeText( StartEditSubCategoryActivity.this, "Uploaded successfully", Toast.LENGTH_SHORT ).show();
                            }
                        } ).addOnFailureListener( new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                progress.dismiss();
                                Toast.makeText( StartEditSubCategoryActivity.this, "Failed " + e.getMessage(), Toast.LENGTH_SHORT ).show();
                            }
                        } );
                    }
                } ).addOnProgressListener( new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                        double progres_time = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                        progress.setMessage( "Uploaded " + (int) progres_time + " %" );
                    }
                } );

            }
        }
    }

    @SuppressLint("WrongConstant")
    private void setImages(List<Uri> newImageList) {

        if (newImageList != null && newImageList.size() != 0) {
/*            for(int i=0; i<newImageList.size(); i++ ){
                Image image = new Image();
                image.setImage( newImageList.get( i ).toString() );
                mImagesList.add(image);
            }*/
            images_recycler.setHasFixedSize( true );
            AddImagesAdapter adapter = new AddImagesAdapter( StartEditSubCategoryActivity.this, newImageList );
            images_recycler.setAdapter( adapter );
            images_recycler.setVisibility( View.VISIBLE );
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager( StartEditSubCategoryActivity.this );
            if (StartEditSubCategoryActivity.this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
                linearLayoutManager.setOrientation( LinearLayout.HORIZONTAL );
            } else {
                linearLayoutManager.setOrientation( LinearLayout.HORIZONTAL );
            }
            RecyclerView.LayoutParams params = new RecyclerView.LayoutParams( RecyclerView.LayoutParams.MATCH_PARENT, RecyclerView.LayoutParams.WRAP_CONTENT );
            params.setMargins( 20, 20, 20, 20 );
            linearLayoutManager.canScrollHorizontally();

            images_recycler.setLayoutManager( linearLayoutManager );
            images_recycler.setItemAnimator( new DefaultItemAnimator() );
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult( requestCode, resultCode, data );
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK) {
            if (data.getClipData() != null) {
                int countClipData = data.getClipData().getItemCount();
                int currentImageSlect = 0;
                ImageList = new ArrayList<>();
                while (currentImageSlect < countClipData) {
                    ImageUri = data.getClipData().getItemAt( currentImageSlect ).getUri();
                    ImageList.add( ImageUri );
                    currentImageSlect = currentImageSlect + 1;
                }

            } else {
                showalertbox("Please Select 4 Images");
            }
        }
    }

    @Override
    public void onBackPressed() {
        this.finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(mSubCategory!=null&&mSubCategory.getProductUniquekey()!=null){
            getProductColor( mSubCategory.getProductUniquekey() );
            getProductSize( mSubCategory.getProductUniquekey() );
            getProductWeight( mSubCategory.getProductUniquekey() );
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            this.finish();
        }
        return super.onOptionsItemSelected( item );
    }


    public class AddImagesAdapter extends RecyclerView.Adapter<AddImagesAdapter.ViewHolder> {
        private Context mCtx;
        private List<Uri> images;

        public AddImagesAdapter(Context mCtx, List<Uri> images) {
            this.mCtx = mCtx;
            this.images = images;
        }

        @Override
        public AddImagesAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View itemLayoutView = LayoutInflater.from( viewGroup.getContext() ).inflate( R.layout.cooks_promotion_adapter, null );
            AddImagesAdapter.ViewHolder viewHolder = new AddImagesAdapter.ViewHolder( itemLayoutView );
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(AddImagesAdapter.ViewHolder holder, int position) {
            Uri image1 = images.get( position );
            Image image  = new Image();
            if (image1.getPath() != null) {
                image.setImage( image1.toString() );
                image.setColorName( "Green");
                mImagesList.add( image );
                Picasso.with( mCtx ).load( image1.toString() ).into( holder.imageView );
            }
        }


        @Override
        public int getItemCount() {
            return images == null ? 0 : images.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            private ImageView imageView;

            public ViewHolder(View itemLayoutView) {
                super( itemLayoutView );
                imageView = itemLayoutView.findViewById( R.id.imageView );
            }
        }
    }


    public class ImagesAdapter extends RecyclerView.Adapter<ImagesAdapter.ViewHolder> {
        private Context mCtx;
        private List<Image> images;
        private ArrayList<Image> xImageList = new ArrayList<>(  );

        public ImagesAdapter(Context mCtx, List<Image> images) {
            this.mCtx = mCtx;
            this.images = images;
        }

        @Override
        public ImagesAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View itemLayoutView = LayoutInflater.from( viewGroup.getContext() ).inflate( R.layout.cooks_promotion_adapter, null );
            ImagesAdapter.ViewHolder viewHolder = new ImagesAdapter.ViewHolder( itemLayoutView );
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(ImagesAdapter.ViewHolder holder, int position) {
            Image image1 = images.get( position );
            if (image1.getImage() != null) {
                Picasso.with( mCtx ).load( image1.getImage() ).into( holder.imageView );
                holder.imageView.setOnClickListener( new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        xImageList = new ArrayList<>(  );
                        image1.setSelected(!image1.isSelected());
                        if(image1.isSelected()){
                            xImageList.add( image1 );
                        }
                        for(Image image2:xImageList){
                            if (image2.isSelected()) {
                                image2.setImage( image2.getImage() );
                                mImagesList.add( image2 );
                                image = image2;
                                holder.checkBox.setChecked( true );
                            } else {
                                holder.checkBox.setChecked( false );
                            }
                        }
                    }
                });
            }
        }


        @Override
        public int getItemCount() {
            return images == null ? 0 : images.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            private ImageView imageView;
            private CheckBox checkBox;

            public ViewHolder(View itemLayoutView) {
                super( itemLayoutView );
                imageView = itemLayoutView.findViewById( R.id.imageView );
                checkBox = itemLayoutView.findViewById( R.id.checkbox );
            }
        }
    }

    public class WeightAdapter extends RecyclerView.Adapter<WeightAdapter.ViewHolder> {
        private Context mCtx;
        private List<Weight> weights;
        private ArrayList<Weight> xWeightList = new ArrayList<>(  );

        public WeightAdapter(Context mCtx, List<Weight> weights) {
            this.mCtx = mCtx;
            this.weights = weights;
        }

        @NonNull
        @Override
        public WeightAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View itemLayoutView = LayoutInflater.from( parent.getContext() ).inflate( R.layout.text, null );
            WeightAdapter.ViewHolder viewHolder = new WeightAdapter.ViewHolder( itemLayoutView );
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(@NonNull WeightAdapter.ViewHolder holder, int position) {
            final Weight weight1 = weights.get( position );
            if (weight1.getKg() != 0) {
                holder.item_weight.setVisibility( View.VISIBLE );
                holder.item_weight_dozan.setVisibility( View.GONE );
                holder.item_weight_pond.setVisibility( View.GONE );
                holder.item_weight.setText( "" + weight1.getKg()+" Kg" );
            } else if (weight1.getDozan() != 0) {
                holder.item_weight_dozan.setVisibility( View.VISIBLE );
                holder.item_weight.setVisibility( GONE );
                holder.item_weight_pond.setVisibility( GONE );
                holder.item_weight_dozan.setText( weight1.getDozan() + "\n Dozan" );
            } else if (weight1.getPond() != 0) {
                holder.item_weight_dozan.setVisibility( GONE );
                holder.item_weight.setVisibility( GONE );
                holder.item_weight_pond.setVisibility( VISIBLE );
                holder.item_weight_pond.setText( "" + weight1.getPond()+ "\n Pound" );
            }
            holder.item_weight.setOnClickListener( new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mWeightList = new ArrayList<>(  );
                    weight1.setSelected(!weight1.isSelected());
                    if(weight1.isSelected()){
                        xWeightList.add( weight1 );
                    }
                    for(Weight weight2:xWeightList){
                        if (weight2.isSelected()) {
                            weight2.setKg( weight2.getKg() );
                            weight2.setDozan( weight2.getDozan() );
                            weight2.setPond( weight2.getPond() );
                            mWeightList.add( weight2 );
                            weight = weight2;
                            holder.item_weight.setBackgroundColor( android.graphics.Color.parseColor( "#567845" ) );
                            holder.item_weight.setTextColor( android.graphics.Color.parseColor( "#ffffff" ) );
                        } else {
                            holder.item_weight.setBackgroundColor( android.graphics.Color.parseColor( "#ffffff" ) );
                            holder.item_weight.setTextColor( android.graphics.Color.parseColor( "#000000" ) );
                        }
                    }
                }
            } );
            holder.item_weight_dozan.setOnClickListener( new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mWeightList = new ArrayList<>(  );
                    weight1.setSelected(!weight1.isSelected());
                    if(weight1.isSelected()){
                        xWeightList.add( weight1 );
                    }
                    for(Weight weight2:xWeightList){
                        if (weight2.isSelected()) {
                            weight2.setKg( weight2.getKg() );
                            weight2.setDozan( weight2.getDozan() );
                            weight2.setPond( weight2.getPond() );
                            mWeightList.add( weight2 );
                            weight = weight2;
                            holder.item_weight_dozan.setBackgroundColor( android.graphics.Color.parseColor( "#567845" ) );
                            holder.item_weight_dozan.setTextColor( android.graphics.Color.parseColor( "#ffffff" ) );
                        } else {
                            holder.item_weight_dozan.setBackgroundColor( android.graphics.Color.parseColor( "#ffffff" ) );
                            holder.item_weight_dozan.setTextColor( android.graphics.Color.parseColor( "#000000" ) );
                        }
                    }
                }
            } );
            holder.item_weight_pond.setOnClickListener( new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mWeightList = new ArrayList<>(  );
                    weight1.setSelected(!weight1.isSelected());
                    if(weight1.isSelected()){
                        xWeightList.add( weight1 );
                    }
                    for(Weight weight2:xWeightList){
                        if (weight2.isSelected()) {
                            weight2.setKg( weight2.getKg() );
                            weight2.setDozan( weight2.getDozan() );
                            weight2.setPond( weight2.getPond() );
                            mWeightList.add( weight2 );
                            weight = weight2;
                            holder.item_weight_pond.setBackgroundColor( android.graphics.Color.parseColor( "#567845" ) );
                            holder.item_weight_pond.setTextColor( android.graphics.Color.parseColor( "#ffffff" ) );
                        } else {
                            holder.item_weight_pond.setBackgroundColor( android.graphics.Color.parseColor( "#ffffff" ) );
                            holder.item_weight_pond.setTextColor( android.graphics.Color.parseColor( "#000000" ) );
                        }
                    }
                }
            } );
        }

        @Override
        public int getItemCount() {
            return weights == null ? 0 : weights.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            public Button item_weight;
            public TextView item_weight_dozan, item_weight_pond;

            public ViewHolder(View itemLayoutView) {
                super( itemLayoutView );
                item_weight = itemLayoutView.findViewById( R.id.item_weight );
                item_weight_dozan = itemLayoutView.findViewById( R.id.item_size_text );
                item_weight_pond = itemLayoutView.findViewById( R.id.item_weight_pond );
            }
        }
    }


    public class SizeTextAdapter extends RecyclerView.Adapter<SizeTextAdapter.ViewHolder> {
        private Context mCtx;
        List<Size> sizes;
        List<Size> xSizesTextList = new ArrayList<>(  );

        public SizeTextAdapter(Context mCtx, List<Size> sizes) {
            this.mCtx = mCtx;
            this.sizes = sizes;
        }

        @NonNull
        @Override
        public SizeTextAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View itemLayoutView = LayoutInflater.from( parent.getContext() ).inflate( R.layout.text, null );
            SizeTextAdapter.ViewHolder viewHolder = new SizeTextAdapter.ViewHolder( itemLayoutView );
            return viewHolder;
        }

        @SuppressLint("ResourceAsColor")
        @Override
        public void onBindViewHolder(@NonNull SizeTextAdapter.ViewHolder holder, int position) {
            final Size size1 = sizes.get( position );
            if (!size1.getSizeText().isEmpty()&&size1.getSizeText()!=null) {
                holder.item_size.setVisibility( GONE );
                holder.item_size_text.setVisibility( View.VISIBLE );
                holder.item_size_text.setText( size1.getSizeText() );
            }
            holder.item_size_text.setOnClickListener( new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mSizesTextList = new ArrayList<>(  );
                    size1.setSelected(!size1.isSelected());
                    if(size1.isSelected()){
                        xSizesTextList.add( size1 );
                    }
                    for(Size size:xSizesTextList){
                        if (size.isSelected()) {
                            sizeText = size;
                            mSizesTextList.add( size );
                            holder.item_size_text.setBackgroundColor( android.graphics.Color.parseColor( "#567845" ) );
                            holder.item_size_text.setTextColor( android.graphics.Color.parseColor( "#ffffff" ) );
                        } else {
                            holder.item_size_text.setBackgroundColor( android.graphics.Color.parseColor( "#ffffff" ) );
                            holder.item_size_text.setTextColor( android.graphics.Color.parseColor( "#000000" ) );
                        }
                    }
                }
            } );
        }

        @Override
        public int getItemCount() {
            return sizes == null ? 0 : sizes.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            public TextView item_size_text;
            public Button item_size;
            private View view;

            public ViewHolder(View itemLayoutView) {
                super( itemLayoutView );
                view = itemLayoutView;
                item_size_text = itemLayoutView.findViewById( R.id.item_size_text );
                item_size = itemLayoutView.findViewById( R.id.item_weight );
            }
        }
    }


    public class SizeNumberAdapter extends RecyclerView.Adapter<SizeNumberAdapter.ViewHolder> {
        private Context mCtx;
        List<Size> sizes;
        private List<Size> xSizeNumberList = new ArrayList<>(  );

        public SizeNumberAdapter(Context mCtx, List<Size> sizes) {
            this.mCtx = mCtx;
            this.sizes = sizes;
        }

        @NonNull
        @Override
        public SizeNumberAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View itemLayoutView = LayoutInflater.from( parent.getContext() ).inflate( R.layout.text, null );
            SizeNumberAdapter.ViewHolder viewHolder = new SizeNumberAdapter.ViewHolder( itemLayoutView );
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(@NonNull SizeNumberAdapter.ViewHolder holder, int position) {
            final Size size1 = sizes.get( position );
            if (size1.getSizeNumber() != 0) {
                holder.item_size.setVisibility( View.VISIBLE );
                holder.item_size_text.setVisibility( GONE );
                holder.item_size.setText( String.valueOf( size1.getSizeNumber() ) );
            }
            holder.item_size.setOnClickListener( new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mSizeNumberList = new ArrayList<>(  );
                    size1.setSelected(!size1.isSelected());
                    if(size1.isSelected()){
                        xSizeNumberList.add( size1 );
                    }
                    for(Size size:xSizeNumberList){
                        if (size.isSelected()) {
                            sizeNumber = size;
                            mSizeNumberList.add( size );
                            holder.item_size.setBackgroundColor( android.graphics.Color.parseColor( "#567845" ) );
                            holder.item_size.setTextColor( android.graphics.Color.parseColor( "#ffffff" ) );
                        } else {
                            holder.item_size.setBackgroundColor( android.graphics.Color.parseColor( "#ffffff" ) );
                            holder.item_size.setTextColor( android.graphics.Color.parseColor( "#000000" ) );
                        }
                    }
                }
            });
        }

        @Override
        public int getItemCount() {
            return sizes == null ? 0 : sizes.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            public Button item_size;
            private TextView item_size_text;

            public ViewHolder(View itemLayoutView) {
                super( itemLayoutView );
                item_size = itemLayoutView.findViewById( R.id.item_weight );
                item_size_text = itemLayoutView.findViewById( R.id.item_size_text );
            }
        }
    }


    public class ColorsAdapter extends RecyclerView.Adapter<ColorsAdapter.ViewHolder> {
        private Context mCtx;
        List<com.test.sample.hirecooks.Models.SubCategory.Color> colors;
        private  List<Color> xColorList = new ArrayList<>(  );

        public ColorsAdapter(Context mCtx, List<com.test.sample.hirecooks.Models.SubCategory.Color> colors) {
            this.mCtx = mCtx;
            this.colors = colors;
        }

        @NonNull
        @Override
        public ColorsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View itemLayoutView = LayoutInflater.from( parent.getContext() ).inflate( R.layout.text, null );
            ColorsAdapter.ViewHolder viewHolder = new ColorsAdapter.ViewHolder( itemLayoutView );
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(@NonNull ColorsAdapter.ViewHolder holder, int position) {
            final com.test.sample.hirecooks.Models.SubCategory.Color color1 = colors.get( position );
            holder.item_color.setVisibility( VISIBLE );
            holder.item_color.setText( color1.getColorName() );
            holder.item_color.setOnClickListener( new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mColoList = new ArrayList<>(  );
                    color1.setSelected(!color1.isSelected());
                    if(color1.isSelected()){
                        xColorList.add( color1 );
                    }
                    for(Color color1:xColorList){
                        if (color1.isSelected()) {
                            color1.setColorName( color1.getColorName() );
                            color1.setColor( color1.getColor() );
                            mColoList.add( color1 );
                            color = color1;
                            holder.item_color.setBackgroundColor( android.graphics.Color.parseColor( "#567845" ) );
                            holder.item_color.setTextColor( android.graphics.Color.parseColor( "#ffffff" ) );
                        } else {
                            holder.item_color.setBackgroundColor( android.graphics.Color.parseColor( "#ffffff" ) );
                            holder.item_color.setTextColor( android.graphics.Color.parseColor( "#000000" ) );
                        }
                    }
                }
            } );
        }

        @Override
        public int getItemCount() {
            return colors == null ? 0 : colors.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            public Button item_color;

            public ViewHolder(View itemLayoutView) {
                super( itemLayoutView );
                item_color = itemLayoutView.findViewById( R.id.item_weight );
            }
        }
    }
}

