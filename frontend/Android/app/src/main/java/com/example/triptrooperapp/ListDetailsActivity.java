package com.example.triptrooperapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.annotation.SuppressLint;
import android.app.DownloadManager;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Request;
import okhttp3.Response;

public class ListDetailsActivity extends AppCompatActivity {

    private TextView listName;
    private LinearLayout activityLayout;
    private Toolbar toolbar;

    private FloatingActionButton addActivity;

    private FloatingActionButton optimizeButton;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_details);

        toolbar = findViewById(R.id.toolbar);

        addActivity = findViewById(R.id.create_list);
        optimizeButton = findViewById(R.id.optimize_button);

        Intent intent = getIntent();
        String listNamePassed = intent.getStringExtra("listName");
        String listId = intent.getStringExtra("id");

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

        addActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ListDetailsActivity.this, ActivitiesActivity.class);
                startActivity(intent);
            }
        });

        optimizeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

    }

    private void deleteUserList(){
        Intent intent = getIntent();
        String listId = intent.getStringExtra("id");
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(ListDetailsActivity.this);
        BackendServiceClass backendService = new BackendServiceClass("users/"+listId+"/remove/list", "authorization",account.getIdToken());
        Request request = backendService.doPutRequestWithHeaderOnly();
        new Thread(() -> {
            Response response = backendService.getResponseFromRequest(request);
            if (response.isSuccessful()){
                runOnUiThread(() -> {
                    Toast.makeText(ListDetailsActivity.this, "List Deleted", Toast.LENGTH_SHORT).show();
                    Intent intentTo = new Intent(ListDetailsActivity.this, ListActivity.class);
                    startActivity(intentTo);
                });
            }
            else{
                try {
                    Log.d("TAG", response.body().string());
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }).start();

    }

    private void deleteGroupList(){
        Intent intent = getIntent();
        String listId = intent.getStringExtra("id");
        String groupId = intent.getStringExtra("groupId");
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(ListDetailsActivity.this);
        JSONObject json = new JSONObject();
        try {
            json.put("listId", listId);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        BackendServiceClass backendService = new BackendServiceClass("groups/"+groupId+"/remove/list", json,"authorization",account.getIdToken());
        Request request = backendService.doPutRequestWithJsonAndHeader();
        new Thread(() -> {
            Response response = backendService.getResponseFromRequest(request);
            if (response.isSuccessful()){
                runOnUiThread(() -> {
                    Toast.makeText(ListDetailsActivity.this, "List Deleted", Toast.LENGTH_SHORT).show();
                    Intent intentTo = new Intent(ListDetailsActivity.this, GroupListActivity.class);
                    intentTo.putExtra("groupId", groupId);
                    startActivity(intentTo);
                });
            }
            else{
                try {
                    Log.d("TAG", response.body().string());
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }).start();
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            Intent intentFrom = getIntent();
            String context = intentFrom.getStringExtra("context");
            if (context.equals("userList")){
                Intent intent = new Intent(this, ListActivity.class);
                startActivity(intent);
                overridePendingTransition(0,0);
            }
            else {
                Intent intent = new Intent(this, GroupListActivity.class);
                intent.putExtra("groupId", intentFrom.getStringExtra("groupId"));
                startActivity(intent);
                overridePendingTransition(0,0);
            }

            return true;
        } else if (item.getItemId() == R.id.action_delete) {
            // TODO: pass the id to backend to delete the list.
            Intent intentFrom = getIntent();
            String context = intentFrom.getStringExtra("context");
            if (context.equals("userList")){
                deleteUserList();
            }
            else {
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