package com.example.triptrooperapp;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

/**
 * Activities screen.
 */
public class ActivitiesActivity extends AppCompatActivity {

    private GreenButtonView viewActivityByDestinationButton;
    private GreenButtonView viewActivityByCurrentLocationButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_activities);

        initializeActivityScreenButton();

        viewActivityByCurrentLocationButton.setButtonActionOnClick(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkLocationPermissionAndNavigate();

            }
        });

        viewActivityByDestinationButton.setButtonActionOnClick(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(ActivitiesActivity.this, "Hello", Toast.LENGTH_SHORT).show();
                AlertDialog.Builder builder = new AlertDialog.Builder(ActivitiesActivity.this);
                View dialogView = LayoutInflater.from(ActivitiesActivity.this).inflate(R.layout.create_list_dialog_view,null);
                final EditText destinationText = dialogView.findViewById(R.id.list_name_textField);
                destinationText.setHint("Destination Name");
                GreenButtonView createListButton = dialogView.findViewById(R.id.create_list_button);
                createListButton.setButtonText("Enter destination");
                builder.setView(dialogView);

                final AlertDialog dialog = builder.create();
                createListButton.setButtonActionOnClick(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (destinationText.getText().toString().equals("")){
                            Toast.makeText(ActivitiesActivity.this, "No destination entered.", Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                        }
                        else {
                            Intent intentTo = new Intent(ActivitiesActivity.this, PlacesActivity.class);
                            intentTo.putExtra("destination", destinationText.getText().toString());
                            intentTo.putExtra("context", "byDestination");
                            intentTo.putExtra("list","--");
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
        viewActivityByDestinationButton = findViewById(R.id.activity_destination);
        viewActivityByCurrentLocationButton = findViewById(R.id.activity_location);

        viewActivityByCurrentLocationButton.setButtonText("View Activities nearby");
        viewActivityByDestinationButton.setButtonText("View Activities for your destination");
    }


    /**
     * Checks Location permission of user. If location permission is given it navigates to phone details screen.
     */
    private void checkLocationPermissionAndNavigate(){
        if (areLocationPermissionsAlreadyGranted()){
            Intent intent = new Intent(ActivitiesActivity.this, PlacesActivity.class);
            intent.putExtra("context", "nearby");
            intent.putExtra("list","--");
            startActivity(intent);
            return;
        }
        else{
            if (areLocationPermissionsPreviouslyDenied()){
                new AlertDialog.Builder(this)
                        .setTitle("Location Permission")
                        .setMessage("We need location permission for viewing activities.")
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                Toast.makeText(ActivitiesActivity.this, "Permission for location denied", Toast.LENGTH_LONG)
                                        .show();
                                dialogInterface.dismiss();
                            }
                        })
                        .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                requestLocationPermission();
                            }
                        }).show();
            }
            else{
                requestLocationPermission();
            }
        }
    }

    /**
     * Checks if permissions are granted.
     * @return boolean
     */
    private boolean areLocationPermissionsAlreadyGranted(){
        return ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION)
                == PackageManager.PERMISSION_GRANTED;
    }

    /**
     * check whether we should show a rationale for a permission request. It returns true
     * if the app has previously requested the permission and the user denied it (and possibly selected the "Don't ask again" option)
     * @return boolean
     */
    private boolean areLocationPermissionsPreviouslyDenied(){
        return ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.ACCESS_COARSE_LOCATION)
                || ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.ACCESS_FINE_LOCATION);
    }

    /**
     * Requests the user for location permission.
     */
    private void requestLocationPermission(){
        ActivityCompat.requestPermissions(this, new String[] {android.Manifest.permission.ACCESS_COARSE_LOCATION,
                android.Manifest.permission.ACCESS_FINE_LOCATION}, 1);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Intent phoneDetailsIntent = new Intent(ActivitiesActivity.this, PlacesActivity.class);
                phoneDetailsIntent.putExtra("list","--");
                startActivity(phoneDetailsIntent);
            } else {
                Toast.makeText(this, "Location permission is required!", Toast.LENGTH_LONG).show();
            }
        }
    }
}