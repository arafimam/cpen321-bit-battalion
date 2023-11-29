package com.example.triptrooperapp;


import android.Manifest;
import android.app.AlertDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;

import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Request;
import okhttp3.Response;

/**
 * We can use the Main Activity as the Initial Login Page.
 * If the user is logged in directly go to another activity from the
 * main activity.
 */
public class MainActivity extends AppCompatActivity {

    private final String TAG = "MAIN_ACTIVITY";
    private GoogleSignInClient mGoogleSignInClient;
    private ActivityResultLauncher<Intent> signInActivityIntent;
    private ProgressBar progressbar;

    private String deviceRegistrationToken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /* Sign in Button component.*/
        GreenButtonView signInButton = findViewById(R.id.sign_in_google);
        progressbar = findViewById(R.id.spinner);
        progressbar.setVisibility(View.GONE);

        GoogleSignInOptions gso =
                new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                        .requestIdToken(getString(R.string.web_client_id))
                        .requestProfile()
                        .requestEmail()
                        .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        signInActivityIntent = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        progressbar.setVisibility(View.VISIBLE);
                        Intent data = result.getData();
                        Bundle extras = data.getExtras();
                        if (extras != null) {
                            for (String key : extras.keySet()) {
                                Object value = extras.get(key);
                                Log.d("TAG", key + ": " + value);
                            }
                        }
                        Task<GoogleSignInAccount> task =
                                GoogleSignIn.getSignedInAccountFromIntent(data);
                        handleSignInResult(task);
                    }
                });


        signInButton.setButtonText("Sign In With Google");
        signInButton.setButtonActionOnClick(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signIn();
            }
        });

        ActivityResultLauncher<String> requestPermissionLauncher =
                registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                    if (isGranted) {
                        // FCM SDK (and your app) can post notifications.
                        Toast.makeText(this, "Notification permission granted!",
                                Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(this, "We need those permissions to " +
                                "show " +
                                "notifications!", Toast.LENGTH_SHORT).show();
                    }
                });

        if (ContextCompat.checkSelfPermission(
                this, android.Manifest.permission.POST_NOTIFICATIONS) ==
                PackageManager.PERMISSION_GRANTED) {

            Log.d(TAG, "PERMISSION GRANTED");
            // You can use the API that requires the permission.

        } else if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                android.Manifest.permission.POST_NOTIFICATIONS)) {

            new AlertDialog.Builder(this).setTitle("Please allow notification" +
                    " permissions").setMessage("This permission is required " +
                    "to show notifications").setNegativeButton("CANCEL",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Toast.makeText(MainActivity.this, "We need those " +
                                    "permissions to run!", Toast.LENGTH_LONG).show();
                            dialog.dismiss();
                        }
                    }).setPositiveButton("OK",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions(MainActivity.this,
                                    new String[]{android.Manifest.permission.POST_NOTIFICATIONS}, 1);
                        }
                    }).create().show();
        } else {
            requestPermissionLauncher.launch(
                    Manifest.permission.POST_NOTIFICATIONS);
        }

        //retrieve current firebase token

        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "Fetching FCM registration token " +
                                    "failed", task.getException());
                            return;
                        }

                        // Get new FCM registration token
                        String token = task.getResult();

                        deviceRegistrationToken = token;

                        // Log and toast

                        Log.d(TAG, token);
                        //Toast.makeText(MainActivity.this, token, Toast
                        // .LENGTH_SHORT).show();
                        //TODO:Send token to backend here
                    }
                });

        createNotificationChannel();


    }

    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is not in the Support Library.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.channel_name);
            String description = getString(R.string.channel_description);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("CHANNEL_ID"
                    , name, importance);
            channel.setDescription(description);
            // Register the channel with the system. You can't change the
            // importance
            // or other notification behaviors after this.
            NotificationManager notificationManager =
                    getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account =
                    completedTask.getResult(ApiException.class);
            setLoggedInUser(account);
        } catch (ApiException e) {
            setLoggedInUser(null);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        progressbar.setVisibility(View.VISIBLE);
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        if (account != null) {
            progressbar.setVisibility(View.GONE);
            Intent intent = new Intent(MainActivity.this, HomeActivity.class);
            startActivity(intent);
        }
        progressbar.setVisibility(View.GONE);
    }

    private void signIn() {
        // get last signed in.
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        // user is not already signed in.
        if (account == null) {
            Intent signInIntent = mGoogleSignInClient.getSignInIntent();
            signInActivityIntent.launch(signInIntent);
        }
        // user is logged in. So launch loginServerInfo activity.
        else {
            setLoggedInUser(account);
        }
    }

    private void setLoggedInUser(GoogleSignInAccount account) {
        if (account == null) {
            progressbar.setVisibility(View.GONE);
            Toast.makeText(MainActivity.this, "You need to log in to open " +
                    "this page.", Toast.LENGTH_LONG).show();
        } else {
            Log.d("TAG", account.getIdToken());
            setTokenToBackend(account.getIdToken(), account.getDisplayName(),
                    deviceRegistrationToken);
        }
    }

    private void setTokenToBackend(String idToken, String username,
                                   String deviceRegistrationToken) {
        JSONObject json = new JSONObject();
        try {
            json.put("idToken", idToken);
            json.put("username", username);
            json.put("deviceRegistrationToken", deviceRegistrationToken);

            Log.d("GOOGLETOKEN", idToken);
        } catch (Exception e) {
            e.printStackTrace();
        }

        Request request = BackendServiceClass.loginUserPostRequest(json);
        new Thread(() -> {

            Response loginResponse =
                    BackendServiceClass.getResponseFromRequest(request);
            if (loginResponse.isSuccessful()) {
                runOnUiThread(() -> {
                    progressbar.setVisibility(View.GONE);
                    Intent intent = new Intent(MainActivity.this,
                            HomeActivity.class);
                    startActivity(intent);
                });
            } else {
                try {
                    Log.d("ERR", loginResponse.body().string());
                } catch (IOException e) {
                    throw new CustomException("error", e);
                }
                runOnUiThread(() -> {
                    progressbar.setVisibility(View.GONE);
                    Toast.makeText(MainActivity.this, "Unable to login. " +
                            "Please try again", Toast.LENGTH_SHORT).show();
                });
            }
        }).start();
    }
}