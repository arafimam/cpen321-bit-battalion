package com.example.triptrooperapp;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

/**
 * Activities screen.
 */
public class PlacesActivity extends AppCompatActivity {

    private LinearLayout placesBoxContainer;
    private ProgressBar loader;

    private TextView textHeader;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_groups);

        placesBoxContainer = findViewById(R.id.list_layout_container);
        loader = findViewById(R.id.loader);
        textHeader = findViewById(R.id.group_header);

        textHeader.setText(R.string.place_list_header);
        loader.setVisibility(View.INVISIBLE);

        //TODO: Get location permissions

        Log.d("PLACES","Starting up places activity");



        // TODO: REPLACE WITH BACKEND API CALL TO RETRIEVE Places. CREATE A DS. TO HOLD THE BACKEND API CALL THEN USE THE FOR LOOP BELOW.
        for (int i=0; i<10;i++){
            PlacesBoxComponentView placesBoxComponentView = new PlacesBoxComponentView(PlacesActivity.this);

           placesBoxComponentView.setMainTitle2Text("Name- " + (i + 1));
            //placesBoxComponentView.setSubTitleText(String.format("%d%d%d%d",i,(i+3),(i+2),(i+1)));
           // placesBoxComponentView.setSubTitleText("Status- Open Now");

            placesBoxComponentView.setSideTitle2Text("Address: 1234");
            placesBoxComponentView.setMainTitleText("Open Now: Yes");
            placesBoxComponentView.setSideTitleText("Rating: 4");
            int finalI = i;

            placesBoxContainer.addView(placesBoxComponentView);

        }


    }
}