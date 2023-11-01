package com.example.triptrooperapp;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;

public class DefaultCardButtonView extends LinearLayout {

    private TextView mainTitle;
    private CardView cardView;

    public DefaultCardButtonView(Context context) {
        super(context);
        initializeDefaultCard(context);
    }

    public DefaultCardButtonView(Context context,
                                 @Nullable AttributeSet attrs) {
        super(context, attrs);
        initializeDefaultCard(context);
    }

    private void initializeDefaultCard(Context context) {
        LayoutInflater inflater =
                (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.default_card_button_view, this, true);
        mainTitle = findViewById(R.id.main_title);
        cardView = findViewById(R.id.card_view);
    }

    public void setMainTitleText(String mainTitleTextToSet) {
        mainTitle.setText(mainTitleTextToSet);
    }

    public void setActionForOnClick(View.OnClickListener listener) {
        cardView.setOnClickListener(listener);
    }
}
