package com.example.mpprojectmp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

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

    public void loginMethod(View view) {
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

        // Sign in with email and password
        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    // Check user type in the Realtime Database
                    String userId = mAuth.getCurrentUser().getUid();
                    mDatabase.child(userId).child("userType").get().addOnCompleteListener(task1 -> {
                        if (task1.isSuccessful() && task1.getResult().exists()) {
                            String userType = task1.getResult().getValue(String.class);
                            if ("registered".equals(userType)) {
                                Toast.makeText(SignInDivActivity.this, "This user cannot log in.", Toast.LENGTH_LONG).show();
                                mAuth.signOut(); // Log out the user
                            } else {
                                // Redirect to the main page if login is successful
                                Toast.makeText(getApplicationContext(), "Login Successful!", Toast.LENGTH_LONG).show();
                                startActivity(new Intent(SignInDivActivity.this, ButtonsActivity.class));
                            }
                        } else {
                            Toast.makeText(SignInDivActivity.this, "User type not found.", Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    Toast.makeText(getApplicationContext(), task.getException().toString(), Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}