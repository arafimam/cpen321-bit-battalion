package com.example.triptrooperapp;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.FragmentActivity;

import com.example.triptrooperapp.databinding.ActivityMapsBinding;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GreenButtonView summaryButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActivityMapsBinding binding =
                ActivityMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        summaryButton = findViewById(R.id.summary);
        summaryButton.setButtonText("Schedule Summary");
        summaryButton.setVisibility(View.GONE);

        // Obtain the SupportMapFragment and get notified when the map is
        // ready to be used.
        SupportMapFragment mapFragment =
                (SupportMapFragment) getSupportFragmentManager()
                        .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the
     * camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will
     * be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered
     * once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {


        Intent intent = getIntent();
        if (intent.getStringExtra("context").equals("onePlace")) {
            double latitude = intent.getDoubleExtra("latitude", 0.0);
            double longitude = intent.getDoubleExtra("longitude", 0.0);
            String placeName = intent.getStringExtra("placeName");

            LatLng place = new LatLng(latitude, longitude);
            Marker marker =
                    googleMap.addMarker(new MarkerOptions().position(place).title(placeName));
            marker.setTag(placeName);
            if (marker != null) {
                marker.showInfoWindow();
            }

            googleMap.moveCamera(CameraUpdateFactory.newLatLng(place));

            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(place, 15));
        }

        // for complex algo.
        else {
            summaryButton.setVisibility(View.VISIBLE);
            ArrayList<String> placeNames = intent.getStringArrayListExtra(
                    "placeNames");
            ArrayList<String> lats = intent.getStringArrayListExtra(
                    "latitudes");
            ArrayList<String> longs = intent.getStringArrayListExtra(
                    "longitudes");
            ArrayList<String> addresses = intent.getStringArrayListExtra(
                    "addresses");
            ArrayList<String> ratings = intent.getStringArrayListExtra(
                    "ratings");

            String summary = "Your Optimized schedule sequence:\n";
            for (int i = 0; i < placeNames.size(); i++) {
                summary += "\n" + (i + 1) + " " + placeNames.get(i) +
                        "\nAddress: " +
                        addresses.get(i) + "\n" + "Rating: " + ratings.get(i) +
                        "/5\n\n";
            }
            String finalSummary = summary;
            summaryButton.setButtonActionOnClick(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    AlertDialog.Builder builder =
                            new AlertDialog.Builder(MapsActivity.this);
                    builder.setMessage(
                                    finalSummary)
                            .setTitle(
                                    "Our recommendation");
                    builder.setNegativeButton("Close",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface,
                                                    int i) {
                                    builder.create().dismiss();
                                }
                            });
                    builder.create().show();
                }
            });

            int[] colors = new int[]{Color.BLUE, Color.RED, Color.GREEN,
                    Color.MAGENTA, Color.CYAN};

            if (placeNames.size() > 1) {
                for (int i = 0; i < placeNames.size(); i++) {
                    double latitude = Double.parseDouble(lats.get(i));
                    double longitude = Double.parseDouble(longs.get(i));
                    String placeName = placeNames.get(i);

                    LatLng place = new LatLng(latitude, longitude);
                    Marker marker =
                            googleMap.addMarker(new MarkerOptions().position(place).title((i + 1) + ". " + placeName));
                    marker.setTag("Marker");
                    if (i == 0) {
                        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(place, 15));
                    } else {

                        int colorIndex = i % colors.length;

                        int polylineColor = colors[colorIndex];
                        LatLng previousPlace =
                                new LatLng(Double.parseDouble(lats.get(i - 1)), Double.parseDouble(longs.get(i - 1)));
                        googleMap.addPolyline(new PolylineOptions()
                                .add(previousPlace, place)
                                .width(10)
                                .color(polylineColor));
                    }
                }
            }
        }
    }
}