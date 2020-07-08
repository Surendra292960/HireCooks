package com.test.sample.hirecooks.Activity.Users;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.test.sample.hirecooks.Adapter.Users.GalleryAdapter;
import com.test.sample.hirecooks.ApiServiceCall.ApiClient;
import com.test.sample.hirecooks.Models.HotelImage;
import com.test.sample.hirecooks.R;
import com.test.sample.hirecooks.Utils.Action;
import com.test.sample.hirecooks.Utils.UploadCallBack;
import com.test.sample.hirecooks.WebApis.UserApi;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UploadImagesActivity extends AppCompatActivity {
    static int REQUEST_GALLERY = 200, REQUEST_CAMERA = 100;
    FloatingActionButton addImage;
    LinearLayout mLinearLayout;
    Button mUploadImages;
    String TAG = "UploadImagesActivity";
    String[] selectedImageList;
    int childcount = 0, count = 0;
    @Override
    protected void onCreate ( Bundle savedInstanceState ) {
        super.onCreate ( savedInstanceState );
        try {
            setContentView ( R.layout.activity_upload_images );
            setTitle ( "Upload Images" );
            getSupportActionBar ( ).setDisplayHomeAsUpEnabled ( true );
            getSupportActionBar ( ).setHomeButtonEnabled ( true );
            addImage = findViewById ( R.id.add_hotel_image );
            mLinearLayout = findViewById ( R.id.add_hotel_iamge_view_parent );
            mUploadImages = findViewById ( R.id.upload_images_btn );
            addImage.setOnClickListener ( new View.OnClickListener ( ) {
                @Override
                public void onClick ( View v ) {
                    selectImage ( );
                }
            } );
            mUploadImages.setOnClickListener ( new View.OnClickListener ( ) {
                @Override
                public void onClick ( View v ) {
                    try {
                        if ( selectedImageList != null && selectedImageList.length != 0 ) {
                            childcount = selectedImageList.length;
                            for ( int i = 0 ; i < selectedImageList.length ; i++ ) {
                                LinearLayout linearLayout = ( LinearLayout ) mLinearLayout.getChildAt ( i );
                                Switch hide = ( Switch ) linearLayout.getChildAt ( 0 );
                                LinearLayout linearLayout2 = ( LinearLayout ) linearLayout.getChildAt ( 2 );
                                EditText editText = ( EditText ) linearLayout2.getChildAt ( 0 );
                                EditText order = ( EditText ) linearLayout2.getChildAt ( 1 );
                                String s = editText.getText ( ).toString ( );
                                String orders = order.getText ( ).toString ( );
                                boolean hides = hide.isChecked ( );
                                File file = new File ( selectedImageList[ i ] );
                                if ( file.length ( ) <= 1 * 1024 * 1024 ) {
                                    FileOutputStream out = null;
                                    String[] filearray = selectedImageList[ i ].split ( "/" );
                                    final String filename = getFilename ( filearray[ filearray.length - 1 ] );
                                    out = new FileOutputStream ( filename );
                                    Bitmap myBitmap = BitmapFactory.decodeFile ( selectedImageList[ i ] );
                                    myBitmap.compress ( Bitmap.CompressFormat.JPEG , 100 , out );
                                    uploadImage ( filename , s , orders , hides );

                                } else {
                                    compressImage ( selectedImageList[ i ] , s , orders , hides );
                                }
                            }
                        }
                    } catch ( Exception ex ) {
                        ex.printStackTrace ( );
                    }

                }
            } );
        } catch ( Exception e ) {
            e.printStackTrace ( );
        }

    }
    public void selectImage ( ) {
        try {
            final String[] imageSelectionArray = { "Gallery" , "Cancel" };
            AlertDialog.Builder builder = new AlertDialog.Builder ( UploadImagesActivity.this );
            builder.setTitle ( "Select Photo" );
            builder.setCancelable ( false );
            builder.setItems ( imageSelectionArray , new DialogInterface.OnClickListener ( ) {
                @Override
                public void onClick ( DialogInterface dialog , int which ) {
                    if ( imageSelectionArray[ which ].equals ( "Gallery" ) ) {
                        boolean result = ApiClient.checkPermissionOfCamera ( UploadImagesActivity.this , Manifest.permission.READ_EXTERNAL_STORAGE
                                , "This App Needs Storage Permission" );
                        if ( result ) {
                            gotoGallery ( );
                        }
                    } else if ( imageSelectionArray[ which ].equals ( "Take Photo" ) ) {
                        boolean result = ApiClient.checkPermissionOfCamera ( UploadImagesActivity.this , Manifest.permission.CAMERA ,
                                "This Application Needs Camera Permission" );
                        if ( result ) {
                            gotoCamera ( );
                        }
                    } else {
                        dialog.dismiss ( );
                    }
                }
            } );
            AlertDialog dialog = builder.create ( );
            dialog.show ( );
        } catch ( Exception e ) {
            e.printStackTrace ( );
        }

    }
    private void gotoCamera ( ) {
        Intent cameraIntent = new Intent ( MediaStore.ACTION_IMAGE_CAPTURE );
        startActivityForResult ( cameraIntent , REQUEST_CAMERA );
    }
    private void gotoGallery ( ) {
        Intent i = new Intent ( Action.ACTION_MULTIPLE_PICK );
        startActivityForResult ( i , 200 );
    }
    @Override
    protected void onActivityResult ( int requestCode , int resultCode , Intent data ) {
        super.onActivityResult ( requestCode , resultCode , data );
        if ( resultCode == Activity.RESULT_OK ) {
            if ( requestCode == REQUEST_CAMERA ) {
                onImageCaptureResult ( data );
            } else if ( requestCode == REQUEST_GALLERY ) {
                onSelectImageFromGalleryResult ( data );
            }
        }
    }
    private void onImageCaptureResult ( Intent data ) {
        try {
            if ( data != null ) {
                Bitmap bitmap = ( Bitmap ) data.getExtras ( ).get ( "data" );
                if ( bitmap != null ) {
                    addView ( null , ApiClient.getResizedBitmap ( bitmap , 700 ) );
                }
            }

        } catch ( Exception e ) {
            e.printStackTrace ( );
        }

    }
    private void onSelectImageFromGalleryResult ( Intent data ) {
        try {
            String[] all_path = data.getStringArrayExtra ( "all_path" );
            selectedImageList = all_path;
            for ( String s : all_path ) {
                File imgFile = new File ( s );
                if ( imgFile.exists ( ) ) {
                    Bitmap myBitmap = BitmapFactory.decodeFile ( imgFile.getAbsolutePath ( ) );
                    addView ( null , ApiClient.getResizedBitmap ( myBitmap , 700 ) );
                }
            }
        } catch ( Exception e ) {
            e.printStackTrace ( );
        }

    }
    public void addView ( String uri , Bitmap bitmap ) {
        LayoutInflater vi = ( LayoutInflater ) getApplicationContext ( ).getSystemService ( Context.LAYOUT_INFLATER_SERVICE );
        try {
            View v = vi.inflate ( R.layout.gallery_layout , null );
            ImageView hotelImage = v.findViewById ( R.id.hotel_images );
            EditText textView = v.findViewById ( R.id.hotel_image_caption );
            EditText order = v.findViewById ( R.id.hotel_image_order );
            Switch hide = v.findViewById ( R.id.hide_image );
            if ( uri != null ) {
            } else if ( bitmap != null ) {
                hotelImage.setImageBitmap ( bitmap );
            }
            mLinearLayout.addView ( v );
        } catch ( Exception e ) {
            e.printStackTrace ( );
        }

    }
    public void uploadImages ( final HotelImage hotelImage ) {
        UserApi api = ApiClient.getClient ( ).create ( UserApi.class );
        final Call< HotelImage > HotelImagereaponse = api.uploadImages (hotelImage);
        HotelImagereaponse.enqueue ( new Callback< HotelImage >( ) {
            @Override
            public void onResponse ( Call < HotelImage > call , Response< HotelImage > response ) {
                if ( response.code ( ) == 200 || response.code ( ) == 201 ) {
                    try {
                        if ( response.body ( ) != null ) {
                            ++ count;
                            if ( childcount == count ) {
                          /*      Toast.makeText ( UploadImagesActivity.this , "Image Uploaded Successfully" , Toast.LENGTH_SHORT ).show ( );
                                Intent back = new Intent ( UploadImagesActivity.this , HotelImagesList.class );
                                startActivity ( back );
                                UploadImagesActivity.this.finish ( );*/
                            }
                        } else {
                            Toast.makeText ( UploadImagesActivity.this , "Something went wrong." , Toast.LENGTH_SHORT ).show ( );
                            return;
                        }
                    } catch ( Exception e ) {
                        e.printStackTrace ( );
                    }

                } else {
                    Toast.makeText ( UploadImagesActivity.this , "Something went wrong. Due to " + response.code ( ) , Toast.LENGTH_SHORT ).show ( );
                    return;
                }
            }
            @Override
            public void onFailure (Call <HotelImage> call , Throwable t ) {
                Toast.makeText ( UploadImagesActivity.this , "Please check your Internet Connection try after sometime" , Toast.LENGTH_SHORT ).show ( );
                return;
            }
        } );
    }

    public String compressImage ( String filePath , String caption , String order , boolean hide ) {
        Bitmap scaledBitmap = null;
        BitmapFactory.Options options = new BitmapFactory.Options ( );
        options.inJustDecodeBounds = true;
        Bitmap bmp = BitmapFactory.decodeFile ( filePath , options );
        int actualHeight = options.outHeight;
        int actualWidth = options.outWidth;
        float maxHeight = actualHeight / 2;
        float maxWidth = actualWidth / 2;
        float imgRatio = actualWidth / actualHeight;
        float maxRatio = maxWidth / maxHeight;
        if ( actualHeight > maxHeight || actualWidth > maxWidth ) {
            if ( imgRatio < maxRatio ) {
                imgRatio = maxHeight / actualHeight;
                actualWidth = ( int ) ( imgRatio * actualWidth );
                actualHeight = ( int ) maxHeight;
            } else if ( imgRatio > maxRatio ) {
                imgRatio = maxWidth / actualWidth;
                actualHeight = ( int ) ( imgRatio * actualHeight );
                actualWidth = ( int ) maxWidth;
            } else {
                actualHeight = ( int ) maxHeight;
                actualWidth = ( int ) maxWidth;

            }
        }
        options.inSampleSize = calculateInSampleSize ( options , actualWidth , actualHeight );
        options.inJustDecodeBounds = false;
        options.inPurgeable = true;
        options.inInputShareable = true;
        options.inTempStorage = new byte[ 16 * 1024 ];
        try {
            bmp = BitmapFactory.decodeFile ( filePath , options );
        } catch ( OutOfMemoryError exception ) {
            exception.printStackTrace ( );

        }
        try {
            scaledBitmap = Bitmap.createBitmap ( actualWidth , actualHeight , Bitmap.Config.ARGB_8888 );
        } catch ( OutOfMemoryError exception ) {
            exception.printStackTrace ( );
        }
        float ratioX = actualWidth / ( float ) options.outWidth;
        float ratioY = actualHeight / ( float ) options.outHeight;
        float middleX = actualWidth / 2.0f;
        float middleY = actualHeight / 2.0f;
        Matrix scaleMatrix = new Matrix ( );
        scaleMatrix.setScale ( ratioX , ratioY , middleX , middleY );
        Canvas canvas = new Canvas ( scaledBitmap );
        canvas.setMatrix ( scaleMatrix );
        canvas.drawBitmap ( bmp , middleX - bmp.getWidth ( ) / 2 , middleY - bmp.getHeight ( ) / 2 , new Paint( Paint.FILTER_BITMAP_FLAG ) );
        ExifInterface exif;
        try {
            exif = new ExifInterface ( filePath );
            int orientation = exif.getAttributeInt (
                    ExifInterface.TAG_ORIENTATION , 0 );
            Log.d ( "EXIF" , "Exif: " + orientation );
            Matrix matrix = new Matrix ( );
            if ( orientation == 6 ) {
                matrix.postRotate ( 90 );
                Log.d ( "EXIF" , "Exif: " + orientation );
            } else if ( orientation == 3 ) {
                matrix.postRotate ( 180 );
                Log.d ( "EXIF" , "Exif: " + orientation );
            } else if ( orientation == 8 ) {
                matrix.postRotate ( 270 );
                Log.d ( "EXIF" , "Exif: " + orientation );
            }
            scaledBitmap = Bitmap.createBitmap ( scaledBitmap , 0 , 0 ,
                    scaledBitmap.getWidth ( ) , scaledBitmap.getHeight ( ) , matrix ,
                    true );
        } catch ( IOException e ) {
            e.printStackTrace ( );
        }
        FileOutputStream out = null;
        String[] filearray = filePath.split ( "/" );
        final String filename = getFilename ( filearray[ filearray.length - 1 ] );
        try {
            out = new FileOutputStream ( filename );
            scaledBitmap.compress ( Bitmap.CompressFormat.JPEG , 100 , out );
            uploadImage ( filename , caption , order , hide );

        } catch ( FileNotFoundException e ) {
            e.printStackTrace ( );
        }
        return filename;

    }
    public String getFilename ( String filePath ) {
        File file = new File ( Environment.getExternalStorageDirectory ( ).getPath ( ) , "MyFolder/Images" );
        if ( ! file.exists ( ) ) {
            file.mkdirs ( );
        }
        String uriSting;
        if ( filePath.contains ( ".jpg" ) ) {
            uriSting = ( file.getAbsolutePath ( ) + "/" + filePath );
        } else {
            uriSting = ( file.getAbsolutePath ( ) + "/" + filePath + ".jpg" );
        }
        return uriSting;

    }
    public int calculateInSampleSize ( BitmapFactory.Options options , int reqWidth , int reqHeight ) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;
        if ( height > reqHeight || width > reqWidth ) {
            final int heightRatio = Math.round ( ( float ) height / ( float ) reqHeight );
            final int widthRatio = Math.round ( ( float ) width / ( float ) reqWidth );
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        }
        final float totalPixels = width * height;
        final float totalReqPixelsCap = reqWidth * reqHeight * 2;
        while (totalPixels / ( inSampleSize * inSampleSize ) > totalReqPixelsCap) {
            inSampleSize++;
        }
        return inSampleSize;
    }
    private void uploadImage ( final String filePath , final String cap , final String order , final boolean hide ) {
        final File file = new File ( filePath );
        int size = 1 * 1024 * 1024;
        if ( file != null ) {
            if ( file.length ( ) > size ) {
                compressImage ( filePath , cap , order , hide );
            } else {
                final ProgressDialog dialog = new ProgressDialog ( UploadImagesActivity.this );
                dialog.setCancelable ( false );
                dialog.show ( );
                Log.d ( TAG , "Filename " + file.getName ( ) );
                RequestBody mFile = RequestBody.create ( MediaType.parse ( "image" ) , file );
                MultipartBody.Part fileToUpload = MultipartBody.Part.createFormData ( "file" , file.getName ( ) , mFile );
                RequestBody filename = RequestBody.create ( MediaType.parse ( "text/plain" ) , file.getName ( ) );
                UserApi uploadImage = ApiClient.getClient ( ).create ( UserApi.class );
                Call < String > fileUpload = uploadImage.uploadFile ( fileToUpload , filename );
                fileUpload.enqueue ( new Callback < String > ( ) {
                    @Override
                    public void onResponse ( Call < String > call , Response < String > response ) {
                        if ( dialog != null && dialog.isShowing ( ) ) {
                            dialog.dismiss ( );
                        }
                        String[] spliting = response.body ( ).split ( "HotelImage" );
                        for ( String s : spliting ) {
                        }
                        HotelImage hotelImage = new HotelImage ( );
                        if ( cap == null || cap.isEmpty ( ) ) {
                        ///    hotelImage.setCaption ( 0 );
                        } else {
//                            hotelImage.setCaption (Integer.valueOf(cap));
                        }
                        if ( order == null || order.isEmpty ( ) ) {
                            hotelImage.setImageOrder ( 0 );
                        } else {
                           // hotelImage.setImageOrder ( Integer.parseInt ( order ) );
                        }
                        if ( hide ) {
                            String example = "true";
                            hotelImage.setImages ( example );

                        } else {
                            hotelImage.setImages ( null );

                        }
                        uploadImages ( hotelImage );
                        if ( filePath.contains ( "MyFolder/Images" ) ) {
                            file.delete ( );
                        }
                    }
                    @Override
                    public void onFailure ( Call < String > call , Throwable t ) {
                        Log.d ( TAG , "Error " + t.getMessage ( ) );
                    }
                } );
            }
        }
    }
}
