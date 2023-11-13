package com.example.triptrooperapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
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
    private NetworkChecker networkChecker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_groups);

        networkChecker = new NetworkChecker(GroupsActivity.this);
        listBoxContainer = findViewById(R.id.list_layout_container);
        initializeCreateGroupButton();
        if (!networkChecker.haveNetworkConnection()) {
            handleNoConnection("Unable to retrieve your groups now.");
            checkEmptyGroupsView();
        } else {
            loader = findViewById(R.id.loader);
            loader.setVisibility(View.INVISIBLE);
            retrieveUserGroups();
        }
    }

    /**
     * Initializes the create group button.
     */
    private void initializeCreateGroupButton() {
        FloatingActionButton createGroupButton =
                findViewById(R.id.create_group);
        createGroupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!networkChecker.haveNetworkConnection()) {
                    handleNoConnection("Cannot create group now." +
                            "Try again later.");
                    return;
                }
                Intent intent = new Intent(GroupsActivity.this,
                        CreateGroupActivity.class);
                startActivity(intent);
            }
        });
    }

    /**
     * Handle no connection.
     */
    private void handleNoConnection(String message) {
        AlertDialog.Builder builder =
                new AlertDialog.Builder(GroupsActivity.this);
        builder.setMessage(
                        message)
                .setTitle(
                        "No internet.");
        builder.create().show();
    }

    /**
     * Retrieves user group.
     */
    private void retrieveUserGroups() {
        loader.setVisibility(View.VISIBLE);
        GoogleSignInAccount account =
                GoogleSignIn.getLastSignedInAccount(GroupsActivity.this);
        Request request =
                BackendServiceClass.getGroupsOfUserGetRequest(account.getIdToken());

        new Thread(() -> {
            Response response =
                    BackendServiceClass.getResponseFromRequest(request);
            if (response.isSuccessful()) {
                runOnUiThread(() -> {
                    try {
                        String responseBody = response.body().string();
                        Log.d("TAG", responseBody);
                        JSONObject jsonObject = new JSONObject(responseBody);
                        JSONArray groupsArray = jsonObject.getJSONArray(
                                "groups");

                        // creating the groups view.
                        for (int i = 0; i < groupsArray.length(); i++) {
                            ListBoxComponentView listBoxComponentView =
                                    new ListBoxComponentView(GroupsActivity.this);
                            JSONObject groupInfo = groupsArray.getJSONObject(i);
                            listBoxComponentView.setMainTitleText(
                                    groupInfo.getString("groupName"));
                            listBoxComponentView.setSideTitleText(
                                    "Group code: " + groupInfo.getString(
                                            "groupCode"));
                            listBoxComponentView.setVisibilityOfTextViews(
                                    View.VISIBLE, View.VISIBLE, View.INVISIBLE);
                            listBoxContainer.addView(listBoxComponentView);
                            listBoxComponentView.sideTitle.setTag("sideTitle" + i);

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
                        checkEmptyGroupsView();

                    } catch (JSONException | IOException e) {
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
                    handleErrorWithRetrievingGroups();
                    checkEmptyGroupsView();
                });
            }
        }).start();
    }

    /**
     * Handle error with retrieving groups.
     */
    private void handleErrorWithRetrievingGroups() {
        runOnUiThread(() -> {
            AlertDialog.Builder builder =
                    new AlertDialog.Builder(GroupsActivity.this);
            builder.setMessage(
                            "Unable to retrieve your Groups. Try again later.")
                    .setTitle(
                            "Something went wrong.");
            builder.create().show();
        });
    }

    /**
     * Checks for empty groups view.
     */
    private void checkEmptyGroupsView() {
        TextView noGroup = findViewById(R.id.textView_noGroup);
        if (listBoxContainer.getChildCount() == 0) {
            noGroup.setVisibility(View.VISIBLE);
        } else {
            noGroup.setVisibility(View.INVISIBLE);
        }
    }
}