package com.example.healthmonitoringapp.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

import java.io.ByteArrayOutputStream;

/**
 * Singleton class to manage user data storage using SharedPreferences.
 */
public class SharedPrefManager {
    private static final String PREF_NAME = "health_monitoring_pref";

    // User Details Keys
    private static final String KEY_NAME = "user_name";
    private static final String KEY_EMAIL = "user_email";
    private static final String KEY_PROFILE_PIC = "user_profile_pic"; // Stored as Base64
    private static final String KEY_DOB = "user_dob";
    private static final String KEY_PHONE = "user_phone";
    private static final String KEY_ADDRESS = "user_address";

    private static volatile SharedPrefManager instance;
    private final SharedPreferences sharedPreferences;
    private final SharedPreferences.Editor editor;


    public String getUserProfilePic() {
        return sharedPreferences.getString(KEY_PROFILE_PIC, "");
    }

    /**
     * Private constructor to prevent direct instantiation.
     */
    private SharedPrefManager(Context context) {
        sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
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
     */
    public void saveUserDetails(String name, String email, String profilePicBase64, String dob, String phone, String address) {
        editor.putString(KEY_NAME, name);
        editor.putString(KEY_EMAIL, email);
        editor.putString(KEY_PROFILE_PIC, profilePicBase64);
        editor.putString(KEY_DOB, dob);
        editor.putString(KEY_PHONE, phone);
        editor.putString(KEY_ADDRESS, address);
        editor.apply();
    }

    /**
     * Retrieves the stored user profile picture as a Base64 encoded string.
     */
    public String getUserProfilePicBase64() {
        return sharedPreferences.getString(KEY_PROFILE_PIC, "");
    }

    /**
     * Retrieves the stored user profile picture as a Bitmap.
     * @return Bitmap of the profile picture or null if not found.
     */
    public Bitmap getUserProfilePicBitmap() {
        String encodedImage = sharedPreferences.getString(KEY_PROFILE_PIC, "");
        if (!encodedImage.isEmpty()) {
            byte[] decodedBytes = Base64.decode(encodedImage, Base64.DEFAULT);
            return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
        }
        return null;
    }

    /**
     * Saves user profile picture as a Base64 encoded string.
     */
    public void saveUserProfilePicture(Bitmap bitmap) {
        if (bitmap != null) {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
            String encodedImage = Base64.encodeToString(byteArrayOutputStream.toByteArray(), Base64.DEFAULT);
            editor.putString(KEY_PROFILE_PIC, encodedImage);
            editor.apply();
        }
    }

    /**
     * Retrieves the stored user name.
     */
    public String getUserName() {
        return sharedPreferences.getString(KEY_NAME, "");
    }

    /**
     * Retrieves the stored user email.
     */
    public String getUserEmail() {
        return sharedPreferences.getString(KEY_EMAIL, "");
    }

    /**
     * Retrieves the stored user Date of Birth.
     */
    public String getUserDOB() {
        return sharedPreferences.getString(KEY_DOB, "");
    }

    /**
     * Retrieves the stored user phone number.
     */
    public String getUserPhone() {
        return sharedPreferences.getString(KEY_PHONE, "");
    }

    /**
     * Retrieves the stored user address.
     */
    public String getUserAddress() {
        return sharedPreferences.getString(KEY_ADDRESS, "");
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
        editor.remove(KEY_DOB);
        editor.remove(KEY_PHONE);
        editor.remove(KEY_ADDRESS);
        editor.apply();
    }
}
