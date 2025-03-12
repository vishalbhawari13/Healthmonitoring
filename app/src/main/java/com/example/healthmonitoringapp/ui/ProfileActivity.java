package com.example.healthmonitoringapp.ui;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.healthmonitoringapp.R;
import com.example.healthmonitoringapp.utils.SharedPrefManager;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Calendar;

public class ProfileActivity extends AppCompatActivity {

    private static final int PICK_IMAGE = 1;
    private ImageView imgUserProfile;
    private EditText dobEditText, phoneEditText, addressEditText;
    private Button btnChangePhoto, saveProfileButton;
    private SharedPrefManager sharedPrefManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        imgUserProfile = findViewById(R.id.profileImageView);
        dobEditText = findViewById(R.id.dobEditText);
        phoneEditText = findViewById(R.id.phoneEditText);
        addressEditText = findViewById(R.id.addressEditText);
        btnChangePhoto = findViewById(R.id.btnChangePhoto);
        saveProfileButton = findViewById(R.id.saveProfileButton);

        sharedPrefManager = SharedPrefManager.getInstance(this);

        loadUserData();
        fetchGoogleSignInDetails();

        btnChangePhoto.setOnClickListener(view -> selectProfileImage());
        dobEditText.setOnClickListener(view -> showDatePicker());
        saveProfileButton.setOnClickListener(view -> saveUserData());
    }

    /**
     * Fetch Google Sign-In details and update UI.
     */
    private void fetchGoogleSignInDetails() {
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        if (account != null) {
            if (sharedPrefManager.getUserName().isEmpty()) {
                sharedPrefManager.saveUserDetails(
                        account.getDisplayName(),
                        account.getEmail(),
                        account.getPhotoUrl() != null ? account.getPhotoUrl().toString() : "",
                        sharedPrefManager.getUserDOB(),
                        sharedPrefManager.getUserPhone(),
                        sharedPrefManager.getUserAddress()
                );
            }
            if (sharedPrefManager.getUserProfilePic().isEmpty() && account.getPhotoUrl() != null) {
                Glide.with(this)
                        .load(account.getPhotoUrl().toString())
                        .into(imgUserProfile);
            }
        }
    }

    /**
     * Opens the gallery to select a profile picture.
     */
    private void selectProfileImage() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_IMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE && resultCode == RESULT_OK && data != null) {
            Uri imageUri = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
                imgUserProfile.setImageBitmap(bitmap);
                saveImageToPreferences(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Saves the selected profile image in SharedPreferences.
     */
    private void saveImageToPreferences(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
        String encodedImage = Base64.encodeToString(byteArrayOutputStream.toByteArray(), Base64.DEFAULT);

        sharedPrefManager.saveUserDetails(
                sharedPrefManager.getUserName(),
                sharedPrefManager.getUserEmail(),
                encodedImage, // Save Base64 image
                sharedPrefManager.getUserDOB(),
                sharedPrefManager.getUserPhone(),
                sharedPrefManager.getUserAddress()
        );

        loadUserData();
    }

    /**
     * Opens a date picker to select the Date of Birth.
     */
    private void showDatePicker() {
        final Calendar calendar = Calendar.getInstance();
        DatePickerDialog datePickerDialog = new DatePickerDialog(this, (DatePicker view, int selectedYear, int selectedMonth, int selectedDay) -> {
            String dob = selectedDay + "/" + (selectedMonth + 1) + "/" + selectedYear;
            dobEditText.setText(dob);
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.show();
    }

    /**
     * Saves user data (DOB, Phone, Address) and navigates to HomeActivity.
     */
    private void saveUserData() {
        String dob = dobEditText.getText().toString().trim();
        String phone = phoneEditText.getText().toString().trim();
        String address = addressEditText.getText().toString().trim();

        if (dob.isEmpty() || phone.isEmpty() || address.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        sharedPrefManager.saveUserDetails(
                sharedPrefManager.getUserName(),
                sharedPrefManager.getUserEmail(),
                sharedPrefManager.getUserProfilePic(),
                dob,
                phone,
                address
        );

        Toast.makeText(this, "Data Saved Successfully", Toast.LENGTH_SHORT).show();

        // Redirect to HomeActivity
        Intent intent = new Intent(ProfileActivity.this, HomeActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    /**
     * Loads user data from SharedPreferences and updates the UI.
     */
    private void loadUserData() {
        dobEditText.setText(sharedPrefManager.getUserDOB());
        phoneEditText.setText(sharedPrefManager.getUserPhone());
        addressEditText.setText(sharedPrefManager.getUserAddress());

        String encodedImage = sharedPrefManager.getUserProfilePic();
        if (encodedImage != null && !encodedImage.isEmpty()) {
            if (encodedImage.startsWith("http")) {
                Glide.with(this).load(encodedImage).into(imgUserProfile);
            } else {
                try {
                    byte[] decodedBytes = Base64.decode(encodedImage, Base64.DEFAULT);
                    Bitmap bitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
                    imgUserProfile.setImageBitmap(bitmap);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
