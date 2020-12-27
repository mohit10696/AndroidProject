package com.BlackTech.allVideostatus2020.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
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
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.BlackTech.allVideostatus2020.ObservableLayer.HomeVideoViewModel;
import com.BlackTech.allVideostatus2020.R;
import com.BlackTech.allVideostatus2020.Utility.Config;
import com.BlackTech.allVideostatus2020.Utility.LanguageSelectore;
import com.BlackTech.allVideostatus2020.adapter.HorizontalAdapter;
import com.BlackTech.allVideostatus2020.adapter.Horizontalcategory;
import com.BlackTech.allVideostatus2020.adapter.NavigationBarAdapter;
import com.BlackTech.allVideostatus2020.adapter.SearchAdapters;
import com.BlackTech.allVideostatus2020.adapter.VideoItemAdapter;
import com.BlackTech.allVideostatus2020.getSet.categoryGetSet;
import com.BlackTech.allVideostatus2020.getSet.menuGetSet;
import com.BlackTech.allVideostatus2020.models.Categorys;
import com.BlackTech.allVideostatus2020.models.Searchdata;
import com.BlackTech.allVideostatus2020.models.SelectCategory;
import com.BlackTech.allVideostatus2020.models.Videocategory;
import com.BlackTech.allVideostatus2020.onClickListners.onCategoryListClick;
import com.BlackTech.allVideostatus2020.onClickListners.onVideoListClick;
import com.android.volley.NetworkResponse;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.facebook.appevents.AppEventsConstants;
import com.facebook.share.internal.ShareConstants;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.messaging.FirebaseMessaging;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {
    InterstitialAd interstitialAd;
    private static Boolean isLoad = false;
    private long backPressedTime = 0;
    public static Boolean isSh = false;
    public static final String prefName = "VideoStatus";
    public static Typeface main_bold;
    public static Typeface main_medium;
    public ArrayList<categoryGetSet> categoryGetSets;
    public String catStr;
    public List<Object> dataCombined;
    public List<Object> categordataCombined;
    public DrawerLayout drawer;
    public EditText editsearch;
    public HomeVideoViewModel homeVideoViewModel;
    private ImageView ic_back;
    public ImageView ic_text;
    public RecyclerView list_like;
    private ListView list_sliderMenu;
    private RecyclerView list_item_videos;
    public RelativeLayout mainView;
    public int numberOfRecords;
    public int pageNumber;
    public ProgressBar progressBar;
    public String search = "";
    public View searchView;
    public String subcatStr;
    private VideoItemAdapter verticalAdapter;
    private Horizontalcategory horizontalAdapter;
    private SearchAdapters searchAdapters;
    private AdView mAdView;
    private InterstitialAd mInterstitialAd;


    public static void changeLoad(Boolean bool) {
        isLoad = bool;
    }

    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_main);
        LanguageSelectore.setLanguageIfAR(this);
        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });

        AdView adView = this.findViewById(R.id.ad_view1);
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);
        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId("ca-app-pub-3149366996577725/5814837459");
        mInterstitialAd.loadAd(new AdRequest.Builder().build());

        this.numberOfRecords = getResources().getInteger(R.integer.numberOfRecords);
        this.homeVideoViewModel = new ViewModelProvider(this).get(HomeVideoViewModel.class);
        if (isNetworkConnected(this)) {
            getDefaultCategory();
        } else {
            showErrorDialog(this);
        }
        initViews();
        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences(Config.SHARED_PREF, 0);
        if (sharedPreferences.getString("regId", null) != null) {
            String string = sharedPreferences.getString("regId", null);
            Log.e("fireBaseRid132", "Firebase Reg id: " + string);
        } else {
            new BroadcastReceiver() {
                public void onReceive(Context context, Intent intent) {
                    if (Objects.equals(intent.getAction(), Config.REGISTRATION_COMPLETE)) {
                        FirebaseMessaging.getInstance().subscribeToTopic(Config.TOPIC_GLOBAL);
                        MainActivity.this.displayFireBaseRegId();
                    } else if (Objects.equals(intent.getAction(), Config.PUSH_NOTIFICATION)) {
                        String stringExtra = intent.getStringExtra(ShareConstants.WEB_DIALOG_PARAM_MESSAGE);
                        Context applicationContext = MainActivity.this.getApplicationContext();
                        Toast.makeText(applicationContext, "notification: " + stringExtra, Toast.LENGTH_SHORT).show();
                    }
                }
            };
        }
        createNotificationChannel();
    }

    private void globalTypeface() {
        main_medium = Typeface.createFromAsset(getAssets(), "fonts/Roboto-Regular.ttf");
        main_bold = Typeface.createFromAsset(getAssets(), "fonts/Roboto-Bold.ttf");
    }

    private void getDefaultCategory() {
        /// darmesh

        homeVideoViewModel.LoadCategory().observe(this, new Observer<ArrayList<Categorys>>() {
            @Override
            public void onChanged(ArrayList<Categorys> categorys) {
//                Log.e("check_dharmeshapi", "-----------------------------" + categorys.get(0).Category);
                if (categorys != null) {
                    list_like.setLayoutManager(new LinearLayoutManager(MainActivity.this, RecyclerView.HORIZONTAL, false));
                    list_like.setAdapter(new HorizontalAdapter(MainActivity.this, categorys, new onCategoryListClick() {
                        public void onItemClick(View view, int i) {
                            //Toast.makeText(MainActivity.this, "hello friend", Toast.LENGTH_SHORT).show();
                            homeVideoViewModel.getSelectedCategory(categorys.get(i).Category).observe(MainActivity.this, new Observer<ArrayList<SelectCategory>>() {
                                @Override
                                public void onChanged(ArrayList<SelectCategory> selectCategories) {
                                    if (selectCategories != null) {
//                                        MainActivity.this.updateUI(selectCategories);
                                        list_item_videos.setVisibility(View.VISIBLE);
                                        if (categordataCombined == null || !isLoad.booleanValue()) {
                                            categordataCombined = new ArrayList();
                                        }
                                        for (int i = 0; i < selectCategories.size(); i++) {
                                            categordataCombined.add(selectCategories.get(i));
                                           // Log.e("CheckselectCategories", " ***************************************" + selectCategories.get(i).Category);
                                        }
                                        progressBar.setVisibility(View.GONE);
                                        onVideoListClick r5 = new onVideoListClick() {
                                            public void onItemClick(View view, int i) {

                                            }

                                            @Override
                                            public void onVideoItemClick(View view, Videocategory data) {
//                                                Toast.makeText(MainActivity.this, "Video_category" + data.video_name, Toast.LENGTH_SHORT).show();
//                                                Intent intent = new Intent(MainActivity.this, VideoDetailActivity.class);
//                                                intent.putExtra("videoserilizeable", data);
//                                                startActivity(intent);

                                            }

                                            @Override
                                            public void onSubcatItemClick(View view, SelectCategory data) {
                                               // Toast.makeText(MainActivity.this, "Video_category" + data.video_name, Toast.LENGTH_SHORT).show();
                                                Intent intent = new Intent(MainActivity.this, VideoDetailActivity.class);
                                                intent.putExtra("videoserilizeable", data);
                                                intent.putExtra("flag", "1");
                                                startActivity(intent);
                                            }

                                            @Override
                                            public void onSearchVideoItemClick(View view, Searchdata data) {
//                                                Toast.makeText(MainActivity.this, "Video_category" + data.video_name, Toast.LENGTH_SHORT).show();
//                                                Intent intent = new Intent(MainActivity.this, VideoDetailActivity.class);
//                                                intent.putExtra("videoserilizeable", data);
////                                                intent.putExtra("flag", "2");
//                                                startActivity(intent);
                                            }
                                        };

                                        horizontalAdapter = new Horizontalcategory(categordataCombined, MainActivity.this, r5);
                                        list_item_videos.setAdapter(horizontalAdapter);
                                        horizontalAdapter.notifyDataSetChanged();
//                                        Horizontalcategory vusus = horizontalAdapter;
//                                        if (vusus != null) {
//                                        }

//                                        if (!isLoad.booleanValue()) {
//
//
////                                            notifyAdapter();
//                                        } else {
//                                            notifyAdapter();
//                                            isLoad = false;
//                                        }
                                    }
                                }
                            });
                        }
                    }));
                }

            }
        });
        this.homeVideoViewModel.getMenuList().observe(this, new Observer<ArrayList<menuGetSet>>() {
            public void onChanged(@Nullable ArrayList<menuGetSet> arrayList) {
                if (arrayList != null) {
                    MainActivity.this.setUpVideoList();
                    MainActivity mainActivity = MainActivity.this;
                    String unused = mainActivity.catStr = mainActivity.getSharedPreferences(MainActivity.prefName, 0).getString("Category", arrayList.get(0).getCat_id());
                    int unused2 = MainActivity.this.pageNumber = 1;
                    MainActivity.this.homeVideoViewModel.LoadVideoList(MainActivity.this.getSharedPreferences(MainActivity.prefName, 0).getString("Category", MainActivity.this.catStr), "", MainActivity.this.getSharedPreferences(MainActivity.prefName, 0).getString("orderBy", "desc"), String.valueOf(MainActivity.this.numberOfRecords), String.valueOf(MainActivity.this.pageNumber));
                    MainActivity mainActivity2 = MainActivity.this;
//                    mainActivity2.setUpCategoryView(mainActivity2.catStr);
                    MainActivity.this.getIntents();
                }
            }
        });
    }

    public void onResume() {
        super.onResume();

    }

    public void getIntents() {
        this.subcatStr = getIntent().getStringExtra("subCategory");
        if (this.subcatStr == null) {
            this.subcatStr = "";
            return;
        }
        this.pageNumber = 1;
        this.homeVideoViewModel.LoadVideoList(getSharedPreferences(prefName, 0).getString("Category", this.catStr), this.subcatStr, getSharedPreferences(prefName, 0).getString("orderBy", "desc"), String.valueOf(this.numberOfRecords), String.valueOf(this.pageNumber));
    }

