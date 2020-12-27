package com.BlackTech.allVideostatus2020.Utility;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.util.Log;
import com.BlackTech.allVideostatus2020.activity.MainActivity;
import java.util.Locale;

public class LanguageSelectore {
    public static String Languagecode = "ar";

    public static void setLanguage(String str, Context context) {
        SharedPreferences.Editor edit = context.getSharedPreferences(MainActivity.prefName, 0).edit();
        edit.putString("lang", str);
        edit.apply();
        Resources resources = context.getResources();
        Configuration configuration = resources.getConfiguration();
        Locale locale = new Locale(str);
        configuration.setLocale(locale);
        resources.updateConfiguration(configuration, resources.getDisplayMetrics());
        Resources resources2 = context.getApplicationContext().getResources();
        Configuration configuration2 = resources2.getConfiguration();
        configuration2.setLocale(locale);
        resources2.updateConfiguration(configuration2, resources2.getDisplayMetrics());
    }

    public static String getLanguage(Context context) {
        return context.getSharedPreferences(MainActivity.prefName, 0).getString("lang", "en");
    }

    public static void setLanguageIfAR(Context context) {
        String displayLanguage = Locale.getDefault().getDisplayLanguage();
        Log.e("langyage", "setLanguageIfAR: " + displayLanguage);
        if (displayLanguage.equals("ar")) {
            setLanguage("ar", context);
            Log.e("MENU", "onCreate: TRUE Hausa" + displayLanguage);
        }
    }
}
