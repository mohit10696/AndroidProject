package com.BlackTech.allVideostatus2020.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.squareup.picasso.Picasso;
import com.BlackTech.allVideostatus2020.R;
import com.BlackTech.allVideostatus2020.activity.MainActivity;
import com.BlackTech.allVideostatus2020.getSet.categoryGetSet;
import java.util.ArrayList;

public class CategoryItemAdapter extends BaseAdapter {
    private final Context context;
    private final ArrayList<categoryGetSet> data;

    public long getItemId(int i) {
        return (long) i;
    }

    public CategoryItemAdapter(ArrayList<categoryGetSet> arrayList, Context context2) {
        this.data = arrayList;
        this.context = context2;
    }

    public Object getItem(int i) {
        return this.data.get(i);
    }

   public View getView(int i, View view, ViewGroup viewGroup) {
        if (view == null) {
            view = LayoutInflater.from(viewGroup.getContext()).inflate((int) R.layout.item_category, viewGroup, false);
        }
        TextView textView = (TextView) view.findViewById(R.id.txt_name);
        textView.setText(this.data.get(i).getSubCategoryName());
        textView.setTypeface(MainActivity.main_medium);
        Picasso picasso = Picasso.get();
        picasso.load(this.context.getString(R.string.link) + "images/subcategory/" + this.data.get(i).getImageName()).fit().into((ImageView) view.findViewById(R.id.imageCategory));
        return view;
    }
    public int getCount() {
        ArrayList<categoryGetSet> arrayList = this.data;
        if (arrayList != null) {
            return arrayList.size();
        }
        return 0;
    }
}
