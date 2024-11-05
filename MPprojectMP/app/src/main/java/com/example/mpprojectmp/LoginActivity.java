package com.example.mpprojectmp;
import android.content.Intent;
import android.os.Bundle;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;


public class LoginActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private EditText et_email, et_password;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //you tell the Dalvik VM to run your code in addition to the existing code in the onCreate() of the parent class
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mAuth = FirebaseAuth.getInstance();
        et_email = findViewById(R.id.emailEditText);
        et_password = findViewById(R.id.passwordEditText);
    }

    public void loginMethod(View view) {
        String email = et_email.getText().toString().trim();
        String password = et_password.getText().toString().trim();
        if(email.isEmpty()){
            et_email.setError("Email is required");
            et_email.requestFocus();
            return;
        }
        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            et_email.setError("Please provide valid Email");
            et_email.requestFocus();
            return;
        }
        if(password.isEmpty() || password.length() < 6){ //you can also check the num. of chars.
            et_password.setError("Password is required and must be > 6");
            et_password.requestFocus();
            return;
        }
        mAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    Toast.makeText(getApplicationContext(), "CORRECT!", Toast.LENGTH_LONG).show();
                }
                else{
                    Toast.makeText(getApplicationContext(), task.getException().toString(), Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}



