package com.BlackTech.allVideostatus2020.activity;

import android.app.AlertDialog;
import android.app.DownloadManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.CursorIndexOutOfBoundsException;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.BlackTech.allVideostatus2020.ObservableLayer.HomeVideoViewModel;
import com.BlackTech.allVideostatus2020.R;
import com.BlackTech.allVideostatus2020.Utility.Config;
import com.BlackTech.allVideostatus2020.Utility.LanguageSelectore;
import com.BlackTech.allVideostatus2020.adapter.VideoSubItemAdapter;
import com.BlackTech.allVideostatus2020.getSet.videoListGetSet;
import com.BlackTech.allVideostatus2020.models.Searchdata;
import com.BlackTech.allVideostatus2020.models.SelectCategory;
import com.BlackTech.allVideostatus2020.models.Videocategory;
import com.BlackTech.allVideostatus2020.onClickListners.OnLoadListeners;
import com.BlackTech.allVideostatus2020.onClickListners.onVideoListClick;
import com.BlackTech.allVideostatus2020.restapiclient.ApiInterface;
import com.BlackTech.allVideostatus2020.restapiclient.RestClient;
import com.android.volley.NetworkResponse;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.facebook.appevents.AppEventsConstants;
import com.facebook.messenger.ShareToMessengerParams;
import com.facebook.share.internal.MessengerShareContentUtility;
import com.facebook.share.model.ShareVideo;
import com.facebook.share.model.ShareVideoContent;
import com.facebook.share.widget.ShareDialog;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.mediacodec.MediaCodecRenderer;
import com.google.android.exoplayer2.mediacodec.MediaCodecUtil;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.ErrorMessageProvider;
import com.google.android.exoplayer2.util.MimeTypes;
import com.google.android.exoplayer2.util.Util;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;

public class VideoDetailActivity extends AppCompatActivity implements View.OnClickListener {
    ProgressDialog dialog;
    public ArrayList<Videocategory> dataSuggested = new ArrayList();
    public View detail_toolbar;
    public DownloadManager downloadManager;
    public File file;
    public VideoSubItemAdapter horizontalAdapter;
    public boolean isFacebook;
    public boolean isInstagram;
    public boolean isMessanger;
    public boolean isShare;
    public boolean isTwitter;
    public boolean isWhatsApp;
    private ImageButton btn_cancelDownload;
    private ImageView btn_download;
    private ImageView btn_fb;
    private ImageView btn_what;
    private ImageView btn_sh;
    private ImageView btn_twit;
    private ImageView btn_insta;
    private ImageView btn_mess;
    private ImageView btn_show;
    private TextView thank;
    private int pageNumber;
    private SimpleExoPlayer player;
    private PlayerView playerView;
    public ProgressBar progressBar;
    public RelativeLayout rel_showProgress;
    public TextView tv_downloads;
    public TextView txt_fileSize;
    public TextView txt_progress;
    public TextView tv_views;
    public TextView tv_filename;
    public TextView tv_category;
    public videoListGetSet videoData;
    private String videoPath;
    public final ArrayList<Long> list = new ArrayList<>();
    private RecyclerView list_extraVideo;
    private final int numberOfRecords = 4;
    private AdView mAdView;
    private InterstitialAd mInterstitialAd;
    private Videocategory str_video;
    private SelectCategory str_selevideo;
    private Searchdata str_searchvideo;
    public HomeVideoViewModel homeVideoViewModel;
    public BroadcastReceiver onDownloadComplete;
    public String Img_title;
    String flug_str = "";
    Uri contentURL;
    File outputFile = null;

//    SimpleExoPlayer player;


    private final BroadcastReceiver onComplete = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
//            MediaScannerConnection.scanFile(context, new String[]{VideoDetailActivity.this.file.toString()}, null, new MediaScannerConnection.OnScanCompletedListener() {
//                public void onScanCompleted(String str, Uri uri) {
//                    Log.i("ExternalStorage", "Scanned " + str + ":");
//                    StringBuilder sb = new StringBuilder();
//                    sb.append("-> uri=");
//                    sb.append(uri);
//                    Log.i("ExternalStorage", sb.toString());
//                }
//            });
            long longExtra = intent.getLongExtra("extra_download_id", -1);
            Log.e("IN", "" + longExtra);
            VideoDetailActivity.this.list.remove(Long.valueOf(longExtra));
            if (VideoDetailActivity.this.list.isEmpty()) {
                Log.e("INSIDE", "" + longExtra);
                Notification.Builder contentText = new Notification.Builder(VideoDetailActivity.this).setSmallIcon(R.drawable.icon_start).setContentTitle(VideoDetailActivity.this.getResources().getString(R.string.app_name)).setContentText(VideoDetailActivity.this.getResources().getString(R.string.all_download_complete));
                NotificationManager notificationManager = (NotificationManager) VideoDetailActivity.this.getSystemService(NOTIFICATION_SERVICE);
                if (notificationManager != null) {
                    notificationManager.notify(455, contentText.build());
                }
            }
        }
    };

    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        LanguageSelectore.setLanguageIfAR(this);
        setContentView(R.layout.activity_videodetails);
        this.homeVideoViewModel = new ViewModelProvider(this).get(HomeVideoViewModel.class);
        getIntents();
        initViews();
        if (MainActivity.isNetworkConnected(this)) {
            setUpVideoList();
            updateView();
        } else {
            MainActivity.showErrorDialog(this);
        }
        startDownloadingForSharing();
    }

    private void updateView() {
        String img_title;
        ApiInterface apiInterface = RestClient.getRetrofit().create(ApiInterface.class);
//        Call<ArrayList<Object>> call = apiInterface.UpdateView(str_video.Img_title);
        if(flug_str.equalsIgnoreCase("1"))img_title=str_selevideo.Img_title;
        else img_title = str_video.Img_title;
        Call<ArrayList<Object>> call = apiInterface.UpdateView(img_title);
        call.enqueue(new Callback<ArrayList<Object>>() {
            @Override
            public void onResponse(Call<ArrayList<Object>> call, retrofit2.Response<ArrayList<Object>> response) {
               // Toast.makeText(getApplicationContext(),"pass",Toast.LENGTH_LONG).show();
                try {
                   JSONArray jsonArray = new JSONArray(response.body());
                    JSONObject jsonObject = jsonArray.getJSONObject(0);
                   // Toast.makeText(getApplicationContext(),jsonObject.getString("View"),Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                    Toast.makeText(getApplicationContext(),"Fail",Toast.LENGTH_SHORT).show();
                }
                //tv_views.setText();
            }

            @Override
            public void onFailure(Call<ArrayList<Object>> call, Throwable t) {
                Log.e("APIFAIL", "onFailure: "+t.getLocalizedMessage() );
                Toast.makeText(getApplicationContext(),"failure"+t.toString(),Toast.LENGTH_LONG).show();
            }
        });
    }


    private void initViews() {
        tv_filename = findViewById(R.id.tv_video_title);
        tv_category = findViewById(R.id.tv_video_subcat);
        tv_views = findViewById(R.id.tv_num_views);
        this.tv_downloads = findViewById(R.id.tv_downloads);
        this.list_extraVideo = findViewById(R.id.list_extraVideo);
        this.btn_download = findViewById(R.id.btn_download);
        this.btn_what = findViewById(R.id.btn_whatsapp);
        this.btn_sh = findViewById(R.id.btn_share);
        this.btn_twit = findViewById(R.id.btn_twitter);
        this.btn_mess = findViewById(R.id.btn_messanger);
        this.btn_insta = findViewById(R.id.btn_instagram);
        this.btn_fb = findViewById(R.id.btn_facebook);
        this.progressBar = findViewById(R.id.progressBarHorizontal);
        this.thank = findViewById(R.id.thanks);
        this.txt_progress = findViewById(R.id.txt_progress);
        this.txt_fileSize = findViewById(R.id.txt_fileSize);
        this.btn_cancelDownload = findViewById(R.id.btn_cancelDownload);
        this.rel_showProgress = findViewById(R.id.rel_showProgress);
        this.detail_toolbar = findViewById(R.id.detail_toolbar);
        this.tv_downloads.setTypeface(MainActivity.main_medium);
        this.btn_download.setOnClickListener(this);
        findViewById(R.id.btn_facebook).setOnClickListener(this);
        findViewById(R.id.btn_twitter).setOnClickListener(this);
        findViewById(R.id.btn_messanger).setOnClickListener(this);
        findViewById(R.id.btn_instagram).setOnClickListener(this);
        findViewById(R.id.btn_share).setOnClickListener(this);
        findViewById(R.id.btn_whatsapp).setOnClickListener(this);
        this.downloadManager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
        registerReceiver(this.onComplete, new IntentFilter("android.intent.action.DOWNLOAD_COMPLETE"));

//        this.videoPath = Config.BASE_URL+str_video.video_name;
        changeBar();
        this.playerView = findViewById(R.id.video_player);
        playerView.setUseController(true);
//        this.playerView.setErrorMessageProvider(new PlayerErrorMessageProvider());
        this.playerView.requestFocus();

        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });

        /*AdView adView = (AdView)this.findViewById(R.id.ad_view1);
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);
*/
        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId("ca-app-pub-3149366996577725/5814837459");
        mInterstitialAd.loadAd(new AdRequest.Builder().build());
    }

    private void getIntents() {
        this.videoData = new videoListGetSet();
        Intent intent = getIntent();
        String stringExtra = intent.getStringExtra("videoTitle");
        String stringExtra2 = intent.getStringExtra("videoId");
        String stringExtra3 = intent.getStringExtra("videoCategory");
        String stringExtra4 = intent.getStringExtra("videoSubCategory");
        String stringExtra5 = intent.getStringExtra("videoDownload");
        String stringExtra6 = intent.getStringExtra("videoImage");
        String stringExtra7 = intent.getStringExtra("videoView");
        String stringExtra8 = intent.getStringExtra("videoCategoryId");
        String stringExtra9 = intent.getStringExtra("videoSubCategoryId");
        String stringExtra10 = intent.getStringExtra("id");
        this.videoData.setVideoFileName(stringExtra2);
        this.videoData.setVideo_title(stringExtra);
        this.videoData.setVideo_subCat_id(stringExtra9);
        this.videoData.setVideo_cat_id(stringExtra8);
        this.videoData.setVideo_subcategory(stringExtra4);
        this.videoData.setVideo_category(stringExtra3);
        this.videoData.setVideoDownload(stringExtra5);
        this.videoData.setVideoImage(stringExtra6);
        this.videoData.setVideoView(stringExtra7);
        this.videoData.setId(stringExtra10);
        Log.e("Video data service ", "getIntents: "+this.videoData.getVideo_title() );
    }

