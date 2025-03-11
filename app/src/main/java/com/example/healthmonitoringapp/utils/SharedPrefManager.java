package com.example.healthmonitoringapp.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPrefManager {
    private static final String SHARED_PREF_NAME = "health_monitoring_pref";
    private static final String KEY_NAME = "key_name";
    private static final String KEY_EMAIL = "key_email";

    private static SharedPrefManager instance;
    private final SharedPreferences sharedPreferences;

    private SharedPrefManager(Context context) {
        sharedPreferences = context.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
    }

    public static synchronized SharedPrefManager getInstance(Context context) {
        if (instance == null) {
            instance = new SharedPrefManager(context.getApplicationContext());
        }
        return instance;
    }

    public void saveUserDetails(String name, String email) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY_NAME, name);
        editor.putString(KEY_EMAIL, email);
        editor.apply();
    }

    public String getUserName() {
        return sharedPreferences.getString(KEY_NAME, null);
    }

    public String getUserEmail() {
        return sharedPreferences.getString(KEY_EMAIL, null);
    }

    public void clearUserData() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
    }
}
