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
import com.BlackTech.allVideostatus2020.models.Categorys;
import com.BlackTech.allVideostatus2020.onClickListners.onCategoryListClick;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class HorizontalAdapter extends RecyclerView.Adapter<HorizontalAdapter.MyViewHolder> {
    private final Context currentContext;
    private final ArrayList<Categorys> data;
    public final onCategoryListClick onCategoryListClick;
    public static boolean isLoading;


    public HorizontalAdapter(Context context, ArrayList<Categorys> arrayList, onCategoryListClick oncategorylistclick) {
        this.currentContext = context;
        this.data = arrayList;
        isLoading = false;
        this.onCategoryListClick = oncategorylistclick;
    }

    public static void setLoaded(Boolean bool) {
        isLoading = bool.booleanValue();
    }

    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, int i) {
        myViewHolder.txt_name.setText(this.data.get(i).Category);
        myViewHolder.txt_name.setTypeface(MainActivity.main_medium);

        Log.e("temp", this.data.get(i).Category);
        Picasso picasso = Picasso.get();
        picasso.load(Config.BASE_URL + this.data.get(i).Img_Category).fit().into(myViewHolder.imageview);


    }

    public int getItemCount() {
        return this.data.size();
    }

    @NonNull
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        final View inflate = LayoutInflater.from(viewGroup.getContext()).inflate((int) R.layout.item_choose, viewGroup, false);
        final MyViewHolder myViewHolder = new MyViewHolder(inflate);
        inflate.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {

                HorizontalAdapter.this.onCategoryListClick.onItemClick(inflate, myViewHolder.getLayoutPosition());
            }
        });
        return myViewHolder;
    }

    class MyViewHolder extends RecyclerView.ViewHolder  {
        final ImageView imageview;
        final TextView txt_name;

        MyViewHolder(View view) {
            super(view);
            this.txt_name = (TextView) view.findViewById(R.id.txt_name);
            this.imageview = (ImageView) view.findViewById(R.id.imageCategory);
//            itemView.setOnClickListener(this);
        }


//        @Override
//        public void onClick(View view) {
//            HorizontalAdapter.this.onCategoryListClick.onItemClick(view, getLayoutPosition());
//
//        }
    }
}
