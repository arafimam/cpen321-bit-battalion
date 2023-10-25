package com.example.triptrooperapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

/**
 * Groups screen.
 */
public class GroupsActivity extends AppCompatActivity {

    private LinearLayout listBoxContainer;
    private FloatingActionButton createGroupButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_groups);

        listBoxContainer = findViewById(R.id.list_layout_container);

        // TODO: REPLACE WITH BACKEND API CALL TO RETRIEVE LIST. CREATE A DS. TO HOLD THE BACKEND API CALL THEN USE THE FOR LOOP BELOW.
        for (int i=0; i<10;i++){
            ListBoxComponentView listBoxComponentView = new ListBoxComponentView(this);

            listBoxComponentView.setMainTitleText("Group- " + (i + 1));
            listBoxComponentView.setSubTitleText(String.format("%d%d%d%d",i,(i+3),(i+2),(i+1)));
            listBoxComponentView.setSideTitleText("New York");
            int finalI = i;
            listBoxComponentView.setActionOnCardClick(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(GroupsActivity.this, String.format("Selected group %d", finalI+1), Toast.LENGTH_SHORT).show();
                }
            });
            listBoxContainer.addView(listBoxComponentView);

        }

        // TODO: Change this to open create list dialog.
        createGroupButton = findViewById(R.id.create_group);
        createGroupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(GroupsActivity.this, "Clicked Create Group", Toast.LENGTH_SHORT).show();
            }
        });
    }
}