package com.example.triptrooperapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

/**
 * Activities screen.
 */
public class ActivitiesActivity extends AppCompatActivity {

    private GreenButtonView viewActivityByDestinationButton;
    private GreenButtonView viewActivityByCurrentLocationButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_activities);

        initializeActivityScreenButton();

        //TODO: action for the above two buttons.
    }

    /**
     * Initializes activity screen button.
     */
    private void initializeActivityScreenButton() {
        viewActivityByDestinationButton = findViewById(R.id.activity_destination);
        viewActivityByCurrentLocationButton = findViewById(R.id.activity_location);

        viewActivityByCurrentLocationButton.setButtonText("View Activities nearby");
        viewActivityByDestinationButton.setButtonText("View Activities for your destination");
    }
}