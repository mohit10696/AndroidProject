package com.BlackTech.allVideostatus2020.activity;


import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.graphics.drawable.DrawerArrowDrawable;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.BlackTech.allVideostatus2020.models.Searchdata;
import com.BlackTech.allVideostatus2020.models.SelectCategory;
import com.BlackTech.allVideostatus2020.models.Videocategory;
import com.android.volley.NetworkResponse;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.facebook.appevents.AppEventsConstants;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;

import com.BlackTech.allVideostatus2020.ObservableLayer.PopularVideoViewModel;
import com.BlackTech.allVideostatus2020.R;
import com.BlackTech.allVideostatus2020.adapter.NavigationBarAdapter;
import com.BlackTech.allVideostatus2020.adapter.VideoItemAdapter;
import com.BlackTech.allVideostatus2020.getSet.videoListGetSet;
import com.BlackTech.allVideostatus2020.onClickListners.OnLoadListeners;
import com.BlackTech.allVideostatus2020.onClickListners.onVideoListClick;
import com.BlackTech.allVideostatus2020.Utility.LanguageSelectore;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import org.json.JSONException;
import org.json.JSONObject;

public class PopularActivity extends AppCompatActivity {

    private ListView list_SliderMenu;
    private RecyclerView list_item_videos;
    public LinearLayout mainView;
    private int numberOfRecords;
    private int pageNumber;
    private PopularVideoViewModel popularVideoViewModel;
    public ProgressBar progressBar;
    private static Boolean isLoad = false;
    public List<Object> dataCombined;
    public DrawerLayout drawer;
    private VideoItemAdapter horizontalAdapter;
    private AdView mAdView;
    private InterstitialAd mInterstitialAd;

    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        LanguageSelectore.setLanguageIfAR(this);
        setContentView((int) R.layout.activity_popular);
        this.numberOfRecords = getResources().getInteger(R.integer.numberOfRecords);
        this.popularVideoViewModel = (PopularVideoViewModel) ViewModelProviders.of(this).get(PopularVideoViewModel.class);
        initViews();

        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });

        AdView adView = (AdView)this.findViewById(R.id.ad_view2);
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);
    }


    public void onResume() {
        super.onResume();
    }

    private void setUpDrawer() {
        this.drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        this.mainView = (LinearLayout) findViewById(R.id.mainView);
        this.progressBar = (ProgressBar) findViewById(R.id.progressBar);
        setSupportActionBar(toolbar);
        ((ImageView) toolbar.findViewById(R.id.btn_search)).setVisibility(View.GONE);
        TextView textView = (TextView) toolbar.findViewById(R.id.title);
        textView.setText((int) R.string.txt_popular);
        textView.setTypeface(MainActivity.main_medium);
        ActionBarDrawerToggle r1 = new ActionBarDrawerToggle(this, this.drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close) {
            public void onDrawerSlide(View view, float f) {
                super.onDrawerSlide(view, f);
                if (PopularActivity.this.getResources().getConfiguration().getLayoutDirection() == 1) {
                    PopularActivity.this.mainView.setTranslationX((-f) * ((float) view.getWidth()));
                } else {
                    PopularActivity.this.mainView.setTranslationX(f * ((float) view.getWidth()));
                }
                PopularActivity.this.drawer.bringChildToFront(view);
                PopularActivity.this.drawer.requestLayout();
            }
        };
        DrawerArrowDrawable drawerArrowDrawable = new DrawerArrowDrawable(this);
        drawerArrowDrawable.setColor(getResources().getColor(R.color.colorArrow));
        r1.setDrawerArrowDrawable(drawerArrowDrawable);
        this.drawer.addDrawerListener(r1);
        r1.syncState();
    }

    private void initViews() {
        this.pageNumber = 1;
        this.list_SliderMenu = (ListView) findViewById(R.id.list_slidermenu);
        this.list_item_videos = (RecyclerView) findViewById(R.id.list_item_videos);
        setUpDrawer();
        setUpNavigationBar();
        if (MainActivity.isNetworkConnected(this)) {
            setUpVideoList();
        } else {
            MainActivity.showErrorDialog(this);
        }
    }


    private void setUpNavigationBar() {
        this.list_SliderMenu.setAdapter((ListAdapter) new NavigationBarAdapter(this, this.drawer));
    }

    public static void changeLoad(Boolean bool) {
        isLoad = bool;
    }

    private void setUpVideoList() {
        this.list_item_videos.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
        this.popularVideoViewModel.getPostsList().observe(this, new Observer<ArrayList<videoListGetSet>>() {
            public void onChanged(@Nullable ArrayList<videoListGetSet> arrayList) {
                PopularActivity.this.progressBar.setVisibility(View.VISIBLE);
                PopularActivity.this.updateUI(arrayList);
            }
        });
    }

    public void updateUI(ArrayList<videoListGetSet> arrayList) {
        ProgressBar progressBar2 = this.progressBar;
        if (progressBar2 != null) {
            progressBar2.setVisibility(View.GONE);
        }
        if (arrayList != null) {
            if (this.dataCombined == null || !isLoad.booleanValue()) {
                this.dataCombined = new ArrayList();
            }
            for (int i = 0; i < arrayList.size(); i++) {
                if (getString(R.string.show_admmob_ads).equals("yes")) {
                    if (i % 2 == 0 && i != 0) {
                        this.dataCombined.add("ad");
                    }
                } else if (getString(R.string.show_facebook_ads).equals("yes") && i % 2 == 0 && i != 0) {
                    this.dataCombined.add("ad");
                }
                this.dataCombined.add(arrayList.get(i));
            }
            onVideoListClick r5 = new onVideoListClick() {
                public void onItemClick(View view, int i) {
                    if (PopularActivity.this.dataCombined.get(i) instanceof videoListGetSet) {
                        PopularActivity popularActivity = PopularActivity.this;
                        popularActivity.increaseViewOfItem(((videoListGetSet) popularActivity.dataCombined.get(i)).getId(), i);
//                        if (mInterstitialAd.isLoaded()) {
//                            mInterstitialAd.show();
//                            mInterstitialAd.setAdListener(new AdListener() {
//                                @Override
//                                public void onAdClosed() {
//                                    super.onAdClosed();
//                                    AdRequest adRequest = new AdRequest.Builder().build();
//                                    mInterstitialAd.loadAd(adRequest);
//                                    PopularActivity popularActivity = PopularActivity.this;
//                                    popularActivity.increaseViewOfItem(((videoListGetSet) popularActivity.dataCombined.get(i)).getId(), i);
//                                }
//                            });
//                        }
//                        else {
//                            PopularActivity popularActivity = PopularActivity.this;
//                            popularActivity.increaseViewOfItem(((videoListGetSet) popularActivity.dataCombined.get(i)).getId(), i);
//                        }

                    }
                }

                @Override
                public void onVideoItemClick(View view, Videocategory data) {

                }

                @Override
                public void onSubcatItemClick(View view, SelectCategory data) {

                }

                @Override
                public void onSearchVideoItemClick(View view, Searchdata data) {

                }
            };
            if (!isLoad.booleanValue()) {
                this.horizontalAdapter = new VideoItemAdapter(this.dataCombined, this, r5, this.list_item_videos);
                this.list_item_videos.setAdapter(this.horizontalAdapter);
                notifyAdapter();
            } else {
                notifyAdapter();
                isLoad = false;
            }
            this.horizontalAdapter.setOnLoadMoreListener(new OnLoadListeners() {
                public void onLoadMore() {
                    PopularActivity.this.progressBar.setVisibility(View.VISIBLE);
                    PopularActivity.this.getMoreDataFromServer();
                }
            });
        }
    }


    public void increaseViewOfItem(String str, final int i) {
        this.progressBar.setVisibility(View.VISIBLE);
        String str2 = getString(R.string.link) + "api/video_view_count.php?video_id=" + str;
        Log.w(getClass().getName(), str2);
        Volley.newRequestQueue(this).add(new StringRequest(0, str2, new Response.Listener<String>() {
            public void onResponse(String str) {
                PopularActivity.this.progressBar.setVisibility(View.GONE);
                PopularActivity.this.progressBar.setVisibility(View.GONE);
                Log.e("Response", str);
                try {
                    JSONObject jSONObject = new JSONObject(str).getJSONObject("data");
                    if (Objects.equals(jSONObject.getString("success"), AppEventsConstants.EVENT_PARAM_VALUE_YES)) {
                        ((videoListGetSet) PopularActivity.this.dataCombined.get(i)).setVideoView(jSONObject.getString("success"));
                    } else {
                        Toast.makeText(PopularActivity.this, (int) R.string.network_error, Toast.LENGTH_SHORT).show();
                    }
                    if (PopularActivity.this.dataCombined.get(i) instanceof videoListGetSet) {
                        Intent intent = new Intent(PopularActivity.this, VideoDetailActivity.class);
                        intent.putExtra("videoTitle", ((videoListGetSet) PopularActivity.this.dataCombined.get(i)).getVideo_title());
                        intent.putExtra("videoId", ((videoListGetSet) PopularActivity.this.dataCombined.get(i)).getVideoFileName());
                        intent.putExtra("videoCategory", ((videoListGetSet) PopularActivity.this.dataCombined.get(i)).getVideo_category());
                        intent.putExtra("videoSubCategory", ((videoListGetSet) PopularActivity.this.dataCombined.get(i)).getVideo_subcategory());
                        intent.putExtra("videoDownload", ((videoListGetSet) PopularActivity.this.dataCombined.get(i)).getVideoDownload());
                        intent.putExtra("videoImage", ((videoListGetSet) PopularActivity.this.dataCombined.get(i)).getVideoImage());
                        intent.putExtra("videoCategoryId", ((videoListGetSet) PopularActivity.this.dataCombined.get(i)).getVideo_cat_id());
                        intent.putExtra("videoSubCategoryId", ((videoListGetSet) PopularActivity.this.dataCombined.get(i)).getVideo_subCat_id());
                        intent.putExtra("videoView", ((videoListGetSet) PopularActivity.this.dataCombined.get(i)).getVideoView());
                        intent.putExtra("id", ((videoListGetSet) PopularActivity.this.dataCombined.get(i)).getId());
                        PopularActivity.this.startActivity(intent);
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
                    Toast.makeText(PopularActivity.this.getApplicationContext(), String.valueOf(networkResponse.statusCode), Toast.LENGTH_SHORT).show();
                }
            }
        }));
    }
    public void onDestroy() {
        super.onDestroy();
    }


    private void notifyAdapter() {
        VideoItemAdapter videoItemAdapter = this.horizontalAdapter;
        if (videoItemAdapter != null) {
        }
    }

    public void getMoreDataFromServer() {
        this.pageNumber++;
        Log.e("Check Page", "" + this.pageNumber);
        this.popularVideoViewModel.LoadMoreData(String.valueOf(this.numberOfRecords), String.valueOf(this.pageNumber));
        this.progressBar.setVisibility(View.GONE);
    }

    public void onBackPressed() {
        DrawerLayout drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawerLayout.isDrawerOpen((int) GravityCompat.START)) {
            drawerLayout.closeDrawer((int) GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

}
