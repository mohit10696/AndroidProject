package com.BlackTech.allVideostatus2020.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.BlackTech.allVideostatus2020.Utility.Config;
import com.BlackTech.allVideostatus2020.models.Videocategory;
import com.squareup.picasso.Picasso;
import com.BlackTech.allVideostatus2020.R;
import com.BlackTech.allVideostatus2020.activity.MainActivity;
import com.BlackTech.allVideostatus2020.getSet.videoListGetSet;
import com.BlackTech.allVideostatus2020.onClickListners.OnLoadListeners;
import com.BlackTech.allVideostatus2020.onClickListners.onVideoListClick;

import java.util.List;

public class VideoItemAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    public final LinearLayoutManager linearLayoutManager;
    public OnLoadListeners onLoadMoreListener;
    public final onVideoListClick onVideoListClick;
    public static boolean isLoading;
    private final Context context;
    private final List data;

    public VideoItemAdapter(List list, Context context2, onVideoListClick onvideolistclick, RecyclerView recyclerView) {
        this.data = list;
        this.context = context2;
        this.onVideoListClick = onvideolistclick;
        isLoading = false;
        this.linearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            public void onScrolled(RecyclerView recyclerView, int i, int i2) {
                super.onScrolled(recyclerView, i, i2);
                if (VideoItemAdapter.this.linearLayoutManager != null) {
                    int childCount = VideoItemAdapter.this.linearLayoutManager.getChildCount();
                    int itemCount = VideoItemAdapter.this.linearLayoutManager.getItemCount();
                    int findFirstVisibleItemPosition = VideoItemAdapter.this.linearLayoutManager.findFirstVisibleItemPosition();
                    Log.e("Check", VideoItemAdapter.isLoading + "  Total Item Count " + itemCount + "lastVisibleItem " + findFirstVisibleItemPosition + "visible threshold " + childCount);
                    if (!VideoItemAdapter.isLoading && itemCount <= findFirstVisibleItemPosition + childCount) {
                        if (VideoItemAdapter.this.onLoadMoreListener != null) {
                            VideoItemAdapter.this.onLoadMoreListener.onLoadMore();
                        }
                        boolean unused = VideoItemAdapter.isLoading = true;
                    }
                }
            }
        });
    }

    public void setOnLoadMoreListener(OnLoadListeners onLoadListeners) {
        this.onLoadMoreListener = onLoadListeners;
    }

    public int getItemViewType(int i) {
        return this.data.get(i) instanceof videoListGetSet ? 0 : 1;
    }

    public int getItemCount() {
        return this.data.size();
    }

    public static void setLoaded(Boolean bool) {
        isLoading = bool.booleanValue();
    }

    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
//        if (getItemViewType(i) != 1 && (viewHolder instanceof MyViewHolder)) {
            MyViewHolder myViewHolder = (MyViewHolder) viewHolder;
            myViewHolder.tf_video_title.setText(((Videocategory) this.data.get(i)).Category);
            Log.e("check_video_name" , "**********************************" + ((Videocategory) this.data.get(i)).Category);
            myViewHolder.tf_video_title.setTypeface(MainActivity.main_medium);
//            myViewHolder.tfVideoSubcat.setText(((Videocategory) this.data.get(i)).getVideo_category() + " : " + ((videoListGetSet) this.data.get(i)).getVideo_subcategory());
            myViewHolder.tfVideoSubcat.setTypeface(MainActivity.main_medium);
            Picasso picasso = Picasso.get();
            picasso.load(Config.BASE_URL + ((Videocategory) this.data.get(i)).Thumbnail).into(myViewHolder.iv_video);
//        }
    }
    @NonNull
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        final View inflate = LayoutInflater.from(viewGroup.getContext()).inflate((int) R.layout.item_data, viewGroup, false);
        final MyViewHolder myViewHolder = new MyViewHolder(inflate);
        inflate.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
//                VideoItemAdapter.this.onVideoListClick.onItemClick(inflate, myViewHolder.getLayoutPosition());
               onVideoListClick.onVideoItemClick(inflate,  ((Videocategory) data.get(myViewHolder.getLayoutPosition())));
            }
        });
        return myViewHolder;
    }


}
