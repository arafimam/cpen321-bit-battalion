package com.example.triptrooperapp;

import android.annotation.SuppressLint;
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
import java.util.ArrayList;
import java.util.List;

import okhttp3.Request;
import okhttp3.Response;

public class ListDetailsActivity extends AppCompatActivity {

    private LinearLayout activityLayout;
    private List<String> placesIds;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_details);

        Toolbar toolbar = findViewById(R.id.toolbar);
        placesIds = new ArrayList<>();
        FloatingActionButton addActivity = findViewById(R.id.create_list);
        FloatingActionButton optimizeButton =
                findViewById(R.id.optimize_button);

        Intent intent = getIntent();
        String listNamePassed = intent.getStringExtra("listName");
        String listId = intent.getStringExtra("id");

        TextView listName = findViewById(R.id.list_name_topic);
        listName.setText(listNamePassed);

        activityLayout = findViewById(R.id.list_activity_container);

        retrievePlacesForList();

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        addActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder =
                        new AlertDialog.Builder(ListDetailsActivity.this);
                View dialogView =
                        LayoutInflater.from(ListDetailsActivity.this).
                                inflate(R.layout.member_list_view, null);
                final LinearLayout activityOptions =
                        dialogView.findViewById(R.id.member_list_container);

                DefaultCardButtonView nearby =
                        new DefaultCardButtonView(ListDetailsActivity.this);
                nearby.setMainTitleText("Add Activity by nearby location");

                nearby.setActionForOnClick(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(ListDetailsActivity.this,
                                PlacesActivity.class);
                        intent.putExtra("context", "nearby");
                        intent.putExtra("list", "list");
                        Intent intentFrom = getIntent();
                        String listId = intentFrom.getStringExtra("id");
                        intent.putExtra("listId", listId);
                        startActivity(intent);
                    }
                });

                DefaultCardButtonView destination =
                        new DefaultCardButtonView(ListDetailsActivity.this);
                destination.setMainTitleText("Add Activity by destination");


                /**
                 * Destination button
                 */
                destination.setActionForOnClick(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        AlertDialog.Builder builder =
                                new AlertDialog.Builder(ListDetailsActivity.this);
                        View dialogView =
                                LayoutInflater.from(ListDetailsActivity.this).
                                        inflate(R.layout.create_list_dialog_view, null);
                        final EditText destinationText =
                                dialogView.findViewById(R.id.list_name_textField);
                        destinationText.setHint("Destination Name");
                        GreenButtonView createListButton =
                                dialogView.findViewById(R.id.create_list_button);
                        createListButton.setButtonText("Enter destination");
                        builder.setView(dialogView);

                        final AlertDialog dialog = builder.create();
                        createListButton.setButtonActionOnClick(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                if (destinationText.getText().toString().equals("")) {
                                    Toast.makeText(ListDetailsActivity.this,
                                            "No destination entered.",
                                            Toast.LENGTH_SHORT).show();
                                    dialog.dismiss();
                                } else {
                                    Intent intentTo =
                                            new Intent(ListDetailsActivity.this, PlacesActivity.class);
                                    intentTo.putExtra("destination",
                                            destinationText.getText().toString());
                                    intentTo.putExtra("context",
                                            "byDestination");
                                    intentTo.putExtra("list", "list");
                                    intentTo.putExtra("listId", listId);
                                    startActivity(intentTo);
                                    dialog.dismiss();
                                }
                            }
                        });
                        dialog.show();
                    }
                });

                activityOptions.addView(nearby);
                activityOptions.addView(destination);
                builder.setView(dialogView);

                final AlertDialog dialog = builder.create();
                dialog.show();

            }
        });

        optimizeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doComplexAlgorithm();
            }
        });

    }

    private void doComplexAlgorithm() {
        for (int i = 0; i < placesIds.size(); i++) {
            Log.d("TAG", placesIds.get(i));
        }
        GoogleSignInAccount account =
                GoogleSignIn.getLastSignedInAccount(ListDetailsActivity.this);
        Log.d("TAG", account.getIdToken());
        JSONArray placeArray = new JSONArray(placesIds);
        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("placeIds", placeArray);

        } catch (JSONException e) {
            throw new CustomException("error", e);
        }

        Intent intent = getIntent();
        String listId = intent.getStringExtra("id");
        String url = "lists/" + listId + "/add/schedule";
        BackendServiceClass backendServiceClass = new BackendServiceClass(url
                , jsonBody, "authorization", account.getIdToken());
        Request request = backendServiceClass.doPutRequestWithJsonAndHeader();

        new Thread(() -> {
            Response response =
                    backendServiceClass.getResponseFromRequest(request);
            if (response.isSuccessful()) {
                try {
                    String responseBody = response.body().string();
                    JSONObject jsonResponse = new JSONObject(responseBody);
                    JSONArray schedules = jsonResponse.getJSONArray("schedule");

                    runOnUiThread(() -> {
                        AlertDialog.Builder builder =
                                new AlertDialog.Builder(ListDetailsActivity.this);
                        View dialogView =
                                LayoutInflater.from(ListDetailsActivity.this).inflate(R.layout.member_list_view, null);
                        final LinearLayout scheduleContainer =
                                dialogView.findViewById(R.id.member_list_container);

                        for (int i = 0; i < schedules.length(); i++) {
                            try {
                                JSONObject schedule =
                                        schedules.getJSONObject(i);
                                ListBoxComponentView listBox =
                                        new ListBoxComponentView(ListDetailsActivity.this);
                                String placeName = schedule.getString(
                                        "displayName");
                                String address = schedule.getString(
                                        "shortFormattedAddress");
                                String rating = schedule.optString("rating",
                                        "--");
                                listBox.setMainTitleText(placeName);
                                listBox.setSubTitleText(address);
                                listBox.setSideTitleText("     Rating: " + rating + "/5");

                                scheduleContainer.addView(listBox);
                            } catch (JSONException e) {
                                throw new CustomException("error", e);
                            }


                        }
                        builder.setView(dialogView);
                        builder.setNegativeButton("Close", null);
                        builder.setTitle("Optimized Schedule");
                        final AlertDialog dialog = builder.create();

                        dialog.show();
                    });


                } catch (IOException e) {
                    throw new CustomException("error", e);
                } catch (JSONException e) {
                    throw new CustomException("error", e);
                }
            } else {
                try {
                    Log.d("TAG", "Failured");
                    Log.d("TAG", response.body().string());
                } catch (IOException e) {
                    throw new CustomException("error", e);
                }
            }
        }).start();
    }

    private void retrievePlacesForList() {
        Intent intent = getIntent();
        String listId = intent.getStringExtra("id");
        String url = "lists/" + listId + "/places";

        GoogleSignInAccount account =
                GoogleSignIn.getLastSignedInAccount(ListDetailsActivity.this);
        BackendServiceClass backendService = new BackendServiceClass(url,
                "authorization", account.getIdToken());
        Request request = backendService.getGetRequestWithHeaderOnly();
        new Thread(() -> {
            Response response = backendService.getResponseFromRequest(request);

            if (response.isSuccessful()) {
                try {
                    String responseBody = response.body().string();
                    JSONObject jsonResponse = new JSONObject(responseBody);
                    JSONArray places = jsonResponse.getJSONArray("places");
                    Log.d("TAG", places.toString());
                    for (int i = 0; i < places.length(); i++) {
                        JSONObject place = places.getJSONObject(i);
                        runOnUiThread(() -> {
                            ListBoxComponentView listBox =
                                    new ListBoxComponentView(ListDetailsActivity.this);

                            try {
                                String placeName = place.getString(
                                        "displayName");
                                String address = place.getString(
                                        "shortFormattedAddress");
                                String rating = place.optString("rating", "--");
                                listBox.setMainTitleText(placeName);
                                listBox.setSubTitleText(address);
                                listBox.setSideTitleText("     Rating: " + rating + "/5");
                                String placeId = place.getString("placeId");
                                placesIds.add(placeId);
                                activityLayout.addView(listBox);
                            } catch (JSONException e) {
                                throw new CustomException("error", e);
                            }
                        });
                    }


                } catch (IOException e) {
                    throw new CustomException("error", e);
                } catch (JSONException e) {
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

    private void deleteUserList() {
        Intent intent = getIntent();
        String listId = intent.getStringExtra("id");
        GoogleSignInAccount account =
                GoogleSignIn.getLastSignedInAccount(ListDetailsActivity.this);
        BackendServiceClass backendService =
                new BackendServiceClass("users/" + listId + "/remove/list",
                        "authorization", account.getIdToken());
        Request request = backendService.doPutRequestWithHeaderOnly();
        new Thread(() -> {
            Response response = backendService.getResponseFromRequest(request);
            if (response.isSuccessful()) {
                runOnUiThread(() -> {
                    Toast.makeText(ListDetailsActivity.this, "List Deleted",
                            Toast.LENGTH_SHORT).show();
                    Intent intentTo = new Intent(ListDetailsActivity.this,
                            ListActivity.class);
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

    private void deleteGroupList() {
        Intent intent = getIntent();
        String listId = intent.getStringExtra("id");
        String groupId = intent.getStringExtra("groupId");
        GoogleSignInAccount account =
                GoogleSignIn.getLastSignedInAccount(ListDetailsActivity.this);
        JSONObject json = new JSONObject();
        try {
            json.put("listId", listId);
        } catch (JSONException e) {
            throw new CustomException("error", e);
        }

        BackendServiceClass backendService = new BackendServiceClass("groups" +
                "/" + groupId + "/remove/list", json, "authorization",
                account.getIdToken());
        Request request = backendService.doPutRequestWithJsonAndHeader();
        new Thread(() -> {
            Response response = backendService.getResponseFromRequest(request);
            if (response.isSuccessful()) {
                runOnUiThread(() -> {
                    Toast.makeText(ListDetailsActivity.this, "List Deleted",
                            Toast.LENGTH_SHORT).show();
                    Intent intentTo = new Intent(ListDetailsActivity.this,
                            GroupListActivity.class);
                    intentTo.putExtra("groupId", groupId);
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            Intent intentFrom = getIntent();
            String context = intentFrom.getStringExtra("context");
            if (context.equals("userList")) {
                Intent intent = new Intent(this, ListActivity.class);
                startActivity(intent);
                overridePendingTransition(0, 0);
            } else {
                Intent intent = new Intent(this, GroupListActivity.class);
                intent.putExtra("groupId", intentFrom.getStringExtra("groupId"
                ));
                startActivity(intent);
                overridePendingTransition(0, 0);
            }

            return true;
        } else if (item.getItemId() == R.id.action_delete) {
            // TODO: pass the id to backend to delete the list.
            Intent intentFrom = getIntent();
            String context = intentFrom.getStringExtra("context");
            if (context.equals("userList")) {
                deleteUserList();
            } else {
                deleteGroupList();
            }

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
}