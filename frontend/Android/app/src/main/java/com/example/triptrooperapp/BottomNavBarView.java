package com.example.triptrooperapp;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

/**
 * Bottom Navigation Bar containing Home, List, Activity and Group menu.
 */
public class BottomNavBarView extends RelativeLayout {

    private static final String SHARED_PREFS_NAME = "BottomNavPrefs";
    private static final String SELECTED_ITEM_ID_KEY = "selectedItemId";

    public BottomNavBarView(Context context) {
        super(context);
        initializeBottomNav(context);
    }

    public BottomNavBarView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initializeBottomNav(context);
    }

    /**
     * Initializes the bottom navigation bar and sets up button navigation.
     *
     * @param context
     */
    private void initializeBottomNav(Context context) {
        LayoutInflater inflater =
                (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.bottom_nav_bar, this, true);
        BottomNavigationView bottomNavigationView =
                findViewById(R.id.bottom_navigation);

        /* Need to save which item was selected when new activity is launched.*/
        SharedPreferences sharedPref =
                context.getSharedPreferences(SHARED_PREFS_NAME,
                        Context.MODE_PRIVATE);
        int defaultItemId = R.id.nav_home;
        int selectedItemId = sharedPref.getInt(SELECTED_ITEM_ID_KEY,
                defaultItemId);
        bottomNavigationView.setSelectedItemId(selectedItemId);

        bottomNavigationView.setOnItemSelectedListener(
                new NavigationBarView.OnItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                        SharedPreferences sharedPref =
                                getContext().getSharedPreferences(SHARED_PREFS_NAME,
                                        Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPref.edit();
                        editor.putInt(SELECTED_ITEM_ID_KEY, item.getItemId());
                        editor.apply();

                        /* Navigate to Home page*/
                        if (item.getItemId() == R.id.nav_home) {
                            navigateToActivity(HomeActivity.class);
                        }

                        /*Navigate to List page */
                        else if (item.getItemId() == R.id.nav_list) {
                            navigateToActivity(ListActivity.class);
                        }

                        /* Navigate to group page*/
                        else if (item.getItemId() == R.id.nav_group) {
                            navigateToActivity(GroupsActivity.class);
                        }

                        /*Navigate to activity page*/
                        else if (item.getItemId() == R.id.nav_activity) {
                            navigateToActivity(ActivitiesActivity.class);
                        }

                /* This case should not be possible but keeping here for
                debugging purposes.*/
                        else {
                            Toast.makeText(BottomNavBarView.this.getContext(),
                                    "Incorrect nav option",
                                    Toast.LENGTH_SHORT).show();
                        }
                        return true;
                    }
                });
    }

    /**
     * Disables the default navigation animation from the bottom navigation bar.
     */
    private void disableNavigationAnimation() {
        if (BottomNavBarView.this.getContext() instanceof Activity) {
            ((Activity) BottomNavBarView.this.getContext()).
                    overridePendingTransition(0, 0);
        }
    }

    /**
     * Navigates to the activityClass.
     *
     * @param activityClass
     */
    private void navigateToActivity(Class<?> activityClass) {
        Intent intent = new Intent(BottomNavBarView.this.getContext(),
                activityClass);
        BottomNavBarView.this.getContext().startActivity(intent);
        disableNavigationAnimation();
    }
}
