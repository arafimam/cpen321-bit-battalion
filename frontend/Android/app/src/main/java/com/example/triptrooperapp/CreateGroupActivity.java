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
                if (groupNameField.getText().toString().equals("") ){
                    Toast.makeText(CreateGroupActivity.this, "Group name or destination not entered",
                            Toast.LENGTH_SHORT).show();
                }
                else {
                    // TODO: Make backend call to add a new group.
                    Toast.makeText(CreateGroupActivity.this, groupNameField.getText().toString(),
                            Toast.LENGTH_SHORT).show();
                }
            }
        });

        joinGroupButton.setButtonActionOnClick(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (groupCodeField.getText().toString().equals("")){
                    Toast.makeText(CreateGroupActivity.this, "Group code not entered",
                            Toast.LENGTH_SHORT).show();
                }
                else{
                    // TODO: Make backend call to make the user join a group.
                    Toast.makeText(CreateGroupActivity.this, groupCodeField.getText().toString(),
                            Toast.LENGTH_SHORT).show();
                }
            }
        });

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
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
}