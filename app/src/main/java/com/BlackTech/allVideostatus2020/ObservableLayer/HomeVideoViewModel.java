package com.BlackTech.allVideostatus2020.ObservableLayer;

import android.app.Application;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.BlackTech.allVideostatus2020.R;
import com.BlackTech.allVideostatus2020.activity.MainActivity;
import com.BlackTech.allVideostatus2020.adapter.VideoItemAdapter;
import com.BlackTech.allVideostatus2020.getSet.menuGetSet;
import com.BlackTech.allVideostatus2020.getSet.videoListGetSet;
import com.BlackTech.allVideostatus2020.models.Categorys;
import com.BlackTech.allVideostatus2020.models.Searchdata;
import com.BlackTech.allVideostatus2020.models.SelectCategory;
import com.BlackTech.allVideostatus2020.models.Videocategory;
import com.BlackTech.allVideostatus2020.restapiclient.ApiInterface;
import com.BlackTech.allVideostatus2020.restapiclient.RestClient;
import com.android.volley.NetworkResponse;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.facebook.appevents.AppEventsConstants;
import com.facebook.share.internal.MessengerShareContentUtility;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;

public class HomeVideoViewModel extends AndroidViewModel {

    public MutableLiveData<ArrayList<menuGetSet>> menuList;

    public MutableLiveData<ArrayList<videoListGetSet>> videoList;
    // image category
    public MutableLiveData<ArrayList<Categorys>> categorylist;
    // video category
    public MutableLiveData<ArrayList<Videocategory>> videocategorylist;
    // selecte video category
    public MutableLiveData<ArrayList<SelectCategory>> selectevideocatgory;
    // searcvideoname
    public MutableLiveData<ArrayList<Searchdata>> searchdata;


    private final ApiInterface apiInterface;

    public HomeVideoViewModel(@NonNull Application application) {
        super(application);
        if (this.videoList == null) {
            this.videoList = new MutableLiveData<>();
        }
        if (this.menuList == null) {
            this.menuList = new MutableLiveData<>();
            LoadDefaultCategory();
        }
        if (this.categorylist == null) {
            this.categorylist = new MutableLiveData<>();

        }
        apiInterface = RestClient.getRetrofit().create(ApiInterface.class);
    }


    public MutableLiveData<ArrayList<videoListGetSet>> getVideosList() {
        return this.videoList;
    }

    public MutableLiveData<ArrayList<menuGetSet>> getMenuList() {
        return this.menuList;
    }


    public MutableLiveData<ArrayList<Searchdata>> getSearchdata(String videoname){
        searchdata = new MutableLiveData<ArrayList<Searchdata>>();

        apiInterface.getSearchData(videoname).enqueue(new Callback<ArrayList<Searchdata>>() {
            @Override
            public void onResponse(Call<ArrayList<Searchdata>> call, retrofit2.Response<ArrayList<Searchdata>> response) {
                searchdata.setValue(response.body());
            }

            @Override
            public void onFailure(Call<ArrayList<Searchdata>> call, Throwable t) {
                searchdata.setValue(null);
            }
        });
        return searchdata;
    }

    public MutableLiveData<ArrayList<SelectCategory>> getSelectedCategory(String category){
        selectevideocatgory = new MutableLiveData<ArrayList<SelectCategory>>();

        apiInterface.getSelecteCategory(category).enqueue(new Callback<ArrayList<SelectCategory>>() {
            @Override
            public void onResponse(Call<ArrayList<SelectCategory>> call , retrofit2.Response<ArrayList<SelectCategory>> response) {
                Log.e("Critical", category+"onResponse: "+response.body().toString() );
                selectevideocatgory.setValue(response.body());
            }

            @Override
            public void onFailure(Call<ArrayList<SelectCategory>> call, Throwable t) {
                selectevideocatgory.setValue(null);
            }
        });

        return selectevideocatgory;
    }


    public MutableLiveData<ArrayList<Videocategory>> getVideocategorylist() {

        videocategorylist = new MutableLiveData<ArrayList<Videocategory>>();

        apiInterface.getVideoCategorys().enqueue(new Callback<ArrayList<Videocategory>>() {
            @Override
            public void onResponse(Call<ArrayList<Videocategory>> call, retrofit2.Response<ArrayList<Videocategory>> response) {
                videocategorylist.setValue(response.body());
                Log.e("APIDATA", "onResponse: "+response.body().toString() );
            }

            @Override
            public void onFailure(Call<ArrayList<Videocategory>> call, Throwable t) {
                videocategorylist.setValue(null);
            }
        });

        return videocategorylist;
    }


