package com.example.healthmonitoringapp.ui;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.bumptech.glide.Glide;
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
    private MaterialButton btnSignIn, btnLogout;
    private TextView txtWelcome, txtUserName, txtUserEmail;
    private ImageView imgUserProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Initialize UI elements
        btnSignIn = findViewById(R.id.btnSignIn);
        btnLogout = findViewById(R.id.btnLogout);
        txtWelcome = findViewById(R.id.txtWelcome);
        txtUserName = findViewById(R.id.txtUserName);
        txtUserEmail = findViewById(R.id.txtUserEmail);
        imgUserProfile = findViewById(R.id.imgUserProfile);

        // Google Sign-In Configuration
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .requestScopes(new com.google.android.gms.common.api.Scope("https://www.googleapis.com/auth/fitness.activity.read"))
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        // Check if user is already signed in
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        if (account != null) {
            updateUI(account);
        } else {
            showSignInButton();
        }

        btnSignIn.setOnClickListener(v -> signIn());
        btnLogout.setOnClickListener(v -> signOut());
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
                updateUI(account);
            }
        } catch (ApiException e) {
            Log.w("Google Sign-In", "Sign-in failed", e);
            Toast.makeText(this, "Sign-in failed", Toast.LENGTH_SHORT).show();
        }
    }

    private void updateUI(GoogleSignInAccount account) {
        String personName = account.getDisplayName();
        String personEmail = account.getEmail();
        String personPhotoUrl = (account.getPhotoUrl() != null) ? account.getPhotoUrl().toString() : "";

        // Save user details
// Save user details including profile picture
        SharedPrefManager.getInstance(this).saveUserDetails(personName, personEmail, personPhotoUrl);

        txtWelcome.setText("Welcome to PulseGuard!");
        txtUserName.setText(personName);
        txtUserEmail.setText(personEmail);

        // Load profile picture using Glide
        Glide.with(this).load(personPhotoUrl)
                .placeholder(R.drawable.default_profile)
                .into(imgUserProfile);

        // Show logout button, hide sign-in button
        btnSignIn.setVisibility(View.GONE);
        btnLogout.setVisibility(View.VISIBLE);
        txtUserName.setVisibility(View.VISIBLE);
        txtUserEmail.setVisibility(View.VISIBLE);
        imgUserProfile.setVisibility(View.VISIBLE);
    }

    private void signOut() {
        mGoogleSignInClient.signOut().addOnCompleteListener(this, task -> {
            SharedPrefManager.getInstance(this).clearUserData(); // ✅ Corrected method name
            showSignInButton();
        });
    }


    private void showSignInButton() {
        txtWelcome.setText("Welcome! Please sign in to continue.");
        txtUserName.setVisibility(View.GONE);
        txtUserEmail.setVisibility(View.GONE);
        imgUserProfile.setVisibility(View.GONE);
        btnSignIn.setVisibility(View.VISIBLE);
        btnLogout.setVisibility(View.GONE);
    }
}
