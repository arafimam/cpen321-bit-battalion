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

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

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
                ExecutorService service = Executors.newFixedThreadPool(1);
                if(result.getResultCode() == Activity.RESULT_OK){
                    try {
                        SignInCredential credential = oneTapClient.getSignInCredentialFromIntent(result.getData());
                        String idToken = credential.getGoogleIdToken();
                        Log.d(TAG, idToken);
                        if(idToken!=null){

                            service.execute(new Runnable() {
                                @Override
                                public void run() {
                                    JSONObject json = new JSONObject();
                                    try {
                                        json.put("idToken", idToken);
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                        return;
                                    }
                                    Log.d(TAG, json.toString());

                                    MediaType JSON = MediaType.parse("application/json; charset=utf-8");

                                    RequestBody body = RequestBody.create(json.toString(), JSON);
                                    OkHttpClient client = new OkHttpClient();

                                    Request request = new Request.Builder()
                                            .url("https://10.0.2.2:8081/users/login")
                                            .post(body)
                                            .build();
                                    Log.d(TAG, body.toString());
                                    client.newCall(request).enqueue(new Callback() {
                                        @Override
                                        public void onFailure(Call call, IOException e) {
                                            e.printStackTrace();
                                            Log.d(TAG, call.toString());
                                        }

                                        @Override
                                        public void onResponse(Call call, Response response) throws IOException {
                                            if (response.isSuccessful()){
                                                Log.d(TAG, "hello");
                                                String responseBody = response.body().string();
                                                Log.d(TAG, responseBody);
                                            } else {
                                                Log.d(TAG, "handleError");
                                            }
                                        }
                                    });

                                }
                            });
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