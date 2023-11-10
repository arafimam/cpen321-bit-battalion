package com.example.triptrooperapp;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class LocationService {

    /**
     * Checks if permissions are granted.
     *
     * @return boolean
     */
    public static boolean areLocationPermissionsAlreadyGranted(Context context) {
        return ContextCompat.checkSelfPermission(context,
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(context,
                android.Manifest.permission.ACCESS_COARSE_LOCATION)
                == PackageManager.PERMISSION_GRANTED;
    }

    /**
     * check whether we should show a rationale for a permission request. It
     * returns true
     * if the app has previously requested the permission and the user denied
     * it (and possibly selected the "Don't ask again" option)
     *
     * @return boolean
     */
    public static boolean areLocationPermissionsPreviouslyDenied(Activity activity) {
        return ActivityCompat.shouldShowRequestPermissionRationale(activity,
                android.Manifest.permission.ACCESS_COARSE_LOCATION)
                || ActivityCompat.shouldShowRequestPermissionRationale(activity,
                android.Manifest.permission.ACCESS_FINE_LOCATION);
    }

    /**
     * Requests the user for location permission.
     */
    public static void requestLocationPermission(Activity activity) {
        ActivityCompat.requestPermissions(activity,
                new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION,
                        android.Manifest.permission.ACCESS_FINE_LOCATION}, 1);
    }
}
