package com.example.healthmonitoringapp.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import com.example.healthmonitoringapp.R;
import com.google.android.material.button.MaterialButton;

public class IntroActivity extends AppCompatActivity {

    private MaterialButton btnGoogleSignIn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);

        btnGoogleSignIn = findViewById(R.id.btnGoogleSignIn);

        // Open LoginActivity when the button is clicked
        btnGoogleSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(IntroActivity.this, LoginActivity.class));
                finish();
            }
        });
    }
}
