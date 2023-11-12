package com.example.triptrooperapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class placeDetails extends AppCompatActivity {


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

    }


}