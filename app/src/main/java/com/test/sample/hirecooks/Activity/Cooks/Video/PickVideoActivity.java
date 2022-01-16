package com.test.sample.hirecooks.Activity.Cooks.Video;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.test.sample.hirecooks.ApiServiceCall.ApiClient;
import com.test.sample.hirecooks.Models.Users.User;
import com.test.sample.hirecooks.Models.Video.Example;
import com.test.sample.hirecooks.Models.Video.Video;
import com.test.sample.hirecooks.R;
import com.test.sample.hirecooks.Utils.SharedPrefManager;
import com.test.sample.hirecooks.WebApis.VideoApi;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PickVideoActivity extends AppCompatActivity {
    SimpleDateFormat format2 = new SimpleDateFormat("yyy-dd-MM");
    private VideoView videoView;
    private Button upload_video,select_video,update_video;
    private EditText name,tag;
    private static final String TAG = "PickVideoActivity";
    private static final int SELECT_VIDEOS = 1;
    private static final int SELECT_VIDEOS_KITKAT = 1;
    private List<String> selectedVideos;
    private User user;
    private VideoApi mService;
    private Video videos;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pick_video);
        initViews();
        Bundle bundle = getIntent().getExtras();
        if(bundle!=null){
           videos = (Video)bundle.getSerializable( "Video" );
            if(videos!=null){
                videoView.setVideoPath(videos.getVideo_link());
                videoView.requestFocus();
                videoView.start();
                name.setText( videos.getName() );
                tag.setText( videos.getTag() );
                upload_video.setVisibility( View.GONE );
                update_video.setVisibility( View.VISIBLE );
            }
        }
    }


    private void initViews() {
        user = SharedPrefManager.getInstance( this ).getUser();
        videoView =  findViewById(R.id.video_1);
        select_video = findViewById( R.id.select_video );
        upload_video = findViewById( R.id.upload_video );
        update_video = findViewById( R.id.update_video );
        select_video.setVisibility( View.VISIBLE );
        name = findViewById( R.id.name );
        tag = findViewById( R.id.tag );

        select_video.setOnClickListener( v -> {
            Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
            intent.setType("video/mp4");
            startActivityForResult(intent, SELECT_VIDEOS_KITKAT);
        });

        upload_video.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validation();
            }
        } );

        update_video.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validation();
            }
        } );
    }

    private void validation() {
        String name_ = name.getText().toString().trim();
        String tag_ = tag.getText().toString().trim();

        if(TextUtils.isEmpty( name_ )){
            name.setError( "Please Enater Name" );
            name.requestFocus();
            return;
        }if(TextUtils.isEmpty( tag_ )){
            tag.setError( "Please Enater Tag" );
            tag.requestFocus();
            return;
        }

        Example example = new Example();
        List<Example> exampleList = new ArrayList<>(  );
        Video video = new Video();
        List<Video> videoList = new ArrayList<>(  );

        if(videos!=null){
            video.setName( name_ );
            video.setTag( tag_ );
            if(selectedVideos!=null&&selectedVideos.size()!=0){
                video.setVideo_link(selectedVideos.get( 0 ));
            }else{
                video.setVideo_link(videos.getVideo_link());
            }
            video.setFirmId( videos.getFirmId() );
            video.setUserId(videos.getUserId());
            video.setUser_type(videos.getUser_type());
            video.setCreated_at(format2.format(new Date( )));
            videoList.add( video );
            example.setVideos(videoList);
            exampleList.add( example );
            updateVideo(videos.getId(),exampleList);
        }else {
            video.setName( name_ );
            video.setTag( tag_ );
            video.setVideo_link( selectedVideos.get( 0 ) );
            if(user.getFirmId()!=null){
                video.setFirmId( user.getFirmId() );
            }else{
                video.setFirmId( "Not_Available");
            }
            video.setUserId(user.getId());
            video.setUser_type(user.getUserType());
            video.setCreated_at(format2.format(new Date( )));
            videoList.add( video );
            example.setVideos(videoList);
            exampleList.add( example );
            addVideo(exampleList);
        }
    }

    private void updateVideo(int id, List<Example> video) {
        Gson gson = new Gson();
        String json = gson.toJson( video );
        System.out.println( "Suree : "+json );
        mService = ApiClient.getClient().create( VideoApi.class );
        Call<List<Example>> call = mService.updateVideo(id,video);
        call.enqueue( new Callback<List<Example>>() {
            @Override
            public void onResponse(Call<List<Example>> call, Response<List<Example>> response) {
                if (response.code() == 200) {
                    for(Example example:response.body()){
                        Toast.makeText( PickVideoActivity.this, example.getMessage(), Toast.LENGTH_SHORT ).show();
                        finish();
                    }
                }
            }

            @Override
            public void onFailure(Call<List<Example>> call, Throwable t) {
                System.out.println( "Suree :" + t.getMessage() );
            }
        } );
    }

    public void addVideo(List<Example> video) {
        mService = ApiClient.getClient().create( VideoApi.class );
        Call<List<Example>> call = mService.addVideo(video);
        call.enqueue( new Callback<List<Example>>() {
            @Override
            public void onResponse(Call<List<Example>> call, Response<List<Example>> response) {
                if (response.code() == 200) {
                    for(Example example:response.body()){
                        Toast.makeText( PickVideoActivity.this, example.getMessage(), Toast.LENGTH_SHORT ).show();
                        finish();
                    }
                }
            }

            @Override
            public void onFailure(Call<List<Example>> call, Throwable t) {
                System.out.println( "Suree :" + t.getMessage() );
            }
        } );
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult( requestCode, resultCode, data );
        if (resultCode == RESULT_OK) {
            select_video.setVisibility( View.GONE );
            upload_video.setVisibility( View.VISIBLE );
            selectedVideos = new ArrayList<>(  );
            selectedVideos = getSelectedVideos( requestCode, data );
            Log.d( "path", selectedVideos.toString() );
            videoView.setVideoPath( selectedVideos.get( 0 ) );
            videoView.requestFocus();
            videoView.start();
        }
    }

    private List<String> getSelectedVideos(int requestCode, Intent data) {
        List<String> result = new ArrayList<>();
        ClipData clipData = data.getClipData();
        if(clipData != null) {
            for(int i=0;i<clipData.getItemCount();i++) {
                ClipData.Item videoItem = clipData.getItemAt(i);
                Uri videoURI = videoItem.getUri();
                String filePath = getPath(this, videoURI);
                result.add(filePath);
            }
        }
        else {
            Uri videoURI = data.getData();
            String filePath = getPath(this, videoURI);
            result.add(filePath);
        }

        return result;
    }

    @SuppressLint("NewApi")
    public static String getPath(final Context context, final Uri uri) {
        // DocumentProvider
        if (DocumentsContract.isDocumentUri( context, uri )) {
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }
                // TODO handle non-primary volumes
            }
            // DownloadsProvider
            else if (isDownloadsDocument(uri)) {
                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId( Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));
                return getDataColumn(context, contentUri, null, null);
            }
            // MediaProvider
            else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }

                final String selection = "_id=?";
                final String[] selectionArgs = new String[] {
                        split[1]
                };

                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        }
        // MediaStore (and general)
        else if ("content".equalsIgnoreCase(uri.getScheme())) {
            // Return the remote address
            if (isGooglePhotosUri(uri)) return uri.getLastPathSegment();
            return getDataColumn(context, uri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }

        return null;
    }

    public static String getDataColumn(Context context, Uri uri, String selection, String[] selectionArgs) {

        final String column = "_data";
        final String[] projection = {
                column
        };
        try (Cursor cursor = context.getContentResolver().query( uri, projection, selection, selectionArgs,
                null )) {
            if (cursor != null && cursor.moveToFirst()) {
                final int index = cursor.getColumnIndexOrThrow( column );
                return cursor.getString( index );
            }
        }
        return null;
    }


    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is Google Photos.
     */
    public static boolean isGooglePhotosUri(Uri uri) {
        return "com.google.android.apps.photos.content".equals(uri.getAuthority());
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