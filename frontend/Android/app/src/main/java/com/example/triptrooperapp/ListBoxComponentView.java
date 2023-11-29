package com.example.triptrooperapp;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;

public class ListBoxComponentView extends LinearLayout {

    private CardView cardView;
    public TextView mainTitle;
    public TextView sideTitle;
    private TextView subTitle;

    public TextView buttonAddToList;

    public TextView buttonViewPlace;

    public ListBoxComponentView(Context context) {
        super(context);
        initializeCardViewComponent(context);
    }

    public ListBoxComponentView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initializeCardViewComponent(context);
    }

    private void initializeCardViewComponent(Context context) {
        LayoutInflater inflater =
                (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.list_box, this, true);

        cardView = findViewById(R.id.card_view);
        mainTitle = findViewById(R.id.main_title);
        sideTitle = findViewById(R.id.side_title);
        subTitle = findViewById(R.id.sub_title);
        buttonAddToList = findViewById(R.id.button_add_to_list);
        buttonAddToList.setVisibility(GONE);

        buttonViewPlace = findViewById(R.id.button_view_place);
        buttonViewPlace.setVisibility(GONE);
    }

    /**
     * Setter function for main title text.
     *
     * @param mainTitleTextToSet
     */
    public void setMainTitleText(String mainTitleTextToSet) {
        mainTitle.setText(mainTitleTextToSet);
    }

    /**
     * Setter function for side title text.
     *
     * @param sideTitleTextToSet
     */
    public void setSideTitleText(String sideTitleTextToSet) {
        sideTitle.setText(sideTitleTextToSet);
    }

    /**
     * Setter function for sub title text.
     *
     * @param subTitleTextToSet
     */
    public void setSubTitleText(String subTitleTextToSet) {
        subTitle.setText(subTitleTextToSet);
    }

    /**
     * Sets the visibility of TextView.
     * Do not set visibility to GONE
     *
     * @param visibilityOfMainTitle VISIBLE OR INVISIBLE
     * @param visibilityOfSideTitle VISIBLE OR INVISIBLE
     * @param visibilityOfSubTitle  VISIBLE OR INVISIBLE
     */
    public void setVisibilityOfTextViews(int visibilityOfMainTitle,
                                         int visibilityOfSideTitle,
                                         int visibilityOfSubTitle) {
        mainTitle.setVisibility(visibilityOfMainTitle);
        sideTitle.setVisibility(visibilityOfSideTitle);
        subTitle.setVisibility(visibilityOfSubTitle);
    }

    public void setActionOnCardClick(View.OnClickListener listener) {
        cardView.setOnClickListener(listener);
    }

    public void showAddToListButton() {
        buttonAddToList.setVisibility(VISIBLE);
    }

    public void setButtonColorToRed() {
        buttonAddToList.setBackgroundColor(Color.parseColor("#FF0000"));
        buttonAddToList.setText("Remove Place");
    }

    public void setAddButtonAction(View.OnClickListener listener) {
        buttonAddToList.setOnClickListener(listener);
    }

    public void showViewPlaceButton() {
        buttonViewPlace.setVisibility(VISIBLE);
    }

    public void setViewPlaceButtonAction(View.OnClickListener listener) {
        buttonViewPlace.setOnClickListener(listener);
    }

    public void setSameActionForAddButtonAndCard(View.OnClickListener listener) {
        buttonAddToList.setOnClickListener(listener);
        cardView.setOnClickListener(listener);
    }
}
