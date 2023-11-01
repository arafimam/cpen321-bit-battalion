package com.example.triptrooperapp;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;

public class GreenButtonView extends LinearLayout {

    private Button button;

    public GreenButtonView(Context context) {
        super(context);
        initializeTheButton(context);
    }

    public GreenButtonView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initializeTheButton(context);
    }

    /**
     * Initializes the button.
     *
     * @param context
     */
    private void initializeTheButton(Context context) {
        LayoutInflater inflater =
                (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.green_button, this, true);
        button = findViewById(R.id.green_button);
    }

    /**
     * Setter method for the button text.
     *
     * @param buttonTextToEnter
     */
    public void setButtonText(String buttonTextToEnter) {
        button.setText(buttonTextToEnter);
    }

    /**
     * Setter method for the button on click listener.
     *
     * @param listener
     */
    public void setButtonActionOnClick(View.OnClickListener listener) {
        button.setOnClickListener(listener);
    }
}
