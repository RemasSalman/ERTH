package com.example.mpprojectmp;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;


public class ProfileActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private ImageView profileImage;
    private TextView usernameText, researcherInfoText;
    private Button addResearchButton, viewResearchButton, backToHomeButton, logoutButton;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        profileImage = findViewById(R.id.profileImage);
        usernameText = findViewById(R.id.usernameText);
        researcherInfoText = findViewById(R.id.researcherInfoText);
        addResearchButton = findViewById(R.id.addResearchButton);
        viewResearchButton = findViewById(R.id.viewResearchButton);
        backToHomeButton = findViewById(R.id.backToHomeButton);
        logoutButton = findViewById(R.id.logoutButton);

        // load data
        loadResearcherProfile();

        // add articles
      //
        logoutButton.setOnClickListener(v -> {
            mAuth.signOut();
            Toast.makeText(ProfileActivity.this, "Logged Out", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(ProfileActivity.this, ButtonsActivity.class));
            finish();
        });


        bottomNavigation();
    }
    private void bottomNavigation() {
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigation);
        bottomNavigationView.setSelectedItemId(R.id.bottom_profile);

        bottomNavigationView.setOnItemSelectedListener(item ->{
            int itemId = item.getItemId();

            if(itemId == R.id.bottom_Map) {
                startActivity(new Intent(getApplicationContext(), BottomNavActivity.class));
                overridePendingTransition(R.anim.slide_right, R.anim.slide_left);
                finish();
                return true;
            } else if (itemId == R.id.bottom_home) {
                startActivity(new Intent(getApplicationContext(), MapActivity.class));
                overridePendingTransition(R.anim.slide_right, R.anim.slide_left);
                finish();
                return true;
            } else if (itemId == R.id.bottom_profile) {
                return true;
            }
            return false;

        } );
    }
    private void loadResearcherProfile() {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            String uid = user.getUid();
            db.collection("users").document(uid).get().addOnSuccessListener(document -> {
                if (document.exists()) {
                    String username = document.getString("username");
                    String researcherInfo = "Research Papers: " + document.getString("papers") + "\n"
                            + "Phone: " + document.getString("phone") + "\n"
                            + "Email: " + document.getString("email") + "\n"
                            + "Institution: " + document.getString("institution");

                    usernameText.setText(username);
                    researcherInfoText.setText(researcherInfo);
                } else {
                    Toast.makeText(this, "Researcher data not found", Toast.LENGTH_SHORT).show();
                }
            });
}
}
}
