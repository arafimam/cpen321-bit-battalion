package com.example.triptrooperapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class CreateGroupActivity extends AppCompatActivity {

    private GreenButtonView createGroupButton;
    private GreenButtonView joinGroupButton;

    private EditText groupNameField;
    private EditText groupCodeField;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_group);
        initializeButtonTextFieldAndToolbar();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent intent = new Intent(this, GroupsActivity.class);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * Initializes screen buttons, toolbar and text fields.
     */
    private void initializeButtonTextFieldAndToolbar() {
        createGroupButton = findViewById(R.id.create_group);
        createGroupButton.setButtonText("Create Group");
        joinGroupButton = findViewById(R.id.join_group_btn);
        joinGroupButton.setButtonText("Join Group");
        groupNameField = findViewById(R.id.group_name_text_field);
        groupCodeField = findViewById(R.id.join_group_text_field);
        toolbar = findViewById(R.id.toolbar);

        createGroupButton.setButtonActionOnClick(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setButtonClickValidation(groupNameField.getText().toString(),
                        groupNameField.getText().toString(), "Group name not entered", true);
            }
        });

        joinGroupButton.setButtonActionOnClick(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setButtonClickValidation(groupCodeField.getText().toString(),
                        groupCodeField.getText().toString(), "Group code not entered", false);
            }
        });

        /*Back arrow button and disable title in header bar*/
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
    }

    /**
     * Set Button Click validation. Upon successful validation do backend call.
     * if text to validate is empty shows a toast message with failureMessage
     * @param textToValidate
     * @param successMessage
     * @param failureMessage
     */
    private void setButtonClickValidation(String textToValidate, String successMessage,
                                          String failureMessage, boolean isCreatingNewGroup) {
        if (textToValidate.equals("")) {
            Toast.makeText(CreateGroupActivity.this, failureMessage, Toast.LENGTH_SHORT).show();
        }
        else {
            // TODO: if isCreateNewGroup true make backend call to create group else make backend call to join group.
            Toast.makeText(CreateGroupActivity.this, successMessage, Toast.LENGTH_SHORT).show();
        }

    }
}