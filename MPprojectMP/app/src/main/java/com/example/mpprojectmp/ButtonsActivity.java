package com.example.mpprojectmp;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

public class ButtonsActivity extends AppCompatActivity {

    Button individualsButton, universitiesButton, researchersButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buttons);

        researchersButton = findViewById(R.id.researchersButton);
        universitiesButton = findViewById(R.id.universitiesButton);
        individualsButton = findViewById(R.id.individualsButton);

        researchersButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ButtonsActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });

        universitiesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ButtonsActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });


        individualsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ButtonsActivity.this, SignInDivActivity.class);
                startActivity(intent);
            }
        });
    }
}