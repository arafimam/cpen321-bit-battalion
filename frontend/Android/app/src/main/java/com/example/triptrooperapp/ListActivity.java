package com.example.triptrooperapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

/**
 * List screen.
 */
public class ListActivity extends AppCompatActivity {

    private LinearLayout listBoxContainer;
    private FloatingActionButton createListButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        listBoxContainer = findViewById(R.id.list_layout_container);

        // TODO: REPLACE WITH BACKEND API CALL TO RETRIEVE LIST. CREATE A DS. TO HOLD THE BACKEND API CALL THEN USE THE FOR LOOP BELOW.
        for (int i=0; i<10;i++){
            ListBoxComponentView listBoxComponentView = new ListBoxComponentView(this);

            listBoxComponentView.setMainTitleText("List- " + (i + 1));
            listBoxComponentView.setSubTitleText("Number of items " + (i + 15));
            listBoxComponentView.setVisibilityOfTextViews(View.VISIBLE, View.INVISIBLE, View.VISIBLE);
            int finalI = i;
            listBoxComponentView.setActionOnCardClick(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(ListActivity.this, String.format("Selected List %d", finalI+1), Toast.LENGTH_SHORT).show();
                }
            });
            listBoxContainer.addView(listBoxComponentView);

        }

        // TODO: Navigate to Create Group page here.
        createListButton = findViewById(R.id.create_list);
        createListButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(ListActivity.this, "Clicked Create List", Toast.LENGTH_SHORT).show();
            }
        });
    }
}