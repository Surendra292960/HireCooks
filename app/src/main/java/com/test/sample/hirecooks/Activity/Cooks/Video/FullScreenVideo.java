package com.test.sample.hirecooks.Activity.Cooks.Video;

import android.net.Uri;
import android.os.Bundle;
import android.widget.MediaController;
import android.widget.VideoView;

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
        Objects.requireNonNull(getSupportActionBar()).setHomeButtonEnabled(true);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        Objects.requireNonNull(getSupportActionBar()).setTitle("");
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
}
