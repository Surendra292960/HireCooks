package com.test.sample.hirecooks.Activity.Cooks.Video;

import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.MediaController;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.test.sample.hirecooks.R;

import java.util.Objects;

public class FullScreenVideo extends AppCompatActivity {
    private VideoView video_view;
    private String video;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_full_screen_video );
        initViews();
        Bundle bundle = getIntent().getExtras();
        if(bundle!=null){
            video = (String) bundle.getSerializable("Video");
            if(video!=null){
                video_view.setVideoURI( Uri.parse(video));
                video_view.setMediaController(new MediaController(FullScreenVideo.this));
                video_view.requestFocus();
                video_view.start();
            }
        }
    }

    private void initViews() {
        video_view = findViewById( R.id.fullscreen_video );

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId()==android.R.id.home){
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
