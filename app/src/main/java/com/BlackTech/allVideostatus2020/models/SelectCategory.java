package com.BlackTech.allVideostatus2020.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class SelectCategory implements Serializable {

    @Expose
    @SerializedName("video_name")
    public String video_name;
    @Expose
    @SerializedName("Thumbnail")
    public String Thumbnail;
    @Expose
    @SerializedName("Category")
    public String Category;
    @Expose
    @SerializedName("Id")
    public String Id;
    @Expose
    @SerializedName("Img_title")
    public String Img_title;
    @Expose
    @SerializedName("View")
    public String View;


}
