package com.BlackTech.allVideostatus2020.activity;

import android.content.Intent;
import android.graphics.ColorFilter;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.graphics.drawable.DrawerArrowDrawable;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.android.volley.NetworkResponse;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.facebook.appevents.AppEventsConstants;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;

import com.BlackTech.allVideostatus2020.R;
import com.BlackTech.allVideostatus2020.adapter.CategoryItemAdapter;
import com.BlackTech.allVideostatus2020.adapter.NavigationBarAdapter;
import com.BlackTech.allVideostatus2020.getSet.categoryGetSet;
import com.BlackTech.allVideostatus2020.getSet.menuGetSet;
import com.BlackTech.allVideostatus2020.Utility.LanguageSelectore;

import java.util.ArrayList;
import java.util.Objects;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class CategoryActivity extends AppCompatActivity {
    public ArrayList<categoryGetSet> categoryGetSets;
    public DrawerLayout drawer;
    private ListView listSliderMenu;
    public LinearLayout mainView;
    public ArrayList<menuGetSet> menuGetSets;
    public ProgressBar progressBar;
    public TextView text_lang;

    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        LanguageSelectore.setLanguageIfAR(this);
        setContentView((int) R.layout.activity_category);
        initViews();
        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });
        AdView adView = (AdView)this.findViewById(R.id.ad_view3);
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);

        if (MainActivity.isNetworkConnected(this)) {
            setUpMenu();
        } else {
            MainActivity.showErrorDialog(this);
        }

    }


