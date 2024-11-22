package com.example.mpprojectmp;


import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
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
        addSiteData("Al-Hajar", "N26 47 1 E37 57 18", "2008", 0.98, 1.02, 0.98, 60.0, 20.0, 2.5);
        addSiteData("Al-Turaif", "N24 44 2.88 E46 34 20.88", "2010", 0.80, 1.01, 0.97, 55.0, 25.0, 3.0);
        addSiteData("Cultural Fever", "N18 19 0.16 E44 32 43.21", "2021", 1.08, 1.02, 1.01, 42.0, 8.0, 1.3);
        addSiteData("Rock Art", "N28 0 38 E40 54 47", "2015", 1.18, 1.02, 1.01, 28.0, 7.0, 1.1);

        sendSitesToMapActivity();
    }

    // Method to convert DMS to Decimal
    private double convertDMS2Decimal(String dms) {
        String[] parts = dms.split(" ");
        double degrees = Double.parseDouble(parts[0].substring(1));  // Remove N/S/E/W and get degrees
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
    private void addSiteData(String siteName, String area, String registrationDate, double carbon14, double carbon13, double carbon12, double humidity, double pollutant, double erosion) {
        double criticalityScore = calculateCriticalityScore(carbon14, carbon13, carbon12, humidity, pollutant, erosion);

        Map<String, Serializable> siteInfo = new HashMap<>();
        siteInfo.put("name", siteName);
        siteInfo.put("Area", area);
        siteInfo.put("date of registration", registrationDate);
        siteInfo.put("carbon 14 Ratio", carbon14);
        siteInfo.put("carbon 13 Ratio", carbon13);
        siteInfo.put("carbon 12 Ratio", carbon12);
        siteInfo.put("humidity Level", humidity);
        siteInfo.put("pollutant Level", pollutant);
        siteInfo.put("erosion Rate", erosion);
        siteInfo.put("Criticality Score", criticalityScore);

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
    private double calculateCriticalityScore(double carbon14, double carbon13, double carbon12, double humidity, double pollutant, double erosion) {
        double carbonScore = (carbon14 + carbon13 + carbon12) / 3;
        double environmentScore = (humidity + pollutant + erosion) / 3;
        return carbonScore + environmentScore;
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
}
