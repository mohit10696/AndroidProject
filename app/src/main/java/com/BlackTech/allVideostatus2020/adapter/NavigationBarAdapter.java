package com.BlackTech.allVideostatus2020.adapter;

import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;

import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.BlackTech.allVideostatus2020.R;
import com.BlackTech.allVideostatus2020.activity.MainActivity;
import com.BlackTech.allVideostatus2020.activity.PopularActivity;

public class NavigationBarAdapter extends BaseAdapter {

    public final DrawerLayout drawerLayout;
    private final LayoutInflater layoutInflater;
    public final Context mContext;
    private final String[] navItems;
    private AdView mAdView;
    private InterstitialAd mInterstitialAd;

    public long getItemId(int i) {
        return (long) i;
    }

    public NavigationBarAdapter(Context context, DrawerLayout drawerLayout2) {
        this.navItems = new String[]{context.getString(R.string.nav_home), context.getString(R.string.nav_popular), context.getString(R.string.nav_categories), context.getString(R.string.nav_rate), context.getString(R.string.nav_share), context.getString(R.string.nav_share_insta), context.getString(R.string.nav_share_you), context.getString(R.string.nav_more)};
        this.layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.mContext = context;
        this.drawerLayout = drawerLayout2;
    }

    public int getCount() {
        return this.navItems.length;
    }

    public Object getItem(int i) {
        return this.navItems[i];
    }

    public View getView(final int i, View view, ViewGroup viewGroup) {
        if (view == null) {
            view = this.layoutInflater.inflate((int) R.layout.item_navigationdrawer, viewGroup, false);
        }
        TextView textView = (TextView) view.findViewById(R.id.navItem);
        textView.setText(this.navItems[i]);
        textView.setTypeface(MainActivity.main_medium);
        String name = this.mContext.getClass().getName();
        Log.e(name, "Position" + i + "SharedPrefPosition" + this.mContext.getSharedPreferences(MainActivity.prefName, 0).getInt("selectedItemNav", -2));


        view.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent intent = null;
                SharedPreferences.Editor edit = NavigationBarAdapter.this.mContext.getSharedPreferences(MainActivity.prefName, 0).edit();
                switch (i) {
                    case 0:
                        intent = new Intent(NavigationBarAdapter.this.mContext, MainActivity.class);
                        NavigationBarAdapter.this.saveClickEvent(edit, i);
                        break;
                    case 1:
                        NavigationBarAdapter.this.openWhatsAppConversationUsingUri(mContext, "+919084841551", "Hello Sir...");
                        NavigationBarAdapter.this.saveClickEvent(edit, i);
                        break;
                    case 2:
                        NavigationBarAdapter.this.shareApp();
                        NavigationBarAdapter.this.saveClickEvent(edit, i);
                        intent = null;
                        break;
                    case 3:
                        NavigationBarAdapter.this.rateUs();
                        NavigationBarAdapter.this.saveClickEvent(edit, i);
                        intent = null;
                        break;
                    case 4:
                        NavigationBarAdapter.this.getOpenFacebookIntent();
                        NavigationBarAdapter.this.saveClickEvent(edit, i);
                        intent = null;
                        break;
                    case 5:
                        NavigationBarAdapter.this.openinstagram();
                        NavigationBarAdapter.this.saveClickEvent(edit, i);
                        intent = null;
                        break;
                    case 6:
                        NavigationBarAdapter.this.openyoutube();
                        NavigationBarAdapter.this.saveClickEvent(edit, i);
                        intent = null;
                        break;
                    case 7:
                        NavigationBarAdapter.this.moreapp();
                        NavigationBarAdapter.this.saveClickEvent(edit, i);
                        intent = null;
                        break;
                    default:
                        intent = null;
                        break;
                }
                if (intent != null) {
                    intent.addFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
                    NavigationBarAdapter.this.mContext.startActivity(intent);
                    if (NavigationBarAdapter.this.drawerLayout.isDrawerOpen((int) GravityCompat.START)) {
                        NavigationBarAdapter.this.drawerLayout.closeDrawer((int) GravityCompat.START);
                    }
                }
            }
        });
        return view;
    }

    public void shareApp() {
        Intent intent = new Intent("android.intent.action.SEND");
        intent.setType("text/plain");
        intent.putExtra("android.intent.extra.TEXT", this.mContext.getString(R.string.playStore_address) + this.mContext.getPackageName());
        this.mContext.startActivity(Intent.createChooser(intent, "Share link!"));
    }

    public static void openWhatsAppConversationUsingUri(Context context, String numberWithCountryCode, String message) {

        Uri uri = Uri.parse("https://api.whatsapp.com/send?phone=" + numberWithCountryCode + "&text=" + message);

        Intent sendIntent = new Intent(Intent.ACTION_VIEW, uri);

        context.startActivity(sendIntent);
    }

    public  void openinstagram(){
        Uri uri = Uri.parse("http://instagram.com/sagarzone1");
        Intent likeIng = new Intent(Intent.ACTION_VIEW, uri);

        likeIng.setPackage("com.instagram.android");

        mContext.startActivity(likeIng);
    }
    public void getOpenFacebookIntent() {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.facebook.com/SagarZone1"));

        mContext.startActivity(intent);

    }

    public void openyoutube(){
        mContext.startActivity(new Intent(Intent.ACTION_VIEW,   Uri.parse("https://www.youtube.com/channel/UCoHkgTSby3Nu33TdqqAEbMg")));
    }

    public void moreapp(){
        mContext.startActivity(new Intent(Intent.ACTION_VIEW,   Uri.parse("https://play.google.com/store/apps/developer?id=Sagar+Zone")));
    }

    public void requestVideo() {
        String string = this.mContext.getString(R.string.email);
        String string2 = this.mContext.getString(R.string.txt_request_video);
        String[] strArr = {string};
        Intent intent = new Intent("android.intent.action.SEND");
        intent.setType("message/rfc822");
        intent.putExtra("android.intent.extra.EMAIL", strArr);
        intent.putExtra("android.intent.extra.SUBJECT", string2);
        try {
            intent.setPackage("com.google.android.gm");
            if (isAvailable(this.mContext, intent)) {
                this.mContext.startActivity(intent);
                return;
            }
            intent.setPackage("com.android.email");
            if (isAvailable(this.mContext, intent)) {
                this.mContext.startActivity(intent);
            }
        } catch (ActivityNotFoundException unused) {
            Toast.makeText(this.mContext, (int) R.string.txt_no_email_client, Toast.LENGTH_SHORT).show();
        }
    }



    
    public void saveClickEvent(SharedPreferences.Editor editor, int i) {
        editor.putInt("selectedItemNav", i);
        editor.apply();
    }

    private static boolean isAvailable(Context context, Intent intent) {
        return context.getPackageManager().queryIntentActivities(intent, intent.getFlags()).size() > 0;
    }

    public void rateUs() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this.mContext);
        builder.setTitle((int) R.string.request_rateus);
        builder.setMessage(this.mContext.getString(R.string.txt_rate_part1) + this.mContext.getString(R.string.app_name) + this.mContext.getString(R.string.txt_rate_part2));
        builder.setPositiveButton((int) R.string.txt_rate_it, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {
                Intent intent = new Intent("android.intent.action.VIEW");
                intent.setData(Uri.parse(NavigationBarAdapter.this.mContext.getString(R.string.playStore_address) + NavigationBarAdapter.this.mContext.getPackageName()));
                NavigationBarAdapter.this.mContext.startActivity(intent);
            }
        });
        builder.setNegativeButton((int) R.string.txt_not_now, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        builder.show();
    }


}
