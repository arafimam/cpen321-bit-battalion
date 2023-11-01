package com.example.triptrooperapp;


import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;


import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;


import com.google.android.gms.auth.api.identity.BeginSignInRequest;
import com.google.android.gms.auth.api.identity.Identity;
import com.google.android.gms.auth.api.identity.SignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;


import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Request;
import okhttp3.Response;

import com.example.triptrooperapp.FirebaseMessageService;
import com.google.firebase.messaging.FirebaseMessaging;

/**
 * We can use the Main Activity as the Initial Login Page.
 * If the user is logged in directly go to another activity from the
 * main activity.
 */
public class MainActivity extends AppCompatActivity {

    private GreenButtonView signInButton; /* Sign in Button component.*/
    private SignInClient oneTapClient;
    private String TAG = "MAIN_ACTIVITY";
    private GoogleSignInClient mGoogleSignInClient;
    private ActivityResultLauncher<Intent> signInActivityIntent;
    private ActivityResultLauncher<String> requestPermissionLauncher;
    private ProgressBar progressbar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        signInButton = findViewById(R.id.sign_in_google);
        progressbar = findViewById(R.id.spinner);
        progressbar.setVisibility(View.GONE);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
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
                        Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
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

        //TODO:clean up request permission code
        requestPermissionLauncher = registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
            if (isGranted) {
                // FCM SDK (and your app) can post notifications.
                Toast.makeText(this,"Notification permission granted!",Toast.LENGTH_SHORT).show();
            } else {
                // TODO: Inform user that that your app will not show notifications.
                Toast.makeText(this,"We need those permissions to show notifications!",Toast.LENGTH_SHORT).show();
            }
        });

        if (ContextCompat.checkSelfPermission(
                this, android.Manifest.permission.POST_NOTIFICATIONS) ==
                PackageManager.PERMISSION_GRANTED) {

            Log.d(TAG,"PERMISSION GRANTED");
            // You can use the API that requires the permission.

        } else if (ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.POST_NOTIFICATIONS)) {
            // In an educational UI, explain to the user why your app requires this
            // permission for a specific feature to behave as expected, and what
            // features are disabled if it's declined. In this UI, include a
            // "cancel" or "no thanks" button that lets the user continue
            // using your app without granting the permission.
            new AlertDialog.Builder(this).setTitle("Please allow notification permissions").setMessage("This permission is required to show notifications").setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Toast.makeText(MainActivity.this,"We need those permissions to run!",Toast.LENGTH_LONG).show();
                    dialog.dismiss();
                }
            }).setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    ActivityCompat.requestPermissions(MainActivity.this,new String[] {android.Manifest.permission.POST_NOTIFICATIONS},1);
                }
            }).create().show();
        } else {
            // You can directly ask for the permission.
            // The registered ActivityResultCallback gets the result of this request.
            requestPermissionLauncher.launch(
                    Manifest.permission.POST_NOTIFICATIONS);
        }

        //retrieve current firebase token

        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "Fetching FCM registration token failed", task.getException());
                            return;
                        }

                        // Get new FCM registration token
                        String token = task.getResult();

                        // Log and toast

                        Log.d(TAG, token);
                        Toast.makeText(MainActivity.this, token, Toast.LENGTH_SHORT).show();
                        //TODO:Send token to backend here
                    }
                });
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
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
        if (account != null){
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
        if (account == null){
            Intent signInIntent = mGoogleSignInClient.getSignInIntent();
            signInActivityIntent.launch(signInIntent);
        }
        // user is logged in. So launch loginServerInfo activity.
        else{
            setLoggedInUser(account);
        }
    }

    private void setLoggedInUser(GoogleSignInAccount account){
        if (account == null){
            progressbar.setVisibility(View.GONE);
            Toast.makeText(MainActivity.this, "You need to log in to open this page.", Toast.LENGTH_LONG).show();
        }
        else{
            setTokenToBackend(account.getIdToken(), account.getDisplayName());
        }
    }

    private void setTokenToBackend(String idToken, String username){
        JSONObject json = new JSONObject();
        try{
            json.put("idToken", idToken);
            json.put("username",username);
        }catch (Exception e){
            e.printStackTrace();
        }
        BackendServiceClass backendService = new BackendServiceClass("users/login",json);
        Request request = backendService.getPostRequestWithJsonParameter();
        new Thread(()-> {

            Response loginResponse = backendService.getResponseFromRequest(request);
            if (loginResponse.isSuccessful()){
                runOnUiThread(()->{
                    progressbar.setVisibility(View.GONE);
                    Intent intent = new Intent(MainActivity.this, HomeActivity.class);
                    startActivity(intent);
                });
            }
            else {
                runOnUiThread(()->{
                    progressbar.setVisibility(View.GONE);
                    Toast.makeText(MainActivity.this, "Unable to login. Please try again", Toast.LENGTH_SHORT).show();
                });
            }
        }).start();
    }
}