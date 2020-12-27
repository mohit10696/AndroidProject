package com.BlackTech.allVideostatus2020.onClickListners;

import android.view.View;

import com.BlackTech.allVideostatus2020.models.Searchdata;
import com.BlackTech.allVideostatus2020.models.SelectCategory;
import com.BlackTech.allVideostatus2020.models.Videocategory;

public interface onVideoListClick {
    void onItemClick(View view, int i);
    void onVideoItemClick(View view, Videocategory data);
    void onSubcatItemClick(View view, SelectCategory data);
    void onSearchVideoItemClick(View view, Searchdata data);
}
