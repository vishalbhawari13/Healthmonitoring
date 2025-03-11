package com.example.healthmonitoringapp.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPrefManager {
    private static final String SHARED_PREF_NAME = "health_monitoring_pref";
    private static final String KEY_NAME = "key_name";
    private static final String KEY_EMAIL = "key_email";
    private static final String KEY_PROFILE_PIC = "key_profile_pic";

    private static volatile SharedPrefManager instance;
    private final SharedPreferences sharedPreferences;
    private final SharedPreferences.Editor editor;

    /**
     * Private constructor to prevent direct instantiation.
     */
    private SharedPrefManager(Context context) {
        sharedPreferences = context.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    /**
     * Returns a singleton instance of SharedPrefManager.
     */
    public static SharedPrefManager getInstance(Context context) {
        if (instance == null) {
            synchronized (SharedPrefManager.class) {
                if (instance == null) {
                    instance = new SharedPrefManager(context.getApplicationContext());
                }
            }
        }
        return instance;
    }

    /**
     * Saves user details in SharedPreferences.
     *
     * @param name  The user's name
     * @param email The user's email
     * @param profilePic The user's profile picture URL
     */
    public void saveUserDetails(String name, String email, String profilePic) {
        editor.putString(KEY_NAME, name);
        editor.putString(KEY_EMAIL, email);
        editor.putString(KEY_PROFILE_PIC, profilePic);
        editor.apply();
    }

    /**
     * Retrieves the stored user name.
     *
     * @return The user's name or an empty string if not found.
     */
    public String getUserName() {
        return sharedPreferences.getString(KEY_NAME, "");
    }

    /**
     * Retrieves the stored user email.
     *
     * @return The user's email or an empty string if not found.
     */
    public String getUserEmail() {
        return sharedPreferences.getString(KEY_EMAIL, "");
    }

    /**
     * Retrieves the stored user profile picture URL.
     *
     * @return The profile picture URL or an empty string if not found.
     */
    public String getUserProfilePic() {
        return sharedPreferences.getString(KEY_PROFILE_PIC, "");
    }

    /**
     * Checks if a user is logged in.
     *
     * @return True if user data exists, false otherwise.
     */
    public boolean isUserLoggedIn() {
        return sharedPreferences.contains(KEY_EMAIL);
    }

    /**
     * Clears only user-related data without affecting other stored preferences.
     */
    public void clearUserData() {
        editor.remove(KEY_NAME);
        editor.remove(KEY_EMAIL);
        editor.remove(KEY_PROFILE_PIC);
        editor.apply();
    }
}
