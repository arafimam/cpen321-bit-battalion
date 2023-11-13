package com.example.triptrooperapp;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Request;
import okhttp3.Response;


public class GroupDetailsActivity extends AppCompatActivity {

    private DefaultCardButtonView memberBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_details);
        Intent intent = getIntent();
        String groupId = intent.getStringExtra("id");


        memberBtn = findViewById(R.id.member_btn);
        //memberBtn.setMainTitleText("Members (10)");

        GreenButtonView leaveGroupBtn = findViewById(R.id.leave_group);
        leaveGroupBtn.setButtonText("Leave Group");
        leaveGroupBtn.setButtonActionOnClick(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder =
                        new AlertDialog.Builder(GroupDetailsActivity.this);
                builder.setMessage(
                                "Are you sure you want to leave the group? " +
                                        "You can join again using the group " +
                                        "code.")
                        .setTitle(
                                "Leave group");
                builder.setPositiveButton("Leave",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                leaveGroupViaBackend();
                            }
                        });

                builder.setNegativeButton("Close",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface,
                                                int i) {
                                builder.create().dismiss();
                            }
                        });
                builder.create().show();
            }
        });

        DefaultCardButtonView listBtn = findViewById(R.id.list_btn);
        listBtn.setMainTitleText("View Lists");
        listBtn.setActionForOnClick(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intentTo = new Intent(GroupDetailsActivity.this,
                        GroupListActivity.class);
                intentTo.putExtra("groupId", groupId);
                startActivity(intentTo);
            }
        });

        setScreenContentByBackend();

        Toolbar toolbar = findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
    }

    private void leaveGroupViaBackend() {
        Intent intent = getIntent();
        String groupId = intent.getStringExtra("id");
        GoogleSignInAccount account =
                GoogleSignIn.getLastSignedInAccount(GroupDetailsActivity.this);

        Request request =
                BackendServiceClass.leaveGroupPutRequest(account.getIdToken(),
                        groupId);
        new Thread(() -> {
            Response response =
                    BackendServiceClass.getResponseFromRequest(request);
            if (response.isSuccessful()) {
                runOnUiThread(() -> {
                    Toast.makeText(GroupDetailsActivity.this, "Left group",
                            Toast.LENGTH_SHORT).show();
                    Intent intentTo = new Intent(GroupDetailsActivity.this,
                            GroupsActivity.class);
                    startActivity(intentTo);
                });
            } else {
                try {
                    Log.d("TAG", response.body().string());
                } catch (IOException e) {
                    throw new CustomException("error", e);
                }
            }
        }).start();

    }


    private void setScreenContentByBackend() {
        Intent intent = getIntent();
        String groupId = intent.getStringExtra("id");
        GoogleSignInAccount account =
                GoogleSignIn.getLastSignedInAccount(GroupDetailsActivity.this);

        Request request =
                BackendServiceClass.getGroupsForUserGetRequest(account.getIdToken(),
                        groupId);
        new Thread(() -> {
            Response response =
                    BackendServiceClass.getResponseFromRequest(request);
            if (response.isSuccessful()) {
                try {
                    String responseBody = response.body().string();
                    JSONObject jsonResponse = new JSONObject(responseBody);
                    JSONObject groupObject = jsonResponse.getJSONObject(
                            "group");
                    JSONArray members = groupObject.getJSONArray("members");

                    //JSONArray memberArray = jsonResponse.getJSONArray
                    // ("members");

                    runOnUiThread(() -> {
                        setMemberInformation(members);
                        // TODO: add a function for list and view expenses.
                    });
                } catch (IOException e) {
                    throw new CustomException("error", e);
                } catch (JSONException e) {
                    throw new CustomException("error", e);
                }
            } else {
                Log.d("TAG", "Sdsda");
            }
        }).start();
    }

    private void setMemberInformation(JSONArray memberArray) {
        memberBtn.setMainTitleText(String.format("Members (%d)",
                memberArray.length()));
        memberBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder =
                        new AlertDialog.Builder(GroupDetailsActivity.this);
                View dialogView =
                        LayoutInflater.from(GroupDetailsActivity.this).inflate(R.layout.member_list_view, null);
                final LinearLayout memberList =
                        dialogView.findViewById(R.id.member_list_container);


                for (int i = 0; i < memberArray.length(); i++) {
                    DefaultCardButtonView cardButtonView =
                            new DefaultCardButtonView(GroupDetailsActivity.this);
                    cardButtonView.setTag("member" + i);
                    try {
                        JSONObject member = memberArray.getJSONObject(i);
                        String username = member.getString("username");
                        cardButtonView.setMainTitleText(username);
                    } catch (JSONException e) {
                        throw new CustomException("error", e);
                    }
                    memberList.addView(cardButtonView);
                }

                builder.setView(dialogView);
                builder.setNegativeButton("Close",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface,
                                                int i) {
                                dialogInterface.dismiss();
                            }
                        });
                final AlertDialog dialog = builder.create();

                dialog.show();

            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            Intent intent = new Intent(this, GroupsActivity.class);
            startActivity(intent);
            overridePendingTransition(0, 0);
            return true;
        } else if (item.getItemId() == R.id.action_delete) {
            AlertDialog.Builder builder =
                    new AlertDialog.Builder(GroupDetailsActivity.this);
            builder.setMessage(
                            "Are you sure you want to delete thr group? " +
                                    "Deleting the group will remove this " +
                                    "group for all users and there is no " +
                                    "turning back.")
                    .setTitle(
                            "Confirm Delete");
            builder.setPositiveButton("Delete",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface,
                                            int i) {
                            deleteGroupViaBackend();
                        }
                    });

            builder.setNegativeButton("Close",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface,
                                            int i) {
                            builder.create().dismiss();
                        }
                    });
            builder.create().show();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.header_bar_menu_delete, menu);
        return true;
    }

    private void deleteGroupViaBackend() {
        Intent intent = getIntent();
        String groupId = intent.getStringExtra("id");
        GoogleSignInAccount account =
                GoogleSignIn.getLastSignedInAccount(GroupDetailsActivity.this);

        Request request =
                BackendServiceClass.deleteGroupDeleteRequest(account.getIdToken(),
                        groupId);

        new Thread(() -> {
            Response response =
                    BackendServiceClass.getResponseFromRequest(request);
            if (response.isSuccessful()) {
                runOnUiThread(() -> {
                    Toast.makeText(GroupDetailsActivity.this, "Deleted group"
                            , Toast.LENGTH_SHORT).show();
                    Intent intentToShift =
                            new Intent(GroupDetailsActivity.this,
                                    GroupsActivity.class);
                    startActivity(intentToShift);
                });
            } else {
                runOnUiThread(() -> {
                    try {
                        Log.d("TAG", response.body().string());
                    } catch (IOException e) {
                        throw new CustomException("error", e);
                    }
                    Toast.makeText(GroupDetailsActivity.this, "Unable to " +
                            "delete group", Toast.LENGTH_SHORT).show();
                });
            }
        }).start();


    }
}