//    public void onNewIntent(Intent intent) {
//        super.onNewIntent(intent);
//        releasePlayer();
//        setIntent(intent);
//    }

    private void releasePlayer() {
        SimpleExoPlayer simpleExoPlayer = this.player;
        if (simpleExoPlayer != null) {
            simpleExoPlayer.release();
            this.player = null;
        }
    }


    public void onStart() {
        super.onStart();
        if (Util.SDK_INT > 23) {
        }
    }

    public void onStop() {
        super.onStop();
        if (Util.SDK_INT > 23) {
            releasePlayer();
        }
    }


    public void onResume() {
        super.onResume();
        if (Util.SDK_INT <= 23 || this.player == null) {
//            initializePlayer();
        }
    }


    public void changeBar() {

        this.btn_download.setImageResource(R.drawable.icon_download_on);


//        if (Boolean.valueOf(hasCurrentVideo()).booleanValue()) {
//            this.btn_download.setImageResource(R.drawable.icon_donwload_off);
//        } else {
//            this.btn_download.setImageResource(R.drawable.icon_download_on);
//        }
    }

    public void updateUI(ArrayList<Videocategory> str_video) {
        onVideoListClick r0 = new onVideoListClick() {
            public void onItemClick(View view, int i) {
                VideoDetailActivity videoDetailActivity = VideoDetailActivity.this;
                videoDetailActivity.increaseViewOfItem(str_video, i);

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

        this.list_extraVideo.setLayoutManager(new GridLayoutManager(this, 2, RecyclerView.VERTICAL, false));
        this.horizontalAdapter = new VideoSubItemAdapter(str_video, this, r0, this.list_extraVideo);
        this.list_extraVideo.setAdapter(this.horizontalAdapter);
        this.horizontalAdapter.setOnLoadMoreListener(new OnLoadListeners() {
            public void onLoadMore() {
//                VideoDetailActivity.this.progressBar.setVisibility(View.VISIBLE);
                VideoDetailActivity.this.getMoreDataFromServer();
            }
        });
    }

    private void setUpVideoList() {
        final ProgressBar progressBar2 = findViewById(R.id.progressBar);
        progressBar2.setVisibility(View.VISIBLE);
        Intent intent = getIntent();
        flug_str = intent.getStringExtra("flag");
        Log.e("check_flug_button", flug_str);
        if (flug_str.equalsIgnoreCase("0")) {
            str_video = (Videocategory) getIntent().getSerializableExtra("videoserilizeable");
            Log.e("getVideodata", str_video.Img_title);
            this.videoPath = Config.BASE_URL + str_video.video_name;
            TrackSelector trackSelectorDef = new DefaultTrackSelector();
            player = ExoPlayerFactory.newSimpleInstance(this, trackSelectorDef); //creating a player instance
            String userAgent = Util.getUserAgent(this, this.getString(R.string.app_name));
            DefaultDataSourceFactory defdataSourceFactory = new DefaultDataSourceFactory(this, userAgent);
            Uri uriOfContentUrl = Uri.parse(Config.BASE_URL + str_video.video_name);
            this.contentURL = uriOfContentUrl;
            MediaSource mediaSource = new ExtractorMediaSource.Factory(defdataSourceFactory).createMediaSource(uriOfContentUrl);  // creating a media source
            player.prepare(mediaSource, false, false);
            player.setPlayWhenReady(true); // start loading video and play it at the moment a chunk of it is available offline
            player.setVolume(7f);
            playerView.setPlayer(player);
            homeVideoViewModel.getVideocategorylist().observe(this, new Observer<ArrayList<Videocategory>>() {
                @Override
                public void onChanged(ArrayList<Videocategory> videocategories) {
                    if (videocategories != null) {
//                    MainActivity.this.updateUI(videocategories);
                        Log.e("getVideodata", videocategories.toString());
                        progressBar2.setVisibility(View.INVISIBLE);
                        VideoDetailActivity.this.updateUI(videocategories);

                    }
                }
            });
            tv_filename.setText(this.str_video.video_name);
            tv_category.setText(this.str_video.Img_title+" - "+this.str_video.Category);
            tv_views.setText(this.str_video.View + " " + getString(R.string.txt_views));
        }

        if (flug_str.equalsIgnoreCase("1")) {
            str_selevideo = (SelectCategory) getIntent().getSerializableExtra("videoserilizeable");
            Log.e("getVideodata", str_selevideo.Category);
            this.videoPath = Config.BASE_URL + str_selevideo.video_name;
            TrackSelector trackSelectorDef = new DefaultTrackSelector();
            player = ExoPlayerFactory.newSimpleInstance(this, trackSelectorDef); //creating a player instance
            String userAgent = Util.getUserAgent(this, this.getString(R.string.app_name));
            DefaultDataSourceFactory defdataSourceFactory = new DefaultDataSourceFactory(this, userAgent);
            Uri uriOfContentUrl = Uri.parse(Config.BASE_URL + str_selevideo.video_name);
            this.contentURL = uriOfContentUrl;
            MediaSource mediaSource = new ExtractorMediaSource.Factory(defdataSourceFactory).createMediaSource(uriOfContentUrl);  // creating a media source
            player.prepare(mediaSource, false, false);
            player.setPlayWhenReady(true); // start loading video and play it at the moment a chunk of it is available offline
            player.setVolume(7f);
            playerView.setPlayer(player);
            homeVideoViewModel.getVideocategorylist().observe(this, new Observer<ArrayList<Videocategory>>() {
                @Override
                public void onChanged(ArrayList<Videocategory> videocategories) {
                    if (videocategories != null) {
//                    MainActivity.this.updateUI(videocategories);
                        progressBar2.setVisibility(View.INVISIBLE);
                        Log.e("getVideodata", videocategories.toString());
                        VideoDetailActivity.this.updateUI(videocategories);

                    }
                }
            });
            tv_filename.setText(this.str_selevideo.video_name);
            tv_category.setText(this.str_selevideo.Img_title+" - "+this.str_selevideo.Category);
            tv_views.setText(this.str_selevideo.View + " " + getString(R.string.txt_views));
        }

        if (flug_str.equalsIgnoreCase("2")) {
            str_searchvideo = (Searchdata) getIntent().getSerializableExtra("videoserilizeable");
            Log.e("getVideodata", str_searchvideo.video_name);
            this.videoPath = Config.BASE_URL + str_searchvideo.video_name;
            TrackSelector trackSelectorDef = new DefaultTrackSelector();
            player = ExoPlayerFactory.newSimpleInstance(this, trackSelectorDef); //creating a player instance
            String userAgent = Util.getUserAgent(this, this.getString(R.string.app_name));
            DefaultDataSourceFactory defdataSourceFactory = new DefaultDataSourceFactory(this, userAgent);
            Uri uriOfContentUrl = Uri.parse(Config.BASE_URL + str_searchvideo.video_name);
            this.contentURL = uriOfContentUrl;
            MediaSource mediaSource = new ExtractorMediaSource.Factory(defdataSourceFactory).createMediaSource(uriOfContentUrl);  // creating a media source
            player.prepare(mediaSource, false, false);
            player.setPlayWhenReady(true); // start loading video and play it at the moment a chunk of it is available offline
            player.setVolume(7f);
            playerView.setPlayer(player);
//            player.setPlayWhenReady(true);
            homeVideoViewModel.getVideocategorylist().observe(this, new Observer<ArrayList<Videocategory>>() {
                @Override
                public void onChanged(ArrayList<Videocategory> videocategories) {
                    if (videocategories != null) {
//                    MainActivity.this.updateUI(videocategories);
                        progressBar2.setVisibility(View.INVISIBLE);
                        Log.e("getVideodata", videocategories.toString());
                        VideoDetailActivity.this.updateUI(videocategories);

                    }
                }
            });
            tv_filename.setText(this.str_searchvideo.video_name);
            tv_category.setText(this.str_searchvideo.Category);
            tv_views.setText(this.videoData.getVideoView() + " " + getString(R.string.txt_views));


        }


//        initializePlayer();


//        String replace = (getString(R.string.link) + "api/video_list.php?category=" + this.videoData.getVideo_cat_id() + "&subcategory=" + this.videoData.getVideo_subCat_id() + "&sort_by=" + getSharedPreferences(MainActivity.prefName, 0).getString("orderBy", "asc") + "&noofrecords=" + 4 + "&pageno=" + this.pageNumber).replace(" ", "%20");
//        StringBuilder sb = new StringBuilder();
//        sb.append("");
//        sb.append(replace);
//        Log.d("checkUrl", sb.toString());


//        Volley.newRequestQueue(this).add(new StringRequest(0, replace, new Response.Listener<String>() {
//            public void onResponse(String str) {
//                progressBar2.setVisibility(View.INVISIBLE);
//                Log.e("Response", str);
//                try {
//                    JSONObject jSONObject = new JSONObject(str);
//                    if (Objects.equals(jSONObject.getString("success"), AppEventsConstants.EVENT_PARAM_VALUE_YES)) {
//                        ArrayList unused = VideoDetailActivity.this.dataSuggested = new ArrayList();
//                        JSONArray jSONArray = jSONObject.getJSONArray("video");
//                        for (int i = 0; i < jSONArray.length(); i++) {
//                            videoListGetSet videolistgetset = new videoListGetSet();
//                            JSONObject jSONObject2 = jSONArray.getJSONObject(i);
//                            String string = jSONObject2.getString("video_category");
//                            String string2 = jSONObject2.getString("video_subcategory");
//                            String string3 = jSONObject2.getString("video_title");
//                            String string4 = jSONObject2.getString("video");
//                            String string5 = jSONObject2.getString(MessengerShareContentUtility.MEDIA_IMAGE);
//                            String string6 = jSONObject2.getString("view");
//                            String string7 = jSONObject2.getString("download");
//                            String string8 = jSONObject2.getString("id");
//                            videolistgetset.setVideo_cat_id(jSONObject2.getString("cat_id"));
//                            videolistgetset.setVideo_subCat_id(jSONObject2.getString("subcat_id"));
//                            videolistgetset.setVideo_title(string3);
//                            videolistgetset.setVideo_category(string);
//                            videolistgetset.setVideo_subcategory(string2);
//                            videolistgetset.setVideoDownload(string7);
//                            videolistgetset.setVideoImage(string5);
//                            videolistgetset.setVideoView(string6);
//                            videolistgetset.setVideoFileName(string4);
//                            videolistgetset.setId(string8);
//                            if (!string3.equals(VideoDetailActivity.this.videoData.getVideo_title())) {
//                                VideoDetailActivity.this.dataSuggested.add(videolistgetset);
//                            }
//                        }
//                        VideoDetailActivity.this.updateUI();
//                        return;
//                    }
//                    Toast.makeText(VideoDetailActivity.this, (int) R.string.error, Toast.LENGTH_SHORT).show();
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//            }
//        }, new Response.ErrorListener() {
//            public void onErrorResponse(VolleyError volleyError) {
//                Toast.makeText(VideoDetailActivity.this.getApplicationContext(), volleyError.getMessage(), Toast.LENGTH_SHORT).show();
//            }
//        }));
    }


    public void getMoreDataFromServer() {
        this.pageNumber++;
        String replace = (getString(R.string.link) + "api/video_list.php?category=" + this.videoData.getVideo_cat_id() + "&subcategory=" + this.videoData.getVideo_subCat_id() + "&sort_by=" + getSharedPreferences(MainActivity.prefName, 0).getString("orderBy", "asc") + "&noofrecords=" + 4 + "&pageno=" + this.pageNumber).replace(" ", "%20");
        Log.w(getClass().getName(), replace);
        Volley.newRequestQueue(getApplication()).add(new StringRequest(0, replace, new Response.Listener<String>() {
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
                            if (!string4.equals(VideoDetailActivity.this.videoData.getVideo_title())) {
                                arrayList.add(videolistgetset);
                            }
                        }
                        VideoDetailActivity.this.dataSuggested.addAll(arrayList);
                        VideoSubItemAdapter.setLoaded(false);
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
                    Toast.makeText(VideoDetailActivity.this.getApplication(), String.valueOf(networkResponse.statusCode), Toast.LENGTH_SHORT).show();
                }
            }
        }));

    }

    public void shareToWhatsApp() {
//           str_video = (SelectCategory) getIntent().getSerializableExtra("videoserilizeable");
//         str_video = (Searchdata) getIntent().getSerializableExtra("videoserilizeable");

        if (flug_str.equalsIgnoreCase("2")) {
            str_searchvideo = (Searchdata) getIntent().getSerializableExtra("videoserilizeable");
            Log.e("check_video_url", "------------------------------------" + Config.BASE_URL + str_searchvideo.video_name);

            File searchoutputFile = new File(Environment.getExternalStoragePublicDirectory
                    (Environment.DIRECTORY_DOWNLOADS), "searchmyfile.mp4");
            try {
                DownloadManager downloadmanager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
                Uri uri = Uri.parse(Config.BASE_URL + str_searchvideo.video_name);
                Log.e("check_video_url", "------------------------------------" + Config.BASE_URL + str_searchvideo.video_name);

                DownloadManager.Request request = new DownloadManager.Request(this.contentURL);
                request.setTitle("My File");
                request.setDescription("Downloading");
                request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                request.setVisibleInDownloadsUi(false);
                Uri urddi = Uri.fromFile(searchoutputFile);
                request.setDestinationUri(urddi);
                downloadmanager.enqueue(request);

            } catch (Exception e) {
                e.printStackTrace();

            }
            Intent intent = new Intent("android.intent.action.SEND");
            intent.setType("video/*");
            Uri uriForFile = FileProvider.getUriForFile(this, getString(R.string.authority), searchoutputFile);
            intent.setPackage("com.whatsapp");
            intent.putExtra("android.intent.extra.STREAM", uriForFile);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            if (intent.resolveActivity(getPackageManager()) != null) {
                startActivity(intent);
                startActivity(Intent.createChooser(intent, "Share to"));
                return;
            }
            alertForApp(getString(R.string.install_whatsapp), "com.whatsapp");


        } else if (flug_str.equalsIgnoreCase("1")) {
            str_selevideo = (SelectCategory) getIntent().getSerializableExtra("videoserilizeable");
            outputFile = new File(Environment.getExternalStoragePublicDirectory
                    (Environment.DIRECTORY_DOWNLOADS), "myfile.mp4");
            //    outputFile.mkdir();
            try {
                //       outputFile.createNewFile();
                if (!outputFile.exists()) {
                    DownloadManager downloadmanager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
                    Uri uri = Uri.parse(Config.BASE_URL + str_selevideo.video_name);
                    DownloadManager.Request request = new DownloadManager.Request(this.contentURL);
                    request.setTitle("My File");
                    request.setDescription("Downloading");
                    request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                    request.setVisibleInDownloadsUi(false);
                    Uri urddi = Uri.fromFile(outputFile);
                    request.setDestinationUri(urddi);
                    downloadmanager.enqueue(request);
                    Log.e("check_video_file", "------------------------------------" + urddi);
                }
            } catch (Exception e) {
                e.printStackTrace();

            }
            Intent intent = new Intent("android.intent.action.SEND");
            intent.setType("video/*");
            Uri uriForFile = FileProvider.getUriForFile(this, getString(R.string.authority), outputFile);
            intent.setPackage("com.whatsapp");
            intent.putExtra("android.intent.extra.STREAM", uriForFile);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            if (intent.resolveActivity(getPackageManager()) != null) {
                startActivity(intent);
                startActivity(Intent.createChooser(intent, "Share to"));
                return;
            }
            alertForApp(getString(R.string.install_whatsapp), "com.whatsapp");

        } else if (flug_str.equalsIgnoreCase("0")) {

            str_video = (Videocategory) getIntent().getSerializableExtra("videoserilizeable");
            outputFile = new File(Environment.getExternalStoragePublicDirectory
                    (Environment.DIRECTORY_DOWNLOADS), "myfile.mp4");
            //  outputFile.mkdir();
            try {
                //        outputFile.createNewFile();
                if (!outputFile.exists()) {
                    DownloadManager downloadmanager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
                    Uri uri = Uri.parse(Config.BASE_URL + str_video.video_name);
                    DownloadManager.Request request = new DownloadManager.Request(this.contentURL);
                    request.setTitle("My File");
                    request.setDescription("Downloading");
                    request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                    request.setVisibleInDownloadsUi(false);
                    Uri urddi = Uri.fromFile(outputFile);
                    request.setDestinationUri(urddi);
                    downloadmanager.enqueue(request);
                    Log.e("check_video_file", "------------------------------------" + urddi);
                }
            } catch (Exception e) {
                e.printStackTrace();

            }
            Intent intent = new Intent("android.intent.action.SEND");
            intent.setType("video/*");
            Uri uriForFile = FileProvider.getUriForFile(this, getString(R.string.authority), outputFile);
            intent.setPackage("com.whatsapp");
            intent.putExtra("android.intent.extra.STREAM", uriForFile);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            if (intent.resolveActivity(getPackageManager()) != null) {
                startActivity(intent);
                startActivity(Intent.createChooser(intent, "Share to"));
                return;
            }
            alertForApp(getString(R.string.install_whatsapp), "com.whatsapp");
        }
    }

    public void shareToFB() {
//           str_video = (SelectCategory) getIntent().getSerializableExtra("videoserilizeable");
//         str_video = (Searchdata) getIntent().getSerializableExtra("videoserilizeable");

        if (flug_str.equalsIgnoreCase("2")) {
            str_searchvideo = (Searchdata) getIntent().getSerializableExtra("videoserilizeable");
            Log.e("check_video_url", "------------------------------------" + Config.BASE_URL + str_searchvideo.video_name);

            File searchoutputFile = new File(Environment.getExternalStoragePublicDirectory
                    (Environment.DIRECTORY_DOWNLOADS), "searchmyfile.mp4");
            try {
                DownloadManager downloadmanager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
                Uri uri = Uri.parse(Config.BASE_URL + str_searchvideo.video_name);
                Log.e("check_video_url", "------------------------------------" + Config.BASE_URL + str_searchvideo.video_name);

                DownloadManager.Request request = new DownloadManager.Request(this.contentURL);
                request.setTitle("My File");
                request.setDescription("Downloading");
                request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                request.setVisibleInDownloadsUi(false);
                Uri urddi = Uri.fromFile(searchoutputFile);
                request.setDestinationUri(urddi);
                downloadmanager.enqueue(request);

            } catch (Exception e) {
                e.printStackTrace();

            }
            Intent intent = new Intent("android.intent.action.SEND");
            intent.setType("video/*");
            Uri uriForFile = FileProvider.getUriForFile(this, getString(R.string.authority), searchoutputFile);
            intent.setPackage("com.facebook.katana");
            intent.putExtra("android.intent.extra.STREAM", uriForFile);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            if (intent.resolveActivity(getPackageManager()) != null) {
                startActivity(intent);
                startActivity(Intent.createChooser(intent, "Share to"));
                return;
            }
            alertForApp(getString(R.string.install_facebook), "com.facebook.katana");


        } else if (flug_str.equalsIgnoreCase("1")) {
            str_selevideo = (SelectCategory) getIntent().getSerializableExtra("videoserilizeable");
            outputFile = new File(Environment.getExternalStoragePublicDirectory
                    (Environment.DIRECTORY_DOWNLOADS), "myfile.mp4");
            // outputFile.mkdir();
            try {
                //  outputFile.createNewFile();
                if (!outputFile.exists()) {
                    DownloadManager downloadmanager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
                    Uri uri = Uri.parse(Config.BASE_URL + str_selevideo.video_name);
                    DownloadManager.Request request = new DownloadManager.Request(this.contentURL);
                    request.setTitle("My File");
                    request.setDescription("Downloading");
                    request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                    request.setVisibleInDownloadsUi(false);
                    Uri urddi = Uri.fromFile(outputFile);
                    request.setDestinationUri(urddi);
                    downloadmanager.enqueue(request);
                    Log.e("check_video_file", "------------------------------------" + urddi);
                }
            } catch (Exception e) {
                e.printStackTrace();

            }
            Intent intent = new Intent("android.intent.action.SEND");
            intent.setType("video/*");
            Uri uriForFile = FileProvider.getUriForFile(this, getString(R.string.authority), outputFile);
            intent.setPackage("com.facebook.katana");
            intent.putExtra("android.intent.extra.STREAM", uriForFile);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            if (intent.resolveActivity(getPackageManager()) != null) {
                startActivity(intent);
                startActivity(Intent.createChooser(intent, "Share to"));
                return;
            }
            alertForApp(getString(R.string.install_facebook), "com.facebook.katana");

        } else if (flug_str.equalsIgnoreCase("0")) {

            str_video = (Videocategory) getIntent().getSerializableExtra("videoserilizeable");
            outputFile = new File(Environment.getExternalStoragePublicDirectory
                    (Environment.DIRECTORY_DOWNLOADS), "myfile.mp4");
            // outputFile.mkdir();
            try {
                //    outputFile.createNewFile();
                if (!outputFile.exists()) {
                    DownloadManager downloadmanager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
                    Uri uri = Uri.parse(Config.BASE_URL + str_video.video_name);
                    DownloadManager.Request request = new DownloadManager.Request(this.contentURL);
                    request.setTitle("My File");
                    request.setDescription("Downloading");
                    request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                    request.setVisibleInDownloadsUi(false);
                    Uri urddi = Uri.fromFile(outputFile);
                    request.setDestinationUri(urddi);
                    downloadmanager.enqueue(request);
                    Log.e("check_video_file", "------------------------------------" + urddi);
                }
            } catch (Exception e) {
                e.printStackTrace();

            }
            Intent intent = new Intent("android.intent.action.SEND");
            intent.setType("video/*");
            Uri uriForFile = FileProvider.getUriForFile(this, getString(R.string.authority), outputFile);
            intent.setPackage("com.facebook.katana");
            intent.putExtra("android.intent.extra.STREAM", uriForFile);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            if (intent.resolveActivity(getPackageManager()) != null) {
                startActivity(intent);
                startActivity(Intent.createChooser(intent, "Share to"));
                return;
            }
            alertForApp(getString(R.string.install_facebook), "com.facebook.katana");
        }
    }

    public void shareToMessanger() {
//           str_video = (SelectCategory) getIntent().getSerializableExtra("videoserilizeable");
//         str_video = (Searchdata) getIntent().getSerializableExtra("videoserilizeable");

        if (flug_str.equalsIgnoreCase("2")) {
            str_searchvideo = (Searchdata) getIntent().getSerializableExtra("videoserilizeable");
            Log.e("check_video_url", "------------------------------------" + Config.BASE_URL + str_searchvideo.video_name);

            File searchoutputFile = new File(Environment.getExternalStoragePublicDirectory
                    (Environment.DIRECTORY_DOWNLOADS), "searchmyfile.mp4");
            try {
                DownloadManager downloadmanager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
                Uri uri = Uri.parse(Config.BASE_URL + str_searchvideo.video_name);
                Log.e("check_video_url", "------------------------------------" + Config.BASE_URL + str_searchvideo.video_name);

                DownloadManager.Request request = new DownloadManager.Request(uri);
                request.setTitle("My File");
                request.setDescription("Downloading");
                request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                request.setVisibleInDownloadsUi(false);
                Uri urddi = Uri.fromFile(searchoutputFile);
                request.setDestinationUri(urddi);
                downloadmanager.enqueue(request);

            } catch (Exception e) {
                e.printStackTrace();

            }
            Intent intent = new Intent("android.intent.action.SEND");
            intent.setType("video/*");
            Uri uriForFile = FileProvider.getUriForFile(this, getString(R.string.authority), searchoutputFile);
            intent.setPackage("com.facebook.orca");
            intent.putExtra("android.intent.extra.STREAM", uriForFile);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            if (intent.resolveActivity(getPackageManager()) != null) {
                startActivity(intent);
                startActivity(Intent.createChooser(intent, "Share to"));
                return;
            }
            alertForApp(getString(R.string.install_Messenger), "com.facebook.orca");


        } else if (flug_str.equalsIgnoreCase("1")) {
            str_selevideo = (SelectCategory) getIntent().getSerializableExtra("videoserilizeable");
            outputFile = new File(Environment.getExternalStoragePublicDirectory
                    (Environment.DIRECTORY_DOWNLOADS), "myfile.mp4");
            //    outputFile.mkdir();
            try {
                //  outputFile.createNewFile();
                if (!outputFile.exists()) {
                    DownloadManager downloadmanager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
                    Uri uri = Uri.parse(Config.BASE_URL + str_selevideo.video_name);
                    DownloadManager.Request request = new DownloadManager.Request(this.contentURL);
                    request.setTitle("My File");
                    request.setDescription("Downloading");
                    request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                    request.setVisibleInDownloadsUi(false);
                    Uri urddi = Uri.fromFile(outputFile);
                    request.setDestinationUri(urddi);
                    downloadmanager.enqueue(request);
                    Log.e("check_video_file", "------------------------------------" + urddi);
                }
            } catch (Exception e) {
                e.printStackTrace();

            }
            Intent intent = new Intent("android.intent.action.SEND");
            intent.setType("video/*");
            Uri uriForFile = FileProvider.getUriForFile(this, getString(R.string.authority), outputFile);
            intent.setPackage("com.facebook.orca");
            intent.putExtra("android.intent.extra.STREAM", uriForFile);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            if (intent.resolveActivity(getPackageManager()) != null) {
                startActivity(intent);
                startActivity(Intent.createChooser(intent, "Share to"));
                return;
            }
            alertForApp(getString(R.string.install_Messenger), "com.facebook.orca");

        } else if (flug_str.equalsIgnoreCase("0")) {

            str_video = (Videocategory) getIntent().getSerializableExtra("videoserilizeable");
            outputFile = new File(Environment.getExternalStoragePublicDirectory
                    (Environment.DIRECTORY_DOWNLOADS), "myfile.mp4");
            //   outputFile.mkdir();
            try {
                //    outputFile.createNewFile();
                if (!outputFile.exists()) {
                    DownloadManager downloadmanager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
                    Uri uri = Uri.parse(Config.BASE_URL + str_video.video_name);
                    DownloadManager.Request request = new DownloadManager.Request(this.contentURL);
                    request.setTitle("My File");
                    request.setDescription("Downloading");
                    request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                    request.setVisibleInDownloadsUi(false);
                    Uri urddi = Uri.fromFile(outputFile);
                    request.setDestinationUri(urddi);
                    downloadmanager.enqueue(request);
                    Log.e("check_video_file", "------------------------------------" + urddi);
                }
            } catch (Exception e) {
                e.printStackTrace();

            }
            Intent intent = new Intent("android.intent.action.SEND");
            intent.setType("video/*");
            Uri uriForFile = FileProvider.getUriForFile(this, getString(R.string.authority), outputFile);
            intent.setPackage("com.facebook.orca");
            intent.putExtra("android.intent.extra.STREAM", uriForFile);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            if (intent.resolveActivity(getPackageManager()) != null) {
                startActivity(intent);
                startActivity(Intent.createChooser(intent, "Share to"));
                return;
            }
            alertForApp(getString(R.string.install_Messenger), "com.facebook.orca");
        }
    }


    public void shareToDefault() {

        if (flug_str.equalsIgnoreCase("2")) {
            Toast.makeText(this, "SHARING File 1", Toast.LENGTH_SHORT).show();
            str_searchvideo = (Searchdata) getIntent().getSerializableExtra("videoserilizeable");
            outputFile = new File(Environment.getExternalStoragePublicDirectory
                    (Environment.DIRECTORY_DOWNLOADS), "myfile.mp4");
            //     outputFile.mkdir();
            try {
                //   outputFile.createNewFile();
                if (!outputFile.exists()) {
                    DownloadManager downloadmanager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
                    Uri uri = Uri.parse(Config.BASE_URL + str_searchvideo.video_name);
                    DownloadManager.Request request = new DownloadManager.Request(this.contentURL);
                    request.setTitle("My File");
                    request.setDescription("Downloading");
                    request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                    request.setVisibleInDownloadsUi(false);
                    Uri urddi = Uri.fromFile(outputFile);
                    request.setDestinationUri(urddi);
                    downloadmanager.enqueue(request);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            Intent intent = new Intent("android.intent.action.SEND");
            intent.setType("video/*");
            intent.putExtra("android.intent.extra.STREAM", FileProvider.getUriForFile(this, getString(R.string.authority), outputFile));
            if (intent.resolveActivity(getPackageManager()) != null) {
                startActivity(intent);
                startActivity(Intent.createChooser(intent, "Share to"));
                return;
            }
            Toast.makeText(this, "try_later", Toast.LENGTH_SHORT).show();


        } else if (flug_str.equalsIgnoreCase("1")) {
            Toast.makeText(this, "SHARING File 2", Toast.LENGTH_SHORT).show();
            str_selevideo = (SelectCategory) getIntent().getSerializableExtra("videoserilizeable");
            outputFile = new File(Environment.getExternalStoragePublicDirectory
                    (Environment.DIRECTORY_DOWNLOADS), "myfile.mp4");
            //   outputFile.mkdir();
            try {
                //    outputFile.createNewFile();

                if (!outputFile.exists()) {
                    DownloadManager downloadmanager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
                    Uri uri = Uri.parse(Config.BASE_URL + str_selevideo.video_name);
                    DownloadManager.Request request = new DownloadManager.Request(this.contentURL);
                    request.setTitle("My File");
                    request.setDescription("Downloading");
                    request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                    request.setVisibleInDownloadsUi(false);
                    Uri urddi = Uri.fromFile(outputFile);
                    request.setDestinationUri(urddi);
                    downloadmanager.enqueue(request);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            Intent intent = new Intent("android.intent.action.SEND");
            intent.setType("video/*");
            intent.putExtra("android.intent.extra.STREAM", FileProvider.getUriForFile(this, getString(R.string.authority), outputFile));
            if (intent.resolveActivity(getPackageManager()) != null) {
                startActivity(intent);
                startActivity(Intent.createChooser(intent, "Share to"));
                return;
            }
            Toast.makeText(this, "try_later", Toast.LENGTH_SHORT).show();

        } else if (flug_str.equalsIgnoreCase("0")) {

            str_video = (Videocategory) getIntent().getSerializableExtra("videoserilizeable");
            outputFile = new File(Environment.getExternalStoragePublicDirectory
                    (Environment.DIRECTORY_DOWNLOADS), "myfile.mp4");
            //outputFile.mkdir();

            try {
                // outputFile.createNewFile();

                if (outputFile.exists()) {
                    boolean result = outputFile.delete();
                }
//                else{
                DownloadManager downloadmanager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
                Uri uri = Uri.parse(Config.BASE_URL + str_video.video_name);
                Toast.makeText(this, uri.toString(), Toast.LENGTH_SHORT).show();
                DownloadManager.Request request = new DownloadManager.Request(this.contentURL);
                request.setTitle("My File");
                request.setDescription("Downloading");
                request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                request.setVisibleInDownloadsUi(false);
                Uri urddi = Uri.fromFile(outputFile);
                request.setDestinationUri(urddi);
                downloadmanager.enqueue(request);
//                }
            } catch (Exception e) {
                Toast.makeText(this, "error" + e.getMessage(), Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
            Intent intent = new Intent("android.intent.action.SEND");
            intent.setType("video/*");
            intent.putExtra("android.intent.extra.STREAM", FileProvider.getUriForFile(this, getString(R.string.authority), outputFile));
            if (intent.resolveActivity(getPackageManager()) != null) {
                startActivity(intent);
                startActivity(Intent.createChooser(intent, "Share to"));
                return;
            }
            Toast.makeText(this, "try_later", Toast.LENGTH_SHORT).show();
        }
    }

    public void startDownloadingForSharing() {

        outputFile = new File(Environment.getExternalStoragePublicDirectory
                (Environment.DIRECTORY_DOWNLOADS), "myfile.mp4");
        if (outputFile.exists()) {
            outputFile.delete();
        }
        registerReceiver(onDownloadComplete, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
        DownloadManager downloadmanager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
        DownloadManager.Request request = new DownloadManager.Request(this.contentURL);
        request.setTitle("My File");
        request.setDescription("Downloading");
        request.setNotificationVisibility(0);
        request.setVisibleInDownloadsUi(false);
        Uri urddi = Uri.fromFile(outputFile);
        request.setDestinationUri(urddi);
        downloadmanager.enqueue(request);

        this.onDownloadComplete = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                //Fetching the download id received with the broadcast
                long id = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
                //Checking if the received broadcast is for our enqueued download by matching download id
//                if (downloadID == id) {
                Toast.makeText(getApplicationContext(), "Download Completed", Toast.LENGTH_SHORT).show();
//                }
            }
        };
    }


    public void shareToTwitter() {

        if (flug_str.equalsIgnoreCase("2")) {
            str_searchvideo = (Searchdata) getIntent().getSerializableExtra("videoserilizeable");
            outputFile = new File(Environment.getExternalStoragePublicDirectory
                    (Environment.DIRECTORY_DOWNLOADS), "myfile.mp4");
            //  outputFile.mkdir();
            try {
                //  outputFile.createNewFile();
                if (!outputFile.exists()) {
                    DownloadManager downloadmanager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
                    Uri uri = Uri.parse(Config.BASE_URL + str_video.video_name);
                    DownloadManager.Request request = new DownloadManager.Request(this.contentURL);
                    request.setTitle("My File");
                    request.setDescription("Downloading");
                    request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                    request.setVisibleInDownloadsUi(false);
                    Uri urddi = Uri.fromFile(outputFile);
                    request.setDestinationUri(urddi);
                    downloadmanager.enqueue(request);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            Intent intent = new Intent("android.intent.action.SEND");
            intent.setType("video/*");
            Uri uriForFile = FileProvider.getUriForFile(this, getString(R.string.authority), outputFile);
            intent.setPackage("com.twitter.android");
            intent.putExtra("android.intent.extra.STREAM", uriForFile);
            if (intent.resolveActivity(getPackageManager()) != null) {
                startActivity(Intent.createChooser(intent, "Share to"));
            } else {
                alertForApp(getString(R.string.install_twitter), "com.twitter.android");
            }

        } else if (flug_str.equalsIgnoreCase("1")) {
            str_selevideo = (SelectCategory) getIntent().getSerializableExtra("videoserilizeable");
            outputFile = new File(Environment.getExternalStoragePublicDirectory
                    (Environment.DIRECTORY_DOWNLOADS), "myfile.mp4");
            //    outputFile.mkdir();
            try {
                //   outputFile.createNewFile();
                if (!outputFile.exists()) {
                    DownloadManager downloadmanager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
                    Uri uri = Uri.parse(Config.BASE_URL + str_video.video_name);
                    DownloadManager.Request request = new DownloadManager.Request(this.contentURL);
                    request.setTitle("My File");
                    request.setDescription("Downloading");
                    request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                    request.setVisibleInDownloadsUi(false);
                    Uri urddi = Uri.fromFile(outputFile);
                    request.setDestinationUri(urddi);
                    downloadmanager.enqueue(request);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            Intent intent = new Intent("android.intent.action.SEND");
            intent.setType("video/*");
            Uri uriForFile = FileProvider.getUriForFile(this, getString(R.string.authority), outputFile);
            intent.setPackage("com.twitter.android");
            intent.putExtra("android.intent.extra.STREAM", uriForFile);
            if (intent.resolveActivity(getPackageManager()) != null) {
                startActivity(Intent.createChooser(intent, "Share to"));
            } else {
                alertForApp(getString(R.string.install_twitter), "com.twitter.android");
            }

        } else if (flug_str.equalsIgnoreCase("0")) {
            str_video = (Videocategory) getIntent().getSerializableExtra("videoserilizeable");
            outputFile = new File(Environment.getExternalStoragePublicDirectory
                    (Environment.DIRECTORY_DOWNLOADS), "myfile.mp4");
            // outputFile.mkdir();
            try {
                //  outputFile.createNewFile();
                if (!outputFile.exists()) {
                    DownloadManager downloadmanager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
                    Uri uri = Uri.parse(Config.BASE_URL + str_video.video_name);
                    DownloadManager.Request request = new DownloadManager.Request(this.contentURL);
                    request.setTitle("My File");
                    request.setDescription("Downloading");
                    request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                    request.setVisibleInDownloadsUi(false);
                    Uri urddi = Uri.fromFile(outputFile);
                    request.setDestinationUri(urddi);
                    downloadmanager.enqueue(request);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            Intent intent = new Intent("android.intent.action.SEND");
            intent.setType("video/*");
            Uri uriForFile = FileProvider.getUriForFile(this, getString(R.string.authority), outputFile);
            intent.setPackage("com.twitter.android");
            intent.putExtra("android.intent.extra.STREAM", uriForFile);
            if (intent.resolveActivity(getPackageManager()) != null) {
                startActivity(Intent.createChooser(intent, "Share to"));
            } else {
                alertForApp(getString(R.string.install_twitter), "com.twitter.android");
            }
        }


    }

    public void shareToInstaGram() {

        if (flug_str.equalsIgnoreCase("2")) {
            str_searchvideo = (Searchdata) getIntent().getSerializableExtra("videoserilizeable");
            outputFile = new File(Environment.getExternalStoragePublicDirectory
                    (Environment.DIRECTORY_DOWNLOADS), "myfile.mp4");
            //    outputFile.mkdir();
            try {
                //  outputFile.createNewFile();
                if (!outputFile.exists()) {
                    DownloadManager downloadmanager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
                    Uri uri = Uri.parse(Config.BASE_URL + str_video.video_name);
                    DownloadManager.Request request = new DownloadManager.Request(this.contentURL);
                    request.setTitle("My File");
                    request.setDescription("Downloading");
                    request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                    request.setVisibleInDownloadsUi(false);
                    Uri urddi = Uri.fromFile(outputFile);
                    request.setDestinationUri(urddi);
                    downloadmanager.enqueue(request);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            Intent intent = new Intent("android.intent.action.SEND");
            intent.setType("video/*");
            Uri uriForFile = FileProvider.getUriForFile(this, getString(R.string.authority), outputFile);
            intent.setPackage("com.instagram.android");
            intent.putExtra("android.intent.extra.STREAM", uriForFile);
            if (intent.resolveActivity(getPackageManager()) != null) {
                startActivity(Intent.createChooser(intent, "Share to"));
            } else {
                alertForApp(getString(R.string.install_twitter), "com.instagram.android");
            }

        } else if (flug_str.equalsIgnoreCase("1")) {
            str_selevideo = (SelectCategory) getIntent().getSerializableExtra("videoserilizeable");
            outputFile = new File(Environment.getExternalStoragePublicDirectory
                    (Environment.DIRECTORY_DOWNLOADS), "myfile.mp4");
            // outputFile.mkdir();
            try {
                // outputFile.createNewFile();
                if (!outputFile.exists()) {
                    DownloadManager downloadmanager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
                    Uri uri = Uri.parse(Config.BASE_URL + str_video.video_name);
                    DownloadManager.Request request = new DownloadManager.Request(this.contentURL);
                    request.setTitle("My File");
                    request.setDescription("Downloading");
                    request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                    request.setVisibleInDownloadsUi(false);
                    Uri urddi = Uri.fromFile(outputFile);
                    request.setDestinationUri(urddi);
                    downloadmanager.enqueue(request);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            Intent intent = new Intent("android.intent.action.SEND");
            intent.setType("video/*");
            Uri uriForFile = FileProvider.getUriForFile(this, getString(R.string.authority), outputFile);
            intent.setPackage("com.instagram.android");
            intent.putExtra("android.intent.extra.STREAM", uriForFile);
            if (intent.resolveActivity(getPackageManager()) != null) {
                startActivity(Intent.createChooser(intent, "Share to"));
            } else {
                alertForApp(getString(R.string.install_twitter), "com.instagram.android");
            }

        } else if (flug_str.equalsIgnoreCase("0")) {
            str_video = (Videocategory) getIntent().getSerializableExtra("videoserilizeable");
            outputFile = new File(Environment.getExternalStoragePublicDirectory
                    (Environment.DIRECTORY_DOWNLOADS), "myfile.mp4");
            //  outputFile.mkdir();
            try {
                //  outputFile.createNewFile();
                if (!outputFile.exists()) {
                    DownloadManager downloadmanager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
                    Uri uri = Uri.parse(Config.BASE_URL + str_video.video_name);
                    DownloadManager.Request request = new DownloadManager.Request(this.contentURL);
                    request.setTitle("My File");
                    request.setDescription("Downloading");
                    request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                    request.setVisibleInDownloadsUi(false);
                    Uri urddi = Uri.fromFile(outputFile);
                    request.setDestinationUri(urddi);
                    downloadmanager.enqueue(request);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            Intent intent = new Intent("android.intent.action.SEND");
            intent.setType("video/*");
            Uri uriForFile = FileProvider.getUriForFile(this, getString(R.string.authority), outputFile);
            intent.setPackage("com.instagram.android");
            intent.putExtra("android.intent.extra.STREAM", uriForFile);
            if (intent.resolveActivity(getPackageManager()) != null) {
                startActivity(Intent.createChooser(intent, "Share to"));
            } else {
                alertForApp(getString(R.string.install_twitter), "com.instagram.android");
            }
        }


    }


    public void shareToMessenger() {

        str_video = (Videocategory) getIntent().getSerializableExtra("videoserilizeable");
        outputFile = new File(Environment.getExternalStoragePublicDirectory
                (Environment.DIRECTORY_DOWNLOADS), "myfile.mp4");
        // outputFile.mkdir();
        try {
            // outputFile.createNewFile();

            if (!outputFile.exists()) {
                DownloadManager downloadmanager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
                Uri uri = Uri.parse(Config.BASE_URL + str_video.video_name);
                DownloadManager.Request request = new DownloadManager.Request(this.contentURL);
                request.setTitle("My File");
                request.setDescription("Downloading");
                request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                request.setVisibleInDownloadsUi(false);
                Uri urddi = Uri.fromFile(outputFile);
                request.setDestinationUri(urddi);
                downloadmanager.enqueue(request);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        ShareToMessengerParams.newBuilder(FileProvider.getUriForFile(this, getString(R.string.authority), outputFile), MimeTypes.VIDEO_MP4).build();
    }


    public void shareToFaceBook() {
//        Log.e("check_file", "*************************************" + file);
//        ShareVideoContent content = new ShareVideoContent.Builder().setVideo(file).build();
//        final ShareApi shareApi = new ShareApi(content);
//
//        ShareVideoContent build = new ShareVideoContent.Builder().setVideo(new ShareVideo.Builder().setLocalUrl(Uri.fromFile(this.file)).build()).build();
//        if (ShareDialog.canShow(ShareVideoContent.class)) {
//            ShareDialog.show(this, build);
//        } else {
//            alertForApp(getString(R.string.install_facebook), "com.facebook.katana");
//        }


        Uri videoFileUri = Uri.parse("https://www.youtube.com/watch?v=zeLqNx7dMBM");
        ShareVideo shareVideo = new ShareVideo.Builder()
                .setLocalUrl(videoFileUri)
                .build();
        //There is no use by content
        ShareVideoContent content = new ShareVideoContent.Builder()
                .setVideo(shareVideo)
                .build();
        if (ShareDialog.canShow(ShareVideoContent.class)) {
            ShareDialog.show(this, content);

        }


    }


    public void onClick(View view) {
//        dialog = ProgressDialog.show(VideoDetailActivity.this, "",
//                "Loading. Please wait...", true);
        int id = view.getId();
//        Toast.makeText(getApplicationContext(), this.contentURL.toString(), Toast.LENGTH_LONG).show();
//        if(id!=  R.id.btn_download) {
//            Toast.makeText(getApplicationContext(),"DOownloadingfirst",Toast.LENGTH_LONG).show();
        outputFile = new File(Environment.getExternalStoragePublicDirectory
                (Environment.DIRECTORY_DOWNLOADS), "myfile.mp4");
//        if(outputFile.exists()){
//            outputFile.delete();
//
//        }
//            DownloadManager downloadmanager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
//            DownloadManager.Request request = new DownloadManager.Request(this.contentURL);
//            request.setTitle("My File");
//            request.setDescription("Downloading");
//            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
//            request.setVisibleInDownloadsUi(false);
//            Uri urddi = Uri.fromFile(outputFile);
//            request.setDestinationUri(urddi);
//            downloadmanager.enqueue(request);
//        }

//        Boolean valueOf = Boolean.valueOf(hasCurrentVideo());
        switch (id) {
            case R.id.btn_download:
                if (!isStoragePermissionGranted()) {
                    return;
                }
//
                startDownloading();

                return;
            case R.id.btn_facebook:
                if (!isStoragePermissionGranted()) {
                    return;
                }
//                shareToFaceBook();
                shareToFB();

                return;
            case R.id.btn_instagram:
                if (!isStoragePermissionGranted()) {
                    return;
                }
                shareToInstaGram();

                return;
            case R.id.btn_messanger:
                if (!isStoragePermissionGranted()) {
                    return;
                }
//                if (valueOf.booleanValue()) {
                shareToMessanger();

                return;
            default:
                return;
            case R.id.btn_share:
                if (!isStoragePermissionGranted()) {
                    return;
                }
                shareToDefault();

                return;
            case R.id.btn_twitter:
                if (!isStoragePermissionGranted()) {
                    return;
                }
                shareToTwitter();

                return;
            case R.id.btn_whatsapp:
                if (!isStoragePermissionGranted()) {
                    return;
                }
                shareToWhatsApp();

                return;
        }
    }

    public void increaseViewOfItem(ArrayList<Videocategory> str, final int i) {
        Log.e("check_position", "uuuuuuuuuuuuuuuuuuuuuu" + str);
        Intent intent = new Intent(VideoDetailActivity.this, SubCatogory.class);
        intent.putExtra("videoTitle", str.get(i).Img_title);
//        intent.putExtra("id", VideoDetailActivity.this.dataSuggested.get(i).getId());
        intent.putExtra("videoCategory", str.get(i).Id);
//        intent.putExtra("videoSubCategory", VideoDetailActivity.this.dataSuggested.get(i).getVideo_subCat_id());
//        intent.putExtra("videoDownload", VideoDetailActivity.this.dataSuggested.get(i).getVideoDownload());
//        intent.putExtra("videoSubCategoryId", VideoDetailActivity.this.dataSuggested.get(i).getVideo_subCat_id());
//        intent.putExtra("videoCategoryId", VideoDetailActivity.this.dataSuggested.get(i).getVideo_cat_id());
        intent.putExtra("videoImage", str.get(i).Thumbnail);
        intent.putExtra("videoView", str.get(i).video_name);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        finish();
        VideoDetailActivity.this.startActivity(intent);
    }


    private void startDownload() {

        outputFile = new File(Environment.getExternalStoragePublicDirectory
                (Environment.DIRECTORY_DOWNLOADS), "VideoStatus");
        outputFile.mkdir();
        btn_fb.setVisibility(View.INVISIBLE);
        btn_what.setVisibility(View.INVISIBLE);
        btn_sh.setVisibility(View.INVISIBLE);
        btn_twit.setVisibility(View.INVISIBLE);
        btn_insta.setVisibility(View.INVISIBLE);
        btn_mess.setVisibility(View.INVISIBLE);
        thank.setVisibility(View.INVISIBLE);
        btn_download.setVisibility(View.INVISIBLE);
        this.list.clear();
//        this.videoPath = this.videoPath.replace(" ", "%20");
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(this.videoPath));
        request.setAllowedNetworkTypes(3);
        request.setAllowedOverRoaming(false);
        request.setTitle(getResources().getString(R.string.downloading) + str_video.Img_title);
        request.setVisibleInDownloadsUi(true);
        request.allowScanningByMediaScanner();
        request.setDestinationInExternalPublicDir(getString(R.string.destinationFolderName), str_video.video_name);
        final long enqueue = this.downloadManager.enqueue(request);
        Log.e("OUT", "" + enqueue);
        this.list.add(Long.valueOf(enqueue));
        setupProgress(Long.valueOf(enqueue));
        this.btn_cancelDownload.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                VideoDetailActivity.this.cancelDownload(Long.valueOf(enqueue));
                thank.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.INVISIBLE);
                txt_fileSize.setVisibility(View.INVISIBLE);
                txt_progress.setVisibility(View.INVISIBLE);
                btn_cancelDownload.setVisibility(View.INVISIBLE);
            }
        });
    }


    public void startDownloading() {
        outputFile = new File(Environment.getExternalStoragePublicDirectory
                (Environment.DIRECTORY_DOWNLOADS), "VideoStatus");
        if (!outputFile.isDirectory()) {
            outputFile.mkdir();
        }
//        str_selevideo = (SelectCategory) getIntent().getSerializableExtra("videoserilizeable");
//        str_video = (Videocategory) getIntent().getSerializableExtra("videoserilizeable");
        outputFile = new File(Environment.getExternalStoragePublicDirectory
                (Environment.DIRECTORY_DOWNLOADS), "VideoStatus/status.mp4");
//        str_searchvideo = (Searchdata) getIntent().getSerializableExtra("videoserilizeable");
        DownloadManager downloadmanager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
//        Uri uri = Uri.parse(Config.BASE_URL + str_video.video_name);
        Toast.makeText(this, "Downloading", Toast.LENGTH_SHORT).show();
        DownloadManager.Request request = new DownloadManager.Request(this.contentURL);
        request.setTitle("Status");
        request.setDescription("Downloading");
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        request.setVisibleInDownloadsUi(false);
        Uri urddi = Uri.fromFile(outputFile);
        request.setDestinationUri(urddi);
        downloadmanager.enqueue(request);
//        if (flug_str.equalsIgnoreCase("2")) {
//            Toast.makeText(this, "SHARING File 1", Toast.LENGTH_SHORT).show();
//            str_searchvideo = (Searchdata) getIntent().getSerializableExtra("videoserilizeable");
//            outputFile = new File(Environment.getExternalStoragePublicDirectory
//                    (Environment.DIRECTORY_DOWNLOADS), "myfile.mp4");
//            outputFile.mkdir();
//            try {
//                outputFile.createNewFile();
//                if (!outputFile.exists()) {
//                    DownloadManager downloadmanager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
//                    Uri uri = Uri.parse(Config.BASE_URL + str_searchvideo.video_name);
//                    DownloadManager.Request request = new DownloadManager.Request(uri);
//                    request.setTitle("My File");
//                    request.setDescription("Downloading");
//                    request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
//                    request.setVisibleInDownloadsUi(false);
//                    Uri urddi = Uri.fromFile(outputFile);
//                    request.setDestinationUri(urddi);
//                    downloadmanager.enqueue(request);
//                }
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//            Intent intent = new Intent("android.intent.action.SEND");
//            intent.setType("video/*");
//            intent.putExtra("android.intent.extra.STREAM", FileProvider.getUriForFile(this, getString(R.string.authority), outputFile));
//            if (intent.resolveActivity(getPackageManager()) != null) {
//                startActivity(intent);
//                startActivity(Intent.createChooser(intent, "Share to"));
//                return;
//            }
//            Toast.makeText(this, "try_later", Toast.LENGTH_SHORT).show();
//
//
//        }else if (flug_str.equalsIgnoreCase("1")){
//            Toast.makeText(this, "SHARING File 2", Toast.LENGTH_SHORT).show();
//            str_selevideo = (SelectCategory) getIntent().getSerializableExtra("videoserilizeable");
//            outputFile = new File(Environment.getExternalStoragePublicDirectory
//                    (Environment.DIRECTORY_DOWNLOADS), "myfile.mp4");
//            outputFile.mkdir();
//            try {
//                outputFile.createNewFile();
//
//                if (!outputFile.exists()) {
//                    DownloadManager downloadmanager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
//                    Uri uri = Uri.parse(Config.BASE_URL + str_selevideo.video_name);
//                    DownloadManager.Request request = new DownloadManager.Request(uri);
//                    request.setTitle("My File");
//                    request.setDescription("Downloading");
//                    request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
//                    request.setVisibleInDownloadsUi(false);
//                    Uri urddi = Uri.fromFile(outputFile);
//                    request.setDestinationUri(urddi);
//                    downloadmanager.enqueue(request);
//                }
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//            Intent intent = new Intent("android.intent.action.SEND");
//            intent.setType("video/*");
//            intent.putExtra("android.intent.extra.STREAM", FileProvider.getUriForFile(this, getString(R.string.authority), outputFile));
//            if (intent.resolveActivity(getPackageManager()) != null) {
//                startActivity(intent);
//                startActivity(Intent.createChooser(intent, "Share to"));
//                return;
//            }
//            Toast.makeText(this, "try_later", Toast.LENGTH_SHORT).show();
//
//        }else if (flug_str.equalsIgnoreCase("0")){
//
//            str_video = (Videocategory) getIntent().getSerializableExtra("videoserilizeable");
//            outputFile = new File(Environment.getExternalStoragePublicDirectory
//                    (Environment.DIRECTORY_DOWNLOADS), "myfile.mp4");
//            //outputFile.mkdir();
//
//            try {
//                // outputFile.createNewFile();
//
//                if (outputFile.exists()) {
//                    boolean result = outputFile.delete();
//                }
////                else{
//                DownloadManager downloadmanager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
//                Uri uri = Uri.parse(Config.BASE_URL + str_video.video_name);
//                Toast.makeText(this,uri.toString(), Toast.LENGTH_SHORT).show();
//                DownloadManager.Request request = new DownloadManager.Request(uri);
//                request.setTitle("My File");
//                request.setDescription("Downloading");
//                request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
//                request.setVisibleInDownloadsUi(false);
//                Uri urddi = Uri.fromFile(outputFile);
//                request.setDestinationUri(urddi);
//                downloadmanager.enqueue(request);
////                }
//            } catch (Exception e) {
//                Toast.makeText(this,"error"+e.getMessage(), Toast.LENGTH_SHORT).show();
//                e.printStackTrace();
//            }
//            Intent intent = new Intent("android.intent.action.SEND");
//            intent.setType("video/*");
//            intent.putExtra("android.intent.extra.STREAM", FileProvider.getUriForFile(this, getString(R.string.authority), outputFile));
//            if (intent.resolveActivity(getPackageManager()) != null) {
//                startActivity(intent);
//                startActivity(Intent.createChooser(intent, "Share to"));
//                return;
//            }
//            Toast.makeText(this, "try_later", Toast.LENGTH_SHORT).show();
//        }
    }

    private boolean isStoragePermissionGranted() {
        if (Build.VERSION.SDK_INT < 23 || checkSelfPermission("android.permission.WRITE_EXTERNAL_STORAGE") == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        ActivityCompat.requestPermissions(this, new String[]{"android.permission.WRITE_EXTERNAL_STORAGE", "android.permission.READ_EXTERNAL_STORAGE"}, 1);
        return false;
    }


    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(this.onComplete);
    }

    public void onBackPressed() {
        super.onBackPressed();
    }


    public void onPause() {
        super.onPause();
        releasePlayer();
    }

    private boolean hasCurrentVideo() {
        str_video = (Videocategory) getIntent().getSerializableExtra("videoserilizeable");
        this.file = new File(Environment.getExternalStoragePublicDirectory("/VideoStatus"), str_video.video_name);
        return this.file.exists();
    }

    private void setupProgress(final Long l) {
        new Thread(new Runnable() {
            public void run() {
                boolean z = true;
                while (z) {
                    DownloadManager.Query query = new DownloadManager.Query();
                    query.setFilterById(l.longValue());
                    try {
                        Cursor query2 = VideoDetailActivity.this.downloadManager.query(query);
                        if (query2 == null || query2.getCount() == 0) {
                            VideoDetailActivity.this.runOnUiThread(new Runnable() {
                                public void run() {
                                    VideoDetailActivity.this.rel_showProgress.setVisibility(View.VISIBLE);
                                    VideoDetailActivity.this.detail_toolbar.setVisibility(View.INVISIBLE);
                                }
                            });
                            z = false;
                        } else {
                            query2.moveToFirst();
                            int i = query2.getInt(query2.getColumnIndex("bytes_so_far"));
                            final int i2 = query2.getInt(query2.getColumnIndex("total_size"));
                            if (query2.getInt(query2.getColumnIndex("status")) == 16) {
                                VideoDetailActivity.this.runOnUiThread(new Runnable() {
                                    public void run() {
                                        VideoDetailActivity.this.rel_showProgress.setVisibility(View.VISIBLE);
                                        VideoDetailActivity.this.detail_toolbar.setVisibility(View.INVISIBLE);
                                    }
                                });
                                z = false;
                            }
                            double d = i;
                            Double.isNaN(d);
                            double d2 = i2;
                            Double.isNaN(d2);
                            final double d3 = (d * 100.0d) / d2;
                            final String str = VideoDetailActivity.this.getResources().getString(R.string.downloading) + " " + ((int) d3) + "%";
                            VideoDetailActivity.this.runOnUiThread(new Runnable() {
                                public void run() {
                                    VideoDetailActivity.this.rel_showProgress.setVisibility(View.VISIBLE);
                                    VideoDetailActivity.this.detail_toolbar.setVisibility(View.VISIBLE);
                                    VideoDetailActivity.this.txt_progress.setText(str);
                                    String format = String.format(Locale.ENGLISH, "%.2f", Float.valueOf(((float) i2) / 1048576.0f));
                                    VideoDetailActivity.this.txt_fileSize.setText(format + " MB");
                                    if (Build.VERSION.SDK_INT >= 24) {
                                        VideoDetailActivity.this.progressBar.setProgress((int) d3, true);
                                    } else {
                                        VideoDetailActivity.this.progressBar.setProgress((int) d3);
                                    }
                                }
                            });
                            if (query2.getInt(query2.getColumnIndex("status")) == 8) {
                                VideoDetailActivity.this.runOnUiThread(new Runnable() {
                                    public void run() {
                                        VideoDetailActivity.this.rel_showProgress.setVisibility(View.INVISIBLE);
                                        VideoDetailActivity.this.detail_toolbar.setVisibility(View.INVISIBLE);
                                        if (VideoDetailActivity.this.isWhatsApp) {
                                            VideoDetailActivity.this.shareToWhatsApp();
                                        } else if (VideoDetailActivity.this.isFacebook) {
                                            VideoDetailActivity.this.shareToFaceBook();
                                        } else if (VideoDetailActivity.this.isTwitter) {
                                            VideoDetailActivity.this.shareToTwitter();
                                        } else if (VideoDetailActivity.this.isMessanger) {
                                            VideoDetailActivity.this.shareToMessenger();
                                        } else if (VideoDetailActivity.this.isInstagram) {
                                            VideoDetailActivity.this.shareToInstaGram();
                                        } else if (VideoDetailActivity.this.isShare) {
                                            VideoDetailActivity.this.shareToDefault();
                                        }
                                        VideoDetailActivity.this.increaseDownload();
                                        VideoDetailActivity.this.saveToDatabase();
                                        VideoDetailActivity.this.changeBar();
                                    }
                                });
                                z = false;
                            }
                            query2.close();
                        }
                    } catch (CursorIndexOutOfBoundsException e) {
                        e.printStackTrace();
                        VideoDetailActivity.this.runOnUiThread(new Runnable() {
                            public void run() {
                                VideoDetailActivity.this.rel_showProgress.setVisibility(View.INVISIBLE);
                                VideoDetailActivity.this.detail_toolbar.setVisibility(View.VISIBLE);
                            }
                        });
                        throw e;
                    }
                }
            }
        }).start();
    }


    public void increaseDownload() {
        String replace = (getString(R.string.link) + "api/video_view_count.php?download=" + this.videoData.getId()).replace(" ", "%20");
        Log.w(getClass().getName(), replace);
        Volley.newRequestQueue(this).add(new StringRequest(0, replace, new Response.Listener<String>() {
            public void onResponse(String str) {
                Log.e("Response", str);
                try {
                    JSONObject jSONObject = new JSONObject(str).getJSONObject("data");
                    if (Objects.equals(jSONObject.getString("success"), AppEventsConstants.EVENT_PARAM_VALUE_YES)) {
                        VideoDetailActivity.this.videoData.setVideoDownload(jSONObject.getString("download"));
                        VideoDetailActivity.this.tv_downloads.setText(VideoDetailActivity.this.videoData.getVideoDownload() + " " + VideoDetailActivity.this.getResources().getString(R.string.txt_downloads));
                        return;
                    }
                    Toast.makeText(VideoDetailActivity.this, R.string.network_error, Toast.LENGTH_SHORT).show();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            public void onErrorResponse(VolleyError volleyError) {
                NetworkResponse networkResponse = volleyError.networkResponse;
                if (networkResponse != null) {
                    Log.e("Status code", String.valueOf(networkResponse.statusCode));
                    Toast.makeText(VideoDetailActivity.this.getApplicationContext(), String.valueOf(networkResponse.statusCode), Toast.LENGTH_SHORT).show();
                }
            }
        }));
    }


    public void saveToDatabase() {
//        DBAdapter dBAdapter = new DBAdapter(this);
//        try {
//            dBAdapter.createDataBase();
////            try {
////                dBAdapter.openDataBase();
////            } catch (SQLException e) {
////                e.printStackTrace();
////            }
//            SQLiteDatabase writableDatabase = dBAdapter.getWritableDatabase();
//            ContentValues contentValues = new ContentValues();
//            contentValues.put("category", this.videoData.getVideo_category());
//            contentValues.put("subcategory", this.videoData.getVideo_subcategory());
//            contentValues.put("categoryid", this.videoData.getVideo_cat_id());
//            contentValues.put("subcategoryid", this.videoData.getVideo_subCat_id());
//            contentValues.put("video", this.videoData.getVideoFileName());
//            contentValues.put(MessengerShareContentUtility.MEDIA_IMAGE, this.videoData.getVideoImage());
//            contentValues.put("view", this.videoData.getVideoView());
//            contentValues.put("downloads", this.videoData.getVideoDownload());
//            contentValues.put("title", this.videoData.getVideo_title());
//            writableDatabase.insert("Downloaded", null, contentValues);
//            dBAdapter.close();
//        } catch (SQLException unused) {
//            throw new Error("Unable To Create DataBase");
//        }
    }


    public void cancelDownload(Long l) {
        this.downloadManager.remove(l.longValue());
        Toast.makeText(this, R.string.download_cancel, Toast.LENGTH_SHORT).show();
    }

    private void alertForApp(String str, final String str2) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("App Not Found");
        builder.setIcon(R.drawable.ic_error_black_24dp);
        builder.setMessage(str);
        builder.setCancelable(false);
        builder.setPositiveButton(R.string.txt_ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {
                Intent intent = new Intent("android.intent.action.VIEW");
                intent.setData(Uri.parse(VideoDetailActivity.this.getString(R.string.playStore_address) + str2));
                VideoDetailActivity.this.startActivity(intent);
                dialogInterface.cancel();
            }
        }).setNegativeButton(R.string.txt_cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });
        builder.create().show();
    }

    private class PlayerErrorMessageProvider implements ErrorMessageProvider<ExoPlaybackException> {
        private PlayerErrorMessageProvider() {
        }

        public Pair<Integer, String> getErrorMessage(ExoPlaybackException exoPlaybackException) {
            String string = VideoDetailActivity.this.getString(R.string.error_generic);
            if (exoPlaybackException.type == 1) {
                Exception rendererException = exoPlaybackException.getRendererException();
                if (rendererException instanceof MediaCodecRenderer.DecoderInitializationException) {
                    MediaCodecRenderer.DecoderInitializationException decoderInitializationException = (MediaCodecRenderer.DecoderInitializationException) rendererException;
                    if (decoderInitializationException.decoderName != null) {
                        string = VideoDetailActivity.this.getString(R.string.error_instantiating_decoder, decoderInitializationException.decoderName);
                    } else if (decoderInitializationException.getCause() instanceof MediaCodecUtil.DecoderQueryException) {
                        string = VideoDetailActivity.this.getString(R.string.error_querying_decoders);
                    } else if (decoderInitializationException.secureDecoderRequired) {
                        string = VideoDetailActivity.this.getString(R.string.error_no_secure_decoder, decoderInitializationException.mimeType);
                    } else {
                        string = VideoDetailActivity.this.getString(R.string.error_no_decoder, decoderInitializationException.mimeType);
                    }
                }
            }
            return Pair.create(0, string);
        }
    }

    private void initializePlayer() {
        ExtractorMediaSource extractorMediaSource;
        DefaultBandwidthMeter defaultBandwidthMeter = new DefaultBandwidthMeter();
        this.player = ExoPlayerFactory.newSimpleInstance(this, new DefaultTrackSelector(new AdaptiveTrackSelection.Factory(defaultBandwidthMeter)));
        this.playerView.setPlayer(this.player);
        DefaultDataSourceFactory defaultDataSourceFactory = new DefaultDataSourceFactory(this, Util.getUserAgent(this, getString(R.string.app_name)), defaultBandwidthMeter);
        if (hasCurrentVideo()) {
            this.file = new File(Environment.getExternalStoragePublicDirectory(getString(R.string.destinationFolderPath)), str_video.video_name);
            extractorMediaSource = new ExtractorMediaSource.Factory(defaultDataSourceFactory).createMediaSource(Uri.parse(this.file.getPath()));
        } else {
            extractorMediaSource = new ExtractorMediaSource.Factory(defaultDataSourceFactory).createMediaSource(Uri.parse(Config.BASE_URL + str_video.video_name));
        }

//        this.file = new File(Environment.getExternalStoragePublicDirectory(getString(R.string.destinationFolderPath)), str_video.video_name);
//            extractorMediaSource = new ExtractorMediaSource.Factory(defaultDataSourceFactory).createMediaSource(Uri.parse(Config.BASE_URL+str_video.video_name.replace(" ", "%20")));
//        String userAgent = Util.getUserAgent(this, this.getString(R.string.app_name));
//        DefaultDataSourceFactory defdataSourceFactory = new DefaultDataSourceFactory(this, userAgent);
//        Uri uriOfContentUrl = Uri.parse(Config.BASE_URL + str_video.video_name);
//        MediaSource mediaSource = new ExtractorMediaSource.Factory(defdataSourceFactory).createMediaSource(uriOfContentUrl);  // creating a media source

//
//        MediaSource audioSource = new ExtractorMediaSource(Uri.parse(Config.BASE_URL+str_video.video_name),
//                factory, new DefaultExtractorsFactory(), null, null);

        this.player.prepare(extractorMediaSource);
        this.player.setPlayWhenReady(true);

    }

}
