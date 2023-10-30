package com.example.triptrooperapp;


import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;


import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;


import com.google.android.gms.auth.api.identity.BeginSignInRequest;
import com.google.android.gms.auth.api.identity.Identity;
import com.google.android.gms.auth.api.identity.SignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;


import org.json.JSONObject;

import java.io.IOException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

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
    private BeginSignInRequest signInRequest;

    private String TAG = "MAIN_ACTIVITY";
    private GoogleSignInClient mGoogleSignInClient;
    private ActivityResultLauncher<Intent> signInActivityIntent;
    private static final int REQ_ONE_TAP = 2;  // Can be any integer unique to the Activity.
    private boolean showOneTapUI = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        signInButton = findViewById(R.id.sign_in_google);

        /* Code for configuring one tap client */
        oneTapClient = Identity.getSignInClient(this);

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


    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            setLoggedInUser(account);
        } catch (ApiException e) {
            setLoggedInUser(null);
        }
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
            Toast.makeText(MainActivity.this, "You need to log in to open this page", Toast.LENGTH_LONG).show();
        }
        else{
            if (account.getIdToken() == null){
                Log.d("Tag", "No Token");
            }
            else {
                Log.d("Tag","YEYYE");
            }
            Log.d("Tag", account.getIdToken());
            sendIdTokenToBackend(account.getIdToken(), account.getId());
        }
    }


    private void sendIdTokenToBackend(String idToken, String username){
        OkHttpClient client = getOkHttpClient();
        String url = "https://34.220.237.44:8081/users/login";
        JSONObject json = new JSONObject();
        try{
            json.put("idToken", idToken);
            json.put("username",username);
        }catch (Exception e){
            e.printStackTrace();
        }

        MediaType JSON = MediaType.parse("application/json; charset=utf-8");

        RequestBody body = RequestBody.create(json.toString(), JSON);
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();

        new Thread(() -> {
            try {
                Response loginResponse = client.newCall(request).execute();
                if (loginResponse.isSuccessful()){
                    runOnUiThread(()->{
                        Intent intent = new Intent(MainActivity.this, HomeActivity.class);
                        startActivity(intent);
                    });
                }
                else {
                    runOnUiThread(() -> {
                        Toast.makeText(MainActivity.this, "HELLo", Toast.LENGTH_SHORT).show();
                    });

                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }).start();


    }

    public static OkHttpClient getOkHttpClient() {
        try {
            // Create a trust manager that does not validate certificate chains
            final TrustManager[] trustAllCerts = new TrustManager[] {
                    new X509TrustManager() {
                        @Override
                        public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                        }

                        @Override
                        public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                        }

                        @Override
                        public X509Certificate[] getAcceptedIssuers() {
                            return new X509Certificate[]{};
                        }
                    }
            };

            // Install the all-trusting trust manager
            final SSLContext sslContext = SSLContext.getInstance("SSL");
            sslContext.init(null, trustAllCerts, new java.security.SecureRandom());

            // Create an ssl socket factory with our all-trusting manager
            final SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();

            OkHttpClient.Builder builder = new OkHttpClient.Builder();
            builder.sslSocketFactory(sslSocketFactory, (X509TrustManager)trustAllCerts[0]);
            builder.hostnameVerifier(new HostnameVerifier() {
                @Override
                public boolean verify(String hostname, SSLSession session) {
                    return true;
                }
            });

            return builder.build();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}