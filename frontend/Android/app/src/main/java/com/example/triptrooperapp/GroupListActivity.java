package com.example.triptrooperapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Request;
import okhttp3.Response;

public class GroupListActivity extends AppCompatActivity {

    private LinearLayout listContainer;

    /**
     * Requires groupName and groupId in intent.
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_list);

        listContainer = findViewById(R.id.list_layout_container);
        retrieveGroupList();
        Toolbar toolbar = findViewById(R.id.toolbar);
        TextView groupListHeader = findViewById(R.id.list_header);
        groupListHeader.setText("Group Lists");

        FloatingActionButton addList = findViewById(R.id.create_list);
        addList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder =
                        new AlertDialog.Builder(GroupListActivity.this);
                View dialogView =
                        LayoutInflater.from(GroupListActivity.this).
                                inflate(R.layout.create_list_dialog_view, null);
                final EditText listNameText =
                        dialogView.findViewById(R.id.list_name_textField);
                GreenButtonView createListButton =
                        dialogView.findViewById(R.id.create_list_button);
                createListButton.setButtonText("Create List");
                builder.setView(dialogView);

                final AlertDialog dialog = builder.create();
                createListButton.setButtonActionOnClick(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (listNameText.getText().toString().equals("")) {
                            Toast.makeText(GroupListActivity.this, "List Name" +
                                    " Empty", Toast.LENGTH_SHORT).show();
                        } else {
                            createGroupList(listNameText.getText().toString());
                            dialog.dismiss();
                        }
                    }
                });
                dialog.show();

            }
        });

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
    }

    private void retrieveGroupList() {
        GoogleSignInAccount account =
                GoogleSignIn.getLastSignedInAccount(GroupListActivity.this);
        Intent intent = getIntent();
        final String groupId = intent.getStringExtra("groupId");

        Request request =
                BackendServiceClass.getGroupListsGetRequest(account.getIdToken(),
                        groupId);
        new Thread(() -> {
            Response response =
                    BackendServiceClass.getResponseFromRequest(request);
            if (response.isSuccessful()) {
                try {
                    JSONObject jsonResponse =
                            new JSONObject(response.body().string());
                    JSONArray listArray = jsonResponse.getJSONArray("lists");
                    runOnUiThread(() -> {
                        for (int i = 0; i < listArray.length(); i++) {
                            try {
                                JSONObject list = listArray.getJSONObject(i);
                                final String listName = list.getString(
                                        "listName");
                                final String listId = list.getString("_id");
                                ListBoxComponentView listBox =
                                        new ListBoxComponentView(GroupListActivity.this);
                                listBox.setMainTitleText(listName);
                                listBox.setVisibilityOfTextViews(View.VISIBLE
                                        , View.INVISIBLE, View.INVISIBLE);
                                listBox.setActionOnCardClick(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        Intent intent =
                                                new Intent(GroupListActivity.this,
                                                        ListDetailsActivity.class);
                                        intent.putExtra("listName", listName);
                                        intent.putExtra("id", listId);
                                        intent.putExtra("context", "groupList");
                                        intent.putExtra("groupId", groupId);
                                        startActivity(intent);
                                    }
                                });
                                listContainer.addView(listBox);
                            } catch (JSONException e) {
                                throw new CustomException("error", e);
                            }
                        }
                    });

                } catch (JSONException e) {
                    throw new CustomException("error", e);
                } catch (IOException e) {
                    throw new CustomException("error", e);
                }
            } else {
                try {
                    Log.d("TAG", response.body().string());
                } catch (IOException e) {
                    throw new CustomException("error", e);
                }
            }
        }).start();
    }

    private void createGroupList(String listName) {
        GoogleSignInAccount account =
                GoogleSignIn.getLastSignedInAccount(GroupListActivity.this);
        Intent intent = getIntent();
        final String groupId = intent.getStringExtra("groupId");

        JSONObject json = new JSONObject();
        try {
            json.put("listName", listName);
        } catch (JSONException e) {
            throw new CustomException("error", e);
        }
        Request request = BackendServiceClass.createGroupListPutRequest(json,
                account.getIdToken(), groupId);
        new Thread(() -> {
            Response response =
                    BackendServiceClass.getResponseFromRequest(request);
            if (response.isSuccessful()) {
                runOnUiThread(() -> {
                    Toast.makeText(GroupListActivity.this,
                            "List " + listName + " Created.",
                            Toast.LENGTH_SHORT).show();
                    recreate();
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            Intent intentGet = getIntent();
            String groupId = intentGet.getStringExtra("groupId");
            Intent intent = new Intent(this, GroupDetailsActivity.class);
            intent.putExtra("id", groupId);
            startActivity(intent);
            overridePendingTransition(0, 0);
            return true;
        } else if (item.getItemId() == R.id.action_delete) {
            // TODO: pass the id to backend to delete the list.
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //getMenuInflater().inflate(R.menu.header_bar_menu_delete, menu);
        return true;
    }
}