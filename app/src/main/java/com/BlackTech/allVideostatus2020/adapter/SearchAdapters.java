package com.BlackTech.allVideostatus2020.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.BlackTech.allVideostatus2020.R;
import com.BlackTech.allVideostatus2020.Utility.Config;
import com.BlackTech.allVideostatus2020.activity.MainActivity;
import com.BlackTech.allVideostatus2020.models.Searchdata;
import com.BlackTech.allVideostatus2020.onClickListners.OnLoadListeners;
import com.BlackTech.allVideostatus2020.onClickListners.onVideoListClick;
import com.squareup.picasso.Picasso;

import java.util.List;

public class SearchAdapters extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public final LinearLayoutManager linearLayoutManager;
    public OnLoadListeners onLoadMoreListener;
    public final onVideoListClick onVideoListClick;
    public static boolean isLoading;
    private final Context context;
    private final List data;


    public SearchAdapters(List list, Context context2, onVideoListClick onvideolistclick, RecyclerView recyclerView) {
        this.data = list;
        this.context = context2;
        this.onVideoListClick = onvideolistclick;
        isLoading = false;
        this.linearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            public void onScrolled(RecyclerView recyclerView, int i, int i2) {
                super.onScrolled(recyclerView, i, i2);
                if (SearchAdapters.this.linearLayoutManager != null) {
                    int childCount = SearchAdapters.this.linearLayoutManager.getChildCount();
                    int itemCount = SearchAdapters.this.linearLayoutManager.getItemCount();
                    int findFirstVisibleItemPosition = SearchAdapters.this.linearLayoutManager.findFirstVisibleItemPosition();
                    Log.e("Check", VideoItemAdapter.isLoading + "  Total Item Count " + itemCount + "lastVisibleItem " + findFirstVisibleItemPosition + "visible threshold " + childCount);
                    if (!VideoItemAdapter.isLoading && itemCount <= findFirstVisibleItemPosition + childCount) {
                        if (SearchAdapters.this.onLoadMoreListener != null) {
                            SearchAdapters.this.onLoadMoreListener.onLoadMore();
                        }
                        boolean unused = VideoItemAdapter.isLoading = true;
                    }
                }
            }
        });
    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        final View inflate = LayoutInflater.from(parent.getContext()).inflate((int) R.layout.item_data, parent, false);
        final MyViewHolder myViewHolder = new MyViewHolder(inflate);
        inflate.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                onVideoListClick.onSearchVideoItemClick(inflate,  ((Searchdata) data.get(myViewHolder.getLayoutPosition())));
            }
        });
        return myViewHolder;    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
//        if (getItemViewType(i) != 1 && (viewHolder instanceof MyViewHolder)) {
        MyViewHolder myViewHolder = (MyViewHolder) holder;
        myViewHolder.tf_video_title.setText(((Searchdata) this.data.get(position)).Category);
        Log.e("check_video_name" , "**********************************" + ((Searchdata) this.data.get(position)).Category);
        myViewHolder.tf_video_title.setTypeface(MainActivity.main_medium);
//            myViewHolder.tfVideoSubcat.setText(((Videocategory) this.data.get(i)).getVideo_category() + " : " + ((videoListGetSet) this.data.get(i)).getVideo_subcategory());
        myViewHolder.tfVideoSubcat.setTypeface(MainActivity.main_medium);
        Picasso picasso = Picasso.get();
        picasso.load(Config.BASE_URL + ((Searchdata) this.data.get(position)).Thumbnail).into(myViewHolder.iv_video);
//        }
    }

    @Override
    public int getItemCount() {
        return this.data.size();
    }

    public void setOnLoadMoreListener(OnLoadListeners onLoadListeners) {
        this.onLoadMoreListener = onLoadListeners;
    }

    public static void setLoaded(Boolean bool) {
        isLoading = bool.booleanValue();
    }


}
