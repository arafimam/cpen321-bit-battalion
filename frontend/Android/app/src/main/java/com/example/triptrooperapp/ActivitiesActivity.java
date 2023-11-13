package com.example.triptrooperapp;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

/**
 * Activities screen.
 */
public class ActivitiesActivity extends AppCompatActivity {

    private GreenButtonView viewActivityByDestinationButton;
    private GreenButtonView viewActivityByCurrentLocationButton;
    private NetworkChecker networkChecker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_activities);
        networkChecker = new NetworkChecker(this);
        initializeActivityScreenButton();
        setActivityByLocationButton();
        setActivityByCurrentLocationButton();

        TextView activityHeader = findViewById(R.id.activityHeader);
        activityHeader.setText("Explore destinations and discover nearby " +
                "places. For personalized collections, visit your group or " +
                "list screen to curate your favorites");
    }

    private void handleNoConnection(String message) {
        AlertDialog.Builder builder =
                new AlertDialog.Builder(ActivitiesActivity.this);
        builder.setMessage(
                        message)
                .setTitle(
                        "No internet.");
        builder.create().show();
    }

    /**
     * Sets activity by current location button.
     */
    private void setActivityByCurrentLocationButton() {
        viewActivityByCurrentLocationButton.
                setButtonActionOnClick(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (!networkChecker.haveNetworkConnection()) {
                            handleNoConnection("Unable to view Places near " +
                                    "you. Try again later");
                            return;
                        }
                        checkLocationPermissionAndNavigate();
                    }
                });
    }

    /**
     * Sets activity by location button.
     */
    private void setActivityByLocationButton() {
        viewActivityByDestinationButton.
                setButtonActionOnClick(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        AlertDialog.Builder builder =
                                new AlertDialog.Builder(ActivitiesActivity.this);
                        View dialogView =
                                LayoutInflater.from(ActivitiesActivity.this).
                                        inflate(R.layout.create_list_dialog_view,
                                                null);
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
                                if (!networkChecker.haveNetworkConnection()) {
                                    handleNoConnection("Unable to view places" +
                                            " by " +
                                            "destination. Try again later");
                                    return;
                                }
                                if (destinationText.getText().toString().equals("")) {
                                    Toast.makeText(ActivitiesActivity.this,
                                            "No " +
                                                    "destination entered.",
                                            Toast.LENGTH_SHORT).show();
                                    dialog.dismiss();
                                } else {
                                    Intent intentTo =
                                            new Intent(ActivitiesActivity.this,
                                                    PlacesActivity.class);
                                    intentTo.putExtra("destination",
                                            destinationText.getText().toString());
                                    intentTo.putExtra("context",
                                            "byDestination");
                                    intentTo.putExtra("list", "--");
                                    startActivity(intentTo);
                                    dialog.dismiss();
                                }
                            }
                        });
                        dialog.show();

                    }
                });
    }

    /**
     * Initializes activity screen button.
     */
    private void initializeActivityScreenButton() {
        viewActivityByDestinationButton =
                findViewById(R.id.activity_destination);
        viewActivityByCurrentLocationButton =
                findViewById(R.id.activity_location);

        viewActivityByCurrentLocationButton.setButtonText("Find local gems " +
                "near you");
        viewActivityByDestinationButton.setButtonText("Explore places by " +
                "destination");
    }


    /**
     * Checks Location permission of user. If location permission is given it
     * navigates to phone details screen.
     */
    private void checkLocationPermissionAndNavigate() {
        if (LocationService.areLocationPermissionsAlreadyGranted(this)) {
            Intent intent = new Intent(ActivitiesActivity.this,
                    PlacesActivity.class);
            intent.putExtra("context", "nearby");
            intent.putExtra("list", "--");
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
                                        Toast.makeText(ActivitiesActivity.this,
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
                Intent phoneDetailsIntent =
                        new Intent(ActivitiesActivity.this,
                                PlacesActivity.class);
                phoneDetailsIntent.putExtra("context", "nearby");
                phoneDetailsIntent.putExtra("list", "--");
                startActivity(phoneDetailsIntent);
            } else {
                Toast.makeText(this, "Location permission is required!",
                        Toast.LENGTH_LONG).show();
            }
        }
    }
}