package com.example.mpprojectmp;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.auth.FirebaseAuth;

public class AddResearchActivity extends AppCompatActivity {

    private EditText editTextResearch;
    private Button btnPostResearch;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_research);

        editTextResearch = findViewById(R.id.editTextResearch);
        btnPostResearch = findViewById(R.id.btnPostResearch);
        databaseReference = FirebaseDatabase.getInstance().getReference("researches");

        btnPostResearch.setOnClickListener(view -> {
            String researchText = editTextResearch.getText().toString().trim();
            if (!researchText.isEmpty()) {
                String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
                databaseReference.child(userId).push().setValue(researchText)
                        .addOnSuccessListener(aVoid -> {
                            Toast.makeText(AddResearchActivity.this, "Research posted successfully", Toast.LENGTH_SHORT).show();
                            finish();
                        })
                        .addOnFailureListener(e -> {
                            Toast.makeText(AddResearchActivity.this, "Failed to post research", Toast.LENGTH_SHORT).show();
                        });
            } else {
                Toast.makeText(AddResearchActivity.this, "Please enter your research", Toast.LENGTH_SHORT).show();
            }
        });
    }
}