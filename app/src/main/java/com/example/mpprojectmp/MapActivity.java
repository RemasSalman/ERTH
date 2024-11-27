package com.example.mpprojectmp;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Map;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback {

    private final ArrayList<Map<String, Serializable>> criticalSites = new ArrayList<>();
    private GoogleMap Map;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            criticalSites.addAll((ArrayList<Map<String, Serializable>>) extras.getSerializable("critical_sites"));
        }
        bottomNavigation();
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        Map = googleMap;

        for (Map<String, Serializable> site : criticalSites) {
            double latitude = (double) site.get("latitude");
            double longitude = (double) site.get("longitude");
            String siteName = site.get("name").toString();

            LatLng siteLocation = new LatLng(latitude, longitude);

            Map.addMarker(new MarkerOptions()
                            .position(siteLocation)
                            .title(siteName)
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)))
                    .setTag(site);
        }

        if (!criticalSites.isEmpty()) {
            LatLng firstSite = new LatLng(
                    (double) criticalSites.get(0).get("latitude"),
                    (double) criticalSites.get(0).get("longitude")
            );
            Map.moveCamera(CameraUpdateFactory.newLatLngZoom(firstSite, 10));
        }

        Map.setOnMarkerClickListener(marker -> {
            Map<String, Serializable> siteDetails = (Map<String, Serializable>) marker.getTag();
            if (siteDetails != null) {
                showBottomSheet(siteDetails);
            }
            return true; // Returning true indicates that the event is consumed
        });
    }

    private void showBottomSheet(Map<String, Serializable> siteDetails) {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this, R.style.ThemeOverlay_Custom_BottomSheetDialog);
        View bottomSheetView = LayoutInflater.from(this).inflate(R.layout.bottom_sheet_layout, null);

        // Set up the UI elements
        ImageView productImage = bottomSheetView.findViewById(R.id.product_image);
        TextView productName = bottomSheetView.findViewById(R.id.product_name);
        TextView productDetails = bottomSheetView.findViewById(R.id.product_details);
        LinearLayout addToFavorites = bottomSheetView.findViewById(R.id.add_to_favorites);
        LinearLayout shareProduct = bottomSheetView.findViewById(R.id.share_product);

        // Populate data
        productName.setText(siteDetails.get("name").toString());
        productDetails.setText("Latitude: " + siteDetails.get("latitude") + ", \nLongitude: " + siteDetails.get("longitude"));
        // Set a placeholder image for now
        productImage.setImageResource(R.drawable.ic_placeholder);

        // Add click listeners
        addToFavorites.setOnClickListener(v -> {
            // Add logic to handle "Add to Favorites" click here
        });

        shareProduct.setOnClickListener(v -> {
            // Add logic to handle "Share Product Via" click here
        });

        FrameLayout bottomSheet = bottomSheetDialog.findViewById(com.google.android.material.R.id.design_bottom_sheet);
        if (bottomSheet != null) {
            BottomSheetBehavior<View> behavior = BottomSheetBehavior.from(bottomSheet);
            behavior.setPeekHeight(300); // Adjust as needed
            behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
            bottomSheet.setBackgroundResource(R.drawable.rounded_top_background); // Apply rounded background
        }
        bottomSheetDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        bottomSheetDialog.setContentView(bottomSheetView);
        bottomSheetDialog.show();

    }

    private void bottomNavigation() {
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigation);
        bottomNavigationView.setSelectedItemId(R.id.bottom_Map);
        bottomNavigationView.setOnItemSelectedListener(item -> {

            int itemId = item.getItemId();
            if (itemId == R.id.bottom_profile) {
                startActivity(new Intent(getApplicationContext(), ProfileActivity.class));
                overridePendingTransition(R.anim.slide_right, R.anim.slide_left);
                finish();
                return true;
            } else if (itemId == R.id.bottom_home) {
                startActivity(new Intent(getApplicationContext(), MapActivity.class));
                overridePendingTransition(R.anim.slide_right, R.anim.slide_left);
                finish();
                return true;
            } else if (itemId == R.id.bottom_Map){
                return true;
            }else
                return false;
        });
    }


}
