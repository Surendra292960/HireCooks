package com.test.sample.hirecooks.Activity.Cooks;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.test.sample.hirecooks.Activity.Cooks.Video.PickVideoActivity;
import com.test.sample.hirecooks.Activity.Model.MyModel;
import com.test.sample.hirecooks.Adapter.Cooks.CooksImagesAdapter;
import com.test.sample.hirecooks.ApiServiceCall.ApiClient;
import com.test.sample.hirecooks.Libraries.Autoplayvideo.AAH_CustomRecyclerView;
import com.test.sample.hirecooks.Libraries.Autoplayvideo.AAH_CustomViewHolder;
import com.test.sample.hirecooks.Libraries.Autoplayvideo.AAH_VideosAdapter;
import com.test.sample.hirecooks.MapLocation;
import com.test.sample.hirecooks.Models.MapLocationResponse.Map;
import com.test.sample.hirecooks.Models.MapLocationResponse.Result;
import com.test.sample.hirecooks.Models.Users.User;
import com.test.sample.hirecooks.Models.Video.Example;
import com.test.sample.hirecooks.Models.Video.Video;
import com.test.sample.hirecooks.Models.cooks.Request.CooksImages;
import com.test.sample.hirecooks.Models.cooks.Request.CooksImagesResult;
import com.test.sample.hirecooks.R;
import com.test.sample.hirecooks.Utils.APIUrl;
import com.test.sample.hirecooks.Utils.BaseActivity;
import com.test.sample.hirecooks.Utils.Common;
import com.test.sample.hirecooks.Utils.ProgressBarUtil;
import com.test.sample.hirecooks.Utils.SharedPrefManager;
import com.test.sample.hirecooks.WebApis.CookImages;
import com.test.sample.hirecooks.WebApis.MapApi;
import com.test.sample.hirecooks.WebApis.VideoApi;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UpdateCookDetails extends BaseActivity {
    private RecyclerView cooks_images_recycler;
    private TextView name,address,phone_number,add_videos,profile_type,cook_name,edit_cookImages,edit_address,update_images;
    private CircleImageView cook_image;
    private ImageView imageView;
    private ProgressBarUtil progressBarUtil;
    private CookImages mService = Common.getCookImagesAPI();
    private User user;
    @BindView(R.id.rv_home)
    AAH_CustomRecyclerView image_recycler;

    private final List<MyModel> modelList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cooks_details);
        Objects.requireNonNull(getSupportActionBar()).setHomeButtonEnabled(true);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        Objects.requireNonNull(getSupportActionBar()).setTitle("Cooks Details");
        initViews();

        getLocation(user);
        getCookImages(user);
        getCookVideos(user);
    }

    @SuppressLint("WrongConstant")
    private void initViews() {
        ButterKnife.bind(this);
        progressBarUtil = new ProgressBarUtil( this );
        user = SharedPrefManager.getInstance( this ).getUser();
        cooks_images_recycler = findViewById(R.id.cooks_images_recycler);
        image_recycler = findViewById(R.id.rv_home);
        phone_number = findViewById(R.id.phone_number);
        address = findViewById(R.id.address);
        imageView = findViewById(R.id.imageView);
        cook_image = findViewById(R.id.cook_image);
        name = findViewById(R.id.name);
        cook_name = findViewById(R.id.cook_name);
        add_videos = findViewById(R.id.add_videos);
        profile_type = findViewById(R.id.profile_type);
        edit_cookImages = findViewById(R.id.edit_cookImages);
        edit_address = findViewById(R.id.edit_address);
        update_images = findViewById(R.id.update_images);

        edit_cookImages.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle(  );
                Intent intent = new Intent( UpdateCookDetails.this,CookImagesList.class );
                bundle.putSerializable( "Cooks", user );
                intent.putExtras( bundle );
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity( intent );
            }
        } );

        edit_address.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity( new Intent( UpdateCookDetails.this, MapLocation.class ) .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK));
            }
        } );

        add_videos.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity( new Intent( UpdateCookDetails.this, PickVideoActivity.class ) );
            }
        } );

    }

    private void getCookVideos(User users) {
        VideoApi mService = ApiClient.getClient().create( VideoApi.class );
        Call<List<Example>> call = mService.getVideos(users.getId());
        call.enqueue( new Callback<List<Example>>() {
            @Override
            public void onResponse(Call<List<Example>> call, Response<List<Example>> response) {
                if(response.code()==200){
                    for(Example example:response.body()){
                        if(!example.getError()){
                            setVideoData( example.getVideos() );
                        }
                        else{
                            Toast.makeText( UpdateCookDetails.this, example.getMessage(), Toast.LENGTH_SHORT ).show();
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<List<Example>> call, Throwable t) {

            }
        } );
    }

    @SuppressLint("WrongConstant")
    private void setVideoData(List<Video> videoList){
        EditVideosAdapter mAdapter = new EditVideosAdapter(this,videoList);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            linearLayoutManager.setOrientation( LinearLayout.HORIZONTAL);
        } else {
            linearLayoutManager.setOrientation(LinearLayout.HORIZONTAL);
        }
        image_recycler.setLayoutManager(linearLayoutManager);
        image_recycler.setItemAnimator(new DefaultItemAnimator());

        //todo before setAdapter
        image_recycler.setActivity(this);

        //optional - to play only first visible video
        image_recycler.setPlayOnlyFirstVideo(true); // false by default

        //optional - by default we check if url ends with ".mp4". If your urls do not end with mp4, you can set this param to false and implement your own check to see if video points to url
        image_recycler.setCheckForMp4(false); //true by default

        //optional - download videos to local storage (requires "android.permission.WRITE_EXTERNAL_STORAGE" in manifest or ask in runtime)
        image_recycler.setDownloadPath( Environment.getExternalStorageDirectory() + "/MyVideo"); // (Environment.getExternalStorageDirectory() + "/Video") by default

        image_recycler.setDownloadVideos(true); // false by default

        image_recycler.setVisiblePercent(50); // percentage of View that needs to be visible to start playing

        //extra - start downloading all videos in background before loading RecyclerView
        List<String> urls = new ArrayList<>();
        for (MyModel object : modelList) {
            if (object.getVideo_url() != null && object.getVideo_url().contains("http"))
                urls.add(object.getVideo_url());
        }
        image_recycler.preDownload(urls);

        image_recycler.setAdapter(mAdapter);
        //call this functions when u want to start autoplay on loading async lists (eg firebase)
        image_recycler.smoothScrollBy(0,1);
        image_recycler.smoothScrollBy(0,-1);
    }
    @SuppressLint("WrongConstant")
    private void setData(Map maps) {
        if(maps.getAddress()!=null){
            address.setText(maps.getAddress());
        }
        if(user.getImage()!=null){
            if(user.getImage().equalsIgnoreCase("https://")){
                Glide.with(this).load(user.getImage()).into(cook_image);
            }else{
                Glide.with(this).load(APIUrl.PROFILE_URL+user.getImage()).into(cook_image);
            }
        }
        phone_number.setText(user.getPhone());
        name.setText(user.getName());
        cook_name.setText(user.getName());
        profile_type.setText(user.getUserType());

        if(user.getUserType().equalsIgnoreCase( "Cook" )){
            if(user.getId().equals( user.getId() )) {
                edit_cookImages.setVisibility( View.VISIBLE );
                edit_address.setVisibility( View.VISIBLE );
                update_images.setVisibility( View.VISIBLE );
                add_videos.setVisibility( View.VISIBLE );
            }
        }else if(user.getUserType().equalsIgnoreCase( "SuperAdmin" )){
            edit_cookImages.setVisibility( View.VISIBLE );
            edit_address.setVisibility( View.VISIBLE );
            update_images.setVisibility( View.VISIBLE );
            add_videos.setVisibility( View.VISIBLE );
        }
    }

    private void getLocation(User userResponse) {
        MapApi service = ApiClient.getClient().create(MapApi.class);
        Call<Result> call = service.getMapDetails(userResponse.getId());
        call.enqueue(new Callback<Result>() {
            @SuppressLint("ShowToast")
            @Override
            public void onResponse(@NonNull Call<Result> call, @NonNull Response<Result> response) {
                int statusCode = response.code();
                if (statusCode == 200) {
                    if(response.body().getMaps()!=null){
                        setData(response.body().getMaps());
                    }else{
                        Toast.makeText(UpdateCookDetails.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @SuppressLint("ShowToast")
            @Override
            public void onFailure(@NonNull Call<Result> call, @NonNull Throwable t) {
                Toast.makeText(getApplicationContext(), R.string.error + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void getCookImages(User user) {
        progressBarUtil.showProgress();
        mService.getCookImagesByUserId(user.getId())
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
                            // ShowToast( result.getMessage() );
                            getCategoryCooks(result.getImages());
                        }else{
                            //ShowToast( result.getMessage() );
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

    @SuppressLint("WrongConstant")
    private void getCategoryCooks(List<CooksImages> cooksImages) {
        CooksImagesAdapter mAdapter = new CooksImagesAdapter( this,cooksImages, user );
        cooks_images_recycler.setAdapter ( mAdapter );

        LinearLayoutManager tlinearLayoutManager = new LinearLayoutManager(this);
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            tlinearLayoutManager.setOrientation(LinearLayout.HORIZONTAL);
        } else {
            tlinearLayoutManager.setOrientation(LinearLayout.HORIZONTAL);
        }
        cooks_images_recycler.setLayoutManager(tlinearLayoutManager);
        cooks_images_recycler.setItemAnimator(new DefaultItemAnimator());
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id==android.R.id.home){
            this.finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(user!=null){
            getCookImages(user);
            getLocation(user);
            getCookVideos(user);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        //add this code to pause videos (when app is minimised or paused)
        image_recycler.stopVideos();
    }



    public class EditVideosAdapter extends AAH_VideosAdapter {
        private final List<Video> list;
        Context context;
        private static final int TYPE_VIDEO = 0, TYPE_TEXT = 1;

        public class MyViewHolder extends AAH_CustomViewHolder {
            final TextView tv;
            final ImageView img_vol, img_playback;
            final LinearLayout cooks_promotion_lay;
            //to mute/un-mute video (optional)
            boolean isMuted;

            public MyViewHolder(View x) {
                super(x);
                tv = x.findViewById(R.id.tv);
                img_vol = x.findViewById(R.id.img_vol);
                img_playback = x.findViewById(R.id.img_playback);
                cooks_promotion_lay = x.findViewById(R.id.cooks_promotion_lay);
            }

            //override this method to get callback when video starts to play
            @Override
            public void videoStarted() {
                super.videoStarted();
                img_playback.setImageResource(R.drawable.ic_pause);
                if (isMuted) {
                    muteVideo();
                    img_vol.setImageResource(R.drawable.ic_mute);
                } else {
                    unmuteVideo();
                    img_vol.setImageResource(R.drawable.ic_unmute);
                }
            }

            @Override
            public void pauseVideo() {
                super.pauseVideo();
                img_playback.setImageResource(R.drawable.ic_play);
            }
        }

        public EditVideosAdapter(Context context, List<Video> list_urls) {
            this.list = list_urls;
            this.context = context;
        }

        @Override
        public AAH_CustomViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_card, parent, false);
            return new EditVideosAdapter.MyViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(final AAH_CustomViewHolder holder, int position) {
            //((MyViewHolder) holder).tv.setText(list.get(position).getName());
            //todo
      /*  holder.setImageUrl(list.get(position).getImage_url());
        holder.setVideoUrl(list.get(position).getVideo_url());
        //load image into imageview
        if (list.get(position).getImage_url() != null && !list.get(position).getImage_url().isEmpty()) {
            Glide.load(holder.getImageUrl()).config(Bitmap.Config.RGB_565).into(holder.getAAH_ImageView());
        }*/
            holder.setLooping(true); //optional - true by default
            //to play pause videos manually (optional)
            ((EditVideosAdapter.MyViewHolder) holder).img_playback.setOnClickListener( new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (holder.isPlaying()) {
                        holder.pauseVideo();
                        holder.setPaused(true);
                    } else {
                        holder.playVideo();
                        holder.setPaused(false);
                    }
                }
            });

            //to mute/un-mute video (optional)
            ((EditVideosAdapter.MyViewHolder) holder).img_vol.setOnClickListener( new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (((EditVideosAdapter.MyViewHolder) holder).isMuted) {
                        holder.unmuteVideo();
                        ((EditVideosAdapter.MyViewHolder) holder).img_vol.setImageResource(R.drawable.ic_unmute);
                    } else {
                        holder.muteVideo();
                        ((EditVideosAdapter.MyViewHolder) holder).img_vol.setImageResource(R.drawable.ic_mute);
                    }
                    ((EditVideosAdapter.MyViewHolder) holder).isMuted = !((EditVideosAdapter.MyViewHolder) holder).isMuted;
                }
            });
            //to mute/un-mute video (optional)
            ((EditVideosAdapter.MyViewHolder) holder).cooks_promotion_lay.setOnClickListener( new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    showalert( list.get( position ));
                }
            });

            if (list.get(position).getVideo_link() == null) {
                ((EditVideosAdapter.MyViewHolder) holder).img_vol.setVisibility(View.GONE);
                ((EditVideosAdapter.MyViewHolder) holder).img_playback.setVisibility(View.GONE);
            } else {
                ((EditVideosAdapter.MyViewHolder) holder).img_vol.setVisibility(View.VISIBLE);
                ((EditVideosAdapter.MyViewHolder) holder).img_playback.setVisibility(View.VISIBLE);
            }
        }

        private void showalert(Video video) {
            final android.app.AlertDialog.Builder dialogBuilder = new android.app.AlertDialog.Builder( UpdateCookDetails.this);
            LayoutInflater inflater = UpdateCookDetails.this.getLayoutInflater();
            View view = inflater.inflate(R.layout.edit_subcategory_alert,null);
            AppCompatTextView deleteBtn = view.findViewById(R.id.no_btn);
            deleteBtn.setText( "Delete" );
            AppCompatTextView editBtn = view.findViewById(R.id.edit_btn);
            dialogBuilder.setView(view);
            final android.app.AlertDialog dialog = dialogBuilder.create();
            dialog.show();
            deleteBtn.setOnClickListener( v -> {
                try {
                    dialog.dismiss();
                   deleteVideo(video.getId());
                }
                catch (Exception ex) {
                    ex.printStackTrace();
                }
            } );
            editBtn.setOnClickListener( v -> {
                try {
                    dialog.dismiss();
                    dialog.dismiss();
                    Intent intent = new Intent( context, PickVideoActivity.class );
                    Bundle bundle = new Bundle(  );
                    bundle.putSerializable( "Video" ,video );
                    intent.putExtras( bundle );
                    ((Activity)context).startActivity( intent );
                }
                catch (Exception ex) {
                    ex.printStackTrace();
                }
            } );
        }


        @Override
        public int getItemCount() {
            return list.size();
        }

        @Override
        public int getItemViewType(int position) {
            if (list.get(position).getName().startsWith("text")) {
                return TYPE_TEXT;
            } else return TYPE_VIDEO;
        }
    }

    private void deleteVideo(int id) {
        VideoApi mService = ApiClient.getClient().create( VideoApi.class );
        Call<List<Example>> call = mService.deleteVideo(id);
        call.enqueue( new Callback<List<Example>>() {
            @Override
            public void onResponse(Call<List<Example>> call, Response<List<Example>> response) {
                if(response.code()==200){
                    for(Example example:response.body()){
                        Toast.makeText( UpdateCookDetails.this, example.getMessage(), Toast.LENGTH_SHORT ).show();
                        if(user!=null){
                            getCookVideos( user );
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<List<Example>> call, Throwable t) {

            }
        } );
    }

}
