package com.example.triptrooperapp;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
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
    private FloatingActionButton addActivity;
    private FloatingActionButton optimizeButton;
    private NetworkChecker networkChecker;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_details);

        networkChecker = new NetworkChecker(ListDetailsActivity.this);

        Toolbar toolbar = findViewById(R.id.toolbar);
        placesIds = new ArrayList<>();
        addActivity = findViewById(R.id.create_list);
        optimizeButton = findViewById(R.id.optimize_button);

        Intent intent = getIntent();
        String listNamePassed = intent.getStringExtra("listName");

        TextView listName = findViewById(R.id.list_name_topic);
        listName.setText(listNamePassed);

        activityLayout = findViewById(R.id.list_activity_container);
        setAddPlacesButton();
        setOptimizeButton();
        retrievePlacesForList();

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
    }

    private void handleNoConnection(String message) {
        AlertDialog.Builder builder =
                new AlertDialog.Builder(ListDetailsActivity.this);
        builder.setMessage(
                        message)
                .setTitle(
                        "No internet.");
        builder.create().show();
    }

    /**
     * checks for no places in list.
     */
    private void checkForEmptyPlacesInList() {
        TextView noPlace = findViewById(R.id.textView_noPlace);
        if (activityLayout.getChildCount() == 0) {
            noPlace.setVisibility(View.VISIBLE);
        } else {
            noPlace.setVisibility(View.GONE);
        }
    }

    /**
     * Sets everything needed for the add places button.
     */
    private void setAddPlacesButton() {
        addActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!networkChecker.haveNetworkConnection()) {
                    handleNoConnection("Cannot add places now. Try again " +
                            "later");
                    return;
                }
                AlertDialog.Builder builder =
                        new AlertDialog.Builder(ListDetailsActivity.this);
                View dialogView =
                        LayoutInflater.from(ListDetailsActivity.this).
                                inflate(R.layout.member_list_view, null);
                final LinearLayout activityOptions =
                        dialogView.findViewById(R.id.member_list_container);

                DefaultCardButtonView nearby = setNearbyPlacesButton();
                DefaultCardButtonView destination =
                        setDestinationPlacesButton();

                activityOptions.addView(nearby);
                activityOptions.addView(destination);
                builder.setView(dialogView);

                final AlertDialog dialog = builder.create();
                dialog.show();
            }
        });
    }

    /**
     * Sets action for nearby places.
     * Only valid when dialog for add place opened.
     */
    private DefaultCardButtonView setNearbyPlacesButton() {
        DefaultCardButtonView nearby =
                new DefaultCardButtonView(ListDetailsActivity.this);
        nearby.setMainTitleText("Add Local gems near you");

        nearby.setActionForOnClick(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // need to check for location first.
                checkLocationPermissionAndNavigate();
            }
        });
        return nearby;
    }

    private void checkLocationPermissionAndNavigate() {
        if (LocationService.areLocationPermissionsAlreadyGranted(this)) {
            Intent intent = new Intent(ListDetailsActivity.this,
                    PlacesActivity.class);
            intent.putExtra("context", "nearby");
            intent.putExtra("list", "list");
            Intent intentFrom = getIntent();
            String listId = intentFrom.getStringExtra("id");
            intent.putExtra("listId", listId);
            if (intentFrom.getStringExtra("groupId") != null) {
                intent.putExtra("group", "group");
            }
            startActivity(intent);
            return;
        } else {
            if (LocationService.areLocationPermissionsPreviouslyDenied(this)) {
                final Activity currentActivity = this;
                new AlertDialog.Builder(this)
                        .setTitle("Location Permission")
                        .setMessage("We need location permission for viewing " +
                                "activities.")
                        .setNegativeButton("Cancel",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(
                                            DialogInterface dialogInterface,
                                            int i
                                    ) {
                                        Toast.makeText(ListDetailsActivity.this,
                                                        "Permission for " +
                                                                "location " +
                                                                "denied",
                                                        Toast.LENGTH_LONG)
                                                .show();
                                        dialogInterface.dismiss();
                                    }
                                })

                        .setPositiveButton("Confirm",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface,
                                                        int i) {
                                        LocationService.requestLocationPermission(currentActivity);
                                    }
                                }).show();
            } else {
                LocationService.requestLocationPermission(this);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions,
                                           int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions,
                grantResults);

        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] ==
                    PackageManager.PERMISSION_GRANTED) {

                Intent intent = new Intent(ListDetailsActivity.this,
                        PlacesActivity.class);
                intent.putExtra("context", "nearby");
                intent.putExtra("list", "list");
                Intent intentFrom = getIntent();
                String listId = intentFrom.getStringExtra("id");
                intent.putExtra("listId", listId);
                startActivity(intent);
            } else {
                Toast.makeText(this, "Location permission is required!",
                        Toast.LENGTH_LONG).show();
            }
        }
    }

    /**
     * Sets action for destination button.
     * Only valid when add places dialog opened.
     */
    private DefaultCardButtonView setDestinationPlacesButton() {
        Intent intent = getIntent();
        String listId = intent.getStringExtra("id");
        DefaultCardButtonView destination =
                new DefaultCardButtonView(ListDetailsActivity.this);
        destination.setMainTitleText("Explore places by destination");

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
                GreenButtonView destNameBtn =
                        dialogView.findViewById(R.id.create_list_button);
                destNameBtn.setButtonText("Enter destination");
                builder.setView(dialogView);

                final AlertDialog dialog = builder.create();
                destNameBtn.setButtonActionOnClick(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (destinationText.getText().toString().equals("")) {
                            Toast.makeText(ListDetailsActivity.this,
                                    "No destination entered.",
                                    Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                        } else {
                            Intent intentTo =
                                    new Intent(ListDetailsActivity.this,
                                            PlacesActivity.class);
                            intentTo.putExtra("destination",
                                    destinationText.getText().toString());
                            intentTo.putExtra("context",
                                    "byDestination");
                            intentTo.putExtra("list", "list");
                            intentTo.putExtra("listId", listId);
                            Intent intentFrom = getIntent();
                            if (intentFrom.getStringExtra("groupId") != null) {
                                intentTo.putExtra("group", "group");
                            }
                            startActivity(intentTo);
                            dialog.dismiss();
                        }
                    }
                });
                dialog.show();
            }
        });

        return destination;
    }

    /**
     * Sets everything of the optimize button.
     */
    private void setOptimizeButton() {
        optimizeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (activityLayout.getChildCount() <= 1) {
                    AlertDialog.Builder builder =
                            new AlertDialog.Builder(ListDetailsActivity.this);
                    builder.setMessage(
                                    "Your list is empty, and without any " +
                                            "added places, " +
                                            "we're unable to generate an " +
                                            "optimized schedule for you. " +
                                            "Begin your journey by adding " +
                                            "some destinations to visit!")
                            .setTitle(
                                    "Schedule Creation Unavailable");
                    builder.setNegativeButton("Close",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface,
                                                    int i) {
                                    builder.create().dismiss();
                                }
                            });
                    builder.create().show();
                    return;
                }
                AlertDialog.Builder builder =
                        new AlertDialog.Builder(ListDetailsActivity.this);
                builder.setMessage(
                                "Our app has crafted a personalized schedule " +
                                        "just for you. " +
                                        "With our optimized routing, you'll " +
                                        "save time and cover less distance " +
                                        "while visiting all " +
                                        "your chosen spots. View it on google" +
                                        " maps ")
                        .setTitle(
                                "Optimized schedule ready");
                builder.setNegativeButton("Close",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface,
                                                int i) {
                                builder.create().dismiss();
                            }
                        });
                builder.setPositiveButton("View in Maps",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface,
                                                int i) {
                                doComplexAlgorithm();
                            }
                        });
                builder.create().show();

            }
        });
    }

    /**
     * Creates the optimized schedule.
     */
    private void doComplexAlgorithm() {
        if (!networkChecker.haveNetworkConnection()) {
            handleNoConnection("Cannot optimize your schedule. Try again " +
                    "later");
            return;
        }
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
        Request request =
                BackendServiceClass.getOptimizedSchedulePutRequest(jsonBody,
                        account.getIdToken(), listId);

        ArrayList<String> placeNames = new ArrayList<>();
        ArrayList<String> longitudes = new ArrayList<>();
        ArrayList<String> latitude = new ArrayList<>();
        ArrayList<String> addresses = new ArrayList<>();
        ArrayList<String> ratings = new ArrayList<>();

        new Thread(() -> {
            Response response =
                    BackendServiceClass.getResponseFromRequest(request);
            if (response.isSuccessful()) {
                try {
                    String responseBody = response.body().string();
                    Log.d("TAG", responseBody);
                    JSONObject jsonResponse = new JSONObject(responseBody);
                    JSONArray schedules = jsonResponse.getJSONArray("schedule");

                    runOnUiThread(() -> {
                        for (int i = 0; i < schedules.length(); i++) {
                            try {
                                JSONObject schedule =
                                        schedules.getJSONObject(i);

                                String placeName = schedule.getString(
                                        "displayName");
                                String address = schedule.getString(
                                        "shortFormattedAddress");
                                String rating = schedule.optString("rating",
                                        "--");

                                JSONObject locationObj = schedule.getJSONObject(
                                        "location");
                                double lat = locationObj.getDouble(
                                        "latitude");
                                double longi = locationObj.getDouble(
                                        "longitude");

                                placeNames.add(placeName);
                                latitude.add(String.valueOf(lat));
                                longitudes.add(String.valueOf(longi));
                                addresses.add(address);
                                ratings.add(rating);

                            } catch (JSONException e) {
                                throw new CustomException("error", e);
                            }


                        }

                        Intent mapsIntent =
                                new Intent(ListDetailsActivity.this,
                                        MapsActivity.class);
                        mapsIntent.putStringArrayListExtra("placeNames",
                                placeNames);
                        mapsIntent.putStringArrayListExtra("longitudes",
                                longitudes);
                        mapsIntent.putStringArrayListExtra("latitudes",
                                latitude);
                        mapsIntent.putStringArrayListExtra("addresses",
                                addresses);
                        mapsIntent.putStringArrayListExtra("ratings", ratings);

                        mapsIntent.putExtra("context", "complex");
                        startActivity(mapsIntent);
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

    private void deletePlaceFromList(String placeId,
                                     ListBoxComponentView listBox) {
        Intent intent = getIntent();
        String listId = intent.getStringExtra("id");
        AlertDialog.Builder builder =
                new AlertDialog.Builder(ListDetailsActivity.this);
        builder.setMessage(
                        "Are you sure you want to remove the place from the " +
                                "list?")
                .setTitle(
                        "Remove List")
                .setNegativeButton("Close",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface,
                                                int i) {
                                builder.create().dismiss();
                            }
                        })
                .setPositiveButton("Remove",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface,
                                                int i) {
                                //TODO: make backend api call to remove
                                // places from
                                // list. For now just showing a toast
                                GoogleSignInAccount account =
                                        GoogleSignIn.getLastSignedInAccount(ListDetailsActivity.this);
                                JSONObject json = new JSONObject();
                                try {
                                    json.put("placeId", placeId);
                                } catch (JSONException e) {
                                    throw new CustomException("error", e);
                                }
                                Request request =
                                        BackendServiceClass.removePlaceFromList(
                                                account.getIdToken(), json,
                                                listId

                                        );
                                new Thread(() -> {
                                    Response response =
                                            BackendServiceClass.getResponseFromRequest(request);
                                    if (response.isSuccessful()) {
                                        runOnUiThread(() -> {
                                            activityLayout.removeView(listBox);
                                            checkForEmptyPlacesInList();
                                        });
                                    } else {
                                        try {
                                            Log.d("TAG",
                                                    response.body().string());
                                        } catch (IOException e) {
                                            throw new CustomException("error"
                                                    , e);
                                        }
                                    }
                                }).start();

                            }
                        });
        builder.create().show();
    }

    /**
     * Retrieves places associated to the list.
     */
    private void retrievePlacesForList() {
        if (!networkChecker.haveNetworkConnection()) {
            handleNoConnection("Cannot retrieve places for this list.");
            return;
        }

        Intent intent = getIntent();
        String listId = intent.getStringExtra("id");

        GoogleSignInAccount account =
                GoogleSignIn.getLastSignedInAccount(ListDetailsActivity.this);

        Request request =
                BackendServiceClass.getPlacesForListGetRequest(account.getIdToken(),
                        listId);
        new Thread(() -> {
            Response response =
                    BackendServiceClass.getResponseFromRequest(request);

            if (response.isSuccessful()) {
                try {
                    String responseBody = response.body().string();
                    JSONObject jsonResponse = new JSONObject(responseBody);
                    JSONArray places = jsonResponse.getJSONArray("places");
                    Log.d("TAG", places.toString());
                    for (int i = 0; i < places.length(); i++) {
                        JSONObject place = places.getJSONObject(i);
                        int finalI = i;
                        int finalI1 = i;
                        runOnUiThread(() -> {
                            ListBoxComponentView listBox =
                                    new ListBoxComponentView(ListDetailsActivity.this);
                            listBox.setTag("place" + finalI);

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
                                JSONObject locationObj = place.getJSONObject(
                                        "location");
                                double latitude = locationObj.getDouble(
                                        "latitude");
                                double longitude = locationObj.getDouble(
                                        "longitude");

                                placesIds.add(placeId);
                                listBox.showAddToListButton();
                                listBox.setButtonColorToRed();
                                listBox.showViewPlaceButton();
                                listBox.buttonAddToList.setTag("remove" + finalI1);
                                listBox.setAddButtonAction(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        deletePlaceFromList(placeId, listBox);
                                    }
                                });

                                listBox.setViewPlaceButtonAction(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        Intent intentTo =
                                                new Intent(ListDetailsActivity.this, PlaceDetails.class);
                                        intentTo.putExtra("placeName",
                                                placeName);
                                        intentTo.putExtra("address", address);
                                        intentTo.putExtra("rating", rating);
                                        intentTo.putExtra("longitude",
                                                longitude);
                                        intentTo.putExtra("latitude", latitude);
                                        startActivity(intentTo);
                                    }
                                });
                                activityLayout.addView(listBox);
                            } catch (JSONException e) {
                                throw new CustomException("error", e);
                            }
                        });
                    }
                    runOnUiThread(() -> {
                        checkForEmptyPlacesInList();
                    });
                } catch (IOException e) {
                    throw new CustomException("error", e);
                } catch (JSONException e) {
                    throw new CustomException("error", e);
                }
            } else {
                runOnUiThread(() -> {
                    checkForEmptyPlacesInList();
                });

                try {
                    Log.d("TAG", response.body().string());
                } catch (IOException e) {
                    throw new CustomException("error", e);
                }
            }
        }).start();
    }

    /**
     * deletes list if it is a user list.
     */
    private void deleteUserList() {
        if (!networkChecker.haveNetworkConnection()) {
            handleNoConnection("Unable to delete now. Try again later.");
            return;
        }
        Intent intent = getIntent();
        String listId = intent.getStringExtra("id");
        GoogleSignInAccount account =
                GoogleSignIn.getLastSignedInAccount(ListDetailsActivity.this);
        Request request =
                BackendServiceClass.deleteUserListDeleteRequest(account.getIdToken(),
                        listId);
        new Thread(() -> {
            Response response =
                    BackendServiceClass.getResponseFromRequest(request);
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

    /**
     * Deletes the list if it is a group list.
     */
    private void deleteGroupList() {
        if (!networkChecker.haveNetworkConnection()) {
            handleNoConnection("Unable to delete now. Try again later");
            return;
        }
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

        Request request =
                BackendServiceClass.deleteGroupListDeleteRequest(account.getIdToken(),
                        json, groupId);
        new Thread(() -> {
            Response response =
                    BackendServiceClass.getResponseFromRequest(request);
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
            onDeleteSelected();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    /**
     * Sets action for on delete selected.
     */
    private void onDeleteSelected() {
        AlertDialog.Builder builder =
                new AlertDialog.Builder(ListDetailsActivity.this);
        builder.setMessage(
                        "Are you certain you want to remove this list? " +
                                "It contains all your carefully selected " +
                                "destinations " +
                                "and future memories. Once deleted, this " +
                                "action cannot be undone")
                .setTitle(
                        "Delete List");
        builder.setPositiveButton("Confirm",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface,
                                        int i) {
                        Intent intentFrom = getIntent();
                        String context = intentFrom.getStringExtra("context");
                        if (context.equals("userList")) {
                            deleteUserList();
                        } else {
                            deleteGroupList();
                        }
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.header_bar_menu_delete, menu);
        return true;
    }
}