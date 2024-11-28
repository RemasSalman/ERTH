
        package com.example.mpprojectmp;


import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

import com.example.mpprojectmp.BottomNavActivity;
import com.example.mpprojectmp.MapActivity;
import com.example.mpprojectmp.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.FirebaseFirestore;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class SitesCriticality extends AppCompatActivity {

    private final ArrayList<Map<String, Serializable>> criticalSites = new ArrayList<>();
    private final ArrayList<Map<String, Serializable>> allSites = new ArrayList<>();
    private final double criticality = 80.0;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = FirebaseFirestore.getInstance();

        // Adding sample data
        addSiteData("Al-Hajar", "N26 47 1 E37 57 18", "2008", "no", "Sarah@gmail.com", "+966537851300", 1.1, 1.05, 1.0, 40.0, 10.0, 1.5);
        addSiteData("Al-Turaif", "N24 44 2.88 E46 34 20.88", "2010", "yes", "Raghad@gmail.com", "+966536789660", 1.25, 1.02, 1.01, 25.0, 4.0, 0.8);
        addSiteData("Cultural Fever", "N18 19 0.16 E44 32 43.21", "2021", "yes", "Ethar@gmail.com", "+966589765340", 1.1, 1.04, 1.02, 50.0, 15.0, 1.8);
        addSiteData("Rock Art", "N28 0 38 E40 54 47", "2015", "no", "Remas@gmail.com", "+966567895440", 2.5, 1.01, 1.00, 10.0, 1.0, 0.2);

        sendSitesToMapActivity();
        bottomNavigation();
    }

    // Method to convert DMS to Decimal
    private double convertDMS2Decimal(String dms) {
        String[] parts = dms.split(" ");
        double degrees = Double.parseDouble(parts[0].substring(1));
        double minutes = Double.parseDouble(parts[1]);
        double seconds = Double.parseDouble(parts[2]);

        // Convert to decimal degrees
        double decimal = degrees + (minutes / 60.0) + (seconds / 3600.0);

        // Adjust if it's S or W (negative latitude or longitude)
        if (dms.charAt(0) == 'S' || dms.charAt(0) == 'W') {
            decimal = -decimal;
        }
        return decimal;
    }

    // Add site data and calculate criticality score
    private void addSiteData(String siteName, String area, String registrationDate, String IsExpedition, String Email, String PhoneNumber, double carbon14, double carbon13, double carbon12, double humidity, double pollutant, double erosion) {
        double criticalityScore = calculateCriticalityScore(carbon14, carbon13, carbon12, humidity, pollutant, erosion);
        String criticalityState;

        if (criticalityScore >=80) {
            criticalityState = "critical";
        } else {
            criticalityState = "non-critical";
        }

        Map<String, Serializable> siteInfo = new HashMap<>();
        siteInfo.put("name", siteName);
        siteInfo.put("Area", area);
        siteInfo.put("date of registration", registrationDate);
        siteInfo.put("Expedition", IsExpedition);
        siteInfo.put("Email", Email);
        siteInfo.put("PhoneNumber", PhoneNumber);
        siteInfo.put("carbon 14 Ratio", carbon14);
        siteInfo.put("carbon 13 Ratio", carbon13);
        siteInfo.put("carbon 12 Ratio", carbon12);
        siteInfo.put("humidity Level", humidity);
        siteInfo.put("pollutant Level", pollutant);
        siteInfo.put("erosion Rate", erosion);
        siteInfo.put("Criticality Score", criticalityScore);
        siteInfo.put("Criticality State", criticalityState);


        // Convert DMS format coordinates to decimal
        String[] areaParts = area.split(" ");
        double latitude = convertDMS2Decimal(areaParts[0] + " " + areaParts[1] + " " + areaParts[2]);
        double longitude = convertDMS2Decimal(areaParts[3] + " " + areaParts[4] + " " + areaParts[5]);

        siteInfo.put("latitude", latitude);
        siteInfo.put("longitude", longitude);

        allSites.add(siteInfo);
        criticalSites.add(siteInfo);
    }

    // Calculate criticality score
    private double calculateCriticalityScore(double carbon14Ratio, double carbon13Ratio, double carbon12Ratio,
                                             double humidityLevel, double pollutantLevel, double erosionRate) {
        // Calculate individual impacts
        double c14Impact = 1 / carbon14Ratio;
        double isotopeRatioImpact = (carbon13Ratio / carbon12Ratio) * 100;
        double humidityImpact = humidityLevel * 0.3;
        double pollutantImpact = pollutantLevel * 0.2;
        double erosionImpact = erosionRate * 0.4;

        // Calculate total criticality score
        double criticalityScore = c14Impact + isotopeRatioImpact + humidityImpact + pollutantImpact + erosionImpact;

        return criticalityScore;
    }
    // Send data to MainActivity
    private void sendSitesToMapActivity() {
        Intent intent = new Intent(SitesCriticality.this, MapActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("all_sites", allSites);
        bundle.putSerializable("critical_sites", criticalSites);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    private void bottomNavigation() {
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigation);
        bottomNavigationView.setSelectedItemId(R.id.bottom_Site);
        bottomNavigationView.setOnItemSelectedListener(item -> {

            int itemId = item.getItemId();
            if (itemId ==  R.id.bottom_Site){
                return true;

            } else if (itemId ==  R.id.bottom_home) {
                startActivity(new Intent(getApplicationContext(), BottomNavActivity.class));
                overridePendingTransition(R.anim.slide_right, R.anim.slide_left);
                finish();
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

}

