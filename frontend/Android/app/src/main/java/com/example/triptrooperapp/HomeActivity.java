package com.example.triptrooperapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

public class HomeActivity extends AppCompatActivity {

    private GreenButtonView signOutButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        signOutButton = findViewById(R.id.sign_out_google);
        signOutButton.setButtonText("Sign Out");

        signOutButton.setButtonActionOnClick(view ->
                // TODO: Replace this with Sign out functionality..
                {

                    Intent intent = new Intent(HomeActivity.this, MainActivity.class);
                    startActivity(intent);
                }
        );
    }
}