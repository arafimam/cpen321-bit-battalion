package com.example.triptrooperapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Request;
import okhttp3.Response;

/**
 * List screen.
 */
public class ListActivity extends AppCompatActivity {

    private LinearLayout listBoxContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        listBoxContainer = findViewById(R.id.list_layout_container);
        retrieveListForUser();


        /**
         * Create List, Floating action Button.
         */
        FloatingActionButton createListButton = findViewById(R.id.create_list);
        createListButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder =
                        new AlertDialog.Builder(ListActivity.this);
                View dialogView =
                        LayoutInflater.from(ListActivity.this).inflate(
                                R.layout.create_list_dialog_view, null);
                final EditText listNameText =
                        dialogView.findViewById(R.id.list_name_textField);
                GreenButtonView createListButton =
                        dialogView.findViewById(R.id.create_list_button);
                createListButton.setButtonText("Create List");
                builder.setView(dialogView);

                final AlertDialog dialog = builder.create();
                createListButton.setButtonActionOnClick(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (listNameText.getText().toString().equals("")) {
                            Toast.makeText(ListActivity.this, "List Name " +
                                    "Empty", Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                        } else {
                            createListByUser(listNameText.getText().toString());
                            dialog.dismiss();
                        }
                    }
                });
                dialog.show();

            }
        });
    }

    private void retrieveListForUser() {
        GoogleSignInAccount account =
                GoogleSignIn.getLastSignedInAccount(ListActivity.this);

        Request request =
                BackendServiceClass.getListsOfUserGetRequest(account.getIdToken());

        new Thread(() -> {
            Response response =
                    BackendServiceClass.getResponseFromRequest(request);
            if (response.isSuccessful()) {
                try {
                    String responseBody = response.body().string();
                    JSONObject jsonResponse = new JSONObject(responseBody);
                    JSONArray listArray = jsonResponse.getJSONArray("lists");

                    runOnUiThread(() -> {
                        for (int i = 0; i < listArray.length(); i++) {
                            try {
                                JSONObject list = listArray.getJSONObject(i);
                                ListBoxComponentView listBox =
                                        new ListBoxComponentView(ListActivity.this);
                                final String listName = list.getString(
                                        "listName");
                                final String listId = list.getString("_id");
                                listBox.setMainTitleText(listName);
                                listBox.setVisibilityOfTextViews(View.VISIBLE
                                        , View.INVISIBLE, View.INVISIBLE);
                                listBox.setActionOnCardClick(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        Intent intent =
                                                new Intent(ListActivity.this,
                                                        ListDetailsActivity.class);
                                        intent.putExtra("listName", listName);
                                        intent.putExtra("id", listId);
                                        intent.putExtra("context", "userList");
                                        startActivity(intent);
                                    }
                                });
                                listBoxContainer.addView(listBox);
                            } catch (JSONException e) {
                                throw new CustomException("error", e);
                            }
                        }
                    });

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

    private void createListByUser(String listName) {
        GoogleSignInAccount account =
                GoogleSignIn.getLastSignedInAccount(ListActivity.this);
        JSONObject json = new JSONObject();
        try {
            json.put("listName", listName);
        } catch (JSONException e) {
            throw new CustomException("error", e);
        }

        Request request = BackendServiceClass.createListForUser(json,
                account.getIdToken());
        new Thread(() -> {
            Response response =
                    BackendServiceClass.getResponseFromRequest(request);
            if (response.isSuccessful()) {
                runOnUiThread(() -> {
                    Toast.makeText(ListActivity.this,
                            "Created list " + listName, Toast.LENGTH_SHORT).show();
                    recreate();
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
}