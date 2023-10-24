package com.example.triptrooperapp;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.widget.RelativeLayout;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.annotation.NonNull;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

public class BottomNavBarView extends RelativeLayout {

    private BottomNavigationView bottomNavigationView;
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

    private void initializeBottomNav(Context context) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.bottom_nav_bar, this, true);
        bottomNavigationView = findViewById(R.id.bottom_navigation);

        SharedPreferences sharedPref = context.getSharedPreferences(SHARED_PREFS_NAME, Context.MODE_PRIVATE);
        int defaultItemId = R.id.nav_home;
        int selectedItemId = sharedPref.getInt(SELECTED_ITEM_ID_KEY, defaultItemId);
        bottomNavigationView.setSelectedItemId(selectedItemId);

        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                SharedPreferences sharedPref = getContext().getSharedPreferences(SHARED_PREFS_NAME, Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putInt(SELECTED_ITEM_ID_KEY, item.getItemId());
                editor.apply();

                if (item.getItemId() == R.id.nav_home){
                    /* Navigate to Home page*/
                    Intent intent = new Intent(BottomNavBarView.this.getContext(), HomeActivity.class);
                    BottomNavBarView.this.getContext().startActivity(intent);
                }

                else if (item.getItemId() == R.id.nav_list){
                    /* navigate to list page*/
                    Intent intent = new Intent(BottomNavBarView.this.getContext(), ListActivity.class);
                    BottomNavBarView.this.getContext().startActivity(intent);

                }

                else if (item.getItemId() == R.id.nav_group){
                    /* navigate to group page*/
                    Intent intent = new Intent(BottomNavBarView.this.getContext(), GroupsActivity.class);
                    BottomNavBarView.this.getContext().startActivity(intent);
                }

                else if (item.getItemId() == R.id.nav_activity){
                    /* navigate to activity page*/
                    Intent intent = new Intent(BottomNavBarView.this.getContext(), ActivitiesActivity.class);
                    BottomNavBarView.this.getContext().startActivity(intent);
                }

                else {
                    Toast.makeText(BottomNavBarView.this.getContext(), "Incorrect nav option", Toast.LENGTH_SHORT).show();
                }
                return true;
            }
        });
    }
}
