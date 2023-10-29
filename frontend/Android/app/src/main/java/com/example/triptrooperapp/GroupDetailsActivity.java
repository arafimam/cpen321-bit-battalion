package com.example.triptrooperapp;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;


public class GroupDetailsActivity extends AppCompatActivity {

    private DefaultCardButtonView memberBtn;
    private DefaultCardButtonView listBtn;
    private DefaultCardButtonView expenseBtn;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_details);

        memberBtn = findViewById(R.id.member_btn);
        memberBtn.setMainTitleText("Members (10)");

        listBtn = findViewById(R.id.list_btn);
        listBtn.setMainTitleText("View Lists");

        expenseBtn = findViewById(R.id.expense_btn);
        expenseBtn.setMainTitleText("View Expenses");

        memberBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(GroupDetailsActivity.this);
                View dialogView = LayoutInflater.from(GroupDetailsActivity.this).inflate(R.layout.member_list_view,null);
                final LinearLayout memberList = dialogView.findViewById(R.id.member_list_container);

                List<String> nameList = new ArrayList<>();
                nameList.add("John Chen");
                nameList.add("Bob Vho");
                nameList.add("Some Dude");
                nameList.add("Another Dude");

                for (int i=0 ; i<nameList.size(); i++){
                    DefaultCardButtonView cardButtonView = new DefaultCardButtonView(GroupDetailsActivity.this);

                    cardButtonView.setMainTitleText(nameList.get(i));

                    memberList.addView(cardButtonView);
                }

                builder.setView(dialogView);
                builder.setNegativeButton("Close", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
                final AlertDialog dialog = builder.create();

                dialog.show();

            }
        });

        toolbar = findViewById(R.id.toolbar);

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