package com.example.mpprojectmp;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Objects;

public class SignInDivActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private EditText et_email, et_password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in_div);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference("users");
        et_email = findViewById(R.id.email_Signin);
        et_password = findViewById(R.id.password_Signin);

    }

    public void registerPage(View view) {
        Intent intent = new Intent(this, SignUpDivActivity.class);
        startActivity(intent);
    }

    public void SigninMethod(View view) {
        String email = et_email.getText().toString().trim();
        String password = et_password.getText().toString().trim();

        // Input validation
        if (email.isEmpty()) {
            et_email.setError("Email is required");
            et_email.requestFocus();
            return;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            et_email.setError("Please provide valid Email");
            et_email.requestFocus();
            return;
        }
        if (password.isEmpty() || password.length() < 6) {
            et_password.setError("Password is required and must be > 6");
            et_password.requestFocus();
            return;
        }

        // Attempt to sign in
        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                checkUserType();
            } else {
                Toast.makeText(getApplicationContext(), Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void checkUserType() {
        String userId = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();
        mDatabase.child(userId).child("userType").get().addOnCompleteListener(task1 -> {
            if (task1.isSuccessful()) {
                if (task1.getResult().exists()) {
                    handleUserType(task1.getResult().getValue(String.class));
                } else {
                    Toast.makeText(SignInDivActivity.this, "User type not found.", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(SignInDivActivity.this, "Database error: " + Objects.requireNonNull(task1.getException()).getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void handleUserType(String userType) {
        if ("registered".equals(userType)) {
            Toast.makeText(SignInDivActivity.this, "This user cannot log in.", Toast.LENGTH_LONG).show();
            mAuth.signOut(); // Log out the user
        } else {
            Toast.makeText(getApplicationContext(), "Login Successful!", Toast.LENGTH_LONG).show();
            navigateToHome();
        }
    }

    private void navigateToHome() {
        Intent intent = new Intent(SignInDivActivity.this, BottomNavActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish(); // Close SignInDivActivity
    }
}