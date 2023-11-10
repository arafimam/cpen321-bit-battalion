package com.example.triptrooperapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
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
 * List screen for user.
 * Accessed via the bottom navigation bar.
 */
public class ListActivity extends AppCompatActivity {

    private LinearLayout listBoxContainer;
    private FloatingActionButton createListButton;
    private NetworkChecker networkChecker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        networkChecker = new NetworkChecker(ListActivity.this);
        createListButton = findViewById(R.id.create_list);
        listBoxContainer = findViewById(R.id.list_layout_container);
        setActionForAddListButton();
        if (!networkChecker.haveNetworkConnection()) {
            handleNoConnection(
                    "Unable to retrieve your lists."
            );
            checkEmptyListView();
        } else {
            retrieveListForUser();
        }
    }

    /**
     * Handle no internet connection.
     */
    private void handleNoConnection(String message) {
        AlertDialog.Builder builder =
                new AlertDialog.Builder(ListActivity.this);
        builder.setMessage(
                        message)
                .setTitle(
                        "No internet.");
        builder.create().show();
    }

    /**
     * Sets up everything for the floating action button for
     * adding lists.
     * When clicked opens up a alert dialog that shows
     * "List name" text field and create list button.
     */
    private void setActionForAddListButton() {
        createListButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // create the alert dialog for create list.
                if (!networkChecker.haveNetworkConnection()) {
                    handleNoConnection("Cannot create list at this moment.");
                    return;
                }
                AlertDialog.Builder builder =
                        new AlertDialog.Builder(ListActivity.this);
                View dialogView =
                        LayoutInflater.from(ListActivity.this).inflate(
                                R.layout.create_list_dialog_view, null);

                // set up the text field and create list btn.
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
                        if (!networkChecker.haveNetworkConnection()) {
                            handleNoConnection("List not created. Try again " +
                                    "later.");
                        } else {
                            handleErrorForCreateList(
                                    listNameText.getText().toString(),
                                    dialog
                            );
                        }
                    }
                });
                dialog.show();

            }
        });
    }

    /**
     * Checks if list name is empty.
     * If empty show a toast message
     * If not creates a list.
     */
    private void handleErrorForCreateList(String enteredListName,
                                          AlertDialog dialog) {
        if (enteredListName.isEmpty()) {
            Toast.makeText(ListActivity.this,
                            "No list name entered.", Toast.LENGTH_LONG)
                    .show();
        } else {
            createListByUser(enteredListName);
            dialog.dismiss();
        }
        checkEmptyListView();
    }

    /**
     * retrieves list for user.
     */
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
                                final String listName = list.getString(
                                        "listName");
                                final String listId = list.getString("_id");
                                ListBoxComponentView listBox =
                                        createListBoxForEachList(listName,
                                                listId);
                                listBoxContainer.addView(listBox);
                            } catch (JSONException e) {
                                throw new CustomException("error", e);
                            }
                        }
                        checkEmptyListView();
                    });

                } catch (IOException | JSONException e) {
                    throw new CustomException("error", e);
                }
            }

            // if response is not successful.
            else {
                try {
                    Log.d("TAG", response.body().string());
                } catch (IOException e) {
                    throw new CustomException("error", e);
                }
                handleErrorForRetrievingUserList();
            }
        }).start();
    }


    /**
     * Creates list box for each user.
     * Returns Null if listName or listId is Null.
     */
    private ListBoxComponentView createListBoxForEachList(
            String listName,
            String listId
    ) {
        if (listName.isEmpty() || listId.isEmpty()) {
            return null;
        }

        ListBoxComponentView listBox =
                new ListBoxComponentView(ListActivity.this);
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

        return listBox;
    }

    /**
     * Handle error in retrieving user list.
     * Shows an error dialog.
     */
    private void handleErrorForRetrievingUserList() {
        runOnUiThread(() -> {
            AlertDialog.Builder builder =
                    new AlertDialog.Builder(ListActivity.this);
            builder.setMessage(
                            "Unable to retrieve your lists. Try again later.")
                    .setTitle(
                            "Something went wrong.");
            builder.create().show();
        });
    }

    /**
     * Creates list by user.
     */
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
                    refreshListView();
                    checkEmptyListView();
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
     * Refreshes the list view.
     */
    private void refreshListView() {
        listBoxContainer.removeAllViews();
        retrieveListForUser();
    }

    /**
     * Checks for empty list view.
     * If no list added shows a message and a
     * error logo.
     */
    private void checkEmptyListView() {
        TextView noListText = findViewById(R.id.textView_noList);
        if (listBoxContainer.getChildCount() == 0) {
            noListText.setVisibility(View.VISIBLE);
        } else {
            noListText.setVisibility(View.GONE);
        }
    }
}