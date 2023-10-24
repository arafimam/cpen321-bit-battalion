package com.example.triptrooperapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

/**
 * We can use the Main Activity as the Initial Login Page.
 * If the user is logged in directly go to another activity from the
 * main activity.
 */
public class MainActivity extends AppCompatActivity {

    private GreenButtonView signInButton; /* Sign in Button component.*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        signInButton = findViewById(R.id.sign_in_google);
        signInButton.setButtonText("Sign In With Google");
        signInButton.setButtonActionOnClick(view ->
                // TODO: Replace this google sign in auth.
                Toast.makeText(MainActivity.this, "Sign In Button Clicked", Toast.LENGTH_LONG).show());
    }
}