//    public boolean onCreateOptionsMenu(Menu menu) {
//        if (MainActivity.isNetworkConnected(this)) {
//            setUpMenu(menu);
//        } else {
//            MainActivity.showErrorDialog(this);
//        }
//        return super.onCreateOptionsMenu(menu);
//    }

    private void setUpDrawer() {
        this.drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        this.mainView = (LinearLayout) findViewById(R.id.mainView);
        setSupportActionBar(toolbar);
        Drawable overflowIcon = toolbar.getOverflowIcon();
        if (overflowIcon != null) {
            overflowIcon.setColorFilter(new ColorFilter());
            toolbar.setOverflowIcon(overflowIcon);
        }
//        this.text_lang = (TextView) toolbar.findViewById(R.id.text_lang);
        TextView textView = (TextView) toolbar.findViewById(R.id.title);
        textView.setText((int) R.string.txt_categories);
        textView.setTypeface(MainActivity.main_medium);
        ActionBarDrawerToggle r1 = new ActionBarDrawerToggle(this, this.drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close) {
            public void onDrawerSlide(View view, float f) {
                super.onDrawerSlide(view, f);
                if (CategoryActivity.this.getResources().getConfiguration().getLayoutDirection() == 1) {
                    CategoryActivity.this.mainView.setTranslationX((-f) * ((float) view.getWidth()));
                } else {
                    CategoryActivity.this.mainView.setTranslationX(f * ((float) view.getWidth()));
                }
                CategoryActivity.this.drawer.bringChildToFront(view);
                CategoryActivity.this.drawer.requestLayout();
            }
        };
        DrawerArrowDrawable drawerArrowDrawable = new DrawerArrowDrawable(this);
        drawerArrowDrawable.setColor(getResources().getColor(R.color.colorArrow));
        r1.setDrawerArrowDrawable(drawerArrowDrawable);
        this.drawer.addDrawerListener(r1);
        r1.syncState();
    }

    private void initViews() {
        this.listSliderMenu = (ListView) findViewById(R.id.list_slidermenu);
        this.progressBar = (ProgressBar) findViewById(R.id.progressBar);
        setUpDrawer();
        setUpNavigationBar();
    }



    private void setUpMenu() {
        String str = getString(R.string.link) + "api/video_category.php";
        Log.w(getClass().getName(), str);
        Volley.newRequestQueue(this).add(new StringRequest(0, str, new Response.Listener<String>() {
            public void onResponse(String str) {
                Log.e("Response", str);
                CategoryActivity.this.progressBar.setVisibility(View.GONE);
                try {
                    JSONObject jSONObject = new JSONObject(str);
                    if (Objects.equals(jSONObject.getString("success"), AppEventsConstants.EVENT_PARAM_VALUE_YES)) {
                        ArrayList unused = CategoryActivity.this.menuGetSets = new ArrayList();
                        JSONArray jSONArray = jSONObject.getJSONArray("category");
                        for (int i = 0; i < jSONArray.length(); i++) {
                            menuGetSet menugetset = new menuGetSet();
                            JSONObject jSONObject2 = jSONArray.getJSONObject(i);
                            String string = jSONObject2.getString("category");
                            String string2 = jSONObject2.getString("id");
                            menugetset.setMenu_category(string);
                            menugetset.setCat_id(string2);
                            CategoryActivity.this.menuGetSets.add(menugetset);
                        }
                        if (CategoryActivity.this.menuGetSets.size() > 0) {
                            for (int i2 = 0; i2 < CategoryActivity.this.menuGetSets.size(); i2++) {
//                                menu.add(0, i2, 0, ((menuGetSet) CategoryActivity.this.menuGetSets.get(i2)).getMenu_category());
                            }
                        }
                        int parseInt = Integer.parseInt(CategoryActivity.this.getSharedPreferences(MainActivity.prefName, 0).getString("positionCategory", AppEventsConstants.EVENT_PARAM_VALUE_NO));
//                        CategoryActivity.this.text_lang.setText(((menuGetSet) CategoryActivity.this.menuGetSets.get(parseInt)).getMenu_category());
                        CategoryActivity.this.setUpCategoryList(((menuGetSet) CategoryActivity.this.menuGetSets.get(parseInt)).getCat_id());
                        return;
                    }
                    Toast.makeText(CategoryActivity.this, jSONObject.getString("video"), Toast.LENGTH_SHORT).show();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            public void onErrorResponse(VolleyError volleyError) {
                NetworkResponse networkResponse = volleyError.networkResponse;
                if (networkResponse != null) {
                    Log.e("Status code", String.valueOf(networkResponse.statusCode));
                    Toast.makeText(CategoryActivity.this.getApplicationContext(), String.valueOf(networkResponse.statusCode), Toast.LENGTH_SHORT).show();
                }
            }
        }));
    }

    public void updateUI() {
        CategoryItemAdapter categoryItemAdapter = new CategoryItemAdapter(this.categoryGetSets, this);
        ListView listView = (ListView) findViewById(R.id.list_category);
        listView.setAdapter((ListAdapter) categoryItemAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long j) {
                Intent intent = new Intent(CategoryActivity.this, MainActivity.class);
                intent.putExtra("subCategory", ((categoryGetSet) CategoryActivity.this.categoryGetSets.get(i)).getSubCategoryId());
                CategoryActivity.this.startActivity(intent);
            }
        });
    }

    public void setUpCategoryList(String str) {
        String str2 = getString(R.string.link) + "api/video_subcategory.php?category=" + str;
        Log.w(getClass().getName(), str2);
        Volley.newRequestQueue(this).add(new StringRequest(0, str2, new Response.Listener<String>() {
            public void onResponse(String str) {
                Log.e("Response", str);
                try {
                    JSONObject jSONObject = new JSONObject(str);
                    if (Objects.equals(jSONObject.getString("success"), AppEventsConstants.EVENT_PARAM_VALUE_YES)) {
                        ArrayList unused = CategoryActivity.this.categoryGetSets = new ArrayList();
                        JSONArray jSONArray = jSONObject.getJSONArray("video");
                        for (int i = 0; i < jSONArray.length(); i++) {
                            categoryGetSet categorygetset = new categoryGetSet();
                            JSONObject jSONObject2 = jSONArray.getJSONObject(i);
                            String string = jSONObject2.getString("subcategory");
                            String string2 = jSONObject2.getString("id");
                            categorygetset.setImageName(jSONObject2.getString("subcategory_icon").replace(" ", "%20"));
                            categorygetset.setSubCategoryId(string2);
                            categorygetset.setSubCategoryName(string);
                            CategoryActivity.this.categoryGetSets.add(categorygetset);
                        }
                        CategoryActivity.this.updateUI();
                        return;
                    }
                    Toast.makeText(CategoryActivity.this, jSONObject.getString("video"), Toast.LENGTH_SHORT).show();
                    CategoryActivity.this.emptyUI();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            public void onErrorResponse(VolleyError volleyError) {
                NetworkResponse networkResponse = volleyError.networkResponse;
                if (networkResponse != null) {
                    Log.e("Status code", String.valueOf(networkResponse.statusCode));
                    Toast.makeText(CategoryActivity.this.getApplicationContext(), String.valueOf(networkResponse.statusCode), Toast.LENGTH_SHORT).show();
                }
            }
        }));
    }

    public void emptyUI() {
        ((ListView) findViewById(R.id.list_category)).setAdapter((ListAdapter) new CategoryItemAdapter(null, this));
    }


    private void setUpNavigationBar() {
        this.listSliderMenu.setAdapter((ListAdapter) new NavigationBarAdapter(this, this.drawer));
    }

//    public boolean onOptionsItemSelected(MenuItem menuItem) {
////        this.text_lang.setText(menuItem.getTitle());
//        SharedPreferences.Editor edit = getSharedPreferences(MainActivity.prefName, 0).edit();
//        edit.putString("Category", "" + this.menuGetSets.get(menuItem.getItemId()).getCat_id()).apply();
//        SharedPreferences.Editor edit2 = getSharedPreferences(MainActivity.prefName, 0).edit();
//        edit2.putString("positionCategory", "" + menuItem.getItemId()).apply();
//        setUpCategoryList(this.menuGetSets.get(menuItem.getItemId()).getCat_id());
//        return true;
//    }


    public void onBackPressed() {
        DrawerLayout drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawerLayout.isDrawerOpen((int) GravityCompat.START)) {
            drawerLayout.closeDrawer((int) GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
}
