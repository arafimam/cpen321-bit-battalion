package com.example.triptrooperapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class PlaceDetails extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place_details);

        TextView placeName = findViewById(R.id.placeName);
        TextView address = findViewById(R.id.addressText);
        TextView rating = findViewById(R.id.ratingText);
        GreenButtonView gmapBtn = findViewById(R.id.view_in_map);

        Intent intent = getIntent();

        placeName.setText(intent.getStringExtra("placeName"));
        address.setText(intent.getStringExtra("address"));
        rating.setText(intent.getStringExtra("rating") + "/5");
        gmapBtn.setButtonText("View in Google Maps");

        double longitude = intent.getDoubleExtra("longitude", 0.0);
        double latitude = intent.getDoubleExtra("latitude", 0.0);


        gmapBtn.setButtonActionOnClick(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent mapsIntent = new Intent(PlaceDetails.this,
                        MapsActivity.class);
                mapsIntent.putExtra("placeName", intent.getStringExtra(
                        "placeName"));
                mapsIntent.putExtra("latitude", latitude);
                mapsIntent.putExtra("longitude", longitude);
                mapsIntent.putExtra("context", "onePlace");


                startActivity(mapsIntent);
            }
        });

    }


}