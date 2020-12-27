package com.BlackTech.allVideostatus2020.ObservableLayer;

import android.app.Application;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.android.volley.NetworkResponse;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.facebook.appevents.AppEventsConstants;
import com.facebook.share.internal.MessengerShareContentUtility;
import com.BlackTech.allVideostatus2020.R;
import com.BlackTech.allVideostatus2020.activity.PopularActivity;
import com.BlackTech.allVideostatus2020.adapter.VideoItemAdapter;
import com.BlackTech.allVideostatus2020.getSet.videoListGetSet;
import java.util.ArrayList;
import java.util.Objects;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class PopularVideoViewModel extends AndroidViewModel {

    public MutableLiveData<ArrayList<videoListGetSet>> videoList;

    public PopularVideoViewModel(@NonNull Application application) {
        super(application);
        if (this.videoList == null) {
            this.videoList = new MutableLiveData<>();
            LoadVideoList("4", AppEventsConstants.EVENT_PARAM_VALUE_YES);
        }
    }

    public MutableLiveData<ArrayList<videoListGetSet>> getPostsList() {
        return this.videoList;
    }

    private void LoadVideoList(String str, String str2) {
        String str3 = getApplication().getString(R.string.link) + "api/popular_video.php?noofrecords=" + str + "&pageno=" + str2;
        Log.d("CheckUrl", "" + str3);
        String replace = str3.replace(" ", "%20");
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
                        PopularVideoViewModel.this.videoList.setValue(arrayList);
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
                    Toast.makeText(PopularVideoViewModel.this.getApplication(), String.valueOf(networkResponse.statusCode), Toast.LENGTH_SHORT).show();
                }
            }
        }));
    }

    public void LoadMoreData(String str, String str2) {
        String str3 = getApplication().getString(R.string.link) + "api/popular_video.php?noofrecords=" + str + "&pageno=" + str2;
        Log.w(getClass().getName(), str3);
        Volley.newRequestQueue(getApplication()).add(new StringRequest(0, str3.replace(" ", "%20"), new Response.Listener<String>() {
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
                        PopularActivity.changeLoad(true);
                        VideoItemAdapter.setLoaded(false);
                        PopularVideoViewModel.this.videoList.setValue(arrayList);
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
                    Toast.makeText(PopularVideoViewModel.this.getApplication(), String.valueOf(networkResponse.statusCode), Toast.LENGTH_SHORT).show();
                }
            }
        }));
    }
}
