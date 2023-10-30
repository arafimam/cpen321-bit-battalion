package com.example.triptrooperapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.auth.api.identity.Identity;
import com.google.android.gms.auth.api.identity.SignInClient;

public class HomeActivity extends AppCompatActivity {

    private GreenButtonView signOutButton;
    private SignInClient oneTapClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        signOutButton = findViewById(R.id.sign_out_google);
        signOutButton.setButtonText("Sign Out");

        oneTapClient = Identity.getSignInClient(this);

        signOutButton.setButtonActionOnClick(view ->
                // TODO: Replace this with Sign out functionality..
                {
                    oneTapClient.signOut();
                    Intent intent = new Intent(HomeActivity.this, MainActivity.class);
                    startActivity(intent);
                }
        );
    }
}