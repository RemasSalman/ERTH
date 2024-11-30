package com.example.mpprojectmp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ProfileActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private DatabaseReference databaseReference;

    private ImageView profileImage;
    private TextView usernameText, emailText;
    private EditText editName, editPhone, editInstitution;
    private Button saveButton, logoutButton;
    private String userId, currentUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        // Get the current user's ID
        currentUserId = getCurrentUserId();

        // Check if Intent has userId, else use the current user's userId
        Intent intent = getIntent();
        userId = intent.hasExtra("userId") ? intent.getStringExtra("userId") : currentUserId;

        if (userId != null) {
            databaseReference = FirebaseDatabase.getInstance().getReference("users").child(userId);
        } else {
            Toast.makeText(this, "Unable to determine user", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Initialize Views
        profileImage = findViewById(R.id.profileImage);
        usernameText = findViewById(R.id.usernameText);
        emailText = findViewById(R.id.emailText);
        editName = findViewById(R.id.editName);
        editPhone = findViewById(R.id.editPhone);
        editInstitution = findViewById(R.id.editInstitution);
        saveButton = findViewById(R.id.saveButton);
        logoutButton = findViewById(R.id.logoutButton);

        // Hide buttons if viewing a different user's profile
        if (!userId.equals(currentUserId)) {
            saveButton.setVisibility(View.GONE);
            logoutButton.setVisibility(View.GONE);
        }

        // Load User Profile
        loadUserProfile();

        // Setup Bottom Navigation
        bottomNavigation();

        // Listener for editing username in real-time
        editName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                usernameText.setText(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        // Save Button Listener
        saveButton.setOnClickListener(v -> saveUserProfile());

        // Logout Button Listener
        logoutButton.setOnClickListener(v -> {
            mAuth.signOut();
            Toast.makeText(ProfileActivity.this, "Logged Out", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(ProfileActivity.this, LoginActivity.class));
            finish();
        });
    }

    private String getCurrentUserId() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        return currentUser != null ? currentUser.getUid() : null;
    }

    private void bottomNavigation() {
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigation);
        bottomNavigationView.setSelectedItemId(R.id.bottom_profile);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.bottom_Site) {
                startActivity(new Intent(getApplicationContext(), SitesCriticality.class));
                overridePendingTransition(R.anim.slide_right, R.anim.slide_left);
                finish();
                return true;
            } else if (itemId == R.id.bottom_home) {
                startActivity(new Intent(getApplicationContext(), BottomNavActivity.class));
                overridePendingTransition(R.anim.slide_right, R.anim.slide_left);
                finish();
                return true;
            } else if (itemId == R.id.bottom_profile) {
                return true;
            }
            return false;
        });
    }

    private void loadUserProfile() {
        databaseReference.get().addOnSuccessListener(snapshot -> {
            if (snapshot.exists()) {
                String name = snapshot.child("username").getValue(String.class);
                String phone = snapshot.child("phone").getValue(String.class);
                String institution = snapshot.child("institution").getValue(String.class);
                String email = snapshot.child("email").getValue(String.class);
                String userType = snapshot.child("userType").getValue(String.class);

                editName.setText(name);
                editPhone.setText(phone);
                editInstitution.setText(institution);
                usernameText.setText(name);
                emailText.setText(email);

                if ("researcher".equals(userType)) {
                    loadUserResearch();
                } else {
                    findViewById(R.id.researchText).setVisibility(View.GONE);
                    findViewById(R.id.researchListText).setVisibility(View.GONE);
                }
            } else {
                Toast.makeText(this, "User data not found", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(e ->
                Toast.makeText(this, "Failed to load data", Toast.LENGTH_SHORT).show()
        );
    }

    private void loadUserResearch() {
        databaseReference.child("researches").get().addOnSuccessListener(snapshot -> {
            if (snapshot.exists()) {
                StringBuilder researchList = new StringBuilder();
                for (DataSnapshot researchSnapshot : snapshot.getChildren()) {
                    String title = researchSnapshot.getValue(String.class);
                    researchList.append("\n - ").append(title);
                }
                TextView researchListText = findViewById(R.id.researchListText);
                researchListText.setText(researchList.toString());
            } else {
                TextView researchListText = findViewById(R.id.researchListText);
                researchListText.setText("No research found");
            }
        }).addOnFailureListener(e ->
                Toast.makeText(this, "Failed to load research", Toast.LENGTH_SHORT).show()
        );
    }

    private void saveUserProfile() {
        String name = editName.getText().toString();
        String phone = editPhone.getText().toString();
        String institution = editInstitution.getText().toString();

        databaseReference.child("username").setValue(name);
        databaseReference.child("phone").setValue(phone);
        databaseReference.child("institution").setValue(institution)
                .addOnSuccessListener(aVoid ->
                        Toast.makeText(this, "Profile updated successfully!", Toast.LENGTH_SHORT).show()
                ).addOnFailureListener(e ->
                        Toast.makeText(this, "Failed to update profile", Toast.LENGTH_SHORT).show()
                );
    }
}
