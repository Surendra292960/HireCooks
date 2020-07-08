package com.test.sample.hirecooks.Activity.Users;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.ipaulpro.afilechooser.utils.FileUtils;
import com.test.sample.hirecooks.ApiServiceCall.ApiClient;
import com.test.sample.hirecooks.Models.Images.Image;
import com.test.sample.hirecooks.R;
import com.test.sample.hirecooks.Utils.UploadCallBack;
import com.test.sample.hirecooks.WebApis.UserApi;

import java.io.File;
import java.util.ArrayList;
import java.util.Objects;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddUserImagesActivity extends AppCompatActivity implements UploadCallBack {
    private static final int PICK_FILE_REQUEST = 1;
    private Uri selectedFileUri;
    private View appRoot;
    private UserApi mService;
    private String TAG = "AddUserImagesActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_user_images);
        appRoot = findViewById(R.id.appRoot);
        Objects.requireNonNull(getSupportActionBar()).setHomeButtonEnabled(true);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
    }

    public void ImageClick(View view) {
        chooseImage();
    }


    private void chooseImage() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_FILE_REQUEST);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent resultData) {
        super.onActivityResult(requestCode, resultCode, resultData);
        if (resultCode == RESULT_OK) {
            if (requestCode == PICK_FILE_REQUEST) {
                if (resultData != null) {
                    if (resultData.getClipData() != null) {
                        int count = resultData.getClipData().getItemCount();
                        int currentItem = 0;
                        while (currentItem < count) {
                            Uri imageUri = resultData.getClipData().getItemAt(currentItem).getUri();
                            currentItem = currentItem + 1;
                            ArrayList<Uri> arrayList = new ArrayList<>();
                            arrayList.add(imageUri);
                            uploadImages(arrayList);
                            Log.d("Uri Selected", imageUri.toString());
                        }
                    } else if (resultData.getData() != null) {
                        final Uri uri = resultData.getData();
                        Log.i(TAG, "Uri = " + uri.toString());
                        ArrayList<Uri> arrayList = new ArrayList<>();
                        arrayList.add(uri);
                        uploadImages(arrayList);
                    }
                }
            }
        }
    }

    @NonNull
    private RequestBody createPartFromString(String descriptionString) {
        return RequestBody.create(MediaType.parse(FileUtils.MIME_TYPE_TEXT), descriptionString);
    }

    @NonNull
    private MultipartBody.Part prepareFilePart(String partName, Uri fileUri) {
        // use the FileUtils to get the actual file by uri
        File file = FileUtils.getFile(this, fileUri);
        // create RequestBody instance from file
        RequestBody requestFile = RequestBody.create (MediaType.parse(FileUtils.MIME_TYPE_IMAGE), file);
        // MultipartBody.Part is used to send also the actual file name
        return MultipartBody.Part.createFormData(partName, file.getName(), requestFile);
    }

    private void uploadImages(ArrayList<Uri> arrayList){
        ArrayList<String> filePaths = new ArrayList<>();
        filePaths.add(arrayList.toString());

        MultipartBody.Builder builder = new MultipartBody.Builder();
        builder.setType(MultipartBody.FORM);
        // Map is used to multipart the file using okhttp3.RequestBody
        // Multiple Images
        for (int i = 0; i < filePaths.size(); i++) {
            File file = new File(filePaths.get(i));
            builder.addFormDataPart("files[]", file.getName(), RequestBody.create(MediaType.parse("multipart/form-data"), file));
        }

        mService = ApiClient.getClient().create(UserApi.class);
        MultipartBody requestBody = builder.build();
        Gson gson = new Gson();
        String json = gson.toJson(requestBody);
        Call<Image> call = mService.uploadMultiple("Image");
        call.enqueue(new Callback<Image>() {
            @Override
            public void onResponse(Call<Image> call, Response<Image> response) {
                Toast.makeText(AddUserImagesActivity.this, "Success " + response.message(), Toast.LENGTH_LONG).show();
            }

            @Override
            public void onFailure(Call<Image> call, Throwable t) {

                Log.d(TAG, "Error " + t.getMessage());
            }
        });
    }
   /* private void uploadFile() {
        if(selectedFileUri!=null){
        mService = ApiClient.getClient().create(UserApi.class);
        MultipartBody.Builder builder = new MultipartBody.Builder();
        builder.setType(MultipartBody.FORM);
        builder.addFormDataPart("event_name", "xyz");
        builder.addFormDataPart("desc", "Lorem ipsum");
        // Single Image
        builder.addFormDataPart("files",file1.getName(), RequestBody.create(MediaType.parse("image/*"), file1));
        // Multiple Images
        for (int i = 0; i <filePaths.size() ; i++) {
            File file = new File(filePaths.get(i));
            RequestBody requestImage = RequestBody.create(MediaType.parse("multipart/form-data"), file);
            builder.addFormDataPart("event_images[]", file.getName(), RequestBody.create(MediaType.parse("multipart/form-data"), file));
        }


        MultipartBody requestBody = builder.build();
        Call<ResponseBody> call = service.event_store(requestBody);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Toast.makeText(getBaseContext(),"All fine",Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(getBaseContext(),t.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });    }
}*/

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return null;
    }

    @Override
    public void onProgressUpdate(int percentage) {

    }
    @Override
    public void onBackPressed() {
        this.finish();
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
