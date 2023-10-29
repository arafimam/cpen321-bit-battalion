package com.example.triptrooperapp;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.IntentSenderRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.content.IntentSender;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.auth.api.identity.BeginSignInRequest;
import com.google.android.gms.auth.api.identity.BeginSignInResult;
import com.google.android.gms.auth.api.identity.Identity;
import com.google.android.gms.auth.api.identity.SignInClient;
import com.google.android.gms.auth.api.identity.SignInCredential;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

/**
 * We can use the Main Activity as the Initial Login Page.
 * If the user is logged in directly go to another activity from the
 * main activity.
 */
public class MainActivity extends AppCompatActivity {

    private GreenButtonView signInButton; /* Sign in Button component.*/
    private SignInClient oneTapClient;
    private BeginSignInRequest signUpRequest;
    private String TAG = "MAIN_ACTIVITY";

    private static final int REQ_ONE_TAP = 2;  // Can be any integer unique to the Activity.
    private boolean showOneTapUI = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        signInButton = findViewById(R.id.sign_in_google);

        /* Code for configuring one tap client */
        oneTapClient = Identity.getSignInClient(this);
        signUpRequest = BeginSignInRequest.builder()
                .setGoogleIdTokenRequestOptions(BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                        .setSupported(true)
                        // Your server's client ID, not your Android client ID.
                        .setServerClientId(getString(R.string.web_client_id))
                        // Show all accounts on the device.
                        .setFilterByAuthorizedAccounts(false)
                        .build())
                .build();

        /* Activity result launcher for one tap ui */

        ActivityResultLauncher<IntentSenderRequest> activityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartIntentSenderForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {
                if(result.getResultCode() == Activity.RESULT_OK){
                    try {
                        SignInCredential credential = oneTapClient.getSignInCredentialFromIntent(result.getData());
                        String idToken = credential.getGoogleIdToken();

                        if(idToken!=null){
                            Log.d(TAG,idToken);
                            Toast.makeText(getApplicationContext(),"ID token: "+idToken,Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(MainActivity.this, HomeActivity.class);
                            startActivity(intent);
                        }
                    } catch (ApiException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        });

        signInButton.setButtonText("Sign In With Google");
        signInButton.setButtonActionOnClick(view -> {
            // TODO: Replace this google sign in auth.
            // Toast.makeText(MainActivity.this, "Sign In Button Clicked", Toast.LENGTH_LONG).show();
            oneTapClient.beginSignIn(signUpRequest)
                    .addOnSuccessListener(this, new OnSuccessListener<BeginSignInResult>() {
                        @Override
                        public void onSuccess(BeginSignInResult result) {
                            IntentSenderRequest intentSenderRequest = new IntentSenderRequest.Builder(result.getPendingIntent().getIntentSender()).build();
                            activityResultLauncher.launch(intentSenderRequest);
                        }
                    })
                    .addOnFailureListener(this, new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            // No Google Accounts found. Just continue presenting the signed-out UI.
                            Log.d(TAG, e.getLocalizedMessage());
                        }
                    });

                    /**
                    // TODO: Should launch this activity once user authenticated.
                    Intent intent = new Intent(MainActivity.this, HomeActivity.class);
                    startActivity(intent);
                     **/
                }
        );
    }
}