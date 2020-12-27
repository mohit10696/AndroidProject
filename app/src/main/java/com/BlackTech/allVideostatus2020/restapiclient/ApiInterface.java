package com.BlackTech.allVideostatus2020.restapiclient;

import com.BlackTech.allVideostatus2020.models.Categorys;
import com.BlackTech.allVideostatus2020.models.Searchdata;
import com.BlackTech.allVideostatus2020.models.SelectCategory;
import com.BlackTech.allVideostatus2020.models.UpdateView;
import com.BlackTech.allVideostatus2020.models.Videocategory;

import org.json.JSONArray;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ApiInterface {


    @GET("api_category.php")
    Call<ArrayList<Categorys>> getCategorys();

    @GET("api_video_data.php")
    Call<ArrayList<Videocategory>> getVideoCategorys();

    @GET("api_input_category.php?")
    Call<ArrayList<SelectCategory>> getSelecteCategory(@Query("Category") String category);

    @GET("api_search_vid_data.php?")
    Call<ArrayList<Searchdata>> getSearchData(@Query("video_name") String video_name);

    @GET("api_search_vid_data.php?")
    Call<ArrayList<Object>> UpdateView(@Query("Img_title") String img_title);

}
