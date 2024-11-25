package com.example.mpprojectmp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.List;


public class BottomNavActivity extends AppCompatActivity {

    private SearchView searchView;
    private ListView listView;
    private ArrayAdapter <String> adapter;
    private List<String> itemList; // This will hold your data
    private List<String> filteredList;
    private FloatingActionButton fabAddResearch;
    private ListView researchListView;
    private ArrayAdapter<String> researchAdapter;
    private List<String> researchList;
    private TextView tvResearchHeader;

    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_bottom_nav);

        searchView = findViewById(R.id.searchView);
        listView = findViewById(R.id.listView);
        fabAddResearch = findViewById(R.id.fab_add_research);
        fabAddResearch.setVisibility(View.GONE);
        researchListView = findViewById(R.id.researchListView);
        tvResearchHeader = findViewById(R.id.tv_research_header);


        databaseReference = FirebaseDatabase.getInstance().getReference("users"); // Adjust to your database node
        itemList = new ArrayList<>();
        filteredList = new ArrayList<>();
        researchList = new ArrayList<>();

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1
                , filteredList);
        listView.setAdapter(adapter);
        researchAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, researchList);
        researchListView.setAdapter(researchAdapter);

        loadDataFromFirebase();
        loadResearchesFromFirebase();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filterData(newText);
                // Show the ListView if there is a query
                if (!newText.isEmpty() && !filteredList.isEmpty()) {
                    listView.setVisibility(ListView.VISIBLE);
                    researchListView.setVisibility(ListView.GONE);
                    tvResearchHeader.setVisibility(TextView.GONE);
                } else {
                    listView.setVisibility(ListView.GONE);// Hide if the query is empty
                    researchListView.setVisibility(ListView.VISIBLE);
                    tvResearchHeader.setVisibility(TextView.VISIBLE);
                }
                return true;

            }
        });

        fabAddResearch.setOnClickListener(view -> {
            Intent intent = new Intent(BottomNavActivity.this, AddResearchActivity.class);
            startActivity(intent);
        });



        bottomNavigation();
    }


    private void bottomNavigation() {
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigation);
        bottomNavigationView.setSelectedItemId(R.id.bottom_home);
        bottomNavigationView.setOnItemSelectedListener(item -> {

            int itemId = item.getItemId();
            if (itemId ==  R.id.bottom_Map){
                startActivity(new Intent(getApplicationContext(), MapActivity.class));
                overridePendingTransition(R.anim.slide_right, R.anim.slide_left);
                finish();
                return true;
            } else if (itemId ==  R.id.bottom_home) {
                return true;
            }else if (itemId ==  R.id.bottom_profile) {
                startActivity(new Intent(getApplicationContext(), ProfileActivity.class));
                overridePendingTransition(R.anim.slide_right, R.anim.slide_left);
                finish();
                return true;
            }else
                return false;


        });
    }

    private void loadResearchesFromFirebase() {
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                researchList.clear();

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String fullName = snapshot.child("fullName").getValue(String.class);
                    String userType = snapshot.child("userType").getValue(String.class);

                    if ("researcher".equalsIgnoreCase(userType)) {
                        DataSnapshot researchesSnapshot = snapshot.child("researches");
                        for (DataSnapshot researchSnapshot : researchesSnapshot.getChildren()) {
                            String researchTitle = researchSnapshot.getValue(String.class);
                            if (fullName != null && researchTitle != null) {
                                researchList.add(fullName + " : is posted these researche\n " + researchTitle);
                            }
                        }
                    }
                }
                researchAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(BottomNavActivity.this, "Error: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void loadDataFromFirebase() {
        // Get the current user defined
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("users");

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                itemList.clear(); // Empty the list before adding new data

                // Read all data from the database
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String email = snapshot.child("email").getValue(String.class);
                    String fullName = snapshot.child("fullName").getValue(String.class);

                    // Merge full name with email
                    if (email != null && fullName != null) {
                        String combined = fullName + " (" + email + ")";
                        itemList.add(combined);
                    }

                    // If current user, check user type
                    if (snapshot.getKey().equals(userId)) {
                        String userType = snapshot.child("userType").getValue(String.class);


                        if ("researcher".equalsIgnoreCase(userType)) {
                            fabAddResearch.setVisibility(View.VISIBLE);
                        } else {
                            fabAddResearch.setVisibility(View.GONE);
                        }
                    }
                }
                // Display all data initially
                filteredList.clear();
                filteredList.addAll(itemList);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(BottomNavActivity.this, "Error: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void filterData(String query) {
        filteredList.clear(); // Clear the filtered list before adding new filtered data

        if (query.isEmpty()) {
            filteredList.addAll(itemList); // Show all items if the query is empty
        } else {
            // Filter results based on email and full name
            for (String item : itemList) {
                String[] parts = item.split(" \\("); // Split to get the full name and email
                String fullName = parts[0]; // Full name part
                String email = parts[1].replace(")", ""); // Remove closing parenthesis from email

                if (fullName.toLowerCase().contains(query.toLowerCase()) ||
                        email.toLowerCase().contains(query.toLowerCase())) {
                    filteredList.add(item); // Add to filtered results if it matches
                }
            }
        }
        adapter.notifyDataSetChanged(); // Notify the adapter about the changes in filtered data
    }
}
