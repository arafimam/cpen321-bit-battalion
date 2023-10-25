package com.example.triptrooperapp;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
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

        /**
         * Create List, Floating action Button.
         */
        createListButton = findViewById(R.id.create_list);
        createListButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(ListActivity.this);
                View dialogView = LayoutInflater.from(ListActivity.this).inflate(R.layout.create_list_dialog_view,null);
                final EditText listNameText = dialogView.findViewById(R.id.list_name_textField);
                GreenButtonView createListButton = dialogView.findViewById(R.id.create_list_button);
                createListButton.setButtonText("Create List");
                builder.setView(dialogView);

                final AlertDialog dialog = builder.create();
                createListButton.setButtonActionOnClick(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (listNameText.getText().toString().equals("")){
                            Toast.makeText(ListActivity.this, "List Name Empty", Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                        }
                        else {
                            // TODO: send back end api call to make list with name and also update the lists.
                            Toast.makeText(ListActivity.this, "Created List", Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                        }
                    }
                });
                dialog.show();

            }
        });
    }
}