    public MutableLiveData<ArrayList<Categorys>> LoadCategory() {
        categorylist = new MutableLiveData<ArrayList<Categorys>>();
        apiInterface.getCategorys().enqueue(new Callback<ArrayList<Categorys>>() {
            @Override
            public void onResponse(@NonNull Call<ArrayList<Categorys>> call, @NonNull retrofit2.Response<ArrayList<Categorys>> response) {
                categorylist.setValue(response.body());

            }

            @Override
            public void onFailure(@NonNull Call<ArrayList<Categorys>> call, Throwable t) {

                categorylist.setValue(null);
            }
        });
        return categorylist;

    }


    public void LoadVideoList(String str, String str2, String str3, String str4, String str5) {
        String str6 = getApplication().getString(R.string.link) + "api/video_list.php?category=" + str + "&subcategory=" + str2 + "&sort_by=" + str3 + "&noofrecords=" + str4 + "&pageno=" + str5;
        Log.d("CheckUrl", "" + str6);
        String replace = str6.replace(" ", "%20");
        Log.w(getClass().getName(), replace);
        Volley.newRequestQueue(getApplication()).add(new StringRequest(0, replace, new Response.Listener<String>() {
            public void onResponse(String str) {
                Log.e("Response", str);
                ArrayList arrayList = new ArrayList();
                try {
                    arrayList.clear();
                    JSONObject jSONObject = new JSONObject(str);
                    if (Objects.equals(jSONObject.getString("success"), AppEventsConstants.EVENT_PARAM_VALUE_YES)) {
                        JSONArray jSONArray = jSONObject.getJSONArray("video");
                        for (int i = 0; i < jSONArray.length(); i++) {
                            videoListGetSet videolistgetset = new videoListGetSet();
                            JSONObject jSONObject2 = jSONArray.getJSONObject(i);
                            String string = jSONObject2.getString("id");
                            String string2 = jSONObject2.getString("video_category");
                            String string3 = jSONObject2.getString("video_subcategory");
                            String string4 = jSONObject2.getString("video_title");
                            String string5 = jSONObject2.getString("video");
                            String string6 = jSONObject2.getString(MessengerShareContentUtility.MEDIA_IMAGE);
                            String string7 = jSONObject2.getString("view");
                            String string8 = jSONObject2.getString("download");
                            videolistgetset.setVideo_cat_id(jSONObject2.getString("cat_id"));
                            videolistgetset.setVideo_subCat_id(jSONObject2.getString("subcat_id"));
                            videolistgetset.setId(string);
                            videolistgetset.setVideo_title(string4);
                            videolistgetset.setVideo_category(string2);
                            videolistgetset.setVideo_subcategory(string3);
                            videolistgetset.setVideoDownload(string8);
                            videolistgetset.setVideoImage(string6);
                            videolistgetset.setVideoView(string7);
                            videolistgetset.setVideoFileName(string5);
                            arrayList.add(videolistgetset);
                        }
                        HomeVideoViewModel.this.videoList.setValue(arrayList);
                    } else if (HomeVideoViewModel.this.videoList.getValue() != null) {
                        HomeVideoViewModel.this.videoList.setValue(null);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            public void onErrorResponse(VolleyError volleyError) {
                NetworkResponse networkResponse = volleyError.networkResponse;
                if (networkResponse != null) {
                    Log.e("Status code", String.valueOf(networkResponse.statusCode));
                    Toast.makeText(HomeVideoViewModel.this.getApplication(), String.valueOf(networkResponse.statusCode), Toast.LENGTH_SHORT).show();
                }
            }
        }));
    }

    public void LoadMoreData(String str, String str2, String str3, String str4, final String str5) {
        String str6 = getApplication().getString(R.string.link) + "api/video_list.php?category=" + str + "&subcategory=" + str2 + "&sort_by=" + str3 + "&noofrecords=" + str4 + "&pageno=" + str5;
        Log.w(getClass().getName(), str6);
        Volley.newRequestQueue(getApplication()).add(new StringRequest(0, str6.replace(" ", "%20"), new Response.Listener<String>() {
            public void onResponse(String str) {
                Log.e("Response", str);
                ArrayList arrayList = new ArrayList();
                try {
                    Integer.parseInt(str5);
                    JSONObject jSONObject = new JSONObject(str);
                    if (Objects.equals(jSONObject.getString("success"), AppEventsConstants.EVENT_PARAM_VALUE_YES)) {
                        JSONArray jSONArray = jSONObject.getJSONArray("video");
                        for (int i = 0; i < jSONArray.length(); i++) {
                            videoListGetSet videolistgetset = new videoListGetSet();
                            JSONObject jSONObject2 = jSONArray.getJSONObject(i);
                            String string = jSONObject2.getString("id");
                            String string2 = jSONObject2.getString("video_category");
                            String string3 = jSONObject2.getString("video_subcategory");
                            String string4 = jSONObject2.getString("video_title");
                            String string5 = jSONObject2.getString("video");
                            String string6 = jSONObject2.getString(MessengerShareContentUtility.MEDIA_IMAGE);
                            String string7 = jSONObject2.getString("view");
                            String string8 = jSONObject2.getString("download");
                            videolistgetset.setVideo_cat_id(jSONObject2.getString("cat_id"));
                            videolistgetset.setVideo_subCat_id(jSONObject2.getString("subcat_id"));
                            videolistgetset.setId(string);
                            videolistgetset.setVideo_title(string4);
                            videolistgetset.setVideo_category(string2);
                            videolistgetset.setVideo_subcategory(string3);
                            videolistgetset.setVideoDownload(string8);
                            videolistgetset.setVideoImage(string6);
                            videolistgetset.setVideoView(string7);
                            videolistgetset.setVideoFileName(string5);
                            arrayList.add(videolistgetset);
                        }
                        MainActivity.changeLoad(true);
                        VideoItemAdapter.setLoaded(false);
                        HomeVideoViewModel.this.videoList.setValue(arrayList);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            public void onErrorResponse(VolleyError volleyError) {
                NetworkResponse networkResponse = volleyError.networkResponse;
                if (networkResponse != null) {
                    Log.e("Status code", String.valueOf(networkResponse.statusCode));
                    Toast.makeText(HomeVideoViewModel.this.getApplication(), String.valueOf(networkResponse.statusCode), Toast.LENGTH_SHORT).show();
                }
            }
        }));
    }

    public void SearchData(String str, String str2, String str3, String str4) {
        String str5 = getApplication().getString(R.string.link) + "api/video_search.php?search=" + str + "&sort_by=" + str2 + "&noofrecords=" + str3 + "&pageno=" + str4;
        Log.w(getClass().getName(), str5);
        Volley.newRequestQueue(getApplication()).add(new StringRequest(0, str5.replace(" ", "%20"), new Response.Listener<String>() {
            public void onResponse(String str) {
                Log.e("Response", str);
                ArrayList arrayList = new ArrayList();
                try {
                    JSONObject jSONObject = new JSONObject(str);
                    if (Objects.equals(jSONObject.getString("success"), AppEventsConstants.EVENT_PARAM_VALUE_YES)) {
                        JSONArray jSONArray = jSONObject.getJSONArray("video");
                        for (int i = 0; i < jSONArray.length(); i++) {
                            videoListGetSet videolistgetset = new videoListGetSet();
                            JSONObject jSONObject2 = jSONArray.getJSONObject(i);
                            String string = jSONObject2.getString("id");
                            String string2 = jSONObject2.getString("video_category");
                            String string3 = jSONObject2.getString("video_subcategory");
                            String string4 = jSONObject2.getString("video_title");
                            String string5 = jSONObject2.getString("video");
                            String string6 = jSONObject2.getString(MessengerShareContentUtility.MEDIA_IMAGE);
                            String string7 = jSONObject2.getString("view");
                            String string8 = jSONObject2.getString("download");
                            videolistgetset.setVideo_cat_id(jSONObject2.optString("cat_id"));
                            videolistgetset.setVideo_subCat_id(jSONObject2.optString("subcat_id"));
                            videolistgetset.setId(string);
                            videolistgetset.setVideo_title(string4);
                            videolistgetset.setVideo_category(string2);
                            videolistgetset.setVideo_subcategory(string3);
                            videolistgetset.setVideoDownload(string8);
                            videolistgetset.setVideoImage(string6);
                            videolistgetset.setVideoView(string7);
                            videolistgetset.setVideoFileName(string5);
                            arrayList.add(videolistgetset);
                        }
                        HomeVideoViewModel.this.videoList.setValue(arrayList);
                    } else if (HomeVideoViewModel.this.videoList.getValue() != null) {
                        HomeVideoViewModel.this.videoList.setValue(null);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            public void onErrorResponse(VolleyError volleyError) {
                NetworkResponse networkResponse = volleyError.networkResponse;
                if (networkResponse != null) {
                    Log.e("Status code", String.valueOf(networkResponse.statusCode));
                    Toast.makeText(HomeVideoViewModel.this.getApplication(), String.valueOf(networkResponse.statusCode), Toast.LENGTH_SHORT).show();
                }
            }
        }));
    }

    public void LoadMoreSearchData(String str, String str2, String str3, final String str4) {
        String str5 = getApplication().getString(R.string.link) + "api/video_search.php?search=" + str + "&sort_by=" + str2 + "&noofrecords=" + str3 + "&pageno=" + str4;
        Log.w(getClass().getName(), str5);
        Volley.newRequestQueue(getApplication()).add(new StringRequest(0, str5.replace(" ", "%20"), new Response.Listener<String>() {
            public void onResponse(String str) {
                Log.e("Response", str);
                ArrayList arrayList = new ArrayList();
                try {
                    Integer.parseInt(str4);
                    JSONObject jSONObject = new JSONObject(str);
                    if (Objects.equals(jSONObject.getString("success"), AppEventsConstants.EVENT_PARAM_VALUE_YES)) {
                        JSONArray jSONArray = jSONObject.getJSONArray("video");
                        for (int i = 0; i < jSONArray.length(); i++) {
                            videoListGetSet videolistgetset = new videoListGetSet();
                            JSONObject jSONObject2 = jSONArray.getJSONObject(i);
                            String string = jSONObject2.getString("id");
                            String string2 = jSONObject2.getString("video_category");
                            String string3 = jSONObject2.getString("video_subcategory");
                            String string4 = jSONObject2.getString("video_title");
                            String string5 = jSONObject2.getString("video");
                            String string6 = jSONObject2.getString(MessengerShareContentUtility.MEDIA_IMAGE);
                            String string7 = jSONObject2.getString("view");
                            String string8 = jSONObject2.getString("download");
                            videolistgetset.setVideo_cat_id(jSONObject2.optString("cat_id"));
                            videolistgetset.setVideo_subCat_id(jSONObject2.optString("subcat_id"));
                            videolistgetset.setId(string);
                            videolistgetset.setVideo_title(string4);
                            videolistgetset.setVideo_category(string2);
                            videolistgetset.setVideo_subcategory(string3);
                            videolistgetset.setVideoDownload(string8);
                            videolistgetset.setVideoImage(string6);
                            videolistgetset.setVideoView(string7);
                            videolistgetset.setVideoFileName(string5);
                            arrayList.add(videolistgetset);
                        }
                        MainActivity.changeLoad(true);
                        VideoItemAdapter.setLoaded(false);
                        HomeVideoViewModel.this.videoList.setValue(arrayList);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            public void onErrorResponse(VolleyError volleyError) {
                NetworkResponse networkResponse = volleyError.networkResponse;
                if (networkResponse != null) {
                    Log.e("Status code", String.valueOf(networkResponse.statusCode));
                    Toast.makeText(HomeVideoViewModel.this.getApplication(), String.valueOf(networkResponse.statusCode), Toast.LENGTH_SHORT).show();
                }
            }
        }));
    }

    private void LoadDefaultCategory() {
        String str = getApplication().getString(R.string.link) + "api/video_category.php";
        Log.w(getClass().getName(), str);
        Volley.newRequestQueue(getApplication()).add(new StringRequest(0, str.replace(" ", "%20"), new Response.Listener<String>() {
            public void onResponse(String str) {
                Log.e("Response", str);
                try {
                    JSONObject jSONObject = new JSONObject(str);
                    if (Objects.equals(jSONObject.getString("success"), AppEventsConstants.EVENT_PARAM_VALUE_YES)) {
                        ArrayList arrayList = new ArrayList();
                        JSONArray jSONArray = jSONObject.getJSONArray("category");
                        for (int i = 0; i < jSONArray.length(); i++) {
                            menuGetSet menugetset = new menuGetSet();
                            JSONObject jSONObject2 = jSONArray.getJSONObject(i);
                            menugetset.setMenu_category(jSONObject2.getString("category"));
                            menugetset.setCat_id(jSONObject2.getString("id"));
                            arrayList.add(menugetset);
                        }
                        HomeVideoViewModel.this.menuList.setValue(arrayList);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            public void onErrorResponse(VolleyError volleyError) {
                NetworkResponse networkResponse = volleyError.networkResponse;
                if (networkResponse != null) {
                    Log.e("Status code", String.valueOf(networkResponse.statusCode));
                    Toast.makeText(HomeVideoViewModel.this.getApplication(), String.valueOf(networkResponse.statusCode), Toast.LENGTH_SHORT).show();
                }
            }
        }));
    }
}
