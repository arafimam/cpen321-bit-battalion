package com.example.triptrooperapp;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Request;
import okhttp3.Response;

/**
 * Activities screen.
 */
public class PlacesActivity extends AppCompatActivity implements LocationListener {

    private LinearLayout placesBoxContainer;
    private String longitude;
    private String latitude;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_places);
        Intent intentFrom = getIntent();
        String context = intentFrom.getStringExtra("context");

        // setting toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        placesBoxContainer = findViewById(R.id.list_layout_container);

        TextView textHeader = findViewById(R.id.group_header);

        LocationManager locationManager =
                (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_COARSE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {

            // Request location updates
            int checkLocationUpdateTime = 1000;
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, checkLocationUpdateTime, 0, this);

            // Get last known location immediately
            Location lastKnownLocation =
                    locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if (lastKnownLocation != null) {
                longitude = String.valueOf(lastKnownLocation.getLongitude());
                latitude = String.valueOf(lastKnownLocation.getLatitude());
                Log.d("TAG", "Latitude: " + lastKnownLocation.getLatitude());
                Log.d("TAG", "Longitude: " + lastKnownLocation.getLongitude());
            } else {
                Log.d("TAG", "Last known location is null");
            }
        }

        if (context.equals("nearby")) {
            textHeader.setText("Local Gems near you");
            retrievePlaces();
        } else {
            textHeader.setText("Your Places");
            retrievePlacesByDestination();
        }
    }

    private void handleErrorsWithMessage(String message) {
        AlertDialog.Builder builder =
                new AlertDialog.Builder(PlacesActivity.this);
        builder.setMessage(
                        message)
                .setTitle(
                        "Something went wrong")
                .setNegativeButton("Close",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface,
                                                int i) {
                                builder.create().dismiss();
                            }
                        });
        builder.create().show();
    }

    /**
     * Retrieve nearby places.
     */
    private void retrievePlaces() {
        Log.d("TAG", "Retrieving places...");
        GoogleSignInAccount account =
                GoogleSignIn.getLastSignedInAccount(PlacesActivity.this);

        Request request =
                BackendServiceClass.getPlacesNearbyGetRequest(account.getIdToken(),
                        longitude, latitude);

        new Thread(() -> {
            Response response =
                    BackendServiceClass.getResponseFromRequest(request);
            if (response.isSuccessful()) {
                try {
                    String responseBody = response.body().string();
                    Log.d("TAG", responseBody);

                    JSONObject jsonResponse = new JSONObject(responseBody);
                    JSONArray places = jsonResponse.getJSONArray("places");
                    for (int i = 0; i < places.length(); i++) {
                        JSONObject place = places.getJSONObject(i);
                        int finalI = i;
                        runOnUiThread(() -> {
                            ListBoxComponentView listBox =
                                    new ListBoxComponentView(PlacesActivity.this);
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
                                JSONObject locationObj = place.getJSONObject(
                                        "location");
                                double lat = locationObj.getDouble(
                                        "latitude");
                                double longi = locationObj.getDouble(
                                        "longitude");

                                Intent intentFrom = getIntent();
                                if (intentFrom.getStringExtra("list").equals(
                                        "list")) {
                                    listBox.showAddToListButton();
                                    listBox.setSameActionForAddButtonAndCard(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            handleAddPlaceToList(listBox,
                                                    place, account, placeName);
                                        }
                                    });

                                } else if (intentFrom.getStringExtra("list").equals("--")) {
                                    listBox.showViewPlaceButton();
                                    listBox.buttonViewPlace.setTag("viewPlace"
                                            + finalI);
                                    listBox.setViewPlaceButtonAction(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            Intent intentTo =
                                                    new Intent(PlacesActivity.this, PlaceDetails.class);
                                            intentTo.putExtra("placeName",
                                                    placeName);
                                            intentTo.putExtra("rating", rating);
                                            intentTo.putExtra("address",
                                                    address);
                                            intentTo.putExtra("longitude",
                                                    longi);
                                            intentTo.putExtra("latitude", lat);
                                            startActivity(intentTo);
                                        }
                                    });
                                }

                                placesBoxContainer.addView(listBox);


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
                runOnUiThread(() -> {
                    handleErrorsWithMessage("Unable to retrieve places near " +
                            "you. " +
                            "Try again Later.");
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
     * Handles add to list click.
     *
     * @param listBox
     * @param place
     * @param account
     * @param placeName
     */
    private void handleAddPlaceToList(ListBoxComponentView listBox,
                                      JSONObject place,
                                      GoogleSignInAccount account,
                                      String placeName) {
        AlertDialog.Builder builder =
                new AlertDialog.Builder(PlacesActivity.this);
        builder.setMessage(
                        "Are you sure you want to add place " + placeName +
                                " to " +
                                "list?")
                .setTitle(
                        "Add Place")
                .setPositiveButton("Add",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface,
                                                int i) {
                                Intent intentFrom = getIntent();
                                String listId =
                                        intentFrom.getStringExtra("listId");

                                JSONObject jsonResponseForList =
                                        new JSONObject();
                                try {
                                    jsonResponseForList.put(
                                            "place", place);
                                } catch (JSONException e) {
                                    throw new CustomException(
                                            "error", e);
                                }

                                Request request1 =
                                        BackendServiceClass.addNearbyPlaceToListPutRequest(
                                                account.getIdToken(),
                                                jsonResponseForList,
                                                listId
                                        );
                                new Thread(() -> {
                                    Response response1 =
                                            BackendServiceClass.getResponseFromRequest(request1);
                                    if (response1.isSuccessful()) {
                                        try {

                                            Log.d("TAG",
                                                    response1.body().string());
                                        } catch (IOException e) {
                                            throw new CustomException("error"
                                                    , e);
                                        }
                                        runOnUiThread(() -> {
                                            Toast.makeText(PlacesActivity.this,
                                                    "Added " + placeName + " " +
                                                            "in List"
                                                    , Toast.LENGTH_SHORT).show();
                                            placesBoxContainer.removeView(listBox);
                                            builder.create().dismiss();
                                        });
                                    } else {
                                        try {
                                            Log.d("TAG",
                                                    response1.body().string());
                                        } catch (IOException e) {
                                            throw new CustomException("error"
                                                    , e);
                                        }
                                    }
                                }).start();

                            }
                        }).
                setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface,
                                                int i) {
                                builder.create().dismiss();
                            }
                        });
        builder.create().show();
    }

    /**
     * Retrieve places by destination.
     */
    private void retrievePlacesByDestination() {
        Intent intentFrom = getIntent();
        String destination = intentFrom.getStringExtra("destination");

        GoogleSignInAccount account =
                GoogleSignIn.getLastSignedInAccount(PlacesActivity.this);

        Request request = BackendServiceClass.getPlacesByDestination(
                account.getIdToken(),
                destination, ""
        );
        new Thread(() -> {
            Response response =
                    BackendServiceClass.getResponseFromRequest(request);
            if (response.isSuccessful()) {
                try {
                    String responseBody = response.body().string();
                    Log.d("TAG", responseBody);
                    // displayName, shortFormattedAddress, rating
                    JSONObject jsonResponse = new JSONObject(responseBody);
                    JSONArray places = jsonResponse.getJSONArray("places");
                    for (int i = 0; i < places.length(); i++) {
                        JSONObject place = places.getJSONObject(i);
                        int finalI = i;
                        runOnUiThread(() -> {
                            ListBoxComponentView listBox =
                                    new ListBoxComponentView(PlacesActivity.this);
                            listBox.setTag("place" + finalI);
                            listBox.mainTitle.setTag("placeName" + finalI);
                            try {
                                String placeName = place.getString(
                                        "displayName");
                                String address = place.getString(
                                        "shortFormattedAddress");
                                String rating = place.optString("rating", "--");
                                listBox.setMainTitleText(placeName);
                                listBox.setSubTitleText(address);
                                listBox.setSideTitleText("     Rating: " + rating + "/5");

                                JSONObject locationObj = place.getJSONObject(
                                        "location");
                                double lat = locationObj.getDouble(
                                        "latitude");
                                double longi = locationObj.getDouble(
                                        "longitude");

                                if (intentFrom.getStringExtra("list").equals(
                                        "list")) {
                                    listBox.showAddToListButton();
                                    listBox.setSameActionForAddButtonAndCard(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            handleAddDestinationPlaceToList(listBox,
                                                    place, account, placeName);

                                        }
                                    });
                                } else if (intentFrom.getStringExtra("list").equals("--")) {
                                    listBox.showViewPlaceButton();
                                    listBox.buttonViewPlace.setTag("viewPlace"
                                            + finalI);
                                    listBox.setViewPlaceButtonAction(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            Intent intentTo =
                                                    new Intent(PlacesActivity.this, PlaceDetails.class);
                                            intentTo.putExtra("placeName",
                                                    placeName);
                                            intentTo.putExtra("rating", rating);
                                            intentTo.putExtra("address",
                                                    address);
                                            intentTo.putExtra("longitude",
                                                    longi);
                                            intentTo.putExtra("latitude", lat);
                                            startActivity(intentTo);
                                        }
                                    });
                                }

                                placesBoxContainer.addView(listBox);

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
                runOnUiThread(() -> {
                    handleErrorsWithMessage("Unable to retrieve places for " +
                            "your " +
                            "destination. " +
                            "Try again Later.");
                });
                try {
                    Log.d("TAG", response.body().string());
                } catch (IOException e) {
                    throw new CustomException("error", e);
                }
            }
        }).start();
    }

    private void handleAddDestinationPlaceToList(ListBoxComponentView listBox,
                                                 JSONObject place,
                                                 GoogleSignInAccount account,
                                                 String placeName) {
        AlertDialog.Builder builder =
                new AlertDialog.Builder(PlacesActivity.this);
        builder.setMessage(
                        "Are you sure you want to add place " + placeName +
                                " to " +
                                "list?")
                .setTitle(
                        "Add Place")
                .setPositiveButton("Add",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface,
                                                int i) {
                                Intent intentFrom = getIntent();
                                String listId =
                                        intentFrom.getStringExtra("listId");

                                Log.d("TAG", place.toString());

                                JSONObject jsonResponseForList =
                                        new JSONObject();
                                try {
                                    jsonResponseForList.put(
                                            "place", place);
                                } catch (JSONException e) {
                                    throw new CustomException(
                                            "error", e);
                                }

                                Request request1 =
                                        BackendServiceClass.addDestinationPlacesToList(
                                                account.getIdToken(),
                                                jsonResponseForList,
                                                listId
                                        );
                                new Thread(() -> {
                                    Response response1 =
                                            BackendServiceClass.getResponseFromRequest(request1);
                                    if (response1.isSuccessful()) {
                                        try {

                                            Log.d("TAG",
                                                    response1.body().string());
                                        } catch (IOException e) {
                                            throw new CustomException("error"
                                                    , e);
                                        }
                                        runOnUiThread(() -> {
                                            Toast.makeText(
                                                    PlacesActivity.this,
                                                    "Added " + placeName + " " +
                                                            "in List",
                                                    Toast.LENGTH_SHORT).show();
                                            placesBoxContainer.removeView(listBox);
                                        });
                                    } else {
                                        try {
                                            String responseBody =
                                                    response1.body().string();
                                            String key = "\"errorMessage\":";
                                            int startIndex =
                                                    responseBody.indexOf(key) + key.length();
                                            int endIndex =
                                                    responseBody.indexOf("\"",
                                                            startIndex + 1);
                                            String errorMessage =
                                                    responseBody.substring(startIndex + 1,
                                                            endIndex);
                                            runOnUiThread(() -> {
                                                AlertDialog.Builder builder =
                                                        new AlertDialog.Builder(PlacesActivity.this);
                                                builder.setMessage(
                                                                errorMessage)
                                                        .setTitle(
                                                                "Something went wrong");

                                                builder.setNegativeButton(
                                                        "Close",
                                                        new DialogInterface.OnClickListener() {
                                                            @Override
                                                            public void onClick(DialogInterface dialogInterface,
                                                                                int i) {
                                                                builder.create().dismiss();
                                                            }
                                                        });
                                                builder.create().show();
                                            });

                                        } catch (IOException e) {
                                            throw new CustomException("error"
                                                    , e);
                                        }
                                    }
                                }).start();

                            }
                        }).
                setNegativeButton("Cancel",
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
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            Intent intentFrom = getIntent();
            if (intentFrom.getStringExtra("group") != null) {
                Intent intent = new Intent(this, GroupsActivity.class);
                startActivity(intent);
            } else if (intentFrom.getStringExtra("list").equals("list")) {
                Intent intent = new Intent(this, ListActivity.class);
                startActivity(intent);
            } else {
                Intent intent = new Intent(this, ActivitiesActivity.class);
                startActivity(intent);
                overridePendingTransition(0, 0);
            }

            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onLocationChanged(@NonNull Location location) {
        Log.d("TAG", String.valueOf(location.getLatitude()));
        Log.d("TAG", String.valueOf(location.getLongitude()));
    }

}
