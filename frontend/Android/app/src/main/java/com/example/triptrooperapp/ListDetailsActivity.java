package com.example.triptrooperapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class ListDetailsActivity extends AppCompatActivity {

    private TextView listName;
    private LinearLayout activityLayout;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_details);

        toolbar = findViewById(R.id.toolbar);

        Intent intent = getIntent();
        // TODO: change this to ID instead.
        String listNamePassed = intent.getStringExtra("listName");
        listName = findViewById(R.id.list_name_topic);
        listName.setText(listNamePassed);

        activityLayout = findViewById(R.id.list_activity_container);

        // TODO: use the ID to get the activities with that list.
        for (int i= 0; i<15; i++) {
            ListBoxComponentView listBox = new ListBoxComponentView(this);
            listBox.setMainTitleText("Activity- "+ i);
            listBox.setSubTitleText("Location: New York");
            listBox.setSideTitleText("10: 00 AM");

            activityLayout.addView(listBox);
        }

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            Intent intent = new Intent(this, ListActivity.class);
            startActivity(intent);
            overridePendingTransition(0,0);
            return true;
        } else if (item.getItemId() == R.id.action_delete) {
            // TODO: pass the id to backend to delete the list.
            Toast.makeText(this, "Delete List Clicked", Toast.LENGTH_SHORT).show();
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