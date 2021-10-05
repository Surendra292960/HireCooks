package com.test.sample.hirecooks.Adapter.Videos;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.test.sample.hirecooks.Activity.Cooks.Video.FullScreenVideo;
import com.test.sample.hirecooks.Libraries.Autoplayvideo.AAH_CustomViewHolder;
import com.test.sample.hirecooks.Libraries.Autoplayvideo.AAH_VideosAdapter;
import com.test.sample.hirecooks.Models.Video.Video;
import com.test.sample.hirecooks.R;

import java.util.List;


public class VideosAdapter extends AAH_VideosAdapter {
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

    public VideosAdapter(Context context, List<Video> list_urls) {
        this.list = list_urls;
        this.context = context;
    }

    @Override
    public AAH_CustomViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_card, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final AAH_CustomViewHolder holder, @SuppressLint("RecyclerView") int position) {
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
        ((MyViewHolder) holder).img_playback.setOnClickListener(new View.OnClickListener() {
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
        ((MyViewHolder) holder).img_vol.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (((MyViewHolder) holder).isMuted) {
                    holder.unmuteVideo();
                    ((MyViewHolder) holder).img_vol.setImageResource(R.drawable.ic_unmute);
                } else {
                    holder.muteVideo();
                    ((MyViewHolder) holder).img_vol.setImageResource(R.drawable.ic_mute);
                }
                ((MyViewHolder) holder).isMuted = !((MyViewHolder) holder).isMuted;
            }
        });
        //to mute/un-mute video (optional)
        ((MyViewHolder) holder).cooks_promotion_lay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent( context, FullScreenVideo.class );
                Bundle bundle = new Bundle(  );
                bundle.putSerializable( "Video" ,list.get( position ).getVideo_link() );
                intent.putExtras( bundle );
                ((Activity)context).startActivity( intent );
            }
        });

        if (list.get(position).getVideo_link() == null) {
            ((MyViewHolder) holder).img_vol.setVisibility(View.GONE);
            ((MyViewHolder) holder).img_playback.setVisibility(View.GONE);
        } else {
            ((MyViewHolder) holder).img_vol.setVisibility(View.VISIBLE);
            ((MyViewHolder) holder).img_playback.setVisibility(View.VISIBLE);
        }
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