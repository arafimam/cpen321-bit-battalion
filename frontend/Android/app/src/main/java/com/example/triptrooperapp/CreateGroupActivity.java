package com.example.triptrooperapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Request;
import okhttp3.Response;

public class CreateGroupActivity extends AppCompatActivity {

    private EditText groupNameField;
    private EditText groupCodeField;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_group);
        initializeButtonTextFieldAndToolbar();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            Intent intent = new Intent(this, GroupsActivity.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Initializes screen buttons, toolbar and text fields.
     */
    private void initializeButtonTextFieldAndToolbar() {
        GreenButtonView createGroupButton = findViewById(R.id.create_group);
        createGroupButton.setButtonText("Create Group");
        GreenButtonView joinGroupButton = findViewById(R.id.join_group_btn);
        joinGroupButton.setButtonText("Join Group");
        groupNameField = findViewById(R.id.group_name_text_field);
        groupCodeField = findViewById(R.id.join_group_text_field);
        Toolbar toolbar = findViewById(R.id.toolbar);

        createGroupButton.setButtonActionOnClick(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setButtonClickValidation(groupNameField.getText().toString(),
                        groupNameField.getText().toString(), true);
            }
        });

        joinGroupButton.setButtonActionOnClick(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setButtonClickValidation(groupCodeField.getText().toString(),
                        groupCodeField.getText().toString(), false);
            }
        });

        /*Back arrow button and disable title in header bar*/
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
    }

    /**
     * Set Button Click validation. Upon successful validation do backend call.
     * if text to validate is empty shows a toast message with failureMessage
     *
     * @param textToValidate
     * @param failureMessage
     */
    private void setButtonClickValidation(String textToValidate,
                                          String failureMessage,
                                          boolean isCreatingNewGroup) {
        if (textToValidate.equals("")) {
            Toast.makeText(CreateGroupActivity.this, failureMessage,
                    Toast.LENGTH_SHORT).show();
        } else {
            if (isCreatingNewGroup) {
                JSONObject json = new JSONObject();
                try {
                    json.put("groupName", textToValidate);
                } catch (JSONException e) {
                    throw new CustomException("error", e);
                }
                GoogleSignInAccount account =
                        GoogleSignIn.getLastSignedInAccount(this);
                BackendServiceClass backendServiceClass =
                        new BackendServiceClass("groups/create", json,
                                "authorization", account.getIdToken());
                Request request =
                        backendServiceClass.getPostRequestWithHeaderAndJsonParameter();

                new Thread(() -> {
                    Response response =
                            backendServiceClass.getResponseFromRequest(request);
                    if (response.isSuccessful()) {
                        String responseBody =
                                backendServiceClass.getResponseBody(response);
                        try {
                            JSONObject jsonResponse =
                                    new JSONObject(responseBody);
                            String groupCode = jsonResponse.getString(
                                    "groupCode");
                            runOnUiThread(() -> {
                                Toast.makeText(CreateGroupActivity.this,
                                        "Created group with code: " + groupCode,
                                        Toast.LENGTH_SHORT).show();
                            });

                        } catch (JSONException e) {
                            throw new CustomException("error", e);
                        }
                    } else {
                        runOnUiThread(() -> {
                            try {
                                Log.d("TAG", response.body().string());
                            } catch (IOException e) {
                                throw new CustomException("error", e);
                            }
                            Toast.makeText(CreateGroupActivity.this, "Unable " +
                                    "to create group.", Toast.LENGTH_SHORT).show();
                        });
                    }
                }).start();
            }

            // join group
            else {
                JSONObject json = new JSONObject();
                try {
                    json.put("groupCode", textToValidate);
                    GoogleSignInAccount account =
                            GoogleSignIn.getLastSignedInAccount(this);
                    BackendServiceClass backendService =
                            new BackendServiceClass("groups/join", json,
                                    "authorization", account.getIdToken());
                    Request request =
                            backendService.doPutRequestWithJsonAndHeader();

                    new Thread(() -> {
                        Response response =
                                backendService.getResponseFromRequest(request);
                        if (response.isSuccessful()) {
                            runOnUiThread(() -> {
                                Toast.makeText(CreateGroupActivity.this,
                                        "Join group successful",
                                        Toast.LENGTH_SHORT).show();
                            });
                        } else {
                            try {
                                Log.d("TAG", response.body().string());
                            } catch (IOException e) {
                                throw new CustomException("error", e);
                            }
                        }
                    }).start();
                } catch (JSONException e) {
                    throw new CustomException("error", e);
                }

            }
        }

    }
}