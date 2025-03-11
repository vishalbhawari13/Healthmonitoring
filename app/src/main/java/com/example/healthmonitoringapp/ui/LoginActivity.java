package com.example.healthmonitoringapp.ui;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.healthmonitoringapp.R;
import com.example.healthmonitoringapp.utils.SharedPrefManager;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;

public class LoginActivity extends AppCompatActivity {

    private static final int RC_SIGN_IN = 100;
    private GoogleSignInClient mGoogleSignInClient;
    private MaterialButton btnSignIn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        btnSignIn = findViewById(R.id.btnSignIn);

        // Configure Google Sign-In to always show account chooser
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .requestScopes(new com.google.android.gms.common.api.Scope("https://www.googleapis.com/auth/fitness.activity.read"))
                .build(); // REMOVED .setAccountName(null)


        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        btnSignIn.setOnClickListener(v -> signIn());
    }

    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            if (account != null) {
                String personName = account.getDisplayName();
                String personEmail = account.getEmail();

                // Save user info
                SharedPrefManager.getInstance(this).saveUserDetails(personName, personEmail);

                Toast.makeText(this, "Welcome " + personName, Toast.LENGTH_SHORT).show();

                // Open Profile Activity
                startActivity(new Intent(LoginActivity.this, ProfileActivity.class));
                finish();
            }
        } catch (ApiException e) {
            Log.w("Google Sign-In", "Sign-in failed: " + e.getStatusCode(), e);
            Toast.makeText(this, "Sign-in failed. Try again!", Toast.LENGTH_SHORT).show();
        }
    }
}