//    public void setUpCategoryView(final String str) {
//        String replace = (getString(R.string.link) + "api/video_subcategory.php?category=" + str).replace(" ", "%20");
//        Log.w(getClass().getName(), replace);
//        Volley.newRequestQueue(this).add(new StringRequest(0, replace, new Response.Listener<String>() {
//            public void onResponse(final String str) {
//                Log.e("Response", str);
//                try {
//                    JSONObject jSONObject = new JSONObject(str);
//                    if (Objects.equals(jSONObject.getString("success"), AppEventsConstants.EVENT_PARAM_VALUE_YES)) {
//                        ArrayList unused = MainActivity.this.categoryGetSets = new ArrayList();
//                        JSONArray jSONArray = jSONObject.getJSONArray("video");
//                        for (int i = 0; i < jSONArray.length(); i++) {
//                            categoryGetSet categorygetset = new categoryGetSet();
//                            JSONObject jSONObject2 = jSONArray.getJSONObject(i);
//                            String string = jSONObject2.getString("subcategory");
//                            String string2 = jSONObject2.getString("id");
//                            categorygetset.setImageName(jSONObject2.getString("subcategory_icon").replace(" ", "%20"));
//                            categorygetset.setSubCategoryId(string2);
//                            categorygetset.setSubCategoryName(string);
//                            MainActivity.this.categoryGetSets.add(categorygetset);
//                        }
//                    }
//                    if (MainActivity.this.categoryGetSets != null) {
//                        MainActivity.this.list_like.setLayoutManager(new LinearLayoutManager(MainActivity.this, RecyclerView.HORIZONTAL, false));
//                        MainActivity.this.list_like.setAdapter(new HorizontalAdapter(MainActivity.this, MainActivity.this.categoryGetSets, new onCategoryListClick() {
//                            public void onItemClick(View view, int i) {
//                                String unused = MainActivity.this.subcatStr = ((categoryGetSet) MainActivity.this.categoryGetSets.get(i)).getSubCategoryId();
//                                int unused2 = MainActivity.this.pageNumber = 1;
//                                MainActivity.this.homeVideoViewModel.LoadVideoList(MainActivity.this.getSharedPreferences(MainActivity.prefName, 0).getString("Category", str), MainActivity.this.subcatStr, MainActivity.this.getSharedPreferences(MainActivity.prefName, 0).getString("orderBy", "desc"), String.valueOf(MainActivity.this.numberOfRecords), String.valueOf(MainActivity.this.pageNumber));
//                            }
//                        }));
//                    }
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//            }
//        }, new Response.ErrorListener() {
//            public void onErrorResponse(VolleyError volleyError) {
//                NetworkResponse networkResponse = volleyError.networkResponse;
//                if (networkResponse != null) {
//                    Log.e("Status code", String.valueOf(networkResponse.statusCode));
//                    Toast.makeText(MainActivity.this.getApplicationContext(), String.valueOf(networkResponse.statusCode), Toast.LENGTH_SHORT).show();
//                }
//            }
//        }));
//    }

    public void displayFireBaseRegId() {
        String string = getApplicationContext().getSharedPreferences(Config.SHARED_PREF, 0).getString("regId", null);
        Log.e("fireBaseRid", "Firebase Reg id: " + string);
    }

    public void setUpVideoList() {
        this.list_item_videos.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
//        this.homeVideoViewModel.getVideosList().observe(this, new Observer<ArrayList<videoListGetSet>>() {
//            public void onChanged(@Nullable ArrayList<videoListGetSet> arrayList) {
//                MainActivity.this.progressBar.setVisibility(View.VISIBLE);
//                if (arrayList != null) {
//                    MainActivity.this.updateUI(arrayList);
//                } else {
//                    MainActivity.this.emptyUI();
//                }
//            }
//        });


        homeVideoViewModel.getVideocategorylist().observe(this, new Observer<ArrayList<Videocategory>>() {
            @Override
            public void onChanged(ArrayList<Videocategory> videocategories) {
                if (videocategories != null) {
                    MainActivity.this.updateUI(videocategories);
                } else {
                    MainActivity.this.emptyUI();
                }
            }
        });
    }

    private void initViews() {
        this.list_sliderMenu = findViewById(R.id.list_slidermenu);
        this.list_like = findViewById(R.id.list_like);
        this.list_item_videos = findViewById(R.id.list_item_videos);
        this.progressBar = findViewById(R.id.progressBar);
        this.pageNumber = 1;
        this.searchView = findViewById(R.id.searchView);
        this.ic_back = findViewById(R.id.ic_back);
        this.ic_text = findViewById(R.id.ic_text);
        this.editsearch = findViewById(R.id.editsearch);
        setUpDrawer();
       setUpNavigationBar();
        setUpSearch();
    }

    private void setUpSearch() {
        this.ic_back.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                MainActivity.this.searchView.setVisibility(View.GONE);
                MainActivity.this.editsearch.setText("");
                Boolean unused = MainActivity.isSh = false;
                int unused2 = MainActivity.this.pageNumber = 1;
                Intent intent = new Intent(MainActivity.this, MainActivity.class);
                startActivity(intent);
                MainActivity.this.homeVideoViewModel.LoadVideoList(MainActivity.this.getSharedPreferences(MainActivity.prefName, 0).getString("Category", MainActivity.this.catStr), "", MainActivity.this.getSharedPreferences(MainActivity.prefName, 0).getString("orderBy", "desc"), String.valueOf(MainActivity.this.numberOfRecords), String.valueOf(MainActivity.this.pageNumber));
            }
        });
        this.ic_text.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                MainActivity.this.editsearch.setText("");
            }
        });
        this.editsearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if (i != 3) {
                    return false;
                }
                Boolean unused = MainActivity.isSh = true;
                int unused2 = MainActivity.this.pageNumber = 1;
                MainActivity.this.performSearch(textView.getText().toString());
                String unused3 = MainActivity.this.search = textView.getText().toString();
                MainActivity.this.editsearch.onEditorAction(6);
                return true;
            }
        });
        this.editsearch.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable editable) {
            }

            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            }

            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                if (charSequence.toString().trim().length() == 0) {
                    MainActivity.this.ic_text.setVisibility(View.GONE);
                } else {
                    MainActivity.this.ic_text.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    public void performSearch(String str) {
        this.progressBar.setVisibility(View.VISIBLE);
//        this.homeVideoViewModel.SearchData(str, getSharedPreferences(prefName, 0).getString("orderBy", "desc"), String.valueOf(this.numberOfRecords), String.valueOf(this.pageNumber));
        this.progressBar.setVisibility(View.GONE);


        homeVideoViewModel.getSearchdata(str + ".mp4").observe(this, new Observer<ArrayList<Searchdata>>() {
            @Override
            public void onChanged(ArrayList<Searchdata> searchdata) {



//
//                searchAdapters = new SearchAdapters(dataCombined, this, r5, this.list_item_videos);
//                this.list_item_videos.setAdapter(this.verticalAdapter);
//                notifyAdapter();


                SearchVideoUi(searchdata);


            }
        });
    }

    private void SearchVideoUi(ArrayList<Searchdata> searchdata) {
        this.list_item_videos.setVisibility(View.VISIBLE);
        dataCombined = new ArrayList();
        if (searchdata != null) {
            for (int i = 0; i < searchdata.size(); i++) {
//                if ((getString(R.string.show_admmob_ads).equals("yes") || getString(R.string.show_facebook_ads).equals("yes")) && i % 2 == 0 && i != 0) {
//                    this.dataCombined.add("ad");
//                }
                dataCombined.add(searchdata.get(i));
            }
            this.progressBar.setVisibility(View.GONE);
            onVideoListClick r5 = new onVideoListClick() {
                public void onItemClick(View view, int i) {


                }

                @Override
                public void onVideoItemClick(View view, Videocategory data) {


                }

                @Override
                public void onSubcatItemClick(View view, SelectCategory data) {

                }

                @Override
                public void onSearchVideoItemClick(View view, Searchdata data) {
                    Toast.makeText(MainActivity.this, "Video_category" + data.video_name, Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(MainActivity.this, VideoDetailActivity.class);
                    intent.putExtra("videoserilizeable", data);
                    intent.putExtra("flag", "2");
                    startActivity(intent);
                }
            };

            searchAdapters = new SearchAdapters(dataCombined, this, r5, this.list_item_videos);
            this.list_item_videos.setAdapter(searchAdapters);
            notifyAdapter();

        }


    }

    private void setUpDrawer() {
        this.drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        this.mainView = (RelativeLayout) findViewById(R.id.mainView);
        toolbar.setNavigationIcon((int) R.drawable.icon_selectsidemenu);
        setSupportActionBar(toolbar);
        ((TextView) toolbar.findViewById(R.id.title)).setTypeface(main_medium);
        ((ImageView) toolbar.findViewById(R.id.btn_search)).setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                MainActivity.this.searchView.setVisibility(View.VISIBLE);
                MainActivity.this.editsearch.requestFocus();
                InputMethodManager inputMethodManager = (InputMethodManager) MainActivity.this.getSystemService(Context.INPUT_METHOD_SERVICE);
                if (inputMethodManager != null) {
                    inputMethodManager.showSoftInput(MainActivity.this.editsearch, 1);
                }
            }
        });
        ActionBarDrawerToggle r1 = new ActionBarDrawerToggle(this, this.drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close) {
            public void onDrawerSlide(View view, float f) {
                super.onDrawerSlide(view, f);
                if (MainActivity.this.getResources().getConfiguration().getLayoutDirection() == 1) {
                    MainActivity.this.mainView.setTranslationX((-f) * ((float) view.getWidth()));
                } else {
                    MainActivity.this.mainView.setTranslationX(f * ((float) view.getWidth()));
                }
                MainActivity.this.drawer.bringChildToFront(view);
                MainActivity.this.drawer.requestLayout();
            }
        };
        DrawerArrowDrawable drawerArrowDrawable = new DrawerArrowDrawable(this);
        drawerArrowDrawable.setColor(getResources().getColor(R.color.colorArrow));
        r1.setDrawerArrowDrawable(drawerArrowDrawable);
        this.drawer.addDrawerListener(r1);
        r1.syncState();
    }

    private void setUpNavigationBar() {
        this.list_sliderMenu.setAdapter((ListAdapter) new NavigationBarAdapter(this, this.drawer));
    }
    public void updateUI(ArrayList<Videocategory> arrayList) {
        this.list_item_videos.setVisibility(View.VISIBLE);
        dataCombined = new ArrayList();
        if (arrayList != null) {
            for (int i = 0; i < arrayList.size(); i++) {
//                if ((getString(R.string.show_admmob_ads).equals("yes") || getString(R.string.show_facebook_ads).equals("yes")) && i % 2 == 0 && i != 0) {
//                    this.dataCombined.add("ad");
//                }
                dataCombined.add(arrayList.get(i));
            }
            this.progressBar.setVisibility(View.GONE);
            onVideoListClick r5 = new onVideoListClick() {
                public void onItemClick(View view, int i) {


                }

                @Override
                public void onVideoItemClick(View view, Videocategory data) {
                    //Toast.makeText(MainActivity.this, "Video_category" + data.video_name, Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(MainActivity.this, VideoDetailActivity.class);
                    intent.putExtra("videoserilizeable", data);
                    intent.putExtra("flag", "0");
                    startActivity(intent);

                }

                @Override
                public void onSubcatItemClick(View view, SelectCategory data) {

                }

                @Override
                public void onSearchVideoItemClick(View view, Searchdata data) {

                }
            };

            this.verticalAdapter = new VideoItemAdapter(dataCombined, this, r5, this.list_item_videos);
            this.list_item_videos.setAdapter(this.verticalAdapter);
            notifyAdapter();


//            if (!isLoad.booleanValue()) {
//                this.verticalAdapter = new VideoItemAdapter(dataCombined, this, r5, this.list_item_videos);
//                this.list_item_videos.setAdapter(this.verticalAdapter);
////                notifyAdapter();
//            } else {
////                notifyAdapter();
//                isLoad = false;
//            }
////            this.verticalAdapter.setOnLoadMoreListener(new OnLoadListeners() {
////                public void onLoadMore() {
////                    MainActivity.this.progressBar.setVisibility(View.VISIBLE);
////                    MainActivity.this.getMoreDataFromServer();
////                }
////            });
        }
    }

    public void emptyUI() {
        ProgressBar progressBar2 = this.progressBar;
        if (progressBar2 != null) {
            progressBar2.setVisibility(View.GONE);
        }
        this.list_item_videos.setVisibility(View.GONE);
        Snackbar make = Snackbar.make(this.mainView, R.string.txt_no_data_avail, BaseTransientBottomBar.LENGTH_LONG);
        make.getView().findViewById(R.id.snackbar_text).setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        make.show();
    }

    private void notifyAdapter() {
        VideoItemAdapter videoItemAdapter = this.verticalAdapter;
        if (videoItemAdapter != null) {
        }
    }

    public void getMoreDataFromServer() {
        this.pageNumber++;
        if (!isSh.booleanValue()) {
            Log.e("Check Page", "" + this.pageNumber);
            this.homeVideoViewModel.LoadMoreData(getSharedPreferences(prefName, 0).getString("Category", this.catStr), this.subcatStr, getSharedPreferences(prefName, 0).getString("orderBy", "desc"), String.valueOf(this.numberOfRecords), String.valueOf(this.pageNumber));
            this.progressBar.setVisibility(View.GONE);
            return;
        }
        this.homeVideoViewModel.LoadMoreSearchData(this.search, getSharedPreferences(prefName, 0).getString("orderBy", "desc"), String.valueOf(this.numberOfRecords), String.valueOf(this.pageNumber));
        this.progressBar.setVisibility(View.GONE);
    }

    /* access modifiers changed from: private */
    public void increaseViewOfItem(String str, final int i) {
        String str2 = getString(R.string.link) + "api/video_view_count.php?video_id=" + str;
        Log.w(getClass().getName(), str2);
        Volley.newRequestQueue(this).add(new StringRequest(0, str2.replace(" ", "%20"), new Response.Listener<String>() {
            public void onResponse(String str) {
                Log.e("Response", str);
                try {
                    JSONObject jSONObject = new JSONObject(str).getJSONObject("data");
                    if (Objects.equals(jSONObject.getString("success"), AppEventsConstants.EVENT_PARAM_VALUE_YES)) {
//                        ((videoListGetSet) MainActivity.this.dataCombined.get(i)).setVideoView(jSONObject.getString("view"));
                    } else {
                        Toast.makeText(MainActivity.this, "Downloaded Successfully", Toast.LENGTH_SHORT).show();
                    }
//                    if (MainActivity.this.dataCombined.get(i) instanceof videoListGetSet) {
//                        Log.d("CheckAllValue", "" + ((videoListGetSet) MainActivity.this.dataCombined.get(i)).getVideo_title() + "::::" + ((videoListGetSet) MainActivity.this.dataCombined.get(i)).getVideoFileName() + "::::" + ((videoListGetSet) MainActivity.this.dataCombined.get(i)).getVideo_cat_id() + "::::" + ((videoListGetSet) MainActivity.this.dataCombined.get(i)).getVideo_subCat_id() + "::::" + ((videoListGetSet) MainActivity.this.dataCombined.get(i)).getVideoDownload() + "::::" + ((videoListGetSet) MainActivity.this.dataCombined.get(i)).getVideoImage() + "::::" + ((videoListGetSet) MainActivity.this.dataCombined.get(i)).getVideoView() + "::::" + ((videoListGetSet) MainActivity.this.dataCombined.get(i)).getId());
//                        Intent intent = new Intent(MainActivity.this, VideoDetailActivity.class);
//                        intent.putExtra("videoTitle", ((videoListGetSet) MainActivity.this.dataCombined.get(i)).getVideo_title());
//                        intent.putExtra("videoId", ((videoListGetSet) MainActivity.this.dataCombined.get(i)).getVideoFileName());
//                        intent.putExtra("videoCategory", ((videoListGetSet) MainActivity.this.dataCombined.get(i)).getVideo_category());
//                        intent.putExtra("videoSubCategory", ((videoListGetSet) MainActivity.this.dataCombined.get(i)).getVideo_subcategory());
//                        intent.putExtra("videoCategoryId", ((videoListGetSet) MainActivity.this.dataCombined.get(i)).getVideo_cat_id());
//                        intent.putExtra("videoSubCategoryId", ((videoListGetSet) MainActivity.this.dataCombined.get(i)).getVideo_subCat_id());
//                        intent.putExtra("videoDownload", ((videoListGetSet) MainActivity.this.dataCombined.get(i)).getVideoDownload());
//                        intent.putExtra("videoImage", ((videoListGetSet) MainActivity.this.dataCombined.get(i)).getVideoImage());
//                        intent.putExtra("videoView", ((videoListGetSet) MainActivity.this.dataCombined.get(i)).getVideoView());
//                        intent.putExtra("id", ((videoListGetSet) MainActivity.this.dataCombined.get(i)).getId());
//                        MainActivity.this.startActivity(intent);
//                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            public void onErrorResponse(VolleyError volleyError) {
                NetworkResponse networkResponse = volleyError.networkResponse;
                if (networkResponse != null) {
                    Log.e("Status code", String.valueOf(networkResponse.statusCode));
                    Toast.makeText(MainActivity.this.getApplicationContext(), String.valueOf(networkResponse.statusCode), Toast.LENGTH_SHORT).show();
                }
            }
        }));
    }

    @Override
    public void onBackPressed() {
        long t = System.currentTimeMillis();
        if (t - backPressedTime > 2000) {
            backPressedTime = t;
            Toast.makeText(this, "Press back again to Exit ", Toast.LENGTH_SHORT).show();
        } else {
            moveTaskToBack(true);
            android.os.Process.killProcess(android.os.Process.myPid());
            System.exit(1);
        }
        DrawerLayout drawerLayout = findViewById(R.id.drawer_layout);
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        }
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= 26) {
            String string = getString(R.string.app_name);
            String string2 = getString(R.string.app_description);
            NotificationChannel notificationChannel = new NotificationChannel("100", string, NotificationManager.IMPORTANCE_DEFAULT);
            notificationChannel.setDescription(string2);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(notificationChannel);
            }
        }
    }

    public void onDestroy() {
        super.onDestroy();
    }

    public static boolean isNetworkConnected(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        return connectivityManager != null && connectivityManager.getActiveNetworkInfo() != null;
    }

    public static void showErrorDialog(Context context) {
        try {
            ProgressBar progressBar2 = ((Activity) context).findViewById(R.id.progressBar);
            if (progressBar2.getVisibility() == View.VISIBLE) {
                progressBar2.setVisibility(View.GONE);
            }
            final AlertDialog create = new AlertDialog.Builder(context).create();
            create.setTitle(context.getString(R.string.info));
            create.setMessage(context.getString(R.string.noInternet));
            create.setButton(-1, context.getString(R.string.txt_ok), new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialogInterface, int i) {
                    create.dismiss();
                }
            });
            create.show();
        } catch (Exception e) {
            Log.d("Home", "Show Dialog: " + e.getMessage());
        }
    }
}
