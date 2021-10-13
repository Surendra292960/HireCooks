package com.test.sample.hirecooks.Adapter.Videos;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import com.test.sample.hirecooks.Libraries.Autoplayvideo.AAH_CustomViewHolder;
import com.test.sample.hirecooks.Libraries.Autoplayvideo.AAH_VideosAdapter;
import com.test.sample.hirecooks.Models.Video.Video;
import com.test.sample.hirecooks.R;
import java.util.List;

public class VideosAdapter extends AAH_VideosAdapter {
    private final List<Video> list;
    private Context mCtx;

    public class MyViewHolder extends AAH_CustomViewHolder {
        ImageView img_vol, img_playback;
        LinearLayout cooks_promotion_lay;
        //to mute/un-mute video (optional)
        boolean isMuted;

        public MyViewHolder(View x) {
            super(x);
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

    public VideosAdapter(Context mCtx,List<Video> list_urls) {
        this.list = list_urls;
        this.mCtx = mCtx;
    }

    @Override
    public AAH_CustomViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_card, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final AAH_CustomViewHolder holder, @SuppressLint("RecyclerView") int position) {
        holder.setVideoUrl(list.get(position).getVideo_link());
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

        ((MyViewHolder) holder).cooks_promotion_lay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_VIEW );
                intent.setDataAndType(Uri.parse(list.get(position).getVideo_link()), "video/*");
                mCtx.startActivity(intent);
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
}