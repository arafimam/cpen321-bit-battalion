package com.example.triptrooperapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Request;
import okhttp3.Response;

/**
 * Groups screen.
 */
public class GroupsActivity extends AppCompatActivity {

    private LinearLayout listBoxContainer;
    private ProgressBar loader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_groups);

        listBoxContainer = findViewById(R.id.list_layout_container);
        loader = findViewById(R.id.loader);
        loader.setVisibility(View.INVISIBLE);

        retrieveUserGroups();


        FloatingActionButton createGroupButton =
                findViewById(R.id.create_group);
        createGroupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(GroupsActivity.this,
                        CreateGroupActivity.class);
                startActivity(intent);
            }
        });
    }

    private void retrieveUserGroups() {
        loader.setVisibility(View.VISIBLE);
        GoogleSignInAccount account =
                GoogleSignIn.getLastSignedInAccount(GroupsActivity.this);
        BackendServiceClass backendService = new BackendServiceClass("groups" +
                "/all", "authorization", account.getIdToken());
        Request request = backendService.getGetRequestWithHeaderOnly();

        new Thread(() -> {
            Response response = backendService.getResponseFromRequest(request);
            if (response.isSuccessful()) {
                runOnUiThread(() -> {

                    try {
                        String responseBody = response.body().string();
                        JSONObject jsonObject = new JSONObject(responseBody);
                        JSONArray groupsArray = jsonObject.getJSONArray(
                                "groups");

                        for (int i = 0; i < groupsArray.length(); i++) {
                            ListBoxComponentView listBoxComponentView =
                                    new ListBoxComponentView(GroupsActivity.this);
                            JSONObject groupInfo = groupsArray.getJSONObject(i);
                            listBoxComponentView.setMainTitleText(
                                    groupInfo.getString("groupName"));
                            listBoxComponentView.setSideTitleText(
                                    groupInfo.getString("groupCode"));
                            listBoxComponentView.setVisibilityOfTextViews(
                                    View.VISIBLE, View.VISIBLE, View.INVISIBLE);
                            listBoxContainer.addView(listBoxComponentView);

                            final String groupId = groupInfo.getString("_id");
                            listBoxComponentView.setActionOnCardClick(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    Intent intent =
                                            new Intent(GroupsActivity.this,
                                                    GroupDetailsActivity.class);
                                    intent.putExtra("id", groupId);
                                    startActivity(intent);
                                }
                            });
                        }

                    } catch (JSONException e) {
                        throw new CustomException("error", e);
                    } catch (IOException e) {
                        throw new CustomException("error", e);
                    }

                    loader.setVisibility(View.INVISIBLE);
                });
            } else {
                runOnUiThread(() -> {
                    try {
                        Log.d("TAG", response.body().string());
                    } catch (IOException e) {
                        throw new CustomException("error", e);
                    }
                    loader.setVisibility(View.INVISIBLE);
                    Toast.makeText(GroupsActivity.this, "Something wrong",
                            Toast.LENGTH_SHORT).show();
                });
            }
        }).start();

    }
}