package com.BlackTech.allVideostatus2020.adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.BlackTech.allVideostatus2020.R;
import com.BlackTech.allVideostatus2020.Utility.Config;
import com.BlackTech.allVideostatus2020.activity.MainActivity;
import com.BlackTech.allVideostatus2020.activity.VideoDetailActivity;
import com.BlackTech.allVideostatus2020.models.SelectCategory;
import com.BlackTech.allVideostatus2020.models.Videocategory;
import com.BlackTech.allVideostatus2020.onClickListners.OnLoadListeners;
import com.BlackTech.allVideostatus2020.onClickListners.onVideoListClick;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class VideoSubItemAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    public static boolean isLoading;
    private final Context context;
    private final ArrayList<Videocategory> data;
    public final GridLayoutManager gridLayoutManager;
    public OnLoadListeners onLoadMoreListener;
    public final onVideoListClick onVideoListClick;

    public VideoSubItemAdapter(ArrayList<Videocategory> list, Context context2, onVideoListClick onvideolistclick, RecyclerView recyclerView) {
        this.data = list;
        this.context = context2;
        this.onVideoListClick = onvideolistclick;
        isLoading = false;
        this.gridLayoutManager = (GridLayoutManager) recyclerView.getLayoutManager();
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            public void onScrolled(RecyclerView recyclerView, int i, int i2) {
                super.onScrolled(recyclerView, i, i2);
                if (VideoSubItemAdapter.this.gridLayoutManager != null) {
                    int childCount = VideoSubItemAdapter.this.gridLayoutManager.getChildCount();
                    int itemCount = VideoSubItemAdapter.this.gridLayoutManager.getItemCount();
                    int findFirstVisibleItemPosition = VideoSubItemAdapter.this.gridLayoutManager.findFirstVisibleItemPosition();
                    Log.e("Check", VideoSubItemAdapter.isLoading + "  Total Item Count " + itemCount + "lastVisibleItem " + findFirstVisibleItemPosition + "visible threshold " + childCount);
                    if (!VideoSubItemAdapter.isLoading && itemCount <= findFirstVisibleItemPosition + childCount) {
                        if (VideoSubItemAdapter.this.onLoadMoreListener != null) {
                            VideoSubItemAdapter.this.onLoadMoreListener.onLoadMore();
                        }
                        boolean unused = VideoSubItemAdapter.isLoading = true;
                    }
                }
            }
        });
    }

    public void setOnLoadMoreListener(OnLoadListeners onLoadListeners) {
        this.onLoadMoreListener = onLoadListeners;
    }

    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
        if (getItemViewType(i) != 1 && (viewHolder instanceof MyViewSubListHolder)) {
            MyViewSubListHolder myViewSubListHolder = (MyViewSubListHolder) viewHolder;
            myViewSubListHolder.tf_video_title.setText(data.get(i).Img_title);
            myViewSubListHolder.tf_video_title.setTypeface(MainActivity.main_medium);
            Picasso picasso = Picasso.get();
            picasso.load(Config.BASE_URL + data.get(i).Thumbnail).into(myViewSubListHolder.iv_video);
            Log.e("checkImageUrl", "" + Config.BASE_URL + data.get(i).Thumbnail);
        }
    }


    @NonNull
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        final View inflate = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_subdata, viewGroup, false);
        final MyViewSubListHolder myViewSubListHolder = new MyViewSubListHolder(inflate);
        inflate.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Log.e("MOhit", data.get(0).video_name);
                Log.e("MOhit", data.get(1).video_name);
                Log.e("MOhit", data.get(2).video_name);
                //VideoSubItemAdapter.this.onVideoListClick.onItemClick(inflate, );
                Videocategory data2 = new Videocategory();
                int index = myViewSubListHolder.getLayoutPosition();
                data2.video_name = data.get(index).video_name;
                data2.Category  = data.get(index).Category;
                data2.Id = data.get(index).Id;
                data2.Thumbnail = data.get(index).Thumbnail;
                data2.Img_title = data.get(index).Img_title;
                data2.View = data.get(index).View;
               // Toast.makeText(context, String.valueOf(), Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(context, VideoDetailActivity.class);
                intent.putExtra("videoserilizeable", data2);
                intent.putExtra("flag", "0");
                context.startActivity(intent);
            }
        });
        return myViewSubListHolder;
    }

    public static void setLoaded(Boolean bool) {
        isLoading = bool.booleanValue();
    }

//
    public int getItemViewType(int i) {
        return this.data.get(i) instanceof Videocategory ? 0 : 1;
    }

    public int getItemCount() {
        return this.data.size();
    }

    class MyViewSubListHolder extends RecyclerView.ViewHolder {
        final ImageView iv_video;
        TextView tf_video_title;

        MyViewSubListHolder(View view) {
            super(view);
            this.tf_video_title = view.findViewById(R.id.tf_video_title);
            this.iv_video = view.findViewById(R.id.iv_video);
        }
    }
}
