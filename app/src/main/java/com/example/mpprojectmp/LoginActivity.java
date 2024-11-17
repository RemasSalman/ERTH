package com.example.mpprojectmp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class LoginActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private EditText et_email, et_password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference("users");
        et_email = findViewById(R.id.emailEditText);
        et_password = findViewById(R.id.passwordEditText);
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
                    String userId = mAuth.getCurrentUser().getUid();
                    mDatabase.child(userId).child("userType").get().addOnCompleteListener(task1 -> {
                        if (task1.isSuccessful() && task1.getResult().exists()) {
                            String userType = task1.getResult().getValue(String.class);
                            if ("individuals".equals(userType)) {
                                Toast.makeText(LoginActivity.this, "access denied", Toast.LENGTH_SHORT).show();
                             } else {
                                Toast.makeText(LoginActivity.this, "User type not recognized.", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(LoginActivity.this, "User type not found.", Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    Toast.makeText(getApplicationContext(), "Authentication failed.", Toast.LENGTH_LONG).show();
                }
            }
  });
}
}