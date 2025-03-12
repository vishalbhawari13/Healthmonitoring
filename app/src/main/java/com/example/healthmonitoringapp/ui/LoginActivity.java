package com.example.healthmonitoringapp.ui;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.healthmonitoringapp.R;
import com.example.healthmonitoringapp.utils.SharedPrefManager;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity";

    private MaterialButton btnSignIn, btnLogout, btnEditProfile;
    private TextView txtWelcome, txtUserName, txtUserEmail;
    private ImageView imgUserProfile;

    private GoogleSignInClient googleSignInClient;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private SharedPrefManager sharedPrefManager;

    private final ActivityResultLauncher<Intent> signInLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    handleSignInResult(result.getData());
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Initialize Firebase Auth and Firestore
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        sharedPrefManager = SharedPrefManager.getInstance(this);

        // Initialize UI elements
        btnSignIn = findViewById(R.id.btnSignIn);
        btnLogout = findViewById(R.id.btnLogout);
        btnEditProfile = findViewById(R.id.btnEditProfile);
        txtWelcome = findViewById(R.id.txtWelcome);
        txtUserName = findViewById(R.id.txtUserName);
        txtUserEmail = findViewById(R.id.txtUserEmail);
        imgUserProfile = findViewById(R.id.imgUserProfile);

        // Configure Google Sign-In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        googleSignInClient = GoogleSignIn.getClient(this, gso);

        // Set click listeners
        btnSignIn.setOnClickListener(v -> signIn());
        btnLogout.setOnClickListener(v -> signOut());
        btnEditProfile.setOnClickListener(v -> openProfileEditor());

        // Check if user is already signed in
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            updateUI(currentUser);
        } else {
            showSignInButton();
        }
    }

    private void signIn() {
        Intent signInIntent = googleSignInClient.getSignInIntent();
        signInLauncher.launch(signInIntent);
    }

    private void handleSignInResult(Intent data) {
        try {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            GoogleSignInAccount account = task.getResult(ApiException.class);
            if (account != null) {
                firebaseAuthWithGoogle(account);
            }
        } catch (ApiException e) {
            Log.e(TAG, "Google sign-in failed", e);
            Toast.makeText(this, R.string.sign_in_failed, Toast.LENGTH_SHORT).show();
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount account) {
        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null) {
                            saveUserToFirestore(user);
                        }
                    } else {
                        Log.e(TAG, "Firebase authentication failed", task.getException());
                        Toast.makeText(this, R.string.auth_failed, Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void saveUserToFirestore(FirebaseUser user) {
        String name = user.getDisplayName();
        String email = user.getEmail();
        String profilePic = (user.getPhotoUrl() != null) ? user.getPhotoUrl().toString() : "";

        if (email == null) {
            Toast.makeText(this, R.string.email_not_found, Toast.LENGTH_SHORT).show();
            return;
        }

        DocumentReference userRef = db.collection("users").document(email);

        userRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                Map<String, Object> userData = new HashMap<>();
                userData.put("name", name);
                userData.put("email", email);
                userData.put("profilePic", profilePic);

                if (document.exists()) {
                    userData.put("dob", document.getString("dob"));
                    userData.put("phone", document.getString("phone"));
                    userData.put("address", document.getString("address"));
                }

                userRef.set(userData);
                sharedPrefManager.saveUserDetails(name, email, "", "", "", profilePic);
            }
        });

        updateUI(user);
    }

    private void updateUI(FirebaseUser user) {
        txtWelcome.setText(getString(R.string.welcome_message));
        txtUserName.setText(user.getDisplayName());
        txtUserEmail.setText(user.getEmail());

        Glide.with(this)
                .load(user.getPhotoUrl())
                .placeholder(R.drawable.default_profile)
                .into(imgUserProfile);

        btnSignIn.setVisibility(View.GONE);
        btnLogout.setVisibility(View.VISIBLE);
        btnEditProfile.setVisibility(View.VISIBLE);
    }

    private void signOut() {
        googleSignInClient.signOut().addOnCompleteListener(this, task -> {
            mAuth.signOut();
            sharedPrefManager.clearUserData();
            showSignInButton();
        });
    }

    private void showSignInButton() {
        txtWelcome.setText(getString(R.string.sign_in_prompt));
        btnSignIn.setVisibility(View.VISIBLE);
        btnLogout.setVisibility(View.GONE);
        btnEditProfile.setVisibility(View.GONE);
    }

    private void openProfileEditor() {
        startActivity(new Intent(this, ProfileActivity.class));
    }
}
