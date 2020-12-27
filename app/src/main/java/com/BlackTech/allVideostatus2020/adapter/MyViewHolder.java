package com.BlackTech.allVideostatus2020.adapter;


import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.BlackTech.allVideostatus2020.R;

class MyViewHolder extends RecyclerView.ViewHolder {
    final ImageView iv_video;
    final TextView tfVideoSubcat;
    final TextView tf_num_views;
    TextView tf_video_title;

    MyViewHolder(View view) {
        super(view);
        this.tf_video_title = (TextView) view.findViewById(R.id.tf_video_title);
        this.tfVideoSubcat = (TextView) view.findViewById(R.id.tv_video_subcat);
        this.tf_num_views = (TextView) view.findViewById(R.id.tf_num_views);
        this.tf_video_title = (TextView) view.findViewById(R.id.tf_video_title);
        this.iv_video = (ImageView) view.findViewById(R.id.iv_video);
    }
}
