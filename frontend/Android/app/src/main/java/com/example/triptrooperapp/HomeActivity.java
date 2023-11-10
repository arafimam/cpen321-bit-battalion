package com.example.triptrooperapp;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

public class HomeActivity extends AppCompatActivity {

    private GoogleSignInClient mGoogleSignInClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // initialize sign out button.
        GreenButtonView signOutButton = findViewById(R.id.sign_out_google);
        signOutButton.setButtonText("Sign Out");

        // initialize google sign in client.
        GoogleSignInOptions gso =
                new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                        .requestIdToken(getString(R.string.web_client_id))
                        .requestProfile()
                        .requestEmail()
                        .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        // sign out button action.
        signOutButton.setButtonActionOnClick(view ->
                {
                    signOut();
                }
        );
    }

    /**
     * Signs out the user and navigates the user back to Main activity
     * which is the sign in screen.
     */
    private void signOut() {
        mGoogleSignInClient.signOut().addOnCompleteListener(HomeActivity.this
                , new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Intent intent = new Intent(HomeActivity.this,
                                MainActivity.class);
                        startActivity(intent);
                    }
                });
    }

}