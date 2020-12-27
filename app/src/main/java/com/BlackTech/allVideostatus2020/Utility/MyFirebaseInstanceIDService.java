package com.BlackTech.allVideostatus2020.Utility;

import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.android.volley.NetworkResponse;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
import com.BlackTech.allVideostatus2020.R;
import java.util.HashMap;
import java.util.Map;
import org.json.JSONException;
import org.json.JSONObject;

public class MyFirebaseInstanceIDService extends FirebaseInstanceIdService {
    private static final String TAG = "MyFirebaseInstanceIDService";

    public void onTokenRefresh() {
        super.onTokenRefresh();
        String token = FirebaseInstanceId.getInstance().getToken();
        storeRegIdInPref(token);
        sendRegistrationToServer(token);
        Intent intent = new Intent(Config.REGISTRATION_COMPLETE);
        intent.putExtra("token", token);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    private void sendRegistrationToServer(String str) {
        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences(Config.SHARED_PREF, 0);
        String str2 = TAG;
        Log.e(str2, "sendRegistrationToServer: " + str);
        if (sharedPreferences.getBoolean("firstrun", true) && str != null) {
            postRegistrationId(str);
        }
    }

    private void postRegistrationId(String str) {
        String replace = (getString(R.string.link) + "api/token.php").replace(" ", "%20");
        Log.w(getClass().getName(), replace);
        final String str2 = str;
        Volley.newRequestQueue(getApplicationContext()).add(new StringRequest(1, replace, new Response.Listener<String>() {
            public void onResponse(String str) {
                Log.e("Response", str);
                try {
                    if (new JSONObject(str).getJSONArray("Status").getJSONObject(0).getString("id").equals("True")) {
                        Toast.makeText(MyFirebaseInstanceIDService.this.getApplicationContext(), (int) R.string.registration_successful, Toast.LENGTH_SHORT).show();
                        MyFirebaseInstanceIDService.this.getSharedPreferences(Config.SHARED_PREF, 0).edit().putBoolean("firstrun", false).apply();
                        return;
                    }
                    Toast.makeText(MyFirebaseInstanceIDService.this.getApplicationContext(), (int) R.string.registration_successful, Toast.LENGTH_SHORT).show();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            public void onErrorResponse(VolleyError volleyError) {
                NetworkResponse networkResponse = volleyError.networkResponse;
                if (networkResponse != null) {
                    Log.e("Status code", String.valueOf(networkResponse.statusCode));
                    Toast.makeText(MyFirebaseInstanceIDService.this.getApplicationContext(), String.valueOf(networkResponse.statusCode), Toast.LENGTH_SHORT).show();
                }
            }
        }) {
            /* access modifiers changed from: protected */
            public Map<String, String> getParams() {
                HashMap hashMap = new HashMap();
                hashMap.put("token_id", str2);
                hashMap.put("device_type", "android");
                return hashMap;
            }
        });
    }

    private void storeRegIdInPref(String str) {
        SharedPreferences.Editor edit = getApplicationContext().getSharedPreferences(Config.SHARED_PREF, 0).edit();
        String str2 = TAG;
        //Log.e(str2, "sendRegistrationToServer11: " + str);
        edit.putString("regId", str);
        edit.apply();
    }
}
