package com.example.healthmonitoringapp.ui;

import android.app.Activity;
import android.os.Bundle;

import com.example.healthmonitoringapp.R;

public class ProfileActivity extends Activity {  // ✅ Using android.app.Activity

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
    }
}
