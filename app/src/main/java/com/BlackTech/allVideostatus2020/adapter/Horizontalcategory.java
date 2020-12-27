package com.BlackTech.allVideostatus2020.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.BlackTech.allVideostatus2020.R;
import com.BlackTech.allVideostatus2020.Utility.Config;
import com.BlackTech.allVideostatus2020.activity.MainActivity;
import com.BlackTech.allVideostatus2020.models.SelectCategory;
import com.BlackTech.allVideostatus2020.onClickListners.onVideoListClick;
import com.squareup.picasso.Picasso;

import java.util.List;

public class Horizontalcategory extends RecyclerView.Adapter<Horizontalcategory.Horizontalcatholder> {
    private final Context currentContext;
    private final List data;
    public final onVideoListClick onVideoListClick;


    public Horizontalcategory(List list, Context context2, onVideoListClick onvideolistclick) {
        this.currentContext = context2;
        this.data = list;
        this.onVideoListClick = onvideolistclick;
    }


    @NonNull
    @Override
    public Horizontalcatholder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        final View inflate = LayoutInflater.from(viewGroup.getContext()).inflate((int) R.layout.item_data, viewGroup, false);
        final Horizontalcatholder myViewHolder = new Horizontalcatholder(inflate);
        inflate.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                onVideoListClick.onSubcatItemClick(inflate, ((SelectCategory) data.get(myViewHolder.getLayoutPosition())));
            }
        });
        return myViewHolder;

    }

    @Override
    public void onBindViewHolder(@NonNull Horizontalcatholder holder, int position) {

        Horizontalcatholder myViewHolder = (Horizontalcatholder) holder;
        myViewHolder.tf_video_title.setText(((SelectCategory) this.data.get(position)).Category);
        Log.e("check_video_name_horizont", "**********************************" + ((SelectCategory) this.data.get(position)).Category);
        myViewHolder.tf_video_title.setTypeface(MainActivity.main_medium);
//            myViewHolder.tfVideoSubcat.setText(((Videocategory) this.data.get(i)).getVideo_category() + " : " + ((videoListGetSet) this.data.get(i)).getVideo_subcategory());
        myViewHolder.tfVideoSubcat.setTypeface(MainActivity.main_medium);
        Picasso picasso = Picasso.get();
        picasso.load(Config.BASE_URL + ((SelectCategory) this.data.get(position)).Thumbnail).into(myViewHolder.iv_video);


//        myViewHolder.iv_video.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
////                onVideoListClick.onSubcatItemClick(view, ((SelectCategory) data.get(myViewHolder.getLayoutPosition())));
//
//
//                Intent intent = new Intent(currentContext, VideoDetailActivity.class);
////                intent.putExtra("videoserilizeable", ((SelectCategory) data.get(myViewHolder.getLayoutPosition())));
//                currentContext.startActivity(intent);
//
//            }
//        });

    }

    @Override
    public int getItemCount() {
        return this.data.size();
    }

    public class Horizontalcatholder extends RecyclerView.ViewHolder implements View.OnClickListener {
        final ImageView iv_video;
        final TextView tfVideoSubcat;
        final TextView tf_num_views;
        TextView tf_video_title;

        Horizontalcatholder(View view) {
            super(view);
            this.tf_video_title = (TextView) view.findViewById(R.id.tf_video_title);
            this.tfVideoSubcat = (TextView) view.findViewById(R.id.tv_video_subcat);
            this.tf_num_views = (TextView) view.findViewById(R.id.tf_num_views);
            this.tf_video_title = (TextView) view.findViewById(R.id.tf_video_title);
            this.iv_video = (ImageView) view.findViewById(R.id.iv_video);
            itemView.setOnClickListener(this);

        }

        @Override
        public void onClick(View view) {
            onVideoListClick.onSubcatItemClick(view, ((SelectCategory) data.get(getLayoutPosition())));

        }
    }
}
