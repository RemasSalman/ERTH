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

public class SignUpDivActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private EditText et_fullName, et_password, et_email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up_div);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference("users");

        et_fullName = findViewById(R.id.FullName);
        et_email = findViewById(R.id.email);
        et_password = findViewById(R.id.Password);
    }

    public void signUpDB(View view) {
        String name = et_fullName.getText().toString();
        String email = et_email.getText().toString().trim();
        String password = et_password.getText().toString().trim();

        if (name.isEmpty()) {
            et_fullName.setError("Full Name is required");
            et_fullName.requestFocus();
            return;
        }
        if (email.isEmpty()) {
            et_email.setError("Email is required");
            et_email.requestFocus();
            return;
        }
        if (password.isEmpty() || password.length() < 6) {
            et_password.setError("Password is required and must be > 6");
            et_password.requestFocus();
            return;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            et_email.setError("Please provide valid Email");
            et_email.requestFocus();
            return;
        }

        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    User user = new User(name, email);

                    String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
                    mDatabase.child(userId).setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                String userId = mAuth.getCurrentUser().getUid();
                                mDatabase.child("users").child(userId);
                                mDatabase.child(userId).child("userType").setValue("individuals");
                                Toast.makeText(getApplicationContext(), "You has been registered!", Toast.LENGTH_LONG).show();
                                Intent intent = new Intent();
                                startActivity(intent.setClass(SignUpDivActivity.this, SignInDivActivity.class));
                            } else {
                                Toast.makeText(getApplicationContext(), "FAILED: " + task.getException().toString(), Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                } else {
                    Toast.makeText(getApplicationContext(), task.getException().toString(), Toast.LENGTH_LONG).show();
                }
            }
        });
    }

}